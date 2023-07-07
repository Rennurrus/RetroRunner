package com.badlogic.gdx.utils;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.regex.Pattern;

public class JsonWriter extends Writer {
    private JsonObject current;
    private boolean named;
    private OutputType outputType = OutputType.json;
    private boolean quoteLongValues = false;
    private final Array<JsonObject> stack = new Array<>();
    final Writer writer;

    public JsonWriter(Writer writer2) {
        this.writer = writer2;
    }

    public Writer getWriter() {
        return this.writer;
    }

    public void setOutputType(OutputType outputType2) {
        this.outputType = outputType2;
    }

    public void setQuoteLongValues(boolean quoteLongValues2) {
        this.quoteLongValues = quoteLongValues2;
    }

    public JsonWriter name(String name) throws IOException {
        JsonObject jsonObject = this.current;
        if (jsonObject == null || jsonObject.array) {
            throw new IllegalStateException("Current item must be an object.");
        }
        if (!this.current.needsComma) {
            this.current.needsComma = true;
        } else {
            this.writer.write(44);
        }
        this.writer.write(this.outputType.quoteName(name));
        this.writer.write(58);
        this.named = true;
        return this;
    }

    public JsonWriter object() throws IOException {
        requireCommaOrName();
        Array<JsonObject> array = this.stack;
        JsonObject jsonObject = new JsonObject(false);
        this.current = jsonObject;
        array.add(jsonObject);
        return this;
    }

    public JsonWriter array() throws IOException {
        requireCommaOrName();
        Array<JsonObject> array = this.stack;
        JsonObject jsonObject = new JsonObject(true);
        this.current = jsonObject;
        array.add(jsonObject);
        return this;
    }

    public JsonWriter value(Object value) throws IOException {
        if (this.quoteLongValues && ((value instanceof Long) || (value instanceof Double) || (value instanceof BigDecimal) || (value instanceof BigInteger))) {
            value = value.toString();
        } else if (value instanceof Number) {
            Number number = (Number) value;
            long longValue = number.longValue();
            if (number.doubleValue() == ((double) longValue)) {
                value = Long.valueOf(longValue);
            }
        }
        requireCommaOrName();
        this.writer.write(this.outputType.quoteValue(value));
        return this;
    }

    public JsonWriter json(String json) throws IOException {
        requireCommaOrName();
        this.writer.write(json);
        return this;
    }

    private void requireCommaOrName() throws IOException {
        JsonObject jsonObject = this.current;
        if (jsonObject != null) {
            if (jsonObject.array) {
                if (!this.current.needsComma) {
                    this.current.needsComma = true;
                } else {
                    this.writer.write(44);
                }
            } else if (this.named) {
                this.named = false;
            } else {
                throw new IllegalStateException("Name must be set.");
            }
        }
    }

    public JsonWriter object(String name) throws IOException {
        return name(name).object();
    }

    public JsonWriter array(String name) throws IOException {
        return name(name).array();
    }

    public JsonWriter set(String name, Object value) throws IOException {
        return name(name).value(value);
    }

    public JsonWriter json(String name, String json) throws IOException {
        return name(name).json(json);
    }

    public JsonWriter pop() throws IOException {
        if (!this.named) {
            this.stack.pop().close();
            this.current = this.stack.size == 0 ? null : this.stack.peek();
            return this;
        }
        throw new IllegalStateException("Expected an object, array, or value since a name was set.");
    }

    public void write(char[] cbuf, int off, int len) throws IOException {
        this.writer.write(cbuf, off, len);
    }

    public void flush() throws IOException {
        this.writer.flush();
    }

    public void close() throws IOException {
        while (this.stack.size > 0) {
            pop();
        }
        this.writer.close();
    }

    private class JsonObject {
        final boolean array;
        boolean needsComma;

        JsonObject(boolean array2) throws IOException {
            this.array = array2;
            JsonWriter.this.writer.write(array2 ? 91 : 123);
        }

        /* access modifiers changed from: package-private */
        public void close() throws IOException {
            JsonWriter.this.writer.write(this.array ? 93 : 125);
        }
    }

    public enum OutputType {
        json,
        javascript,
        minimal;
        
        private static Pattern javascriptPattern;
        private static Pattern minimalNamePattern;
        private static Pattern minimalValuePattern;

        static {
            javascriptPattern = Pattern.compile("^[a-zA-Z_$][a-zA-Z_$0-9]*$");
            minimalNamePattern = Pattern.compile("^[^\":,}/ ][^:]*$");
            minimalValuePattern = Pattern.compile("^[^\":,{\\[\\]/ ][^}\\],]*$");
        }

        public String quoteValue(Object value) {
            int length;
            if (value == null) {
                return "null";
            }
            String string = value.toString();
            if ((value instanceof Number) || (value instanceof Boolean)) {
                return string;
            }
            StringBuilder buffer = new StringBuilder(string);
            buffer.replace('\\', "\\\\").replace(13, "\\r").replace(10, "\\n").replace(9, "\\t");
            if (this == minimal && !string.equals("true") && !string.equals("false") && !string.equals("null") && !string.contains("//") && !string.contains("/*") && (length = buffer.length()) > 0 && buffer.charAt(length - 1) != ' ' && minimalValuePattern.matcher(buffer).matches()) {
                return buffer.toString();
            }
            return '\"' + buffer.replace('\"', "\\\"").toString() + '\"';
        }

        /* JADX WARNING: Code restructure failed: missing block: B:3:0x0030, code lost:
            if (r1 != 2) goto L_0x0065;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public java.lang.String quoteName(java.lang.String r5) {
            /*
                r4 = this;
                com.badlogic.gdx.utils.StringBuilder r0 = new com.badlogic.gdx.utils.StringBuilder
                r0.<init>((java.lang.String) r5)
                r1 = 92
                java.lang.String r2 = "\\\\"
                com.badlogic.gdx.utils.StringBuilder r1 = r0.replace((char) r1, (java.lang.String) r2)
                r2 = 13
                java.lang.String r3 = "\\r"
                com.badlogic.gdx.utils.StringBuilder r1 = r1.replace((char) r2, (java.lang.String) r3)
                r2 = 10
                java.lang.String r3 = "\\n"
                com.badlogic.gdx.utils.StringBuilder r1 = r1.replace((char) r2, (java.lang.String) r3)
                r2 = 9
                java.lang.String r3 = "\\t"
                r1.replace((char) r2, (java.lang.String) r3)
                int[] r1 = com.badlogic.gdx.utils.JsonWriter.AnonymousClass1.$SwitchMap$com$badlogic$gdx$utils$JsonWriter$OutputType
                int r2 = r4.ordinal()
                r1 = r1[r2]
                r2 = 1
                if (r1 == r2) goto L_0x0033
                r2 = 2
                if (r1 == r2) goto L_0x0054
                goto L_0x0065
            L_0x0033:
                java.lang.String r1 = "//"
                boolean r1 = r5.contains(r1)
                if (r1 != 0) goto L_0x0054
                java.lang.String r1 = "/*"
                boolean r1 = r5.contains(r1)
                if (r1 != 0) goto L_0x0054
                java.util.regex.Pattern r1 = minimalNamePattern
                java.util.regex.Matcher r1 = r1.matcher(r0)
                boolean r1 = r1.matches()
                if (r1 == 0) goto L_0x0054
                java.lang.String r1 = r0.toString()
                return r1
            L_0x0054:
                java.util.regex.Pattern r1 = javascriptPattern
                java.util.regex.Matcher r1 = r1.matcher(r0)
                boolean r1 = r1.matches()
                if (r1 == 0) goto L_0x0065
                java.lang.String r1 = r0.toString()
                return r1
            L_0x0065:
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                r2 = 34
                r1.append(r2)
                java.lang.String r3 = "\\\""
                com.badlogic.gdx.utils.StringBuilder r3 = r0.replace((char) r2, (java.lang.String) r3)
                java.lang.String r3 = r3.toString()
                r1.append(r3)
                r1.append(r2)
                java.lang.String r1 = r1.toString()
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.utils.JsonWriter.OutputType.quoteName(java.lang.String):java.lang.String");
        }
    }

    /* renamed from: com.badlogic.gdx.utils.JsonWriter$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$badlogic$gdx$utils$JsonWriter$OutputType = new int[OutputType.values().length];

        static {
            try {
                $SwitchMap$com$badlogic$gdx$utils$JsonWriter$OutputType[OutputType.minimal.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$badlogic$gdx$utils$JsonWriter$OutputType[OutputType.javascript.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }
}
