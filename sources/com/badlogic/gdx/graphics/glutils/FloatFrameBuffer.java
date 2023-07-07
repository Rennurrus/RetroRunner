package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer;

public class FloatFrameBuffer extends FrameBuffer {
    FloatFrameBuffer() {
    }

    protected FloatFrameBuffer(GLFrameBuffer.GLFrameBufferBuilder<? extends GLFrameBuffer<Texture>> bufferBuilder) {
        super(bufferBuilder);
    }

    public FloatFrameBuffer(int width, int height, boolean hasDepth) {
        GLFrameBuffer.FloatFrameBufferBuilder bufferBuilder = new GLFrameBuffer.FloatFrameBufferBuilder(width, height);
        bufferBuilder.addFloatAttachment(GL30.GL_RGBA32F, GL20.GL_RGBA, GL20.GL_FLOAT, false);
        if (hasDepth) {
            bufferBuilder.addBasicDepthRenderBuffer();
        }
        this.bufferBuilder = bufferBuilder;
        build();
    }

    /* access modifiers changed from: protected */
    public Texture createTexture(GLFrameBuffer.FrameBufferTextureAttachmentSpec attachmentSpec) {
        Texture result = new Texture((TextureData) new FloatTextureData(this.bufferBuilder.width, this.bufferBuilder.height, attachmentSpec.internalFormat, attachmentSpec.format, attachmentSpec.type, attachmentSpec.isGpuOnly));
        if (Gdx.app.getType() == Application.ApplicationType.Desktop || Gdx.app.getType() == Application.ApplicationType.Applet) {
            result.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        } else {
            result.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
        result.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        return result;
    }
}
