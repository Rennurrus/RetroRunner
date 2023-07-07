package com.badlogic.gdx.utils;

import com.badlogic.gdx.math.MathUtils;
import java.util.NoSuchElementException;

public class IntSet {
    private static final int EMPTY = 0;
    private static final int PRIME1 = -1105259343;
    private static final int PRIME2 = -1262997959;
    private static final int PRIME3 = -825114047;
    int capacity;
    boolean hasZeroValue;
    private int hashShift;
    private IntSetIterator iterator1;
    private IntSetIterator iterator2;
    int[] keyTable;
    private float loadFactor;
    private int mask;
    private int pushIterations;
    public int size;
    private int stashCapacity;
    int stashSize;
    private int threshold;

    public IntSet() {
        this(51, 0.8f);
    }

    public IntSet(int initialCapacity) {
        this(initialCapacity, 0.8f);
    }

    public IntSet(int initialCapacity, float loadFactor2) {
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
                    this.keyTable = new int[(this.capacity + this.stashCapacity)];
                    return;
                }
                throw new IllegalArgumentException("loadFactor must be > 0: " + loadFactor2);
            }
            throw new IllegalArgumentException("initialCapacity is too large: " + initialCapacity2);
        }
        throw new IllegalArgumentException("initialCapacity must be >= 0: " + initialCapacity);
    }

    public IntSet(IntSet set) {
        this((int) Math.floor((double) (((float) set.capacity) * set.loadFactor)), set.loadFactor);
        this.stashSize = set.stashSize;
        int[] iArr = set.keyTable;
        System.arraycopy(iArr, 0, this.keyTable, 0, iArr.length);
        this.size = set.size;
        this.hasZeroValue = set.hasZeroValue;
    }

    public boolean add(int key) {
        int index2;
        int key2;
        int index3;
        int key3;
        int i = key;
        if (i != 0) {
            int[] keyTable2 = this.keyTable;
            int index1 = i & this.mask;
            int key1 = keyTable2[index1];
            if (key1 == i || (key2 = keyTable2[index2]) == i || (key3 = keyTable2[index3]) == i) {
                return false;
            }
            int i2 = this.capacity;
            int n = this.stashSize + i2;
            while (i2 < n) {
                if (keyTable2[i2] == i) {
                    return false;
                }
                i2++;
            }
            if (key1 == 0) {
                keyTable2[index1] = i;
                int i3 = this.size;
                this.size = i3 + 1;
                if (i3 >= this.threshold) {
                    resize(this.capacity << 1);
                }
                return true;
            } else if (key2 == 0) {
                keyTable2[(index2 = hash2(key))] = i;
                int i4 = this.size;
                this.size = i4 + 1;
                if (i4 >= this.threshold) {
                    resize(this.capacity << 1);
                }
                return true;
            } else if (key3 == 0) {
                keyTable2[(index3 = hash3(key))] = i;
                int i5 = this.size;
                this.size = i5 + 1;
                if (i5 >= this.threshold) {
                    resize(this.capacity << 1);
                }
                return true;
            } else {
                int i6 = key3;
                push(key, index1, key1, index2, key2, index3, key3);
                return true;
            }
        } else if (this.hasZeroValue) {
            return false;
        } else {
            this.hasZeroValue = true;
            this.size++;
            return true;
        }
    }

    public void addAll(IntArray array) {
        addAll(array.items, 0, array.size);
    }

    public void addAll(IntArray array, int offset, int length) {
        if (offset + length <= array.size) {
            addAll(array.items, offset, length);
            return;
        }
        throw new IllegalArgumentException("offset + length must be <= size: " + offset + " + " + length + " <= " + array.size);
    }

    public void addAll(int... array) {
        addAll(array, 0, array.length);
    }

    public void addAll(int[] array, int offset, int length) {
        ensureCapacity(length);
        int i = offset;
        int n = i + length;
        while (i < n) {
            add(array[i]);
            i++;
        }
    }

    public void addAll(IntSet set) {
        ensureCapacity(set.size);
        IntSetIterator iterator = set.iterator();
        while (iterator.hasNext) {
            add(iterator.next());
        }
    }

    private void addResize(int key) {
        if (key == 0) {
            this.hasZeroValue = true;
            return;
        }
        int index1 = key & this.mask;
        int[] iArr = this.keyTable;
        int key1 = iArr[index1];
        if (key1 == 0) {
            iArr[index1] = key;
            int i = this.size;
            this.size = i + 1;
            if (i >= this.threshold) {
                resize(this.capacity << 1);
                return;
            }
            return;
        }
        int index2 = hash2(key);
        int[] iArr2 = this.keyTable;
        int key2 = iArr2[index2];
        if (key2 == 0) {
            iArr2[index2] = key;
            int i2 = this.size;
            this.size = i2 + 1;
            if (i2 >= this.threshold) {
                resize(this.capacity << 1);
                return;
            }
            return;
        }
        int index3 = hash3(key);
        int[] iArr3 = this.keyTable;
        int key3 = iArr3[index3];
        if (key3 == 0) {
            iArr3[index3] = key;
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

    private void push(int insertKey, int index1, int key1, int index2, int key2, int index3, int key3) {
        int evictedKey;
        int[] keyTable2 = this.keyTable;
        int mask2 = this.mask;
        int i = 0;
        int pushIterations2 = this.pushIterations;
        while (true) {
            int random = MathUtils.random(2);
            if (random == 0) {
                evictedKey = key1;
                keyTable2[index1] = insertKey;
            } else if (random != 1) {
                evictedKey = key3;
                keyTable2[index3] = insertKey;
            } else {
                evictedKey = key2;
                keyTable2[index2] = insertKey;
            }
            index1 = evictedKey & mask2;
            key1 = keyTable2[index1];
            if (key1 == 0) {
                keyTable2[index1] = evictedKey;
                int i2 = this.size;
                this.size = i2 + 1;
                if (i2 >= this.threshold) {
                    resize(this.capacity << 1);
                    return;
                }
                return;
            }
            index2 = hash2(evictedKey);
            key2 = keyTable2[index2];
            if (key2 == 0) {
                keyTable2[index2] = evictedKey;
                int i3 = this.size;
                this.size = i3 + 1;
                if (i3 >= this.threshold) {
                    resize(this.capacity << 1);
                    return;
                }
                return;
            }
            index3 = hash3(evictedKey);
            key3 = keyTable2[index3];
            if (key3 == 0) {
                keyTable2[index3] = evictedKey;
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
            insertKey = evictedKey;
        }
    }

    private void addStash(int key) {
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

    public boolean remove(int key) {
        if (key != 0) {
            int index = this.mask & key;
            int[] iArr = this.keyTable;
            if (iArr[index] == key) {
                iArr[index] = 0;
                this.size--;
                return true;
            }
            int index2 = hash2(key);
            int[] iArr2 = this.keyTable;
            if (iArr2[index2] == key) {
                iArr2[index2] = 0;
                this.size--;
                return true;
            }
            int index3 = hash3(key);
            int[] iArr3 = this.keyTable;
            if (iArr3[index3] != key) {
                return removeStash(key);
            }
            iArr3[index3] = 0;
            this.size--;
            return true;
        } else if (!this.hasZeroValue) {
            return false;
        } else {
            this.hasZeroValue = false;
            this.size--;
            return true;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean removeStash(int key) {
        int[] keyTable2 = this.keyTable;
        int i = this.capacity;
        int n = this.stashSize + i;
        while (i < n) {
            if (keyTable2[i] == key) {
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
            int[] iArr = this.keyTable;
            iArr[index] = iArr[lastIndex];
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
        this.hasZeroValue = false;
        this.size = 0;
        resize(maximumCapacity);
    }

    public void clear() {
        if (this.size != 0) {
            int[] keyTable2 = this.keyTable;
            int i = this.capacity + this.stashSize;
            while (true) {
                int i2 = i - 1;
                if (i > 0) {
                    keyTable2[i2] = 0;
                    i = i2;
                } else {
                    this.size = 0;
                    this.stashSize = 0;
                    this.hasZeroValue = false;
                    return;
                }
            }
        }
    }

    public boolean contains(int key) {
        if (key == 0) {
            return this.hasZeroValue;
        }
        if (this.keyTable[this.mask & key] == key) {
            return true;
        }
        if (this.keyTable[hash2(key)] == key) {
            return true;
        }
        if (this.keyTable[hash3(key)] != key) {
            return containsKeyStash(key);
        }
        return true;
    }

    private boolean containsKeyStash(int key) {
        int[] keyTable2 = this.keyTable;
        int i = this.capacity;
        int n = this.stashSize + i;
        while (i < n) {
            if (keyTable2[i] == key) {
                return true;
            }
            i++;
        }
        return false;
    }

    public int first() {
        if (this.hasZeroValue) {
            return 0;
        }
        int[] keyTable2 = this.keyTable;
        int n = this.capacity + this.stashSize;
        for (int i = 0; i < n; i++) {
            if (keyTable2[i] != 0) {
                return keyTable2[i];
            }
        }
        throw new IllegalStateException("IntSet is empty.");
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
        int[] oldKeyTable = this.keyTable;
        this.keyTable = new int[(this.stashCapacity + newSize)];
        int oldSize = this.size;
        this.size = this.hasZeroValue ? 1 : 0;
        this.stashSize = 0;
        if (oldSize > 0) {
            for (int i = 0; i < oldEndIndex; i++) {
                int key = oldKeyTable[i];
                if (key != 0) {
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
            int[] iArr = this.keyTable;
            if (iArr[i] != 0) {
                h += iArr[i];
            }
        }
        return h;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof IntSet)) {
            return false;
        }
        IntSet other = (IntSet) obj;
        if (other.size != this.size || other.hasZeroValue != this.hasZeroValue) {
            return false;
        }
        int[] keyTable2 = this.keyTable;
        int n = this.capacity + this.stashSize;
        for (int i = 0; i < n; i++) {
            if (keyTable2[i] != 0 && !other.contains(keyTable2[i])) {
                return false;
            }
        }
        return true;
    }

    /* JADX WARNING: Removed duplicated region for block: B:15:0x0032  */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0040  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String toString() {
        /*
            r5 = this;
            int r0 = r5.size
            if (r0 != 0) goto L_0x0007
            java.lang.String r0 = "[]"
            return r0
        L_0x0007:
            com.badlogic.gdx.utils.StringBuilder r0 = new com.badlogic.gdx.utils.StringBuilder
            r1 = 32
            r0.<init>((int) r1)
            r1 = 91
            r0.append((char) r1)
            int[] r1 = r5.keyTable
            int r2 = r1.length
            boolean r3 = r5.hasZeroValue
            if (r3 == 0) goto L_0x0020
            java.lang.String r3 = "0"
            r0.append((java.lang.String) r3)
            goto L_0x002e
        L_0x0020:
            int r3 = r2 + -1
            if (r2 <= 0) goto L_0x002d
            r2 = r1[r3]
            if (r2 != 0) goto L_0x002a
            r2 = r3
            goto L_0x0020
        L_0x002a:
            r0.append((int) r2)
        L_0x002d:
            r2 = r3
        L_0x002e:
            int r3 = r2 + -1
            if (r2 <= 0) goto L_0x0040
            r2 = r1[r3]
            if (r2 != 0) goto L_0x0037
            goto L_0x002d
        L_0x0037:
            java.lang.String r4 = ", "
            r0.append((java.lang.String) r4)
            r0.append((int) r2)
            goto L_0x002d
        L_0x0040:
            r2 = 93
            r0.append((char) r2)
            java.lang.String r2 = r0.toString()
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.utils.IntSet.toString():java.lang.String");
    }

    public IntSetIterator iterator() {
        if (Collections.allocateIterators) {
            return new IntSetIterator(this);
        }
        if (this.iterator1 == null) {
            this.iterator1 = new IntSetIterator(this);
            this.iterator2 = new IntSetIterator(this);
        }
        if (!this.iterator1.valid) {
            this.iterator1.reset();
            IntSetIterator intSetIterator = this.iterator1;
            intSetIterator.valid = true;
            this.iterator2.valid = false;
            return intSetIterator;
        }
        this.iterator2.reset();
        IntSetIterator intSetIterator2 = this.iterator2;
        intSetIterator2.valid = true;
        this.iterator1.valid = false;
        return intSetIterator2;
    }

    public static IntSet with(int... array) {
        IntSet set = new IntSet();
        set.addAll(array);
        return set;
    }

    public static class IntSetIterator {
        static final int INDEX_ILLEGAL = -2;
        static final int INDEX_ZERO = -1;
        int currentIndex;
        public boolean hasNext;
        int nextIndex;
        final IntSet set;
        boolean valid = true;

        public IntSetIterator(IntSet set2) {
            this.set = set2;
            reset();
        }

        public void reset() {
            this.currentIndex = INDEX_ILLEGAL;
            this.nextIndex = -1;
            if (this.set.hasZeroValue) {
                this.hasNext = true;
            } else {
                findNextIndex();
            }
        }

        /* access modifiers changed from: package-private */
        public void findNextIndex() {
            this.hasNext = false;
            int[] keyTable = this.set.keyTable;
            int n = this.set.capacity + this.set.stashSize;
            do {
                int i = this.nextIndex + 1;
                this.nextIndex = i;
                if (i >= n) {
                    return;
                }
            } while (keyTable[this.nextIndex] == 0);
            this.hasNext = true;
        }

        public void remove() {
            if (this.currentIndex != -1 || !this.set.hasZeroValue) {
                int i = this.currentIndex;
                if (i < 0) {
                    throw new IllegalStateException("next must be called before remove.");
                } else if (i >= this.set.capacity) {
                    this.set.removeStashIndex(this.currentIndex);
                    this.nextIndex = this.currentIndex - 1;
                    findNextIndex();
                } else {
                    this.set.keyTable[this.currentIndex] = 0;
                }
            } else {
                this.set.hasZeroValue = false;
            }
            this.currentIndex = INDEX_ILLEGAL;
            IntSet intSet = this.set;
            intSet.size--;
        }

        public int next() {
            if (!this.hasNext) {
                throw new NoSuchElementException();
            } else if (this.valid) {
                int key = this.nextIndex == -1 ? 0 : this.set.keyTable[this.nextIndex];
                this.currentIndex = this.nextIndex;
                findNextIndex();
                return key;
            } else {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
        }

        public IntArray toArray() {
            IntArray array = new IntArray(true, this.set.size);
            while (this.hasNext) {
                array.add(next());
            }
            return array;
        }
    }
}
