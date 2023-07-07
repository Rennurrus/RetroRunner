package com.badlogic.gdx.maps.tiled.renderers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class IsometricTiledMapRenderer extends BatchTiledMapRenderer {
    private Vector2 bottomLeft = new Vector2();
    private Vector2 bottomRight = new Vector2();
    private Matrix4 invIsotransform;
    private Matrix4 isoTransform;
    private Vector3 screenPos = new Vector3();
    private Vector2 topLeft = new Vector2();
    private Vector2 topRight = new Vector2();

    public IsometricTiledMapRenderer(TiledMap map) {
        super(map);
        init();
    }

    public IsometricTiledMapRenderer(TiledMap map, Batch batch) {
        super(map, batch);
        init();
    }

    public IsometricTiledMapRenderer(TiledMap map, float unitScale) {
        super(map, unitScale);
        init();
    }

    public IsometricTiledMapRenderer(TiledMap map, float unitScale, Batch batch) {
        super(map, unitScale, batch);
        init();
    }

    private void init() {
        this.isoTransform = new Matrix4();
        this.isoTransform.idt();
        this.isoTransform.scale((float) (Math.sqrt(2.0d) / 2.0d), (float) (Math.sqrt(2.0d) / 4.0d), 1.0f);
        this.isoTransform.rotate(0.0f, 0.0f, 1.0f, -45.0f);
        this.invIsotransform = new Matrix4(this.isoTransform);
        this.invIsotransform.inv();
    }

    private Vector3 translateScreenToIso(Vector2 vec) {
        this.screenPos.set(vec.x, vec.y, 0.0f);
        this.screenPos.mul(this.invIsotransform);
        return this.screenPos;
    }

    public void renderTileLayer(TiledMapTileLayer layer) {
        float color;
        float halfTileWidth;
        float halfTileHeight;
        float layerOffsetY;
        float layerOffsetX;
        float tileHeight;
        IsometricTiledMapRenderer isometricTiledMapRenderer = this;
        Color batchColor = isometricTiledMapRenderer.batch.getColor();
        float color2 = Color.toFloatBits(batchColor.r, batchColor.g, batchColor.b, batchColor.a * layer.getOpacity());
        float tileWidth = layer.getTileWidth() * isometricTiledMapRenderer.unitScale;
        float tileHeight2 = layer.getTileHeight() * isometricTiledMapRenderer.unitScale;
        float layerOffsetX2 = layer.getRenderOffsetX() * isometricTiledMapRenderer.unitScale;
        float layerOffsetY2 = (-layer.getRenderOffsetY()) * isometricTiledMapRenderer.unitScale;
        float halfTileWidth2 = tileWidth * 0.5f;
        float halfTileHeight2 = 0.5f * tileHeight2;
        isometricTiledMapRenderer.topRight.set((isometricTiledMapRenderer.viewBounds.x + isometricTiledMapRenderer.viewBounds.width) - layerOffsetX2, isometricTiledMapRenderer.viewBounds.y - layerOffsetY2);
        isometricTiledMapRenderer.bottomLeft.set(isometricTiledMapRenderer.viewBounds.x - layerOffsetX2, (isometricTiledMapRenderer.viewBounds.y + isometricTiledMapRenderer.viewBounds.height) - layerOffsetY2);
        isometricTiledMapRenderer.topLeft.set(isometricTiledMapRenderer.viewBounds.x - layerOffsetX2, isometricTiledMapRenderer.viewBounds.y - layerOffsetY2);
        isometricTiledMapRenderer.bottomRight.set((isometricTiledMapRenderer.viewBounds.x + isometricTiledMapRenderer.viewBounds.width) - layerOffsetX2, (isometricTiledMapRenderer.viewBounds.y + isometricTiledMapRenderer.viewBounds.height) - layerOffsetY2);
        int row1 = ((int) (isometricTiledMapRenderer.translateScreenToIso(isometricTiledMapRenderer.topLeft).y / tileWidth)) - 2;
        int col1 = ((int) (isometricTiledMapRenderer.translateScreenToIso(isometricTiledMapRenderer.bottomLeft).x / tileWidth)) - 2;
        int col2 = ((int) (isometricTiledMapRenderer.translateScreenToIso(isometricTiledMapRenderer.topRight).x / tileWidth)) + 2;
        int row = ((int) (isometricTiledMapRenderer.translateScreenToIso(isometricTiledMapRenderer.bottomRight).y / tileWidth)) + 2;
        while (row >= row1) {
            int col = col1;
            while (col <= col2) {
                Color batchColor2 = batchColor;
                float x = (((float) col) * halfTileWidth2) + (((float) row) * halfTileWidth2);
                float tileWidth2 = tileWidth;
                float y = (((float) row) * halfTileHeight2) - (((float) col) * halfTileHeight2);
                TiledMapTileLayer.Cell cell = layer.getCell(col, row);
                if (cell == null) {
                    color = color2;
                    tileHeight = tileHeight2;
                    layerOffsetX = layerOffsetX2;
                    layerOffsetY = layerOffsetY2;
                    halfTileHeight = halfTileHeight2;
                    halfTileWidth = halfTileWidth2;
                } else {
                    TiledMapTile tile = cell.getTile();
                    if (tile != null) {
                        boolean flipX = cell.getFlipHorizontally();
                        boolean flipY = cell.getFlipVertically();
                        int rotations = cell.getRotation();
                        TextureRegion region = tile.getTextureRegion();
                        tileHeight = tileHeight2;
                        float x1 = x + (tile.getOffsetX() * isometricTiledMapRenderer.unitScale) + layerOffsetX2;
                        layerOffsetX = layerOffsetX2;
                        float y1 = (tile.getOffsetY() * isometricTiledMapRenderer.unitScale) + y + layerOffsetY2;
                        float f = y;
                        float x2 = x1 + (((float) region.getRegionWidth()) * isometricTiledMapRenderer.unitScale);
                        layerOffsetY = layerOffsetY2;
                        float y2 = (((float) region.getRegionHeight()) * isometricTiledMapRenderer.unitScale) + y1;
                        float u1 = region.getU();
                        float v1 = region.getV2();
                        float u2 = region.getU2();
                        float v2 = region.getV();
                        halfTileHeight = halfTileHeight2;
                        halfTileWidth = halfTileWidth2;
                        isometricTiledMapRenderer.vertices[0] = x1;
                        isometricTiledMapRenderer.vertices[1] = y1;
                        isometricTiledMapRenderer.vertices[2] = color2;
                        isometricTiledMapRenderer.vertices[3] = u1;
                        isometricTiledMapRenderer.vertices[4] = v1;
                        isometricTiledMapRenderer.vertices[5] = x1;
                        isometricTiledMapRenderer.vertices[6] = y2;
                        isometricTiledMapRenderer.vertices[7] = color2;
                        isometricTiledMapRenderer.vertices[8] = u1;
                        isometricTiledMapRenderer.vertices[9] = v2;
                        isometricTiledMapRenderer.vertices[10] = x2;
                        isometricTiledMapRenderer.vertices[11] = y2;
                        isometricTiledMapRenderer.vertices[12] = color2;
                        isometricTiledMapRenderer.vertices[13] = u2;
                        isometricTiledMapRenderer.vertices[14] = v2;
                        isometricTiledMapRenderer.vertices[15] = x2;
                        isometricTiledMapRenderer.vertices[16] = y1;
                        isometricTiledMapRenderer.vertices[17] = color2;
                        isometricTiledMapRenderer.vertices[18] = u2;
                        isometricTiledMapRenderer.vertices[19] = v1;
                        if (flipX) {
                            float temp = isometricTiledMapRenderer.vertices[3];
                            float f2 = y2;
                            isometricTiledMapRenderer.vertices[3] = isometricTiledMapRenderer.vertices[13];
                            isometricTiledMapRenderer.vertices[13] = temp;
                            float temp2 = isometricTiledMapRenderer.vertices[8];
                            isometricTiledMapRenderer.vertices[8] = isometricTiledMapRenderer.vertices[18];
                            isometricTiledMapRenderer.vertices[18] = temp2;
                        }
                        if (flipY) {
                            float temp3 = isometricTiledMapRenderer.vertices[4];
                            isometricTiledMapRenderer.vertices[4] = isometricTiledMapRenderer.vertices[14];
                            isometricTiledMapRenderer.vertices[14] = temp3;
                            float temp4 = isometricTiledMapRenderer.vertices[9];
                            isometricTiledMapRenderer.vertices[9] = isometricTiledMapRenderer.vertices[19];
                            isometricTiledMapRenderer.vertices[19] = temp4;
                        }
                        if (rotations == 0) {
                            color = color2;
                        } else if (rotations == 1) {
                            color = color2;
                            float tempV = isometricTiledMapRenderer.vertices[4];
                            isometricTiledMapRenderer.vertices[4] = isometricTiledMapRenderer.vertices[9];
                            isometricTiledMapRenderer.vertices[9] = isometricTiledMapRenderer.vertices[14];
                            isometricTiledMapRenderer.vertices[14] = isometricTiledMapRenderer.vertices[19];
                            isometricTiledMapRenderer.vertices[19] = tempV;
                            float tempU = isometricTiledMapRenderer.vertices[3];
                            float f3 = tempV;
                            isometricTiledMapRenderer.vertices[3] = isometricTiledMapRenderer.vertices[8];
                            isometricTiledMapRenderer.vertices[8] = isometricTiledMapRenderer.vertices[13];
                            isometricTiledMapRenderer.vertices[13] = isometricTiledMapRenderer.vertices[18];
                            isometricTiledMapRenderer.vertices[18] = tempU;
                        } else if (rotations == 2) {
                            color = color2;
                            float tempU2 = isometricTiledMapRenderer.vertices[3];
                            isometricTiledMapRenderer.vertices[3] = isometricTiledMapRenderer.vertices[13];
                            isometricTiledMapRenderer.vertices[13] = tempU2;
                            float tempU3 = isometricTiledMapRenderer.vertices[8];
                            isometricTiledMapRenderer.vertices[8] = isometricTiledMapRenderer.vertices[18];
                            isometricTiledMapRenderer.vertices[18] = tempU3;
                            float tempV2 = isometricTiledMapRenderer.vertices[4];
                            isometricTiledMapRenderer.vertices[4] = isometricTiledMapRenderer.vertices[14];
                            isometricTiledMapRenderer.vertices[14] = tempV2;
                            float tempV3 = isometricTiledMapRenderer.vertices[9];
                            isometricTiledMapRenderer.vertices[9] = isometricTiledMapRenderer.vertices[19];
                            isometricTiledMapRenderer.vertices[19] = tempV3;
                        } else if (rotations != 3) {
                            color = color2;
                        } else {
                            float tempV4 = isometricTiledMapRenderer.vertices[4];
                            isometricTiledMapRenderer.vertices[4] = isometricTiledMapRenderer.vertices[19];
                            isometricTiledMapRenderer.vertices[19] = isometricTiledMapRenderer.vertices[14];
                            isometricTiledMapRenderer.vertices[14] = isometricTiledMapRenderer.vertices[9];
                            isometricTiledMapRenderer.vertices[9] = tempV4;
                            float tempU4 = isometricTiledMapRenderer.vertices[3];
                            color = color2;
                            isometricTiledMapRenderer.vertices[3] = isometricTiledMapRenderer.vertices[18];
                            isometricTiledMapRenderer.vertices[18] = isometricTiledMapRenderer.vertices[13];
                            isometricTiledMapRenderer.vertices[13] = isometricTiledMapRenderer.vertices[8];
                            isometricTiledMapRenderer.vertices[8] = tempU4;
                        }
                        isometricTiledMapRenderer.batch.draw(region.getTexture(), isometricTiledMapRenderer.vertices, 0, 20);
                    } else {
                        color = color2;
                        tileHeight = tileHeight2;
                        layerOffsetX = layerOffsetX2;
                        layerOffsetY = layerOffsetY2;
                        halfTileHeight = halfTileHeight2;
                        halfTileWidth = halfTileWidth2;
                    }
                }
                col++;
                isometricTiledMapRenderer = this;
                batchColor = batchColor2;
                tileWidth = tileWidth2;
                tileHeight2 = tileHeight;
                layerOffsetX2 = layerOffsetX;
                layerOffsetY2 = layerOffsetY;
                halfTileHeight2 = halfTileHeight;
                halfTileWidth2 = halfTileWidth;
                color2 = color;
            }
            float f4 = color2;
            float f5 = tileWidth;
            float f6 = tileHeight2;
            float f7 = layerOffsetX2;
            float f8 = layerOffsetY2;
            float f9 = halfTileHeight2;
            float f10 = halfTileWidth2;
            row--;
            isometricTiledMapRenderer = this;
        }
        float f11 = color2;
        float f12 = tileWidth;
        float f13 = tileHeight2;
        float f14 = layerOffsetX2;
        float f15 = layerOffsetY2;
        float f16 = halfTileHeight2;
        float f17 = halfTileWidth2;
    }
}
