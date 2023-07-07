package com.badlogic.gdx.assets.loaders;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class SkinLoader extends AsynchronousAssetLoader<Skin, SkinParameter> {
    public SkinLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, SkinParameter parameter) {
        Array<AssetDescriptor> deps = new Array<>();
        if (parameter == null || parameter.textureAtlasPath == null) {
            deps.add(new AssetDescriptor(file.pathWithoutExtension() + ".atlas", TextureAtlas.class));
        } else if (parameter.textureAtlasPath != null) {
            deps.add(new AssetDescriptor(parameter.textureAtlasPath, TextureAtlas.class));
        }
        return deps;
    }

    public void loadAsync(AssetManager manager, String fileName, FileHandle file, SkinParameter parameter) {
    }

    public Skin loadSync(AssetManager manager, String fileName, FileHandle file, SkinParameter parameter) {
        String textureAtlasPath = file.pathWithoutExtension() + ".atlas";
        ObjectMap<String, Object> resources = null;
        if (parameter != null) {
            if (parameter.textureAtlasPath != null) {
                textureAtlasPath = parameter.textureAtlasPath;
            }
            if (parameter.resources != null) {
                resources = parameter.resources;
            }
        }
        Skin skin = newSkin((TextureAtlas) manager.get(textureAtlasPath, TextureAtlas.class));
        if (resources != null) {
            ObjectMap.Entries<String, Object> it = resources.entries().iterator();
            while (it.hasNext()) {
                ObjectMap.Entry<String, Object> entry = (ObjectMap.Entry) it.next();
                skin.add((String) entry.key, entry.value);
            }
        }
        skin.load(file);
        return skin;
    }

    /* access modifiers changed from: protected */
    public Skin newSkin(TextureAtlas atlas) {
        return new Skin(atlas);
    }

    public static class SkinParameter extends AssetLoaderParameters<Skin> {
        public final ObjectMap<String, Object> resources;
        public final String textureAtlasPath;

        public SkinParameter() {
            this((String) null, (ObjectMap<String, Object>) null);
        }

        public SkinParameter(ObjectMap<String, Object> resources2) {
            this((String) null, resources2);
        }

        public SkinParameter(String textureAtlasPath2) {
            this(textureAtlasPath2, (ObjectMap<String, Object>) null);
        }

        public SkinParameter(String textureAtlasPath2, ObjectMap<String, Object> resources2) {
            this.textureAtlasPath = textureAtlasPath2;
            this.resources = resources2;
        }
    }
}
