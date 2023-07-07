package com.badlogic.gdx.scenes.scene2d.actions;

import com.badlogic.gdx.graphics.Color;

public class AlphaAction extends TemporalAction {
    private Color color;
    private float end;
    private float start;

    /* access modifiers changed from: protected */
    public void begin() {
        if (this.color == null) {
            this.color = this.target.getColor();
        }
        this.start = this.color.a;
    }

    /* access modifiers changed from: protected */
    public void update(float percent) {
        if (percent == 0.0f) {
            this.color.a = this.start;
        } else if (percent == 1.0f) {
            this.color.a = this.end;
        } else {
            Color color2 = this.color;
            float f = this.start;
            color2.a = f + ((this.end - f) * percent);
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

    public float getAlpha() {
        return this.end;
    }

    public void setAlpha(float alpha) {
        this.end = alpha;
    }
}
