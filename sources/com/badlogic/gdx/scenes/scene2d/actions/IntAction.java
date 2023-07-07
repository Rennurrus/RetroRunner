package com.badlogic.gdx.scenes.scene2d.actions;

import com.badlogic.gdx.math.Interpolation;

public class IntAction extends TemporalAction {
    private int end;
    private int start;
    private int value;

    public IntAction() {
        this.start = 0;
        this.end = 1;
    }

    public IntAction(int start2, int end2) {
        this.start = start2;
        this.end = end2;
    }

    public IntAction(int start2, int end2, float duration) {
        super(duration);
        this.start = start2;
        this.end = end2;
    }

    public IntAction(int start2, int end2, float duration, Interpolation interpolation) {
        super(duration, interpolation);
        this.start = start2;
        this.end = end2;
    }

    /* access modifiers changed from: protected */
    public void begin() {
        this.value = this.start;
    }

    /* access modifiers changed from: protected */
    public void update(float percent) {
        if (percent == 0.0f) {
            this.value = this.start;
        } else if (percent == 1.0f) {
            this.value = this.end;
        } else {
            int i = this.start;
            this.value = (int) (((float) i) + (((float) (this.end - i)) * percent));
        }
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value2) {
        this.value = value2;
    }

    public int getStart() {
        return this.start;
    }

    public void setStart(int start2) {
        this.start = start2;
    }

    public int getEnd() {
        return this.end;
    }

    public void setEnd(int end2) {
        this.end = end2;
    }
}
