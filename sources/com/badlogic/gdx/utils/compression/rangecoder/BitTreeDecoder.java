package com.badlogic.gdx.utils.compression.rangecoder;

import java.io.IOException;

public class BitTreeDecoder {
    short[] Models;
    int NumBitLevels;

    public BitTreeDecoder(int numBitLevels) {
        this.NumBitLevels = numBitLevels;
        this.Models = new short[(1 << numBitLevels)];
    }

    public void Init() {
        Decoder.InitBitModels(this.Models);
    }

    public int Decode(Decoder rangeDecoder) throws IOException {
        int m = 1;
        for (int bitIndex = this.NumBitLevels; bitIndex != 0; bitIndex--) {
            m = (m << 1) + rangeDecoder.DecodeBit(this.Models, m);
        }
        return m - (1 << this.NumBitLevels);
    }

    public int ReverseDecode(Decoder rangeDecoder) throws IOException {
        int m = 1;
        int symbol = 0;
        for (int bitIndex = 0; bitIndex < this.NumBitLevels; bitIndex++) {
            int bit = rangeDecoder.DecodeBit(this.Models, m);
            m = (m << 1) + bit;
            symbol |= bit << bitIndex;
        }
        return symbol;
    }

    public static int ReverseDecode(short[] Models2, int startIndex, Decoder rangeDecoder, int NumBitLevels2) throws IOException {
        int m = 1;
        int symbol = 0;
        for (int bitIndex = 0; bitIndex < NumBitLevels2; bitIndex++) {
            int bit = rangeDecoder.DecodeBit(Models2, startIndex + m);
            m = (m << 1) + bit;
            symbol |= bit << bitIndex;
        }
        return symbol;
    }
}
