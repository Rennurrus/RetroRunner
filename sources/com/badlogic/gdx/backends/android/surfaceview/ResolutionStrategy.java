package com.badlogic.gdx.backends.android.surfaceview;

public interface ResolutionStrategy {
    MeasuredDimension calcMeasures(int i, int i2);

    public static class MeasuredDimension {
        public final int height;
        public final int width;

        public MeasuredDimension(int width2, int height2) {
            this.width = width2;
            this.height = height2;
        }
    }
}
