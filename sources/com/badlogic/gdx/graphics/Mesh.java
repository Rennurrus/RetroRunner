package com.badlogic.gdx.graphics;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.IndexArray;
import com.badlogic.gdx.graphics.glutils.IndexBufferObject;
import com.badlogic.gdx.graphics.glutils.IndexBufferObjectSubData;
import com.badlogic.gdx.graphics.glutils.IndexData;
import com.badlogic.gdx.graphics.glutils.InstanceBufferObject;
import com.badlogic.gdx.graphics.glutils.InstanceData;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.VertexArray;
import com.badlogic.gdx.graphics.glutils.VertexBufferObject;
import com.badlogic.gdx.graphics.glutils.VertexBufferObjectSubData;
import com.badlogic.gdx.graphics.glutils.VertexBufferObjectWithVAO;
import com.badlogic.gdx.graphics.glutils.VertexData;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;
import java.util.Map;

public class Mesh implements Disposable {
    static final Map<Application, Array<Mesh>> meshes = new HashMap();
    boolean autoBind;
    final IndexData indices;
    InstanceData instances;
    boolean isInstanced;
    final boolean isVertexArray;
    private final Vector3 tmpV;
    final VertexData vertices;

    public enum VertexDataType {
        VertexArray,
        VertexBufferObject,
        VertexBufferObjectSubData,
        VertexBufferObjectWithVAO
    }

    protected Mesh(VertexData vertices2, IndexData indices2, boolean isVertexArray2) {
        this.autoBind = true;
        this.isInstanced = false;
        this.tmpV = new Vector3();
        this.vertices = vertices2;
        this.indices = indices2;
        this.isVertexArray = isVertexArray2;
        addManagedMesh(Gdx.app, this);
    }

    public Mesh(boolean isStatic, int maxVertices, int maxIndices, VertexAttribute... attributes) {
        this.autoBind = true;
        this.isInstanced = false;
        this.tmpV = new Vector3();
        this.vertices = makeVertexBuffer(isStatic, maxVertices, new VertexAttributes(attributes));
        this.indices = new IndexBufferObject(isStatic, maxIndices);
        this.isVertexArray = false;
        addManagedMesh(Gdx.app, this);
    }

    public Mesh(boolean isStatic, int maxVertices, int maxIndices, VertexAttributes attributes) {
        this.autoBind = true;
        this.isInstanced = false;
        this.tmpV = new Vector3();
        this.vertices = makeVertexBuffer(isStatic, maxVertices, attributes);
        this.indices = new IndexBufferObject(isStatic, maxIndices);
        this.isVertexArray = false;
        addManagedMesh(Gdx.app, this);
    }

    public Mesh(boolean staticVertices, boolean staticIndices, int maxVertices, int maxIndices, VertexAttributes attributes) {
        this.autoBind = true;
        this.isInstanced = false;
        this.tmpV = new Vector3();
        this.vertices = makeVertexBuffer(staticVertices, maxVertices, attributes);
        this.indices = new IndexBufferObject(staticIndices, maxIndices);
        this.isVertexArray = false;
        addManagedMesh(Gdx.app, this);
    }

    private VertexData makeVertexBuffer(boolean isStatic, int maxVertices, VertexAttributes vertexAttributes) {
        if (Gdx.gl30 != null) {
            return new VertexBufferObjectWithVAO(isStatic, maxVertices, vertexAttributes);
        }
        return new VertexBufferObject(isStatic, maxVertices, vertexAttributes);
    }

    public Mesh(VertexDataType type, boolean isStatic, int maxVertices, int maxIndices, VertexAttribute... attributes) {
        this(type, isStatic, maxVertices, maxIndices, new VertexAttributes(attributes));
    }

    /* renamed from: com.badlogic.gdx.graphics.Mesh$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$badlogic$gdx$graphics$Mesh$VertexDataType = new int[VertexDataType.values().length];

        static {
            try {
                $SwitchMap$com$badlogic$gdx$graphics$Mesh$VertexDataType[VertexDataType.VertexBufferObject.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$badlogic$gdx$graphics$Mesh$VertexDataType[VertexDataType.VertexBufferObjectSubData.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$badlogic$gdx$graphics$Mesh$VertexDataType[VertexDataType.VertexBufferObjectWithVAO.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$badlogic$gdx$graphics$Mesh$VertexDataType[VertexDataType.VertexArray.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    public Mesh(VertexDataType type, boolean isStatic, int maxVertices, int maxIndices, VertexAttributes attributes) {
        this.autoBind = true;
        this.isInstanced = false;
        this.tmpV = new Vector3();
        int i = AnonymousClass1.$SwitchMap$com$badlogic$gdx$graphics$Mesh$VertexDataType[type.ordinal()];
        if (i == 1) {
            this.vertices = new VertexBufferObject(isStatic, maxVertices, attributes);
            this.indices = new IndexBufferObject(isStatic, maxIndices);
            this.isVertexArray = false;
        } else if (i == 2) {
            this.vertices = new VertexBufferObjectSubData(isStatic, maxVertices, attributes);
            this.indices = new IndexBufferObjectSubData(isStatic, maxIndices);
            this.isVertexArray = false;
        } else if (i != 3) {
            this.vertices = new VertexArray(maxVertices, attributes);
            this.indices = new IndexArray(maxIndices);
            this.isVertexArray = true;
        } else {
            this.vertices = new VertexBufferObjectWithVAO(isStatic, maxVertices, attributes);
            this.indices = new IndexBufferObjectSubData(isStatic, maxIndices);
            this.isVertexArray = false;
        }
        addManagedMesh(Gdx.app, this);
    }

    public Mesh enableInstancedRendering(boolean isStatic, int maxInstances, VertexAttribute... attributes) {
        if (!this.isInstanced) {
            this.isInstanced = true;
            this.instances = new InstanceBufferObject(isStatic, maxInstances, attributes);
            return this;
        }
        throw new GdxRuntimeException("Trying to enable InstancedRendering on same Mesh instance twice. Use disableInstancedRendering to clean up old InstanceData first");
    }

    public Mesh disableInstancedRendering() {
        if (this.isInstanced) {
            this.isInstanced = false;
            this.instances.dispose();
            this.instances = null;
        }
        return this;
    }

    public Mesh setInstanceData(float[] instanceData, int offset, int count) {
        InstanceData instanceData2 = this.instances;
        if (instanceData2 != null) {
            instanceData2.setInstanceData(instanceData, offset, count);
            return this;
        }
        throw new GdxRuntimeException("An InstanceBufferObject must be set before setting instance data!");
    }

    public Mesh setInstanceData(float[] instanceData) {
        InstanceData instanceData2 = this.instances;
        if (instanceData2 != null) {
            instanceData2.setInstanceData(instanceData, 0, instanceData.length);
            return this;
        }
        throw new GdxRuntimeException("An InstanceBufferObject must be set before setting instance data!");
    }

    public Mesh setInstanceData(FloatBuffer instanceData, int count) {
        InstanceData instanceData2 = this.instances;
        if (instanceData2 != null) {
            instanceData2.setInstanceData(instanceData, count);
            return this;
        }
        throw new GdxRuntimeException("An InstanceBufferObject must be set before setting instance data!");
    }

    public Mesh setInstanceData(FloatBuffer instanceData) {
        InstanceData instanceData2 = this.instances;
        if (instanceData2 != null) {
            instanceData2.setInstanceData(instanceData, instanceData.limit());
            return this;
        }
        throw new GdxRuntimeException("An InstanceBufferObject must be set before setting instance data!");
    }

    public Mesh updateInstanceData(int targetOffset, float[] source) {
        return updateInstanceData(targetOffset, source, 0, source.length);
    }

    public Mesh updateInstanceData(int targetOffset, float[] source, int sourceOffset, int count) {
        this.instances.updateInstanceData(targetOffset, source, sourceOffset, count);
        return this;
    }

    public Mesh updateInstanceData(int targetOffset, FloatBuffer source) {
        return updateInstanceData(targetOffset, source, 0, source.limit());
    }

    public Mesh updateInstanceData(int targetOffset, FloatBuffer source, int sourceOffset, int count) {
        this.instances.updateInstanceData(targetOffset, source, sourceOffset, count);
        return this;
    }

    public Mesh setVertices(float[] vertices2) {
        this.vertices.setVertices(vertices2, 0, vertices2.length);
        return this;
    }

    public Mesh setVertices(float[] vertices2, int offset, int count) {
        this.vertices.setVertices(vertices2, offset, count);
        return this;
    }

    public Mesh updateVertices(int targetOffset, float[] source) {
        return updateVertices(targetOffset, source, 0, source.length);
    }

    public Mesh updateVertices(int targetOffset, float[] source, int sourceOffset, int count) {
        this.vertices.updateVertices(targetOffset, source, sourceOffset, count);
        return this;
    }

    public float[] getVertices(float[] vertices2) {
        return getVertices(0, -1, vertices2);
    }

    public float[] getVertices(int srcOffset, float[] vertices2) {
        return getVertices(srcOffset, -1, vertices2);
    }

    public float[] getVertices(int srcOffset, int count, float[] vertices2) {
        return getVertices(srcOffset, count, vertices2, 0);
    }

    public float[] getVertices(int srcOffset, int count, float[] vertices2, int destOffset) {
        int max = (getNumVertices() * getVertexSize()) / 4;
        if (count == -1 && (count = max - srcOffset) > vertices2.length - destOffset) {
            count = vertices2.length - destOffset;
        }
        if (srcOffset < 0 || count <= 0 || srcOffset + count > max || destOffset < 0 || destOffset >= vertices2.length) {
            throw new IndexOutOfBoundsException();
        } else if (vertices2.length - destOffset >= count) {
            int pos = getVerticesBuffer().position();
            getVerticesBuffer().position(srcOffset);
            getVerticesBuffer().get(vertices2, destOffset, count);
            getVerticesBuffer().position(pos);
            return vertices2;
        } else {
            throw new IllegalArgumentException("not enough room in vertices array, has " + vertices2.length + " floats, needs " + count);
        }
    }

    public Mesh setIndices(short[] indices2) {
        this.indices.setIndices(indices2, 0, indices2.length);
        return this;
    }

    public Mesh setIndices(short[] indices2, int offset, int count) {
        this.indices.setIndices(indices2, offset, count);
        return this;
    }

    public void getIndices(short[] indices2) {
        getIndices(indices2, 0);
    }

    public void getIndices(short[] indices2, int destOffset) {
        getIndices(0, indices2, destOffset);
    }

    public void getIndices(int srcOffset, short[] indices2, int destOffset) {
        getIndices(srcOffset, -1, indices2, destOffset);
    }

    public void getIndices(int srcOffset, int count, short[] indices2, int destOffset) {
        int max = getNumIndices();
        if (count < 0) {
            count = max - srcOffset;
        }
        if (srcOffset < 0 || srcOffset >= max || srcOffset + count > max) {
            throw new IllegalArgumentException("Invalid range specified, offset: " + srcOffset + ", count: " + count + ", max: " + max);
        } else if (indices2.length - destOffset >= count) {
            int pos = getIndicesBuffer().position();
            getIndicesBuffer().position(srcOffset);
            getIndicesBuffer().get(indices2, destOffset, count);
            getIndicesBuffer().position(pos);
        } else {
            throw new IllegalArgumentException("not enough room in indices array, has " + indices2.length + " shorts, needs " + count);
        }
    }

    public int getNumIndices() {
        return this.indices.getNumIndices();
    }

    public int getNumVertices() {
        return this.vertices.getNumVertices();
    }

    public int getMaxVertices() {
        return this.vertices.getNumMaxVertices();
    }

    public int getMaxIndices() {
        return this.indices.getNumMaxIndices();
    }

    public int getVertexSize() {
        return this.vertices.getAttributes().vertexSize;
    }

    public void setAutoBind(boolean autoBind2) {
        this.autoBind = autoBind2;
    }

    public void bind(ShaderProgram shader) {
        bind(shader, (int[]) null);
    }

    public void bind(ShaderProgram shader, int[] locations) {
        this.vertices.bind(shader, locations);
        InstanceData instanceData = this.instances;
        if (instanceData != null && instanceData.getNumInstances() > 0) {
            this.instances.bind(shader, locations);
        }
        if (this.indices.getNumIndices() > 0) {
            this.indices.bind();
        }
    }

    public void unbind(ShaderProgram shader) {
        unbind(shader, (int[]) null);
    }

    public void unbind(ShaderProgram shader, int[] locations) {
        this.vertices.unbind(shader, locations);
        InstanceData instanceData = this.instances;
        if (instanceData != null && instanceData.getNumInstances() > 0) {
            this.instances.unbind(shader, locations);
        }
        if (this.indices.getNumIndices() > 0) {
            this.indices.unbind();
        }
    }

    public void render(ShaderProgram shader, int primitiveType) {
        render(shader, primitiveType, 0, this.indices.getNumMaxIndices() > 0 ? getNumIndices() : getNumVertices(), this.autoBind);
    }

    public void render(ShaderProgram shader, int primitiveType, int offset, int count) {
        render(shader, primitiveType, offset, count, this.autoBind);
    }

    public void render(ShaderProgram shader, int primitiveType, int offset, int count, boolean autoBind2) {
        if (count != 0) {
            if (autoBind2) {
                bind(shader);
            }
            if (!this.isVertexArray) {
                int numInstances = 0;
                if (this.isInstanced) {
                    numInstances = this.instances.getNumInstances();
                }
                if (this.indices.getNumIndices() > 0) {
                    if (count + offset > this.indices.getNumMaxIndices()) {
                        throw new GdxRuntimeException("Mesh attempting to access memory outside of the index buffer (count: " + count + ", offset: " + offset + ", max: " + this.indices.getNumMaxIndices() + ")");
                    } else if (!this.isInstanced || numInstances <= 0) {
                        Gdx.gl20.glDrawElements(primitiveType, count, (int) GL20.GL_UNSIGNED_SHORT, offset * 2);
                    } else {
                        Gdx.gl30.glDrawElementsInstanced(primitiveType, count, GL20.GL_UNSIGNED_SHORT, offset * 2, numInstances);
                    }
                } else if (!this.isInstanced || numInstances <= 0) {
                    Gdx.gl20.glDrawArrays(primitiveType, offset, count);
                } else {
                    Gdx.gl30.glDrawArraysInstanced(primitiveType, offset, count, numInstances);
                }
            } else if (this.indices.getNumIndices() > 0) {
                ShortBuffer buffer = this.indices.getBuffer();
                int oldPosition = buffer.position();
                int oldLimit = buffer.limit();
                buffer.position(offset);
                buffer.limit(offset + count);
                Gdx.gl20.glDrawElements(primitiveType, count, (int) GL20.GL_UNSIGNED_SHORT, (Buffer) buffer);
                buffer.position(oldPosition);
                buffer.limit(oldLimit);
            } else {
                Gdx.gl20.glDrawArrays(primitiveType, offset, count);
            }
            if (autoBind2) {
                unbind(shader);
            }
        }
    }

    public void dispose() {
        if (meshes.get(Gdx.app) != null) {
            meshes.get(Gdx.app).removeValue(this, true);
        }
        this.vertices.dispose();
        InstanceData instanceData = this.instances;
        if (instanceData != null) {
            instanceData.dispose();
        }
        this.indices.dispose();
    }

    public VertexAttribute getVertexAttribute(int usage) {
        VertexAttributes attributes = this.vertices.getAttributes();
        int len = attributes.size();
        for (int i = 0; i < len; i++) {
            if (attributes.get(i).usage == usage) {
                return attributes.get(i);
            }
        }
        return null;
    }

    public VertexAttributes getVertexAttributes() {
        return this.vertices.getAttributes();
    }

    public FloatBuffer getVerticesBuffer() {
        return this.vertices.getBuffer();
    }

    public BoundingBox calculateBoundingBox() {
        BoundingBox bbox = new BoundingBox();
        calculateBoundingBox(bbox);
        return bbox;
    }

    public void calculateBoundingBox(BoundingBox bbox) {
        int numVertices = getNumVertices();
        if (numVertices != 0) {
            FloatBuffer verts = this.vertices.getBuffer();
            bbox.inf();
            VertexAttribute posAttrib = getVertexAttribute(1);
            int vertexSize = this.vertices.getAttributes().vertexSize / 4;
            int idx = posAttrib.offset / 4;
            int i = posAttrib.numComponents;
            if (i == 1) {
                for (int i2 = 0; i2 < numVertices; i2++) {
                    bbox.ext(verts.get(idx), 0.0f, 0.0f);
                    idx += vertexSize;
                }
            } else if (i == 2) {
                for (int i3 = 0; i3 < numVertices; i3++) {
                    bbox.ext(verts.get(idx), verts.get(idx + 1), 0.0f);
                    idx += vertexSize;
                }
            } else if (i == 3) {
                for (int i4 = 0; i4 < numVertices; i4++) {
                    bbox.ext(verts.get(idx), verts.get(idx + 1), verts.get(idx + 2));
                    idx += vertexSize;
                }
            }
        } else {
            throw new GdxRuntimeException("No vertices defined");
        }
    }

    public BoundingBox calculateBoundingBox(BoundingBox out, int offset, int count) {
        return extendBoundingBox(out.inf(), offset, count);
    }

    public BoundingBox calculateBoundingBox(BoundingBox out, int offset, int count, Matrix4 transform) {
        return extendBoundingBox(out.inf(), offset, count, transform);
    }

    public BoundingBox extendBoundingBox(BoundingBox out, int offset, int count) {
        return extendBoundingBox(out, offset, count, (Matrix4) null);
    }

    public BoundingBox extendBoundingBox(BoundingBox out, int offset, int count, Matrix4 transform) {
        BoundingBox boundingBox = out;
        int i = offset;
        int i2 = count;
        Matrix4 matrix4 = transform;
        int numIndices = getNumIndices();
        int numVertices = getNumVertices();
        int max = numIndices == 0 ? numVertices : numIndices;
        if (i < 0 || i2 < 1 || i + i2 > max) {
            throw new GdxRuntimeException("Invalid part specified ( offset=" + i + ", count=" + count + ", max=" + max + " )");
        }
        FloatBuffer verts = this.vertices.getBuffer();
        ShortBuffer index = this.indices.getBuffer();
        VertexAttribute posAttrib = getVertexAttribute(1);
        int posoff = posAttrib.offset / 4;
        int vertexSize = this.vertices.getAttributes().vertexSize / 4;
        int end = i + i2;
        int i3 = posAttrib.numComponents;
        int i4 = numVertices;
        if (i3 == 1) {
            VertexAttribute vertexAttribute = posAttrib;
            if (numIndices > 0) {
                for (int i5 = offset; i5 < end; i5++) {
                    this.tmpV.set(verts.get((index.get(i5) * vertexSize) + posoff), 0.0f, 0.0f);
                    if (matrix4 != null) {
                        this.tmpV.mul(matrix4);
                    }
                    boundingBox.ext(this.tmpV);
                }
            } else {
                for (int i6 = offset; i6 < end; i6++) {
                    this.tmpV.set(verts.get((i6 * vertexSize) + posoff), 0.0f, 0.0f);
                    if (matrix4 != null) {
                        this.tmpV.mul(matrix4);
                    }
                    boundingBox.ext(this.tmpV);
                }
            }
        } else if (i3 == 2) {
            VertexAttribute vertexAttribute2 = posAttrib;
            if (numIndices > 0) {
                for (int i7 = offset; i7 < end; i7++) {
                    int idx = (index.get(i7) * vertexSize) + posoff;
                    this.tmpV.set(verts.get(idx), verts.get(idx + 1), 0.0f);
                    if (matrix4 != null) {
                        this.tmpV.mul(matrix4);
                    }
                    boundingBox.ext(this.tmpV);
                }
            } else {
                for (int i8 = offset; i8 < end; i8++) {
                    int idx2 = (i8 * vertexSize) + posoff;
                    this.tmpV.set(verts.get(idx2), verts.get(idx2 + 1), 0.0f);
                    if (matrix4 != null) {
                        this.tmpV.mul(matrix4);
                    }
                    boundingBox.ext(this.tmpV);
                }
            }
        } else if (i3 != 3) {
            int i9 = max;
            VertexAttribute vertexAttribute3 = posAttrib;
        } else if (numIndices > 0) {
            int i10 = offset;
            while (i10 < end) {
                int idx3 = (index.get(i10) * vertexSize) + posoff;
                VertexAttribute posAttrib2 = posAttrib;
                int max2 = max;
                this.tmpV.set(verts.get(idx3), verts.get(idx3 + 1), verts.get(idx3 + 2));
                if (matrix4 != null) {
                    this.tmpV.mul(matrix4);
                }
                boundingBox.ext(this.tmpV);
                i10++;
                int i11 = count;
                posAttrib = posAttrib2;
                max = max2;
            }
            VertexAttribute vertexAttribute4 = posAttrib;
        } else {
            VertexAttribute vertexAttribute5 = posAttrib;
            for (int i12 = offset; i12 < end; i12++) {
                int idx4 = (i12 * vertexSize) + posoff;
                this.tmpV.set(verts.get(idx4), verts.get(idx4 + 1), verts.get(idx4 + 2));
                if (matrix4 != null) {
                    this.tmpV.mul(matrix4);
                }
                boundingBox.ext(this.tmpV);
            }
        }
        return boundingBox;
    }

    public float calculateRadiusSquared(float centerX, float centerY, float centerZ, int offset, int count, Matrix4 transform) {
        float f = centerX;
        float f2 = centerY;
        float f3 = centerZ;
        int i = count;
        Matrix4 matrix4 = transform;
        int numIndices = getNumIndices();
        if (offset < 0 || i < 1 || offset + i > numIndices) {
            throw new GdxRuntimeException("Not enough indices");
        }
        FloatBuffer verts = this.vertices.getBuffer();
        ShortBuffer index = this.indices.getBuffer();
        VertexAttribute posAttrib = getVertexAttribute(1);
        int posoff = posAttrib.offset / 4;
        int vertexSize = this.vertices.getAttributes().vertexSize / 4;
        int end = offset + i;
        float result = 0.0f;
        int i2 = posAttrib.numComponents;
        if (i2 == 1) {
            VertexAttribute vertexAttribute = posAttrib;
            int posoff2 = posoff;
            for (int i3 = offset; i3 < end; i3++) {
                this.tmpV.set(verts.get((index.get(i3) * vertexSize) + posoff2), 0.0f, 0.0f);
                if (matrix4 != null) {
                    this.tmpV.mul(matrix4);
                }
                float r = this.tmpV.sub(f, f2, f3).len2();
                if (r > result) {
                    result = r;
                }
            }
        } else if (i2 == 2) {
            VertexAttribute vertexAttribute2 = posAttrib;
            int posoff3 = posoff;
            for (int i4 = offset; i4 < end; i4++) {
                int idx = (index.get(i4) * vertexSize) + posoff3;
                this.tmpV.set(verts.get(idx), verts.get(idx + 1), 0.0f);
                if (matrix4 != null) {
                    this.tmpV.mul(matrix4);
                }
                float r2 = this.tmpV.sub(f, f2, f3).len2();
                if (r2 > result) {
                    result = r2;
                }
            }
        } else if (i2 != 3) {
            int i5 = numIndices;
            VertexAttribute vertexAttribute3 = posAttrib;
            int i6 = posoff;
        } else {
            int i7 = offset;
            while (i7 < end) {
                int idx2 = (index.get(i7) * vertexSize) + posoff;
                int numIndices2 = numIndices;
                VertexAttribute posAttrib2 = posAttrib;
                int posoff4 = posoff;
                this.tmpV.set(verts.get(idx2), verts.get(idx2 + 1), verts.get(idx2 + 2));
                if (matrix4 != null) {
                    this.tmpV.mul(matrix4);
                }
                float r3 = this.tmpV.sub(f, f2, f3).len2();
                if (r3 > result) {
                    result = r3;
                }
                i7++;
                numIndices = numIndices2;
                posAttrib = posAttrib2;
                posoff = posoff4;
            }
            VertexAttribute vertexAttribute4 = posAttrib;
            int i8 = posoff;
        }
        return result;
    }

    public float calculateRadius(float centerX, float centerY, float centerZ, int offset, int count, Matrix4 transform) {
        return (float) Math.sqrt((double) calculateRadiusSquared(centerX, centerY, centerZ, offset, count, transform));
    }

    public float calculateRadius(Vector3 center, int offset, int count, Matrix4 transform) {
        return calculateRadius(center.x, center.y, center.z, offset, count, transform);
    }

    public float calculateRadius(float centerX, float centerY, float centerZ, int offset, int count) {
        return calculateRadius(centerX, centerY, centerZ, offset, count, (Matrix4) null);
    }

    public float calculateRadius(Vector3 center, int offset, int count) {
        return calculateRadius(center.x, center.y, center.z, offset, count, (Matrix4) null);
    }

    public float calculateRadius(float centerX, float centerY, float centerZ) {
        return calculateRadius(centerX, centerY, centerZ, 0, getNumIndices(), (Matrix4) null);
    }

    public float calculateRadius(Vector3 center) {
        return calculateRadius(center.x, center.y, center.z, 0, getNumIndices(), (Matrix4) null);
    }

    public ShortBuffer getIndicesBuffer() {
        return this.indices.getBuffer();
    }

    private static void addManagedMesh(Application app, Mesh mesh) {
        Array<Mesh> managedResources = meshes.get(app);
        if (managedResources == null) {
            managedResources = new Array<>();
        }
        managedResources.add(mesh);
        meshes.put(app, managedResources);
    }

    public static void invalidateAllMeshes(Application app) {
        Array<Mesh> meshesArray = meshes.get(app);
        if (meshesArray != null) {
            for (int i = 0; i < meshesArray.size; i++) {
                meshesArray.get(i).vertices.invalidate();
                meshesArray.get(i).indices.invalidate();
            }
        }
    }

    public static void clearAllMeshes(Application app) {
        meshes.remove(app);
    }

    public static String getManagedStatus() {
        StringBuilder builder = new StringBuilder();
        builder.append("Managed meshes/app: { ");
        for (Application app : meshes.keySet()) {
            builder.append(meshes.get(app).size);
            builder.append(" ");
        }
        builder.append("}");
        return builder.toString();
    }

    public void scale(float scaleX, float scaleY, float scaleZ) {
        VertexAttribute posAttr = getVertexAttribute(1);
        int numComponents = posAttr.numComponents;
        int numVertices = getNumVertices();
        int vertexSize = getVertexSize() / 4;
        float[] vertices2 = new float[(numVertices * vertexSize)];
        getVertices(vertices2);
        int idx = posAttr.offset / 4;
        if (numComponents == 1) {
            for (int i = 0; i < numVertices; i++) {
                vertices2[idx] = vertices2[idx] * scaleX;
                idx += vertexSize;
            }
        } else if (numComponents == 2) {
            for (int i2 = 0; i2 < numVertices; i2++) {
                vertices2[idx] = vertices2[idx] * scaleX;
                int i3 = idx + 1;
                vertices2[i3] = vertices2[i3] * scaleY;
                idx += vertexSize;
            }
        } else if (numComponents == 3) {
            for (int i4 = 0; i4 < numVertices; i4++) {
                vertices2[idx] = vertices2[idx] * scaleX;
                int i5 = idx + 1;
                vertices2[i5] = vertices2[i5] * scaleY;
                int i6 = idx + 2;
                vertices2[i6] = vertices2[i6] * scaleZ;
                idx += vertexSize;
            }
        }
        setVertices(vertices2);
    }

    public void transform(Matrix4 matrix) {
        transform(matrix, 0, getNumVertices());
    }

    public void transform(Matrix4 matrix, int start, int count) {
        VertexAttribute posAttr = getVertexAttribute(1);
        int posOffset = posAttr.offset / 4;
        int stride = getVertexSize() / 4;
        int numComponents = posAttr.numComponents;
        int numVertices = getNumVertices();
        float[] vertices2 = new float[(count * stride)];
        getVertices(start * stride, count * stride, vertices2);
        transform(matrix, vertices2, stride, posOffset, numComponents, 0, count);
        updateVertices(start * stride, vertices2);
    }

    public static void transform(Matrix4 matrix, float[] vertices2, int vertexSize, int offset, int dimensions, int start, int count) {
        if (offset < 0 || dimensions < 1 || offset + dimensions > vertexSize) {
            throw new IndexOutOfBoundsException();
        } else if (start < 0 || count < 1 || (start + count) * vertexSize > vertices2.length) {
            throw new IndexOutOfBoundsException("start = " + start + ", count = " + count + ", vertexSize = " + vertexSize + ", length = " + vertices2.length);
        } else {
            Vector3 tmp = new Vector3();
            int idx = (start * vertexSize) + offset;
            if (dimensions == 1) {
                for (int i = 0; i < count; i++) {
                    tmp.set(vertices2[idx], 0.0f, 0.0f).mul(matrix);
                    vertices2[idx] = tmp.x;
                    idx += vertexSize;
                }
            } else if (dimensions == 2) {
                for (int i2 = 0; i2 < count; i2++) {
                    tmp.set(vertices2[idx], vertices2[idx + 1], 0.0f).mul(matrix);
                    vertices2[idx] = tmp.x;
                    vertices2[idx + 1] = tmp.y;
                    idx += vertexSize;
                }
            } else if (dimensions == 3) {
                for (int i3 = 0; i3 < count; i3++) {
                    tmp.set(vertices2[idx], vertices2[idx + 1], vertices2[idx + 2]).mul(matrix);
                    vertices2[idx] = tmp.x;
                    vertices2[idx + 1] = tmp.y;
                    vertices2[idx + 2] = tmp.z;
                    idx += vertexSize;
                }
            }
        }
    }

    public void transformUV(Matrix3 matrix) {
        transformUV(matrix, 0, getNumVertices());
    }

    /* access modifiers changed from: protected */
    public void transformUV(Matrix3 matrix, int start, int count) {
        int vertexSize = getVertexSize() / 4;
        float[] vertices2 = new float[(getNumVertices() * vertexSize)];
        getVertices(0, vertices2.length, vertices2);
        transformUV(matrix, vertices2, vertexSize, getVertexAttribute(16).offset / 4, start, count);
        setVertices(vertices2, 0, vertices2.length);
    }

    public static void transformUV(Matrix3 matrix, float[] vertices2, int vertexSize, int offset, int start, int count) {
        if (start < 0 || count < 1 || (start + count) * vertexSize > vertices2.length) {
            throw new IndexOutOfBoundsException("start = " + start + ", count = " + count + ", vertexSize = " + vertexSize + ", length = " + vertices2.length);
        }
        Vector2 tmp = new Vector2();
        int idx = (start * vertexSize) + offset;
        for (int i = 0; i < count; i++) {
            tmp.set(vertices2[idx], vertices2[idx + 1]).mul(matrix);
            vertices2[idx] = tmp.x;
            vertices2[idx + 1] = tmp.y;
            idx += vertexSize;
        }
    }

    public Mesh copy(boolean isStatic, boolean removeDuplicates, int[] usage) {
        Mesh result;
        int vertexSize;
        boolean z = isStatic;
        int[] iArr = usage;
        int vertexSize2 = getVertexSize() / 4;
        int numVertices = getNumVertices();
        float[] vertices2 = new float[(numVertices * vertexSize2)];
        getVertices(0, vertices2.length, vertices2);
        short[] checks = null;
        VertexAttribute[] attrs = null;
        short newVertexSize = 0;
        if (iArr != null) {
            int size = 0;
            int as = 0;
            for (int i = 0; i < iArr.length; i++) {
                if (getVertexAttribute(iArr[i]) != null) {
                    size += getVertexAttribute(iArr[i]).numComponents;
                    as++;
                }
            }
            if (size > 0) {
                attrs = new VertexAttribute[as];
                checks = new short[size];
                int idx = -1;
                int ai = -1;
                for (int vertexAttribute : iArr) {
                    VertexAttribute a = getVertexAttribute(vertexAttribute);
                    if (a != null) {
                        int idx2 = idx;
                        for (int j = 0; j < a.numComponents; j++) {
                            idx2++;
                            checks[idx2] = (short) (a.offset + j);
                        }
                        ai++;
                        attrs[ai] = a.copy();
                        newVertexSize += a.numComponents;
                        idx = idx2;
                    }
                }
            }
        }
        if (checks == null) {
            checks = new short[vertexSize2];
            for (short i2 = 0; i2 < vertexSize2; i2 = (short) (i2 + 1)) {
                checks[i2] = i2;
            }
            newVertexSize = vertexSize2;
        }
        int numIndices = getNumIndices();
        short[] indices2 = null;
        if (numIndices > 0) {
            indices2 = new short[numIndices];
            getIndices(indices2);
            if (removeDuplicates || newVertexSize != vertexSize2) {
                float[] tmp = new float[vertices2.length];
                int size2 = 0;
                int i3 = 0;
                while (i3 < numIndices) {
                    int idx1 = indices2[i3] * vertexSize2;
                    short j2 = -1;
                    if (removeDuplicates) {
                        short newIndex = -1;
                        short j3 = 0;
                        while (j3 < size2 && newIndex < 0) {
                            int idx22 = j3 * newVertexSize;
                            boolean found = true;
                            int k = 0;
                            while (k < checks.length && found) {
                                if (tmp[idx22 + k] != vertices2[idx1 + checks[k]]) {
                                    found = false;
                                }
                                k++;
                                int[] iArr2 = usage;
                            }
                            if (found) {
                                newIndex = j3;
                            }
                            j3 = (short) (j3 + 1);
                            int[] iArr3 = usage;
                        }
                        j2 = newIndex;
                    }
                    if (j2 > 0) {
                        indices2[i3] = j2;
                        vertexSize = vertexSize2;
                    } else {
                        int idx3 = size2 * newVertexSize;
                        int j4 = 0;
                        while (true) {
                            vertexSize = vertexSize2;
                            if (j4 >= checks.length) {
                                break;
                            }
                            tmp[idx3 + j4] = vertices2[idx1 + checks[j4]];
                            j4++;
                            vertexSize2 = vertexSize;
                        }
                        indices2[i3] = (short) size2;
                        size2++;
                    }
                    i3++;
                    int[] iArr4 = usage;
                    vertexSize2 = vertexSize;
                }
                vertices2 = tmp;
                numVertices = size2;
            } else {
                int i4 = vertexSize2;
            }
        }
        if (attrs == null) {
            result = new Mesh(z, numVertices, indices2 == null ? 0 : indices2.length, getVertexAttributes());
        } else {
            result = new Mesh(z, numVertices, indices2 == null ? 0 : indices2.length, attrs);
        }
        result.setVertices(vertices2, 0, numVertices * newVertexSize);
        if (indices2 != null) {
            result.setIndices(indices2);
        }
        return result;
    }

    public Mesh copy(boolean isStatic) {
        return copy(isStatic, false, (int[]) null);
    }
}
