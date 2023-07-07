package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

public interface TextureProvider {
    Texture load(String str);

    public static class FileTextureProvider implements TextureProvider {
        private Texture.TextureFilter magFilter;
        private Texture.TextureFilter minFilter;
        private Texture.TextureWrap uWrap;
        private boolean useMipMaps;
        private Texture.TextureWrap vWrap;

        public FileTextureProvider() {
            Texture.TextureFilter textureFilter = Texture.TextureFilter.Linear;
            this.magFilter = textureFilter;
            this.minFilter = textureFilter;
            Texture.TextureWrap textureWrap = Texture.TextureWrap.Repeat;
            this.vWrap = textureWrap;
            this.uWrap = textureWrap;
            this.useMipMaps = false;
        }

        public FileTextureProvider(Texture.TextureFilter minFilter2, Texture.TextureFilter magFilter2, Texture.TextureWrap uWrap2, Texture.TextureWrap vWrap2, boolean useMipMaps2) {
            this.minFilter = minFilter2;
            this.magFilter = magFilter2;
            this.uWrap = uWrap2;
            this.vWrap = vWrap2;
            this.useMipMaps = useMipMaps2;
        }

        public Texture load(String fileName) {
            Texture result = new Texture(Gdx.files.internal(fileName), this.useMipMaps);
            result.setFilter(this.minFilter, this.magFilter);
            result.setWrap(this.uWrap, this.vWrap);
            return result;
        }
    }

    public static class AssetTextureProvider implements TextureProvider {
        public final AssetManager assetManager;

        public AssetTextureProvider(AssetManager assetManager2) {
            this.assetManager = assetManager2;
        }

        public Texture load(String fileName) {
            return (Texture) this.assetManager.get(fileName, Texture.class);
        }
    }
}
