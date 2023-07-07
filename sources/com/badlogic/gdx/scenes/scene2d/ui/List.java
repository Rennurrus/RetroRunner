package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ArraySelection;
import com.badlogic.gdx.scenes.scene2d.utils.Cullable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.twi.game.BuildConfig;

public class List<T> extends Widget implements Cullable {
    private int alignment;
    private Rectangle cullingArea;
    float itemHeight;
    final Array<T> items;
    private InputListener keyListener;
    int overIndex;
    private float prefHeight;
    private float prefWidth;
    int pressedIndex;
    ArraySelection<T> selection;
    ListStyle style;
    boolean typeToSelect;

    public List(Skin skin) {
        this((ListStyle) skin.get(ListStyle.class));
    }

    public List(Skin skin, String styleName) {
        this((ListStyle) skin.get(styleName, ListStyle.class));
    }

    public List(ListStyle style2) {
        this.items = new Array<>();
        this.selection = new ArraySelection<>(this.items);
        this.alignment = 8;
        this.pressedIndex = -1;
        this.overIndex = -1;
        this.selection.setActor(this);
        this.selection.setRequired(true);
        setStyle(style2);
        setSize(getPrefWidth(), getPrefHeight());
        AnonymousClass1 r0 = new InputListener() {
            String prefix;
            long typeTimeout;

            public boolean keyDown(InputEvent event, int keycode) {
                if (List.this.items.isEmpty()) {
                    return false;
                }
                if (keycode != 3) {
                    if (keycode != 29) {
                        if (keycode == 19) {
                            int index = List.this.items.indexOf(List.this.getSelected(), false) - 1;
                            if (index < 0) {
                                index = List.this.items.size - 1;
                            }
                            List.this.setSelectedIndex(index);
                            return true;
                        } else if (keycode == 20) {
                            int index2 = List.this.items.indexOf(List.this.getSelected(), false) + 1;
                            if (index2 >= List.this.items.size) {
                                index2 = 0;
                            }
                            List.this.setSelectedIndex(index2);
                            return true;
                        } else if (keycode == 131) {
                            if (List.this.getStage() != null) {
                                List.this.getStage().setKeyboardFocus((Actor) null);
                            }
                            return true;
                        } else if (keycode == 132) {
                            List list = List.this;
                            list.setSelectedIndex(list.items.size - 1);
                            return true;
                        }
                    } else if (UIUtils.ctrl() != 0 && List.this.selection.getMultiple()) {
                        List.this.selection.clear();
                        List.this.selection.addAll(List.this.items);
                        return true;
                    }
                    return false;
                }
                List.this.setSelectedIndex(0);
                return true;
            }

            public boolean keyTyped(InputEvent event, char character) {
                if (!List.this.typeToSelect) {
                    return false;
                }
                long time = System.currentTimeMillis();
                if (time > this.typeTimeout) {
                    this.prefix = BuildConfig.FLAVOR;
                }
                this.typeTimeout = 300 + time;
                this.prefix += Character.toLowerCase(character);
                int i = 0;
                int n = List.this.items.size;
                while (true) {
                    if (i >= n) {
                        break;
                    }
                    List list = List.this;
                    if (list.toString(list.items.get(i)).toLowerCase().startsWith(this.prefix)) {
                        List.this.setSelectedIndex(i);
                        break;
                    }
                    i++;
                }
                return false;
            }
        };
        this.keyListener = r0;
        addListener(r0);
        addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                int index;
                if (pointer != 0 || button != 0 || List.this.selection.isDisabled()) {
                    return true;
                }
                if (List.this.getStage() != null) {
                    List.this.getStage().setKeyboardFocus(List.this);
                }
                if (List.this.items.size == 0 || (index = List.this.getItemIndexAt(y)) == -1) {
                    return true;
                }
                List.this.selection.choose(List.this.items.get(index));
                List.this.pressedIndex = index;
                return true;
            }

            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (pointer == 0 && button == 0) {
                    List.this.pressedIndex = -1;
                }
            }

            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                List list = List.this;
                list.overIndex = list.getItemIndexAt(y);
            }

            public boolean mouseMoved(InputEvent event, float x, float y) {
                List list = List.this;
                list.overIndex = list.getItemIndexAt(y);
                return false;
            }

            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (pointer == 0) {
                    List.this.pressedIndex = -1;
                }
                if (pointer == -1) {
                    List.this.overIndex = -1;
                }
            }
        });
    }

    public void setStyle(ListStyle style2) {
        if (style2 != null) {
            this.style = style2;
            invalidateHierarchy();
            return;
        }
        throw new IllegalArgumentException("style cannot be null.");
    }

    public ListStyle getStyle() {
        return this.style;
    }

    public void layout() {
        BitmapFont font = this.style.font;
        Drawable selectedDrawable = this.style.selection;
        this.itemHeight = font.getCapHeight() - (font.getDescent() * 2.0f);
        this.itemHeight += selectedDrawable.getTopHeight() + selectedDrawable.getBottomHeight();
        this.prefWidth = 0.0f;
        Pool<GlyphLayout> layoutPool = Pools.get(GlyphLayout.class);
        GlyphLayout layout = layoutPool.obtain();
        for (int i = 0; i < this.items.size; i++) {
            layout.setText(font, toString(this.items.get(i)));
            this.prefWidth = Math.max(layout.width, this.prefWidth);
        }
        layoutPool.free(layout);
        this.prefWidth += selectedDrawable.getLeftWidth() + selectedDrawable.getRightWidth();
        this.prefHeight = ((float) this.items.size) * this.itemHeight;
        Drawable background = this.style.background;
        if (background != null) {
            this.prefWidth += background.getLeftWidth() + background.getRightWidth();
            this.prefHeight += background.getTopHeight() + background.getBottomHeight();
        }
    }

    public void draw(Batch batch, float parentAlpha) {
        float x;
        float width;
        int i;
        Drawable background;
        Drawable drawable;
        validate();
        drawBackground(batch, parentAlpha);
        BitmapFont font = this.style.font;
        Drawable selectedDrawable = this.style.selection;
        Color fontColorSelected = this.style.fontColorSelected;
        Color fontColorUnselected = this.style.fontColorUnselected;
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        float x2 = getX();
        float y = getY();
        float width2 = getWidth();
        float itemY = getHeight();
        Drawable background2 = this.style.background;
        if (background2 != null) {
            float leftWidth = background2.getLeftWidth();
            itemY -= background2.getTopHeight();
            x = x2 + leftWidth;
            width = width2 - (background2.getRightWidth() + leftWidth);
        } else {
            x = x2;
            width = width2;
        }
        float textOffsetX = selectedDrawable.getLeftWidth();
        float textWidth = (width - textOffsetX) - selectedDrawable.getRightWidth();
        float textOffsetY = selectedDrawable.getTopHeight() - font.getDescent();
        font.setColor(fontColorUnselected.r, fontColorUnselected.g, fontColorUnselected.b, fontColorUnselected.a * parentAlpha);
        int i2 = 0;
        float itemY2 = itemY;
        while (i2 < this.items.size) {
            Rectangle rectangle = this.cullingArea;
            if (rectangle == null || (itemY2 - this.itemHeight <= rectangle.y + this.cullingArea.height && itemY2 >= this.cullingArea.y)) {
                T item = this.items.get(i2);
                boolean selected = this.selection.contains(item);
                if (this.pressedIndex == i2 && this.style.down != null) {
                    drawable = this.style.down;
                } else if (selected) {
                    font.setColor(fontColorSelected.r, fontColorSelected.g, fontColorSelected.b, fontColorSelected.a * parentAlpha);
                    drawable = selectedDrawable;
                } else if (this.overIndex != i2 || this.style.over == null) {
                    drawable = null;
                } else {
                    drawable = this.style.over;
                }
                if (drawable != null) {
                    float f = this.itemHeight;
                    drawable.draw(batch, x, (y + itemY2) - f, width, f);
                }
                T t = item;
                i = i2;
                background = background2;
                drawItem(batch, font, i2, item, x + textOffsetX, (y + itemY2) - textOffsetY, textWidth);
                if (selected) {
                    font.setColor(fontColorUnselected.r, fontColorUnselected.g, fontColorUnselected.b, fontColorUnselected.a * parentAlpha);
                }
            } else if (itemY2 < this.cullingArea.y) {
                Drawable drawable2 = background2;
                return;
            } else {
                i = i2;
                background = background2;
            }
            itemY2 -= this.itemHeight;
            i2 = i + 1;
            Batch batch2 = batch;
            background2 = background;
        }
        int i3 = i2;
        Drawable drawable3 = background2;
    }

    /* access modifiers changed from: protected */
    public void drawBackground(Batch batch, float parentAlpha) {
        if (this.style.background != null) {
            Color color = getColor();
            batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
            this.style.background.draw(batch, getX(), getY(), getWidth(), getHeight());
        }
    }

    /* access modifiers changed from: protected */
    public GlyphLayout drawItem(Batch batch, BitmapFont font, int index, T item, float x, float y, float width) {
        String string = toString(item);
        return font.draw(batch, string, x, y, 0, string.length(), width, this.alignment, false, "...");
    }

    public ArraySelection<T> getSelection() {
        return this.selection;
    }

    public void setSelection(ArraySelection<T> selection2) {
        this.selection = selection2;
    }

    public T getSelected() {
        return this.selection.first();
    }

    public void setSelected(T item) {
        if (this.items.contains(item, false)) {
            this.selection.set(item);
        } else if (!this.selection.getRequired() || this.items.size <= 0) {
            this.selection.clear();
        } else {
            this.selection.set(this.items.first());
        }
    }

    public int getSelectedIndex() {
        ObjectSet<T> selected = this.selection.items();
        if (selected.size == 0) {
            return -1;
        }
        return this.items.indexOf(selected.first(), false);
    }

    public void setSelectedIndex(int index) {
        if (index < -1 || index >= this.items.size) {
            throw new IllegalArgumentException("index must be >= -1 and < " + this.items.size + ": " + index);
        } else if (index == -1) {
            this.selection.clear();
        } else {
            this.selection.set(this.items.get(index));
        }
    }

    public T getOverItem() {
        int i = this.overIndex;
        if (i == -1) {
            return null;
        }
        return this.items.get(i);
    }

    public T getPressedItem() {
        int i = this.pressedIndex;
        if (i == -1) {
            return null;
        }
        return this.items.get(i);
    }

    public T getItemAt(float y) {
        int index = getItemIndexAt(y);
        if (index == -1) {
            return null;
        }
        return this.items.get(index);
    }

    public int getItemIndexAt(float y) {
        float height = getHeight();
        Drawable background = this.style.background;
        if (background != null) {
            height -= background.getTopHeight() + background.getBottomHeight();
            y -= background.getBottomHeight();
        }
        int index = (int) ((height - y) / this.itemHeight);
        if (index < 0 || index >= this.items.size) {
            return -1;
        }
        return index;
    }

    public void setItems(T... newItems) {
        if (newItems != null) {
            float oldPrefWidth = getPrefWidth();
            float oldPrefHeight = getPrefHeight();
            this.items.clear();
            this.items.addAll(newItems);
            this.overIndex = -1;
            this.pressedIndex = -1;
            this.selection.validate();
            invalidate();
            if (oldPrefWidth != getPrefWidth() || oldPrefHeight != getPrefHeight()) {
                invalidateHierarchy();
                return;
            }
            return;
        }
        throw new IllegalArgumentException("newItems cannot be null.");
    }

    public void setItems(Array newItems) {
        if (newItems != null) {
            float oldPrefWidth = getPrefWidth();
            float oldPrefHeight = getPrefHeight();
            Array<T> array = this.items;
            if (newItems != array) {
                array.clear();
                this.items.addAll(newItems);
            }
            this.overIndex = -1;
            this.pressedIndex = -1;
            this.selection.validate();
            invalidate();
            if (oldPrefWidth != getPrefWidth() || oldPrefHeight != getPrefHeight()) {
                invalidateHierarchy();
                return;
            }
            return;
        }
        throw new IllegalArgumentException("newItems cannot be null.");
    }

    public void clearItems() {
        if (this.items.size != 0) {
            this.items.clear();
            this.overIndex = -1;
            this.pressedIndex = -1;
            this.selection.clear();
            invalidateHierarchy();
        }
    }

    public Array<T> getItems() {
        return this.items;
    }

    public float getItemHeight() {
        return this.itemHeight;
    }

    public float getPrefWidth() {
        validate();
        return this.prefWidth;
    }

    public float getPrefHeight() {
        validate();
        return this.prefHeight;
    }

    public String toString(T object) {
        return object.toString();
    }

    public void setCullingArea(Rectangle cullingArea2) {
        this.cullingArea = cullingArea2;
    }

    public Rectangle getCullingArea() {
        return this.cullingArea;
    }

    public void setAlignment(int alignment2) {
        this.alignment = alignment2;
    }

    public void setTypeToSelect(boolean typeToSelect2) {
        this.typeToSelect = typeToSelect2;
    }

    public InputListener getKeyListener() {
        return this.keyListener;
    }

    public static class ListStyle {
        public Drawable background;
        public Drawable down;
        public BitmapFont font;
        public Color fontColorSelected = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        public Color fontColorUnselected = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        public Drawable over;
        public Drawable selection;

        public ListStyle() {
        }

        public ListStyle(BitmapFont font2, Color fontColorSelected2, Color fontColorUnselected2, Drawable selection2) {
            this.font = font2;
            this.fontColorSelected.set(fontColorSelected2);
            this.fontColorUnselected.set(fontColorUnselected2);
            this.selection = selection2;
        }

        public ListStyle(ListStyle style) {
            this.font = style.font;
            this.fontColorSelected.set(style.fontColorSelected);
            this.fontColorUnselected.set(style.fontColorUnselected);
            this.selection = style.selection;
            this.down = style.down;
            this.over = style.over;
            this.background = style.background;
        }
    }
}
