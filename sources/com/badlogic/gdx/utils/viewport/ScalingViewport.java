package com.badlogic.gdx.utils.viewport;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Scaling;

public class ScalingViewport extends Viewport {
    private Scaling scaling;

    public ScalingViewport(Scaling scaling2, float worldWidth, float worldHeight) {
        this(scaling2, worldWidth, worldHeight, new OrthographicCamera());
    }

    public ScalingViewport(Scaling scaling2, float worldWidth, float worldHeight, Camera camera) {
        this.scaling = scaling2;
        setWorldSize(worldWidth, worldHeight);
        setCamera(camera);
    }

    public void update(int screenWidth, int screenHeight, boolean centerCamera) {
        Vector2 scaled = this.scaling.apply(getWorldWidth(), getWorldHeight(), (float) screenWidth, (float) screenHeight);
        int viewportWidth = Math.round(scaled.x);
        int viewportHeight = Math.round(scaled.y);
        setScreenBounds((screenWidth - viewportWidth) / 2, (screenHeight - viewportHeight) / 2, viewportWidth, viewportHeight);
        apply(centerCamera);
    }

    public Scaling getScaling() {
        return this.scaling;
    }

    public void setScaling(Scaling scaling2) {
        this.scaling = scaling2;
    }
}
