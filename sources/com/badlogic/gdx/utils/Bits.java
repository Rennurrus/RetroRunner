package com.badlogic.gdx.utils;

public class Bits {
    long[] bits = {0};

    public Bits() {
    }

    public Bits(int nbits) {
        checkCapacity(nbits >>> 6);
    }

    public boolean get(int index) {
        int word = index >>> 6;
        long[] jArr = this.bits;
        if (word >= jArr.length) {
            return false;
        }
        if (((1 << (index & 63)) & jArr[word]) != 0) {
            return true;
        }
        return false;
    }

    public boolean getAndClear(int index) {
        int word = index >>> 6;
        long[] jArr = this.bits;
        if (word >= jArr.length) {
            return false;
        }
        long oldBits = jArr[word];
        jArr[word] = jArr[word] & ((1 << (index & 63)) ^ -1);
        if (jArr[word] != oldBits) {
            return true;
        }
        return false;
    }

    public boolean getAndSet(int index) {
        int word = index >>> 6;
        checkCapacity(word);
        long[] jArr = this.bits;
        long oldBits = jArr[word];
        jArr[word] = jArr[word] | (1 << (index & 63));
        return jArr[word] == oldBits;
    }

    public void set(int index) {
        int word = index >>> 6;
        checkCapacity(word);
        long[] jArr = this.bits;
        jArr[word] = jArr[word] | (1 << (index & 63));
    }

    public void flip(int index) {
        int word = index >>> 6;
        checkCapacity(word);
        long[] jArr = this.bits;
        jArr[word] = jArr[word] ^ (1 << (index & 63));
    }

    private void checkCapacity(int len) {
        long[] jArr = this.bits;
        if (len >= jArr.length) {
            long[] newBits = new long[(len + 1)];
            System.arraycopy(jArr, 0, newBits, 0, jArr.length);
            this.bits = newBits;
        }
    }

    public void clear(int index) {
        int word = index >>> 6;
        long[] jArr = this.bits;
        if (word < jArr.length) {
            jArr[word] = jArr[word] & ((1 << (index & 63)) ^ -1);
        }
    }

    public void clear() {
        long[] bits2 = this.bits;
        int length = bits2.length;
        for (int i = 0; i < length; i++) {
            bits2[i] = 0;
        }
    }

    public int numBits() {
        return this.bits.length << 6;
    }

    public int length() {
        long[] bits2 = this.bits;
        for (int word = bits2.length - 1; word >= 0; word--) {
            long bitsAtWord = bits2[word];
            if (bitsAtWord != 0) {
                for (int bit = 63; bit >= 0; bit--) {
                    if (((1 << (bit & 63)) & bitsAtWord) != 0) {
                        return (word << 6) + bit + 1;
                    }
                }
                continue;
            }
        }
        return 0;
    }

    public boolean notEmpty() {
        return !isEmpty();
    }

    public boolean isEmpty() {
        for (long j : this.bits) {
            if (j != 0) {
                return false;
            }
        }
        return true;
    }

    public int nextSetBit(int fromIndex) {
        long[] bits2 = this.bits;
        int word = fromIndex >>> 6;
        int bitsLength = bits2.length;
        if (word >= bitsLength) {
            return -1;
        }
        long bitsAtWord = bits2[word];
        if (bitsAtWord != 0) {
            for (int i = fromIndex & 63; i < 64; i++) {
                if (((1 << (i & 63)) & bitsAtWord) != 0) {
                    return (word << 6) + i;
                }
            }
        }
        while (true) {
            word++;
            if (word >= bitsLength) {
                return -1;
            }
            if (word != 0) {
                long bitsAtWord2 = bits2[word];
                if (bitsAtWord2 != 0) {
                    for (int i2 = 0; i2 < 64; i2++) {
                        if (((1 << (i2 & 63)) & bitsAtWord2) != 0) {
                            return (word << 6) + i2;
                        }
                    }
                    continue;
                } else {
                    continue;
                }
            }
        }
    }

    public int nextClearBit(int fromIndex) {
        long[] bits2 = this.bits;
        int word = fromIndex >>> 6;
        int bitsLength = bits2.length;
        if (word >= bitsLength) {
            return bits2.length << 6;
        }
        long bitsAtWord = bits2[word];
        for (int i = fromIndex & 63; i < 64; i++) {
            if (((1 << (i & 63)) & bitsAtWord) == 0) {
                return (word << 6) + i;
            }
        }
        while (true) {
            word++;
            if (word >= bitsLength) {
                return bits2.length << 6;
            }
            if (word == 0) {
                return word << 6;
            }
            long bitsAtWord2 = bits2[word];
            int i2 = 0;
            while (true) {
                if (i2 < 64) {
                    if (((1 << (i2 & 63)) & bitsAtWord2) == 0) {
                        return (word << 6) + i2;
                    }
                    i2++;
                }
            }
        }
    }

    public void and(Bits other) {
        int commonWords = Math.min(this.bits.length, other.bits.length);
        for (int i = 0; commonWords > i; i++) {
            long[] jArr = this.bits;
            jArr[i] = jArr[i] & other.bits[i];
        }
        long[] jArr2 = this.bits;
        if (jArr2.length > commonWords) {
            int s = jArr2.length;
            for (int i2 = commonWords; s > i2; i2++) {
                this.bits[i2] = 0;
            }
        }
    }

    public void andNot(Bits other) {
        int i = 0;
        int j = this.bits.length;
        int k = other.bits.length;
        while (i < j && i < k) {
            long[] jArr = this.bits;
            jArr[i] = jArr[i] & (other.bits[i] ^ -1);
            i++;
        }
    }

    public void or(Bits other) {
        int commonWords = Math.min(this.bits.length, other.bits.length);
        for (int i = 0; commonWords > i; i++) {
            long[] jArr = this.bits;
            jArr[i] = jArr[i] | other.bits[i];
        }
        long[] jArr2 = other.bits;
        if (commonWords < jArr2.length) {
            checkCapacity(jArr2.length);
            int s = other.bits.length;
            for (int i2 = commonWords; s > i2; i2++) {
                this.bits[i2] = other.bits[i2];
            }
        }
    }

    public void xor(Bits other) {
        int commonWords = Math.min(this.bits.length, other.bits.length);
        for (int i = 0; commonWords > i; i++) {
            long[] jArr = this.bits;
            jArr[i] = jArr[i] ^ other.bits[i];
        }
        long[] jArr2 = other.bits;
        if (commonWords < jArr2.length) {
            checkCapacity(jArr2.length);
            int s = other.bits.length;
            for (int i2 = commonWords; s > i2; i2++) {
                this.bits[i2] = other.bits[i2];
            }
        }
    }

    public boolean intersects(Bits other) {
        long[] bits2 = this.bits;
        long[] otherBits = other.bits;
        for (int i = Math.min(bits2.length, otherBits.length) - 1; i >= 0; i--) {
            if ((bits2[i] & otherBits[i]) != 0) {
                return true;
            }
        }
        return false;
    }

    public boolean containsAll(Bits other) {
        long[] bits2 = this.bits;
        long[] otherBits = other.bits;
        int otherBitsLength = otherBits.length;
        int bitsLength = bits2.length;
        for (int i = bitsLength; i < otherBitsLength; i++) {
            if (otherBits[i] != 0) {
                return false;
            }
        }
        for (int i2 = Math.min(bitsLength, otherBitsLength) - 1; i2 >= 0; i2--) {
            if ((bits2[i2] & otherBits[i2]) != otherBits[i2]) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int word = length() >>> 6;
        int hash = 0;
        for (int i = 0; word >= i; i++) {
            long[] jArr = this.bits;
            hash = (hash * 127) + ((int) (jArr[i] ^ (jArr[i] >>> 32)));
        }
        return hash;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Bits other = (Bits) obj;
        long[] otherBits = other.bits;
        int commonWords = Math.min(this.bits.length, otherBits.length);
        for (int i = 0; commonWords > i; i++) {
            if (this.bits[i] != otherBits[i]) {
                return false;
            }
        }
        if (this.bits.length == otherBits.length || length() == other.length()) {
            return true;
        }
        return false;
    }
}
