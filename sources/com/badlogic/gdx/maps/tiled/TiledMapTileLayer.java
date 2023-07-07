package com.badlogic.gdx.maps.tiled;

import com.badlogic.gdx.maps.MapLayer;
import java.lang.reflect.Array;

public class TiledMapTileLayer extends MapLayer {
    private Cell[][] cells;
    private int height;
    private float tileHeight;
    private float tileWidth;
    private int width;

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public float getTileWidth() {
        return this.tileWidth;
    }

    public float getTileHeight() {
        return this.tileHeight;
    }

    public TiledMapTileLayer(int width2, int height2, int tileWidth2, int tileHeight2) {
        this.width = width2;
        this.height = height2;
        this.tileWidth = (float) tileWidth2;
        this.tileHeight = (float) tileHeight2;
        this.cells = (Cell[][]) Array.newInstance(Cell.class, new int[]{width2, height2});
    }

    public Cell getCell(int x, int y) {
        if (x < 0 || x >= this.width || y < 0 || y >= this.height) {
            return null;
        }
        return this.cells[x][y];
    }

    public void setCell(int x, int y, Cell cell) {
        if (x >= 0 && x < this.width && y >= 0 && y < this.height) {
            this.cells[x][y] = cell;
        }
    }

    public static class Cell {
        public static final int ROTATE_0 = 0;
        public static final int ROTATE_180 = 2;
        public static final int ROTATE_270 = 3;
        public static final int ROTATE_90 = 1;
        private boolean flipHorizontally;
        private boolean flipVertically;
        private int rotation;
        private TiledMapTile tile;

        public TiledMapTile getTile() {
            return this.tile;
        }

        public Cell setTile(TiledMapTile tile2) {
            this.tile = tile2;
            return this;
        }

        public boolean getFlipHorizontally() {
            return this.flipHorizontally;
        }

        public Cell setFlipHorizontally(boolean flipHorizontally2) {
            this.flipHorizontally = flipHorizontally2;
            return this;
        }

        public boolean getFlipVertically() {
            return this.flipVertically;
        }

        public Cell setFlipVertically(boolean flipVertically2) {
            this.flipVertically = flipVertically2;
            return this;
        }

        public int getRotation() {
            return this.rotation;
        }

        public Cell setRotation(int rotation2) {
            this.rotation = rotation2;
            return this;
        }
    }
}
