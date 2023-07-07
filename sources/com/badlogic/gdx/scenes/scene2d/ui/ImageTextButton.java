package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Scaling;
import com.twi.game.BuildConfig;

public class ImageTextButton extends Button {
    private final Image image;
    private Label label;
    private ImageTextButtonStyle style;

    public ImageTextButton(String text, Skin skin) {
        this(text, (ImageTextButtonStyle) skin.get(ImageTextButtonStyle.class));
        setSkin(skin);
    }

    public ImageTextButton(String text, Skin skin, String styleName) {
        this(text, (ImageTextButtonStyle) skin.get(styleName, ImageTextButtonStyle.class));
        setSkin(skin);
    }

    public ImageTextButton(String text, ImageTextButtonStyle style2) {
        super((Button.ButtonStyle) style2);
        this.style = style2;
        defaults().space(3.0f);
        this.image = new Image();
        this.image.setScaling(Scaling.fit);
        this.label = new Label((CharSequence) text, new Label.LabelStyle(style2.font, style2.fontColor));
        this.label.setAlignment(1);
        add(this.image);
        add(this.label);
        setStyle(style2);
        setSize(getPrefWidth(), getPrefHeight());
    }

    public void setStyle(Button.ButtonStyle style2) {
        if (style2 instanceof ImageTextButtonStyle) {
            super.setStyle(style2);
            this.style = (ImageTextButtonStyle) style2;
            if (this.image != null) {
                updateImage();
            }
            Label label2 = this.label;
            if (label2 != null) {
                ImageTextButtonStyle textButtonStyle = (ImageTextButtonStyle) style2;
                Label.LabelStyle labelStyle = label2.getStyle();
                labelStyle.font = textButtonStyle.font;
                labelStyle.fontColor = textButtonStyle.fontColor;
                this.label.setStyle(labelStyle);
                return;
            }
            return;
        }
        throw new IllegalArgumentException("style must be a ImageTextButtonStyle.");
    }

    public ImageTextButtonStyle getStyle() {
        return this.style;
    }

    /* access modifiers changed from: protected */
    public void updateImage() {
        Drawable drawable = null;
        if (isDisabled() && this.style.imageDisabled != null) {
            drawable = this.style.imageDisabled;
        } else if (isPressed() && this.style.imageDown != null) {
            drawable = this.style.imageDown;
        } else if (this.isChecked && this.style.imageChecked != null) {
            drawable = (this.style.imageCheckedOver == null || !isOver()) ? this.style.imageChecked : this.style.imageCheckedOver;
        } else if (isOver() && this.style.imageOver != null) {
            drawable = this.style.imageOver;
        } else if (this.style.imageUp != null) {
            drawable = this.style.imageUp;
        }
        this.image.setDrawable(drawable);
    }

    public void draw(Batch batch, float parentAlpha) {
        Color fontColor;
        updateImage();
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

    public Image getImage() {
        return this.image;
    }

    public Cell getImageCell() {
        return getCell(this.image);
    }

    public void setLabel(Label label2) {
        getLabelCell().setActor(label2);
        this.label = label2;
    }

    public Label getLabel() {
        return this.label;
    }

    public Cell getLabelCell() {
        return getCell(this.label);
    }

    public void setText(CharSequence text) {
        this.label.setText(text);
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
        sb.append(className.indexOf(36) != -1 ? "ImageTextButton " : BuildConfig.FLAVOR);
        sb.append(className);
        sb.append(": ");
        sb.append(this.image.getDrawable());
        sb.append(" ");
        sb.append(this.label.getText());
        return sb.toString();
    }

    public static class ImageTextButtonStyle extends TextButton.TextButtonStyle {
        public Drawable imageChecked;
        public Drawable imageCheckedOver;
        public Drawable imageDisabled;
        public Drawable imageDown;
        public Drawable imageOver;
        public Drawable imageUp;

        public ImageTextButtonStyle() {
        }

        public ImageTextButtonStyle(Drawable up, Drawable down, Drawable checked, BitmapFont font) {
            super(up, down, checked, font);
        }

        public ImageTextButtonStyle(ImageTextButtonStyle style) {
            super(style);
            Drawable drawable = style.imageUp;
            if (drawable != null) {
                this.imageUp = drawable;
            }
            Drawable drawable2 = style.imageDown;
            if (drawable2 != null) {
                this.imageDown = drawable2;
            }
            Drawable drawable3 = style.imageOver;
            if (drawable3 != null) {
                this.imageOver = drawable3;
            }
            Drawable drawable4 = style.imageChecked;
            if (drawable4 != null) {
                this.imageChecked = drawable4;
            }
            Drawable drawable5 = style.imageCheckedOver;
            if (drawable5 != null) {
                this.imageCheckedOver = drawable5;
            }
            Drawable drawable6 = style.imageDisabled;
            if (drawable6 != null) {
                this.imageDisabled = drawable6;
            }
        }

        public ImageTextButtonStyle(TextButton.TextButtonStyle style) {
            super(style);
        }
    }
}
