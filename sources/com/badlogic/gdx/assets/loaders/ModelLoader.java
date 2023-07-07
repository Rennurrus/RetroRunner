package com.badlogic.gdx.assets.loaders;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ModelLoader.ModelParameters;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMaterial;
import com.badlogic.gdx.graphics.g3d.model.data.ModelTexture;
import com.badlogic.gdx.graphics.g3d.utils.TextureProvider;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import java.util.Iterator;

public abstract class ModelLoader<P extends ModelParameters> extends AsynchronousAssetLoader<Model, P> {
    protected ModelParameters defaultParameters = new ModelParameters();
    protected Array<ObjectMap.Entry<String, ModelData>> items = new Array<>();

    public abstract ModelData loadModelData(FileHandle fileHandle, P p);

    public ModelLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    public ModelData loadModelData(FileHandle fileHandle) {
        return loadModelData(fileHandle, (ModelParameters) null);
    }

    public Model loadModel(FileHandle fileHandle, TextureProvider textureProvider, P parameters) {
        ModelData data = loadModelData(fileHandle, parameters);
        if (data == null) {
            return null;
        }
        return new Model(data, textureProvider);
    }

    public Model loadModel(FileHandle fileHandle, P parameters) {
        return loadModel(fileHandle, new TextureProvider.FileTextureProvider(), parameters);
    }

    public Model loadModel(FileHandle fileHandle, TextureProvider textureProvider) {
        return loadModel(fileHandle, textureProvider, (ModelParameters) null);
    }

    public Model loadModel(FileHandle fileHandle) {
        return loadModel(fileHandle, new TextureProvider.FileTextureProvider(), (ModelParameters) null);
    }

    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, P parameters) {
        Array<AssetDescriptor> deps = new Array<>();
        ModelData data = loadModelData(file, parameters);
        if (data == null) {
            return deps;
        }
        ObjectMap.Entry<String, ModelData> item = new ObjectMap.Entry<>();
        item.key = fileName;
        item.value = data;
        synchronized (this.items) {
            this.items.add(item);
        }
        TextureLoader.TextureParameter textureParameter = parameters != null ? parameters.textureParameter : this.defaultParameters.textureParameter;
        Iterator<ModelMaterial> it = data.materials.iterator();
        while (it.hasNext()) {
            ModelMaterial modelMaterial = it.next();
            if (modelMaterial.textures != null) {
                Iterator<ModelTexture> it2 = modelMaterial.textures.iterator();
                while (it2.hasNext()) {
                    deps.add(new AssetDescriptor(it2.next().fileName, Texture.class, textureParameter));
                }
            }
        }
        return deps;
    }

    public void loadAsync(AssetManager manager, String fileName, FileHandle file, P p) {
    }

    public Model loadSync(AssetManager manager, String fileName, FileHandle file, P p) {
        ModelData data = null;
        synchronized (this.items) {
            for (int i = 0; i < this.items.size; i++) {
                if (((String) this.items.get(i).key).equals(fileName)) {
                    data = (ModelData) this.items.get(i).value;
                    this.items.removeIndex(i);
                }
            }
        }
        if (data == null) {
            return null;
        }
        Model result = new Model(data, new TextureProvider.AssetTextureProvider(manager));
        Iterator<Disposable> disposables = result.getManagedDisposables().iterator();
        while (disposables.hasNext()) {
            if (disposables.next() instanceof Texture) {
                disposables.remove();
            }
        }
        return result;
    }

    public static class ModelParameters extends AssetLoaderParameters<Model> {
        public TextureLoader.TextureParameter textureParameter = new TextureLoader.TextureParameter();

        public ModelParameters() {
            TextureLoader.TextureParameter textureParameter2 = this.textureParameter;
            Texture.TextureFilter textureFilter = Texture.TextureFilter.Linear;
            textureParameter2.magFilter = textureFilter;
            textureParameter2.minFilter = textureFilter;
            TextureLoader.TextureParameter textureParameter3 = this.textureParameter;
            Texture.TextureWrap textureWrap = Texture.TextureWrap.Repeat;
            textureParameter3.wrapV = textureWrap;
            textureParameter3.wrapU = textureWrap;
        }
    }
}
