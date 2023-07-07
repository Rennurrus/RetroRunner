package com.badlogic.gdx.utils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonValue;
import com.twi.game.BuildConfig;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class UBJsonReader implements BaseJsonReader {
    public boolean oldFormat = true;

    public JsonValue parse(InputStream input) {
        try {
            DataInputStream din = new DataInputStream(input);
            JsonValue parse = parse(din);
            StreamUtils.closeQuietly(din);
            return parse;
        } catch (IOException ex) {
            throw new SerializationException((Throwable) ex);
        } catch (Throwable th) {
            StreamUtils.closeQuietly((Closeable) null);
            throw th;
        }
    }

    public JsonValue parse(FileHandle file) {
        try {
            return parse((InputStream) file.read(8192));
        } catch (Exception ex) {
            throw new SerializationException("Error parsing file: " + file, ex);
        }
    }

    public JsonValue parse(DataInputStream din) throws IOException {
        try {
            return parse(din, din.readByte());
        } finally {
            StreamUtils.closeQuietly(din);
        }
    }

    /* access modifiers changed from: protected */
    public JsonValue parse(DataInputStream din, byte type) throws IOException {
        if (type == 91) {
            return parseArray(din);
        }
        if (type == 123) {
            return parseObject(din);
        }
        if (type == 90) {
            return new JsonValue(JsonValue.ValueType.nullValue);
        }
        if (type == 84) {
            return new JsonValue(true);
        }
        if (type == 70) {
            return new JsonValue(false);
        }
        if (type == 66) {
            return new JsonValue((long) readUChar(din));
        }
        if (type == 85) {
            return new JsonValue((long) readUChar(din));
        }
        if (type == 105) {
            return new JsonValue((long) (this.oldFormat ? din.readShort() : din.readByte()));
        } else if (type == 73) {
            return new JsonValue((long) (this.oldFormat ? din.readInt() : din.readShort()));
        } else if (type == 108) {
            return new JsonValue((long) din.readInt());
        } else {
            if (type == 76) {
                return new JsonValue(din.readLong());
            }
            if (type == 100) {
                return new JsonValue((double) din.readFloat());
            }
            if (type == 68) {
                return new JsonValue(din.readDouble());
            }
            if (type == 115 || type == 83) {
                return new JsonValue(parseString(din, type));
            }
            if (type == 97 || type == 65) {
                return parseData(din, type);
            }
            if (type == 67) {
                return new JsonValue((long) din.readChar());
            }
            throw new GdxRuntimeException("Unrecognized data type");
        }
    }

    /* access modifiers changed from: protected */
    public JsonValue parseArray(DataInputStream din) throws IOException {
        JsonValue result = new JsonValue(JsonValue.ValueType.array);
        byte type = din.readByte();
        byte valueType = 0;
        if (type == 36) {
            valueType = din.readByte();
            type = din.readByte();
        }
        long size = -1;
        if (type == 35) {
            size = parseSize(din, false, -1);
            if (size < 0) {
                throw new GdxRuntimeException("Unrecognized data type");
            } else if (size == 0) {
                return result;
            } else {
                type = valueType == 0 ? din.readByte() : valueType;
            }
        }
        JsonValue prev = null;
        long c = 0;
        while (din.available() > 0 && type != 93) {
            JsonValue val = parse(din, type);
            val.parent = result;
            if (prev != null) {
                val.prev = prev;
                prev.next = val;
                result.size++;
            } else {
                result.child = val;
                result.size = 1;
            }
            prev = val;
            if (size > 0) {
                long j = 1 + c;
                c = j;
                if (j >= size) {
                    break;
                }
            }
            type = valueType == 0 ? din.readByte() : valueType;
        }
        return result;
    }

    /* access modifiers changed from: protected */
    public JsonValue parseObject(DataInputStream din) throws IOException {
        DataInputStream dataInputStream = din;
        JsonValue result = new JsonValue(JsonValue.ValueType.object);
        byte type = din.readByte();
        byte valueType = 0;
        if (type == 36) {
            valueType = din.readByte();
            type = din.readByte();
        }
        long size = -1;
        if (type == 35) {
            size = parseSize(dataInputStream, false, -1);
            if (size < 0) {
                throw new GdxRuntimeException("Unrecognized data type");
            } else if (size == 0) {
                return result;
            } else {
                type = din.readByte();
            }
        }
        JsonValue prev = null;
        long c = 0;
        while (din.available() > 0 && type != 125) {
            String key = parseString(dataInputStream, true, type);
            JsonValue child = parse(dataInputStream, valueType == 0 ? din.readByte() : valueType);
            child.setName(key);
            child.parent = result;
            if (prev != null) {
                child.prev = prev;
                prev.next = child;
                result.size++;
            } else {
                result.child = child;
                result.size = 1;
            }
            prev = child;
            if (size > 0) {
                long j = 1 + c;
                c = j;
                if (j >= size) {
                    break;
                }
            }
            type = din.readByte();
        }
        return result;
    }

    /* access modifiers changed from: protected */
    public JsonValue parseData(DataInputStream din, byte blockType) throws IOException {
        byte dataType = din.readByte();
        long size = blockType == 65 ? readUInt(din) : (long) readUChar(din);
        JsonValue result = new JsonValue(JsonValue.ValueType.array);
        JsonValue prev = null;
        for (long i = 0; i < size; i++) {
            JsonValue val = parse(din, dataType);
            val.parent = result;
            if (prev != null) {
                prev.next = val;
                result.size++;
            } else {
                result.child = val;
                result.size = 1;
            }
            prev = val;
        }
        return result;
    }

    /* access modifiers changed from: protected */
    public String parseString(DataInputStream din, byte type) throws IOException {
        return parseString(din, false, type);
    }

    /* access modifiers changed from: protected */
    public String parseString(DataInputStream din, boolean sOptional, byte type) throws IOException {
        long size = -1;
        if (type == 83) {
            size = parseSize(din, true, -1);
        } else if (type == 115) {
            size = (long) readUChar(din);
        } else if (sOptional) {
            size = parseSize(din, type, false, -1);
        }
        if (size >= 0) {
            return size > 0 ? readString(din, size) : BuildConfig.FLAVOR;
        }
        throw new GdxRuntimeException("Unrecognized data type, string expected");
    }

    /* access modifiers changed from: protected */
    public long parseSize(DataInputStream din, boolean useIntOnError, long defaultValue) throws IOException {
        return parseSize(din, din.readByte(), useIntOnError, defaultValue);
    }

    /* access modifiers changed from: protected */
    public long parseSize(DataInputStream din, byte type, boolean useIntOnError, long defaultValue) throws IOException {
        if (type == 105) {
            return (long) readUChar(din);
        }
        if (type == 73) {
            return (long) readUShort(din);
        }
        if (type == 108) {
            return readUInt(din);
        }
        if (type == 76) {
            return din.readLong();
        }
        if (useIntOnError) {
            return (((long) (((short) type) & 255)) << 24) | (((long) (((short) din.readByte()) & 255)) << 16) | (((long) (((short) din.readByte()) & 255)) << 8) | ((long) (((short) din.readByte()) & 255));
        }
        return defaultValue;
    }

    /* access modifiers changed from: protected */
    public short readUChar(DataInputStream din) throws IOException {
        return (short) (((short) din.readByte()) & 255);
    }

    /* access modifiers changed from: protected */
    public int readUShort(DataInputStream din) throws IOException {
        return din.readShort() & 65535;
    }

    /* access modifiers changed from: protected */
    public long readUInt(DataInputStream din) throws IOException {
        return ((long) din.readInt()) & -1;
    }

    /* access modifiers changed from: protected */
    public String readString(DataInputStream din, long size) throws IOException {
        byte[] data = new byte[((int) size)];
        din.readFully(data);
        return new String(data, "UTF-8");
    }
}
