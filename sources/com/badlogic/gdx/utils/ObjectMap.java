package com.badlogic.gdx.utils;

import com.badlogic.gdx.math.MathUtils;
import com.twi.game.BuildConfig;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ObjectMap<K, V> implements Iterable<Entry<K, V>> {
    private static final int PRIME1 = -1105259343;
    private static final int PRIME2 = -1262997959;
    private static final int PRIME3 = -825114047;
    static final Object dummy = new Object();
    int capacity;
    Entries entries1;
    Entries entries2;
    private int hashShift;
    K[] keyTable;
    Keys keys1;
    Keys keys2;
    private float loadFactor;
    private int mask;
    private int pushIterations;
    public int size;
    private int stashCapacity;
    int stashSize;
    private int threshold;
    V[] valueTable;
    Values values1;
    Values values2;

    public ObjectMap() {
        this(51, 0.8f);
    }

    public ObjectMap(int initialCapacity) {
        this(initialCapacity, 0.8f);
    }

    public ObjectMap(int initialCapacity, float loadFactor2) {
        if (initialCapacity >= 0) {
            int initialCapacity2 = MathUtils.nextPowerOfTwo((int) Math.ceil((double) (((float) initialCapacity) / loadFactor2)));
            if (initialCapacity2 <= 1073741824) {
                this.capacity = initialCapacity2;
                if (loadFactor2 > 0.0f) {
                    this.loadFactor = loadFactor2;
                    int i = this.capacity;
                    this.threshold = (int) (((float) i) * loadFactor2);
                    this.mask = i - 1;
                    this.hashShift = 31 - Integer.numberOfTrailingZeros(i);
                    this.stashCapacity = Math.max(3, ((int) Math.ceil(Math.log((double) this.capacity))) * 2);
                    this.pushIterations = Math.max(Math.min(this.capacity, 8), ((int) Math.sqrt((double) this.capacity)) / 8);
                    this.keyTable = (Object[]) new Object[(this.capacity + this.stashCapacity)];
                    this.valueTable = (Object[]) new Object[this.keyTable.length];
                    return;
                }
                throw new IllegalArgumentException("loadFactor must be > 0: " + loadFactor2);
            }
            throw new IllegalArgumentException("initialCapacity is too large: " + initialCapacity2);
        }
        throw new IllegalArgumentException("initialCapacity must be >= 0: " + initialCapacity);
    }

    public ObjectMap(ObjectMap<? extends K, ? extends V> map) {
        this((int) Math.floor((double) (((float) map.capacity) * map.loadFactor)), map.loadFactor);
        this.stashSize = map.stashSize;
        K[] kArr = map.keyTable;
        System.arraycopy(kArr, 0, this.keyTable, 0, kArr.length);
        V[] vArr = map.valueTable;
        System.arraycopy(vArr, 0, this.valueTable, 0, vArr.length);
        this.size = map.size;
    }

    public V put(K key, V value) {
        K k = key;
        if (k != null) {
            K[] keyTable2 = this.keyTable;
            int hashCode = key.hashCode();
            int index1 = hashCode & this.mask;
            K key1 = keyTable2[index1];
            if (k.equals(key1)) {
                V[] vArr = this.valueTable;
                V oldValue = vArr[index1];
                vArr[index1] = value;
                return oldValue;
            }
            int index2 = hash2(hashCode);
            K key2 = keyTable2[index2];
            if (k.equals(key2)) {
                V[] vArr2 = this.valueTable;
                V oldValue2 = vArr2[index2];
                vArr2[index2] = value;
                return oldValue2;
            }
            int index3 = hash3(hashCode);
            K key3 = keyTable2[index3];
            if (k.equals(key3)) {
                V[] vArr3 = this.valueTable;
                V oldValue3 = vArr3[index3];
                vArr3[index3] = value;
                return oldValue3;
            }
            int i = this.capacity;
            int n = this.stashSize + i;
            while (i < n) {
                if (k.equals(keyTable2[i])) {
                    V[] vArr4 = this.valueTable;
                    V oldValue4 = vArr4[i];
                    vArr4[i] = value;
                    return oldValue4;
                }
                i++;
            }
            if (key1 == null) {
                keyTable2[index1] = k;
                this.valueTable[index1] = value;
                int i2 = this.size;
                this.size = i2 + 1;
                if (i2 >= this.threshold) {
                    resize(this.capacity << 1);
                }
                return null;
            } else if (key2 == null) {
                keyTable2[index2] = k;
                this.valueTable[index2] = value;
                int i3 = this.size;
                this.size = i3 + 1;
                if (i3 >= this.threshold) {
                    resize(this.capacity << 1);
                }
                return null;
            } else if (key3 == null) {
                keyTable2[index3] = k;
                this.valueTable[index3] = value;
                int i4 = this.size;
                this.size = i4 + 1;
                if (i4 >= this.threshold) {
                    resize(this.capacity << 1);
                }
                return null;
            } else {
                K k2 = key2;
                push(key, value, index1, key1, index2, key2, index3, key3);
                return null;
            }
        } else {
            throw new IllegalArgumentException("key cannot be null.");
        }
    }

    public void putAll(ObjectMap<? extends K, ? extends V> map) {
        ensureCapacity(map.size);
        Entries<? extends K, ? extends V> it = map.iterator();
        while (it.hasNext()) {
            Entry<? extends K, ? extends V> entry = (Entry) it.next();
            put(entry.key, entry.value);
        }
    }

    private void putResize(K key, V value) {
        int hashCode = key.hashCode();
        int index1 = hashCode & this.mask;
        K[] kArr = this.keyTable;
        K key1 = kArr[index1];
        if (key1 == null) {
            kArr[index1] = key;
            this.valueTable[index1] = value;
            int i = this.size;
            this.size = i + 1;
            if (i >= this.threshold) {
                resize(this.capacity << 1);
                return;
            }
            return;
        }
        int index2 = hash2(hashCode);
        K[] kArr2 = this.keyTable;
        K key2 = kArr2[index2];
        if (key2 == null) {
            kArr2[index2] = key;
            this.valueTable[index2] = value;
            int i2 = this.size;
            this.size = i2 + 1;
            if (i2 >= this.threshold) {
                resize(this.capacity << 1);
                return;
            }
            return;
        }
        int index3 = hash3(hashCode);
        K[] kArr3 = this.keyTable;
        K key3 = kArr3[index3];
        if (key3 == null) {
            kArr3[index3] = key;
            this.valueTable[index3] = value;
            int i3 = this.size;
            this.size = i3 + 1;
            if (i3 >= this.threshold) {
                resize(this.capacity << 1);
                return;
            }
            return;
        }
        push(key, value, index1, key1, index2, key2, index3, key3);
    }

    private void push(K insertKey, V insertValue, int index1, K key1, int index2, K key2, int index3, K key3) {
        V evictedValue;
        K evictedKey;
        K[] keyTable2 = this.keyTable;
        V[] valueTable2 = this.valueTable;
        int mask2 = this.mask;
        int pushIterations2 = this.pushIterations;
        K insertKey2 = insertKey;
        V insertValue2 = insertValue;
        int index12 = index1;
        K key12 = key1;
        int index22 = index2;
        K key22 = key2;
        int index32 = index3;
        int i = 0;
        K key32 = key3;
        while (true) {
            int random = MathUtils.random(2);
            if (random == 0) {
                evictedKey = key12;
                V evictedValue2 = valueTable2[index12];
                keyTable2[index12] = insertKey2;
                valueTable2[index12] = insertValue2;
                evictedValue = evictedValue2;
            } else if (random != 1) {
                evictedKey = key32;
                V evictedValue3 = valueTable2[index32];
                keyTable2[index32] = insertKey2;
                valueTable2[index32] = insertValue2;
                evictedValue = evictedValue3;
            } else {
                evictedKey = key22;
                V evictedValue4 = valueTable2[index22];
                keyTable2[index22] = insertKey2;
                valueTable2[index22] = insertValue2;
                evictedValue = evictedValue4;
            }
            K k = key32;
            int hashCode = evictedKey.hashCode();
            index12 = hashCode & mask2;
            key12 = keyTable2[index12];
            if (key12 == null) {
                keyTable2[index12] = evictedKey;
                valueTable2[index12] = evictedValue;
                int i2 = mask2;
                int mask3 = this.size;
                int i3 = index32;
                this.size = mask3 + 1;
                if (mask3 >= this.threshold) {
                    resize(this.capacity << 1);
                    return;
                }
                return;
            }
            int mask4 = mask2;
            int i4 = index32;
            index22 = hash2(hashCode);
            key22 = keyTable2[index22];
            if (key22 == null) {
                keyTable2[index22] = evictedKey;
                valueTable2[index22] = evictedValue;
                int i5 = this.size;
                this.size = i5 + 1;
                if (i5 >= this.threshold) {
                    resize(this.capacity << 1);
                    return;
                }
                return;
            }
            index32 = hash3(hashCode);
            K key33 = keyTable2[index32];
            if (key33 == null) {
                keyTable2[index32] = evictedKey;
                valueTable2[index32] = evictedValue;
                K[] kArr = keyTable2;
                int i6 = this.size;
                V[] vArr = valueTable2;
                this.size = i6 + 1;
                if (i6 >= this.threshold) {
                    resize(this.capacity << 1);
                    return;
                }
                return;
            }
            K[] keyTable3 = keyTable2;
            V[] valueTable3 = valueTable2;
            i++;
            if (i == pushIterations2) {
                putStash(evictedKey, evictedValue);
                return;
            }
            insertKey2 = evictedKey;
            insertValue2 = evictedValue;
            key32 = key33;
            mask2 = mask4;
            keyTable2 = keyTable3;
            valueTable2 = valueTable3;
        }
    }

    private void putStash(K key, V value) {
        int i = this.stashSize;
        if (i == this.stashCapacity) {
            resize(this.capacity << 1);
            putResize(key, value);
            return;
        }
        int index = this.capacity + i;
        this.keyTable[index] = key;
        this.valueTable[index] = value;
        this.stashSize = i + 1;
        this.size++;
    }

    public V get(K key) {
        int hashCode = key.hashCode();
        int index = this.mask & hashCode;
        if (!key.equals(this.keyTable[index])) {
            index = hash2(hashCode);
            if (!key.equals(this.keyTable[index])) {
                index = hash3(hashCode);
                if (!key.equals(this.keyTable[index])) {
                    return getStash(key, (Object) null);
                }
            }
        }
        return this.valueTable[index];
    }

    public V get(K key, V defaultValue) {
        int hashCode = key.hashCode();
        int index = this.mask & hashCode;
        if (!key.equals(this.keyTable[index])) {
            index = hash2(hashCode);
            if (!key.equals(this.keyTable[index])) {
                index = hash3(hashCode);
                if (!key.equals(this.keyTable[index])) {
                    return getStash(key, defaultValue);
                }
            }
        }
        return this.valueTable[index];
    }

    private V getStash(K key, V defaultValue) {
        K[] keyTable2 = this.keyTable;
        int i = this.capacity;
        int n = this.stashSize + i;
        while (i < n) {
            if (key.equals(keyTable2[i])) {
                return this.valueTable[i];
            }
            i++;
        }
        return defaultValue;
    }

    public V remove(K key) {
        int hashCode = key.hashCode();
        int index = this.mask & hashCode;
        if (key.equals(this.keyTable[index])) {
            this.keyTable[index] = null;
            V[] vArr = this.valueTable;
            V oldValue = vArr[index];
            vArr[index] = null;
            this.size--;
            return oldValue;
        }
        int index2 = hash2(hashCode);
        if (key.equals(this.keyTable[index2])) {
            this.keyTable[index2] = null;
            V[] vArr2 = this.valueTable;
            V oldValue2 = vArr2[index2];
            vArr2[index2] = null;
            this.size--;
            return oldValue2;
        }
        int index3 = hash3(hashCode);
        if (!key.equals(this.keyTable[index3])) {
            return removeStash(key);
        }
        this.keyTable[index3] = null;
        V[] vArr3 = this.valueTable;
        V oldValue3 = vArr3[index3];
        vArr3[index3] = null;
        this.size--;
        return oldValue3;
    }

    /* access modifiers changed from: package-private */
    public V removeStash(K key) {
        K[] keyTable2 = this.keyTable;
        int i = this.capacity;
        int n = this.stashSize + i;
        while (i < n) {
            if (key.equals(keyTable2[i])) {
                V oldValue = this.valueTable[i];
                removeStashIndex(i);
                this.size--;
                return oldValue;
            }
            i++;
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public void removeStashIndex(int index) {
        this.stashSize--;
        int lastIndex = this.capacity + this.stashSize;
        if (index < lastIndex) {
            K[] kArr = this.keyTable;
            kArr[index] = kArr[lastIndex];
            V[] vArr = this.valueTable;
            vArr[index] = vArr[lastIndex];
            kArr[lastIndex] = null;
            vArr[lastIndex] = null;
            return;
        }
        this.keyTable[index] = null;
        this.valueTable[index] = null;
    }

    public boolean notEmpty() {
        return this.size > 0;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public void shrink(int maximumCapacity) {
        if (maximumCapacity >= 0) {
            if (this.size > maximumCapacity) {
                maximumCapacity = this.size;
            }
            if (this.capacity > maximumCapacity) {
                resize(MathUtils.nextPowerOfTwo(maximumCapacity));
                return;
            }
            return;
        }
        throw new IllegalArgumentException("maximumCapacity must be >= 0: " + maximumCapacity);
    }

    public void clear(int maximumCapacity) {
        if (this.capacity <= maximumCapacity) {
            clear();
            return;
        }
        this.size = 0;
        resize(maximumCapacity);
    }

    public void clear() {
        if (this.size != 0) {
            K[] keyTable2 = this.keyTable;
            V[] valueTable2 = this.valueTable;
            int i = this.capacity + this.stashSize;
            while (true) {
                int i2 = i - 1;
                if (i > 0) {
                    keyTable2[i2] = null;
                    valueTable2[i2] = null;
                    i = i2;
                } else {
                    this.size = 0;
                    this.stashSize = 0;
                    return;
                }
            }
        }
    }

    public boolean containsValue(Object value, boolean identity) {
        V[] valueTable2 = this.valueTable;
        if (value == null) {
            K[] keyTable2 = this.keyTable;
            int i = this.capacity + this.stashSize;
            while (true) {
                int i2 = i - 1;
                if (i <= 0) {
                    return false;
                }
                if (keyTable2[i2] != null && valueTable2[i2] == null) {
                    return true;
                }
                i = i2;
            }
        } else if (identity) {
            int i3 = this.capacity + this.stashSize;
            while (true) {
                int i4 = i3 - 1;
                if (i3 <= 0) {
                    return false;
                }
                if (valueTable2[i4] == value) {
                    return true;
                }
                i3 = i4;
            }
        } else {
            int i5 = this.capacity + this.stashSize;
            while (true) {
                int i6 = i5 - 1;
                if (i5 <= 0) {
                    return false;
                }
                if (value.equals(valueTable2[i6])) {
                    return true;
                }
                i5 = i6;
            }
        }
    }

    public boolean containsKey(K key) {
        int hashCode = key.hashCode();
        if (key.equals(this.keyTable[this.mask & hashCode])) {
            return true;
        }
        if (key.equals(this.keyTable[hash2(hashCode)])) {
            return true;
        }
        if (!key.equals(this.keyTable[hash3(hashCode)])) {
            return containsKeyStash(key);
        }
        return true;
    }

    private boolean containsKeyStash(K key) {
        K[] keyTable2 = this.keyTable;
        int i = this.capacity;
        int n = this.stashSize + i;
        while (i < n) {
            if (key.equals(keyTable2[i])) {
                return true;
            }
            i++;
        }
        return false;
    }

    public K findKey(Object value, boolean identity) {
        V[] valueTable2 = this.valueTable;
        if (value == null) {
            K[] keyTable2 = this.keyTable;
            int i = this.capacity + this.stashSize;
            while (true) {
                int i2 = i - 1;
                if (i <= 0) {
                    return null;
                }
                if (keyTable2[i2] != null && valueTable2[i2] == null) {
                    return keyTable2[i2];
                }
                i = i2;
            }
        } else if (identity) {
            int i3 = this.capacity + this.stashSize;
            while (true) {
                int i4 = i3 - 1;
                if (i3 <= 0) {
                    return null;
                }
                if (valueTable2[i4] == value) {
                    return this.keyTable[i4];
                }
                i3 = i4;
            }
        } else {
            int i5 = this.capacity + this.stashSize;
            while (true) {
                int i6 = i5 - 1;
                if (i5 <= 0) {
                    return null;
                }
                if (value.equals(valueTable2[i6])) {
                    return this.keyTable[i6];
                }
                i5 = i6;
            }
        }
    }

    public void ensureCapacity(int additionalCapacity) {
        if (additionalCapacity >= 0) {
            int sizeNeeded = this.size + additionalCapacity;
            if (sizeNeeded >= this.threshold) {
                resize(MathUtils.nextPowerOfTwo((int) Math.ceil((double) (((float) sizeNeeded) / this.loadFactor))));
                return;
            }
            return;
        }
        throw new IllegalArgumentException("additionalCapacity must be >= 0: " + additionalCapacity);
    }

    private void resize(int newSize) {
        int oldEndIndex = this.capacity + this.stashSize;
        this.capacity = newSize;
        this.threshold = (int) (((float) newSize) * this.loadFactor);
        this.mask = newSize - 1;
        this.hashShift = 31 - Integer.numberOfTrailingZeros(newSize);
        this.stashCapacity = Math.max(3, ((int) Math.ceil(Math.log((double) newSize))) * 2);
        this.pushIterations = Math.max(Math.min(newSize, 8), ((int) Math.sqrt((double) newSize)) / 8);
        K[] oldKeyTable = this.keyTable;
        V[] oldValueTable = this.valueTable;
        int i = this.stashCapacity;
        this.keyTable = (Object[]) new Object[(newSize + i)];
        this.valueTable = (Object[]) new Object[(i + newSize)];
        int oldSize = this.size;
        this.size = 0;
        this.stashSize = 0;
        if (oldSize > 0) {
            for (int i2 = 0; i2 < oldEndIndex; i2++) {
                K key = oldKeyTable[i2];
                if (key != null) {
                    putResize(key, oldValueTable[i2]);
                }
            }
        }
    }

    private int hash2(int h) {
        int h2 = h * PRIME2;
        return ((h2 >>> this.hashShift) ^ h2) & this.mask;
    }

    private int hash3(int h) {
        int h2 = h * PRIME3;
        return ((h2 >>> this.hashShift) ^ h2) & this.mask;
    }

    public int hashCode() {
        int h = 0;
        K[] keyTable2 = this.keyTable;
        V[] valueTable2 = this.valueTable;
        int n = this.capacity + this.stashSize;
        for (int i = 0; i < n; i++) {
            K key = keyTable2[i];
            if (key != null) {
                h += key.hashCode() * 31;
                V value = valueTable2[i];
                if (value != null) {
                    h += value.hashCode();
                }
            }
        }
        return h;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ObjectMap)) {
            return false;
        }
        ObjectMap other = (ObjectMap) obj;
        if (other.size != this.size) {
            return false;
        }
        K[] keyTable2 = this.keyTable;
        V[] valueTable2 = this.valueTable;
        int n = this.capacity + this.stashSize;
        for (int i = 0; i < n; i++) {
            K key = keyTable2[i];
            if (key != null) {
                V value = valueTable2[i];
                if (value == null) {
                    if (other.get(key, dummy) != null) {
                        return false;
                    }
                } else if (!value.equals(other.get(key))) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean equalsIdentity(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof IdentityMap)) {
            return false;
        }
        IdentityMap other = (IdentityMap) obj;
        if (other.size != this.size) {
            return false;
        }
        K[] keyTable2 = this.keyTable;
        V[] valueTable2 = this.valueTable;
        int n = this.capacity + this.stashSize;
        for (int i = 0; i < n; i++) {
            K key = keyTable2[i];
            if (key != null && valueTable2[i] != other.get(key, dummy)) {
                return false;
            }
        }
        return true;
    }

    public String toString(String separator) {
        return toString(separator, false);
    }

    public String toString() {
        return toString(", ", true);
    }

    private String toString(String separator, boolean braces) {
        int i;
        if (this.size == 0) {
            return braces ? "{}" : BuildConfig.FLAVOR;
        }
        StringBuilder buffer = new StringBuilder(32);
        if (braces) {
            buffer.append('{');
        }
        K[] keyTable2 = this.keyTable;
        V[] valueTable2 = this.valueTable;
        int i2 = keyTable2.length;
        while (true) {
            i = i2 - 1;
            if (i2 > 0) {
                K key = keyTable2[i];
                if (key != null) {
                    buffer.append((Object) key);
                    buffer.append('=');
                    buffer.append((Object) valueTable2[i]);
                    break;
                }
                i2 = i;
            } else {
                break;
            }
        }
        while (true) {
            int i3 = i - 1;
            if (i <= 0) {
                break;
            }
            K key2 = keyTable2[i3];
            if (key2 != null) {
                buffer.append(separator);
                buffer.append((Object) key2);
                buffer.append('=');
                buffer.append((Object) valueTable2[i3]);
            }
            i = i3;
        }
        if (braces) {
            buffer.append('}');
        }
        return buffer.toString();
    }

    public Entries<K, V> iterator() {
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
            this.entries1.reset();
            Entries<K, V> entries = this.entries1;
            entries.valid = true;
            this.entries2.valid = false;
            return entries;
        }
        this.entries2.reset();
        Entries<K, V> entries3 = this.entries2;
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
            this.values1.reset();
            Values<V> values = this.values1;
            values.valid = true;
            this.values2.valid = false;
            return values;
        }
        this.values2.reset();
        Values<V> values3 = this.values2;
        values3.valid = true;
        this.values1.valid = false;
        return values3;
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
            this.keys1.reset();
            Keys<K> keys = this.keys1;
            keys.valid = true;
            this.keys2.valid = false;
            return keys;
        }
        this.keys2.reset();
        Keys<K> keys3 = this.keys2;
        keys3.valid = true;
        this.keys1.valid = false;
        return keys3;
    }

    public static class Entry<K, V> {
        public K key;
        public V value;

        public String toString() {
            return this.key + "=" + this.value;
        }
    }

    private static abstract class MapIterator<K, V, I> implements Iterable<I>, Iterator<I> {
        int currentIndex;
        public boolean hasNext;
        final ObjectMap<K, V> map;
        int nextIndex;
        boolean valid = true;

        public MapIterator(ObjectMap<K, V> map2) {
            this.map = map2;
            reset();
        }

        public void reset() {
            this.currentIndex = -1;
            this.nextIndex = -1;
            findNextIndex();
        }

        /* access modifiers changed from: package-private */
        public void findNextIndex() {
            this.hasNext = false;
            K[] keyTable = this.map.keyTable;
            int n = this.map.capacity + this.map.stashSize;
            do {
                int i = this.nextIndex + 1;
                this.nextIndex = i;
                if (i >= n) {
                    return;
                }
            } while (keyTable[this.nextIndex] == null);
            this.hasNext = true;
        }

        public void remove() {
            int i = this.currentIndex;
            if (i >= 0) {
                if (i >= this.map.capacity) {
                    this.map.removeStashIndex(this.currentIndex);
                    this.nextIndex = this.currentIndex - 1;
                    findNextIndex();
                } else {
                    this.map.keyTable[this.currentIndex] = null;
                    this.map.valueTable[this.currentIndex] = null;
                }
                this.currentIndex = -1;
                ObjectMap<K, V> objectMap = this.map;
                objectMap.size--;
                return;
            }
            throw new IllegalStateException("next must be called before remove.");
        }
    }

    public static class Entries<K, V> extends MapIterator<K, V, Entry<K, V>> {
        Entry<K, V> entry = new Entry<>();

        public /* bridge */ /* synthetic */ void remove() {
            super.remove();
        }

        public /* bridge */ /* synthetic */ void reset() {
            super.reset();
        }

        public Entries(ObjectMap<K, V> map) {
            super(map);
        }

        public Entry<K, V> next() {
            if (!this.hasNext) {
                throw new NoSuchElementException();
            } else if (this.valid) {
                K[] keyTable = this.map.keyTable;
                this.entry.key = keyTable[this.nextIndex];
                this.entry.value = this.map.valueTable[this.nextIndex];
                this.currentIndex = this.nextIndex;
                findNextIndex();
                return this.entry;
            } else {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
        }

        public boolean hasNext() {
            if (this.valid) {
                return this.hasNext;
            }
            throw new GdxRuntimeException("#iterator() cannot be used nested.");
        }

        public Entries<K, V> iterator() {
            return this;
        }
    }

    public static class Values<V> extends MapIterator<Object, V, V> {
        public /* bridge */ /* synthetic */ void remove() {
            super.remove();
        }

        public /* bridge */ /* synthetic */ void reset() {
            super.reset();
        }

        public Values(ObjectMap<?, V> map) {
            super(map);
        }

        public boolean hasNext() {
            if (this.valid) {
                return this.hasNext;
            }
            throw new GdxRuntimeException("#iterator() cannot be used nested.");
        }

        public V next() {
            if (!this.hasNext) {
                throw new NoSuchElementException();
            } else if (this.valid) {
                V value = this.map.valueTable[this.nextIndex];
                this.currentIndex = this.nextIndex;
                findNextIndex();
                return value;
            } else {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
        }

        public Values<V> iterator() {
            return this;
        }

        public Array<V> toArray() {
            return toArray(new Array(true, this.map.size));
        }

        public Array<V> toArray(Array<V> array) {
            while (this.hasNext) {
                array.add(next());
            }
            return array;
        }
    }

    public static class Keys<K> extends MapIterator<K, Object, K> {
        public /* bridge */ /* synthetic */ void remove() {
            super.remove();
        }

        public /* bridge */ /* synthetic */ void reset() {
            super.reset();
        }

        public Keys(ObjectMap<K, ?> map) {
            super(map);
        }

        public boolean hasNext() {
            if (this.valid) {
                return this.hasNext;
            }
            throw new GdxRuntimeException("#iterator() cannot be used nested.");
        }

        public K next() {
            if (!this.hasNext) {
                throw new NoSuchElementException();
            } else if (this.valid) {
                K key = this.map.keyTable[this.nextIndex];
                this.currentIndex = this.nextIndex;
                findNextIndex();
                return key;
            } else {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
        }

        public Keys<K> iterator() {
            return this;
        }

        public Array<K> toArray() {
            return toArray(new Array(true, this.map.size));
        }

        public Array<K> toArray(Array<K> array) {
            while (this.hasNext) {
                array.add(next());
            }
            return array;
        }
    }
}
