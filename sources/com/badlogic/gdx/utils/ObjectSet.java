package com.badlogic.gdx.utils;

import com.badlogic.gdx.math.MathUtils;
import com.twi.game.BuildConfig;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ObjectSet<T> implements Iterable<T> {
    private static final int PRIME1 = -1105259343;
    private static final int PRIME2 = -1262997959;
    private static final int PRIME3 = -825114047;
    int capacity;
    private int hashShift;
    private ObjectSetIterator iterator1;
    private ObjectSetIterator iterator2;
    T[] keyTable;
    private float loadFactor;
    private int mask;
    private int pushIterations;
    public int size;
    private int stashCapacity;
    int stashSize;
    private int threshold;

    public ObjectSet() {
        this(51, 0.8f);
    }

    public ObjectSet(int initialCapacity) {
        this(initialCapacity, 0.8f);
    }

    public ObjectSet(int initialCapacity, float loadFactor2) {
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
                    return;
                }
                throw new IllegalArgumentException("loadFactor must be > 0: " + loadFactor2);
            }
            throw new IllegalArgumentException("initialCapacity is too large: " + initialCapacity2);
        }
        throw new IllegalArgumentException("initialCapacity must be >= 0: " + initialCapacity);
    }

    public ObjectSet(ObjectSet<? extends T> set) {
        this((int) Math.floor((double) (((float) set.capacity) * set.loadFactor)), set.loadFactor);
        this.stashSize = set.stashSize;
        T[] tArr = set.keyTable;
        System.arraycopy(tArr, 0, this.keyTable, 0, tArr.length);
        this.size = set.size;
    }

    public boolean add(T key) {
        T t = key;
        if (t != null) {
            T[] keyTable2 = this.keyTable;
            int hashCode = key.hashCode();
            int index1 = hashCode & this.mask;
            T key1 = keyTable2[index1];
            if (t.equals(key1)) {
                return false;
            }
            int index2 = hash2(hashCode);
            T key2 = keyTable2[index2];
            if (t.equals(key2)) {
                return false;
            }
            int index3 = hash3(hashCode);
            T key3 = keyTable2[index3];
            if (t.equals(key3)) {
                return false;
            }
            int i = this.capacity;
            int n = this.stashSize + i;
            while (i < n) {
                if (t.equals(keyTable2[i])) {
                    return false;
                }
                i++;
            }
            if (key1 == null) {
                keyTable2[index1] = t;
                int i2 = this.size;
                this.size = i2 + 1;
                if (i2 >= this.threshold) {
                    resize(this.capacity << 1);
                }
                return true;
            } else if (key2 == null) {
                keyTable2[index2] = t;
                int i3 = this.size;
                this.size = i3 + 1;
                if (i3 >= this.threshold) {
                    resize(this.capacity << 1);
                }
                return true;
            } else if (key3 == null) {
                keyTable2[index3] = t;
                int i4 = this.size;
                this.size = i4 + 1;
                if (i4 >= this.threshold) {
                    resize(this.capacity << 1);
                }
                return true;
            } else {
                T t2 = key3;
                push(key, index1, key1, index2, key2, index3, key3);
                return true;
            }
        } else {
            throw new IllegalArgumentException("key cannot be null.");
        }
    }

    public void addAll(Array<? extends T> array) {
        addAll(array.items, 0, array.size);
    }

    public void addAll(Array<? extends T> array, int offset, int length) {
        if (offset + length <= array.size) {
            addAll((T[]) (Object[]) array.items, offset, length);
            return;
        }
        throw new IllegalArgumentException("offset + length must be <= size: " + offset + " + " + length + " <= " + array.size);
    }

    public void addAll(T... array) {
        addAll(array, 0, array.length);
    }

    public void addAll(T[] array, int offset, int length) {
        ensureCapacity(length);
        int i = offset;
        int n = i + length;
        while (i < n) {
            add(array[i]);
            i++;
        }
    }

    public void addAll(ObjectSet<T> set) {
        ensureCapacity(set.size);
        ObjectSetIterator<T> it = set.iterator();
        while (it.hasNext()) {
            add(it.next());
        }
    }

    private void addResize(T key) {
        int hashCode = key.hashCode();
        int index1 = hashCode & this.mask;
        T[] tArr = this.keyTable;
        T key1 = tArr[index1];
        if (key1 == null) {
            tArr[index1] = key;
            int i = this.size;
            this.size = i + 1;
            if (i >= this.threshold) {
                resize(this.capacity << 1);
                return;
            }
            return;
        }
        int index2 = hash2(hashCode);
        T[] tArr2 = this.keyTable;
        T key2 = tArr2[index2];
        if (key2 == null) {
            tArr2[index2] = key;
            int i2 = this.size;
            this.size = i2 + 1;
            if (i2 >= this.threshold) {
                resize(this.capacity << 1);
                return;
            }
            return;
        }
        int index3 = hash3(hashCode);
        T[] tArr3 = this.keyTable;
        T key3 = tArr3[index3];
        if (key3 == null) {
            tArr3[index3] = key;
            int i3 = this.size;
            this.size = i3 + 1;
            if (i3 >= this.threshold) {
                resize(this.capacity << 1);
                return;
            }
            return;
        }
        push(key, index1, key1, index2, key2, index3, key3);
    }

    private void push(T insertKey, int index1, T key1, int index2, T key2, int index3, T key3) {
        T evictedKey;
        T[] keyTable2 = this.keyTable;
        int mask2 = this.mask;
        int pushIterations2 = this.pushIterations;
        T insertKey2 = insertKey;
        int index12 = index1;
        T key12 = key1;
        int index22 = index2;
        T key22 = key2;
        int index32 = index3;
        int i = 0;
        T key32 = key3;
        while (true) {
            int random = MathUtils.random(2);
            if (random == 0) {
                evictedKey = key12;
                keyTable2[index12] = insertKey2;
            } else if (random != 1) {
                evictedKey = key32;
                keyTable2[index32] = insertKey2;
            } else {
                evictedKey = key22;
                keyTable2[index22] = insertKey2;
            }
            int hashCode = evictedKey.hashCode();
            index12 = hashCode & mask2;
            key12 = keyTable2[index12];
            if (key12 == null) {
                keyTable2[index12] = evictedKey;
                int i2 = this.size;
                this.size = i2 + 1;
                if (i2 >= this.threshold) {
                    resize(this.capacity << 1);
                    return;
                }
                return;
            }
            index22 = hash2(hashCode);
            key22 = keyTable2[index22];
            if (key22 == null) {
                keyTable2[index22] = evictedKey;
                int i3 = this.size;
                this.size = i3 + 1;
                if (i3 >= this.threshold) {
                    resize(this.capacity << 1);
                    return;
                }
                return;
            }
            index32 = hash3(hashCode);
            key32 = keyTable2[index32];
            if (key32 == null) {
                keyTable2[index32] = evictedKey;
                int i4 = this.size;
                this.size = i4 + 1;
                if (i4 >= this.threshold) {
                    resize(this.capacity << 1);
                    return;
                }
                return;
            }
            i++;
            if (i == pushIterations2) {
                addStash(evictedKey);
                return;
            }
            insertKey2 = evictedKey;
        }
    }

    private void addStash(T key) {
        int i = this.stashSize;
        if (i == this.stashCapacity) {
            resize(this.capacity << 1);
            addResize(key);
            return;
        }
        this.keyTable[this.capacity + i] = key;
        this.stashSize = i + 1;
        this.size++;
    }

    public boolean remove(T key) {
        int hashCode = key.hashCode();
        int index = this.mask & hashCode;
        if (key.equals(this.keyTable[index])) {
            this.keyTable[index] = null;
            this.size--;
            return true;
        }
        int index2 = hash2(hashCode);
        if (key.equals(this.keyTable[index2])) {
            this.keyTable[index2] = null;
            this.size--;
            return true;
        }
        int index3 = hash3(hashCode);
        if (!key.equals(this.keyTable[index3])) {
            return removeStash(key);
        }
        this.keyTable[index3] = null;
        this.size--;
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean removeStash(T key) {
        T[] keyTable2 = this.keyTable;
        int i = this.capacity;
        int n = this.stashSize + i;
        while (i < n) {
            if (key.equals(keyTable2[i])) {
                removeStashIndex(i);
                this.size--;
                return true;
            }
            i++;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void removeStashIndex(int index) {
        this.stashSize--;
        int lastIndex = this.capacity + this.stashSize;
        if (index < lastIndex) {
            T[] tArr = this.keyTable;
            tArr[index] = tArr[lastIndex];
            tArr[lastIndex] = null;
        }
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
            T[] keyTable2 = this.keyTable;
            int i = this.capacity + this.stashSize;
            while (true) {
                int i2 = i - 1;
                if (i > 0) {
                    keyTable2[i2] = null;
                    i = i2;
                } else {
                    this.size = 0;
                    this.stashSize = 0;
                    return;
                }
            }
        }
    }

    public boolean contains(T key) {
        int hashCode = key.hashCode();
        if (!key.equals(this.keyTable[this.mask & hashCode])) {
            if (!key.equals(this.keyTable[hash2(hashCode)])) {
                if (key.equals(this.keyTable[hash3(hashCode)]) || getKeyStash(key) != null) {
                    return true;
                }
                return false;
            }
        }
        return true;
    }

    public T get(T key) {
        int hashCode = key.hashCode();
        T found = this.keyTable[this.mask & hashCode];
        if (!key.equals(found)) {
            found = this.keyTable[hash2(hashCode)];
            if (!key.equals(found)) {
                found = this.keyTable[hash3(hashCode)];
                if (!key.equals(found)) {
                    return getKeyStash(key);
                }
            }
        }
        return found;
    }

    private T getKeyStash(T key) {
        T[] keyTable2 = this.keyTable;
        int i = this.capacity;
        int n = this.stashSize + i;
        while (i < n) {
            if (key.equals(keyTable2[i])) {
                return keyTable2[i];
            }
            i++;
        }
        return null;
    }

    public T first() {
        T[] keyTable2 = this.keyTable;
        int n = this.capacity + this.stashSize;
        for (int i = 0; i < n; i++) {
            if (keyTable2[i] != null) {
                return keyTable2[i];
            }
        }
        throw new IllegalStateException("ObjectSet is empty.");
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
        T[] oldKeyTable = this.keyTable;
        this.keyTable = (Object[]) new Object[(this.stashCapacity + newSize)];
        int oldSize = this.size;
        this.size = 0;
        this.stashSize = 0;
        if (oldSize > 0) {
            for (int i = 0; i < oldEndIndex; i++) {
                T key = oldKeyTable[i];
                if (key != null) {
                    addResize(key);
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
        int n = this.capacity + this.stashSize;
        for (int i = 0; i < n; i++) {
            T[] tArr = this.keyTable;
            if (tArr[i] != null) {
                h += tArr[i].hashCode();
            }
        }
        return h;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ObjectSet)) {
            return false;
        }
        ObjectSet other = (ObjectSet) obj;
        if (other.size != this.size) {
            return false;
        }
        T[] keyTable2 = this.keyTable;
        int n = this.capacity + this.stashSize;
        for (int i = 0; i < n; i++) {
            if (keyTable2[i] != null && !other.contains(keyTable2[i])) {
                return false;
            }
        }
        return true;
    }

    public String toString() {
        return '{' + toString(", ") + '}';
    }

    public String toString(String separator) {
        int i;
        if (this.size == 0) {
            return BuildConfig.FLAVOR;
        }
        StringBuilder buffer = new StringBuilder(32);
        T[] keyTable2 = this.keyTable;
        int i2 = keyTable2.length;
        while (true) {
            i = i2 - 1;
            if (i2 > 0) {
                T key = keyTable2[i];
                if (key != null) {
                    buffer.append((Object) key);
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
                return buffer.toString();
            }
            T key2 = keyTable2[i3];
            if (key2 != null) {
                buffer.append(separator);
                buffer.append((Object) key2);
            }
            i = i3;
        }
    }

    public ObjectSetIterator<T> iterator() {
        if (Collections.allocateIterators) {
            return new ObjectSetIterator<>(this);
        }
        if (this.iterator1 == null) {
            this.iterator1 = new ObjectSetIterator(this);
            this.iterator2 = new ObjectSetIterator(this);
        }
        if (!this.iterator1.valid) {
            this.iterator1.reset();
            ObjectSetIterator<T> objectSetIterator = this.iterator1;
            objectSetIterator.valid = true;
            this.iterator2.valid = false;
            return objectSetIterator;
        }
        this.iterator2.reset();
        ObjectSetIterator<T> objectSetIterator2 = this.iterator2;
        objectSetIterator2.valid = true;
        this.iterator1.valid = false;
        return objectSetIterator2;
    }

    public static <T> ObjectSet<T> with(T... array) {
        ObjectSet set = new ObjectSet();
        set.addAll(array);
        return set;
    }

    public static class ObjectSetIterator<K> implements Iterable<K>, Iterator<K> {
        int currentIndex;
        public boolean hasNext;
        int nextIndex;
        final ObjectSet<K> set;
        boolean valid = true;

        public ObjectSetIterator(ObjectSet<K> set2) {
            this.set = set2;
            reset();
        }

        public void reset() {
            this.currentIndex = -1;
            this.nextIndex = -1;
            findNextIndex();
        }

        private void findNextIndex() {
            this.hasNext = false;
            T[] tArr = this.set.keyTable;
            int n = this.set.capacity + this.set.stashSize;
            do {
                int i = this.nextIndex + 1;
                this.nextIndex = i;
                if (i >= n) {
                    return;
                }
            } while (tArr[this.nextIndex] == null);
            this.hasNext = true;
        }

        public void remove() {
            int i = this.currentIndex;
            if (i >= 0) {
                if (i >= this.set.capacity) {
                    this.set.removeStashIndex(this.currentIndex);
                    this.nextIndex = this.currentIndex - 1;
                    findNextIndex();
                } else {
                    this.set.keyTable[this.currentIndex] = null;
                }
                this.currentIndex = -1;
                ObjectSet<K> objectSet = this.set;
                objectSet.size--;
                return;
            }
            throw new IllegalStateException("next must be called before remove.");
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
                Object[] objArr = this.set.keyTable;
                int i = this.nextIndex;
                Object obj = objArr[i];
                this.currentIndex = i;
                findNextIndex();
                return obj;
            } else {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
        }

        public ObjectSetIterator<K> iterator() {
            return this;
        }

        public Array<K> toArray(Array<K> array) {
            while (this.hasNext) {
                array.add(next());
            }
            return array;
        }

        public Array<K> toArray() {
            return toArray(new Array(true, this.set.size));
        }
    }
}
