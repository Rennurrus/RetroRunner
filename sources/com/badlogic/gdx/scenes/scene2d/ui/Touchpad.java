package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Pools;

public class Touchpad extends Widget {
    private final Circle deadzoneBounds;
    private float deadzoneRadius;
    private final Circle knobBounds;
    private final Vector2 knobPercent;
    private final Vector2 knobPosition;
    boolean resetOnTouchUp;
    private TouchpadStyle style;
    private final Circle touchBounds;
    boolean touched;

    public Touchpad(float deadzoneRadius2, Skin skin) {
        this(deadzoneRadius2, (TouchpadStyle) skin.get(TouchpadStyle.class));
    }

    public Touchpad(float deadzoneRadius2, Skin skin, String styleName) {
        this(deadzoneRadius2, (TouchpadStyle) skin.get(styleName, TouchpadStyle.class));
    }

    public Touchpad(float deadzoneRadius2, TouchpadStyle style2) {
        this.resetOnTouchUp = true;
        this.knobBounds = new Circle(0.0f, 0.0f, 0.0f);
        this.touchBounds = new Circle(0.0f, 0.0f, 0.0f);
        this.deadzoneBounds = new Circle(0.0f, 0.0f, 0.0f);
        this.knobPosition = new Vector2();
        this.knobPercent = new Vector2();
        if (deadzoneRadius2 >= 0.0f) {
            this.deadzoneRadius = deadzoneRadius2;
            this.knobPosition.set(getWidth() / 2.0f, getHeight() / 2.0f);
            setStyle(style2);
            setSize(getPrefWidth(), getPrefHeight());
            addListener(new InputListener() {
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    if (Touchpad.this.touched) {
                        return false;
                    }
                    Touchpad touchpad = Touchpad.this;
                    touchpad.touched = true;
                    touchpad.calculatePositionAndValue(x, y, false);
                    return true;
                }

                public void touchDragged(InputEvent event, float x, float y, int pointer) {
                    Touchpad.this.calculatePositionAndValue(x, y, false);
                }

                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    Touchpad touchpad = Touchpad.this;
                    touchpad.touched = false;
                    touchpad.calculatePositionAndValue(x, y, touchpad.resetOnTouchUp);
                }
            });
            return;
        }
        throw new IllegalArgumentException("deadzoneRadius must be > 0");
    }

    /* access modifiers changed from: package-private */
    public void calculatePositionAndValue(float x, float y, boolean isTouchUp) {
        float oldPositionX = this.knobPosition.x;
        float oldPositionY = this.knobPosition.y;
        float oldPercentX = this.knobPercent.x;
        float oldPercentY = this.knobPercent.y;
        float centerX = this.knobBounds.x;
        float centerY = this.knobBounds.y;
        this.knobPosition.set(centerX, centerY);
        this.knobPercent.set(0.0f, 0.0f);
        if (!isTouchUp && !this.deadzoneBounds.contains(x, y)) {
            this.knobPercent.set((x - centerX) / this.knobBounds.radius, (y - centerY) / this.knobBounds.radius);
            float length = this.knobPercent.len();
            if (length > 1.0f) {
                this.knobPercent.scl(1.0f / length);
            }
            if (this.knobBounds.contains(x, y)) {
                this.knobPosition.set(x, y);
            } else {
                this.knobPosition.set(this.knobPercent).nor().scl(this.knobBounds.radius).add(this.knobBounds.x, this.knobBounds.y);
            }
        }
        if (oldPercentX != this.knobPercent.x || oldPercentY != this.knobPercent.y) {
            ChangeListener.ChangeEvent changeEvent = (ChangeListener.ChangeEvent) Pools.obtain(ChangeListener.ChangeEvent.class);
            if (fire(changeEvent)) {
                this.knobPercent.set(oldPercentX, oldPercentY);
                this.knobPosition.set(oldPositionX, oldPositionY);
            }
            Pools.free(changeEvent);
        }
    }

    public void setStyle(TouchpadStyle style2) {
        if (style2 != null) {
            this.style = style2;
            invalidateHierarchy();
            return;
        }
        throw new IllegalArgumentException("style cannot be null");
    }

    public TouchpadStyle getStyle() {
        return this.style;
    }

    public Actor hit(float x, float y, boolean touchable) {
        if ((!touchable || getTouchable() == Touchable.enabled) && isVisible() && this.touchBounds.contains(x, y)) {
            return this;
        }
        return null;
    }

    public void layout() {
        float halfWidth = getWidth() / 2.0f;
        float halfHeight = getHeight() / 2.0f;
        float radius = Math.min(halfWidth, halfHeight);
        this.touchBounds.set(halfWidth, halfHeight, radius);
        if (this.style.knob != null) {
            radius -= Math.max(this.style.knob.getMinWidth(), this.style.knob.getMinHeight()) / 2.0f;
        }
        this.knobBounds.set(halfWidth, halfHeight, radius);
        this.deadzoneBounds.set(halfWidth, halfHeight, this.deadzoneRadius);
        this.knobPosition.set(halfWidth, halfHeight);
        this.knobPercent.set(0.0f, 0.0f);
    }

    public void draw(Batch batch, float parentAlpha) {
        validate();
        Color c = getColor();
        batch.setColor(c.r, c.g, c.b, c.a * parentAlpha);
        float x = getX();
        float y = getY();
        float w = getWidth();
        float h = getHeight();
        Drawable bg = this.style.background;
        if (bg != null) {
            bg.draw(batch, x, y, w, h);
        }
        Drawable knob = this.style.knob;
        if (knob != null) {
            float x2 = x + (this.knobPosition.x - (knob.getMinWidth() / 2.0f));
            knob.draw(batch, x2, y + (this.knobPosition.y - (knob.getMinHeight() / 2.0f)), knob.getMinWidth(), knob.getMinHeight());
        }
    }

    public float getPrefWidth() {
        if (this.style.background != null) {
            return this.style.background.getMinWidth();
        }
        return 0.0f;
    }

    public float getPrefHeight() {
        if (this.style.background != null) {
            return this.style.background.getMinHeight();
        }
        return 0.0f;
    }

    public boolean isTouched() {
        return this.touched;
    }

    public boolean getResetOnTouchUp() {
        return this.resetOnTouchUp;
    }

    public void setResetOnTouchUp(boolean reset) {
        this.resetOnTouchUp = reset;
    }

    public void setDeadzone(float deadzoneRadius2) {
        if (deadzoneRadius2 >= 0.0f) {
            this.deadzoneRadius = deadzoneRadius2;
            invalidate();
            return;
        }
        throw new IllegalArgumentException("deadzoneRadius must be > 0");
    }

    public float getKnobX() {
        return this.knobPosition.x;
    }

    public float getKnobY() {
        return this.knobPosition.y;
    }

    public float getKnobPercentX() {
        return this.knobPercent.x;
    }

    public float getKnobPercentY() {
        return this.knobPercent.y;
    }

    public static class TouchpadStyle {
        public Drawable background;
        public Drawable knob;

        public TouchpadStyle() {
        }

        public TouchpadStyle(Drawable background2, Drawable knob2) {
            this.background = background2;
            this.knob = knob2;
        }

        public TouchpadStyle(TouchpadStyle style) {
            this.background = style.background;
            this.knob = style.knob;
        }
    }
}
