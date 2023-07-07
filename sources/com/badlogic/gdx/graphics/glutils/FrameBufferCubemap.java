package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class FrameBufferCubemap extends GLFrameBuffer<Cubemap> {
    private static final Cubemap.CubemapSide[] cubemapSides = Cubemap.CubemapSide.values();
    private int currentSide;

    FrameBufferCubemap() {
    }

    protected FrameBufferCubemap(GLFrameBuffer.GLFrameBufferBuilder<? extends GLFrameBuffer<Cubemap>> bufferBuilder) {
        super(bufferBuilder);
    }

    public FrameBufferCubemap(Pixmap.Format format, int width, int height, boolean hasDepth) {
        this(format, width, height, hasDepth, false);
    }

    public FrameBufferCubemap(Pixmap.Format format, int width, int height, boolean hasDepth, boolean hasStencil) {
        GLFrameBuffer.FrameBufferCubemapBuilder frameBufferBuilder = new GLFrameBuffer.FrameBufferCubemapBuilder(width, height);
        frameBufferBuilder.addBasicColorTextureAttachment(format);
        if (hasDepth) {
            frameBufferBuilder.addBasicDepthRenderBuffer();
        }
        if (hasStencil) {
            frameBufferBuilder.addBasicStencilRenderBuffer();
        }
        this.bufferBuilder = frameBufferBuilder;
        build();
    }

    /* access modifiers changed from: protected */
    public Cubemap createTexture(GLFrameBuffer.FrameBufferTextureAttachmentSpec attachmentSpec) {
        GLOnlyTextureData data = new GLOnlyTextureData(this.bufferBuilder.width, this.bufferBuilder.height, 0, attachmentSpec.internalFormat, attachmentSpec.format, attachmentSpec.type);
        Cubemap result = new Cubemap((TextureData) data, (TextureData) data, (TextureData) data, (TextureData) data, (TextureData) data, (TextureData) data);
        result.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        result.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        return result;
    }

    /* access modifiers changed from: protected */
    public void disposeColorTexture(Cubemap colorTexture) {
        colorTexture.dispose();
    }

    /* access modifiers changed from: protected */
    public void attachFrameBufferColorTexture(Cubemap texture) {
        GL20 gl = Gdx.gl20;
        int glHandle = texture.getTextureObjectHandle();
        for (Cubemap.CubemapSide side : Cubemap.CubemapSide.values()) {
            gl.glFramebufferTexture2D(GL20.GL_FRAMEBUFFER, GL20.GL_COLOR_ATTACHMENT0, side.glEnum, glHandle, 0);
        }
    }

    public void bind() {
        this.currentSide = -1;
        super.bind();
    }

    public boolean nextSide() {
        int i = this.currentSide;
        if (i > 5) {
            throw new GdxRuntimeException("No remaining sides.");
        } else if (i == 5) {
            return false;
        } else {
            this.currentSide = i + 1;
            bindSide(getSide());
            return true;
        }
    }

    /* access modifiers changed from: protected */
    public void bindSide(Cubemap.CubemapSide side) {
        Gdx.gl20.glFramebufferTexture2D(GL20.GL_FRAMEBUFFER, GL20.GL_COLOR_ATTACHMENT0, side.glEnum, ((Cubemap) getColorBufferTexture()).getTextureObjectHandle(), 0);
    }

    public Cubemap.CubemapSide getSide() {
        int i = this.currentSide;
        if (i < 0) {
            return null;
        }
        return cubemapSides[i];
    }
}
