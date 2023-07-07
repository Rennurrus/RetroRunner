package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.IntArray;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class VertexBufferObjectWithVAO implements VertexData {
    static final IntBuffer tmpHandle = BufferUtils.newIntBuffer(1);
    final VertexAttributes attributes;
    final FloatBuffer buffer;
    int bufferHandle;
    final ByteBuffer byteBuffer;
    IntArray cachedLocations;
    boolean isBound;
    boolean isDirty;
    final boolean isStatic;
    final boolean ownsBuffer;
    final int usage;
    int vaoHandle;

    public VertexBufferObjectWithVAO(boolean isStatic2, int numVertices, VertexAttribute... attributes2) {
        this(isStatic2, numVertices, new VertexAttributes(attributes2));
    }

    public VertexBufferObjectWithVAO(boolean isStatic2, int numVertices, VertexAttributes attributes2) {
        this.isDirty = false;
        this.isBound = false;
        this.vaoHandle = -1;
        this.cachedLocations = new IntArray();
        this.isStatic = isStatic2;
        this.attributes = attributes2;
        this.byteBuffer = BufferUtils.newUnsafeByteBuffer(this.attributes.vertexSize * numVertices);
        this.buffer = this.byteBuffer.asFloatBuffer();
        this.ownsBuffer = true;
        this.buffer.flip();
        this.byteBuffer.flip();
        this.bufferHandle = Gdx.gl20.glGenBuffer();
        this.usage = isStatic2 ? GL20.GL_STATIC_DRAW : GL20.GL_DYNAMIC_DRAW;
        createVAO();
    }

    public VertexBufferObjectWithVAO(boolean isStatic2, ByteBuffer unmanagedBuffer, VertexAttributes attributes2) {
        this.isDirty = false;
        this.isBound = false;
        this.vaoHandle = -1;
        this.cachedLocations = new IntArray();
        this.isStatic = isStatic2;
        this.attributes = attributes2;
        this.byteBuffer = unmanagedBuffer;
        this.ownsBuffer = false;
        this.buffer = this.byteBuffer.asFloatBuffer();
        this.buffer.flip();
        this.byteBuffer.flip();
        this.bufferHandle = Gdx.gl20.glGenBuffer();
        this.usage = isStatic2 ? GL20.GL_STATIC_DRAW : GL20.GL_DYNAMIC_DRAW;
        createVAO();
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

    public void bind(ShaderProgram shader) {
        bind(shader, (int[]) null);
    }

    public void bind(ShaderProgram shader, int[] locations) {
        GL30 gl = Gdx.gl30;
        gl.glBindVertexArray(this.vaoHandle);
        bindAttributes(shader, locations);
        bindData(gl);
        this.isBound = true;
    }

    private void bindAttributes(ShaderProgram shader, int[] locations) {
        boolean stillValid = this.cachedLocations.size != 0;
        int numAttributes = this.attributes.size();
        if (stillValid) {
            if (locations == null) {
                int i = 0;
                while (stillValid && i < numAttributes) {
                    stillValid = shader.getAttributeLocation(this.attributes.get(i).alias) == this.cachedLocations.get(i);
                    i++;
                }
            } else {
                boolean stillValid2 = locations.length == this.cachedLocations.size;
                int i2 = 0;
                while (stillValid && i2 < numAttributes) {
                    stillValid2 = locations[i2] == this.cachedLocations.get(i2);
                    i2++;
                }
            }
        }
        if (!stillValid) {
            Gdx.gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, this.bufferHandle);
            unbindAttributes(shader);
            this.cachedLocations.clear();
            for (int i3 = 0; i3 < numAttributes; i3++) {
                VertexAttribute attribute = this.attributes.get(i3);
                if (locations == null) {
                    this.cachedLocations.add(shader.getAttributeLocation(attribute.alias));
                } else {
                    this.cachedLocations.add(locations[i3]);
                }
                int location = this.cachedLocations.get(i3);
                if (location >= 0) {
                    shader.enableVertexAttribute(location);
                    shader.setVertexAttribute(location, attribute.numComponents, attribute.type, attribute.normalized, this.attributes.vertexSize, attribute.offset);
                }
            }
        }
    }

    private void unbindAttributes(ShaderProgram shaderProgram) {
        if (this.cachedLocations.size != 0) {
            int numAttributes = this.attributes.size();
            for (int i = 0; i < numAttributes; i++) {
                int location = this.cachedLocations.get(i);
                if (location >= 0) {
                    shaderProgram.disableVertexAttribute(location);
                }
            }
        }
    }

    private void bindData(GL20 gl) {
        if (this.isDirty) {
            gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, this.bufferHandle);
            this.byteBuffer.limit(this.buffer.limit() * 4);
            gl.glBufferData(GL20.GL_ARRAY_BUFFER, this.byteBuffer.limit(), this.byteBuffer, this.usage);
            this.isDirty = false;
        }
    }

    public void unbind(ShaderProgram shader) {
        unbind(shader, (int[]) null);
    }

    public void unbind(ShaderProgram shader, int[] locations) {
        Gdx.gl30.glBindVertexArray(0);
        this.isBound = false;
    }

    public void invalidate() {
        this.bufferHandle = Gdx.gl30.glGenBuffer();
        createVAO();
        this.isDirty = true;
    }

    public void dispose() {
        GL30 gl = Gdx.gl30;
        gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, 0);
        gl.glDeleteBuffer(this.bufferHandle);
        this.bufferHandle = 0;
        if (this.ownsBuffer) {
            BufferUtils.disposeUnsafeByteBuffer(this.byteBuffer);
        }
        deleteVAO();
    }

    private void createVAO() {
        tmpHandle.clear();
        Gdx.gl30.glGenVertexArrays(1, tmpHandle);
        this.vaoHandle = tmpHandle.get();
    }

    private void deleteVAO() {
        if (this.vaoHandle != -1) {
            tmpHandle.clear();
            tmpHandle.put(this.vaoHandle);
            tmpHandle.flip();
            Gdx.gl30.glDeleteVertexArrays(1, tmpHandle);
            this.vaoHandle = -1;
        }
    }
}
