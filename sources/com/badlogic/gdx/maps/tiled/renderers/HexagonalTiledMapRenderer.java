package com.badlogic.gdx.maps.tiled.renderers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;

public class HexagonalTiledMapRenderer extends BatchTiledMapRenderer {
    private float hexSideLength = 0.0f;
    private boolean staggerAxisX = true;
    private boolean staggerIndexEven = false;

    public HexagonalTiledMapRenderer(TiledMap map) {
        super(map);
        init(map);
    }

    public HexagonalTiledMapRenderer(TiledMap map, float unitScale) {
        super(map, unitScale);
        init(map);
    }

    public HexagonalTiledMapRenderer(TiledMap map, Batch batch) {
        super(map, batch);
        init(map);
    }

    public HexagonalTiledMapRenderer(TiledMap map, float unitScale, Batch batch) {
        super(map, unitScale, batch);
        init(map);
    }

    private void init(TiledMap map) {
        String axis = (String) map.getProperties().get("staggeraxis", String.class);
        if (axis != null) {
            if (axis.equals("x")) {
                this.staggerAxisX = true;
            } else {
                this.staggerAxisX = false;
            }
        }
        String index = (String) map.getProperties().get("staggerindex", String.class);
        if (index != null) {
            if (index.equals("even")) {
                this.staggerIndexEven = true;
            } else {
                this.staggerIndexEven = false;
            }
        }
        Integer length = (Integer) map.getProperties().get("hexsidelength", Integer.class);
        if (length != null) {
            this.hexSideLength = (float) length.intValue();
        } else if (this.staggerAxisX) {
            Integer length2 = (Integer) map.getProperties().get("tilewidth", Integer.class);
            if (length2 != null) {
                this.hexSideLength = ((float) length2.intValue()) * 0.5f;
            } else {
                this.hexSideLength = ((TiledMapTileLayer) map.getLayers().get(0)).getTileWidth() * 0.5f;
            }
        } else {
            Integer length3 = (Integer) map.getProperties().get("tileheight", Integer.class);
            if (length3 != null) {
                this.hexSideLength = ((float) length3.intValue()) * 0.5f;
            } else {
                this.hexSideLength = ((TiledMapTileLayer) map.getLayers().get(0)).getTileHeight() * 0.5f;
            }
        }
    }

    public void renderTileLayer(TiledMapTileLayer layer) {
        int row1;
        int row12;
        float shiftX;
        int layerWidth;
        int layerWidth2;
        int layerHeight;
        int layerHeight2;
        TiledMapTileLayer tiledMapTileLayer = layer;
        Color batchColor = this.batch.getColor();
        float color = Color.toFloatBits(batchColor.r, batchColor.g, batchColor.b, batchColor.a * layer.getOpacity());
        int layerWidth3 = layer.getWidth();
        int layerHeight3 = layer.getHeight();
        float layerTileWidth = layer.getTileWidth() * this.unitScale;
        float layerTileHeight = layer.getTileHeight() * this.unitScale;
        float layerOffsetX = layer.getRenderOffsetX() * this.unitScale;
        float layerOffsetY = (-layer.getRenderOffsetY()) * this.unitScale;
        float layerHexLength = this.hexSideLength * this.unitScale;
        if (this.staggerAxisX) {
            float tileWidthLowerCorner = (layerTileWidth - layerHexLength) / 2.0f;
            float tileWidthUpperCorner = (layerTileWidth + layerHexLength) / 2.0f;
            float layerTileHeight50 = 0.5f * layerTileHeight;
            int row13 = Math.max(0, (int) (((this.viewBounds.y - layerTileHeight50) - layerOffsetX) / layerTileHeight));
            int row2 = Math.min(layerHeight3, (int) ((((this.viewBounds.y + this.viewBounds.height) + layerTileHeight) - layerOffsetX) / layerTileHeight));
            Color color2 = batchColor;
            int col1 = Math.max(0, (int) (((this.viewBounds.x - tileWidthLowerCorner) - layerOffsetY) / tileWidthUpperCorner));
            float f = tileWidthLowerCorner;
            int col2 = Math.min(layerWidth3, (int) ((((this.viewBounds.x + this.viewBounds.width) + tileWidthUpperCorner) - layerOffsetY) / tileWidthUpperCorner));
            int i = this.staggerIndexEven;
            if (col1 % 2 == 0) {
                layerWidth = layerWidth3;
                layerWidth2 = 1;
            } else {
                layerWidth = layerWidth3;
                layerWidth2 = 0;
            }
            int colA = i == layerWidth2 ? col1 + 1 : col1;
            int i2 = this.staggerIndexEven;
            if (col1 % 2 == 0) {
                layerHeight = layerHeight3;
                layerHeight2 = 1;
            } else {
                layerHeight = layerHeight3;
                layerHeight2 = 0;
            }
            int colB = i2 == layerHeight2 ? col1 : col1 + 1;
            int row = row2 - 1;
            while (row >= row13) {
                int i3 = colA;
                int colA2 = colA;
                while (colA < col2) {
                    renderCell(tiledMapTileLayer.getCell(colA, row), (((float) colA) * tileWidthUpperCorner) + layerOffsetX, (((float) row) * layerTileHeight) + layerTileHeight50 + layerOffsetY, color);
                    colA += 2;
                    row13 = row13;
                    row2 = row2;
                    col1 = col1;
                }
                int row14 = row13;
                int row22 = row2;
                int col12 = col1;
                for (int col = colB; col < col2; col += 2) {
                    renderCell(tiledMapTileLayer.getCell(col, row), (((float) col) * tileWidthUpperCorner) + layerOffsetX, (((float) row) * layerTileHeight) + layerOffsetY, color);
                }
                row--;
                colA = colA2;
                row13 = row14;
                row2 = row22;
                col1 = col12;
            }
            int i4 = row13;
            int i5 = row2;
            int i6 = col1;
            float f2 = layerHexLength;
            int i7 = layerWidth;
            int i8 = layerHeight;
            float f3 = layerTileHeight;
            return;
        }
        int layerWidth4 = layerWidth3;
        int layerHeight4 = layerHeight3;
        float tileHeightLowerCorner = (layerTileHeight - layerHexLength) / 2.0f;
        float tileHeightUpperCorner = (layerTileHeight + layerHexLength) / 2.0f;
        float layerTileWidth50 = 0.5f * layerTileWidth;
        int row15 = Math.max(0, (int) (((this.viewBounds.y - tileHeightLowerCorner) - layerOffsetX) / tileHeightUpperCorner));
        int row23 = Math.min(layerHeight4, (int) ((((this.viewBounds.y + this.viewBounds.height) + tileHeightUpperCorner) - layerOffsetX) / tileHeightUpperCorner));
        int col13 = Math.max(0, (int) (((this.viewBounds.x - layerTileWidth50) - layerOffsetY) / layerTileWidth));
        float f4 = tileHeightLowerCorner;
        int col22 = Math.min(layerWidth4, (int) ((((this.viewBounds.x + this.viewBounds.width) + layerTileWidth) - layerOffsetY) / layerTileWidth));
        float f5 = layerTileHeight;
        int row3 = row23 - 1;
        while (row3 >= row15) {
            if (row3 % 2 == 0) {
                row1 = row15;
                row12 = 1;
            } else {
                row1 = row15;
                row12 = 0;
            }
            float layerHexLength2 = layerHexLength;
            if (row12 == this.staggerIndexEven) {
                shiftX = layerTileWidth50;
            } else {
                shiftX = 0.0f;
            }
            int col3 = col13;
            while (col3 < col22) {
                renderCell(tiledMapTileLayer.getCell(col3, row3), (((float) col3) * layerTileWidth) + shiftX + layerOffsetX, (((float) row3) * tileHeightUpperCorner) + layerOffsetY, color);
                col3++;
                tiledMapTileLayer = layer;
                col22 = col22;
            }
            row3--;
            tiledMapTileLayer = layer;
            row15 = row1;
            layerHexLength = layerHexLength2;
        }
        int i9 = row15;
        float f6 = layerHexLength;
    }

    private void renderCell(TiledMapTileLayer.Cell cell, float x, float y, float color) {
        if (cell != null) {
            TiledMapTile tile = cell.getTile();
            if (tile == null) {
            } else if (!(tile instanceof AnimatedTiledMapTile)) {
                boolean flipX = cell.getFlipHorizontally();
                boolean flipY = cell.getFlipVertically();
                int rotations = cell.getRotation();
                TextureRegion region = tile.getTextureRegion();
                float x1 = x + (tile.getOffsetX() * this.unitScale);
                float y1 = y + (tile.getOffsetY() * this.unitScale);
                float x2 = (((float) region.getRegionWidth()) * this.unitScale) + x1;
                float y2 = (((float) region.getRegionHeight()) * this.unitScale) + y1;
                float u1 = region.getU();
                float v1 = region.getV2();
                float u2 = region.getU2();
                float v2 = region.getV();
                this.vertices[0] = x1;
                this.vertices[1] = y1;
                this.vertices[2] = color;
                this.vertices[3] = u1;
                this.vertices[4] = v1;
                this.vertices[5] = x1;
                this.vertices[6] = y2;
                this.vertices[7] = color;
                this.vertices[8] = u1;
                this.vertices[9] = v2;
                this.vertices[10] = x2;
                this.vertices[11] = y2;
                this.vertices[12] = color;
                this.vertices[13] = u2;
                this.vertices[14] = v2;
                this.vertices[15] = x2;
                this.vertices[16] = y1;
                this.vertices[17] = color;
                this.vertices[18] = u2;
                this.vertices[19] = v1;
                if (flipX) {
                    float temp = this.vertices[3];
                    TiledMapTile tiledMapTile = tile;
                    this.vertices[3] = this.vertices[13];
                    this.vertices[13] = temp;
                    float temp2 = this.vertices[8];
                    this.vertices[8] = this.vertices[18];
                    this.vertices[18] = temp2;
                }
                if (flipY) {
                    float temp3 = this.vertices[4];
                    this.vertices[4] = this.vertices[14];
                    this.vertices[14] = temp3;
                    float temp4 = this.vertices[9];
                    this.vertices[9] = this.vertices[19];
                    this.vertices[19] = temp4;
                }
                if (rotations == 2) {
                    float tempU = this.vertices[3];
                    this.vertices[3] = this.vertices[13];
                    this.vertices[13] = tempU;
                    float tempU2 = this.vertices[8];
                    this.vertices[8] = this.vertices[18];
                    this.vertices[18] = tempU2;
                    float tempV = this.vertices[4];
                    float f = tempU2;
                    this.vertices[4] = this.vertices[14];
                    this.vertices[14] = tempV;
                    float tempV2 = this.vertices[9];
                    this.vertices[9] = this.vertices[19];
                    this.vertices[19] = tempV2;
                }
                boolean z = flipX;
                this.batch.draw(region.getTexture(), this.vertices, 0, 20);
            }
        }
    }
}
