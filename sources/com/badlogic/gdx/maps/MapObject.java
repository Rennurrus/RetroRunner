package com.badlogic.gdx.maps;

import com.badlogic.gdx.graphics.Color;
import com.twi.game.BuildConfig;

public class MapObject {
    private Color color = Color.WHITE.cpy();
    private String name = BuildConfig.FLAVOR;
    private float opacity = 1.0f;
    private MapProperties properties = new MapProperties();
    private boolean visible = true;

    public String getName() {
        return this.name;
    }

    public void setName(String name2) {
        this.name = name2;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color2) {
        this.color = color2;
    }

    public float getOpacity() {
        return this.opacity;
    }

    public void setOpacity(float opacity2) {
        this.opacity = opacity2;
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
}
