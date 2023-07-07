package com.badlogic.gdx;

import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.GLVersion;

public interface Graphics {

    public enum GraphicsType {
        AndroidGL,
        LWJGL,
        WebGL,
        iOSGL,
        JGLFW,
        Mock,
        LWJGL3
    }

    int getBackBufferHeight();

    int getBackBufferWidth();

    BufferFormat getBufferFormat();

    float getDeltaTime();

    float getDensity();

    DisplayMode getDisplayMode();

    DisplayMode getDisplayMode(Monitor monitor);

    DisplayMode[] getDisplayModes();

    DisplayMode[] getDisplayModes(Monitor monitor);

    long getFrameId();

    int getFramesPerSecond();

    GL20 getGL20();

    GL30 getGL30();

    GLVersion getGLVersion();

    int getHeight();

    Monitor getMonitor();

    Monitor[] getMonitors();

    float getPpcX();

    float getPpcY();

    float getPpiX();

    float getPpiY();

    Monitor getPrimaryMonitor();

    float getRawDeltaTime();

    GraphicsType getType();

    int getWidth();

    boolean isContinuousRendering();

    boolean isFullscreen();

    boolean isGL30Available();

    Cursor newCursor(Pixmap pixmap, int i, int i2);

    void requestRendering();

    void setContinuousRendering(boolean z);

    void setCursor(Cursor cursor);

    boolean setFullscreenMode(DisplayMode displayMode);

    void setGL20(GL20 gl20);

    void setGL30(GL30 gl30);

    void setResizable(boolean z);

    void setSystemCursor(Cursor.SystemCursor systemCursor);

    void setTitle(String str);

    void setUndecorated(boolean z);

    void setVSync(boolean z);

    boolean setWindowedMode(int i, int i2);

    boolean supportsDisplayModeChange();

    boolean supportsExtension(String str);

    public static class DisplayMode {
        public final int bitsPerPixel;
        public final int height;
        public final int refreshRate;
        public final int width;

        protected DisplayMode(int width2, int height2, int refreshRate2, int bitsPerPixel2) {
            this.width = width2;
            this.height = height2;
            this.refreshRate = refreshRate2;
            this.bitsPerPixel = bitsPerPixel2;
        }

        public String toString() {
            return this.width + "x" + this.height + ", bpp: " + this.bitsPerPixel + ", hz: " + this.refreshRate;
        }
    }

    public static class Monitor {
        public final String name;
        public final int virtualX;
        public final int virtualY;

        protected Monitor(int virtualX2, int virtualY2, String name2) {
            this.virtualX = virtualX2;
            this.virtualY = virtualY2;
            this.name = name2;
        }
    }

    public static class BufferFormat {
        public final int a;
        public final int b;
        public final boolean coverageSampling;
        public final int depth;
        public final int g;
        public final int r;
        public final int samples;
        public final int stencil;

        public BufferFormat(int r2, int g2, int b2, int a2, int depth2, int stencil2, int samples2, boolean coverageSampling2) {
            this.r = r2;
            this.g = g2;
            this.b = b2;
            this.a = a2;
            this.depth = depth2;
            this.stencil = stencil2;
            this.samples = samples2;
            this.coverageSampling = coverageSampling2;
        }

        public String toString() {
            return "r: " + this.r + ", g: " + this.g + ", b: " + this.b + ", a: " + this.a + ", depth: " + this.depth + ", stencil: " + this.stencil + ", num samples: " + this.samples + ", coverage sampling: " + this.coverageSampling;
        }
    }
}
