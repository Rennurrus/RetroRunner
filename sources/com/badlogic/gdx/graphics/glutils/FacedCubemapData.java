package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.CubemapData;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class FacedCubemapData implements CubemapData {
    protected final TextureData[] data;

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public FacedCubemapData() {
        /*
            r8 = this;
            r0 = 0
            r7 = r0
            com.badlogic.gdx.graphics.TextureData r7 = (com.badlogic.gdx.graphics.TextureData) r7
            r1 = r8
            r2 = r7
            r3 = r7
            r4 = r7
            r5 = r7
            r6 = r7
            r1.<init>((com.badlogic.gdx.graphics.TextureData) r2, (com.badlogic.gdx.graphics.TextureData) r3, (com.badlogic.gdx.graphics.TextureData) r4, (com.badlogic.gdx.graphics.TextureData) r5, (com.badlogic.gdx.graphics.TextureData) r6, (com.badlogic.gdx.graphics.TextureData) r7)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.graphics.glutils.FacedCubemapData.<init>():void");
    }

    public FacedCubemapData(FileHandle positiveX, FileHandle negativeX, FileHandle positiveY, FileHandle negativeY, FileHandle positiveZ, FileHandle negativeZ) {
        this(TextureData.Factory.loadFromFile(positiveX, false), TextureData.Factory.loadFromFile(negativeX, false), TextureData.Factory.loadFromFile(positiveY, false), TextureData.Factory.loadFromFile(negativeY, false), TextureData.Factory.loadFromFile(positiveZ, false), TextureData.Factory.loadFromFile(negativeZ, false));
    }

    public FacedCubemapData(FileHandle positiveX, FileHandle negativeX, FileHandle positiveY, FileHandle negativeY, FileHandle positiveZ, FileHandle negativeZ, boolean useMipMaps) {
        this(TextureData.Factory.loadFromFile(positiveX, useMipMaps), TextureData.Factory.loadFromFile(negativeX, useMipMaps), TextureData.Factory.loadFromFile(positiveY, useMipMaps), TextureData.Factory.loadFromFile(negativeY, useMipMaps), TextureData.Factory.loadFromFile(positiveZ, useMipMaps), TextureData.Factory.loadFromFile(negativeZ, useMipMaps));
    }

    public FacedCubemapData(Pixmap positiveX, Pixmap negativeX, Pixmap positiveY, Pixmap negativeY, Pixmap positiveZ, Pixmap negativeZ) {
        this(positiveX, negativeX, positiveY, negativeY, positiveZ, negativeZ, false);
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public FacedCubemapData(com.badlogic.gdx.graphics.Pixmap r18, com.badlogic.gdx.graphics.Pixmap r19, com.badlogic.gdx.graphics.Pixmap r20, com.badlogic.gdx.graphics.Pixmap r21, com.badlogic.gdx.graphics.Pixmap r22, com.badlogic.gdx.graphics.Pixmap r23, boolean r24) {
        /*
            r17 = this;
            r0 = r18
            r1 = r19
            r2 = r20
            r3 = r21
            r4 = r22
            r5 = r23
            r6 = r24
            r7 = 0
            r8 = 0
            if (r0 != 0) goto L_0x0014
            r11 = r8
            goto L_0x001a
        L_0x0014:
            com.badlogic.gdx.graphics.glutils.PixmapTextureData r9 = new com.badlogic.gdx.graphics.glutils.PixmapTextureData
            r9.<init>(r0, r8, r6, r7)
            r11 = r9
        L_0x001a:
            if (r1 != 0) goto L_0x001e
            r12 = r8
            goto L_0x0024
        L_0x001e:
            com.badlogic.gdx.graphics.glutils.PixmapTextureData r9 = new com.badlogic.gdx.graphics.glutils.PixmapTextureData
            r9.<init>(r1, r8, r6, r7)
            r12 = r9
        L_0x0024:
            if (r2 != 0) goto L_0x0028
            r13 = r8
            goto L_0x002e
        L_0x0028:
            com.badlogic.gdx.graphics.glutils.PixmapTextureData r9 = new com.badlogic.gdx.graphics.glutils.PixmapTextureData
            r9.<init>(r2, r8, r6, r7)
            r13 = r9
        L_0x002e:
            if (r3 != 0) goto L_0x0032
            r14 = r8
            goto L_0x0038
        L_0x0032:
            com.badlogic.gdx.graphics.glutils.PixmapTextureData r9 = new com.badlogic.gdx.graphics.glutils.PixmapTextureData
            r9.<init>(r3, r8, r6, r7)
            r14 = r9
        L_0x0038:
            if (r4 != 0) goto L_0x003c
            r15 = r8
            goto L_0x0042
        L_0x003c:
            com.badlogic.gdx.graphics.glutils.PixmapTextureData r9 = new com.badlogic.gdx.graphics.glutils.PixmapTextureData
            r9.<init>(r4, r8, r6, r7)
            r15 = r9
        L_0x0042:
            if (r5 != 0) goto L_0x0047
            r16 = r8
            goto L_0x004e
        L_0x0047:
            com.badlogic.gdx.graphics.glutils.PixmapTextureData r9 = new com.badlogic.gdx.graphics.glutils.PixmapTextureData
            r9.<init>(r5, r8, r6, r7)
            r16 = r9
        L_0x004e:
            r10 = r17
            r10.<init>((com.badlogic.gdx.graphics.TextureData) r11, (com.badlogic.gdx.graphics.TextureData) r12, (com.badlogic.gdx.graphics.TextureData) r13, (com.badlogic.gdx.graphics.TextureData) r14, (com.badlogic.gdx.graphics.TextureData) r15, (com.badlogic.gdx.graphics.TextureData) r16)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.graphics.glutils.FacedCubemapData.<init>(com.badlogic.gdx.graphics.Pixmap, com.badlogic.gdx.graphics.Pixmap, com.badlogic.gdx.graphics.Pixmap, com.badlogic.gdx.graphics.Pixmap, com.badlogic.gdx.graphics.Pixmap, com.badlogic.gdx.graphics.Pixmap, boolean):void");
    }

    public FacedCubemapData(int width, int height, int depth, Pixmap.Format format) {
        this((TextureData) new PixmapTextureData(new Pixmap(depth, height, format), (Pixmap.Format) null, false, true), (TextureData) new PixmapTextureData(new Pixmap(depth, height, format), (Pixmap.Format) null, false, true), (TextureData) new PixmapTextureData(new Pixmap(width, depth, format), (Pixmap.Format) null, false, true), (TextureData) new PixmapTextureData(new Pixmap(width, depth, format), (Pixmap.Format) null, false, true), (TextureData) new PixmapTextureData(new Pixmap(width, height, format), (Pixmap.Format) null, false, true), (TextureData) new PixmapTextureData(new Pixmap(width, height, format), (Pixmap.Format) null, false, true));
    }

    public FacedCubemapData(TextureData positiveX, TextureData negativeX, TextureData positiveY, TextureData negativeY, TextureData positiveZ, TextureData negativeZ) {
        this.data = new TextureData[6];
        TextureData[] textureDataArr = this.data;
        textureDataArr[0] = positiveX;
        textureDataArr[1] = negativeX;
        textureDataArr[2] = positiveY;
        textureDataArr[3] = negativeY;
        textureDataArr[4] = positiveZ;
        textureDataArr[5] = negativeZ;
    }

    public boolean isManaged() {
        for (TextureData data2 : this.data) {
            if (!data2.isManaged()) {
                return false;
            }
        }
        return true;
    }

    public void load(Cubemap.CubemapSide side, FileHandle file) {
        this.data[side.index] = TextureData.Factory.loadFromFile(file, false);
    }

    public void load(Cubemap.CubemapSide side, Pixmap pixmap) {
        TextureData[] textureDataArr = this.data;
        int i = side.index;
        PixmapTextureData pixmapTextureData = null;
        if (pixmap != null) {
            pixmapTextureData = new PixmapTextureData(pixmap, (Pixmap.Format) null, false, false);
        }
        textureDataArr[i] = pixmapTextureData;
    }

    public boolean isComplete() {
        int i = 0;
        while (true) {
            TextureData[] textureDataArr = this.data;
            if (i >= textureDataArr.length) {
                return true;
            }
            if (textureDataArr[i] == null) {
                return false;
            }
            i++;
        }
    }

    public TextureData getTextureData(Cubemap.CubemapSide side) {
        return this.data[side.index];
    }

    public int getWidth() {
        int width = 0;
        if (this.data[Cubemap.CubemapSide.PositiveZ.index] != null) {
            int width2 = this.data[Cubemap.CubemapSide.PositiveZ.index].getWidth();
            int tmp = width2;
            if (width2 > 0) {
                width = tmp;
            }
        }
        if (this.data[Cubemap.CubemapSide.NegativeZ.index] != null) {
            int width3 = this.data[Cubemap.CubemapSide.NegativeZ.index].getWidth();
            int tmp2 = width3;
            if (width3 > width) {
                width = tmp2;
            }
        }
        if (this.data[Cubemap.CubemapSide.PositiveY.index] != null) {
            int width4 = this.data[Cubemap.CubemapSide.PositiveY.index].getWidth();
            int tmp3 = width4;
            if (width4 > width) {
                width = tmp3;
            }
        }
        if (this.data[Cubemap.CubemapSide.NegativeY.index] == null) {
            return width;
        }
        int width5 = this.data[Cubemap.CubemapSide.NegativeY.index].getWidth();
        int tmp4 = width5;
        if (width5 > width) {
            return tmp4;
        }
        return width;
    }

    public int getHeight() {
        int height = 0;
        if (this.data[Cubemap.CubemapSide.PositiveZ.index] != null) {
            int height2 = this.data[Cubemap.CubemapSide.PositiveZ.index].getHeight();
            int tmp = height2;
            if (height2 > 0) {
                height = tmp;
            }
        }
        if (this.data[Cubemap.CubemapSide.NegativeZ.index] != null) {
            int height3 = this.data[Cubemap.CubemapSide.NegativeZ.index].getHeight();
            int tmp2 = height3;
            if (height3 > height) {
                height = tmp2;
            }
        }
        if (this.data[Cubemap.CubemapSide.PositiveX.index] != null) {
            int height4 = this.data[Cubemap.CubemapSide.PositiveX.index].getHeight();
            int tmp3 = height4;
            if (height4 > height) {
                height = tmp3;
            }
        }
        if (this.data[Cubemap.CubemapSide.NegativeX.index] == null) {
            return height;
        }
        int height5 = this.data[Cubemap.CubemapSide.NegativeX.index].getHeight();
        int tmp4 = height5;
        if (height5 > height) {
            return tmp4;
        }
        return height;
    }

    public boolean isPrepared() {
        return false;
    }

    public void prepare() {
        if (isComplete()) {
            int i = 0;
            while (true) {
                TextureData[] textureDataArr = this.data;
                if (i < textureDataArr.length) {
                    if (!textureDataArr[i].isPrepared()) {
                        this.data[i].prepare();
                    }
                    i++;
                } else {
                    return;
                }
            }
        } else {
            throw new GdxRuntimeException("You need to complete your cubemap data before using it");
        }
    }

    public void consumeCubemapData() {
        int i = 0;
        while (true) {
            TextureData[] textureDataArr = this.data;
            if (i < textureDataArr.length) {
                if (textureDataArr[i].getType() == TextureData.TextureDataType.Custom) {
                    this.data[i].consumeCustomData(GL20.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i);
                } else {
                    Pixmap pixmap = this.data[i].consumePixmap();
                    boolean disposePixmap = this.data[i].disposePixmap();
                    if (this.data[i].getFormat() != pixmap.getFormat()) {
                        Pixmap tmp = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), this.data[i].getFormat());
                        tmp.setBlending(Pixmap.Blending.None);
                        tmp.drawPixmap(pixmap, 0, 0, 0, 0, pixmap.getWidth(), pixmap.getHeight());
                        if (this.data[i].disposePixmap()) {
                            pixmap.dispose();
                        }
                        pixmap = tmp;
                        disposePixmap = true;
                    }
                    Gdx.gl.glPixelStorei(GL20.GL_UNPACK_ALIGNMENT, 1);
                    Gdx.gl.glTexImage2D(i + GL20.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, pixmap.getGLInternalFormat(), pixmap.getWidth(), pixmap.getHeight(), 0, pixmap.getGLFormat(), pixmap.getGLType(), pixmap.getPixels());
                    if (disposePixmap) {
                        pixmap.dispose();
                    }
                }
                i++;
            } else {
                return;
            }
        }
    }
}
