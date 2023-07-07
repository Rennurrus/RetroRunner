package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.NumberUtils;

public class Sprite extends TextureRegion {
    static final int SPRITE_SIZE = 20;
    static final int VERTEX_SIZE = 5;
    private Rectangle bounds;
    private final Color color;
    private boolean dirty;
    float height;
    private float originX;
    private float originY;
    private float rotation;
    private float scaleX;
    private float scaleY;
    final float[] vertices;
    float width;
    private float x;
    private float y;

    public Sprite() {
        this.vertices = new float[20];
        this.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.dirty = true;
        setColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public Sprite(Texture texture) {
        this(texture, 0, 0, texture.getWidth(), texture.getHeight());
    }

    public Sprite(Texture texture, int srcWidth, int srcHeight) {
        this(texture, 0, 0, srcWidth, srcHeight);
    }

    public Sprite(Texture texture, int srcX, int srcY, int srcWidth, int srcHeight) {
        this.vertices = new float[20];
        this.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.dirty = true;
        if (texture != null) {
            this.texture = texture;
            setRegion(srcX, srcY, srcWidth, srcHeight);
            setColor(1.0f, 1.0f, 1.0f, 1.0f);
            setSize((float) Math.abs(srcWidth), (float) Math.abs(srcHeight));
            setOrigin(this.width / 2.0f, this.height / 2.0f);
            return;
        }
        throw new IllegalArgumentException("texture cannot be null.");
    }

    public Sprite(TextureRegion region) {
        this.vertices = new float[20];
        this.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.dirty = true;
        setRegion(region);
        setColor(1.0f, 1.0f, 1.0f, 1.0f);
        setSize((float) region.getRegionWidth(), (float) region.getRegionHeight());
        setOrigin(this.width / 2.0f, this.height / 2.0f);
    }

    public Sprite(TextureRegion region, int srcX, int srcY, int srcWidth, int srcHeight) {
        this.vertices = new float[20];
        this.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.dirty = true;
        setRegion(region, srcX, srcY, srcWidth, srcHeight);
        setColor(1.0f, 1.0f, 1.0f, 1.0f);
        setSize((float) Math.abs(srcWidth), (float) Math.abs(srcHeight));
        setOrigin(this.width / 2.0f, this.height / 2.0f);
    }

    public Sprite(Sprite sprite) {
        this.vertices = new float[20];
        this.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.dirty = true;
        set(sprite);
    }

    public void set(Sprite sprite) {
        if (sprite != null) {
            System.arraycopy(sprite.vertices, 0, this.vertices, 0, 20);
            this.texture = sprite.texture;
            this.u = sprite.u;
            this.v = sprite.v;
            this.u2 = sprite.u2;
            this.v2 = sprite.v2;
            this.x = sprite.x;
            this.y = sprite.y;
            this.width = sprite.width;
            this.height = sprite.height;
            this.regionWidth = sprite.regionWidth;
            this.regionHeight = sprite.regionHeight;
            this.originX = sprite.originX;
            this.originY = sprite.originY;
            this.rotation = sprite.rotation;
            this.scaleX = sprite.scaleX;
            this.scaleY = sprite.scaleY;
            this.color.set(sprite.color);
            this.dirty = sprite.dirty;
            return;
        }
        throw new IllegalArgumentException("sprite cannot be null.");
    }

    public void setBounds(float x2, float y2, float width2, float height2) {
        this.x = x2;
        this.y = y2;
        this.width = width2;
        this.height = height2;
        if (!this.dirty) {
            float x22 = x2 + width2;
            float y22 = y2 + height2;
            float[] vertices2 = this.vertices;
            vertices2[0] = x2;
            vertices2[1] = y2;
            vertices2[5] = x2;
            vertices2[6] = y22;
            vertices2[10] = x22;
            vertices2[11] = y22;
            vertices2[15] = x22;
            vertices2[16] = y2;
            if (this.rotation != 0.0f || this.scaleX != 1.0f || this.scaleY != 1.0f) {
                this.dirty = true;
            }
        }
    }

    public void setSize(float width2, float height2) {
        this.width = width2;
        this.height = height2;
        if (!this.dirty) {
            float f = this.x;
            float x2 = f + width2;
            float f2 = this.y;
            float y2 = f2 + height2;
            float[] vertices2 = this.vertices;
            vertices2[0] = f;
            vertices2[1] = f2;
            vertices2[5] = f;
            vertices2[6] = y2;
            vertices2[10] = x2;
            vertices2[11] = y2;
            vertices2[15] = x2;
            vertices2[16] = f2;
            if (this.rotation != 0.0f || this.scaleX != 1.0f || this.scaleY != 1.0f) {
                this.dirty = true;
            }
        }
    }

    public void setPosition(float x2, float y2) {
        translate(x2 - this.x, y2 - this.y);
    }

    public void setOriginBasedPosition(float x2, float y2) {
        setPosition(x2 - this.originX, y2 - this.originY);
    }

    public void setX(float x2) {
        translateX(x2 - this.x);
    }

    public void setY(float y2) {
        translateY(y2 - this.y);
    }

    public void setCenterX(float x2) {
        setX(x2 - (this.width / 2.0f));
    }

    public void setCenterY(float y2) {
        setY(y2 - (this.height / 2.0f));
    }

    public void setCenter(float x2, float y2) {
        setCenterX(x2);
        setCenterY(y2);
    }

    public void translateX(float xAmount) {
        this.x += xAmount;
        if (!this.dirty) {
            float[] vertices2 = this.vertices;
            vertices2[0] = vertices2[0] + xAmount;
            vertices2[5] = vertices2[5] + xAmount;
            vertices2[10] = vertices2[10] + xAmount;
            vertices2[15] = vertices2[15] + xAmount;
        }
    }

    public void translateY(float yAmount) {
        this.y += yAmount;
        if (!this.dirty) {
            float[] vertices2 = this.vertices;
            vertices2[1] = vertices2[1] + yAmount;
            vertices2[6] = vertices2[6] + yAmount;
            vertices2[11] = vertices2[11] + yAmount;
            vertices2[16] = vertices2[16] + yAmount;
        }
    }

    public void translate(float xAmount, float yAmount) {
        this.x += xAmount;
        this.y += yAmount;
        if (!this.dirty) {
            float[] vertices2 = this.vertices;
            vertices2[0] = vertices2[0] + xAmount;
            vertices2[1] = vertices2[1] + yAmount;
            vertices2[5] = vertices2[5] + xAmount;
            vertices2[6] = vertices2[6] + yAmount;
            vertices2[10] = vertices2[10] + xAmount;
            vertices2[11] = vertices2[11] + yAmount;
            vertices2[15] = vertices2[15] + xAmount;
            vertices2[16] = vertices2[16] + yAmount;
        }
    }

    public void setColor(Color tint) {
        this.color.set(tint);
        float color2 = tint.toFloatBits();
        float[] vertices2 = this.vertices;
        vertices2[2] = color2;
        vertices2[7] = color2;
        vertices2[12] = color2;
        vertices2[17] = color2;
    }

    public void setAlpha(float a) {
        Color color2 = this.color;
        color2.a = a;
        float color3 = color2.toFloatBits();
        float[] fArr = this.vertices;
        fArr[2] = color3;
        fArr[7] = color3;
        fArr[12] = color3;
        fArr[17] = color3;
    }

    public void setColor(float r, float g, float b, float a) {
        this.color.set(r, g, b, a);
        float color2 = this.color.toFloatBits();
        float[] vertices2 = this.vertices;
        vertices2[2] = color2;
        vertices2[7] = color2;
        vertices2[12] = color2;
        vertices2[17] = color2;
    }

    public void setPackedColor(float packedColor) {
        Color.abgr8888ToColor(this.color, packedColor);
        float[] vertices2 = this.vertices;
        vertices2[2] = packedColor;
        vertices2[7] = packedColor;
        vertices2[12] = packedColor;
        vertices2[17] = packedColor;
    }

    public void setOrigin(float originX2, float originY2) {
        this.originX = originX2;
        this.originY = originY2;
        this.dirty = true;
    }

    public void setOriginCenter() {
        this.originX = this.width / 2.0f;
        this.originY = this.height / 2.0f;
        this.dirty = true;
    }

    public void setRotation(float degrees) {
        this.rotation = degrees;
        this.dirty = true;
    }

    public float getRotation() {
        return this.rotation;
    }

    public void rotate(float degrees) {
        if (degrees != 0.0f) {
            this.rotation += degrees;
            this.dirty = true;
        }
    }

    public void rotate90(boolean clockwise) {
        float[] vertices2 = this.vertices;
        if (clockwise) {
            float temp = vertices2[4];
            vertices2[4] = vertices2[19];
            vertices2[19] = vertices2[14];
            vertices2[14] = vertices2[9];
            vertices2[9] = temp;
            float temp2 = vertices2[3];
            vertices2[3] = vertices2[18];
            vertices2[18] = vertices2[13];
            vertices2[13] = vertices2[8];
            vertices2[8] = temp2;
            return;
        }
        float temp3 = vertices2[4];
        vertices2[4] = vertices2[9];
        vertices2[9] = vertices2[14];
        vertices2[14] = vertices2[19];
        vertices2[19] = temp3;
        float temp4 = vertices2[3];
        vertices2[3] = vertices2[8];
        vertices2[8] = vertices2[13];
        vertices2[13] = vertices2[18];
        vertices2[18] = temp4;
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
        if (this.dirty) {
            this.dirty = false;
            float[] vertices2 = this.vertices;
            float localX = -this.originX;
            float localY = -this.originY;
            float localX2 = this.width + localX;
            float localY2 = this.height + localY;
            float worldOriginX = this.x - localX;
            float worldOriginY = this.y - localY;
            if (!(this.scaleX == 1.0f && this.scaleY == 1.0f)) {
                float f = this.scaleX;
                localX *= f;
                float f2 = this.scaleY;
                localY *= f2;
                localX2 *= f;
                localY2 *= f2;
            }
            float f3 = this.rotation;
            if (f3 != 0.0f) {
                float cos = MathUtils.cosDeg(f3);
                float sin = MathUtils.sinDeg(this.rotation);
                float localXCos = localX * cos;
                float localXSin = localX * sin;
                float localY2Cos = localY2 * cos;
                float localY2Sin = localY2 * sin;
                float x1 = (localXCos - (localY * sin)) + worldOriginX;
                float y1 = (localY * cos) + localXSin + worldOriginY;
                vertices2[0] = x1;
                vertices2[1] = y1;
                float x2 = (localXCos - localY2Sin) + worldOriginX;
                float y2 = localY2Cos + localXSin + worldOriginY;
                vertices2[5] = x2;
                vertices2[6] = y2;
                float x3 = ((localX2 * cos) - localY2Sin) + worldOriginX;
                float y3 = localY2Cos + (localX2 * sin) + worldOriginY;
                vertices2[10] = x3;
                vertices2[11] = y3;
                vertices2[15] = x1 + (x3 - x2);
                vertices2[16] = y3 - (y2 - y1);
            } else {
                float x12 = localX + worldOriginX;
                float y12 = localY + worldOriginY;
                float x22 = localX2 + worldOriginX;
                float y22 = localY2 + worldOriginY;
                vertices2[0] = x12;
                vertices2[1] = y12;
                vertices2[5] = x12;
                vertices2[6] = y22;
                vertices2[10] = x22;
                vertices2[11] = y22;
                vertices2[15] = x22;
                vertices2[16] = y12;
            }
        }
        return this.vertices;
    }

    public Rectangle getBoundingRectangle() {
        float[] vertices2 = getVertices();
        float minx = vertices2[0];
        float miny = vertices2[1];
        float maxx = vertices2[0];
        float maxy = vertices2[1];
        float minx2 = minx > vertices2[5] ? vertices2[5] : minx;
        float minx3 = minx2 > vertices2[10] ? vertices2[10] : minx2;
        float minx4 = minx3 > vertices2[15] ? vertices2[15] : minx3;
        float maxx2 = maxx < vertices2[5] ? vertices2[5] : maxx;
        float maxx3 = maxx2 < vertices2[10] ? vertices2[10] : maxx2;
        float maxx4 = maxx3 < vertices2[15] ? vertices2[15] : maxx3;
        float miny2 = miny > vertices2[6] ? vertices2[6] : miny;
        float miny3 = miny2 > vertices2[11] ? vertices2[11] : miny2;
        float miny4 = miny3 > vertices2[16] ? vertices2[16] : miny3;
        float maxy2 = maxy < vertices2[6] ? vertices2[6] : maxy;
        float maxy3 = maxy2 < vertices2[11] ? vertices2[11] : maxy2;
        float maxy4 = maxy3 < vertices2[16] ? vertices2[16] : maxy3;
        if (this.bounds == null) {
            this.bounds = new Rectangle();
        }
        Rectangle rectangle = this.bounds;
        rectangle.x = minx4;
        rectangle.y = miny4;
        rectangle.width = maxx4 - minx4;
        rectangle.height = maxy4 - miny4;
        return rectangle;
    }

    public void draw(Batch batch) {
        batch.draw(this.texture, getVertices(), 0, 20);
    }

    public void draw(Batch batch, float alphaModulation) {
        float oldAlpha = getColor().a;
        setAlpha(oldAlpha * alphaModulation);
        draw(batch);
        setAlpha(oldAlpha);
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

    public float getScaleX() {
        return this.scaleX;
    }

    public float getScaleY() {
        return this.scaleY;
    }

    public Color getColor() {
        int intBits = NumberUtils.floatToIntColor(this.vertices[2]);
        Color color2 = this.color;
        color2.r = ((float) (intBits & 255)) / 255.0f;
        color2.g = ((float) ((intBits >>> 8) & 255)) / 255.0f;
        color2.b = ((float) ((intBits >>> 16) & 255)) / 255.0f;
        color2.a = ((float) ((intBits >>> 24) & 255)) / 255.0f;
        return color2;
    }

    public void setRegion(float u, float v, float u2, float v2) {
        super.setRegion(u, v, u2, v2);
        float[] vertices2 = this.vertices;
        vertices2[3] = u;
        vertices2[4] = v2;
        vertices2[8] = u;
        vertices2[9] = v;
        vertices2[13] = u2;
        vertices2[14] = v;
        vertices2[18] = u2;
        vertices2[19] = v2;
    }

    public void setU(float u) {
        super.setU(u);
        float[] fArr = this.vertices;
        fArr[3] = u;
        fArr[8] = u;
    }

    public void setV(float v) {
        super.setV(v);
        float[] fArr = this.vertices;
        fArr[9] = v;
        fArr[14] = v;
    }

    public void setU2(float u2) {
        super.setU2(u2);
        float[] fArr = this.vertices;
        fArr[13] = u2;
        fArr[18] = u2;
    }

    public void setV2(float v2) {
        super.setV2(v2);
        float[] fArr = this.vertices;
        fArr[4] = v2;
        fArr[19] = v2;
    }

    public void setFlip(boolean x2, boolean y2) {
        boolean performX = false;
        boolean performY = false;
        if (isFlipX() != x2) {
            performX = true;
        }
        if (isFlipY() != y2) {
            performY = true;
        }
        flip(performX, performY);
    }

    public void flip(boolean x2, boolean y2) {
        super.flip(x2, y2);
        float[] vertices2 = this.vertices;
        if (x2) {
            float temp = vertices2[3];
            vertices2[3] = vertices2[13];
            vertices2[13] = temp;
            float temp2 = vertices2[8];
            vertices2[8] = vertices2[18];
            vertices2[18] = temp2;
        }
        if (y2) {
            float temp3 = vertices2[4];
            vertices2[4] = vertices2[14];
            vertices2[14] = temp3;
            float temp4 = vertices2[9];
            vertices2[9] = vertices2[19];
            vertices2[19] = temp4;
        }
    }

    public void scroll(float xAmount, float yAmount) {
        float[] vertices2 = this.vertices;
        if (xAmount != 0.0f) {
            float u = (vertices2[3] + xAmount) % 1.0f;
            float u2 = (this.width / ((float) this.texture.getWidth())) + u;
            this.u = u;
            this.u2 = u2;
            vertices2[3] = u;
            vertices2[8] = u;
            vertices2[13] = u2;
            vertices2[18] = u2;
        }
        if (yAmount != 0.0f) {
            float v = (vertices2[9] + yAmount) % 1.0f;
            float v2 = (this.height / ((float) this.texture.getHeight())) + v;
            this.v = v;
            this.v2 = v2;
            vertices2[4] = v2;
            vertices2[9] = v;
            vertices2[14] = v;
            vertices2[19] = v2;
        }
    }
}
