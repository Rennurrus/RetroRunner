package com.badlogic.gdx.utils.compression.rangecoder;

import java.io.IOException;

public class BitTreeEncoder {
    short[] Models;
    int NumBitLevels;

    public BitTreeEncoder(int numBitLevels) {
        this.NumBitLevels = numBitLevels;
        this.Models = new short[(1 << numBitLevels)];
    }

    public void Init() {
        Decoder.InitBitModels(this.Models);
    }

    public void Encode(Encoder rangeEncoder, int symbol) throws IOException {
        int m = 1;
        int bitIndex = this.NumBitLevels;
        while (bitIndex != 0) {
            bitIndex--;
            int bit = (symbol >>> bitIndex) & 1;
            rangeEncoder.Encode(this.Models, m, bit);
            m = (m << 1) | bit;
        }
    }

    public void ReverseEncode(Encoder rangeEncoder, int symbol) throws IOException {
        int m = 1;
        for (int i = 0; i < this.NumBitLevels; i++) {
            int bit = symbol & 1;
            rangeEncoder.Encode(this.Models, m, bit);
            m = (m << 1) | bit;
            symbol >>= 1;
        }
    }

    public int GetPrice(int symbol) {
        int price = 0;
        int m = 1;
        int bitIndex = this.NumBitLevels;
        while (bitIndex != 0) {
            bitIndex--;
            int bit = (symbol >>> bitIndex) & 1;
            price += Encoder.GetPrice(this.Models[m], bit);
            m = (m << 1) + bit;
        }
        return price;
    }

    public int ReverseGetPrice(int symbol) {
        int price = 0;
        int m = 1;
        for (int i = this.NumBitLevels; i != 0; i--) {
            int bit = symbol & 1;
            symbol >>>= 1;
            price += Encoder.GetPrice(this.Models[m], bit);
            m = (m << 1) | bit;
        }
        return price;
    }

    public static int ReverseGetPrice(short[] Models2, int startIndex, int NumBitLevels2, int symbol) {
        int price = 0;
        int m = 1;
        for (int i = NumBitLevels2; i != 0; i--) {
            int bit = symbol & 1;
            symbol >>>= 1;
            price += Encoder.GetPrice(Models2[startIndex + m], bit);
            m = (m << 1) | bit;
        }
        return price;
    }

    public static void ReverseEncode(short[] Models2, int startIndex, Encoder rangeEncoder, int NumBitLevels2, int symbol) throws IOException {
        int m = 1;
        for (int i = 0; i < NumBitLevels2; i++) {
            int bit = symbol & 1;
            rangeEncoder.Encode(Models2, startIndex + m, bit);
            m = (m << 1) | bit;
            symbol >>= 1;
        }
    }
}
