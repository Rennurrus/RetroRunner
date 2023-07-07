package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;

public class Button extends Table implements Disableable {
    ButtonGroup buttonGroup;
    private ClickListener clickListener;
    boolean focused;
    boolean isChecked;
    boolean isDisabled;
    private boolean programmaticChangeEvents;
    private ButtonStyle style;

    public Button(Skin skin) {
        super(skin);
        this.programmaticChangeEvents = true;
        initialize();
        setStyle((ButtonStyle) skin.get(ButtonStyle.class));
        setSize(getPrefWidth(), getPrefHeight());
    }

    public Button(Skin skin, String styleName) {
        super(skin);
        this.programmaticChangeEvents = true;
        initialize();
        setStyle((ButtonStyle) skin.get(styleName, ButtonStyle.class));
        setSize(getPrefWidth(), getPrefHeight());
    }

    public Button(Actor child, Skin skin, String styleName) {
        this(child, (ButtonStyle) skin.get(styleName, ButtonStyle.class));
        setSkin(skin);
    }

    public Button(Actor child, ButtonStyle style2) {
        this.programmaticChangeEvents = true;
        initialize();
        add(child);
        setStyle(style2);
        setSize(getPrefWidth(), getPrefHeight());
    }

    public Button(ButtonStyle style2) {
        this.programmaticChangeEvents = true;
        initialize();
        setStyle(style2);
        setSize(getPrefWidth(), getPrefHeight());
    }

    public Button() {
        this.programmaticChangeEvents = true;
        initialize();
    }

    private void initialize() {
        setTouchable(Touchable.enabled);
        AnonymousClass1 r0 = new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (!Button.this.isDisabled()) {
                    Button button = Button.this;
                    button.setChecked(!button.isChecked, true);
                }
            }
        };
        this.clickListener = r0;
        addListener(r0);
        addListener(new FocusListener() {
            public void keyboardFocusChanged(FocusListener.FocusEvent event, Actor actor, boolean focused) {
                Button.this.focused = focused;
            }
        });
    }

    public Button(Drawable up) {
        this(new ButtonStyle(up, (Drawable) null, (Drawable) null));
    }

    public Button(Drawable up, Drawable down) {
        this(new ButtonStyle(up, down, (Drawable) null));
    }

    public Button(Drawable up, Drawable down, Drawable checked) {
        this(new ButtonStyle(up, down, checked));
    }

    public Button(Actor child, Skin skin) {
        this(child, (ButtonStyle) skin.get(ButtonStyle.class));
    }

    public void setChecked(boolean isChecked2) {
        setChecked(isChecked2, this.programmaticChangeEvents);
    }

    /* access modifiers changed from: package-private */
    public void setChecked(boolean isChecked2, boolean fireEvent) {
        if (this.isChecked != isChecked2) {
            ButtonGroup buttonGroup2 = this.buttonGroup;
            if (buttonGroup2 == null || buttonGroup2.canCheck(this, isChecked2)) {
                this.isChecked = isChecked2;
                if (fireEvent) {
                    ChangeListener.ChangeEvent changeEvent = (ChangeListener.ChangeEvent) Pools.obtain(ChangeListener.ChangeEvent.class);
                    if (fire(changeEvent)) {
                        this.isChecked = !isChecked2;
                    }
                    Pools.free(changeEvent);
                }
            }
        }
    }

    public void toggle() {
        setChecked(!this.isChecked);
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public boolean isPressed() {
        return this.clickListener.isVisualPressed();
    }

    public boolean isOver() {
        return this.clickListener.isOver();
    }

    public ClickListener getClickListener() {
        return this.clickListener;
    }

    public boolean isDisabled() {
        return this.isDisabled;
    }

    public void setDisabled(boolean isDisabled2) {
        this.isDisabled = isDisabled2;
    }

    public void setProgrammaticChangeEvents(boolean programmaticChangeEvents2) {
        this.programmaticChangeEvents = programmaticChangeEvents2;
    }

    public void setStyle(ButtonStyle style2) {
        Drawable background;
        if (style2 != null) {
            this.style = style2;
            if (isPressed() && !isDisabled()) {
                background = style2.down == null ? style2.up : style2.down;
            } else if (isDisabled() && style2.disabled != null) {
                background = style2.disabled;
            } else if (!this.isChecked || style2.checked == null) {
                if (isOver() && style2.over != null) {
                    background = style2.over;
                } else if (!this.focused || style2.focused == null) {
                    background = style2.up;
                } else {
                    background = style2.focused;
                }
            } else if (isOver() && style2.checkedOver != null) {
                background = style2.checkedOver;
            } else if (!this.focused || style2.checkedFocused == null) {
                background = style2.checked;
            } else {
                background = style2.checkedFocused;
            }
            setBackground(background);
            return;
        }
        throw new IllegalArgumentException("style cannot be null.");
    }

    public ButtonStyle getStyle() {
        return this.style;
    }

    public ButtonGroup getButtonGroup() {
        return this.buttonGroup;
    }

    public void draw(Batch batch, float parentAlpha) {
        float offsetY;
        float offsetX;
        validate();
        boolean isDisabled2 = isDisabled();
        boolean isPressed = isPressed();
        boolean isChecked2 = isChecked();
        boolean isOver = isOver();
        Drawable background = null;
        if (isDisabled2 && this.style.disabled != null) {
            background = this.style.disabled;
        } else if (isPressed && this.style.down != null) {
            background = this.style.down;
        } else if (!isChecked2 || this.style.checked == null) {
            if (isOver && this.style.over != null) {
                background = this.style.over;
            } else if (this.focused && this.style.focused != null) {
                background = this.style.focused;
            } else if (this.style.up != null) {
                background = this.style.up;
            }
        } else if (this.style.checkedOver != null && isOver) {
            background = this.style.checkedOver;
        } else if (this.style.checkedFocused == null || !this.focused) {
            background = this.style.checked;
        } else {
            background = this.style.checkedFocused;
        }
        setBackground(background);
        if (isPressed && !isDisabled2) {
            offsetX = this.style.pressedOffsetX;
            offsetY = this.style.pressedOffsetY;
        } else if (!isChecked2 || isDisabled2) {
            offsetX = this.style.unpressedOffsetX;
            offsetY = this.style.unpressedOffsetY;
        } else {
            offsetX = this.style.checkedOffsetX;
            offsetY = this.style.checkedOffsetY;
        }
        Array<Actor> children = getChildren();
        for (int i = 0; i < children.size; i++) {
            children.get(i).moveBy(offsetX, offsetY);
        }
        super.draw(batch, parentAlpha);
        for (int i2 = 0; i2 < children.size; i2++) {
            children.get(i2).moveBy(-offsetX, -offsetY);
        }
        Stage stage = getStage();
        if (stage != null && stage.getActionsRequestRendering() && isPressed != this.clickListener.isPressed()) {
            Gdx.graphics.requestRendering();
        }
    }

    public float getPrefWidth() {
        float width = super.getPrefWidth();
        if (this.style.up != null) {
            width = Math.max(width, this.style.up.getMinWidth());
        }
        if (this.style.down != null) {
            width = Math.max(width, this.style.down.getMinWidth());
        }
        if (this.style.checked != null) {
            return Math.max(width, this.style.checked.getMinWidth());
        }
        return width;
    }

    public float getPrefHeight() {
        float height = super.getPrefHeight();
        if (this.style.up != null) {
            height = Math.max(height, this.style.up.getMinHeight());
        }
        if (this.style.down != null) {
            height = Math.max(height, this.style.down.getMinHeight());
        }
        if (this.style.checked != null) {
            return Math.max(height, this.style.checked.getMinHeight());
        }
        return height;
    }

    public float getMinWidth() {
        return getPrefWidth();
    }

    public float getMinHeight() {
        return getPrefHeight();
    }

    public static class ButtonStyle {
        public Drawable checked;
        public Drawable checkedFocused;
        public float checkedOffsetX;
        public float checkedOffsetY;
        public Drawable checkedOver;
        public Drawable disabled;
        public Drawable down;
        public Drawable focused;
        public Drawable over;
        public float pressedOffsetX;
        public float pressedOffsetY;
        public float unpressedOffsetX;
        public float unpressedOffsetY;
        public Drawable up;

        public ButtonStyle() {
        }

        public ButtonStyle(Drawable up2, Drawable down2, Drawable checked2) {
            this.up = up2;
            this.down = down2;
            this.checked = checked2;
        }

        public ButtonStyle(ButtonStyle style) {
            this.up = style.up;
            this.down = style.down;
            this.over = style.over;
            this.focused = style.focused;
            this.checked = style.checked;
            this.checkedOver = style.checkedOver;
            this.checkedFocused = style.checkedFocused;
            this.disabled = style.disabled;
            this.pressedOffsetX = style.pressedOffsetX;
            this.pressedOffsetY = style.pressedOffsetY;
            this.unpressedOffsetX = style.unpressedOffsetX;
            this.unpressedOffsetY = style.unpressedOffsetY;
            this.checkedOffsetX = style.checkedOffsetX;
            this.checkedOffsetY = style.checkedOffsetY;
        }
    }
}
