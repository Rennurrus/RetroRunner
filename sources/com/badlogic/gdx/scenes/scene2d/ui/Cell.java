package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.utils.Pool;

public class Cell<T extends Actor> implements Pool.Poolable {
    private static final Integer bottomi = 4;
    private static final Integer centeri = onei;
    private static Cell defaults;
    private static Files files;
    private static final Integer lefti = 8;
    private static final Float onef = Float.valueOf(1.0f);
    private static final Integer onei = 1;
    private static final Integer righti = 16;
    private static final Integer topi = 2;
    private static final Float zerof = Float.valueOf(0.0f);
    private static final Integer zeroi = 0;
    Actor actor;
    float actorHeight;
    float actorWidth;
    float actorX;
    float actorY;
    Integer align;
    int cellAboveIndex = -1;
    Integer colspan;
    int column;
    float computedPadBottom;
    float computedPadLeft;
    float computedPadRight;
    float computedPadTop;
    boolean endRow;
    Integer expandX;
    Integer expandY;
    Float fillX;
    Float fillY;
    Value maxHeight;
    Value maxWidth;
    Value minHeight;
    Value minWidth;
    Value padBottom;
    Value padLeft;
    Value padRight;
    Value padTop;
    Value prefHeight;
    Value prefWidth;
    int row;
    Value spaceBottom;
    Value spaceLeft;
    Value spaceRight;
    Value spaceTop;
    private Table table;
    Boolean uniformX;
    Boolean uniformY;

    public Cell() {
        Cell defaults2 = defaults();
        if (defaults2 != null) {
            set(defaults2);
        }
    }

    public void setTable(Table table2) {
        this.table = table2;
    }

    public <A extends Actor> Cell<A> setActor(A newActor) {
        A a = this.actor;
        if (a != newActor) {
            if (a != null && a.getParent() == this.table) {
                this.actor.remove();
            }
            this.actor = newActor;
            if (newActor != null) {
                this.table.addActor(newActor);
            }
        }
        return this;
    }

    public Cell<T> clearActor() {
        setActor((Actor) null);
        return this;
    }

    public T getActor() {
        return this.actor;
    }

    public boolean hasActor() {
        return this.actor != null;
    }

    public Cell<T> size(Value size) {
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

    public Cell<T> size(Value width, Value height) {
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

    public Cell<T> size(float size) {
        size((Value) Value.Fixed.valueOf(size));
        return this;
    }

    public Cell<T> size(float width, float height) {
        size((Value) Value.Fixed.valueOf(width), (Value) Value.Fixed.valueOf(height));
        return this;
    }

    public Cell<T> width(Value width) {
        if (width != null) {
            this.minWidth = width;
            this.prefWidth = width;
            this.maxWidth = width;
            return this;
        }
        throw new IllegalArgumentException("width cannot be null.");
    }

    public Cell<T> width(float width) {
        width((Value) Value.Fixed.valueOf(width));
        return this;
    }

    public Cell<T> height(Value height) {
        if (height != null) {
            this.minHeight = height;
            this.prefHeight = height;
            this.maxHeight = height;
            return this;
        }
        throw new IllegalArgumentException("height cannot be null.");
    }

    public Cell<T> height(float height) {
        height((Value) Value.Fixed.valueOf(height));
        return this;
    }

    public Cell<T> minSize(Value size) {
        if (size != null) {
            this.minWidth = size;
            this.minHeight = size;
            return this;
        }
        throw new IllegalArgumentException("size cannot be null.");
    }

    public Cell<T> minSize(Value width, Value height) {
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

    public Cell<T> minWidth(Value minWidth2) {
        if (minWidth2 != null) {
            this.minWidth = minWidth2;
            return this;
        }
        throw new IllegalArgumentException("minWidth cannot be null.");
    }

    public Cell<T> minHeight(Value minHeight2) {
        if (minHeight2 != null) {
            this.minHeight = minHeight2;
            return this;
        }
        throw new IllegalArgumentException("minHeight cannot be null.");
    }

    public Cell<T> minSize(float size) {
        minSize((Value) Value.Fixed.valueOf(size));
        return this;
    }

    public Cell<T> minSize(float width, float height) {
        minSize((Value) Value.Fixed.valueOf(width), (Value) Value.Fixed.valueOf(height));
        return this;
    }

    public Cell<T> minWidth(float minWidth2) {
        this.minWidth = Value.Fixed.valueOf(minWidth2);
        return this;
    }

    public Cell<T> minHeight(float minHeight2) {
        this.minHeight = Value.Fixed.valueOf(minHeight2);
        return this;
    }

    public Cell<T> prefSize(Value size) {
        if (size != null) {
            this.prefWidth = size;
            this.prefHeight = size;
            return this;
        }
        throw new IllegalArgumentException("size cannot be null.");
    }

    public Cell<T> prefSize(Value width, Value height) {
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

    public Cell<T> prefWidth(Value prefWidth2) {
        if (prefWidth2 != null) {
            this.prefWidth = prefWidth2;
            return this;
        }
        throw new IllegalArgumentException("prefWidth cannot be null.");
    }

    public Cell<T> prefHeight(Value prefHeight2) {
        if (prefHeight2 != null) {
            this.prefHeight = prefHeight2;
            return this;
        }
        throw new IllegalArgumentException("prefHeight cannot be null.");
    }

    public Cell<T> prefSize(float width, float height) {
        prefSize((Value) Value.Fixed.valueOf(width), (Value) Value.Fixed.valueOf(height));
        return this;
    }

    public Cell<T> prefSize(float size) {
        prefSize((Value) Value.Fixed.valueOf(size));
        return this;
    }

    public Cell<T> prefWidth(float prefWidth2) {
        this.prefWidth = Value.Fixed.valueOf(prefWidth2);
        return this;
    }

    public Cell<T> prefHeight(float prefHeight2) {
        this.prefHeight = Value.Fixed.valueOf(prefHeight2);
        return this;
    }

    public Cell<T> maxSize(Value size) {
        if (size != null) {
            this.maxWidth = size;
            this.maxHeight = size;
            return this;
        }
        throw new IllegalArgumentException("size cannot be null.");
    }

    public Cell<T> maxSize(Value width, Value height) {
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

    public Cell<T> maxWidth(Value maxWidth2) {
        if (maxWidth2 != null) {
            this.maxWidth = maxWidth2;
            return this;
        }
        throw new IllegalArgumentException("maxWidth cannot be null.");
    }

    public Cell<T> maxHeight(Value maxHeight2) {
        if (maxHeight2 != null) {
            this.maxHeight = maxHeight2;
            return this;
        }
        throw new IllegalArgumentException("maxHeight cannot be null.");
    }

    public Cell<T> maxSize(float size) {
        maxSize((Value) Value.Fixed.valueOf(size));
        return this;
    }

    public Cell<T> maxSize(float width, float height) {
        maxSize((Value) Value.Fixed.valueOf(width), (Value) Value.Fixed.valueOf(height));
        return this;
    }

    public Cell<T> maxWidth(float maxWidth2) {
        this.maxWidth = Value.Fixed.valueOf(maxWidth2);
        return this;
    }

    public Cell<T> maxHeight(float maxHeight2) {
        this.maxHeight = Value.Fixed.valueOf(maxHeight2);
        return this;
    }

    public Cell<T> space(Value space) {
        if (space != null) {
            this.spaceTop = space;
            this.spaceLeft = space;
            this.spaceBottom = space;
            this.spaceRight = space;
            return this;
        }
        throw new IllegalArgumentException("space cannot be null.");
    }

    public Cell<T> space(Value top, Value left, Value bottom, Value right) {
        if (top == null) {
            throw new IllegalArgumentException("top cannot be null.");
        } else if (left == null) {
            throw new IllegalArgumentException("left cannot be null.");
        } else if (bottom == null) {
            throw new IllegalArgumentException("bottom cannot be null.");
        } else if (right != null) {
            this.spaceTop = top;
            this.spaceLeft = left;
            this.spaceBottom = bottom;
            this.spaceRight = right;
            return this;
        } else {
            throw new IllegalArgumentException("right cannot be null.");
        }
    }

    public Cell<T> spaceTop(Value spaceTop2) {
        if (spaceTop2 != null) {
            this.spaceTop = spaceTop2;
            return this;
        }
        throw new IllegalArgumentException("spaceTop cannot be null.");
    }

    public Cell<T> spaceLeft(Value spaceLeft2) {
        if (spaceLeft2 != null) {
            this.spaceLeft = spaceLeft2;
            return this;
        }
        throw new IllegalArgumentException("spaceLeft cannot be null.");
    }

    public Cell<T> spaceBottom(Value spaceBottom2) {
        if (spaceBottom2 != null) {
            this.spaceBottom = spaceBottom2;
            return this;
        }
        throw new IllegalArgumentException("spaceBottom cannot be null.");
    }

    public Cell<T> spaceRight(Value spaceRight2) {
        if (spaceRight2 != null) {
            this.spaceRight = spaceRight2;
            return this;
        }
        throw new IllegalArgumentException("spaceRight cannot be null.");
    }

    public Cell<T> space(float space) {
        if (space >= 0.0f) {
            space((Value) Value.Fixed.valueOf(space));
            return this;
        }
        throw new IllegalArgumentException("space cannot be < 0: " + space);
    }

    public Cell<T> space(float top, float left, float bottom, float right) {
        if (top < 0.0f) {
            throw new IllegalArgumentException("top cannot be < 0: " + top);
        } else if (left < 0.0f) {
            throw new IllegalArgumentException("left cannot be < 0: " + left);
        } else if (bottom < 0.0f) {
            throw new IllegalArgumentException("bottom cannot be < 0: " + bottom);
        } else if (right >= 0.0f) {
            space((Value) Value.Fixed.valueOf(top), (Value) Value.Fixed.valueOf(left), (Value) Value.Fixed.valueOf(bottom), (Value) Value.Fixed.valueOf(right));
            return this;
        } else {
            throw new IllegalArgumentException("right cannot be < 0: " + right);
        }
    }

    public Cell<T> spaceTop(float spaceTop2) {
        if (spaceTop2 >= 0.0f) {
            this.spaceTop = Value.Fixed.valueOf(spaceTop2);
            return this;
        }
        throw new IllegalArgumentException("spaceTop cannot be < 0: " + spaceTop2);
    }

    public Cell<T> spaceLeft(float spaceLeft2) {
        if (spaceLeft2 >= 0.0f) {
            this.spaceLeft = Value.Fixed.valueOf(spaceLeft2);
            return this;
        }
        throw new IllegalArgumentException("spaceLeft cannot be < 0: " + spaceLeft2);
    }

    public Cell<T> spaceBottom(float spaceBottom2) {
        if (spaceBottom2 >= 0.0f) {
            this.spaceBottom = Value.Fixed.valueOf(spaceBottom2);
            return this;
        }
        throw new IllegalArgumentException("spaceBottom cannot be < 0: " + spaceBottom2);
    }

    public Cell<T> spaceRight(float spaceRight2) {
        if (spaceRight2 >= 0.0f) {
            this.spaceRight = Value.Fixed.valueOf(spaceRight2);
            return this;
        }
        throw new IllegalArgumentException("spaceRight cannot be < 0: " + spaceRight2);
    }

    public Cell<T> pad(Value pad) {
        if (pad != null) {
            this.padTop = pad;
            this.padLeft = pad;
            this.padBottom = pad;
            this.padRight = pad;
            return this;
        }
        throw new IllegalArgumentException("pad cannot be null.");
    }

    public Cell<T> pad(Value top, Value left, Value bottom, Value right) {
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

    public Cell<T> padTop(Value padTop2) {
        if (padTop2 != null) {
            this.padTop = padTop2;
            return this;
        }
        throw new IllegalArgumentException("padTop cannot be null.");
    }

    public Cell<T> padLeft(Value padLeft2) {
        if (padLeft2 != null) {
            this.padLeft = padLeft2;
            return this;
        }
        throw new IllegalArgumentException("padLeft cannot be null.");
    }

    public Cell<T> padBottom(Value padBottom2) {
        if (padBottom2 != null) {
            this.padBottom = padBottom2;
            return this;
        }
        throw new IllegalArgumentException("padBottom cannot be null.");
    }

    public Cell<T> padRight(Value padRight2) {
        if (padRight2 != null) {
            this.padRight = padRight2;
            return this;
        }
        throw new IllegalArgumentException("padRight cannot be null.");
    }

    public Cell<T> pad(float pad) {
        pad((Value) Value.Fixed.valueOf(pad));
        return this;
    }

    public Cell<T> pad(float top, float left, float bottom, float right) {
        pad((Value) Value.Fixed.valueOf(top), (Value) Value.Fixed.valueOf(left), (Value) Value.Fixed.valueOf(bottom), (Value) Value.Fixed.valueOf(right));
        return this;
    }

    public Cell<T> padTop(float padTop2) {
        this.padTop = Value.Fixed.valueOf(padTop2);
        return this;
    }

    public Cell<T> padLeft(float padLeft2) {
        this.padLeft = Value.Fixed.valueOf(padLeft2);
        return this;
    }

    public Cell<T> padBottom(float padBottom2) {
        this.padBottom = Value.Fixed.valueOf(padBottom2);
        return this;
    }

    public Cell<T> padRight(float padRight2) {
        this.padRight = Value.Fixed.valueOf(padRight2);
        return this;
    }

    public Cell<T> fill() {
        Float f = onef;
        this.fillX = f;
        this.fillY = f;
        return this;
    }

    public Cell<T> fillX() {
        this.fillX = onef;
        return this;
    }

    public Cell<T> fillY() {
        this.fillY = onef;
        return this;
    }

    public Cell<T> fill(float x, float y) {
        this.fillX = Float.valueOf(x);
        this.fillY = Float.valueOf(y);
        return this;
    }

    public Cell<T> fill(boolean x, boolean y) {
        this.fillX = x ? onef : zerof;
        this.fillY = y ? onef : zerof;
        return this;
    }

    public Cell<T> fill(boolean fill) {
        this.fillX = fill ? onef : zerof;
        this.fillY = fill ? onef : zerof;
        return this;
    }

    public Cell<T> align(int align2) {
        this.align = Integer.valueOf(align2);
        return this;
    }

    public Cell<T> center() {
        this.align = centeri;
        return this;
    }

    public Cell<T> top() {
        Integer num = this.align;
        if (num == null) {
            this.align = topi;
        } else {
            this.align = Integer.valueOf((num.intValue() | 2) & -5);
        }
        return this;
    }

    public Cell<T> left() {
        Integer num = this.align;
        if (num == null) {
            this.align = lefti;
        } else {
            this.align = Integer.valueOf((num.intValue() | 8) & -17);
        }
        return this;
    }

    public Cell<T> bottom() {
        Integer num = this.align;
        if (num == null) {
            this.align = bottomi;
        } else {
            this.align = Integer.valueOf((num.intValue() | 4) & -3);
        }
        return this;
    }

    public Cell<T> right() {
        Integer num = this.align;
        if (num == null) {
            this.align = righti;
        } else {
            this.align = Integer.valueOf((num.intValue() | 16) & -9);
        }
        return this;
    }

    public Cell<T> grow() {
        Integer num = onei;
        this.expandX = num;
        this.expandY = num;
        Float f = onef;
        this.fillX = f;
        this.fillY = f;
        return this;
    }

    public Cell<T> growX() {
        this.expandX = onei;
        this.fillX = onef;
        return this;
    }

    public Cell<T> growY() {
        this.expandY = onei;
        this.fillY = onef;
        return this;
    }

    public Cell<T> expand() {
        Integer num = onei;
        this.expandX = num;
        this.expandY = num;
        return this;
    }

    public Cell<T> expandX() {
        this.expandX = onei;
        return this;
    }

    public Cell<T> expandY() {
        this.expandY = onei;
        return this;
    }

    public Cell<T> expand(int x, int y) {
        this.expandX = Integer.valueOf(x);
        this.expandY = Integer.valueOf(y);
        return this;
    }

    public Cell<T> expand(boolean x, boolean y) {
        this.expandX = x ? onei : zeroi;
        this.expandY = y ? onei : zeroi;
        return this;
    }

    public Cell<T> colspan(int colspan2) {
        this.colspan = Integer.valueOf(colspan2);
        return this;
    }

    public Cell<T> uniform() {
        this.uniformX = Boolean.TRUE;
        this.uniformY = Boolean.TRUE;
        return this;
    }

    public Cell<T> uniformX() {
        this.uniformX = Boolean.TRUE;
        return this;
    }

    public Cell<T> uniformY() {
        this.uniformY = Boolean.TRUE;
        return this;
    }

    public Cell<T> uniform(boolean uniform) {
        this.uniformX = Boolean.valueOf(uniform);
        this.uniformY = Boolean.valueOf(uniform);
        return this;
    }

    public Cell<T> uniform(boolean x, boolean y) {
        this.uniformX = Boolean.valueOf(x);
        this.uniformY = Boolean.valueOf(y);
        return this;
    }

    public void setActorBounds(float x, float y, float width, float height) {
        this.actorX = x;
        this.actorY = y;
        this.actorWidth = width;
        this.actorHeight = height;
    }

    public float getActorX() {
        return this.actorX;
    }

    public void setActorX(float actorX2) {
        this.actorX = actorX2;
    }

    public float getActorY() {
        return this.actorY;
    }

    public void setActorY(float actorY2) {
        this.actorY = actorY2;
    }

    public float getActorWidth() {
        return this.actorWidth;
    }

    public void setActorWidth(float actorWidth2) {
        this.actorWidth = actorWidth2;
    }

    public float getActorHeight() {
        return this.actorHeight;
    }

    public void setActorHeight(float actorHeight2) {
        this.actorHeight = actorHeight2;
    }

    public int getColumn() {
        return this.column;
    }

    public int getRow() {
        return this.row;
    }

    public Value getMinWidthValue() {
        return this.minWidth;
    }

    public float getMinWidth() {
        return this.minWidth.get(this.actor);
    }

    public Value getMinHeightValue() {
        return this.minHeight;
    }

    public float getMinHeight() {
        return this.minHeight.get(this.actor);
    }

    public Value getPrefWidthValue() {
        return this.prefWidth;
    }

    public float getPrefWidth() {
        return this.prefWidth.get(this.actor);
    }

    public Value getPrefHeightValue() {
        return this.prefHeight;
    }

    public float getPrefHeight() {
        return this.prefHeight.get(this.actor);
    }

    public Value getMaxWidthValue() {
        return this.maxWidth;
    }

    public float getMaxWidth() {
        return this.maxWidth.get(this.actor);
    }

    public Value getMaxHeightValue() {
        return this.maxHeight;
    }

    public float getMaxHeight() {
        return this.maxHeight.get(this.actor);
    }

    public Value getSpaceTopValue() {
        return this.spaceTop;
    }

    public float getSpaceTop() {
        return this.spaceTop.get(this.actor);
    }

    public Value getSpaceLeftValue() {
        return this.spaceLeft;
    }

    public float getSpaceLeft() {
        return this.spaceLeft.get(this.actor);
    }

    public Value getSpaceBottomValue() {
        return this.spaceBottom;
    }

    public float getSpaceBottom() {
        return this.spaceBottom.get(this.actor);
    }

    public Value getSpaceRightValue() {
        return this.spaceRight;
    }

    public float getSpaceRight() {
        return this.spaceRight.get(this.actor);
    }

    public Value getPadTopValue() {
        return this.padTop;
    }

    public float getPadTop() {
        return this.padTop.get(this.actor);
    }

    public Value getPadLeftValue() {
        return this.padLeft;
    }

    public float getPadLeft() {
        return this.padLeft.get(this.actor);
    }

    public Value getPadBottomValue() {
        return this.padBottom;
    }

    public float getPadBottom() {
        return this.padBottom.get(this.actor);
    }

    public Value getPadRightValue() {
        return this.padRight;
    }

    public float getPadRight() {
        return this.padRight.get(this.actor);
    }

    public float getPadX() {
        return this.padLeft.get(this.actor) + this.padRight.get(this.actor);
    }

    public float getPadY() {
        return this.padTop.get(this.actor) + this.padBottom.get(this.actor);
    }

    public float getFillX() {
        return this.fillX.floatValue();
    }

    public float getFillY() {
        return this.fillY.floatValue();
    }

    public int getAlign() {
        return this.align.intValue();
    }

    public int getExpandX() {
        return this.expandX.intValue();
    }

    public int getExpandY() {
        return this.expandY.intValue();
    }

    public int getColspan() {
        return this.colspan.intValue();
    }

    public boolean getUniformX() {
        return this.uniformX.booleanValue();
    }

    public boolean getUniformY() {
        return this.uniformY.booleanValue();
    }

    public boolean isEndRow() {
        return this.endRow;
    }

    public float getComputedPadTop() {
        return this.computedPadTop;
    }

    public float getComputedPadLeft() {
        return this.computedPadLeft;
    }

    public float getComputedPadBottom() {
        return this.computedPadBottom;
    }

    public float getComputedPadRight() {
        return this.computedPadRight;
    }

    public void row() {
        this.table.row();
    }

    public Table getTable() {
        return this.table;
    }

    /* access modifiers changed from: package-private */
    public void clear() {
        this.minWidth = null;
        this.minHeight = null;
        this.prefWidth = null;
        this.prefHeight = null;
        this.maxWidth = null;
        this.maxHeight = null;
        this.spaceTop = null;
        this.spaceLeft = null;
        this.spaceBottom = null;
        this.spaceRight = null;
        this.padTop = null;
        this.padLeft = null;
        this.padBottom = null;
        this.padRight = null;
        this.fillX = null;
        this.fillY = null;
        this.align = null;
        this.expandX = null;
        this.expandY = null;
        this.colspan = null;
        this.uniformX = null;
        this.uniformY = null;
    }

    public void reset() {
        this.actor = null;
        this.table = null;
        this.endRow = false;
        this.cellAboveIndex = -1;
        set(defaults());
    }

    /* access modifiers changed from: package-private */
    public void set(Cell cell) {
        this.minWidth = cell.minWidth;
        this.minHeight = cell.minHeight;
        this.prefWidth = cell.prefWidth;
        this.prefHeight = cell.prefHeight;
        this.maxWidth = cell.maxWidth;
        this.maxHeight = cell.maxHeight;
        this.spaceTop = cell.spaceTop;
        this.spaceLeft = cell.spaceLeft;
        this.spaceBottom = cell.spaceBottom;
        this.spaceRight = cell.spaceRight;
        this.padTop = cell.padTop;
        this.padLeft = cell.padLeft;
        this.padBottom = cell.padBottom;
        this.padRight = cell.padRight;
        this.fillX = cell.fillX;
        this.fillY = cell.fillY;
        this.align = cell.align;
        this.expandX = cell.expandX;
        this.expandY = cell.expandY;
        this.colspan = cell.colspan;
        this.uniformX = cell.uniformX;
        this.uniformY = cell.uniformY;
    }

    /* access modifiers changed from: package-private */
    public void merge(Cell cell) {
        if (cell != null) {
            Value value = cell.minWidth;
            if (value != null) {
                this.minWidth = value;
            }
            Value value2 = cell.minHeight;
            if (value2 != null) {
                this.minHeight = value2;
            }
            Value value3 = cell.prefWidth;
            if (value3 != null) {
                this.prefWidth = value3;
            }
            Value value4 = cell.prefHeight;
            if (value4 != null) {
                this.prefHeight = value4;
            }
            Value value5 = cell.maxWidth;
            if (value5 != null) {
                this.maxWidth = value5;
            }
            Value value6 = cell.maxHeight;
            if (value6 != null) {
                this.maxHeight = value6;
            }
            Value value7 = cell.spaceTop;
            if (value7 != null) {
                this.spaceTop = value7;
            }
            Value value8 = cell.spaceLeft;
            if (value8 != null) {
                this.spaceLeft = value8;
            }
            Value value9 = cell.spaceBottom;
            if (value9 != null) {
                this.spaceBottom = value9;
            }
            Value value10 = cell.spaceRight;
            if (value10 != null) {
                this.spaceRight = value10;
            }
            Value value11 = cell.padTop;
            if (value11 != null) {
                this.padTop = value11;
            }
            Value value12 = cell.padLeft;
            if (value12 != null) {
                this.padLeft = value12;
            }
            Value value13 = cell.padBottom;
            if (value13 != null) {
                this.padBottom = value13;
            }
            Value value14 = cell.padRight;
            if (value14 != null) {
                this.padRight = value14;
            }
            Float f = cell.fillX;
            if (f != null) {
                this.fillX = f;
            }
            Float f2 = cell.fillY;
            if (f2 != null) {
                this.fillY = f2;
            }
            Integer num = cell.align;
            if (num != null) {
                this.align = num;
            }
            Integer num2 = cell.expandX;
            if (num2 != null) {
                this.expandX = num2;
            }
            Integer num3 = cell.expandY;
            if (num3 != null) {
                this.expandY = num3;
            }
            Integer num4 = cell.colspan;
            if (num4 != null) {
                this.colspan = num4;
            }
            Boolean bool = cell.uniformX;
            if (bool != null) {
                this.uniformX = bool;
            }
            Boolean bool2 = cell.uniformY;
            if (bool2 != null) {
                this.uniformY = bool2;
            }
        }
    }

    public String toString() {
        Actor actor2 = this.actor;
        return actor2 != null ? actor2.toString() : super.toString();
    }

    public static Cell defaults() {
        Files files2 = files;
        if (files2 == null || files2 != Gdx.files) {
            files = Gdx.files;
            defaults = new Cell();
            defaults.minWidth = Value.minWidth;
            defaults.minHeight = Value.minHeight;
            defaults.prefWidth = Value.prefWidth;
            defaults.prefHeight = Value.prefHeight;
            defaults.maxWidth = Value.maxWidth;
            defaults.maxHeight = Value.maxHeight;
            defaults.spaceTop = Value.zero;
            defaults.spaceLeft = Value.zero;
            defaults.spaceBottom = Value.zero;
            defaults.spaceRight = Value.zero;
            defaults.padTop = Value.zero;
            defaults.padLeft = Value.zero;
            defaults.padBottom = Value.zero;
            defaults.padRight = Value.zero;
            Cell cell = defaults;
            Float f = zerof;
            cell.fillX = f;
            cell.fillY = f;
            cell.align = centeri;
            Integer num = zeroi;
            cell.expandX = num;
            cell.expandY = num;
            cell.colspan = onei;
            cell.uniformX = null;
            cell.uniformY = null;
        }
        return defaults;
    }
}
