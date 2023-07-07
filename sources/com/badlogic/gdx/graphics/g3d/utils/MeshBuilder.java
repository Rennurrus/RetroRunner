package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.ArrowShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.CapsuleShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.ConeShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.CylinderShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.EllipseShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.PatchShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.SphereShapeBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.ShortArray;
import java.util.Iterator;

public class MeshBuilder implements MeshPartBuilder {
    private static IntIntMap indicesMap = null;
    private static final ShortArray tmpIndices = new ShortArray();
    private static final FloatArray tmpVertices = new FloatArray();
    private static final Vector3 vTmp = new Vector3();
    private VertexAttributes attributes;
    private int biNorOffset;
    private final BoundingBox bounds = new BoundingBox();
    private int colOffset;
    private int colSize;
    private final Color color = new Color(Color.WHITE);
    private int cpOffset;
    private boolean hasColor = false;
    private boolean hasUVTransform = false;
    private ShortArray indices = new ShortArray();
    private int istart;
    private short lastIndex = -1;
    private int norOffset;
    private final Matrix3 normalTransform = new Matrix3();
    private MeshPart part;
    private Array<MeshPart> parts = new Array<>();
    private int posOffset;
    private int posSize;
    private final Matrix4 positionTransform = new Matrix4();
    private int primitiveType;
    private int stride;
    private int tangentOffset;
    private final Color tempC1 = new Color();
    private final Vector3 tmpNormal = new Vector3();
    private float uOffset = 0.0f;
    private float uScale = 1.0f;
    private int uvOffset;
    private float vOffset = 0.0f;
    private float vScale = 1.0f;
    private final MeshPartBuilder.VertexInfo vertTmp1 = new MeshPartBuilder.VertexInfo();
    private final MeshPartBuilder.VertexInfo vertTmp2 = new MeshPartBuilder.VertexInfo();
    private final MeshPartBuilder.VertexInfo vertTmp3 = new MeshPartBuilder.VertexInfo();
    private final MeshPartBuilder.VertexInfo vertTmp4 = new MeshPartBuilder.VertexInfo();
    private float[] vertex;
    private boolean vertexTransformationEnabled = false;
    private FloatArray vertices = new FloatArray();
    private int vindex;

    public static VertexAttributes createAttributes(long usage) {
        Array<VertexAttribute> attrs = new Array<>();
        if ((usage & 1) == 1) {
            attrs.add(new VertexAttribute(1, 3, ShaderProgram.POSITION_ATTRIBUTE));
        }
        if ((usage & 2) == 2) {
            attrs.add(new VertexAttribute(2, 4, ShaderProgram.COLOR_ATTRIBUTE));
        }
        if ((usage & 4) == 4) {
            attrs.add(new VertexAttribute(4, 4, ShaderProgram.COLOR_ATTRIBUTE));
        }
        if ((usage & 8) == 8) {
            attrs.add(new VertexAttribute(8, 3, ShaderProgram.NORMAL_ATTRIBUTE));
        }
        if ((usage & 16) == 16) {
            attrs.add(new VertexAttribute(16, 2, "a_texCoord0"));
        }
        VertexAttribute[] attributes2 = new VertexAttribute[attrs.size];
        for (int i = 0; i < attributes2.length; i++) {
            attributes2[i] = attrs.get(i);
        }
        return new VertexAttributes(attributes2);
    }

    public void begin(long attributes2) {
        begin(createAttributes(attributes2), -1);
    }

    public void begin(VertexAttributes attributes2) {
        begin(attributes2, -1);
    }

    public void begin(long attributes2, int primitiveType2) {
        begin(createAttributes(attributes2), primitiveType2);
    }

    public void begin(VertexAttributes attributes2, int primitiveType2) {
        if (this.attributes == null) {
            this.attributes = attributes2;
            this.vertices.clear();
            this.indices.clear();
            this.parts.clear();
            int i = 0;
            this.vindex = 0;
            int i2 = -1;
            this.lastIndex = -1;
            this.istart = 0;
            this.part = null;
            this.stride = attributes2.vertexSize / 4;
            float[] fArr = this.vertex;
            if (fArr == null || fArr.length < this.stride) {
                this.vertex = new float[this.stride];
            }
            VertexAttribute a = attributes2.findByUsage(1);
            if (a != null) {
                this.posOffset = a.offset / 4;
                this.posSize = a.numComponents;
                VertexAttribute a2 = attributes2.findByUsage(8);
                this.norOffset = a2 == null ? -1 : a2.offset / 4;
                VertexAttribute a3 = attributes2.findByUsage(256);
                this.biNorOffset = a3 == null ? -1 : a3.offset / 4;
                VertexAttribute a4 = attributes2.findByUsage(128);
                this.tangentOffset = a4 == null ? -1 : a4.offset / 4;
                VertexAttribute a5 = attributes2.findByUsage(2);
                this.colOffset = a5 == null ? -1 : a5.offset / 4;
                if (a5 != null) {
                    i = a5.numComponents;
                }
                this.colSize = i;
                VertexAttribute a6 = attributes2.findByUsage(4);
                this.cpOffset = a6 == null ? -1 : a6.offset / 4;
                VertexAttribute a7 = attributes2.findByUsage(16);
                if (a7 != null) {
                    i2 = a7.offset / 4;
                }
                this.uvOffset = i2;
                setColor((Color) null);
                setVertexTransform((Matrix4) null);
                setUVRange((TextureRegion) null);
                this.primitiveType = primitiveType2;
                this.bounds.inf();
                return;
            }
            throw new GdxRuntimeException("Cannot build mesh without position attribute");
        }
        throw new RuntimeException("Call end() first");
    }

    private void endpart() {
        MeshPart meshPart = this.part;
        if (meshPart != null) {
            this.bounds.getCenter(meshPart.center);
            this.bounds.getDimensions(this.part.halfExtents).scl(0.5f);
            MeshPart meshPart2 = this.part;
            meshPart2.radius = meshPart2.halfExtents.len();
            this.bounds.inf();
            MeshPart meshPart3 = this.part;
            meshPart3.offset = this.istart;
            meshPart3.size = this.indices.size - this.istart;
            this.istart = this.indices.size;
            this.part = null;
        }
    }

    public MeshPart part(String id, int primitiveType2) {
        return part(id, primitiveType2, new MeshPart());
    }

    public MeshPart part(String id, int primitiveType2, MeshPart meshPart) {
        if (this.attributes != null) {
            endpart();
            this.part = meshPart;
            MeshPart meshPart2 = this.part;
            meshPart2.id = id;
            meshPart2.primitiveType = primitiveType2;
            this.primitiveType = primitiveType2;
            this.parts.add(meshPart2);
            setColor((Color) null);
            setVertexTransform((Matrix4) null);
            setUVRange((TextureRegion) null);
            return this.part;
        }
        throw new RuntimeException("Call begin() first");
    }

    public Mesh end(Mesh mesh) {
        endpart();
        VertexAttributes vertexAttributes = this.attributes;
        if (vertexAttributes == null) {
            throw new GdxRuntimeException("Call begin() first");
        } else if (!vertexAttributes.equals(mesh.getVertexAttributes())) {
            throw new GdxRuntimeException("Mesh attributes don't match");
        } else if (mesh.getMaxVertices() * this.stride < this.vertices.size) {
            throw new GdxRuntimeException("Mesh can't hold enough vertices: " + mesh.getMaxVertices() + " * " + this.stride + " < " + this.vertices.size);
        } else if (mesh.getMaxIndices() >= this.indices.size) {
            mesh.setVertices(this.vertices.items, 0, this.vertices.size);
            mesh.setIndices(this.indices.items, 0, this.indices.size);
            Iterator<MeshPart> it = this.parts.iterator();
            while (it.hasNext()) {
                it.next().mesh = mesh;
            }
            this.parts.clear();
            this.attributes = null;
            this.vertices.clear();
            this.indices.clear();
            return mesh;
        } else {
            throw new GdxRuntimeException("Mesh can't hold enough indices: " + mesh.getMaxIndices() + " < " + this.indices.size);
        }
    }

    public Mesh end() {
        return end(new Mesh(true, this.vertices.size / this.stride, this.indices.size, this.attributes));
    }

    public void clear() {
        this.vertices.clear();
        this.indices.clear();
        this.parts.clear();
        this.vindex = 0;
        this.lastIndex = -1;
        this.istart = 0;
        this.part = null;
    }

    public int getFloatsPerVertex() {
        return this.stride;
    }

    public int getNumVertices() {
        return this.vertices.size / this.stride;
    }

    public void getVertices(float[] out, int destOffset) {
        if (this.attributes == null) {
            throw new GdxRuntimeException("Must be called in between #begin and #end");
        } else if (destOffset < 0 || destOffset > out.length - this.vertices.size) {
            throw new GdxRuntimeException("Array to small or offset out of range");
        } else {
            System.arraycopy(this.vertices.items, 0, out, destOffset, this.vertices.size);
        }
    }

    /* access modifiers changed from: protected */
    public float[] getVertices() {
        return this.vertices.items;
    }

    public int getNumIndices() {
        return this.indices.size;
    }

    public void getIndices(short[] out, int destOffset) {
        if (this.attributes == null) {
            throw new GdxRuntimeException("Must be called in between #begin and #end");
        } else if (destOffset < 0 || destOffset > out.length - this.indices.size) {
            throw new GdxRuntimeException("Array to small or offset out of range");
        } else {
            System.arraycopy(this.indices.items, 0, out, destOffset, this.indices.size);
        }
    }

    /* access modifiers changed from: protected */
    public short[] getIndices() {
        return this.indices.items;
    }

    public VertexAttributes getAttributes() {
        return this.attributes;
    }

    public MeshPart getMeshPart() {
        return this.part;
    }

    public int getPrimitiveType() {
        return this.primitiveType;
    }

    public void setColor(float r, float g, float b, float a) {
        this.color.set(r, g, b, a);
        this.hasColor = !this.color.equals(Color.WHITE);
    }

    public void setColor(Color color2) {
        Color color3 = this.color;
        boolean z = color2 != null;
        this.hasColor = z;
        color3.set(!z ? Color.WHITE : color2);
    }

    public void setUVRange(float u1, float v1, float u2, float v2) {
        this.uOffset = u1;
        this.vOffset = v1;
        this.uScale = u2 - u1;
        this.vScale = v2 - v1;
        this.hasUVTransform = !MathUtils.isZero(u1) || !MathUtils.isZero(v1) || !MathUtils.isEqual(u2, 1.0f) || !MathUtils.isEqual(v2, 1.0f);
    }

    public void setUVRange(TextureRegion region) {
        boolean z = region != null;
        this.hasUVTransform = z;
        if (!z) {
            this.vOffset = 0.0f;
            this.uOffset = 0.0f;
            this.vScale = 1.0f;
            this.uScale = 1.0f;
            return;
        }
        setUVRange(region.getU(), region.getV(), region.getU2(), region.getV2());
    }

    public Matrix4 getVertexTransform(Matrix4 out) {
        return out.set(this.positionTransform);
    }

    public void setVertexTransform(Matrix4 transform) {
        this.vertexTransformationEnabled = transform != null;
        if (this.vertexTransformationEnabled) {
            this.positionTransform.set(transform);
            this.normalTransform.set(transform).inv().transpose();
            return;
        }
        this.positionTransform.idt();
        this.normalTransform.idt();
    }

    public boolean isVertexTransformationEnabled() {
        return this.vertexTransformationEnabled;
    }

    public void setVertexTransformationEnabled(boolean enabled) {
        this.vertexTransformationEnabled = enabled;
    }

    public void ensureVertices(int numVertices) {
        this.vertices.ensureCapacity(this.stride * numVertices);
    }

    public void ensureIndices(int numIndices) {
        this.indices.ensureCapacity(numIndices);
    }

    public void ensureCapacity(int numVertices, int numIndices) {
        ensureVertices(numVertices);
        ensureIndices(numIndices);
    }

    public void ensureTriangleIndices(int numTriangles) {
        int i = this.primitiveType;
        if (i == 1) {
            ensureIndices(numTriangles * 6);
        } else if (i == 4 || i == 0) {
            ensureIndices(numTriangles * 3);
        } else {
            throw new GdxRuntimeException("Incorrect primtive type");
        }
    }

    @Deprecated
    public void ensureTriangles(int numVertices, int numTriangles) {
        ensureVertices(numVertices);
        ensureTriangleIndices(numTriangles);
    }

    @Deprecated
    public void ensureTriangles(int numTriangles) {
        ensureVertices(numTriangles * 3);
        ensureTriangleIndices(numTriangles);
    }

    public void ensureRectangleIndices(int numRectangles) {
        int i = this.primitiveType;
        if (i == 0) {
            ensureIndices(numRectangles * 4);
        } else if (i == 1) {
            ensureIndices(numRectangles * 8);
        } else {
            ensureIndices(numRectangles * 6);
        }
    }

    @Deprecated
    public void ensureRectangles(int numVertices, int numRectangles) {
        ensureVertices(numVertices);
        ensureRectangleIndices(numRectangles);
    }

    public void ensureRectangles(int numRectangles) {
        ensureVertices(numRectangles * 4);
        ensureRectangleIndices(numRectangles);
    }

    public short lastIndex() {
        return this.lastIndex;
    }

    private static final void transformPosition(float[] values, int offset, int size, Matrix4 transform) {
        if (size > 2) {
            vTmp.set(values[offset], values[offset + 1], values[offset + 2]).mul(transform);
            values[offset] = vTmp.x;
            values[offset + 1] = vTmp.y;
            values[offset + 2] = vTmp.z;
        } else if (size > 1) {
            vTmp.set(values[offset], values[offset + 1], 0.0f).mul(transform);
            values[offset] = vTmp.x;
            values[offset + 1] = vTmp.y;
        } else {
            values[offset] = vTmp.set(values[offset], 0.0f, 0.0f).mul(transform).x;
        }
    }

    private static final void transformNormal(float[] values, int offset, int size, Matrix3 transform) {
        if (size > 2) {
            vTmp.set(values[offset], values[offset + 1], values[offset + 2]).mul(transform).nor();
            values[offset] = vTmp.x;
            values[offset + 1] = vTmp.y;
            values[offset + 2] = vTmp.z;
        } else if (size > 1) {
            vTmp.set(values[offset], values[offset + 1], 0.0f).mul(transform).nor();
            values[offset] = vTmp.x;
            values[offset + 1] = vTmp.y;
        } else {
            values[offset] = vTmp.set(values[offset], 0.0f, 0.0f).mul(transform).nor().x;
        }
    }

    private final void addVertex(float[] values, int offset) {
        int o = this.vertices.size;
        this.vertices.addAll(values, offset, this.stride);
        int i = this.vindex;
        this.vindex = i + 1;
        this.lastIndex = (short) i;
        if (this.vertexTransformationEnabled) {
            transformPosition(this.vertices.items, this.posOffset + o, this.posSize, this.positionTransform);
            if (this.norOffset >= 0) {
                transformNormal(this.vertices.items, this.norOffset + o, 3, this.normalTransform);
            }
            if (this.biNorOffset >= 0) {
                transformNormal(this.vertices.items, this.biNorOffset + o, 3, this.normalTransform);
            }
            if (this.tangentOffset >= 0) {
                transformNormal(this.vertices.items, this.tangentOffset + o, 3, this.normalTransform);
            }
        }
        float x = this.vertices.items[this.posOffset + o];
        float z = 0.0f;
        float y = this.posSize > 1 ? this.vertices.items[this.posOffset + o + 1] : 0.0f;
        if (this.posSize > 2) {
            z = this.vertices.items[this.posOffset + o + 2];
        }
        this.bounds.ext(x, y, z);
        if (this.hasColor) {
            if (this.colOffset >= 0) {
                float[] fArr = this.vertices.items;
                int i2 = this.colOffset + o;
                fArr[i2] = fArr[i2] * this.color.r;
                float[] fArr2 = this.vertices.items;
                int i3 = this.colOffset + o + 1;
                fArr2[i3] = fArr2[i3] * this.color.g;
                float[] fArr3 = this.vertices.items;
                int i4 = this.colOffset + o + 2;
                fArr3[i4] = fArr3[i4] * this.color.b;
                if (this.colSize > 3) {
                    float[] fArr4 = this.vertices.items;
                    int i5 = this.colOffset + o + 3;
                    fArr4[i5] = fArr4[i5] * this.color.a;
                }
            } else if (this.cpOffset >= 0) {
                Color.abgr8888ToColor(this.tempC1, this.vertices.items[this.cpOffset + o]);
                this.vertices.items[this.cpOffset + o] = this.tempC1.mul(this.color).toFloatBits();
            }
        }
        if (this.hasUVTransform && this.uvOffset >= 0) {
            this.vertices.items[this.uvOffset + o] = this.uOffset + (this.uScale * this.vertices.items[this.uvOffset + o]);
            this.vertices.items[this.uvOffset + o + 1] = this.vOffset + (this.vScale * this.vertices.items[this.uvOffset + o + 1]);
        }
    }

    public short vertex(Vector3 pos, Vector3 nor, Color col, Vector2 uv) {
        int i;
        if (this.vindex <= 32767) {
            this.vertex[this.posOffset] = pos.x;
            if (this.posSize > 1) {
                this.vertex[this.posOffset + 1] = pos.y;
            }
            if (this.posSize > 2) {
                this.vertex[this.posOffset + 2] = pos.z;
            }
            if (this.norOffset >= 0) {
                if (nor == null) {
                    nor = this.tmpNormal.set(pos).nor();
                }
                this.vertex[this.norOffset] = nor.x;
                this.vertex[this.norOffset + 1] = nor.y;
                this.vertex[this.norOffset + 2] = nor.z;
            }
            if (this.colOffset >= 0) {
                if (col == null) {
                    col = Color.WHITE;
                }
                this.vertex[this.colOffset] = col.r;
                this.vertex[this.colOffset + 1] = col.g;
                this.vertex[this.colOffset + 2] = col.b;
                if (this.colSize > 3) {
                    this.vertex[this.colOffset + 3] = col.a;
                }
            } else if (this.cpOffset > 0) {
                if (col == null) {
                    col = Color.WHITE;
                }
                this.vertex[this.cpOffset] = col.toFloatBits();
            }
            if (uv != null && (i = this.uvOffset) >= 0) {
                this.vertex[i] = uv.x;
                this.vertex[this.uvOffset + 1] = uv.y;
            }
            addVertex(this.vertex, 0);
            return this.lastIndex;
        }
        throw new GdxRuntimeException("Too many vertices used");
    }

    public short vertex(float... values) {
        int n = values.length - this.stride;
        int i = 0;
        while (i <= n) {
            addVertex(values, i);
            i += this.stride;
        }
        return this.lastIndex;
    }

    public short vertex(MeshPartBuilder.VertexInfo info) {
        Vector2 vector2 = null;
        Vector3 vector3 = info.hasPosition ? info.position : null;
        Vector3 vector32 = info.hasNormal ? info.normal : null;
        Color color2 = info.hasColor ? info.color : null;
        if (info.hasUV) {
            vector2 = info.uv;
        }
        return vertex(vector3, vector32, color2, vector2);
    }

    public void index(short value) {
        this.indices.add(value);
    }

    public void index(short value1, short value2) {
        ensureIndices(2);
        this.indices.add(value1);
        this.indices.add(value2);
    }

    public void index(short value1, short value2, short value3) {
        ensureIndices(3);
        this.indices.add(value1);
        this.indices.add(value2);
        this.indices.add(value3);
    }

    public void index(short value1, short value2, short value3, short value4) {
        ensureIndices(4);
        this.indices.add(value1);
        this.indices.add(value2);
        this.indices.add(value3);
        this.indices.add(value4);
    }

    public void index(short value1, short value2, short value3, short value4, short value5, short value6) {
        ensureIndices(6);
        this.indices.add(value1);
        this.indices.add(value2);
        this.indices.add(value3);
        this.indices.add(value4);
        this.indices.add(value5);
        this.indices.add(value6);
    }

    public void index(short value1, short value2, short value3, short value4, short value5, short value6, short value7, short value8) {
        ensureIndices(8);
        this.indices.add(value1);
        this.indices.add(value2);
        this.indices.add(value3);
        this.indices.add(value4);
        this.indices.add(value5);
        this.indices.add(value6);
        this.indices.add(value7);
        this.indices.add(value8);
    }

    public void line(short index1, short index2) {
        if (this.primitiveType == 1) {
            index(index1, index2);
            return;
        }
        throw new GdxRuntimeException("Incorrect primitive type");
    }

    public void line(MeshPartBuilder.VertexInfo p1, MeshPartBuilder.VertexInfo p2) {
        ensureVertices(2);
        line(vertex(p1), vertex(p2));
    }

    public void line(Vector3 p1, Vector3 p2) {
        line(this.vertTmp1.set(p1, (Vector3) null, (Color) null, (Vector2) null), this.vertTmp2.set(p2, (Vector3) null, (Color) null, (Vector2) null));
    }

    public void line(float x1, float y1, float z1, float x2, float y2, float z2) {
        line(this.vertTmp1.set((Vector3) null, (Vector3) null, (Color) null, (Vector2) null).setPos(x1, y1, z1), this.vertTmp2.set((Vector3) null, (Vector3) null, (Color) null, (Vector2) null).setPos(x2, y2, z2));
    }

    public void line(Vector3 p1, Color c1, Vector3 p2, Color c2) {
        line(this.vertTmp1.set(p1, (Vector3) null, c1, (Vector2) null), this.vertTmp2.set(p2, (Vector3) null, c2, (Vector2) null));
    }

    public void triangle(short index1, short index2, short index3) {
        int i = this.primitiveType;
        if (i == 4 || i == 0) {
            index(index1, index2, index3);
        } else if (i == 1) {
            index(index1, index2, index2, index3, index3, index1);
        } else {
            throw new GdxRuntimeException("Incorrect primitive type");
        }
    }

    public void triangle(MeshPartBuilder.VertexInfo p1, MeshPartBuilder.VertexInfo p2, MeshPartBuilder.VertexInfo p3) {
        ensureVertices(3);
        triangle(vertex(p1), vertex(p2), vertex(p3));
    }

    public void triangle(Vector3 p1, Vector3 p2, Vector3 p3) {
        triangle(this.vertTmp1.set(p1, (Vector3) null, (Color) null, (Vector2) null), this.vertTmp2.set(p2, (Vector3) null, (Color) null, (Vector2) null), this.vertTmp3.set(p3, (Vector3) null, (Color) null, (Vector2) null));
    }

    public void triangle(Vector3 p1, Color c1, Vector3 p2, Color c2, Vector3 p3, Color c3) {
        triangle(this.vertTmp1.set(p1, (Vector3) null, c1, (Vector2) null), this.vertTmp2.set(p2, (Vector3) null, c2, (Vector2) null), this.vertTmp3.set(p3, (Vector3) null, c3, (Vector2) null));
    }

    public void rect(short corner00, short corner10, short corner11, short corner01) {
        int i = this.primitiveType;
        if (i == 4) {
            index(corner00, corner10, corner11, corner11, corner01, corner00);
        } else if (i == 1) {
            index(corner00, corner10, corner10, corner11, corner11, corner01, corner01, corner00);
        } else if (i == 0) {
            index(corner00, corner10, corner11, corner01);
        } else {
            throw new GdxRuntimeException("Incorrect primitive type");
        }
    }

    public void rect(MeshPartBuilder.VertexInfo corner00, MeshPartBuilder.VertexInfo corner10, MeshPartBuilder.VertexInfo corner11, MeshPartBuilder.VertexInfo corner01) {
        ensureVertices(4);
        rect(vertex(corner00), vertex(corner10), vertex(corner11), vertex(corner01));
    }

    public void rect(Vector3 corner00, Vector3 corner10, Vector3 corner11, Vector3 corner01, Vector3 normal) {
        rect(this.vertTmp1.set(corner00, normal, (Color) null, (Vector2) null).setUV(0.0f, 1.0f), this.vertTmp2.set(corner10, normal, (Color) null, (Vector2) null).setUV(1.0f, 1.0f), this.vertTmp3.set(corner11, normal, (Color) null, (Vector2) null).setUV(1.0f, 0.0f), this.vertTmp4.set(corner01, normal, (Color) null, (Vector2) null).setUV(0.0f, 0.0f));
    }

    public void rect(float x00, float y00, float z00, float x10, float y10, float z10, float x11, float y11, float z11, float x01, float y01, float z01, float normalX, float normalY, float normalZ) {
        float f = normalX;
        float f2 = normalY;
        float f3 = normalZ;
        rect(this.vertTmp1.set((Vector3) null, (Vector3) null, (Color) null, (Vector2) null).setPos(x00, y00, z00).setNor(f, f2, f3).setUV(0.0f, 1.0f), this.vertTmp2.set((Vector3) null, (Vector3) null, (Color) null, (Vector2) null).setPos(x10, y10, z10).setNor(f, f2, f3).setUV(1.0f, 1.0f), this.vertTmp3.set((Vector3) null, (Vector3) null, (Color) null, (Vector2) null).setPos(x11, y11, z11).setNor(f, f2, f3).setUV(1.0f, 0.0f), this.vertTmp4.set((Vector3) null, (Vector3) null, (Color) null, (Vector2) null).setPos(x01, y01, z01).setNor(f, f2, f3).setUV(0.0f, 0.0f));
    }

    public void addMesh(Mesh mesh) {
        addMesh(mesh, 0, mesh.getNumIndices());
    }

    public void addMesh(MeshPart meshpart) {
        if (meshpart.primitiveType == this.primitiveType) {
            addMesh(meshpart.mesh, meshpart.offset, meshpart.size);
            return;
        }
        throw new GdxRuntimeException("Primitive type doesn't match");
    }

    public void addMesh(Mesh mesh, int indexOffset, int numIndices) {
        if (!this.attributes.equals(mesh.getVertexAttributes())) {
            throw new GdxRuntimeException("Vertex attributes do not match");
        } else if (numIndices > 0) {
            int numFloats = mesh.getNumVertices() * this.stride;
            tmpVertices.clear();
            tmpVertices.ensureCapacity(numFloats);
            FloatArray floatArray = tmpVertices;
            floatArray.size = numFloats;
            mesh.getVertices(floatArray.items);
            tmpIndices.clear();
            tmpIndices.ensureCapacity(numIndices);
            ShortArray shortArray = tmpIndices;
            shortArray.size = numIndices;
            mesh.getIndices(indexOffset, numIndices, shortArray.items, 0);
            addMesh(tmpVertices.items, tmpIndices.items, 0, numIndices);
        }
    }

    public void addMesh(float[] vertices2, short[] indices2, int indexOffset, int numIndices) {
        IntIntMap intIntMap = indicesMap;
        if (intIntMap == null) {
            indicesMap = new IntIntMap(numIndices);
        } else {
            intIntMap.clear();
            indicesMap.ensureCapacity(numIndices);
        }
        ensureIndices(numIndices);
        int numVertices = vertices2.length / this.stride;
        ensureVertices(numVertices < numIndices ? numVertices : numIndices);
        for (int i = 0; i < numIndices; i++) {
            short sidx = indices2[indexOffset + i];
            int didx = indicesMap.get(sidx, -1);
            if (didx < 0) {
                addVertex(vertices2, this.stride * sidx);
                IntIntMap intIntMap2 = indicesMap;
                short s = this.lastIndex;
                didx = s;
                intIntMap2.put(sidx, s);
            }
            index((short) didx);
        }
    }

    public void addMesh(float[] vertices2, short[] indices2) {
        short offset = (short) (this.lastIndex + 1);
        ensureVertices(vertices2.length / this.stride);
        int v = 0;
        while (v < vertices2.length) {
            addVertex(vertices2, v);
            v += this.stride;
        }
        ensureIndices(indices2.length);
        for (short s : indices2) {
            index((short) (s + offset));
        }
    }

    @Deprecated
    public void patch(MeshPartBuilder.VertexInfo corner00, MeshPartBuilder.VertexInfo corner10, MeshPartBuilder.VertexInfo corner11, MeshPartBuilder.VertexInfo corner01, int divisionsU, int divisionsV) {
        PatchShapeBuilder.build(this, corner00, corner10, corner11, corner01, divisionsU, divisionsV);
    }

    @Deprecated
    public void patch(Vector3 corner00, Vector3 corner10, Vector3 corner11, Vector3 corner01, Vector3 normal, int divisionsU, int divisionsV) {
        PatchShapeBuilder.build(this, corner00, corner10, corner11, corner01, normal, divisionsU, divisionsV);
    }

    @Deprecated
    public void patch(float x00, float y00, float z00, float x10, float y10, float z10, float x11, float y11, float z11, float x01, float y01, float z01, float normalX, float normalY, float normalZ, int divisionsU, int divisionsV) {
        PatchShapeBuilder.build(this, x00, y00, z00, x10, y10, z10, x11, y11, z11, x01, y01, z01, normalX, normalY, normalZ, divisionsU, divisionsV);
    }

    @Deprecated
    public void box(MeshPartBuilder.VertexInfo corner000, MeshPartBuilder.VertexInfo corner010, MeshPartBuilder.VertexInfo corner100, MeshPartBuilder.VertexInfo corner110, MeshPartBuilder.VertexInfo corner001, MeshPartBuilder.VertexInfo corner011, MeshPartBuilder.VertexInfo corner101, MeshPartBuilder.VertexInfo corner111) {
        BoxShapeBuilder.build((MeshPartBuilder) this, corner000, corner010, corner100, corner110, corner001, corner011, corner101, corner111);
    }

    @Deprecated
    public void box(Vector3 corner000, Vector3 corner010, Vector3 corner100, Vector3 corner110, Vector3 corner001, Vector3 corner011, Vector3 corner101, Vector3 corner111) {
        BoxShapeBuilder.build((MeshPartBuilder) this, corner000, corner010, corner100, corner110, corner001, corner011, corner101, corner111);
    }

    @Deprecated
    public void box(Matrix4 transform) {
        BoxShapeBuilder.build((MeshPartBuilder) this, transform);
    }

    @Deprecated
    public void box(float width, float height, float depth) {
        BoxShapeBuilder.build(this, width, height, depth);
    }

    @Deprecated
    public void box(float x, float y, float z, float width, float height, float depth) {
        BoxShapeBuilder.build(this, x, y, z, width, height, depth);
    }

    @Deprecated
    public void circle(float radius, int divisions, float centerX, float centerY, float centerZ, float normalX, float normalY, float normalZ) {
        EllipseShapeBuilder.build((MeshPartBuilder) this, radius, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ);
    }

    @Deprecated
    public void circle(float radius, int divisions, Vector3 center, Vector3 normal) {
        EllipseShapeBuilder.build(this, radius, divisions, center, normal);
    }

    @Deprecated
    public void circle(float radius, int divisions, Vector3 center, Vector3 normal, Vector3 tangent, Vector3 binormal) {
        EllipseShapeBuilder.build((MeshPartBuilder) this, radius, divisions, center, normal, tangent, binormal);
    }

    @Deprecated
    public void circle(float radius, int divisions, float centerX, float centerY, float centerZ, float normalX, float normalY, float normalZ, float tangentX, float tangentY, float tangentZ, float binormalX, float binormalY, float binormalZ) {
        EllipseShapeBuilder.build(this, radius, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ, tangentX, tangentY, tangentZ, binormalX, binormalY, binormalZ);
    }

    @Deprecated
    public void circle(float radius, int divisions, float centerX, float centerY, float centerZ, float normalX, float normalY, float normalZ, float angleFrom, float angleTo) {
        EllipseShapeBuilder.build(this, radius, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ, angleFrom, angleTo);
    }

    @Deprecated
    public void circle(float radius, int divisions, Vector3 center, Vector3 normal, float angleFrom, float angleTo) {
        EllipseShapeBuilder.build((MeshPartBuilder) this, radius, divisions, center, normal, angleFrom, angleTo);
    }

    @Deprecated
    public void circle(float radius, int divisions, Vector3 center, Vector3 normal, Vector3 tangent, Vector3 binormal, float angleFrom, float angleTo) {
        Vector3 vector3 = center;
        Vector3 vector32 = normal;
        Vector3 vector33 = tangent;
        Vector3 vector34 = binormal;
        circle(radius, divisions, vector3.x, vector3.y, vector3.z, vector32.x, vector32.y, vector32.z, vector33.x, vector33.y, vector33.z, vector34.x, vector34.y, vector34.z, angleFrom, angleTo);
    }

    @Deprecated
    public void circle(float radius, int divisions, float centerX, float centerY, float centerZ, float normalX, float normalY, float normalZ, float tangentX, float tangentY, float tangentZ, float binormalX, float binormalY, float binormalZ, float angleFrom, float angleTo) {
        EllipseShapeBuilder.build(this, radius, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ, tangentX, tangentY, tangentZ, binormalX, binormalY, binormalZ, angleFrom, angleTo);
    }

    @Deprecated
    public void ellipse(float width, float height, int divisions, float centerX, float centerY, float centerZ, float normalX, float normalY, float normalZ) {
        EllipseShapeBuilder.build((MeshPartBuilder) this, width, height, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ);
    }

    @Deprecated
    public void ellipse(float width, float height, int divisions, Vector3 center, Vector3 normal) {
        EllipseShapeBuilder.build(this, width, height, divisions, center, normal);
    }

    @Deprecated
    public void ellipse(float width, float height, int divisions, Vector3 center, Vector3 normal, Vector3 tangent, Vector3 binormal) {
        EllipseShapeBuilder.build((MeshPartBuilder) this, width, height, divisions, center, normal, tangent, binormal);
    }

    @Deprecated
    public void ellipse(float width, float height, int divisions, float centerX, float centerY, float centerZ, float normalX, float normalY, float normalZ, float tangentX, float tangentY, float tangentZ, float binormalX, float binormalY, float binormalZ) {
        EllipseShapeBuilder.build(this, width, height, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ, tangentX, tangentY, tangentZ, binormalX, binormalY, binormalZ);
    }

    @Deprecated
    public void ellipse(float width, float height, int divisions, float centerX, float centerY, float centerZ, float normalX, float normalY, float normalZ, float angleFrom, float angleTo) {
        EllipseShapeBuilder.build((MeshPartBuilder) this, width, height, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ, angleFrom, angleTo);
    }

    @Deprecated
    public void ellipse(float width, float height, int divisions, Vector3 center, Vector3 normal, float angleFrom, float angleTo) {
        EllipseShapeBuilder.build((MeshPartBuilder) this, width, height, divisions, center, normal, angleFrom, angleTo);
    }

    @Deprecated
    public void ellipse(float width, float height, int divisions, Vector3 center, Vector3 normal, Vector3 tangent, Vector3 binormal, float angleFrom, float angleTo) {
        EllipseShapeBuilder.build((MeshPartBuilder) this, width, height, divisions, center, normal, tangent, binormal, angleFrom, angleTo);
    }

    @Deprecated
    public void ellipse(float width, float height, int divisions, float centerX, float centerY, float centerZ, float normalX, float normalY, float normalZ, float tangentX, float tangentY, float tangentZ, float binormalX, float binormalY, float binormalZ, float angleFrom, float angleTo) {
        EllipseShapeBuilder.build(this, width, height, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ, tangentX, tangentY, tangentZ, binormalX, binormalY, binormalZ, angleFrom, angleTo);
    }

    @Deprecated
    public void ellipse(float width, float height, float innerWidth, float innerHeight, int divisions, Vector3 center, Vector3 normal) {
        EllipseShapeBuilder.build((MeshPartBuilder) this, width, height, innerWidth, innerHeight, divisions, center, normal);
    }

    @Deprecated
    public void ellipse(float width, float height, float innerWidth, float innerHeight, int divisions, float centerX, float centerY, float centerZ, float normalX, float normalY, float normalZ) {
        EllipseShapeBuilder.build((MeshPartBuilder) this, width, height, innerWidth, innerHeight, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ);
    }

    @Deprecated
    public void ellipse(float width, float height, float innerWidth, float innerHeight, int divisions, float centerX, float centerY, float centerZ, float normalX, float normalY, float normalZ, float angleFrom, float angleTo) {
        EllipseShapeBuilder.build(this, width, height, innerWidth, innerHeight, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ, angleFrom, angleTo);
    }

    @Deprecated
    public void ellipse(float width, float height, float innerWidth, float innerHeight, int divisions, float centerX, float centerY, float centerZ, float normalX, float normalY, float normalZ, float tangentX, float tangentY, float tangentZ, float binormalX, float binormalY, float binormalZ, float angleFrom, float angleTo) {
        EllipseShapeBuilder.build(this, width, height, innerWidth, innerHeight, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ, tangentX, tangentY, tangentZ, binormalX, binormalY, binormalZ, angleFrom, angleTo);
    }

    @Deprecated
    public void cylinder(float width, float height, float depth, int divisions) {
        CylinderShapeBuilder.build(this, width, height, depth, divisions);
    }

    @Deprecated
    public void cylinder(float width, float height, float depth, int divisions, float angleFrom, float angleTo) {
        CylinderShapeBuilder.build(this, width, height, depth, divisions, angleFrom, angleTo);
    }

    @Deprecated
    public void cylinder(float width, float height, float depth, int divisions, float angleFrom, float angleTo, boolean close) {
        CylinderShapeBuilder.build(this, width, height, depth, divisions, angleFrom, angleTo, close);
    }

    @Deprecated
    public void cone(float width, float height, float depth, int divisions) {
        cone(width, height, depth, divisions, 0.0f, 360.0f);
    }

    @Deprecated
    public void cone(float width, float height, float depth, int divisions, float angleFrom, float angleTo) {
        ConeShapeBuilder.build(this, width, height, depth, divisions, angleFrom, angleTo);
    }

    @Deprecated
    public void sphere(float width, float height, float depth, int divisionsU, int divisionsV) {
        SphereShapeBuilder.build(this, width, height, depth, divisionsU, divisionsV);
    }

    @Deprecated
    public void sphere(Matrix4 transform, float width, float height, float depth, int divisionsU, int divisionsV) {
        SphereShapeBuilder.build(this, transform, width, height, depth, divisionsU, divisionsV);
    }

    @Deprecated
    public void sphere(float width, float height, float depth, int divisionsU, int divisionsV, float angleUFrom, float angleUTo, float angleVFrom, float angleVTo) {
        SphereShapeBuilder.build(this, width, height, depth, divisionsU, divisionsV, angleUFrom, angleUTo, angleVFrom, angleVTo);
    }

    @Deprecated
    public void sphere(Matrix4 transform, float width, float height, float depth, int divisionsU, int divisionsV, float angleUFrom, float angleUTo, float angleVFrom, float angleVTo) {
        SphereShapeBuilder.build(this, transform, width, height, depth, divisionsU, divisionsV, angleUFrom, angleUTo, angleVFrom, angleVTo);
    }

    @Deprecated
    public void capsule(float radius, float height, int divisions) {
        CapsuleShapeBuilder.build(this, radius, height, divisions);
    }

    @Deprecated
    public void arrow(float x1, float y1, float z1, float x2, float y2, float z2, float capLength, float stemThickness, int divisions) {
        ArrowShapeBuilder.build(this, x1, y1, z1, x2, y2, z2, capLength, stemThickness, divisions);
    }
}
