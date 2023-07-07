package com.badlogic.gdx.scenes.scene2d.actions;

import com.badlogic.gdx.graphics.Color;

public class ColorAction extends TemporalAction {
    private Color color;
    private final Color end = new Color();
    private float startA;
    private float startB;
    private float startG;
    private float startR;

    /* access modifiers changed from: protected */
    public void begin() {
        if (this.color == null) {
            this.color = this.target.getColor();
        }
        this.startR = this.color.r;
        this.startG = this.color.g;
        this.startB = this.color.b;
        this.startA = this.color.a;
    }

    /* access modifiers changed from: protected */
    public void update(float percent) {
        if (percent == 0.0f) {
            this.color.set(this.startR, this.startG, this.startB, this.startA);
        } else if (percent == 1.0f) {
            this.color.set(this.end);
        } else {
            this.color.set(this.startR + ((this.end.r - this.startR) * percent), this.startG + ((this.end.g - this.startG) * percent), this.startB + ((this.end.b - this.startB) * percent), this.startA + ((this.end.a - this.startA) * percent));
        }
    }

    public void reset() {
        super.reset();
        this.color = null;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color2) {
        this.color = color2;
    }

    public Color getEndColor() {
        return this.end;
    }

    public void setEndColor(Color color2) {
        this.end.set(color2);
    }
}
