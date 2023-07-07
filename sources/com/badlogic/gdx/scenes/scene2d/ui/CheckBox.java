package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Scaling;

public class CheckBox extends TextButton {
    private Image image;
    private Cell imageCell;
    private CheckBoxStyle style;

    public CheckBox(String text, Skin skin) {
        this(text, (CheckBoxStyle) skin.get(CheckBoxStyle.class));
    }

    public CheckBox(String text, Skin skin, String styleName) {
        this(text, (CheckBoxStyle) skin.get(styleName, CheckBoxStyle.class));
    }

    public CheckBox(String text, CheckBoxStyle style2) {
        super(text, (TextButton.TextButtonStyle) style2);
        clearChildren();
        Label label = getLabel();
        Image image2 = new Image(style2.checkboxOff, Scaling.none);
        this.image = image2;
        this.imageCell = add(image2);
        add(label);
        label.setAlignment(8);
        setSize(getPrefWidth(), getPrefHeight());
    }

    public void setStyle(Button.ButtonStyle style2) {
        if (style2 instanceof CheckBoxStyle) {
            super.setStyle(style2);
            this.style = (CheckBoxStyle) style2;
            return;
        }
        throw new IllegalArgumentException("style must be a CheckBoxStyle.");
    }

    public CheckBoxStyle getStyle() {
        return this.style;
    }

    public void draw(Batch batch, float parentAlpha) {
        Drawable checkbox = null;
        if (isDisabled()) {
            if (!this.isChecked || this.style.checkboxOnDisabled == null) {
                checkbox = this.style.checkboxOffDisabled;
            } else {
                checkbox = this.style.checkboxOnDisabled;
            }
        }
        if (checkbox == null) {
            boolean over = isOver() && !isDisabled();
            if (this.isChecked && this.style.checkboxOn != null) {
                checkbox = (!over || this.style.checkboxOnOver == null) ? this.style.checkboxOn : this.style.checkboxOnOver;
            } else if (!over || this.style.checkboxOver == null) {
                checkbox = this.style.checkboxOff;
            } else {
                checkbox = this.style.checkboxOver;
            }
        }
        this.image.setDrawable(checkbox);
        super.draw(batch, parentAlpha);
    }

    public Image getImage() {
        return this.image;
    }

    public Cell getImageCell() {
        return this.imageCell;
    }

    public static class CheckBoxStyle extends TextButton.TextButtonStyle {
        public Drawable checkboxOff;
        public Drawable checkboxOffDisabled;
        public Drawable checkboxOn;
        public Drawable checkboxOnDisabled;
        public Drawable checkboxOnOver;
        public Drawable checkboxOver;

        public CheckBoxStyle() {
        }

        public CheckBoxStyle(Drawable checkboxOff2, Drawable checkboxOn2, BitmapFont font, Color fontColor) {
            this.checkboxOff = checkboxOff2;
            this.checkboxOn = checkboxOn2;
            this.font = font;
            this.fontColor = fontColor;
        }

        public CheckBoxStyle(CheckBoxStyle style) {
            super(style);
            this.checkboxOff = style.checkboxOff;
            this.checkboxOn = style.checkboxOn;
            this.checkboxOver = style.checkboxOver;
            this.checkboxOffDisabled = style.checkboxOffDisabled;
            this.checkboxOnDisabled = style.checkboxOnDisabled;
        }
    }
}
