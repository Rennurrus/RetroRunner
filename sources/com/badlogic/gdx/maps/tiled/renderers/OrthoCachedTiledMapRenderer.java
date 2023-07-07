package com.badlogic.gdx.maps.tiled.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapImageLayer;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import java.util.Iterator;

public class OrthoCachedTiledMapRenderer implements TiledMapRenderer, Disposable {
    protected static final int NUM_VERTICES = 20;
    private static final float tolerance = 1.0E-5f;
    protected boolean blending;
    protected final Rectangle cacheBounds;
    protected boolean cached;
    protected boolean canCacheMoreE;
    protected boolean canCacheMoreN;
    protected boolean canCacheMoreS;
    protected boolean canCacheMoreW;
    protected int count;
    protected final TiledMap map;
    protected float maxTileHeight;
    protected float maxTileWidth;
    protected float overCache;
    protected final SpriteCache spriteCache;
    protected float unitScale;
    protected final float[] vertices;
    protected final Rectangle viewBounds;

    public OrthoCachedTiledMapRenderer(TiledMap map2) {
        this(map2, 1.0f, 2000);
    }

    public OrthoCachedTiledMapRenderer(TiledMap map2, float unitScale2) {
        this(map2, unitScale2, 2000);
    }

    public OrthoCachedTiledMapRenderer(TiledMap map2, float unitScale2, int cacheSize) {
        this.vertices = new float[20];
        this.viewBounds = new Rectangle();
        this.cacheBounds = new Rectangle();
        this.overCache = 0.5f;
        this.map = map2;
        this.unitScale = unitScale2;
        this.spriteCache = new SpriteCache(cacheSize, true);
    }

    public void setView(OrthographicCamera camera) {
        this.spriteCache.setProjectionMatrix(camera.combined);
        float width = (camera.viewportWidth * camera.zoom) + (this.maxTileWidth * 2.0f * this.unitScale);
        float height = (camera.viewportHeight * camera.zoom) + (this.maxTileHeight * 2.0f * this.unitScale);
        this.viewBounds.set(camera.position.x - (width / 2.0f), camera.position.y - (height / 2.0f), width, height);
        if ((this.canCacheMoreW && this.viewBounds.x < this.cacheBounds.x - tolerance) || ((this.canCacheMoreS && this.viewBounds.y < this.cacheBounds.y - tolerance) || ((this.canCacheMoreE && this.viewBounds.x + this.viewBounds.width > this.cacheBounds.x + this.cacheBounds.width + tolerance) || (this.canCacheMoreN && this.viewBounds.y + this.viewBounds.height > this.cacheBounds.y + this.cacheBounds.height + tolerance)))) {
            this.cached = false;
        }
    }

    public void setView(Matrix4 projection, float x, float y, float width, float height) {
        this.spriteCache.setProjectionMatrix(projection);
        float f = this.maxTileWidth;
        float f2 = this.unitScale;
        float x2 = x - (f * f2);
        float f3 = this.maxTileHeight;
        this.viewBounds.set(x2, y - (f3 * f2), width + (f * 2.0f * f2), height + (f3 * 2.0f * f2));
        if ((this.canCacheMoreW && this.viewBounds.x < this.cacheBounds.x - tolerance) || ((this.canCacheMoreS && this.viewBounds.y < this.cacheBounds.y - tolerance) || ((this.canCacheMoreE && this.viewBounds.x + this.viewBounds.width > this.cacheBounds.x + this.cacheBounds.width + tolerance) || (this.canCacheMoreN && this.viewBounds.y + this.viewBounds.height > this.cacheBounds.y + this.cacheBounds.height + tolerance)))) {
            this.cached = false;
        }
    }

    public void render() {
        if (!this.cached) {
            this.cached = true;
            this.count = 0;
            this.spriteCache.clear();
            float extraWidth = this.viewBounds.width * this.overCache;
            float extraHeight = this.viewBounds.height * this.overCache;
            this.cacheBounds.x = this.viewBounds.x - extraWidth;
            this.cacheBounds.y = this.viewBounds.y - extraHeight;
            this.cacheBounds.width = this.viewBounds.width + (extraWidth * 2.0f);
            this.cacheBounds.height = this.viewBounds.height + (2.0f * extraHeight);
            Iterator<MapLayer> it = this.map.getLayers().iterator();
            while (it.hasNext()) {
                MapLayer layer = it.next();
                this.spriteCache.beginCache();
                if (layer instanceof TiledMapTileLayer) {
                    renderTileLayer((TiledMapTileLayer) layer);
                } else if (layer instanceof TiledMapImageLayer) {
                    renderImageLayer((TiledMapImageLayer) layer);
                }
                this.spriteCache.endCache();
            }
        }
        if (this.blending) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }
        this.spriteCache.begin();
        MapLayers mapLayers = this.map.getLayers();
        int j = mapLayers.getCount();
        for (int i = 0; i < j; i++) {
            MapLayer layer2 = mapLayers.get(i);
            if (layer2.isVisible()) {
                this.spriteCache.draw(i);
                renderObjects(layer2);
            }
        }
        this.spriteCache.end();
        if (this.blending) {
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }

    public void render(int[] layers) {
        if (!this.cached) {
            this.cached = true;
            this.count = 0;
            this.spriteCache.clear();
            float extraWidth = this.viewBounds.width * this.overCache;
            float extraHeight = this.viewBounds.height * this.overCache;
            this.cacheBounds.x = this.viewBounds.x - extraWidth;
            this.cacheBounds.y = this.viewBounds.y - extraHeight;
            this.cacheBounds.width = this.viewBounds.width + (extraWidth * 2.0f);
            this.cacheBounds.height = this.viewBounds.height + (2.0f * extraHeight);
            Iterator<MapLayer> it = this.map.getLayers().iterator();
            while (it.hasNext()) {
                MapLayer layer = it.next();
                this.spriteCache.beginCache();
                if (layer instanceof TiledMapTileLayer) {
                    renderTileLayer((TiledMapTileLayer) layer);
                } else if (layer instanceof TiledMapImageLayer) {
                    renderImageLayer((TiledMapImageLayer) layer);
                }
                this.spriteCache.endCache();
            }
        }
        if (this.blending) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }
        this.spriteCache.begin();
        MapLayers mapLayers = this.map.getLayers();
        for (int i : layers) {
            MapLayer layer2 = mapLayers.get(i);
            if (layer2.isVisible()) {
                this.spriteCache.draw(i);
                renderObjects(layer2);
            }
        }
        this.spriteCache.end();
        if (this.blending) {
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }

    public void renderObjects(MapLayer layer) {
        Iterator<MapObject> it = layer.getObjects().iterator();
        while (it.hasNext()) {
            renderObject(it.next());
        }
    }

    public void renderObject(MapObject object) {
    }

    public void renderTileLayer(TiledMapTileLayer layer) {
        float color;
        int row1;
        float layerOffsetY;
        float layerTileHeight;
        float layerTileWidth;
        float layerOffsetX;
        int col2;
        int col1;
        int layerHeight;
        int layerWidth;
        OrthoCachedTiledMapRenderer orthoCachedTiledMapRenderer = this;
        float color2 = Color.toFloatBits(1.0f, 1.0f, 1.0f, layer.getOpacity());
        int layerWidth2 = layer.getWidth();
        int layerHeight2 = layer.getHeight();
        float layerTileWidth2 = layer.getTileWidth() * orthoCachedTiledMapRenderer.unitScale;
        float layerTileHeight2 = layer.getTileHeight() * orthoCachedTiledMapRenderer.unitScale;
        float layerOffsetX2 = layer.getRenderOffsetX() * orthoCachedTiledMapRenderer.unitScale;
        float layerOffsetY2 = (-layer.getRenderOffsetY()) * orthoCachedTiledMapRenderer.unitScale;
        int col12 = Math.max(0, (int) ((orthoCachedTiledMapRenderer.cacheBounds.x - layerOffsetX2) / layerTileWidth2));
        int col22 = Math.min(layerWidth2, (int) ((((orthoCachedTiledMapRenderer.cacheBounds.x + orthoCachedTiledMapRenderer.cacheBounds.width) + layerTileWidth2) - layerOffsetX2) / layerTileWidth2));
        int row12 = Math.max(0, (int) ((orthoCachedTiledMapRenderer.cacheBounds.y - layerOffsetY2) / layerTileHeight2));
        int row2 = Math.min(layerHeight2, (int) ((((orthoCachedTiledMapRenderer.cacheBounds.y + orthoCachedTiledMapRenderer.cacheBounds.height) + layerTileHeight2) - layerOffsetY2) / layerTileHeight2));
        orthoCachedTiledMapRenderer.canCacheMoreN = row2 < layerHeight2;
        orthoCachedTiledMapRenderer.canCacheMoreE = col22 < layerWidth2;
        orthoCachedTiledMapRenderer.canCacheMoreW = col12 > 0;
        orthoCachedTiledMapRenderer.canCacheMoreS = row12 > 0;
        float[] vertices2 = orthoCachedTiledMapRenderer.vertices;
        int row = row2;
        while (row >= row12) {
            int col = col12;
            while (col < col22) {
                TiledMapTileLayer.Cell cell = layer.getCell(col, row);
                if (cell == null) {
                    color = color2;
                    layerWidth = layerWidth2;
                    layerHeight = layerHeight2;
                    layerTileWidth = layerTileWidth2;
                    layerTileHeight = layerTileHeight2;
                    layerOffsetX = layerOffsetX2;
                    layerOffsetY = layerOffsetY2;
                    col1 = col12;
                    col2 = col22;
                    row1 = row12;
                } else {
                    TiledMapTile tile = cell.getTile();
                    if (tile == null) {
                        color = color2;
                        layerWidth = layerWidth2;
                        layerHeight = layerHeight2;
                        layerTileWidth = layerTileWidth2;
                        layerTileHeight = layerTileHeight2;
                        layerOffsetX = layerOffsetX2;
                        layerOffsetY = layerOffsetY2;
                        col1 = col12;
                        col2 = col22;
                        row1 = row12;
                    } else {
                        layerWidth = layerWidth2;
                        orthoCachedTiledMapRenderer.count++;
                        boolean flipX = cell.getFlipHorizontally();
                        boolean flipY = cell.getFlipVertically();
                        layerHeight = layerHeight2;
                        int rotations = cell.getRotation();
                        TextureRegion region = tile.getTextureRegion();
                        col1 = col12;
                        Texture texture = region.getTexture();
                        col2 = col22;
                        layerTileWidth = layerTileWidth2;
                        float x1 = (((float) col) * layerTileWidth2) + (tile.getOffsetX() * orthoCachedTiledMapRenderer.unitScale) + layerOffsetX2;
                        layerTileHeight = layerTileHeight2;
                        float y1 = (((float) row) * layerTileHeight2) + (tile.getOffsetY() * orthoCachedTiledMapRenderer.unitScale) + layerOffsetY2;
                        layerOffsetX = layerOffsetX2;
                        float x2 = (((float) region.getRegionWidth()) * orthoCachedTiledMapRenderer.unitScale) + x1;
                        layerOffsetY = layerOffsetY2;
                        float y2 = (((float) region.getRegionHeight()) * orthoCachedTiledMapRenderer.unitScale) + y1;
                        float adjustX = 0.5f / ((float) texture.getWidth());
                        row1 = row12;
                        float adjustY = 0.5f / ((float) texture.getHeight());
                        float u1 = region.getU() + adjustX;
                        float v1 = region.getV2() - adjustY;
                        float u2 = region.getU2() - adjustX;
                        float v2 = region.getV() + adjustY;
                        vertices2[0] = x1;
                        vertices2[1] = y1;
                        float f = adjustX;
                        vertices2[2] = color2;
                        vertices2[3] = u1;
                        vertices2[4] = v1;
                        vertices2[5] = x1;
                        vertices2[6] = y2;
                        vertices2[7] = color2;
                        vertices2[8] = u1;
                        vertices2[9] = v2;
                        vertices2[10] = x2;
                        vertices2[11] = y2;
                        vertices2[12] = color2;
                        vertices2[13] = u2;
                        vertices2[14] = v2;
                        vertices2[15] = x2;
                        vertices2[16] = y1;
                        vertices2[17] = color2;
                        vertices2[18] = u2;
                        vertices2[19] = v1;
                        if (flipX) {
                            float temp = vertices2[3];
                            vertices2[3] = vertices2[13];
                            vertices2[13] = temp;
                            float temp2 = vertices2[8];
                            vertices2[8] = vertices2[18];
                            vertices2[18] = temp2;
                        }
                        if (flipY) {
                            float temp3 = vertices2[4];
                            vertices2[4] = vertices2[14];
                            vertices2[14] = temp3;
                            float temp4 = vertices2[9];
                            vertices2[9] = vertices2[19];
                            vertices2[19] = temp4;
                        }
                        if (rotations != 0) {
                            if (rotations == 1) {
                                float tempU = vertices2[4];
                                vertices2[4] = vertices2[9];
                                vertices2[9] = vertices2[14];
                                vertices2[14] = vertices2[19];
                                vertices2[19] = tempU;
                                float tempU2 = vertices2[3];
                                vertices2[3] = vertices2[8];
                                vertices2[8] = vertices2[13];
                                vertices2[13] = vertices2[18];
                                vertices2[18] = tempU2;
                            } else if (rotations == 2) {
                                float tempU3 = vertices2[3];
                                vertices2[3] = vertices2[13];
                                vertices2[13] = tempU3;
                                float tempU4 = vertices2[8];
                                vertices2[8] = vertices2[18];
                                vertices2[18] = tempU4;
                                float tempV = vertices2[4];
                                vertices2[4] = vertices2[14];
                                vertices2[14] = tempV;
                                float tempV2 = vertices2[9];
                                vertices2[9] = vertices2[19];
                                vertices2[19] = tempV2;
                            } else if (rotations == 3) {
                                float tempV3 = vertices2[4];
                                vertices2[4] = vertices2[19];
                                vertices2[19] = vertices2[14];
                                vertices2[14] = vertices2[9];
                                vertices2[9] = tempV3;
                                float tempU5 = vertices2[3];
                                vertices2[3] = vertices2[18];
                                vertices2[18] = vertices2[13];
                                vertices2[13] = vertices2[8];
                                vertices2[8] = tempU5;
                            }
                        }
                        color = color2;
                        orthoCachedTiledMapRenderer.spriteCache.add(texture, vertices2, 0, 20);
                    }
                }
                col++;
                orthoCachedTiledMapRenderer = this;
                layerWidth2 = layerWidth;
                layerHeight2 = layerHeight;
                col12 = col1;
                col22 = col2;
                layerOffsetX2 = layerOffsetX;
                layerTileWidth2 = layerTileWidth;
                layerTileHeight2 = layerTileHeight;
                layerOffsetY2 = layerOffsetY;
                row12 = row1;
                color2 = color;
            }
            TiledMapTileLayer tiledMapTileLayer = layer;
            int i = layerWidth2;
            int i2 = layerHeight2;
            float f2 = layerTileWidth2;
            float f3 = layerTileHeight2;
            float f4 = layerOffsetX2;
            float f5 = layerOffsetY2;
            int i3 = col12;
            int i4 = col22;
            int i5 = row12;
            row--;
            orthoCachedTiledMapRenderer = this;
            color2 = color2;
        }
        TiledMapTileLayer tiledMapTileLayer2 = layer;
        float f6 = color2;
        int i6 = layerWidth2;
        int i7 = layerHeight2;
        float f7 = layerTileWidth2;
        float f8 = layerTileHeight2;
        float f9 = layerOffsetX2;
        float f10 = layerOffsetY2;
        int i8 = col12;
        int i9 = col22;
        int i10 = row12;
    }

    public void renderImageLayer(TiledMapImageLayer layer) {
        float color = Color.toFloatBits(1.0f, 1.0f, 1.0f, layer.getOpacity());
        float[] vertices2 = this.vertices;
        TextureRegion region = layer.getTextureRegion();
        if (region != null) {
            float x = layer.getX();
            float y = layer.getY();
            float f = this.unitScale;
            float x1 = x * f;
            float y1 = f * y;
            float x2 = (((float) region.getRegionWidth()) * this.unitScale) + x1;
            float y2 = (((float) region.getRegionHeight()) * this.unitScale) + y1;
            float u1 = region.getU();
            float v1 = region.getV2();
            float u2 = region.getU2();
            float v2 = region.getV();
            vertices2[0] = x1;
            vertices2[1] = y1;
            vertices2[2] = color;
            vertices2[3] = u1;
            vertices2[4] = v1;
            vertices2[5] = x1;
            vertices2[6] = y2;
            vertices2[7] = color;
            vertices2[8] = u1;
            vertices2[9] = v2;
            vertices2[10] = x2;
            vertices2[11] = y2;
            vertices2[12] = color;
            vertices2[13] = u2;
            vertices2[14] = v2;
            vertices2[15] = x2;
            vertices2[16] = y1;
            vertices2[17] = color;
            vertices2[18] = u2;
            vertices2[19] = v1;
            float f2 = color;
            this.spriteCache.add(region.getTexture(), vertices2, 0, 20);
        }
    }

    public void invalidateCache() {
        this.cached = false;
    }

    public boolean isCached() {
        return this.cached;
    }

    public void setOverCache(float overCache2) {
        this.overCache = overCache2;
    }

    public void setMaxTileSize(float maxPixelWidth, float maxPixelHeight) {
        this.maxTileWidth = maxPixelWidth;
        this.maxTileHeight = maxPixelHeight;
    }

    public void setBlending(boolean blending2) {
        this.blending = blending2;
    }

    public SpriteCache getSpriteCache() {
        return this.spriteCache;
    }

    public void dispose() {
        this.spriteCache.dispose();
    }
}
