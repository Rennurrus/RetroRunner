package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

public class ShapeRenderer implements Disposable {
    private boolean autoShapeType;
    private final Color color;
    private final Matrix4 combinedMatrix;
    private float defaultRectLineWidth;
    private boolean matrixDirty;
    private final Matrix4 projectionMatrix;
    private final ImmediateModeRenderer renderer;
    private ShapeType shapeType;
    private final Vector2 tmp;
    private final Matrix4 transformMatrix;

    public enum ShapeType {
        Point(0),
        Line(1),
        Filled(4);
        
        private final int glType;

        private ShapeType(int glType2) {
            this.glType = glType2;
        }

        public int getGlType() {
            return this.glType;
        }
    }

    public ShapeRenderer() {
        this(5000);
    }

    public ShapeRenderer(int maxVertices) {
        this(maxVertices, (ShaderProgram) null);
    }

    public ShapeRenderer(int maxVertices, ShaderProgram defaultShader) {
        this.matrixDirty = false;
        this.projectionMatrix = new Matrix4();
        this.transformMatrix = new Matrix4();
        this.combinedMatrix = new Matrix4();
        this.tmp = new Vector2();
        this.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        this.defaultRectLineWidth = 0.75f;
        if (defaultShader == null) {
            this.renderer = new ImmediateModeRenderer20(maxVertices, false, true, 0);
        } else {
            this.renderer = new ImmediateModeRenderer20(maxVertices, false, true, 0, defaultShader);
        }
        this.projectionMatrix.setToOrtho2D(0.0f, 0.0f, (float) Gdx.graphics.getWidth(), (float) Gdx.graphics.getHeight());
        this.matrixDirty = true;
    }

    public void setColor(Color color2) {
        this.color.set(color2);
    }

    public void setColor(float r, float g, float b, float a) {
        this.color.set(r, g, b, a);
    }

    public Color getColor() {
        return this.color;
    }

    public void updateMatrices() {
        this.matrixDirty = true;
    }

    public void setProjectionMatrix(Matrix4 matrix) {
        this.projectionMatrix.set(matrix);
        this.matrixDirty = true;
    }

    public Matrix4 getProjectionMatrix() {
        return this.projectionMatrix;
    }

    public void setTransformMatrix(Matrix4 matrix) {
        this.transformMatrix.set(matrix);
        this.matrixDirty = true;
    }

    public Matrix4 getTransformMatrix() {
        return this.transformMatrix;
    }

    public void identity() {
        this.transformMatrix.idt();
        this.matrixDirty = true;
    }

    public void translate(float x, float y, float z) {
        this.transformMatrix.translate(x, y, z);
        this.matrixDirty = true;
    }

    public void rotate(float axisX, float axisY, float axisZ, float degrees) {
        this.transformMatrix.rotate(axisX, axisY, axisZ, degrees);
        this.matrixDirty = true;
    }

    public void scale(float scaleX, float scaleY, float scaleZ) {
        this.transformMatrix.scale(scaleX, scaleY, scaleZ);
        this.matrixDirty = true;
    }

    public void setAutoShapeType(boolean autoShapeType2) {
        this.autoShapeType = autoShapeType2;
    }

    public void begin() {
        if (this.autoShapeType) {
            begin(ShapeType.Line);
            return;
        }
        throw new IllegalStateException("autoShapeType must be true to use this method.");
    }

    public void begin(ShapeType type) {
        if (this.shapeType == null) {
            this.shapeType = type;
            if (this.matrixDirty) {
                this.combinedMatrix.set(this.projectionMatrix);
                Matrix4.mul(this.combinedMatrix.val, this.transformMatrix.val);
                this.matrixDirty = false;
            }
            this.renderer.begin(this.combinedMatrix, this.shapeType.getGlType());
            return;
        }
        throw new IllegalStateException("Call end() before beginning a new shape batch.");
    }

    public void set(ShapeType type) {
        ShapeType shapeType2 = this.shapeType;
        if (shapeType2 != type) {
            if (shapeType2 == null) {
                throw new IllegalStateException("begin must be called first.");
            } else if (this.autoShapeType) {
                end();
                begin(type);
            } else {
                throw new IllegalStateException("autoShapeType must be enabled.");
            }
        }
    }

    public void point(float x, float y, float z) {
        if (this.shapeType == ShapeType.Line) {
            float size = this.defaultRectLineWidth * 0.5f;
            line(x - size, y - size, z, x + size, y + size, z);
        } else if (this.shapeType == ShapeType.Filled) {
            float f = this.defaultRectLineWidth;
            float size2 = 0.5f * f;
            box(x - size2, y - size2, z - size2, f, f, f);
        } else {
            check(ShapeType.Point, (ShapeType) null, 1);
            this.renderer.color(this.color);
            this.renderer.vertex(x, y, z);
        }
    }

    public final void line(float x, float y, float z, float x2, float y2, float z2) {
        Color color2 = this.color;
        line(x, y, z, x2, y2, z2, color2, color2);
    }

    public final void line(Vector3 v0, Vector3 v1) {
        float f = v0.x;
        float f2 = v0.y;
        float f3 = v0.z;
        float f4 = v1.x;
        float f5 = v1.y;
        float f6 = v1.z;
        Color color2 = this.color;
        line(f, f2, f3, f4, f5, f6, color2, color2);
    }

    public final void line(float x, float y, float x2, float y2) {
        Color color2 = this.color;
        line(x, y, 0.0f, x2, y2, 0.0f, color2, color2);
    }

    public final void line(Vector2 v0, Vector2 v1) {
        float f = v0.x;
        float f2 = v0.y;
        float f3 = v1.x;
        float f4 = v1.y;
        Color color2 = this.color;
        line(f, f2, 0.0f, f3, f4, 0.0f, color2, color2);
    }

    public final void line(float x, float y, float x2, float y2, Color c1, Color c2) {
        line(x, y, 0.0f, x2, y2, 0.0f, c1, c2);
    }

    public void line(float x, float y, float z, float x2, float y2, float z2, Color c1, Color c2) {
        Color color2 = c1;
        Color color3 = c2;
        if (this.shapeType == ShapeType.Filled) {
            rectLine(x, y, x2, y2, this.defaultRectLineWidth, c1, c2);
            return;
        }
        check(ShapeType.Line, (ShapeType) null, 2);
        this.renderer.color(color2.r, color2.g, color2.b, color2.a);
        float f = x;
        float f2 = y;
        float f3 = z;
        this.renderer.vertex(x, y, z);
        this.renderer.color(color3.r, color3.g, color3.b, color3.a);
        float f4 = x2;
        this.renderer.vertex(x2, y2, z2);
    }

    public void curve(float x1, float y1, float cx1, float cy1, float cx2, float cy2, float x2, float y2, int segments) {
        float f = x2;
        float f2 = y2;
        int segments2 = segments;
        check(ShapeType.Line, (ShapeType) null, (segments2 * 2) + 2);
        float colorBits = this.color.toFloatBits();
        float subdiv_step = 1.0f / ((float) segments2);
        float subdiv_step2 = subdiv_step * subdiv_step;
        float subdiv_step3 = subdiv_step * subdiv_step * subdiv_step;
        float pre1 = subdiv_step * 3.0f;
        float pre2 = subdiv_step2 * 3.0f;
        float pre4 = subdiv_step2 * 6.0f;
        float pre5 = 6.0f * subdiv_step3;
        float tmp1x = (x1 - (cx1 * 2.0f)) + cx2;
        float tmp1y = (y1 - (2.0f * cy1)) + cy2;
        float tmp2x = (((cx1 - cx2) * 3.0f) - x1) + f;
        float tmp2y = (((cy1 - cy2) * 3.0f) - y1) + f2;
        float fx = x1;
        float dfx = ((cx1 - x1) * pre1) + (tmp1x * pre2) + (tmp2x * subdiv_step3);
        float dfy = ((cy1 - y1) * pre1) + (tmp1y * pre2) + (tmp2y * subdiv_step3);
        float ddfx = (tmp1x * pre4) + (tmp2x * pre5);
        float ddfy = (tmp1y * pre4) + (tmp2y * pre5);
        float dddfx = tmp2x * pre5;
        float dddfy = tmp2y * pre5;
        float fy = subdiv_step2;
        float fy2 = y1;
        while (true) {
            int segments3 = segments2 - 1;
            float subdiv_step4 = subdiv_step;
            if (segments2 > 0) {
                this.renderer.color(colorBits);
                this.renderer.vertex(fx, fy2, 0.0f);
                fx += dfx;
                fy2 += dfy;
                dfx += ddfx;
                dfy += ddfy;
                ddfx += dddfx;
                ddfy += dddfy;
                this.renderer.color(colorBits);
                this.renderer.vertex(fx, fy2, 0.0f);
                segments2 = segments3;
                subdiv_step = subdiv_step4;
            } else {
                this.renderer.color(colorBits);
                this.renderer.vertex(fx, fy2, 0.0f);
                this.renderer.color(colorBits);
                this.renderer.vertex(f, f2, 0.0f);
                return;
            }
        }
    }

    public void triangle(float x1, float y1, float x2, float y2, float x3, float y3) {
        check(ShapeType.Line, ShapeType.Filled, 6);
        float colorBits = this.color.toFloatBits();
        if (this.shapeType == ShapeType.Line) {
            this.renderer.color(colorBits);
            this.renderer.vertex(x1, y1, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x2, y2, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x2, y2, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x3, y3, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x3, y3, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x1, y1, 0.0f);
            return;
        }
        this.renderer.color(colorBits);
        this.renderer.vertex(x1, y1, 0.0f);
        this.renderer.color(colorBits);
        this.renderer.vertex(x2, y2, 0.0f);
        this.renderer.color(colorBits);
        this.renderer.vertex(x3, y3, 0.0f);
    }

    public void triangle(float x1, float y1, float x2, float y2, float x3, float y3, Color col1, Color col2, Color col3) {
        check(ShapeType.Line, ShapeType.Filled, 6);
        if (this.shapeType == ShapeType.Line) {
            this.renderer.color(col1.r, col1.g, col1.b, col1.a);
            this.renderer.vertex(x1, y1, 0.0f);
            this.renderer.color(col2.r, col2.g, col2.b, col2.a);
            this.renderer.vertex(x2, y2, 0.0f);
            this.renderer.color(col2.r, col2.g, col2.b, col2.a);
            this.renderer.vertex(x2, y2, 0.0f);
            this.renderer.color(col3.r, col3.g, col3.b, col3.a);
            this.renderer.vertex(x3, y3, 0.0f);
            this.renderer.color(col3.r, col3.g, col3.b, col3.a);
            this.renderer.vertex(x3, y3, 0.0f);
            this.renderer.color(col1.r, col1.g, col1.b, col1.a);
            this.renderer.vertex(x1, y1, 0.0f);
            return;
        }
        this.renderer.color(col1.r, col1.g, col1.b, col1.a);
        this.renderer.vertex(x1, y1, 0.0f);
        this.renderer.color(col2.r, col2.g, col2.b, col2.a);
        this.renderer.vertex(x2, y2, 0.0f);
        this.renderer.color(col3.r, col3.g, col3.b, col3.a);
        this.renderer.vertex(x3, y3, 0.0f);
    }

    public void rect(float x, float y, float width, float height) {
        check(ShapeType.Line, ShapeType.Filled, 8);
        float colorBits = this.color.toFloatBits();
        if (this.shapeType == ShapeType.Line) {
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y + height, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y + height, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y + height, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y + height, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y, 0.0f);
            return;
        }
        this.renderer.color(colorBits);
        this.renderer.vertex(x, y, 0.0f);
        this.renderer.color(colorBits);
        this.renderer.vertex(x + width, y, 0.0f);
        this.renderer.color(colorBits);
        this.renderer.vertex(x + width, y + height, 0.0f);
        this.renderer.color(colorBits);
        this.renderer.vertex(x + width, y + height, 0.0f);
        this.renderer.color(colorBits);
        this.renderer.vertex(x, y + height, 0.0f);
        this.renderer.color(colorBits);
        this.renderer.vertex(x, y, 0.0f);
    }

    public void rect(float x, float y, float width, float height, Color col1, Color col2, Color col3, Color col4) {
        check(ShapeType.Line, ShapeType.Filled, 8);
        if (this.shapeType == ShapeType.Line) {
            this.renderer.color(col1.r, col1.g, col1.b, col1.a);
            this.renderer.vertex(x, y, 0.0f);
            this.renderer.color(col2.r, col2.g, col2.b, col2.a);
            this.renderer.vertex(x + width, y, 0.0f);
            this.renderer.color(col2.r, col2.g, col2.b, col2.a);
            this.renderer.vertex(x + width, y, 0.0f);
            this.renderer.color(col3.r, col3.g, col3.b, col3.a);
            this.renderer.vertex(x + width, y + height, 0.0f);
            this.renderer.color(col3.r, col3.g, col3.b, col3.a);
            this.renderer.vertex(x + width, y + height, 0.0f);
            this.renderer.color(col4.r, col4.g, col4.b, col4.a);
            this.renderer.vertex(x, y + height, 0.0f);
            this.renderer.color(col4.r, col4.g, col4.b, col4.a);
            this.renderer.vertex(x, y + height, 0.0f);
            this.renderer.color(col1.r, col1.g, col1.b, col1.a);
            this.renderer.vertex(x, y, 0.0f);
            return;
        }
        this.renderer.color(col1.r, col1.g, col1.b, col1.a);
        this.renderer.vertex(x, y, 0.0f);
        this.renderer.color(col2.r, col2.g, col2.b, col2.a);
        this.renderer.vertex(x + width, y, 0.0f);
        this.renderer.color(col3.r, col3.g, col3.b, col3.a);
        this.renderer.vertex(x + width, y + height, 0.0f);
        this.renderer.color(col3.r, col3.g, col3.b, col3.a);
        this.renderer.vertex(x + width, y + height, 0.0f);
        this.renderer.color(col4.r, col4.g, col4.b, col4.a);
        this.renderer.vertex(x, y + height, 0.0f);
        this.renderer.color(col1.r, col1.g, col1.b, col1.a);
        this.renderer.vertex(x, y, 0.0f);
    }

    public void rect(float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float degrees) {
        Color color2 = this.color;
        rect(x, y, originX, originY, width, height, scaleX, scaleY, degrees, color2, color2, color2, color2);
    }

    public void rect(float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float degrees, Color col1, Color col2, Color col3, Color col4) {
        float f = originX;
        float f2 = originY;
        Color color2 = col1;
        Color color3 = col2;
        Color color4 = col3;
        Color color5 = col4;
        check(ShapeType.Line, ShapeType.Filled, 8);
        float cos = MathUtils.cosDeg(degrees);
        float sin = MathUtils.sinDeg(degrees);
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
        float worldOriginX = x + f;
        float worldOriginY = y + f2;
        float x1 = ((cos * fx) - (sin * fy)) + worldOriginX;
        float y1 = (sin * fx) + (cos * fy) + worldOriginY;
        float x2 = ((cos * fx2) - (sin * fy)) + worldOriginX;
        float f3 = fx;
        float y2 = (sin * fx2) + (cos * fy) + worldOriginY;
        float f4 = fy;
        float x3 = ((cos * fx2) - (sin * fy2)) + worldOriginX;
        float f5 = cos;
        float cos2 = (sin * fx2) + (cos * fy2) + worldOriginY;
        float f6 = sin;
        float x4 = x1 + (x3 - x2);
        float f7 = fx2;
        float y4 = cos2 - (y2 - y1);
        float f8 = fy2;
        float f9 = worldOriginX;
        float f10 = worldOriginY;
        if (this.shapeType == ShapeType.Line) {
            float y42 = y4;
            this.renderer.color(color2.r, color2.g, color2.b, color2.a);
            this.renderer.vertex(x1, y1, 0.0f);
            this.renderer.color(color3.r, color3.g, color3.b, color3.a);
            this.renderer.vertex(x2, y2, 0.0f);
            this.renderer.color(color3.r, color3.g, color3.b, color3.a);
            this.renderer.vertex(x2, y2, 0.0f);
            this.renderer.color(color4.r, color4.g, color4.b, color4.a);
            this.renderer.vertex(x3, cos2, 0.0f);
            this.renderer.color(color4.r, color4.g, color4.b, color4.a);
            this.renderer.vertex(x3, cos2, 0.0f);
            this.renderer.color(color5.r, color5.g, color5.b, color5.a);
            float x42 = x4;
            float y43 = y42;
            this.renderer.vertex(x42, y43, 0.0f);
            float x32 = x3;
            this.renderer.color(color5.r, color5.g, color5.b, color5.a);
            this.renderer.vertex(x42, y43, 0.0f);
            this.renderer.color(color2.r, color2.g, color2.b, color2.a);
            this.renderer.vertex(x1, y1, 0.0f);
            float f11 = cos2;
            float f12 = x32;
            float y3 = x2;
            return;
        }
        float x33 = x3;
        float y44 = y4;
        float y45 = x4;
        this.renderer.color(color2.r, color2.g, color2.b, color2.a);
        this.renderer.vertex(x1, y1, 0.0f);
        this.renderer.color(color3.r, color3.g, color3.b, color3.a);
        this.renderer.vertex(x2, y2, 0.0f);
        this.renderer.color(color4.r, color4.g, color4.b, color4.a);
        float y32 = cos2;
        float x34 = x33;
        this.renderer.vertex(x34, y32, 0.0f);
        float f13 = x2;
        this.renderer.color(color4.r, color4.g, color4.b, color4.a);
        this.renderer.vertex(x34, y32, 0.0f);
        this.renderer.color(color5.r, color5.g, color5.b, color5.a);
        this.renderer.vertex(y45, y44, 0.0f);
        this.renderer.color(color2.r, color2.g, color2.b, color2.a);
        this.renderer.vertex(x1, y1, 0.0f);
    }

    public void rectLine(float x1, float y1, float x2, float y2, float width) {
        check(ShapeType.Line, ShapeType.Filled, 8);
        float colorBits = this.color.toFloatBits();
        Vector2 t = this.tmp.set(y2 - y1, x1 - x2).nor();
        float width2 = width * 0.5f;
        float tx = t.x * width2;
        float ty = t.y * width2;
        if (this.shapeType == ShapeType.Line) {
            this.renderer.color(colorBits);
            this.renderer.vertex(x1 + tx, y1 + ty, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x1 - tx, y1 - ty, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x2 + tx, y2 + ty, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x2 - tx, y2 - ty, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x2 + tx, y2 + ty, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x1 + tx, y1 + ty, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x2 - tx, y2 - ty, 0.0f);
            this.renderer.color(colorBits);
            this.renderer.vertex(x1 - tx, y1 - ty, 0.0f);
            return;
        }
        this.renderer.color(colorBits);
        this.renderer.vertex(x1 + tx, y1 + ty, 0.0f);
        this.renderer.color(colorBits);
        this.renderer.vertex(x1 - tx, y1 - ty, 0.0f);
        this.renderer.color(colorBits);
        this.renderer.vertex(x2 + tx, y2 + ty, 0.0f);
        this.renderer.color(colorBits);
        this.renderer.vertex(x2 - tx, y2 - ty, 0.0f);
        this.renderer.color(colorBits);
        this.renderer.vertex(x2 + tx, y2 + ty, 0.0f);
        this.renderer.color(colorBits);
        this.renderer.vertex(x1 - tx, y1 - ty, 0.0f);
    }

    public void rectLine(float x1, float y1, float x2, float y2, float width, Color c1, Color c2) {
        check(ShapeType.Line, ShapeType.Filled, 8);
        float col1Bits = c1.toFloatBits();
        float col2Bits = c2.toFloatBits();
        Vector2 t = this.tmp.set(y2 - y1, x1 - x2).nor();
        float width2 = 0.5f * width;
        float tx = t.x * width2;
        float ty = t.y * width2;
        if (this.shapeType == ShapeType.Line) {
            this.renderer.color(col1Bits);
            this.renderer.vertex(x1 + tx, y1 + ty, 0.0f);
            this.renderer.color(col1Bits);
            this.renderer.vertex(x1 - tx, y1 - ty, 0.0f);
            this.renderer.color(col2Bits);
            this.renderer.vertex(x2 + tx, y2 + ty, 0.0f);
            this.renderer.color(col2Bits);
            this.renderer.vertex(x2 - tx, y2 - ty, 0.0f);
            this.renderer.color(col2Bits);
            this.renderer.vertex(x2 + tx, y2 + ty, 0.0f);
            this.renderer.color(col1Bits);
            this.renderer.vertex(x1 + tx, y1 + ty, 0.0f);
            this.renderer.color(col2Bits);
            this.renderer.vertex(x2 - tx, y2 - ty, 0.0f);
            this.renderer.color(col1Bits);
            this.renderer.vertex(x1 - tx, y1 - ty, 0.0f);
            return;
        }
        this.renderer.color(col1Bits);
        this.renderer.vertex(x1 + tx, y1 + ty, 0.0f);
        this.renderer.color(col1Bits);
        this.renderer.vertex(x1 - tx, y1 - ty, 0.0f);
        this.renderer.color(col2Bits);
        this.renderer.vertex(x2 + tx, y2 + ty, 0.0f);
        this.renderer.color(col2Bits);
        this.renderer.vertex(x2 - tx, y2 - ty, 0.0f);
        this.renderer.color(col2Bits);
        this.renderer.vertex(x2 + tx, y2 + ty, 0.0f);
        this.renderer.color(col1Bits);
        this.renderer.vertex(x1 - tx, y1 - ty, 0.0f);
    }

    public void rectLine(Vector2 p1, Vector2 p2, float width) {
        rectLine(p1.x, p1.y, p2.x, p2.y, width);
    }

    public void box(float x, float y, float z, float width, float height, float depth) {
        float depth2 = -depth;
        float colorBits = this.color.toFloatBits();
        if (this.shapeType == ShapeType.Line) {
            check(ShapeType.Line, ShapeType.Filled, 24);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y, z);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y, z);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y, z);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y, z + depth2);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y, z + depth2);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y, z + depth2);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y, z + depth2);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y, z);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y, z);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y + height, z);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y + height, z);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y + height, z);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y + height, z);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y + height, z + depth2);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y + height, z + depth2);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y + height, z + depth2);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y + height, z + depth2);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y + height, z);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y, z);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y + height, z);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y, z + depth2);
            this.renderer.color(colorBits);
            this.renderer.vertex(x + width, y + height, z + depth2);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y, z + depth2);
            this.renderer.color(colorBits);
            this.renderer.vertex(x, y + height, z + depth2);
            return;
        }
        check(ShapeType.Line, ShapeType.Filled, 36);
        this.renderer.color(colorBits);
        this.renderer.vertex(x, y, z);
        this.renderer.color(colorBits);
        this.renderer.vertex(x + width, y, z);
        this.renderer.color(colorBits);
        this.renderer.vertex(x + width, y + height, z);
        this.renderer.color(colorBits);
        this.renderer.vertex(x, y, z);
        this.renderer.color(colorBits);
        this.renderer.vertex(x + width, y + height, z);
        this.renderer.color(colorBits);
        this.renderer.vertex(x, y + height, z);
        this.renderer.color(colorBits);
        this.renderer.vertex(x + width, y, z + depth2);
        this.renderer.color(colorBits);
        this.renderer.vertex(x, y, z + depth2);
        this.renderer.color(colorBits);
        this.renderer.vertex(x + width, y + height, z + depth2);
        this.renderer.color(colorBits);
        this.renderer.vertex(x + width, y + height, z + depth2);
        this.renderer.color(colorBits);
        this.renderer.vertex(x, y, z + depth2);
        this.renderer.color(colorBits);
        this.renderer.vertex(x, y + height, z + depth2);
        this.renderer.color(colorBits);
        this.renderer.vertex(x, y, z + depth2);
        this.renderer.color(colorBits);
        this.renderer.vertex(x, y, z);
        this.renderer.color(colorBits);
        this.renderer.vertex(x, y + height, z);
        this.renderer.color(colorBits);
        this.renderer.vertex(x, y, z + depth2);
        this.renderer.color(colorBits);
        this.renderer.vertex(x, y + height, z);
        this.renderer.color(colorBits);
        this.renderer.vertex(x, y + height, z + depth2);
        this.renderer.color(colorBits);
        this.renderer.vertex(x + width, y, z);
        this.renderer.color(colorBits);
        this.renderer.vertex(x + width, y, z + depth2);
        this.renderer.color(colorBits);
        this.renderer.vertex(x + width, y + height, z + depth2);
        this.renderer.color(colorBits);
        this.renderer.vertex(x + width, y, z);
        this.renderer.color(colorBits);
        this.renderer.vertex(x + width, y + height, z + depth2);
        this.renderer.color(colorBits);
        this.renderer.vertex(x + width, y + height, z);
        this.renderer.color(colorBits);
        this.renderer.vertex(x, y + height, z);
        this.renderer.color(colorBits);
        this.renderer.vertex(x + width, y + height, z);
        this.renderer.color(colorBits);
        this.renderer.vertex(x + width, y + height, z + depth2);
        this.renderer.color(colorBits);
        this.renderer.vertex(x, y + height, z);
        this.renderer.color(colorBits);
        this.renderer.vertex(x + width, y + height, z + depth2);
        this.renderer.color(colorBits);
        this.renderer.vertex(x, y + height, z + depth2);
        this.renderer.color(colorBits);
        this.renderer.vertex(x, y, z + depth2);
        this.renderer.color(colorBits);
        this.renderer.vertex(x + width, y, z + depth2);
        this.renderer.color(colorBits);
        this.renderer.vertex(x + width, y, z);
        this.renderer.color(colorBits);
        this.renderer.vertex(x, y, z + depth2);
        this.renderer.color(colorBits);
        this.renderer.vertex(x + width, y, z);
        this.renderer.color(colorBits);
        this.renderer.vertex(x, y, z);
    }

    public void x(float x, float y, float size) {
        line(x - size, y - size, x + size, y + size);
        line(x - size, y + size, x + size, y - size);
    }

    public void x(Vector2 p, float size) {
        x(p.x, p.y, size);
    }

    public void arc(float x, float y, float radius, float start, float degrees) {
        arc(x, y, radius, start, degrees, Math.max(1, (int) (((float) Math.cbrt((double) radius)) * 6.0f * (degrees / 360.0f))));
    }

    public void arc(float x, float y, float radius, float start, float degrees, int segments) {
        float f = x;
        float f2 = y;
        int i = segments;
        if (i > 0) {
            float colorBits = this.color.toFloatBits();
            float theta = ((degrees / 360.0f) * 6.2831855f) / ((float) i);
            float cos = MathUtils.cos(theta);
            float sin = MathUtils.sin(theta);
            float cx = MathUtils.cos(start * 0.017453292f) * radius;
            float cy = MathUtils.sin(0.017453292f * start) * radius;
            if (this.shapeType == ShapeType.Line) {
                check(ShapeType.Line, ShapeType.Filled, (i * 2) + 2);
                this.renderer.color(colorBits);
                this.renderer.vertex(f, f2, 0.0f);
                this.renderer.color(colorBits);
                this.renderer.vertex(f + cx, f2 + cy, 0.0f);
                for (int i2 = 0; i2 < i; i2++) {
                    this.renderer.color(colorBits);
                    this.renderer.vertex(f + cx, f2 + cy, 0.0f);
                    float temp = cx;
                    cx = (cos * cx) - (sin * cy);
                    cy = (sin * temp) + (cos * cy);
                    this.renderer.color(colorBits);
                    this.renderer.vertex(f + cx, f2 + cy, 0.0f);
                }
                this.renderer.color(colorBits);
                this.renderer.vertex(f + cx, f2 + cy, 0.0f);
            } else {
                check(ShapeType.Line, ShapeType.Filled, (i * 3) + 3);
                for (int i3 = 0; i3 < i; i3++) {
                    this.renderer.color(colorBits);
                    this.renderer.vertex(f, f2, 0.0f);
                    this.renderer.color(colorBits);
                    this.renderer.vertex(f + cx, f2 + cy, 0.0f);
                    float temp2 = cx;
                    cx = (cos * cx) - (sin * cy);
                    cy = (sin * temp2) + (cos * cy);
                    this.renderer.color(colorBits);
                    this.renderer.vertex(f + cx, f2 + cy, 0.0f);
                }
                this.renderer.color(colorBits);
                this.renderer.vertex(f, f2, 0.0f);
                this.renderer.color(colorBits);
                this.renderer.vertex(f + cx, f2 + cy, 0.0f);
            }
            float f3 = cx;
            this.renderer.color(colorBits);
            this.renderer.vertex(f + 0.0f, f2 + 0.0f, 0.0f);
            return;
        }
        throw new IllegalArgumentException("segments must be > 0.");
    }

    public void circle(float x, float y, float radius) {
        circle(x, y, radius, Math.max(1, (int) (((float) Math.cbrt((double) radius)) * 6.0f)));
    }

    public void circle(float x, float y, float radius, int segments) {
        float f = x;
        float f2 = y;
        int i = segments;
        if (i > 0) {
            float colorBits = this.color.toFloatBits();
            float angle = 6.2831855f / ((float) i);
            float cos = MathUtils.cos(angle);
            float sin = MathUtils.sin(angle);
            float cx = radius;
            float cy = 0.0f;
            if (this.shapeType == ShapeType.Line) {
                check(ShapeType.Line, ShapeType.Filled, (i * 2) + 2);
                for (int i2 = 0; i2 < i; i2++) {
                    this.renderer.color(colorBits);
                    this.renderer.vertex(f + cx, f2 + cy, 0.0f);
                    float temp = cx;
                    cx = (cos * cx) - (sin * cy);
                    cy = (sin * temp) + (cos * cy);
                    this.renderer.color(colorBits);
                    this.renderer.vertex(f + cx, f2 + cy, 0.0f);
                }
                this.renderer.color(colorBits);
                this.renderer.vertex(f + cx, f2 + cy, 0.0f);
            } else {
                check(ShapeType.Line, ShapeType.Filled, (i * 3) + 3);
                int segments2 = i - 1;
                for (int i3 = 0; i3 < segments2; i3++) {
                    this.renderer.color(colorBits);
                    this.renderer.vertex(f, f2, 0.0f);
                    this.renderer.color(colorBits);
                    this.renderer.vertex(f + cx, f2 + cy, 0.0f);
                    float temp2 = cx;
                    cx = (cos * cx) - (sin * cy);
                    cy = (sin * temp2) + (cos * cy);
                    this.renderer.color(colorBits);
                    this.renderer.vertex(f + cx, f2 + cy, 0.0f);
                }
                this.renderer.color(colorBits);
                this.renderer.vertex(f, f2, 0.0f);
                this.renderer.color(colorBits);
                this.renderer.vertex(f + cx, f2 + cy, 0.0f);
            }
            float f3 = cx;
            this.renderer.color(colorBits);
            this.renderer.vertex(f + radius, f2 + 0.0f, 0.0f);
            return;
        }
        throw new IllegalArgumentException("segments must be > 0.");
    }

    public void ellipse(float x, float y, float width, float height) {
        ellipse(x, y, width, height, Math.max(1, (int) (((float) Math.cbrt((double) Math.max(width * 0.5f, 0.5f * height))) * 12.0f)));
    }

    public void ellipse(float x, float y, float width, float height, int segments) {
        int i = segments;
        if (i > 0) {
            check(ShapeType.Line, ShapeType.Filled, i * 3);
            float colorBits = this.color.toFloatBits();
            float angle = 6.2831855f / ((float) i);
            float cx = (width / 2.0f) + x;
            float cy = (height / 2.0f) + y;
            if (this.shapeType == ShapeType.Line) {
                for (int i2 = 0; i2 < i; i2++) {
                    this.renderer.color(colorBits);
                    this.renderer.vertex((width * 0.5f * MathUtils.cos(((float) i2) * angle)) + cx, (height * 0.5f * MathUtils.sin(((float) i2) * angle)) + cy, 0.0f);
                    this.renderer.color(colorBits);
                    this.renderer.vertex((width * 0.5f * MathUtils.cos(((float) (i2 + 1)) * angle)) + cx, (height * 0.5f * MathUtils.sin(((float) (i2 + 1)) * angle)) + cy, 0.0f);
                }
                return;
            }
            for (int i3 = 0; i3 < i; i3++) {
                this.renderer.color(colorBits);
                this.renderer.vertex((width * 0.5f * MathUtils.cos(((float) i3) * angle)) + cx, (height * 0.5f * MathUtils.sin(((float) i3) * angle)) + cy, 0.0f);
                this.renderer.color(colorBits);
                this.renderer.vertex(cx, cy, 0.0f);
                this.renderer.color(colorBits);
                this.renderer.vertex((width * 0.5f * MathUtils.cos(((float) (i3 + 1)) * angle)) + cx, (height * 0.5f * MathUtils.sin(((float) (i3 + 1)) * angle)) + cy, 0.0f);
            }
            return;
        }
        throw new IllegalArgumentException("segments must be > 0.");
    }

    public void ellipse(float x, float y, float width, float height, float rotation) {
        ellipse(x, y, width, height, rotation, Math.max(1, (int) (((float) Math.cbrt((double) Math.max(width * 0.5f, 0.5f * height))) * 12.0f)));
    }

    public void ellipse(float x, float y, float width, float height, float rotation, int segments) {
        int i = segments;
        if (i > 0) {
            check(ShapeType.Line, ShapeType.Filled, i * 3);
            float colorBits = this.color.toFloatBits();
            float angle = 6.2831855f / ((float) i);
            float rotation2 = (3.1415927f * rotation) / 180.0f;
            float sin = MathUtils.sin(rotation2);
            float cos = MathUtils.cos(rotation2);
            float cx = x + (width / 2.0f);
            float cy = y + (height / 2.0f);
            float x1 = width * 0.5f;
            float y1 = 0.0f;
            if (this.shapeType == ShapeType.Line) {
                for (int i2 = 0; i2 < i; i2++) {
                    this.renderer.color(colorBits);
                    this.renderer.vertex(((cos * x1) + cx) - (sin * y1), cy + (sin * x1) + (cos * y1), 0.0f);
                    x1 = width * 0.5f * MathUtils.cos(((float) (i2 + 1)) * angle);
                    y1 = height * 0.5f * MathUtils.sin(((float) (i2 + 1)) * angle);
                    this.renderer.color(colorBits);
                    this.renderer.vertex(((cos * x1) + cx) - (sin * y1), (sin * x1) + cy + (cos * y1), 0.0f);
                }
                return;
            }
            for (int i3 = 0; i3 < i; i3++) {
                this.renderer.color(colorBits);
                this.renderer.vertex(((cos * x1) + cx) - (sin * y1), (sin * x1) + cy + (cos * y1), 0.0f);
                this.renderer.color(colorBits);
                this.renderer.vertex(cx, cy, 0.0f);
                x1 = width * 0.5f * MathUtils.cos(((float) (i3 + 1)) * angle);
                y1 = height * 0.5f * MathUtils.sin(((float) (i3 + 1)) * angle);
                this.renderer.color(colorBits);
                this.renderer.vertex(((cos * x1) + cx) - (sin * y1), cy + (sin * x1) + (cos * y1), 0.0f);
            }
            return;
        }
        throw new IllegalArgumentException("segments must be > 0.");
    }

    public void cone(float x, float y, float z, float radius, float height) {
        cone(x, y, z, radius, height, Math.max(1, (int) (((float) Math.sqrt((double) radius)) * 4.0f)));
    }

    public void cone(float x, float y, float z, float radius, float height, int segments) {
        float f = x;
        float f2 = y;
        float f3 = z;
        int i = segments;
        if (i > 0) {
            check(ShapeType.Line, ShapeType.Filled, (i * 4) + 2);
            float colorBits = this.color.toFloatBits();
            float angle = 6.2831855f / ((float) i);
            float cos = MathUtils.cos(angle);
            float sin = MathUtils.sin(angle);
            float cx = radius;
            float cy = 0.0f;
            if (this.shapeType == ShapeType.Line) {
                for (int i2 = 0; i2 < i; i2++) {
                    this.renderer.color(colorBits);
                    this.renderer.vertex(f + cx, f2 + cy, f3);
                    this.renderer.color(colorBits);
                    this.renderer.vertex(f, f2, f3 + height);
                    this.renderer.color(colorBits);
                    this.renderer.vertex(f + cx, f2 + cy, f3);
                    float temp = cx;
                    cx = (cos * cx) - (sin * cy);
                    cy = (sin * temp) + (cos * cy);
                    this.renderer.color(colorBits);
                    this.renderer.vertex(f + cx, f2 + cy, f3);
                }
                this.renderer.color(colorBits);
                this.renderer.vertex(f + cx, f2 + cy, f3);
            } else {
                int segments2 = i - 1;
                int i3 = 0;
                while (i3 < segments2) {
                    this.renderer.color(colorBits);
                    this.renderer.vertex(f, f2, f3);
                    this.renderer.color(colorBits);
                    this.renderer.vertex(f + cx, f2 + cy, f3);
                    float temp2 = cx;
                    float temp22 = cy;
                    cx = (cos * cx) - (sin * cy);
                    cy = (sin * temp2) + (cos * cy);
                    this.renderer.color(colorBits);
                    this.renderer.vertex(f + cx, f2 + cy, f3);
                    this.renderer.color(colorBits);
                    this.renderer.vertex(f + temp2, f2 + temp22, f3);
                    this.renderer.color(colorBits);
                    this.renderer.vertex(f + cx, f2 + cy, f3);
                    this.renderer.color(colorBits);
                    this.renderer.vertex(f, f2, f3 + height);
                    i3++;
                    segments2 = segments2;
                }
                this.renderer.color(colorBits);
                this.renderer.vertex(f, f2, f3);
                this.renderer.color(colorBits);
                this.renderer.vertex(f + cx, f2 + cy, f3);
                int i4 = segments2;
            }
            float temp3 = cx;
            float temp23 = cy;
            float cx2 = radius;
            this.renderer.color(colorBits);
            this.renderer.vertex(f + cx2, f2 + 0.0f, f3);
            if (this.shapeType != ShapeType.Line) {
                this.renderer.color(colorBits);
                this.renderer.vertex(f + temp3, f2 + temp23, f3);
                this.renderer.color(colorBits);
                this.renderer.vertex(f + cx2, f2 + 0.0f, f3);
                this.renderer.color(colorBits);
                this.renderer.vertex(f, f2, f3 + height);
                return;
            }
            return;
        }
        throw new IllegalArgumentException("segments must be > 0.");
    }

    public void polygon(float[] vertices, int offset, int count) {
        float y2;
        float x2;
        if (count < 6) {
            throw new IllegalArgumentException("Polygons must contain at least 3 points.");
        } else if (count % 2 == 0) {
            check(ShapeType.Line, (ShapeType) null, count);
            float colorBits = this.color.toFloatBits();
            float firstX = vertices[0];
            float firstY = vertices[1];
            int n = offset + count;
            for (int i = offset; i < n; i += 2) {
                float x1 = vertices[i];
                float y1 = vertices[i + 1];
                if (i + 2 >= count) {
                    x2 = firstX;
                    y2 = firstY;
                } else {
                    x2 = vertices[i + 2];
                    y2 = vertices[i + 3];
                }
                this.renderer.color(colorBits);
                this.renderer.vertex(x1, y1, 0.0f);
                this.renderer.color(colorBits);
                this.renderer.vertex(x2, y2, 0.0f);
            }
        } else {
            throw new IllegalArgumentException("Polygons must have an even number of vertices.");
        }
    }

    public void polygon(float[] vertices) {
        polygon(vertices, 0, vertices.length);
    }

    public void polyline(float[] vertices, int offset, int count) {
        if (count < 4) {
            throw new IllegalArgumentException("Polylines must contain at least 2 points.");
        } else if (count % 2 == 0) {
            check(ShapeType.Line, (ShapeType) null, count);
            float colorBits = this.color.toFloatBits();
            int n = (offset + count) - 2;
            for (int i = offset; i < n; i += 2) {
                float x1 = vertices[i];
                float y1 = vertices[i + 1];
                float x2 = vertices[i + 2];
                float y2 = vertices[i + 3];
                this.renderer.color(colorBits);
                this.renderer.vertex(x1, y1, 0.0f);
                this.renderer.color(colorBits);
                this.renderer.vertex(x2, y2, 0.0f);
            }
        } else {
            throw new IllegalArgumentException("Polylines must have an even number of vertices.");
        }
    }

    public void polyline(float[] vertices) {
        polyline(vertices, 0, vertices.length);
    }

    private void check(ShapeType preferred, ShapeType other, int newVertices) {
        ShapeType shapeType2 = this.shapeType;
        if (shapeType2 == null) {
            throw new IllegalStateException("begin must be called first.");
        } else if (shapeType2 == preferred || shapeType2 == other) {
            if (this.matrixDirty) {
                ShapeType type = this.shapeType;
                end();
                begin(type);
            } else if (this.renderer.getMaxVertices() - this.renderer.getNumVertices() < newVertices) {
                ShapeType type2 = this.shapeType;
                end();
                begin(type2);
            }
        } else if (this.autoShapeType) {
            end();
            begin(preferred);
        } else if (other == null) {
            throw new IllegalStateException("Must call begin(ShapeType." + preferred + ").");
        } else {
            throw new IllegalStateException("Must call begin(ShapeType." + preferred + ") or begin(ShapeType." + other + ").");
        }
    }

    public void end() {
        this.renderer.end();
        this.shapeType = null;
    }

    public void flush() {
        ShapeType type = this.shapeType;
        if (type != null) {
            end();
            begin(type);
        }
    }

    public ShapeType getCurrentType() {
        return this.shapeType;
    }

    public ImmediateModeRenderer getRenderer() {
        return this.renderer;
    }

    public boolean isDrawing() {
        return this.shapeType != null;
    }

    public void dispose() {
        this.renderer.dispose();
    }
}
