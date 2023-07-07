package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Scaling;
import com.twi.game.BuildConfig;

public class ImageButton extends Button {
    private final Image image;
    private ImageButtonStyle style;

    public ImageButton(Skin skin) {
        this((ImageButtonStyle) skin.get(ImageButtonStyle.class));
        setSkin(skin);
    }

    public ImageButton(Skin skin, String styleName) {
        this((ImageButtonStyle) skin.get(styleName, ImageButtonStyle.class));
        setSkin(skin);
    }

    public ImageButton(ImageButtonStyle style2) {
        super((Button.ButtonStyle) style2);
        this.image = new Image();
        this.image.setScaling(Scaling.fit);
        add(this.image);
        setStyle(style2);
        setSize(getPrefWidth(), getPrefHeight());
    }

    public ImageButton(Drawable imageUp) {
        this(new ImageButtonStyle((Drawable) null, (Drawable) null, (Drawable) null, imageUp, (Drawable) null, (Drawable) null));
    }

    public ImageButton(Drawable imageUp, Drawable imageDown) {
        this(new ImageButtonStyle((Drawable) null, (Drawable) null, (Drawable) null, imageUp, imageDown, (Drawable) null));
    }

    public ImageButton(Drawable imageUp, Drawable imageDown, Drawable imageChecked) {
        this(new ImageButtonStyle((Drawable) null, (Drawable) null, (Drawable) null, imageUp, imageDown, imageChecked));
    }

    public void setStyle(Button.ButtonStyle style2) {
        if (style2 instanceof ImageButtonStyle) {
            super.setStyle(style2);
            this.style = (ImageButtonStyle) style2;
            if (this.image != null) {
                updateImage();
                return;
            }
            return;
        }
        throw new IllegalArgumentException("style must be an ImageButtonStyle.");
    }

    public ImageButtonStyle getStyle() {
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
        updateImage();
        super.draw(batch, parentAlpha);
    }

    public Image getImage() {
        return this.image;
    }

    public Cell getImageCell() {
        return getCell(this.image);
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
        sb.append(className.indexOf(36) != -1 ? "ImageButton " : BuildConfig.FLAVOR);
        sb.append(className);
        sb.append(": ");
        sb.append(this.image.getDrawable());
        return sb.toString();
    }

    public static class ImageButtonStyle extends Button.ButtonStyle {
        public Drawable imageChecked;
        public Drawable imageCheckedOver;
        public Drawable imageDisabled;
        public Drawable imageDown;
        public Drawable imageOver;
        public Drawable imageUp;

        public ImageButtonStyle() {
        }

        public ImageButtonStyle(Drawable up, Drawable down, Drawable checked, Drawable imageUp2, Drawable imageDown2, Drawable imageChecked2) {
            super(up, down, checked);
            this.imageUp = imageUp2;
            this.imageDown = imageDown2;
            this.imageChecked = imageChecked2;
        }

        public ImageButtonStyle(ImageButtonStyle style) {
            super(style);
            this.imageUp = style.imageUp;
            this.imageDown = style.imageDown;
            this.imageOver = style.imageOver;
            this.imageChecked = style.imageChecked;
            this.imageCheckedOver = style.imageCheckedOver;
            this.imageDisabled = style.imageDisabled;
        }

        public ImageButtonStyle(Button.ButtonStyle style) {
            super(style);
        }
    }
}
