package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.StringBuilder;
import com.twi.game.BuildConfig;

public class Label extends Widget {
    private static final GlyphLayout prefSizeLayout = new GlyphLayout();
    private static final Color tempColor = new Color();
    private BitmapFontCache cache;
    private String ellipsis;
    private boolean fontScaleChanged;
    private float fontScaleX;
    private float fontScaleY;
    private int intValue;
    private int labelAlign;
    private float lastPrefHeight;
    private final GlyphLayout layout;
    private int lineAlign;
    private final Vector2 prefSize;
    private boolean prefSizeInvalid;
    private LabelStyle style;
    private final StringBuilder text;
    private boolean wrap;

    public Label(CharSequence text2, Skin skin) {
        this(text2, (LabelStyle) skin.get(LabelStyle.class));
    }

    public Label(CharSequence text2, Skin skin, String styleName) {
        this(text2, (LabelStyle) skin.get(styleName, LabelStyle.class));
    }

    public Label(CharSequence text2, Skin skin, String fontName, Color color) {
        this(text2, new LabelStyle(skin.getFont(fontName), color));
    }

    public Label(CharSequence text2, Skin skin, String fontName, String colorName) {
        this(text2, new LabelStyle(skin.getFont(fontName), skin.getColor(colorName)));
    }

    public Label(CharSequence text2, LabelStyle style2) {
        this.layout = new GlyphLayout();
        this.prefSize = new Vector2();
        this.text = new StringBuilder();
        this.intValue = Integer.MIN_VALUE;
        this.labelAlign = 8;
        this.lineAlign = 8;
        this.prefSizeInvalid = true;
        this.fontScaleX = 1.0f;
        this.fontScaleY = 1.0f;
        this.fontScaleChanged = false;
        if (text2 != null) {
            this.text.append(text2);
        }
        setStyle(style2);
        if (text2 != null && text2.length() > 0) {
            setSize(getPrefWidth(), getPrefHeight());
        }
    }

    public void setStyle(LabelStyle style2) {
        if (style2 == null) {
            throw new IllegalArgumentException("style cannot be null.");
        } else if (style2.font != null) {
            this.style = style2;
            this.cache = style2.font.newFontCache();
            invalidateHierarchy();
        } else {
            throw new IllegalArgumentException("Missing LabelStyle font.");
        }
    }

    public LabelStyle getStyle() {
        return this.style;
    }

    public boolean setText(int value) {
        if (this.intValue == value) {
            return false;
        }
        setText((CharSequence) Integer.toString(value));
        this.intValue = value;
        return true;
    }

    public void setText(CharSequence newText) {
        if (newText == null) {
            newText = BuildConfig.FLAVOR;
        }
        if (newText instanceof StringBuilder) {
            if (!this.text.equals(newText)) {
                this.text.setLength(0);
                this.text.append((StringBuilder) newText);
            } else {
                return;
            }
        } else if (!textEquals(newText)) {
            this.text.setLength(0);
            this.text.append(newText);
        } else {
            return;
        }
        this.intValue = Integer.MIN_VALUE;
        invalidateHierarchy();
    }

    public boolean textEquals(CharSequence other) {
        int length = this.text.length;
        char[] chars = this.text.chars;
        if (length != other.length()) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (chars[i] != other.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    public StringBuilder getText() {
        return this.text;
    }

    public void invalidate() {
        super.invalidate();
        this.prefSizeInvalid = true;
    }

    private void scaleAndComputePrefSize() {
        BitmapFont font = this.cache.getFont();
        float oldScaleX = font.getScaleX();
        float oldScaleY = font.getScaleY();
        if (this.fontScaleChanged) {
            font.getData().setScale(this.fontScaleX, this.fontScaleY);
        }
        computePrefSize();
        if (this.fontScaleChanged) {
            font.getData().setScale(oldScaleX, oldScaleY);
        }
    }

    private void computePrefSize() {
        this.prefSizeInvalid = false;
        GlyphLayout prefSizeLayout2 = prefSizeLayout;
        if (!this.wrap || this.ellipsis != null) {
            prefSizeLayout2.setText(this.cache.getFont(), this.text);
        } else {
            float width = getWidth();
            if (this.style.background != null) {
                width -= this.style.background.getLeftWidth() + this.style.background.getRightWidth();
            }
            float width2 = width;
            prefSizeLayout2.setText(this.cache.getFont(), this.text, Color.WHITE, width2, 8, true);
        }
        this.prefSize.set(prefSizeLayout2.width, prefSizeLayout2.height);
    }

    public void layout() {
        float height;
        float y;
        float x;
        float width;
        float textWidth;
        float x2;
        GlyphLayout layout2;
        float x3;
        float y2;
        BitmapFont font = this.cache.getFont();
        float oldScaleX = font.getScaleX();
        float oldScaleY = font.getScaleY();
        if (this.fontScaleChanged) {
            font.getData().setScale(this.fontScaleX, this.fontScaleY);
        }
        boolean wrap2 = this.wrap && this.ellipsis == null;
        if (wrap2) {
            float prefHeight = getPrefHeight();
            if (prefHeight != this.lastPrefHeight) {
                this.lastPrefHeight = prefHeight;
                invalidateHierarchy();
            }
        }
        float width2 = getWidth();
        float height2 = getHeight();
        Drawable background = this.style.background;
        if (background != null) {
            float x4 = background.getLeftWidth();
            float y3 = background.getBottomHeight();
            width = width2 - (background.getLeftWidth() + background.getRightWidth());
            height = height2 - (background.getBottomHeight() + background.getTopHeight());
            x = x4;
            y = y3;
        } else {
            width = width2;
            height = height2;
            x = 0.0f;
            y = 0.0f;
        }
        GlyphLayout layout3 = this.layout;
        if (wrap2 || this.text.indexOf("\n") != -1) {
            StringBuilder stringBuilder = this.text;
            Drawable drawable = background;
            layout2 = layout3;
            layout3.setText(font, stringBuilder, 0, stringBuilder.length, Color.WHITE, width, this.lineAlign, wrap2, this.ellipsis);
            float textWidth2 = layout2.width;
            float textHeight = layout2.height;
            int i = this.labelAlign;
            if ((i & 8) != 0) {
                textWidth = textWidth2;
                x3 = x;
                x2 = textHeight;
            } else if ((i & 16) != 0) {
                textWidth = textWidth2;
                x3 = x + (width - textWidth2);
                x2 = textHeight;
            } else {
                textWidth = textWidth2;
                x3 = x + ((width - textWidth2) / 2.0f);
                x2 = textHeight;
            }
        } else {
            textWidth = width;
            Drawable drawable2 = background;
            layout2 = layout3;
            x3 = x;
            x2 = font.getData().capHeight;
        }
        int i2 = this.labelAlign;
        float f = 0.0f;
        if ((i2 & 2) != 0) {
            if (!this.cache.getFont().isFlipped()) {
                f = height - x2;
            }
            y2 = y + f + this.style.font.getDescent();
        } else if ((i2 & 4) != 0) {
            if (this.cache.getFont().isFlipped()) {
                f = height - x2;
            }
            y2 = (y + f) - this.style.font.getDescent();
        } else {
            y2 = y + ((height - x2) / 2.0f);
        }
        if (!this.cache.getFont().isFlipped()) {
            y2 += x2;
        }
        StringBuilder stringBuilder2 = this.text;
        boolean z = wrap2;
        layout2.setText(font, stringBuilder2, 0, stringBuilder2.length, Color.WHITE, textWidth, this.lineAlign, wrap2, this.ellipsis);
        this.cache.setText(layout2, x3, y2);
        if (this.fontScaleChanged) {
            font.getData().setScale(oldScaleX, oldScaleY);
        }
    }

    public void draw(Batch batch, float parentAlpha) {
        validate();
        Color color = tempColor.set(getColor());
        color.a *= parentAlpha;
        if (this.style.background != null) {
            batch.setColor(color.r, color.g, color.b, color.a);
            this.style.background.draw(batch, getX(), getY(), getWidth(), getHeight());
        }
        if (this.style.fontColor != null) {
            color.mul(this.style.fontColor);
        }
        this.cache.tint(color);
        this.cache.setPosition(getX(), getY());
        this.cache.draw(batch);
    }

    public float getPrefWidth() {
        if (this.wrap) {
            return 0.0f;
        }
        if (this.prefSizeInvalid) {
            scaleAndComputePrefSize();
        }
        float width = this.prefSize.x;
        Drawable background = this.style.background;
        if (background != null) {
            return width + background.getLeftWidth() + background.getRightWidth();
        }
        return width;
    }

    public float getPrefHeight() {
        if (this.prefSizeInvalid) {
            scaleAndComputePrefSize();
        }
        float descentScaleCorrection = 1.0f;
        if (this.fontScaleChanged) {
            descentScaleCorrection = this.fontScaleY / this.style.font.getScaleY();
        }
        float height = this.prefSize.y - ((this.style.font.getDescent() * descentScaleCorrection) * 2.0f);
        Drawable background = this.style.background;
        if (background != null) {
            return height + background.getTopHeight() + background.getBottomHeight();
        }
        return height;
    }

    public GlyphLayout getGlyphLayout() {
        return this.layout;
    }

    public void setWrap(boolean wrap2) {
        this.wrap = wrap2;
        invalidateHierarchy();
    }

    public int getLabelAlign() {
        return this.labelAlign;
    }

    public int getLineAlign() {
        return this.lineAlign;
    }

    public void setAlignment(int alignment) {
        setAlignment(alignment, alignment);
    }

    public void setAlignment(int labelAlign2, int lineAlign2) {
        this.labelAlign = labelAlign2;
        if ((lineAlign2 & 8) != 0) {
            this.lineAlign = 8;
        } else if ((lineAlign2 & 16) != 0) {
            this.lineAlign = 16;
        } else {
            this.lineAlign = 1;
        }
        invalidate();
    }

    public void setFontScale(float fontScale) {
        setFontScale(fontScale, fontScale);
    }

    public void setFontScale(float fontScaleX2, float fontScaleY2) {
        this.fontScaleChanged = true;
        this.fontScaleX = fontScaleX2;
        this.fontScaleY = fontScaleY2;
        invalidateHierarchy();
    }

    public float getFontScaleX() {
        return this.fontScaleX;
    }

    public void setFontScaleX(float fontScaleX2) {
        setFontScale(fontScaleX2, this.fontScaleY);
    }

    public float getFontScaleY() {
        return this.fontScaleY;
    }

    public void setFontScaleY(float fontScaleY2) {
        setFontScale(this.fontScaleX, fontScaleY2);
    }

    public void setEllipsis(String ellipsis2) {
        this.ellipsis = ellipsis2;
    }

    public void setEllipsis(boolean ellipsis2) {
        if (ellipsis2) {
            this.ellipsis = "...";
        } else {
            this.ellipsis = null;
        }
    }

    /* access modifiers changed from: protected */
    public BitmapFontCache getBitmapFontCache() {
        return this.cache;
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
        sb.append(className.indexOf(36) != -1 ? "Label " : BuildConfig.FLAVOR);
        sb.append(className);
        sb.append(": ");
        sb.append(this.text);
        return sb.toString();
    }

    public static class LabelStyle {
        public Drawable background;
        public BitmapFont font;
        public Color fontColor;

        public LabelStyle() {
        }

        public LabelStyle(BitmapFont font2, Color fontColor2) {
            this.font = font2;
            this.fontColor = fontColor2;
        }

        public LabelStyle(LabelStyle style) {
            this.font = style.font;
            Color color = style.fontColor;
            if (color != null) {
                this.fontColor = new Color(color);
            }
            this.background = style.background;
        }
    }
}
