package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.graphics.GLTexture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class MipMapTextureData implements TextureData {
    TextureData[] mips;

    public MipMapTextureData(TextureData... mipMapData) {
        this.mips = new TextureData[mipMapData.length];
        System.arraycopy(mipMapData, 0, this.mips, 0, mipMapData.length);
    }

    public TextureData.TextureDataType getType() {
        return TextureData.TextureDataType.Custom;
    }

    public boolean isPrepared() {
        return true;
    }

    public void prepare() {
    }

    public Pixmap consumePixmap() {
        throw new GdxRuntimeException("It's compressed, use the compressed method");
    }

    public boolean disposePixmap() {
        return false;
    }

    public void consumeCustomData(int target) {
        int i = 0;
        while (true) {
            TextureData[] textureDataArr = this.mips;
            if (i < textureDataArr.length) {
                GLTexture.uploadImageData(target, textureDataArr[i], i);
                i++;
            } else {
                return;
            }
        }
    }

    public int getWidth() {
        return this.mips[0].getWidth();
    }

    public int getHeight() {
        return this.mips[0].getHeight();
    }

    public Pixmap.Format getFormat() {
        return this.mips[0].getFormat();
    }

    public boolean useMipMaps() {
        return false;
    }

    public boolean isManaged() {
        return true;
    }
}
