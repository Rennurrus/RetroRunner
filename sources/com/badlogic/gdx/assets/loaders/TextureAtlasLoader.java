package com.badlogic.gdx.assets.loaders;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import java.util.Iterator;

public class TextureAtlasLoader extends SynchronousAssetLoader<TextureAtlas, TextureAtlasParameter> {
    TextureAtlas.TextureAtlasData data;

    public TextureAtlasLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    public TextureAtlas load(AssetManager assetManager, String fileName, FileHandle file, TextureAtlasParameter parameter) {
        Iterator<TextureAtlas.TextureAtlasData.Page> it = this.data.getPages().iterator();
        while (it.hasNext()) {
            TextureAtlas.TextureAtlasData.Page page = it.next();
            page.texture = (Texture) assetManager.get(page.textureFile.path().replaceAll("\\\\", "/"), Texture.class);
        }
        TextureAtlas atlas = new TextureAtlas(this.data);
        this.data = null;
        return atlas;
    }

    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle atlasFile, TextureAtlasParameter parameter) {
        FileHandle imgDir = atlasFile.parent();
        if (parameter != null) {
            this.data = new TextureAtlas.TextureAtlasData(atlasFile, imgDir, parameter.flip);
        } else {
            this.data = new TextureAtlas.TextureAtlasData(atlasFile, imgDir, false);
        }
        Array<AssetDescriptor> dependencies = new Array<>();
        Iterator<TextureAtlas.TextureAtlasData.Page> it = this.data.getPages().iterator();
        while (it.hasNext()) {
            TextureAtlas.TextureAtlasData.Page page = it.next();
            TextureLoader.TextureParameter params = new TextureLoader.TextureParameter();
            params.format = page.format;
            params.genMipMaps = page.useMipMaps;
            params.minFilter = page.minFilter;
            params.magFilter = page.magFilter;
            dependencies.add(new AssetDescriptor(page.textureFile, Texture.class, params));
        }
        return dependencies;
    }

    public static class TextureAtlasParameter extends AssetLoaderParameters<TextureAtlas> {
        public boolean flip = false;

        public TextureAtlasParameter() {
        }

        public TextureAtlasParameter(boolean flip2) {
            this.flip = flip2;
        }
    }
}
