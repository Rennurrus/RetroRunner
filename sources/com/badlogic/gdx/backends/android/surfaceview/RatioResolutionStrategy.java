package com.badlogic.gdx.backends.android.surfaceview;

import android.view.View;
import com.badlogic.gdx.backends.android.surfaceview.ResolutionStrategy;

public class RatioResolutionStrategy implements ResolutionStrategy {
    private final float ratio;

    public RatioResolutionStrategy(float ratio2) {
        this.ratio = ratio2;
    }

    public RatioResolutionStrategy(float width, float height) {
        this.ratio = width / height;
    }

    public ResolutionStrategy.MeasuredDimension calcMeasures(int widthMeasureSpec, int heightMeasureSpec) {
        int height;
        int width;
        int specWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        int specHeight = View.MeasureSpec.getSize(heightMeasureSpec);
        float desiredRatio = this.ratio;
        if (((float) specWidth) / ((float) specHeight) < desiredRatio) {
            width = specWidth;
            height = Math.round(((float) width) / desiredRatio);
        } else {
            height = specHeight;
            width = Math.round(((float) height) * desiredRatio);
        }
        return new ResolutionStrategy.MeasuredDimension(width, height);
    }
}
