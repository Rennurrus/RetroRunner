package com.badlogic.gdx.utils;

import com.badlogic.gdx.utils.ObjectMap;
import java.util.NoSuchElementException;

public class OrderedMap<K, V> extends ObjectMap<K, V> {
    final Array<K> keys;

    public OrderedMap() {
        this.keys = new Array<>();
    }

    public OrderedMap(int initialCapacity) {
        super(initialCapacity);
        this.keys = new Array<>(this.capacity);
    }

    public OrderedMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
        this.keys = new Array<>(this.capacity);
    }

    public OrderedMap(OrderedMap<? extends K, ? extends V> map) {
        super(map);
        this.keys = new Array<>(map.keys);
    }

    public V put(K key, V value) {
        if (!containsKey(key)) {
            this.keys.add(key);
        }
        return super.put(key, value);
    }

    public V remove(K key) {
        this.keys.removeValue(key, false);
        return super.remove(key);
    }

    public V removeIndex(int index) {
        return super.remove(this.keys.removeIndex(index));
    }

    public void clear(int maximumCapacity) {
        this.keys.clear();
        super.clear(maximumCapacity);
    }

    public void clear() {
        this.keys.clear();
        super.clear();
    }

    public Array<K> orderedKeys() {
        return this.keys;
    }

    public ObjectMap.Entries<K, V> iterator() {
        return entries();
    }

    public ObjectMap.Entries<K, V> entries() {
        if (Collections.allocateIterators) {
            return new ObjectMap.Entries<>(this);
        }
        if (this.entries1 == null) {
            this.entries1 = new OrderedMapEntries(this);
            this.entries2 = new OrderedMapEntries(this);
        }
        if (!this.entries1.valid) {
            this.entries1.reset();
            this.entries1.valid = true;
            this.entries2.valid = false;
            return this.entries1;
        }
        this.entries2.reset();
        this.entries2.valid = true;
        this.entries1.valid = false;
        return this.entries2;
    }

    public ObjectMap.Values<V> values() {
        if (Collections.allocateIterators) {
            return new ObjectMap.Values<>(this);
        }
        if (this.values1 == null) {
            this.values1 = new OrderedMapValues(this);
            this.values2 = new OrderedMapValues(this);
        }
        if (!this.values1.valid) {
            this.values1.reset();
            this.values1.valid = true;
            this.values2.valid = false;
            return this.values1;
        }
        this.values2.reset();
        this.values2.valid = true;
        this.values1.valid = false;
        return this.values2;
    }

    public ObjectMap.Keys<K> keys() {
        if (Collections.allocateIterators) {
            return new ObjectMap.Keys<>(this);
        }
        if (this.keys1 == null) {
            this.keys1 = new OrderedMapKeys(this);
            this.keys2 = new OrderedMapKeys(this);
        }
        if (!this.keys1.valid) {
            this.keys1.reset();
            this.keys1.valid = true;
            this.keys2.valid = false;
            return this.keys1;
        }
        this.keys2.reset();
        this.keys2.valid = true;
        this.keys1.valid = false;
        return this.keys2;
    }

    public String toString() {
        if (this.size == 0) {
            return "{}";
        }
        StringBuilder buffer = new StringBuilder(32);
        buffer.append('{');
        Array<K> keys2 = this.keys;
        int n = keys2.size;
        for (int i = 0; i < n; i++) {
            K key = keys2.get(i);
            if (i > 0) {
                buffer.append(", ");
            }
            buffer.append((Object) key);
            buffer.append('=');
            buffer.append(get(key));
        }
        buffer.append('}');
        return buffer.toString();
    }

    public static class OrderedMapEntries<K, V> extends ObjectMap.Entries<K, V> {
        private Array<K> keys;

        public OrderedMapEntries(OrderedMap<K, V> map) {
            super(map);
            this.keys = map.keys;
        }

        public void reset() {
            boolean z = false;
            this.nextIndex = 0;
            if (this.map.size > 0) {
                z = true;
            }
            this.hasNext = z;
        }

        public ObjectMap.Entry next() {
            if (!this.hasNext) {
                throw new NoSuchElementException();
            } else if (this.valid) {
                this.entry.key = this.keys.get(this.nextIndex);
                this.entry.value = this.map.get(this.entry.key);
                boolean z = true;
                this.nextIndex++;
                if (this.nextIndex >= this.map.size) {
                    z = false;
                }
                this.hasNext = z;
                return this.entry;
            } else {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
        }

        public void remove() {
            if (this.currentIndex >= 0) {
                this.map.remove(this.entry.key);
                this.nextIndex--;
                return;
            }
            throw new IllegalStateException("next must be called before remove.");
        }
    }

    public static class OrderedMapKeys<K> extends ObjectMap.Keys<K> {
        private Array<K> keys;

        public OrderedMapKeys(OrderedMap<K, ?> map) {
            super(map);
            this.keys = map.keys;
        }

        public void reset() {
            boolean z = false;
            this.nextIndex = 0;
            if (this.map.size > 0) {
                z = true;
            }
            this.hasNext = z;
        }

        public K next() {
            if (!this.hasNext) {
                throw new NoSuchElementException();
            } else if (this.valid) {
                K key = this.keys.get(this.nextIndex);
                this.currentIndex = this.nextIndex;
                boolean z = true;
                this.nextIndex++;
                if (this.nextIndex >= this.map.size) {
                    z = false;
                }
                this.hasNext = z;
                return key;
            } else {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
        }

        public void remove() {
            if (this.currentIndex >= 0) {
                ((OrderedMap) this.map).removeIndex(this.nextIndex - 1);
                this.nextIndex = this.currentIndex;
                this.currentIndex = -1;
                return;
            }
            throw new IllegalStateException("next must be called before remove.");
        }
    }

    public static class OrderedMapValues<V> extends ObjectMap.Values<V> {
        private Array keys;

        public OrderedMapValues(OrderedMap<?, V> map) {
            super(map);
            this.keys = map.keys;
        }

        public void reset() {
            boolean z = false;
            this.nextIndex = 0;
            if (this.map.size > 0) {
                z = true;
            }
            this.hasNext = z;
        }

        public V next() {
            if (!this.hasNext) {
                throw new NoSuchElementException();
            } else if (this.valid) {
                V value = this.map.get(this.keys.get(this.nextIndex));
                this.currentIndex = this.nextIndex;
                boolean z = true;
                this.nextIndex++;
                if (this.nextIndex >= this.map.size) {
                    z = false;
                }
                this.hasNext = z;
                return value;
            } else {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
        }

        public void remove() {
            if (this.currentIndex >= 0) {
                ((OrderedMap) this.map).removeIndex(this.currentIndex);
                this.nextIndex = this.currentIndex;
                this.currentIndex = -1;
                return;
            }
            throw new IllegalStateException("next must be called before remove.");
        }
    }
}
