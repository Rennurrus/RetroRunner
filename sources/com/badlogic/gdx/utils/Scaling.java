package com.badlogic.gdx.utils;

import com.badlogic.gdx.math.Vector2;

public enum Scaling {
    fit,
    fill,
    fillX,
    fillY,
    stretch,
    stretchX,
    stretchY,
    none;
    
    private static final Vector2 temp = null;

    static {
        temp = new Vector2();
    }

    public Vector2 apply(float sourceWidth, float sourceHeight, float targetWidth, float targetHeight) {
        switch (this) {
            case fit:
                float scale = targetHeight / targetWidth > sourceHeight / sourceWidth ? targetWidth / sourceWidth : targetHeight / sourceHeight;
                Vector2 vector2 = temp;
                vector2.x = sourceWidth * scale;
                vector2.y = sourceHeight * scale;
                break;
            case fill:
                float scale2 = targetHeight / targetWidth < sourceHeight / sourceWidth ? targetWidth / sourceWidth : targetHeight / sourceHeight;
                Vector2 vector22 = temp;
                vector22.x = sourceWidth * scale2;
                vector22.y = sourceHeight * scale2;
                break;
            case fillX:
                float scale3 = targetWidth / sourceWidth;
                Vector2 vector23 = temp;
                vector23.x = sourceWidth * scale3;
                vector23.y = sourceHeight * scale3;
                break;
            case fillY:
                float scale4 = targetHeight / sourceHeight;
                Vector2 vector24 = temp;
                vector24.x = sourceWidth * scale4;
                vector24.y = sourceHeight * scale4;
                break;
            case stretch:
                Vector2 vector25 = temp;
                vector25.x = targetWidth;
                vector25.y = targetHeight;
                break;
            case stretchX:
                Vector2 vector26 = temp;
                vector26.x = targetWidth;
                vector26.y = sourceHeight;
                break;
            case stretchY:
                Vector2 vector27 = temp;
                vector27.x = sourceWidth;
                vector27.y = targetHeight;
                break;
            case none:
                Vector2 vector28 = temp;
                vector28.x = sourceWidth;
                vector28.y = sourceHeight;
                break;
        }
        return temp;
    }
}
