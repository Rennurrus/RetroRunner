package com.badlogic.gdx.utils.compression.lzma;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.compression.ICodeProgress;
import com.badlogic.gdx.utils.compression.lz.BinTree;
import com.badlogic.gdx.utils.compression.rangecoder.BitTreeEncoder;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Encoder {
    public static final int EMatchFinderTypeBT2 = 0;
    public static final int EMatchFinderTypeBT4 = 1;
    static byte[] g_FastPos = new byte[2048];
    static final int kDefaultDictionaryLogSize = 22;
    static final int kIfinityPrice = 268435455;
    static final int kNumFastBytesDefault = 32;
    public static final int kNumLenSpecSymbols = 16;
    static final int kNumOpts = 4096;
    public static final int kPropSize = 5;
    int _additionalOffset;
    int _alignPriceCount;
    int[] _alignPrices = new int[16];
    int _dictionarySize = 4194304;
    int _dictionarySizePrev = -1;
    int _distTableSize = 44;
    int[] _distancesPrices = new int[GL20.GL_NEVER];
    boolean _finished;
    InputStream _inStream;
    short[] _isMatch = new short[192];
    short[] _isRep = new short[12];
    short[] _isRep0Long = new short[192];
    short[] _isRepG0 = new short[12];
    short[] _isRepG1 = new short[12];
    short[] _isRepG2 = new short[12];
    LenPriceTableEncoder _lenEncoder = new LenPriceTableEncoder();
    LiteralEncoder _literalEncoder = new LiteralEncoder();
    int _longestMatchLength;
    boolean _longestMatchWasFound;
    int[] _matchDistances = new int[548];
    BinTree _matchFinder = null;
    int _matchFinderType = 1;
    int _matchPriceCount;
    boolean _needReleaseMFStream = false;
    int _numDistancePairs;
    int _numFastBytes = 32;
    int _numFastBytesPrev = -1;
    int _numLiteralContextBits = 3;
    int _numLiteralPosStateBits = 0;
    Optimal[] _optimum = new Optimal[4096];
    int _optimumCurrentIndex;
    int _optimumEndIndex;
    BitTreeEncoder _posAlignEncoder = new BitTreeEncoder(4);
    short[] _posEncoders = new short[114];
    BitTreeEncoder[] _posSlotEncoder = new BitTreeEncoder[4];
    int[] _posSlotPrices = new int[256];
    int _posStateBits = 2;
    int _posStateMask = 3;
    byte _previousByte;
    com.badlogic.gdx.utils.compression.rangecoder.Encoder _rangeEncoder = new com.badlogic.gdx.utils.compression.rangecoder.Encoder();
    int[] _repDistances = new int[4];
    LenPriceTableEncoder _repMatchLenEncoder = new LenPriceTableEncoder();
    int _state = Base.StateInit();
    boolean _writeEndMark = false;
    int backRes;
    boolean[] finished = new boolean[1];
    long nowPos64;
    long[] processedInSize = new long[1];
    long[] processedOutSize = new long[1];
    byte[] properties = new byte[5];
    int[] repLens = new int[4];
    int[] reps = new int[4];
    int[] tempPrices = new int[128];

    static {
        int c = 2;
        byte[] bArr = g_FastPos;
        bArr[0] = 0;
        bArr[1] = 1;
        for (int slotFast = 2; slotFast < 22; slotFast++) {
            int k = 1 << ((slotFast >> 1) - 1);
            int j = 0;
            while (j < k) {
                g_FastPos[c] = (byte) slotFast;
                j++;
                c++;
            }
        }
    }

    static int GetPosSlot(int pos) {
        if (pos < 2048) {
            return g_FastPos[pos];
        }
        if (pos < 2097152) {
            return g_FastPos[pos >> 10] + 20;
        }
        return g_FastPos[pos >> 20] + 40;
    }

    static int GetPosSlot2(int pos) {
        if (pos < 131072) {
            return g_FastPos[pos >> 6] + 12;
        }
        if (pos < 134217728) {
            return g_FastPos[pos >> 16] + 32;
        }
        return g_FastPos[pos >> 26] + 52;
    }

    /* access modifiers changed from: package-private */
    public void BaseInit() {
        this._state = Base.StateInit();
        this._previousByte = 0;
        for (int i = 0; i < 4; i++) {
            this._repDistances[i] = 0;
        }
    }

    class LiteralEncoder {
        Encoder2[] m_Coders;
        int m_NumPosBits;
        int m_NumPrevBits;
        int m_PosMask;

        class Encoder2 {
            short[] m_Encoders = new short[GL20.GL_SRC_COLOR];

            Encoder2() {
            }

            public void Init() {
                com.badlogic.gdx.utils.compression.rangecoder.Encoder.InitBitModels(this.m_Encoders);
            }

            public void Encode(com.badlogic.gdx.utils.compression.rangecoder.Encoder rangeEncoder, byte symbol) throws IOException {
                int context = 1;
                for (int i = 7; i >= 0; i--) {
                    int bit = (symbol >> i) & 1;
                    rangeEncoder.Encode(this.m_Encoders, context, bit);
                    context = (context << 1) | bit;
                }
            }

            public void EncodeMatched(com.badlogic.gdx.utils.compression.rangecoder.Encoder rangeEncoder, byte matchByte, byte symbol) throws IOException {
                int context = 1;
                boolean same = true;
                for (int i = 7; i >= 0; i--) {
                    boolean z = true;
                    int bit = (symbol >> i) & 1;
                    int state = context;
                    if (same) {
                        int matchBit = (matchByte >> i) & 1;
                        state += (matchBit + 1) << 8;
                        if (matchBit != bit) {
                            z = false;
                        }
                        same = z;
                    }
                    rangeEncoder.Encode(this.m_Encoders, state, bit);
                    context = (context << 1) | bit;
                }
            }

            public int GetPrice(boolean matchMode, byte matchByte, byte symbol) {
                int price = 0;
                int context = 1;
                int i = 7;
                if (matchMode) {
                    while (true) {
                        if (i < 0) {
                            break;
                        }
                        int matchBit = (matchByte >> i) & 1;
                        int bit = (symbol >> i) & 1;
                        price += com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice(this.m_Encoders[((matchBit + 1) << 8) + context], bit);
                        context = (context << 1) | bit;
                        if (matchBit != bit) {
                            i--;
                            break;
                        }
                        i--;
                    }
                }
                while (i >= 0) {
                    int bit2 = (symbol >> i) & 1;
                    price += com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice(this.m_Encoders[context], bit2);
                    context = (context << 1) | bit2;
                    i--;
                }
                return price;
            }
        }

        LiteralEncoder() {
        }

        public void Create(int numPosBits, int numPrevBits) {
            if (this.m_Coders == null || this.m_NumPrevBits != numPrevBits || this.m_NumPosBits != numPosBits) {
                this.m_NumPosBits = numPosBits;
                this.m_PosMask = (1 << numPosBits) - 1;
                this.m_NumPrevBits = numPrevBits;
                int numStates = 1 << (this.m_NumPrevBits + this.m_NumPosBits);
                this.m_Coders = new Encoder2[numStates];
                for (int i = 0; i < numStates; i++) {
                    this.m_Coders[i] = new Encoder2();
                }
            }
        }

        public void Init() {
            int numStates = 1 << (this.m_NumPrevBits + this.m_NumPosBits);
            for (int i = 0; i < numStates; i++) {
                this.m_Coders[i].Init();
            }
        }

        public Encoder2 GetSubCoder(int pos, byte prevByte) {
            Encoder2[] encoder2Arr = this.m_Coders;
            int i = this.m_NumPrevBits;
            return encoder2Arr[((this.m_PosMask & pos) << i) + ((prevByte & 255) >>> (8 - i))];
        }
    }

    class LenEncoder {
        short[] _choice = new short[2];
        BitTreeEncoder _highCoder = new BitTreeEncoder(8);
        BitTreeEncoder[] _lowCoder = new BitTreeEncoder[16];
        BitTreeEncoder[] _midCoder = new BitTreeEncoder[16];

        public LenEncoder() {
            for (int posState = 0; posState < 16; posState++) {
                this._lowCoder[posState] = new BitTreeEncoder(3);
                this._midCoder[posState] = new BitTreeEncoder(3);
            }
        }

        public void Init(int numPosStates) {
            com.badlogic.gdx.utils.compression.rangecoder.Encoder.InitBitModels(this._choice);
            for (int posState = 0; posState < numPosStates; posState++) {
                this._lowCoder[posState].Init();
                this._midCoder[posState].Init();
            }
            this._highCoder.Init();
        }

        public void Encode(com.badlogic.gdx.utils.compression.rangecoder.Encoder rangeEncoder, int symbol, int posState) throws IOException {
            if (symbol < 8) {
                rangeEncoder.Encode(this._choice, 0, 0);
                this._lowCoder[posState].Encode(rangeEncoder, symbol);
                return;
            }
            int symbol2 = symbol - 8;
            rangeEncoder.Encode(this._choice, 0, 1);
            if (symbol2 < 8) {
                rangeEncoder.Encode(this._choice, 1, 0);
                this._midCoder[posState].Encode(rangeEncoder, symbol2);
                return;
            }
            rangeEncoder.Encode(this._choice, 1, 1);
            this._highCoder.Encode(rangeEncoder, symbol2 - 8);
        }

        public void SetPrices(int posState, int numSymbols, int[] prices, int st) {
            int a0 = com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice0(this._choice[0]);
            int a1 = com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._choice[0]);
            int b0 = com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice0(this._choice[1]) + a1;
            int b1 = com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._choice[1]) + a1;
            int i = 0;
            while (i < 8) {
                if (i < numSymbols) {
                    prices[st + i] = this._lowCoder[posState].GetPrice(i) + a0;
                    i++;
                } else {
                    return;
                }
            }
            while (i < 16) {
                if (i < numSymbols) {
                    prices[st + i] = this._midCoder[posState].GetPrice(i - 8) + b0;
                    i++;
                } else {
                    return;
                }
            }
            while (i < numSymbols) {
                prices[st + i] = this._highCoder.GetPrice((i - 8) - 8) + b1;
                i++;
            }
        }
    }

    class LenPriceTableEncoder extends LenEncoder {
        int[] _counters = new int[16];
        int[] _prices = new int[GL20.GL_DONT_CARE];
        int _tableSize;

        LenPriceTableEncoder() {
            super();
        }

        public void SetTableSize(int tableSize) {
            this._tableSize = tableSize;
        }

        public int GetPrice(int symbol, int posState) {
            return this._prices[(posState * Base.kNumLenSymbols) + symbol];
        }

        /* access modifiers changed from: package-private */
        public void UpdateTable(int posState) {
            SetPrices(posState, this._tableSize, this._prices, posState * Base.kNumLenSymbols);
            this._counters[posState] = this._tableSize;
        }

        public void UpdateTables(int numPosStates) {
            for (int posState = 0; posState < numPosStates; posState++) {
                UpdateTable(posState);
            }
        }

        public void Encode(com.badlogic.gdx.utils.compression.rangecoder.Encoder rangeEncoder, int symbol, int posState) throws IOException {
            super.Encode(rangeEncoder, symbol, posState);
            int[] iArr = this._counters;
            int i = iArr[posState] - 1;
            iArr[posState] = i;
            if (i == 0) {
                UpdateTable(posState);
            }
        }
    }

    class Optimal {
        public int BackPrev;
        public int BackPrev2;
        public int Backs0;
        public int Backs1;
        public int Backs2;
        public int Backs3;
        public int PosPrev;
        public int PosPrev2;
        public boolean Prev1IsChar;
        public boolean Prev2;
        public int Price;
        public int State;

        Optimal() {
        }

        public void MakeAsChar() {
            this.BackPrev = -1;
            this.Prev1IsChar = false;
        }

        public void MakeAsShortRep() {
            this.BackPrev = 0;
            this.Prev1IsChar = false;
        }

        public boolean IsShortRep() {
            return this.BackPrev == 0;
        }
    }

    /* access modifiers changed from: package-private */
    public void Create() {
        if (this._matchFinder == null) {
            BinTree bt = new BinTree();
            int numHashBytes = 4;
            if (this._matchFinderType == 0) {
                numHashBytes = 2;
            }
            bt.SetType(numHashBytes);
            this._matchFinder = bt;
        }
        this._literalEncoder.Create(this._numLiteralPosStateBits, this._numLiteralContextBits);
        if (this._dictionarySize != this._dictionarySizePrev || this._numFastBytesPrev != this._numFastBytes) {
            this._matchFinder.Create(this._dictionarySize, 4096, this._numFastBytes, 274);
            this._dictionarySizePrev = this._dictionarySize;
            this._numFastBytesPrev = this._numFastBytes;
        }
    }

    public Encoder() {
        for (int i = 0; i < 4096; i++) {
            this._optimum[i] = new Optimal();
        }
        for (int i2 = 0; i2 < 4; i2++) {
            this._posSlotEncoder[i2] = new BitTreeEncoder(6);
        }
    }

    /* access modifiers changed from: package-private */
    public void SetWriteEndMarkerMode(boolean writeEndMarker) {
        this._writeEndMark = writeEndMarker;
    }

    /* access modifiers changed from: package-private */
    public void Init() {
        BaseInit();
        this._rangeEncoder.Init();
        com.badlogic.gdx.utils.compression.rangecoder.Encoder.InitBitModels(this._isMatch);
        com.badlogic.gdx.utils.compression.rangecoder.Encoder.InitBitModels(this._isRep0Long);
        com.badlogic.gdx.utils.compression.rangecoder.Encoder.InitBitModels(this._isRep);
        com.badlogic.gdx.utils.compression.rangecoder.Encoder.InitBitModels(this._isRepG0);
        com.badlogic.gdx.utils.compression.rangecoder.Encoder.InitBitModels(this._isRepG1);
        com.badlogic.gdx.utils.compression.rangecoder.Encoder.InitBitModels(this._isRepG2);
        com.badlogic.gdx.utils.compression.rangecoder.Encoder.InitBitModels(this._posEncoders);
        this._literalEncoder.Init();
        for (int i = 0; i < 4; i++) {
            this._posSlotEncoder[i].Init();
        }
        this._lenEncoder.Init(1 << this._posStateBits);
        this._repMatchLenEncoder.Init(1 << this._posStateBits);
        this._posAlignEncoder.Init();
        this._longestMatchWasFound = false;
        this._optimumEndIndex = 0;
        this._optimumCurrentIndex = 0;
        this._additionalOffset = 0;
    }

    /* access modifiers changed from: package-private */
    public int ReadMatchDistances() throws IOException {
        int[] iArr;
        int lenRes = 0;
        this._numDistancePairs = this._matchFinder.GetMatches(this._matchDistances);
        int i = this._numDistancePairs;
        if (i > 0 && (lenRes = (iArr = this._matchDistances)[i - 2]) == this._numFastBytes) {
            lenRes += this._matchFinder.GetMatchLen(lenRes - 1, iArr[i - 1], 273 - lenRes);
        }
        this._additionalOffset++;
        return lenRes;
    }

    /* access modifiers changed from: package-private */
    public void MovePos(int num) throws IOException {
        if (num > 0) {
            this._matchFinder.Skip(num);
            this._additionalOffset += num;
        }
    }

    /* access modifiers changed from: package-private */
    public int GetRepLen1Price(int state, int posState) {
        return com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice0(this._isRepG0[state]) + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice0(this._isRep0Long[(state << 4) + posState]);
    }

    /* access modifiers changed from: package-private */
    public int GetPureRepPrice(int repIndex, int state, int posState) {
        if (repIndex == 0) {
            return com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice0(this._isRepG0[state]) + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._isRep0Long[(state << 4) + posState]);
        }
        int price = com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._isRepG0[state]);
        if (repIndex == 1) {
            return price + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice0(this._isRepG1[state]);
        }
        return price + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._isRepG1[state]) + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice(this._isRepG2[state], repIndex - 2);
    }

    /* access modifiers changed from: package-private */
    public int GetRepPrice(int repIndex, int len, int state, int posState) {
        return GetPureRepPrice(repIndex, state, posState) + this._repMatchLenEncoder.GetPrice(len - 2, posState);
    }

    /* access modifiers changed from: package-private */
    public int GetPosLenPrice(int pos, int len, int posState) {
        int price;
        int lenToPosState = Base.GetLenToPosState(len);
        if (pos < 128) {
            price = this._distancesPrices[(lenToPosState * 128) + pos];
        } else {
            price = this._posSlotPrices[(lenToPosState << 6) + GetPosSlot2(pos)] + this._alignPrices[pos & 15];
        }
        return this._lenEncoder.GetPrice(len - 2, posState) + price;
    }

    /* access modifiers changed from: package-private */
    public int Backward(int cur) {
        Optimal[] optimalArr;
        this._optimumEndIndex = cur;
        int posMem = this._optimum[cur].PosPrev;
        int backMem = this._optimum[cur].BackPrev;
        do {
            if (this._optimum[cur].Prev1IsChar) {
                this._optimum[posMem].MakeAsChar();
                Optimal[] optimalArr2 = this._optimum;
                optimalArr2[posMem].PosPrev = posMem - 1;
                if (optimalArr2[cur].Prev2) {
                    Optimal[] optimalArr3 = this._optimum;
                    optimalArr3[posMem - 1].Prev1IsChar = false;
                    optimalArr3[posMem - 1].PosPrev = optimalArr3[cur].PosPrev2;
                    Optimal[] optimalArr4 = this._optimum;
                    optimalArr4[posMem - 1].BackPrev = optimalArr4[cur].BackPrev2;
                }
            }
            int posPrev = posMem;
            int backCur = backMem;
            backMem = this._optimum[posPrev].BackPrev;
            posMem = this._optimum[posPrev].PosPrev;
            optimalArr = this._optimum;
            optimalArr[posPrev].BackPrev = backCur;
            optimalArr[posPrev].PosPrev = cur;
            cur = posPrev;
        } while (cur > 0);
        this.backRes = optimalArr[0].BackPrev;
        this._optimumCurrentIndex = this._optimum[0].PosPrev;
        return this._optimumCurrentIndex;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:242:0x07db  */
    /* JADX WARNING: Removed duplicated region for block: B:277:0x07d6 A[EDGE_INSN: B:277:0x07d6->B:241:0x07d6 ?: BREAK  , SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int GetOptimum(int r46) throws java.io.IOException {
        /*
            r45 = this;
            r0 = r45
            r1 = r46
            int r2 = r0._optimumEndIndex
            int r3 = r0._optimumCurrentIndex
            if (r2 == r3) goto L_0x0026
            com.badlogic.gdx.utils.compression.lzma.Encoder$Optimal[] r2 = r0._optimum
            r2 = r2[r3]
            int r2 = r2.PosPrev
            int r3 = r0._optimumCurrentIndex
            int r2 = r2 - r3
            com.badlogic.gdx.utils.compression.lzma.Encoder$Optimal[] r4 = r0._optimum
            r3 = r4[r3]
            int r3 = r3.BackPrev
            r0.backRes = r3
            com.badlogic.gdx.utils.compression.lzma.Encoder$Optimal[] r3 = r0._optimum
            int r4 = r0._optimumCurrentIndex
            r3 = r3[r4]
            int r3 = r3.PosPrev
            r0._optimumCurrentIndex = r3
            return r2
        L_0x0026:
            r2 = 0
            r0._optimumEndIndex = r2
            r0._optimumCurrentIndex = r2
            boolean r3 = r0._longestMatchWasFound
            if (r3 != 0) goto L_0x0034
            int r3 = r45.ReadMatchDistances()
            goto L_0x0038
        L_0x0034:
            int r3 = r0._longestMatchLength
            r0._longestMatchWasFound = r2
        L_0x0038:
            int r4 = r0._numDistancePairs
            com.badlogic.gdx.utils.compression.lz.BinTree r5 = r0._matchFinder
            int r5 = r5.GetNumAvailableBytes()
            r6 = 1
            int r5 = r5 + r6
            r7 = -1
            r8 = 2
            if (r5 >= r8) goto L_0x0049
            r0.backRes = r7
            return r6
        L_0x0049:
            r9 = 273(0x111, float:3.83E-43)
            if (r5 <= r9) goto L_0x004f
            r5 = 273(0x111, float:3.83E-43)
        L_0x004f:
            r10 = 0
            r11 = 0
        L_0x0051:
            r12 = 4
            if (r11 >= r12) goto L_0x0074
            int[] r12 = r0.reps
            int[] r13 = r0._repDistances
            r13 = r13[r11]
            r12[r11] = r13
            int[] r13 = r0.repLens
            com.badlogic.gdx.utils.compression.lz.BinTree r14 = r0._matchFinder
            r12 = r12[r11]
            int r12 = r14.GetMatchLen(r7, r12, r9)
            r13[r11] = r12
            int[] r12 = r0.repLens
            r13 = r12[r11]
            r12 = r12[r10]
            if (r13 <= r12) goto L_0x0071
            r10 = r11
        L_0x0071:
            int r11 = r11 + 1
            goto L_0x0051
        L_0x0074:
            int[] r9 = r0.repLens
            r13 = r9[r10]
            int r14 = r0._numFastBytes
            if (r13 < r14) goto L_0x0086
            r0.backRes = r10
            r2 = r9[r10]
            int r6 = r2 + -1
            r0.MovePos(r6)
            return r2
        L_0x0086:
            if (r3 < r14) goto L_0x0097
            int[] r2 = r0._matchDistances
            int r6 = r4 + -1
            r2 = r2[r6]
            int r2 = r2 + r12
            r0.backRes = r2
            int r2 = r3 + -1
            r0.MovePos(r2)
            return r3
        L_0x0097:
            com.badlogic.gdx.utils.compression.lz.BinTree r9 = r0._matchFinder
            byte r9 = r9.GetIndexByte(r7)
            com.badlogic.gdx.utils.compression.lz.BinTree r13 = r0._matchFinder
            int[] r14 = r0._repDistances
            r14 = r14[r2]
            int r14 = 0 - r14
            int r14 = r14 - r6
            int r14 = r14 - r6
            byte r13 = r13.GetIndexByte(r14)
            if (r3 >= r8) goto L_0x00b8
            if (r9 == r13) goto L_0x00b8
            int[] r14 = r0.repLens
            r14 = r14[r10]
            if (r14 >= r8) goto L_0x00b8
            r0.backRes = r7
            return r6
        L_0x00b8:
            com.badlogic.gdx.utils.compression.lzma.Encoder$Optimal[] r14 = r0._optimum
            r15 = r14[r2]
            int r7 = r0._state
            r15.State = r7
            int r15 = r0._posStateMask
            r15 = r15 & r1
            r14 = r14[r6]
            short[] r2 = r0._isMatch
            int r7 = r7 << r12
            int r7 = r7 + r15
            short r2 = r2[r7]
            int r2 = com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice0(r2)
            com.badlogic.gdx.utils.compression.lzma.Encoder$LiteralEncoder r7 = r0._literalEncoder
            byte r8 = r0._previousByte
            com.badlogic.gdx.utils.compression.lzma.Encoder$LiteralEncoder$Encoder2 r7 = r7.GetSubCoder(r1, r8)
            int r8 = r0._state
            boolean r8 = com.badlogic.gdx.utils.compression.lzma.Base.StateIsCharState(r8)
            r8 = r8 ^ r6
            int r7 = r7.GetPrice(r8, r13, r9)
            int r2 = r2 + r7
            r14.Price = r2
            com.badlogic.gdx.utils.compression.lzma.Encoder$Optimal[] r2 = r0._optimum
            r2 = r2[r6]
            r2.MakeAsChar()
            short[] r2 = r0._isMatch
            int r7 = r0._state
            int r7 = r7 << r12
            int r7 = r7 + r15
            short r2 = r2[r7]
            int r2 = com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(r2)
            short[] r7 = r0._isRep
            int r8 = r0._state
            short r7 = r7[r8]
            int r7 = com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(r7)
            int r7 = r7 + r2
            if (r13 != r9) goto L_0x011f
            int r8 = r0._state
            int r8 = r0.GetRepLen1Price(r8, r15)
            int r8 = r8 + r7
            com.badlogic.gdx.utils.compression.lzma.Encoder$Optimal[] r14 = r0._optimum
            r14 = r14[r6]
            int r14 = r14.Price
            if (r8 >= r14) goto L_0x011f
            com.badlogic.gdx.utils.compression.lzma.Encoder$Optimal[] r14 = r0._optimum
            r12 = r14[r6]
            r12.Price = r8
            r12 = r14[r6]
            r12.MakeAsShortRep()
        L_0x011f:
            int[] r8 = r0.repLens
            r12 = r8[r10]
            if (r3 < r12) goto L_0x0127
            r8 = r3
            goto L_0x0129
        L_0x0127:
            r8 = r8[r10]
        L_0x0129:
            r12 = 2
            if (r8 >= r12) goto L_0x0135
            com.badlogic.gdx.utils.compression.lzma.Encoder$Optimal[] r12 = r0._optimum
            r12 = r12[r6]
            int r12 = r12.BackPrev
            r0.backRes = r12
            return r6
        L_0x0135:
            com.badlogic.gdx.utils.compression.lzma.Encoder$Optimal[] r12 = r0._optimum
            r14 = r12[r6]
            r6 = 0
            r14.PosPrev = r6
            r14 = r12[r6]
            int[] r1 = r0.reps
            r19 = r5
            r5 = r1[r6]
            r14.Backs0 = r5
            r5 = r12[r6]
            r14 = 1
            r6 = r1[r14]
            r5.Backs1 = r6
            r5 = 0
            r6 = r12[r5]
            r14 = 2
            r5 = r1[r14]
            r6.Backs2 = r5
            r5 = 0
            r6 = r12[r5]
            r5 = 3
            r1 = r1[r5]
            r6.Backs3 = r1
            r1 = r8
        L_0x015e:
            com.badlogic.gdx.utils.compression.lzma.Encoder$Optimal[] r6 = r0._optimum
            int r12 = r1 + -1
            r1 = r6[r1]
            r6 = 268435455(0xfffffff, float:2.5243547E-29)
            r1.Price = r6
            r1 = 2
            if (r12 >= r1) goto L_0x0822
            r11 = 0
            r14 = r11
        L_0x016e:
            r11 = 4
            if (r14 >= r11) goto L_0x01af
            int[] r11 = r0.repLens
            r11 = r11[r14]
            if (r11 >= r1) goto L_0x0178
            goto L_0x01a0
        L_0x0178:
            int r1 = r0._state
            int r1 = r0.GetPureRepPrice(r14, r1, r15)
            int r1 = r1 + r7
        L_0x017f:
            com.badlogic.gdx.utils.compression.lzma.Encoder$LenPriceTableEncoder r6 = r0._repMatchLenEncoder
            int r5 = r11 + -2
            int r5 = r6.GetPrice(r5, r15)
            int r5 = r5 + r1
            com.badlogic.gdx.utils.compression.lzma.Encoder$Optimal[] r6 = r0._optimum
            r6 = r6[r11]
            r22 = r1
            int r1 = r6.Price
            if (r5 >= r1) goto L_0x019b
            r6.Price = r5
            r1 = 0
            r6.PosPrev = r1
            r6.BackPrev = r14
            r6.Prev1IsChar = r1
        L_0x019b:
            int r11 = r11 + -1
            r1 = 2
            if (r11 >= r1) goto L_0x01a8
        L_0x01a0:
            int r14 = r14 + 1
            r1 = 2
            r5 = 3
            r6 = 268435455(0xfffffff, float:2.5243547E-29)
            goto L_0x016e
        L_0x01a8:
            r1 = r22
            r5 = 3
            r6 = 268435455(0xfffffff, float:2.5243547E-29)
            goto L_0x017f
        L_0x01af:
            short[] r1 = r0._isRep
            int r5 = r0._state
            short r1 = r1[r5]
            int r1 = com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice0(r1)
            int r1 = r1 + r2
            int[] r5 = r0.repLens
            r6 = 0
            r11 = r5[r6]
            r6 = 2
            if (r11 < r6) goto L_0x01c8
            r6 = 0
            r5 = r5[r6]
            r6 = 1
            int r5 = r5 + r6
            goto L_0x01c9
        L_0x01c8:
            r5 = 2
        L_0x01c9:
            if (r5 > r3) goto L_0x020a
            r6 = 0
        L_0x01cc:
            int[] r11 = r0._matchDistances
            r11 = r11[r6]
            if (r5 <= r11) goto L_0x01d5
            int r6 = r6 + 2
            goto L_0x01cc
        L_0x01d5:
            int[] r11 = r0._matchDistances
            int r12 = r6 + 1
            r11 = r11[r12]
            int r12 = r0.GetPosLenPrice(r11, r5, r15)
            int r12 = r12 + r1
            r22 = r1
            com.badlogic.gdx.utils.compression.lzma.Encoder$Optimal[] r1 = r0._optimum
            r1 = r1[r5]
            r23 = r2
            int r2 = r1.Price
            if (r12 >= r2) goto L_0x01f8
            r1.Price = r12
            r2 = 0
            r1.PosPrev = r2
            int r2 = r11 + 4
            r1.BackPrev = r2
            r2 = 0
            r1.Prev1IsChar = r2
        L_0x01f8:
            int[] r2 = r0._matchDistances
            r2 = r2[r6]
            if (r5 != r2) goto L_0x0203
            int r6 = r6 + 2
            if (r6 != r4) goto L_0x0203
            goto L_0x020e
        L_0x0203:
            int r5 = r5 + 1
            r1 = r22
            r2 = r23
            goto L_0x01d5
        L_0x020a:
            r22 = r1
            r23 = r2
        L_0x020e:
            r1 = 0
            r2 = r46
        L_0x0211:
            r6 = 1
            int r1 = r1 + r6
            if (r1 != r8) goto L_0x021a
            int r6 = r0.Backward(r1)
            return r6
        L_0x021a:
            int r6 = r45.ReadMatchDistances()
            int r4 = r0._numDistancePairs
            int r11 = r0._numFastBytes
            if (r6 < r11) goto L_0x022e
            r0._longestMatchLength = r6
            r11 = 1
            r0._longestMatchWasFound = r11
            int r11 = r0.Backward(r1)
            return r11
        L_0x022e:
            int r2 = r2 + 1
            com.badlogic.gdx.utils.compression.lzma.Encoder$Optimal[] r11 = r0._optimum
            r11 = r11[r1]
            int r11 = r11.PosPrev
            com.badlogic.gdx.utils.compression.lzma.Encoder$Optimal[] r12 = r0._optimum
            r12 = r12[r1]
            boolean r12 = r12.Prev1IsChar
            if (r12 == 0) goto L_0x0278
            int r11 = r11 + -1
            com.badlogic.gdx.utils.compression.lzma.Encoder$Optimal[] r12 = r0._optimum
            r12 = r12[r1]
            boolean r12 = r12.Prev2
            if (r12 == 0) goto L_0x0269
            com.badlogic.gdx.utils.compression.lzma.Encoder$Optimal[] r12 = r0._optimum
            r24 = r3
            r3 = r12[r1]
            int r3 = r3.PosPrev2
            r3 = r12[r3]
            int r3 = r3.State
            com.badlogic.gdx.utils.compression.lzma.Encoder$Optimal[] r12 = r0._optimum
            r12 = r12[r1]
            int r12 = r12.BackPrev2
            r46 = r4
            r4 = 4
            if (r12 >= r4) goto L_0x0264
            int r3 = com.badlogic.gdx.utils.compression.lzma.Base.StateUpdateRep(r3)
            goto L_0x0273
        L_0x0264:
            int r3 = com.badlogic.gdx.utils.compression.lzma.Base.StateUpdateMatch(r3)
            goto L_0x0273
        L_0x0269:
            r24 = r3
            r46 = r4
            com.badlogic.gdx.utils.compression.lzma.Encoder$Optimal[] r3 = r0._optimum
            r3 = r3[r11]
            int r3 = r3.State
        L_0x0273:
            int r3 = com.badlogic.gdx.utils.compression.lzma.Base.StateUpdateChar(r3)
            goto L_0x0282
        L_0x0278:
            r24 = r3
            r46 = r4
            com.badlogic.gdx.utils.compression.lzma.Encoder$Optimal[] r3 = r0._optimum
            r3 = r3[r11]
            int r3 = r3.State
        L_0x0282:
            int r4 = r1 + -1
            if (r11 != r4) goto L_0x02a4
            com.badlogic.gdx.utils.compression.lzma.Encoder$Optimal[] r4 = r0._optimum
            r4 = r4[r1]
            boolean r4 = r4.IsShortRep()
            if (r4 == 0) goto L_0x029a
            int r3 = com.badlogic.gdx.utils.compression.lzma.Base.StateUpdateShortRep(r3)
            r26 = r5
            r27 = r7
            goto L_0x038a
        L_0x029a:
            int r3 = com.badlogic.gdx.utils.compression.lzma.Base.StateUpdateChar(r3)
            r26 = r5
            r27 = r7
            goto L_0x038a
        L_0x02a4:
            com.badlogic.gdx.utils.compression.lzma.Encoder$Optimal[] r4 = r0._optimum
            r4 = r4[r1]
            boolean r4 = r4.Prev1IsChar
            if (r4 == 0) goto L_0x02c6
            com.badlogic.gdx.utils.compression.lzma.Encoder$Optimal[] r4 = r0._optimum
            r4 = r4[r1]
            boolean r4 = r4.Prev2
            if (r4 == 0) goto L_0x02c6
            com.badlogic.gdx.utils.compression.lzma.Encoder$Optimal[] r4 = r0._optimum
            r4 = r4[r1]
            int r11 = r4.PosPrev2
            com.badlogic.gdx.utils.compression.lzma.Encoder$Optimal[] r4 = r0._optimum
            r4 = r4[r1]
            int r4 = r4.BackPrev2
            int r3 = com.badlogic.gdx.utils.compression.lzma.Base.StateUpdateRep(r3)
            r12 = 4
            goto L_0x02d8
        L_0x02c6:
            com.badlogic.gdx.utils.compression.lzma.Encoder$Optimal[] r4 = r0._optimum
            r4 = r4[r1]
            int r4 = r4.BackPrev
            r12 = 4
            if (r4 >= r12) goto L_0x02d4
            int r3 = com.badlogic.gdx.utils.compression.lzma.Base.StateUpdateRep(r3)
            goto L_0x02d8
        L_0x02d4:
            int r3 = com.badlogic.gdx.utils.compression.lzma.Base.StateUpdateMatch(r3)
        L_0x02d8:
            com.badlogic.gdx.utils.compression.lzma.Encoder$Optimal[] r12 = r0._optimum
            r12 = r12[r11]
            r25 = r3
            r3 = 4
            if (r4 >= r3) goto L_0x036a
            if (r4 != 0) goto L_0x0308
            int[] r3 = r0.reps
            r26 = r5
            int r5 = r12.Backs0
            r16 = 0
            r3[r16] = r5
            int[] r3 = r0.reps
            int r5 = r12.Backs1
            r27 = r7
            r7 = 1
            r3[r7] = r5
            int[] r3 = r0.reps
            int r5 = r12.Backs2
            r17 = 2
            r3[r17] = r5
            int[] r3 = r0.reps
            int r5 = r12.Backs3
            r18 = 3
            r3[r18] = r5
            goto L_0x0388
        L_0x0308:
            r26 = r5
            r27 = r7
            r7 = 1
            if (r4 != r7) goto L_0x032d
            int[] r3 = r0.reps
            int r5 = r12.Backs1
            r16 = 0
            r3[r16] = r5
            int[] r3 = r0.reps
            int r5 = r12.Backs0
            r3[r7] = r5
            int[] r3 = r0.reps
            int r5 = r12.Backs2
            r7 = 2
            r3[r7] = r5
            int[] r3 = r0.reps
            int r5 = r12.Backs3
            r17 = 3
            r3[r17] = r5
            goto L_0x0388
        L_0x032d:
            r7 = 2
            if (r4 != r7) goto L_0x034d
            int[] r3 = r0.reps
            int r5 = r12.Backs2
            r7 = 0
            r3[r7] = r5
            int[] r3 = r0.reps
            int r5 = r12.Backs0
            r7 = 1
            r3[r7] = r5
            int[] r3 = r0.reps
            int r5 = r12.Backs1
            r7 = 2
            r3[r7] = r5
            int[] r3 = r0.reps
            int r5 = r12.Backs3
            r7 = 3
            r3[r7] = r5
            goto L_0x0388
        L_0x034d:
            int[] r3 = r0.reps
            int r5 = r12.Backs3
            r7 = 0
            r3[r7] = r5
            int[] r3 = r0.reps
            int r5 = r12.Backs0
            r7 = 1
            r3[r7] = r5
            int[] r3 = r0.reps
            int r5 = r12.Backs1
            r7 = 2
            r3[r7] = r5
            int[] r3 = r0.reps
            int r5 = r12.Backs2
            r7 = 3
            r3[r7] = r5
            goto L_0x0388
        L_0x036a:
            r26 = r5
            r27 = r7
            int[] r3 = r0.reps
            int r5 = r4 + -4
            r7 = 0
            r3[r7] = r5
            int r5 = r12.Backs0
            r7 = 1
            r3[r7] = r5
            int[] r3 = r0.reps
            int r5 = r12.Backs1
            r7 = 2
            r3[r7] = r5
            int[] r3 = r0.reps
            int r5 = r12.Backs2
            r7 = 3
            r3[r7] = r5
        L_0x0388:
            r3 = r25
        L_0x038a:
            com.badlogic.gdx.utils.compression.lzma.Encoder$Optimal[] r4 = r0._optimum
            r5 = r4[r1]
            r5.State = r3
            r5 = r4[r1]
            int[] r7 = r0.reps
            r25 = r8
            r12 = 0
            r8 = r7[r12]
            r5.Backs0 = r8
            r5 = r4[r1]
            r8 = 1
            r12 = r7[r8]
            r5.Backs1 = r12
            r5 = r4[r1]
            r8 = 2
            r12 = r7[r8]
            r5.Backs2 = r12
            r5 = r4[r1]
            r21 = 3
            r7 = r7[r21]
            r5.Backs3 = r7
            r4 = r4[r1]
            int r4 = r4.Price
            com.badlogic.gdx.utils.compression.lz.BinTree r5 = r0._matchFinder
            r7 = -1
            byte r9 = r5.GetIndexByte(r7)
            com.badlogic.gdx.utils.compression.lz.BinTree r5 = r0._matchFinder
            int[] r7 = r0.reps
            r8 = 0
            r7 = r7[r8]
            int r7 = 0 - r7
            r8 = 1
            int r7 = r7 - r8
            int r7 = r7 - r8
            byte r13 = r5.GetIndexByte(r7)
            int r5 = r0._posStateMask
            r15 = r2 & r5
            short[] r5 = r0._isMatch
            int r7 = r3 << 4
            int r7 = r7 + r15
            short r5 = r5[r7]
            int r5 = com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice0(r5)
            int r5 = r5 + r4
            com.badlogic.gdx.utils.compression.lzma.Encoder$LiteralEncoder r7 = r0._literalEncoder
            com.badlogic.gdx.utils.compression.lz.BinTree r8 = r0._matchFinder
            r12 = -2
            byte r8 = r8.GetIndexByte(r12)
            com.badlogic.gdx.utils.compression.lzma.Encoder$LiteralEncoder$Encoder2 r7 = r7.GetSubCoder(r2, r8)
            boolean r8 = com.badlogic.gdx.utils.compression.lzma.Base.StateIsCharState(r3)
            r12 = 1
            r8 = r8 ^ r12
            int r7 = r7.GetPrice(r8, r13, r9)
            int r5 = r5 + r7
            com.badlogic.gdx.utils.compression.lzma.Encoder$Optimal[] r7 = r0._optimum
            int r8 = r1 + 1
            r7 = r7[r8]
            r8 = 0
            int r12 = r7.Price
            if (r5 >= r12) goto L_0x0407
            r7.Price = r5
            r7.PosPrev = r1
            r7.MakeAsChar()
            r8 = 1
        L_0x0407:
            short[] r12 = r0._isMatch
            int r28 = r3 << 4
            int r28 = r28 + r15
            short r12 = r12[r28]
            int r12 = com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(r12)
            int r23 = r4 + r12
            short[] r12 = r0._isRep
            short r12 = r12[r3]
            int r12 = com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(r12)
            int r12 = r23 + r12
            if (r13 != r9) goto L_0x0445
            r27 = r4
            int r4 = r7.PosPrev
            if (r4 >= r1) goto L_0x042f
            int r4 = r7.BackPrev
            if (r4 == 0) goto L_0x042c
            goto L_0x042f
        L_0x042c:
            r28 = r8
            goto L_0x0449
        L_0x042f:
            int r4 = r0.GetRepLen1Price(r3, r15)
            int r4 = r4 + r12
            r28 = r8
            int r8 = r7.Price
            if (r4 > r8) goto L_0x0449
            r7.Price = r4
            r7.PosPrev = r1
            r7.MakeAsShortRep()
            r8 = 1
            r28 = r8
            goto L_0x0449
        L_0x0445:
            r27 = r4
            r28 = r8
        L_0x0449:
            com.badlogic.gdx.utils.compression.lz.BinTree r4 = r0._matchFinder
            int r4 = r4.GetNumAvailableBytes()
            r8 = 1
            int r4 = r4 + r8
            int r8 = 4095 - r1
            int r4 = java.lang.Math.min(r8, r4)
            r8 = r4
            r19 = r7
            r7 = 2
            if (r8 >= r7) goto L_0x046a
            r4 = r46
            r19 = r8
            r7 = r12
            r3 = r24
            r8 = r25
            r5 = r26
            goto L_0x0211
        L_0x046a:
            int r7 = r0._numFastBytes
            if (r8 <= r7) goto L_0x0471
            int r7 = r0._numFastBytes
            goto L_0x0472
        L_0x0471:
            r7 = r8
        L_0x0472:
            if (r28 != 0) goto L_0x0502
            if (r13 == r9) goto L_0x0502
            int r8 = r4 + -1
            r29 = r9
            int r9 = r0._numFastBytes
            int r8 = java.lang.Math.min(r8, r9)
            com.badlogic.gdx.utils.compression.lz.BinTree r9 = r0._matchFinder
            r30 = r10
            int[] r10 = r0.reps
            r31 = r11
            r11 = 0
            r10 = r10[r11]
            int r9 = r9.GetMatchLen(r11, r10, r8)
            r10 = 2
            if (r9 < r10) goto L_0x04f9
            int r10 = com.badlogic.gdx.utils.compression.lzma.Base.StateUpdateChar(r3)
            int r11 = r2 + 1
            r32 = r8
            int r8 = r0._posStateMask
            r8 = r8 & r11
            short[] r11 = r0._isMatch
            int r33 = r10 << 4
            int r33 = r33 + r8
            short r11 = r11[r33]
            int r11 = com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(r11)
            int r11 = r11 + r5
            r33 = r5
            short[] r5 = r0._isRep
            short r5 = r5[r10]
            int r5 = com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(r5)
            int r11 = r11 + r5
            int r5 = r1 + 1
            int r5 = r5 + r9
            r34 = r13
            r13 = r25
        L_0x04bc:
            if (r13 >= r5) goto L_0x04d2
            r35 = r14
            com.badlogic.gdx.utils.compression.lzma.Encoder$Optimal[] r14 = r0._optimum
            int r13 = r13 + 1
            r14 = r14[r13]
            r25 = r13
            r13 = 268435455(0xfffffff, float:2.5243547E-29)
            r14.Price = r13
            r13 = r25
            r14 = r35
            goto L_0x04bc
        L_0x04d2:
            r35 = r14
            r14 = 0
            int r25 = r0.GetRepPrice(r14, r9, r10, r8)
            int r14 = r11 + r25
            r36 = r8
            com.badlogic.gdx.utils.compression.lzma.Encoder$Optimal[] r8 = r0._optimum
            r8 = r8[r5]
            r37 = r5
            int r5 = r8.Price
            if (r14 >= r5) goto L_0x04f6
            r8.Price = r14
            int r5 = r1 + 1
            r8.PosPrev = r5
            r5 = 0
            r8.BackPrev = r5
            r5 = 1
            r8.Prev1IsChar = r5
            r5 = 0
            r8.Prev2 = r5
        L_0x04f6:
            r25 = r13
            goto L_0x050e
        L_0x04f9:
            r33 = r5
            r32 = r8
            r34 = r13
            r35 = r14
            goto L_0x050e
        L_0x0502:
            r33 = r5
            r29 = r9
            r30 = r10
            r31 = r11
            r34 = r13
            r35 = r14
        L_0x050e:
            r5 = 2
            r8 = 0
        L_0x0510:
            r10 = 4
            if (r8 >= r10) goto L_0x066f
            com.badlogic.gdx.utils.compression.lz.BinTree r9 = r0._matchFinder
            int[] r11 = r0.reps
            r11 = r11[r8]
            r14 = -1
            int r9 = r9.GetMatchLen(r14, r11, r7)
            r11 = 2
            if (r9 >= r11) goto L_0x0527
            r42 = r4
            r41 = r12
            goto L_0x065b
        L_0x0527:
            r11 = r9
            r13 = r9
            r9 = r25
        L_0x052b:
            int r10 = r1 + r13
            if (r9 >= r10) goto L_0x053d
            com.badlogic.gdx.utils.compression.lzma.Encoder$Optimal[] r10 = r0._optimum
            int r9 = r9 + 1
            r10 = r10[r9]
            r14 = 268435455(0xfffffff, float:2.5243547E-29)
            r10.Price = r14
            r10 = 4
            r14 = -1
            goto L_0x052b
        L_0x053d:
            int r10 = r0.GetRepPrice(r8, r13, r3, r15)
            int r10 = r10 + r12
            com.badlogic.gdx.utils.compression.lzma.Encoder$Optimal[] r14 = r0._optimum
            int r25 = r1 + r13
            r14 = r14[r25]
            r25 = r9
            int r9 = r14.Price
            if (r10 >= r9) goto L_0x0557
            r14.Price = r10
            r14.PosPrev = r1
            r14.BackPrev = r8
            r9 = 0
            r14.Prev1IsChar = r9
        L_0x0557:
            int r13 = r13 + -1
            r9 = 2
            if (r13 >= r9) goto L_0x0663
            r9 = r11
            if (r8 != 0) goto L_0x0561
            int r5 = r9 + 1
        L_0x0561:
            if (r9 >= r4) goto L_0x064f
            int r10 = r4 + -1
            int r10 = r10 - r9
            int r13 = r0._numFastBytes
            int r10 = java.lang.Math.min(r10, r13)
            com.badlogic.gdx.utils.compression.lz.BinTree r13 = r0._matchFinder
            int[] r14 = r0.reps
            r14 = r14[r8]
            int r13 = r13.GetMatchLen(r9, r14, r10)
            r14 = 2
            if (r13 < r14) goto L_0x0642
            int r14 = com.badlogic.gdx.utils.compression.lzma.Base.StateUpdateRep(r3)
            int r32 = r2 + r9
            r36 = r5
            int r5 = r0._posStateMask
            r5 = r32 & r5
            int r32 = r0.GetRepPrice(r8, r9, r3, r15)
            int r32 = r12 + r32
            r37 = r10
            short[] r10 = r0._isMatch
            int r38 = r14 << 4
            int r38 = r38 + r5
            short r10 = r10[r38]
            int r10 = com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice0(r10)
            int r32 = r32 + r10
            com.badlogic.gdx.utils.compression.lzma.Encoder$LiteralEncoder r10 = r0._literalEncoder
            r38 = r5
            int r5 = r2 + r9
            r39 = r11
            com.badlogic.gdx.utils.compression.lz.BinTree r11 = r0._matchFinder
            int r40 = r9 + -1
            r42 = r4
            r41 = r12
            r12 = 1
            int r4 = r40 + -1
            byte r4 = r11.GetIndexByte(r4)
            com.badlogic.gdx.utils.compression.lzma.Encoder$LiteralEncoder$Encoder2 r4 = r10.GetSubCoder(r5, r4)
            com.badlogic.gdx.utils.compression.lz.BinTree r5 = r0._matchFinder
            int r10 = r9 + -1
            int[] r11 = r0.reps
            r11 = r11[r8]
            int r11 = r11 + r12
            int r10 = r10 - r11
            byte r5 = r5.GetIndexByte(r10)
            com.badlogic.gdx.utils.compression.lz.BinTree r10 = r0._matchFinder
            int r11 = r9 + -1
            byte r10 = r10.GetIndexByte(r11)
            int r4 = r4.GetPrice(r12, r5, r10)
            int r32 = r32 + r4
            int r4 = com.badlogic.gdx.utils.compression.lzma.Base.StateUpdateChar(r14)
            int r5 = r2 + r9
            int r5 = r5 + r12
            int r10 = r0._posStateMask
            r5 = r5 & r10
            short[] r10 = r0._isMatch
            int r11 = r4 << 4
            int r11 = r11 + r5
            short r10 = r10[r11]
            int r10 = com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(r10)
            int r10 = r32 + r10
            short[] r11 = r0._isRep
            short r11 = r11[r4]
            int r11 = com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(r11)
            int r11 = r11 + r10
            int r12 = r9 + 1
            int r12 = r12 + r13
            r14 = r25
        L_0x05f8:
            r38 = r10
            int r10 = r1 + r12
            if (r14 >= r10) goto L_0x0610
            com.badlogic.gdx.utils.compression.lzma.Encoder$Optimal[] r10 = r0._optimum
            int r14 = r14 + 1
            r10 = r10[r14]
            r25 = r14
            r14 = 268435455(0xfffffff, float:2.5243547E-29)
            r10.Price = r14
            r14 = r25
            r10 = r38
            goto L_0x05f8
        L_0x0610:
            r10 = 0
            int r25 = r0.GetRepPrice(r10, r13, r4, r5)
            int r10 = r11 + r25
            r40 = r4
            com.badlogic.gdx.utils.compression.lzma.Encoder$Optimal[] r4 = r0._optimum
            int r25 = r1 + r12
            r4 = r4[r25]
            r43 = r5
            int r5 = r4.Price
            if (r10 >= r5) goto L_0x063b
            r4.Price = r10
            int r5 = r1 + r9
            r44 = r9
            r9 = 1
            int r5 = r5 + r9
            r4.PosPrev = r5
            r5 = 0
            r4.BackPrev = r5
            r4.Prev1IsChar = r9
            r4.Prev2 = r9
            r4.PosPrev2 = r1
            r4.BackPrev2 = r8
            goto L_0x063d
        L_0x063b:
            r44 = r9
        L_0x063d:
            r25 = r14
            r5 = r36
            goto L_0x065b
        L_0x0642:
            r42 = r4
            r36 = r5
            r44 = r9
            r37 = r10
            r39 = r11
            r41 = r12
            goto L_0x0659
        L_0x064f:
            r42 = r4
            r36 = r5
            r44 = r9
            r39 = r11
            r41 = r12
        L_0x0659:
            r5 = r36
        L_0x065b:
            int r8 = r8 + 1
            r12 = r41
            r4 = r42
            goto L_0x0510
        L_0x0663:
            r42 = r4
            r39 = r11
            r41 = r12
            r9 = r25
            r10 = 4
            r14 = -1
            goto L_0x052b
        L_0x066f:
            r42 = r4
            r41 = r12
            if (r6 <= r7) goto L_0x0685
            r6 = r7
            r4 = 0
        L_0x0677:
            int[] r8 = r0._matchDistances
            r9 = r8[r4]
            if (r6 <= r9) goto L_0x0680
            int r4 = r4 + 2
            goto L_0x0677
        L_0x0680:
            r8[r4] = r6
            int r4 = r4 + 2
            goto L_0x0687
        L_0x0685:
            r4 = r46
        L_0x0687:
            if (r6 < r5) goto L_0x07ff
            short[] r8 = r0._isRep
            short r8 = r8[r3]
            int r8 = com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice0(r8)
            int r8 = r23 + r8
            r9 = r25
        L_0x0695:
            int r10 = r1 + r6
            if (r9 >= r10) goto L_0x06a5
            com.badlogic.gdx.utils.compression.lzma.Encoder$Optimal[] r10 = r0._optimum
            int r9 = r9 + 1
            r10 = r10[r9]
            r11 = 268435455(0xfffffff, float:2.5243547E-29)
            r10.Price = r11
            goto L_0x0695
        L_0x06a5:
            r10 = 0
        L_0x06a6:
            int[] r11 = r0._matchDistances
            r11 = r11[r10]
            if (r5 <= r11) goto L_0x06af
            int r10 = r10 + 2
            goto L_0x06a6
        L_0x06af:
            r11 = r5
        L_0x06b0:
            int[] r12 = r0._matchDistances
            int r13 = r10 + 1
            r12 = r12[r13]
            int r13 = r0.GetPosLenPrice(r12, r11, r15)
            int r13 = r13 + r8
            com.badlogic.gdx.utils.compression.lzma.Encoder$Optimal[] r14 = r0._optimum
            int r22 = r1 + r11
            r14 = r14[r22]
            r32 = r5
            int r5 = r14.Price
            if (r13 >= r5) goto L_0x06d2
            r14.Price = r13
            r14.PosPrev = r1
            int r5 = r12 + 4
            r14.BackPrev = r5
            r5 = 0
            r14.Prev1IsChar = r5
        L_0x06d2:
            int[] r5 = r0._matchDistances
            r5 = r5[r10]
            if (r11 != r5) goto L_0x07de
            r5 = r42
            if (r11 >= r5) goto L_0x07bf
            int r22 = r5 + -1
            r42 = r5
            int r5 = r22 - r11
            r36 = r6
            int r6 = r0._numFastBytes
            int r5 = java.lang.Math.min(r5, r6)
            com.badlogic.gdx.utils.compression.lz.BinTree r6 = r0._matchFinder
            int r6 = r6.GetMatchLen(r11, r12, r5)
            r46 = r5
            r5 = 2
            if (r6 < r5) goto L_0x07b1
            int r17 = com.badlogic.gdx.utils.compression.lzma.Base.StateUpdateMatch(r3)
            int r22 = r2 + r11
            int r5 = r0._posStateMask
            r5 = r22 & r5
            r38 = r3
            short[] r3 = r0._isMatch
            int r22 = r17 << 4
            int r22 = r22 + r5
            short r3 = r3[r22]
            int r3 = com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice0(r3)
            int r3 = r3 + r13
            r22 = r5
            com.badlogic.gdx.utils.compression.lzma.Encoder$LiteralEncoder r5 = r0._literalEncoder
            r39 = r7
            int r7 = r2 + r11
            r40 = r8
            com.badlogic.gdx.utils.compression.lz.BinTree r8 = r0._matchFinder
            int r25 = r11 + -1
            r43 = r9
            r44 = r13
            r9 = 1
            int r13 = r25 + -1
            byte r8 = r8.GetIndexByte(r13)
            com.badlogic.gdx.utils.compression.lzma.Encoder$LiteralEncoder$Encoder2 r5 = r5.GetSubCoder(r7, r8)
            com.badlogic.gdx.utils.compression.lz.BinTree r7 = r0._matchFinder
            int r8 = r12 + 1
            int r8 = r11 - r8
            int r8 = r8 - r9
            byte r7 = r7.GetIndexByte(r8)
            com.badlogic.gdx.utils.compression.lz.BinTree r8 = r0._matchFinder
            int r13 = r11 + -1
            byte r8 = r8.GetIndexByte(r13)
            int r5 = r5.GetPrice(r9, r7, r8)
            int r3 = r3 + r5
            int r5 = com.badlogic.gdx.utils.compression.lzma.Base.StateUpdateChar(r17)
            int r7 = r2 + r11
            int r7 = r7 + r9
            int r8 = r0._posStateMask
            r7 = r7 & r8
            short[] r8 = r0._isMatch
            int r9 = r5 << 4
            int r9 = r9 + r7
            short r8 = r8[r9]
            int r8 = com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(r8)
            int r8 = r8 + r3
            short[] r9 = r0._isRep
            short r9 = r9[r5]
            int r9 = com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(r9)
            int r9 = r9 + r8
            int r13 = r11 + 1
            int r13 = r13 + r6
            r17 = r2
            r2 = r43
        L_0x0769:
            r22 = r3
            int r3 = r1 + r13
            if (r2 >= r3) goto L_0x0781
            com.badlogic.gdx.utils.compression.lzma.Encoder$Optimal[] r3 = r0._optimum
            int r2 = r2 + 1
            r3 = r3[r2]
            r25 = r2
            r2 = 268435455(0xfffffff, float:2.5243547E-29)
            r3.Price = r2
            r3 = r22
            r2 = r25
            goto L_0x0769
        L_0x0781:
            r25 = r2
            r2 = 268435455(0xfffffff, float:2.5243547E-29)
            r3 = 0
            int r20 = r0.GetRepPrice(r3, r6, r5, r7)
            int r3 = r9 + r20
            com.badlogic.gdx.utils.compression.lzma.Encoder$Optimal[] r2 = r0._optimum
            int r43 = r1 + r13
            r14 = r2[r43]
            int r2 = r14.Price
            if (r3 >= r2) goto L_0x07ad
            r14.Price = r3
            int r2 = r1 + r11
            r0 = 1
            int r2 = r2 + r0
            r14.PosPrev = r2
            r2 = 0
            r14.BackPrev = r2
            r14.Prev1IsChar = r0
            r14.Prev2 = r0
            r14.PosPrev2 = r1
            int r0 = r12 + 4
            r14.BackPrev2 = r0
            goto L_0x07ae
        L_0x07ad:
            r2 = 0
        L_0x07ae:
            r44 = r3
            goto L_0x07d2
        L_0x07b1:
            r17 = r2
            r38 = r3
            r39 = r7
            r40 = r8
            r43 = r9
            r44 = r13
            r2 = 0
            goto L_0x07d0
        L_0x07bf:
            r17 = r2
            r38 = r3
            r42 = r5
            r36 = r6
            r39 = r7
            r40 = r8
            r43 = r9
            r44 = r13
            r2 = 0
        L_0x07d0:
            r25 = r43
        L_0x07d2:
            int r10 = r10 + 2
            if (r10 != r4) goto L_0x07db
            r8 = r25
            r22 = r40
            goto L_0x080c
        L_0x07db:
            r9 = r25
            goto L_0x07ed
        L_0x07de:
            r17 = r2
            r38 = r3
            r36 = r6
            r39 = r7
            r40 = r8
            r43 = r9
            r44 = r13
            r2 = 0
        L_0x07ed:
            int r11 = r11 + 1
            r0 = r45
            r2 = r17
            r5 = r32
            r6 = r36
            r3 = r38
            r7 = r39
            r8 = r40
            goto L_0x06b0
        L_0x07ff:
            r17 = r2
            r38 = r3
            r32 = r5
            r36 = r6
            r39 = r7
            r2 = 0
            r8 = r25
        L_0x080c:
            r0 = r45
            r2 = r17
            r3 = r24
            r5 = r26
            r9 = r29
            r10 = r30
            r13 = r34
            r14 = r35
            r19 = r39
            r7 = r41
            goto L_0x0211
        L_0x0822:
            r23 = r2
            r24 = r3
            r30 = r10
            r2 = 0
            r21 = 3
            r0 = r45
            r1 = r12
            r2 = r23
            r5 = 3
            goto L_0x015e
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.utils.compression.lzma.Encoder.GetOptimum(int):int");
    }

    /* access modifiers changed from: package-private */
    public boolean ChangePair(int smallDist, int bigDist) {
        return smallDist < (1 << (32 - 7)) && bigDist >= (smallDist << 7);
    }

    /* access modifiers changed from: package-private */
    public void WriteEndMarker(int posState) throws IOException {
        if (this._writeEndMark) {
            this._rangeEncoder.Encode(this._isMatch, (this._state << 4) + posState, 1);
            this._rangeEncoder.Encode(this._isRep, this._state, 0);
            this._state = Base.StateUpdateMatch(this._state);
            this._lenEncoder.Encode(this._rangeEncoder, 2 - 2, posState);
            this._posSlotEncoder[Base.GetLenToPosState(2)].Encode(this._rangeEncoder, 63);
            int posReduced = (1 << 30) - 1;
            this._rangeEncoder.EncodeDirectBits(posReduced >> 4, 30 - 4);
            this._posAlignEncoder.ReverseEncode(this._rangeEncoder, posReduced & 15);
        }
    }

    /* access modifiers changed from: package-private */
    public void Flush(int nowPos) throws IOException {
        ReleaseMFStream();
        WriteEndMarker(this._posStateMask & nowPos);
        this._rangeEncoder.FlushData();
        this._rangeEncoder.FlushStream();
    }

    public void CodeOneBlock(long[] inSize, long[] outSize, boolean[] finished2) throws IOException {
        int i = 0;
        inSize[0] = 0;
        outSize[0] = 0;
        finished2[0] = true;
        InputStream inputStream = this._inStream;
        if (inputStream != null) {
            this._matchFinder.SetStream(inputStream);
            this._matchFinder.Init();
            this._needReleaseMFStream = true;
            this._inStream = null;
        }
        if (!this._finished) {
            this._finished = true;
            long progressPosValuePrev = this.nowPos64;
            int i2 = 4;
            if (this.nowPos64 == 0) {
                if (this._matchFinder.GetNumAvailableBytes() == 0) {
                    Flush((int) this.nowPos64);
                    return;
                }
                ReadMatchDistances();
                this._rangeEncoder.Encode(this._isMatch, (this._state << 4) + (this._posStateMask & ((int) this.nowPos64)), 0);
                this._state = Base.StateUpdateChar(this._state);
                byte curByte = this._matchFinder.GetIndexByte(0 - this._additionalOffset);
                this._literalEncoder.GetSubCoder((int) this.nowPos64, this._previousByte).Encode(this._rangeEncoder, curByte);
                this._previousByte = curByte;
                this._additionalOffset--;
                this.nowPos64++;
            }
            if (this._matchFinder.GetNumAvailableBytes() == 0) {
                Flush((int) this.nowPos64);
                return;
            }
            while (true) {
                int len = GetOptimum((int) this.nowPos64);
                int pos = this.backRes;
                int posState = this._posStateMask & ((int) this.nowPos64);
                int complexState = (this._state << i2) + posState;
                if (len == 1 && pos == -1) {
                    this._rangeEncoder.Encode(this._isMatch, complexState, i);
                    byte curByte2 = this._matchFinder.GetIndexByte(0 - this._additionalOffset);
                    LiteralEncoder.Encoder2 subCoder = this._literalEncoder.GetSubCoder((int) this.nowPos64, this._previousByte);
                    if (!Base.StateIsCharState(this._state)) {
                        subCoder.EncodeMatched(this._rangeEncoder, this._matchFinder.GetIndexByte(((0 - this._repDistances[i]) - 1) - this._additionalOffset), curByte2);
                    } else {
                        subCoder.Encode(this._rangeEncoder, curByte2);
                    }
                    this._previousByte = curByte2;
                    this._state = Base.StateUpdateChar(this._state);
                } else {
                    this._rangeEncoder.Encode(this._isMatch, complexState, 1);
                    if (pos < i2) {
                        this._rangeEncoder.Encode(this._isRep, this._state, 1);
                        if (pos == 0) {
                            this._rangeEncoder.Encode(this._isRepG0, this._state, i);
                            if (len == 1) {
                                this._rangeEncoder.Encode(this._isRep0Long, complexState, i);
                            } else {
                                this._rangeEncoder.Encode(this._isRep0Long, complexState, 1);
                            }
                        } else {
                            this._rangeEncoder.Encode(this._isRepG0, this._state, 1);
                            if (pos == 1) {
                                this._rangeEncoder.Encode(this._isRepG1, this._state, i);
                            } else {
                                this._rangeEncoder.Encode(this._isRepG1, this._state, 1);
                                this._rangeEncoder.Encode(this._isRepG2, this._state, pos - 2);
                            }
                        }
                        if (len == 1) {
                            this._state = Base.StateUpdateShortRep(this._state);
                        } else {
                            this._repMatchLenEncoder.Encode(this._rangeEncoder, len - 2, posState);
                            this._state = Base.StateUpdateRep(this._state);
                        }
                        int distance = this._repDistances[pos];
                        if (pos != 0) {
                            for (int i3 = pos; i3 >= 1; i3--) {
                                int[] iArr = this._repDistances;
                                iArr[i3] = iArr[i3 - 1];
                            }
                            this._repDistances[i] = distance;
                        }
                    } else {
                        this._rangeEncoder.Encode(this._isRep, this._state, i);
                        this._state = Base.StateUpdateMatch(this._state);
                        this._lenEncoder.Encode(this._rangeEncoder, len - 2, posState);
                        int pos2 = pos - 4;
                        int posSlot = GetPosSlot(pos2);
                        this._posSlotEncoder[Base.GetLenToPosState(len)].Encode(this._rangeEncoder, posSlot);
                        if (posSlot >= i2) {
                            int footerBits = (posSlot >> 1) - 1;
                            int baseVal = ((posSlot & 1) | 2) << footerBits;
                            int posReduced = pos2 - baseVal;
                            if (posSlot < 14) {
                                BitTreeEncoder.ReverseEncode(this._posEncoders, (baseVal - posSlot) - 1, this._rangeEncoder, footerBits, posReduced);
                            } else {
                                this._rangeEncoder.EncodeDirectBits(posReduced >> 4, footerBits - 4);
                                this._posAlignEncoder.ReverseEncode(this._rangeEncoder, posReduced & 15);
                                this._alignPriceCount++;
                            }
                        }
                        int distance2 = pos2;
                        for (int i4 = 3; i4 >= 1; i4--) {
                            int[] iArr2 = this._repDistances;
                            iArr2[i4] = iArr2[i4 - 1];
                        }
                        this._repDistances[0] = distance2;
                        this._matchPriceCount++;
                    }
                    this._previousByte = this._matchFinder.GetIndexByte((len - 1) - this._additionalOffset);
                }
                this._additionalOffset -= len;
                this.nowPos64 += (long) len;
                if (this._additionalOffset == 0) {
                    if (this._matchPriceCount >= 128) {
                        FillDistancesPrices();
                    }
                    if (this._alignPriceCount >= 16) {
                        FillAlignPrices();
                    }
                    inSize[0] = this.nowPos64;
                    outSize[0] = this._rangeEncoder.GetProcessedSizeAdd();
                    if (this._matchFinder.GetNumAvailableBytes() == 0) {
                        Flush((int) this.nowPos64);
                        return;
                    } else if (this.nowPos64 - progressPosValuePrev >= 4096) {
                        this._finished = false;
                        finished2[0] = false;
                        return;
                    }
                }
                i = 0;
                i2 = 4;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void ReleaseMFStream() {
        BinTree binTree = this._matchFinder;
        if (binTree != null && this._needReleaseMFStream) {
            binTree.ReleaseStream();
            this._needReleaseMFStream = false;
        }
    }

    /* access modifiers changed from: package-private */
    public void SetOutStream(OutputStream outStream) {
        this._rangeEncoder.SetStream(outStream);
    }

    /* access modifiers changed from: package-private */
    public void ReleaseOutStream() {
        this._rangeEncoder.ReleaseStream();
    }

    /* access modifiers changed from: package-private */
    public void ReleaseStreams() {
        ReleaseMFStream();
        ReleaseOutStream();
    }

    /* access modifiers changed from: package-private */
    public void SetStreams(InputStream inStream, OutputStream outStream, long inSize, long outSize) {
        this._inStream = inStream;
        this._finished = false;
        Create();
        SetOutStream(outStream);
        Init();
        FillDistancesPrices();
        FillAlignPrices();
        this._lenEncoder.SetTableSize((this._numFastBytes + 1) - 2);
        this._lenEncoder.UpdateTables(1 << this._posStateBits);
        this._repMatchLenEncoder.SetTableSize((this._numFastBytes + 1) - 2);
        this._repMatchLenEncoder.UpdateTables(1 << this._posStateBits);
        this.nowPos64 = 0;
    }

    public void Code(InputStream inStream, OutputStream outStream, long inSize, long outSize, ICodeProgress progress) throws IOException {
        this._needReleaseMFStream = false;
        try {
            SetStreams(inStream, outStream, inSize, outSize);
            while (true) {
                CodeOneBlock(this.processedInSize, this.processedOutSize, this.finished);
                if (!this.finished[0]) {
                    if (progress != null) {
                        progress.SetProgress(this.processedInSize[0], this.processedOutSize[0]);
                    }
                } else {
                    return;
                }
            }
        } finally {
            ReleaseStreams();
        }
    }

    public void WriteCoderProperties(OutputStream outStream) throws IOException {
        this.properties[0] = (byte) ((((this._posStateBits * 5) + this._numLiteralPosStateBits) * 9) + this._numLiteralContextBits);
        for (int i = 0; i < 4; i++) {
            this.properties[i + 1] = (byte) (this._dictionarySize >> (i * 8));
        }
        outStream.write(this.properties, 0, 5);
    }

    /* access modifiers changed from: package-private */
    public void FillDistancesPrices() {
        for (int i = 4; i < 128; i++) {
            int posSlot = GetPosSlot(i);
            int footerBits = (posSlot >> 1) - 1;
            int baseVal = ((posSlot & 1) | 2) << footerBits;
            this.tempPrices[i] = BitTreeEncoder.ReverseGetPrice(this._posEncoders, (baseVal - posSlot) - 1, footerBits, i - baseVal);
        }
        for (int lenToPosState = 0; lenToPosState < 4; lenToPosState++) {
            BitTreeEncoder encoder = this._posSlotEncoder[lenToPosState];
            int st = lenToPosState << 6;
            for (int posSlot2 = 0; posSlot2 < this._distTableSize; posSlot2++) {
                this._posSlotPrices[st + posSlot2] = encoder.GetPrice(posSlot2);
            }
            for (int posSlot3 = 14; posSlot3 < this._distTableSize; posSlot3++) {
                int[] iArr = this._posSlotPrices;
                int i2 = st + posSlot3;
                iArr[i2] = iArr[i2] + ((((posSlot3 >> 1) - 1) - 4) << 6);
            }
            int st2 = lenToPosState * 128;
            int i3 = 0;
            while (i3 < 4) {
                this._distancesPrices[st2 + i3] = this._posSlotPrices[st + i3];
                i3++;
            }
            while (i3 < 128) {
                this._distancesPrices[st2 + i3] = this._posSlotPrices[GetPosSlot(i3) + st] + this.tempPrices[i3];
                i3++;
            }
        }
        this._matchPriceCount = 0;
    }

    /* access modifiers changed from: package-private */
    public void FillAlignPrices() {
        for (int i = 0; i < 16; i++) {
            this._alignPrices[i] = this._posAlignEncoder.ReverseGetPrice(i);
        }
        this._alignPriceCount = 0;
    }

    public boolean SetAlgorithm(int algorithm) {
        return true;
    }

    public boolean SetDictionarySize(int dictionarySize) {
        if (dictionarySize < 1 || dictionarySize > (1 << 29)) {
            return false;
        }
        this._dictionarySize = dictionarySize;
        int dicLogSize = 0;
        while (dictionarySize > (1 << dicLogSize)) {
            dicLogSize++;
        }
        this._distTableSize = dicLogSize * 2;
        return true;
    }

    public boolean SetNumFastBytes(int numFastBytes) {
        if (numFastBytes < 5 || numFastBytes > 273) {
            return false;
        }
        this._numFastBytes = numFastBytes;
        return true;
    }

    public boolean SetMatchFinder(int matchFinderIndex) {
        if (matchFinderIndex < 0 || matchFinderIndex > 2) {
            return false;
        }
        int matchFinderIndexPrev = this._matchFinderType;
        this._matchFinderType = matchFinderIndex;
        if (this._matchFinder == null || matchFinderIndexPrev == this._matchFinderType) {
            return true;
        }
        this._dictionarySizePrev = -1;
        this._matchFinder = null;
        return true;
    }

    public boolean SetLcLpPb(int lc, int lp, int pb) {
        if (lp < 0 || lp > 4 || lc < 0 || lc > 8 || pb < 0 || pb > 4) {
            return false;
        }
        this._numLiteralPosStateBits = lp;
        this._numLiteralContextBits = lc;
        this._posStateBits = pb;
        this._posStateMask = (1 << this._posStateBits) - 1;
        return true;
    }

    public void SetEndMarkerMode(boolean endMarkerMode) {
        this._writeEndMark = endMarkerMode;
    }
}
