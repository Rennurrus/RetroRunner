package com.badlogic.gdx.math;

import java.util.Random;

public class RandomXS128 extends Random {
    private static final double NORM_DOUBLE = 1.1102230246251565E-16d;
    private static final double NORM_FLOAT = 5.9604644775390625E-8d;
    private long seed0;
    private long seed1;

    public RandomXS128() {
        setSeed(new Random().nextLong());
    }

    public RandomXS128(long seed) {
        setSeed(seed);
    }

    public RandomXS128(long seed02, long seed12) {
        setState(seed02, seed12);
    }

    public long nextLong() {
        long s1 = this.seed0;
        long s0 = this.seed1;
        this.seed0 = s0;
        long s12 = s1 ^ (s1 << 23);
        long j = ((s12 ^ s0) ^ (s12 >>> 17)) ^ (s0 >>> 26);
        this.seed1 = j;
        return j + s0;
    }

    /* access modifiers changed from: protected */
    public final int next(int bits) {
        return (int) (nextLong() & ((1 << bits) - 1));
    }

    public int nextInt() {
        return (int) nextLong();
    }

    public int nextInt(int n) {
        return (int) nextLong((long) n);
    }

    public long nextLong(long n) {
        long bits;
        long value;
        if (n > 0) {
            do {
                bits = nextLong() >>> 1;
                value = bits % n;
            } while ((bits - value) + (n - 1) < 0);
            return value;
        }
        throw new IllegalArgumentException("n must be positive");
    }

    public double nextDouble() {
        double nextLong = (double) (nextLong() >>> 11);
        Double.isNaN(nextLong);
        return nextLong * NORM_DOUBLE;
    }

    public float nextFloat() {
        double nextLong = (double) (nextLong() >>> 40);
        Double.isNaN(nextLong);
        return (float) (nextLong * NORM_FLOAT);
    }

    public boolean nextBoolean() {
        return (nextLong() & 1) != 0;
    }

    public void nextBytes(byte[] bytes) {
        int n;
        int i = bytes.length;
        while (i != 0) {
            int n2 = i < 8 ? i : 8;
            long bits = nextLong();
            while (true) {
                n = n2 - 1;
                if (n2 == 0) {
                    break;
                }
                i--;
                bytes[i] = (byte) ((int) bits);
                bits >>= 8;
                n2 = n;
            }
            int i2 = n;
        }
    }

    public void setSeed(long seed) {
        long seed02 = murmurHash3(seed == 0 ? Long.MIN_VALUE : seed);
        setState(seed02, murmurHash3(seed02));
    }

    public void setState(long seed02, long seed12) {
        this.seed0 = seed02;
        this.seed1 = seed12;
    }

    public long getState(int seed) {
        return seed == 0 ? this.seed0 : this.seed1;
    }

    private static final long murmurHash3(long x) {
        long x2 = (x ^ (x >>> 33)) * -49064778989728563L;
        long x3 = (x2 ^ (x2 >>> 33)) * -4265267296055464877L;
        return x3 ^ (x3 >>> 33);
    }
}
