package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.CubemapData;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.glutils.ETC1;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StreamUtils;
import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.zip.GZIPInputStream;

public class KTXTextureData implements TextureData, CubemapData {
    private static final int GL_TEXTURE_1D = 4660;
    private static final int GL_TEXTURE_1D_ARRAY_EXT = 4660;
    private static final int GL_TEXTURE_2D_ARRAY_EXT = 4660;
    private static final int GL_TEXTURE_3D = 4660;
    private ByteBuffer compressedData;
    private FileHandle file;
    private int glBaseInternalFormat;
    private int glFormat;
    private int glInternalFormat;
    private int glType;
    private int glTypeSize;
    private int imagePos;
    private int numberOfArrayElements;
    private int numberOfFaces;
    private int numberOfMipmapLevels;
    private int pixelDepth = -1;
    private int pixelHeight = -1;
    private int pixelWidth = -1;
    private boolean useMipMaps;

    public KTXTextureData(FileHandle file2, boolean genMipMaps) {
        this.file = file2;
        this.useMipMaps = genMipMaps;
    }

    public TextureData.TextureDataType getType() {
        return TextureData.TextureDataType.Custom;
    }

    public boolean isPrepared() {
        return this.compressedData != null;
    }

    public void prepare() {
        if (this.compressedData == null) {
            FileHandle fileHandle = this.file;
            if (fileHandle != null) {
                if (fileHandle.name().endsWith(".zktx")) {
                    byte[] buffer = new byte[GL20.GL_TEXTURE_MAG_FILTER];
                    try {
                        DataInputStream in = new DataInputStream(new BufferedInputStream(new GZIPInputStream(this.file.read())));
                        this.compressedData = BufferUtils.newUnsafeByteBuffer(in.readInt());
                        while (true) {
                            int read = in.read(buffer);
                            int readBytes = read;
                            if (read == -1) {
                                break;
                            }
                            this.compressedData.put(buffer, 0, readBytes);
                        }
                        this.compressedData.position(0);
                        this.compressedData.limit(this.compressedData.capacity());
                        StreamUtils.closeQuietly(in);
                    } catch (Exception e) {
                        throw new GdxRuntimeException("Couldn't load zktx file '" + this.file + "'", e);
                    } catch (Throwable th) {
                        StreamUtils.closeQuietly((Closeable) null);
                        throw th;
                    }
                } else {
                    this.compressedData = ByteBuffer.wrap(this.file.readBytes());
                }
                if (this.compressedData.get() != -85) {
                    throw new GdxRuntimeException("Invalid KTX Header");
                } else if (this.compressedData.get() != 75) {
                    throw new GdxRuntimeException("Invalid KTX Header");
                } else if (this.compressedData.get() != 84) {
                    throw new GdxRuntimeException("Invalid KTX Header");
                } else if (this.compressedData.get() != 88) {
                    throw new GdxRuntimeException("Invalid KTX Header");
                } else if (this.compressedData.get() != 32) {
                    throw new GdxRuntimeException("Invalid KTX Header");
                } else if (this.compressedData.get() != 49) {
                    throw new GdxRuntimeException("Invalid KTX Header");
                } else if (this.compressedData.get() != 49) {
                    throw new GdxRuntimeException("Invalid KTX Header");
                } else if (this.compressedData.get() != -69) {
                    throw new GdxRuntimeException("Invalid KTX Header");
                } else if (this.compressedData.get() != 13) {
                    throw new GdxRuntimeException("Invalid KTX Header");
                } else if (this.compressedData.get() != 10) {
                    throw new GdxRuntimeException("Invalid KTX Header");
                } else if (this.compressedData.get() != 26) {
                    throw new GdxRuntimeException("Invalid KTX Header");
                } else if (this.compressedData.get() == 10) {
                    int endianTag = this.compressedData.getInt();
                    if (endianTag == 67305985 || endianTag == 16909060) {
                        if (endianTag != 67305985) {
                            ByteBuffer byteBuffer = this.compressedData;
                            byteBuffer.order(byteBuffer.order() == ByteOrder.BIG_ENDIAN ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN);
                        }
                        this.glType = this.compressedData.getInt();
                        this.glTypeSize = this.compressedData.getInt();
                        this.glFormat = this.compressedData.getInt();
                        this.glInternalFormat = this.compressedData.getInt();
                        this.glBaseInternalFormat = this.compressedData.getInt();
                        this.pixelWidth = this.compressedData.getInt();
                        this.pixelHeight = this.compressedData.getInt();
                        this.pixelDepth = this.compressedData.getInt();
                        this.numberOfArrayElements = this.compressedData.getInt();
                        this.numberOfFaces = this.compressedData.getInt();
                        this.numberOfMipmapLevels = this.compressedData.getInt();
                        if (this.numberOfMipmapLevels == 0) {
                            this.numberOfMipmapLevels = 1;
                            this.useMipMaps = true;
                        }
                        this.imagePos = this.compressedData.position() + this.compressedData.getInt();
                        if (!this.compressedData.isDirect()) {
                            int pos = this.imagePos;
                            for (int level = 0; level < this.numberOfMipmapLevels; level++) {
                                pos += (this.numberOfFaces * ((this.compressedData.getInt(pos) + 3) & -4)) + 4;
                            }
                            this.compressedData.limit(pos);
                            this.compressedData.position(0);
                            ByteBuffer directBuffer = BufferUtils.newUnsafeByteBuffer(pos);
                            directBuffer.order(this.compressedData.order());
                            directBuffer.put(this.compressedData);
                            this.compressedData = directBuffer;
                            return;
                        }
                        return;
                    }
                    throw new GdxRuntimeException("Invalid KTX Header");
                } else {
                    throw new GdxRuntimeException("Invalid KTX Header");
                }
            } else {
                throw new GdxRuntimeException("Need a file to load from");
            }
        } else {
            throw new GdxRuntimeException("Already prepared");
        }
    }

    public void consumeCubemapData() {
        consumeCustomData(GL20.GL_TEXTURE_CUBE_MAP);
    }

    public void consumeCustomData(int target) {
        int glTarget;
        int pos;
        boolean compressed;
        int faceLodSizeRounded;
        int level;
        int glFormat2;
        int target2 = target;
        if (this.compressedData != null) {
            IntBuffer buffer = BufferUtils.newIntBuffer(16);
            boolean compressed2 = false;
            if (this.glType == 0 || this.glFormat == 0) {
                if (this.glType + this.glFormat == 0) {
                    compressed2 = true;
                } else {
                    throw new GdxRuntimeException("either both or none of glType, glFormat must be zero");
                }
            }
            int textureDimensions = 1;
            int glTarget2 = 4660;
            if (this.pixelHeight > 0) {
                textureDimensions = 2;
                glTarget2 = GL20.GL_TEXTURE_2D;
            }
            if (this.pixelDepth > 0) {
                textureDimensions = 3;
                glTarget2 = 4660;
            }
            int i = this.numberOfFaces;
            int i2 = 1;
            if (i == 6) {
                if (textureDimensions == 2) {
                    glTarget2 = GL20.GL_TEXTURE_CUBE_MAP;
                } else {
                    throw new GdxRuntimeException("cube map needs 2D faces");
                }
            } else if (i != 1) {
                boolean z = compressed2;
                throw new GdxRuntimeException("numberOfFaces must be either 1 or 6");
            }
            if (this.numberOfArrayElements > 0) {
                if (glTarget == 4660) {
                    glTarget = 4660;
                } else if (glTarget == 3553) {
                    glTarget = 4660;
                } else {
                    throw new GdxRuntimeException("No API for 3D and cube arrays yet");
                }
                textureDimensions++;
            }
            if (glTarget != 4660) {
                int singleFace = -1;
                if (this.numberOfFaces != 6 || target2 == 34067) {
                    if (this.numberOfFaces == 6 && target2 == 34067) {
                        target2 = GL20.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
                    } else if (target2 != glTarget && (34069 > target2 || target2 > 34074 || target2 != 3553)) {
                        throw new GdxRuntimeException("Invalid target requested : 0x" + Integer.toHexString(target) + ", expecting : 0x" + Integer.toHexString(glTarget));
                    }
                } else if (34069 > target2 || target2 > 34074) {
                    throw new GdxRuntimeException("You must specify either GL_TEXTURE_CUBE_MAP to bind all 6 faces of the cube or the requested face GL_TEXTURE_CUBE_MAP_POSITIVE_X and followings.");
                } else {
                    singleFace = target2 - GL20.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
                    target2 = GL20.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
                }
                Gdx.gl.glGetIntegerv(GL20.GL_UNPACK_ALIGNMENT, buffer);
                int previousUnpackAlignment = buffer.get(0);
                if (previousUnpackAlignment != 4) {
                    Gdx.gl.glPixelStorei(GL20.GL_UNPACK_ALIGNMENT, 4);
                }
                int glInternalFormat2 = this.glInternalFormat;
                int glFormat3 = this.glFormat;
                int pos2 = this.imagePos;
                int level2 = 0;
                while (level2 < this.numberOfMipmapLevels) {
                    int pixelWidth2 = Math.max(i2, this.pixelWidth >> level2);
                    int pixelHeight2 = Math.max(i2, this.pixelHeight >> level2);
                    int pixelDepth2 = Math.max(i2, this.pixelDepth >> level2);
                    this.compressedData.position(pos2);
                    int faceLodSize = this.compressedData.getInt();
                    IntBuffer buffer2 = buffer;
                    int faceLodSizeRounded2 = (faceLodSize + 3) & -4;
                    int glTarget3 = glTarget;
                    int glTarget4 = pos2 + 4;
                    int pos3 = pixelDepth2;
                    int face = 0;
                    while (true) {
                        int pixelHeight3 = pixelHeight2;
                        if (face >= this.numberOfFaces) {
                            break;
                        }
                        this.compressedData.position(glTarget4);
                        int pos4 = glTarget4 + faceLodSizeRounded2;
                        if (singleFace == -1 || singleFace == face) {
                            ByteBuffer data = this.compressedData.slice();
                            data.limit(faceLodSizeRounded2);
                            faceLodSizeRounded = faceLodSizeRounded2;
                            if (textureDimensions == 1) {
                                compressed = compressed2;
                                pos = pos4;
                                level = level2;
                                glFormat2 = glFormat3;
                            } else {
                                if (textureDimensions == 2) {
                                    int pixelHeight4 = this.numberOfArrayElements > 0 ? this.numberOfArrayElements : pixelHeight3;
                                    if (compressed2) {
                                        compressed = compressed2;
                                        if (glInternalFormat2 == ETC1.ETC1_RGB8_OES) {
                                            pos = pos4;
                                            if (!Gdx.graphics.supportsExtension("GL_OES_compressed_ETC1_RGB8_texture")) {
                                                Pixmap pixmap = ETC1.decodeImage(new ETC1.ETC1Data(pixelWidth2, pixelHeight4, data, 0), Pixmap.Format.RGB888);
                                                Gdx.gl.glTexImage2D(target2 + face, level2, pixmap.getGLInternalFormat(), pixmap.getWidth(), pixmap.getHeight(), 0, pixmap.getGLFormat(), pixmap.getGLType(), pixmap.getPixels());
                                                pixmap.dispose();
                                                level = level2;
                                                glFormat2 = glFormat3;
                                            } else {
                                                level = level2;
                                                glFormat2 = glFormat3;
                                                Gdx.gl.glCompressedTexImage2D(target2 + face, level, glInternalFormat2, pixelWidth2, pixelHeight4, 0, faceLodSize, data);
                                            }
                                        } else {
                                            pos = pos4;
                                            level = level2;
                                            glFormat2 = glFormat3;
                                            Gdx.gl.glCompressedTexImage2D(target2 + face, level, glInternalFormat2, pixelWidth2, pixelHeight4, 0, faceLodSize, data);
                                        }
                                    } else {
                                        compressed = compressed2;
                                        pos = pos4;
                                        level = level2;
                                        glFormat2 = glFormat3;
                                        Gdx.gl.glTexImage2D(target2 + face, level, glInternalFormat2, pixelWidth2, pixelHeight4, 0, glFormat2, this.glType, data);
                                    }
                                    pixelHeight2 = pixelHeight4;
                                } else {
                                    compressed = compressed2;
                                    pos = pos4;
                                    level = level2;
                                    glFormat2 = glFormat3;
                                    if (textureDimensions == 3 && this.numberOfArrayElements > 0) {
                                        int pixelDepth3 = this.numberOfArrayElements;
                                        pixelHeight2 = pixelHeight3;
                                    }
                                }
                                face++;
                                glFormat3 = glFormat2;
                                level2 = level;
                                faceLodSizeRounded2 = faceLodSizeRounded;
                                compressed2 = compressed;
                                glTarget4 = pos;
                            }
                        } else {
                            faceLodSizeRounded = faceLodSizeRounded2;
                            compressed = compressed2;
                            pos = pos4;
                            level = level2;
                            glFormat2 = glFormat3;
                        }
                        pixelHeight2 = pixelHeight3;
                        face++;
                        glFormat3 = glFormat2;
                        level2 = level;
                        faceLodSizeRounded2 = faceLodSizeRounded;
                        compressed2 = compressed;
                        glTarget4 = pos;
                    }
                    boolean compressed3 = compressed2;
                    int i3 = glFormat3;
                    level2++;
                    pos2 = glTarget4;
                    buffer = buffer2;
                    glTarget = glTarget3;
                    compressed2 = compressed3;
                    i2 = 1;
                }
                boolean z2 = compressed2;
                int i4 = glTarget;
                int i5 = level2;
                int i6 = glFormat3;
                if (previousUnpackAlignment != 4) {
                    Gdx.gl.glPixelStorei(GL20.GL_UNPACK_ALIGNMENT, previousUnpackAlignment);
                }
                if (useMipMaps()) {
                    Gdx.gl.glGenerateMipmap(target2);
                }
                disposePreparedData();
                return;
            }
            boolean z3 = compressed2;
            int i7 = glTarget;
            throw new GdxRuntimeException("Unsupported texture format (only 2D texture are supported in LibGdx for the time being)");
        }
        throw new GdxRuntimeException("Call prepare() before calling consumeCompressedData()");
    }

    public void disposePreparedData() {
        ByteBuffer byteBuffer = this.compressedData;
        if (byteBuffer != null) {
            BufferUtils.disposeUnsafeByteBuffer(byteBuffer);
        }
        this.compressedData = null;
    }

    public Pixmap consumePixmap() {
        throw new GdxRuntimeException("This TextureData implementation does not return a Pixmap");
    }

    public boolean disposePixmap() {
        throw new GdxRuntimeException("This TextureData implementation does not return a Pixmap");
    }

    public int getWidth() {
        return this.pixelWidth;
    }

    public int getHeight() {
        return this.pixelHeight;
    }

    public int getNumberOfMipMapLevels() {
        return this.numberOfMipmapLevels;
    }

    public int getNumberOfFaces() {
        return this.numberOfFaces;
    }

    public int getGlInternalFormat() {
        return this.glInternalFormat;
    }

    public ByteBuffer getData(int requestedLevel, int requestedFace) {
        int pos = this.imagePos;
        for (int level = 0; level < this.numberOfMipmapLevels; level++) {
            int faceLodSizeRounded = (this.compressedData.getInt(pos) + 3) & -4;
            pos += 4;
            if (level == requestedLevel) {
                for (int face = 0; face < this.numberOfFaces; face++) {
                    if (face == requestedFace) {
                        this.compressedData.position(pos);
                        ByteBuffer data = this.compressedData.slice();
                        data.limit(faceLodSizeRounded);
                        return data;
                    }
                    pos += faceLodSizeRounded;
                }
                continue;
            } else {
                pos += this.numberOfFaces * faceLodSizeRounded;
            }
        }
        return null;
    }

    public Pixmap.Format getFormat() {
        throw new GdxRuntimeException("This TextureData implementation directly handles texture formats.");
    }

    public boolean useMipMaps() {
        return this.useMipMaps;
    }

    public boolean isManaged() {
        return true;
    }
}
