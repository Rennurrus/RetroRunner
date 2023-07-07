package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.twi.game.BuildConfig;

public class ImmediateModeRenderer20 implements ImmediateModeRenderer {
    private final int colorOffset;
    private final int maxVertices;
    private final Mesh mesh;
    private final int normalOffset;
    private int numSetTexCoords;
    private final int numTexCoords;
    private int numVertices;
    private boolean ownsShader;
    private int primitiveType;
    private final Matrix4 projModelView;
    private ShaderProgram shader;
    private final String[] shaderUniformNames;
    private final int texCoordOffset;
    private int vertexIdx;
    private final int vertexSize;
    private final float[] vertices;

    public ImmediateModeRenderer20(boolean hasNormals, boolean hasColors, int numTexCoords2) {
        this(5000, hasNormals, hasColors, numTexCoords2, createDefaultShader(hasNormals, hasColors, numTexCoords2));
        this.ownsShader = true;
    }

    public ImmediateModeRenderer20(int maxVertices2, boolean hasNormals, boolean hasColors, int numTexCoords2) {
        this(maxVertices2, hasNormals, hasColors, numTexCoords2, createDefaultShader(hasNormals, hasColors, numTexCoords2));
        this.ownsShader = true;
    }

    public ImmediateModeRenderer20(int maxVertices2, boolean hasNormals, boolean hasColors, int numTexCoords2, ShaderProgram shader2) {
        this.projModelView = new Matrix4();
        this.maxVertices = maxVertices2;
        this.numTexCoords = numTexCoords2;
        this.shader = shader2;
        int i = 0;
        this.mesh = new Mesh(false, maxVertices2, 0, buildVertexAttributes(hasNormals, hasColors, numTexCoords2));
        this.vertices = new float[((this.mesh.getVertexAttributes().vertexSize / 4) * maxVertices2)];
        this.vertexSize = this.mesh.getVertexAttributes().vertexSize / 4;
        this.normalOffset = this.mesh.getVertexAttribute(8) != null ? this.mesh.getVertexAttribute(8).offset / 4 : 0;
        this.colorOffset = this.mesh.getVertexAttribute(4) != null ? this.mesh.getVertexAttribute(4).offset / 4 : 0;
        this.texCoordOffset = this.mesh.getVertexAttribute(16) != null ? this.mesh.getVertexAttribute(16).offset / 4 : i;
        this.shaderUniformNames = new String[numTexCoords2];
        for (int i2 = 0; i2 < numTexCoords2; i2++) {
            String[] strArr = this.shaderUniformNames;
            strArr[i2] = "u_sampler" + i2;
        }
    }

    private VertexAttribute[] buildVertexAttributes(boolean hasNormals, boolean hasColor, int numTexCoords2) {
        Array<VertexAttribute> attribs = new Array<>();
        attribs.add(new VertexAttribute(1, 3, ShaderProgram.POSITION_ATTRIBUTE));
        if (hasNormals) {
            attribs.add(new VertexAttribute(8, 3, ShaderProgram.NORMAL_ATTRIBUTE));
        }
        if (hasColor) {
            attribs.add(new VertexAttribute(4, 4, ShaderProgram.COLOR_ATTRIBUTE));
        }
        for (int i = 0; i < numTexCoords2; i++) {
            attribs.add(new VertexAttribute(16, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + i));
        }
        VertexAttribute[] array = new VertexAttribute[attribs.size];
        for (int i2 = 0; i2 < attribs.size; i2++) {
            array[i2] = attribs.get(i2);
        }
        return array;
    }

    public void setShader(ShaderProgram shader2) {
        if (this.ownsShader) {
            this.shader.dispose();
        }
        this.shader = shader2;
        this.ownsShader = false;
    }

    public void begin(Matrix4 projModelView2, int primitiveType2) {
        this.projModelView.set(projModelView2);
        this.primitiveType = primitiveType2;
    }

    public void color(Color color) {
        this.vertices[this.vertexIdx + this.colorOffset] = color.toFloatBits();
    }

    public void color(float r, float g, float b, float a) {
        this.vertices[this.vertexIdx + this.colorOffset] = Color.toFloatBits(r, g, b, a);
    }

    public void color(float colorBits) {
        this.vertices[this.vertexIdx + this.colorOffset] = colorBits;
    }

    public void texCoord(float u, float v) {
        int idx = this.vertexIdx + this.texCoordOffset;
        float[] fArr = this.vertices;
        int i = this.numSetTexCoords;
        fArr[idx + i] = u;
        fArr[idx + i + 1] = v;
        this.numSetTexCoords = i + 2;
    }

    public void normal(float x, float y, float z) {
        int idx = this.vertexIdx + this.normalOffset;
        float[] fArr = this.vertices;
        fArr[idx] = x;
        fArr[idx + 1] = y;
        fArr[idx + 2] = z;
    }

    public void vertex(float x, float y, float z) {
        int idx = this.vertexIdx;
        float[] fArr = this.vertices;
        fArr[idx] = x;
        fArr[idx + 1] = y;
        fArr[idx + 2] = z;
        this.numSetTexCoords = 0;
        this.vertexIdx += this.vertexSize;
        this.numVertices++;
    }

    public void flush() {
        if (this.numVertices != 0) {
            this.shader.begin();
            this.shader.setUniformMatrix("u_projModelView", this.projModelView);
            for (int i = 0; i < this.numTexCoords; i++) {
                this.shader.setUniformi(this.shaderUniformNames[i], i);
            }
            this.mesh.setVertices(this.vertices, 0, this.vertexIdx);
            this.mesh.render(this.shader, this.primitiveType);
            this.shader.end();
            this.numSetTexCoords = 0;
            this.vertexIdx = 0;
            this.numVertices = 0;
        }
    }

    public void end() {
        flush();
    }

    public int getNumVertices() {
        return this.numVertices;
    }

    public int getMaxVertices() {
        return this.maxVertices;
    }

    public void dispose() {
        ShaderProgram shaderProgram;
        if (this.ownsShader && (shaderProgram = this.shader) != null) {
            shaderProgram.dispose();
        }
        this.mesh.dispose();
    }

    private static String createVertexShader(boolean hasNormals, boolean hasColors, int numTexCoords2) {
        StringBuilder sb = new StringBuilder();
        sb.append("attribute vec4 a_position;\n");
        String str = BuildConfig.FLAVOR;
        sb.append(hasNormals ? "attribute vec3 a_normal;\n" : str);
        sb.append(hasColors ? "attribute vec4 a_color;\n" : str);
        String shader2 = sb.toString();
        for (int i = 0; i < numTexCoords2; i++) {
            shader2 = shader2 + "attribute vec2 a_texCoord" + i + ";\n";
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append(shader2 + "uniform mat4 u_projModelView;\n");
        sb2.append(hasColors ? "varying vec4 v_col;\n" : str);
        String shader3 = sb2.toString();
        for (int i2 = 0; i2 < numTexCoords2; i2++) {
            shader3 = shader3 + "varying vec2 v_tex" + i2 + ";\n";
        }
        StringBuilder sb3 = new StringBuilder();
        sb3.append(shader3);
        sb3.append("void main() {\n   gl_Position = u_projModelView * a_position;\n");
        if (hasColors) {
            str = "   v_col = a_color;\n";
        }
        sb3.append(str);
        String shader4 = sb3.toString();
        for (int i3 = 0; i3 < numTexCoords2; i3++) {
            shader4 = shader4 + "   v_tex" + i3 + " = " + ShaderProgram.TEXCOORD_ATTRIBUTE + i3 + ";\n";
        }
        return (shader4 + "   gl_PointSize = 1.0;\n") + "}\n";
    }

    private static String createFragmentShader(boolean hasNormals, boolean hasColors, int numTexCoords2) {
        String shader2;
        String shader3 = "#ifdef GL_ES\nprecision mediump float;\n#endif\n";
        if (hasColors) {
            shader3 = shader3 + "varying vec4 v_col;\n";
        }
        for (int i = 0; i < numTexCoords2; i++) {
            shader3 = (shader3 + "varying vec2 v_tex" + i + ";\n") + "uniform sampler2D u_sampler" + i + ";\n";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(shader3);
        sb.append("void main() {\n   gl_FragColor = ");
        sb.append(hasColors ? "v_col" : "vec4(1, 1, 1, 1)");
        String shader4 = sb.toString();
        if (numTexCoords2 > 0) {
            shader4 = shader4 + " * ";
        }
        for (int i2 = 0; i2 < numTexCoords2; i2++) {
            if (i2 == numTexCoords2 - 1) {
                shader2 = shader2 + " texture2D(u_sampler" + i2 + ",  v_tex" + i2 + ")";
            } else {
                shader2 = shader2 + " texture2D(u_sampler" + i2 + ",  v_tex" + i2 + ") *";
            }
        }
        return shader2 + ";\n}";
    }

    public static ShaderProgram createDefaultShader(boolean hasNormals, boolean hasColors, int numTexCoords2) {
        return new ShaderProgram(createVertexShader(hasNormals, hasColors, numTexCoords2), createFragmentShader(hasNormals, hasColors, numTexCoords2));
    }
}
