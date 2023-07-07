package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.nio.Buffer;

public class GLOnlyTextureData implements TextureData {
    int format;
    int height = 0;
    int internalFormat;
    boolean isPrepared = false;
    int mipLevel = 0;
    int type;
    int width = 0;

    public GLOnlyTextureData(int width2, int height2, int mipMapLevel, int internalFormat2, int format2, int type2) {
        this.width = width2;
        this.height = height2;
        this.mipLevel = mipMapLevel;
        this.internalFormat = internalFormat2;
        this.format = format2;
        this.type = type2;
    }

    public TextureData.TextureDataType getType() {
        return TextureData.TextureDataType.Custom;
    }

    public boolean isPrepared() {
        return this.isPrepared;
    }

    public void prepare() {
        if (!this.isPrepared) {
            this.isPrepared = true;
            return;
        }
        throw new GdxRuntimeException("Already prepared");
    }

    public void consumeCustomData(int target) {
        Gdx.gl.glTexImage2D(target, this.mipLevel, this.internalFormat, this.width, this.height, 0, this.format, this.type, (Buffer) null);
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
        return false;
    }
}
