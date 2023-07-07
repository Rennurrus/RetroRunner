package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class FileTextureData implements TextureData {
    final FileHandle file;
    Pixmap.Format format;
    int height = 0;
    boolean isPrepared = false;
    Pixmap pixmap;
    boolean useMipMaps;
    int width = 0;

    public FileTextureData(FileHandle file2, Pixmap preloadedPixmap, Pixmap.Format format2, boolean useMipMaps2) {
        this.file = file2;
        this.pixmap = preloadedPixmap;
        this.format = format2;
        this.useMipMaps = useMipMaps2;
        Pixmap pixmap2 = this.pixmap;
        if (pixmap2 != null) {
            this.width = pixmap2.getWidth();
            this.height = this.pixmap.getHeight();
            if (format2 == null) {
                this.format = this.pixmap.getFormat();
            }
        }
    }

    public boolean isPrepared() {
        return this.isPrepared;
    }

    public void prepare() {
        if (!this.isPrepared) {
            if (this.pixmap == null) {
                if (this.file.extension().equals("cim")) {
                    this.pixmap = PixmapIO.readCIM(this.file);
                } else {
                    this.pixmap = new Pixmap(this.file);
                }
                this.width = this.pixmap.getWidth();
                this.height = this.pixmap.getHeight();
                if (this.format == null) {
                    this.format = this.pixmap.getFormat();
                }
            }
            this.isPrepared = true;
            return;
        }
        throw new GdxRuntimeException("Already prepared");
    }

    public Pixmap consumePixmap() {
        if (this.isPrepared) {
            this.isPrepared = false;
            Pixmap pixmap2 = this.pixmap;
            this.pixmap = null;
            return pixmap2;
        }
        throw new GdxRuntimeException("Call prepare() before calling getPixmap()");
    }

    public boolean disposePixmap() {
        return true;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public Pixmap.Format getFormat() {
        return this.format;
    }

    public boolean useMipMaps() {
        return this.useMipMaps;
    }

    public boolean isManaged() {
        return true;
    }

    public FileHandle getFileHandle() {
        return this.file;
    }

    public TextureData.TextureDataType getType() {
        return TextureData.TextureDataType.Pixmap;
    }

    public void consumeCustomData(int target) {
        throw new GdxRuntimeException("This TextureData implementation does not upload data itself");
    }

    public String toString() {
        return this.file.toString();
    }
}
