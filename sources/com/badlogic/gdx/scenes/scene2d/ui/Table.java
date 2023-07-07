package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;

public class Table extends WidgetGroup {
    public static Value backgroundBottom = new Value() {
        public float get(Actor context) {
            Drawable background = ((Table) context).background;
            if (background == null) {
                return 0.0f;
            }
            return background.getBottomHeight();
        }
    };
    public static Value backgroundLeft = new Value() {
        public float get(Actor context) {
            Drawable background = ((Table) context).background;
            if (background == null) {
                return 0.0f;
            }
            return background.getLeftWidth();
        }
    };
    public static Value backgroundRight = new Value() {
        public float get(Actor context) {
            Drawable background = ((Table) context).background;
            if (background == null) {
                return 0.0f;
            }
            return background.getRightWidth();
        }
    };
    public static Value backgroundTop = new Value() {
        public float get(Actor context) {
            Drawable background = ((Table) context).background;
            if (background == null) {
                return 0.0f;
            }
            return background.getTopHeight();
        }
    };
    static final Pool<Cell> cellPool = new Pool<Cell>() {
        /* access modifiers changed from: protected */
        public Cell newObject() {
            return new Cell();
        }
    };
    private static float[] columnWeightedWidth;
    public static Color debugActorColor = new Color(0.0f, 1.0f, 0.0f, 1.0f);
    public static Color debugCellColor = new Color(1.0f, 0.0f, 0.0f, 1.0f);
    public static Color debugTableColor = new Color(0.0f, 0.0f, 1.0f, 1.0f);
    private static float[] rowWeightedHeight;
    int align;
    Drawable background;
    private final Cell cellDefaults;
    private final Array<Cell> cells;
    private boolean clip;
    private final Array<Cell> columnDefaults;
    private float[] columnMinWidth;
    private float[] columnPrefWidth;
    private float[] columnWidth;
    private int columns;
    Debug debug;
    Array<DebugRect> debugRects;
    private float[] expandHeight;
    private float[] expandWidth;
    private boolean implicitEndRow;
    Value padBottom;
    Value padLeft;
    Value padRight;
    Value padTop;
    boolean round;
    private Cell rowDefaults;
    private float[] rowHeight;
    private float[] rowMinHeight;
    private float[] rowPrefHeight;
    private int rows;
    private boolean sizeInvalid;
    private Skin skin;
    private float tableMinHeight;
    private float tableMinWidth;
    private float tablePrefHeight;
    private float tablePrefWidth;

    public enum Debug {
        none,
        all,
        table,
        cell,
        actor
    }

    public static class DebugRect extends Rectangle {
        static Pool<DebugRect> pool = Pools.get(DebugRect.class);
        Color color;
    }

    public Table() {
        this((Skin) null);
    }

    public Table(Skin skin2) {
        this.cells = new Array<>(4);
        this.columnDefaults = new Array<>(2);
        this.sizeInvalid = true;
        this.padTop = backgroundTop;
        this.padLeft = backgroundLeft;
        this.padBottom = backgroundBottom;
        this.padRight = backgroundRight;
        this.align = 1;
        this.debug = Debug.none;
        this.round = true;
        this.skin = skin2;
        this.cellDefaults = obtainCell();
        setTransform(false);
        setTouchable(Touchable.childrenOnly);
    }

    private Cell obtainCell() {
        Cell cell = cellPool.obtain();
        cell.setTable(this);
        return cell;
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

    public void setBackground(String drawableName) {
        Skin skin2 = this.skin;
        if (skin2 != null) {
            setBackground(skin2.getDrawable(drawableName));
            return;
        }
        throw new IllegalStateException("Table must have a skin set to use this method.");
    }

    public void setBackground(Drawable background2) {
        if (this.background != background2) {
            float padTopOld = getPadTop();
            float padLeftOld = getPadLeft();
            float padBottomOld = getPadBottom();
            float padRightOld = getPadRight();
            this.background = background2;
            float padTopNew = getPadTop();
            float padLeftNew = getPadLeft();
            float padBottomNew = getPadBottom();
            float padRightNew = getPadRight();
            if (padTopOld + padBottomOld != padTopNew + padBottomNew || padLeftOld + padRightOld != padLeftNew + padRightNew) {
                invalidateHierarchy();
            } else if (padTopOld != padTopNew || padLeftOld != padLeftNew || padBottomOld != padBottomNew || padRightOld != padRightNew) {
                invalidate();
            }
        }
    }

    public Table background(Drawable background2) {
        setBackground(background2);
        return this;
    }

    public Table background(String drawableName) {
        setBackground(drawableName);
        return this;
    }

    public Drawable getBackground() {
        return this.background;
    }

    public Actor hit(float x, float y, boolean touchable) {
        if (!this.clip || ((!touchable || getTouchable() != Touchable.disabled) && x >= 0.0f && x < getWidth() && y >= 0.0f && y < getHeight())) {
            return super.hit(x, y, touchable);
        }
        return null;
    }

    public void setClip(boolean enabled) {
        this.clip = enabled;
        setTransform(enabled);
        invalidate();
    }

    public boolean getClip() {
        return this.clip;
    }

    public void invalidate() {
        this.sizeInvalid = true;
        super.invalidate();
    }

    public <T extends Actor> Cell<T> add(T actor) {
        Cell columnCell;
        Cell<T> cell = obtainCell();
        cell.actor = actor;
        if (this.implicitEndRow) {
            this.implicitEndRow = false;
            this.rows--;
            this.cells.peek().endRow = false;
        }
        Array<Cell> cells2 = this.cells;
        int cellCount = cells2.size;
        if (cellCount > 0) {
            Cell lastCell = cells2.peek();
            if (!lastCell.endRow) {
                cell.column = lastCell.column + lastCell.colspan.intValue();
                cell.row = lastCell.row;
            } else {
                cell.column = 0;
                cell.row = lastCell.row + 1;
            }
            if (cell.row > 0) {
                int i = cellCount - 1;
                loop0:
                while (true) {
                    if (i < 0) {
                        break;
                    }
                    Cell other = cells2.get(i);
                    int column = other.column;
                    int nn = other.colspan.intValue() + column;
                    while (column < nn) {
                        if (column == cell.column) {
                            cell.cellAboveIndex = i;
                            break loop0;
                        }
                        column++;
                    }
                    i--;
                }
            }
        } else {
            cell.column = 0;
            cell.row = 0;
        }
        cells2.add(cell);
        cell.set(this.cellDefaults);
        if (cell.column < this.columnDefaults.size && (columnCell = this.columnDefaults.get(cell.column)) != null) {
            cell.merge(columnCell);
        }
        cell.merge(this.rowDefaults);
        if (actor != null) {
            addActor(actor);
        }
        return cell;
    }

    public Table add(Actor... actors) {
        for (Actor add : actors) {
            add(add);
        }
        return this;
    }

    public Cell<Label> add(CharSequence text) {
        Skin skin2 = this.skin;
        if (skin2 != null) {
            return add(new Label(text, skin2));
        }
        throw new IllegalStateException("Table must have a skin set to use this method.");
    }

    public Cell<Label> add(CharSequence text, String labelStyleName) {
        Skin skin2 = this.skin;
        if (skin2 != null) {
            return add(new Label(text, (Label.LabelStyle) skin2.get(labelStyleName, Label.LabelStyle.class)));
        }
        throw new IllegalStateException("Table must have a skin set to use this method.");
    }

    public Cell<Label> add(CharSequence text, String fontName, Color color) {
        Skin skin2 = this.skin;
        if (skin2 != null) {
            return add(new Label(text, new Label.LabelStyle(skin2.getFont(fontName), color)));
        }
        throw new IllegalStateException("Table must have a skin set to use this method.");
    }

    public Cell<Label> add(CharSequence text, String fontName, String colorName) {
        Skin skin2 = this.skin;
        if (skin2 != null) {
            return add(new Label(text, new Label.LabelStyle(skin2.getFont(fontName), this.skin.getColor(colorName))));
        }
        throw new IllegalStateException("Table must have a skin set to use this method.");
    }

    public Cell add() {
        return add((Actor) null);
    }

    public Cell<Stack> stack(Actor... actors) {
        Stack stack = new Stack();
        if (actors != null) {
            for (Actor addActor : actors) {
                stack.addActor(addActor);
            }
        }
        return add(stack);
    }

    public boolean removeActor(Actor actor) {
        return removeActor(actor, true);
    }

    public boolean removeActor(Actor actor, boolean unfocus) {
        if (!super.removeActor(actor, unfocus)) {
            return false;
        }
        Cell cell = getCell(actor);
        if (cell == null) {
            return true;
        }
        cell.actor = null;
        return true;
    }

    public void clearChildren() {
        Array<Cell> cells2 = this.cells;
        for (int i = cells2.size - 1; i >= 0; i--) {
            Actor actor = cells2.get(i).actor;
            if (actor != null) {
                actor.remove();
            }
        }
        cellPool.freeAll(cells2);
        cells2.clear();
        this.rows = 0;
        this.columns = 0;
        Cell cell = this.rowDefaults;
        if (cell != null) {
            cellPool.free(cell);
        }
        this.rowDefaults = null;
        this.implicitEndRow = false;
        super.clearChildren();
    }

    public void reset() {
        clearChildren();
        this.padTop = backgroundTop;
        this.padLeft = backgroundLeft;
        this.padBottom = backgroundBottom;
        this.padRight = backgroundRight;
        this.align = 1;
        debug(Debug.none);
        this.cellDefaults.reset();
        int n = this.columnDefaults.size;
        for (int i = 0; i < n; i++) {
            Cell columnCell = this.columnDefaults.get(i);
            if (columnCell != null) {
                cellPool.free(columnCell);
            }
        }
        this.columnDefaults.clear();
    }

    public Cell row() {
        if (this.cells.size > 0) {
            if (!this.implicitEndRow) {
                if (this.cells.peek().endRow) {
                    return this.rowDefaults;
                }
                endRow();
            }
            invalidate();
        }
        this.implicitEndRow = false;
        Cell cell = this.rowDefaults;
        if (cell != null) {
            cellPool.free(cell);
        }
        this.rowDefaults = obtainCell();
        this.rowDefaults.clear();
        return this.rowDefaults;
    }

    private void endRow() {
        Array<Cell> cells2 = this.cells;
        int rowColumns = 0;
        for (int i = cells2.size - 1; i >= 0; i--) {
            Cell cell = cells2.get(i);
            if (cell.endRow) {
                break;
            }
            rowColumns += cell.colspan.intValue();
        }
        this.columns = Math.max(this.columns, rowColumns);
        this.rows++;
        cells2.peek().endRow = true;
    }

    public Cell columnDefaults(int column) {
        Cell cell = this.columnDefaults.size > column ? this.columnDefaults.get(column) : null;
        if (cell == null) {
            cell = obtainCell();
            cell.clear();
            if (column >= this.columnDefaults.size) {
                for (int i = this.columnDefaults.size; i < column; i++) {
                    this.columnDefaults.add(null);
                }
                this.columnDefaults.add(cell);
            } else {
                this.columnDefaults.set(column, cell);
            }
        }
        return cell;
    }

    public <T extends Actor> Cell<T> getCell(T actor) {
        Array<Cell> cells2 = this.cells;
        int n = cells2.size;
        for (int i = 0; i < n; i++) {
            Cell c = cells2.get(i);
            if (c.actor == actor) {
                return c;
            }
        }
        return null;
    }

    public Array<Cell> getCells() {
        return this.cells;
    }

    public float getPrefWidth() {
        if (this.sizeInvalid) {
            computeSize();
        }
        float width = this.tablePrefWidth;
        Drawable drawable = this.background;
        if (drawable != null) {
            return Math.max(width, drawable.getMinWidth());
        }
        return width;
    }

    public float getPrefHeight() {
        if (this.sizeInvalid) {
            computeSize();
        }
        float height = this.tablePrefHeight;
        Drawable drawable = this.background;
        if (drawable != null) {
            return Math.max(height, drawable.getMinHeight());
        }
        return height;
    }

    public float getMinWidth() {
        if (this.sizeInvalid) {
            computeSize();
        }
        return this.tableMinWidth;
    }

    public float getMinHeight() {
        if (this.sizeInvalid) {
            computeSize();
        }
        return this.tableMinHeight;
    }

    public Cell defaults() {
        return this.cellDefaults;
    }

    public Table pad(Value pad) {
        if (pad != null) {
            this.padTop = pad;
            this.padLeft = pad;
            this.padBottom = pad;
            this.padRight = pad;
            this.sizeInvalid = true;
            return this;
        }
        throw new IllegalArgumentException("pad cannot be null.");
    }

    public Table pad(Value top, Value left, Value bottom, Value right) {
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
            this.sizeInvalid = true;
            return this;
        } else {
            throw new IllegalArgumentException("right cannot be null.");
        }
    }

    public Table padTop(Value padTop2) {
        if (padTop2 != null) {
            this.padTop = padTop2;
            this.sizeInvalid = true;
            return this;
        }
        throw new IllegalArgumentException("padTop cannot be null.");
    }

    public Table padLeft(Value padLeft2) {
        if (padLeft2 != null) {
            this.padLeft = padLeft2;
            this.sizeInvalid = true;
            return this;
        }
        throw new IllegalArgumentException("padLeft cannot be null.");
    }

    public Table padBottom(Value padBottom2) {
        if (padBottom2 != null) {
            this.padBottom = padBottom2;
            this.sizeInvalid = true;
            return this;
        }
        throw new IllegalArgumentException("padBottom cannot be null.");
    }

    public Table padRight(Value padRight2) {
        if (padRight2 != null) {
            this.padRight = padRight2;
            this.sizeInvalid = true;
            return this;
        }
        throw new IllegalArgumentException("padRight cannot be null.");
    }

    public Table pad(float pad) {
        pad((Value) Value.Fixed.valueOf(pad));
        return this;
    }

    public Table pad(float top, float left, float bottom, float right) {
        this.padTop = Value.Fixed.valueOf(top);
        this.padLeft = Value.Fixed.valueOf(left);
        this.padBottom = Value.Fixed.valueOf(bottom);
        this.padRight = Value.Fixed.valueOf(right);
        this.sizeInvalid = true;
        return this;
    }

    public Table padTop(float padTop2) {
        this.padTop = Value.Fixed.valueOf(padTop2);
        this.sizeInvalid = true;
        return this;
    }

    public Table padLeft(float padLeft2) {
        this.padLeft = Value.Fixed.valueOf(padLeft2);
        this.sizeInvalid = true;
        return this;
    }

    public Table padBottom(float padBottom2) {
        this.padBottom = Value.Fixed.valueOf(padBottom2);
        this.sizeInvalid = true;
        return this;
    }

    public Table padRight(float padRight2) {
        this.padRight = Value.Fixed.valueOf(padRight2);
        this.sizeInvalid = true;
        return this;
    }

    public Table align(int align2) {
        this.align = align2;
        return this;
    }

    public Table center() {
        this.align = 1;
        return this;
    }

    public Table top() {
        this.align |= 2;
        this.align &= -5;
        return this;
    }

    public Table left() {
        this.align |= 8;
        this.align &= -17;
        return this;
    }

    public Table bottom() {
        this.align |= 4;
        this.align &= -3;
        return this;
    }

    public Table right() {
        this.align |= 16;
        this.align &= -9;
        return this;
    }

    public void setDebug(boolean enabled) {
        debug(enabled ? Debug.all : Debug.none);
    }

    public Table debug() {
        super.debug();
        return this;
    }

    public Table debugAll() {
        super.debugAll();
        return this;
    }

    public Table debugTable() {
        super.setDebug(true);
        if (this.debug != Debug.table) {
            this.debug = Debug.table;
            invalidate();
        }
        return this;
    }

    public Table debugCell() {
        super.setDebug(true);
        if (this.debug != Debug.cell) {
            this.debug = Debug.cell;
            invalidate();
        }
        return this;
    }

    public Table debugActor() {
        super.setDebug(true);
        if (this.debug != Debug.actor) {
            this.debug = Debug.actor;
            invalidate();
        }
        return this;
    }

    public Table debug(Debug debug2) {
        super.setDebug(debug2 != Debug.none);
        if (this.debug != debug2) {
            this.debug = debug2;
            if (debug2 == Debug.none) {
                clearDebugRects();
            } else {
                invalidate();
            }
        }
        return this;
    }

    public Debug getTableDebug() {
        return this.debug;
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

    public int getAlign() {
        return this.align;
    }

    public int getRow(float y) {
        Array<Cell> cells2 = this.cells;
        int row = 0;
        float y2 = y + getPadTop();
        int i = 0;
        int n = cells2.size;
        if (n == 0) {
            return -1;
        }
        if (n == 1) {
            return 0;
        }
        while (true) {
            if (i >= n) {
                break;
            }
            int i2 = i + 1;
            Cell c = cells2.get(i);
            if (c.actorY + c.computedPadTop < y2) {
                int i3 = i2;
                break;
            }
            if (c.endRow) {
                row++;
            }
            i = i2;
        }
        return Math.min(row, this.rows - 1);
    }

    public void setSkin(Skin skin2) {
        this.skin = skin2;
    }

    public void setRound(boolean round2) {
        this.round = round2;
    }

    public int getRows() {
        return this.rows;
    }

    public int getColumns() {
        return this.columns;
    }

    public float getRowHeight(int rowIndex) {
        float[] fArr = this.rowHeight;
        if (fArr == null) {
            return 0.0f;
        }
        return fArr[rowIndex];
    }

    public float getRowMinHeight(int rowIndex) {
        if (this.sizeInvalid) {
            computeSize();
        }
        return this.rowMinHeight[rowIndex];
    }

    public float getRowPrefHeight(int rowIndex) {
        if (this.sizeInvalid) {
            computeSize();
        }
        return this.rowPrefHeight[rowIndex];
    }

    public float getColumnWidth(int columnIndex) {
        float[] fArr = this.columnWidth;
        if (fArr == null) {
            return 0.0f;
        }
        return fArr[columnIndex];
    }

    public float getColumnMinWidth(int columnIndex) {
        if (this.sizeInvalid) {
            computeSize();
        }
        return this.columnMinWidth[columnIndex];
    }

    public float getColumnPrefWidth(int columnIndex) {
        if (this.sizeInvalid) {
            computeSize();
        }
        return this.columnPrefWidth[columnIndex];
    }

    private float[] ensureSize(float[] array, int size) {
        if (array == null || array.length < size) {
            return new float[size];
        }
        int n = array.length;
        for (int i = 0; i < n; i++) {
            array[i] = 0.0f;
        }
        return array;
    }

    public void layout() {
        float width = getWidth();
        float height = getHeight();
        layout(0.0f, 0.0f, width, height);
        Array<Cell> cells2 = this.cells;
        if (this.round) {
            int n = cells2.size;
            for (int i = 0; i < n; i++) {
                Cell c = cells2.get(i);
                float actorWidth = (float) Math.round(c.actorWidth);
                float actorHeight = (float) Math.round(c.actorHeight);
                float actorX = (float) Math.round(c.actorX);
                float actorY = (height - ((float) Math.round(c.actorY))) - actorHeight;
                c.setActorBounds(actorX, actorY, actorWidth, actorHeight);
                Actor actor = c.actor;
                if (actor != null) {
                    actor.setBounds(actorX, actorY, actorWidth, actorHeight);
                }
            }
        } else {
            int n2 = cells2.size;
            for (int i2 = 0; i2 < n2; i2++) {
                Cell c2 = cells2.get(i2);
                float actorHeight2 = c2.actorHeight;
                float actorY2 = (height - c2.actorY) - actorHeight2;
                c2.setActorY(actorY2);
                Actor actor2 = c2.actor;
                if (actor2 != null) {
                    actor2.setBounds(c2.actorX, actorY2, c2.actorWidth, actorHeight2);
                }
            }
        }
        Array<Actor> children = getChildren();
        int n3 = children.size;
        for (int i3 = 0; i3 < n3; i3++) {
            Actor child = children.get(i3);
            if (child instanceof Layout) {
                ((Layout) child).validate();
            }
        }
    }

    private void computeSize() {
        int cellCount;
        float uniformPrefWidth;
        float uniformMinHeight;
        Array<Cell> cells2;
        float uniformMinWidth;
        int nn;
        float f;
        float[] expandHeight2;
        float f2;
        float[] expandWidth2;
        this.sizeInvalid = false;
        Array<Cell> cells3 = this.cells;
        int cellCount2 = cells3.size;
        if (cellCount2 > 0 && !cells3.peek().endRow) {
            endRow();
            this.implicitEndRow = true;
        }
        int columns2 = this.columns;
        int rows2 = this.rows;
        float[] columnMinWidth2 = ensureSize(this.columnMinWidth, columns2);
        this.columnMinWidth = columnMinWidth2;
        float[] rowMinHeight2 = ensureSize(this.rowMinHeight, rows2);
        this.rowMinHeight = rowMinHeight2;
        float[] columnPrefWidth2 = ensureSize(this.columnPrefWidth, columns2);
        this.columnPrefWidth = columnPrefWidth2;
        float[] rowPrefHeight2 = ensureSize(this.rowPrefHeight, rows2);
        this.rowPrefHeight = rowPrefHeight2;
        float[] columnWidth2 = ensureSize(this.columnWidth, columns2);
        this.columnWidth = columnWidth2;
        float[] rowHeight2 = ensureSize(this.rowHeight, rows2);
        this.rowHeight = rowHeight2;
        float[] expandWidth3 = ensureSize(this.expandWidth, columns2);
        this.expandWidth = expandWidth3;
        float[] expandHeight3 = ensureSize(this.expandHeight, rows2);
        this.expandHeight = expandHeight3;
        float spaceRightLast = 0.0f;
        int i = 0;
        while (i < cellCount2) {
            Cell c = cells3.get(i);
            float[] columnWidth3 = columnWidth2;
            int column = c.column;
            float[] rowHeight3 = rowHeight2;
            int row = c.row;
            int colspan = c.colspan.intValue();
            int cellCount3 = cellCount2;
            Actor a = c.actor;
            int i2 = i;
            if (c.expandY.intValue() != 0 && expandHeight3[row] == 0.0f) {
                expandHeight3[row] = (float) c.expandY.intValue();
            }
            if (colspan == 1 && c.expandX.intValue() != 0 && expandWidth3[column] == 0.0f) {
                expandWidth3[column] = (float) c.expandX.intValue();
            }
            float f3 = c.padLeft.get(a);
            if (column == 0) {
                expandHeight2 = expandHeight3;
                float f4 = spaceRightLast;
                f2 = 0.0f;
            } else {
                expandHeight2 = expandHeight3;
                float f5 = spaceRightLast;
                f2 = Math.max(0.0f, c.spaceLeft.get(a) - spaceRightLast);
            }
            c.computedPadLeft = f3 + f2;
            c.computedPadTop = c.padTop.get(a);
            if (c.cellAboveIndex != -1) {
                expandWidth2 = expandWidth3;
                c.computedPadTop += Math.max(0.0f, c.spaceTop.get(a) - cells3.get(c.cellAboveIndex).spaceBottom.get(a));
            } else {
                expandWidth2 = expandWidth3;
            }
            float spaceRight = c.spaceRight.get(a);
            c.computedPadRight = c.padRight.get(a) + (column + colspan == columns2 ? 0.0f : spaceRight);
            c.computedPadBottom = c.padBottom.get(a) + (row == rows2 + -1 ? 0.0f : c.spaceBottom.get(a));
            float spaceRightLast2 = spaceRight;
            float prefWidth = c.prefWidth.get(a);
            float prefHeight = c.prefHeight.get(a);
            float f6 = spaceRight;
            float minWidth = c.minWidth.get(a);
            float spaceRightLast3 = spaceRightLast2;
            float minHeight = c.minHeight.get(a);
            int rows3 = rows2;
            float maxWidth = c.maxWidth.get(a);
            int columns3 = columns2;
            float maxHeight = c.maxHeight.get(a);
            if (prefWidth < minWidth) {
                prefWidth = minWidth;
            }
            if (prefHeight < minHeight) {
                prefHeight = minHeight;
            }
            if (maxWidth > 0.0f && prefWidth > maxWidth) {
                prefWidth = maxWidth;
            }
            if (maxHeight > 0.0f && prefHeight > maxHeight) {
                prefHeight = maxHeight;
            }
            Actor actor = a;
            if (colspan == 1) {
                int i3 = colspan;
                float hpadding = c.computedPadLeft + c.computedPadRight;
                float f7 = maxHeight;
                columnPrefWidth2[column] = Math.max(columnPrefWidth2[column], prefWidth + hpadding);
                columnMinWidth2[column] = Math.max(columnMinWidth2[column], minWidth + hpadding);
            } else {
                float f8 = maxHeight;
            }
            float vpadding = c.computedPadTop + c.computedPadBottom;
            rowPrefHeight2[row] = Math.max(rowPrefHeight2[row], prefHeight + vpadding);
            rowMinHeight2[row] = Math.max(rowMinHeight2[row], minHeight + vpadding);
            i = i2 + 1;
            columnWidth2 = columnWidth3;
            rowHeight2 = rowHeight3;
            cellCount2 = cellCount3;
            expandHeight3 = expandHeight2;
            spaceRightLast = spaceRightLast3;
            expandWidth3 = expandWidth2;
            rows2 = rows3;
            columns2 = columns3;
        }
        int cellCount4 = cellCount2;
        int columns4 = columns2;
        int rows4 = rows2;
        float[] fArr = columnWidth2;
        float[] fArr2 = rowHeight2;
        float[] expandWidth4 = expandWidth3;
        float[] fArr3 = expandHeight3;
        float f9 = spaceRightLast;
        int i4 = i;
        float uniformMinWidth2 = 0.0f;
        float uniformMinHeight2 = 0.0f;
        float uniformPrefWidth2 = 0.0f;
        float uniformPrefHeight = 0.0f;
        int i5 = 0;
        while (true) {
            cellCount = cellCount4;
            if (i5 >= cellCount) {
                break;
            }
            Cell c2 = cells3.get(i5);
            int column2 = c2.column;
            int expandX = c2.expandX.intValue();
            if (expandX != 0) {
                int nn2 = c2.colspan.intValue() + column2;
                int ii = column2;
                while (true) {
                    if (ii >= nn2) {
                        int ii2 = column2;
                        while (ii2 < nn2) {
                            expandWidth4[ii2] = (float) expandX;
                            ii2++;
                            nn2 = nn2;
                        }
                    } else if (expandWidth4[ii] != 0.0f) {
                        break;
                    } else {
                        ii++;
                    }
                }
            }
            if (c2.uniformX == Boolean.TRUE && c2.colspan.intValue() == 1) {
                float hpadding2 = c2.computedPadLeft + c2.computedPadRight;
                uniformMinWidth2 = Math.max(uniformMinWidth2, columnMinWidth2[column2] - hpadding2);
                uniformPrefWidth2 = Math.max(uniformPrefWidth2, columnPrefWidth2[column2] - hpadding2);
            }
            if (c2.uniformY == Boolean.TRUE) {
                float vpadding2 = c2.computedPadTop + c2.computedPadBottom;
                uniformMinHeight2 = Math.max(uniformMinHeight2, rowMinHeight2[c2.row] - vpadding2);
                uniformPrefHeight = Math.max(uniformPrefHeight, rowPrefHeight2[c2.row] - vpadding2);
            }
            i5++;
            cellCount4 = cellCount;
        }
        if (uniformPrefWidth2 > 0.0f || uniformPrefHeight > 0.0f) {
            for (int i6 = 0; i6 < cellCount; i6++) {
                Cell c3 = cells3.get(i6);
                if (uniformPrefWidth2 > 0.0f && c3.uniformX == Boolean.TRUE && c3.colspan.intValue() == 1) {
                    float hpadding3 = c3.computedPadLeft + c3.computedPadRight;
                    columnMinWidth2[c3.column] = uniformMinWidth2 + hpadding3;
                    columnPrefWidth2[c3.column] = uniformPrefWidth2 + hpadding3;
                }
                if (uniformPrefHeight > 0.0f && c3.uniformY == Boolean.TRUE) {
                    float vpadding3 = c3.computedPadTop + c3.computedPadBottom;
                    rowMinHeight2[c3.row] = uniformMinHeight2 + vpadding3;
                    rowPrefHeight2[c3.row] = uniformPrefHeight + vpadding3;
                }
            }
        }
        int i7 = 0;
        while (i7 < cellCount) {
            Cell c4 = cells3.get(i7);
            int colspan2 = c4.colspan.intValue();
            if (colspan2 == 1) {
                uniformMinWidth = uniformMinWidth2;
                cells2 = cells3;
                uniformMinHeight = uniformMinHeight2;
                uniformPrefWidth = uniformPrefWidth2;
            } else {
                int column3 = c4.column;
                Actor a2 = c4.actor;
                float minWidth2 = c4.minWidth.get(a2);
                uniformMinWidth = uniformMinWidth2;
                float prefWidth2 = c4.prefWidth.get(a2);
                cells2 = cells3;
                float maxWidth2 = c4.maxWidth.get(a2);
                if (prefWidth2 < minWidth2) {
                    prefWidth2 = minWidth2;
                }
                if (maxWidth2 > 0.0f && prefWidth2 > maxWidth2) {
                    prefWidth2 = maxWidth2;
                }
                float f10 = maxWidth2;
                uniformMinHeight = uniformMinHeight2;
                float spannedMinWidth = -(c4.computedPadLeft + c4.computedPadRight);
                float spannedPrefWidth = spannedMinWidth;
                int ii3 = column3;
                float spannedMinWidth2 = spannedMinWidth;
                int nn3 = ii3 + colspan2;
                float spannedPrefWidth2 = spannedPrefWidth;
                float totalExpandWidth = 0.0f;
                for (int ii4 = ii3; ii4 < nn3; ii4++) {
                    spannedMinWidth2 += columnMinWidth2[ii4];
                    spannedPrefWidth2 += columnPrefWidth2[ii4];
                    totalExpandWidth += expandWidth4[ii4];
                }
                float extraMinWidth = Math.max(0.0f, minWidth2 - spannedMinWidth2);
                uniformPrefWidth = uniformPrefWidth2;
                float extraPrefWidth = Math.max(0.0f, prefWidth2 - spannedPrefWidth2);
                int ii5 = column3;
                int nn4 = ii5 + colspan2;
                float f11 = prefWidth2;
                int ii6 = ii5;
                while (ii6 < nn4) {
                    if (totalExpandWidth == 0.0f) {
                        nn = nn4;
                        f = 1.0f / ((float) colspan2);
                    } else {
                        nn = nn4;
                        f = expandWidth4[ii6] / totalExpandWidth;
                    }
                    float ratio = f;
                    columnMinWidth2[ii6] = columnMinWidth2[ii6] + (extraMinWidth * ratio);
                    columnPrefWidth2[ii6] = columnPrefWidth2[ii6] + (extraPrefWidth * ratio);
                    ii6++;
                    nn4 = nn;
                }
            }
            i7++;
            uniformMinWidth2 = uniformMinWidth;
            cells3 = cells2;
            uniformMinHeight2 = uniformMinHeight;
            uniformPrefWidth2 = uniformPrefWidth;
        }
        Array<Cell> array = cells3;
        float f12 = uniformMinHeight2;
        float f13 = uniformPrefWidth2;
        this.tableMinWidth = 0.0f;
        this.tableMinHeight = 0.0f;
        this.tablePrefWidth = 0.0f;
        this.tablePrefHeight = 0.0f;
        int i8 = 0;
        while (true) {
            int columns5 = columns4;
            if (i8 >= columns5) {
                break;
            }
            this.tableMinWidth += columnMinWidth2[i8];
            this.tablePrefWidth += columnPrefWidth2[i8];
            i8++;
            columns4 = columns5;
        }
        int i9 = 0;
        while (true) {
            int rows5 = rows4;
            if (i9 < rows5) {
                this.tableMinHeight += rowMinHeight2[i9];
                this.tablePrefHeight += Math.max(rowMinHeight2[i9], rowPrefHeight2[i9]);
                i9++;
                rows4 = rows5;
            } else {
                float hpadding4 = this.padLeft.get(this) + this.padRight.get(this);
                float vpadding4 = this.padTop.get(this) + this.padBottom.get(this);
                this.tableMinWidth += hpadding4;
                this.tableMinHeight += vpadding4;
                this.tablePrefWidth = Math.max(this.tablePrefWidth + hpadding4, this.tableMinWidth);
                this.tablePrefHeight = Math.max(this.tablePrefHeight + vpadding4, this.tableMinHeight);
                return;
            }
        }
    }

    private void layout(float layoutX, float layoutY, float layoutWidth, float layoutHeight) {
        float[] expandHeight2;
        float padLeft2;
        float padTop2;
        float[] columnPrefWidth2;
        float totalGrowWidth;
        float[] rowWeightedHeight2;
        int columns2;
        int rows2;
        int cellCount;
        float x;
        float y;
        float[] columnWidth2;
        float[] rowHeight2;
        int cellCount2;
        Cell c;
        int rows3;
        float[] columnWidth3;
        Array<Cell> cells2 = this.cells;
        int cellCount3 = cells2.size;
        if (this.sizeInvalid) {
            computeSize();
        }
        float padLeft3 = this.padLeft.get(this);
        float hpadding = padLeft3 + this.padRight.get(this);
        float padTop3 = this.padTop.get(this);
        float vpadding = padTop3 + this.padBottom.get(this);
        int columns3 = this.columns;
        int rows4 = this.rows;
        float[] expandWidth2 = this.expandWidth;
        float[] expandHeight3 = this.expandHeight;
        float[] columnWidth4 = this.columnWidth;
        float[] rowHeight3 = this.rowHeight;
        float totalExpandWidth = 0.0f;
        for (int i = 0; i < columns3; i++) {
            totalExpandWidth += expandWidth2[i];
        }
        float totalExpandHeight = 0.0f;
        for (int i2 = 0; i2 < rows4; i2++) {
            totalExpandHeight += expandHeight3[i2];
        }
        float f = this.tablePrefWidth;
        float f2 = this.tableMinWidth;
        float totalGrowWidth2 = f - f2;
        if (totalGrowWidth2 == 0.0f) {
            expandHeight2 = expandHeight3;
            padLeft2 = padLeft3;
            padTop2 = padTop3;
            columnPrefWidth2 = this.columnMinWidth;
        } else {
            float extraWidth = Math.min(totalGrowWidth2, Math.max(0.0f, layoutWidth - f2));
            float[] columnWeightedWidth2 = ensureSize(columnWeightedWidth, columns3);
            columnWeightedWidth = columnWeightedWidth2;
            padTop2 = padTop3;
            float[] columnMinWidth2 = this.columnMinWidth;
            padLeft2 = padLeft3;
            float[] columnPrefWidth3 = this.columnPrefWidth;
            expandHeight2 = expandHeight3;
            for (int i3 = 0; i3 < columns3; i3++) {
                columnWeightedWidth2[i3] = columnMinWidth2[i3] + (extraWidth * ((columnPrefWidth3[i3] - columnMinWidth2[i3]) / totalGrowWidth2));
            }
            columnPrefWidth2 = columnWeightedWidth2;
        }
        float totalGrowHeight = this.tablePrefHeight - this.tableMinHeight;
        if (totalGrowHeight == 0.0f) {
            rowWeightedHeight2 = this.rowMinHeight;
            totalGrowWidth = totalGrowWidth2;
        } else {
            float[] rowWeightedHeight3 = ensureSize(rowWeightedHeight, rows4);
            rowWeightedHeight = rowWeightedHeight3;
            float extraHeight = Math.min(totalGrowHeight, Math.max(0.0f, layoutHeight - this.tableMinHeight));
            float[] rowMinHeight2 = this.rowMinHeight;
            totalGrowWidth = totalGrowWidth2;
            float[] rowPrefHeight2 = this.rowPrefHeight;
            for (int i4 = 0; i4 < rows4; i4++) {
                rowWeightedHeight3[i4] = rowMinHeight2[i4] + (extraHeight * ((rowPrefHeight2[i4] - rowMinHeight2[i4]) / totalGrowHeight));
            }
            rowWeightedHeight2 = rowWeightedHeight3;
        }
        int i5 = 0;
        while (i5 < cellCount3) {
            Cell c2 = cells2.get(i5);
            int column = c2.column;
            int row = c2.row;
            float totalGrowHeight2 = totalGrowHeight;
            Actor a = c2.actor;
            Array<Cell> cells3 = cells2;
            int colspan = c2.colspan.intValue();
            int ii = column;
            int cellCount4 = cellCount3;
            int nn = ii + colspan;
            int rows5 = rows4;
            float vpadding2 = vpadding;
            float spannedWeightedWidth = 0.0f;
            for (int ii2 = ii; ii2 < nn; ii2++) {
                spannedWeightedWidth += columnPrefWidth2[ii2];
            }
            float weightedHeight = rowWeightedHeight2[row];
            float prefWidth = c2.prefWidth.get(a);
            float[] rowWeightedHeight4 = rowWeightedHeight2;
            float prefHeight = c2.prefHeight.get(a);
            float[] columnWeightedWidth3 = columnPrefWidth2;
            float minWidth = c2.minWidth.get(a);
            float[] expandWidth3 = expandWidth2;
            float minHeight = c2.minHeight.get(a);
            int columns4 = columns3;
            float maxWidth = c2.maxWidth.get(a);
            float hpadding2 = hpadding;
            float maxHeight = c2.maxHeight.get(a);
            if (prefWidth < minWidth) {
                prefWidth = minWidth;
            }
            if (prefHeight < minHeight) {
                prefHeight = minHeight;
            }
            if (maxWidth > 0.0f && prefWidth > maxWidth) {
                prefWidth = maxWidth;
            }
            if (maxHeight > 0.0f && prefHeight > maxHeight) {
                prefHeight = maxHeight;
            }
            float f3 = minWidth;
            float f4 = maxHeight;
            c2.actorWidth = Math.min((spannedWeightedWidth - c2.computedPadLeft) - c2.computedPadRight, prefWidth);
            c2.actorHeight = Math.min((weightedHeight - c2.computedPadTop) - c2.computedPadBottom, prefHeight);
            if (colspan == 1) {
                columnWidth4[column] = Math.max(columnWidth4[column], spannedWeightedWidth);
            }
            rowHeight3[row] = Math.max(rowHeight3[row], weightedHeight);
            i5++;
            totalGrowHeight = totalGrowHeight2;
            vpadding = vpadding2;
            cells2 = cells3;
            rowWeightedHeight2 = rowWeightedHeight4;
            cellCount3 = cellCount4;
            rows4 = rows5;
            columnPrefWidth2 = columnWeightedWidth3;
            expandWidth2 = expandWidth3;
            columns3 = columns4;
            hpadding = hpadding2;
        }
        Array<Cell> cells4 = cells2;
        int cellCount5 = cellCount3;
        float[] columnWeightedWidth4 = columnPrefWidth2;
        float hpadding3 = hpadding;
        float f5 = totalGrowHeight;
        float vpadding3 = vpadding;
        int columns5 = columns3;
        int rows6 = rows4;
        float[] expandWidth4 = expandWidth2;
        if (totalExpandWidth > 0.0f) {
            float extra = layoutWidth - hpadding3;
            int i6 = 0;
            while (true) {
                columns2 = columns5;
                if (i6 >= columns2) {
                    break;
                }
                extra -= columnWidth4[i6];
                i6++;
                columns5 = columns2;
            }
            if (extra > 0.0f) {
                float used = 0.0f;
                int lastIndex = 0;
                for (int i7 = 0; i7 < columns2; i7++) {
                    if (expandWidth4[i7] != 0.0f) {
                        float amount = (expandWidth4[i7] * extra) / totalExpandWidth;
                        columnWidth4[i7] = columnWidth4[i7] + amount;
                        used += amount;
                        lastIndex = i7;
                    }
                }
                columnWidth4[lastIndex] = columnWidth4[lastIndex] + (extra - used);
            }
        } else {
            columns2 = columns5;
        }
        if (totalExpandHeight > 0.0f) {
            float extra2 = layoutHeight - vpadding3;
            int i8 = 0;
            while (true) {
                rows2 = rows6;
                if (i8 >= rows2) {
                    break;
                }
                extra2 -= rowHeight3[i8];
                i8++;
                rows6 = rows2;
            }
            if (extra2 > 0.0f) {
                float used2 = 0.0f;
                int lastIndex2 = 0;
                for (int i9 = 0; i9 < rows2; i9++) {
                    if (expandHeight2[i9] != 0.0f) {
                        float amount2 = (expandHeight2[i9] * extra2) / totalExpandHeight;
                        rowHeight3[i9] = rowHeight3[i9] + amount2;
                        used2 += amount2;
                        lastIndex2 = i9;
                    }
                }
                rowHeight3[lastIndex2] = rowHeight3[lastIndex2] + (extra2 - used2);
            }
        } else {
            rows2 = rows6;
        }
        int i10 = 0;
        while (true) {
            cellCount = cellCount5;
            if (i10 >= cellCount) {
                break;
            }
            Array<Cell> cells5 = cells4;
            Cell c3 = cells5.get(i10);
            int colspan2 = c3.colspan.intValue();
            if (colspan2 != 1) {
                float extraWidth2 = 0.0f;
                int column2 = c3.column;
                int nn2 = column2 + colspan2;
                while (column2 < nn2) {
                    extraWidth2 += columnWeightedWidth4[column2] - columnWidth4[column2];
                    column2++;
                }
                float extraWidth3 = (extraWidth2 - Math.max(0.0f, c3.computedPadLeft + c3.computedPadRight)) / ((float) colspan2);
                if (extraWidth3 > 0.0f) {
                    int column3 = c3.column;
                    int nn3 = column3 + colspan2;
                    while (column3 < nn3) {
                        columnWidth4[column3] = columnWidth4[column3] + extraWidth3;
                        column3++;
                    }
                }
            }
            i10++;
            cellCount5 = cellCount;
            cells4 = cells5;
        }
        Array<Cell> cells6 = cells4;
        float tableHeight = vpadding3;
        float tableWidth = hpadding3;
        for (int i11 = 0; i11 < columns2; i11++) {
            tableWidth += columnWidth4[i11];
        }
        float tableHeight2 = tableHeight;
        for (int i12 = 0; i12 < rows2; i12++) {
            tableHeight2 += rowHeight3[i12];
        }
        int align2 = this.align;
        float x2 = layoutX + padLeft2;
        if ((align2 & 16) != 0) {
            x = x2 + (layoutWidth - tableWidth);
        } else if ((align2 & 8) == 0) {
            x = x2 + ((layoutWidth - tableWidth) / 2.0f);
        } else {
            x = x2;
        }
        float x3 = layoutY + padTop2;
        if ((align2 & 4) != 0) {
            y = x3 + (layoutHeight - tableHeight2);
        } else if ((align2 & 2) == 0) {
            y = x3 + ((layoutHeight - tableHeight2) / 2.0f);
        } else {
            y = x3;
        }
        float currentX = x;
        float currentY = y;
        int i13 = 0;
        int i14 = align2;
        while (i13 < cellCount) {
            Cell c4 = cells6.get(i13);
            float spannedCellWidth = 0.0f;
            int column4 = c4.column;
            int columns6 = columns2;
            int nn4 = c4.colspan.intValue() + column4;
            while (column4 < nn4) {
                spannedCellWidth += columnWidth4[column4];
                column4++;
            }
            float spannedCellWidth2 = spannedCellWidth - (c4.computedPadLeft + c4.computedPadRight);
            float currentX2 = currentX + c4.computedPadLeft;
            float fillX = c4.fillX.floatValue();
            float fillY = c4.fillY.floatValue();
            if (fillX > 0.0f) {
                columnWidth3 = columnWidth4;
                float f6 = fillX;
                rows3 = rows2;
                c4.actorWidth = Math.max(spannedCellWidth2 * fillX, c4.minWidth.get(c4.actor));
                float maxWidth2 = c4.maxWidth.get(c4.actor);
                if (maxWidth2 > 0.0f) {
                    c4.actorWidth = Math.min(c4.actorWidth, maxWidth2);
                }
            } else {
                columnWidth3 = columnWidth4;
                float f7 = fillX;
                rows3 = rows2;
            }
            if (fillY > 0.0f) {
                c4.actorHeight = Math.max(((rowHeight3[c4.row] * fillY) - c4.computedPadTop) - c4.computedPadBottom, c4.minHeight.get(c4.actor));
                float maxHeight2 = c4.maxHeight.get(c4.actor);
                if (maxHeight2 > 0.0f) {
                    c4.actorHeight = Math.min(c4.actorHeight, maxHeight2);
                }
            }
            int align3 = c4.align.intValue();
            if ((align3 & 8) != 0) {
                c4.actorX = currentX2;
            } else if ((align3 & 16) != 0) {
                c4.actorX = (currentX2 + spannedCellWidth2) - c4.actorWidth;
            } else {
                c4.actorX = ((spannedCellWidth2 - c4.actorWidth) / 2.0f) + currentX2;
            }
            if ((align3 & 2) != 0) {
                c4.actorY = c4.computedPadTop + currentY;
            } else if ((align3 & 4) != 0) {
                c4.actorY = ((rowHeight3[c4.row] + currentY) - c4.actorHeight) - c4.computedPadBottom;
            } else {
                c4.actorY = ((((rowHeight3[c4.row] - c4.actorHeight) + c4.computedPadTop) - c4.computedPadBottom) / 2.0f) + currentY;
            }
            if (c4.endRow) {
                currentX = x;
                currentY += rowHeight3[c4.row];
            } else {
                currentX = currentX2 + spannedCellWidth2 + c4.computedPadRight;
            }
            i13++;
            columnWidth4 = columnWidth3;
            columns2 = columns6;
            rows2 = rows3;
        }
        float[] columnWidth5 = columnWidth4;
        int i15 = columns2;
        int i16 = rows2;
        if (this.debug != Debug.none) {
            clearDebugRects();
            float currentX3 = x;
            float currentY2 = y;
            if (this.debug == Debug.table || this.debug == Debug.all) {
                float f8 = totalGrowWidth;
                rowHeight2 = rowHeight3;
                columnWidth2 = columnWidth5;
                addDebugRect(layoutX, layoutY, layoutWidth, layoutHeight, debugTableColor);
                addDebugRect(x, y, tableWidth - hpadding3, tableHeight2 - vpadding3, debugTableColor);
            } else {
                rowHeight2 = rowHeight3;
                float f9 = totalGrowWidth;
                columnWidth2 = columnWidth5;
            }
            float currentY3 = currentY2;
            float currentX4 = currentX3;
            int i17 = 0;
            while (i17 < cellCount) {
                Cell c5 = cells6.get(i17);
                if (this.debug == Debug.actor || this.debug == Debug.all) {
                    cellCount2 = cellCount;
                    c = c5;
                    addDebugRect(c5.actorX, c5.actorY, c5.actorWidth, c5.actorHeight, debugActorColor);
                } else {
                    cellCount2 = cellCount;
                    c = c5;
                }
                float spannedCellWidth3 = 0.0f;
                int column5 = c.column;
                int nn5 = c.colspan.intValue() + column5;
                while (column5 < nn5) {
                    spannedCellWidth3 += columnWidth2[column5];
                    column5++;
                }
                float spannedCellWidth4 = spannedCellWidth3 - (c.computedPadLeft + c.computedPadRight);
                float currentX5 = currentX4 + c.computedPadLeft;
                if (this.debug == Debug.cell || this.debug == Debug.all) {
                    addDebugRect(currentX5, currentY3 + c.computedPadTop, spannedCellWidth4, (rowHeight2[c.row] - c.computedPadTop) - c.computedPadBottom, debugCellColor);
                }
                if (c.endRow) {
                    currentY3 += rowHeight2[c.row];
                    currentX4 = x;
                } else {
                    currentX4 = currentX5 + spannedCellWidth4 + c.computedPadRight;
                }
                i17++;
                cellCount = cellCount2;
            }
        }
    }

    private void clearDebugRects() {
        if (this.debugRects != null) {
            DebugRect.pool.freeAll(this.debugRects);
            this.debugRects.clear();
        }
    }

    private void addDebugRect(float x, float y, float w, float h, Color color) {
        if (this.debugRects == null) {
            this.debugRects = new Array<>();
        }
        DebugRect rect = DebugRect.pool.obtain();
        rect.color = color;
        rect.set(x, (getHeight() - y) - h, w, h);
        this.debugRects.add(rect);
    }

    public void drawDebug(ShapeRenderer shapes) {
        if (isTransform()) {
            applyTransform(shapes, computeTransform());
            drawDebugRects(shapes);
            if (this.clip) {
                shapes.flush();
                float x = 0.0f;
                float y = 0.0f;
                float width = getWidth();
                float height = getHeight();
                if (this.background != null) {
                    x = this.padLeft.get(this);
                    y = this.padBottom.get(this);
                    width -= this.padRight.get(this) + x;
                    height -= this.padTop.get(this) + y;
                }
                if (clipBegin(x, y, width, height)) {
                    drawDebugChildren(shapes);
                    clipEnd();
                }
            } else {
                drawDebugChildren(shapes);
            }
            resetTransform(shapes);
            return;
        }
        drawDebugRects(shapes);
        super.drawDebug(shapes);
    }

    /* access modifiers changed from: protected */
    public void drawDebugBounds(ShapeRenderer shapes) {
    }

    private void drawDebugRects(ShapeRenderer shapes) {
        if (this.debugRects != null && getDebug()) {
            shapes.set(ShapeRenderer.ShapeType.Line);
            if (getStage() != null) {
                shapes.setColor(getStage().getDebugColor());
            }
            float x = 0.0f;
            float y = 0.0f;
            if (!isTransform()) {
                x = getX();
                y = getY();
            }
            int n = this.debugRects.size;
            for (int i = 0; i < n; i++) {
                DebugRect debugRect = this.debugRects.get(i);
                shapes.setColor(debugRect.color);
                shapes.rect(debugRect.x + x, debugRect.y + y, debugRect.width, debugRect.height);
            }
        }
    }

    public Skin getSkin() {
        return this.skin;
    }
}
