package com.badlogic.gdx.maps;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ObjectMap;

public interface ImageResolver {
    TextureRegion getImage(String str);

    public static class DirectImageResolver implements ImageResolver {
        private final ObjectMap<String, Texture> images;

        public DirectImageResolver(ObjectMap<String, Texture> images2) {
            this.images = images2;
        }

        public TextureRegion getImage(String name) {
            return new TextureRegion(this.images.get(name));
        }
    }

    public static class AssetManagerImageResolver implements ImageResolver {
        private final AssetManager assetManager;

        public AssetManagerImageResolver(AssetManager assetManager2) {
            this.assetManager = assetManager2;
        }

        public TextureRegion getImage(String name) {
            return new TextureRegion((Texture) this.assetManager.get(name, Texture.class));
        }
    }

    public static class TextureAtlasImageResolver implements ImageResolver {
        private final TextureAtlas atlas;

        public TextureAtlasImageResolver(TextureAtlas atlas2) {
            this.atlas = atlas2;
        }

        public TextureRegion getImage(String name) {
            return this.atlas.findRegion(name);
        }
    }
}
