package com.badlogic.gdx.graphics.g3d.particles;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import java.util.Iterator;

public class ResourceData<T> implements Json.Serializable {
    private int currentLoadIndex;
    private Array<SaveData> data;
    public T resource;
    Array<AssetData> sharedAssets;
    private ObjectMap<String, SaveData> uniqueData;

    public interface Configurable<T> {
        void load(AssetManager assetManager, ResourceData<T> resourceData);

        void save(AssetManager assetManager, ResourceData<T> resourceData);
    }

    public static class SaveData implements Json.Serializable {
        IntArray assets = new IntArray();
        ObjectMap<String, Object> data = new ObjectMap<>();
        private int loadIndex = 0;
        protected ResourceData resources;

        public SaveData() {
        }

        public SaveData(ResourceData resources2) {
            this.resources = resources2;
        }

        public <K> void saveAsset(String filename, Class<K> type) {
            int i = this.resources.getAssetData(filename, type);
            if (i == -1) {
                this.resources.sharedAssets.add(new AssetData(filename, type));
                i = this.resources.sharedAssets.size - 1;
            }
            this.assets.add(i);
        }

        public void save(String key, Object value) {
            this.data.put(key, value);
        }

        public AssetDescriptor loadAsset() {
            if (this.loadIndex == this.assets.size) {
                return null;
            }
            Array<AssetData> array = this.resources.sharedAssets;
            IntArray intArray = this.assets;
            int i = this.loadIndex;
            this.loadIndex = i + 1;
            AssetData data2 = array.get(intArray.get(i));
            return new AssetDescriptor(data2.filename, data2.type);
        }

        public <K> K load(String key) {
            return this.data.get(key);
        }

        public void write(Json json) {
            json.writeValue("data", (Object) this.data, ObjectMap.class);
            json.writeValue("indices", (Object) this.assets.toArray(), int[].class);
        }

        public void read(Json json, JsonValue jsonData) {
            this.data = (ObjectMap) json.readValue("data", ObjectMap.class, jsonData);
            this.assets.addAll((int[]) json.readValue("indices", int[].class, jsonData));
        }
    }

    public static class AssetData<T> implements Json.Serializable {
        public String filename;
        public Class<T> type;

        public AssetData() {
        }

        public AssetData(String filename2, Class<T> type2) {
            this.filename = filename2;
            this.type = type2;
        }

        public void write(Json json) {
            json.writeValue("filename", (Object) this.filename);
            json.writeValue("type", (Object) this.type.getName());
        }

        public void read(Json json, JsonValue jsonData) {
            this.filename = (String) json.readValue("filename", String.class, jsonData);
            String className = (String) json.readValue("type", String.class, jsonData);
            try {
                this.type = ClassReflection.forName(className);
            } catch (ReflectionException e) {
                throw new GdxRuntimeException("Class not found: " + className, e);
            }
        }
    }

    public ResourceData() {
        this.uniqueData = new ObjectMap<>();
        this.data = new Array<>(true, 3, SaveData.class);
        this.sharedAssets = new Array<>();
        this.currentLoadIndex = 0;
    }

    public ResourceData(T resource2) {
        this();
        this.resource = resource2;
    }

    /* access modifiers changed from: package-private */
    public <K> int getAssetData(String filename, Class<K> type) {
        int i = 0;
        Iterator<AssetData> it = this.sharedAssets.iterator();
        while (it.hasNext()) {
            AssetData data2 = it.next();
            if (data2.filename.equals(filename) && data2.type.equals(type)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public Array<AssetDescriptor> getAssetDescriptors() {
        Array<AssetDescriptor> descriptors = new Array<>();
        Iterator<AssetData> it = this.sharedAssets.iterator();
        while (it.hasNext()) {
            AssetData data2 = it.next();
            descriptors.add(new AssetDescriptor(data2.filename, data2.type));
        }
        return descriptors;
    }

    public Array<AssetData> getAssets() {
        return this.sharedAssets;
    }

    public SaveData createSaveData() {
        SaveData saveData = new SaveData(this);
        this.data.add(saveData);
        return saveData;
    }

    public SaveData createSaveData(String key) {
        SaveData saveData = new SaveData(this);
        if (!this.uniqueData.containsKey(key)) {
            this.uniqueData.put(key, saveData);
            return saveData;
        }
        throw new RuntimeException("Key already used, data must be unique, use a different key");
    }

    public SaveData getSaveData() {
        Array<SaveData> array = this.data;
        int i = this.currentLoadIndex;
        this.currentLoadIndex = i + 1;
        return array.get(i);
    }

    public SaveData getSaveData(String key) {
        return this.uniqueData.get(key);
    }

    public void write(Json json) {
        json.writeValue("unique", (Object) this.uniqueData, ObjectMap.class);
        json.writeValue("data", this.data, Array.class, SaveData.class);
        json.writeValue("assets", (Object) this.sharedAssets.toArray(AssetData.class), AssetData[].class);
        json.writeValue("resource", (Object) this.resource, (Class) null);
    }

    public void read(Json json, JsonValue jsonData) {
        this.uniqueData = (ObjectMap) json.readValue("unique", ObjectMap.class, jsonData);
        ObjectMap.Entries<String, SaveData> it = this.uniqueData.entries().iterator();
        while (it.hasNext()) {
            ((SaveData) ((ObjectMap.Entry) it.next()).value).resources = this;
        }
        this.data = (Array) json.readValue("data", Array.class, SaveData.class, jsonData);
        Iterator<SaveData> it2 = this.data.iterator();
        while (it2.hasNext()) {
            it2.next().resources = this;
        }
        this.sharedAssets.addAll((Array) json.readValue("assets", Array.class, AssetData.class, jsonData));
        this.resource = json.readValue("resource", (Class) null, jsonData);
    }
}
