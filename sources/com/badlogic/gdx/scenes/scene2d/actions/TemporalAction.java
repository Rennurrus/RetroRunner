package com.badlogic.gdx.scenes.scene2d.actions;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.utils.Pool;

public abstract class TemporalAction extends Action {
    private boolean began;
    private boolean complete;
    private float duration;
    private Interpolation interpolation;
    private boolean reverse;
    private float time;

    /* access modifiers changed from: protected */
    public abstract void update(float f);

    public TemporalAction() {
    }

    public TemporalAction(float duration2) {
        this.duration = duration2;
    }

    public TemporalAction(float duration2, Interpolation interpolation2) {
        this.duration = duration2;
        this.interpolation = interpolation2;
    }

    public boolean act(float delta) {
        boolean z = true;
        if (this.complete) {
            return true;
        }
        Pool pool = getPool();
        setPool((Pool) null);
        try {
            if (!this.began) {
                begin();
                this.began = true;
            }
            this.time += delta;
            if (this.time < this.duration) {
                z = false;
            }
            this.complete = z;
            float percent = this.complete ? 1.0f : this.time / this.duration;
            if (this.interpolation != null) {
                percent = this.interpolation.apply(percent);
            }
            update(this.reverse ? 1.0f - percent : percent);
            if (this.complete) {
                end();
            }
            return this.complete;
        } finally {
            setPool(pool);
        }
    }

    /* access modifiers changed from: protected */
    public void begin() {
    }

    /* access modifiers changed from: protected */
    public void end() {
    }

    public void finish() {
        this.time = this.duration;
    }

    public void restart() {
        this.time = 0.0f;
        this.began = false;
        this.complete = false;
    }

    public void reset() {
        super.reset();
        this.reverse = false;
        this.interpolation = null;
    }

    public float getTime() {
        return this.time;
    }

    public void setTime(float time2) {
        this.time = time2;
    }

    public float getDuration() {
        return this.duration;
    }

    public void setDuration(float duration2) {
        this.duration = duration2;
    }

    public Interpolation getInterpolation() {
        return this.interpolation;
    }

    public void setInterpolation(Interpolation interpolation2) {
        this.interpolation = interpolation2;
    }

    public boolean isReverse() {
        return this.reverse;
    }

    public void setReverse(boolean reverse2) {
        this.reverse = reverse2;
    }

    public boolean isComplete() {
        return this.complete;
    }
}
