package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class NinePatch {
    public static final int BOTTOM_CENTER = 7;
    public static final int BOTTOM_LEFT = 6;
    public static final int BOTTOM_RIGHT = 8;
    public static final int MIDDLE_CENTER = 4;
    public static final int MIDDLE_LEFT = 3;
    public static final int MIDDLE_RIGHT = 5;
    public static final int TOP_CENTER = 1;
    public static final int TOP_LEFT = 0;
    public static final int TOP_RIGHT = 2;
    private static final Color tmpDrawColor = new Color();
    private int bottomCenter;
    private float bottomHeight;
    private int bottomLeft;
    private int bottomRight;
    private final Color color;
    private int idx;
    private float leftWidth;
    private int middleCenter;
    private float middleHeight;
    private int middleLeft;
    private int middleRight;
    private float middleWidth;
    private float padBottom;
    private float padLeft;
    private float padRight;
    private float padTop;
    private float rightWidth;
    private Texture texture;
    private int topCenter;
    private float topHeight;
    private int topLeft;
    private int topRight;
    private float[] vertices;

    public NinePatch(Texture texture2, int left, int right, int top, int bottom) {
        this(new TextureRegion(texture2), left, right, top, bottom);
    }

    public NinePatch(TextureRegion region, int left, int right, int top, int bottom) {
        this.bottomLeft = -1;
        this.bottomCenter = -1;
        this.bottomRight = -1;
        this.middleLeft = -1;
        this.middleCenter = -1;
        this.middleRight = -1;
        this.topLeft = -1;
        this.topCenter = -1;
        this.topRight = -1;
        this.vertices = new float[180];
        this.color = new Color(Color.WHITE);
        this.padLeft = -1.0f;
        this.padRight = -1.0f;
        this.padTop = -1.0f;
        this.padBottom = -1.0f;
        if (region != null) {
            int middleWidth2 = (region.getRegionWidth() - left) - right;
            int middleHeight2 = (region.getRegionHeight() - top) - bottom;
            TextureRegion[] patches = new TextureRegion[9];
            if (top > 0) {
                if (left > 0) {
                    patches[0] = new TextureRegion(region, 0, 0, left, top);
                }
                if (middleWidth2 > 0) {
                    patches[1] = new TextureRegion(region, left, 0, middleWidth2, top);
                }
                if (right > 0) {
                    patches[2] = new TextureRegion(region, left + middleWidth2, 0, right, top);
                }
            }
            if (middleHeight2 > 0) {
                if (left > 0) {
                    patches[3] = new TextureRegion(region, 0, top, left, middleHeight2);
                }
                if (middleWidth2 > 0) {
                    patches[4] = new TextureRegion(region, left, top, middleWidth2, middleHeight2);
                }
                if (right > 0) {
                    patches[5] = new TextureRegion(region, left + middleWidth2, top, right, middleHeight2);
                }
            }
            if (bottom > 0) {
                if (left > 0) {
                    patches[6] = new TextureRegion(region, 0, top + middleHeight2, left, bottom);
                }
                if (middleWidth2 > 0) {
                    patches[7] = new TextureRegion(region, left, top + middleHeight2, middleWidth2, bottom);
                }
                if (right > 0) {
                    patches[8] = new TextureRegion(region, left + middleWidth2, top + middleHeight2, right, bottom);
                }
            }
            if (left == 0 && middleWidth2 == 0) {
                patches[1] = patches[2];
                patches[4] = patches[5];
                patches[7] = patches[8];
                patches[2] = null;
                patches[5] = null;
                patches[8] = null;
            }
            if (top == 0 && middleHeight2 == 0) {
                patches[3] = patches[6];
                patches[4] = patches[7];
                patches[5] = patches[8];
                patches[6] = null;
                patches[7] = null;
                patches[8] = null;
            }
            load(patches);
            return;
        }
        throw new IllegalArgumentException("region cannot be null.");
    }

    public NinePatch(Texture texture2, Color color2) {
        this(texture2);
        setColor(color2);
    }

    public NinePatch(Texture texture2) {
        this(new TextureRegion(texture2));
    }

    public NinePatch(TextureRegion region, Color color2) {
        this(region);
        setColor(color2);
    }

    public NinePatch(TextureRegion region) {
        this.bottomLeft = -1;
        this.bottomCenter = -1;
        this.bottomRight = -1;
        this.middleLeft = -1;
        this.middleCenter = -1;
        this.middleRight = -1;
        this.topLeft = -1;
        this.topCenter = -1;
        this.topRight = -1;
        this.vertices = new float[180];
        this.color = new Color(Color.WHITE);
        this.padLeft = -1.0f;
        this.padRight = -1.0f;
        this.padTop = -1.0f;
        this.padBottom = -1.0f;
        load(new TextureRegion[]{null, null, null, null, region, null, null, null, null});
    }

    public NinePatch(TextureRegion... patches) {
        this.bottomLeft = -1;
        this.bottomCenter = -1;
        this.bottomRight = -1;
        this.middleLeft = -1;
        this.middleCenter = -1;
        this.middleRight = -1;
        this.topLeft = -1;
        this.topCenter = -1;
        this.topRight = -1;
        this.vertices = new float[180];
        this.color = new Color(Color.WHITE);
        this.padLeft = -1.0f;
        this.padRight = -1.0f;
        this.padTop = -1.0f;
        this.padBottom = -1.0f;
        if (patches == null || patches.length != 9) {
            throw new IllegalArgumentException("NinePatch needs nine TextureRegions");
        }
        load(patches);
        float leftWidth2 = getLeftWidth();
        if ((patches[0] == null || ((float) patches[0].getRegionWidth()) == leftWidth2) && ((patches[3] == null || ((float) patches[3].getRegionWidth()) == leftWidth2) && (patches[6] == null || ((float) patches[6].getRegionWidth()) == leftWidth2))) {
            float rightWidth2 = getRightWidth();
            if ((patches[2] == null || ((float) patches[2].getRegionWidth()) == rightWidth2) && ((patches[5] == null || ((float) patches[5].getRegionWidth()) == rightWidth2) && (patches[8] == null || ((float) patches[8].getRegionWidth()) == rightWidth2))) {
                float bottomHeight2 = getBottomHeight();
                if ((patches[6] == null || ((float) patches[6].getRegionHeight()) == bottomHeight2) && ((patches[7] == null || ((float) patches[7].getRegionHeight()) == bottomHeight2) && (patches[8] == null || ((float) patches[8].getRegionHeight()) == bottomHeight2))) {
                    float topHeight2 = getTopHeight();
                    if ((patches[0] != null && ((float) patches[0].getRegionHeight()) != topHeight2) || ((patches[1] != null && ((float) patches[1].getRegionHeight()) != topHeight2) || (patches[2] != null && ((float) patches[2].getRegionHeight()) != topHeight2))) {
                        throw new GdxRuntimeException("Top side patches must have the same height");
                    }
                    return;
                }
                throw new GdxRuntimeException("Bottom side patches must have the same height");
            }
            throw new GdxRuntimeException("Right side patches must have the same width");
        }
        throw new GdxRuntimeException("Left side patches must have the same width");
    }

    public NinePatch(NinePatch ninePatch) {
        this(ninePatch, ninePatch.color);
    }

    public NinePatch(NinePatch ninePatch, Color color2) {
        this.bottomLeft = -1;
        this.bottomCenter = -1;
        this.bottomRight = -1;
        this.middleLeft = -1;
        this.middleCenter = -1;
        this.middleRight = -1;
        this.topLeft = -1;
        this.topCenter = -1;
        this.topRight = -1;
        this.vertices = new float[180];
        this.color = new Color(Color.WHITE);
        this.padLeft = -1.0f;
        this.padRight = -1.0f;
        this.padTop = -1.0f;
        this.padBottom = -1.0f;
        this.texture = ninePatch.texture;
        this.bottomLeft = ninePatch.bottomLeft;
        this.bottomCenter = ninePatch.bottomCenter;
        this.bottomRight = ninePatch.bottomRight;
        this.middleLeft = ninePatch.middleLeft;
        this.middleCenter = ninePatch.middleCenter;
        this.middleRight = ninePatch.middleRight;
        this.topLeft = ninePatch.topLeft;
        this.topCenter = ninePatch.topCenter;
        this.topRight = ninePatch.topRight;
        this.leftWidth = ninePatch.leftWidth;
        this.rightWidth = ninePatch.rightWidth;
        this.middleWidth = ninePatch.middleWidth;
        this.middleHeight = ninePatch.middleHeight;
        this.topHeight = ninePatch.topHeight;
        this.bottomHeight = ninePatch.bottomHeight;
        this.padLeft = ninePatch.padLeft;
        this.padTop = ninePatch.padTop;
        this.padBottom = ninePatch.padBottom;
        this.padRight = ninePatch.padRight;
        this.vertices = new float[ninePatch.vertices.length];
        float[] fArr = ninePatch.vertices;
        System.arraycopy(fArr, 0, this.vertices, 0, fArr.length);
        this.idx = ninePatch.idx;
        this.color.set(color2);
    }

    private void load(TextureRegion[] patches) {
        float color2 = Color.WHITE_FLOAT_BITS;
        if (patches[6] != null) {
            this.bottomLeft = add(patches[6], color2, false, false);
            this.leftWidth = (float) patches[6].getRegionWidth();
            this.bottomHeight = (float) patches[6].getRegionHeight();
        }
        if (patches[7] != null) {
            this.bottomCenter = add(patches[7], color2, true, false);
            this.middleWidth = Math.max(this.middleWidth, (float) patches[7].getRegionWidth());
            this.bottomHeight = Math.max(this.bottomHeight, (float) patches[7].getRegionHeight());
        }
        if (patches[8] != null) {
            this.bottomRight = add(patches[8], color2, false, false);
            this.rightWidth = Math.max(this.rightWidth, (float) patches[8].getRegionWidth());
            this.bottomHeight = Math.max(this.bottomHeight, (float) patches[8].getRegionHeight());
        }
        if (patches[3] != null) {
            this.middleLeft = add(patches[3], color2, false, true);
            this.leftWidth = Math.max(this.leftWidth, (float) patches[3].getRegionWidth());
            this.middleHeight = Math.max(this.middleHeight, (float) patches[3].getRegionHeight());
        }
        if (patches[4] != null) {
            this.middleCenter = add(patches[4], color2, true, true);
            this.middleWidth = Math.max(this.middleWidth, (float) patches[4].getRegionWidth());
            this.middleHeight = Math.max(this.middleHeight, (float) patches[4].getRegionHeight());
        }
        if (patches[5] != null) {
            this.middleRight = add(patches[5], color2, false, true);
            this.rightWidth = Math.max(this.rightWidth, (float) patches[5].getRegionWidth());
            this.middleHeight = Math.max(this.middleHeight, (float) patches[5].getRegionHeight());
        }
        if (patches[0] != null) {
            this.topLeft = add(patches[0], color2, false, false);
            this.leftWidth = Math.max(this.leftWidth, (float) patches[0].getRegionWidth());
            this.topHeight = Math.max(this.topHeight, (float) patches[0].getRegionHeight());
        }
        if (patches[1] != null) {
            this.topCenter = add(patches[1], color2, true, false);
            this.middleWidth = Math.max(this.middleWidth, (float) patches[1].getRegionWidth());
            this.topHeight = Math.max(this.topHeight, (float) patches[1].getRegionHeight());
        }
        if (patches[2] != null) {
            this.topRight = add(patches[2], color2, false, false);
            this.rightWidth = Math.max(this.rightWidth, (float) patches[2].getRegionWidth());
            this.topHeight = Math.max(this.topHeight, (float) patches[2].getRegionHeight());
        }
        int i = this.idx;
        float[] fArr = this.vertices;
        if (i < fArr.length) {
            float[] newVertices = new float[i];
            System.arraycopy(fArr, 0, newVertices, 0, i);
            this.vertices = newVertices;
        }
    }

    private int add(TextureRegion region, float color2, boolean isStretchW, boolean isStretchH) {
        Texture texture2 = this.texture;
        if (texture2 == null) {
            this.texture = region.getTexture();
        } else if (texture2 != region.getTexture()) {
            throw new IllegalArgumentException("All regions must be from the same texture.");
        }
        float u = region.u;
        float v = region.v2;
        float u2 = region.u2;
        float v2 = region.v;
        if (this.texture.getMagFilter() == Texture.TextureFilter.Linear || this.texture.getMinFilter() == Texture.TextureFilter.Linear) {
            if (isStretchW) {
                float halfTexelWidth = 0.5f / ((float) this.texture.getWidth());
                u += halfTexelWidth;
                u2 -= halfTexelWidth;
            }
            if (isStretchH) {
                float halfTexelHeight = 0.5f / ((float) this.texture.getHeight());
                v -= halfTexelHeight;
                v2 += halfTexelHeight;
            }
        }
        float[] vertices2 = this.vertices;
        int i = this.idx;
        vertices2[i + 2] = color2;
        vertices2[i + 3] = u;
        vertices2[i + 4] = v;
        vertices2[i + 7] = color2;
        vertices2[i + 8] = u;
        vertices2[i + 9] = v2;
        vertices2[i + 12] = color2;
        vertices2[i + 13] = u2;
        vertices2[i + 14] = v2;
        vertices2[i + 17] = color2;
        vertices2[i + 18] = u2;
        vertices2[i + 19] = v;
        this.idx = i + 20;
        return this.idx - 20;
    }

    private void set(int idx2, float x, float y, float width, float height, float color2) {
        float fx2 = x + width;
        float fy2 = y + height;
        float[] vertices2 = this.vertices;
        vertices2[idx2] = x;
        vertices2[idx2 + 1] = y;
        vertices2[idx2 + 2] = color2;
        vertices2[idx2 + 5] = x;
        vertices2[idx2 + 6] = fy2;
        vertices2[idx2 + 7] = color2;
        vertices2[idx2 + 10] = fx2;
        vertices2[idx2 + 11] = fy2;
        vertices2[idx2 + 12] = color2;
        vertices2[idx2 + 15] = fx2;
        vertices2[idx2 + 16] = y;
        vertices2[idx2 + 17] = color2;
    }

    private void prepareVertices(Batch batch, float x, float y, float width, float height) {
        float centerColumnX = x + this.leftWidth;
        float rightColumnX = (x + width) - this.rightWidth;
        float middleRowY = y + this.bottomHeight;
        float topRowY = (y + height) - this.topHeight;
        float c = tmpDrawColor.set(this.color).mul(batch.getColor()).toFloatBits();
        int i = this.bottomLeft;
        if (i != -1) {
            set(i, x, y, centerColumnX - x, middleRowY - y, c);
        }
        int i2 = this.bottomCenter;
        if (i2 != -1) {
            set(i2, centerColumnX, y, rightColumnX - centerColumnX, middleRowY - y, c);
        }
        int i3 = this.bottomRight;
        if (i3 != -1) {
            set(i3, rightColumnX, y, (x + width) - rightColumnX, middleRowY - y, c);
        }
        int i4 = this.middleLeft;
        if (i4 != -1) {
            set(i4, x, middleRowY, centerColumnX - x, topRowY - middleRowY, c);
        }
        int i5 = this.middleCenter;
        if (i5 != -1) {
            set(i5, centerColumnX, middleRowY, rightColumnX - centerColumnX, topRowY - middleRowY, c);
        }
        int i6 = this.middleRight;
        if (i6 != -1) {
            set(i6, rightColumnX, middleRowY, (x + width) - rightColumnX, topRowY - middleRowY, c);
        }
        int i7 = this.topLeft;
        if (i7 != -1) {
            float f = x;
            float f2 = topRowY;
            set(i7, f, f2, centerColumnX - x, (y + height) - topRowY, c);
        }
        int i8 = this.topCenter;
        if (i8 != -1) {
            float f3 = centerColumnX;
            float f4 = topRowY;
            set(i8, f3, f4, rightColumnX - centerColumnX, (y + height) - topRowY, c);
        }
        int i9 = this.topRight;
        if (i9 != -1) {
            set(i9, rightColumnX, topRowY, (x + width) - rightColumnX, (y + height) - topRowY, c);
        }
    }

    public void draw(Batch batch, float x, float y, float width, float height) {
        prepareVertices(batch, x, y, width, height);
        batch.draw(this.texture, this.vertices, 0, this.idx);
    }

    public void draw(Batch batch, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation) {
        prepareVertices(batch, x, y, width, height);
        float worldOriginX = x + originX;
        float worldOriginY = y + originY;
        int n = this.idx;
        float[] vertices2 = this.vertices;
        if (rotation != 0.0f) {
            for (int i = 0; i < n; i += 5) {
                float vx = (vertices2[i] - worldOriginX) * scaleX;
                float vy = (vertices2[i + 1] - worldOriginY) * scaleY;
                float cos = MathUtils.cosDeg(rotation);
                float sin = MathUtils.sinDeg(rotation);
                vertices2[i] = ((cos * vx) - (sin * vy)) + worldOriginX;
                vertices2[i + 1] = (sin * vx) + (cos * vy) + worldOriginY;
            }
        } else if (!(scaleX == 1.0f && scaleY == 1.0f)) {
            for (int i2 = 0; i2 < n; i2 += 5) {
                vertices2[i2] = ((vertices2[i2] - worldOriginX) * scaleX) + worldOriginX;
                vertices2[i2 + 1] = ((vertices2[i2 + 1] - worldOriginY) * scaleY) + worldOriginY;
            }
        }
        Batch batch2 = batch;
        batch.draw(this.texture, vertices2, 0, n);
    }

    public void setColor(Color color2) {
        this.color.set(color2);
    }

    public Color getColor() {
        return this.color;
    }

    public float getLeftWidth() {
        return this.leftWidth;
    }

    public void setLeftWidth(float leftWidth2) {
        this.leftWidth = leftWidth2;
    }

    public float getRightWidth() {
        return this.rightWidth;
    }

    public void setRightWidth(float rightWidth2) {
        this.rightWidth = rightWidth2;
    }

    public float getTopHeight() {
        return this.topHeight;
    }

    public void setTopHeight(float topHeight2) {
        this.topHeight = topHeight2;
    }

    public float getBottomHeight() {
        return this.bottomHeight;
    }

    public void setBottomHeight(float bottomHeight2) {
        this.bottomHeight = bottomHeight2;
    }

    public float getMiddleWidth() {
        return this.middleWidth;
    }

    public void setMiddleWidth(float middleWidth2) {
        this.middleWidth = middleWidth2;
    }

    public float getMiddleHeight() {
        return this.middleHeight;
    }

    public void setMiddleHeight(float middleHeight2) {
        this.middleHeight = middleHeight2;
    }

    public float getTotalWidth() {
        return this.leftWidth + this.middleWidth + this.rightWidth;
    }

    public float getTotalHeight() {
        return this.topHeight + this.middleHeight + this.bottomHeight;
    }

    public void setPadding(float left, float right, float top, float bottom) {
        this.padLeft = left;
        this.padRight = right;
        this.padTop = top;
        this.padBottom = bottom;
    }

    public float getPadLeft() {
        float f = this.padLeft;
        if (f == -1.0f) {
            return getLeftWidth();
        }
        return f;
    }

    public void setPadLeft(float left) {
        this.padLeft = left;
    }

    public float getPadRight() {
        float f = this.padRight;
        if (f == -1.0f) {
            return getRightWidth();
        }
        return f;
    }

    public void setPadRight(float right) {
        this.padRight = right;
    }

    public float getPadTop() {
        float f = this.padTop;
        if (f == -1.0f) {
            return getTopHeight();
        }
        return f;
    }

    public void setPadTop(float top) {
        this.padTop = top;
    }

    public float getPadBottom() {
        float f = this.padBottom;
        if (f == -1.0f) {
            return getBottomHeight();
        }
        return f;
    }

    public void setPadBottom(float bottom) {
        this.padBottom = bottom;
    }

    public void scale(float scaleX, float scaleY) {
        this.leftWidth *= scaleX;
        this.rightWidth *= scaleX;
        this.topHeight *= scaleY;
        this.bottomHeight *= scaleY;
        this.middleWidth *= scaleX;
        this.middleHeight *= scaleY;
        float f = this.padLeft;
        if (f != -1.0f) {
            this.padLeft = f * scaleX;
        }
        float f2 = this.padRight;
        if (f2 != -1.0f) {
            this.padRight = f2 * scaleX;
        }
        float f3 = this.padTop;
        if (f3 != -1.0f) {
            this.padTop = f3 * scaleY;
        }
        float f4 = this.padBottom;
        if (f4 != -1.0f) {
            this.padBottom = f4 * scaleY;
        }
    }

    public Texture getTexture() {
        return this.texture;
    }
}
