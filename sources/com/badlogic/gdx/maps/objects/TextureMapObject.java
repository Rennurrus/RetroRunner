package com.badlogic.gdx.maps.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;

public class TextureMapObject extends MapObject {
    private float originX;
    private float originY;
    private float rotation;
    private float scaleX;
    private float scaleY;
    private TextureRegion textureRegion;
    private float x;
    private float y;

    public float getX() {
        return this.x;
    }

    public void setX(float x2) {
        this.x = x2;
    }

    public float getY() {
        return this.y;
    }

    public void setY(float y2) {
        this.y = y2;
    }

    public float getOriginX() {
        return this.originX;
    }

    public void setOriginX(float x2) {
        this.originX = x2;
    }

    public float getOriginY() {
        return this.originY;
    }

    public void setOriginY(float y2) {
        this.originY = y2;
    }

    public float getScaleX() {
        return this.scaleX;
    }

    public void setScaleX(float x2) {
        this.scaleX = x2;
    }

    public float getScaleY() {
        return this.scaleY;
    }

    public void setScaleY(float y2) {
        this.scaleY = y2;
    }

    public float getRotation() {
        return this.rotation;
    }

    public void setRotation(float rotation2) {
        this.rotation = rotation2;
    }

    public TextureRegion getTextureRegion() {
        return this.textureRegion;
    }

    public void setTextureRegion(TextureRegion region) {
        this.textureRegion = region;
    }

    public TextureMapObject() {
        this((TextureRegion) null);
    }

    public TextureMapObject(TextureRegion textureRegion2) {
        this.x = 0.0f;
        this.y = 0.0f;
        this.originX = 0.0f;
        this.originY = 0.0f;
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.rotation = 0.0f;
        this.textureRegion = null;
        this.textureRegion = textureRegion2;
    }
}
