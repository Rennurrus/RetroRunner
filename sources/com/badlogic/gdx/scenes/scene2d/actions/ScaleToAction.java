package com.badlogic.gdx.scenes.scene2d.actions;

public class ScaleToAction extends TemporalAction {
    private float endX;
    private float endY;
    private float startX;
    private float startY;

    /* access modifiers changed from: protected */
    public void begin() {
        this.startX = this.target.getScaleX();
        this.startY = this.target.getScaleY();
    }

    /* access modifiers changed from: protected */
    public void update(float percent) {
        float y;
        float x;
        if (percent == 0.0f) {
            x = this.startX;
            y = this.startY;
        } else if (percent == 1.0f) {
            x = this.endX;
            y = this.endY;
        } else {
            float x2 = this.startX;
            x = x2 + ((this.endX - x2) * percent);
            float f = this.startY;
            y = f + ((this.endY - f) * percent);
        }
        this.target.setScale(x, y);
    }

    public void setScale(float x, float y) {
        this.endX = x;
        this.endY = y;
    }

    public void setScale(float scale) {
        this.endX = scale;
        this.endY = scale;
    }

    public float getX() {
        return this.endX;
    }

    public void setX(float x) {
        this.endX = x;
    }

    public float getY() {
        return this.endY;
    }

    public void setY(float y) {
        this.endY = y;
    }
}
