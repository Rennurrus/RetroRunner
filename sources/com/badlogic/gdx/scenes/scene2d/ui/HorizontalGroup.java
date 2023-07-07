package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.SnapshotArray;

public class HorizontalGroup extends WidgetGroup {
    private int align = 8;
    private boolean expand;
    private float fill;
    private float lastPrefHeight;
    private float padBottom;
    private float padLeft;
    private float padRight;
    private float padTop;
    private float prefHeight;
    private float prefWidth;
    private boolean reverse;
    private boolean round = true;
    private int rowAlign;
    private FloatArray rowSizes;
    private boolean sizeInvalid = true;
    private float space;
    private boolean wrap;
    private float wrapSpace;

    public HorizontalGroup() {
        setTouchable(Touchable.childrenOnly);
    }

    public void invalidate() {
        super.invalidate();
        this.sizeInvalid = true;
    }

    private void computeSize() {
        float width;
        float height;
        float space2;
        int n;
        this.sizeInvalid = false;
        SnapshotArray<Actor> children = getChildren();
        int n2 = children.size;
        this.prefHeight = 0.0f;
        if (this.wrap) {
            this.prefWidth = 0.0f;
            FloatArray floatArray = this.rowSizes;
            if (floatArray == null) {
                this.rowSizes = new FloatArray();
            } else {
                floatArray.clear();
            }
            FloatArray rowSizes2 = this.rowSizes;
            float space3 = this.space;
            float wrapSpace2 = this.wrapSpace;
            float pad = this.padLeft + this.padRight;
            float groupWidth = getWidth() - pad;
            float x = 0.0f;
            float y = 0.0f;
            float rowHeight = 0.0f;
            int i = 0;
            int incr = 1;
            if (this.reverse) {
                i = n2 - 1;
                n2 = -1;
                incr = -1;
            }
            while (i != n2) {
                Actor child = children.get(i);
                if (child instanceof Layout) {
                    Layout layout = (Layout) child;
                    width = layout.getPrefWidth();
                    if (width > groupWidth) {
                        width = Math.max(groupWidth, layout.getMinWidth());
                    }
                    height = layout.getPrefHeight();
                } else {
                    width = child.getWidth();
                    height = child.getHeight();
                }
                float incrX = width + (x > 0.0f ? space3 : 0.0f);
                if (x + incrX <= groupWidth || x <= 0.0f) {
                    n = n2;
                    space2 = space3;
                } else {
                    rowSizes2.add(x);
                    rowSizes2.add(rowHeight);
                    n = n2;
                    space2 = space3;
                    this.prefWidth = Math.max(this.prefWidth, x + pad);
                    if (y > 0.0f) {
                        y += wrapSpace2;
                    }
                    y += rowHeight;
                    rowHeight = 0.0f;
                    x = 0.0f;
                    incrX = width;
                }
                x += incrX;
                rowHeight = Math.max(rowHeight, height);
                i += incr;
                n2 = n;
                space3 = space2;
            }
            int n3 = n2;
            float f = space3;
            rowSizes2.add(x);
            rowSizes2.add(rowHeight);
            this.prefWidth = Math.max(this.prefWidth, x + pad);
            if (y > 0.0f) {
                y += wrapSpace2;
            }
            this.prefHeight = Math.max(this.prefHeight, y + rowHeight);
            int i2 = n3;
        } else {
            this.prefWidth = this.padLeft + this.padRight + (this.space * ((float) (n2 - 1)));
            for (int i3 = 0; i3 < n2; i3++) {
                Actor child2 = children.get(i3);
                if (child2 instanceof Layout) {
                    Layout layout2 = (Layout) child2;
                    this.prefWidth += layout2.getPrefWidth();
                    this.prefHeight = Math.max(this.prefHeight, layout2.getPrefHeight());
                } else {
                    this.prefWidth += child2.getWidth();
                    this.prefHeight = Math.max(this.prefHeight, child2.getHeight());
                }
            }
        }
        this.prefHeight += this.padTop + this.padBottom;
        if (this.round) {
            this.prefWidth = (float) Math.round(this.prefWidth);
            this.prefHeight = (float) Math.round(this.prefHeight);
        }
    }

    public void layout() {
        float startY;
        float height;
        float width;
        float padBottom2;
        float rowHeight;
        float fill2;
        int align2;
        boolean round2;
        if (this.sizeInvalid) {
            computeSize();
        }
        if (this.wrap) {
            layoutWrapped();
            return;
        }
        boolean round3 = this.round;
        int align3 = this.align;
        float space2 = this.space;
        float padBottom3 = this.padBottom;
        float fill3 = this.fill;
        float rowHeight2 = ((this.expand ? getHeight() : this.prefHeight) - this.padTop) - padBottom3;
        float x = this.padLeft;
        if ((align3 & 16) != 0) {
            x += getWidth() - this.prefWidth;
        } else if ((align3 & 8) == 0) {
            x += (getWidth() - this.prefWidth) / 2.0f;
        }
        if ((align3 & 4) != 0) {
            startY = padBottom3;
        } else if ((align3 & 2) != 0) {
            startY = (getHeight() - this.padTop) - rowHeight2;
        } else {
            startY = ((((getHeight() - padBottom3) - this.padTop) - rowHeight2) / 2.0f) + padBottom3;
        }
        int align4 = this.rowAlign;
        SnapshotArray<Actor> children = getChildren();
        int i = 0;
        int n = children.size;
        int incr = 1;
        if (this.reverse) {
            i = n - 1;
            n = -1;
            incr = -1;
        }
        while (i != n) {
            Actor child = children.get(i);
            Layout layout = null;
            if (child instanceof Layout) {
                layout = (Layout) child;
                width = layout.getPrefWidth();
                height = layout.getPrefHeight();
            } else {
                width = child.getWidth();
                height = child.getHeight();
            }
            if (fill3 > 0.0f) {
                height = rowHeight2 * fill3;
            }
            float height2 = height;
            if (layout != null) {
                padBottom2 = padBottom3;
                height2 = Math.max(height2, layout.getMinHeight());
                float maxHeight = layout.getMaxHeight();
                if (maxHeight > 0.0f && height2 > maxHeight) {
                    height2 = maxHeight;
                }
            } else {
                padBottom2 = padBottom3;
            }
            float y = startY;
            if ((align4 & 2) != 0) {
                y += rowHeight2 - height2;
            } else if ((align4 & 4) == 0) {
                y += (rowHeight2 - height2) / 2.0f;
            }
            if (round3) {
                round2 = round3;
                align2 = align4;
                fill2 = fill3;
                rowHeight = rowHeight2;
                child.setBounds((float) Math.round(x), (float) Math.round(y), (float) Math.round(width), (float) Math.round(height2));
            } else {
                round2 = round3;
                align2 = align4;
                fill2 = fill3;
                rowHeight = rowHeight2;
                child.setBounds(x, y, width, height2);
            }
            x += width + space2;
            if (layout != null) {
                layout.validate();
            }
            i += incr;
            padBottom3 = padBottom2;
            round3 = round2;
            align4 = align2;
            fill3 = fill2;
            rowHeight2 = rowHeight;
        }
        int i2 = align4;
        float f = padBottom3;
        float f2 = fill3;
        float f3 = rowHeight2;
    }

    private void layoutWrapped() {
        float x;
        float height;
        float width;
        float maxWidth;
        float fill2;
        int r;
        float wrapSpace2;
        boolean round2;
        int align2;
        float prefHeight2 = getPrefHeight();
        if (prefHeight2 != this.lastPrefHeight) {
            this.lastPrefHeight = prefHeight2;
            invalidateHierarchy();
        }
        int align3 = this.align;
        boolean round3 = this.round;
        float space2 = this.space;
        float padBottom2 = this.padBottom;
        float fill3 = this.fill;
        float wrapSpace3 = this.wrapSpace;
        float maxWidth2 = (this.prefWidth - this.padLeft) - this.padRight;
        float rowY = prefHeight2 - this.padTop;
        float groupWidth = getWidth();
        float xStart = this.padLeft;
        if ((align3 & 2) != 0) {
            rowY += getHeight() - prefHeight2;
        } else if ((align3 & 4) == 0) {
            rowY += (getHeight() - prefHeight2) / 2.0f;
        }
        if ((align3 & 16) != 0) {
            xStart += groupWidth - this.prefWidth;
        } else if ((align3 & 8) == 0) {
            xStart += (groupWidth - this.prefWidth) / 2.0f;
        }
        float groupWidth2 = groupWidth - this.padRight;
        int align4 = this.rowAlign;
        FloatArray rowSizes2 = this.rowSizes;
        SnapshotArray<Actor> children = getChildren();
        int i = 0;
        float f = prefHeight2;
        int n = children.size;
        int incr = 1;
        float f2 = padBottom2;
        if (this.reverse) {
            i = n - 1;
            n = -1;
            incr = -1;
        }
        float f3 = rowY;
        int r2 = 0;
        int i2 = i;
        float rowHeight = 0.0f;
        float x2 = 0.0f;
        float rowY2 = f3;
        while (i2 != n) {
            Actor child = children.get(i2);
            Layout layout = null;
            int n2 = n;
            if ((child instanceof Layout) != 0) {
                layout = (Layout) child;
                width = layout.getPrefWidth();
                if (width > groupWidth2) {
                    float f4 = width;
                    width = Math.max(groupWidth2, layout.getMinWidth());
                } else {
                    float f5 = width;
                }
                height = layout.getPrefHeight();
            } else {
                width = child.getWidth();
                height = child.getHeight();
            }
            if (x + width > groupWidth2 || r2 == 0) {
                x = xStart;
                if ((align4 & 16) != 0) {
                    x += maxWidth2 - rowSizes2.get(r2);
                } else if ((align4 & 8) == 0) {
                    x += (maxWidth2 - rowSizes2.get(r2)) / 2.0f;
                }
                maxWidth = maxWidth2;
                float rowHeight2 = rowSizes2.get(r2 + 1);
                if (r2 > 0) {
                    rowY2 -= wrapSpace3;
                }
                rowY2 -= rowHeight2;
                r2 += 2;
                rowHeight = rowHeight2;
            } else {
                maxWidth = maxWidth2;
            }
            if (fill3 > 0.0f) {
                height = rowHeight * fill3;
            }
            float height2 = height;
            if (layout != null) {
                fill2 = fill3;
                height2 = Math.max(height2, layout.getMinHeight());
                float maxHeight = layout.getMaxHeight();
                if (maxHeight > 0.0f && height2 > maxHeight) {
                    height2 = maxHeight;
                }
            } else {
                fill2 = fill3;
            }
            float y = rowY2;
            if ((align4 & 2) != 0) {
                y += rowHeight - height2;
            } else if ((align4 & 4) == 0) {
                y += (rowHeight - height2) / 2.0f;
            }
            if (round3) {
                align2 = align4;
                round2 = round3;
                wrapSpace2 = wrapSpace3;
                r = r2;
                child.setBounds((float) Math.round(x), (float) Math.round(y), (float) Math.round(width), (float) Math.round(height2));
            } else {
                align2 = align4;
                round2 = round3;
                wrapSpace2 = wrapSpace3;
                r = r2;
                child.setBounds(x, y, width, height2);
            }
            x2 = x + width + space2;
            if (layout != null) {
                layout.validate();
            }
            i2 += incr;
            n = n2;
            fill3 = fill2;
            maxWidth2 = maxWidth;
            align4 = align2;
            round3 = round2;
            wrapSpace3 = wrapSpace2;
            r2 = r;
        }
        int i3 = align4;
        boolean z = round3;
        float f6 = fill3;
        float f7 = wrapSpace3;
        float f8 = maxWidth2;
    }

    public float getPrefWidth() {
        if (this.wrap) {
            return 0.0f;
        }
        if (this.sizeInvalid) {
            computeSize();
        }
        return this.prefWidth;
    }

    public float getPrefHeight() {
        if (this.sizeInvalid) {
            computeSize();
        }
        return this.prefHeight;
    }

    public void setRound(boolean round2) {
        this.round = round2;
    }

    public HorizontalGroup reverse() {
        this.reverse = true;
        return this;
    }

    public HorizontalGroup reverse(boolean reverse2) {
        this.reverse = reverse2;
        return this;
    }

    public boolean getReverse() {
        return this.reverse;
    }

    public HorizontalGroup space(float space2) {
        this.space = space2;
        return this;
    }

    public float getSpace() {
        return this.space;
    }

    public HorizontalGroup wrapSpace(float wrapSpace2) {
        this.wrapSpace = wrapSpace2;
        return this;
    }

    public float getWrapSpace() {
        return this.wrapSpace;
    }

    public HorizontalGroup pad(float pad) {
        this.padTop = pad;
        this.padLeft = pad;
        this.padBottom = pad;
        this.padRight = pad;
        return this;
    }

    public HorizontalGroup pad(float top, float left, float bottom, float right) {
        this.padTop = top;
        this.padLeft = left;
        this.padBottom = bottom;
        this.padRight = right;
        return this;
    }

    public HorizontalGroup padTop(float padTop2) {
        this.padTop = padTop2;
        return this;
    }

    public HorizontalGroup padLeft(float padLeft2) {
        this.padLeft = padLeft2;
        return this;
    }

    public HorizontalGroup padBottom(float padBottom2) {
        this.padBottom = padBottom2;
        return this;
    }

    public HorizontalGroup padRight(float padRight2) {
        this.padRight = padRight2;
        return this;
    }

    public float getPadTop() {
        return this.padTop;
    }

    public float getPadLeft() {
        return this.padLeft;
    }

    public float getPadBottom() {
        return this.padBottom;
    }

    public float getPadRight() {
        return this.padRight;
    }

    public HorizontalGroup align(int align2) {
        this.align = align2;
        return this;
    }

    public HorizontalGroup center() {
        this.align = 1;
        return this;
    }

    public HorizontalGroup top() {
        this.align |= 2;
        this.align &= -5;
        return this;
    }

    public HorizontalGroup left() {
        this.align |= 8;
        this.align &= -17;
        return this;
    }

    public HorizontalGroup bottom() {
        this.align |= 4;
        this.align &= -3;
        return this;
    }

    public HorizontalGroup right() {
        this.align |= 16;
        this.align &= -9;
        return this;
    }

    public int getAlign() {
        return this.align;
    }

    public HorizontalGroup fill() {
        this.fill = 1.0f;
        return this;
    }

    public HorizontalGroup fill(float fill2) {
        this.fill = fill2;
        return this;
    }

    public float getFill() {
        return this.fill;
    }

    public HorizontalGroup expand() {
        this.expand = true;
        return this;
    }

    public HorizontalGroup expand(boolean expand2) {
        this.expand = expand2;
        return this;
    }

    public boolean getExpand() {
        return this.expand;
    }

    public HorizontalGroup grow() {
        this.expand = true;
        this.fill = 1.0f;
        return this;
    }

    public HorizontalGroup wrap() {
        this.wrap = true;
        return this;
    }

    public HorizontalGroup wrap(boolean wrap2) {
        this.wrap = wrap2;
        return this;
    }

    public boolean getWrap() {
        return this.wrap;
    }

    public HorizontalGroup rowAlign(int rowAlign2) {
        this.rowAlign = rowAlign2;
        return this;
    }

    public HorizontalGroup rowCenter() {
        this.rowAlign = 1;
        return this;
    }

    public HorizontalGroup rowTop() {
        this.rowAlign |= 2;
        this.rowAlign &= -5;
        return this;
    }

    public HorizontalGroup rowLeft() {
        this.rowAlign |= 8;
        this.rowAlign &= -17;
        return this;
    }

    public HorizontalGroup rowBottom() {
        this.rowAlign |= 4;
        this.rowAlign &= -3;
        return this;
    }

    public HorizontalGroup rowRight() {
        this.rowAlign |= 16;
        this.rowAlign &= -9;
        return this;
    }

    /* access modifiers changed from: protected */
    public void drawDebugBounds(ShapeRenderer shapes) {
        super.drawDebugBounds(shapes);
        if (getDebug()) {
            shapes.set(ShapeRenderer.ShapeType.Line);
            if (getStage() != null) {
                shapes.setColor(getStage().getDebugColor());
            }
            shapes.rect(getX() + this.padLeft, getY() + this.padBottom, getOriginX(), getOriginY(), (getWidth() - this.padLeft) - this.padRight, (getHeight() - this.padBottom) - this.padTop, getScaleX(), getScaleY(), getRotation());
        }
    }
}
