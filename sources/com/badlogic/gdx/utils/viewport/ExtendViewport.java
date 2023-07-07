package com.badlogic.gdx.utils.viewport;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Scaling;

public class ExtendViewport extends Viewport {
    private float maxWorldHeight;
    private float maxWorldWidth;
    private float minWorldHeight;
    private float minWorldWidth;

    public ExtendViewport(float minWorldWidth2, float minWorldHeight2) {
        this(minWorldWidth2, minWorldHeight2, 0.0f, 0.0f, new OrthographicCamera());
    }

    public ExtendViewport(float minWorldWidth2, float minWorldHeight2, Camera camera) {
        this(minWorldWidth2, minWorldHeight2, 0.0f, 0.0f, camera);
    }

    public ExtendViewport(float minWorldWidth2, float minWorldHeight2, float maxWorldWidth2, float maxWorldHeight2) {
        this(minWorldWidth2, minWorldHeight2, maxWorldWidth2, maxWorldHeight2, new OrthographicCamera());
    }

    public ExtendViewport(float minWorldWidth2, float minWorldHeight2, float maxWorldWidth2, float maxWorldHeight2, Camera camera) {
        this.minWorldWidth = minWorldWidth2;
        this.minWorldHeight = minWorldHeight2;
        this.maxWorldWidth = maxWorldWidth2;
        this.maxWorldHeight = maxWorldHeight2;
        setCamera(camera);
    }

    public void update(int screenWidth, int screenHeight, boolean centerCamera) {
        float worldWidth = this.minWorldWidth;
        float worldHeight = this.minWorldHeight;
        Vector2 scaled = Scaling.fit.apply(worldWidth, worldHeight, (float) screenWidth, (float) screenHeight);
        int viewportWidth = Math.round(scaled.x);
        int viewportHeight = Math.round(scaled.y);
        if (viewportWidth < screenWidth) {
            float toViewportSpace = ((float) viewportHeight) / worldHeight;
            float lengthen = ((float) (screenWidth - viewportWidth)) * (worldHeight / ((float) viewportHeight));
            float f = this.maxWorldWidth;
            if (f > 0.0f) {
                lengthen = Math.min(lengthen, f - this.minWorldWidth);
            }
            worldWidth += lengthen;
            viewportWidth += Math.round(lengthen * toViewportSpace);
        } else if (viewportHeight < screenHeight) {
            float toViewportSpace2 = ((float) viewportWidth) / worldWidth;
            float lengthen2 = ((float) (screenHeight - viewportHeight)) * (worldWidth / ((float) viewportWidth));
            float f2 = this.maxWorldHeight;
            if (f2 > 0.0f) {
                lengthen2 = Math.min(lengthen2, f2 - this.minWorldHeight);
            }
            worldHeight += lengthen2;
            viewportHeight += Math.round(lengthen2 * toViewportSpace2);
        }
        setWorldSize(worldWidth, worldHeight);
        setScreenBounds((screenWidth - viewportWidth) / 2, (screenHeight - viewportHeight) / 2, viewportWidth, viewportHeight);
        apply(centerCamera);
    }

    public float getMinWorldWidth() {
        return this.minWorldWidth;
    }

    public void setMinWorldWidth(float minWorldWidth2) {
        this.minWorldWidth = minWorldWidth2;
    }

    public float getMinWorldHeight() {
        return this.minWorldHeight;
    }

    public void setMinWorldHeight(float minWorldHeight2) {
        this.minWorldHeight = minWorldHeight2;
    }

    public float getMaxWorldWidth() {
        return this.maxWorldWidth;
    }

    public void setMaxWorldWidth(float maxWorldWidth2) {
        this.maxWorldWidth = maxWorldWidth2;
    }

    public float getMaxWorldHeight() {
        return this.maxWorldHeight;
    }

    public void setMaxWorldHeight(float maxWorldHeight2) {
        this.maxWorldHeight = maxWorldHeight2;
    }
}
