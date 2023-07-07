package com.badlogic.gdx.backends.android.surfaceview;

import com.badlogic.gdx.backends.android.surfaceview.ResolutionStrategy;

public class FixedResolutionStrategy implements ResolutionStrategy {
    private final int height;
    private final int width;

    public FixedResolutionStrategy(int width2, int height2) {
        this.width = width2;
        this.height = height2;
    }

    public ResolutionStrategy.MeasuredDimension calcMeasures(int widthMeasureSpec, int heightMeasureSpec) {
        return new ResolutionStrategy.MeasuredDimension(this.width, this.height);
    }
}
