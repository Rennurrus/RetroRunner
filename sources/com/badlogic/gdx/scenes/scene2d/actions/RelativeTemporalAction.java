package com.badlogic.gdx.scenes.scene2d.actions;

public abstract class RelativeTemporalAction extends TemporalAction {
    private float lastPercent;

    /* access modifiers changed from: protected */
    public abstract void updateRelative(float f);

    /* access modifiers changed from: protected */
    public void begin() {
        this.lastPercent = 0.0f;
    }

    /* access modifiers changed from: protected */
    public void update(float percent) {
        updateRelative(percent - this.lastPercent);
        this.lastPercent = percent;
    }
}
