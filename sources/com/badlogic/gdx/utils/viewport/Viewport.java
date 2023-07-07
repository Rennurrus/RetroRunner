package com.badlogic.gdx.utils.viewport;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.HdpiUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;

public abstract class Viewport {
    private Camera camera;
    private int screenHeight;
    private int screenWidth;
    private int screenX;
    private int screenY;
    private final Vector3 tmp = new Vector3();
    private float worldHeight;
    private float worldWidth;

    public void apply() {
        apply(false);
    }

    public void apply(boolean centerCamera) {
        HdpiUtils.glViewport(this.screenX, this.screenY, this.screenWidth, this.screenHeight);
        Camera camera2 = this.camera;
        camera2.viewportWidth = this.worldWidth;
        camera2.viewportHeight = this.worldHeight;
        if (centerCamera) {
            camera2.position.set(this.worldWidth / 2.0f, this.worldHeight / 2.0f, 0.0f);
        }
        this.camera.update();
    }

    public final void update(int screenWidth2, int screenHeight2) {
        update(screenWidth2, screenHeight2, false);
    }

    public void update(int screenWidth2, int screenHeight2, boolean centerCamera) {
        apply(centerCamera);
    }

    public Vector2 unproject(Vector2 screenCoords) {
        this.tmp.set(screenCoords.x, screenCoords.y, 1.0f);
        this.camera.unproject(this.tmp, (float) this.screenX, (float) this.screenY, (float) this.screenWidth, (float) this.screenHeight);
        screenCoords.set(this.tmp.x, this.tmp.y);
        return screenCoords;
    }

    public Vector2 project(Vector2 worldCoords) {
        this.tmp.set(worldCoords.x, worldCoords.y, 1.0f);
        this.camera.project(this.tmp, (float) this.screenX, (float) this.screenY, (float) this.screenWidth, (float) this.screenHeight);
        worldCoords.set(this.tmp.x, this.tmp.y);
        return worldCoords;
    }

    public Vector3 unproject(Vector3 screenCoords) {
        this.camera.unproject(screenCoords, (float) this.screenX, (float) this.screenY, (float) this.screenWidth, (float) this.screenHeight);
        return screenCoords;
    }

    public Vector3 project(Vector3 worldCoords) {
        this.camera.project(worldCoords, (float) this.screenX, (float) this.screenY, (float) this.screenWidth, (float) this.screenHeight);
        return worldCoords;
    }

    public Ray getPickRay(float screenX2, float screenY2) {
        return this.camera.getPickRay(screenX2, screenY2, (float) this.screenX, (float) this.screenY, (float) this.screenWidth, (float) this.screenHeight);
    }

    public void calculateScissors(Matrix4 batchTransform, Rectangle area, Rectangle scissor) {
        ScissorStack.calculateScissors(this.camera, (float) this.screenX, (float) this.screenY, (float) this.screenWidth, (float) this.screenHeight, batchTransform, area, scissor);
    }

    public Vector2 toScreenCoordinates(Vector2 worldCoords, Matrix4 transformMatrix) {
        this.tmp.set(worldCoords.x, worldCoords.y, 0.0f);
        this.tmp.mul(transformMatrix);
        this.camera.project(this.tmp);
        this.tmp.y = ((float) Gdx.graphics.getHeight()) - this.tmp.y;
        worldCoords.x = this.tmp.x;
        worldCoords.y = this.tmp.y;
        return worldCoords;
    }

    public Camera getCamera() {
        return this.camera;
    }

    public void setCamera(Camera camera2) {
        this.camera = camera2;
    }

    public float getWorldWidth() {
        return this.worldWidth;
    }

    public void setWorldWidth(float worldWidth2) {
        this.worldWidth = worldWidth2;
    }

    public float getWorldHeight() {
        return this.worldHeight;
    }

    public void setWorldHeight(float worldHeight2) {
        this.worldHeight = worldHeight2;
    }

    public void setWorldSize(float worldWidth2, float worldHeight2) {
        this.worldWidth = worldWidth2;
        this.worldHeight = worldHeight2;
    }

    public int getScreenX() {
        return this.screenX;
    }

    public void setScreenX(int screenX2) {
        this.screenX = screenX2;
    }

    public int getScreenY() {
        return this.screenY;
    }

    public void setScreenY(int screenY2) {
        this.screenY = screenY2;
    }

    public int getScreenWidth() {
        return this.screenWidth;
    }

    public void setScreenWidth(int screenWidth2) {
        this.screenWidth = screenWidth2;
    }

    public int getScreenHeight() {
        return this.screenHeight;
    }

    public void setScreenHeight(int screenHeight2) {
        this.screenHeight = screenHeight2;
    }

    public void setScreenPosition(int screenX2, int screenY2) {
        this.screenX = screenX2;
        this.screenY = screenY2;
    }

    public void setScreenSize(int screenWidth2, int screenHeight2) {
        this.screenWidth = screenWidth2;
        this.screenHeight = screenHeight2;
    }

    public void setScreenBounds(int screenX2, int screenY2, int screenWidth2, int screenHeight2) {
        this.screenX = screenX2;
        this.screenY = screenY2;
        this.screenWidth = screenWidth2;
        this.screenHeight = screenHeight2;
    }

    public int getLeftGutterWidth() {
        return this.screenX;
    }

    public int getRightGutterX() {
        return this.screenX + this.screenWidth;
    }

    public int getRightGutterWidth() {
        return Gdx.graphics.getWidth() - (this.screenX + this.screenWidth);
    }

    public int getBottomGutterHeight() {
        return this.screenY;
    }

    public int getTopGutterY() {
        return this.screenY + this.screenHeight;
    }

    public int getTopGutterHeight() {
        return Gdx.graphics.getHeight() - (this.screenY + this.screenHeight);
    }
}
