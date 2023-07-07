package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class Gdx2DPixmap implements Disposable {
    public static final int GDX2D_BLEND_NONE = 0;
    public static final int GDX2D_BLEND_SRC_OVER = 1;
    public static final int GDX2D_FORMAT_ALPHA = 1;
    public static final int GDX2D_FORMAT_LUMINANCE_ALPHA = 2;
    public static final int GDX2D_FORMAT_RGB565 = 5;
    public static final int GDX2D_FORMAT_RGB888 = 3;
    public static final int GDX2D_FORMAT_RGBA4444 = 6;
    public static final int GDX2D_FORMAT_RGBA8888 = 4;
    public static final int GDX2D_SCALE_LINEAR = 1;
    public static final int GDX2D_SCALE_NEAREST = 0;
    long basePtr;
    int format;
    int height;
    long[] nativeData = new long[4];
    ByteBuffer pixelPtr;
    int width;

    private static native void clear(long j, int i);

    private static native void drawCircle(long j, int i, int i2, int i3, int i4);

    private static native void drawLine(long j, int i, int i2, int i3, int i4, int i5);

    private static native void drawPixmap(long j, long j2, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8);

    private static native void drawRect(long j, int i, int i2, int i3, int i4, int i5);

    private static native void fillCircle(long j, int i, int i2, int i3, int i4);

    private static native void fillRect(long j, int i, int i2, int i3, int i4, int i5);

    private static native void fillTriangle(long j, int i, int i2, int i3, int i4, int i5, int i6, int i7);

    private static native void free(long j);

    public static native String getFailureReason();

    private static native int getPixel(long j, int i, int i2);

    private static native ByteBuffer load(long[] jArr, byte[] bArr, int i, int i2);

    private static native ByteBuffer newPixmap(long[] jArr, int i, int i2, int i3);

    private static native void setBlend(long j, int i);

    private static native void setPixel(long j, int i, int i2, int i3);

    private static native void setScale(long j, int i);

    public static int toGlFormat(int format2) {
        switch (format2) {
            case 1:
                return GL20.GL_ALPHA;
            case 2:
                return GL20.GL_LUMINANCE_ALPHA;
            case 3:
            case 5:
                return GL20.GL_RGB;
            case 4:
            case 6:
                return GL20.GL_RGBA;
            default:
                throw new GdxRuntimeException("unknown format: " + format2);
        }
    }

    public static int toGlType(int format2) {
        switch (format2) {
            case 1:
            case 2:
            case 3:
            case 4:
                return GL20.GL_UNSIGNED_BYTE;
            case 5:
                return GL20.GL_UNSIGNED_SHORT_5_6_5;
            case 6:
                return GL20.GL_UNSIGNED_SHORT_4_4_4_4;
            default:
                throw new GdxRuntimeException("unknown format: " + format2);
        }
    }

    public Gdx2DPixmap(byte[] encodedData, int offset, int len, int requestedFormat) throws IOException {
        this.pixelPtr = load(this.nativeData, encodedData, offset, len);
        if (this.pixelPtr != null) {
            long[] jArr = this.nativeData;
            this.basePtr = jArr[0];
            this.width = (int) jArr[1];
            this.height = (int) jArr[2];
            this.format = (int) jArr[3];
            if (requestedFormat != 0 && requestedFormat != this.format) {
                convert(requestedFormat);
                return;
            }
            return;
        }
        throw new IOException("Error loading pixmap: " + getFailureReason());
    }

    public Gdx2DPixmap(InputStream in, int requestedFormat) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream(GL20.GL_STENCIL_BUFFER_BIT);
        byte[] buffer = new byte[GL20.GL_STENCIL_BUFFER_BIT];
        while (true) {
            int read = in.read(buffer);
            int readBytes = read;
            if (read == -1) {
                break;
            }
            bytes.write(buffer, 0, readBytes);
        }
        byte[] buffer2 = bytes.toByteArray();
        this.pixelPtr = load(this.nativeData, buffer2, 0, buffer2.length);
        if (this.pixelPtr != null) {
            long[] jArr = this.nativeData;
            this.basePtr = jArr[0];
            this.width = (int) jArr[1];
            this.height = (int) jArr[2];
            this.format = (int) jArr[3];
            if (requestedFormat != 0 && requestedFormat != this.format) {
                convert(requestedFormat);
                return;
            }
            return;
        }
        throw new IOException("Error loading pixmap: " + getFailureReason());
    }

    public Gdx2DPixmap(int width2, int height2, int format2) throws GdxRuntimeException {
        this.pixelPtr = newPixmap(this.nativeData, width2, height2, format2);
        if (this.pixelPtr != null) {
            long[] jArr = this.nativeData;
            this.basePtr = jArr[0];
            this.width = (int) jArr[1];
            this.height = (int) jArr[2];
            this.format = (int) jArr[3];
            return;
        }
        throw new GdxRuntimeException("Error loading pixmap.");
    }

    public Gdx2DPixmap(ByteBuffer pixelPtr2, long[] nativeData2) {
        this.pixelPtr = pixelPtr2;
        this.basePtr = nativeData2[0];
        this.width = (int) nativeData2[1];
        this.height = (int) nativeData2[2];
        this.format = (int) nativeData2[3];
    }

    private void convert(int requestedFormat) {
        Gdx2DPixmap pixmap = new Gdx2DPixmap(this.width, this.height, requestedFormat);
        pixmap.setBlend(0);
        pixmap.drawPixmap(this, 0, 0, 0, 0, this.width, this.height);
        dispose();
        this.basePtr = pixmap.basePtr;
        this.format = pixmap.format;
        this.height = pixmap.height;
        this.nativeData = pixmap.nativeData;
        this.pixelPtr = pixmap.pixelPtr;
        this.width = pixmap.width;
    }

    public void dispose() {
        free(this.basePtr);
    }

    public void clear(int color) {
        clear(this.basePtr, color);
    }

    public void setPixel(int x, int y, int color) {
        setPixel(this.basePtr, x, y, color);
    }

    public int getPixel(int x, int y) {
        return getPixel(this.basePtr, x, y);
    }

    public void drawLine(int x, int y, int x2, int y2, int color) {
        drawLine(this.basePtr, x, y, x2, y2, color);
    }

    public void drawRect(int x, int y, int width2, int height2, int color) {
        drawRect(this.basePtr, x, y, width2, height2, color);
    }

    public void drawCircle(int x, int y, int radius, int color) {
        drawCircle(this.basePtr, x, y, radius, color);
    }

    public void fillRect(int x, int y, int width2, int height2, int color) {
        fillRect(this.basePtr, x, y, width2, height2, color);
    }

    public void fillCircle(int x, int y, int radius, int color) {
        fillCircle(this.basePtr, x, y, radius, color);
    }

    public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3, int color) {
        fillTriangle(this.basePtr, x1, y1, x2, y2, x3, y3, color);
    }

    public void drawPixmap(Gdx2DPixmap src, int srcX, int srcY, int dstX, int dstY, int width2, int height2) {
        drawPixmap(src.basePtr, this.basePtr, srcX, srcY, width2, height2, dstX, dstY, width2, height2);
    }

    public void drawPixmap(Gdx2DPixmap src, int srcX, int srcY, int srcWidth, int srcHeight, int dstX, int dstY, int dstWidth, int dstHeight) {
        drawPixmap(src.basePtr, this.basePtr, srcX, srcY, srcWidth, srcHeight, dstX, dstY, dstWidth, dstHeight);
    }

    public void setBlend(int blend) {
        setBlend(this.basePtr, blend);
    }

    public void setScale(int scale) {
        setScale(this.basePtr, scale);
    }

    public static Gdx2DPixmap newPixmap(InputStream in, int requestedFormat) {
        try {
            return new Gdx2DPixmap(in, requestedFormat);
        } catch (IOException e) {
            return null;
        }
    }

    public static Gdx2DPixmap newPixmap(int width2, int height2, int format2) {
        try {
            return new Gdx2DPixmap(width2, height2, format2);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public ByteBuffer getPixels() {
        return this.pixelPtr;
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    public int getFormat() {
        return this.format;
    }

    public int getGLInternalFormat() {
        return toGlFormat(this.format);
    }

    public int getGLFormat() {
        return getGLInternalFormat();
    }

    public int getGLType() {
        return toGlType(this.format);
    }

    public String getFormatString() {
        switch (this.format) {
            case 1:
                return "alpha";
            case 2:
                return "luminance alpha";
            case 3:
                return "rgb888";
            case 4:
                return "rgba8888";
            case 5:
                return "rgb565";
            case 6:
                return "rgba4444";
            default:
                return "unknown";
        }
    }
}
