package com.badlogic.gdx.utils;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.JsonWriter;
import com.twi.game.BuildConfig;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class JsonValue implements Iterable<JsonValue> {
    public JsonValue child;
    private double doubleValue;
    private long longValue;
    public String name;
    public JsonValue next;
    public JsonValue parent;
    public JsonValue prev;
    public int size;
    private String stringValue;
    private ValueType type;

    public static class PrettyPrintSettings {
        public JsonWriter.OutputType outputType;
        public int singleLineColumns;
        public boolean wrapNumericArrays;
    }

    public enum ValueType {
        object,
        array,
        stringValue,
        doubleValue,
        longValue,
        booleanValue,
        nullValue
    }

    public JsonValue(ValueType type2) {
        this.type = type2;
    }

    public JsonValue(String value) {
        set(value);
    }

    public JsonValue(double value) {
        set(value, (String) null);
    }

    public JsonValue(long value) {
        set(value, (String) null);
    }

    public JsonValue(double value, String stringValue2) {
        set(value, stringValue2);
    }

    public JsonValue(long value, String stringValue2) {
        set(value, stringValue2);
    }

    public JsonValue(boolean value) {
        set(value);
    }

    public JsonValue get(int index) {
        JsonValue current = this.child;
        while (current != null && index > 0) {
            index--;
            current = current.next;
        }
        return current;
    }

    public JsonValue get(String name2) {
        JsonValue current = this.child;
        while (current != null) {
            String str = current.name;
            if (str != null && str.equalsIgnoreCase(name2)) {
                break;
            }
            current = current.next;
        }
        return current;
    }

    public boolean has(String name2) {
        return get(name2) != null;
    }

    public JsonValue require(int index) {
        JsonValue current = this.child;
        while (current != null && index > 0) {
            index--;
            current = current.next;
        }
        if (current != null) {
            return current;
        }
        throw new IllegalArgumentException("Child not found with index: " + index);
    }

    public JsonValue require(String name2) {
        JsonValue current = this.child;
        while (current != null) {
            String str = current.name;
            if (str != null && str.equalsIgnoreCase(name2)) {
                break;
            }
            current = current.next;
        }
        if (current != null) {
            return current;
        }
        throw new IllegalArgumentException("Child not found with name: " + name2);
    }

    public JsonValue remove(int index) {
        JsonValue child2 = get(index);
        if (child2 == null) {
            return null;
        }
        JsonValue jsonValue = child2.prev;
        if (jsonValue == null) {
            this.child = child2.next;
            JsonValue jsonValue2 = this.child;
            if (jsonValue2 != null) {
                jsonValue2.prev = null;
            }
        } else {
            jsonValue.next = child2.next;
            JsonValue jsonValue3 = child2.next;
            if (jsonValue3 != null) {
                jsonValue3.prev = jsonValue;
            }
        }
        this.size--;
        return child2;
    }

    public JsonValue remove(String name2) {
        JsonValue child2 = get(name2);
        if (child2 == null) {
            return null;
        }
        JsonValue jsonValue = child2.prev;
        if (jsonValue == null) {
            this.child = child2.next;
            JsonValue jsonValue2 = this.child;
            if (jsonValue2 != null) {
                jsonValue2.prev = null;
            }
        } else {
            jsonValue.next = child2.next;
            JsonValue jsonValue3 = child2.next;
            if (jsonValue3 != null) {
                jsonValue3.prev = jsonValue;
            }
        }
        this.size--;
        return child2;
    }

    public boolean notEmpty() {
        return this.size > 0;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    @Deprecated
    public int size() {
        return this.size;
    }

    /* renamed from: com.badlogic.gdx.utils.JsonValue$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$badlogic$gdx$utils$JsonValue$ValueType = new int[ValueType.values().length];

        static {
            try {
                $SwitchMap$com$badlogic$gdx$utils$JsonValue$ValueType[ValueType.stringValue.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$badlogic$gdx$utils$JsonValue$ValueType[ValueType.doubleValue.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$badlogic$gdx$utils$JsonValue$ValueType[ValueType.longValue.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$badlogic$gdx$utils$JsonValue$ValueType[ValueType.booleanValue.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$badlogic$gdx$utils$JsonValue$ValueType[ValueType.nullValue.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    public String asString() {
        int i = AnonymousClass1.$SwitchMap$com$badlogic$gdx$utils$JsonValue$ValueType[this.type.ordinal()];
        if (i == 1) {
            return this.stringValue;
        }
        if (i == 2) {
            String str = this.stringValue;
            return str != null ? str : Double.toString(this.doubleValue);
        } else if (i == 3) {
            String str2 = this.stringValue;
            return str2 != null ? str2 : Long.toString(this.longValue);
        } else if (i == 4) {
            return this.longValue != 0 ? "true" : "false";
        } else {
            if (i == 5) {
                return null;
            }
            throw new IllegalStateException("Value cannot be converted to string: " + this.type);
        }
    }

    public float asFloat() {
        int i = AnonymousClass1.$SwitchMap$com$badlogic$gdx$utils$JsonValue$ValueType[this.type.ordinal()];
        if (i == 1) {
            return Float.parseFloat(this.stringValue);
        }
        if (i == 2) {
            return (float) this.doubleValue;
        }
        if (i == 3) {
            return (float) this.longValue;
        }
        if (i == 4) {
            return this.longValue != 0 ? 1.0f : 0.0f;
        }
        throw new IllegalStateException("Value cannot be converted to float: " + this.type);
    }

    public double asDouble() {
        int i = AnonymousClass1.$SwitchMap$com$badlogic$gdx$utils$JsonValue$ValueType[this.type.ordinal()];
        if (i == 1) {
            return Double.parseDouble(this.stringValue);
        }
        if (i == 2) {
            return this.doubleValue;
        }
        if (i == 3) {
            return (double) this.longValue;
        }
        if (i == 4) {
            return this.longValue != 0 ? 1.0d : 0.0d;
        }
        throw new IllegalStateException("Value cannot be converted to double: " + this.type);
    }

    public long asLong() {
        int i = AnonymousClass1.$SwitchMap$com$badlogic$gdx$utils$JsonValue$ValueType[this.type.ordinal()];
        if (i == 1) {
            return Long.parseLong(this.stringValue);
        }
        if (i == 2) {
            return (long) this.doubleValue;
        }
        if (i == 3) {
            return this.longValue;
        }
        if (i == 4) {
            return this.longValue != 0 ? 1 : 0;
        }
        throw new IllegalStateException("Value cannot be converted to long: " + this.type);
    }

    public int asInt() {
        int i = AnonymousClass1.$SwitchMap$com$badlogic$gdx$utils$JsonValue$ValueType[this.type.ordinal()];
        if (i == 1) {
            return Integer.parseInt(this.stringValue);
        }
        if (i == 2) {
            return (int) this.doubleValue;
        }
        if (i == 3) {
            return (int) this.longValue;
        }
        if (i != 4) {
            throw new IllegalStateException("Value cannot be converted to int: " + this.type);
        } else if (this.longValue != 0) {
            return 1;
        } else {
            return 0;
        }
    }

    public boolean asBoolean() {
        int i = AnonymousClass1.$SwitchMap$com$badlogic$gdx$utils$JsonValue$ValueType[this.type.ordinal()];
        if (i == 1) {
            return this.stringValue.equalsIgnoreCase("true");
        }
        if (i != 2) {
            if (i != 3) {
                if (i != 4) {
                    throw new IllegalStateException("Value cannot be converted to boolean: " + this.type);
                } else if (this.longValue != 0) {
                    return true;
                } else {
                    return false;
                }
            } else if (this.longValue != 0) {
                return true;
            } else {
                return false;
            }
        } else if (this.doubleValue != 0.0d) {
            return true;
        } else {
            return false;
        }
    }

    public byte asByte() {
        int i = AnonymousClass1.$SwitchMap$com$badlogic$gdx$utils$JsonValue$ValueType[this.type.ordinal()];
        if (i == 1) {
            return Byte.parseByte(this.stringValue);
        }
        if (i == 2) {
            return (byte) ((int) this.doubleValue);
        }
        if (i == 3) {
            return (byte) ((int) this.longValue);
        }
        if (i != 4) {
            throw new IllegalStateException("Value cannot be converted to byte: " + this.type);
        } else if (this.longValue != 0) {
            return 1;
        } else {
            return 0;
        }
    }

    public short asShort() {
        int i = AnonymousClass1.$SwitchMap$com$badlogic$gdx$utils$JsonValue$ValueType[this.type.ordinal()];
        if (i == 1) {
            return Short.parseShort(this.stringValue);
        }
        if (i == 2) {
            return (short) ((int) this.doubleValue);
        }
        if (i == 3) {
            return (short) ((int) this.longValue);
        }
        if (i != 4) {
            throw new IllegalStateException("Value cannot be converted to short: " + this.type);
        } else if (this.longValue != 0) {
            return 1;
        } else {
            return 0;
        }
    }

    public char asChar() {
        int i = AnonymousClass1.$SwitchMap$com$badlogic$gdx$utils$JsonValue$ValueType[this.type.ordinal()];
        if (i != 1) {
            if (i == 2) {
                return (char) ((int) this.doubleValue);
            }
            if (i == 3) {
                return (char) ((int) this.longValue);
            }
            if (i != 4) {
                throw new IllegalStateException("Value cannot be converted to char: " + this.type);
            } else if (this.longValue != 0) {
                return 1;
            } else {
                return 0;
            }
        } else if (this.stringValue.length() == 0) {
            return 0;
        } else {
            return this.stringValue.charAt(0);
        }
    }

    public String[] asStringArray() {
        String v;
        if (this.type == ValueType.array) {
            String[] array = new String[this.size];
            int i = 0;
            JsonValue value = this.child;
            while (value != null) {
                int i2 = AnonymousClass1.$SwitchMap$com$badlogic$gdx$utils$JsonValue$ValueType[value.type.ordinal()];
                if (i2 == 1) {
                    v = value.stringValue;
                } else if (i2 == 2) {
                    v = this.stringValue;
                    if (v == null) {
                        v = Double.toString(value.doubleValue);
                    }
                } else if (i2 == 3) {
                    v = this.stringValue;
                    if (v == null) {
                        v = Long.toString(value.longValue);
                    }
                } else if (i2 == 4) {
                    v = value.longValue != 0 ? "true" : "false";
                } else if (i2 == 5) {
                    v = null;
                } else {
                    throw new IllegalStateException("Value cannot be converted to string: " + value.type);
                }
                array[i] = v;
                value = value.next;
                i++;
            }
            return array;
        }
        throw new IllegalStateException("Value is not an array: " + this.type);
    }

    public float[] asFloatArray() {
        float v;
        if (this.type == ValueType.array) {
            float[] array = new float[this.size];
            int i = 0;
            JsonValue value = this.child;
            while (value != null) {
                int i2 = AnonymousClass1.$SwitchMap$com$badlogic$gdx$utils$JsonValue$ValueType[value.type.ordinal()];
                if (i2 == 1) {
                    v = Float.parseFloat(value.stringValue);
                } else if (i2 == 2) {
                    v = (float) value.doubleValue;
                } else if (i2 == 3) {
                    v = (float) value.longValue;
                } else if (i2 == 4) {
                    v = value.longValue != 0 ? 1.0f : 0.0f;
                } else {
                    throw new IllegalStateException("Value cannot be converted to float: " + value.type);
                }
                array[i] = v;
                value = value.next;
                i++;
            }
            return array;
        }
        throw new IllegalStateException("Value is not an array: " + this.type);
    }

    public double[] asDoubleArray() {
        double v;
        if (this.type == ValueType.array) {
            double[] array = new double[this.size];
            int i = 0;
            JsonValue value = this.child;
            while (value != null) {
                int i2 = AnonymousClass1.$SwitchMap$com$badlogic$gdx$utils$JsonValue$ValueType[value.type.ordinal()];
                if (i2 == 1) {
                    v = Double.parseDouble(value.stringValue);
                } else if (i2 == 2) {
                    v = value.doubleValue;
                } else if (i2 == 3) {
                    v = (double) value.longValue;
                } else if (i2 == 4) {
                    v = value.longValue != 0 ? 1.0d : 0.0d;
                } else {
                    throw new IllegalStateException("Value cannot be converted to double: " + value.type);
                }
                array[i] = v;
                value = value.next;
                i++;
            }
            return array;
        }
        throw new IllegalStateException("Value is not an array: " + this.type);
    }

    public long[] asLongArray() {
        long v;
        if (this.type == ValueType.array) {
            long[] array = new long[this.size];
            int i = 0;
            JsonValue value = this.child;
            while (value != null) {
                int i2 = AnonymousClass1.$SwitchMap$com$badlogic$gdx$utils$JsonValue$ValueType[value.type.ordinal()];
                if (i2 == 1) {
                    v = Long.parseLong(value.stringValue);
                } else if (i2 == 2) {
                    v = (long) value.doubleValue;
                } else if (i2 == 3) {
                    v = value.longValue;
                } else if (i2 == 4) {
                    long j = 0;
                    if (value.longValue != 0) {
                        j = 1;
                    }
                    v = j;
                } else {
                    throw new IllegalStateException("Value cannot be converted to long: " + value.type);
                }
                array[i] = v;
                value = value.next;
                i++;
            }
            return array;
        }
        throw new IllegalStateException("Value is not an array: " + this.type);
    }

    public int[] asIntArray() {
        int v;
        if (this.type == ValueType.array) {
            int[] array = new int[this.size];
            int i = 0;
            JsonValue value = this.child;
            while (value != null) {
                int i2 = AnonymousClass1.$SwitchMap$com$badlogic$gdx$utils$JsonValue$ValueType[value.type.ordinal()];
                int i3 = 1;
                if (i2 == 1) {
                    v = Integer.parseInt(value.stringValue);
                } else if (i2 == 2) {
                    v = (int) value.doubleValue;
                } else if (i2 == 3) {
                    v = (int) value.longValue;
                } else if (i2 == 4) {
                    if (value.longValue == 0) {
                        i3 = 0;
                    }
                    v = i3;
                } else {
                    throw new IllegalStateException("Value cannot be converted to int: " + value.type);
                }
                array[i] = v;
                value = value.next;
                i++;
            }
            return array;
        }
        throw new IllegalStateException("Value is not an array: " + this.type);
    }

    public boolean[] asBooleanArray() {
        boolean v;
        if (this.type == ValueType.array) {
            boolean[] array = new boolean[this.size];
            int i = 0;
            JsonValue value = this.child;
            while (value != null) {
                int i2 = AnonymousClass1.$SwitchMap$com$badlogic$gdx$utils$JsonValue$ValueType[value.type.ordinal()];
                boolean z = true;
                if (i2 == 1) {
                    v = Boolean.parseBoolean(value.stringValue);
                } else if (i2 == 2) {
                    if (value.doubleValue != 0.0d) {
                        z = false;
                    }
                    v = z;
                } else if (i2 == 3) {
                    if (value.longValue != 0) {
                        z = false;
                    }
                    v = z;
                } else if (i2 == 4) {
                    if (value.longValue == 0) {
                        z = false;
                    }
                    v = z;
                } else {
                    throw new IllegalStateException("Value cannot be converted to boolean: " + value.type);
                }
                array[i] = v;
                value = value.next;
                i++;
            }
            return array;
        }
        throw new IllegalStateException("Value is not an array: " + this.type);
    }

    public byte[] asByteArray() {
        byte v;
        if (this.type == ValueType.array) {
            byte[] array = new byte[this.size];
            int i = 0;
            JsonValue value = this.child;
            while (value != null) {
                int i2 = AnonymousClass1.$SwitchMap$com$badlogic$gdx$utils$JsonValue$ValueType[value.type.ordinal()];
                byte b = 1;
                if (i2 == 1) {
                    v = Byte.parseByte(value.stringValue);
                } else if (i2 == 2) {
                    v = (byte) ((int) value.doubleValue);
                } else if (i2 == 3) {
                    v = (byte) ((int) value.longValue);
                } else if (i2 == 4) {
                    if (value.longValue == 0) {
                        b = 0;
                    }
                    v = b;
                } else {
                    throw new IllegalStateException("Value cannot be converted to byte: " + value.type);
                }
                array[i] = v;
                value = value.next;
                i++;
            }
            return array;
        }
        throw new IllegalStateException("Value is not an array: " + this.type);
    }

    public short[] asShortArray() {
        short v;
        if (this.type == ValueType.array) {
            short[] array = new short[this.size];
            int i = 0;
            JsonValue value = this.child;
            while (value != null) {
                int i2 = AnonymousClass1.$SwitchMap$com$badlogic$gdx$utils$JsonValue$ValueType[value.type.ordinal()];
                short s = 1;
                if (i2 == 1) {
                    v = Short.parseShort(value.stringValue);
                } else if (i2 == 2) {
                    v = (short) ((int) value.doubleValue);
                } else if (i2 == 3) {
                    v = (short) ((int) value.longValue);
                } else if (i2 == 4) {
                    if (value.longValue == 0) {
                        s = 0;
                    }
                    v = s;
                } else {
                    throw new IllegalStateException("Value cannot be converted to short: " + value.type);
                }
                array[i] = v;
                value = value.next;
                i++;
            }
            return array;
        }
        throw new IllegalStateException("Value is not an array: " + this.type);
    }

    public char[] asCharArray() {
        char v;
        if (this.type == ValueType.array) {
            char[] array = new char[this.size];
            int i = 0;
            JsonValue value = this.child;
            while (value != null) {
                int i2 = AnonymousClass1.$SwitchMap$com$badlogic$gdx$utils$JsonValue$ValueType[value.type.ordinal()];
                char c = 0;
                if (i2 == 1) {
                    if (value.stringValue.length() != 0) {
                        c = value.stringValue.charAt(0);
                    }
                    v = c;
                } else if (i2 == 2) {
                    v = (char) ((int) value.doubleValue);
                } else if (i2 == 3) {
                    v = (char) ((int) value.longValue);
                } else if (i2 == 4) {
                    if (value.longValue != 0) {
                        c = 1;
                    }
                    v = c;
                } else {
                    throw new IllegalStateException("Value cannot be converted to char: " + value.type);
                }
                array[i] = v;
                value = value.next;
                i++;
            }
            return array;
        }
        throw new IllegalStateException("Value is not an array: " + this.type);
    }

    public boolean hasChild(String name2) {
        return getChild(name2) != null;
    }

    public JsonValue getChild(String name2) {
        JsonValue child2 = get(name2);
        if (child2 == null) {
            return null;
        }
        return child2.child;
    }

    public String getString(String name2, String defaultValue) {
        JsonValue child2 = get(name2);
        return (child2 == null || !child2.isValue() || child2.isNull()) ? defaultValue : child2.asString();
    }

    public float getFloat(String name2, float defaultValue) {
        JsonValue child2 = get(name2);
        return (child2 == null || !child2.isValue() || child2.isNull()) ? defaultValue : child2.asFloat();
    }

    public double getDouble(String name2, double defaultValue) {
        JsonValue child2 = get(name2);
        return (child2 == null || !child2.isValue() || child2.isNull()) ? defaultValue : child2.asDouble();
    }

    public long getLong(String name2, long defaultValue) {
        JsonValue child2 = get(name2);
        return (child2 == null || !child2.isValue() || child2.isNull()) ? defaultValue : child2.asLong();
    }

    public int getInt(String name2, int defaultValue) {
        JsonValue child2 = get(name2);
        return (child2 == null || !child2.isValue() || child2.isNull()) ? defaultValue : child2.asInt();
    }

    public boolean getBoolean(String name2, boolean defaultValue) {
        JsonValue child2 = get(name2);
        return (child2 == null || !child2.isValue() || child2.isNull()) ? defaultValue : child2.asBoolean();
    }

    public byte getByte(String name2, byte defaultValue) {
        JsonValue child2 = get(name2);
        return (child2 == null || !child2.isValue() || child2.isNull()) ? defaultValue : child2.asByte();
    }

    public short getShort(String name2, short defaultValue) {
        JsonValue child2 = get(name2);
        return (child2 == null || !child2.isValue() || child2.isNull()) ? defaultValue : child2.asShort();
    }

    public char getChar(String name2, char defaultValue) {
        JsonValue child2 = get(name2);
        return (child2 == null || !child2.isValue() || child2.isNull()) ? defaultValue : child2.asChar();
    }

    public String getString(String name2) {
        JsonValue child2 = get(name2);
        if (child2 != null) {
            return child2.asString();
        }
        throw new IllegalArgumentException("Named value not found: " + name2);
    }

    public float getFloat(String name2) {
        JsonValue child2 = get(name2);
        if (child2 != null) {
            return child2.asFloat();
        }
        throw new IllegalArgumentException("Named value not found: " + name2);
    }

    public double getDouble(String name2) {
        JsonValue child2 = get(name2);
        if (child2 != null) {
            return child2.asDouble();
        }
        throw new IllegalArgumentException("Named value not found: " + name2);
    }

    public long getLong(String name2) {
        JsonValue child2 = get(name2);
        if (child2 != null) {
            return child2.asLong();
        }
        throw new IllegalArgumentException("Named value not found: " + name2);
    }

    public int getInt(String name2) {
        JsonValue child2 = get(name2);
        if (child2 != null) {
            return child2.asInt();
        }
        throw new IllegalArgumentException("Named value not found: " + name2);
    }

    public boolean getBoolean(String name2) {
        JsonValue child2 = get(name2);
        if (child2 != null) {
            return child2.asBoolean();
        }
        throw new IllegalArgumentException("Named value not found: " + name2);
    }

    public byte getByte(String name2) {
        JsonValue child2 = get(name2);
        if (child2 != null) {
            return child2.asByte();
        }
        throw new IllegalArgumentException("Named value not found: " + name2);
    }

    public short getShort(String name2) {
        JsonValue child2 = get(name2);
        if (child2 != null) {
            return child2.asShort();
        }
        throw new IllegalArgumentException("Named value not found: " + name2);
    }

    public char getChar(String name2) {
        JsonValue child2 = get(name2);
        if (child2 != null) {
            return child2.asChar();
        }
        throw new IllegalArgumentException("Named value not found: " + name2);
    }

    public String getString(int index) {
        JsonValue child2 = get(index);
        if (child2 != null) {
            return child2.asString();
        }
        throw new IllegalArgumentException("Indexed value not found: " + this.name);
    }

    public float getFloat(int index) {
        JsonValue child2 = get(index);
        if (child2 != null) {
            return child2.asFloat();
        }
        throw new IllegalArgumentException("Indexed value not found: " + this.name);
    }

    public double getDouble(int index) {
        JsonValue child2 = get(index);
        if (child2 != null) {
            return child2.asDouble();
        }
        throw new IllegalArgumentException("Indexed value not found: " + this.name);
    }

    public long getLong(int index) {
        JsonValue child2 = get(index);
        if (child2 != null) {
            return child2.asLong();
        }
        throw new IllegalArgumentException("Indexed value not found: " + this.name);
    }

    public int getInt(int index) {
        JsonValue child2 = get(index);
        if (child2 != null) {
            return child2.asInt();
        }
        throw new IllegalArgumentException("Indexed value not found: " + this.name);
    }

    public boolean getBoolean(int index) {
        JsonValue child2 = get(index);
        if (child2 != null) {
            return child2.asBoolean();
        }
        throw new IllegalArgumentException("Indexed value not found: " + this.name);
    }

    public byte getByte(int index) {
        JsonValue child2 = get(index);
        if (child2 != null) {
            return child2.asByte();
        }
        throw new IllegalArgumentException("Indexed value not found: " + this.name);
    }

    public short getShort(int index) {
        JsonValue child2 = get(index);
        if (child2 != null) {
            return child2.asShort();
        }
        throw new IllegalArgumentException("Indexed value not found: " + this.name);
    }

    public char getChar(int index) {
        JsonValue child2 = get(index);
        if (child2 != null) {
            return child2.asChar();
        }
        throw new IllegalArgumentException("Indexed value not found: " + this.name);
    }

    public ValueType type() {
        return this.type;
    }

    public void setType(ValueType type2) {
        if (type2 != null) {
            this.type = type2;
            return;
        }
        throw new IllegalArgumentException("type cannot be null.");
    }

    public boolean isArray() {
        return this.type == ValueType.array;
    }

    public boolean isObject() {
        return this.type == ValueType.object;
    }

    public boolean isString() {
        return this.type == ValueType.stringValue;
    }

    public boolean isNumber() {
        return this.type == ValueType.doubleValue || this.type == ValueType.longValue;
    }

    public boolean isDouble() {
        return this.type == ValueType.doubleValue;
    }

    public boolean isLong() {
        return this.type == ValueType.longValue;
    }

    public boolean isBoolean() {
        return this.type == ValueType.booleanValue;
    }

    public boolean isNull() {
        return this.type == ValueType.nullValue;
    }

    public boolean isValue() {
        int i = AnonymousClass1.$SwitchMap$com$badlogic$gdx$utils$JsonValue$ValueType[this.type.ordinal()];
        if (i == 1 || i == 2 || i == 3 || i == 4 || i == 5) {
            return true;
        }
        return false;
    }

    public String name() {
        return this.name;
    }

    public void setName(String name2) {
        this.name = name2;
    }

    public JsonValue parent() {
        return this.parent;
    }

    public JsonValue child() {
        return this.child;
    }

    public void addChild(String name2, JsonValue value) {
        if (name2 != null) {
            value.name = name2;
            addChild(value);
            return;
        }
        throw new IllegalArgumentException("name cannot be null.");
    }

    public void addChild(JsonValue value) {
        value.parent = this;
        JsonValue current = this.child;
        if (current == null) {
            this.child = value;
            return;
        }
        while (current.next != null) {
            current = current.next;
        }
        current.next = value;
    }

    public JsonValue next() {
        return this.next;
    }

    public void setNext(JsonValue next2) {
        this.next = next2;
    }

    public JsonValue prev() {
        return this.prev;
    }

    public void setPrev(JsonValue prev2) {
        this.prev = prev2;
    }

    public void set(String value) {
        this.stringValue = value;
        this.type = value == null ? ValueType.nullValue : ValueType.stringValue;
    }

    public void set(double value, String stringValue2) {
        this.doubleValue = value;
        this.longValue = (long) value;
        this.stringValue = stringValue2;
        this.type = ValueType.doubleValue;
    }

    public void set(long value, String stringValue2) {
        this.longValue = value;
        this.doubleValue = (double) value;
        this.stringValue = stringValue2;
        this.type = ValueType.longValue;
    }

    public void set(boolean value) {
        this.longValue = value ? 1 : 0;
        this.type = ValueType.booleanValue;
    }

    public String toJson(JsonWriter.OutputType outputType) {
        if (isValue()) {
            return asString();
        }
        StringBuilder buffer = new StringBuilder((int) GL20.GL_NEVER);
        json(this, buffer, outputType);
        return buffer.toString();
    }

    private void json(JsonValue object, StringBuilder buffer, JsonWriter.OutputType outputType) {
        if (object.isObject()) {
            if (object.child == null) {
                buffer.append("{}");
                return;
            }
            int length = buffer.length();
            buffer.append('{');
            for (JsonValue child2 = object.child; child2 != null; child2 = child2.next) {
                buffer.append(outputType.quoteName(child2.name));
                buffer.append(':');
                json(child2, buffer, outputType);
                if (child2.next != null) {
                    buffer.append(',');
                }
            }
            buffer.append('}');
        } else if (object.isArray()) {
            if (object.child == null) {
                buffer.append("[]");
                return;
            }
            int length2 = buffer.length();
            buffer.append('[');
            for (JsonValue child3 = object.child; child3 != null; child3 = child3.next) {
                json(child3, buffer, outputType);
                if (child3.next != null) {
                    buffer.append(',');
                }
            }
            buffer.append(']');
        } else if (object.isString()) {
            buffer.append(outputType.quoteValue(object.asString()));
        } else if (object.isDouble()) {
            double doubleValue2 = object.asDouble();
            long longValue2 = object.asLong();
            buffer.append(doubleValue2 == ((double) longValue2) ? (double) longValue2 : doubleValue2);
        } else if (object.isLong()) {
            buffer.append(object.asLong());
        } else if (object.isBoolean()) {
            buffer.append(object.asBoolean());
        } else if (object.isNull()) {
            buffer.append("null");
        } else {
            throw new SerializationException("Unknown object type: " + object);
        }
    }

    public String toString() {
        String str;
        if (!isValue()) {
            StringBuilder sb = new StringBuilder();
            if (this.name == null) {
                str = BuildConfig.FLAVOR;
            } else {
                str = this.name + ": ";
            }
            sb.append(str);
            sb.append(prettyPrint(JsonWriter.OutputType.minimal, 0));
            return sb.toString();
        } else if (this.name == null) {
            return asString();
        } else {
            return this.name + ": " + asString();
        }
    }

    public String prettyPrint(JsonWriter.OutputType outputType, int singleLineColumns) {
        PrettyPrintSettings settings = new PrettyPrintSettings();
        settings.outputType = outputType;
        settings.singleLineColumns = singleLineColumns;
        return prettyPrint(settings);
    }

    public String prettyPrint(PrettyPrintSettings settings) {
        StringBuilder buffer = new StringBuilder((int) GL20.GL_NEVER);
        prettyPrint(this, buffer, 0, settings);
        return buffer.toString();
    }

    private void prettyPrint(JsonValue object, StringBuilder buffer, int indent, PrettyPrintSettings settings) {
        JsonWriter.OutputType outputType = settings.outputType;
        boolean wrap = true;
        if (object.isObject()) {
            if (object.child == null) {
                buffer.append("{}");
                return;
            }
            boolean newLines = !isFlat(object);
            int start = buffer.length();
            loop0:
            while (true) {
                buffer.append(newLines ? "{\n" : "{ ");
                JsonValue child2 = object.child;
                while (child2 != null) {
                    if (newLines) {
                        indent(indent, buffer);
                    }
                    buffer.append(outputType.quoteName(child2.name));
                    buffer.append(": ");
                    prettyPrint(child2, buffer, indent + 1, settings);
                    if ((!newLines || outputType != JsonWriter.OutputType.minimal) && child2.next != null) {
                        buffer.append(',');
                    }
                    buffer.append(newLines ? 10 : ' ');
                    if (newLines || buffer.length() - start <= settings.singleLineColumns) {
                        child2 = child2.next;
                    } else {
                        buffer.setLength(start);
                        newLines = true;
                    }
                }
                break loop0;
            }
            if (newLines) {
                indent(indent - 1, buffer);
            }
            buffer.append('}');
        } else if (object.isArray()) {
            if (object.child == null) {
                buffer.append("[]");
                return;
            }
            boolean newLines2 = !isFlat(object);
            if (!settings.wrapNumericArrays && isNumeric(object)) {
                wrap = false;
            }
            int start2 = buffer.length();
            loop2:
            while (true) {
                buffer.append(newLines2 ? "[\n" : "[ ");
                JsonValue child3 = object.child;
                while (child3 != null) {
                    if (newLines2) {
                        indent(indent, buffer);
                    }
                    prettyPrint(child3, buffer, indent + 1, settings);
                    if ((!newLines2 || outputType != JsonWriter.OutputType.minimal) && child3.next != null) {
                        buffer.append(',');
                    }
                    buffer.append(newLines2 ? 10 : ' ');
                    if (!wrap || newLines2 || buffer.length() - start2 <= settings.singleLineColumns) {
                        child3 = child3.next;
                    } else {
                        buffer.setLength(start2);
                        newLines2 = true;
                    }
                }
                break loop2;
            }
            if (newLines2) {
                indent(indent - 1, buffer);
            }
            buffer.append(']');
        } else if (object.isString()) {
            buffer.append(outputType.quoteValue(object.asString()));
        } else if (object.isDouble()) {
            double doubleValue2 = object.asDouble();
            long longValue2 = object.asLong();
            buffer.append(doubleValue2 == ((double) longValue2) ? (double) longValue2 : doubleValue2);
        } else if (object.isLong()) {
            buffer.append(object.asLong());
        } else if (object.isBoolean()) {
            buffer.append(object.asBoolean());
        } else if (object.isNull()) {
            buffer.append("null");
        } else {
            throw new SerializationException("Unknown object type: " + object);
        }
    }

    public void prettyPrint(JsonWriter.OutputType outputType, Writer writer) throws IOException {
        PrettyPrintSettings settings = new PrettyPrintSettings();
        settings.outputType = outputType;
        prettyPrint(this, writer, 0, settings);
    }

    private void prettyPrint(JsonValue object, Writer writer, int indent, PrettyPrintSettings settings) throws IOException {
        JsonWriter.OutputType outputType = settings.outputType;
        boolean z = true;
        if (object.isObject()) {
            if (object.child == null) {
                writer.append("{}");
                return;
            }
            if (isFlat(object) && object.size <= 6) {
                z = false;
            }
            boolean newLines = z;
            writer.append(newLines ? "{\n" : "{ ");
            for (JsonValue child2 = object.child; child2 != null; child2 = child2.next) {
                if (newLines) {
                    indent(indent, writer);
                }
                writer.append(outputType.quoteName(child2.name));
                writer.append(": ");
                prettyPrint(child2, writer, indent + 1, settings);
                if ((!newLines || outputType != JsonWriter.OutputType.minimal) && child2.next != null) {
                    writer.append(',');
                }
                writer.append(newLines ? 10 : ' ');
            }
            if (newLines) {
                indent(indent - 1, writer);
            }
            writer.append('}');
        } else if (object.isArray()) {
            if (object.child == null) {
                writer.append("[]");
                return;
            }
            boolean newLines2 = !isFlat(object);
            writer.append(newLines2 ? "[\n" : "[ ");
            for (JsonValue child3 = object.child; child3 != null; child3 = child3.next) {
                if (newLines2) {
                    indent(indent, writer);
                }
                prettyPrint(child3, writer, indent + 1, settings);
                if ((!newLines2 || outputType != JsonWriter.OutputType.minimal) && child3.next != null) {
                    writer.append(',');
                }
                writer.append(newLines2 ? 10 : ' ');
            }
            if (newLines2) {
                indent(indent - 1, writer);
            }
            writer.append(']');
        } else if (object.isString()) {
            writer.append(outputType.quoteValue(object.asString()));
        } else if (object.isDouble()) {
            double doubleValue2 = object.asDouble();
            long longValue2 = object.asLong();
            writer.append(Double.toString(doubleValue2 == ((double) longValue2) ? (double) longValue2 : doubleValue2));
        } else if (object.isLong()) {
            writer.append(Long.toString(object.asLong()));
        } else if (object.isBoolean()) {
            writer.append(Boolean.toString(object.asBoolean()));
        } else if (object.isNull()) {
            writer.append("null");
        } else {
            throw new SerializationException("Unknown object type: " + object);
        }
    }

    private static boolean isFlat(JsonValue object) {
        for (JsonValue child2 = object.child; child2 != null; child2 = child2.next) {
            if (child2.isObject() || child2.isArray()) {
                return false;
            }
        }
        return true;
    }

    private static boolean isNumeric(JsonValue object) {
        for (JsonValue child2 = object.child; child2 != null; child2 = child2.next) {
            if (!child2.isNumber()) {
                return false;
            }
        }
        return true;
    }

    private static void indent(int count, StringBuilder buffer) {
        for (int i = 0; i < count; i++) {
            buffer.append(9);
        }
    }

    private static void indent(int count, Writer buffer) throws IOException {
        for (int i = 0; i < count; i++) {
            buffer.append(9);
        }
    }

    public JsonIterator iterator() {
        return new JsonIterator();
    }

    public class JsonIterator implements Iterator<JsonValue>, Iterable<JsonValue> {
        JsonValue current;
        JsonValue entry = JsonValue.this.child;

        public JsonIterator() {
        }

        public boolean hasNext() {
            return this.entry != null;
        }

        public JsonValue next() {
            this.current = this.entry;
            JsonValue jsonValue = this.current;
            if (jsonValue != null) {
                this.entry = jsonValue.next;
                return this.current;
            }
            throw new NoSuchElementException();
        }

        public void remove() {
            if (this.current.prev == null) {
                JsonValue.this.child = this.current.next;
                if (JsonValue.this.child != null) {
                    JsonValue.this.child.prev = null;
                }
            } else {
                this.current.prev.next = this.current.next;
                if (this.current.next != null) {
                    this.current.next.prev = this.current.prev;
                }
            }
            JsonValue jsonValue = JsonValue.this;
            jsonValue.size--;
        }

        public Iterator<JsonValue> iterator() {
            return this;
        }
    }

    public String trace() {
        String trace;
        JsonValue jsonValue = this.parent;
        if (jsonValue != null) {
            if (jsonValue.type == ValueType.array) {
                trace = "[]";
                int i = 0;
                JsonValue child2 = this.parent.child;
                while (true) {
                    if (child2 == null) {
                        break;
                    } else if (child2 == this) {
                        trace = "[" + i + "]";
                        break;
                    } else {
                        child2 = child2.next;
                        i++;
                    }
                }
            } else if (this.name.indexOf(46) != -1) {
                trace = ".\"" + this.name.replace("\"", "\\\"") + "\"";
            } else {
                trace = '.' + this.name;
            }
            return this.parent.trace() + trace;
        } else if (this.type == ValueType.array) {
            return "[]";
        } else {
            if (this.type == ValueType.object) {
                return "{}";
            }
            return BuildConfig.FLAVOR;
        }
    }
}
