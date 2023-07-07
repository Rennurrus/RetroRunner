package com.badlogic.gdx.utils;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.ObjectMap;
import com.twi.game.BuildConfig;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;

public class XmlReader {
    private static final byte[] _xml_actions = init__xml_actions_0();
    private static final short[] _xml_index_offsets = init__xml_index_offsets_0();
    private static final byte[] _xml_indicies = init__xml_indicies_0();
    private static final byte[] _xml_key_offsets = init__xml_key_offsets_0();
    private static final byte[] _xml_range_lengths = init__xml_range_lengths_0();
    private static final byte[] _xml_single_lengths = init__xml_single_lengths_0();
    private static final byte[] _xml_trans_actions = init__xml_trans_actions_0();
    private static final char[] _xml_trans_keys = init__xml_trans_keys_0();
    private static final byte[] _xml_trans_targs = init__xml_trans_targs_0();
    static final int xml_en_elementBody = 15;
    static final int xml_en_main = 1;
    static final int xml_error = 0;
    static final int xml_first_final = 34;
    static final int xml_start = 1;
    private Element current;
    private final Array<Element> elements = new Array<>(8);
    private Element root;
    private final StringBuilder textBuffer = new StringBuilder(64);

    public Element parse(String xml) {
        char[] data = xml.toCharArray();
        return parse(data, 0, data.length);
    }

    public Element parse(Reader reader) {
        try {
            char[] data = new char[GL20.GL_STENCIL_BUFFER_BIT];
            int offset = 0;
            while (true) {
                int length = reader.read(data, offset, data.length - offset);
                if (length == -1) {
                    Element parse = parse(data, 0, offset);
                    StreamUtils.closeQuietly(reader);
                    return parse;
                } else if (length == 0) {
                    char[] newData = new char[(data.length * 2)];
                    System.arraycopy(data, 0, newData, 0, data.length);
                    data = newData;
                } else {
                    offset += length;
                }
            }
        } catch (IOException ex) {
            throw new SerializationException((Throwable) ex);
        } catch (Throwable th) {
            StreamUtils.closeQuietly(reader);
            throw th;
        }
    }

    public Element parse(InputStream input) {
        try {
            Element parse = parse((Reader) new InputStreamReader(input, "UTF-8"));
            StreamUtils.closeQuietly(input);
            return parse;
        } catch (IOException ex) {
            throw new SerializationException((Throwable) ex);
        } catch (Throwable th) {
            StreamUtils.closeQuietly(input);
            throw th;
        }
    }

    public Element parse(FileHandle file) {
        try {
            return parse(file.reader("UTF-8"));
        } catch (Exception ex) {
            throw new SerializationException("Error parsing file: " + file, ex);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v2, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v47, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v12, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v16, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v4, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v52, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v5, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v55, resolved type: short} */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=int, for r4v6, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=int, for r7v7, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=int, for r8v13, types: [byte] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x02c8  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x02d4  */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x02d7  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00b2  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.badlogic.gdx.utils.XmlReader.Element parse(char[] r26, int r27, int r28) {
        /*
            r25 = this;
            r0 = r25
            r1 = r26
            r2 = r27
            r3 = r28
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 1
            r8 = 0
            r9 = 0
        L_0x000e:
            r12 = 2
            r13 = 1
            if (r9 == 0) goto L_0x001e
            if (r9 == r13) goto L_0x0026
            if (r9 == r12) goto L_0x001a
            r21 = r9
            goto L_0x02de
        L_0x001a:
            r21 = r9
            goto L_0x02d2
        L_0x001e:
            if (r2 != r3) goto L_0x0022
            r9 = 4
            goto L_0x000e
        L_0x0022:
            if (r7 != 0) goto L_0x0026
            r9 = 5
            goto L_0x000e
        L_0x0026:
            byte[] r14 = _xml_key_offsets
            byte r14 = r14[r7]
            short[] r15 = _xml_index_offsets
            short r8 = r15[r7]
            byte[] r15 = _xml_single_lengths
            byte r15 = r15[r7]
            if (r15 <= 0) goto L_0x0067
            r16 = r14
            int r17 = r14 + r15
            int r17 = r17 + -1
            r10 = r16
            r11 = r17
        L_0x003e:
            if (r11 >= r10) goto L_0x0043
            int r14 = r14 + r15
            int r8 = r8 + r15
            goto L_0x0067
        L_0x0043:
            int r17 = r11 - r10
            int r17 = r17 >> 1
            int r17 = r10 + r17
            char r13 = r1[r2]
            char[] r19 = _xml_trans_keys
            char r12 = r19[r17]
            if (r13 >= r12) goto L_0x0056
            int r11 = r17 + -1
            r12 = 2
            r13 = 1
            goto L_0x003e
        L_0x0056:
            char r12 = r1[r2]
            char r13 = r19[r17]
            if (r12 <= r13) goto L_0x0061
            int r10 = r17 + 1
            r12 = 2
            r13 = 1
            goto L_0x003e
        L_0x0061:
            int r12 = r17 - r14
            int r8 = r8 + r12
            r19 = r4
            goto L_0x00a4
        L_0x0067:
            byte[] r10 = _xml_range_lengths
            byte r15 = r10[r7]
            if (r15 <= 0) goto L_0x00a2
            r10 = r14
            int r11 = r15 << 1
            int r11 = r11 + r14
            r12 = 2
            int r11 = r11 - r12
        L_0x0073:
            if (r11 >= r10) goto L_0x0079
            int r8 = r8 + r15
            r19 = r4
            goto L_0x00a4
        L_0x0079:
            int r12 = r11 - r10
            r13 = 1
            int r12 = r12 >> r13
            r12 = r12 & -2
            int r12 = r12 + r10
            char r13 = r1[r2]
            char[] r17 = _xml_trans_keys
            r19 = r4
            char r4 = r17[r12]
            if (r13 >= r4) goto L_0x008f
            int r11 = r12 + -2
            r4 = r19
            goto L_0x0073
        L_0x008f:
            char r4 = r1[r2]
            int r13 = r12 + 1
            char r13 = r17[r13]
            if (r4 <= r13) goto L_0x009c
            int r10 = r12 + 2
            r4 = r19
            goto L_0x0073
        L_0x009c:
            int r4 = r12 - r14
            r13 = 1
            int r4 = r4 >> r13
            int r8 = r8 + r4
            goto L_0x00a4
        L_0x00a2:
            r19 = r4
        L_0x00a4:
            byte[] r4 = _xml_indicies
            byte r8 = r4[r8]
            byte[] r4 = _xml_trans_targs
            byte r4 = r4[r8]
            byte[] r7 = _xml_trans_actions
            byte r10 = r7[r8]
            if (r10 == 0) goto L_0x02c8
            byte r7 = r7[r8]
            byte[] r10 = _xml_actions
            int r11 = r7 + 1
            byte r7 = r10[r7]
            r10 = r6
            r6 = r19
        L_0x00bd:
            int r12 = r7 + -1
            if (r7 <= 0) goto L_0x02bd
            byte[] r7 = _xml_actions
            int r13 = r11 + 1
            byte r7 = r7[r11]
            switch(r7) {
                case 0: goto L_0x02a8;
                case 1: goto L_0x01e8;
                case 2: goto L_0x01d4;
                case 3: goto L_0x01c4;
                case 4: goto L_0x01ae;
                case 5: goto L_0x019b;
                case 6: goto L_0x0186;
                case 7: goto L_0x00d5;
                default: goto L_0x00ca;
            }
        L_0x00ca:
            r17 = r4
            r20 = r8
            r21 = r9
            r9 = 2
            r18 = 1
            goto L_0x02b3
        L_0x00d5:
            r7 = r2
        L_0x00d6:
            if (r7 == r6) goto L_0x00f4
            int r11 = r7 + -1
            char r11 = r1[r11]
            r17 = r4
            r4 = 9
            if (r11 == r4) goto L_0x00ef
            r4 = 10
            if (r11 == r4) goto L_0x00ef
            r4 = 13
            if (r11 == r4) goto L_0x00ef
            r4 = 32
            if (r11 == r4) goto L_0x00ef
            goto L_0x00f6
        L_0x00ef:
            int r7 = r7 + -1
            r4 = r17
            goto L_0x00d6
        L_0x00f4:
            r17 = r4
        L_0x00f6:
            r4 = r6
            r11 = 0
        L_0x00f8:
            if (r4 == r7) goto L_0x0158
            int r19 = r4 + 1
            char r4 = r1[r4]
            r20 = r8
            r8 = 38
            if (r4 == r8) goto L_0x0109
            r4 = r19
            r8 = r20
            goto L_0x00f8
        L_0x0109:
            r4 = r19
            r8 = r19
        L_0x010d:
            if (r8 == r7) goto L_0x014c
            int r19 = r8 + 1
            char r8 = r1[r8]
            r21 = r9
            r9 = 59
            if (r8 == r9) goto L_0x011e
            r8 = r19
            r9 = r21
            goto L_0x010d
        L_0x011e:
            com.badlogic.gdx.utils.StringBuilder r8 = r0.textBuffer
            int r9 = r4 - r6
            r18 = 1
            int r9 = r9 + -1
            r8.append((char[]) r1, (int) r6, (int) r9)
            java.lang.String r8 = new java.lang.String
            int r9 = r19 - r4
            int r9 = r9 + -1
            r8.<init>(r1, r4, r9)
            java.lang.String r9 = r0.entity(r8)
            r22 = r4
            com.badlogic.gdx.utils.StringBuilder r4 = r0.textBuffer
            if (r9 == 0) goto L_0x0140
            r23 = r8
            r8 = r9
            goto L_0x0142
        L_0x0140:
            r23 = r8
        L_0x0142:
            r4.append((java.lang.String) r8)
            r6 = r19
            r4 = 1
            r11 = r4
            r4 = r19
            goto L_0x0153
        L_0x014c:
            r22 = r4
            r21 = r9
            r18 = 1
            r4 = r8
        L_0x0153:
            r8 = r20
            r9 = r21
            goto L_0x00f8
        L_0x0158:
            r20 = r8
            r21 = r9
            r18 = 1
            if (r11 == 0) goto L_0x0179
            if (r6 >= r7) goto L_0x0169
            com.badlogic.gdx.utils.StringBuilder r8 = r0.textBuffer
            int r9 = r7 - r6
            r8.append((char[]) r1, (int) r6, (int) r9)
        L_0x0169:
            com.badlogic.gdx.utils.StringBuilder r8 = r0.textBuffer
            java.lang.String r8 = r8.toString()
            r0.text(r8)
            com.badlogic.gdx.utils.StringBuilder r8 = r0.textBuffer
            r9 = 0
            r8.setLength(r9)
            goto L_0x0183
        L_0x0179:
            java.lang.String r8 = new java.lang.String
            int r9 = r7 - r6
            r8.<init>(r1, r6, r9)
            r0.text(r8)
        L_0x0183:
            r9 = 2
            goto L_0x02b3
        L_0x0186:
            r17 = r4
            r20 = r8
            r21 = r9
            r18 = 1
            java.lang.String r4 = new java.lang.String
            int r7 = r2 - r6
            r4.<init>(r1, r6, r7)
            r0.attribute(r5, r4)
            r9 = 2
            goto L_0x02b3
        L_0x019b:
            r17 = r4
            r20 = r8
            r21 = r9
            r18 = 1
            java.lang.String r4 = new java.lang.String
            int r7 = r2 - r6
            r4.<init>(r1, r6, r7)
            r5 = r4
            r9 = 2
            goto L_0x02b3
        L_0x01ae:
            r17 = r4
            r20 = r8
            r21 = r9
            r18 = 1
            if (r10 == 0) goto L_0x01c1
            r7 = 15
            r9 = 2
            r4 = r6
            r6 = r10
            r8 = r20
            goto L_0x000e
        L_0x01c1:
            r9 = 2
            goto L_0x02b3
        L_0x01c4:
            r17 = r4
            r20 = r8
            r21 = r9
            r25.close()
            r7 = 15
            r9 = 2
            r4 = r6
            r6 = r10
            goto L_0x000e
        L_0x01d4:
            r17 = r4
            r20 = r8
            r21 = r9
            r4 = 0
            r25.close()
            r7 = 15
            r9 = 2
            r24 = r6
            r6 = r4
            r4 = r24
            goto L_0x000e
        L_0x01e8:
            r17 = r4
            r20 = r8
            r21 = r9
            r18 = 1
            char r4 = r1[r6]
            r7 = 63
            r8 = 33
            if (r4 == r7) goto L_0x0209
            if (r4 != r8) goto L_0x01fb
            goto L_0x0209
        L_0x01fb:
            r10 = 1
            java.lang.String r7 = new java.lang.String
            int r8 = r2 - r6
            r7.<init>(r1, r6, r8)
            r0.open(r7)
            r9 = 2
            goto L_0x02b3
        L_0x0209:
            int r7 = r6 + 1
            char r7 = r1[r7]
            r9 = 91
            if (r7 != r9) goto L_0x0269
            int r7 = r6 + 2
            char r7 = r1[r7]
            r8 = 67
            if (r7 != r8) goto L_0x0269
            int r7 = r6 + 3
            char r7 = r1[r7]
            r8 = 68
            if (r7 != r8) goto L_0x0269
            int r7 = r6 + 4
            char r7 = r1[r7]
            r8 = 65
            if (r7 != r8) goto L_0x0269
            int r7 = r6 + 5
            char r7 = r1[r7]
            r11 = 84
            if (r7 != r11) goto L_0x0269
            int r7 = r6 + 6
            char r7 = r1[r7]
            if (r7 != r8) goto L_0x0269
            int r7 = r6 + 7
            char r7 = r1[r7]
            if (r7 != r9) goto L_0x0269
            int r7 = r6 + 8
            int r2 = r7 + 2
        L_0x0241:
            int r6 = r2 + -2
            char r6 = r1[r6]
            r8 = 93
            if (r6 != r8) goto L_0x0265
            int r6 = r2 + -1
            char r6 = r1[r6]
            if (r6 != r8) goto L_0x0265
            char r6 = r1[r2]
            r8 = 62
            if (r6 == r8) goto L_0x0257
            r9 = 2
            goto L_0x0266
        L_0x0257:
            java.lang.String r6 = new java.lang.String
            int r8 = r2 - r7
            r9 = 2
            int r8 = r8 - r9
            r6.<init>(r1, r7, r8)
            r0.text(r6)
            r6 = r7
            goto L_0x029f
        L_0x0265:
            r9 = 2
        L_0x0266:
            int r2 = r2 + 1
            goto L_0x0241
        L_0x0269:
            r7 = 33
            if (r4 != r7) goto L_0x0296
            int r7 = r6 + 1
            char r7 = r1[r7]
            r8 = 45
            if (r7 != r8) goto L_0x0296
            int r7 = r6 + 2
            char r7 = r1[r7]
            if (r7 != r8) goto L_0x0296
            int r2 = r6 + 3
        L_0x027d:
            char r7 = r1[r2]
            if (r7 != r8) goto L_0x0293
            int r7 = r2 + 1
            char r7 = r1[r7]
            if (r7 != r8) goto L_0x0293
            int r7 = r2 + 2
            char r7 = r1[r7]
            r9 = 62
            if (r7 == r9) goto L_0x0290
            goto L_0x0293
        L_0x0290:
            int r2 = r2 + 2
            goto L_0x029f
        L_0x0293:
            int r2 = r2 + 1
            goto L_0x027d
        L_0x0296:
            char r7 = r1[r2]
            r8 = 62
            if (r7 == r8) goto L_0x029f
            int r2 = r2 + 1
            goto L_0x0296
        L_0x029f:
            r7 = 15
            r9 = 2
            r4 = r6
            r6 = r10
            r8 = r20
            goto L_0x000e
        L_0x02a8:
            r17 = r4
            r20 = r8
            r21 = r9
            r9 = 2
            r18 = 1
            r6 = r2
        L_0x02b3:
            r7 = r12
            r11 = r13
            r4 = r17
            r8 = r20
            r9 = r21
            goto L_0x00bd
        L_0x02bd:
            r17 = r4
            r20 = r8
            r21 = r9
            r4 = r6
            r6 = r10
            r7 = r17
            goto L_0x02d2
        L_0x02c8:
            r17 = r4
            r20 = r8
            r21 = r9
            r7 = r17
            r4 = r19
        L_0x02d2:
            if (r7 != 0) goto L_0x02d7
            r9 = 5
            goto L_0x000e
        L_0x02d7:
            int r2 = r2 + 1
            if (r2 == r3) goto L_0x02de
            r9 = 1
            goto L_0x000e
        L_0x02de:
            if (r2 >= r3) goto L_0x031c
            r8 = 1
            r9 = 0
        L_0x02e3:
            if (r9 >= r2) goto L_0x02f0
            char r10 = r1[r9]
            r11 = 10
            if (r10 != r11) goto L_0x02ed
            int r8 = r8 + 1
        L_0x02ed:
            int r9 = r9 + 1
            goto L_0x02e3
        L_0x02f0:
            com.badlogic.gdx.utils.SerializationException r9 = new com.badlogic.gdx.utils.SerializationException
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "Error parsing XML on line "
            r10.append(r11)
            r10.append(r8)
            java.lang.String r11 = " near: "
            r10.append(r11)
            java.lang.String r11 = new java.lang.String
            int r12 = r3 - r2
            r13 = 32
            int r12 = java.lang.Math.min(r13, r12)
            r11.<init>(r1, r2, r12)
            r10.append(r11)
            java.lang.String r10 = r10.toString()
            r9.<init>((java.lang.String) r10)
            throw r9
        L_0x031c:
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.utils.XmlReader$Element> r8 = r0.elements
            int r8 = r8.size
            if (r8 != 0) goto L_0x0328
            com.badlogic.gdx.utils.XmlReader$Element r8 = r0.root
            r9 = 0
            r0.root = r9
            return r8
        L_0x0328:
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.utils.XmlReader$Element> r8 = r0.elements
            java.lang.Object r8 = r8.peek()
            com.badlogic.gdx.utils.XmlReader$Element r8 = (com.badlogic.gdx.utils.XmlReader.Element) r8
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.utils.XmlReader$Element> r9 = r0.elements
            r9.clear()
            com.badlogic.gdx.utils.SerializationException r9 = new com.badlogic.gdx.utils.SerializationException
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "Error parsing XML, unclosed element: "
            r10.append(r11)
            java.lang.String r11 = r8.getName()
            r10.append(r11)
            java.lang.String r10 = r10.toString()
            r9.<init>((java.lang.String) r10)
            goto L_0x0351
        L_0x0350:
            throw r9
        L_0x0351:
            goto L_0x0350
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.utils.XmlReader.parse(char[], int, int):com.badlogic.gdx.utils.XmlReader$Element");
    }

    private static byte[] init__xml_actions_0() {
        return new byte[]{0, 1, 0, 1, 1, 1, 2, 1, 3, 1, 4, 1, 5, 1, 6, 1, 7, 2, 0, 6, 2, 1, 4, 2, 2, 4};
    }

    private static byte[] init__xml_key_offsets_0() {
        return new byte[]{0, 0, 4, 9, 14, 20, 26, 30, 35, 36, 37, 42, 46, 50, 51, 52, 56, 57, 62, 67, 73, 79, 83, 88, 89, 90, 95, 99, 103, 104, 108, 109, 110, 111, 112, 115};
    }

    private static char[] init__xml_trans_keys_0() {
        return new char[]{' ', '<', 9, 13, ' ', '/', '>', 9, 13, ' ', '/', '>', 9, 13, ' ', '/', '=', '>', 9, 13, ' ', '/', '=', '>', 9, 13, ' ', '=', 9, 13, ' ', '\"', '\'', 9, 13, '\"', '\"', ' ', '/', '>', 9, 13, ' ', '>', 9, 13, ' ', '>', 9, 13, '\'', '\'', ' ', '<', 9, 13, '<', ' ', '/', '>', 9, 13, ' ', '/', '>', 9, 13, ' ', '/', '=', '>', 9, 13, ' ', '/', '=', '>', 9, 13, ' ', '=', 9, 13, ' ', '\"', '\'', 9, 13, '\"', '\"', ' ', '/', '>', 9, 13, ' ', '>', 9, 13, ' ', '>', 9, 13, '<', ' ', '/', 9, 13, '>', '>', '\'', '\'', ' ', 9, 13, 0};
    }

    private static byte[] init__xml_single_lengths_0() {
        return new byte[]{0, 2, 3, 3, 4, 4, 2, 3, 1, 1, 3, 2, 2, 1, 1, 2, 1, 3, 3, 4, 4, 2, 3, 1, 1, 3, 2, 2, 1, 2, 1, 1, 1, 1, 1, 0};
    }

    private static byte[] init__xml_range_lengths_0() {
        return new byte[]{0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 0, 1, 0, 0, 0, 0, 1, 0};
    }

    private static short[] init__xml_index_offsets_0() {
        return new short[]{0, 0, 4, 9, 14, 20, 26, 30, 35, 37, 39, 44, 48, 52, 54, 56, 60, 62, 67, 72, 78, 84, 88, 93, 95, 97, 102, 106, 110, 112, 116, 118, 120, 122, 124, 127};
    }

    private static byte[] init__xml_indicies_0() {
        byte[] bArr = new byte[Input.Keys.CONTROL_LEFT];
        // fill-array-data instruction
        bArr[0] = 0;
        bArr[1] = 2;
        bArr[2] = 0;
        bArr[3] = 1;
        bArr[4] = 2;
        bArr[5] = 1;
        bArr[6] = 1;
        bArr[7] = 2;
        bArr[8] = 3;
        bArr[9] = 5;
        bArr[10] = 6;
        bArr[11] = 7;
        bArr[12] = 5;
        bArr[13] = 4;
        bArr[14] = 9;
        bArr[15] = 10;
        bArr[16] = 1;
        bArr[17] = 11;
        bArr[18] = 9;
        bArr[19] = 8;
        bArr[20] = 13;
        bArr[21] = 1;
        bArr[22] = 14;
        bArr[23] = 1;
        bArr[24] = 13;
        bArr[25] = 12;
        bArr[26] = 15;
        bArr[27] = 16;
        bArr[28] = 15;
        bArr[29] = 1;
        bArr[30] = 16;
        bArr[31] = 17;
        bArr[32] = 18;
        bArr[33] = 16;
        bArr[34] = 1;
        bArr[35] = 20;
        bArr[36] = 19;
        bArr[37] = 22;
        bArr[38] = 21;
        bArr[39] = 9;
        bArr[40] = 10;
        bArr[41] = 11;
        bArr[42] = 9;
        bArr[43] = 1;
        bArr[44] = 23;
        bArr[45] = 24;
        bArr[46] = 23;
        bArr[47] = 1;
        bArr[48] = 25;
        bArr[49] = 11;
        bArr[50] = 25;
        bArr[51] = 1;
        bArr[52] = 20;
        bArr[53] = 26;
        bArr[54] = 22;
        bArr[55] = 27;
        bArr[56] = 29;
        bArr[57] = 30;
        bArr[58] = 29;
        bArr[59] = 28;
        bArr[60] = 32;
        bArr[61] = 31;
        bArr[62] = 30;
        bArr[63] = 34;
        bArr[64] = 1;
        bArr[65] = 30;
        bArr[66] = 33;
        bArr[67] = 36;
        bArr[68] = 37;
        bArr[69] = 38;
        bArr[70] = 36;
        bArr[71] = 35;
        bArr[72] = 40;
        bArr[73] = 41;
        bArr[74] = 1;
        bArr[75] = 42;
        bArr[76] = 40;
        bArr[77] = 39;
        bArr[78] = 44;
        bArr[79] = 1;
        bArr[80] = 45;
        bArr[81] = 1;
        bArr[82] = 44;
        bArr[83] = 43;
        bArr[84] = 46;
        bArr[85] = 47;
        bArr[86] = 46;
        bArr[87] = 1;
        bArr[88] = 47;
        bArr[89] = 48;
        bArr[90] = 49;
        bArr[91] = 47;
        bArr[92] = 1;
        bArr[93] = 51;
        bArr[94] = 50;
        bArr[95] = 53;
        bArr[96] = 52;
        bArr[97] = 40;
        bArr[98] = 41;
        bArr[99] = 42;
        bArr[100] = 40;
        bArr[101] = 1;
        bArr[102] = 54;
        bArr[103] = 55;
        bArr[104] = 54;
        bArr[105] = 1;
        bArr[106] = 56;
        bArr[107] = 42;
        bArr[108] = 56;
        bArr[109] = 1;
        bArr[110] = 57;
        bArr[111] = 1;
        bArr[112] = 57;
        bArr[113] = 34;
        bArr[114] = 57;
        bArr[115] = 1;
        bArr[116] = 1;
        bArr[117] = 58;
        bArr[118] = 59;
        bArr[119] = 58;
        bArr[120] = 51;
        bArr[121] = 60;
        bArr[122] = 53;
        bArr[123] = 61;
        bArr[124] = 62;
        bArr[125] = 62;
        bArr[126] = 1;
        bArr[127] = 1;
        bArr[128] = 0;
        return bArr;
    }

    private static byte[] init__xml_trans_targs_0() {
        return new byte[]{1, 0, 2, 3, 3, 4, 11, 34, 5, 4, 11, 34, 5, 6, 7, 6, 7, 8, 13, 9, 10, 9, 10, 12, 34, 12, 14, 14, 16, 15, 17, 16, 17, 18, 30, 18, 19, 26, 28, 20, 19, 26, 28, 20, 21, 22, 21, 22, 23, 32, 24, 25, 24, 25, 27, 28, 27, 29, 31, 35, 33, 33, 34};
    }

    private static byte[] init__xml_trans_actions_0() {
        return new byte[]{0, 0, 0, 1, 0, 3, 3, 20, 1, 0, 0, 9, 0, 11, 11, 0, 0, 0, 0, 1, 17, 0, 13, 5, 23, 0, 1, 0, 1, 0, 0, 0, 15, 1, 0, 0, 3, 3, 20, 1, 0, 0, 9, 0, 11, 11, 0, 0, 0, 0, 1, 17, 0, 13, 5, 23, 0, 0, 0, 7, 1, 0, 0};
    }

    /* access modifiers changed from: protected */
    public void open(String name) {
        Element child = new Element(name, this.current);
        Element parent = this.current;
        if (parent != null) {
            parent.addChild(child);
        }
        this.elements.add(child);
        this.current = child;
    }

    /* access modifiers changed from: protected */
    public void attribute(String name, String value) {
        this.current.setAttribute(name, value);
    }

    /* access modifiers changed from: protected */
    public String entity(String name) {
        if (name.equals("lt")) {
            return "<";
        }
        if (name.equals("gt")) {
            return ">";
        }
        if (name.equals("amp")) {
            return "&";
        }
        if (name.equals("apos")) {
            return "'";
        }
        if (name.equals("quot")) {
            return "\"";
        }
        if (name.startsWith("#x")) {
            return Character.toString((char) Integer.parseInt(name.substring(2), 16));
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public void text(String text) {
        String str;
        String existing = this.current.getText();
        Element element = this.current;
        if (existing != null) {
            str = existing + text;
        } else {
            str = text;
        }
        element.setText(str);
    }

    /* access modifiers changed from: protected */
    public void close() {
        this.root = this.elements.pop();
        this.current = this.elements.size > 0 ? this.elements.peek() : null;
    }

    public static class Element {
        private ObjectMap<String, String> attributes;
        private Array<Element> children;
        private final String name;
        private Element parent;
        private String text;

        public Element(String name2, Element parent2) {
            this.name = name2;
            this.parent = parent2;
        }

        public String getName() {
            return this.name;
        }

        public ObjectMap<String, String> getAttributes() {
            return this.attributes;
        }

        public String getAttribute(String name2) {
            ObjectMap<String, String> objectMap = this.attributes;
            if (objectMap != null) {
                String value = objectMap.get(name2);
                if (value != null) {
                    return value;
                }
                throw new GdxRuntimeException("Element " + this.name + " doesn't have attribute: " + name2);
            }
            throw new GdxRuntimeException("Element " + this.name + " doesn't have attribute: " + name2);
        }

        public String getAttribute(String name2, String defaultValue) {
            String value;
            ObjectMap<String, String> objectMap = this.attributes;
            if (objectMap == null || (value = objectMap.get(name2)) == null) {
                return defaultValue;
            }
            return value;
        }

        public boolean hasAttribute(String name2) {
            ObjectMap<String, String> objectMap = this.attributes;
            if (objectMap == null) {
                return false;
            }
            return objectMap.containsKey(name2);
        }

        public void setAttribute(String name2, String value) {
            if (this.attributes == null) {
                this.attributes = new ObjectMap<>(8);
            }
            this.attributes.put(name2, value);
        }

        public int getChildCount() {
            Array<Element> array = this.children;
            if (array == null) {
                return 0;
            }
            return array.size;
        }

        public Element getChild(int index) {
            Array<Element> array = this.children;
            if (array != null) {
                return array.get(index);
            }
            throw new GdxRuntimeException("Element has no children: " + this.name);
        }

        public void addChild(Element element) {
            if (this.children == null) {
                this.children = new Array<>(8);
            }
            this.children.add(element);
        }

        public String getText() {
            return this.text;
        }

        public void setText(String text2) {
            this.text = text2;
        }

        public void removeChild(int index) {
            Array<Element> array = this.children;
            if (array != null) {
                array.removeIndex(index);
            }
        }

        public void removeChild(Element child) {
            Array<Element> array = this.children;
            if (array != null) {
                array.removeValue(child, true);
            }
        }

        public void remove() {
            this.parent.removeChild(this);
        }

        public Element getParent() {
            return this.parent;
        }

        public String toString() {
            return toString(BuildConfig.FLAVOR);
        }

        public String toString(String indent) {
            String str;
            StringBuilder buffer = new StringBuilder(128);
            buffer.append(indent);
            buffer.append('<');
            buffer.append(this.name);
            ObjectMap<String, String> objectMap = this.attributes;
            if (objectMap != null) {
                ObjectMap.Entries<String, String> it = objectMap.entries().iterator();
                while (it.hasNext()) {
                    ObjectMap.Entry<String, String> entry = (ObjectMap.Entry) it.next();
                    buffer.append(' ');
                    buffer.append((String) entry.key);
                    buffer.append("=\"");
                    buffer.append((String) entry.value);
                    buffer.append('\"');
                }
            }
            if (this.children == null && ((str = this.text) == null || str.length() == 0)) {
                buffer.append("/>");
            } else {
                buffer.append(">\n");
                String childIndent = indent + 9;
                String str2 = this.text;
                if (str2 != null && str2.length() > 0) {
                    buffer.append(childIndent);
                    buffer.append(this.text);
                    buffer.append(10);
                }
                Array<Element> array = this.children;
                if (array != null) {
                    Iterator<Element> it2 = array.iterator();
                    while (it2.hasNext()) {
                        buffer.append(it2.next().toString(childIndent));
                        buffer.append(10);
                    }
                }
                buffer.append(indent);
                buffer.append("</");
                buffer.append(this.name);
                buffer.append('>');
            }
            return buffer.toString();
        }

        public Element getChildByName(String name2) {
            if (this.children == null) {
                return null;
            }
            for (int i = 0; i < this.children.size; i++) {
                Element element = this.children.get(i);
                if (element.name.equals(name2)) {
                    return element;
                }
            }
            return null;
        }

        public boolean hasChild(String name2) {
            if (this.children == null || getChildByName(name2) == null) {
                return false;
            }
            return true;
        }

        public Element getChildByNameRecursive(String name2) {
            if (this.children == null) {
                return null;
            }
            for (int i = 0; i < this.children.size; i++) {
                Element element = this.children.get(i);
                if (element.name.equals(name2)) {
                    return element;
                }
                Element found = element.getChildByNameRecursive(name2);
                if (found != null) {
                    return found;
                }
            }
            return null;
        }

        public boolean hasChildRecursive(String name2) {
            if (this.children == null || getChildByNameRecursive(name2) == null) {
                return false;
            }
            return true;
        }

        public Array<Element> getChildrenByName(String name2) {
            Array<Element> result = new Array<>();
            if (this.children == null) {
                return result;
            }
            for (int i = 0; i < this.children.size; i++) {
                Element child = this.children.get(i);
                if (child.name.equals(name2)) {
                    result.add(child);
                }
            }
            return result;
        }

        public Array<Element> getChildrenByNameRecursively(String name2) {
            Array<Element> result = new Array<>();
            getChildrenByNameRecursively(name2, result);
            return result;
        }

        private void getChildrenByNameRecursively(String name2, Array<Element> result) {
            if (this.children != null) {
                for (int i = 0; i < this.children.size; i++) {
                    Element child = this.children.get(i);
                    if (child.name.equals(name2)) {
                        result.add(child);
                    }
                    child.getChildrenByNameRecursively(name2, result);
                }
            }
        }

        public float getFloatAttribute(String name2) {
            return Float.parseFloat(getAttribute(name2));
        }

        public float getFloatAttribute(String name2, float defaultValue) {
            String value = getAttribute(name2, (String) null);
            if (value == null) {
                return defaultValue;
            }
            return Float.parseFloat(value);
        }

        public int getIntAttribute(String name2) {
            return Integer.parseInt(getAttribute(name2));
        }

        public int getIntAttribute(String name2, int defaultValue) {
            String value = getAttribute(name2, (String) null);
            if (value == null) {
                return defaultValue;
            }
            return Integer.parseInt(value);
        }

        public boolean getBooleanAttribute(String name2) {
            return Boolean.parseBoolean(getAttribute(name2));
        }

        public boolean getBooleanAttribute(String name2, boolean defaultValue) {
            String value = getAttribute(name2, (String) null);
            if (value == null) {
                return defaultValue;
            }
            return Boolean.parseBoolean(value);
        }

        public String get(String name2) {
            String value = get(name2, (String) null);
            if (value != null) {
                return value;
            }
            throw new GdxRuntimeException("Element " + this.name + " doesn't have attribute or child: " + name2);
        }

        public String get(String name2, String defaultValue) {
            String value;
            String value2;
            ObjectMap<String, String> objectMap = this.attributes;
            if (objectMap != null && (value2 = objectMap.get(name2)) != null) {
                return value2;
            }
            Element child = getChildByName(name2);
            if (child == null || (value = child.getText()) == null) {
                return defaultValue;
            }
            return value;
        }

        public int getInt(String name2) {
            String value = get(name2, (String) null);
            if (value != null) {
                return Integer.parseInt(value);
            }
            throw new GdxRuntimeException("Element " + this.name + " doesn't have attribute or child: " + name2);
        }

        public int getInt(String name2, int defaultValue) {
            String value = get(name2, (String) null);
            if (value == null) {
                return defaultValue;
            }
            return Integer.parseInt(value);
        }

        public float getFloat(String name2) {
            String value = get(name2, (String) null);
            if (value != null) {
                return Float.parseFloat(value);
            }
            throw new GdxRuntimeException("Element " + this.name + " doesn't have attribute or child: " + name2);
        }

        public float getFloat(String name2, float defaultValue) {
            String value = get(name2, (String) null);
            if (value == null) {
                return defaultValue;
            }
            return Float.parseFloat(value);
        }

        public boolean getBoolean(String name2) {
            String value = get(name2, (String) null);
            if (value != null) {
                return Boolean.parseBoolean(value);
            }
            throw new GdxRuntimeException("Element " + this.name + " doesn't have attribute or child: " + name2);
        }

        public boolean getBoolean(String name2, boolean defaultValue) {
            String value = get(name2, (String) null);
            if (value == null) {
                return defaultValue;
            }
            return Boolean.parseBoolean(value);
        }
    }
}
