package com.badlogic.gdx.maps.tiled.tiles;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTile;

public class StaticTiledMapTile implements TiledMapTile {
    private TiledMapTile.BlendMode blendMode = TiledMapTile.BlendMode.ALPHA;
    private int id;
    private MapObjects objects;
    private float offsetX;
    private float offsetY;
    private MapProperties properties;
    private TextureRegion textureRegion;

    public int getId() {
        return this.id;
    }

    public void setId(int id2) {
        this.id = id2;
    }

    public TiledMapTile.BlendMode getBlendMode() {
        return this.blendMode;
    }

    public void setBlendMode(TiledMapTile.BlendMode blendMode2) {
        this.blendMode = blendMode2;
    }

    public MapProperties getProperties() {
        if (this.properties == null) {
            this.properties = new MapProperties();
        }
        return this.properties;
    }

    public MapObjects getObjects() {
        if (this.objects == null) {
            this.objects = new MapObjects();
        }
        return this.objects;
    }

    public TextureRegion getTextureRegion() {
        return this.textureRegion;
    }

    public void setTextureRegion(TextureRegion textureRegion2) {
        this.textureRegion = textureRegion2;
    }

    public float getOffsetX() {
        return this.offsetX;
    }

    public void setOffsetX(float offsetX2) {
        this.offsetX = offsetX2;
    }

    public float getOffsetY() {
        return this.offsetY;
    }

    public void setOffsetY(float offsetY2) {
        this.offsetY = offsetY2;
    }

    public StaticTiledMapTile(TextureRegion textureRegion2) {
        this.textureRegion = textureRegion2;
    }

    public StaticTiledMapTile(StaticTiledMapTile copy) {
        if (copy.properties != null) {
            getProperties().putAll(copy.properties);
        }
        this.objects = copy.objects;
        this.textureRegion = copy.textureRegion;
        this.id = copy.id;
    }
}
