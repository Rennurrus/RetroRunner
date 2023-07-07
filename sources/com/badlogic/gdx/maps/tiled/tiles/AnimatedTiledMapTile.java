package com.badlogic.gdx.maps.tiled.tiles;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.TimeUtils;

public class AnimatedTiledMapTile implements TiledMapTile {
    private static final long initialTimeOffset = TimeUtils.millis();
    private static long lastTiledMapRenderTime = 0;
    private int[] animationIntervals;
    private TiledMapTile.BlendMode blendMode = TiledMapTile.BlendMode.ALPHA;
    private int frameCount = 0;
    private StaticTiledMapTile[] frameTiles;
    private int id;
    private int loopDuration;
    private MapObjects objects;
    private MapProperties properties;

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

    public int getCurrentFrameIndex() {
        int currentTime = (int) (lastTiledMapRenderTime % ((long) this.loopDuration));
        int i = 0;
        while (true) {
            int[] iArr = this.animationIntervals;
            if (i < iArr.length) {
                int animationInterval = iArr[i];
                if (currentTime <= animationInterval) {
                    return i;
                }
                currentTime -= animationInterval;
                i++;
            } else {
                throw new GdxRuntimeException("Could not determine current animation frame in AnimatedTiledMapTile.  This should never happen.");
            }
        }
    }

    public TiledMapTile getCurrentFrame() {
        return this.frameTiles[getCurrentFrameIndex()];
    }

    public TextureRegion getTextureRegion() {
        return getCurrentFrame().getTextureRegion();
    }

    public void setTextureRegion(TextureRegion textureRegion) {
        throw new GdxRuntimeException("Cannot set the texture region of AnimatedTiledMapTile.");
    }

    public float getOffsetX() {
        return getCurrentFrame().getOffsetX();
    }

    public void setOffsetX(float offsetX) {
        throw new GdxRuntimeException("Cannot set offset of AnimatedTiledMapTile.");
    }

    public float getOffsetY() {
        return getCurrentFrame().getOffsetY();
    }

    public void setOffsetY(float offsetY) {
        throw new GdxRuntimeException("Cannot set offset of AnimatedTiledMapTile.");
    }

    public int[] getAnimationIntervals() {
        return this.animationIntervals;
    }

    public void setAnimationIntervals(int[] intervals) {
        if (intervals.length == this.animationIntervals.length) {
            this.animationIntervals = intervals;
            this.loopDuration = 0;
            for (int i = 0; i < intervals.length; i++) {
                this.loopDuration += intervals[i];
            }
            return;
        }
        throw new GdxRuntimeException("Cannot set " + intervals.length + " frame intervals. The given int[] must have a size of " + this.animationIntervals.length + ".");
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

    public static void updateAnimationBaseTime() {
        lastTiledMapRenderTime = TimeUtils.millis() - initialTimeOffset;
    }

    public AnimatedTiledMapTile(float interval, Array<StaticTiledMapTile> frameTiles2) {
        this.frameTiles = new StaticTiledMapTile[frameTiles2.size];
        this.frameCount = frameTiles2.size;
        this.loopDuration = frameTiles2.size * ((int) (interval * 1000.0f));
        this.animationIntervals = new int[frameTiles2.size];
        for (int i = 0; i < frameTiles2.size; i++) {
            this.frameTiles[i] = frameTiles2.get(i);
            this.animationIntervals[i] = (int) (interval * 1000.0f);
        }
    }

    public AnimatedTiledMapTile(IntArray intervals, Array<StaticTiledMapTile> frameTiles2) {
        this.frameTiles = new StaticTiledMapTile[frameTiles2.size];
        this.frameCount = frameTiles2.size;
        this.animationIntervals = intervals.toArray();
        this.loopDuration = 0;
        for (int i = 0; i < intervals.size; i++) {
            this.frameTiles[i] = frameTiles2.get(i);
            this.loopDuration += intervals.get(i);
        }
    }

    public StaticTiledMapTile[] getFrameTiles() {
        return this.frameTiles;
    }
}
