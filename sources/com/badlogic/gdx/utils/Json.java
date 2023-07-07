package com.badlogic.gdx.utils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.IntSet;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.reflect.ArrayReflection;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Constructor;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Json {
    private static final boolean debug = false;
    private final ObjectMap<Class, Object[]> classToDefaultValues;
    private final ObjectMap<Class, Serializer> classToSerializer;
    private final ObjectMap<Class, String> classToTag;
    private Serializer defaultSerializer;
    private boolean enumNames;
    private final Object[] equals1;
    private final Object[] equals2;
    private boolean ignoreDeprecated;
    private boolean ignoreUnknownFields;
    private JsonWriter.OutputType outputType;
    private boolean quoteLongValues;
    private boolean readDeprecated;
    private boolean sortFields;
    private final ObjectMap<String, Class> tagToClass;
    private String typeName;
    private final ObjectMap<Class, OrderedMap<String, FieldMetadata>> typeToFields;
    private boolean usePrototypes;
    private JsonWriter writer;

    public interface Serializable {
        void read(Json json, JsonValue jsonValue);

        void write(Json json);
    }

    public interface Serializer<T> {
        T read(Json json, JsonValue jsonValue, Class cls);

        void write(Json json, T t, Class cls);
    }

    public Json() {
        this.typeName = "class";
        this.usePrototypes = true;
        this.enumNames = true;
        this.typeToFields = new ObjectMap<>();
        this.tagToClass = new ObjectMap<>();
        this.classToTag = new ObjectMap<>();
        this.classToSerializer = new ObjectMap<>();
        this.classToDefaultValues = new ObjectMap<>();
        this.equals1 = new Object[]{null};
        this.equals2 = new Object[]{null};
        this.outputType = JsonWriter.OutputType.minimal;
    }

    public Json(JsonWriter.OutputType outputType2) {
        this.typeName = "class";
        this.usePrototypes = true;
        this.enumNames = true;
        this.typeToFields = new ObjectMap<>();
        this.tagToClass = new ObjectMap<>();
        this.classToTag = new ObjectMap<>();
        this.classToSerializer = new ObjectMap<>();
        this.classToDefaultValues = new ObjectMap<>();
        this.equals1 = new Object[]{null};
        this.equals2 = new Object[]{null};
        this.outputType = outputType2;
    }

    public void setIgnoreUnknownFields(boolean ignoreUnknownFields2) {
        this.ignoreUnknownFields = ignoreUnknownFields2;
    }

    public boolean getIgnoreUnknownFields() {
        return this.ignoreUnknownFields;
    }

    public void setIgnoreDeprecated(boolean ignoreDeprecated2) {
        this.ignoreDeprecated = ignoreDeprecated2;
    }

    public void setReadDeprecated(boolean readDeprecated2) {
        this.readDeprecated = readDeprecated2;
    }

    public void setOutputType(JsonWriter.OutputType outputType2) {
        this.outputType = outputType2;
    }

    public void setQuoteLongValues(boolean quoteLongValues2) {
        this.quoteLongValues = quoteLongValues2;
    }

    public void setEnumNames(boolean enumNames2) {
        this.enumNames = enumNames2;
    }

    public void addClassTag(String tag, Class type) {
        this.tagToClass.put(tag, type);
        this.classToTag.put(type, tag);
    }

    public Class getClass(String tag) {
        return this.tagToClass.get(tag);
    }

    public String getTag(Class type) {
        return this.classToTag.get(type);
    }

    public void setTypeName(String typeName2) {
        this.typeName = typeName2;
    }

    public void setDefaultSerializer(Serializer defaultSerializer2) {
        this.defaultSerializer = defaultSerializer2;
    }

    public <T> void setSerializer(Class<T> type, Serializer<T> serializer) {
        this.classToSerializer.put(type, serializer);
    }

    public <T> Serializer<T> getSerializer(Class<T> type) {
        return this.classToSerializer.get(type);
    }

    public void setUsePrototypes(boolean usePrototypes2) {
        this.usePrototypes = usePrototypes2;
    }

    public void setElementType(Class type, String fieldName, Class elementType) {
        FieldMetadata metadata = getFields(type).get(fieldName);
        if (metadata != null) {
            metadata.elementType = elementType;
            return;
        }
        throw new SerializationException("Field not found: " + fieldName + " (" + type.getName() + ")");
    }

    public void setDeprecated(Class type, String fieldName, boolean deprecated) {
        FieldMetadata metadata = getFields(type).get(fieldName);
        if (metadata != null) {
            metadata.deprecated = deprecated;
            return;
        }
        throw new SerializationException("Field not found: " + fieldName + " (" + type.getName() + ")");
    }

    public void setSortFields(boolean sortFields2) {
        this.sortFields = sortFields2;
    }

    private OrderedMap<String, FieldMetadata> getFields(Class type) {
        OrderedMap<String, FieldMetadata> fields = this.typeToFields.get(type);
        if (fields != null) {
            return fields;
        }
        Array<Class> classHierarchy = new Array<>();
        for (Class nextClass = type; nextClass != Object.class; nextClass = nextClass.getSuperclass()) {
            classHierarchy.add(nextClass);
        }
        ArrayList<Field> allFields = new ArrayList<>();
        for (int i = classHierarchy.size - 1; i >= 0; i--) {
            Collections.addAll(allFields, ClassReflection.getDeclaredFields(classHierarchy.get(i)));
        }
        OrderedMap<String, FieldMetadata> nameToField = new OrderedMap<>(allFields.size());
        int n = allFields.size();
        for (int i2 = 0; i2 < n; i2++) {
            Field field = allFields.get(i2);
            if (!field.isTransient() && !field.isStatic() && !field.isSynthetic()) {
                if (!field.isAccessible()) {
                    try {
                        field.setAccessible(true);
                    } catch (AccessControlException e) {
                    }
                }
                nameToField.put(field.getName(), new FieldMetadata(field));
            }
        }
        if (this.sortFields) {
            nameToField.keys.sort();
        }
        this.typeToFields.put(type, nameToField);
        return nameToField;
    }

    public String toJson(Object object) {
        return toJson(object, (Class) object == null ? null : object.getClass(), (Class) null);
    }

    public String toJson(Object object, Class knownType) {
        return toJson(object, knownType, (Class) null);
    }

    public String toJson(Object object, Class knownType, Class elementType) {
        StringWriter buffer = new StringWriter();
        toJson(object, knownType, elementType, (Writer) buffer);
        return buffer.toString();
    }

    public void toJson(Object object, FileHandle file) {
        toJson(object, (Class) object == null ? null : object.getClass(), (Class) null, file);
    }

    public void toJson(Object object, Class knownType, FileHandle file) {
        toJson(object, knownType, (Class) null, file);
    }

    public void toJson(Object object, Class knownType, Class elementType, FileHandle file) {
        Writer writer2 = null;
        try {
            writer2 = file.writer(false, "UTF-8");
            toJson(object, knownType, elementType, writer2);
            StreamUtils.closeQuietly(writer2);
        } catch (Exception ex) {
            throw new SerializationException("Error writing file: " + file, ex);
        } catch (Throwable th) {
            StreamUtils.closeQuietly(writer2);
            throw th;
        }
    }

    public void toJson(Object object, Writer writer2) {
        toJson(object, (Class) object == null ? null : object.getClass(), (Class) null, writer2);
    }

    public void toJson(Object object, Class knownType, Writer writer2) {
        toJson(object, knownType, (Class) null, writer2);
    }

    public void toJson(Object object, Class knownType, Class elementType, Writer writer2) {
        setWriter(writer2);
        try {
            writeValue(object, knownType, elementType);
        } finally {
            StreamUtils.closeQuietly(this.writer);
            this.writer = null;
        }
    }

    public void setWriter(Writer writer2) {
        if (!(writer2 instanceof JsonWriter)) {
            writer2 = new JsonWriter(writer2);
        }
        this.writer = (JsonWriter) writer2;
        this.writer.setOutputType(this.outputType);
        this.writer.setQuoteLongValues(this.quoteLongValues);
    }

    public JsonWriter getWriter() {
        return this.writer;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0070, code lost:
        if (java.util.Arrays.deepEquals(r13.equals1, r13.equals2) != false) goto L_0x0072;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void writeFields(java.lang.Object r14) {
        /*
            r13 = this;
            java.lang.String r0 = ")"
            java.lang.String r1 = " ("
            java.lang.Class r2 = r14.getClass()
            java.lang.Object[] r3 = r13.getDefaultValues(r2)
            com.badlogic.gdx.utils.OrderedMap r4 = r13.getFields(r2)
            r5 = 0
            com.badlogic.gdx.utils.OrderedMap$OrderedMapValues r6 = new com.badlogic.gdx.utils.OrderedMap$OrderedMapValues
            r6.<init>(r4)
            com.badlogic.gdx.utils.ObjectMap$Values r6 = r6.iterator()
        L_0x001a:
            boolean r7 = r6.hasNext()
            if (r7 == 0) goto L_0x0100
            java.lang.Object r7 = r6.next()
            com.badlogic.gdx.utils.Json$FieldMetadata r7 = (com.badlogic.gdx.utils.Json.FieldMetadata) r7
            boolean r8 = r13.ignoreDeprecated
            if (r8 == 0) goto L_0x002f
            boolean r8 = r7.deprecated
            if (r8 == 0) goto L_0x002f
            goto L_0x001a
        L_0x002f:
            com.badlogic.gdx.utils.reflect.Field r8 = r7.field
            java.lang.Object r9 = r8.get(r14)     // Catch:{ ReflectionException -> 0x00d5, SerializationException -> 0x00b5, Exception -> 0x0090 }
            if (r3 == 0) goto L_0x007c
            int r10 = r5 + 1
            r5 = r3[r5]     // Catch:{ ReflectionException -> 0x007a, SerializationException -> 0x0078, Exception -> 0x0076 }
            if (r9 != 0) goto L_0x0040
            if (r5 != 0) goto L_0x0040
            goto L_0x0072
        L_0x0040:
            if (r9 == 0) goto L_0x0074
            if (r5 == 0) goto L_0x0074
            boolean r11 = r9.equals(r5)     // Catch:{ ReflectionException -> 0x007a, SerializationException -> 0x0078, Exception -> 0x0076 }
            if (r11 == 0) goto L_0x004b
            goto L_0x0072
        L_0x004b:
            java.lang.Class r11 = r9.getClass()     // Catch:{ ReflectionException -> 0x007a, SerializationException -> 0x0078, Exception -> 0x0076 }
            boolean r11 = r11.isArray()     // Catch:{ ReflectionException -> 0x007a, SerializationException -> 0x0078, Exception -> 0x0076 }
            if (r11 == 0) goto L_0x0074
            java.lang.Class r11 = r5.getClass()     // Catch:{ ReflectionException -> 0x007a, SerializationException -> 0x0078, Exception -> 0x0076 }
            boolean r11 = r11.isArray()     // Catch:{ ReflectionException -> 0x007a, SerializationException -> 0x0078, Exception -> 0x0076 }
            if (r11 == 0) goto L_0x0074
            java.lang.Object[] r11 = r13.equals1     // Catch:{ ReflectionException -> 0x007a, SerializationException -> 0x0078, Exception -> 0x0076 }
            r12 = 0
            r11[r12] = r9     // Catch:{ ReflectionException -> 0x007a, SerializationException -> 0x0078, Exception -> 0x0076 }
            java.lang.Object[] r11 = r13.equals2     // Catch:{ ReflectionException -> 0x007a, SerializationException -> 0x0078, Exception -> 0x0076 }
            r11[r12] = r5     // Catch:{ ReflectionException -> 0x007a, SerializationException -> 0x0078, Exception -> 0x0076 }
            java.lang.Object[] r11 = r13.equals1     // Catch:{ ReflectionException -> 0x007a, SerializationException -> 0x0078, Exception -> 0x0076 }
            java.lang.Object[] r12 = r13.equals2     // Catch:{ ReflectionException -> 0x007a, SerializationException -> 0x0078, Exception -> 0x0076 }
            boolean r11 = java.util.Arrays.deepEquals(r11, r12)     // Catch:{ ReflectionException -> 0x007a, SerializationException -> 0x0078, Exception -> 0x0076 }
            if (r11 == 0) goto L_0x0074
        L_0x0072:
            r5 = r10
            goto L_0x001a
        L_0x0074:
            r5 = r10
            goto L_0x007c
        L_0x0076:
            r5 = move-exception
            goto L_0x0093
        L_0x0078:
            r5 = move-exception
            goto L_0x00b8
        L_0x007a:
            r5 = move-exception
            goto L_0x00d8
        L_0x007c:
            com.badlogic.gdx.utils.JsonWriter r10 = r13.writer     // Catch:{ ReflectionException -> 0x00d5, SerializationException -> 0x00b5, Exception -> 0x0090 }
            java.lang.String r11 = r8.getName()     // Catch:{ ReflectionException -> 0x00d5, SerializationException -> 0x00b5, Exception -> 0x0090 }
            r10.name(r11)     // Catch:{ ReflectionException -> 0x00d5, SerializationException -> 0x00b5, Exception -> 0x0090 }
            java.lang.Class r10 = r8.getType()     // Catch:{ ReflectionException -> 0x00d5, SerializationException -> 0x00b5, Exception -> 0x0090 }
            java.lang.Class r11 = r7.elementType     // Catch:{ ReflectionException -> 0x00d5, SerializationException -> 0x00b5, Exception -> 0x0090 }
            r13.writeValue((java.lang.Object) r9, (java.lang.Class) r10, (java.lang.Class) r11)     // Catch:{ ReflectionException -> 0x00d5, SerializationException -> 0x00b5, Exception -> 0x0090 }
            goto L_0x001a
        L_0x0090:
            r6 = move-exception
            r10 = r5
            r5 = r6
        L_0x0093:
            com.badlogic.gdx.utils.SerializationException r6 = new com.badlogic.gdx.utils.SerializationException
            r6.<init>((java.lang.Throwable) r5)
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r9.append(r8)
            r9.append(r1)
            java.lang.String r1 = r2.getName()
            r9.append(r1)
            r9.append(r0)
            java.lang.String r0 = r9.toString()
            r6.addTrace(r0)
            throw r6
        L_0x00b5:
            r6 = move-exception
            r10 = r5
            r5 = r6
        L_0x00b8:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r8)
            r6.append(r1)
            java.lang.String r1 = r2.getName()
            r6.append(r1)
            r6.append(r0)
            java.lang.String r0 = r6.toString()
            r5.addTrace(r0)
            throw r5
        L_0x00d5:
            r6 = move-exception
            r10 = r5
            r5 = r6
        L_0x00d8:
            com.badlogic.gdx.utils.SerializationException r6 = new com.badlogic.gdx.utils.SerializationException
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r11 = "Error accessing field: "
            r9.append(r11)
            java.lang.String r11 = r8.getName()
            r9.append(r11)
            r9.append(r1)
            java.lang.String r1 = r2.getName()
            r9.append(r1)
            r9.append(r0)
            java.lang.String r0 = r9.toString()
            r6.<init>(r0, r5)
            throw r6
        L_0x0100:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.utils.Json.writeFields(java.lang.Object):void");
    }

    private Object[] getDefaultValues(Class type) {
        if (!this.usePrototypes) {
            return null;
        }
        if (this.classToDefaultValues.containsKey(type)) {
            return this.classToDefaultValues.get(type);
        }
        try {
            Object object = newInstance(type);
            ObjectMap<String, FieldMetadata> fields = getFields(type);
            Object[] values = new Object[fields.size];
            this.classToDefaultValues.put(type, values);
            int i = 0;
            ObjectMap.Values<FieldMetadata> it = fields.values().iterator();
            while (it.hasNext()) {
                FieldMetadata metadata = (FieldMetadata) it.next();
                if (!this.ignoreDeprecated || !metadata.deprecated) {
                    Field field = metadata.field;
                    int i2 = i + 1;
                    try {
                        values[i] = field.get(object);
                        i = i2;
                    } catch (ReflectionException ex) {
                        throw new SerializationException("Error accessing field: " + field.getName() + " (" + type.getName() + ")", ex);
                    } catch (SerializationException ex2) {
                        ex2.addTrace(field + " (" + type.getName() + ")");
                        throw ex2;
                    } catch (RuntimeException runtimeEx) {
                        SerializationException ex3 = new SerializationException((Throwable) runtimeEx);
                        ex3.addTrace(field + " (" + type.getName() + ")");
                        throw ex3;
                    }
                }
            }
            return values;
        } catch (Exception e) {
            this.classToDefaultValues.put(type, null);
            return null;
        }
    }

    public void writeField(Object object, String name) {
        writeField(object, name, name, (Class) null);
    }

    public void writeField(Object object, String name, Class elementType) {
        writeField(object, name, name, elementType);
    }

    public void writeField(Object object, String fieldName, String jsonName) {
        writeField(object, fieldName, jsonName, (Class) null);
    }

    public void writeField(Object object, String fieldName, String jsonName, Class elementType) {
        Class type = object.getClass();
        FieldMetadata metadata = getFields(type).get(fieldName);
        if (metadata != null) {
            Field field = metadata.field;
            if (elementType == null) {
                elementType = metadata.elementType;
            }
            try {
                this.writer.name(jsonName);
                writeValue(field.get(object), field.getType(), elementType);
            } catch (ReflectionException ex) {
                throw new SerializationException("Error accessing field: " + field.getName() + " (" + type.getName() + ")", ex);
            } catch (SerializationException ex2) {
                ex2.addTrace(field + " (" + type.getName() + ")");
                throw ex2;
            } catch (Exception runtimeEx) {
                SerializationException ex3 = new SerializationException((Throwable) runtimeEx);
                ex3.addTrace(field + " (" + type.getName() + ")");
                throw ex3;
            }
        } else {
            throw new SerializationException("Field not found: " + fieldName + " (" + type.getName() + ")");
        }
    }

    public void writeValue(String name, Object value) {
        try {
            this.writer.name(name);
            if (value == null) {
                writeValue(value, (Class) null, (Class) null);
            } else {
                writeValue(value, (Class) value.getClass(), (Class) null);
            }
        } catch (IOException ex) {
            throw new SerializationException((Throwable) ex);
        }
    }

    public void writeValue(String name, Object value, Class knownType) {
        try {
            this.writer.name(name);
            writeValue(value, knownType, (Class) null);
        } catch (IOException ex) {
            throw new SerializationException((Throwable) ex);
        }
    }

    public void writeValue(String name, Object value, Class knownType, Class elementType) {
        try {
            this.writer.name(name);
            writeValue(value, knownType, elementType);
        } catch (IOException ex) {
            throw new SerializationException((Throwable) ex);
        }
    }

    public void writeValue(Object value) {
        if (value == null) {
            writeValue(value, (Class) null, (Class) null);
        } else {
            writeValue(value, (Class) value.getClass(), (Class) null);
        }
    }

    public void writeValue(Object value, Class knownType) {
        writeValue(value, knownType, (Class) null);
    }

    public void writeValue(Object value, Class knownType, Class elementType) {
        if (value == null) {
            try {
                this.writer.value((Object) null);
            } catch (IOException ex) {
                throw new SerializationException((Throwable) ex);
            }
        } else {
            if (!((knownType != null && knownType.isPrimitive()) || knownType == String.class || knownType == Integer.class || knownType == Boolean.class || knownType == Float.class || knownType == Long.class || knownType == Double.class || knownType == Short.class || knownType == Byte.class)) {
                if (knownType != Character.class) {
                    Class actualType = value.getClass();
                    if (actualType.isPrimitive() || actualType == String.class || actualType == Integer.class || actualType == Boolean.class || actualType == Float.class || actualType == Long.class || actualType == Double.class || actualType == Short.class || actualType == Byte.class || actualType == Character.class) {
                        writeObjectStart(actualType, (Class) null);
                        writeValue("value", value);
                        writeObjectEnd();
                        return;
                    } else if (value instanceof Serializable) {
                        writeObjectStart(actualType, knownType);
                        ((Serializable) value).write(this);
                        writeObjectEnd();
                        return;
                    } else {
                        Serializer serializer = this.classToSerializer.get(actualType);
                        if (serializer != null) {
                            serializer.write(this, value, knownType);
                            return;
                        } else if (value instanceof Array) {
                            if (knownType == null || actualType == knownType || actualType == Array.class) {
                                writeArrayStart();
                                Array array = (Array) value;
                                int n = array.size;
                                for (int i = 0; i < n; i++) {
                                    writeValue(array.get(i), elementType, (Class) null);
                                }
                                writeArrayEnd();
                                return;
                            }
                            throw new SerializationException("Serialization of an Array other than the known type is not supported.\nKnown type: " + knownType + "\nActual type: " + actualType);
                        } else if (value instanceof Queue) {
                            if (!(knownType == null || actualType == knownType)) {
                                if (actualType != Queue.class) {
                                    throw new SerializationException("Serialization of a Queue other than the known type is not supported.\nKnown type: " + knownType + "\nActual type: " + actualType);
                                }
                            }
                            writeArrayStart();
                            Queue queue = (Queue) value;
                            int n2 = queue.size;
                            for (int i2 = 0; i2 < n2; i2++) {
                                writeValue(queue.get(i2), elementType, (Class) null);
                            }
                            writeArrayEnd();
                            return;
                        } else if (value instanceof Collection) {
                            if (this.typeName == null || actualType == ArrayList.class || (knownType != null && knownType == actualType)) {
                                writeArrayStart();
                                for (Object item : (Collection) value) {
                                    writeValue(item, elementType, (Class) null);
                                }
                                writeArrayEnd();
                                return;
                            }
                            writeObjectStart(actualType, knownType);
                            writeArrayStart("items");
                            for (Object item2 : (Collection) value) {
                                writeValue(item2, elementType, (Class) null);
                            }
                            writeArrayEnd();
                            writeObjectEnd();
                            return;
                        } else if (actualType.isArray()) {
                            if (elementType == null) {
                                elementType = actualType.getComponentType();
                            }
                            int length = ArrayReflection.getLength(value);
                            writeArrayStart();
                            for (int i3 = 0; i3 < length; i3++) {
                                writeValue(ArrayReflection.get(value, i3), elementType, (Class) null);
                            }
                            writeArrayEnd();
                            return;
                        } else if (value instanceof ObjectMap) {
                            if (knownType == null) {
                                knownType = ObjectMap.class;
                            }
                            writeObjectStart(actualType, knownType);
                            ObjectMap.Entries it = ((ObjectMap) value).entries().iterator();
                            while (it.hasNext()) {
                                ObjectMap.Entry entry = (ObjectMap.Entry) it.next();
                                this.writer.name(convertToString((Object) entry.key));
                                writeValue((Object) entry.value, elementType, (Class) null);
                            }
                            writeObjectEnd();
                            return;
                        } else if (value instanceof ObjectSet) {
                            if (knownType == null) {
                                knownType = ObjectSet.class;
                            }
                            writeObjectStart(actualType, knownType);
                            this.writer.name("values");
                            writeArrayStart();
                            ObjectSet.ObjectSetIterator it2 = ((ObjectSet) value).iterator();
                            while (it2.hasNext()) {
                                writeValue(it2.next(), elementType, (Class) null);
                            }
                            writeArrayEnd();
                            writeObjectEnd();
                            return;
                        } else if (value instanceof IntSet) {
                            if (knownType == null) {
                                knownType = IntSet.class;
                            }
                            writeObjectStart(actualType, knownType);
                            this.writer.name("values");
                            writeArrayStart();
                            IntSet.IntSetIterator iter = ((IntSet) value).iterator();
                            while (iter.hasNext) {
                                writeValue((Object) Integer.valueOf(iter.next()), Integer.class, (Class) null);
                            }
                            writeArrayEnd();
                            writeObjectEnd();
                            return;
                        } else if (value instanceof ArrayMap) {
                            if (knownType == null) {
                                knownType = ArrayMap.class;
                            }
                            writeObjectStart(actualType, knownType);
                            ArrayMap map = (ArrayMap) value;
                            int n3 = map.size;
                            for (int i4 = 0; i4 < n3; i4++) {
                                this.writer.name(convertToString((Object) map.keys[i4]));
                                writeValue((Object) map.values[i4], elementType, (Class) null);
                            }
                            writeObjectEnd();
                            return;
                        } else if (value instanceof Map) {
                            if (knownType == null) {
                                knownType = HashMap.class;
                            }
                            writeObjectStart(actualType, knownType);
                            for (Map.Entry entry2 : ((Map) value).entrySet()) {
                                this.writer.name(convertToString(entry2.getKey()));
                                writeValue(entry2.getValue(), elementType, (Class) null);
                            }
                            writeObjectEnd();
                            return;
                        } else if (!ClassReflection.isAssignableFrom(Enum.class, actualType)) {
                            writeObjectStart(actualType, knownType);
                            writeFields(value);
                            writeObjectEnd();
                            return;
                        } else if (this.typeName == null || (knownType != null && knownType == actualType)) {
                            this.writer.value(convertToString((Enum) value));
                            return;
                        } else {
                            if (actualType.getEnumConstants() == null) {
                                actualType = actualType.getSuperclass();
                            }
                            writeObjectStart(actualType, (Class) null);
                            this.writer.name("value");
                            this.writer.value(convertToString((Enum) value));
                            writeObjectEnd();
                            return;
                        }
                    }
                }
            }
            this.writer.value(value);
        }
    }

    public void writeObjectStart(String name) {
        try {
            this.writer.name(name);
            writeObjectStart();
        } catch (IOException ex) {
            throw new SerializationException((Throwable) ex);
        }
    }

    public void writeObjectStart(String name, Class actualType, Class knownType) {
        try {
            this.writer.name(name);
            writeObjectStart(actualType, knownType);
        } catch (IOException ex) {
            throw new SerializationException((Throwable) ex);
        }
    }

    public void writeObjectStart() {
        try {
            this.writer.object();
        } catch (IOException ex) {
            throw new SerializationException((Throwable) ex);
        }
    }

    public void writeObjectStart(Class actualType, Class knownType) {
        try {
            this.writer.object();
            if (knownType == null || knownType != actualType) {
                writeType(actualType);
            }
        } catch (IOException ex) {
            throw new SerializationException((Throwable) ex);
        }
    }

    public void writeObjectEnd() {
        try {
            this.writer.pop();
        } catch (IOException ex) {
            throw new SerializationException((Throwable) ex);
        }
    }

    public void writeArrayStart(String name) {
        try {
            this.writer.name(name);
            this.writer.array();
        } catch (IOException ex) {
            throw new SerializationException((Throwable) ex);
        }
    }

    public void writeArrayStart() {
        try {
            this.writer.array();
        } catch (IOException ex) {
            throw new SerializationException((Throwable) ex);
        }
    }

    public void writeArrayEnd() {
        try {
            this.writer.pop();
        } catch (IOException ex) {
            throw new SerializationException((Throwable) ex);
        }
    }

    public void writeType(Class type) {
        if (this.typeName != null) {
            String className = getTag(type);
            if (className == null) {
                className = type.getName();
            }
            try {
                this.writer.set(this.typeName, className);
            } catch (IOException ex) {
                throw new SerializationException((Throwable) ex);
            }
        }
    }

    public <T> T fromJson(Class<T> type, Reader reader) {
        return readValue(type, (Class) null, new JsonReader().parse(reader));
    }

    public <T> T fromJson(Class<T> type, Class elementType, Reader reader) {
        return readValue(type, elementType, new JsonReader().parse(reader));
    }

    public <T> T fromJson(Class<T> type, InputStream input) {
        return readValue(type, (Class) null, new JsonReader().parse(input));
    }

    public <T> T fromJson(Class<T> type, Class elementType, InputStream input) {
        return readValue(type, elementType, new JsonReader().parse(input));
    }

    public <T> T fromJson(Class<T> type, FileHandle file) {
        try {
            return readValue(type, (Class) null, new JsonReader().parse(file));
        } catch (Exception ex) {
            throw new SerializationException("Error reading file: " + file, ex);
        }
    }

    public <T> T fromJson(Class<T> type, Class elementType, FileHandle file) {
        try {
            return readValue(type, elementType, new JsonReader().parse(file));
        } catch (Exception ex) {
            throw new SerializationException("Error reading file: " + file, ex);
        }
    }

    public <T> T fromJson(Class<T> type, char[] data, int offset, int length) {
        return readValue(type, (Class) null, new JsonReader().parse(data, offset, length));
    }

    public <T> T fromJson(Class<T> type, Class elementType, char[] data, int offset, int length) {
        return readValue(type, elementType, new JsonReader().parse(data, offset, length));
    }

    public <T> T fromJson(Class<T> type, String json) {
        return readValue(type, (Class) null, new JsonReader().parse(json));
    }

    public <T> T fromJson(Class<T> type, Class elementType, String json) {
        return readValue(type, elementType, new JsonReader().parse(json));
    }

    public void readField(Object object, String name, JsonValue jsonData) {
        readField(object, name, name, (Class) null, jsonData);
    }

    public void readField(Object object, String name, Class elementType, JsonValue jsonData) {
        readField(object, name, name, elementType, jsonData);
    }

    public void readField(Object object, String fieldName, String jsonName, JsonValue jsonData) {
        readField(object, fieldName, jsonName, (Class) null, jsonData);
    }

    public void readField(Object object, String fieldName, String jsonName, Class elementType, JsonValue jsonMap) {
        Class type = object.getClass();
        FieldMetadata metadata = getFields(type).get(fieldName);
        if (metadata != null) {
            Field field = metadata.field;
            if (elementType == null) {
                elementType = metadata.elementType;
            }
            readField(object, field, jsonName, elementType, jsonMap);
            return;
        }
        throw new SerializationException("Field not found: " + fieldName + " (" + type.getName() + ")");
    }

    public void readField(Object object, Field field, String jsonName, Class elementType, JsonValue jsonMap) {
        JsonValue jsonValue = jsonMap.get(jsonName);
        if (jsonValue != null) {
            try {
                field.set(object, readValue(field.getType(), elementType, jsonValue));
            } catch (ReflectionException ex) {
                throw new SerializationException("Error accessing field: " + field.getName() + " (" + field.getDeclaringClass().getName() + ")", ex);
            } catch (SerializationException ex2) {
                ex2.addTrace(field.getName() + " (" + field.getDeclaringClass().getName() + ")");
                throw ex2;
            } catch (RuntimeException runtimeEx) {
                SerializationException ex3 = new SerializationException((Throwable) runtimeEx);
                ex3.addTrace(jsonValue.trace());
                ex3.addTrace(field.getName() + " (" + field.getDeclaringClass().getName() + ")");
                throw ex3;
            }
        }
    }

    public void readFields(Object object, JsonValue jsonMap) {
        Class type = object.getClass();
        ObjectMap<String, FieldMetadata> fields = getFields(type);
        for (JsonValue child = jsonMap.child; child != null; child = child.next) {
            FieldMetadata metadata = fields.get(child.name().replace(" ", "_"));
            if (metadata == null) {
                if (!child.name.equals(this.typeName) && !this.ignoreUnknownFields && !ignoreUnknownField(type, child.name)) {
                    SerializationException ex = new SerializationException("Field not found: " + child.name + " (" + type.getName() + ")");
                    ex.addTrace(child.trace());
                    throw ex;
                }
            } else if (!this.ignoreDeprecated || this.readDeprecated || !metadata.deprecated) {
                Field field = metadata.field;
                try {
                    field.set(object, readValue(field.getType(), metadata.elementType, child));
                } catch (ReflectionException ex2) {
                    throw new SerializationException("Error accessing field: " + field.getName() + " (" + type.getName() + ")", ex2);
                } catch (SerializationException ex3) {
                    ex3.addTrace(field.getName() + " (" + type.getName() + ")");
                    throw ex3;
                } catch (RuntimeException runtimeEx) {
                    SerializationException ex4 = new SerializationException((Throwable) runtimeEx);
                    ex4.addTrace(child.trace());
                    ex4.addTrace(field.getName() + " (" + type.getName() + ")");
                    throw ex4;
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean ignoreUnknownField(Class type, String fieldName) {
        return false;
    }

    public <T> T readValue(String name, Class<T> type, JsonValue jsonMap) {
        return readValue(type, (Class) null, jsonMap.get(name));
    }

    public <T> T readValue(String name, Class<T> type, T defaultValue, JsonValue jsonMap) {
        JsonValue jsonValue = jsonMap.get(name);
        if (jsonValue == null) {
            return defaultValue;
        }
        return readValue(type, (Class) null, jsonValue);
    }

    public <T> T readValue(String name, Class<T> type, Class elementType, JsonValue jsonMap) {
        return readValue(type, elementType, jsonMap.get(name));
    }

    public <T> T readValue(String name, Class<T> type, Class elementType, T defaultValue, JsonValue jsonMap) {
        return readValue(type, elementType, defaultValue, jsonMap.get(name));
    }

    public <T> T readValue(Class<T> type, Class elementType, T defaultValue, JsonValue jsonData) {
        if (jsonData == null) {
            return defaultValue;
        }
        return readValue(type, elementType, jsonData);
    }

    public <T> T readValue(Class<T> type, JsonValue jsonData) {
        return readValue(type, (Class) null, jsonData);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:110:0x017f, code lost:
        if (r10 == java.lang.Object.class) goto L_0x0181;
     */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public <T> T readValue(java.lang.Class<T> r10, java.lang.Class r11, com.badlogic.gdx.utils.JsonValue r12) {
        /*
            r9 = this;
            r0 = 0
            if (r12 != 0) goto L_0x0004
            return r0
        L_0x0004:
            boolean r1 = r12.isObject()
            java.lang.String r2 = ")"
            java.lang.String r3 = " ("
            if (r1 == 0) goto L_0x014f
            java.lang.String r1 = r9.typeName
            if (r1 != 0) goto L_0x0014
            r1 = r0
            goto L_0x0018
        L_0x0014:
            java.lang.String r1 = r12.getString(r1, r0)
        L_0x0018:
            if (r1 == 0) goto L_0x002d
            java.lang.Class r10 = r9.getClass(r1)
            if (r10 != 0) goto L_0x002d
            java.lang.Class r4 = com.badlogic.gdx.utils.reflect.ClassReflection.forName(r1)     // Catch:{ ReflectionException -> 0x0026 }
            r10 = r4
            goto L_0x002d
        L_0x0026:
            r0 = move-exception
            com.badlogic.gdx.utils.SerializationException r2 = new com.badlogic.gdx.utils.SerializationException
            r2.<init>((java.lang.Throwable) r0)
            throw r2
        L_0x002d:
            if (r10 != 0) goto L_0x0039
            com.badlogic.gdx.utils.Json$Serializer r0 = r9.defaultSerializer
            if (r0 == 0) goto L_0x0038
            java.lang.Object r0 = r0.read(r9, r12, r10)
            return r0
        L_0x0038:
            return r12
        L_0x0039:
            java.lang.String r4 = r9.typeName
            if (r4 == 0) goto L_0x0073
            java.lang.Class<java.util.Collection> r4 = java.util.Collection.class
            boolean r4 = com.badlogic.gdx.utils.reflect.ClassReflection.isAssignableFrom(r4, r10)
            if (r4 == 0) goto L_0x0073
            java.lang.String r4 = "items"
            com.badlogic.gdx.utils.JsonValue r12 = r12.get((java.lang.String) r4)
            if (r12 == 0) goto L_0x004f
            goto L_0x014f
        L_0x004f:
            com.badlogic.gdx.utils.SerializationException r0 = new com.badlogic.gdx.utils.SerializationException
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "Unable to convert object to collection: "
            r4.append(r5)
            r4.append(r12)
            r4.append(r3)
            java.lang.String r3 = r10.getName()
            r4.append(r3)
            r4.append(r2)
            java.lang.String r2 = r4.toString()
            r0.<init>((java.lang.String) r2)
            throw r0
        L_0x0073:
            com.badlogic.gdx.utils.ObjectMap<java.lang.Class, com.badlogic.gdx.utils.Json$Serializer> r2 = r9.classToSerializer
            java.lang.Object r2 = r2.get(r10)
            com.badlogic.gdx.utils.Json$Serializer r2 = (com.badlogic.gdx.utils.Json.Serializer) r2
            if (r2 == 0) goto L_0x0082
            java.lang.Object r0 = r2.read(r9, r12, r10)
            return r0
        L_0x0082:
            java.lang.Class<java.lang.String> r3 = java.lang.String.class
            if (r10 == r3) goto L_0x0148
            java.lang.Class<java.lang.Integer> r3 = java.lang.Integer.class
            if (r10 == r3) goto L_0x0148
            java.lang.Class<java.lang.Boolean> r3 = java.lang.Boolean.class
            if (r10 == r3) goto L_0x0148
            java.lang.Class<java.lang.Float> r3 = java.lang.Float.class
            if (r10 == r3) goto L_0x0148
            java.lang.Class<java.lang.Long> r3 = java.lang.Long.class
            if (r10 == r3) goto L_0x0148
            java.lang.Class<java.lang.Double> r3 = java.lang.Double.class
            if (r10 == r3) goto L_0x0148
            java.lang.Class<java.lang.Short> r3 = java.lang.Short.class
            if (r10 == r3) goto L_0x0148
            java.lang.Class<java.lang.Byte> r3 = java.lang.Byte.class
            if (r10 == r3) goto L_0x0148
            java.lang.Class<java.lang.Character> r3 = java.lang.Character.class
            if (r10 == r3) goto L_0x0148
            java.lang.Class<java.lang.Enum> r3 = java.lang.Enum.class
            boolean r3 = com.badlogic.gdx.utils.reflect.ClassReflection.isAssignableFrom(r3, r10)
            if (r3 == 0) goto L_0x00b0
            goto L_0x0148
        L_0x00b0:
            java.lang.Object r3 = r9.newInstance(r10)
            boolean r4 = r3 instanceof com.badlogic.gdx.utils.Json.Serializable
            if (r4 == 0) goto L_0x00bf
            r0 = r3
            com.badlogic.gdx.utils.Json$Serializable r0 = (com.badlogic.gdx.utils.Json.Serializable) r0
            r0.read(r9, r12)
            return r3
        L_0x00bf:
            boolean r4 = r3 instanceof com.badlogic.gdx.utils.ObjectMap
            if (r4 == 0) goto L_0x00d7
            r4 = r3
            com.badlogic.gdx.utils.ObjectMap r4 = (com.badlogic.gdx.utils.ObjectMap) r4
            com.badlogic.gdx.utils.JsonValue r5 = r12.child
        L_0x00c8:
            if (r5 == 0) goto L_0x00d6
            java.lang.String r6 = r5.name
            java.lang.Object r7 = r9.readValue(r11, (java.lang.Class) r0, (com.badlogic.gdx.utils.JsonValue) r5)
            r4.put(r6, r7)
            com.badlogic.gdx.utils.JsonValue r5 = r5.next
            goto L_0x00c8
        L_0x00d6:
            return r4
        L_0x00d7:
            boolean r4 = r3 instanceof com.badlogic.gdx.utils.ObjectSet
            java.lang.String r5 = "values"
            if (r4 == 0) goto L_0x00f1
            r4 = r3
            com.badlogic.gdx.utils.ObjectSet r4 = (com.badlogic.gdx.utils.ObjectSet) r4
            com.badlogic.gdx.utils.JsonValue r5 = r12.getChild(r5)
        L_0x00e4:
            if (r5 == 0) goto L_0x00f0
            java.lang.Object r6 = r9.readValue(r11, (java.lang.Class) r0, (com.badlogic.gdx.utils.JsonValue) r5)
            r4.add(r6)
            com.badlogic.gdx.utils.JsonValue r5 = r5.next
            goto L_0x00e4
        L_0x00f0:
            return r4
        L_0x00f1:
            boolean r4 = r3 instanceof com.badlogic.gdx.utils.IntSet
            if (r4 == 0) goto L_0x0109
            r0 = r3
            com.badlogic.gdx.utils.IntSet r0 = (com.badlogic.gdx.utils.IntSet) r0
            com.badlogic.gdx.utils.JsonValue r4 = r12.getChild(r5)
        L_0x00fc:
            if (r4 == 0) goto L_0x0108
            int r5 = r4.asInt()
            r0.add(r5)
            com.badlogic.gdx.utils.JsonValue r4 = r4.next
            goto L_0x00fc
        L_0x0108:
            return r0
        L_0x0109:
            boolean r4 = r3 instanceof com.badlogic.gdx.utils.ArrayMap
            if (r4 == 0) goto L_0x0121
            r4 = r3
            com.badlogic.gdx.utils.ArrayMap r4 = (com.badlogic.gdx.utils.ArrayMap) r4
            com.badlogic.gdx.utils.JsonValue r5 = r12.child
        L_0x0112:
            if (r5 == 0) goto L_0x0120
            java.lang.String r6 = r5.name
            java.lang.Object r7 = r9.readValue(r11, (java.lang.Class) r0, (com.badlogic.gdx.utils.JsonValue) r5)
            r4.put(r6, r7)
            com.badlogic.gdx.utils.JsonValue r5 = r5.next
            goto L_0x0112
        L_0x0120:
            return r4
        L_0x0121:
            boolean r4 = r3 instanceof java.util.Map
            if (r4 == 0) goto L_0x0144
            r4 = r3
            java.util.Map r4 = (java.util.Map) r4
            com.badlogic.gdx.utils.JsonValue r5 = r12.child
        L_0x012a:
            if (r5 == 0) goto L_0x0143
            java.lang.String r6 = r5.name
            java.lang.String r7 = r9.typeName
            boolean r6 = r6.equals(r7)
            if (r6 == 0) goto L_0x0137
            goto L_0x0140
        L_0x0137:
            java.lang.String r6 = r5.name
            java.lang.Object r7 = r9.readValue(r11, (java.lang.Class) r0, (com.badlogic.gdx.utils.JsonValue) r5)
            r4.put(r6, r7)
        L_0x0140:
            com.badlogic.gdx.utils.JsonValue r5 = r5.next
            goto L_0x012a
        L_0x0143:
            return r4
        L_0x0144:
            r9.readFields(r3, r12)
            return r3
        L_0x0148:
            java.lang.String r0 = "value"
            java.lang.Object r0 = r9.readValue((java.lang.String) r0, r10, (com.badlogic.gdx.utils.JsonValue) r12)
            return r0
        L_0x014f:
            if (r10 == 0) goto L_0x0173
            com.badlogic.gdx.utils.ObjectMap<java.lang.Class, com.badlogic.gdx.utils.Json$Serializer> r1 = r9.classToSerializer
            java.lang.Object r1 = r1.get(r10)
            com.badlogic.gdx.utils.Json$Serializer r1 = (com.badlogic.gdx.utils.Json.Serializer) r1
            if (r1 == 0) goto L_0x0160
            java.lang.Object r0 = r1.read(r9, r12, r10)
            return r0
        L_0x0160:
            java.lang.Class<com.badlogic.gdx.utils.Json$Serializable> r4 = com.badlogic.gdx.utils.Json.Serializable.class
            boolean r4 = com.badlogic.gdx.utils.reflect.ClassReflection.isAssignableFrom(r4, r10)
            if (r4 == 0) goto L_0x0173
            java.lang.Object r0 = r9.newInstance(r10)
            r2 = r0
            com.badlogic.gdx.utils.Json$Serializable r2 = (com.badlogic.gdx.utils.Json.Serializable) r2
            r2.read(r9, r12)
            return r0
        L_0x0173:
            boolean r1 = r12.isArray()
            java.lang.String r4 = "Unable to convert value to required type: "
            if (r1 == 0) goto L_0x0242
            if (r10 == 0) goto L_0x0181
            java.lang.Class<java.lang.Object> r1 = java.lang.Object.class
            if (r10 != r1) goto L_0x0183
        L_0x0181:
            java.lang.Class<com.badlogic.gdx.utils.Array> r10 = com.badlogic.gdx.utils.Array.class
        L_0x0183:
            java.lang.Class<com.badlogic.gdx.utils.Array> r1 = com.badlogic.gdx.utils.Array.class
            boolean r1 = com.badlogic.gdx.utils.reflect.ClassReflection.isAssignableFrom(r1, r10)
            if (r1 == 0) goto L_0x01aa
            java.lang.Class<com.badlogic.gdx.utils.Array> r1 = com.badlogic.gdx.utils.Array.class
            if (r10 != r1) goto L_0x0195
            com.badlogic.gdx.utils.Array r1 = new com.badlogic.gdx.utils.Array
            r1.<init>()
            goto L_0x019b
        L_0x0195:
            java.lang.Object r1 = r9.newInstance(r10)
            com.badlogic.gdx.utils.Array r1 = (com.badlogic.gdx.utils.Array) r1
        L_0x019b:
            com.badlogic.gdx.utils.JsonValue r2 = r12.child
        L_0x019d:
            if (r2 == 0) goto L_0x01a9
            java.lang.Object r3 = r9.readValue(r11, (java.lang.Class) r0, (com.badlogic.gdx.utils.JsonValue) r2)
            r1.add(r3)
            com.badlogic.gdx.utils.JsonValue r2 = r2.next
            goto L_0x019d
        L_0x01a9:
            return r1
        L_0x01aa:
            java.lang.Class<com.badlogic.gdx.utils.Queue> r1 = com.badlogic.gdx.utils.Queue.class
            boolean r1 = com.badlogic.gdx.utils.reflect.ClassReflection.isAssignableFrom(r1, r10)
            if (r1 == 0) goto L_0x01d1
            java.lang.Class<com.badlogic.gdx.utils.Queue> r1 = com.badlogic.gdx.utils.Queue.class
            if (r10 != r1) goto L_0x01bc
            com.badlogic.gdx.utils.Queue r1 = new com.badlogic.gdx.utils.Queue
            r1.<init>()
            goto L_0x01c2
        L_0x01bc:
            java.lang.Object r1 = r9.newInstance(r10)
            com.badlogic.gdx.utils.Queue r1 = (com.badlogic.gdx.utils.Queue) r1
        L_0x01c2:
            com.badlogic.gdx.utils.JsonValue r2 = r12.child
        L_0x01c4:
            if (r2 == 0) goto L_0x01d0
            java.lang.Object r3 = r9.readValue(r11, (java.lang.Class) r0, (com.badlogic.gdx.utils.JsonValue) r2)
            r1.addLast(r3)
            com.badlogic.gdx.utils.JsonValue r2 = r2.next
            goto L_0x01c4
        L_0x01d0:
            return r1
        L_0x01d1:
            java.lang.Class<java.util.Collection> r1 = java.util.Collection.class
            boolean r1 = com.badlogic.gdx.utils.reflect.ClassReflection.isAssignableFrom(r1, r10)
            if (r1 == 0) goto L_0x01fa
            boolean r1 = r10.isInterface()
            if (r1 == 0) goto L_0x01e5
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            goto L_0x01eb
        L_0x01e5:
            java.lang.Object r1 = r9.newInstance(r10)
            java.util.Collection r1 = (java.util.Collection) r1
        L_0x01eb:
            com.badlogic.gdx.utils.JsonValue r2 = r12.child
        L_0x01ed:
            if (r2 == 0) goto L_0x01f9
            java.lang.Object r3 = r9.readValue(r11, (java.lang.Class) r0, (com.badlogic.gdx.utils.JsonValue) r2)
            r1.add(r3)
            com.badlogic.gdx.utils.JsonValue r2 = r2.next
            goto L_0x01ed
        L_0x01f9:
            return r1
        L_0x01fa:
            boolean r1 = r10.isArray()
            if (r1 == 0) goto L_0x0220
            java.lang.Class r1 = r10.getComponentType()
            if (r11 != 0) goto L_0x0207
            r11 = r1
        L_0x0207:
            int r2 = r12.size
            java.lang.Object r2 = com.badlogic.gdx.utils.reflect.ArrayReflection.newInstance(r1, r2)
            r3 = 0
            com.badlogic.gdx.utils.JsonValue r4 = r12.child
        L_0x0210:
            if (r4 == 0) goto L_0x021f
            int r5 = r3 + 1
            java.lang.Object r6 = r9.readValue(r11, (java.lang.Class) r0, (com.badlogic.gdx.utils.JsonValue) r4)
            com.badlogic.gdx.utils.reflect.ArrayReflection.set(r2, r3, r6)
            com.badlogic.gdx.utils.JsonValue r4 = r4.next
            r3 = r5
            goto L_0x0210
        L_0x021f:
            return r2
        L_0x0220:
            com.badlogic.gdx.utils.SerializationException r0 = new com.badlogic.gdx.utils.SerializationException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r4)
            r1.append(r12)
            r1.append(r3)
            java.lang.String r3 = r10.getName()
            r1.append(r3)
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.<init>((java.lang.String) r1)
            throw r0
        L_0x0242:
            boolean r1 = r12.isNumber()
            if (r1 == 0) goto L_0x02cb
            if (r10 == 0) goto L_0x02b7
            java.lang.Class r1 = java.lang.Float.TYPE     // Catch:{ NumberFormatException -> 0x02c0 }
            if (r10 == r1) goto L_0x02b7
            java.lang.Class<java.lang.Float> r1 = java.lang.Float.class
            if (r10 != r1) goto L_0x0253
            goto L_0x02b7
        L_0x0253:
            java.lang.Class r1 = java.lang.Integer.TYPE     // Catch:{ NumberFormatException -> 0x02c0 }
            if (r10 == r1) goto L_0x02ae
            java.lang.Class<java.lang.Integer> r1 = java.lang.Integer.class
            if (r10 != r1) goto L_0x025c
            goto L_0x02ae
        L_0x025c:
            java.lang.Class r1 = java.lang.Long.TYPE     // Catch:{ NumberFormatException -> 0x02c0 }
            if (r10 == r1) goto L_0x02a5
            java.lang.Class<java.lang.Long> r1 = java.lang.Long.class
            if (r10 != r1) goto L_0x0265
            goto L_0x02a5
        L_0x0265:
            java.lang.Class r1 = java.lang.Double.TYPE     // Catch:{ NumberFormatException -> 0x02c0 }
            if (r10 == r1) goto L_0x029c
            java.lang.Class<java.lang.Double> r1 = java.lang.Double.class
            if (r10 != r1) goto L_0x026e
            goto L_0x029c
        L_0x026e:
            java.lang.Class<java.lang.String> r1 = java.lang.String.class
            if (r10 != r1) goto L_0x0277
            java.lang.String r0 = r12.asString()     // Catch:{ NumberFormatException -> 0x02c0 }
            return r0
        L_0x0277:
            java.lang.Class r1 = java.lang.Short.TYPE     // Catch:{ NumberFormatException -> 0x02c0 }
            if (r10 == r1) goto L_0x0293
            java.lang.Class<java.lang.Short> r1 = java.lang.Short.class
            if (r10 != r1) goto L_0x0280
            goto L_0x0293
        L_0x0280:
            java.lang.Class r1 = java.lang.Byte.TYPE     // Catch:{ NumberFormatException -> 0x02c0 }
            if (r10 == r1) goto L_0x028a
            java.lang.Class<java.lang.Byte> r1 = java.lang.Byte.class
            if (r10 != r1) goto L_0x0289
            goto L_0x028a
        L_0x0289:
            goto L_0x02c1
        L_0x028a:
            byte r1 = r12.asByte()     // Catch:{ NumberFormatException -> 0x02c0 }
            java.lang.Byte r0 = java.lang.Byte.valueOf(r1)     // Catch:{ NumberFormatException -> 0x02c0 }
            return r0
        L_0x0293:
            short r1 = r12.asShort()     // Catch:{ NumberFormatException -> 0x02c0 }
            java.lang.Short r0 = java.lang.Short.valueOf(r1)     // Catch:{ NumberFormatException -> 0x02c0 }
            return r0
        L_0x029c:
            double r5 = r12.asDouble()     // Catch:{ NumberFormatException -> 0x02c0 }
            java.lang.Double r0 = java.lang.Double.valueOf(r5)     // Catch:{ NumberFormatException -> 0x02c0 }
            return r0
        L_0x02a5:
            long r5 = r12.asLong()     // Catch:{ NumberFormatException -> 0x02c0 }
            java.lang.Long r0 = java.lang.Long.valueOf(r5)     // Catch:{ NumberFormatException -> 0x02c0 }
            return r0
        L_0x02ae:
            int r1 = r12.asInt()     // Catch:{ NumberFormatException -> 0x02c0 }
            java.lang.Integer r0 = java.lang.Integer.valueOf(r1)     // Catch:{ NumberFormatException -> 0x02c0 }
            return r0
        L_0x02b7:
            float r1 = r12.asFloat()     // Catch:{ NumberFormatException -> 0x02c0 }
            java.lang.Float r0 = java.lang.Float.valueOf(r1)     // Catch:{ NumberFormatException -> 0x02c0 }
            return r0
        L_0x02c0:
            r1 = move-exception
        L_0x02c1:
            com.badlogic.gdx.utils.JsonValue r1 = new com.badlogic.gdx.utils.JsonValue
            java.lang.String r5 = r12.asString()
            r1.<init>((java.lang.String) r5)
            r12 = r1
        L_0x02cb:
            boolean r1 = r12.isBoolean()
            if (r1 == 0) goto L_0x02f1
            if (r10 == 0) goto L_0x02dd
            java.lang.Class r1 = java.lang.Boolean.TYPE     // Catch:{ NumberFormatException -> 0x02e6 }
            if (r10 == r1) goto L_0x02dd
            java.lang.Class<java.lang.Boolean> r1 = java.lang.Boolean.class
            if (r10 != r1) goto L_0x02dc
            goto L_0x02dd
        L_0x02dc:
            goto L_0x02e7
        L_0x02dd:
            boolean r1 = r12.asBoolean()     // Catch:{ NumberFormatException -> 0x02e6 }
            java.lang.Boolean r0 = java.lang.Boolean.valueOf(r1)     // Catch:{ NumberFormatException -> 0x02e6 }
            return r0
        L_0x02e6:
            r1 = move-exception
        L_0x02e7:
            com.badlogic.gdx.utils.JsonValue r1 = new com.badlogic.gdx.utils.JsonValue
            java.lang.String r5 = r12.asString()
            r1.<init>((java.lang.String) r5)
            r12 = r1
        L_0x02f1:
            boolean r1 = r12.isString()
            if (r1 == 0) goto L_0x03c6
            java.lang.String r0 = r12.asString()
            if (r10 == 0) goto L_0x03c5
            java.lang.Class<java.lang.String> r1 = java.lang.String.class
            if (r10 != r1) goto L_0x0303
            goto L_0x03c5
        L_0x0303:
            java.lang.Class r1 = java.lang.Integer.TYPE     // Catch:{ NumberFormatException -> 0x0358 }
            if (r10 == r1) goto L_0x0353
            java.lang.Class<java.lang.Integer> r1 = java.lang.Integer.class
            if (r10 != r1) goto L_0x030c
            goto L_0x0353
        L_0x030c:
            java.lang.Class r1 = java.lang.Float.TYPE     // Catch:{ NumberFormatException -> 0x0358 }
            if (r10 == r1) goto L_0x034e
            java.lang.Class<java.lang.Float> r1 = java.lang.Float.class
            if (r10 != r1) goto L_0x0315
            goto L_0x034e
        L_0x0315:
            java.lang.Class r1 = java.lang.Long.TYPE     // Catch:{ NumberFormatException -> 0x0358 }
            if (r10 == r1) goto L_0x0349
            java.lang.Class<java.lang.Long> r1 = java.lang.Long.class
            if (r10 != r1) goto L_0x031e
            goto L_0x0349
        L_0x031e:
            java.lang.Class r1 = java.lang.Double.TYPE     // Catch:{ NumberFormatException -> 0x0358 }
            if (r10 == r1) goto L_0x0344
            java.lang.Class<java.lang.Double> r1 = java.lang.Double.class
            if (r10 != r1) goto L_0x0327
            goto L_0x0344
        L_0x0327:
            java.lang.Class r1 = java.lang.Short.TYPE     // Catch:{ NumberFormatException -> 0x0358 }
            if (r10 == r1) goto L_0x033f
            java.lang.Class<java.lang.Short> r1 = java.lang.Short.class
            if (r10 != r1) goto L_0x0330
            goto L_0x033f
        L_0x0330:
            java.lang.Class r1 = java.lang.Byte.TYPE     // Catch:{ NumberFormatException -> 0x0358 }
            if (r10 == r1) goto L_0x033a
            java.lang.Class<java.lang.Byte> r1 = java.lang.Byte.class
            if (r10 != r1) goto L_0x0339
            goto L_0x033a
        L_0x0339:
            goto L_0x0359
        L_0x033a:
            java.lang.Byte r1 = java.lang.Byte.valueOf(r0)     // Catch:{ NumberFormatException -> 0x0358 }
            return r1
        L_0x033f:
            java.lang.Short r1 = java.lang.Short.valueOf(r0)     // Catch:{ NumberFormatException -> 0x0358 }
            return r1
        L_0x0344:
            java.lang.Double r1 = java.lang.Double.valueOf(r0)     // Catch:{ NumberFormatException -> 0x0358 }
            return r1
        L_0x0349:
            java.lang.Long r1 = java.lang.Long.valueOf(r0)     // Catch:{ NumberFormatException -> 0x0358 }
            return r1
        L_0x034e:
            java.lang.Float r1 = java.lang.Float.valueOf(r0)     // Catch:{ NumberFormatException -> 0x0358 }
            return r1
        L_0x0353:
            java.lang.Integer r1 = java.lang.Integer.valueOf(r0)     // Catch:{ NumberFormatException -> 0x0358 }
            return r1
        L_0x0358:
            r1 = move-exception
        L_0x0359:
            java.lang.Class r1 = java.lang.Boolean.TYPE
            if (r10 == r1) goto L_0x03c0
            java.lang.Class<java.lang.Boolean> r1 = java.lang.Boolean.class
            if (r10 != r1) goto L_0x0362
            goto L_0x03c0
        L_0x0362:
            java.lang.Class r1 = java.lang.Character.TYPE
            if (r10 == r1) goto L_0x03b6
            java.lang.Class<java.lang.Character> r1 = java.lang.Character.class
            if (r10 != r1) goto L_0x036b
            goto L_0x03b6
        L_0x036b:
            java.lang.Class<java.lang.Enum> r1 = java.lang.Enum.class
            boolean r1 = com.badlogic.gdx.utils.reflect.ClassReflection.isAssignableFrom(r1, r10)
            if (r1 == 0) goto L_0x038f
            java.lang.Object[] r1 = r10.getEnumConstants()
            java.lang.Enum[] r1 = (java.lang.Enum[]) r1
            java.lang.Enum[] r1 = (java.lang.Enum[]) r1
            r5 = 0
            int r6 = r1.length
        L_0x037d:
            if (r5 >= r6) goto L_0x038f
            r7 = r1[r5]
            java.lang.String r8 = r9.convertToString((java.lang.Enum) r7)
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x038c
            return r7
        L_0x038c:
            int r5 = r5 + 1
            goto L_0x037d
        L_0x038f:
            java.lang.Class<java.lang.CharSequence> r1 = java.lang.CharSequence.class
            if (r10 != r1) goto L_0x0394
            return r0
        L_0x0394:
            com.badlogic.gdx.utils.SerializationException r1 = new com.badlogic.gdx.utils.SerializationException
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r4)
            r5.append(r12)
            r5.append(r3)
            java.lang.String r3 = r10.getName()
            r5.append(r3)
            r5.append(r2)
            java.lang.String r2 = r5.toString()
            r1.<init>((java.lang.String) r2)
            throw r1
        L_0x03b6:
            r1 = 0
            char r1 = r0.charAt(r1)
            java.lang.Character r1 = java.lang.Character.valueOf(r1)
            return r1
        L_0x03c0:
            java.lang.Boolean r1 = java.lang.Boolean.valueOf(r0)
            return r1
        L_0x03c5:
            return r0
        L_0x03c6:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.utils.Json.readValue(java.lang.Class, java.lang.Class, com.badlogic.gdx.utils.JsonValue):java.lang.Object");
    }

    public void copyFields(Object from, Object to) {
        ObjectMap<String, FieldMetadata> toFields = getFields(from.getClass());
        ObjectMap.Entries<String, FieldMetadata> it = getFields(from.getClass()).iterator();
        while (it.hasNext()) {
            ObjectMap.Entry<String, FieldMetadata> entry = (ObjectMap.Entry) it.next();
            FieldMetadata toField = toFields.get(entry.key);
            Field fromField = ((FieldMetadata) entry.value).field;
            if (toField != null) {
                try {
                    toField.field.set(to, fromField.get(from));
                } catch (ReflectionException ex) {
                    throw new SerializationException("Error copying field: " + fromField.getName(), ex);
                }
            } else {
                throw new SerializationException("To object is missing field" + ((String) entry.key));
            }
        }
    }

    private String convertToString(Enum e) {
        return this.enumNames ? e.name() : e.toString();
    }

    private String convertToString(Object object) {
        if (object instanceof Enum) {
            return convertToString((Enum) object);
        }
        if (object instanceof Class) {
            return ((Class) object).getName();
        }
        return String.valueOf(object);
    }

    /* access modifiers changed from: protected */
    public Object newInstance(Class type) {
        try {
            return ClassReflection.newInstance(type);
        } catch (Exception e) {
            ex = e;
            try {
                Constructor constructor = ClassReflection.getDeclaredConstructor(type, new Class[0]);
                constructor.setAccessible(true);
                return constructor.newInstance(new Object[0]);
            } catch (SecurityException e2) {
                throw new SerializationException("Error constructing instance of class: " + type.getName(), ex);
            } catch (ReflectionException e3) {
                if (ClassReflection.isAssignableFrom(Enum.class, type)) {
                    if (type.getEnumConstants() == null) {
                        type = type.getSuperclass();
                    }
                    return type.getEnumConstants()[0];
                } else if (type.isArray()) {
                    throw new SerializationException("Encountered JSON object when expected array of type: " + type.getName(), ex);
                } else if (!ClassReflection.isMemberClass(type) || ClassReflection.isStaticClass(type)) {
                    throw new SerializationException("Class cannot be created (missing no-arg constructor): " + type.getName(), ex);
                } else {
                    throw new SerializationException("Class cannot be created (non-static member class): " + type.getName(), ex);
                }
            } catch (Exception privateConstructorException) {
                ex = privateConstructorException;
                throw new SerializationException("Error constructing instance of class: " + type.getName(), ex);
            }
        }
    }

    public String prettyPrint(Object object) {
        return prettyPrint(object, 0);
    }

    public String prettyPrint(String json) {
        return prettyPrint(json, 0);
    }

    public String prettyPrint(Object object, int singleLineColumns) {
        return prettyPrint(toJson(object), singleLineColumns);
    }

    public String prettyPrint(String json, int singleLineColumns) {
        return new JsonReader().parse(json).prettyPrint(this.outputType, singleLineColumns);
    }

    public String prettyPrint(Object object, JsonValue.PrettyPrintSettings settings) {
        return prettyPrint(toJson(object), settings);
    }

    public String prettyPrint(String json, JsonValue.PrettyPrintSettings settings) {
        return new JsonReader().parse(json).prettyPrint(settings);
    }

    private static class FieldMetadata {
        boolean deprecated;
        Class elementType;
        final Field field;

        public FieldMetadata(Field field2) {
            this.field = field2;
            this.elementType = field2.getElementType((ClassReflection.isAssignableFrom(ObjectMap.class, field2.getType()) || ClassReflection.isAssignableFrom(Map.class, field2.getType())) ? 1 : 0);
            this.deprecated = field2.isAnnotationPresent(Deprecated.class);
        }
    }

    public static abstract class ReadOnlySerializer<T> implements Serializer<T> {
        public abstract T read(Json json, JsonValue jsonValue, Class cls);

        public void write(Json json, T t, Class knownType) {
        }
    }
}
