package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class PolygonSprite {
    private Rectangle bounds = new Rectangle();
    private final Color color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
    private boolean dirty;
    private float height;
    private float originX;
    private float originY;
    PolygonRegion region;
    private float rotation;
    private float scaleX = 1.0f;
    private float scaleY = 1.0f;
    private float[] vertices;
    private float width;
    private float x;
    private float y;

    public PolygonSprite(PolygonRegion region2) {
        setRegion(region2);
        setSize((float) region2.region.regionWidth, (float) region2.region.regionHeight);
        setOrigin(this.width / 2.0f, this.height / 2.0f);
    }

    public PolygonSprite(PolygonSprite sprite) {
        set(sprite);
    }

    public void set(PolygonSprite sprite) {
        if (sprite != null) {
            setRegion(sprite.region);
            this.x = sprite.x;
            this.y = sprite.y;
            this.width = sprite.width;
            this.height = sprite.height;
            this.originX = sprite.originX;
            this.originY = sprite.originY;
            this.rotation = sprite.rotation;
            this.scaleX = sprite.scaleX;
            this.scaleY = sprite.scaleY;
            this.color.set(sprite.color);
            return;
        }
        throw new IllegalArgumentException("sprite cannot be null.");
    }

    public void setBounds(float x2, float y2, float width2, float height2) {
        this.x = x2;
        this.y = y2;
        this.width = width2;
        this.height = height2;
        this.dirty = true;
    }

    public void setSize(float width2, float height2) {
        this.width = width2;
        this.height = height2;
        this.dirty = true;
    }

    public void setPosition(float x2, float y2) {
        translate(x2 - this.x, y2 - this.y);
    }

    public void setX(float x2) {
        translateX(x2 - this.x);
    }

    public void setY(float y2) {
        translateY(y2 - this.y);
    }

    public void translateX(float xAmount) {
        this.x += xAmount;
        if (!this.dirty) {
            float[] vertices2 = this.vertices;
            for (int i = 0; i < vertices2.length; i += 5) {
                vertices2[i] = vertices2[i] + xAmount;
            }
        }
    }

    public void translateY(float yAmount) {
        this.y += yAmount;
        if (!this.dirty) {
            float[] vertices2 = this.vertices;
            for (int i = 1; i < vertices2.length; i += 5) {
                vertices2[i] = vertices2[i] + yAmount;
            }
        }
    }

    public void translate(float xAmount, float yAmount) {
        this.x += xAmount;
        this.y += yAmount;
        if (!this.dirty) {
            float[] vertices2 = this.vertices;
            for (int i = 0; i < vertices2.length; i += 5) {
                vertices2[i] = vertices2[i] + xAmount;
                int i2 = i + 1;
                vertices2[i2] = vertices2[i2] + yAmount;
            }
        }
    }

    public void setColor(Color tint) {
        this.color.set(tint);
        float color2 = tint.toFloatBits();
        float[] vertices2 = this.vertices;
        for (int i = 2; i < vertices2.length; i += 5) {
            vertices2[i] = color2;
        }
    }

    public void setColor(float r, float g, float b, float a) {
        this.color.set(r, g, b, a);
        float packedColor = this.color.toFloatBits();
        float[] vertices2 = this.vertices;
        for (int i = 2; i < vertices2.length; i += 5) {
            vertices2[i] = packedColor;
        }
    }

    public void setOrigin(float originX2, float originY2) {
        this.originX = originX2;
        this.originY = originY2;
        this.dirty = true;
    }

    public void setRotation(float degrees) {
        this.rotation = degrees;
        this.dirty = true;
    }

    public void rotate(float degrees) {
        this.rotation += degrees;
        this.dirty = true;
    }

    public void setScale(float scaleXY) {
        this.scaleX = scaleXY;
        this.scaleY = scaleXY;
        this.dirty = true;
    }

    public void setScale(float scaleX2, float scaleY2) {
        this.scaleX = scaleX2;
        this.scaleY = scaleY2;
        this.dirty = true;
    }

    public void scale(float amount) {
        this.scaleX += amount;
        this.scaleY += amount;
        this.dirty = true;
    }

    public float[] getVertices() {
        if (!this.dirty) {
            return this.vertices;
        }
        this.dirty = false;
        float originX2 = this.originX;
        float originY2 = this.originY;
        float scaleX2 = this.scaleX;
        float scaleY2 = this.scaleY;
        PolygonRegion region2 = this.region;
        float[] vertices2 = this.vertices;
        float[] regionVertices = region2.vertices;
        float worldOriginX = this.x + originX2;
        float worldOriginY = this.y + originY2;
        float sX = this.width / ((float) region2.region.getRegionWidth());
        float sY = this.height / ((float) region2.region.getRegionHeight());
        float cos = MathUtils.cosDeg(this.rotation);
        float sin = MathUtils.sinDeg(this.rotation);
        int i = 0;
        int v = 0;
        int n = regionVertices.length;
        while (i < n) {
            float fx = ((regionVertices[i] * sX) - originX2) * scaleX2;
            float fy = ((regionVertices[i + 1] * sY) - originY2) * scaleY2;
            vertices2[v] = ((cos * fx) - (sin * fy)) + worldOriginX;
            vertices2[v + 1] = (sin * fx) + (cos * fy) + worldOriginY;
            i += 2;
            v += 5;
        }
        return vertices2;
    }

    public Rectangle getBoundingRectangle() {
        float[] vertices2 = getVertices();
        float minx = vertices2[0];
        float miny = vertices2[1];
        float maxx = vertices2[0];
        float maxy = vertices2[1];
        for (int i = 5; i < vertices2.length; i += 5) {
            float x2 = vertices2[i];
            float y2 = vertices2[i + 1];
            minx = minx > x2 ? x2 : minx;
            maxx = maxx < x2 ? x2 : maxx;
            miny = miny > y2 ? y2 : miny;
            maxy = maxy < y2 ? y2 : maxy;
        }
        Rectangle rectangle = this.bounds;
        rectangle.x = minx;
        rectangle.y = miny;
        rectangle.width = maxx - minx;
        rectangle.height = maxy - miny;
        return rectangle;
    }

    public void draw(PolygonSpriteBatch spriteBatch) {
        PolygonRegion region2 = this.region;
        spriteBatch.draw(region2.region.texture, getVertices(), 0, this.vertices.length, region2.triangles, 0, region2.triangles.length);
    }

    public void draw(PolygonSpriteBatch spriteBatch, float alphaModulation) {
        Color color2 = getColor();
        float oldAlpha = color2.a;
        color2.a *= alphaModulation;
        setColor(color2);
        draw(spriteBatch);
        color2.a = oldAlpha;
        setColor(color2);
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    public float getOriginX() {
        return this.originX;
    }

    public float getOriginY() {
        return this.originY;
    }

    public float getRotation() {
        return this.rotation;
    }

    public float getScaleX() {
        return this.scaleX;
    }

    public float getScaleY() {
        return this.scaleY;
    }

    public Color getColor() {
        return this.color;
    }

    public Color getPackedColor() {
        Color.abgr8888ToColor(this.color, this.vertices[2]);
        return this.color;
    }

    public void setRegion(PolygonRegion region2) {
        this.region = region2;
        float[] regionVertices = region2.vertices;
        float[] textureCoords = region2.textureCoords;
        int verticesLength = (regionVertices.length / 2) * 5;
        float[] fArr = this.vertices;
        if (fArr == null || fArr.length != verticesLength) {
            this.vertices = new float[verticesLength];
        }
        float floatColor = this.color.toFloatBits();
        float[] vertices2 = this.vertices;
        int i = 0;
        for (int v = 2; v < verticesLength; v += 5) {
            vertices2[v] = floatColor;
            vertices2[v + 1] = textureCoords[i];
            vertices2[v + 2] = textureCoords[i + 1];
            i += 2;
        }
        this.dirty = true;
    }

    public PolygonRegion getRegion() {
        return this.region;
    }
}
