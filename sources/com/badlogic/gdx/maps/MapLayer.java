package com.badlogic.gdx.maps;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.twi.game.BuildConfig;

public class MapLayer {
    private String name = BuildConfig.FLAVOR;
    private MapObjects objects = new MapObjects();
    private float offsetX;
    private float offsetY;
    private float opacity = 1.0f;
    private MapLayer parent;
    private MapProperties properties = new MapProperties();
    private boolean renderOffsetDirty = true;
    private float renderOffsetX;
    private float renderOffsetY;
    private boolean visible = true;

    public String getName() {
        return this.name;
    }

    public void setName(String name2) {
        this.name = name2;
    }

    public float getOpacity() {
        return this.opacity;
    }

    public void setOpacity(float opacity2) {
        this.opacity = opacity2;
    }

    public float getOffsetX() {
        return this.offsetX;
    }

    public void setOffsetX(float offsetX2) {
        this.offsetX = offsetX2;
        invalidateRenderOffset();
    }

    public float getOffsetY() {
        return this.offsetY;
    }

    public void setOffsetY(float offsetY2) {
        this.offsetY = offsetY2;
        invalidateRenderOffset();
    }

    public float getRenderOffsetX() {
        if (this.renderOffsetDirty) {
            calculateRenderOffsets();
        }
        return this.renderOffsetX;
    }

    public float getRenderOffsetY() {
        if (this.renderOffsetDirty) {
            calculateRenderOffsets();
        }
        return this.renderOffsetY;
    }

    public void invalidateRenderOffset() {
        this.renderOffsetDirty = true;
    }

    public MapLayer getParent() {
        return this.parent;
    }

    public void setParent(MapLayer parent2) {
        if (parent2 != this) {
            this.parent = parent2;
            return;
        }
        throw new GdxRuntimeException("Can't set self as the parent");
    }

    public MapObjects getObjects() {
        return this.objects;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible2) {
        this.visible = visible2;
    }

    public MapProperties getProperties() {
        return this.properties;
    }

    /* access modifiers changed from: protected */
    public void calculateRenderOffsets() {
        MapLayer mapLayer = this.parent;
        if (mapLayer != null) {
            mapLayer.calculateRenderOffsets();
            this.renderOffsetX = this.parent.getRenderOffsetX() + this.offsetX;
            this.renderOffsetY = this.parent.getRenderOffsetY() + this.offsetY;
        } else {
            this.renderOffsetX = this.offsetX;
            this.renderOffsetY = this.offsetY;
        }
        this.renderOffsetDirty = false;
    }
}
