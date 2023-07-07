package com.badlogic.gdx.utils;

import com.badlogic.gdx.math.MathUtils;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class IntFloatMap implements Iterable<Entry> {
    private static final int EMPTY = 0;
    private static final int PRIME1 = -1105259343;
    private static final int PRIME2 = -1262997959;
    private static final int PRIME3 = -825114047;
    int capacity;
    private Entries entries1;
    private Entries entries2;
    boolean hasZeroValue;
    private int hashShift;
    int[] keyTable;
    private Keys keys1;
    private Keys keys2;
    private float loadFactor;
    private int mask;
    private int pushIterations;
    public int size;
    private int stashCapacity;
    int stashSize;
    private int threshold;
    float[] valueTable;
    private Values values1;
    private Values values2;
    float zeroValue;

    public IntFloatMap() {
        this(51, 0.8f);
    }

    public IntFloatMap(int initialCapacity) {
        this(initialCapacity, 0.8f);
    }

    public IntFloatMap(int initialCapacity, float loadFactor2) {
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
                    this.valueTable = new float[this.keyTable.length];
                    return;
                }
                throw new IllegalArgumentException("loadFactor must be > 0: " + loadFactor2);
            }
            throw new IllegalArgumentException("initialCapacity is too large: " + initialCapacity2);
        }
        throw new IllegalArgumentException("initialCapacity must be >= 0: " + initialCapacity);
    }

    public IntFloatMap(IntFloatMap map) {
        this((int) Math.floor((double) (((float) map.capacity) * map.loadFactor)), map.loadFactor);
        this.stashSize = map.stashSize;
        int[] iArr = map.keyTable;
        System.arraycopy(iArr, 0, this.keyTable, 0, iArr.length);
        float[] fArr = map.valueTable;
        System.arraycopy(fArr, 0, this.valueTable, 0, fArr.length);
        this.size = map.size;
        this.zeroValue = map.zeroValue;
        this.hasZeroValue = map.hasZeroValue;
    }

    public void put(int key, float value) {
        int i = key;
        float f = value;
        if (i == 0) {
            this.zeroValue = f;
            if (!this.hasZeroValue) {
                this.hasZeroValue = true;
                this.size++;
                return;
            }
            return;
        }
        int[] keyTable2 = this.keyTable;
        int index1 = i & this.mask;
        int key1 = keyTable2[index1];
        if (i == key1) {
            this.valueTable[index1] = f;
            return;
        }
        int index2 = hash2(key);
        int key2 = keyTable2[index2];
        if (i == key2) {
            this.valueTable[index2] = f;
            return;
        }
        int index3 = hash3(key);
        int key3 = keyTable2[index3];
        if (i == key3) {
            this.valueTable[index3] = f;
            return;
        }
        int i2 = this.capacity;
        int n = this.stashSize + i2;
        while (i2 < n) {
            if (i == keyTable2[i2]) {
                this.valueTable[i2] = f;
                return;
            }
            i2++;
        }
        if (key1 == 0) {
            keyTable2[index1] = i;
            this.valueTable[index1] = f;
            int i3 = this.size;
            this.size = i3 + 1;
            if (i3 >= this.threshold) {
                resize(this.capacity << 1);
            }
        } else if (key2 == 0) {
            keyTable2[index2] = i;
            this.valueTable[index2] = f;
            int i4 = this.size;
            this.size = i4 + 1;
            if (i4 >= this.threshold) {
                resize(this.capacity << 1);
            }
        } else if (key3 == 0) {
            keyTable2[index3] = i;
            this.valueTable[index3] = f;
            int i5 = this.size;
            this.size = i5 + 1;
            if (i5 >= this.threshold) {
                resize(this.capacity << 1);
            }
        } else {
            int i6 = key2;
            push(key, value, index1, key1, index2, key2, index3, key3);
        }
    }

    public void putAll(IntFloatMap map) {
        Iterator<Entry> it = map.entries().iterator();
        while (it.hasNext()) {
            Entry entry = it.next();
            put(entry.key, entry.value);
        }
    }

    private void putResize(int key, float value) {
        float f = value;
        if (key == 0) {
            this.zeroValue = f;
            this.hasZeroValue = true;
            return;
        }
        int index1 = key & this.mask;
        int[] iArr = this.keyTable;
        int key1 = iArr[index1];
        if (key1 == 0) {
            iArr[index1] = key;
            this.valueTable[index1] = f;
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
            this.valueTable[index2] = f;
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
            this.valueTable[index3] = f;
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

    private void push(int insertKey, float insertValue, int index1, int key1, int index2, int key2, int index3, int key3) {
        float evictedValue;
        int evictedKey;
        int[] keyTable2 = this.keyTable;
        float[] valueTable2 = this.valueTable;
        int mask2 = this.mask;
        int pushIterations2 = this.pushIterations;
        int insertKey2 = insertKey;
        float insertValue2 = insertValue;
        int index12 = index1;
        int key12 = key1;
        int index22 = index2;
        int key22 = key2;
        int index32 = index3;
        int i = 0;
        int key32 = key3;
        while (true) {
            int random = MathUtils.random(2);
            if (random == 0) {
                evictedKey = key12;
                float evictedValue2 = valueTable2[index12];
                keyTable2[index12] = insertKey2;
                valueTable2[index12] = insertValue2;
                evictedValue = evictedValue2;
            } else if (random != 1) {
                evictedKey = key32;
                float evictedValue3 = valueTable2[index32];
                keyTable2[index32] = insertKey2;
                valueTable2[index32] = insertValue2;
                evictedValue = evictedValue3;
            } else {
                evictedKey = key22;
                float evictedValue4 = valueTable2[index22];
                keyTable2[index22] = insertKey2;
                valueTable2[index22] = insertValue2;
                evictedValue = evictedValue4;
            }
            index12 = evictedKey & mask2;
            key12 = keyTable2[index12];
            if (key12 == 0) {
                keyTable2[index12] = evictedKey;
                valueTable2[index12] = evictedValue;
                int i2 = mask2;
                int mask3 = this.size;
                int i3 = key32;
                this.size = mask3 + 1;
                if (mask3 >= this.threshold) {
                    resize(this.capacity << 1);
                    return;
                }
                return;
            }
            int mask4 = mask2;
            int i4 = key32;
            index22 = hash2(evictedKey);
            key22 = keyTable2[index22];
            if (key22 == 0) {
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
            index32 = hash3(evictedKey);
            key32 = keyTable2[index32];
            if (key32 == 0) {
                keyTable2[index32] = evictedKey;
                valueTable2[index32] = evictedValue;
                int i6 = this.size;
                int[] iArr = keyTable2;
                this.size = i6 + 1;
                if (i6 >= this.threshold) {
                    resize(this.capacity << 1);
                    return;
                }
                return;
            }
            int[] keyTable3 = keyTable2;
            i++;
            if (i == pushIterations2) {
                putStash(evictedKey, evictedValue);
                return;
            }
            insertKey2 = evictedKey;
            insertValue2 = evictedValue;
            mask2 = mask4;
            keyTable2 = keyTable3;
        }
    }

    private void putStash(int key, float value) {
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

    public float get(int key, float defaultValue) {
        if (key != 0) {
            int index = this.mask & key;
            if (this.keyTable[index] != key) {
                index = hash2(key);
                if (this.keyTable[index] != key) {
                    index = hash3(key);
                    if (this.keyTable[index] != key) {
                        return getStash(key, defaultValue);
                    }
                }
            }
            return this.valueTable[index];
        } else if (!this.hasZeroValue) {
            return defaultValue;
        } else {
            return this.zeroValue;
        }
    }

    private float getStash(int key, float defaultValue) {
        int[] keyTable2 = this.keyTable;
        int i = this.capacity;
        int n = this.stashSize + i;
        while (i < n) {
            if (key == keyTable2[i]) {
                return this.valueTable[i];
            }
            i++;
        }
        return defaultValue;
    }

    public float getAndIncrement(int key, float defaultValue, float increment) {
        if (key != 0) {
            int index = this.mask & key;
            if (key != this.keyTable[index]) {
                index = hash2(key);
                if (key != this.keyTable[index]) {
                    index = hash3(key);
                    if (key != this.keyTable[index]) {
                        return getAndIncrementStash(key, defaultValue, increment);
                    }
                }
            }
            float[] fArr = this.valueTable;
            float value = fArr[index];
            fArr[index] = value + increment;
            return value;
        } else if (this.hasZeroValue) {
            float value2 = this.zeroValue;
            this.zeroValue += increment;
            return value2;
        } else {
            this.hasZeroValue = true;
            this.zeroValue = defaultValue + increment;
            this.size++;
            return defaultValue;
        }
    }

    private float getAndIncrementStash(int key, float defaultValue, float increment) {
        int[] keyTable2 = this.keyTable;
        int i = this.capacity;
        int n = this.stashSize + i;
        while (i < n) {
            if (key == keyTable2[i]) {
                float[] fArr = this.valueTable;
                float value = fArr[i];
                fArr[i] = value + increment;
                return value;
            }
            i++;
        }
        put(key, defaultValue + increment);
        return defaultValue;
    }

    public float remove(int key, float defaultValue) {
        if (key != 0) {
            int index = this.mask & key;
            int[] iArr = this.keyTable;
            if (key == iArr[index]) {
                iArr[index] = 0;
                this.size--;
                return this.valueTable[index];
            }
            int index2 = hash2(key);
            int[] iArr2 = this.keyTable;
            if (key == iArr2[index2]) {
                iArr2[index2] = 0;
                this.size--;
                return this.valueTable[index2];
            }
            int index3 = hash3(key);
            int[] iArr3 = this.keyTable;
            if (key != iArr3[index3]) {
                return removeStash(key, defaultValue);
            }
            iArr3[index3] = 0;
            this.size--;
            return this.valueTable[index3];
        } else if (!this.hasZeroValue) {
            return defaultValue;
        } else {
            this.hasZeroValue = false;
            this.size--;
            return this.zeroValue;
        }
    }

    /* access modifiers changed from: package-private */
    public float removeStash(int key, float defaultValue) {
        int[] keyTable2 = this.keyTable;
        int i = this.capacity;
        int n = this.stashSize + i;
        while (i < n) {
            if (key == keyTable2[i]) {
                float oldValue = this.valueTable[i];
                removeStashIndex(i);
                this.size--;
                return oldValue;
            }
            i++;
        }
        return defaultValue;
    }

    /* access modifiers changed from: package-private */
    public void removeStashIndex(int index) {
        this.stashSize--;
        int lastIndex = this.capacity + this.stashSize;
        if (index < lastIndex) {
            int[] iArr = this.keyTable;
            iArr[index] = iArr[lastIndex];
            float[] fArr = this.valueTable;
            fArr[index] = fArr[lastIndex];
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
                    this.hasZeroValue = false;
                    this.size = 0;
                    this.stashSize = 0;
                    return;
                }
            }
        }
    }

    public boolean containsValue(float value) {
        if (this.hasZeroValue && this.zeroValue == value) {
            return true;
        }
        int[] keyTable2 = this.keyTable;
        float[] valueTable2 = this.valueTable;
        int i = this.capacity + this.stashSize;
        while (true) {
            int i2 = i - 1;
            if (i <= 0) {
                return false;
            }
            if (keyTable2[i2] != 0 && valueTable2[i2] == value) {
                return true;
            }
            i = i2;
        }
    }

    public boolean containsValue(float value, float epsilon) {
        if (this.hasZeroValue && Math.abs(this.zeroValue - value) <= epsilon) {
            return true;
        }
        float[] valueTable2 = this.valueTable;
        int i = this.capacity + this.stashSize;
        while (true) {
            int i2 = i - 1;
            if (i <= 0) {
                return false;
            }
            if (Math.abs(valueTable2[i2] - value) <= epsilon) {
                return true;
            }
            i = i2;
        }
    }

    public boolean containsKey(int key) {
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
            if (key == keyTable2[i]) {
                return true;
            }
            i++;
        }
        return false;
    }

    public int findKey(float value, int notFound) {
        if (this.hasZeroValue && this.zeroValue == value) {
            return 0;
        }
        int[] keyTable2 = this.keyTable;
        float[] valueTable2 = this.valueTable;
        int i = this.capacity + this.stashSize;
        while (true) {
            int i2 = i - 1;
            if (i <= 0) {
                return notFound;
            }
            if (keyTable2[i2] != 0 && valueTable2[i2] == value) {
                return keyTable2[i2];
            }
            i = i2;
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
        int[] oldKeyTable = this.keyTable;
        float[] oldValueTable = this.valueTable;
        int i = this.stashCapacity;
        this.keyTable = new int[(newSize + i)];
        this.valueTable = new float[(i + newSize)];
        int oldSize = this.size;
        this.size = this.hasZeroValue ? 1 : 0;
        this.stashSize = 0;
        if (oldSize > 0) {
            for (int i2 = 0; i2 < oldEndIndex; i2++) {
                int key = oldKeyTable[i2];
                if (key != 0) {
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
        if (this.hasZeroValue) {
            h = 0 + Float.floatToIntBits(this.zeroValue);
        }
        int[] keyTable2 = this.keyTable;
        float[] valueTable2 = this.valueTable;
        int n = this.capacity + this.stashSize;
        for (int i = 0; i < n; i++) {
            int key = keyTable2[i];
            if (key != 0) {
                h = h + (key * 31) + Float.floatToIntBits(valueTable2[i]);
            }
        }
        return h;
    }

    public boolean equals(Object obj) {
        boolean z;
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof IntFloatMap)) {
            return false;
        }
        IntFloatMap other = (IntFloatMap) obj;
        if (other.size != this.size || other.hasZeroValue != (z = this.hasZeroValue)) {
            return false;
        }
        if (z && other.zeroValue != this.zeroValue) {
            return false;
        }
        int[] keyTable2 = this.keyTable;
        float[] valueTable2 = this.valueTable;
        int n = this.capacity + this.stashSize;
        for (int i = 0; i < n; i++) {
            int key = keyTable2[i];
            if (key != 0) {
                float otherValue = other.get(key, 0.0f);
                if ((otherValue == 0.0f && !other.containsKey(key)) || otherValue != valueTable2[i]) {
                    return false;
                }
            }
        }
        return true;
    }

    /* JADX WARNING: Removed duplicated region for block: B:15:0x0043  */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0059  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String toString() {
        /*
            r7 = this;
            int r0 = r7.size
            if (r0 != 0) goto L_0x0007
            java.lang.String r0 = "{}"
            return r0
        L_0x0007:
            com.badlogic.gdx.utils.StringBuilder r0 = new com.badlogic.gdx.utils.StringBuilder
            r1 = 32
            r0.<init>((int) r1)
            r1 = 123(0x7b, float:1.72E-43)
            r0.append((char) r1)
            int[] r1 = r7.keyTable
            float[] r2 = r7.valueTable
            int r3 = r1.length
            boolean r4 = r7.hasZeroValue
            r5 = 61
            if (r4 == 0) goto L_0x0029
            java.lang.String r4 = "0="
            r0.append((java.lang.String) r4)
            float r4 = r7.zeroValue
            r0.append((float) r4)
            goto L_0x003f
        L_0x0029:
            int r4 = r3 + -1
            if (r3 <= 0) goto L_0x003e
            r3 = r1[r4]
            if (r3 != 0) goto L_0x0033
            r3 = r4
            goto L_0x0029
        L_0x0033:
            r0.append((int) r3)
            r0.append((char) r5)
            r6 = r2[r4]
            r0.append((float) r6)
        L_0x003e:
            r3 = r4
        L_0x003f:
            int r4 = r3 + -1
            if (r3 <= 0) goto L_0x0059
            r3 = r1[r4]
            if (r3 != 0) goto L_0x0048
            goto L_0x003e
        L_0x0048:
            java.lang.String r6 = ", "
            r0.append((java.lang.String) r6)
            r0.append((int) r3)
            r0.append((char) r5)
            r6 = r2[r4]
            r0.append((float) r6)
            goto L_0x003e
        L_0x0059:
            r3 = 125(0x7d, float:1.75E-43)
            r0.append((char) r3)
            java.lang.String r3 = r0.toString()
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.utils.IntFloatMap.toString():java.lang.String");
    }

    public Iterator<Entry> iterator() {
        return entries();
    }

    public Entries entries() {
        if (Collections.allocateIterators) {
            return new Entries(this);
        }
        if (this.entries1 == null) {
            this.entries1 = new Entries(this);
            this.entries2 = new Entries(this);
        }
        if (!this.entries1.valid) {
            this.entries1.reset();
            Entries entries = this.entries1;
            entries.valid = true;
            this.entries2.valid = false;
            return entries;
        }
        this.entries2.reset();
        Entries entries3 = this.entries2;
        entries3.valid = true;
        this.entries1.valid = false;
        return entries3;
    }

    public Values values() {
        if (Collections.allocateIterators) {
            return new Values(this);
        }
        if (this.values1 == null) {
            this.values1 = new Values(this);
            this.values2 = new Values(this);
        }
        if (!this.values1.valid) {
            this.values1.reset();
            Values values = this.values1;
            values.valid = true;
            this.values2.valid = false;
            return values;
        }
        this.values2.reset();
        Values values3 = this.values2;
        values3.valid = true;
        this.values1.valid = false;
        return values3;
    }

    public Keys keys() {
        if (Collections.allocateIterators) {
            return new Keys(this);
        }
        if (this.keys1 == null) {
            this.keys1 = new Keys(this);
            this.keys2 = new Keys(this);
        }
        if (!this.keys1.valid) {
            this.keys1.reset();
            Keys keys = this.keys1;
            keys.valid = true;
            this.keys2.valid = false;
            return keys;
        }
        this.keys2.reset();
        Keys keys3 = this.keys2;
        keys3.valid = true;
        this.keys1.valid = false;
        return keys3;
    }

    public static class Entry {
        public int key;
        public float value;

        public String toString() {
            return this.key + "=" + this.value;
        }
    }

    private static class MapIterator {
        static final int INDEX_ILLEGAL = -2;
        static final int INDEX_ZERO = -1;
        int currentIndex;
        public boolean hasNext;
        final IntFloatMap map;
        int nextIndex;
        boolean valid = true;

        public MapIterator(IntFloatMap map2) {
            this.map = map2;
            reset();
        }

        public void reset() {
            this.currentIndex = INDEX_ILLEGAL;
            this.nextIndex = -1;
            if (this.map.hasZeroValue) {
                this.hasNext = true;
            } else {
                findNextIndex();
            }
        }

        /* access modifiers changed from: package-private */
        public void findNextIndex() {
            this.hasNext = false;
            int[] keyTable = this.map.keyTable;
            int n = this.map.capacity + this.map.stashSize;
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
            if (this.currentIndex != -1 || !this.map.hasZeroValue) {
                int i = this.currentIndex;
                if (i < 0) {
                    throw new IllegalStateException("next must be called before remove.");
                } else if (i >= this.map.capacity) {
                    this.map.removeStashIndex(this.currentIndex);
                    this.nextIndex = this.currentIndex - 1;
                    findNextIndex();
                } else {
                    this.map.keyTable[this.currentIndex] = 0;
                }
            } else {
                this.map.hasZeroValue = false;
            }
            this.currentIndex = INDEX_ILLEGAL;
            IntFloatMap intFloatMap = this.map;
            intFloatMap.size--;
        }
    }

    public static class Entries extends MapIterator implements Iterable<Entry>, Iterator<Entry> {
        private Entry entry = new Entry();

        public /* bridge */ /* synthetic */ void reset() {
            super.reset();
        }

        public Entries(IntFloatMap map) {
            super(map);
        }

        public Entry next() {
            if (!this.hasNext) {
                throw new NoSuchElementException();
            } else if (this.valid) {
                int[] keyTable = this.map.keyTable;
                if (this.nextIndex == -1) {
                    Entry entry2 = this.entry;
                    entry2.key = 0;
                    entry2.value = this.map.zeroValue;
                } else {
                    this.entry.key = keyTable[this.nextIndex];
                    this.entry.value = this.map.valueTable[this.nextIndex];
                }
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

        public Iterator<Entry> iterator() {
            return this;
        }

        public void remove() {
            super.remove();
        }
    }

    public static class Values extends MapIterator {
        public /* bridge */ /* synthetic */ void remove() {
            super.remove();
        }

        public /* bridge */ /* synthetic */ void reset() {
            super.reset();
        }

        public Values(IntFloatMap map) {
            super(map);
        }

        public boolean hasNext() {
            if (this.valid) {
                return this.hasNext;
            }
            throw new GdxRuntimeException("#iterator() cannot be used nested.");
        }

        public float next() {
            float value;
            if (!this.hasNext) {
                throw new NoSuchElementException();
            } else if (this.valid) {
                if (this.nextIndex == -1) {
                    value = this.map.zeroValue;
                } else {
                    value = this.map.valueTable[this.nextIndex];
                }
                this.currentIndex = this.nextIndex;
                findNextIndex();
                return value;
            } else {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
        }

        public FloatArray toArray() {
            FloatArray array = new FloatArray(true, this.map.size);
            while (this.hasNext) {
                array.add(next());
            }
            return array;
        }
    }

    public static class Keys extends MapIterator {
        public /* bridge */ /* synthetic */ void remove() {
            super.remove();
        }

        public /* bridge */ /* synthetic */ void reset() {
            super.reset();
        }

        public Keys(IntFloatMap map) {
            super(map);
        }

        public boolean hasNext() {
            if (this.valid) {
                return this.hasNext;
            }
            throw new GdxRuntimeException("#iterator() cannot be used nested.");
        }

        public int next() {
            if (!this.hasNext) {
                throw new NoSuchElementException();
            } else if (this.valid) {
                int key = this.nextIndex == -1 ? 0 : this.map.keyTable[this.nextIndex];
                this.currentIndex = this.nextIndex;
                findNextIndex();
                return key;
            } else {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
        }

        public IntArray toArray() {
            IntArray array = new IntArray(true, this.map.size);
            while (this.hasNext) {
                array.add(next());
            }
            return array;
        }
    }
}
