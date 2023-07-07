package com.badlogic.gdx.scenes.scene2d.actions;

import com.badlogic.gdx.math.MathUtils;

public class RotateToAction extends TemporalAction {
    private float end;
    private float start;
    private boolean useShortestDirection = false;

    public RotateToAction() {
    }

    public RotateToAction(boolean useShortestDirection2) {
        this.useShortestDirection = useShortestDirection2;
    }

    /* access modifiers changed from: protected */
    public void begin() {
        this.start = this.target.getRotation();
    }

    /* access modifiers changed from: protected */
    public void update(float percent) {
        float rotation;
        if (percent == 0.0f) {
            rotation = this.start;
        } else if (percent == 1.0f) {
            rotation = this.end;
        } else if (this.useShortestDirection) {
            rotation = MathUtils.lerpAngleDeg(this.start, this.end, percent);
        } else {
            float rotation2 = this.start;
            rotation = rotation2 + ((this.end - rotation2) * percent);
        }
        this.target.setRotation(rotation);
    }

    public float getRotation() {
        return this.end;
    }

    public void setRotation(float rotation) {
        this.end = rotation;
    }

    public boolean isUseShortestDirection() {
        return this.useShortestDirection;
    }

    public void setUseShortestDirection(boolean useShortestDirection2) {
        this.useShortestDirection = useShortestDirection2;
    }
}
