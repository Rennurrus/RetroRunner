package com.badlogic.gdx.utils;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.ArrayReflection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayMap<K, V> implements Iterable<ObjectMap.Entry<K, V>> {
    private Entries entries1;
    private Entries entries2;
    public K[] keys;
    private Keys keys1;
    private Keys keys2;
    public boolean ordered;
    public int size;
    public V[] values;
    private Values values1;
    private Values values2;

    public ArrayMap() {
        this(true, 16);
    }

    public ArrayMap(int capacity) {
        this(true, capacity);
    }

    public ArrayMap(boolean ordered2, int capacity) {
        this.ordered = ordered2;
        this.keys = (Object[]) new Object[capacity];
        this.values = (Object[]) new Object[capacity];
    }

    public ArrayMap(boolean ordered2, int capacity, Class keyArrayType, Class valueArrayType) {
        this.ordered = ordered2;
        this.keys = (Object[]) ArrayReflection.newInstance(keyArrayType, capacity);
        this.values = (Object[]) ArrayReflection.newInstance(valueArrayType, capacity);
    }

    public ArrayMap(Class keyArrayType, Class valueArrayType) {
        this(false, 16, keyArrayType, valueArrayType);
    }

    public ArrayMap(ArrayMap array) {
        this(array.ordered, array.size, array.keys.getClass().getComponentType(), array.values.getClass().getComponentType());
        this.size = array.size;
        System.arraycopy(array.keys, 0, this.keys, 0, this.size);
        System.arraycopy(array.values, 0, this.values, 0, this.size);
    }

    public int put(K key, V value) {
        int index = indexOfKey(key);
        if (index == -1) {
            int i = this.size;
            if (i == this.keys.length) {
                resize(Math.max(8, (int) (((float) i) * 1.75f)));
            }
            int i2 = this.size;
            this.size = i2 + 1;
            index = i2;
        }
        this.keys[index] = key;
        this.values[index] = value;
        return index;
    }

    public int put(K key, V value, int index) {
        int existingIndex = indexOfKey(key);
        if (existingIndex != -1) {
            removeIndex(existingIndex);
        } else {
            int i = this.size;
            if (i == this.keys.length) {
                resize(Math.max(8, (int) (((float) i) * 1.75f)));
            }
        }
        K[] kArr = this.keys;
        System.arraycopy(kArr, index, kArr, index + 1, this.size - index);
        V[] vArr = this.values;
        System.arraycopy(vArr, index, vArr, index + 1, this.size - index);
        this.keys[index] = key;
        this.values[index] = value;
        this.size++;
        return index;
    }

    public void putAll(ArrayMap<? extends K, ? extends V> map) {
        putAll(map, 0, map.size);
    }

    public void putAll(ArrayMap<? extends K, ? extends V> map, int offset, int length) {
        if (offset + length <= map.size) {
            int sizeNeeded = (this.size + length) - offset;
            if (sizeNeeded >= this.keys.length) {
                resize(Math.max(8, (int) (((float) sizeNeeded) * 1.75f)));
            }
            System.arraycopy(map.keys, offset, this.keys, this.size, length);
            System.arraycopy(map.values, offset, this.values, this.size, length);
            this.size += length;
            return;
        }
        throw new IllegalArgumentException("offset + length must be <= size: " + offset + " + " + length + " <= " + map.size);
    }

    public V get(K key) {
        return get(key, (Object) null);
    }

    public V get(K key, V defaultValue) {
        Object[] keys3 = this.keys;
        int i = this.size - 1;
        if (key == null) {
            while (i >= 0) {
                if (keys3[i] == key) {
                    return this.values[i];
                }
                i--;
            }
        } else {
            while (i >= 0) {
                if (key.equals(keys3[i])) {
                    return this.values[i];
                }
                i--;
            }
        }
        return defaultValue;
    }

    public K getKey(V value, boolean identity) {
        Object[] values3 = this.values;
        int i = this.size - 1;
        if (identity || value == null) {
            while (i >= 0) {
                if (values3[i] == value) {
                    return this.keys[i];
                }
                i--;
            }
            return null;
        }
        while (i >= 0) {
            if (value.equals(values3[i])) {
                return this.keys[i];
            }
            i--;
        }
        return null;
    }

    public K getKeyAt(int index) {
        if (index < this.size) {
            return this.keys[index];
        }
        throw new IndexOutOfBoundsException(String.valueOf(index));
    }

    public V getValueAt(int index) {
        if (index < this.size) {
            return this.values[index];
        }
        throw new IndexOutOfBoundsException(String.valueOf(index));
    }

    public K firstKey() {
        if (this.size != 0) {
            return this.keys[0];
        }
        throw new IllegalStateException("Map is empty.");
    }

    public V firstValue() {
        if (this.size != 0) {
            return this.values[0];
        }
        throw new IllegalStateException("Map is empty.");
    }

    public void setKey(int index, K key) {
        if (index < this.size) {
            this.keys[index] = key;
            return;
        }
        throw new IndexOutOfBoundsException(String.valueOf(index));
    }

    public void setValue(int index, V value) {
        if (index < this.size) {
            this.values[index] = value;
            return;
        }
        throw new IndexOutOfBoundsException(String.valueOf(index));
    }

    public void insert(int index, K key, V value) {
        int i = this.size;
        if (index <= i) {
            if (i == this.keys.length) {
                resize(Math.max(8, (int) (((float) i) * 1.75f)));
            }
            if (this.ordered) {
                K[] kArr = this.keys;
                System.arraycopy(kArr, index, kArr, index + 1, this.size - index);
                V[] vArr = this.values;
                System.arraycopy(vArr, index, vArr, index + 1, this.size - index);
            } else {
                K[] kArr2 = this.keys;
                int i2 = this.size;
                kArr2[i2] = kArr2[index];
                V[] vArr2 = this.values;
                vArr2[i2] = vArr2[index];
            }
            this.size++;
            this.keys[index] = key;
            this.values[index] = value;
            return;
        }
        throw new IndexOutOfBoundsException(String.valueOf(index));
    }

    public boolean containsKey(K key) {
        K[] keys3 = this.keys;
        int i = this.size - 1;
        if (key == null) {
            while (i >= 0) {
                int i2 = i - 1;
                if (keys3[i] == key) {
                    return true;
                }
                i = i2;
            }
            return false;
        }
        while (i >= 0) {
            int i3 = i - 1;
            if (key.equals(keys3[i])) {
                return true;
            }
            i = i3;
        }
        return false;
    }

    public boolean containsValue(V value, boolean identity) {
        V[] values3 = this.values;
        int i = this.size - 1;
        if (identity || value == null) {
            while (i >= 0) {
                int i2 = i - 1;
                if (values3[i] == value) {
                    return true;
                }
                i = i2;
            }
            return false;
        }
        while (i >= 0) {
            int i3 = i - 1;
            if (value.equals(values3[i])) {
                return true;
            }
            i = i3;
        }
        return false;
    }

    public int indexOfKey(K key) {
        Object[] keys3 = this.keys;
        if (key == null) {
            int n = this.size;
            for (int i = 0; i < n; i++) {
                if (keys3[i] == key) {
                    return i;
                }
            }
            return -1;
        }
        int n2 = this.size;
        for (int i2 = 0; i2 < n2; i2++) {
            if (key.equals(keys3[i2])) {
                return i2;
            }
        }
        return -1;
    }

    public int indexOfValue(V value, boolean identity) {
        Object[] values3 = this.values;
        if (identity || value == null) {
            int n = this.size;
            for (int i = 0; i < n; i++) {
                if (values3[i] == value) {
                    return i;
                }
            }
            return -1;
        }
        int n2 = this.size;
        for (int i2 = 0; i2 < n2; i2++) {
            if (value.equals(values3[i2])) {
                return i2;
            }
        }
        return -1;
    }

    public V removeKey(K key) {
        Object[] keys3 = this.keys;
        if (key == null) {
            int n = this.size;
            for (int i = 0; i < n; i++) {
                if (keys3[i] == key) {
                    V value = this.values[i];
                    removeIndex(i);
                    return value;
                }
            }
            return null;
        }
        int n2 = this.size;
        for (int i2 = 0; i2 < n2; i2++) {
            if (key.equals(keys3[i2])) {
                V value2 = this.values[i2];
                removeIndex(i2);
                return value2;
            }
        }
        return null;
    }

    public boolean removeValue(V value, boolean identity) {
        Object[] values3 = this.values;
        if (identity || value == null) {
            int n = this.size;
            for (int i = 0; i < n; i++) {
                if (values3[i] == value) {
                    removeIndex(i);
                    return true;
                }
            }
            return false;
        }
        int n2 = this.size;
        for (int i2 = 0; i2 < n2; i2++) {
            if (value.equals(values3[i2])) {
                removeIndex(i2);
                return true;
            }
        }
        return false;
    }

    public void removeIndex(int index) {
        int i = this.size;
        if (index < i) {
            Object[] keys3 = this.keys;
            this.size = i - 1;
            if (this.ordered) {
                System.arraycopy(keys3, index + 1, keys3, index, this.size - index);
                V[] vArr = this.values;
                System.arraycopy(vArr, index + 1, vArr, index, this.size - index);
            } else {
                int i2 = this.size;
                keys3[index] = keys3[i2];
                V[] vArr2 = this.values;
                vArr2[index] = vArr2[i2];
            }
            int i3 = this.size;
            keys3[i3] = null;
            this.values[i3] = null;
            return;
        }
        throw new IndexOutOfBoundsException(String.valueOf(index));
    }

    public boolean notEmpty() {
        return this.size > 0;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public K peekKey() {
        return this.keys[this.size - 1];
    }

    public V peekValue() {
        return this.values[this.size - 1];
    }

    public void clear(int maximumCapacity) {
        if (this.keys.length <= maximumCapacity) {
            clear();
            return;
        }
        this.size = 0;
        resize(maximumCapacity);
    }

    public void clear() {
        K[] keys3 = this.keys;
        V[] values3 = this.values;
        int n = this.size;
        for (int i = 0; i < n; i++) {
            keys3[i] = null;
            values3[i] = null;
        }
        this.size = 0;
    }

    public void shrink() {
        int length = this.keys.length;
        int i = this.size;
        if (length != i) {
            resize(i);
        }
    }

    public void ensureCapacity(int additionalCapacity) {
        if (additionalCapacity >= 0) {
            int sizeNeeded = this.size + additionalCapacity;
            if (sizeNeeded >= this.keys.length) {
                resize(Math.max(8, sizeNeeded));
                return;
            }
            return;
        }
        throw new IllegalArgumentException("additionalCapacity must be >= 0: " + additionalCapacity);
    }

    /* access modifiers changed from: protected */
    public void resize(int newSize) {
        K[] newKeys = (Object[]) ArrayReflection.newInstance(this.keys.getClass().getComponentType(), newSize);
        System.arraycopy(this.keys, 0, newKeys, 0, Math.min(this.size, newKeys.length));
        this.keys = newKeys;
        V[] newValues = (Object[]) ArrayReflection.newInstance(this.values.getClass().getComponentType(), newSize);
        System.arraycopy(this.values, 0, newValues, 0, Math.min(this.size, newValues.length));
        this.values = newValues;
    }

    public void reverse() {
        int i = this.size;
        int lastIndex = i - 1;
        int n = i / 2;
        for (int i2 = 0; i2 < n; i2++) {
            int ii = lastIndex - i2;
            K[] kArr = this.keys;
            K tempKey = kArr[i2];
            kArr[i2] = kArr[ii];
            kArr[ii] = tempKey;
            V[] vArr = this.values;
            V tempValue = vArr[i2];
            vArr[i2] = vArr[ii];
            vArr[ii] = tempValue;
        }
    }

    public void shuffle() {
        for (int i = this.size - 1; i >= 0; i--) {
            int ii = MathUtils.random(i);
            K[] kArr = this.keys;
            K tempKey = kArr[i];
            kArr[i] = kArr[ii];
            kArr[ii] = tempKey;
            V[] vArr = this.values;
            V tempValue = vArr[i];
            vArr[i] = vArr[ii];
            vArr[ii] = tempValue;
        }
    }

    public void truncate(int newSize) {
        if (this.size > newSize) {
            for (int i = newSize; i < this.size; i++) {
                this.keys[i] = null;
                this.values[i] = null;
            }
            this.size = newSize;
        }
    }

    public int hashCode() {
        K[] keys3 = this.keys;
        V[] values3 = this.values;
        int h = 0;
        int n = this.size;
        for (int i = 0; i < n; i++) {
            K key = keys3[i];
            V value = values3[i];
            if (key != null) {
                h += key.hashCode() * 31;
            }
            if (value != null) {
                h += value.hashCode();
            }
        }
        return h;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ArrayMap)) {
            return false;
        }
        ArrayMap other = (ArrayMap) obj;
        if (other.size != this.size) {
            return false;
        }
        K[] keys3 = this.keys;
        V[] values3 = this.values;
        int n = this.size;
        for (int i = 0; i < n; i++) {
            K key = keys3[i];
            V value = values3[i];
            if (value == null) {
                if (other.get(key, ObjectMap.dummy) != null) {
                    return false;
                }
            } else if (!value.equals(other.get(key))) {
                return false;
            }
        }
        return true;
    }

    public boolean equalsIdentity(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ArrayMap)) {
            return false;
        }
        ArrayMap other = (ArrayMap) obj;
        if (other.size != this.size) {
            return false;
        }
        K[] keys3 = this.keys;
        V[] values3 = this.values;
        int n = this.size;
        for (int i = 0; i < n; i++) {
            if (values3[i] != other.get(keys3[i], ObjectMap.dummy)) {
                return false;
            }
        }
        return true;
    }

    public String toString() {
        if (this.size == 0) {
            return "{}";
        }
        K[] keys3 = this.keys;
        V[] values3 = this.values;
        StringBuilder buffer = new StringBuilder(32);
        buffer.append('{');
        buffer.append((Object) keys3[0]);
        buffer.append('=');
        buffer.append((Object) values3[0]);
        for (int i = 1; i < this.size; i++) {
            buffer.append(", ");
            buffer.append((Object) keys3[i]);
            buffer.append('=');
            buffer.append((Object) values3[i]);
        }
        buffer.append('}');
        return buffer.toString();
    }

    public Iterator<ObjectMap.Entry<K, V>> iterator() {
        return entries();
    }

    public Entries<K, V> entries() {
        if (Collections.allocateIterators) {
            return new Entries<>(this);
        }
        if (this.entries1 == null) {
            this.entries1 = new Entries(this);
            this.entries2 = new Entries(this);
        }
        if (!this.entries1.valid) {
            Entries<K, V> entries = this.entries1;
            entries.index = 0;
            entries.valid = true;
            this.entries2.valid = false;
            return entries;
        }
        Entries<K, V> entries3 = this.entries2;
        entries3.index = 0;
        entries3.valid = true;
        this.entries1.valid = false;
        return entries3;
    }

    public Values<V> values() {
        if (Collections.allocateIterators) {
            return new Values<>(this);
        }
        if (this.values1 == null) {
            this.values1 = new Values(this);
            this.values2 = new Values(this);
        }
        if (!this.values1.valid) {
            Values<V> values3 = this.values1;
            values3.index = 0;
            values3.valid = true;
            this.values2.valid = false;
            return values3;
        }
        Values<V> values4 = this.values2;
        values4.index = 0;
        values4.valid = true;
        this.values1.valid = false;
        return values4;
    }

    public Keys<K> keys() {
        if (Collections.allocateIterators) {
            return new Keys<>(this);
        }
        if (this.keys1 == null) {
            this.keys1 = new Keys(this);
            this.keys2 = new Keys(this);
        }
        if (!this.keys1.valid) {
            Keys<K> keys3 = this.keys1;
            keys3.index = 0;
            keys3.valid = true;
            this.keys2.valid = false;
            return keys3;
        }
        Keys<K> keys4 = this.keys2;
        keys4.index = 0;
        keys4.valid = true;
        this.keys1.valid = false;
        return keys4;
    }

    public static class Entries<K, V> implements Iterable<ObjectMap.Entry<K, V>>, Iterator<ObjectMap.Entry<K, V>> {
        ObjectMap.Entry<K, V> entry = new ObjectMap.Entry<>();
        int index;
        private final ArrayMap<K, V> map;
        boolean valid = true;

        public Entries(ArrayMap<K, V> map2) {
            this.map = map2;
        }

        public boolean hasNext() {
            if (this.valid) {
                return this.index < this.map.size;
            }
            throw new GdxRuntimeException("#iterator() cannot be used nested.");
        }

        public Iterator<ObjectMap.Entry<K, V>> iterator() {
            return this;
        }

        public ObjectMap.Entry<K, V> next() {
            if (this.index >= this.map.size) {
                throw new NoSuchElementException(String.valueOf(this.index));
            } else if (this.valid) {
                this.entry.key = this.map.keys[this.index];
                ObjectMap.Entry<K, V> entry2 = this.entry;
                V[] vArr = this.map.values;
                int i = this.index;
                this.index = i + 1;
                entry2.value = vArr[i];
                return this.entry;
            } else {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
        }

        public void remove() {
            this.index--;
            this.map.removeIndex(this.index);
        }

        public void reset() {
            this.index = 0;
        }
    }

    public static class Values<V> implements Iterable<V>, Iterator<V> {
        int index;
        private final ArrayMap<Object, V> map;
        boolean valid = true;

        public Values(ArrayMap<Object, V> map2) {
            this.map = map2;
        }

        public boolean hasNext() {
            if (this.valid) {
                return this.index < this.map.size;
            }
            throw new GdxRuntimeException("#iterator() cannot be used nested.");
        }

        public Iterator<V> iterator() {
            return this;
        }

        public V next() {
            if (this.index >= this.map.size) {
                throw new NoSuchElementException(String.valueOf(this.index));
            } else if (this.valid) {
                V[] vArr = this.map.values;
                int i = this.index;
                this.index = i + 1;
                return vArr[i];
            } else {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
        }

        public void remove() {
            this.index--;
            this.map.removeIndex(this.index);
        }

        public void reset() {
            this.index = 0;
        }

        public Array<V> toArray() {
            return new Array<>(true, this.map.values, this.index, this.map.size - this.index);
        }

        public Array<V> toArray(Array array) {
            array.addAll((T[]) this.map.values, this.index, this.map.size - this.index);
            return array;
        }
    }

    public static class Keys<K> implements Iterable<K>, Iterator<K> {
        int index;
        private final ArrayMap<K, Object> map;
        boolean valid = true;

        public Keys(ArrayMap<K, Object> map2) {
            this.map = map2;
        }

        public boolean hasNext() {
            if (this.valid) {
                return this.index < this.map.size;
            }
            throw new GdxRuntimeException("#iterator() cannot be used nested.");
        }

        public Iterator<K> iterator() {
            return this;
        }

        public K next() {
            if (this.index >= this.map.size) {
                throw new NoSuchElementException(String.valueOf(this.index));
            } else if (this.valid) {
                K[] kArr = this.map.keys;
                int i = this.index;
                this.index = i + 1;
                return kArr[i];
            } else {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
        }

        public void remove() {
            this.index--;
            this.map.removeIndex(this.index);
        }

        public void reset() {
            this.index = 0;
        }

        public Array<K> toArray() {
            return new Array<>(true, this.map.keys, this.index, this.map.size - this.index);
        }

        public Array<K> toArray(Array array) {
            array.addAll((T[]) this.map.keys, this.index, this.map.size - this.index);
            return array;
        }
    }
}
