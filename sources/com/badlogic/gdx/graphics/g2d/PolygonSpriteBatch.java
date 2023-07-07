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

public class PolygonSpriteBatch implements PolygonBatch {
    private int blendDstFunc;
    private int blendDstFuncAlpha;
    private int blendSrcFunc;
    private int blendSrcFuncAlpha;
    private boolean blendingDisabled;
    private final Color color;
    float colorPacked;
    private final Matrix4 combinedMatrix;
    private ShaderProgram customShader;
    private boolean drawing;
    private float invTexHeight;
    private float invTexWidth;
    private Texture lastTexture;
    public int maxTrianglesInBatch;
    private Mesh mesh;
    private boolean ownsShader;
    private final Matrix4 projectionMatrix;
    public int renderCalls;
    private final ShaderProgram shader;
    public int totalRenderCalls;
    private final Matrix4 transformMatrix;
    private int triangleIndex;
    private final short[] triangles;
    private int vertexIndex;
    private final float[] vertices;

    public PolygonSpriteBatch() {
        this(2000, (ShaderProgram) null);
    }

    public PolygonSpriteBatch(int size) {
        this(size, size * 2, (ShaderProgram) null);
    }

    public PolygonSpriteBatch(int size, ShaderProgram defaultShader) {
        this(size, size * 2, defaultShader);
    }

    public PolygonSpriteBatch(int maxVertices, int maxTriangles, ShaderProgram defaultShader) {
        this.invTexWidth = 0.0f;
        this.invTexHeight = 0.0f;
        this.transformMatrix = new Matrix4();
        this.projectionMatrix = new Matrix4();
        this.combinedMatrix = new Matrix4();
        this.blendSrcFunc = GL20.GL_SRC_ALPHA;
        this.blendDstFunc = GL20.GL_ONE_MINUS_SRC_ALPHA;
        this.blendSrcFuncAlpha = GL20.GL_SRC_ALPHA;
        this.blendDstFuncAlpha = GL20.GL_ONE_MINUS_SRC_ALPHA;
        this.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        this.colorPacked = Color.WHITE_FLOAT_BITS;
        this.renderCalls = 0;
        this.totalRenderCalls = 0;
        this.maxTrianglesInBatch = 0;
        if (maxVertices <= 32767) {
            this.mesh = new Mesh(Gdx.gl30 != null ? Mesh.VertexDataType.VertexBufferObjectWithVAO : Mesh.VertexDataType.VertexArray, false, maxVertices, maxTriangles * 3, new VertexAttribute(1, 2, ShaderProgram.POSITION_ATTRIBUTE), new VertexAttribute(4, 4, ShaderProgram.COLOR_ATTRIBUTE), new VertexAttribute(16, 2, "a_texCoord0"));
            this.vertices = new float[(maxVertices * 5)];
            this.triangles = new short[(maxTriangles * 3)];
            if (defaultShader == null) {
                this.shader = SpriteBatch.createDefaultShader();
                this.ownsShader = true;
            } else {
                this.shader = defaultShader;
            }
            this.projectionMatrix.setToOrtho2D(0.0f, 0.0f, (float) Gdx.graphics.getWidth(), (float) Gdx.graphics.getHeight());
            return;
        }
        throw new IllegalArgumentException("Can't have more than 32767 vertices per batch: " + maxVertices);
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
        throw new IllegalStateException("PolygonSpriteBatch.end must be called before begin.");
    }

    public void end() {
        if (this.drawing) {
            if (this.vertexIndex > 0) {
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
            throw new IllegalStateException("PolygonSpriteBatch.begin must be called before end.");
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

    public void setPackedColor(float packedColor) {
        Color.abgr8888ToColor(this.color, packedColor);
        this.colorPacked = packedColor;
    }

    public Color getColor() {
        return this.color;
    }

    public float getPackedColor() {
        return this.colorPacked;
    }

    public void draw(PolygonRegion region, float x, float y) {
        PolygonRegion polygonRegion = region;
        if (this.drawing) {
            short[] triangles2 = this.triangles;
            short[] regionTriangles = polygonRegion.triangles;
            int regionTrianglesLength = regionTriangles.length;
            float[] regionVertices = polygonRegion.vertices;
            int regionVerticesLength = regionVertices.length;
            Texture texture = polygonRegion.region.texture;
            if (texture != this.lastTexture) {
                switchTexture(texture);
            } else if (this.triangleIndex + regionTrianglesLength > triangles2.length || this.vertexIndex + ((regionVerticesLength * 5) / 2) > this.vertices.length) {
                flush();
            }
            int triangleIndex2 = this.triangleIndex;
            int vertexIndex2 = this.vertexIndex;
            int startVertex = vertexIndex2 / 5;
            int i = 0;
            while (i < regionTrianglesLength) {
                triangles2[triangleIndex2] = (short) (regionTriangles[i] + startVertex);
                i++;
                triangleIndex2++;
            }
            this.triangleIndex = triangleIndex2;
            float[] vertices2 = this.vertices;
            float color2 = this.colorPacked;
            float[] textureCoords = polygonRegion.textureCoords;
            int i2 = 0;
            while (i2 < regionVerticesLength) {
                int vertexIndex3 = vertexIndex2 + 1;
                vertices2[vertexIndex2] = regionVertices[i2] + x;
                int vertexIndex4 = vertexIndex3 + 1;
                vertices2[vertexIndex3] = regionVertices[i2 + 1] + y;
                int vertexIndex5 = vertexIndex4 + 1;
                vertices2[vertexIndex4] = color2;
                int vertexIndex6 = vertexIndex5 + 1;
                vertices2[vertexIndex5] = textureCoords[i2];
                vertices2[vertexIndex6] = textureCoords[i2 + 1];
                i2 += 2;
                vertexIndex2 = vertexIndex6 + 1;
            }
            this.vertexIndex = vertexIndex2;
            return;
        }
        throw new IllegalStateException("PolygonSpriteBatch.begin must be called before draw.");
    }

    public void draw(PolygonRegion region, float x, float y, float width, float height) {
        PolygonRegion polygonRegion = region;
        if (this.drawing) {
            short[] triangles2 = this.triangles;
            short[] regionTriangles = polygonRegion.triangles;
            int regionTrianglesLength = regionTriangles.length;
            float[] regionVertices = polygonRegion.vertices;
            int regionVerticesLength = regionVertices.length;
            TextureRegion textureRegion = polygonRegion.region;
            Texture texture = textureRegion.texture;
            if (texture != this.lastTexture) {
                switchTexture(texture);
            } else if (this.triangleIndex + regionTrianglesLength > triangles2.length || this.vertexIndex + ((regionVerticesLength * 5) / 2) > this.vertices.length) {
                flush();
            }
            int triangleIndex2 = this.triangleIndex;
            int vertexIndex2 = this.vertexIndex;
            int startVertex = vertexIndex2 / 5;
            int i = 0;
            int n = regionTriangles.length;
            while (i < n) {
                triangles2[triangleIndex2] = (short) (regionTriangles[i] + startVertex);
                i++;
                triangleIndex2++;
            }
            this.triangleIndex = triangleIndex2;
            float[] vertices2 = this.vertices;
            float color2 = this.colorPacked;
            float[] textureCoords = polygonRegion.textureCoords;
            float sX = width / ((float) textureRegion.regionWidth);
            float sY = height / ((float) textureRegion.regionHeight);
            short[] sArr = triangles2;
            int vertexIndex3 = vertexIndex2;
            int i2 = 0;
            while (i2 < regionVerticesLength) {
                int vertexIndex4 = vertexIndex3 + 1;
                vertices2[vertexIndex3] = (regionVertices[i2] * sX) + x;
                int vertexIndex5 = vertexIndex4 + 1;
                vertices2[vertexIndex4] = (regionVertices[i2 + 1] * sY) + y;
                int vertexIndex6 = vertexIndex5 + 1;
                vertices2[vertexIndex5] = color2;
                int vertexIndex7 = vertexIndex6 + 1;
                vertices2[vertexIndex6] = textureCoords[i2];
                vertices2[vertexIndex7] = textureCoords[i2 + 1];
                i2 += 2;
                vertexIndex3 = vertexIndex7 + 1;
            }
            this.vertexIndex = vertexIndex3;
            return;
        }
        throw new IllegalStateException("PolygonSpriteBatch.begin must be called before draw.");
    }

    public void draw(PolygonRegion region, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation) {
        PolygonRegion polygonRegion = region;
        if (this.drawing) {
            short[] triangles2 = this.triangles;
            short[] regionTriangles = polygonRegion.triangles;
            int regionTrianglesLength = regionTriangles.length;
            float[] regionVertices = polygonRegion.vertices;
            int regionVerticesLength = regionVertices.length;
            TextureRegion textureRegion = polygonRegion.region;
            Texture texture = textureRegion.texture;
            if (texture != this.lastTexture) {
                switchTexture(texture);
            } else if (this.triangleIndex + regionTrianglesLength > triangles2.length || this.vertexIndex + ((regionVerticesLength * 5) / 2) > this.vertices.length) {
                flush();
            }
            int triangleIndex2 = this.triangleIndex;
            int vertexIndex2 = this.vertexIndex;
            int startVertex = vertexIndex2 / 5;
            int i = 0;
            while (i < regionTrianglesLength) {
                triangles2[triangleIndex2] = (short) (regionTriangles[i] + startVertex);
                i++;
                triangleIndex2++;
            }
            this.triangleIndex = triangleIndex2;
            float[] vertices2 = this.vertices;
            float color2 = this.colorPacked;
            float[] textureCoords = polygonRegion.textureCoords;
            float worldOriginX = x + originX;
            float worldOriginY = y + originY;
            float sX = width / ((float) textureRegion.regionWidth);
            short[] sArr = triangles2;
            float sY = height / ((float) textureRegion.regionHeight);
            float cos = MathUtils.cosDeg(rotation);
            float sin = MathUtils.sinDeg(rotation);
            short[] sArr2 = regionTriangles;
            int vertexIndex3 = vertexIndex2;
            int i2 = 0;
            while (i2 < regionVerticesLength) {
                float fx = ((regionVertices[i2] * sX) - originX) * scaleX;
                float fy = ((regionVertices[i2 + 1] * sY) - originY) * scaleY;
                int vertexIndex4 = vertexIndex3 + 1;
                vertices2[vertexIndex3] = ((cos * fx) - (sin * fy)) + worldOriginX;
                int vertexIndex5 = vertexIndex4 + 1;
                vertices2[vertexIndex4] = (sin * fx) + (cos * fy) + worldOriginY;
                int vertexIndex6 = vertexIndex5 + 1;
                vertices2[vertexIndex5] = color2;
                int vertexIndex7 = vertexIndex6 + 1;
                vertices2[vertexIndex6] = textureCoords[i2];
                vertices2[vertexIndex7] = textureCoords[i2 + 1];
                i2 += 2;
                vertexIndex3 = vertexIndex7 + 1;
            }
            this.vertexIndex = vertexIndex3;
            return;
        }
        throw new IllegalStateException("PolygonSpriteBatch.begin must be called before draw.");
    }

    public void draw(Texture texture, float[] polygonVertices, int verticesOffset, int verticesCount, short[] polygonTriangles, int trianglesOffset, int trianglesCount) {
        int i = verticesCount;
        if (this.drawing) {
            short[] triangles2 = this.triangles;
            float[] vertices2 = this.vertices;
            if (texture != this.lastTexture) {
                switchTexture(texture);
            } else if (this.triangleIndex + trianglesCount > triangles2.length || this.vertexIndex + i > vertices2.length) {
                flush();
            }
            int triangleIndex2 = this.triangleIndex;
            int vertexIndex2 = this.vertexIndex;
            int startVertex = vertexIndex2 / 5;
            int i2 = trianglesOffset;
            int n = i2 + trianglesCount;
            while (i2 < n) {
                triangles2[triangleIndex2] = (short) (polygonTriangles[i2] + startVertex);
                i2++;
                triangleIndex2++;
            }
            this.triangleIndex = triangleIndex2;
            float[] fArr = polygonVertices;
            int i3 = verticesOffset;
            System.arraycopy(polygonVertices, verticesOffset, vertices2, vertexIndex2, i);
            this.vertexIndex += i;
            return;
        }
        Texture texture2 = texture;
        float[] fArr2 = polygonVertices;
        int i4 = verticesOffset;
        throw new IllegalStateException("PolygonSpriteBatch.begin must be called before draw.");
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
            short[] triangles2 = this.triangles;
            float[] vertices2 = this.vertices;
            if (texture != this.lastTexture) {
                switchTexture(texture);
            } else if (this.triangleIndex + 6 > triangles2.length || this.vertexIndex + 20 > vertices2.length) {
                flush();
            }
            int triangleIndex2 = this.triangleIndex;
            int startVertex = this.vertexIndex / 5;
            int triangleIndex3 = triangleIndex2 + 1;
            triangles2[triangleIndex2] = (short) startVertex;
            int triangleIndex4 = triangleIndex3 + 1;
            triangles2[triangleIndex3] = (short) (startVertex + 1);
            int triangleIndex5 = triangleIndex4 + 1;
            triangles2[triangleIndex4] = (short) (startVertex + 2);
            int triangleIndex6 = triangleIndex5 + 1;
            triangles2[triangleIndex5] = (short) (startVertex + 2);
            int triangleIndex7 = triangleIndex6 + 1;
            triangles2[triangleIndex6] = (short) (startVertex + 3);
            int triangleIndex8 = triangleIndex7 + 1;
            triangles2[triangleIndex7] = (short) startVertex;
            this.triangleIndex = triangleIndex8;
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
            short[] sArr = triangles2;
            int i3 = triangleIndex8;
            float f4 = this.invTexHeight;
            float v = ((float) (i2 + srcHeight)) * f4;
            float u2 = ((float) (i + srcWidth)) * f3;
            float v2 = ((float) i2) * f4;
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
            int idx = this.vertexIndex;
            int idx2 = idx + 1;
            vertices2[idx] = x12;
            int idx3 = idx2 + 1;
            vertices2[idx2] = y12;
            int idx4 = idx3 + 1;
            vertices2[idx3] = tmp3;
            int idx5 = idx4 + 1;
            vertices2[idx4] = u;
            int idx6 = idx5 + 1;
            vertices2[idx5] = v;
            int idx7 = idx6 + 1;
            vertices2[idx6] = x22;
            int idx8 = idx7 + 1;
            vertices2[idx7] = y22;
            int idx9 = idx8 + 1;
            vertices2[idx8] = tmp3;
            int idx10 = idx9 + 1;
            vertices2[idx9] = u;
            int idx11 = idx10 + 1;
            vertices2[idx10] = v2;
            int idx12 = idx11 + 1;
            vertices2[idx11] = x32;
            int idx13 = idx12 + 1;
            vertices2[idx12] = y32;
            int idx14 = idx13 + 1;
            vertices2[idx13] = tmp3;
            int idx15 = idx14 + 1;
            vertices2[idx14] = u2;
            int idx16 = idx15 + 1;
            vertices2[idx15] = v2;
            int idx17 = idx16 + 1;
            vertices2[idx16] = x42;
            int idx18 = idx17 + 1;
            vertices2[idx17] = y4;
            int idx19 = idx18 + 1;
            vertices2[idx18] = tmp3;
            int idx20 = idx19 + 1;
            vertices2[idx19] = u2;
            vertices2[idx20] = v;
            this.vertexIndex = idx20 + 1;
            return;
        }
        throw new IllegalStateException("PolygonSpriteBatch.begin must be called before draw.");
    }

    public void draw(Texture texture, float x, float y, float width, float height, int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX, boolean flipY) {
        int i = srcX;
        int i2 = srcY;
        if (this.drawing) {
            short[] triangles2 = this.triangles;
            float[] vertices2 = this.vertices;
            if (texture != this.lastTexture) {
                switchTexture(texture);
            } else if (this.triangleIndex + 6 > triangles2.length || this.vertexIndex + 20 > vertices2.length) {
                flush();
            }
            int triangleIndex2 = this.triangleIndex;
            int startVertex = this.vertexIndex / 5;
            int triangleIndex3 = triangleIndex2 + 1;
            triangles2[triangleIndex2] = (short) startVertex;
            int triangleIndex4 = triangleIndex3 + 1;
            triangles2[triangleIndex3] = (short) (startVertex + 1);
            int triangleIndex5 = triangleIndex4 + 1;
            triangles2[triangleIndex4] = (short) (startVertex + 2);
            int triangleIndex6 = triangleIndex5 + 1;
            triangles2[triangleIndex5] = (short) (startVertex + 2);
            int triangleIndex7 = triangleIndex6 + 1;
            triangles2[triangleIndex6] = (short) (startVertex + 3);
            triangles2[triangleIndex7] = (short) startVertex;
            this.triangleIndex = triangleIndex7 + 1;
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
            int idx = this.vertexIndex;
            int idx2 = idx + 1;
            vertices2[idx] = x;
            int idx3 = idx2 + 1;
            vertices2[idx2] = y;
            int idx4 = idx3 + 1;
            vertices2[idx3] = tmp3;
            int idx5 = idx4 + 1;
            vertices2[idx4] = u;
            int idx6 = idx5 + 1;
            vertices2[idx5] = v;
            int idx7 = idx6 + 1;
            vertices2[idx6] = x;
            int idx8 = idx7 + 1;
            vertices2[idx7] = fy2;
            int idx9 = idx8 + 1;
            vertices2[idx8] = tmp3;
            int idx10 = idx9 + 1;
            vertices2[idx9] = u;
            int idx11 = idx10 + 1;
            vertices2[idx10] = v2;
            int idx12 = idx11 + 1;
            vertices2[idx11] = fx2;
            int idx13 = idx12 + 1;
            vertices2[idx12] = fy2;
            int idx14 = idx13 + 1;
            vertices2[idx13] = tmp3;
            int idx15 = idx14 + 1;
            vertices2[idx14] = u2;
            int idx16 = idx15 + 1;
            vertices2[idx15] = v2;
            int idx17 = idx16 + 1;
            vertices2[idx16] = fx2;
            int idx18 = idx17 + 1;
            vertices2[idx17] = y;
            int idx19 = idx18 + 1;
            vertices2[idx18] = tmp3;
            int idx20 = idx19 + 1;
            vertices2[idx19] = u2;
            vertices2[idx20] = v;
            this.vertexIndex = idx20 + 1;
            return;
        }
        Texture texture2 = texture;
        throw new IllegalStateException("PolygonSpriteBatch.begin must be called before draw.");
    }

    public void draw(Texture texture, float x, float y, int srcX, int srcY, int srcWidth, int srcHeight) {
        int i = srcX;
        int i2 = srcY;
        int i3 = srcWidth;
        int i4 = srcHeight;
        if (this.drawing) {
            short[] triangles2 = this.triangles;
            float[] vertices2 = this.vertices;
            if (texture != this.lastTexture) {
                switchTexture(texture);
            } else if (this.triangleIndex + 6 > triangles2.length || this.vertexIndex + 20 > vertices2.length) {
                flush();
            }
            int triangleIndex2 = this.triangleIndex;
            int startVertex = this.vertexIndex / 5;
            int triangleIndex3 = triangleIndex2 + 1;
            triangles2[triangleIndex2] = (short) startVertex;
            int triangleIndex4 = triangleIndex3 + 1;
            triangles2[triangleIndex3] = (short) (startVertex + 1);
            int triangleIndex5 = triangleIndex4 + 1;
            triangles2[triangleIndex4] = (short) (startVertex + 2);
            int triangleIndex6 = triangleIndex5 + 1;
            triangles2[triangleIndex5] = (short) (startVertex + 2);
            int triangleIndex7 = triangleIndex6 + 1;
            triangles2[triangleIndex6] = (short) (startVertex + 3);
            triangles2[triangleIndex7] = (short) startVertex;
            this.triangleIndex = triangleIndex7 + 1;
            float f = this.invTexWidth;
            float u = ((float) i) * f;
            float f2 = this.invTexHeight;
            float v = ((float) (i2 + i4)) * f2;
            float u2 = ((float) (i + i3)) * f;
            float v2 = ((float) i2) * f2;
            float fx2 = x + ((float) i3);
            float fy2 = y + ((float) i4);
            float color2 = this.colorPacked;
            int idx = this.vertexIndex;
            int idx2 = idx + 1;
            vertices2[idx] = x;
            int idx3 = idx2 + 1;
            vertices2[idx2] = y;
            int idx4 = idx3 + 1;
            vertices2[idx3] = color2;
            int idx5 = idx4 + 1;
            vertices2[idx4] = u;
            int idx6 = idx5 + 1;
            vertices2[idx5] = v;
            int idx7 = idx6 + 1;
            vertices2[idx6] = x;
            int idx8 = idx7 + 1;
            vertices2[idx7] = fy2;
            int idx9 = idx8 + 1;
            vertices2[idx8] = color2;
            int idx10 = idx9 + 1;
            vertices2[idx9] = u;
            int idx11 = idx10 + 1;
            vertices2[idx10] = v2;
            int idx12 = idx11 + 1;
            vertices2[idx11] = fx2;
            int idx13 = idx12 + 1;
            vertices2[idx12] = fy2;
            int idx14 = idx13 + 1;
            vertices2[idx13] = color2;
            int idx15 = idx14 + 1;
            vertices2[idx14] = u2;
            int idx16 = idx15 + 1;
            vertices2[idx15] = v2;
            int idx17 = idx16 + 1;
            vertices2[idx16] = fx2;
            int idx18 = idx17 + 1;
            vertices2[idx17] = y;
            int idx19 = idx18 + 1;
            vertices2[idx18] = color2;
            int idx20 = idx19 + 1;
            vertices2[idx19] = u2;
            vertices2[idx20] = v;
            this.vertexIndex = idx20 + 1;
            return;
        }
        Texture texture2 = texture;
        throw new IllegalStateException("PolygonSpriteBatch.begin must be called before draw.");
    }

    public void draw(Texture texture, float x, float y, float width, float height, float u, float v, float u2, float v2) {
        if (this.drawing) {
            short[] triangles2 = this.triangles;
            float[] vertices2 = this.vertices;
            if (texture != this.lastTexture) {
                switchTexture(texture);
            } else if (this.triangleIndex + 6 > triangles2.length || this.vertexIndex + 20 > vertices2.length) {
                flush();
            }
            int triangleIndex2 = this.triangleIndex;
            int startVertex = this.vertexIndex / 5;
            int triangleIndex3 = triangleIndex2 + 1;
            triangles2[triangleIndex2] = (short) startVertex;
            int triangleIndex4 = triangleIndex3 + 1;
            triangles2[triangleIndex3] = (short) (startVertex + 1);
            int triangleIndex5 = triangleIndex4 + 1;
            triangles2[triangleIndex4] = (short) (startVertex + 2);
            int triangleIndex6 = triangleIndex5 + 1;
            triangles2[triangleIndex5] = (short) (startVertex + 2);
            int triangleIndex7 = triangleIndex6 + 1;
            triangles2[triangleIndex6] = (short) (startVertex + 3);
            triangles2[triangleIndex7] = (short) startVertex;
            this.triangleIndex = triangleIndex7 + 1;
            float fx2 = x + width;
            float fy2 = y + height;
            float color2 = this.colorPacked;
            int idx = this.vertexIndex;
            int idx2 = idx + 1;
            vertices2[idx] = x;
            int idx3 = idx2 + 1;
            vertices2[idx2] = y;
            int idx4 = idx3 + 1;
            vertices2[idx3] = color2;
            int idx5 = idx4 + 1;
            vertices2[idx4] = u;
            int idx6 = idx5 + 1;
            vertices2[idx5] = v;
            int idx7 = idx6 + 1;
            vertices2[idx6] = x;
            int idx8 = idx7 + 1;
            vertices2[idx7] = fy2;
            int idx9 = idx8 + 1;
            vertices2[idx8] = color2;
            int idx10 = idx9 + 1;
            vertices2[idx9] = u;
            int idx11 = idx10 + 1;
            vertices2[idx10] = v2;
            int idx12 = idx11 + 1;
            vertices2[idx11] = fx2;
            int idx13 = idx12 + 1;
            vertices2[idx12] = fy2;
            int idx14 = idx13 + 1;
            vertices2[idx13] = color2;
            int idx15 = idx14 + 1;
            vertices2[idx14] = u2;
            int idx16 = idx15 + 1;
            vertices2[idx15] = v2;
            int idx17 = idx16 + 1;
            vertices2[idx16] = fx2;
            int idx18 = idx17 + 1;
            vertices2[idx17] = y;
            int idx19 = idx18 + 1;
            vertices2[idx18] = color2;
            int idx20 = idx19 + 1;
            vertices2[idx19] = u2;
            vertices2[idx20] = v;
            this.vertexIndex = idx20 + 1;
            return;
        }
        Texture texture2 = texture;
        throw new IllegalStateException("PolygonSpriteBatch.begin must be called before draw.");
    }

    public void draw(Texture texture, float x, float y) {
        draw(texture, x, y, (float) texture.getWidth(), (float) texture.getHeight());
    }

    public void draw(Texture texture, float x, float y, float width, float height) {
        if (this.drawing) {
            short[] triangles2 = this.triangles;
            float[] vertices2 = this.vertices;
            if (texture != this.lastTexture) {
                switchTexture(texture);
            } else if (this.triangleIndex + 6 > triangles2.length || this.vertexIndex + 20 > vertices2.length) {
                flush();
            }
            int triangleIndex2 = this.triangleIndex;
            int startVertex = this.vertexIndex / 5;
            int triangleIndex3 = triangleIndex2 + 1;
            triangles2[triangleIndex2] = (short) startVertex;
            int triangleIndex4 = triangleIndex3 + 1;
            triangles2[triangleIndex3] = (short) (startVertex + 1);
            int triangleIndex5 = triangleIndex4 + 1;
            triangles2[triangleIndex4] = (short) (startVertex + 2);
            int triangleIndex6 = triangleIndex5 + 1;
            triangles2[triangleIndex5] = (short) (startVertex + 2);
            int triangleIndex7 = triangleIndex6 + 1;
            triangles2[triangleIndex6] = (short) (startVertex + 3);
            triangles2[triangleIndex7] = (short) startVertex;
            this.triangleIndex = triangleIndex7 + 1;
            float fx2 = x + width;
            float fy2 = y + height;
            float color2 = this.colorPacked;
            int idx = this.vertexIndex;
            int idx2 = idx + 1;
            vertices2[idx] = x;
            int idx3 = idx2 + 1;
            vertices2[idx2] = y;
            int idx4 = idx3 + 1;
            vertices2[idx3] = color2;
            int idx5 = idx4 + 1;
            vertices2[idx4] = 0.0f;
            int idx6 = idx5 + 1;
            vertices2[idx5] = 1.0f;
            int idx7 = idx6 + 1;
            vertices2[idx6] = x;
            int idx8 = idx7 + 1;
            vertices2[idx7] = fy2;
            int idx9 = idx8 + 1;
            vertices2[idx8] = color2;
            int idx10 = idx9 + 1;
            vertices2[idx9] = 0.0f;
            int idx11 = idx10 + 1;
            vertices2[idx10] = 0.0f;
            int idx12 = idx11 + 1;
            vertices2[idx11] = fx2;
            int idx13 = idx12 + 1;
            vertices2[idx12] = fy2;
            int idx14 = idx13 + 1;
            vertices2[idx13] = color2;
            int idx15 = idx14 + 1;
            vertices2[idx14] = 1.0f;
            int idx16 = idx15 + 1;
            vertices2[idx15] = 0.0f;
            int idx17 = idx16 + 1;
            vertices2[idx16] = fx2;
            int idx18 = idx17 + 1;
            vertices2[idx17] = y;
            int idx19 = idx18 + 1;
            vertices2[idx18] = color2;
            int idx20 = idx19 + 1;
            vertices2[idx19] = 1.0f;
            vertices2[idx20] = 1.0f;
            this.vertexIndex = idx20 + 1;
            return;
        }
        Texture texture2 = texture;
        throw new IllegalStateException("PolygonSpriteBatch.begin must be called before draw.");
    }

    public void draw(Texture texture, float[] spriteVertices, int offset, int count) {
        int batch;
        if (this.drawing) {
            short[] triangles2 = this.triangles;
            float[] vertices2 = this.vertices;
            int triangleCount = (count / 20) * 6;
            if (texture != this.lastTexture) {
                switchTexture(texture);
                batch = Math.min(Math.min(count, vertices2.length - (vertices2.length % 20)), (triangles2.length / 6) * 20);
                triangleCount = (batch / 20) * 6;
            } else if (this.triangleIndex + triangleCount > triangles2.length || this.vertexIndex + count > vertices2.length) {
                flush();
                batch = Math.min(Math.min(count, vertices2.length - (vertices2.length % 20)), (triangles2.length / 6) * 20);
                triangleCount = (batch / 20) * 6;
            } else {
                batch = count;
            }
            int vertexIndex2 = this.vertexIndex;
            short vertex = (short) (vertexIndex2 / 5);
            int triangleIndex2 = this.triangleIndex;
            int n = triangleIndex2 + triangleCount;
            while (triangleIndex2 < n) {
                triangles2[triangleIndex2] = vertex;
                triangles2[triangleIndex2 + 1] = (short) (vertex + 1);
                triangles2[triangleIndex2 + 2] = (short) (vertex + 2);
                triangles2[triangleIndex2 + 3] = (short) (vertex + 2);
                triangles2[triangleIndex2 + 4] = (short) (vertex + 3);
                triangles2[triangleIndex2 + 5] = vertex;
                triangleIndex2 += 6;
                vertex = (short) (vertex + 4);
            }
            while (true) {
                System.arraycopy(spriteVertices, offset, vertices2, vertexIndex2, batch);
                this.vertexIndex = vertexIndex2 + batch;
                this.triangleIndex = triangleIndex2;
                count -= batch;
                if (count != 0) {
                    offset += batch;
                    flush();
                    vertexIndex2 = 0;
                    if (batch > count) {
                        batch = Math.min(count, (triangles2.length / 6) * 20);
                        triangleIndex2 = (batch / 20) * 6;
                    }
                } else {
                    return;
                }
            }
        } else {
            throw new IllegalStateException("PolygonSpriteBatch.begin must be called before draw.");
        }
    }

    public void draw(TextureRegion region, float x, float y) {
        draw(region, x, y, (float) region.getRegionWidth(), (float) region.getRegionHeight());
    }

    public void draw(TextureRegion region, float x, float y, float width, float height) {
        TextureRegion textureRegion = region;
        if (this.drawing) {
            short[] triangles2 = this.triangles;
            float[] vertices2 = this.vertices;
            Texture texture = textureRegion.texture;
            if (texture != this.lastTexture) {
                switchTexture(texture);
            } else if (this.triangleIndex + 6 > triangles2.length || this.vertexIndex + 20 > vertices2.length) {
                flush();
            }
            int triangleIndex2 = this.triangleIndex;
            int startVertex = this.vertexIndex / 5;
            int triangleIndex3 = triangleIndex2 + 1;
            triangles2[triangleIndex2] = (short) startVertex;
            int triangleIndex4 = triangleIndex3 + 1;
            triangles2[triangleIndex3] = (short) (startVertex + 1);
            int triangleIndex5 = triangleIndex4 + 1;
            triangles2[triangleIndex4] = (short) (startVertex + 2);
            int triangleIndex6 = triangleIndex5 + 1;
            triangles2[triangleIndex5] = (short) (startVertex + 2);
            int triangleIndex7 = triangleIndex6 + 1;
            triangles2[triangleIndex6] = (short) (startVertex + 3);
            triangles2[triangleIndex7] = (short) startVertex;
            this.triangleIndex = triangleIndex7 + 1;
            float fx2 = x + width;
            float fy2 = y + height;
            float u = textureRegion.u;
            float v = textureRegion.v2;
            float u2 = textureRegion.u2;
            float v2 = textureRegion.v;
            float color2 = this.colorPacked;
            int idx = this.vertexIndex;
            int idx2 = idx + 1;
            vertices2[idx] = x;
            int idx3 = idx2 + 1;
            vertices2[idx2] = y;
            int idx4 = idx3 + 1;
            vertices2[idx3] = color2;
            int idx5 = idx4 + 1;
            vertices2[idx4] = u;
            int idx6 = idx5 + 1;
            vertices2[idx5] = v;
            int idx7 = idx6 + 1;
            vertices2[idx6] = x;
            int idx8 = idx7 + 1;
            vertices2[idx7] = fy2;
            int idx9 = idx8 + 1;
            vertices2[idx8] = color2;
            int idx10 = idx9 + 1;
            vertices2[idx9] = u;
            int idx11 = idx10 + 1;
            vertices2[idx10] = v2;
            int idx12 = idx11 + 1;
            vertices2[idx11] = fx2;
            int idx13 = idx12 + 1;
            vertices2[idx12] = fy2;
            int idx14 = idx13 + 1;
            vertices2[idx13] = color2;
            int idx15 = idx14 + 1;
            vertices2[idx14] = u2;
            int idx16 = idx15 + 1;
            vertices2[idx15] = v2;
            int idx17 = idx16 + 1;
            vertices2[idx16] = fx2;
            int idx18 = idx17 + 1;
            vertices2[idx17] = y;
            int idx19 = idx18 + 1;
            vertices2[idx18] = color2;
            int idx20 = idx19 + 1;
            vertices2[idx19] = u2;
            vertices2[idx20] = v;
            this.vertexIndex = idx20 + 1;
            return;
        }
        throw new IllegalStateException("PolygonSpriteBatch.begin must be called before draw.");
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
            short[] triangles2 = this.triangles;
            float[] vertices2 = this.vertices;
            Texture texture = textureRegion.texture;
            if (texture != this.lastTexture) {
                switchTexture(texture);
            } else if (this.triangleIndex + 6 > triangles2.length || this.vertexIndex + 20 > vertices2.length) {
                flush();
            }
            int triangleIndex2 = this.triangleIndex;
            int startVertex = this.vertexIndex / 5;
            int triangleIndex3 = triangleIndex2 + 1;
            triangles2[triangleIndex2] = (short) startVertex;
            int triangleIndex4 = triangleIndex3 + 1;
            triangles2[triangleIndex3] = (short) (startVertex + 1);
            int triangleIndex5 = triangleIndex4 + 1;
            triangles2[triangleIndex4] = (short) (startVertex + 2);
            int triangleIndex6 = triangleIndex5 + 1;
            triangles2[triangleIndex5] = (short) (startVertex + 2);
            int triangleIndex7 = triangleIndex6 + 1;
            triangles2[triangleIndex6] = (short) (startVertex + 3);
            int triangleIndex8 = triangleIndex7 + 1;
            triangles2[triangleIndex7] = (short) startVertex;
            this.triangleIndex = triangleIndex8;
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
            float u = textureRegion.u;
            float v = textureRegion.v2;
            short[] sArr = triangles2;
            float u2 = textureRegion.u2;
            Texture texture2 = texture;
            float v2 = textureRegion.v;
            float color2 = this.colorPacked;
            int i = triangleIndex8;
            int idx = this.vertexIndex;
            int idx2 = idx + 1;
            vertices2[idx] = x1 + worldOriginX;
            int idx3 = idx2 + 1;
            vertices2[idx2] = y1 + worldOriginY;
            int idx4 = idx3 + 1;
            vertices2[idx3] = color2;
            int idx5 = idx4 + 1;
            vertices2[idx4] = u;
            int idx6 = idx5 + 1;
            vertices2[idx5] = v;
            int idx7 = idx6 + 1;
            vertices2[idx6] = x2 + worldOriginX;
            int idx8 = idx7 + 1;
            vertices2[idx7] = y2 + worldOriginY;
            int idx9 = idx8 + 1;
            vertices2[idx8] = color2;
            int idx10 = idx9 + 1;
            vertices2[idx9] = u;
            int idx11 = idx10 + 1;
            vertices2[idx10] = v2;
            int idx12 = idx11 + 1;
            vertices2[idx11] = x3 + worldOriginX;
            int idx13 = idx12 + 1;
            vertices2[idx12] = y3 + worldOriginY;
            int idx14 = idx13 + 1;
            vertices2[idx13] = color2;
            int idx15 = idx14 + 1;
            vertices2[idx14] = u2;
            int idx16 = idx15 + 1;
            vertices2[idx15] = v2;
            int idx17 = idx16 + 1;
            vertices2[idx16] = x4 + worldOriginX;
            int idx18 = idx17 + 1;
            vertices2[idx17] = cos + worldOriginY;
            int idx19 = idx18 + 1;
            vertices2[idx18] = color2;
            int idx20 = idx19 + 1;
            vertices2[idx19] = u2;
            vertices2[idx20] = v;
            this.vertexIndex = idx20 + 1;
            return;
        }
        throw new IllegalStateException("PolygonSpriteBatch.begin must be called before draw.");
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
            short[] triangles2 = this.triangles;
            float[] vertices2 = this.vertices;
            Texture texture = textureRegion.texture;
            if (texture != this.lastTexture) {
                switchTexture(texture);
            } else if (this.triangleIndex + 6 > triangles2.length || this.vertexIndex + 20 > vertices2.length) {
                flush();
            }
            int triangleIndex2 = this.triangleIndex;
            int startVertex = this.vertexIndex / 5;
            int triangleIndex3 = triangleIndex2 + 1;
            triangles2[triangleIndex2] = (short) startVertex;
            int triangleIndex4 = triangleIndex3 + 1;
            triangles2[triangleIndex3] = (short) (startVertex + 1);
            int triangleIndex5 = triangleIndex4 + 1;
            triangles2[triangleIndex4] = (short) (startVertex + 2);
            int triangleIndex6 = triangleIndex5 + 1;
            triangles2[triangleIndex5] = (short) (startVertex + 2);
            int triangleIndex7 = triangleIndex6 + 1;
            triangles2[triangleIndex6] = (short) (startVertex + 3);
            triangles2[triangleIndex7] = (short) startVertex;
            this.triangleIndex = triangleIndex7 + 1;
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
            int idx = this.vertexIndex;
            int idx2 = idx + 1;
            vertices2[idx] = x12;
            int idx3 = idx2 + 1;
            vertices2[idx2] = y12;
            int idx4 = idx3 + 1;
            vertices2[idx3] = color2;
            int idx5 = idx4 + 1;
            vertices2[idx4] = u1;
            int idx6 = idx5 + 1;
            vertices2[idx5] = v1;
            int idx7 = idx6 + 1;
            vertices2[idx6] = x22;
            int idx8 = idx7 + 1;
            vertices2[idx7] = y22;
            int idx9 = idx8 + 1;
            vertices2[idx8] = color2;
            int idx10 = idx9 + 1;
            vertices2[idx9] = u2;
            int idx11 = idx10 + 1;
            vertices2[idx10] = v2;
            int idx12 = idx11 + 1;
            vertices2[idx11] = x32;
            int idx13 = idx12 + 1;
            vertices2[idx12] = y32;
            int idx14 = idx13 + 1;
            vertices2[idx13] = color2;
            int idx15 = idx14 + 1;
            vertices2[idx14] = u3;
            int idx16 = idx15 + 1;
            vertices2[idx15] = v3;
            int idx17 = idx16 + 1;
            vertices2[idx16] = x42;
            int idx18 = idx17 + 1;
            vertices2[idx17] = y4;
            int idx19 = idx18 + 1;
            vertices2[idx18] = color2;
            int idx20 = idx19 + 1;
            vertices2[idx19] = u4;
            vertices2[idx20] = u42;
            this.vertexIndex = idx20 + 1;
            return;
        }
        throw new IllegalStateException("PolygonSpriteBatch.begin must be called before draw.");
    }

    public void draw(TextureRegion region, float width, float height, Affine2 transform) {
        TextureRegion textureRegion = region;
        Affine2 affine2 = transform;
        if (this.drawing) {
            short[] triangles2 = this.triangles;
            float[] vertices2 = this.vertices;
            Texture texture = textureRegion.texture;
            if (texture != this.lastTexture) {
                switchTexture(texture);
            } else if (this.triangleIndex + 6 > triangles2.length || this.vertexIndex + 20 > vertices2.length) {
                flush();
            }
            int triangleIndex2 = this.triangleIndex;
            int startVertex = this.vertexIndex / 5;
            int triangleIndex3 = triangleIndex2 + 1;
            triangles2[triangleIndex2] = (short) startVertex;
            int triangleIndex4 = triangleIndex3 + 1;
            triangles2[triangleIndex3] = (short) (startVertex + 1);
            int triangleIndex5 = triangleIndex4 + 1;
            triangles2[triangleIndex4] = (short) (startVertex + 2);
            int triangleIndex6 = triangleIndex5 + 1;
            triangles2[triangleIndex5] = (short) (startVertex + 2);
            int triangleIndex7 = triangleIndex6 + 1;
            triangles2[triangleIndex6] = (short) (startVertex + 3);
            int triangleIndex8 = triangleIndex7 + 1;
            triangles2[triangleIndex7] = (short) startVertex;
            this.triangleIndex = triangleIndex8;
            float x1 = affine2.m02;
            float y1 = affine2.m12;
            float x2 = (affine2.m01 * height) + affine2.m02;
            float y2 = (affine2.m11 * height) + affine2.m12;
            float x3 = (affine2.m00 * width) + (affine2.m01 * height) + affine2.m02;
            float y3 = (affine2.m10 * width) + (affine2.m11 * height) + affine2.m12;
            float x4 = (affine2.m00 * width) + affine2.m02;
            short[] sArr = triangles2;
            float y4 = (affine2.m10 * width) + affine2.m12;
            float u = textureRegion.u;
            float v = textureRegion.v2;
            Texture texture2 = texture;
            float u2 = textureRegion.u2;
            int i = triangleIndex8;
            float v2 = textureRegion.v;
            float color2 = this.colorPacked;
            int i2 = startVertex;
            int startVertex2 = this.vertexIndex;
            int idx = startVertex2 + 1;
            vertices2[startVertex2] = x1;
            int idx2 = idx + 1;
            vertices2[idx] = y1;
            int idx3 = idx2 + 1;
            vertices2[idx2] = color2;
            int idx4 = idx3 + 1;
            vertices2[idx3] = u;
            int idx5 = idx4 + 1;
            vertices2[idx4] = v;
            int idx6 = idx5 + 1;
            vertices2[idx5] = x2;
            int idx7 = idx6 + 1;
            vertices2[idx6] = y2;
            int idx8 = idx7 + 1;
            vertices2[idx7] = color2;
            int idx9 = idx8 + 1;
            vertices2[idx8] = u;
            int idx10 = idx9 + 1;
            vertices2[idx9] = v2;
            int idx11 = idx10 + 1;
            vertices2[idx10] = x3;
            int idx12 = idx11 + 1;
            vertices2[idx11] = y3;
            int idx13 = idx12 + 1;
            vertices2[idx12] = color2;
            int idx14 = idx13 + 1;
            vertices2[idx13] = u2;
            int idx15 = idx14 + 1;
            vertices2[idx14] = v2;
            int idx16 = idx15 + 1;
            vertices2[idx15] = x4;
            int idx17 = idx16 + 1;
            vertices2[idx16] = y4;
            int idx18 = idx17 + 1;
            vertices2[idx17] = color2;
            int idx19 = idx18 + 1;
            vertices2[idx18] = u2;
            vertices2[idx19] = v;
            this.vertexIndex = idx19 + 1;
            return;
        }
        throw new IllegalStateException("PolygonSpriteBatch.begin must be called before draw.");
    }

    public void flush() {
        if (this.vertexIndex != 0) {
            this.renderCalls++;
            this.totalRenderCalls++;
            int trianglesInBatch = this.triangleIndex;
            if (trianglesInBatch > this.maxTrianglesInBatch) {
                this.maxTrianglesInBatch = trianglesInBatch;
            }
            this.lastTexture.bind();
            Mesh mesh2 = this.mesh;
            mesh2.setVertices(this.vertices, 0, this.vertexIndex);
            mesh2.setIndices(this.triangles, 0, trianglesInBatch);
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
            mesh2.render(shaderProgram, 4, 0, trianglesInBatch);
            this.vertexIndex = 0;
            this.triangleIndex = 0;
        }
    }

    public void disableBlending() {
        flush();
        this.blendingDisabled = true;
    }

    public void enableBlending() {
        flush();
        this.blendingDisabled = false;
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

    private void switchTexture(Texture texture) {
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
