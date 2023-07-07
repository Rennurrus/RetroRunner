package com.badlogic.gdx.math;

public class FloatCounter {
    public float average;
    public int count;
    public float latest;
    public float max;
    public final WindowedMean mean;
    public float min;
    public float total;
    public float value;

    public FloatCounter(int windowSize) {
        this.mean = windowSize > 1 ? new WindowedMean(windowSize) : null;
        reset();
    }

    public void put(float value2) {
        this.latest = value2;
        this.total += value2;
        this.count++;
        this.average = this.total / ((float) this.count);
        WindowedMean windowedMean = this.mean;
        if (windowedMean != null) {
            windowedMean.addValue(value2);
            this.value = this.mean.getMean();
        } else {
            this.value = this.latest;
        }
        WindowedMean windowedMean2 = this.mean;
        if (windowedMean2 == null || windowedMean2.hasEnoughData()) {
            float f = this.value;
            if (f < this.min) {
                this.min = f;
            }
            float f2 = this.value;
            if (f2 > this.max) {
                this.max = f2;
            }
        }
    }

    public void reset() {
        this.count = 0;
        this.total = 0.0f;
        this.min = Float.MAX_VALUE;
        this.max = -3.4028235E38f;
        this.average = 0.0f;
        this.latest = 0.0f;
        this.value = 0.0f;
        WindowedMean windowedMean = this.mean;
        if (windowedMean != null) {
            windowedMean.clear();
        }
    }
}
