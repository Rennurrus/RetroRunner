package com.badlogic.gdx.graphics;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.FileTextureArrayData;

public interface TextureArrayData {
    void consumeTextureArrayData();

    int getDepth();

    int getGLType();

    int getHeight();

    int getInternalFormat();

    int getWidth();

    boolean isManaged();

    boolean isPrepared();

    void prepare();

    public static class Factory {
        public static TextureArrayData loadFromFiles(Pixmap.Format format, boolean useMipMaps, FileHandle... files) {
            return new FileTextureArrayData(format, useMipMaps, files);
        }
    }
}
