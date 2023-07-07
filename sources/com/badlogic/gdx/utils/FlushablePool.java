package com.badlogic.gdx.utils;

public abstract class FlushablePool<T> extends Pool<T> {
    protected Array<T> obtained = new Array<>();

    public FlushablePool() {
    }

    public FlushablePool(int initialCapacity) {
        super(initialCapacity);
    }

    public FlushablePool(int initialCapacity, int max) {
        super(initialCapacity, max);
    }

    public T obtain() {
        T result = super.obtain();
        this.obtained.add(result);
        return result;
    }

    public void flush() {
        super.freeAll(this.obtained);
        this.obtained.clear();
    }

    public void free(T object) {
        this.obtained.removeValue(object, true);
        super.free(object);
    }

    public void freeAll(Array<T> objects) {
        this.obtained.removeAll(objects, true);
        super.freeAll(objects);
    }
}
