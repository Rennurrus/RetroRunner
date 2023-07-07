package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.twi.game.BuildConfig;

public class TextButton extends Button {
    private Label label;
    private TextButtonStyle style;

    public TextButton(String text, Skin skin) {
        this(text, (TextButtonStyle) skin.get(TextButtonStyle.class));
        setSkin(skin);
    }

    public TextButton(String text, Skin skin, String styleName) {
        this(text, (TextButtonStyle) skin.get(styleName, TextButtonStyle.class));
        setSkin(skin);
    }

    public TextButton(String text, TextButtonStyle style2) {
        setStyle(style2);
        this.style = style2;
        this.label = new Label((CharSequence) text, new Label.LabelStyle(style2.font, style2.fontColor));
        this.label.setAlignment(1);
        add(this.label).expand().fill();
        setSize(getPrefWidth(), getPrefHeight());
    }

    public void setStyle(Button.ButtonStyle style2) {
        if (style2 == null) {
            throw new NullPointerException("style cannot be null");
        } else if (style2 instanceof TextButtonStyle) {
            super.setStyle(style2);
            this.style = (TextButtonStyle) style2;
            Label label2 = this.label;
            if (label2 != null) {
                TextButtonStyle textButtonStyle = (TextButtonStyle) style2;
                Label.LabelStyle labelStyle = label2.getStyle();
                labelStyle.font = textButtonStyle.font;
                labelStyle.fontColor = textButtonStyle.fontColor;
                this.label.setStyle(labelStyle);
            }
        } else {
            throw new IllegalArgumentException("style must be a TextButtonStyle.");
        }
    }

    public TextButtonStyle getStyle() {
        return this.style;
    }

    public void draw(Batch batch, float parentAlpha) {
        Color fontColor;
        if (isDisabled() && this.style.disabledFontColor != null) {
            fontColor = this.style.disabledFontColor;
        } else if (isPressed() && this.style.downFontColor != null) {
            fontColor = this.style.downFontColor;
        } else if (!this.isChecked || this.style.checkedFontColor == null) {
            fontColor = (!isOver() || this.style.overFontColor == null) ? this.style.fontColor : this.style.overFontColor;
        } else {
            fontColor = (!isOver() || this.style.checkedOverFontColor == null) ? this.style.checkedFontColor : this.style.checkedOverFontColor;
        }
        if (fontColor != null) {
            this.label.getStyle().fontColor = fontColor;
        }
        super.draw(batch, parentAlpha);
    }

    public void setLabel(Label label2) {
        getLabelCell().setActor(label2);
        this.label = label2;
    }

    public Label getLabel() {
        return this.label;
    }

    public Cell<Label> getLabelCell() {
        return getCell(this.label);
    }

    public void setText(String text) {
        this.label.setText((CharSequence) text);
    }

    public CharSequence getText() {
        return this.label.getText();
    }

    public String toString() {
        String name = getName();
        if (name != null) {
            return name;
        }
        String className = getClass().getName();
        int dotIndex = className.lastIndexOf(46);
        if (dotIndex != -1) {
            className = className.substring(dotIndex + 1);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(className.indexOf(36) != -1 ? "TextButton " : BuildConfig.FLAVOR);
        sb.append(className);
        sb.append(": ");
        sb.append(this.label.getText());
        return sb.toString();
    }

    public static class TextButtonStyle extends Button.ButtonStyle {
        public Color checkedFontColor;
        public Color checkedOverFontColor;
        public Color disabledFontColor;
        public Color downFontColor;
        public BitmapFont font;
        public Color fontColor;
        public Color overFontColor;

        public TextButtonStyle() {
        }

        public TextButtonStyle(Drawable up, Drawable down, Drawable checked, BitmapFont font2) {
            super(up, down, checked);
            this.font = font2;
        }

        public TextButtonStyle(TextButtonStyle style) {
            super(style);
            this.font = style.font;
            Color color = style.fontColor;
            if (color != null) {
                this.fontColor = new Color(color);
            }
            Color color2 = style.downFontColor;
            if (color2 != null) {
                this.downFontColor = new Color(color2);
            }
            Color color3 = style.overFontColor;
            if (color3 != null) {
                this.overFontColor = new Color(color3);
            }
            Color color4 = style.checkedFontColor;
            if (color4 != null) {
                this.checkedFontColor = new Color(color4);
            }
            Color color5 = style.checkedOverFontColor;
            if (color5 != null) {
                this.checkedOverFontColor = new Color(color5);
            }
            Color color6 = style.disabledFontColor;
            if (color6 != null) {
                this.disabledFontColor = new Color(color6);
            }
        }
    }
}
