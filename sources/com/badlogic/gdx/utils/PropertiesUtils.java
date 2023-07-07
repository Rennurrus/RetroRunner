package com.badlogic.gdx.utils;

import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.utils.ObjectMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Date;

public final class PropertiesUtils {
    private static final int CONTINUE = 3;
    private static final int IGNORE = 5;
    private static final int KEY_DONE = 4;
    private static final String LINE_SEPARATOR = "\n";
    private static final int NONE = 0;
    private static final int SLASH = 1;
    private static final int UNICODE = 2;

    private PropertiesUtils() {
    }

    public static void load(ObjectMap<String, String> properties, Reader reader) throws IOException {
        char nextChar;
        ObjectMap<String, String> objectMap = properties;
        Reader reader2 = reader;
        if (objectMap == null) {
            throw new NullPointerException("ObjectMap cannot be null");
        } else if (reader2 != null) {
            int mode = 0;
            int unicode = 0;
            int count = 0;
            char[] buf = new char[40];
            int offset = 0;
            int keyLength = -1;
            boolean firstChar = true;
            BufferedReader br = new BufferedReader(reader2);
            while (true) {
                int intVal = br.read();
                if (intVal != -1) {
                    char nextChar2 = (char) intVal;
                    if (offset == buf.length) {
                        char[] newBuf = new char[(buf.length * 2)];
                        System.arraycopy(buf, 0, newBuf, 0, offset);
                        buf = newBuf;
                    }
                    if (mode == 2) {
                        int digit = Character.digit(nextChar2, 16);
                        if (digit >= 0) {
                            unicode = (unicode << 4) + digit;
                            count++;
                            if (count < 4) {
                            }
                        } else if (count <= 4) {
                            throw new IllegalArgumentException("Invalid Unicode sequence: illegal character");
                        }
                        mode = 0;
                        int offset2 = offset + 1;
                        buf[offset] = (char) unicode;
                        if (nextChar2 != 10) {
                            offset = offset2;
                        } else {
                            offset = offset2;
                        }
                    }
                    if (mode == 1) {
                        mode = 0;
                        if (nextChar2 == 10) {
                            mode = 5;
                        } else if (nextChar2 == 13) {
                            mode = 3;
                        } else if (nextChar2 == 'b') {
                            nextChar2 = 8;
                        } else if (nextChar2 == 'f') {
                            nextChar2 = 12;
                        } else if (nextChar2 == 'n') {
                            nextChar2 = 10;
                        } else if (nextChar2 == 'r') {
                            nextChar2 = 13;
                        } else if (nextChar2 == 't') {
                            nextChar2 = 9;
                        } else if (nextChar2 == 'u') {
                            mode = 2;
                            count = 0;
                            unicode = 0;
                        }
                    } else {
                        if (nextChar2 != 10) {
                            if (nextChar2 != 13) {
                                if (nextChar2 == '!' || nextChar2 == '#') {
                                    if (firstChar) {
                                        do {
                                            int intVal2 = br.read();
                                            if (intVal2 == -1 || (nextChar = (char) intVal2) == 13) {
                                                break;
                                            }
                                        } while (nextChar != 10);
                                    }
                                } else if (nextChar2 == ':' || nextChar2 == '=') {
                                    if (keyLength == -1) {
                                        mode = 0;
                                        keyLength = offset;
                                    }
                                } else if (nextChar2 == '\\') {
                                    if (mode == 4) {
                                        keyLength = offset;
                                    }
                                    mode = 1;
                                }
                                if (Character.isSpace(nextChar2)) {
                                    if (mode == 3) {
                                        mode = 5;
                                    }
                                    if (offset != 0 && offset != keyLength && mode != 5) {
                                        if (keyLength == -1) {
                                            mode = 4;
                                        }
                                    }
                                }
                                if (mode == 5 || mode == 3) {
                                    mode = 0;
                                }
                            }
                        } else if (mode == 3) {
                            mode = 5;
                        }
                        mode = 0;
                        firstChar = true;
                        if (offset > 0 || (offset == 0 && keyLength == 0)) {
                            if (keyLength == -1) {
                                keyLength = offset;
                            }
                            String temp = new String(buf, 0, offset);
                            objectMap.put(temp.substring(0, keyLength), temp.substring(keyLength));
                        }
                        keyLength = -1;
                        offset = 0;
                    }
                    firstChar = false;
                    if (mode == 4) {
                        keyLength = offset;
                        mode = 0;
                    }
                    buf[offset] = nextChar2;
                    offset++;
                } else if (mode != 2 || count > 4) {
                    if (keyLength == -1 && offset > 0) {
                        keyLength = offset;
                    }
                    if (keyLength >= 0) {
                        String temp2 = new String(buf, 0, offset);
                        String key = temp2.substring(0, keyLength);
                        String value = temp2.substring(keyLength);
                        if (mode == 1) {
                            value = value + "\u0000";
                        }
                        objectMap.put(key, value);
                        return;
                    }
                    return;
                } else {
                    throw new IllegalArgumentException("Invalid Unicode sequence: expected format \\uxxxx");
                }
            }
        } else {
            throw new NullPointerException("Reader cannot be null");
        }
    }

    public static void store(ObjectMap<String, String> properties, Writer writer, String comment) throws IOException {
        storeImpl(properties, writer, comment, false);
    }

    private static void storeImpl(ObjectMap<String, String> properties, Writer writer, String comment, boolean escapeUnicode) throws IOException {
        if (comment != null) {
            writeComment(writer, comment);
        }
        writer.write("#");
        writer.write(new Date().toString());
        writer.write(LINE_SEPARATOR);
        StringBuilder sb = new StringBuilder((int) HttpStatus.SC_OK);
        ObjectMap.Entries<String, String> it = properties.entries().iterator();
        while (it.hasNext()) {
            ObjectMap.Entry<String, String> entry = (ObjectMap.Entry) it.next();
            dumpString(sb, (String) entry.key, true, escapeUnicode);
            sb.append('=');
            dumpString(sb, (String) entry.value, false, escapeUnicode);
            writer.write(LINE_SEPARATOR);
            writer.write(sb.toString());
            sb.setLength(0);
        }
        writer.flush();
    }

    private static void dumpString(StringBuilder outBuffer, String string, boolean escapeSpace, boolean escapeUnicode) {
        int len = string.length();
        for (int i = 0; i < len; i++) {
            char ch = string.charAt(i);
            if (ch > '=' && ch < 127) {
                outBuffer.append(ch == '\\' ? "\\\\" : Character.valueOf(ch));
            } else if (ch == 9) {
                outBuffer.append("\\t");
            } else if (ch == 10) {
                outBuffer.append("\\n");
            } else if (ch == 12) {
                outBuffer.append("\\f");
            } else if (ch == 13) {
                outBuffer.append("\\r");
            } else if (ch != ' ') {
                if (ch == '!' || ch == '#' || ch == ':' || ch == '=') {
                    outBuffer.append('\\').append(ch);
                } else {
                    if ((ch < ' ' || ch > '~') && escapeUnicode) {
                        String hex = Integer.toHexString(ch);
                        outBuffer.append("\\u");
                        for (int j = 0; j < 4 - hex.length(); j++) {
                            outBuffer.append('0');
                        }
                        outBuffer.append(hex);
                    } else {
                        outBuffer.append(ch);
                    }
                }
            } else if (i == 0 || escapeSpace) {
                outBuffer.append("\\ ");
            } else {
                outBuffer.append(ch);
            }
        }
    }

    private static void writeComment(Writer writer, String comment) throws IOException {
        writer.write("#");
        int len = comment.length();
        int curIndex = 0;
        int lastIndex = 0;
        while (curIndex < len) {
            char c = comment.charAt(curIndex);
            if (c > 255 || c == 10 || c == 13) {
                if (lastIndex != curIndex) {
                    writer.write(comment.substring(lastIndex, curIndex));
                }
                if (c > 255) {
                    String hex = Integer.toHexString(c);
                    writer.write("\\u");
                    for (int j = 0; j < 4 - hex.length(); j++) {
                        writer.write(48);
                    }
                    writer.write(hex);
                } else {
                    writer.write(LINE_SEPARATOR);
                    if (c == 13 && curIndex != len - 1 && comment.charAt(curIndex + 1) == 10) {
                        curIndex++;
                    }
                    if (curIndex == len - 1 || !(comment.charAt(curIndex + 1) == '#' || comment.charAt(curIndex + 1) == '!')) {
                        writer.write("#");
                    }
                }
                lastIndex = curIndex + 1;
            }
            curIndex++;
        }
        if (lastIndex != curIndex) {
            writer.write(comment.substring(lastIndex, curIndex));
        }
        writer.write(LINE_SEPARATOR);
    }
}
