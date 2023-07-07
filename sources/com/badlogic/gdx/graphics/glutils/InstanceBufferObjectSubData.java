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

public class InstanceBufferObjectSubData implements InstanceData {
    final VertexAttributes attributes;
    final FloatBuffer buffer;
    int bufferHandle;
    final ByteBuffer byteBuffer;
    boolean isBound;
    final boolean isDirect;
    boolean isDirty;
    final boolean isStatic;
    final int usage;

    public InstanceBufferObjectSubData(boolean isStatic2, int numInstances, VertexAttribute... instanceAttributes) {
        this(isStatic2, numInstances, new VertexAttributes(instanceAttributes));
    }

    public InstanceBufferObjectSubData(boolean isStatic2, int numInstances, VertexAttributes instanceAttributes) {
        this.isDirty = false;
        this.isBound = false;
        this.isStatic = isStatic2;
        this.attributes = instanceAttributes;
        this.byteBuffer = BufferUtils.newByteBuffer(this.attributes.vertexSize * numInstances);
        this.isDirect = true;
        this.usage = isStatic2 ? GL20.GL_STATIC_DRAW : GL20.GL_DYNAMIC_DRAW;
        this.buffer = this.byteBuffer.asFloatBuffer();
        this.bufferHandle = createBufferObject();
        this.buffer.flip();
        this.byteBuffer.flip();
    }

    private int createBufferObject() {
        int result = Gdx.gl20.glGenBuffer();
        Gdx.gl20.glBindBuffer(GL20.GL_ARRAY_BUFFER, result);
        Gdx.gl20.glBufferData(GL20.GL_ARRAY_BUFFER, this.byteBuffer.capacity(), (Buffer) null, this.usage);
        Gdx.gl20.glBindBuffer(GL20.GL_ARRAY_BUFFER, 0);
        return result;
    }

    public VertexAttributes getAttributes() {
        return this.attributes;
    }

    public int getNumInstances() {
        return (this.buffer.limit() * 4) / this.attributes.vertexSize;
    }

    public int getNumMaxInstances() {
        return this.byteBuffer.capacity() / this.attributes.vertexSize;
    }

    public FloatBuffer getBuffer() {
        this.isDirty = true;
        return this.buffer;
    }

    private void bufferChanged() {
        if (this.isBound) {
            Gdx.gl20.glBufferData(GL20.GL_ARRAY_BUFFER, this.byteBuffer.limit(), (Buffer) null, this.usage);
            Gdx.gl20.glBufferSubData(GL20.GL_ARRAY_BUFFER, 0, this.byteBuffer.limit(), this.byteBuffer);
            this.isDirty = false;
        }
    }

    public void setInstanceData(float[] data, int offset, int count) {
        this.isDirty = true;
        if (this.isDirect) {
            BufferUtils.copy(data, (Buffer) this.byteBuffer, count, offset);
            this.buffer.position(0);
            this.buffer.limit(count);
        } else {
            this.buffer.clear();
            this.buffer.put(data, offset, count);
            this.buffer.flip();
            this.byteBuffer.position(0);
            this.byteBuffer.limit(this.buffer.limit() << 2);
        }
        bufferChanged();
    }

    public void setInstanceData(FloatBuffer data, int count) {
        this.isDirty = true;
        if (this.isDirect) {
            BufferUtils.copy(data, this.byteBuffer, count);
            this.buffer.position(0);
            this.buffer.limit(count);
        } else {
            this.buffer.clear();
            this.buffer.put(data);
            this.buffer.flip();
            this.byteBuffer.position(0);
            this.byteBuffer.limit(this.buffer.limit() << 2);
        }
        bufferChanged();
    }

    public void updateInstanceData(int targetOffset, float[] data, int sourceOffset, int count) {
        this.isDirty = true;
        if (this.isDirect) {
            int pos = this.byteBuffer.position();
            this.byteBuffer.position(targetOffset * 4);
            BufferUtils.copy(data, sourceOffset, count, (Buffer) this.byteBuffer);
            this.byteBuffer.position(pos);
            bufferChanged();
            return;
        }
        throw new GdxRuntimeException("Buffer must be allocated direct.");
    }

    public void updateInstanceData(int targetOffset, FloatBuffer data, int sourceOffset, int count) {
        this.isDirty = true;
        if (this.isDirect) {
            int pos = this.byteBuffer.position();
            this.byteBuffer.position(targetOffset * 4);
            data.position(sourceOffset * 4);
            BufferUtils.copy(data, this.byteBuffer, count);
            this.byteBuffer.position(pos);
            bufferChanged();
            return;
        }
        throw new GdxRuntimeException("Buffer must be allocated direct.");
    }

    public void bind(ShaderProgram shader) {
        bind(shader, (int[]) null);
    }

    public void bind(ShaderProgram shader, int[] locations) {
        ShaderProgram shaderProgram = shader;
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
                int location = shaderProgram.getAttributeLocation(attribute.alias);
                if (location >= 0) {
                    int unitOffset = attribute.unit;
                    shaderProgram.enableVertexAttribute(location + unitOffset);
                    shader.setVertexAttribute(location + unitOffset, attribute.numComponents, attribute.type, attribute.normalized, this.attributes.vertexSize, attribute.offset);
                    Gdx.gl30.glVertexAttribDivisor(location + unitOffset, 1);
                }
            }
        } else {
            for (int i2 = 0; i2 < numAttributes; i2++) {
                VertexAttribute attribute2 = this.attributes.get(i2);
                int location2 = locations[i2];
                if (location2 >= 0) {
                    int unitOffset2 = attribute2.unit;
                    shaderProgram.enableVertexAttribute(location2 + unitOffset2);
                    shader.setVertexAttribute(location2 + unitOffset2, attribute2.numComponents, attribute2.type, attribute2.normalized, this.attributes.vertexSize, attribute2.offset);
                    Gdx.gl30.glVertexAttribDivisor(location2 + unitOffset2, 1);
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
                VertexAttribute attribute = this.attributes.get(i);
                int location = shader.getAttributeLocation(attribute.alias);
                if (location >= 0) {
                    shader.disableVertexAttribute(location + attribute.unit);
                }
            }
        } else {
            for (int i2 = 0; i2 < numAttributes; i2++) {
                VertexAttribute attribute2 = this.attributes.get(i2);
                int location2 = locations[i2];
                if (location2 >= 0) {
                    shader.enableVertexAttribute(location2 + attribute2.unit);
                }
            }
        }
        gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, 0);
        this.isBound = false;
    }

    public void invalidate() {
        this.bufferHandle = createBufferObject();
        this.isDirty = true;
    }

    public void dispose() {
        GL20 gl = Gdx.gl20;
        gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, 0);
        gl.glDeleteBuffer(this.bufferHandle);
        this.bufferHandle = 0;
    }

    public int getBufferHandle() {
        return this.bufferHandle;
    }
}
