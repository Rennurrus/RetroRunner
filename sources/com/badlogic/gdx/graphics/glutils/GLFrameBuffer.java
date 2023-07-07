package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.GLTexture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class GLFrameBuffer<T extends GLTexture> implements Disposable {
    protected static final int GL_DEPTH24_STENCIL8_OES = 35056;
    protected static final Map<Application, Array<GLFrameBuffer>> buffers = new HashMap();
    protected static int defaultFramebufferHandle;
    protected static boolean defaultFramebufferHandleInitialized = false;
    protected GLFrameBufferBuilder<? extends GLFrameBuffer<T>> bufferBuilder;
    protected int depthStencilPackedBufferHandle;
    protected int depthbufferHandle;
    protected int framebufferHandle;
    protected boolean hasDepthStencilPackedBuffer;
    protected boolean isMRT;
    protected int stencilbufferHandle;
    protected Array<T> textureAttachments = new Array<>();

    /* access modifiers changed from: protected */
    public abstract void attachFrameBufferColorTexture(T t);

    /* access modifiers changed from: protected */
    public abstract T createTexture(FrameBufferTextureAttachmentSpec frameBufferTextureAttachmentSpec);

    /* access modifiers changed from: protected */
    public abstract void disposeColorTexture(T t);

    GLFrameBuffer() {
    }

    protected GLFrameBuffer(GLFrameBufferBuilder<? extends GLFrameBuffer<T>> bufferBuilder2) {
        this.bufferBuilder = bufferBuilder2;
        build();
    }

    public T getColorBufferTexture() {
        return (GLTexture) this.textureAttachments.first();
    }

    public Array<T> getTextureAttachments() {
        return this.textureAttachments;
    }

    /* access modifiers changed from: protected */
    public void build() {
        GL20 gl = Gdx.gl20;
        checkValidBuilder();
        if (!defaultFramebufferHandleInitialized) {
            defaultFramebufferHandleInitialized = true;
            if (Gdx.app.getType() == Application.ApplicationType.iOS) {
                IntBuffer intbuf = ByteBuffer.allocateDirect(64).order(ByteOrder.nativeOrder()).asIntBuffer();
                gl.glGetIntegerv(36006, intbuf);
                defaultFramebufferHandle = intbuf.get(0);
            } else {
                defaultFramebufferHandle = 0;
            }
        }
        this.framebufferHandle = gl.glGenFramebuffer();
        gl.glBindFramebuffer(GL20.GL_FRAMEBUFFER, this.framebufferHandle);
        int width = this.bufferBuilder.width;
        int height = this.bufferBuilder.height;
        if (this.bufferBuilder.hasDepthRenderBuffer) {
            this.depthbufferHandle = gl.glGenRenderbuffer();
            gl.glBindRenderbuffer(GL20.GL_RENDERBUFFER, this.depthbufferHandle);
            gl.glRenderbufferStorage(GL20.GL_RENDERBUFFER, this.bufferBuilder.depthRenderBufferSpec.internalFormat, width, height);
        }
        if (this.bufferBuilder.hasStencilRenderBuffer) {
            this.stencilbufferHandle = gl.glGenRenderbuffer();
            gl.glBindRenderbuffer(GL20.GL_RENDERBUFFER, this.stencilbufferHandle);
            gl.glRenderbufferStorage(GL20.GL_RENDERBUFFER, this.bufferBuilder.stencilRenderBufferSpec.internalFormat, width, height);
        }
        if (this.bufferBuilder.hasPackedStencilDepthRenderBuffer) {
            this.depthStencilPackedBufferHandle = gl.glGenRenderbuffer();
            gl.glBindRenderbuffer(GL20.GL_RENDERBUFFER, this.depthStencilPackedBufferHandle);
            gl.glRenderbufferStorage(GL20.GL_RENDERBUFFER, this.bufferBuilder.packedStencilDepthRenderBufferSpec.internalFormat, width, height);
        }
        this.isMRT = this.bufferBuilder.textureAttachmentSpecs.size > 1;
        int colorTextureCounter = 0;
        if (this.isMRT) {
            Iterator<FrameBufferTextureAttachmentSpec> it = this.bufferBuilder.textureAttachmentSpecs.iterator();
            int colorTextureCounter2 = 0;
            while (it.hasNext() != 0) {
                FrameBufferTextureAttachmentSpec attachmentSpec = it.next();
                T texture = createTexture(attachmentSpec);
                this.textureAttachments.add(texture);
                if (attachmentSpec.isColorTexture()) {
                    T t = texture;
                    FrameBufferTextureAttachmentSpec frameBufferTextureAttachmentSpec = attachmentSpec;
                    gl.glFramebufferTexture2D(GL20.GL_FRAMEBUFFER, colorTextureCounter2 + GL20.GL_COLOR_ATTACHMENT0, GL20.GL_TEXTURE_2D, texture.getTextureObjectHandle(), 0);
                    colorTextureCounter2++;
                } else {
                    T texture2 = texture;
                    FrameBufferTextureAttachmentSpec attachmentSpec2 = attachmentSpec;
                    if (attachmentSpec2.isDepth) {
                        gl.glFramebufferTexture2D(GL20.GL_FRAMEBUFFER, GL20.GL_DEPTH_ATTACHMENT, GL20.GL_TEXTURE_2D, texture2.getTextureObjectHandle(), 0);
                    } else if (attachmentSpec2.isStencil) {
                        gl.glFramebufferTexture2D(GL20.GL_FRAMEBUFFER, GL20.GL_STENCIL_ATTACHMENT, GL20.GL_TEXTURE_2D, texture2.getTextureObjectHandle(), 0);
                    }
                }
            }
            colorTextureCounter = colorTextureCounter2;
        } else {
            T texture3 = createTexture(this.bufferBuilder.textureAttachmentSpecs.first());
            this.textureAttachments.add(texture3);
            gl.glBindTexture(texture3.glTarget, texture3.getTextureObjectHandle());
        }
        if (this.isMRT) {
            IntBuffer buffer = BufferUtils.newIntBuffer(colorTextureCounter);
            for (int i = 0; i < colorTextureCounter; i++) {
                buffer.put(i + GL20.GL_COLOR_ATTACHMENT0);
            }
            buffer.position(0);
            Gdx.gl30.glDrawBuffers(colorTextureCounter, buffer);
        } else {
            attachFrameBufferColorTexture((GLTexture) this.textureAttachments.first());
        }
        if (this.bufferBuilder.hasDepthRenderBuffer) {
            gl.glFramebufferRenderbuffer(GL20.GL_FRAMEBUFFER, GL20.GL_DEPTH_ATTACHMENT, GL20.GL_RENDERBUFFER, this.depthbufferHandle);
        }
        if (this.bufferBuilder.hasStencilRenderBuffer) {
            gl.glFramebufferRenderbuffer(GL20.GL_FRAMEBUFFER, GL20.GL_STENCIL_ATTACHMENT, GL20.GL_RENDERBUFFER, this.stencilbufferHandle);
        }
        if (this.bufferBuilder.hasPackedStencilDepthRenderBuffer) {
            gl.glFramebufferRenderbuffer(GL20.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL20.GL_RENDERBUFFER, this.depthStencilPackedBufferHandle);
        }
        gl.glBindRenderbuffer(GL20.GL_RENDERBUFFER, 0);
        Iterator<T> it2 = this.textureAttachments.iterator();
        while (it2.hasNext()) {
            gl.glBindTexture(((GLTexture) it2.next()).glTarget, 0);
        }
        int result = gl.glCheckFramebufferStatus(GL20.GL_FRAMEBUFFER);
        if (result == 36061 && this.bufferBuilder.hasDepthRenderBuffer && this.bufferBuilder.hasStencilRenderBuffer && (Gdx.graphics.supportsExtension("GL_OES_packed_depth_stencil") || Gdx.graphics.supportsExtension("GL_EXT_packed_depth_stencil"))) {
            if (this.bufferBuilder.hasDepthRenderBuffer) {
                gl.glDeleteRenderbuffer(this.depthbufferHandle);
                this.depthbufferHandle = 0;
            }
            if (this.bufferBuilder.hasStencilRenderBuffer) {
                gl.glDeleteRenderbuffer(this.stencilbufferHandle);
                this.stencilbufferHandle = 0;
            }
            if (this.bufferBuilder.hasPackedStencilDepthRenderBuffer) {
                gl.glDeleteRenderbuffer(this.depthStencilPackedBufferHandle);
                this.depthStencilPackedBufferHandle = 0;
            }
            this.depthStencilPackedBufferHandle = gl.glGenRenderbuffer();
            this.hasDepthStencilPackedBuffer = true;
            gl.glBindRenderbuffer(GL20.GL_RENDERBUFFER, this.depthStencilPackedBufferHandle);
            gl.glRenderbufferStorage(GL20.GL_RENDERBUFFER, 35056, width, height);
            gl.glBindRenderbuffer(GL20.GL_RENDERBUFFER, 0);
            gl.glFramebufferRenderbuffer(GL20.GL_FRAMEBUFFER, GL20.GL_DEPTH_ATTACHMENT, GL20.GL_RENDERBUFFER, this.depthStencilPackedBufferHandle);
            gl.glFramebufferRenderbuffer(GL20.GL_FRAMEBUFFER, GL20.GL_STENCIL_ATTACHMENT, GL20.GL_RENDERBUFFER, this.depthStencilPackedBufferHandle);
            result = gl.glCheckFramebufferStatus(GL20.GL_FRAMEBUFFER);
        }
        gl.glBindFramebuffer(GL20.GL_FRAMEBUFFER, defaultFramebufferHandle);
        if (result != 36053) {
            Iterator<T> it3 = this.textureAttachments.iterator();
            while (it3.hasNext()) {
                disposeColorTexture((GLTexture) it3.next());
            }
            if (this.hasDepthStencilPackedBuffer) {
                gl.glDeleteBuffer(this.depthStencilPackedBufferHandle);
            } else {
                if (this.bufferBuilder.hasDepthRenderBuffer) {
                    gl.glDeleteRenderbuffer(this.depthbufferHandle);
                }
                if (this.bufferBuilder.hasStencilRenderBuffer) {
                    gl.glDeleteRenderbuffer(this.stencilbufferHandle);
                }
            }
            gl.glDeleteFramebuffer(this.framebufferHandle);
            if (result == 36054) {
                throw new IllegalStateException("Frame buffer couldn't be constructed: incomplete attachment");
            } else if (result == 36057) {
                throw new IllegalStateException("Frame buffer couldn't be constructed: incomplete dimensions");
            } else if (result == 36055) {
                throw new IllegalStateException("Frame buffer couldn't be constructed: missing attachment");
            } else if (result == 36061) {
                throw new IllegalStateException("Frame buffer couldn't be constructed: unsupported combination of formats");
            } else {
                throw new IllegalStateException("Frame buffer couldn't be constructed: unknown error " + result);
            }
        } else {
            addManagedFrameBuffer(Gdx.app, this);
        }
    }

    private void checkValidBuilder() {
        if (Gdx.graphics.isGL30Available()) {
            return;
        }
        if (this.bufferBuilder.hasPackedStencilDepthRenderBuffer) {
            throw new GdxRuntimeException("Packed Stencil/Render render buffers are not available on GLES 2.0");
        } else if (this.bufferBuilder.textureAttachmentSpecs.size <= 1) {
            Iterator<FrameBufferTextureAttachmentSpec> it = this.bufferBuilder.textureAttachmentSpecs.iterator();
            while (it.hasNext()) {
                FrameBufferTextureAttachmentSpec spec = it.next();
                if (spec.isDepth) {
                    throw new GdxRuntimeException("Depth texture FrameBuffer Attachment not available on GLES 2.0");
                } else if (spec.isStencil) {
                    throw new GdxRuntimeException("Stencil texture FrameBuffer Attachment not available on GLES 2.0");
                } else if (spec.isFloat && !Gdx.graphics.supportsExtension("OES_texture_float")) {
                    throw new GdxRuntimeException("Float texture FrameBuffer Attachment not available on GLES 2.0");
                }
            }
        } else {
            throw new GdxRuntimeException("Multiple render targets not available on GLES 2.0");
        }
    }

    public void dispose() {
        GL20 gl = Gdx.gl20;
        Iterator<T> it = this.textureAttachments.iterator();
        while (it.hasNext()) {
            disposeColorTexture((GLTexture) it.next());
        }
        if (this.hasDepthStencilPackedBuffer) {
            gl.glDeleteRenderbuffer(this.depthStencilPackedBufferHandle);
        } else {
            if (this.bufferBuilder.hasDepthRenderBuffer) {
                gl.glDeleteRenderbuffer(this.depthbufferHandle);
            }
            if (this.bufferBuilder.hasStencilRenderBuffer) {
                gl.glDeleteRenderbuffer(this.stencilbufferHandle);
            }
        }
        gl.glDeleteFramebuffer(this.framebufferHandle);
        if (buffers.get(Gdx.app) != null) {
            buffers.get(Gdx.app).removeValue(this, true);
        }
    }

    public void bind() {
        Gdx.gl20.glBindFramebuffer(GL20.GL_FRAMEBUFFER, this.framebufferHandle);
    }

    public static void unbind() {
        Gdx.gl20.glBindFramebuffer(GL20.GL_FRAMEBUFFER, defaultFramebufferHandle);
    }

    public void begin() {
        bind();
        setFrameBufferViewport();
    }

    /* access modifiers changed from: protected */
    public void setFrameBufferViewport() {
        Gdx.gl20.glViewport(0, 0, this.bufferBuilder.width, this.bufferBuilder.height);
    }

    public void end() {
        end(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
    }

    public void end(int x, int y, int width, int height) {
        unbind();
        Gdx.gl20.glViewport(x, y, width, height);
    }

    public int getFramebufferHandle() {
        return this.framebufferHandle;
    }

    public int getDepthBufferHandle() {
        return this.depthbufferHandle;
    }

    public int getStencilBufferHandle() {
        return this.stencilbufferHandle;
    }

    /* access modifiers changed from: protected */
    public int getDepthStencilPackedBuffer() {
        return this.depthStencilPackedBufferHandle;
    }

    public int getHeight() {
        return this.bufferBuilder.height;
    }

    public int getWidth() {
        return this.bufferBuilder.width;
    }

    private static void addManagedFrameBuffer(Application app, GLFrameBuffer frameBuffer) {
        Array<GLFrameBuffer> managedResources = buffers.get(app);
        if (managedResources == null) {
            managedResources = new Array<>();
        }
        managedResources.add(frameBuffer);
        buffers.put(app, managedResources);
    }

    public static void invalidateAllFrameBuffers(Application app) {
        Array<GLFrameBuffer> bufferArray;
        if (Gdx.gl20 != null && (bufferArray = buffers.get(app)) != null) {
            for (int i = 0; i < bufferArray.size; i++) {
                bufferArray.get(i).build();
            }
        }
    }

    public static void clearAllFrameBuffers(Application app) {
        buffers.remove(app);
    }

    public static StringBuilder getManagedStatus(StringBuilder builder) {
        builder.append("Managed buffers/app: { ");
        for (Application app : buffers.keySet()) {
            builder.append(buffers.get(app).size);
            builder.append(" ");
        }
        builder.append("}");
        return builder;
    }

    public static String getManagedStatus() {
        return getManagedStatus(new StringBuilder()).toString();
    }

    protected static class FrameBufferTextureAttachmentSpec {
        int format;
        int internalFormat;
        boolean isDepth;
        boolean isFloat;
        boolean isGpuOnly;
        boolean isStencil;
        int type;

        public FrameBufferTextureAttachmentSpec(int internalformat, int format2, int type2) {
            this.internalFormat = internalformat;
            this.format = format2;
            this.type = type2;
        }

        public boolean isColorTexture() {
            return !this.isDepth && !this.isStencil;
        }
    }

    protected static class FrameBufferRenderBufferAttachmentSpec {
        int internalFormat;

        public FrameBufferRenderBufferAttachmentSpec(int internalFormat2) {
            this.internalFormat = internalFormat2;
        }
    }

    protected static abstract class GLFrameBufferBuilder<U extends GLFrameBuffer<? extends GLTexture>> {
        protected FrameBufferRenderBufferAttachmentSpec depthRenderBufferSpec;
        protected boolean hasDepthRenderBuffer;
        protected boolean hasPackedStencilDepthRenderBuffer;
        protected boolean hasStencilRenderBuffer;
        protected int height;
        protected FrameBufferRenderBufferAttachmentSpec packedStencilDepthRenderBufferSpec;
        protected FrameBufferRenderBufferAttachmentSpec stencilRenderBufferSpec;
        protected Array<FrameBufferTextureAttachmentSpec> textureAttachmentSpecs = new Array<>();
        protected int width;

        public abstract U build();

        public GLFrameBufferBuilder(int width2, int height2) {
            this.width = width2;
            this.height = height2;
        }

        public GLFrameBufferBuilder<U> addColorTextureAttachment(int internalFormat, int format, int type) {
            this.textureAttachmentSpecs.add(new FrameBufferTextureAttachmentSpec(internalFormat, format, type));
            return this;
        }

        public GLFrameBufferBuilder<U> addBasicColorTextureAttachment(Pixmap.Format format) {
            int glFormat = Pixmap.Format.toGlFormat(format);
            return addColorTextureAttachment(glFormat, glFormat, Pixmap.Format.toGlType(format));
        }

        public GLFrameBufferBuilder<U> addFloatAttachment(int internalFormat, int format, int type, boolean gpuOnly) {
            FrameBufferTextureAttachmentSpec spec = new FrameBufferTextureAttachmentSpec(internalFormat, format, type);
            spec.isFloat = true;
            spec.isGpuOnly = gpuOnly;
            this.textureAttachmentSpecs.add(spec);
            return this;
        }

        public GLFrameBufferBuilder<U> addDepthTextureAttachment(int internalFormat, int type) {
            FrameBufferTextureAttachmentSpec spec = new FrameBufferTextureAttachmentSpec(internalFormat, GL20.GL_DEPTH_COMPONENT, type);
            spec.isDepth = true;
            this.textureAttachmentSpecs.add(spec);
            return this;
        }

        public GLFrameBufferBuilder<U> addStencilTextureAttachment(int internalFormat, int type) {
            FrameBufferTextureAttachmentSpec spec = new FrameBufferTextureAttachmentSpec(internalFormat, GL20.GL_STENCIL_ATTACHMENT, type);
            spec.isStencil = true;
            this.textureAttachmentSpecs.add(spec);
            return this;
        }

        public GLFrameBufferBuilder<U> addDepthRenderBuffer(int internalFormat) {
            this.depthRenderBufferSpec = new FrameBufferRenderBufferAttachmentSpec(internalFormat);
            this.hasDepthRenderBuffer = true;
            return this;
        }

        public GLFrameBufferBuilder<U> addStencilRenderBuffer(int internalFormat) {
            this.stencilRenderBufferSpec = new FrameBufferRenderBufferAttachmentSpec(internalFormat);
            this.hasStencilRenderBuffer = true;
            return this;
        }

        public GLFrameBufferBuilder<U> addStencilDepthPackedRenderBuffer(int internalFormat) {
            this.packedStencilDepthRenderBufferSpec = new FrameBufferRenderBufferAttachmentSpec(internalFormat);
            this.hasPackedStencilDepthRenderBuffer = true;
            return this;
        }

        public GLFrameBufferBuilder<U> addBasicDepthRenderBuffer() {
            return addDepthRenderBuffer(GL20.GL_DEPTH_COMPONENT16);
        }

        public GLFrameBufferBuilder<U> addBasicStencilRenderBuffer() {
            return addStencilRenderBuffer(GL20.GL_STENCIL_INDEX8);
        }

        public GLFrameBufferBuilder<U> addBasicStencilDepthPackedRenderBuffer() {
            return addStencilDepthPackedRenderBuffer(35056);
        }
    }

    public static class FrameBufferBuilder extends GLFrameBufferBuilder<FrameBuffer> {
        public /* bridge */ /* synthetic */ GLFrameBufferBuilder addBasicColorTextureAttachment(Pixmap.Format format) {
            return super.addBasicColorTextureAttachment(format);
        }

        public /* bridge */ /* synthetic */ GLFrameBufferBuilder addBasicDepthRenderBuffer() {
            return super.addBasicDepthRenderBuffer();
        }

        public /* bridge */ /* synthetic */ GLFrameBufferBuilder addBasicStencilDepthPackedRenderBuffer() {
            return super.addBasicStencilDepthPackedRenderBuffer();
        }

        public /* bridge */ /* synthetic */ GLFrameBufferBuilder addBasicStencilRenderBuffer() {
            return super.addBasicStencilRenderBuffer();
        }

        public /* bridge */ /* synthetic */ GLFrameBufferBuilder addColorTextureAttachment(int i, int i2, int i3) {
            return super.addColorTextureAttachment(i, i2, i3);
        }

        public /* bridge */ /* synthetic */ GLFrameBufferBuilder addDepthRenderBuffer(int i) {
            return super.addDepthRenderBuffer(i);
        }

        public /* bridge */ /* synthetic */ GLFrameBufferBuilder addDepthTextureAttachment(int i, int i2) {
            return super.addDepthTextureAttachment(i, i2);
        }

        public /* bridge */ /* synthetic */ GLFrameBufferBuilder addFloatAttachment(int i, int i2, int i3, boolean z) {
            return super.addFloatAttachment(i, i2, i3, z);
        }

        public /* bridge */ /* synthetic */ GLFrameBufferBuilder addStencilDepthPackedRenderBuffer(int i) {
            return super.addStencilDepthPackedRenderBuffer(i);
        }

        public /* bridge */ /* synthetic */ GLFrameBufferBuilder addStencilRenderBuffer(int i) {
            return super.addStencilRenderBuffer(i);
        }

        public /* bridge */ /* synthetic */ GLFrameBufferBuilder addStencilTextureAttachment(int i, int i2) {
            return super.addStencilTextureAttachment(i, i2);
        }

        public FrameBufferBuilder(int width, int height) {
            super(width, height);
        }

        public FrameBuffer build() {
            return new FrameBuffer(this);
        }
    }

    public static class FloatFrameBufferBuilder extends GLFrameBufferBuilder<FloatFrameBuffer> {
        public /* bridge */ /* synthetic */ GLFrameBufferBuilder addBasicColorTextureAttachment(Pixmap.Format format) {
            return super.addBasicColorTextureAttachment(format);
        }

        public /* bridge */ /* synthetic */ GLFrameBufferBuilder addBasicDepthRenderBuffer() {
            return super.addBasicDepthRenderBuffer();
        }

        public /* bridge */ /* synthetic */ GLFrameBufferBuilder addBasicStencilDepthPackedRenderBuffer() {
            return super.addBasicStencilDepthPackedRenderBuffer();
        }

        public /* bridge */ /* synthetic */ GLFrameBufferBuilder addBasicStencilRenderBuffer() {
            return super.addBasicStencilRenderBuffer();
        }

        public /* bridge */ /* synthetic */ GLFrameBufferBuilder addColorTextureAttachment(int i, int i2, int i3) {
            return super.addColorTextureAttachment(i, i2, i3);
        }

        public /* bridge */ /* synthetic */ GLFrameBufferBuilder addDepthRenderBuffer(int i) {
            return super.addDepthRenderBuffer(i);
        }

        public /* bridge */ /* synthetic */ GLFrameBufferBuilder addDepthTextureAttachment(int i, int i2) {
            return super.addDepthTextureAttachment(i, i2);
        }

        public /* bridge */ /* synthetic */ GLFrameBufferBuilder addFloatAttachment(int i, int i2, int i3, boolean z) {
            return super.addFloatAttachment(i, i2, i3, z);
        }

        public /* bridge */ /* synthetic */ GLFrameBufferBuilder addStencilDepthPackedRenderBuffer(int i) {
            return super.addStencilDepthPackedRenderBuffer(i);
        }

        public /* bridge */ /* synthetic */ GLFrameBufferBuilder addStencilRenderBuffer(int i) {
            return super.addStencilRenderBuffer(i);
        }

        public /* bridge */ /* synthetic */ GLFrameBufferBuilder addStencilTextureAttachment(int i, int i2) {
            return super.addStencilTextureAttachment(i, i2);
        }

        public FloatFrameBufferBuilder(int width, int height) {
            super(width, height);
        }

        public FloatFrameBuffer build() {
            return new FloatFrameBuffer(this);
        }
    }

    public static class FrameBufferCubemapBuilder extends GLFrameBufferBuilder<FrameBufferCubemap> {
        public /* bridge */ /* synthetic */ GLFrameBufferBuilder addBasicColorTextureAttachment(Pixmap.Format format) {
            return super.addBasicColorTextureAttachment(format);
        }

        public /* bridge */ /* synthetic */ GLFrameBufferBuilder addBasicDepthRenderBuffer() {
            return super.addBasicDepthRenderBuffer();
        }

        public /* bridge */ /* synthetic */ GLFrameBufferBuilder addBasicStencilDepthPackedRenderBuffer() {
            return super.addBasicStencilDepthPackedRenderBuffer();
        }

        public /* bridge */ /* synthetic */ GLFrameBufferBuilder addBasicStencilRenderBuffer() {
            return super.addBasicStencilRenderBuffer();
        }

        public /* bridge */ /* synthetic */ GLFrameBufferBuilder addColorTextureAttachment(int i, int i2, int i3) {
            return super.addColorTextureAttachment(i, i2, i3);
        }

        public /* bridge */ /* synthetic */ GLFrameBufferBuilder addDepthRenderBuffer(int i) {
            return super.addDepthRenderBuffer(i);
        }

        public /* bridge */ /* synthetic */ GLFrameBufferBuilder addDepthTextureAttachment(int i, int i2) {
            return super.addDepthTextureAttachment(i, i2);
        }

        public /* bridge */ /* synthetic */ GLFrameBufferBuilder addFloatAttachment(int i, int i2, int i3, boolean z) {
            return super.addFloatAttachment(i, i2, i3, z);
        }

        public /* bridge */ /* synthetic */ GLFrameBufferBuilder addStencilDepthPackedRenderBuffer(int i) {
            return super.addStencilDepthPackedRenderBuffer(i);
        }

        public /* bridge */ /* synthetic */ GLFrameBufferBuilder addStencilRenderBuffer(int i) {
            return super.addStencilRenderBuffer(i);
        }

        public /* bridge */ /* synthetic */ GLFrameBufferBuilder addStencilTextureAttachment(int i, int i2) {
            return super.addStencilTextureAttachment(i, i2);
        }

        public FrameBufferCubemapBuilder(int width, int height) {
            super(width, height);
        }

        public FrameBufferCubemap build() {
            return new FrameBufferCubemap(this);
        }
    }
}
