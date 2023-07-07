package com.badlogic.gdx.assets;

import com.badlogic.gdx.files.FileHandle;

public class AssetDescriptor<T> {
    public FileHandle file;
    public final String fileName;
    public final AssetLoaderParameters params;
    public final Class<T> type;

    public AssetDescriptor(String fileName2, Class<T> assetType) {
        this(fileName2, assetType, (AssetLoaderParameters) null);
    }

    public AssetDescriptor(FileHandle file2, Class<T> assetType) {
        this(file2, assetType, (AssetLoaderParameters) null);
    }

    public AssetDescriptor(String fileName2, Class<T> assetType, AssetLoaderParameters<T> params2) {
        this.fileName = fileName2.replaceAll("\\\\", "/");
        this.type = assetType;
        this.params = params2;
    }

    public AssetDescriptor(FileHandle file2, Class<T> assetType, AssetLoaderParameters<T> params2) {
        this.fileName = file2.path().replaceAll("\\\\", "/");
        this.file = file2;
        this.type = assetType;
        this.params = params2;
    }

    public String toString() {
        return this.fileName + ", " + this.type.getName();
    }
}
