package com.badlogic.gdx.maps;

import com.badlogic.gdx.utils.ObjectMap;
import java.util.Iterator;

public class MapProperties {
    private ObjectMap<String, Object> properties = new ObjectMap<>();

    public boolean containsKey(String key) {
        return this.properties.containsKey(key);
    }

    public Object get(String key) {
        return this.properties.get(key);
    }

    public <T> T get(String key, Class<T> cls) {
        return get(key);
    }

    public <T> T get(String key, T defaultValue, Class<T> cls) {
        Object object = get(key);
        return object == null ? defaultValue : object;
    }

    public void put(String key, Object value) {
        this.properties.put(key, value);
    }

    public void putAll(MapProperties properties2) {
        this.properties.putAll(properties2.properties);
    }

    public void remove(String key) {
        this.properties.remove(key);
    }

    public void clear() {
        this.properties.clear();
    }

    public Iterator<String> getKeys() {
        return this.properties.keys();
    }

    public Iterator<Object> getValues() {
        return this.properties.values();
    }
}
