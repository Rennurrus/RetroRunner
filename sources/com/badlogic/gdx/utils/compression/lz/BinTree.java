package com.badlogic.gdx.utils.compression.lz;

import com.badlogic.gdx.graphics.GL20;
import java.io.IOException;

public class BinTree extends InWindow {
    private static final int[] CrcTable = new int[256];
    static final int kBT2HashSize = 65536;
    static final int kEmptyHashValue = 0;
    static final int kHash2Size = 1024;
    static final int kHash3Offset = 1024;
    static final int kHash3Size = 65536;
    static final int kMaxValForNormalize = 1073741823;
    static final int kStartMaxLen = 1;
    boolean HASH_ARRAY = true;
    int _cutValue = 255;
    int _cyclicBufferPos;
    int _cyclicBufferSize = 0;
    int[] _hash;
    int _hashMask;
    int _hashSizeSum = 0;
    int _matchMaxLen;
    int[] _son;
    int kFixHashSize = 66560;
    int kMinMatchCheck = 4;
    int kNumHashDirectBytes = 0;

    public void SetType(int numHashBytes) {
        this.HASH_ARRAY = numHashBytes > 2;
        if (this.HASH_ARRAY) {
            this.kNumHashDirectBytes = 0;
            this.kMinMatchCheck = 4;
            this.kFixHashSize = 66560;
            return;
        }
        this.kNumHashDirectBytes = 2;
        this.kMinMatchCheck = 3;
        this.kFixHashSize = 0;
    }

    public void Init() throws IOException {
        super.Init();
        for (int i = 0; i < this._hashSizeSum; i++) {
            this._hash[i] = 0;
        }
        this._cyclicBufferPos = 0;
        ReduceOffsets(-1);
    }

    public void MovePos() throws IOException {
        int i = this._cyclicBufferPos + 1;
        this._cyclicBufferPos = i;
        if (i >= this._cyclicBufferSize) {
            this._cyclicBufferPos = 0;
        }
        super.MovePos();
        if (this._pos == kMaxValForNormalize) {
            Normalize();
        }
    }

    public boolean Create(int historySize, int keepAddBufferBefore, int matchMaxLen, int keepAddBufferAfter) {
        if (historySize > 1073741567) {
            return false;
        }
        this._cutValue = (matchMaxLen >> 1) + 16;
        super.Create(historySize + keepAddBufferBefore, matchMaxLen + keepAddBufferAfter, ((((historySize + keepAddBufferBefore) + matchMaxLen) + keepAddBufferAfter) / 2) + 256);
        this._matchMaxLen = matchMaxLen;
        int cyclicBufferSize = historySize + 1;
        if (this._cyclicBufferSize != cyclicBufferSize) {
            this._cyclicBufferSize = cyclicBufferSize;
            this._son = new int[(cyclicBufferSize * 2)];
        }
        int hs = 65536;
        if (this.HASH_ARRAY) {
            int hs2 = historySize - 1;
            int hs3 = hs2 | (hs2 >> 1);
            int hs4 = hs3 | (hs3 >> 2);
            int hs5 = hs4 | (hs4 >> 4);
            int hs6 = ((hs5 | (hs5 >> 8)) >> 1) | 65535;
            if (hs6 > 16777216) {
                hs6 >>= 1;
            }
            this._hashMask = hs6;
            hs = hs6 + 1 + this.kFixHashSize;
        }
        if (hs != this._hashSizeSum) {
            this._hashSizeSum = hs;
            this._hash = new int[hs];
        }
        return true;
    }

    public int GetMatches(int[] distances) throws IOException {
        int lenLimit;
        int temp;
        int maxLen;
        int count;
        int hash2Value;
        int i;
        int count2;
        int len;
        int curMatch;
        if (this._pos + this._matchMaxLen <= this._streamPos) {
            lenLimit = this._matchMaxLen;
        } else {
            lenLimit = this._streamPos - this._pos;
            if (lenLimit < this.kMinMatchCheck) {
                MovePos();
                return 0;
            }
        }
        int offset = 0;
        int matchMinPos = this._pos > this._cyclicBufferSize ? this._pos - this._cyclicBufferSize : 0;
        int cur = this._bufferOffset + this._pos;
        int maxLen2 = 1;
        int hash2Value2 = 0;
        int hash3Value = 0;
        if (this.HASH_ARRAY) {
            int temp2 = CrcTable[this._bufferBase[cur] & 255] ^ (this._bufferBase[cur + 1] & 255);
            hash2Value2 = temp2 & 1023;
            int temp3 = temp2 ^ ((this._bufferBase[cur + 2] & 255) << 8);
            hash3Value = temp3 & 65535;
            temp = ((CrcTable[this._bufferBase[cur + 3] & 255] << 5) ^ temp3) & this._hashMask;
        } else {
            temp = (this._bufferBase[cur] & 255) ^ ((this._bufferBase[cur + 1] & 255) << 8);
        }
        int[] iArr = this._hash;
        int curMatch2 = iArr[this.kFixHashSize + temp];
        if (this.HASH_ARRAY) {
            int curMatch22 = iArr[hash2Value2];
            int curMatch3 = iArr[hash3Value + GL20.GL_STENCIL_BUFFER_BIT];
            iArr[hash2Value2] = this._pos;
            this._hash[hash3Value + GL20.GL_STENCIL_BUFFER_BIT] = this._pos;
            if (curMatch22 > matchMinPos && this._bufferBase[this._bufferOffset + curMatch22] == this._bufferBase[cur]) {
                int offset2 = 0 + 1;
                maxLen2 = 2;
                distances[0] = 2;
                offset = offset2 + 1;
                distances[offset2] = (this._pos - curMatch22) - 1;
            }
            if (curMatch3 > matchMinPos && this._bufferBase[this._bufferOffset + curMatch3] == this._bufferBase[cur]) {
                if (curMatch3 == curMatch22) {
                    offset -= 2;
                }
                int offset3 = offset + 1;
                maxLen2 = 3;
                distances[offset] = 3;
                offset = offset3 + 1;
                distances[offset3] = (this._pos - curMatch3) - 1;
                curMatch22 = curMatch3;
            }
            if (offset != 0 && curMatch22 == curMatch2) {
                offset -= 2;
                maxLen2 = 1;
            }
        }
        this._hash[this.kFixHashSize + temp] = this._pos;
        int i2 = this._cyclicBufferPos;
        int ptr0 = (i2 << 1) + 1;
        int ptr1 = i2 << 1;
        int i3 = this.kNumHashDirectBytes;
        int len1 = i3;
        int len0 = i3;
        if (i3 == 0) {
            maxLen = maxLen2;
        } else if (curMatch2 > matchMinPos) {
            maxLen = maxLen2;
            byte b = this._bufferBase[this._bufferOffset + curMatch2 + this.kNumHashDirectBytes];
            byte[] bArr = this._bufferBase;
            int maxLen3 = this.kNumHashDirectBytes;
            if (b != bArr[cur + maxLen3]) {
                int offset4 = offset + 1;
                distances[offset] = maxLen3;
                offset = offset4 + 1;
                distances[offset4] = (this._pos - curMatch2) - 1;
                maxLen = maxLen3;
            }
        } else {
            maxLen = maxLen2;
        }
        int count3 = this._cutValue;
        int maxLen4 = maxLen;
        while (true) {
            if (curMatch2 <= matchMinPos) {
                int i4 = hash2Value2;
                int i5 = hash3Value;
                int i6 = temp;
                count = count3;
                break;
            }
            count = count3 - 1;
            if (count3 == 0) {
                int i7 = matchMinPos;
                int i8 = hash2Value2;
                int i9 = hash3Value;
                int i10 = temp;
                break;
            }
            int delta = this._pos - curMatch2;
            int matchMinPos2 = matchMinPos;
            int matchMinPos3 = this._cyclicBufferPos;
            if (delta <= matchMinPos3) {
                i = matchMinPos3 - delta;
                hash2Value = hash2Value2;
            } else {
                hash2Value = hash2Value2;
                i = (matchMinPos3 - delta) + this._cyclicBufferSize;
            }
            int cyclicPos = i << 1;
            int pby1 = this._bufferOffset + curMatch2;
            int len2 = Math.min(len0, len1);
            int hash3Value2 = hash3Value;
            int hashValue = temp;
            if (this._bufferBase[pby1 + len2] == this._bufferBase[cur + len2]) {
                while (true) {
                    len = len2 + 1;
                    if (len == lenLimit) {
                        count2 = count;
                        break;
                    }
                    count2 = count;
                    if (this._bufferBase[pby1 + len] != this._bufferBase[cur + len]) {
                        break;
                    }
                    len2 = len;
                    count = count2;
                }
                if (maxLen4 < len) {
                    int offset5 = offset + 1;
                    maxLen4 = len;
                    distances[offset] = len;
                    offset = offset5 + 1;
                    distances[offset5] = delta - 1;
                    if (len == lenLimit) {
                        int[] iArr2 = this._son;
                        iArr2[ptr1] = iArr2[cyclicPos];
                        iArr2[ptr0] = iArr2[cyclicPos + 1];
                        break;
                    }
                }
            } else {
                count2 = count;
                len = len2;
            }
            if ((this._bufferBase[pby1 + len] & 255) < (this._bufferBase[cur + len] & 255)) {
                int[] iArr3 = this._son;
                iArr3[ptr1] = curMatch2;
                ptr1 = cyclicPos + 1;
                curMatch = iArr3[ptr1];
                len1 = len;
            } else {
                int[] iArr4 = this._son;
                iArr4[ptr0] = curMatch2;
                ptr0 = cyclicPos;
                curMatch = iArr4[ptr0];
                len0 = len;
            }
            curMatch2 = curMatch;
            matchMinPos = matchMinPos2;
            hash2Value2 = hash2Value;
            hash3Value = hash3Value2;
            temp = hashValue;
            count3 = count2;
        }
        int[] iArr5 = this._son;
        iArr5[ptr1] = 0;
        iArr5[ptr0] = 0;
        int i11 = count;
        MovePos();
        return offset;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:42:0x013f, code lost:
        r3 = r0._son;
        r3[r7] = 0;
        r3[r8] = 0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void Skip(int r20) throws java.io.IOException {
        /*
            r19 = this;
            r0 = r19
            r1 = r20
        L_0x0004:
            int r2 = r0._pos
            int r3 = r0._matchMaxLen
            int r2 = r2 + r3
            int r3 = r0._streamPos
            if (r2 > r3) goto L_0x0010
            int r2 = r0._matchMaxLen
            goto L_0x001e
        L_0x0010:
            int r2 = r0._streamPos
            int r3 = r0._pos
            int r2 = r2 - r3
            int r3 = r0.kMinMatchCheck
            if (r2 >= r3) goto L_0x001e
            r19.MovePos()
            goto L_0x014a
        L_0x001e:
            int r3 = r0._pos
            int r4 = r0._cyclicBufferSize
            if (r3 <= r4) goto L_0x002a
            int r3 = r0._pos
            int r4 = r0._cyclicBufferSize
            int r3 = r3 - r4
            goto L_0x002b
        L_0x002a:
            r3 = 0
        L_0x002b:
            int r4 = r0._bufferOffset
            int r6 = r0._pos
            int r4 = r4 + r6
            boolean r6 = r0.HASH_ARRAY
            if (r6 == 0) goto L_0x007a
            int[] r6 = CrcTable
            byte[] r7 = r0._bufferBase
            byte r7 = r7[r4]
            r7 = r7 & 255(0xff, float:3.57E-43)
            r6 = r6[r7]
            byte[] r7 = r0._bufferBase
            int r8 = r4 + 1
            byte r7 = r7[r8]
            r7 = r7 & 255(0xff, float:3.57E-43)
            r6 = r6 ^ r7
            r7 = r6 & 1023(0x3ff, float:1.434E-42)
            int[] r8 = r0._hash
            int r9 = r0._pos
            r8[r7] = r9
            byte[] r8 = r0._bufferBase
            int r9 = r4 + 2
            byte r8 = r8[r9]
            r8 = r8 & 255(0xff, float:3.57E-43)
            int r8 = r8 << 8
            r6 = r6 ^ r8
            r8 = 65535(0xffff, float:9.1834E-41)
            r8 = r8 & r6
            int[] r9 = r0._hash
            int r10 = r8 + 1024
            int r11 = r0._pos
            r9[r10] = r11
            int[] r9 = CrcTable
            byte[] r10 = r0._bufferBase
            int r11 = r4 + 3
            byte r10 = r10[r11]
            r10 = r10 & 255(0xff, float:3.57E-43)
            r9 = r9[r10]
            int r9 = r9 << 5
            r9 = r9 ^ r6
            int r10 = r0._hashMask
            r6 = r9 & r10
            goto L_0x008b
        L_0x007a:
            byte[] r6 = r0._bufferBase
            byte r6 = r6[r4]
            r6 = r6 & 255(0xff, float:3.57E-43)
            byte[] r7 = r0._bufferBase
            int r8 = r4 + 1
            byte r7 = r7[r8]
            r7 = r7 & 255(0xff, float:3.57E-43)
            int r7 = r7 << 8
            r6 = r6 ^ r7
        L_0x008b:
            int[] r7 = r0._hash
            int r8 = r0.kFixHashSize
            int r9 = r8 + r6
            r9 = r7[r9]
            int r8 = r8 + r6
            int r10 = r0._pos
            r7[r8] = r10
            int r7 = r0._cyclicBufferPos
            int r8 = r7 << 1
            int r8 = r8 + 1
            int r7 = r7 << 1
            int r10 = r0.kNumHashDirectBytes
            r11 = r10
            int r12 = r0._cutValue
        L_0x00a5:
            if (r9 <= r3) goto L_0x013a
            int r13 = r12 + -1
            if (r12 != 0) goto L_0x00b1
            r17 = r3
            r18 = r6
            goto L_0x013f
        L_0x00b1:
            int r12 = r0._pos
            int r12 = r12 - r9
            int r14 = r0._cyclicBufferPos
            if (r12 > r14) goto L_0x00ba
            int r14 = r14 - r12
            goto L_0x00be
        L_0x00ba:
            int r14 = r14 - r12
            int r15 = r0._cyclicBufferSize
            int r14 = r14 + r15
        L_0x00be:
            int r14 = r14 << 1
            int r15 = r0._bufferOffset
            int r15 = r15 + r9
            int r16 = java.lang.Math.min(r10, r11)
            byte[] r5 = r0._bufferBase
            int r17 = r15 + r16
            byte r5 = r5[r17]
            r17 = r3
            byte[] r3 = r0._bufferBase
            int r18 = r4 + r16
            byte r3 = r3[r18]
            if (r5 != r3) goto L_0x0105
        L_0x00d7:
            int r3 = r16 + 1
            if (r3 == r2) goto L_0x00f1
            byte[] r5 = r0._bufferBase
            int r16 = r15 + r3
            byte r5 = r5[r16]
            r18 = r6
            byte[] r6 = r0._bufferBase
            int r16 = r4 + r3
            byte r6 = r6[r16]
            if (r5 == r6) goto L_0x00ec
            goto L_0x00f3
        L_0x00ec:
            r16 = r3
            r6 = r18
            goto L_0x00d7
        L_0x00f1:
            r18 = r6
        L_0x00f3:
            if (r3 != r2) goto L_0x0102
            int[] r5 = r0._son
            r6 = r5[r14]
            r5[r7] = r6
            int r6 = r14 + 1
            r6 = r5[r6]
            r5[r8] = r6
            goto L_0x0147
        L_0x0102:
            r16 = r3
            goto L_0x0107
        L_0x0105:
            r18 = r6
        L_0x0107:
            byte[] r3 = r0._bufferBase
            int r5 = r15 + r16
            byte r3 = r3[r5]
            r3 = r3 & 255(0xff, float:3.57E-43)
            byte[] r5 = r0._bufferBase
            int r6 = r4 + r16
            byte r5 = r5[r6]
            r5 = r5 & 255(0xff, float:3.57E-43)
            if (r3 >= r5) goto L_0x0127
            int[] r3 = r0._son
            r3[r7] = r9
            int r5 = r14 + 1
            r3 = r3[r5]
            r6 = r16
            r9 = r3
            r7 = r5
            r11 = r6
            goto L_0x0133
        L_0x0127:
            int[] r3 = r0._son
            r3[r8] = r9
            r5 = r14
            r3 = r3[r5]
            r6 = r16
            r9 = r3
            r8 = r5
            r10 = r6
        L_0x0133:
            r12 = r13
            r3 = r17
            r6 = r18
            goto L_0x00a5
        L_0x013a:
            r17 = r3
            r18 = r6
            r13 = r12
        L_0x013f:
            int[] r3 = r0._son
            r5 = 0
            r3[r7] = r5
            r3[r8] = r5
        L_0x0147:
            r19.MovePos()
        L_0x014a:
            int r1 = r1 + -1
            if (r1 != 0) goto L_0x014f
            return
        L_0x014f:
            goto L_0x0004
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.utils.compression.lz.BinTree.Skip(int):void");
    }

    /* access modifiers changed from: package-private */
    public void NormalizeLinks(int[] items, int numItems, int subValue) {
        int value;
        for (int i = 0; i < numItems; i++) {
            int value2 = items[i];
            if (value2 <= subValue) {
                value = 0;
            } else {
                value = value2 - subValue;
            }
            items[i] = value;
        }
    }

    /* access modifiers changed from: package-private */
    public void Normalize() {
        int i = this._pos;
        int i2 = this._cyclicBufferSize;
        int subValue = i - i2;
        NormalizeLinks(this._son, i2 * 2, subValue);
        NormalizeLinks(this._hash, this._hashSizeSum, subValue);
        ReduceOffsets(subValue);
    }

    public void SetCutValue(int cutValue) {
        this._cutValue = cutValue;
    }

    static {
        for (int i = 0; i < 256; i++) {
            int r = i;
            for (int j = 0; j < 8; j++) {
                if ((r & 1) != 0) {
                    r = (r >>> 1) ^ -306674912;
                } else {
                    r >>>= 1;
                }
            }
            CrcTable[i] = r;
        }
    }
}
