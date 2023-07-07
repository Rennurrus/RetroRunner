package com.badlogic.gdx.utils;

public abstract class Pool<T> {
    private final Array<T> freeObjects;
    public final int max;
    public int peak;

    public interface Poolable {
        void reset();
    }

    /* access modifiers changed from: protected */
    public abstract T newObject();

    public Pool() {
        this(16, Integer.MAX_VALUE);
    }

    public Pool(int initialCapacity) {
        this(initialCapacity, Integer.MAX_VALUE);
    }

    public Pool(int initialCapacity, int max2) {
        this.freeObjects = new Array<>(false, initialCapacity);
        this.max = max2;
    }

    public T obtain() {
        return this.freeObjects.size == 0 ? newObject() : this.freeObjects.pop();
    }

    public void free(T object) {
        if (object != null) {
            if (this.freeObjects.size < this.max) {
                this.freeObjects.add(object);
                this.peak = Math.max(this.peak, this.freeObjects.size);
            }
            reset(object);
            return;
        }
        throw new IllegalArgumentException("object cannot be null.");
    }

    /* access modifiers changed from: protected */
    public void reset(T object) {
        if (object instanceof Poolable) {
            ((Poolable) object).reset();
        }
    }

    public void freeAll(Array<T> objects) {
        if (objects != null) {
            Array<T> freeObjects2 = this.freeObjects;
            int max2 = this.max;
            for (int i = 0; i < objects.size; i++) {
                T object = objects.get(i);
                if (object != null) {
                    if (freeObjects2.size < max2) {
                        freeObjects2.add(object);
                    }
                    reset(object);
                }
            }
            this.peak = Math.max(this.peak, freeObjects2.size);
            return;
        }
        throw new IllegalArgumentException("objects cannot be null.");
    }

    public void clear() {
        this.freeObjects.clear();
    }

    public int getFree() {
        return this.freeObjects.size;
    }
}
