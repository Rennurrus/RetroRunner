package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.Cullable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;

public class Container<T extends Actor> extends WidgetGroup {
    private T actor;
    private int align;
    private Drawable background;
    private boolean clip;
    private float fillX;
    private float fillY;
    private Value maxHeight;
    private Value maxWidth;
    private Value minHeight;
    private Value minWidth;
    private Value padBottom;
    private Value padLeft;
    private Value padRight;
    private Value padTop;
    private Value prefHeight;
    private Value prefWidth;
    private boolean round;

    public Container() {
        this.minWidth = Value.minWidth;
        this.minHeight = Value.minHeight;
        this.prefWidth = Value.prefWidth;
        this.prefHeight = Value.prefHeight;
        this.maxWidth = Value.zero;
        this.maxHeight = Value.zero;
        this.padTop = Value.zero;
        this.padLeft = Value.zero;
        this.padBottom = Value.zero;
        this.padRight = Value.zero;
        this.round = true;
        setTouchable(Touchable.childrenOnly);
        setTransform(false);
    }

    public Container(T actor2) {
        this();
        setActor(actor2);
    }

    public void draw(Batch batch, float parentAlpha) {
        validate();
        if (isTransform()) {
            applyTransform(batch, computeTransform());
            drawBackground(batch, parentAlpha, 0.0f, 0.0f);
            if (this.clip) {
                batch.flush();
                float padLeft2 = this.padLeft.get(this);
                float padBottom2 = this.padBottom.get(this);
                if (clipBegin(padLeft2, padBottom2, (getWidth() - padLeft2) - this.padRight.get(this), (getHeight() - padBottom2) - this.padTop.get(this))) {
                    drawChildren(batch, parentAlpha);
                    batch.flush();
                    clipEnd();
                }
            } else {
                drawChildren(batch, parentAlpha);
            }
            resetTransform(batch);
            return;
        }
        drawBackground(batch, parentAlpha, getX(), getY());
        super.draw(batch, parentAlpha);
    }

    /* access modifiers changed from: protected */
    public void drawBackground(Batch batch, float parentAlpha, float x, float y) {
        if (this.background != null) {
            Color color = getColor();
            batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
            this.background.draw(batch, x, y, getWidth(), getHeight());
        }
    }

    public void setBackground(Drawable background2) {
        setBackground(background2, true);
    }

    public void setBackground(Drawable background2, boolean adjustPadding) {
        if (this.background != background2) {
            this.background = background2;
            if (adjustPadding) {
                if (background2 == null) {
                    pad((Value) Value.zero);
                } else {
                    pad(background2.getTopHeight(), background2.getLeftWidth(), background2.getBottomHeight(), background2.getRightWidth());
                }
                invalidate();
            }
        }
    }

    public Container<T> background(Drawable background2) {
        setBackground(background2);
        return this;
    }

    public Drawable getBackground() {
        return this.background;
    }

    public void layout() {
        float width;
        float height;
        if (this.actor != null) {
            float padLeft2 = this.padLeft.get(this);
            float padBottom2 = this.padBottom.get(this);
            float containerWidth = (getWidth() - padLeft2) - this.padRight.get(this);
            float containerHeight = (getHeight() - padBottom2) - this.padTop.get(this);
            float minWidth2 = this.minWidth.get(this.actor);
            float minHeight2 = this.minHeight.get(this.actor);
            float prefWidth2 = this.prefWidth.get(this.actor);
            float prefHeight2 = this.prefHeight.get(this.actor);
            float maxWidth2 = this.maxWidth.get(this.actor);
            float maxHeight2 = this.maxHeight.get(this.actor);
            float f = this.fillX;
            if (f > 0.0f) {
                width = f * containerWidth;
            } else {
                width = Math.min(prefWidth2, containerWidth);
            }
            if (width < minWidth2) {
                width = minWidth2;
            }
            if (maxWidth2 > 0.0f && width > maxWidth2) {
                width = maxWidth2;
            }
            float f2 = this.fillY;
            if (f2 > 0.0f) {
                height = f2 * containerHeight;
            } else {
                height = Math.min(prefHeight2, containerHeight);
            }
            if (height < minHeight2) {
                height = minHeight2;
            }
            if (maxHeight2 > 0.0f && height > maxHeight2) {
                height = maxHeight2;
            }
            float x = padLeft2;
            int i = this.align;
            if ((i & 16) != 0) {
                x += containerWidth - width;
            } else if ((i & 8) == 0) {
                x += (containerWidth - width) / 2.0f;
            }
            float y = padBottom2;
            int i2 = this.align;
            if ((i2 & 2) != 0) {
                y += containerHeight - height;
            } else if ((i2 & 4) == 0) {
                y += (containerHeight - height) / 2.0f;
            }
            if (this.round) {
                x = (float) Math.round(x);
                y = (float) Math.round(y);
                width = (float) Math.round(width);
                height = (float) Math.round(height);
            }
            this.actor.setBounds(x, y, width, height);
            T t = this.actor;
            if (t instanceof Layout) {
                ((Layout) t).validate();
            }
        }
    }

    public void setCullingArea(Rectangle cullingArea) {
        super.setCullingArea(cullingArea);
        if (this.fillX == 1.0f && this.fillY == 1.0f) {
            T t = this.actor;
            if (t instanceof Cullable) {
                ((Cullable) t).setCullingArea(cullingArea);
            }
        }
    }

    public void setActor(T actor2) {
        if (actor2 != this) {
            T t = this.actor;
            if (actor2 != t) {
                if (t != null) {
                    super.removeActor(t);
                }
                this.actor = actor2;
                if (actor2 != null) {
                    super.addActor(actor2);
                    return;
                }
                return;
            }
            return;
        }
        throw new IllegalArgumentException("actor cannot be the Container.");
    }

    public T getActor() {
        return this.actor;
    }

    public void addActor(Actor actor2) {
        throw new UnsupportedOperationException("Use Container#setActor.");
    }

    public void addActorAt(int index, Actor actor2) {
        throw new UnsupportedOperationException("Use Container#setActor.");
    }

    public void addActorBefore(Actor actorBefore, Actor actor2) {
        throw new UnsupportedOperationException("Use Container#setActor.");
    }

    public void addActorAfter(Actor actorAfter, Actor actor2) {
        throw new UnsupportedOperationException("Use Container#setActor.");
    }

    public boolean removeActor(Actor actor2) {
        if (actor2 == null) {
            throw new IllegalArgumentException("actor cannot be null.");
        } else if (actor2 != this.actor) {
            return false;
        } else {
            setActor((Actor) null);
            return true;
        }
    }

    public boolean removeActor(Actor actor2, boolean unfocus) {
        if (actor2 == null) {
            throw new IllegalArgumentException("actor cannot be null.");
        } else if (actor2 != this.actor) {
            return false;
        } else {
            this.actor = null;
            return super.removeActor(actor2, unfocus);
        }
    }

    public Container<T> size(Value size) {
        if (size != null) {
            this.minWidth = size;
            this.minHeight = size;
            this.prefWidth = size;
            this.prefHeight = size;
            this.maxWidth = size;
            this.maxHeight = size;
            return this;
        }
        throw new IllegalArgumentException("size cannot be null.");
    }

    public Container<T> size(Value width, Value height) {
        if (width == null) {
            throw new IllegalArgumentException("width cannot be null.");
        } else if (height != null) {
            this.minWidth = width;
            this.minHeight = height;
            this.prefWidth = width;
            this.prefHeight = height;
            this.maxWidth = width;
            this.maxHeight = height;
            return this;
        } else {
            throw new IllegalArgumentException("height cannot be null.");
        }
    }

    public Container<T> size(float size) {
        size((Value) Value.Fixed.valueOf(size));
        return this;
    }

    public Container<T> size(float width, float height) {
        size((Value) Value.Fixed.valueOf(width), (Value) Value.Fixed.valueOf(height));
        return this;
    }

    public Container<T> width(Value width) {
        if (width != null) {
            this.minWidth = width;
            this.prefWidth = width;
            this.maxWidth = width;
            return this;
        }
        throw new IllegalArgumentException("width cannot be null.");
    }

    public Container<T> width(float width) {
        width((Value) Value.Fixed.valueOf(width));
        return this;
    }

    public Container<T> height(Value height) {
        if (height != null) {
            this.minHeight = height;
            this.prefHeight = height;
            this.maxHeight = height;
            return this;
        }
        throw new IllegalArgumentException("height cannot be null.");
    }

    public Container<T> height(float height) {
        height((Value) Value.Fixed.valueOf(height));
        return this;
    }

    public Container<T> minSize(Value size) {
        if (size != null) {
            this.minWidth = size;
            this.minHeight = size;
            return this;
        }
        throw new IllegalArgumentException("size cannot be null.");
    }

    public Container<T> minSize(Value width, Value height) {
        if (width == null) {
            throw new IllegalArgumentException("width cannot be null.");
        } else if (height != null) {
            this.minWidth = width;
            this.minHeight = height;
            return this;
        } else {
            throw new IllegalArgumentException("height cannot be null.");
        }
    }

    public Container<T> minWidth(Value minWidth2) {
        if (minWidth2 != null) {
            this.minWidth = minWidth2;
            return this;
        }
        throw new IllegalArgumentException("minWidth cannot be null.");
    }

    public Container<T> minHeight(Value minHeight2) {
        if (minHeight2 != null) {
            this.minHeight = minHeight2;
            return this;
        }
        throw new IllegalArgumentException("minHeight cannot be null.");
    }

    public Container<T> minSize(float size) {
        minSize((Value) Value.Fixed.valueOf(size));
        return this;
    }

    public Container<T> minSize(float width, float height) {
        minSize((Value) Value.Fixed.valueOf(width), (Value) Value.Fixed.valueOf(height));
        return this;
    }

    public Container<T> minWidth(float minWidth2) {
        this.minWidth = Value.Fixed.valueOf(minWidth2);
        return this;
    }

    public Container<T> minHeight(float minHeight2) {
        this.minHeight = Value.Fixed.valueOf(minHeight2);
        return this;
    }

    public Container<T> prefSize(Value size) {
        if (size != null) {
            this.prefWidth = size;
            this.prefHeight = size;
            return this;
        }
        throw new IllegalArgumentException("size cannot be null.");
    }

    public Container<T> prefSize(Value width, Value height) {
        if (width == null) {
            throw new IllegalArgumentException("width cannot be null.");
        } else if (height != null) {
            this.prefWidth = width;
            this.prefHeight = height;
            return this;
        } else {
            throw new IllegalArgumentException("height cannot be null.");
        }
    }

    public Container<T> prefWidth(Value prefWidth2) {
        if (prefWidth2 != null) {
            this.prefWidth = prefWidth2;
            return this;
        }
        throw new IllegalArgumentException("prefWidth cannot be null.");
    }

    public Container<T> prefHeight(Value prefHeight2) {
        if (prefHeight2 != null) {
            this.prefHeight = prefHeight2;
            return this;
        }
        throw new IllegalArgumentException("prefHeight cannot be null.");
    }

    public Container<T> prefSize(float width, float height) {
        prefSize((Value) Value.Fixed.valueOf(width), (Value) Value.Fixed.valueOf(height));
        return this;
    }

    public Container<T> prefSize(float size) {
        prefSize((Value) Value.Fixed.valueOf(size));
        return this;
    }

    public Container<T> prefWidth(float prefWidth2) {
        this.prefWidth = Value.Fixed.valueOf(prefWidth2);
        return this;
    }

    public Container<T> prefHeight(float prefHeight2) {
        this.prefHeight = Value.Fixed.valueOf(prefHeight2);
        return this;
    }

    public Container<T> maxSize(Value size) {
        if (size != null) {
            this.maxWidth = size;
            this.maxHeight = size;
            return this;
        }
        throw new IllegalArgumentException("size cannot be null.");
    }

    public Container<T> maxSize(Value width, Value height) {
        if (width == null) {
            throw new IllegalArgumentException("width cannot be null.");
        } else if (height != null) {
            this.maxWidth = width;
            this.maxHeight = height;
            return this;
        } else {
            throw new IllegalArgumentException("height cannot be null.");
        }
    }

    public Container<T> maxWidth(Value maxWidth2) {
        if (maxWidth2 != null) {
            this.maxWidth = maxWidth2;
            return this;
        }
        throw new IllegalArgumentException("maxWidth cannot be null.");
    }

    public Container<T> maxHeight(Value maxHeight2) {
        if (maxHeight2 != null) {
            this.maxHeight = maxHeight2;
            return this;
        }
        throw new IllegalArgumentException("maxHeight cannot be null.");
    }

    public Container<T> maxSize(float size) {
        maxSize((Value) Value.Fixed.valueOf(size));
        return this;
    }

    public Container<T> maxSize(float width, float height) {
        maxSize((Value) Value.Fixed.valueOf(width), (Value) Value.Fixed.valueOf(height));
        return this;
    }

    public Container<T> maxWidth(float maxWidth2) {
        this.maxWidth = Value.Fixed.valueOf(maxWidth2);
        return this;
    }

    public Container<T> maxHeight(float maxHeight2) {
        this.maxHeight = Value.Fixed.valueOf(maxHeight2);
        return this;
    }

    public Container<T> pad(Value pad) {
        if (pad != null) {
            this.padTop = pad;
            this.padLeft = pad;
            this.padBottom = pad;
            this.padRight = pad;
            return this;
        }
        throw new IllegalArgumentException("pad cannot be null.");
    }

    public Container<T> pad(Value top, Value left, Value bottom, Value right) {
        if (top == null) {
            throw new IllegalArgumentException("top cannot be null.");
        } else if (left == null) {
            throw new IllegalArgumentException("left cannot be null.");
        } else if (bottom == null) {
            throw new IllegalArgumentException("bottom cannot be null.");
        } else if (right != null) {
            this.padTop = top;
            this.padLeft = left;
            this.padBottom = bottom;
            this.padRight = right;
            return this;
        } else {
            throw new IllegalArgumentException("right cannot be null.");
        }
    }

    public Container<T> padTop(Value padTop2) {
        if (padTop2 != null) {
            this.padTop = padTop2;
            return this;
        }
        throw new IllegalArgumentException("padTop cannot be null.");
    }

    public Container<T> padLeft(Value padLeft2) {
        if (padLeft2 != null) {
            this.padLeft = padLeft2;
            return this;
        }
        throw new IllegalArgumentException("padLeft cannot be null.");
    }

    public Container<T> padBottom(Value padBottom2) {
        if (padBottom2 != null) {
            this.padBottom = padBottom2;
            return this;
        }
        throw new IllegalArgumentException("padBottom cannot be null.");
    }

    public Container<T> padRight(Value padRight2) {
        if (padRight2 != null) {
            this.padRight = padRight2;
            return this;
        }
        throw new IllegalArgumentException("padRight cannot be null.");
    }

    public Container<T> pad(float pad) {
        Value value = Value.Fixed.valueOf(pad);
        this.padTop = value;
        this.padLeft = value;
        this.padBottom = value;
        this.padRight = value;
        return this;
    }

    public Container<T> pad(float top, float left, float bottom, float right) {
        this.padTop = Value.Fixed.valueOf(top);
        this.padLeft = Value.Fixed.valueOf(left);
        this.padBottom = Value.Fixed.valueOf(bottom);
        this.padRight = Value.Fixed.valueOf(right);
        return this;
    }

    public Container<T> padTop(float padTop2) {
        this.padTop = Value.Fixed.valueOf(padTop2);
        return this;
    }

    public Container<T> padLeft(float padLeft2) {
        this.padLeft = Value.Fixed.valueOf(padLeft2);
        return this;
    }

    public Container<T> padBottom(float padBottom2) {
        this.padBottom = Value.Fixed.valueOf(padBottom2);
        return this;
    }

    public Container<T> padRight(float padRight2) {
        this.padRight = Value.Fixed.valueOf(padRight2);
        return this;
    }

    public Container<T> fill() {
        this.fillX = 1.0f;
        this.fillY = 1.0f;
        return this;
    }

    public Container<T> fillX() {
        this.fillX = 1.0f;
        return this;
    }

    public Container<T> fillY() {
        this.fillY = 1.0f;
        return this;
    }

    public Container<T> fill(float x, float y) {
        this.fillX = x;
        this.fillY = y;
        return this;
    }

    public Container<T> fill(boolean x, boolean y) {
        float f = 1.0f;
        this.fillX = x ? 1.0f : 0.0f;
        if (!y) {
            f = 0.0f;
        }
        this.fillY = f;
        return this;
    }

    public Container<T> fill(boolean fill) {
        float f = 1.0f;
        this.fillX = fill ? 1.0f : 0.0f;
        if (!fill) {
            f = 0.0f;
        }
        this.fillY = f;
        return this;
    }

    public Container<T> align(int align2) {
        this.align = align2;
        return this;
    }

    public Container<T> center() {
        this.align = 1;
        return this;
    }

    public Container<T> top() {
        this.align |= 2;
        this.align &= -5;
        return this;
    }

    public Container<T> left() {
        this.align |= 8;
        this.align &= -17;
        return this;
    }

    public Container<T> bottom() {
        this.align |= 4;
        this.align &= -3;
        return this;
    }

    public Container<T> right() {
        this.align |= 16;
        this.align &= -9;
        return this;
    }

    public float getMinWidth() {
        return this.minWidth.get(this.actor) + this.padLeft.get(this) + this.padRight.get(this);
    }

    public Value getMinHeightValue() {
        return this.minHeight;
    }

    public float getMinHeight() {
        return this.minHeight.get(this.actor) + this.padTop.get(this) + this.padBottom.get(this);
    }

    public Value getPrefWidthValue() {
        return this.prefWidth;
    }

    public float getPrefWidth() {
        float v = this.prefWidth.get(this.actor);
        Drawable drawable = this.background;
        if (drawable != null) {
            v = Math.max(v, drawable.getMinWidth());
        }
        return Math.max(getMinWidth(), this.padLeft.get(this) + v + this.padRight.get(this));
    }

    public Value getPrefHeightValue() {
        return this.prefHeight;
    }

    public float getPrefHeight() {
        float v = this.prefHeight.get(this.actor);
        Drawable drawable = this.background;
        if (drawable != null) {
            v = Math.max(v, drawable.getMinHeight());
        }
        return Math.max(getMinHeight(), this.padTop.get(this) + v + this.padBottom.get(this));
    }

    public Value getMaxWidthValue() {
        return this.maxWidth;
    }

    public float getMaxWidth() {
        float v = this.maxWidth.get(this.actor);
        if (v > 0.0f) {
            return v + this.padLeft.get(this) + this.padRight.get(this);
        }
        return v;
    }

    public Value getMaxHeightValue() {
        return this.maxHeight;
    }

    public float getMaxHeight() {
        float v = this.maxHeight.get(this.actor);
        if (v > 0.0f) {
            return v + this.padTop.get(this) + this.padBottom.get(this);
        }
        return v;
    }

    public Value getPadTopValue() {
        return this.padTop;
    }

    public float getPadTop() {
        return this.padTop.get(this);
    }

    public Value getPadLeftValue() {
        return this.padLeft;
    }

    public float getPadLeft() {
        return this.padLeft.get(this);
    }

    public Value getPadBottomValue() {
        return this.padBottom;
    }

    public float getPadBottom() {
        return this.padBottom.get(this);
    }

    public Value getPadRightValue() {
        return this.padRight;
    }

    public float getPadRight() {
        return this.padRight.get(this);
    }

    public float getPadX() {
        return this.padLeft.get(this) + this.padRight.get(this);
    }

    public float getPadY() {
        return this.padTop.get(this) + this.padBottom.get(this);
    }

    public float getFillX() {
        return this.fillX;
    }

    public float getFillY() {
        return this.fillY;
    }

    public int getAlign() {
        return this.align;
    }

    public void setRound(boolean round2) {
        this.round = round2;
    }

    public void setClip(boolean enabled) {
        this.clip = enabled;
        setTransform(enabled);
        invalidate();
    }

    public boolean getClip() {
        return this.clip;
    }

    public Actor hit(float x, float y, boolean touchable) {
        if (!this.clip || ((!touchable || getTouchable() != Touchable.disabled) && x >= 0.0f && x < getWidth() && y >= 0.0f && y < getHeight())) {
            return super.hit(x, y, touchable);
        }
        return null;
    }

    public void drawDebug(ShapeRenderer shapes) {
        boolean draw;
        validate();
        if (isTransform()) {
            applyTransform(shapes, computeTransform());
            if (this.clip) {
                shapes.flush();
                float padLeft2 = this.padLeft.get(this);
                float padBottom2 = this.padBottom.get(this);
                if (this.background == null) {
                    draw = clipBegin(0.0f, 0.0f, getWidth(), getHeight());
                } else {
                    draw = clipBegin(padLeft2, padBottom2, (getWidth() - padLeft2) - this.padRight.get(this), (getHeight() - padBottom2) - this.padTop.get(this));
                }
                if (draw) {
                    drawDebugChildren(shapes);
                    clipEnd();
                }
            } else {
                drawDebugChildren(shapes);
            }
            resetTransform(shapes);
            return;
        }
        super.drawDebug(shapes);
    }
}
