package com.badlogic.gdx.maps.tiled.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTile;

public class TiledMapTileMapObject extends TextureMapObject {
    private boolean flipHorizontally;
    private boolean flipVertically;
    private TiledMapTile tile;

    public TiledMapTileMapObject(TiledMapTile tile2, boolean flipHorizontally2, boolean flipVertically2) {
        this.flipHorizontally = flipHorizontally2;
        this.flipVertically = flipVertically2;
        this.tile = tile2;
        TextureRegion textureRegion = new TextureRegion(tile2.getTextureRegion());
        textureRegion.flip(flipHorizontally2, flipVertically2);
        setTextureRegion(textureRegion);
    }

    public boolean isFlipHorizontally() {
        return this.flipHorizontally;
    }

    public void setFlipHorizontally(boolean flipHorizontally2) {
        this.flipHorizontally = flipHorizontally2;
    }

    public boolean isFlipVertically() {
        return this.flipVertically;
    }

    public void setFlipVertically(boolean flipVertically2) {
        this.flipVertically = flipVertically2;
    }

    public TiledMapTile getTile() {
        return this.tile;
    }

    public void setTile(TiledMapTile tile2) {
        this.tile = tile2;
    }
}
