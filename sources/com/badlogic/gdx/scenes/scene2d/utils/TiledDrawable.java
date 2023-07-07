package com.badlogic.gdx.scenes.scene2d.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TiledDrawable extends TextureRegionDrawable {
    private static final Color temp = new Color();
    private final Color color = new Color(1.0f, 1.0f, 1.0f, 1.0f);

    public TiledDrawable() {
    }

    public TiledDrawable(TextureRegion region) {
        super(region);
    }

    public TiledDrawable(TextureRegionDrawable drawable) {
        super(drawable);
    }

    public void draw(Batch batch, float x, float y, float width, float height) {
        float y2;
        int fullX;
        Batch batch2 = batch;
        Color batchColor = batch.getColor();
        temp.set(batchColor);
        batch2.setColor(batchColor.mul(this.color));
        TextureRegion region = getRegion();
        float regionWidth = (float) region.getRegionWidth();
        float regionHeight = (float) region.getRegionHeight();
        int fullX2 = (int) (width / regionWidth);
        int fullY = (int) (height / regionHeight);
        float remainingX = width - (((float) fullX2) * regionWidth);
        float remainingY = height - (((float) fullY) * regionHeight);
        float startX = x;
        float startY = y;
        float f = (x + width) - remainingX;
        float f2 = (y + height) - remainingY;
        float x2 = x;
        int i = 0;
        float y3 = y;
        while (i < fullX2) {
            float y4 = startY;
            for (int ii = 0; ii < fullY; ii++) {
                batch.draw(region, x2, y4, regionWidth, regionHeight);
                y4 += regionHeight;
            }
            x2 += regionWidth;
            i++;
            y3 = y4;
        }
        Texture texture = region.getTexture();
        float u = region.getU();
        float v2 = region.getV2();
        if (remainingX > 0.0f) {
            float u2 = u + (remainingX / ((float) texture.getWidth()));
            float v = region.getV();
            y2 = startY;
            int ii2 = 0;
            while (ii2 < fullY) {
                batch.draw(texture, x2, y2, remainingX, regionHeight, u, v2, u2, v);
                y2 += regionHeight;
                ii2++;
                fullX2 = fullX2;
                fullY = fullY;
                batchColor = batchColor;
            }
            int i2 = ii2;
            int i3 = fullY;
            Color color2 = batchColor;
            fullX = fullX2;
            if (remainingY > 0.0f) {
                batch.draw(texture, x2, y2, remainingX, remainingY, u, v2, u2, v2 - (remainingY / ((float) texture.getHeight())));
            }
        } else {
            Color color3 = batchColor;
            fullX = fullX2;
            y2 = y3;
        }
        if (remainingY > 0.0f) {
            float u22 = region.getU2();
            float v3 = v2 - (remainingY / ((float) texture.getHeight()));
            float x3 = startX;
            int i4 = 0;
            while (i4 < fullX) {
                batch.draw(texture, x3, y2, regionWidth, remainingY, u, v2, u22, v3);
                x3 += regionWidth;
                i4++;
            }
            int i5 = i4;
        }
        batch2.setColor(temp);
    }

    public void draw(Batch batch, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation) {
        throw new UnsupportedOperationException();
    }

    public Color getColor() {
        return this.color;
    }

    public TiledDrawable tint(Color tint) {
        TiledDrawable drawable = new TiledDrawable((TextureRegionDrawable) this);
        drawable.color.set(tint);
        drawable.setLeftWidth(getLeftWidth());
        drawable.setRightWidth(getRightWidth());
        drawable.setTopHeight(getTopHeight());
        drawable.setBottomHeight(getBottomHeight());
        return drawable;
    }
}
