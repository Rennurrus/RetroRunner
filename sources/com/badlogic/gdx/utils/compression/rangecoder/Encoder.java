package com.badlogic.gdx.utils.compression.rangecoder;

import com.badlogic.gdx.graphics.GL20;
import java.io.IOException;
import java.io.OutputStream;

public class Encoder {
    private static int[] ProbPrices = new int[GL20.GL_NEVER];
    static final int kBitModelTotal = 2048;
    static final int kNumBitModelTotalBits = 11;
    public static final int kNumBitPriceShiftBits = 6;
    static final int kNumMoveBits = 5;
    static final int kNumMoveReducingBits = 2;
    static final int kTopMask = -16777216;
    long Low;
    int Range;
    OutputStream Stream;
    int _cache;
    int _cacheSize;
    long _position;

    public void SetStream(OutputStream stream) {
        this.Stream = stream;
    }

    public void ReleaseStream() {
        this.Stream = null;
    }

    public void Init() {
        this._position = 0;
        this.Low = 0;
        this.Range = -1;
        this._cacheSize = 1;
        this._cache = 0;
    }

    public void FlushData() throws IOException {
        for (int i = 0; i < 5; i++) {
            ShiftLow();
        }
    }

    public void FlushStream() throws IOException {
        this.Stream.flush();
    }

    public void ShiftLow() throws IOException {
        int i;
        long j = this.Low;
        int LowHi = (int) (j >>> 32);
        if (LowHi != 0 || j < 4278190080L) {
            this._position += (long) this._cacheSize;
            int temp = this._cache;
            do {
                this.Stream.write(temp + LowHi);
                temp = 255;
                i = this._cacheSize - 1;
                this._cacheSize = i;
            } while (i != 0);
            this._cache = ((int) this.Low) >>> 24;
        }
        this._cacheSize++;
        this.Low = (this.Low & 16777215) << 8;
    }

    public void EncodeDirectBits(int v, int numTotalBits) throws IOException {
        for (int i = numTotalBits - 1; i >= 0; i--) {
            this.Range >>>= 1;
            if (((v >>> i) & 1) == 1) {
                this.Low += (long) this.Range;
            }
            int i2 = this.Range;
            if ((kTopMask & i2) == 0) {
                this.Range = i2 << 8;
                ShiftLow();
            }
        }
    }

    public long GetProcessedSizeAdd() {
        return ((long) this._cacheSize) + this._position + 4;
    }

    public static void InitBitModels(short[] probs) {
        for (int i = 0; i < probs.length; i++) {
            probs[i] = 1024;
        }
    }

    public void Encode(short[] probs, int index, int symbol) throws IOException {
        short prob = probs[index];
        int i = this.Range;
        int newBound = (i >>> 11) * prob;
        if (symbol == 0) {
            this.Range = newBound;
            probs[index] = (short) (((2048 - prob) >>> 5) + prob);
        } else {
            this.Low += ((long) newBound) & 4294967295L;
            this.Range = i - newBound;
            probs[index] = (short) (prob - (prob >>> 5));
        }
        int i2 = this.Range;
        if ((kTopMask & i2) == 0) {
            this.Range = i2 << 8;
            ShiftLow();
        }
    }

    static {
        for (int i = 9 - 1; i >= 0; i--) {
            int end = 1 << (9 - i);
            for (int j = 1 << ((9 - i) - 1); j < end; j++) {
                ProbPrices[j] = (i << 6) + (((end - j) << 6) >>> ((9 - i) - 1));
            }
        }
    }

    public static int GetPrice(int Prob, int symbol) {
        return ProbPrices[(((Prob - symbol) ^ (-symbol)) & 2047) >>> 2];
    }

    public static int GetPrice0(int Prob) {
        return ProbPrices[Prob >>> 2];
    }

    public static int GetPrice1(int Prob) {
        return ProbPrices[(2048 - Prob) >>> 2];
    }
}
