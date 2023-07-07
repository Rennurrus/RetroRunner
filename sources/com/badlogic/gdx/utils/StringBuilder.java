package com.badlogic.gdx.utils;

import com.twi.game.BuildConfig;
import java.util.Arrays;

public class StringBuilder implements Appendable, CharSequence {
    static final int INITIAL_CAPACITY = 16;
    private static final char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    public char[] chars;
    public int length;

    public static int numChars(int value, int radix) {
        int result = value < 0 ? 2 : 1;
        while (true) {
            int i = value / radix;
            value = i;
            if (i == 0) {
                return result;
            }
            result++;
        }
    }

    public static int numChars(long value, int radix) {
        int result = value < 0 ? 2 : 1;
        while (true) {
            long j = value / ((long) radix);
            value = j;
            if (j == 0) {
                return result;
            }
            result++;
        }
    }

    /* access modifiers changed from: package-private */
    public final char[] getValue() {
        return this.chars;
    }

    public StringBuilder() {
        this.chars = new char[16];
    }

    public StringBuilder(int capacity) {
        if (capacity >= 0) {
            this.chars = new char[capacity];
            return;
        }
        throw new NegativeArraySizeException();
    }

    public StringBuilder(CharSequence seq) {
        this(seq.toString());
    }

    public StringBuilder(StringBuilder builder) {
        this.length = builder.length;
        int i = this.length;
        this.chars = new char[(i + 16)];
        System.arraycopy(builder.chars, 0, this.chars, 0, i);
    }

    public StringBuilder(String string) {
        this.length = string.length();
        int i = this.length;
        this.chars = new char[(i + 16)];
        string.getChars(0, i, this.chars, 0);
    }

    private void enlargeBuffer(int min) {
        char[] cArr = this.chars;
        int newSize = (cArr.length >> 1) + cArr.length + 2;
        char[] newData = new char[(min > newSize ? min : newSize)];
        System.arraycopy(this.chars, 0, newData, 0, this.length);
        this.chars = newData;
    }

    /* access modifiers changed from: package-private */
    public final void appendNull() {
        int newSize = this.length + 4;
        if (newSize > this.chars.length) {
            enlargeBuffer(newSize);
        }
        char[] cArr = this.chars;
        int i = this.length;
        this.length = i + 1;
        cArr[i] = 'n';
        int i2 = this.length;
        this.length = i2 + 1;
        cArr[i2] = 'u';
        int i3 = this.length;
        this.length = i3 + 1;
        cArr[i3] = 'l';
        int i4 = this.length;
        this.length = i4 + 1;
        cArr[i4] = 'l';
    }

    /* access modifiers changed from: package-private */
    public final void append0(char[] value) {
        int newSize = this.length + value.length;
        if (newSize > this.chars.length) {
            enlargeBuffer(newSize);
        }
        System.arraycopy(value, 0, this.chars, this.length, value.length);
        this.length = newSize;
    }

    /* access modifiers changed from: package-private */
    public final void append0(char[] value, int offset, int length2) {
        if (offset > value.length || offset < 0) {
            throw new ArrayIndexOutOfBoundsException("Offset out of bounds: " + offset);
        } else if (length2 < 0 || value.length - offset < length2) {
            throw new ArrayIndexOutOfBoundsException("Length out of bounds: " + length2);
        } else {
            int newSize = this.length + length2;
            if (newSize > this.chars.length) {
                enlargeBuffer(newSize);
            }
            System.arraycopy(value, offset, this.chars, this.length, length2);
            this.length = newSize;
        }
    }

    /* access modifiers changed from: package-private */
    public final void append0(char ch) {
        int i = this.length;
        if (i == this.chars.length) {
            enlargeBuffer(i + 1);
        }
        char[] cArr = this.chars;
        int i2 = this.length;
        this.length = i2 + 1;
        cArr[i2] = ch;
    }

    /* access modifiers changed from: package-private */
    public final void append0(String string) {
        if (string == null) {
            appendNull();
            return;
        }
        int adding = string.length();
        int newSize = this.length + adding;
        if (newSize > this.chars.length) {
            enlargeBuffer(newSize);
        }
        string.getChars(0, adding, this.chars, this.length);
        this.length = newSize;
    }

    /* access modifiers changed from: package-private */
    public final void append0(CharSequence s, int start, int end) {
        if (s == null) {
            s = "null";
        }
        if (start < 0 || end < 0 || start > end || end > s.length()) {
            throw new IndexOutOfBoundsException();
        }
        append0(s.subSequence(start, end).toString());
    }

    public int capacity() {
        return this.chars.length;
    }

    public char charAt(int index) {
        if (index >= 0 && index < this.length) {
            return this.chars[index];
        }
        throw new StringIndexOutOfBoundsException(index);
    }

    /* access modifiers changed from: package-private */
    public final void delete0(int start, int end) {
        if (start >= 0) {
            if (end > this.length) {
                end = this.length;
            }
            if (end != start) {
                if (end > start) {
                    int count = this.length - end;
                    if (count >= 0) {
                        char[] cArr = this.chars;
                        System.arraycopy(cArr, end, cArr, start, count);
                    }
                    this.length -= end - start;
                    return;
                }
            } else {
                return;
            }
        }
        throw new StringIndexOutOfBoundsException();
    }

    /* access modifiers changed from: package-private */
    public final void deleteCharAt0(int location) {
        int i;
        if (location < 0 || location >= (i = this.length)) {
            throw new StringIndexOutOfBoundsException(location);
        }
        int count = (i - location) - 1;
        if (count > 0) {
            char[] cArr = this.chars;
            System.arraycopy(cArr, location + 1, cArr, location, count);
        }
        this.length--;
    }

    public void ensureCapacity(int min) {
        char[] cArr = this.chars;
        if (min > cArr.length) {
            int twice = (cArr.length << 1) + 2;
            enlargeBuffer(twice > min ? twice : min);
        }
    }

    public void getChars(int start, int end, char[] dest, int destStart) {
        int i = this.length;
        if (start > i || end > i || start > end) {
            throw new StringIndexOutOfBoundsException();
        }
        System.arraycopy(this.chars, start, dest, destStart, end - start);
    }

    /* access modifiers changed from: package-private */
    public final void insert0(int index, char[] value) {
        if (index < 0 || index > this.length) {
            throw new StringIndexOutOfBoundsException(index);
        } else if (value.length != 0) {
            move(value.length, index);
            System.arraycopy(value, 0, value, index, value.length);
            this.length += value.length;
        }
    }

    /* access modifiers changed from: package-private */
    public final void insert0(int index, char[] value, int start, int length2) {
        if (index < 0 || index > length2) {
            throw new StringIndexOutOfBoundsException(index);
        } else if (start < 0 || length2 < 0 || length2 > value.length - start) {
            throw new StringIndexOutOfBoundsException("offset " + start + ", length " + length2 + ", char[].length " + value.length);
        } else if (length2 != 0) {
            move(length2, index);
            System.arraycopy(value, start, this.chars, index, length2);
            this.length += length2;
        }
    }

    /* access modifiers changed from: package-private */
    public final void insert0(int index, char ch) {
        if (index < 0 || index > this.length) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        move(1, index);
        this.chars[index] = ch;
        this.length++;
    }

    /* access modifiers changed from: package-private */
    public final void insert0(int index, String string) {
        if (index < 0 || index > this.length) {
            throw new StringIndexOutOfBoundsException(index);
        }
        if (string == null) {
            string = "null";
        }
        int min = string.length();
        if (min != 0) {
            move(min, index);
            string.getChars(0, min, this.chars, index);
            this.length += min;
        }
    }

    /* access modifiers changed from: package-private */
    public final void insert0(int index, CharSequence s, int start, int end) {
        if (s == null) {
            s = "null";
        }
        if (index < 0 || index > this.length || start < 0 || end < 0 || start > end || end > s.length()) {
            throw new IndexOutOfBoundsException();
        }
        insert0(index, s.subSequence(start, end).toString());
    }

    public int length() {
        return this.length;
    }

    private void move(int size, int index) {
        char[] cArr = this.chars;
        int length2 = cArr.length;
        int i = this.length;
        if (length2 - i >= size) {
            System.arraycopy(cArr, index, cArr, index + size, i - index);
            return;
        }
        int a = i + size;
        int b = (cArr.length << 1) + 2;
        char[] newData = new char[(a > b ? a : b)];
        System.arraycopy(this.chars, 0, newData, 0, index);
        System.arraycopy(this.chars, index, newData, index + size, this.length - index);
        this.chars = newData;
    }

    /* access modifiers changed from: package-private */
    public final void replace0(int start, int end, String string) {
        if (start >= 0) {
            if (end > this.length) {
                end = this.length;
            }
            if (end > start) {
                int stringLength = string.length();
                int diff = (end - start) - stringLength;
                if (diff > 0) {
                    char[] cArr = this.chars;
                    System.arraycopy(cArr, end, cArr, start + stringLength, this.length - end);
                } else if (diff < 0) {
                    move(-diff, end);
                }
                string.getChars(0, stringLength, this.chars, start);
                this.length -= diff;
                return;
            } else if (start == end) {
                if (string != null) {
                    insert0(start, string);
                    return;
                }
                throw new NullPointerException();
            }
        }
        throw new StringIndexOutOfBoundsException();
    }

    /* access modifiers changed from: package-private */
    public final void reverse0() {
        int i = this.length;
        if (i >= 2) {
            int end = i - 1;
            char[] cArr = this.chars;
            char frontHigh = cArr[0];
            char endLow = cArr[end];
            boolean allowFrontSur = true;
            boolean allowEndSur = true;
            int i2 = 0;
            int mid = i / 2;
            while (i2 < mid) {
                char[] cArr2 = this.chars;
                char frontLow = cArr2[i2 + 1];
                char endHigh = cArr2[end - 1];
                boolean surAtFront = allowFrontSur && frontLow >= 56320 && frontLow <= 57343 && frontHigh >= 55296 && frontHigh <= 56319;
                if (!surAtFront || this.length >= 3) {
                    boolean surAtEnd = allowEndSur && endHigh >= 55296 && endHigh <= 56319 && endLow >= 56320 && endLow <= 57343;
                    allowEndSur = true;
                    allowFrontSur = true;
                    if (surAtFront == surAtEnd) {
                        if (surAtFront) {
                            char[] cArr3 = this.chars;
                            cArr3[end] = frontLow;
                            cArr3[end - 1] = frontHigh;
                            cArr3[i2] = endHigh;
                            cArr3[i2 + 1] = endLow;
                            frontHigh = cArr3[i2 + 2];
                            endLow = cArr3[end - 2];
                            i2++;
                            end--;
                        } else {
                            char[] cArr4 = this.chars;
                            cArr4[end] = frontHigh;
                            cArr4[i2] = endLow;
                            frontHigh = frontLow;
                            endLow = endHigh;
                        }
                    } else if (surAtFront) {
                        char[] cArr5 = this.chars;
                        cArr5[end] = frontLow;
                        cArr5[i2] = endLow;
                        endLow = endHigh;
                        allowFrontSur = false;
                    } else {
                        char[] cArr6 = this.chars;
                        cArr6[end] = frontHigh;
                        cArr6[i2] = endHigh;
                        frontHigh = frontLow;
                        allowEndSur = false;
                    }
                    i2++;
                    end--;
                } else {
                    return;
                }
            }
            if ((this.length & 1) != 1) {
                return;
            }
            if (!allowFrontSur || !allowEndSur) {
                this.chars[end] = allowFrontSur ? endLow : frontHigh;
            }
        }
    }

    public void setCharAt(int index, char ch) {
        if (index < 0 || index >= this.length) {
            throw new StringIndexOutOfBoundsException(index);
        }
        this.chars[index] = ch;
    }

    public void setLength(int newLength) {
        if (newLength >= 0) {
            char[] cArr = this.chars;
            if (newLength > cArr.length) {
                enlargeBuffer(newLength);
            } else {
                int i = this.length;
                if (i < newLength) {
                    Arrays.fill(cArr, i, newLength, 0);
                }
            }
            this.length = newLength;
            return;
        }
        throw new StringIndexOutOfBoundsException(newLength);
    }

    public String substring(int start) {
        int i;
        if (start < 0 || start > (i = this.length)) {
            throw new StringIndexOutOfBoundsException(start);
        } else if (start == i) {
            return BuildConfig.FLAVOR;
        } else {
            return new String(this.chars, start, i - start);
        }
    }

    public String substring(int start, int end) {
        if (start < 0 || start > end || end > this.length) {
            throw new StringIndexOutOfBoundsException();
        } else if (start == end) {
            return BuildConfig.FLAVOR;
        } else {
            return new String(this.chars, start, end - start);
        }
    }

    public String toString() {
        int i = this.length;
        if (i == 0) {
            return BuildConfig.FLAVOR;
        }
        return new String(this.chars, 0, i);
    }

    public CharSequence subSequence(int start, int end) {
        return substring(start, end);
    }

    public int indexOf(String string) {
        return indexOf(string, 0);
    }

    public int indexOf(String subString, int start) {
        if (start < 0) {
            start = 0;
        }
        int subCount = subString.length();
        if (subCount == 0) {
            int i = this.length;
            return (start < i || start == 0) ? start : i;
        }
        int maxIndex = this.length - subCount;
        if (start > maxIndex) {
            return -1;
        }
        char firstChar = subString.charAt(0);
        while (true) {
            int i2 = start;
            boolean found = false;
            while (true) {
                if (i2 > maxIndex) {
                    break;
                } else if (this.chars[i2] == firstChar) {
                    found = true;
                    break;
                } else {
                    i2++;
                }
            }
            if (!found) {
                return -1;
            }
            int o1 = i2;
            int o2 = 0;
            do {
                o2++;
                if (o2 >= subCount) {
                    break;
                }
                o1++;
            } while (this.chars[o1] == subString.charAt(o2));
            if (o2 == subCount) {
                return i2;
            }
            start = i2 + 1;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:24:0x003f, code lost:
        r7 = r7 + 1;
     */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x005a A[LOOP:0: B:12:0x0025->B:30:0x005a, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0059 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int indexOfIgnoreCase(java.lang.String r13, int r14) {
        /*
            r12 = this;
            if (r14 >= 0) goto L_0x0003
            r14 = 0
        L_0x0003:
            int r0 = r13.length()
            if (r0 != 0) goto L_0x0011
            int r1 = r12.length
            if (r14 < r1) goto L_0x000f
            if (r14 != 0) goto L_0x0010
        L_0x000f:
            r1 = r14
        L_0x0010:
            return r1
        L_0x0011:
            int r1 = r12.length
            int r1 = r1 - r0
            r2 = -1
            if (r14 <= r1) goto L_0x0018
            return r2
        L_0x0018:
            r3 = 0
            char r3 = r13.charAt(r3)
            char r3 = java.lang.Character.toUpperCase(r3)
            char r4 = java.lang.Character.toLowerCase(r3)
        L_0x0025:
            r5 = r14
            r6 = 0
        L_0x0027:
            if (r5 > r1) goto L_0x0036
            char[] r7 = r12.chars
            char r7 = r7[r5]
            if (r7 == r3) goto L_0x0035
            if (r7 != r4) goto L_0x0032
            goto L_0x0035
        L_0x0032:
            int r5 = r5 + 1
            goto L_0x0027
        L_0x0035:
            r6 = 1
        L_0x0036:
            if (r6 != 0) goto L_0x0039
            return r2
        L_0x0039:
            r7 = r5
            r8 = 0
        L_0x003b:
            int r8 = r8 + 1
            if (r8 >= r0) goto L_0x0057
            char[] r9 = r12.chars
            int r7 = r7 + 1
            char r9 = r9[r7]
            char r10 = r13.charAt(r8)
            char r10 = java.lang.Character.toUpperCase(r10)
            if (r9 == r10) goto L_0x0056
            char r11 = java.lang.Character.toLowerCase(r10)
            if (r9 == r11) goto L_0x0056
            goto L_0x0057
        L_0x0056:
            goto L_0x003b
        L_0x0057:
            if (r8 != r0) goto L_0x005a
            return r5
        L_0x005a:
            int r14 = r5 + 1
            goto L_0x0025
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.utils.StringBuilder.indexOfIgnoreCase(java.lang.String, int):int");
    }

    public boolean contains(String subString) {
        return indexOf(subString, 0) != -1;
    }

    public boolean containsIgnoreCase(String subString) {
        return indexOfIgnoreCase(subString, 0) != -1;
    }

    public int lastIndexOf(String string) {
        return lastIndexOf(string, this.length);
    }

    public int lastIndexOf(String subString, int start) {
        int subCount = subString.length();
        int i = this.length;
        if (subCount > i || start < 0) {
            return -1;
        }
        if (subCount <= 0) {
            return start < i ? start : i;
        }
        if (start > i - subCount) {
            start = i - subCount;
        }
        char firstChar = subString.charAt(0);
        while (true) {
            int i2 = start;
            boolean found = false;
            while (true) {
                if (i2 < 0) {
                    break;
                } else if (this.chars[i2] == firstChar) {
                    found = true;
                    break;
                } else {
                    i2--;
                }
            }
            if (!found) {
                return -1;
            }
            int o1 = i2;
            int o2 = 0;
            do {
                o2++;
                if (o2 >= subCount) {
                    break;
                }
                o1++;
            } while (this.chars[o1] == subString.charAt(o2));
            if (o2 == subCount) {
                return i2;
            }
            start = i2 - 1;
        }
    }

    public void trimToSize() {
        int i = this.length;
        char[] cArr = this.chars;
        if (i < cArr.length) {
            char[] newValue = new char[i];
            System.arraycopy(cArr, 0, newValue, 0, i);
            this.chars = newValue;
        }
    }

    public int codePointAt(int index) {
        int i;
        if (index >= 0 && index < (i = this.length)) {
            return Character.codePointAt(this.chars, index, i);
        }
        throw new StringIndexOutOfBoundsException(index);
    }

    public int codePointBefore(int index) {
        if (index >= 1 && index <= this.length) {
            return Character.codePointBefore(this.chars, index);
        }
        throw new StringIndexOutOfBoundsException(index);
    }

    public int codePointCount(int beginIndex, int endIndex) {
        if (beginIndex >= 0 && endIndex <= this.length && beginIndex <= endIndex) {
            return Character.codePointCount(this.chars, beginIndex, endIndex - beginIndex);
        }
        throw new StringIndexOutOfBoundsException();
    }

    public int offsetByCodePoints(int index, int codePointOffset) {
        return Character.offsetByCodePoints(this.chars, 0, this.length, index, codePointOffset);
    }

    public StringBuilder append(boolean b) {
        append0(b ? "true" : "false");
        return this;
    }

    public StringBuilder append(char c) {
        append0(c);
        return this;
    }

    public StringBuilder append(int value) {
        return append(value, 0);
    }

    public StringBuilder append(int value, int minLength) {
        return append(value, minLength, '0');
    }

    public StringBuilder append(int value, int minLength, char prefix) {
        if (value == Integer.MIN_VALUE) {
            append0("-2147483648");
            return this;
        }
        if (value < 0) {
            append0('-');
            value = -value;
        }
        if (minLength > 1) {
            for (int j = minLength - numChars(value, 10); j > 0; j--) {
                append(prefix);
            }
        }
        if (value >= 10000) {
            if (value >= 1000000000) {
                append0(digits[(int) ((((long) value) % 10000000000L) / 1000000000)]);
            }
            if (value >= 100000000) {
                append0(digits[(value % 1000000000) / 100000000]);
            }
            if (value >= 10000000) {
                append0(digits[(value % 100000000) / 10000000]);
            }
            if (value >= 1000000) {
                append0(digits[(value % 10000000) / 1000000]);
            }
            if (value >= 100000) {
                append0(digits[(value % 1000000) / 100000]);
            }
            append0(digits[(value % 100000) / 10000]);
        }
        if (value >= 1000) {
            append0(digits[(value % 10000) / 1000]);
        }
        if (value >= 100) {
            append0(digits[(value % 1000) / 100]);
        }
        if (value >= 10) {
            append0(digits[(value % 100) / 10]);
        }
        append0(digits[value % 10]);
        return this;
    }

    public StringBuilder append(long value) {
        return append(value, 0);
    }

    public StringBuilder append(long value, int minLength) {
        return append(value, minLength, '0');
    }

    public StringBuilder append(long value, int minLength, char prefix) {
        if (value == Long.MIN_VALUE) {
            append0("-9223372036854775808");
            return this;
        }
        if (value < 0) {
            append0('-');
            value = -value;
        }
        if (minLength > 1) {
            for (int j = minLength - numChars(value, 10); j > 0; j--) {
                append(prefix);
            }
        }
        if (value >= 10000) {
            if (value >= 1000000000000000000L) {
                char[] cArr = digits;
                double d = (double) value;
                Double.isNaN(d);
                append0(cArr[(int) ((d % 1.0E19d) / 1.0E18d)]);
            }
            if (value >= 100000000000000000L) {
                append0(digits[(int) ((value % 1000000000000000000L) / 100000000000000000L)]);
            }
            if (value >= 10000000000000000L) {
                append0(digits[(int) ((value % 100000000000000000L) / 10000000000000000L)]);
            }
            if (value >= 1000000000000000L) {
                append0(digits[(int) ((value % 10000000000000000L) / 1000000000000000L)]);
            }
            if (value >= 100000000000000L) {
                append0(digits[(int) ((value % 1000000000000000L) / 100000000000000L)]);
            }
            if (value >= 10000000000000L) {
                append0(digits[(int) ((value % 100000000000000L) / 10000000000000L)]);
            }
            if (value >= 1000000000000L) {
                append0(digits[(int) ((value % 10000000000000L) / 1000000000000L)]);
            }
            if (value >= 100000000000L) {
                append0(digits[(int) ((value % 1000000000000L) / 100000000000L)]);
            }
            if (value >= 10000000000L) {
                append0(digits[(int) ((value % 100000000000L) / 10000000000L)]);
            }
            if (value >= 1000000000) {
                append0(digits[(int) ((value % 10000000000L) / 1000000000)]);
            }
            if (value >= 100000000) {
                append0(digits[(int) ((value % 1000000000) / 100000000)]);
            }
            if (value >= 10000000) {
                append0(digits[(int) ((value % 100000000) / 10000000)]);
            }
            if (value >= 1000000) {
                append0(digits[(int) ((value % 10000000) / 1000000)]);
            }
            if (value >= 100000) {
                append0(digits[(int) ((value % 1000000) / 100000)]);
            }
            append0(digits[(int) ((value % 100000) / 10000)]);
        }
        if (value >= 1000) {
            append0(digits[(int) ((value % 10000) / 1000)]);
        }
        if (value >= 100) {
            append0(digits[(int) ((value % 1000) / 100)]);
        }
        if (value >= 10) {
            append0(digits[(int) ((value % 100) / 10)]);
        }
        append0(digits[(int) (value % 10)]);
        return this;
    }

    public StringBuilder append(float f) {
        append0(Float.toString(f));
        return this;
    }

    public StringBuilder append(double d) {
        append0(Double.toString(d));
        return this;
    }

    public StringBuilder append(Object obj) {
        if (obj == null) {
            appendNull();
        } else {
            append0(obj.toString());
        }
        return this;
    }

    public StringBuilder append(String str) {
        append0(str);
        return this;
    }

    public StringBuilder appendLine(String str) {
        append0(str);
        append0(10);
        return this;
    }

    public StringBuilder append(char[] ch) {
        append0(ch);
        return this;
    }

    public StringBuilder append(char[] str, int offset, int len) {
        append0(str, offset, len);
        return this;
    }

    public StringBuilder append(CharSequence csq) {
        if (csq == null) {
            appendNull();
        } else if (csq instanceof StringBuilder) {
            StringBuilder builder = (StringBuilder) csq;
            append0(builder.chars, 0, builder.length);
        } else {
            append0(csq.toString());
        }
        return this;
    }

    public StringBuilder append(StringBuilder builder) {
        if (builder == null) {
            appendNull();
        } else {
            append0(builder.chars, 0, builder.length);
        }
        return this;
    }

    public StringBuilder append(CharSequence csq, int start, int end) {
        append0(csq, start, end);
        return this;
    }

    public StringBuilder append(StringBuilder builder, int start, int end) {
        if (builder == null) {
            appendNull();
        } else {
            append0(builder.chars, start, end);
        }
        return this;
    }

    public StringBuilder appendCodePoint(int codePoint) {
        append0(Character.toChars(codePoint));
        return this;
    }

    public StringBuilder delete(int start, int end) {
        delete0(start, end);
        return this;
    }

    public StringBuilder deleteCharAt(int index) {
        deleteCharAt0(index);
        return this;
    }

    public void clear() {
        this.length = 0;
    }

    public StringBuilder insert(int offset, boolean b) {
        insert0(offset, b ? "true" : "false");
        return this;
    }

    public StringBuilder insert(int offset, char c) {
        insert0(offset, c);
        return this;
    }

    public StringBuilder insert(int offset, int i) {
        insert0(offset, Integer.toString(i));
        return this;
    }

    public StringBuilder insert(int offset, long l) {
        insert0(offset, Long.toString(l));
        return this;
    }

    public StringBuilder insert(int offset, float f) {
        insert0(offset, Float.toString(f));
        return this;
    }

    public StringBuilder insert(int offset, double d) {
        insert0(offset, Double.toString(d));
        return this;
    }

    public StringBuilder insert(int offset, Object obj) {
        insert0(offset, obj == null ? "null" : obj.toString());
        return this;
    }

    public StringBuilder insert(int offset, String str) {
        insert0(offset, str);
        return this;
    }

    public StringBuilder insert(int offset, char[] ch) {
        insert0(offset, ch);
        return this;
    }

    public StringBuilder insert(int offset, char[] str, int strOffset, int strLen) {
        insert0(offset, str, strOffset, strLen);
        return this;
    }

    public StringBuilder insert(int offset, CharSequence s) {
        insert0(offset, s == null ? "null" : s.toString());
        return this;
    }

    public StringBuilder insert(int offset, CharSequence s, int start, int end) {
        insert0(offset, s, start, end);
        return this;
    }

    public StringBuilder replace(int start, int end, String str) {
        replace0(start, end, str);
        return this;
    }

    public StringBuilder replace(String find, String replace) {
        int findLength = find.length();
        int replaceLength = replace.length();
        int index = 0;
        while (true) {
            int index2 = indexOf(find, index);
            if (index2 == -1) {
                return this;
            }
            replace0(index2, index2 + findLength, replace);
            index = index2 + replaceLength;
        }
    }

    public StringBuilder replace(char find, String replace) {
        int replaceLength = replace.length();
        int index = 0;
        while (index != this.length) {
            if (this.chars[index] == find) {
                replace0(index, index + 1, replace);
                index += replaceLength;
            } else {
                index++;
            }
        }
        return this;
    }

    public StringBuilder reverse() {
        reverse0();
        return this;
    }

    public int hashCode() {
        return ((this.length + 31) * 31) + Arrays.hashCode(this.chars);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        StringBuilder other = (StringBuilder) obj;
        int length2 = this.length;
        if (length2 != other.length) {
            return false;
        }
        char[] chars2 = this.chars;
        char[] chars22 = other.chars;
        for (int i = 0; i < length2; i++) {
            if (chars2[i] != chars22[i]) {
                return false;
            }
        }
        return true;
    }

    public boolean equalsIgnoreCase(StringBuilder other) {
        int length2;
        if (this == other) {
            return true;
        }
        if (other == null || (length2 = this.length) != other.length) {
            return false;
        }
        char[] chars2 = this.chars;
        char[] chars22 = other.chars;
        for (int i = 0; i < length2; i++) {
            char c = chars2[i];
            char upper = Character.toUpperCase(chars22[i]);
            if (c != upper && c != Character.toLowerCase(upper)) {
                return false;
            }
        }
        return true;
    }

    public boolean equalsIgnoreCase(String other) {
        int length2;
        if (other == null || (length2 = this.length) != other.length()) {
            return false;
        }
        char[] chars2 = this.chars;
        for (int i = 0; i < length2; i++) {
            char c = chars2[i];
            char upper = Character.toUpperCase(other.charAt(i));
            if (c != upper && c != Character.toLowerCase(upper)) {
                return false;
            }
        }
        return true;
    }
}
