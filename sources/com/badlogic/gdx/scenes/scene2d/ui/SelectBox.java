package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.ArraySelection;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;

public class SelectBox<T> extends Widget implements Disableable {
    static final Vector2 temp = new Vector2();
    private int alignment;
    private ClickListener clickListener;
    boolean disabled;
    final Array<T> items;
    private float prefHeight;
    private float prefWidth;
    SelectBoxList<T> selectBoxList;
    final ArraySelection<T> selection;
    SelectBoxStyle style;

    public SelectBox(Skin skin) {
        this((SelectBoxStyle) skin.get(SelectBoxStyle.class));
    }

    public SelectBox(Skin skin, String styleName) {
        this((SelectBoxStyle) skin.get(styleName, SelectBoxStyle.class));
    }

    public SelectBox(SelectBoxStyle style2) {
        this.items = new Array<>();
        this.selection = new ArraySelection<>(this.items);
        this.alignment = 8;
        setStyle(style2);
        setSize(getPrefWidth(), getPrefHeight());
        this.selection.setActor(this);
        this.selection.setRequired(true);
        this.selectBoxList = new SelectBoxList<>(this);
        AnonymousClass1 r0 = new ClickListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if ((pointer == 0 && button != 0) || SelectBox.this.disabled) {
                    return false;
                }
                if (SelectBox.this.selectBoxList.hasParent()) {
                    SelectBox.this.hideList();
                    return true;
                }
                SelectBox.this.showList();
                return true;
            }
        };
        this.clickListener = r0;
        addListener(r0);
    }

    public void setMaxListCount(int maxListCount) {
        this.selectBoxList.maxListCount = maxListCount;
    }

    public int getMaxListCount() {
        return this.selectBoxList.maxListCount;
    }

    /* access modifiers changed from: protected */
    public void setStage(Stage stage) {
        if (stage == null) {
            this.selectBoxList.hide();
        }
        super.setStage(stage);
    }

    public void setStyle(SelectBoxStyle style2) {
        if (style2 != null) {
            this.style = style2;
            SelectBoxList<T> selectBoxList2 = this.selectBoxList;
            if (selectBoxList2 != null) {
                selectBoxList2.setStyle(style2.scrollStyle);
                this.selectBoxList.list.setStyle(style2.listStyle);
            }
            invalidateHierarchy();
            return;
        }
        throw new IllegalArgumentException("style cannot be null.");
    }

    public SelectBoxStyle getStyle() {
        return this.style;
    }

    public void setItems(T... newItems) {
        if (newItems != null) {
            float oldPrefWidth = getPrefWidth();
            this.items.clear();
            this.items.addAll(newItems);
            this.selection.validate();
            this.selectBoxList.list.setItems((Array) this.items);
            invalidate();
            if (oldPrefWidth != getPrefWidth()) {
                invalidateHierarchy();
                return;
            }
            return;
        }
        throw new IllegalArgumentException("newItems cannot be null.");
    }

    public void setItems(Array<T> newItems) {
        if (newItems != null) {
            float oldPrefWidth = getPrefWidth();
            Array<T> array = this.items;
            if (newItems != array) {
                array.clear();
                this.items.addAll(newItems);
            }
            this.selection.validate();
            this.selectBoxList.list.setItems((Array) this.items);
            invalidate();
            if (oldPrefWidth != getPrefWidth()) {
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
            this.selection.clear();
            invalidateHierarchy();
        }
    }

    public Array<T> getItems() {
        return this.items;
    }

    public void layout() {
        Drawable bg = this.style.background;
        BitmapFont font = this.style.font;
        if (bg != null) {
            this.prefHeight = Math.max(((bg.getTopHeight() + bg.getBottomHeight()) + font.getCapHeight()) - (font.getDescent() * 2.0f), bg.getMinHeight());
        } else {
            this.prefHeight = font.getCapHeight() - (font.getDescent() * 2.0f);
        }
        float maxItemWidth = 0.0f;
        Pool<GlyphLayout> layoutPool = Pools.get(GlyphLayout.class);
        GlyphLayout layout = layoutPool.obtain();
        for (int i = 0; i < this.items.size; i++) {
            layout.setText(font, toString(this.items.get(i)));
            maxItemWidth = Math.max(layout.width, maxItemWidth);
        }
        layoutPool.free(layout);
        this.prefWidth = maxItemWidth;
        if (bg != null) {
            this.prefWidth += bg.getLeftWidth() + bg.getRightWidth();
        }
        List.ListStyle listStyle = this.style.listStyle;
        ScrollPane.ScrollPaneStyle scrollStyle = this.style.scrollStyle;
        float listWidth = listStyle.selection.getLeftWidth() + maxItemWidth + listStyle.selection.getRightWidth();
        if (scrollStyle.background != null) {
            listWidth += scrollStyle.background.getLeftWidth() + scrollStyle.background.getRightWidth();
        }
        SelectBoxList<T> selectBoxList2 = this.selectBoxList;
        if (selectBoxList2 == null || !selectBoxList2.disableY) {
            float f = 0.0f;
            float minWidth = this.style.scrollStyle.vScroll != null ? this.style.scrollStyle.vScroll.getMinWidth() : 0.0f;
            if (this.style.scrollStyle.vScrollKnob != null) {
                f = this.style.scrollStyle.vScrollKnob.getMinWidth();
            }
            listWidth += Math.max(minWidth, f);
        }
        this.prefWidth = Math.max(this.prefWidth, listWidth);
    }

    public void draw(Batch batch, float parentAlpha) {
        Drawable background;
        float height;
        float width;
        float y;
        validate();
        if (this.disabled && this.style.backgroundDisabled != null) {
            background = this.style.backgroundDisabled;
        } else if (this.selectBoxList.hasParent() && this.style.backgroundOpen != null) {
            background = this.style.backgroundOpen;
        } else if (this.clickListener.isOver() && this.style.backgroundOver != null) {
            background = this.style.backgroundOver;
        } else if (this.style.background != null) {
            background = this.style.background;
        } else {
            background = null;
        }
        BitmapFont font = this.style.font;
        Color fontColor = (!this.disabled || this.style.disabledFontColor == null) ? this.style.fontColor : this.style.disabledFontColor;
        Color color = getColor();
        float x = getX();
        float y2 = getY();
        float width2 = getWidth();
        float height2 = getHeight();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        if (background != null) {
            background.draw(batch, x, y2, width2, height2);
        }
        T selected = this.selection.first();
        if (selected != null) {
            if (background != null) {
                float width3 = width2 - (background.getLeftWidth() + background.getRightWidth());
                float height3 = height2 - (background.getBottomHeight() + background.getTopHeight());
                float x2 = x + background.getLeftWidth();
                float f = height3;
                height = width3;
                width = y2 + ((float) ((int) ((height3 / 2.0f) + background.getBottomHeight() + (font.getData().capHeight / 2.0f))));
                y = x2;
            } else {
                float f2 = height2;
                height = width2;
                width = y2 + ((float) ((int) ((height2 / 2.0f) + (font.getData().capHeight / 2.0f))));
                y = x;
            }
            font.setColor(fontColor.r, fontColor.g, fontColor.b, fontColor.a * parentAlpha);
            drawItem(batch, font, selected, y, width, height);
            float f3 = y;
            return;
        }
        float height4 = width2;
        float width4 = y2;
    }

    /* access modifiers changed from: protected */
    public GlyphLayout drawItem(Batch batch, BitmapFont font, T item, float x, float y, float width) {
        String string = toString(item);
        return font.draw(batch, string, x, y, 0, string.length(), width, this.alignment, false, "...");
    }

    public void setAlignment(int alignment2) {
        this.alignment = alignment2;
    }

    public ArraySelection<T> getSelection() {
        return this.selection;
    }

    public T getSelected() {
        return this.selection.first();
    }

    public void setSelected(T item) {
        if (this.items.contains(item, false)) {
            this.selection.set(item);
        } else if (this.items.size > 0) {
            this.selection.set(this.items.first());
        } else {
            this.selection.clear();
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
        this.selection.set(this.items.get(index));
    }

    public void setDisabled(boolean disabled2) {
        if (disabled2 && !this.disabled) {
            hideList();
        }
        this.disabled = disabled2;
    }

    public boolean isDisabled() {
        return this.disabled;
    }

    public float getPrefWidth() {
        validate();
        return this.prefWidth;
    }

    public float getPrefHeight() {
        validate();
        return this.prefHeight;
    }

    /* access modifiers changed from: protected */
    public String toString(T item) {
        return item.toString();
    }

    public void showList() {
        if (this.items.size != 0 && getStage() != null) {
            this.selectBoxList.show(getStage());
        }
    }

    public void hideList() {
        this.selectBoxList.hide();
    }

    public List<T> getList() {
        return this.selectBoxList.list;
    }

    public void setScrollingDisabled(boolean y) {
        this.selectBoxList.setScrollingDisabled(true, y);
        invalidateHierarchy();
    }

    public ScrollPane getScrollPane() {
        return this.selectBoxList;
    }

    /* access modifiers changed from: protected */
    public void onShow(Actor selectBoxList2, boolean below) {
        selectBoxList2.getColor().a = 0.0f;
        selectBoxList2.addAction(Actions.fadeIn(0.3f, Interpolation.fade));
    }

    /* access modifiers changed from: protected */
    public void onHide(Actor selectBoxList2) {
        selectBoxList2.getColor().a = 1.0f;
        selectBoxList2.addAction(Actions.sequence(Actions.fadeOut(0.15f, Interpolation.fade), Actions.removeActor()));
    }

    static class SelectBoxList<T> extends ScrollPane {
        private InputListener hideListener;
        final List<T> list;
        int maxListCount;
        private Actor previousScrollFocus;
        private final Vector2 screenPosition = new Vector2();
        private final SelectBox<T> selectBox;

        public SelectBoxList(final SelectBox<T> selectBox2) {
            super((Actor) null, selectBox2.style.scrollStyle);
            this.selectBox = selectBox2;
            setOverscroll(false, false);
            setFadeScrollBars(false);
            setScrollingDisabled(true, false);
            this.list = new List<T>(selectBox2.style.listStyle) {
                public String toString(T obj) {
                    return selectBox2.toString(obj);
                }
            };
            this.list.setTouchable(Touchable.disabled);
            this.list.setTypeToSelect(true);
            setActor(this.list);
            this.list.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y) {
                    selectBox2.selection.choose(SelectBoxList.this.list.getSelected());
                    SelectBoxList.this.hide();
                }

                public boolean mouseMoved(InputEvent event, float x, float y) {
                    int index = SelectBoxList.this.list.getItemIndexAt(y);
                    if (index == -1) {
                        return true;
                    }
                    SelectBoxList.this.list.setSelectedIndex(index);
                    return true;
                }
            });
            addListener(new InputListener() {
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    if (toActor == null || !SelectBoxList.this.isAscendantOf(toActor)) {
                        SelectBoxList.this.list.selection.set(selectBox2.getSelected());
                    }
                }
            });
            this.hideListener = new InputListener() {
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    if (SelectBoxList.this.isAscendantOf(event.getTarget())) {
                        return false;
                    }
                    SelectBoxList.this.list.selection.set(selectBox2.getSelected());
                    SelectBoxList.this.hide();
                    return false;
                }

                public boolean keyDown(InputEvent event, int keycode) {
                    if (keycode == 66) {
                        selectBox2.selection.choose(SelectBoxList.this.list.getSelected());
                    } else if (keycode != 131) {
                        return false;
                    }
                    SelectBoxList.this.hide();
                    event.stop();
                    return true;
                }
            };
        }

        public void show(Stage stage) {
            float height;
            boolean below;
            float width;
            Stage stage2 = stage;
            if (!this.list.isTouchable()) {
                stage2.addActor(this);
                stage2.addCaptureListener(this.hideListener);
                stage2.addListener(this.list.getKeyListener());
                this.selectBox.localToStageCoordinates(this.screenPosition.set(0.0f, 0.0f));
                float itemHeight = this.list.getItemHeight();
                int i = this.maxListCount;
                float height2 = ((float) (i <= 0 ? this.selectBox.items.size : Math.min(i, this.selectBox.items.size))) * itemHeight;
                Drawable scrollPaneBackground = getStyle().background;
                if (scrollPaneBackground != null) {
                    height2 += scrollPaneBackground.getTopHeight() + scrollPaneBackground.getBottomHeight();
                }
                Drawable listBackground = this.list.getStyle().background;
                if (listBackground != null) {
                    height2 += listBackground.getTopHeight() + listBackground.getBottomHeight();
                }
                float heightBelow = this.screenPosition.y;
                float heightAbove = (stage.getCamera().viewportHeight - this.screenPosition.y) - this.selectBox.getHeight();
                if (height2 <= heightBelow) {
                    height = height2;
                    below = true;
                } else if (heightAbove > heightBelow) {
                    height = Math.min(height2, heightAbove);
                    below = false;
                } else {
                    height = heightBelow;
                    below = true;
                }
                if (below) {
                    setY(this.screenPosition.y - height);
                } else {
                    setY(this.screenPosition.y + this.selectBox.getHeight());
                }
                setX(this.screenPosition.x);
                setHeight(height);
                validate();
                float width2 = Math.max(getPrefWidth(), this.selectBox.getWidth());
                if (getPrefHeight() <= height || this.disableY) {
                    width = width2;
                } else {
                    width = width2 + getScrollBarWidth();
                }
                setWidth(width);
                validate();
                float f = width;
                scrollTo(0.0f, (this.list.getHeight() - (((float) this.selectBox.getSelectedIndex()) * itemHeight)) - (itemHeight / 2.0f), 0.0f, 0.0f, true, true);
                updateVisualScroll();
                this.previousScrollFocus = null;
                Actor actor = stage.getScrollFocus();
                if (actor != null && !actor.isDescendantOf(this)) {
                    this.previousScrollFocus = actor;
                }
                stage2.setScrollFocus(this);
                this.list.selection.set(this.selectBox.getSelected());
                this.list.setTouchable(Touchable.enabled);
                clearActions();
                this.selectBox.onShow(this, below);
            }
        }

        public void hide() {
            if (this.list.isTouchable() && hasParent()) {
                this.list.setTouchable(Touchable.disabled);
                Stage stage = getStage();
                if (stage != null) {
                    stage.removeCaptureListener(this.hideListener);
                    stage.removeListener(this.list.getKeyListener());
                    Actor actor = this.previousScrollFocus;
                    if (actor != null && actor.getStage() == null) {
                        this.previousScrollFocus = null;
                    }
                    Actor actor2 = stage.getScrollFocus();
                    if (actor2 == null || isAscendantOf(actor2)) {
                        stage.setScrollFocus(this.previousScrollFocus);
                    }
                }
                clearActions();
                this.selectBox.onHide(this);
            }
        }

        public void draw(Batch batch, float parentAlpha) {
            this.selectBox.localToStageCoordinates(SelectBox.temp.set(0.0f, 0.0f));
            if (!SelectBox.temp.equals(this.screenPosition)) {
                hide();
            }
            super.draw(batch, parentAlpha);
        }

        public void act(float delta) {
            super.act(delta);
            toFront();
        }

        /* access modifiers changed from: protected */
        public void setStage(Stage stage) {
            Stage oldStage = getStage();
            if (oldStage != null) {
                oldStage.removeCaptureListener(this.hideListener);
                oldStage.removeListener(this.list.getKeyListener());
            }
            super.setStage(stage);
        }
    }

    public static class SelectBoxStyle {
        public Drawable background;
        public Drawable backgroundDisabled;
        public Drawable backgroundOpen;
        public Drawable backgroundOver;
        public Color disabledFontColor;
        public BitmapFont font;
        public Color fontColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        public List.ListStyle listStyle;
        public ScrollPane.ScrollPaneStyle scrollStyle;

        public SelectBoxStyle() {
        }

        public SelectBoxStyle(BitmapFont font2, Color fontColor2, Drawable background2, ScrollPane.ScrollPaneStyle scrollStyle2, List.ListStyle listStyle2) {
            this.font = font2;
            this.fontColor.set(fontColor2);
            this.background = background2;
            this.scrollStyle = scrollStyle2;
            this.listStyle = listStyle2;
        }

        public SelectBoxStyle(SelectBoxStyle style) {
            this.font = style.font;
            this.fontColor.set(style.fontColor);
            Color color = style.disabledFontColor;
            if (color != null) {
                this.disabledFontColor = new Color(color);
            }
            this.background = style.background;
            this.backgroundOver = style.backgroundOver;
            this.backgroundOpen = style.backgroundOpen;
            this.backgroundDisabled = style.backgroundDisabled;
            this.scrollStyle = new ScrollPane.ScrollPaneStyle(style.scrollStyle);
            this.listStyle = new List.ListStyle(style.listStyle);
        }
    }
}
