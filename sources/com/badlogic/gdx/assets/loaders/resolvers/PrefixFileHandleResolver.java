package com.badlogic.gdx.assets.loaders.resolvers;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;

public class PrefixFileHandleResolver implements FileHandleResolver {
    private FileHandleResolver baseResolver;
    private String prefix;

    public PrefixFileHandleResolver(FileHandleResolver baseResolver2, String prefix2) {
        this.baseResolver = baseResolver2;
        this.prefix = prefix2;
    }

    public void setBaseResolver(FileHandleResolver baseResolver2) {
        this.baseResolver = baseResolver2;
    }

    public FileHandleResolver getBaseResolver() {
        return this.baseResolver;
    }

    public void setPrefix(String prefix2) {
        this.prefix = prefix2;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public FileHandle resolve(String fileName) {
        FileHandleResolver fileHandleResolver = this.baseResolver;
        return fileHandleResolver.resolve(this.prefix + fileName);
    }
}
