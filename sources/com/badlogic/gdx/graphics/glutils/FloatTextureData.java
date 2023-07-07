package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.glutils.GLVersion;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.nio.FloatBuffer;

public class FloatTextureData implements TextureData {
    FloatBuffer buffer;
    int format;
    int height = 0;
    int internalFormat;
    boolean isGpuOnly;
    boolean isPrepared = false;
    int type;
    int width = 0;

    public FloatTextureData(int w, int h, int internalFormat2, int format2, int type2, boolean isGpuOnly2) {
        this.width = w;
        this.height = h;
        this.internalFormat = internalFormat2;
        this.format = format2;
        this.type = type2;
        this.isGpuOnly = isGpuOnly2;
    }

    public TextureData.TextureDataType getType() {
        return TextureData.TextureDataType.Custom;
    }

    public boolean isPrepared() {
        return this.isPrepared;
    }

    public void prepare() {
        if (!this.isPrepared) {
            if (!this.isGpuOnly) {
                int amountOfFloats = 4;
                if (Gdx.graphics.getGLVersion().getType().equals(GLVersion.Type.OpenGL)) {
                    int i = this.internalFormat;
                    if (i == 34842 || i == 34836) {
                        amountOfFloats = 4;
                    }
                    int i2 = this.internalFormat;
                    if (i2 == 34843 || i2 == 34837) {
                        amountOfFloats = 3;
                    }
                    int i3 = this.internalFormat;
                    if (i3 == 33327 || i3 == 33328) {
                        amountOfFloats = 2;
                    }
                    int i4 = this.internalFormat;
                    if (i4 == 33325 || i4 == 33326) {
                        amountOfFloats = 1;
                    }
                }
                this.buffer = BufferUtils.newFloatBuffer(this.width * this.height * amountOfFloats);
            }
            this.isPrepared = true;
            return;
        }
        throw new GdxRuntimeException("Already prepared");
    }

    public void consumeCustomData(int target) {
        if (Gdx.app.getType() == Application.ApplicationType.Android || Gdx.app.getType() == Application.ApplicationType.iOS || Gdx.app.getType() == Application.ApplicationType.WebGL) {
            if (Gdx.graphics.supportsExtension("OES_texture_float")) {
                Gdx.gl.glTexImage2D(target, 0, GL20.GL_RGBA, this.width, this.height, 0, GL20.GL_RGBA, GL20.GL_FLOAT, this.buffer);
                return;
            }
            throw new GdxRuntimeException("Extension OES_texture_float not supported!");
        } else if (Gdx.graphics.isGL30Available() || Gdx.graphics.supportsExtension("GL_ARB_texture_float")) {
            Gdx.gl.glTexImage2D(target, 0, this.internalFormat, this.width, this.height, 0, this.format, GL20.GL_FLOAT, this.buffer);
        } else {
            throw new GdxRuntimeException("Extension GL_ARB_texture_float not supported!");
        }
    }

    public Pixmap consumePixmap() {
        throw new GdxRuntimeException("This TextureData implementation does not return a Pixmap");
    }

    public boolean disposePixmap() {
        throw new GdxRuntimeException("This TextureData implementation does not return a Pixmap");
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public Pixmap.Format getFormat() {
        return Pixmap.Format.RGBA8888;
    }

    public boolean useMipMaps() {
        return false;
    }

    public boolean isManaged() {
        return true;
    }

    public FloatBuffer getBuffer() {
        return this.buffer;
    }
}
