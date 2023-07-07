package com.badlogic.gdx.utils;

public class Pools {
    private static final ObjectMap<Class, Pool> typePools = new ObjectMap<>();

    public static <T> Pool<T> get(Class<T> type, int max) {
        Pool pool = typePools.get(type);
        if (pool != null) {
            return pool;
        }
        Pool pool2 = new ReflectionPool(type, 4, max);
        typePools.put(type, pool2);
        return pool2;
    }

    public static <T> Pool<T> get(Class<T> type) {
        return get(type, 100);
    }

    public static <T> void set(Class<T> type, Pool<T> pool) {
        typePools.put(type, pool);
    }

    public static <T> T obtain(Class<T> type) {
        return get(type).obtain();
    }

    public static void free(Object object) {
        if (object != null) {
            Pool pool = typePools.get(object.getClass());
            if (pool != null) {
                pool.free(object);
                return;
            }
            return;
        }
        throw new IllegalArgumentException("Object cannot be null.");
    }

    public static void freeAll(Array objects) {
        freeAll(objects, false);
    }

    public static void freeAll(Array objects, boolean samePool) {
        if (objects != null) {
            Pool pool = null;
            int n = objects.size;
            for (int i = 0; i < n; i++) {
                Object object = objects.get(i);
                if (!(object == null || (pool == null && (pool = typePools.get(object.getClass())) == null))) {
                    pool.free(object);
                    if (!samePool) {
                        pool = null;
                    }
                }
            }
            return;
        }
        throw new IllegalArgumentException("Objects cannot be null.");
    }

    private Pools() {
    }
}
