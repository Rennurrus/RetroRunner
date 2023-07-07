package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;

public class SpriteBatch implements Batch {
    @Deprecated
    public static Mesh.VertexDataType defaultVertexDataType = Mesh.VertexDataType.VertexArray;
    private int blendDstFunc;
    private int blendDstFuncAlpha;
    private int blendSrcFunc;
    private int blendSrcFuncAlpha;
    private boolean blendingDisabled;
    private final Color color;
    float colorPacked;
    private final Matrix4 combinedMatrix;
    private ShaderProgram customShader;
    boolean drawing;
    int idx;
    float invTexHeight;
    float invTexWidth;
    Texture lastTexture;
    public int maxSpritesInBatch;
    private Mesh mesh;
    private boolean ownsShader;
    private final Matrix4 projectionMatrix;
    public int renderCalls;
    private final ShaderProgram shader;
    public int totalRenderCalls;
    private final Matrix4 transformMatrix;
    final float[] vertices;

    public SpriteBatch() {
        this(1000, (ShaderProgram) null);
    }

    public SpriteBatch(int size) {
        this(size, (ShaderProgram) null);
    }

    public SpriteBatch(int size, ShaderProgram defaultShader) {
        this.idx = 0;
        this.lastTexture = null;
        this.invTexWidth = 0.0f;
        this.invTexHeight = 0.0f;
        this.drawing = false;
        this.transformMatrix = new Matrix4();
        this.projectionMatrix = new Matrix4();
        this.combinedMatrix = new Matrix4();
        this.blendingDisabled = false;
        this.blendSrcFunc = GL20.GL_SRC_ALPHA;
        this.blendDstFunc = GL20.GL_ONE_MINUS_SRC_ALPHA;
        this.blendSrcFuncAlpha = GL20.GL_SRC_ALPHA;
        this.blendDstFuncAlpha = GL20.GL_ONE_MINUS_SRC_ALPHA;
        this.customShader = null;
        this.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        this.colorPacked = Color.WHITE_FLOAT_BITS;
        this.renderCalls = 0;
        this.totalRenderCalls = 0;
        this.maxSpritesInBatch = 0;
        if (size <= 8191) {
            this.mesh = new Mesh(Gdx.gl30 != null ? Mesh.VertexDataType.VertexBufferObjectWithVAO : defaultVertexDataType, false, size * 4, size * 6, new VertexAttribute(1, 2, ShaderProgram.POSITION_ATTRIBUTE), new VertexAttribute(4, 4, ShaderProgram.COLOR_ATTRIBUTE), new VertexAttribute(16, 2, "a_texCoord0"));
            this.projectionMatrix.setToOrtho2D(0.0f, 0.0f, (float) Gdx.graphics.getWidth(), (float) Gdx.graphics.getHeight());
            this.vertices = new float[(size * 20)];
            int len = size * 6;
            short[] indices = new short[len];
            short j = 0;
            int i = 0;
            while (i < len) {
                indices[i] = j;
                indices[i + 1] = (short) (j + 1);
                indices[i + 2] = (short) (j + 2);
                indices[i + 3] = (short) (j + 2);
                indices[i + 4] = (short) (j + 3);
                indices[i + 5] = j;
                i += 6;
                j = (short) (j + 4);
            }
            this.mesh.setIndices(indices);
            if (defaultShader == null) {
                this.shader = createDefaultShader();
                this.ownsShader = true;
                return;
            }
            this.shader = defaultShader;
            return;
        }
        throw new IllegalArgumentException("Can't have more than 8191 sprites per batch: " + size);
    }

    public static ShaderProgram createDefaultShader() {
        ShaderProgram shader2 = new ShaderProgram("attribute vec4 a_position;\nattribute vec4 a_color;\nattribute vec2 a_texCoord0;\nuniform mat4 u_projTrans;\nvarying vec4 v_color;\nvarying vec2 v_texCoords;\n\nvoid main()\n{\n   v_color = a_color;\n   v_color.a = v_color.a * (255.0/254.0);\n   v_texCoords = a_texCoord0;\n   gl_Position =  u_projTrans * a_position;\n}\n", "#ifdef GL_ES\n#define LOWP lowp\nprecision mediump float;\n#else\n#define LOWP \n#endif\nvarying LOWP vec4 v_color;\nvarying vec2 v_texCoords;\nuniform sampler2D u_texture;\nvoid main()\n{\n  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n}");
        if (shader2.isCompiled()) {
            return shader2;
        }
        throw new IllegalArgumentException("Error compiling shader: " + shader2.getLog());
    }

    public void begin() {
        if (!this.drawing) {
            this.renderCalls = 0;
            Gdx.gl.glDepthMask(false);
            ShaderProgram shaderProgram = this.customShader;
            if (shaderProgram != null) {
                shaderProgram.begin();
            } else {
                this.shader.begin();
            }
            setupMatrices();
            this.drawing = true;
            return;
        }
        throw new IllegalStateException("SpriteBatch.end must be called before begin.");
    }

    public void end() {
        if (this.drawing) {
            if (this.idx > 0) {
                flush();
            }
            this.lastTexture = null;
            this.drawing = false;
            GL20 gl = Gdx.gl;
            gl.glDepthMask(true);
            if (isBlendingEnabled()) {
                gl.glDisable(GL20.GL_BLEND);
            }
            ShaderProgram shaderProgram = this.customShader;
            if (shaderProgram != null) {
                shaderProgram.end();
            } else {
                this.shader.end();
            }
        } else {
            throw new IllegalStateException("SpriteBatch.begin must be called before end.");
        }
    }

    public void setColor(Color tint) {
        this.color.set(tint);
        this.colorPacked = tint.toFloatBits();
    }

    public void setColor(float r, float g, float b, float a) {
        this.color.set(r, g, b, a);
        this.colorPacked = this.color.toFloatBits();
    }

    public Color getColor() {
        return this.color;
    }

    public void setPackedColor(float packedColor) {
        Color.abgr8888ToColor(this.color, packedColor);
        this.colorPacked = packedColor;
    }

    public float getPackedColor() {
        return this.colorPacked;
    }

    public void draw(Texture texture, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation, int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX, boolean flipY) {
        float x4;
        float y3;
        float x3;
        float y2;
        float x2;
        float y1;
        float x1;
        float cos;
        float f = originX;
        float f2 = originY;
        int i = srcX;
        int i2 = srcY;
        if (this.drawing) {
            float[] vertices2 = this.vertices;
            if (texture != this.lastTexture) {
                switchTexture(texture);
            } else if (this.idx == vertices2.length) {
                flush();
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
            float f3 = this.invTexWidth;
            float u = ((float) i) * f3;
            float f4 = worldOriginX;
            float f5 = this.invTexHeight;
            float v = ((float) (i2 + srcHeight)) * f5;
            float f6 = worldOriginY;
            float u2 = ((float) (i + srcWidth)) * f3;
            float v2 = ((float) i2) * f5;
            if (flipX) {
                float tmp = u;
                u = u2;
                u2 = tmp;
            }
            if (flipY) {
                float tmp2 = v;
                v = v2;
                v2 = tmp2;
            }
            float tmp3 = this.colorPacked;
            int idx2 = this.idx;
            vertices2[idx2] = x12;
            vertices2[idx2 + 1] = y12;
            vertices2[idx2 + 2] = tmp3;
            vertices2[idx2 + 3] = u;
            vertices2[idx2 + 4] = v;
            vertices2[idx2 + 5] = x22;
            vertices2[idx2 + 6] = y22;
            vertices2[idx2 + 7] = tmp3;
            vertices2[idx2 + 8] = u;
            vertices2[idx2 + 9] = v2;
            vertices2[idx2 + 10] = x32;
            vertices2[idx2 + 11] = y32;
            vertices2[idx2 + 12] = tmp3;
            vertices2[idx2 + 13] = u2;
            vertices2[idx2 + 14] = v2;
            vertices2[idx2 + 15] = x42;
            vertices2[idx2 + 16] = y4;
            vertices2[idx2 + 17] = tmp3;
            vertices2[idx2 + 18] = u2;
            vertices2[idx2 + 19] = v;
            float f7 = u;
            this.idx = idx2 + 20;
            return;
        }
        throw new IllegalStateException("SpriteBatch.begin must be called before draw.");
    }

    public void draw(Texture texture, float x, float y, float width, float height, int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX, boolean flipY) {
        int i = srcX;
        int i2 = srcY;
        if (this.drawing) {
            float[] vertices2 = this.vertices;
            if (texture != this.lastTexture) {
                switchTexture(texture);
            } else if (this.idx == vertices2.length) {
                flush();
            }
            float f = this.invTexWidth;
            float u = ((float) i) * f;
            float f2 = this.invTexHeight;
            float v = ((float) (i2 + srcHeight)) * f2;
            float u2 = ((float) (i + srcWidth)) * f;
            float v2 = ((float) i2) * f2;
            float fx2 = x + width;
            float fy2 = y + height;
            if (flipX) {
                float tmp = u;
                u = u2;
                u2 = tmp;
            }
            if (flipY) {
                float tmp2 = v;
                v = v2;
                v2 = tmp2;
            }
            float tmp3 = this.colorPacked;
            int idx2 = this.idx;
            vertices2[idx2] = x;
            vertices2[idx2 + 1] = y;
            vertices2[idx2 + 2] = tmp3;
            vertices2[idx2 + 3] = u;
            vertices2[idx2 + 4] = v;
            vertices2[idx2 + 5] = x;
            vertices2[idx2 + 6] = fy2;
            vertices2[idx2 + 7] = tmp3;
            vertices2[idx2 + 8] = u;
            vertices2[idx2 + 9] = v2;
            vertices2[idx2 + 10] = fx2;
            vertices2[idx2 + 11] = fy2;
            vertices2[idx2 + 12] = tmp3;
            vertices2[idx2 + 13] = u2;
            vertices2[idx2 + 14] = v2;
            vertices2[idx2 + 15] = fx2;
            vertices2[idx2 + 16] = y;
            vertices2[idx2 + 17] = tmp3;
            vertices2[idx2 + 18] = u2;
            vertices2[idx2 + 19] = v;
            this.idx = idx2 + 20;
            return;
        }
        Texture texture2 = texture;
        throw new IllegalStateException("SpriteBatch.begin must be called before draw.");
    }

    public void draw(Texture texture, float x, float y, int srcX, int srcY, int srcWidth, int srcHeight) {
        int i = srcX;
        int i2 = srcY;
        int i3 = srcWidth;
        int i4 = srcHeight;
        if (this.drawing) {
            float[] vertices2 = this.vertices;
            if (texture != this.lastTexture) {
                switchTexture(texture);
            } else if (this.idx == vertices2.length) {
                flush();
            }
            float f = this.invTexWidth;
            float u = ((float) i) * f;
            float f2 = this.invTexHeight;
            float v = ((float) (i2 + i4)) * f2;
            float u2 = ((float) (i + i3)) * f;
            float v2 = ((float) i2) * f2;
            float fx2 = x + ((float) i3);
            float fy2 = y + ((float) i4);
            float color2 = this.colorPacked;
            int idx2 = this.idx;
            vertices2[idx2] = x;
            vertices2[idx2 + 1] = y;
            vertices2[idx2 + 2] = color2;
            vertices2[idx2 + 3] = u;
            vertices2[idx2 + 4] = v;
            vertices2[idx2 + 5] = x;
            vertices2[idx2 + 6] = fy2;
            vertices2[idx2 + 7] = color2;
            vertices2[idx2 + 8] = u;
            vertices2[idx2 + 9] = v2;
            vertices2[idx2 + 10] = fx2;
            vertices2[idx2 + 11] = fy2;
            vertices2[idx2 + 12] = color2;
            vertices2[idx2 + 13] = u2;
            vertices2[idx2 + 14] = v2;
            vertices2[idx2 + 15] = fx2;
            vertices2[idx2 + 16] = y;
            vertices2[idx2 + 17] = color2;
            vertices2[idx2 + 18] = u2;
            vertices2[idx2 + 19] = v;
            this.idx = idx2 + 20;
            return;
        }
        Texture texture2 = texture;
        throw new IllegalStateException("SpriteBatch.begin must be called before draw.");
    }

    public void draw(Texture texture, float x, float y, float width, float height, float u, float v, float u2, float v2) {
        if (this.drawing) {
            float[] vertices2 = this.vertices;
            if (texture != this.lastTexture) {
                switchTexture(texture);
            } else if (this.idx == vertices2.length) {
                flush();
            }
            float fx2 = x + width;
            float fy2 = y + height;
            float color2 = this.colorPacked;
            int idx2 = this.idx;
            vertices2[idx2] = x;
            vertices2[idx2 + 1] = y;
            vertices2[idx2 + 2] = color2;
            vertices2[idx2 + 3] = u;
            vertices2[idx2 + 4] = v;
            vertices2[idx2 + 5] = x;
            vertices2[idx2 + 6] = fy2;
            vertices2[idx2 + 7] = color2;
            vertices2[idx2 + 8] = u;
            vertices2[idx2 + 9] = v2;
            vertices2[idx2 + 10] = fx2;
            vertices2[idx2 + 11] = fy2;
            vertices2[idx2 + 12] = color2;
            vertices2[idx2 + 13] = u2;
            vertices2[idx2 + 14] = v2;
            vertices2[idx2 + 15] = fx2;
            vertices2[idx2 + 16] = y;
            vertices2[idx2 + 17] = color2;
            vertices2[idx2 + 18] = u2;
            vertices2[idx2 + 19] = v;
            this.idx = idx2 + 20;
            return;
        }
        throw new IllegalStateException("SpriteBatch.begin must be called before draw.");
    }

    public void draw(Texture texture, float x, float y) {
        draw(texture, x, y, (float) texture.getWidth(), (float) texture.getHeight());
    }

    public void draw(Texture texture, float x, float y, float width, float height) {
        if (this.drawing) {
            float[] vertices2 = this.vertices;
            if (texture != this.lastTexture) {
                switchTexture(texture);
            } else if (this.idx == vertices2.length) {
                flush();
            }
            float fx2 = x + width;
            float fy2 = y + height;
            float color2 = this.colorPacked;
            int idx2 = this.idx;
            vertices2[idx2] = x;
            vertices2[idx2 + 1] = y;
            vertices2[idx2 + 2] = color2;
            vertices2[idx2 + 3] = 0.0f;
            vertices2[idx2 + 4] = 1.0f;
            vertices2[idx2 + 5] = x;
            vertices2[idx2 + 6] = fy2;
            vertices2[idx2 + 7] = color2;
            vertices2[idx2 + 8] = 0.0f;
            vertices2[idx2 + 9] = 0.0f;
            vertices2[idx2 + 10] = fx2;
            vertices2[idx2 + 11] = fy2;
            vertices2[idx2 + 12] = color2;
            vertices2[idx2 + 13] = 1.0f;
            vertices2[idx2 + 14] = 0.0f;
            vertices2[idx2 + 15] = fx2;
            vertices2[idx2 + 16] = y;
            vertices2[idx2 + 17] = color2;
            vertices2[idx2 + 18] = 1.0f;
            vertices2[idx2 + 19] = 1.0f;
            this.idx = idx2 + 20;
            return;
        }
        Texture texture2 = texture;
        throw new IllegalStateException("SpriteBatch.begin must be called before draw.");
    }

    public void draw(Texture texture, float[] spriteVertices, int offset, int count) {
        if (this.drawing) {
            int verticesLength = this.vertices.length;
            int remainingVertices = verticesLength;
            if (texture != this.lastTexture) {
                switchTexture(texture);
            } else {
                remainingVertices -= this.idx;
                if (remainingVertices == 0) {
                    flush();
                    remainingVertices = verticesLength;
                }
            }
            int copyCount = Math.min(remainingVertices, count);
            System.arraycopy(spriteVertices, offset, this.vertices, this.idx, copyCount);
            this.idx += copyCount;
            int count2 = count - copyCount;
            while (count2 > 0) {
                offset += copyCount;
                flush();
                copyCount = Math.min(verticesLength, count2);
                System.arraycopy(spriteVertices, offset, this.vertices, 0, copyCount);
                this.idx += copyCount;
                count2 -= copyCount;
            }
            return;
        }
        throw new IllegalStateException("SpriteBatch.begin must be called before draw.");
    }

    public void draw(TextureRegion region, float x, float y) {
        draw(region, x, y, (float) region.getRegionWidth(), (float) region.getRegionHeight());
    }

    public void draw(TextureRegion region, float x, float y, float width, float height) {
        TextureRegion textureRegion = region;
        if (this.drawing) {
            float[] vertices2 = this.vertices;
            Texture texture = textureRegion.texture;
            if (texture != this.lastTexture) {
                switchTexture(texture);
            } else if (this.idx == vertices2.length) {
                flush();
            }
            float fx2 = x + width;
            float fy2 = y + height;
            float u = textureRegion.u;
            float v = textureRegion.v2;
            float u2 = textureRegion.u2;
            float v2 = textureRegion.v;
            float color2 = this.colorPacked;
            int idx2 = this.idx;
            vertices2[idx2] = x;
            vertices2[idx2 + 1] = y;
            vertices2[idx2 + 2] = color2;
            vertices2[idx2 + 3] = u;
            vertices2[idx2 + 4] = v;
            vertices2[idx2 + 5] = x;
            vertices2[idx2 + 6] = fy2;
            vertices2[idx2 + 7] = color2;
            vertices2[idx2 + 8] = u;
            vertices2[idx2 + 9] = v2;
            vertices2[idx2 + 10] = fx2;
            vertices2[idx2 + 11] = fy2;
            vertices2[idx2 + 12] = color2;
            vertices2[idx2 + 13] = u2;
            vertices2[idx2 + 14] = v2;
            vertices2[idx2 + 15] = fx2;
            vertices2[idx2 + 16] = y;
            vertices2[idx2 + 17] = color2;
            vertices2[idx2 + 18] = u2;
            vertices2[idx2 + 19] = v;
            this.idx = idx2 + 20;
            return;
        }
        throw new IllegalStateException("SpriteBatch.begin must be called before draw.");
    }

    public void draw(TextureRegion region, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation) {
        float x4;
        float y3;
        float x3;
        float y2;
        float x2;
        float y1;
        float x1;
        float cos;
        TextureRegion textureRegion = region;
        float f = originX;
        float f2 = originY;
        if (this.drawing) {
            float[] vertices2 = this.vertices;
            Texture texture = textureRegion.texture;
            if (texture != this.lastTexture) {
                switchTexture(texture);
            } else if (this.idx == vertices2.length) {
                flush();
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
            float u = textureRegion.u;
            float v = textureRegion.v2;
            Texture texture2 = texture;
            float u2 = textureRegion.u2;
            float f3 = worldOriginX;
            float worldOriginX2 = textureRegion.v;
            float color2 = this.colorPacked;
            float f4 = worldOriginY;
            int idx2 = this.idx;
            vertices2[idx2] = x12;
            vertices2[idx2 + 1] = y12;
            vertices2[idx2 + 2] = color2;
            vertices2[idx2 + 3] = u;
            vertices2[idx2 + 4] = v;
            vertices2[idx2 + 5] = x22;
            vertices2[idx2 + 6] = y22;
            vertices2[idx2 + 7] = color2;
            vertices2[idx2 + 8] = u;
            vertices2[idx2 + 9] = worldOriginX2;
            vertices2[idx2 + 10] = x32;
            vertices2[idx2 + 11] = y32;
            vertices2[idx2 + 12] = color2;
            vertices2[idx2 + 13] = u2;
            vertices2[idx2 + 14] = worldOriginX2;
            vertices2[idx2 + 15] = x42;
            vertices2[idx2 + 16] = y4;
            vertices2[idx2 + 17] = color2;
            vertices2[idx2 + 18] = u2;
            vertices2[idx2 + 19] = v;
            float f5 = color2;
            this.idx = idx2 + 20;
            return;
        }
        throw new IllegalStateException("SpriteBatch.begin must be called before draw.");
    }

    public void draw(TextureRegion region, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation, boolean clockwise) {
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
            float[] vertices2 = this.vertices;
            Texture texture = textureRegion.texture;
            if (texture != this.lastTexture) {
                switchTexture(texture);
            } else if (this.idx == vertices2.length) {
                flush();
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
            float color2 = this.colorPacked;
            int idx2 = this.idx;
            vertices2[idx2] = x12;
            vertices2[idx2 + 1] = y12;
            vertices2[idx2 + 2] = color2;
            vertices2[idx2 + 3] = u1;
            vertices2[idx2 + 4] = v1;
            vertices2[idx2 + 5] = x22;
            vertices2[idx2 + 6] = y22;
            vertices2[idx2 + 7] = color2;
            vertices2[idx2 + 8] = u2;
            vertices2[idx2 + 9] = v2;
            vertices2[idx2 + 10] = x32;
            vertices2[idx2 + 11] = y32;
            vertices2[idx2 + 12] = color2;
            vertices2[idx2 + 13] = u3;
            vertices2[idx2 + 14] = v3;
            vertices2[idx2 + 15] = x42;
            vertices2[idx2 + 16] = y4;
            vertices2[idx2 + 17] = color2;
            vertices2[idx2 + 18] = u4;
            vertices2[idx2 + 19] = u42;
            float f3 = color2;
            this.idx = idx2 + 20;
            return;
        }
        throw new IllegalStateException("SpriteBatch.begin must be called before draw.");
    }

    public void draw(TextureRegion region, float width, float height, Affine2 transform) {
        TextureRegion textureRegion = region;
        Affine2 affine2 = transform;
        if (this.drawing) {
            float[] vertices2 = this.vertices;
            Texture texture = textureRegion.texture;
            if (texture != this.lastTexture) {
                switchTexture(texture);
            } else if (this.idx == vertices2.length) {
                flush();
            }
            float x1 = affine2.m02;
            float y1 = affine2.m12;
            float x2 = (affine2.m01 * height) + affine2.m02;
            float y2 = (affine2.m11 * height) + affine2.m12;
            float x3 = (affine2.m00 * width) + (affine2.m01 * height) + affine2.m02;
            float y3 = (affine2.m10 * width) + (affine2.m11 * height) + affine2.m12;
            float x4 = (affine2.m00 * width) + affine2.m02;
            float y4 = (affine2.m10 * width) + affine2.m12;
            float u = textureRegion.u;
            float v = textureRegion.v2;
            float u2 = textureRegion.u2;
            float v2 = textureRegion.v;
            float color2 = this.colorPacked;
            Texture texture2 = texture;
            int idx2 = this.idx;
            vertices2[idx2] = x1;
            vertices2[idx2 + 1] = y1;
            vertices2[idx2 + 2] = color2;
            vertices2[idx2 + 3] = u;
            vertices2[idx2 + 4] = v;
            vertices2[idx2 + 5] = x2;
            vertices2[idx2 + 6] = y2;
            vertices2[idx2 + 7] = color2;
            vertices2[idx2 + 8] = u;
            vertices2[idx2 + 9] = v2;
            vertices2[idx2 + 10] = x3;
            vertices2[idx2 + 11] = y3;
            vertices2[idx2 + 12] = color2;
            vertices2[idx2 + 13] = u2;
            vertices2[idx2 + 14] = v2;
            vertices2[idx2 + 15] = x4;
            vertices2[idx2 + 16] = y4;
            vertices2[idx2 + 17] = color2;
            vertices2[idx2 + 18] = u2;
            vertices2[idx2 + 19] = v;
            float f = color2;
            this.idx = idx2 + 20;
            return;
        }
        throw new IllegalStateException("SpriteBatch.begin must be called before draw.");
    }

    public void flush() {
        int i = this.idx;
        if (i != 0) {
            this.renderCalls++;
            this.totalRenderCalls++;
            int spritesInBatch = i / 20;
            if (spritesInBatch > this.maxSpritesInBatch) {
                this.maxSpritesInBatch = spritesInBatch;
            }
            int count = spritesInBatch * 6;
            this.lastTexture.bind();
            Mesh mesh2 = this.mesh;
            mesh2.setVertices(this.vertices, 0, this.idx);
            mesh2.getIndicesBuffer().position(0);
            mesh2.getIndicesBuffer().limit(count);
            if (this.blendingDisabled) {
                Gdx.gl.glDisable(GL20.GL_BLEND);
            } else {
                Gdx.gl.glEnable(GL20.GL_BLEND);
                if (this.blendSrcFunc != -1) {
                    Gdx.gl.glBlendFuncSeparate(this.blendSrcFunc, this.blendDstFunc, this.blendSrcFuncAlpha, this.blendDstFuncAlpha);
                }
            }
            ShaderProgram shaderProgram = this.customShader;
            if (shaderProgram == null) {
                shaderProgram = this.shader;
            }
            mesh2.render(shaderProgram, 4, 0, count);
            this.idx = 0;
        }
    }

    public void disableBlending() {
        if (!this.blendingDisabled) {
            flush();
            this.blendingDisabled = true;
        }
    }

    public void enableBlending() {
        if (this.blendingDisabled) {
            flush();
            this.blendingDisabled = false;
        }
    }

    public void setBlendFunction(int srcFunc, int dstFunc) {
        setBlendFunctionSeparate(srcFunc, dstFunc, srcFunc, dstFunc);
    }

    public void setBlendFunctionSeparate(int srcFuncColor, int dstFuncColor, int srcFuncAlpha, int dstFuncAlpha) {
        if (this.blendSrcFunc != srcFuncColor || this.blendDstFunc != dstFuncColor || this.blendSrcFuncAlpha != srcFuncAlpha || this.blendDstFuncAlpha != dstFuncAlpha) {
            flush();
            this.blendSrcFunc = srcFuncColor;
            this.blendDstFunc = dstFuncColor;
            this.blendSrcFuncAlpha = srcFuncAlpha;
            this.blendDstFuncAlpha = dstFuncAlpha;
        }
    }

    public int getBlendSrcFunc() {
        return this.blendSrcFunc;
    }

    public int getBlendDstFunc() {
        return this.blendDstFunc;
    }

    public int getBlendSrcFuncAlpha() {
        return this.blendSrcFuncAlpha;
    }

    public int getBlendDstFuncAlpha() {
        return this.blendDstFuncAlpha;
    }

    public void dispose() {
        ShaderProgram shaderProgram;
        this.mesh.dispose();
        if (this.ownsShader && (shaderProgram = this.shader) != null) {
            shaderProgram.dispose();
        }
    }

    public Matrix4 getProjectionMatrix() {
        return this.projectionMatrix;
    }

    public Matrix4 getTransformMatrix() {
        return this.transformMatrix;
    }

    public void setProjectionMatrix(Matrix4 projection) {
        if (this.drawing) {
            flush();
        }
        this.projectionMatrix.set(projection);
        if (this.drawing) {
            setupMatrices();
        }
    }

    public void setTransformMatrix(Matrix4 transform) {
        if (this.drawing) {
            flush();
        }
        this.transformMatrix.set(transform);
        if (this.drawing) {
            setupMatrices();
        }
    }

    private void setupMatrices() {
        this.combinedMatrix.set(this.projectionMatrix).mul(this.transformMatrix);
        ShaderProgram shaderProgram = this.customShader;
        if (shaderProgram != null) {
            shaderProgram.setUniformMatrix("u_projTrans", this.combinedMatrix);
            this.customShader.setUniformi("u_texture", 0);
            return;
        }
        this.shader.setUniformMatrix("u_projTrans", this.combinedMatrix);
        this.shader.setUniformi("u_texture", 0);
    }

    /* access modifiers changed from: protected */
    public void switchTexture(Texture texture) {
        flush();
        this.lastTexture = texture;
        this.invTexWidth = 1.0f / ((float) texture.getWidth());
        this.invTexHeight = 1.0f / ((float) texture.getHeight());
    }

    public void setShader(ShaderProgram shader2) {
        if (this.drawing) {
            flush();
            ShaderProgram shaderProgram = this.customShader;
            if (shaderProgram != null) {
                shaderProgram.end();
            } else {
                this.shader.end();
            }
        }
        this.customShader = shader2;
        if (this.drawing) {
            ShaderProgram shaderProgram2 = this.customShader;
            if (shaderProgram2 != null) {
                shaderProgram2.begin();
            } else {
                this.shader.begin();
            }
            setupMatrices();
        }
    }

    public ShaderProgram getShader() {
        ShaderProgram shaderProgram = this.customShader;
        if (shaderProgram == null) {
            return this.shader;
        }
        return shaderProgram;
    }

    public boolean isBlendingEnabled() {
        return !this.blendingDisabled;
    }

    public boolean isDrawing() {
        return this.drawing;
    }
}
