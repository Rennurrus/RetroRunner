package com.badlogic.gdx.utils;

public class Base64Coder {
    public static final CharMap regularMap = new CharMap('+', '/');
    private static final String systemLineSeparator = "\n";
    public static final CharMap urlsafeMap = new CharMap('-', '_');

    public static class CharMap {
        protected final byte[] decodingMap = new byte[128];
        protected final char[] encodingMap = new char[64];

        public CharMap(char char63, char char64) {
            int i = 0;
            char c = 'A';
            while (c <= 'Z') {
                this.encodingMap[i] = c;
                c = (char) (c + 1);
                i++;
            }
            char c2 = 'a';
            while (c2 <= 'z') {
                this.encodingMap[i] = c2;
                c2 = (char) (c2 + 1);
                i++;
            }
            char c3 = '0';
            while (c3 <= '9') {
                this.encodingMap[i] = c3;
                c3 = (char) (c3 + 1);
                i++;
            }
            char[] cArr = this.encodingMap;
            int i2 = i + 1;
            cArr[i] = char63;
            int i3 = i2 + 1;
            cArr[i2] = char64;
            int i4 = 0;
            while (true) {
                byte[] bArr = this.decodingMap;
                if (i4 >= bArr.length) {
                    break;
                }
                bArr[i4] = -1;
                i4++;
            }
            for (int i5 = 0; i5 < 64; i5++) {
                this.decodingMap[this.encodingMap[i5]] = (byte) i5;
            }
        }

        public byte[] getDecodingMap() {
            return this.decodingMap;
        }

        public char[] getEncodingMap() {
            return this.encodingMap;
        }
    }

    public static String encodeString(String s) {
        return encodeString(s, false);
    }

    public static String encodeString(String s, boolean useUrlsafeEncoding) {
        return new String(encode(s.getBytes(), (useUrlsafeEncoding ? urlsafeMap : regularMap).encodingMap));
    }

    public static String encodeLines(byte[] in) {
        return encodeLines(in, 0, in.length, 76, systemLineSeparator, regularMap.encodingMap);
    }

    public static String encodeLines(byte[] in, int iOff, int iLen, int lineLen, String lineSeparator, CharMap charMap) {
        return encodeLines(in, iOff, iLen, lineLen, lineSeparator, charMap.encodingMap);
    }

    public static String encodeLines(byte[] in, int iOff, int iLen, int lineLen, String lineSeparator, char[] charMap) {
        int blockLen = (lineLen * 3) / 4;
        if (blockLen > 0) {
            StringBuilder buf = new StringBuilder((((iLen + 2) / 3) * 4) + (lineSeparator.length() * (((iLen + blockLen) - 1) / blockLen)));
            int ip = 0;
            while (ip < iLen) {
                int l = Math.min(iLen - ip, blockLen);
                buf.append(encode(in, iOff + ip, l, charMap));
                buf.append(lineSeparator);
                ip += l;
            }
            return buf.toString();
        }
        throw new IllegalArgumentException();
    }

    public static char[] encode(byte[] in) {
        return encode(in, regularMap.encodingMap);
    }

    public static char[] encode(byte[] in, CharMap charMap) {
        return encode(in, 0, in.length, charMap);
    }

    public static char[] encode(byte[] in, char[] charMap) {
        return encode(in, 0, in.length, charMap);
    }

    public static char[] encode(byte[] in, int iLen) {
        return encode(in, 0, iLen, regularMap.encodingMap);
    }

    public static char[] encode(byte[] in, int iOff, int iLen, CharMap charMap) {
        return encode(in, iOff, iLen, charMap.encodingMap);
    }

    public static char[] encode(byte[] in, int iOff, int iLen, char[] charMap) {
        int ip;
        int ip2;
        int oDataLen = ((iLen * 4) + 2) / 3;
        char[] out = new char[(((iLen + 2) / 3) * 4)];
        int ip3 = iOff;
        int iEnd = iOff + iLen;
        int op = 0;
        while (ip3 < iEnd) {
            int ip4 = ip3 + 1;
            int i0 = in[ip3] & 255;
            int ip5 = 0;
            if (ip4 < iEnd) {
                ip = ip4 + 1;
                ip2 = in[ip4] & 255;
            } else {
                ip = ip4;
                ip2 = 0;
            }
            if (ip < iEnd) {
                int i = in[ip] & 255;
                ip++;
                ip5 = i;
            }
            int o2 = ((ip2 & 15) << 2) | (ip5 >>> 6);
            int o3 = ip5 & 63;
            int op2 = op + 1;
            out[op] = charMap[i0 >>> 2];
            int op3 = op2 + 1;
            out[op2] = charMap[((i0 & 3) << 4) | (ip2 >>> 4)];
            char c = '=';
            out[op3] = op3 < oDataLen ? charMap[o2] : '=';
            int op4 = op3 + 1;
            if (op4 < oDataLen) {
                c = charMap[o3];
            }
            out[op4] = c;
            op = op4 + 1;
            ip3 = ip;
        }
        return out;
    }

    public static String decodeString(String s) {
        return decodeString(s, false);
    }

    public static String decodeString(String s, boolean useUrlSafeEncoding) {
        return new String(decode(s.toCharArray(), (useUrlSafeEncoding ? urlsafeMap : regularMap).decodingMap));
    }

    public static byte[] decodeLines(String s) {
        return decodeLines(s, regularMap.decodingMap);
    }

    public static byte[] decodeLines(String s, CharMap inverseCharMap) {
        return decodeLines(s, inverseCharMap.decodingMap);
    }

    public static byte[] decodeLines(String s, byte[] inverseCharMap) {
        char[] buf = new char[s.length()];
        int p = 0;
        for (int ip = 0; ip < s.length(); ip++) {
            char c = s.charAt(ip);
            if (!(c == ' ' || c == 13 || c == 10 || c == 9)) {
                buf[p] = c;
                p++;
            }
        }
        return decode(buf, 0, p, inverseCharMap);
    }

    public static byte[] decode(String s) {
        return decode(s.toCharArray());
    }

    public static byte[] decode(String s, CharMap inverseCharMap) {
        return decode(s.toCharArray(), inverseCharMap);
    }

    public static byte[] decode(char[] in, byte[] inverseCharMap) {
        return decode(in, 0, in.length, inverseCharMap);
    }

    public static byte[] decode(char[] in, CharMap inverseCharMap) {
        return decode(in, 0, in.length, inverseCharMap);
    }

    public static byte[] decode(char[] in) {
        return decode(in, 0, in.length, regularMap.decodingMap);
    }

    public static byte[] decode(char[] in, int iOff, int iLen, CharMap inverseCharMap) {
        return decode(in, iOff, iLen, inverseCharMap.decodingMap);
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=char, code=int, for r7v3, types: [char] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static byte[] decode(char[] r19, int r20, int r21, byte[] r22) {
        /*
            int r0 = r21 % 4
            if (r0 != 0) goto L_0x00b0
            r0 = r21
        L_0x0006:
            if (r0 <= 0) goto L_0x0015
            int r1 = r20 + r0
            int r1 = r1 + -1
            char r1 = r19[r1]
            r2 = 61
            if (r1 != r2) goto L_0x0015
            int r0 = r0 + -1
            goto L_0x0006
        L_0x0015:
            int r1 = r0 * 3
            int r1 = r1 / 4
            byte[] r2 = new byte[r1]
            r3 = r20
            int r4 = r20 + r0
            r5 = 0
        L_0x0020:
            if (r3 >= r4) goto L_0x00ab
            int r6 = r3 + 1
            char r3 = r19[r3]
            int r7 = r6 + 1
            char r6 = r19[r6]
            r8 = 65
            if (r7 >= r4) goto L_0x0033
            int r9 = r7 + 1
            char r7 = r19[r7]
            goto L_0x0036
        L_0x0033:
            r9 = r7
            r7 = 65
        L_0x0036:
            if (r9 >= r4) goto L_0x0041
            int r8 = r9 + 1
            char r9 = r19[r9]
            r18 = r9
            r9 = r8
            r8 = r18
        L_0x0041:
            java.lang.String r10 = "Illegal character in Base64 encoded data."
            r11 = 127(0x7f, float:1.78E-43)
            if (r3 > r11) goto L_0x009f
            if (r6 > r11) goto L_0x009f
            if (r7 > r11) goto L_0x009f
            if (r8 > r11) goto L_0x009f
            byte r11 = r22[r3]
            byte r12 = r22[r6]
            byte r13 = r22[r7]
            byte r14 = r22[r8]
            if (r11 < 0) goto L_0x0093
            if (r12 < 0) goto L_0x0093
            if (r13 < 0) goto L_0x0093
            if (r14 < 0) goto L_0x0093
            int r10 = r11 << 2
            int r15 = r12 >>> 4
            r10 = r10 | r15
            r15 = r12 & 15
            int r15 = r15 << 4
            int r16 = r13 >>> 2
            r15 = r15 | r16
            r16 = r13 & 3
            int r16 = r16 << 6
            r21 = r0
            r0 = r16 | r14
            r16 = r3
            int r3 = r5 + 1
            r17 = r4
            byte r4 = (byte) r10
            r2[r5] = r4
            if (r3 >= r1) goto L_0x0083
            int r4 = r3 + 1
            byte r5 = (byte) r15
            r2[r3] = r5
            r3 = r4
        L_0x0083:
            if (r3 >= r1) goto L_0x008c
            int r4 = r3 + 1
            byte r5 = (byte) r0
            r2[r3] = r5
            r5 = r4
            goto L_0x008d
        L_0x008c:
            r5 = r3
        L_0x008d:
            r0 = r21
            r3 = r9
            r4 = r17
            goto L_0x0020
        L_0x0093:
            r21 = r0
            r16 = r3
            r17 = r4
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            r0.<init>(r10)
            throw r0
        L_0x009f:
            r21 = r0
            r16 = r3
            r17 = r4
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            r0.<init>(r10)
            throw r0
        L_0x00ab:
            r21 = r0
            r17 = r4
            return r2
        L_0x00b0:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.String r1 = "Length of Base64 encoded input string is not a multiple of 4."
            r0.<init>(r1)
            goto L_0x00b9
        L_0x00b8:
            throw r0
        L_0x00b9:
            goto L_0x00b8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.utils.Base64Coder.decode(char[], int, int, byte[]):byte[]");
    }

    private Base64Coder() {
    }
}
