package com.badlogic.gdx.utils;

import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Constructor;
import com.badlogic.gdx.utils.reflect.ReflectionException;

public class ReflectionPool<T> extends Pool<T> {
    private final Constructor constructor;

    public ReflectionPool(Class<T> type) {
        this(type, 16, Integer.MAX_VALUE);
    }

    public ReflectionPool(Class<T> type, int initialCapacity) {
        this(type, initialCapacity, Integer.MAX_VALUE);
    }

    public ReflectionPool(Class<T> type, int initialCapacity, int max) {
        super(initialCapacity, max);
        this.constructor = findConstructor(type);
        if (this.constructor == null) {
            throw new RuntimeException("Class cannot be created (missing no-arg constructor): " + type.getName());
        }
    }

    private Constructor findConstructor(Class<T> type) {
        try {
            return ClassReflection.getConstructor(type, (Class[]) null);
        } catch (Exception e) {
            try {
                Constructor constructor2 = ClassReflection.getDeclaredConstructor(type, (Class[]) null);
                constructor2.setAccessible(true);
                return constructor2;
            } catch (ReflectionException e2) {
                return null;
            }
        }
    }

    /* access modifiers changed from: protected */
    public T newObject() {
        try {
            return this.constructor.newInstance((Object[]) null);
        } catch (Exception ex) {
            throw new GdxRuntimeException("Unable to create new instance: " + this.constructor.getDeclaringClass().getName(), ex);
        }
    }
}
