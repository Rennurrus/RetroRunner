package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.graphics.Texture;
import java.lang.reflect.Array;

public class TextureRegion {
    int regionHeight;
    int regionWidth;
    Texture texture;
    float u;
    float u2;
    float v;
    float v2;

    public TextureRegion() {
    }

    public TextureRegion(Texture texture2) {
        if (texture2 != null) {
            this.texture = texture2;
            setRegion(0, 0, texture2.getWidth(), texture2.getHeight());
            return;
        }
        throw new IllegalArgumentException("texture cannot be null.");
    }

    public TextureRegion(Texture texture2, int width, int height) {
        this.texture = texture2;
        setRegion(0, 0, width, height);
    }

    public TextureRegion(Texture texture2, int x, int y, int width, int height) {
        this.texture = texture2;
        setRegion(x, y, width, height);
    }

    public TextureRegion(Texture texture2, float u3, float v3, float u22, float v22) {
        this.texture = texture2;
        setRegion(u3, v3, u22, v22);
    }

    public TextureRegion(TextureRegion region) {
        setRegion(region);
    }

    public TextureRegion(TextureRegion region, int x, int y, int width, int height) {
        setRegion(region, x, y, width, height);
    }

    public void setRegion(Texture texture2) {
        this.texture = texture2;
        setRegion(0, 0, texture2.getWidth(), texture2.getHeight());
    }

    public void setRegion(int x, int y, int width, int height) {
        float invTexWidth = 1.0f / ((float) this.texture.getWidth());
        float invTexHeight = 1.0f / ((float) this.texture.getHeight());
        setRegion(((float) x) * invTexWidth, ((float) y) * invTexHeight, ((float) (x + width)) * invTexWidth, ((float) (y + height)) * invTexHeight);
        this.regionWidth = Math.abs(width);
        this.regionHeight = Math.abs(height);
    }

    public void setRegion(float u3, float v3, float u22, float v22) {
        int texWidth = this.texture.getWidth();
        int texHeight = this.texture.getHeight();
        this.regionWidth = Math.round(Math.abs(u22 - u3) * ((float) texWidth));
        this.regionHeight = Math.round(Math.abs(v22 - v3) * ((float) texHeight));
        if (this.regionWidth == 1 && this.regionHeight == 1) {
            float adjustX = 0.25f / ((float) texWidth);
            u3 += adjustX;
            u22 -= adjustX;
            float adjustY = 0.25f / ((float) texHeight);
            v3 += adjustY;
            v22 -= adjustY;
        }
        this.u = u3;
        this.v = v3;
        this.u2 = u22;
        this.v2 = v22;
    }

    public void setRegion(TextureRegion region) {
        this.texture = region.texture;
        setRegion(region.u, region.v, region.u2, region.v2);
    }

    public void setRegion(TextureRegion region, int x, int y, int width, int height) {
        this.texture = region.texture;
        setRegion(region.getRegionX() + x, region.getRegionY() + y, width, height);
    }

    public Texture getTexture() {
        return this.texture;
    }

    public void setTexture(Texture texture2) {
        this.texture = texture2;
    }

    public float getU() {
        return this.u;
    }

    public void setU(float u3) {
        this.u = u3;
        this.regionWidth = Math.round(Math.abs(this.u2 - u3) * ((float) this.texture.getWidth()));
    }

    public float getV() {
        return this.v;
    }

    public void setV(float v3) {
        this.v = v3;
        this.regionHeight = Math.round(Math.abs(this.v2 - v3) * ((float) this.texture.getHeight()));
    }

    public float getU2() {
        return this.u2;
    }

    public void setU2(float u22) {
        this.u2 = u22;
        this.regionWidth = Math.round(Math.abs(u22 - this.u) * ((float) this.texture.getWidth()));
    }

    public float getV2() {
        return this.v2;
    }

    public void setV2(float v22) {
        this.v2 = v22;
        this.regionHeight = Math.round(Math.abs(v22 - this.v) * ((float) this.texture.getHeight()));
    }

    public int getRegionX() {
        return Math.round(this.u * ((float) this.texture.getWidth()));
    }

    public void setRegionX(int x) {
        setU(((float) x) / ((float) this.texture.getWidth()));
    }

    public int getRegionY() {
        return Math.round(this.v * ((float) this.texture.getHeight()));
    }

    public void setRegionY(int y) {
        setV(((float) y) / ((float) this.texture.getHeight()));
    }

    public int getRegionWidth() {
        return this.regionWidth;
    }

    public void setRegionWidth(int width) {
        if (isFlipX()) {
            setU(this.u2 + (((float) width) / ((float) this.texture.getWidth())));
        } else {
            setU2(this.u + (((float) width) / ((float) this.texture.getWidth())));
        }
    }

    public int getRegionHeight() {
        return this.regionHeight;
    }

    public void setRegionHeight(int height) {
        if (isFlipY()) {
            setV(this.v2 + (((float) height) / ((float) this.texture.getHeight())));
        } else {
            setV2(this.v + (((float) height) / ((float) this.texture.getHeight())));
        }
    }

    public void flip(boolean x, boolean y) {
        if (x) {
            float temp = this.u;
            this.u = this.u2;
            this.u2 = temp;
        }
        if (y) {
            float temp2 = this.v;
            this.v = this.v2;
            this.v2 = temp2;
        }
    }

    public boolean isFlipX() {
        return this.u > this.u2;
    }

    public boolean isFlipY() {
        return this.v > this.v2;
    }

    public void scroll(float xAmount, float yAmount) {
        if (xAmount != 0.0f) {
            float width = (this.u2 - this.u) * ((float) this.texture.getWidth());
            this.u = (this.u + xAmount) % 1.0f;
            this.u2 = this.u + (width / ((float) this.texture.getWidth()));
        }
        if (yAmount != 0.0f) {
            float height = (this.v2 - this.v) * ((float) this.texture.getHeight());
            this.v = (this.v + yAmount) % 1.0f;
            this.v2 = this.v + (height / ((float) this.texture.getHeight()));
        }
    }

    public TextureRegion[][] split(int tileWidth, int tileHeight) {
        int x = getRegionX();
        int y = getRegionY();
        int width = this.regionWidth;
        int rows = this.regionHeight / tileHeight;
        int cols = width / tileWidth;
        int startX = x;
        TextureRegion[][] tiles = (TextureRegion[][]) Array.newInstance(TextureRegion.class, new int[]{rows, cols});
        int y2 = y;
        int row = 0;
        while (row < rows) {
            int x2 = startX;
            int col = 0;
            while (col < cols) {
                tiles[row][col] = new TextureRegion(this.texture, x2, y2, tileWidth, tileHeight);
                col++;
                x2 += tileWidth;
            }
            row++;
            y2 += tileHeight;
            int i = x2;
        }
        return tiles;
    }

    public static TextureRegion[][] split(Texture texture2, int tileWidth, int tileHeight) {
        return new TextureRegion(texture2).split(tileWidth, tileHeight);
    }
}
