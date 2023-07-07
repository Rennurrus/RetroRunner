package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class PixmapTextureData implements TextureData {
    final boolean disposePixmap;
    final Pixmap.Format format;
    final boolean managed;
    final Pixmap pixmap;
    final boolean useMipMaps;

    public PixmapTextureData(Pixmap pixmap2, Pixmap.Format format2, boolean useMipMaps2, boolean disposePixmap2) {
        this(pixmap2, format2, useMipMaps2, disposePixmap2, false);
    }

    public PixmapTextureData(Pixmap pixmap2, Pixmap.Format format2, boolean useMipMaps2, boolean disposePixmap2, boolean managed2) {
        this.pixmap = pixmap2;
        this.format = format2 == null ? pixmap2.getFormat() : format2;
        this.useMipMaps = useMipMaps2;
        this.disposePixmap = disposePixmap2;
        this.managed = managed2;
    }

    public boolean disposePixmap() {
        return this.disposePixmap;
    }

    public Pixmap consumePixmap() {
        return this.pixmap;
    }

    public int getWidth() {
        return this.pixmap.getWidth();
    }

    public int getHeight() {
        return this.pixmap.getHeight();
    }

    public Pixmap.Format getFormat() {
        return this.format;
    }

    public boolean useMipMaps() {
        return this.useMipMaps;
    }

    public boolean isManaged() {
        return this.managed;
    }

    public TextureData.TextureDataType getType() {
        return TextureData.TextureDataType.Pixmap;
    }

    public void consumeCustomData(int target) {
        throw new GdxRuntimeException("This TextureData implementation does not upload data itself");
    }

    public boolean isPrepared() {
        return true;
    }

    public void prepare() {
        throw new GdxRuntimeException("prepare() must not be called on a PixmapTextureData instance as it is already prepared.");
    }
}
