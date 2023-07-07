package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Pools;

public class Slider extends ProgressBar {
    int draggingPointer;
    boolean mouseOver;
    private float[] snapValues;
    private float threshold;
    private Interpolation visualInterpolationInverse;

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public Slider(float r8, float r9, float r10, boolean r11, com.badlogic.gdx.scenes.scene2d.ui.Skin r12) {
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
            java.lang.Class<com.badlogic.gdx.scenes.scene2d.ui.Slider$SliderStyle> r1 = com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle.class
            java.lang.Object r0 = r12.get(r0, r1)
            r6 = r0
            com.badlogic.gdx.scenes.scene2d.ui.Slider$SliderStyle r6 = (com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle) r6
            r1 = r7
            r2 = r8
            r3 = r9
            r4 = r10
            r5 = r11
            r1.<init>((float) r2, (float) r3, (float) r4, (boolean) r5, (com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle) r6)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.scenes.scene2d.ui.Slider.<init>(float, float, float, boolean, com.badlogic.gdx.scenes.scene2d.ui.Skin):void");
    }

    public Slider(float min, float max, float stepSize, boolean vertical, Skin skin, String styleName) {
        this(min, max, stepSize, vertical, (SliderStyle) skin.get(styleName, SliderStyle.class));
    }

    public Slider(float min, float max, float stepSize, boolean vertical, SliderStyle style) {
        super(min, max, stepSize, vertical, (ProgressBar.ProgressBarStyle) style);
        this.draggingPointer = -1;
        this.visualInterpolationInverse = Interpolation.linear;
        addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (Slider.this.disabled || Slider.this.draggingPointer != -1) {
                    return false;
                }
                Slider slider = Slider.this;
                slider.draggingPointer = pointer;
                slider.calculatePositionAndValue(x, y);
                return true;
            }

            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (pointer == Slider.this.draggingPointer) {
                    Slider.this.draggingPointer = -1;
                    if (event.isTouchFocusCancel() || !Slider.this.calculatePositionAndValue(x, y)) {
                        ChangeListener.ChangeEvent changeEvent = (ChangeListener.ChangeEvent) Pools.obtain(ChangeListener.ChangeEvent.class);
                        Slider.this.fire(changeEvent);
                        Pools.free(changeEvent);
                    }
                }
            }

            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                Slider.this.calculatePositionAndValue(x, y);
            }

            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    Slider.this.mouseOver = true;
                }
            }

            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (pointer == -1) {
                    Slider.this.mouseOver = false;
                }
            }
        });
    }

    public void setStyle(SliderStyle style) {
        if (style == null) {
            throw new NullPointerException("style cannot be null");
        } else if (style instanceof SliderStyle) {
            super.setStyle(style);
        } else {
            throw new IllegalArgumentException("style must be a SliderStyle.");
        }
    }

    public SliderStyle getStyle() {
        return (SliderStyle) super.getStyle();
    }

    /* access modifiers changed from: protected */
    public Drawable getKnobDrawable() {
        SliderStyle style = getStyle();
        if (this.disabled && style.disabledKnob != null) {
            return style.disabledKnob;
        }
        if (!isDragging() || style.knobDown == null) {
            return (!this.mouseOver || style.knobOver == null) ? style.knob : style.knobOver;
        }
        return style.knobDown;
    }

    /* access modifiers changed from: package-private */
    public boolean calculatePositionAndValue(float x, float y) {
        float value;
        SliderStyle style = getStyle();
        Drawable knob = getKnobDrawable();
        Drawable bg = (!this.disabled || style.disabledBackground == null) ? style.background : style.disabledBackground;
        float oldPosition = this.position;
        float min = getMinValue();
        float max = getMaxValue();
        if (this.vertical) {
            float height = (getHeight() - bg.getTopHeight()) - bg.getBottomHeight();
            float knobHeight = knob == null ? 0.0f : knob.getMinHeight();
            this.position = (y - bg.getBottomHeight()) - (0.5f * knobHeight);
            value = ((max - min) * this.visualInterpolationInverse.apply(this.position / (height - knobHeight))) + min;
            this.position = Math.max(Math.min(0.0f, bg.getBottomHeight()), this.position);
            this.position = Math.min(height - knobHeight, this.position);
        } else {
            float width = (getWidth() - bg.getLeftWidth()) - bg.getRightWidth();
            float knobWidth = knob == null ? 0.0f : knob.getMinWidth();
            this.position = (x - bg.getLeftWidth()) - (0.5f * knobWidth);
            value = ((max - min) * this.visualInterpolationInverse.apply(this.position / (width - knobWidth))) + min;
            this.position = Math.max(Math.min(0.0f, bg.getLeftWidth()), this.position);
            this.position = Math.min(width - knobWidth, this.position);
        }
        float oldValue = value;
        if (!Gdx.input.isKeyPressed(59) && !Gdx.input.isKeyPressed(60)) {
            value = snap(value);
        }
        boolean valueSet = setValue(value);
        if (value == oldValue) {
            this.position = oldPosition;
        }
        return valueSet;
    }

    /* access modifiers changed from: protected */
    public float snap(float value) {
        float[] fArr = this.snapValues;
        if (fArr == null || fArr.length == 0) {
            return value;
        }
        float bestDiff = -1.0f;
        float bestValue = 0.0f;
        int i = 0;
        while (true) {
            float[] fArr2 = this.snapValues;
            if (i >= fArr2.length) {
                break;
            }
            float snapValue = fArr2[i];
            float diff = Math.abs(value - snapValue);
            if (diff <= this.threshold && (bestDiff == -1.0f || diff < bestDiff)) {
                bestDiff = diff;
                bestValue = snapValue;
            }
            i++;
        }
        return bestDiff == -1.0f ? value : bestValue;
    }

    public void setSnapToValues(float[] values, float threshold2) {
        this.snapValues = values;
        this.threshold = threshold2;
    }

    public boolean isDragging() {
        return this.draggingPointer != -1;
    }

    public void setVisualInterpolationInverse(Interpolation interpolation) {
        this.visualInterpolationInverse = interpolation;
    }

    public static class SliderStyle extends ProgressBar.ProgressBarStyle {
        public Drawable knobDown;
        public Drawable knobOver;

        public SliderStyle() {
        }

        public SliderStyle(Drawable background, Drawable knob) {
            super(background, knob);
        }

        public SliderStyle(SliderStyle style) {
            super(style);
            this.knobOver = style.knobOver;
            this.knobDown = style.knobDown;
        }
    }
}
