package com.badlogic.gdx.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.glutils.MipMapGenerator;
import com.badlogic.gdx.utils.Disposable;

public abstract class GLTexture implements Disposable {
    protected int glHandle;
    public final int glTarget;
    protected Texture.TextureFilter magFilter;
    protected Texture.TextureFilter minFilter;
    protected Texture.TextureWrap uWrap;
    protected Texture.TextureWrap vWrap;

    public abstract int getDepth();

    public abstract int getHeight();

    public abstract int getWidth();

    public abstract boolean isManaged();

    /* access modifiers changed from: protected */
    public abstract void reload();

    public GLTexture(int glTarget2) {
        this(glTarget2, Gdx.gl.glGenTexture());
    }

    public GLTexture(int glTarget2, int glHandle2) {
        this.minFilter = Texture.TextureFilter.Nearest;
        this.magFilter = Texture.TextureFilter.Nearest;
        this.uWrap = Texture.TextureWrap.ClampToEdge;
        this.vWrap = Texture.TextureWrap.ClampToEdge;
        this.glTarget = glTarget2;
        this.glHandle = glHandle2;
    }

    public void bind() {
        Gdx.gl.glBindTexture(this.glTarget, this.glHandle);
    }

    public void bind(int unit) {
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0 + unit);
        Gdx.gl.glBindTexture(this.glTarget, this.glHandle);
    }

    public Texture.TextureFilter getMinFilter() {
        return this.minFilter;
    }

    public Texture.TextureFilter getMagFilter() {
        return this.magFilter;
    }

    public Texture.TextureWrap getUWrap() {
        return this.uWrap;
    }

    public Texture.TextureWrap getVWrap() {
        return this.vWrap;
    }

    public int getTextureObjectHandle() {
        return this.glHandle;
    }

    public void unsafeSetWrap(Texture.TextureWrap u, Texture.TextureWrap v) {
        unsafeSetWrap(u, v, false);
    }

    public void unsafeSetWrap(Texture.TextureWrap u, Texture.TextureWrap v, boolean force) {
        if (u != null && (force || this.uWrap != u)) {
            Gdx.gl.glTexParameteri(this.glTarget, GL20.GL_TEXTURE_WRAP_S, u.getGLEnum());
            this.uWrap = u;
        }
        if (v == null) {
            return;
        }
        if (force || this.vWrap != v) {
            Gdx.gl.glTexParameteri(this.glTarget, GL20.GL_TEXTURE_WRAP_T, v.getGLEnum());
            this.vWrap = v;
        }
    }

    public void setWrap(Texture.TextureWrap u, Texture.TextureWrap v) {
        this.uWrap = u;
        this.vWrap = v;
        bind();
        Gdx.gl.glTexParameteri(this.glTarget, GL20.GL_TEXTURE_WRAP_S, u.getGLEnum());
        Gdx.gl.glTexParameteri(this.glTarget, GL20.GL_TEXTURE_WRAP_T, v.getGLEnum());
    }

    public void unsafeSetFilter(Texture.TextureFilter minFilter2, Texture.TextureFilter magFilter2) {
        unsafeSetFilter(minFilter2, magFilter2, false);
    }

    public void unsafeSetFilter(Texture.TextureFilter minFilter2, Texture.TextureFilter magFilter2, boolean force) {
        if (minFilter2 != null && (force || this.minFilter != minFilter2)) {
            Gdx.gl.glTexParameteri(this.glTarget, GL20.GL_TEXTURE_MIN_FILTER, minFilter2.getGLEnum());
            this.minFilter = minFilter2;
        }
        if (magFilter2 == null) {
            return;
        }
        if (force || this.magFilter != magFilter2) {
            Gdx.gl.glTexParameteri(this.glTarget, GL20.GL_TEXTURE_MAG_FILTER, magFilter2.getGLEnum());
            this.magFilter = magFilter2;
        }
    }

    public void setFilter(Texture.TextureFilter minFilter2, Texture.TextureFilter magFilter2) {
        this.minFilter = minFilter2;
        this.magFilter = magFilter2;
        bind();
        Gdx.gl.glTexParameteri(this.glTarget, GL20.GL_TEXTURE_MIN_FILTER, minFilter2.getGLEnum());
        Gdx.gl.glTexParameteri(this.glTarget, GL20.GL_TEXTURE_MAG_FILTER, magFilter2.getGLEnum());
    }

    /* access modifiers changed from: protected */
    public void delete() {
        if (this.glHandle != 0) {
            Gdx.gl.glDeleteTexture(this.glHandle);
            this.glHandle = 0;
        }
    }

    public void dispose() {
        delete();
    }

    protected static void uploadImageData(int target, TextureData data) {
        uploadImageData(target, data, 0);
    }

    public static void uploadImageData(int target, TextureData data, int miplevel) {
        boolean disposePixmap;
        Pixmap tmp;
        int i = target;
        TextureData textureData = data;
        if (textureData != null) {
            if (!data.isPrepared()) {
                data.prepare();
            }
            if (data.getType() == TextureData.TextureDataType.Custom) {
                textureData.consumeCustomData(target);
                return;
            }
            Pixmap pixmap = data.consumePixmap();
            boolean disposePixmap2 = data.disposePixmap();
            if (data.getFormat() != pixmap.getFormat()) {
                tmp = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), data.getFormat());
                tmp.setBlending(Pixmap.Blending.None);
                tmp.drawPixmap(pixmap, 0, 0, 0, 0, pixmap.getWidth(), pixmap.getHeight());
                if (data.disposePixmap()) {
                    pixmap.dispose();
                }
                Pixmap pixmap2 = tmp;
                disposePixmap = true;
            } else {
                tmp = pixmap;
                disposePixmap = disposePixmap2;
            }
            Gdx.gl.glPixelStorei(GL20.GL_UNPACK_ALIGNMENT, 1);
            if (data.useMipMaps()) {
                MipMapGenerator.generateMipMap(target, tmp, tmp.getWidth(), tmp.getHeight());
            } else {
                Gdx.gl.glTexImage2D(target, miplevel, tmp.getGLInternalFormat(), tmp.getWidth(), tmp.getHeight(), 0, tmp.getGLFormat(), tmp.getGLType(), tmp.getPixels());
            }
            if (disposePixmap) {
                tmp.dispose();
            }
        }
    }
}
