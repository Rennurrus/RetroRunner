package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Pools;

public class ProgressBar extends Widget implements Disableable {
    private float animateDuration;
    private float animateFromValue;
    private Interpolation animateInterpolation;
    private float animateTime;
    boolean disabled;
    private float max;
    private float min;
    float position;
    private boolean round;
    private float stepSize;
    private ProgressBarStyle style;
    private float value;
    final boolean vertical;
    private Interpolation visualInterpolation;

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public ProgressBar(float r8, float r9, float r10, boolean r11, com.badlogic.gdx.scenes.scene2d.ui.Skin r12) {
        /*
            r7 = this;
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "default-"
            r0.append(r1)
            if (r11 == 0) goto L_0x000f
            java.lang.String r1 = "vertical"
            goto L_0x0011
        L_0x000f:
            java.lang.String r1 = "horizontal"
        L_0x0011:
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.Class<com.badlogic.gdx.scenes.scene2d.ui.ProgressBar$ProgressBarStyle> r1 = com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle.class
            java.lang.Object r0 = r12.get(r0, r1)
            r6 = r0
            com.badlogic.gdx.scenes.scene2d.ui.ProgressBar$ProgressBarStyle r6 = (com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle) r6
            r1 = r7
            r2 = r8
            r3 = r9
            r4 = r10
            r5 = r11
            r1.<init>((float) r2, (float) r3, (float) r4, (boolean) r5, (com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle) r6)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.<init>(float, float, float, boolean, com.badlogic.gdx.scenes.scene2d.ui.Skin):void");
    }

    public ProgressBar(float min2, float max2, float stepSize2, boolean vertical2, Skin skin, String styleName) {
        this(min2, max2, stepSize2, vertical2, (ProgressBarStyle) skin.get(styleName, ProgressBarStyle.class));
    }

    public ProgressBar(float min2, float max2, float stepSize2, boolean vertical2, ProgressBarStyle style2) {
        this.animateInterpolation = Interpolation.linear;
        this.visualInterpolation = Interpolation.linear;
        this.round = true;
        if (min2 > max2) {
            throw new IllegalArgumentException("max must be > min. min,max: " + min2 + ", " + max2);
        } else if (stepSize2 > 0.0f) {
            setStyle(style2);
            this.min = min2;
            this.max = max2;
            this.stepSize = stepSize2;
            this.vertical = vertical2;
            this.value = min2;
            setSize(getPrefWidth(), getPrefHeight());
        } else {
            throw new IllegalArgumentException("stepSize must be > 0: " + stepSize2);
        }
    }

    public void setStyle(ProgressBarStyle style2) {
        if (style2 != null) {
            this.style = style2;
            invalidateHierarchy();
            return;
        }
        throw new IllegalArgumentException("style cannot be null.");
    }

    public ProgressBarStyle getStyle() {
        return this.style;
    }

    public void act(float delta) {
        super.act(delta);
        float f = this.animateTime;
        if (f > 0.0f) {
            this.animateTime = f - delta;
            Stage stage = getStage();
            if (stage != null && stage.getActionsRequestRendering()) {
                Gdx.graphics.requestRendering();
            }
        }
    }

    public void draw(Batch batch, float parentAlpha) {
        float positionWidth;
        float bgLeftWidth;
        float knobWidthHalf;
        float bgTopHeight;
        float positionHeight;
        float knobHeightHalf;
        ProgressBarStyle style2 = this.style;
        boolean disabled2 = this.disabled;
        Drawable knob = getKnobDrawable();
        Drawable bg = (!disabled2 || style2.disabledBackground == null) ? style2.background : style2.disabledBackground;
        Drawable knobBefore = (!disabled2 || style2.disabledKnobBefore == null) ? style2.knobBefore : style2.disabledKnobBefore;
        Drawable knobAfter = (!disabled2 || style2.disabledKnobAfter == null) ? style2.knobAfter : style2.disabledKnobAfter;
        Color color = getColor();
        float x = getX();
        float y = getY();
        float width = getWidth();
        float height = getHeight();
        float knobHeight = knob == null ? 0.0f : knob.getMinHeight();
        float knobWidth = knob == null ? 0.0f : knob.getMinWidth();
        float percent = getVisualPercent();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        if (this.vertical) {
            float positionHeight2 = height;
            float bgBottomHeight = 0.0f;
            if (bg != null) {
                if (this.round) {
                    bg.draw(batch, (float) Math.round(x + ((width - bg.getMinWidth()) * 0.5f)), y, (float) Math.round(bg.getMinWidth()), height);
                } else {
                    bg.draw(batch, (x + width) - (bg.getMinWidth() * 0.5f), y, bg.getMinWidth(), height);
                }
                float bgTopHeight2 = bg.getTopHeight();
                bgBottomHeight = bg.getBottomHeight();
                positionHeight = positionHeight2 - (bgTopHeight2 + bgBottomHeight);
                bgTopHeight = bgTopHeight2;
            } else {
                positionHeight = positionHeight2;
                bgTopHeight = 0.0f;
            }
            if (knob == null) {
                float knobHeightHalf2 = knobBefore == null ? 0.0f : knobBefore.getMinHeight() * 0.5f;
                this.position = (positionHeight - knobHeightHalf2) * percent;
                this.position = Math.min(positionHeight - knobHeightHalf2, this.position);
                knobHeightHalf = knobHeightHalf2;
            } else {
                this.position = (positionHeight - knobHeight) * percent;
                this.position = Math.min(positionHeight - knobHeight, this.position) + bgBottomHeight;
                knobHeightHalf = knobHeight * 0.5f;
            }
            this.position = Math.max(Math.min(0.0f, bgBottomHeight), this.position);
            if (knobBefore != null) {
                if (this.round) {
                    knobBefore.draw(batch, (float) Math.round(x + ((width - knobBefore.getMinWidth()) * 0.5f)), (float) Math.round(y + bgTopHeight), (float) Math.round(knobBefore.getMinWidth()), (float) Math.round(this.position + knobHeightHalf));
                } else {
                    knobBefore.draw(batch, x + ((width - knobBefore.getMinWidth()) * 0.5f), y + bgTopHeight, knobBefore.getMinWidth(), this.position + knobHeightHalf);
                }
            }
            if (knobAfter != null) {
                if (this.round) {
                    knobAfter.draw(batch, (float) Math.round(x + ((width - knobAfter.getMinWidth()) * 0.5f)), (float) Math.round(y + this.position + knobHeightHalf), (float) Math.round(knobAfter.getMinWidth()), (float) Math.round((height - this.position) - knobHeightHalf));
                } else {
                    knobAfter.draw(batch, x + ((width - knobAfter.getMinWidth()) * 0.5f), y + this.position + knobHeightHalf, knobAfter.getMinWidth(), (height - this.position) - knobHeightHalf);
                }
            }
            if (knob == null) {
                Color color2 = color;
            } else if (this.round) {
                float f = bgBottomHeight;
                Color color3 = color;
                knob.draw(batch, (float) Math.round(x + ((width - knobWidth) * 0.5f)), (float) Math.round(y + this.position), (float) Math.round(knobWidth), (float) Math.round(knobHeight));
            } else {
                Color color4 = color;
                knob.draw(batch, x + ((width - knobWidth) * 0.5f), y + this.position, knobWidth, knobHeight);
            }
        } else {
            float positionWidth2 = width;
            if (bg != null) {
                if (this.round) {
                    bg.draw(batch, x, (float) Math.round(y + ((height - bg.getMinHeight()) * 0.5f)), width, (float) Math.round(bg.getMinHeight()));
                } else {
                    bg.draw(batch, x, y + ((height - bg.getMinHeight()) * 0.5f), width, bg.getMinHeight());
                }
                float bgLeftWidth2 = bg.getLeftWidth();
                float bgRightWidth = bg.getRightWidth();
                positionWidth = positionWidth2 - (bgLeftWidth2 + bgRightWidth);
                bgLeftWidth = bgLeftWidth2;
                float f2 = bgRightWidth;
            } else {
                positionWidth = positionWidth2;
                bgLeftWidth = 0.0f;
            }
            if (knob == null) {
                float knobWidthHalf2 = knobBefore == null ? 0.0f : knobBefore.getMinWidth() * 0.5f;
                this.position = (positionWidth - knobWidthHalf2) * percent;
                this.position = Math.min(positionWidth - knobWidthHalf2, this.position);
                knobWidthHalf = knobWidthHalf2;
            } else {
                this.position = (positionWidth - knobWidth) * percent;
                this.position = Math.min(positionWidth - knobWidth, this.position) + bgLeftWidth;
                knobWidthHalf = knobWidth * 0.5f;
            }
            this.position = Math.max(Math.min(0.0f, bgLeftWidth), this.position);
            if (knobBefore != null) {
                if (this.round) {
                    knobBefore.draw(batch, (float) Math.round(x + bgLeftWidth), (float) Math.round(y + ((height - knobBefore.getMinHeight()) * 0.5f)), (float) Math.round(this.position + knobWidthHalf), (float) Math.round(knobBefore.getMinHeight()));
                } else {
                    knobBefore.draw(batch, x + bgLeftWidth, y + ((height - knobBefore.getMinHeight()) * 0.5f), this.position + knobWidthHalf, knobBefore.getMinHeight());
                }
            }
            if (knobAfter != null) {
                if (this.round) {
                    knobAfter.draw(batch, (float) Math.round(x + this.position + knobWidthHalf), (float) Math.round(y + ((height - knobAfter.getMinHeight()) * 0.5f)), (float) Math.round((width - this.position) - knobWidthHalf), (float) Math.round(knobAfter.getMinHeight()));
                } else {
                    knobAfter.draw(batch, x + this.position + knobWidthHalf, y + ((height - knobAfter.getMinHeight()) * 0.5f), (width - this.position) - knobWidthHalf, knobAfter.getMinHeight());
                }
            }
            if (knob == null) {
            } else if (this.round) {
                float f3 = bgLeftWidth;
                knob.draw(batch, (float) Math.round(x + this.position), (float) Math.round(y + ((height - knobHeight) * 0.5f)), (float) Math.round(knobWidth), (float) Math.round(knobHeight));
            } else {
                knob.draw(batch, x + this.position, y + ((height - knobHeight) * 0.5f), knobWidth, knobHeight);
            }
        }
    }

    public float getValue() {
        return this.value;
    }

    public float getVisualValue() {
        float f = this.animateTime;
        if (f > 0.0f) {
            return this.animateInterpolation.apply(this.animateFromValue, this.value, 1.0f - (f / this.animateDuration));
        }
        return this.value;
    }

    public float getPercent() {
        float f = this.min;
        float f2 = this.max;
        if (f == f2) {
            return 0.0f;
        }
        return (this.value - f) / (f2 - f);
    }

    public float getVisualPercent() {
        if (this.min == this.max) {
            return 0.0f;
        }
        Interpolation interpolation = this.visualInterpolation;
        float visualValue = getVisualValue();
        float f = this.min;
        return interpolation.apply((visualValue - f) / (this.max - f));
    }

    /* access modifiers changed from: protected */
    public Drawable getKnobDrawable() {
        return (!this.disabled || this.style.disabledKnob == null) ? this.style.knob : this.style.disabledKnob;
    }

    /* access modifiers changed from: protected */
    public float getKnobPosition() {
        return this.position;
    }

    public boolean setValue(float value2) {
        float value3 = clamp(((float) Math.round(value2 / this.stepSize)) * this.stepSize);
        float oldValue = this.value;
        if (value3 == oldValue) {
            return false;
        }
        float oldVisualValue = getVisualValue();
        this.value = value3;
        ChangeListener.ChangeEvent changeEvent = (ChangeListener.ChangeEvent) Pools.obtain(ChangeListener.ChangeEvent.class);
        boolean cancelled = fire(changeEvent);
        if (cancelled) {
            this.value = oldValue;
        } else {
            float f = this.animateDuration;
            if (f > 0.0f) {
                this.animateFromValue = oldVisualValue;
                this.animateTime = f;
            }
        }
        Pools.free(changeEvent);
        return !cancelled;
    }

    /* access modifiers changed from: protected */
    public float clamp(float value2) {
        return MathUtils.clamp(value2, this.min, this.max);
    }

    public void setRange(float min2, float max2) {
        if (min2 <= max2) {
            this.min = min2;
            this.max = max2;
            float f = this.value;
            if (f < min2) {
                setValue(min2);
            } else if (f > max2) {
                setValue(max2);
            }
        } else {
            throw new IllegalArgumentException("min must be <= max: " + min2 + " <= " + max2);
        }
    }

    public void setStepSize(float stepSize2) {
        if (stepSize2 > 0.0f) {
            this.stepSize = stepSize2;
            return;
        }
        throw new IllegalArgumentException("steps must be > 0: " + stepSize2);
    }

    public float getPrefWidth() {
        if (!this.vertical) {
            return 140.0f;
        }
        Drawable knob = getKnobDrawable();
        Drawable bg = (!this.disabled || this.style.disabledBackground == null) ? this.style.background : this.style.disabledBackground;
        float f = 0.0f;
        float minWidth = knob == null ? 0.0f : knob.getMinWidth();
        if (bg != null) {
            f = bg.getMinWidth();
        }
        return Math.max(minWidth, f);
    }

    public float getPrefHeight() {
        if (this.vertical) {
            return 140.0f;
        }
        Drawable knob = getKnobDrawable();
        Drawable bg = (!this.disabled || this.style.disabledBackground == null) ? this.style.background : this.style.disabledBackground;
        float f = 0.0f;
        float minHeight = knob == null ? 0.0f : knob.getMinHeight();
        if (bg != null) {
            f = bg.getMinHeight();
        }
        return Math.max(minHeight, f);
    }

    public float getMinValue() {
        return this.min;
    }

    public float getMaxValue() {
        return this.max;
    }

    public float getStepSize() {
        return this.stepSize;
    }

    public void setAnimateDuration(float duration) {
        this.animateDuration = duration;
    }

    public void setAnimateInterpolation(Interpolation animateInterpolation2) {
        if (animateInterpolation2 != null) {
            this.animateInterpolation = animateInterpolation2;
            return;
        }
        throw new IllegalArgumentException("animateInterpolation cannot be null.");
    }

    public void setVisualInterpolation(Interpolation interpolation) {
        this.visualInterpolation = interpolation;
    }

    public void setRound(boolean round2) {
        this.round = round2;
    }

    public void setDisabled(boolean disabled2) {
        this.disabled = disabled2;
    }

    public boolean isDisabled() {
        return this.disabled;
    }

    public boolean isVertical() {
        return this.vertical;
    }

    public static class ProgressBarStyle {
        public Drawable background;
        public Drawable disabledBackground;
        public Drawable disabledKnob;
        public Drawable disabledKnobAfter;
        public Drawable disabledKnobBefore;
        public Drawable knob;
        public Drawable knobAfter;
        public Drawable knobBefore;

        public ProgressBarStyle() {
        }

        public ProgressBarStyle(Drawable background2, Drawable knob2) {
            this.background = background2;
            this.knob = knob2;
        }

        public ProgressBarStyle(ProgressBarStyle style) {
            this.background = style.background;
            this.disabledBackground = style.disabledBackground;
            this.knob = style.knob;
            this.disabledKnob = style.disabledKnob;
            this.knobBefore = style.knobBefore;
            this.knobAfter = style.knobAfter;
            this.disabledKnobBefore = style.disabledKnobBefore;
            this.disabledKnobAfter = style.disabledKnobAfter;
        }
    }
}
