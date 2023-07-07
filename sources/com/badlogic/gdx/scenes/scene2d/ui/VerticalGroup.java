package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.SnapshotArray;

public class VerticalGroup extends WidgetGroup {
    private int align = 2;
    private int columnAlign;
    private FloatArray columnSizes;
    private boolean expand;
    private float fill;
    private float lastPrefWidth;
    private float padBottom;
    private float padLeft;
    private float padRight;
    private float padTop;
    private float prefHeight;
    private float prefWidth;
    private boolean reverse;
    private boolean round = true;
    private boolean sizeInvalid = true;
    private float space;
    private boolean wrap;
    private float wrapSpace;

    public VerticalGroup() {
        setTouchable(Touchable.childrenOnly);
    }

    public void invalidate() {
        super.invalidate();
        this.sizeInvalid = true;
    }

    private void computeSize() {
        float height;
        float width;
        float space2;
        int n;
        this.sizeInvalid = false;
        SnapshotArray<Actor> children = getChildren();
        int n2 = children.size;
        this.prefWidth = 0.0f;
        if (this.wrap) {
            this.prefHeight = 0.0f;
            FloatArray floatArray = this.columnSizes;
            if (floatArray == null) {
                this.columnSizes = new FloatArray();
            } else {
                floatArray.clear();
            }
            FloatArray columnSizes2 = this.columnSizes;
            float space3 = this.space;
            float wrapSpace2 = this.wrapSpace;
            float pad = this.padTop + this.padBottom;
            float groupHeight = getHeight() - pad;
            float x = 0.0f;
            float y = 0.0f;
            float columnWidth = 0.0f;
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
                    float width2 = layout.getPrefWidth();
                    height = layout.getPrefHeight();
                    if (height > groupHeight) {
                        height = Math.max(groupHeight, layout.getMinHeight());
                    }
                    width = width2;
                } else {
                    float width3 = child.getWidth();
                    height = child.getHeight();
                    width = width3;
                }
                float incrY = height + (y > 0.0f ? space3 : 0.0f);
                if (y + incrY <= groupHeight || y <= 0.0f) {
                    n = n2;
                    space2 = space3;
                } else {
                    columnSizes2.add(y);
                    columnSizes2.add(columnWidth);
                    n = n2;
                    space2 = space3;
                    this.prefHeight = Math.max(this.prefHeight, y + pad);
                    if (x > 0.0f) {
                        x += wrapSpace2;
                    }
                    x += columnWidth;
                    columnWidth = 0.0f;
                    y = 0.0f;
                    incrY = height;
                }
                y += incrY;
                columnWidth = Math.max(columnWidth, width);
                i += incr;
                n2 = n;
                space3 = space2;
            }
            float f = space3;
            columnSizes2.add(y);
            columnSizes2.add(columnWidth);
            this.prefHeight = Math.max(this.prefHeight, y + pad);
            if (x > 0.0f) {
                x += wrapSpace2;
            }
            this.prefWidth = Math.max(this.prefWidth, x + columnWidth);
        } else {
            this.prefHeight = this.padTop + this.padBottom + (this.space * ((float) (n2 - 1)));
            for (int i2 = 0; i2 < n2; i2++) {
                Actor child2 = children.get(i2);
                if (child2 instanceof Layout) {
                    Layout layout2 = (Layout) child2;
                    this.prefWidth = Math.max(this.prefWidth, layout2.getPrefWidth());
                    this.prefHeight += layout2.getPrefHeight();
                } else {
                    this.prefWidth = Math.max(this.prefWidth, child2.getWidth());
                    this.prefHeight += child2.getHeight();
                }
            }
            int i3 = n2;
        }
        this.prefWidth += this.padLeft + this.padRight;
        if (this.round) {
            this.prefWidth = (float) Math.round(this.prefWidth);
            this.prefHeight = (float) Math.round(this.prefHeight);
        }
    }

    public void layout() {
        float startX;
        float width;
        float height;
        float padLeft2;
        float fill2;
        float space2;
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
        float space3 = this.space;
        float padLeft3 = this.padLeft;
        float fill3 = this.fill;
        float columnWidth = ((this.expand ? getWidth() : this.prefWidth) - padLeft3) - this.padRight;
        float y = (this.prefHeight - this.padTop) + space3;
        if ((align3 & 2) != 0) {
            y += getHeight() - this.prefHeight;
        } else if ((align3 & 4) == 0) {
            y += (getHeight() - this.prefHeight) / 2.0f;
        }
        if ((align3 & 8) != 0) {
            startX = padLeft3;
        } else if ((align3 & 16) != 0) {
            startX = (getWidth() - this.padRight) - columnWidth;
        } else {
            startX = ((((getWidth() - padLeft3) - this.padRight) - columnWidth) / 2.0f) + padLeft3;
        }
        int align4 = this.columnAlign;
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
                width = columnWidth * fill3;
            }
            if (layout != null) {
                padLeft2 = padLeft3;
                width = Math.max(width, layout.getMinWidth());
                float maxWidth = layout.getMaxWidth();
                if (maxWidth > 0.0f && width > maxWidth) {
                    width = maxWidth;
                }
            } else {
                padLeft2 = padLeft3;
            }
            float x = startX;
            if ((align4 & 16) != 0) {
                x += columnWidth - width;
            } else if ((align4 & 8) == 0) {
                x += (columnWidth - width) / 2.0f;
            }
            y -= height + space3;
            if (round3) {
                round2 = round3;
                align2 = align4;
                space2 = space3;
                fill2 = fill3;
                child.setBounds((float) Math.round(x), (float) Math.round(y), (float) Math.round(width), (float) Math.round(height));
            } else {
                round2 = round3;
                align2 = align4;
                space2 = space3;
                fill2 = fill3;
                child.setBounds(x, y, width, height);
            }
            if (layout != null) {
                layout.validate();
            }
            i += incr;
            round3 = round2;
            padLeft3 = padLeft2;
            align4 = align2;
            space3 = space2;
            fill3 = fill2;
        }
        int i2 = align4;
        float f = space3;
        float f2 = padLeft3;
        float f3 = fill3;
    }

    private void layoutWrapped() {
        float y;
        float width;
        float width2;
        float fill2;
        float space2;
        boolean round2;
        int align2;
        VerticalGroup verticalGroup = this;
        float prefWidth2 = getPrefWidth();
        if (prefWidth2 != verticalGroup.lastPrefWidth) {
            verticalGroup.lastPrefWidth = prefWidth2;
            invalidateHierarchy();
        }
        int align3 = verticalGroup.align;
        boolean round3 = verticalGroup.round;
        float space3 = verticalGroup.space;
        float padLeft2 = verticalGroup.padLeft;
        float fill3 = verticalGroup.fill;
        float wrapSpace2 = verticalGroup.wrapSpace;
        float maxHeight = (verticalGroup.prefHeight - verticalGroup.padTop) - verticalGroup.padBottom;
        float columnX = padLeft2;
        float groupHeight = getHeight();
        float yStart = (verticalGroup.prefHeight - verticalGroup.padTop) + space3;
        if ((align3 & 16) != 0) {
            columnX += getWidth() - prefWidth2;
        } else if ((align3 & 8) == 0) {
            columnX += (getWidth() - prefWidth2) / 2.0f;
        }
        if ((align3 & 2) != 0) {
            yStart += groupHeight - verticalGroup.prefHeight;
        } else if ((align3 & 4) == 0) {
            yStart += (groupHeight - verticalGroup.prefHeight) / 2.0f;
        }
        float groupHeight2 = groupHeight - verticalGroup.padTop;
        int align4 = verticalGroup.columnAlign;
        FloatArray columnSizes2 = verticalGroup.columnSizes;
        SnapshotArray<Actor> children = getChildren();
        int i = 0;
        float f = prefWidth2;
        int n = children.size;
        int incr = 1;
        float f2 = padLeft2;
        if (verticalGroup.reverse) {
            i = n - 1;
            n = -1;
            incr = -1;
        }
        float f3 = columnX;
        int r = 0;
        int i2 = i;
        float columnWidth = 0.0f;
        float y2 = 0.0f;
        float columnX2 = f3;
        while (i2 != n) {
            int n2 = n;
            Actor child = children.get(i2);
            Layout layout = null;
            SnapshotArray<Actor> children2 = children;
            if (child instanceof Layout) {
                layout = (Layout) child;
                float width3 = layout.getPrefWidth();
                float height = layout.getPrefHeight();
                if (height > groupHeight2) {
                    width = width3;
                    height = Math.max(groupHeight2, layout.getMinHeight());
                } else {
                    width = width3;
                }
                width2 = height;
            } else {
                width = child.getWidth();
                width2 = child.getHeight();
            }
            float groupHeight3 = groupHeight2;
            if ((y - width2) - space3 < verticalGroup.padBottom || r == 0) {
                float y3 = yStart;
                if ((align4 & 4) != 0) {
                    y = y3 - (maxHeight - columnSizes2.get(r));
                } else if ((align4 & 2) == 0) {
                    y = y3 - ((maxHeight - columnSizes2.get(r)) / 2.0f);
                } else {
                    y = y3;
                }
                if (r > 0) {
                    columnX2 = columnX2 + wrapSpace2 + columnWidth;
                }
                r += 2;
                columnWidth = columnSizes2.get(r + 1);
            }
            if (fill3 > 0.0f) {
                width = columnWidth * fill3;
            }
            float width4 = width;
            if (layout != null) {
                width4 = Math.max(width4, layout.getMinWidth());
                float maxWidth = layout.getMaxWidth();
                if (maxWidth > 0.0f && width4 > maxWidth) {
                    width4 = maxWidth;
                }
            }
            float x = columnX2;
            if ((align4 & 16) != 0) {
                x += columnWidth - width4;
            } else if ((align4 & 8) == 0) {
                x += (columnWidth - width4) / 2.0f;
            }
            y2 = y - (width2 + space3);
            if (round3) {
                align2 = align4;
                round2 = round3;
                space2 = space3;
                fill2 = fill3;
                child.setBounds((float) Math.round(x), (float) Math.round(y2), (float) Math.round(width4), (float) Math.round(width2));
            } else {
                align2 = align4;
                round2 = round3;
                space2 = space3;
                fill2 = fill3;
                child.setBounds(x, y2, width4, width2);
            }
            if (layout != null) {
                layout.validate();
            }
            i2 += incr;
            verticalGroup = this;
            n = n2;
            children = children2;
            align4 = align2;
            round3 = round2;
            groupHeight2 = groupHeight3;
            space3 = space2;
            fill3 = fill2;
        }
        int i3 = align4;
        boolean z = round3;
        float f4 = space3;
        float f5 = fill3;
        float f6 = groupHeight2;
        SnapshotArray<Actor> snapshotArray = children;
    }

    public float getPrefWidth() {
        if (this.sizeInvalid) {
            computeSize();
        }
        return this.prefWidth;
    }

    public float getPrefHeight() {
        if (this.wrap) {
            return 0.0f;
        }
        if (this.sizeInvalid) {
            computeSize();
        }
        return this.prefHeight;
    }

    public void setRound(boolean round2) {
        this.round = round2;
    }

    public VerticalGroup reverse() {
        this.reverse = true;
        return this;
    }

    public VerticalGroup reverse(boolean reverse2) {
        this.reverse = reverse2;
        return this;
    }

    public boolean getReverse() {
        return this.reverse;
    }

    public VerticalGroup space(float space2) {
        this.space = space2;
        return this;
    }

    public float getSpace() {
        return this.space;
    }

    public VerticalGroup wrapSpace(float wrapSpace2) {
        this.wrapSpace = wrapSpace2;
        return this;
    }

    public float getWrapSpace() {
        return this.wrapSpace;
    }

    public VerticalGroup pad(float pad) {
        this.padTop = pad;
        this.padLeft = pad;
        this.padBottom = pad;
        this.padRight = pad;
        return this;
    }

    public VerticalGroup pad(float top, float left, float bottom, float right) {
        this.padTop = top;
        this.padLeft = left;
        this.padBottom = bottom;
        this.padRight = right;
        return this;
    }

    public VerticalGroup padTop(float padTop2) {
        this.padTop = padTop2;
        return this;
    }

    public VerticalGroup padLeft(float padLeft2) {
        this.padLeft = padLeft2;
        return this;
    }

    public VerticalGroup padBottom(float padBottom2) {
        this.padBottom = padBottom2;
        return this;
    }

    public VerticalGroup padRight(float padRight2) {
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

    public VerticalGroup align(int align2) {
        this.align = align2;
        return this;
    }

    public VerticalGroup center() {
        this.align = 1;
        return this;
    }

    public VerticalGroup top() {
        this.align |= 2;
        this.align &= -5;
        return this;
    }

    public VerticalGroup left() {
        this.align |= 8;
        this.align &= -17;
        return this;
    }

    public VerticalGroup bottom() {
        this.align |= 4;
        this.align &= -3;
        return this;
    }

    public VerticalGroup right() {
        this.align |= 16;
        this.align &= -9;
        return this;
    }

    public int getAlign() {
        return this.align;
    }

    public VerticalGroup fill() {
        this.fill = 1.0f;
        return this;
    }

    public VerticalGroup fill(float fill2) {
        this.fill = fill2;
        return this;
    }

    public float getFill() {
        return this.fill;
    }

    public VerticalGroup expand() {
        this.expand = true;
        return this;
    }

    public VerticalGroup expand(boolean expand2) {
        this.expand = expand2;
        return this;
    }

    public boolean getExpand() {
        return this.expand;
    }

    public VerticalGroup grow() {
        this.expand = true;
        this.fill = 1.0f;
        return this;
    }

    public VerticalGroup wrap() {
        this.wrap = true;
        return this;
    }

    public VerticalGroup wrap(boolean wrap2) {
        this.wrap = wrap2;
        return this;
    }

    public boolean getWrap() {
        return this.wrap;
    }

    public VerticalGroup columnAlign(int columnAlign2) {
        this.columnAlign = columnAlign2;
        return this;
    }

    public VerticalGroup columnCenter() {
        this.columnAlign = 1;
        return this;
    }

    public VerticalGroup columnTop() {
        this.columnAlign |= 2;
        this.columnAlign &= -5;
        return this;
    }

    public VerticalGroup columnLeft() {
        this.columnAlign |= 8;
        this.columnAlign &= -17;
        return this;
    }

    public VerticalGroup columnBottom() {
        this.columnAlign |= 4;
        this.columnAlign &= -3;
        return this;
    }

    public VerticalGroup columnRight() {
        this.columnAlign |= 16;
        this.columnAlign &= -9;
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
