package com.badlogic.gdx.assets.loaders;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class BitmapFontLoader extends AsynchronousAssetLoader<BitmapFont, BitmapFontParameter> {
    BitmapFont.BitmapFontData data;

    public static class BitmapFontParameter extends AssetLoaderParameters<BitmapFont> {
        public String atlasName = null;
        public BitmapFont.BitmapFontData bitmapFontData = null;
        public boolean flip = false;
        public boolean genMipMaps = false;
        public Texture.TextureFilter magFilter = Texture.TextureFilter.Nearest;
        public Texture.TextureFilter minFilter = Texture.TextureFilter.Nearest;
    }

    public BitmapFontLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, BitmapFontParameter parameter) {
        Array<AssetDescriptor> deps = new Array<>();
        if (parameter == null || parameter.bitmapFontData == null) {
            this.data = new BitmapFont.BitmapFontData(file, parameter != null && parameter.flip);
            if (parameter == null || parameter.atlasName == null) {
                for (int i = 0; i < this.data.getImagePaths().length; i++) {
                    FileHandle resolved = resolve(this.data.getImagePath(i));
                    TextureLoader.TextureParameter textureParams = new TextureLoader.TextureParameter();
                    if (parameter != null) {
                        textureParams.genMipMaps = parameter.genMipMaps;
                        textureParams.minFilter = parameter.minFilter;
                        textureParams.magFilter = parameter.magFilter;
                    }
                    deps.add(new AssetDescriptor(resolved, Texture.class, textureParams));
                }
            } else {
                deps.add(new AssetDescriptor(parameter.atlasName, TextureAtlas.class));
            }
            return deps;
        }
        this.data = parameter.bitmapFontData;
        return deps;
    }

    public void loadAsync(AssetManager manager, String fileName, FileHandle file, BitmapFontParameter parameter) {
    }

    public BitmapFont loadSync(AssetManager manager, String fileName, FileHandle file, BitmapFontParameter parameter) {
        if (parameter == null || parameter.atlasName == null) {
            int n = this.data.getImagePaths().length;
            Array<TextureRegion> regs = new Array<>(n);
            for (int i = 0; i < n; i++) {
                regs.add(new TextureRegion((Texture) manager.get(this.data.getImagePath(i), Texture.class)));
            }
            return new BitmapFont(this.data, regs, true);
        }
        String name = file.sibling(this.data.imagePaths[0]).nameWithoutExtension().toString();
        TextureAtlas.AtlasRegion region = ((TextureAtlas) manager.get(parameter.atlasName, TextureAtlas.class)).findRegion(name);
        if (region != null) {
            return new BitmapFont(file, (TextureRegion) region);
        }
        throw new GdxRuntimeException("Could not find font region " + name + " in atlas " + parameter.atlasName);
    }
}
