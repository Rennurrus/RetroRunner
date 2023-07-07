package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class CpuSpriteBatch extends SpriteBatch {
    private final Affine2 adjustAffine;
    private boolean adjustNeeded;
    private boolean haveIdentityRealMatrix;
    private final Affine2 tmpAffine;
    private final Matrix4 virtualMatrix;

    public CpuSpriteBatch() {
        this(1000);
    }

    public CpuSpriteBatch(int size) {
        this(size, (ShaderProgram) null);
    }

    public CpuSpriteBatch(int size, ShaderProgram defaultShader) {
        super(size, defaultShader);
        this.virtualMatrix = new Matrix4();
        this.adjustAffine = new Affine2();
        this.haveIdentityRealMatrix = true;
        this.tmpAffine = new Affine2();
    }

    public void flushAndSyncTransformMatrix() {
        flush();
        if (this.adjustNeeded) {
            this.haveIdentityRealMatrix = checkIdt(this.virtualMatrix);
            if (this.haveIdentityRealMatrix || this.virtualMatrix.det() != 0.0f) {
                this.adjustNeeded = false;
                super.setTransformMatrix(this.virtualMatrix);
                return;
            }
            throw new GdxRuntimeException("Transform matrix is singular, can't sync");
        }
    }

    public Matrix4 getTransformMatrix() {
        return this.adjustNeeded ? this.virtualMatrix : super.getTransformMatrix();
    }

    public void setTransformMatrix(Matrix4 transform) {
        Matrix4 realMatrix = super.getTransformMatrix();
        if (checkEqual(realMatrix, transform)) {
            this.adjustNeeded = false;
        } else if (isDrawing()) {
            this.virtualMatrix.setAsAffine(transform);
            this.adjustNeeded = true;
            if (this.haveIdentityRealMatrix) {
                this.adjustAffine.set(transform);
                return;
            }
            this.tmpAffine.set(transform);
            this.adjustAffine.set(realMatrix).inv().mul(this.tmpAffine);
        } else {
            realMatrix.setAsAffine(transform);
            this.haveIdentityRealMatrix = checkIdt(realMatrix);
        }
    }

    public void setTransformMatrix(Affine2 transform) {
        Matrix4 realMatrix = super.getTransformMatrix();
        if (checkEqual(realMatrix, transform)) {
            this.adjustNeeded = false;
            return;
        }
        this.virtualMatrix.setAsAffine(transform);
        if (isDrawing()) {
            this.adjustNeeded = true;
            if (this.haveIdentityRealMatrix) {
                this.adjustAffine.set(transform);
            } else {
                this.adjustAffine.set(realMatrix).inv().mul(transform);
            }
        } else {
            realMatrix.setAsAffine(transform);
            this.haveIdentityRealMatrix = checkIdt(realMatrix);
        }
    }

    public void draw(Texture texture, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation, int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX, boolean flipY) {
        if (!this.adjustNeeded) {
            super.draw(texture, x, y, originX, originY, width, height, scaleX, scaleY, rotation, srcX, srcY, srcWidth, srcHeight, flipX, flipY);
        } else {
            drawAdjusted(texture, x, y, originX, originY, width, height, scaleX, scaleY, rotation, srcX, srcY, srcWidth, srcHeight, flipX, flipY);
        }
    }

    public void draw(Texture texture, float x, float y, float width, float height, int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX, boolean flipY) {
        if (!this.adjustNeeded) {
            super.draw(texture, x, y, width, height, srcX, srcY, srcWidth, srcHeight, flipX, flipY);
        } else {
            drawAdjusted(texture, x, y, 0.0f, 0.0f, width, height, 1.0f, 1.0f, 0.0f, srcX, srcY, srcWidth, srcHeight, flipX, flipY);
        }
    }

    public void draw(Texture texture, float x, float y, int srcX, int srcY, int srcWidth, int srcHeight) {
        if (!this.adjustNeeded) {
            super.draw(texture, x, y, srcX, srcY, srcWidth, srcHeight);
            return;
        }
        int i = srcWidth;
        drawAdjusted(texture, x, y, 0.0f, 0.0f, (float) i, (float) srcHeight, 1.0f, 1.0f, 0.0f, srcX, srcY, i, srcHeight, false, false);
    }

    public void draw(Texture texture, float x, float y, float width, float height, float u, float v, float u2, float v2) {
        if (!this.adjustNeeded) {
            super.draw(texture, x, y, width, height, u, v, u2, v2);
        } else {
            drawAdjustedUV(texture, x, y, 0.0f, 0.0f, width, height, 1.0f, 1.0f, 0.0f, u, v, u2, v2, false, false);
        }
    }

    public void draw(Texture texture, float x, float y) {
        if (!this.adjustNeeded) {
            super.draw(texture, x, y);
            return;
        }
        drawAdjusted(texture, x, y, 0.0f, 0.0f, (float) texture.getWidth(), (float) texture.getHeight(), 1.0f, 1.0f, 0.0f, 0, 1, 1, 0, false, false);
    }

    public void draw(Texture texture, float x, float y, float width, float height) {
        if (!this.adjustNeeded) {
            super.draw(texture, x, y, width, height);
        } else {
            drawAdjusted(texture, x, y, 0.0f, 0.0f, width, height, 1.0f, 1.0f, 0.0f, 0, 1, 1, 0, false, false);
        }
    }

    public void draw(TextureRegion region, float x, float y) {
        if (!this.adjustNeeded) {
            super.draw(region, x, y);
            return;
        }
        drawAdjusted(region, x, y, 0.0f, 0.0f, (float) region.getRegionWidth(), (float) region.getRegionHeight(), 1.0f, 1.0f, 0.0f);
    }

    public void draw(TextureRegion region, float x, float y, float width, float height) {
        if (!this.adjustNeeded) {
            super.draw(region, x, y, width, height);
        } else {
            drawAdjusted(region, x, y, 0.0f, 0.0f, width, height, 1.0f, 1.0f, 0.0f);
        }
    }

    public void draw(TextureRegion region, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation) {
        if (!this.adjustNeeded) {
            super.draw(region, x, y, originX, originY, width, height, scaleX, scaleY, rotation);
        } else {
            drawAdjusted(region, x, y, originX, originY, width, height, scaleX, scaleY, rotation);
        }
    }

    public void draw(TextureRegion region, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation, boolean clockwise) {
        if (!this.adjustNeeded) {
            super.draw(region, x, y, originX, originY, width, height, scaleX, scaleY, rotation, clockwise);
        } else {
            drawAdjusted(region, x, y, originX, originY, width, height, scaleX, scaleY, rotation, clockwise);
        }
    }

    public void draw(Texture texture, float[] spriteVertices, int offset, int count) {
        if (count % 20 != 0) {
            throw new GdxRuntimeException("invalid vertex count");
        } else if (!this.adjustNeeded) {
            super.draw(texture, spriteVertices, offset, count);
        } else {
            drawAdjusted(texture, spriteVertices, offset, count);
        }
    }

    public void draw(TextureRegion region, float width, float height, Affine2 transform) {
        if (!this.adjustNeeded) {
            super.draw(region, width, height, transform);
        } else {
            drawAdjusted(region, width, height, transform);
        }
    }

    private void drawAdjusted(TextureRegion region, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation) {
        TextureRegion textureRegion = region;
        drawAdjustedUV(textureRegion.texture, x, y, originX, originY, width, height, scaleX, scaleY, rotation, textureRegion.u, textureRegion.v2, textureRegion.u2, textureRegion.v, false, false);
    }

    private void drawAdjusted(Texture texture, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation, int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX, boolean flipY) {
        int i = srcX;
        int i2 = srcY;
        float invTexWidth = 1.0f / ((float) texture.getWidth());
        float invTexHeight = 1.0f / ((float) texture.getHeight());
        drawAdjustedUV(texture, x, y, originX, originY, width, height, scaleX, scaleY, rotation, ((float) i) * invTexWidth, ((float) (i2 + srcHeight)) * invTexHeight, ((float) (i + srcWidth)) * invTexWidth, ((float) i2) * invTexHeight, flipX, flipY);
    }

    private void drawAdjustedUV(Texture texture, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation, float u, float v, float u2, float v2, boolean flipX, boolean flipY) {
        float x4;
        float y3;
        float x3;
        float y2;
        float x2;
        float y1;
        float x1;
        float cos;
        float u22;
        float u3;
        float v22;
        float v3;
        float f = originX;
        float f2 = originY;
        if (this.drawing) {
            if (texture != this.lastTexture) {
                switchTexture(texture);
            } else if (this.idx == this.vertices.length) {
                super.flush();
            }
            float worldOriginX = x + f;
            float worldOriginY = y + f2;
            float fx = -f;
            float fy = -f2;
            float fx2 = width - f;
            float fy2 = height - f2;
            if (!(scaleX == 1.0f && scaleY == 1.0f)) {
                fx *= scaleX;
                fy *= scaleY;
                fx2 *= scaleX;
                fy2 *= scaleY;
            }
            float p1x = fx;
            float p1y = fy;
            float p2x = fx;
            float p2y = fy2;
            float p3x = fx2;
            float p3y = fy2;
            float p4x = fx2;
            float p4y = fy;
            if (rotation != 0.0f) {
                float cos2 = MathUtils.cosDeg(rotation);
                float sin = MathUtils.sinDeg(rotation);
                x1 = (cos2 * p1x) - (sin * p1y);
                y1 = (sin * p1x) + (cos2 * p1y);
                x2 = (cos2 * p2x) - (sin * p2y);
                y2 = (sin * p2x) + (cos2 * p2y);
                x3 = (cos2 * p3x) - (sin * p3y);
                y3 = (sin * p3x) + (cos2 * p3y);
                x4 = x1 + (x3 - x2);
                cos = y3 - (y2 - y1);
            } else {
                x1 = p1x;
                y1 = p1y;
                x2 = p2x;
                y2 = p2y;
                x3 = p3x;
                y3 = p3y;
                x4 = p4x;
                cos = p4y;
            }
            float x12 = x1 + worldOriginX;
            float y12 = y1 + worldOriginY;
            float x22 = x2 + worldOriginX;
            float y22 = y2 + worldOriginY;
            float x32 = x3 + worldOriginX;
            float y32 = y3 + worldOriginY;
            float x42 = x4 + worldOriginX;
            float y4 = cos + worldOriginY;
            if (flipX) {
                u3 = u2;
                u22 = u;
            } else {
                u3 = u;
                u22 = u2;
            }
            if (flipY) {
                v3 = v2;
                v22 = v;
            } else {
                v3 = v;
                v22 = v2;
            }
            Affine2 t = this.adjustAffine;
            float f3 = worldOriginX;
            float f4 = worldOriginY;
            this.vertices[this.idx + 0] = (t.m00 * x12) + (t.m01 * y12) + t.m02;
            this.vertices[this.idx + 1] = (t.m10 * x12) + (t.m11 * y12) + t.m12;
            this.vertices[this.idx + 2] = this.colorPacked;
            this.vertices[this.idx + 3] = u3;
            this.vertices[this.idx + 4] = v3;
            this.vertices[this.idx + 5] = (t.m00 * x22) + (t.m01 * y22) + t.m02;
            this.vertices[this.idx + 6] = (t.m10 * x22) + (t.m11 * y22) + t.m12;
            this.vertices[this.idx + 7] = this.colorPacked;
            this.vertices[this.idx + 8] = u3;
            this.vertices[this.idx + 9] = v22;
            this.vertices[this.idx + 10] = (t.m00 * x32) + (t.m01 * y32) + t.m02;
            this.vertices[this.idx + 11] = (t.m10 * x32) + (t.m11 * y32) + t.m12;
            this.vertices[this.idx + 12] = this.colorPacked;
            this.vertices[this.idx + 13] = u22;
            this.vertices[this.idx + 14] = v22;
            this.vertices[this.idx + 15] = (t.m00 * x42) + (t.m01 * y4) + t.m02;
            this.vertices[this.idx + 16] = (t.m10 * x42) + (t.m11 * y4) + t.m12;
            this.vertices[this.idx + 17] = this.colorPacked;
            this.vertices[this.idx + 18] = u22;
            this.vertices[this.idx + 19] = v3;
            this.idx += 20;
            return;
        }
        throw new IllegalStateException("CpuSpriteBatch.begin must be called before draw.");
    }

    private void drawAdjusted(TextureRegion region, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation, boolean clockwise) {
        float x4;
        float y3;
        float x3;
        float y2;
        float x2;
        float y1;
        float x1;
        float cos;
        float u4;
        float v3;
        float u3;
        float v2;
        float u2;
        float v1;
        float u1;
        float u42;
        TextureRegion textureRegion = region;
        float f = originX;
        float f2 = originY;
        if (this.drawing) {
            if (textureRegion.texture != this.lastTexture) {
                switchTexture(textureRegion.texture);
            } else if (this.idx == this.vertices.length) {
                super.flush();
            }
            float worldOriginX = x + f;
            float worldOriginY = y + f2;
            float fx = -f;
            float fy = -f2;
            float fx2 = width - f;
            float fy2 = height - f2;
            if (!(scaleX == 1.0f && scaleY == 1.0f)) {
                fx *= scaleX;
                fy *= scaleY;
                fx2 *= scaleX;
                fy2 *= scaleY;
            }
            float p1x = fx;
            float p1y = fy;
            float p2x = fx;
            float p2y = fy2;
            float p3x = fx2;
            float p3y = fy2;
            float p4x = fx2;
            float p4y = fy;
            if (rotation != 0.0f) {
                float cos2 = MathUtils.cosDeg(rotation);
                float sin = MathUtils.sinDeg(rotation);
                x1 = (cos2 * p1x) - (sin * p1y);
                y1 = (sin * p1x) + (cos2 * p1y);
                x2 = (cos2 * p2x) - (sin * p2y);
                y2 = (sin * p2x) + (cos2 * p2y);
                x3 = (cos2 * p3x) - (sin * p3y);
                y3 = (sin * p3x) + (cos2 * p3y);
                x4 = x1 + (x3 - x2);
                cos = y3 - (y2 - y1);
            } else {
                x1 = p1x;
                y1 = p1y;
                x2 = p2x;
                y2 = p2y;
                x3 = p3x;
                y3 = p3y;
                x4 = p4x;
                cos = p4y;
            }
            float x12 = x1 + worldOriginX;
            float y12 = y1 + worldOriginY;
            float x22 = x2 + worldOriginX;
            float y22 = y2 + worldOriginY;
            float x32 = x3 + worldOriginX;
            float y32 = y3 + worldOriginY;
            float x42 = x4 + worldOriginX;
            float y4 = cos + worldOriginY;
            if (clockwise) {
                u1 = textureRegion.u2;
                v1 = textureRegion.v2;
                u2 = textureRegion.u;
                v2 = textureRegion.v2;
                u3 = textureRegion.u;
                v3 = textureRegion.v;
                u4 = textureRegion.u2;
                u42 = textureRegion.v;
            } else {
                u1 = textureRegion.u;
                v1 = textureRegion.v;
                u2 = textureRegion.u2;
                v2 = textureRegion.v;
                u3 = textureRegion.u2;
                v3 = textureRegion.v2;
                u4 = textureRegion.u;
                u42 = textureRegion.v2;
            }
            Affine2 t = this.adjustAffine;
            float f3 = worldOriginX;
            float f4 = worldOriginY;
            float f5 = fx;
            this.vertices[this.idx + 0] = (t.m00 * x12) + (t.m01 * y12) + t.m02;
            this.vertices[this.idx + 1] = (t.m10 * x12) + (t.m11 * y12) + t.m12;
            this.vertices[this.idx + 2] = this.colorPacked;
            this.vertices[this.idx + 3] = u1;
            this.vertices[this.idx + 4] = v1;
            this.vertices[this.idx + 5] = (t.m00 * x22) + (t.m01 * y22) + t.m02;
            this.vertices[this.idx + 6] = (t.m10 * x22) + (t.m11 * y22) + t.m12;
            this.vertices[this.idx + 7] = this.colorPacked;
            this.vertices[this.idx + 8] = u2;
            this.vertices[this.idx + 9] = v2;
            this.vertices[this.idx + 10] = (t.m00 * x32) + (t.m01 * y32) + t.m02;
            this.vertices[this.idx + 11] = (t.m10 * x32) + (t.m11 * y32) + t.m12;
            this.vertices[this.idx + 12] = this.colorPacked;
            this.vertices[this.idx + 13] = u3;
            this.vertices[this.idx + 14] = v3;
            this.vertices[this.idx + 15] = (t.m00 * x42) + (t.m01 * y4) + t.m02;
            this.vertices[this.idx + 16] = (t.m10 * x42) + (t.m11 * y4) + t.m12;
            this.vertices[this.idx + 17] = this.colorPacked;
            this.vertices[this.idx + 18] = u4;
            this.vertices[this.idx + 19] = u42;
            this.idx += 20;
            return;
        }
        throw new IllegalStateException("CpuSpriteBatch.begin must be called before draw.");
    }

    private void drawAdjusted(TextureRegion region, float width, float height, Affine2 transform) {
        TextureRegion textureRegion = region;
        if (this.drawing) {
            if (textureRegion.texture != this.lastTexture) {
                switchTexture(textureRegion.texture);
            } else if (this.idx == this.vertices.length) {
                super.flush();
            }
            Affine2 t = transform;
            float x1 = t.m02;
            float y1 = t.m12;
            float x2 = (t.m01 * height) + t.m02;
            float y2 = (t.m11 * height) + t.m12;
            float x3 = (t.m00 * width) + (t.m01 * height) + t.m02;
            float y3 = (t.m10 * width) + (t.m11 * height) + t.m12;
            float x4 = (t.m00 * width) + t.m02;
            float y4 = (t.m10 * width) + t.m12;
            float u = textureRegion.u;
            float v = textureRegion.v2;
            float u2 = textureRegion.u2;
            float v2 = textureRegion.v;
            Affine2 t2 = this.adjustAffine;
            float y42 = y4;
            float x42 = x4;
            this.vertices[this.idx + 0] = (t2.m00 * x1) + (t2.m01 * y1) + t2.m02;
            this.vertices[this.idx + 1] = (t2.m10 * x1) + (t2.m11 * y1) + t2.m12;
            this.vertices[this.idx + 2] = this.colorPacked;
            this.vertices[this.idx + 3] = u;
            this.vertices[this.idx + 4] = v;
            this.vertices[this.idx + 5] = (t2.m00 * x2) + (t2.m01 * y2) + t2.m02;
            this.vertices[this.idx + 6] = (t2.m10 * x2) + (t2.m11 * y2) + t2.m12;
            this.vertices[this.idx + 7] = this.colorPacked;
            this.vertices[this.idx + 8] = u;
            this.vertices[this.idx + 9] = v2;
            this.vertices[this.idx + 10] = (t2.m00 * x3) + (t2.m01 * y3) + t2.m02;
            this.vertices[this.idx + 11] = (t2.m10 * x3) + (t2.m11 * y3) + t2.m12;
            this.vertices[this.idx + 12] = this.colorPacked;
            this.vertices[this.idx + 13] = u2;
            this.vertices[this.idx + 14] = v2;
            this.vertices[this.idx + 15] = (t2.m00 * x42) + (t2.m01 * y42) + t2.m02;
            this.vertices[this.idx + 16] = (t2.m10 * x42) + (t2.m11 * y42) + t2.m12;
            this.vertices[this.idx + 17] = this.colorPacked;
            this.vertices[this.idx + 18] = u2;
            this.vertices[this.idx + 19] = v;
            this.idx += 20;
            return;
        }
        throw new IllegalStateException("CpuSpriteBatch.begin must be called before draw.");
    }

    private void drawAdjusted(Texture texture, float[] spriteVertices, int offset, int count) {
        if (this.drawing) {
            if (texture != this.lastTexture) {
                switchTexture(texture);
            }
            Affine2 t = this.adjustAffine;
            int copyCount = Math.min(this.vertices.length - this.idx, count);
            do {
                count -= copyCount;
                while (copyCount > 0) {
                    float x = spriteVertices[offset];
                    float y = spriteVertices[offset + 1];
                    this.vertices[this.idx] = (t.m00 * x) + (t.m01 * y) + t.m02;
                    this.vertices[this.idx + 1] = (t.m10 * x) + (t.m11 * y) + t.m12;
                    this.vertices[this.idx + 2] = spriteVertices[offset + 2];
                    this.vertices[this.idx + 3] = spriteVertices[offset + 3];
                    this.vertices[this.idx + 4] = spriteVertices[offset + 4];
                    this.idx += 5;
                    offset += 5;
                    copyCount -= 5;
                }
                if (count > 0) {
                    super.flush();
                    copyCount = Math.min(this.vertices.length, count);
                    continue;
                }
            } while (count > 0);
            return;
        }
        throw new IllegalStateException("CpuSpriteBatch.begin must be called before draw.");
    }

    private static boolean checkEqual(Matrix4 a, Matrix4 b) {
        if (a == b) {
            return true;
        }
        if (a.val[0] == b.val[0] && a.val[1] == b.val[1] && a.val[4] == b.val[4] && a.val[5] == b.val[5] && a.val[12] == b.val[12] && a.val[13] == b.val[13]) {
            return true;
        }
        return false;
    }

    private static boolean checkEqual(Matrix4 matrix, Affine2 affine) {
        float[] val = matrix.getValues();
        return val[0] == affine.m00 && val[1] == affine.m10 && val[4] == affine.m01 && val[5] == affine.m11 && val[12] == affine.m02 && val[13] == affine.m12;
    }

    private static boolean checkIdt(Matrix4 matrix) {
        float[] val = matrix.getValues();
        return val[0] == 1.0f && val[1] == 0.0f && val[4] == 0.0f && val[5] == 1.0f && val[12] == 0.0f && val[13] == 0.0f;
    }
}
