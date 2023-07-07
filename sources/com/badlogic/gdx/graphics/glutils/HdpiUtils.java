package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.Gdx;

public class HdpiUtils {
    private static HdpiMode mode = HdpiMode.Logical;

    public static void setMode(HdpiMode mode2) {
        mode = mode2;
    }

    public static void glScissor(int x, int y, int width, int height) {
        if (mode != HdpiMode.Logical || (Gdx.graphics.getWidth() == Gdx.graphics.getBackBufferWidth() && Gdx.graphics.getHeight() == Gdx.graphics.getBackBufferHeight())) {
            Gdx.gl.glScissor(x, y, width, height);
        } else {
            Gdx.gl.glScissor(toBackBufferX(x), toBackBufferY(y), toBackBufferX(width), toBackBufferY(height));
        }
    }

    public static void glViewport(int x, int y, int width, int height) {
        if (mode != HdpiMode.Logical || (Gdx.graphics.getWidth() == Gdx.graphics.getBackBufferWidth() && Gdx.graphics.getHeight() == Gdx.graphics.getBackBufferHeight())) {
            Gdx.gl.glViewport(x, y, width, height);
        } else {
            Gdx.gl.glViewport(toBackBufferX(x), toBackBufferY(y), toBackBufferX(width), toBackBufferY(height));
        }
    }

    public static int toLogicalX(int backBufferX) {
        return (int) (((float) (Gdx.graphics.getWidth() * backBufferX)) / ((float) Gdx.graphics.getBackBufferWidth()));
    }

    public static int toLogicalY(int backBufferY) {
        return (int) (((float) (Gdx.graphics.getHeight() * backBufferY)) / ((float) Gdx.graphics.getBackBufferHeight()));
    }

    public static int toBackBufferX(int logicalX) {
        return (int) (((float) (Gdx.graphics.getBackBufferWidth() * logicalX)) / ((float) Gdx.graphics.getWidth()));
    }

    public static int toBackBufferY(int logicalY) {
        return (int) (((float) (Gdx.graphics.getBackBufferHeight() * logicalY)) / ((float) Gdx.graphics.getHeight()));
    }
}
