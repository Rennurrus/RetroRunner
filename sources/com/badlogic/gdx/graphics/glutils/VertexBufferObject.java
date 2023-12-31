package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class VertexBufferObject implements VertexData {
    private VertexAttributes attributes;
    private FloatBuffer buffer;
    private int bufferHandle;
    private ByteBuffer byteBuffer;
    boolean isBound;
    boolean isDirty;
    private boolean ownsBuffer;
    private int usage;

    public VertexBufferObject(boolean isStatic, int numVertices, VertexAttribute... attributes2) {
        this(isStatic, numVertices, new VertexAttributes(attributes2));
    }

    public VertexBufferObject(boolean isStatic, int numVertices, VertexAttributes attributes2) {
        this.isDirty = false;
        this.isBound = false;
        this.bufferHandle = Gdx.gl20.glGenBuffer();
        ByteBuffer data = BufferUtils.newUnsafeByteBuffer(attributes2.vertexSize * numVertices);
        data.limit(0);
        setBuffer(data, true, attributes2);
        setUsage(isStatic ? GL20.GL_STATIC_DRAW : GL20.GL_DYNAMIC_DRAW);
    }

    protected VertexBufferObject(int usage2, ByteBuffer data, boolean ownsBuffer2, VertexAttributes attributes2) {
        this.isDirty = false;
        this.isBound = false;
        this.bufferHandle = Gdx.gl20.glGenBuffer();
        setBuffer(data, ownsBuffer2, attributes2);
        setUsage(usage2);
    }

    public VertexAttributes getAttributes() {
        return this.attributes;
    }

    public int getNumVertices() {
        return (this.buffer.limit() * 4) / this.attributes.vertexSize;
    }

    public int getNumMaxVertices() {
        return this.byteBuffer.capacity() / this.attributes.vertexSize;
    }

    public FloatBuffer getBuffer() {
        this.isDirty = true;
        return this.buffer;
    }

    /* access modifiers changed from: protected */
    public void setBuffer(Buffer data, boolean ownsBuffer2, VertexAttributes value) {
        ByteBuffer byteBuffer2;
        if (!this.isBound) {
            if (this.ownsBuffer && (byteBuffer2 = this.byteBuffer) != null) {
                BufferUtils.disposeUnsafeByteBuffer(byteBuffer2);
            }
            this.attributes = value;
            if (data instanceof ByteBuffer) {
                this.byteBuffer = (ByteBuffer) data;
                this.ownsBuffer = ownsBuffer2;
                int l = this.byteBuffer.limit();
                ByteBuffer byteBuffer3 = this.byteBuffer;
                byteBuffer3.limit(byteBuffer3.capacity());
                this.buffer = this.byteBuffer.asFloatBuffer();
                this.byteBuffer.limit(l);
                this.buffer.limit(l / 4);
                return;
            }
            throw new GdxRuntimeException("Only ByteBuffer is currently supported");
        }
        throw new GdxRuntimeException("Cannot change attributes while VBO is bound");
    }

    private void bufferChanged() {
        if (this.isBound) {
            Gdx.gl20.glBufferData(GL20.GL_ARRAY_BUFFER, this.byteBuffer.limit(), this.byteBuffer, this.usage);
            this.isDirty = false;
        }
    }

    public void setVertices(float[] vertices, int offset, int count) {
        this.isDirty = true;
        BufferUtils.copy(vertices, (Buffer) this.byteBuffer, count, offset);
        this.buffer.position(0);
        this.buffer.limit(count);
        bufferChanged();
    }

    public void updateVertices(int targetOffset, float[] vertices, int sourceOffset, int count) {
        this.isDirty = true;
        int pos = this.byteBuffer.position();
        this.byteBuffer.position(targetOffset * 4);
        BufferUtils.copy(vertices, sourceOffset, count, (Buffer) this.byteBuffer);
        this.byteBuffer.position(pos);
        this.buffer.position(0);
        bufferChanged();
    }

    /* access modifiers changed from: protected */
    public int getUsage() {
        return this.usage;
    }

    /* access modifiers changed from: protected */
    public void setUsage(int value) {
        if (!this.isBound) {
            this.usage = value;
            return;
        }
        throw new GdxRuntimeException("Cannot change usage while VBO is bound");
    }

    public void bind(ShaderProgram shader) {
        bind(shader, (int[]) null);
    }

    public void bind(ShaderProgram shader, int[] locations) {
        GL20 gl = Gdx.gl20;
        gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, this.bufferHandle);
        if (this.isDirty) {
            this.byteBuffer.limit(this.buffer.limit() * 4);
            gl.glBufferData(GL20.GL_ARRAY_BUFFER, this.byteBuffer.limit(), this.byteBuffer, this.usage);
            this.isDirty = false;
        }
        int numAttributes = this.attributes.size();
        if (locations == null) {
            for (int i = 0; i < numAttributes; i++) {
                VertexAttribute attribute = this.attributes.get(i);
                int location = shader.getAttributeLocation(attribute.alias);
                if (location >= 0) {
                    shader.enableVertexAttribute(location);
                    shader.setVertexAttribute(location, attribute.numComponents, attribute.type, attribute.normalized, this.attributes.vertexSize, attribute.offset);
                }
            }
        } else {
            for (int i2 = 0; i2 < numAttributes; i2++) {
                VertexAttribute attribute2 = this.attributes.get(i2);
                int location2 = locations[i2];
                if (location2 >= 0) {
                    shader.enableVertexAttribute(location2);
                    shader.setVertexAttribute(location2, attribute2.numComponents, attribute2.type, attribute2.normalized, this.attributes.vertexSize, attribute2.offset);
                }
            }
        }
        this.isBound = true;
    }

    public void unbind(ShaderProgram shader) {
        unbind(shader, (int[]) null);
    }

    public void unbind(ShaderProgram shader, int[] locations) {
        GL20 gl = Gdx.gl20;
        int numAttributes = this.attributes.size();
        if (locations == null) {
            for (int i = 0; i < numAttributes; i++) {
                shader.disableVertexAttribute(this.attributes.get(i).alias);
            }
        } else {
            for (int i2 = 0; i2 < numAttributes; i2++) {
                int location = locations[i2];
                if (location >= 0) {
                    shader.disableVertexAttribute(location);
                }
            }
        }
        gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, 0);
        this.isBound = false;
    }

    public void invalidate() {
        this.bufferHandle = Gdx.gl20.glGenBuffer();
        this.isDirty = true;
    }

    public void dispose() {
        GL20 gl = Gdx.gl20;
        gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, 0);
        gl.glDeleteBuffer(this.bufferHandle);
        this.bufferHandle = 0;
        if (this.ownsBuffer) {
            BufferUtils.disposeUnsafeByteBuffer(this.byteBuffer);
        }
    }
}
