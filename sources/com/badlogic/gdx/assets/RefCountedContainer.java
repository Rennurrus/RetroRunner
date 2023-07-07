package com.badlogic.gdx.assets;

public class RefCountedContainer {
    Object object;
    int refCount = 1;

    public RefCountedContainer(Object object2) {
        if (object2 != null) {
            this.object = object2;
            return;
        }
        throw new IllegalArgumentException("Object must not be null");
    }

    public void incRefCount() {
        this.refCount++;
    }

    public void decRefCount() {
        this.refCount--;
    }

    public int getRefCount() {
        return this.refCount;
    }

    public void setRefCount(int refCount2) {
        this.refCount = refCount2;
    }

    public <T> T getObject(Class<T> cls) {
        return this.object;
    }

    public void setObject(Object asset) {
        this.object = asset;
    }
}
