package com.badlogic.gdx.utils;

import com.badlogic.gdx.math.MathUtils;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class IntMap<V> implements Iterable<Entry<V>> {
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
    V[] valueTable;
    private Values values1;
    private Values values2;
    V zeroValue;

    public IntMap() {
        this(51, 0.8f);
    }

    public IntMap(int initialCapacity) {
        this(initialCapacity, 0.8f);
    }

    public IntMap(int initialCapacity, float loadFactor2) {
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
                    this.valueTable = (Object[]) new Object[this.keyTable.length];
                    return;
                }
                throw new IllegalArgumentException("loadFactor must be > 0: " + loadFactor2);
            }
            throw new IllegalArgumentException("initialCapacity is too large: " + initialCapacity2);
        }
        throw new IllegalArgumentException("initialCapacity must be >= 0: " + initialCapacity);
    }

    public IntMap(IntMap<? extends V> map) {
        this((int) Math.floor((double) (((float) map.capacity) * map.loadFactor)), map.loadFactor);
        this.stashSize = map.stashSize;
        int[] iArr = map.keyTable;
        System.arraycopy(iArr, 0, this.keyTable, 0, iArr.length);
        V[] vArr = map.valueTable;
        System.arraycopy(vArr, 0, this.valueTable, 0, vArr.length);
        this.size = map.size;
        this.zeroValue = map.zeroValue;
        this.hasZeroValue = map.hasZeroValue;
    }

    public V put(int key, V value) {
        int i = key;
        V v = value;
        if (i == 0) {
            V oldValue = this.zeroValue;
            this.zeroValue = v;
            if (!this.hasZeroValue) {
                this.hasZeroValue = true;
                this.size++;
            }
            return oldValue;
        }
        int[] keyTable2 = this.keyTable;
        int index1 = i & this.mask;
        int key1 = keyTable2[index1];
        if (key1 == i) {
            V[] vArr = this.valueTable;
            V oldValue2 = vArr[index1];
            vArr[index1] = v;
            return oldValue2;
        }
        int index2 = hash2(key);
        int key2 = keyTable2[index2];
        if (key2 == i) {
            V[] vArr2 = this.valueTable;
            V oldValue3 = vArr2[index2];
            vArr2[index2] = v;
            return oldValue3;
        }
        int index3 = hash3(key);
        int key3 = keyTable2[index3];
        if (key3 == i) {
            V[] vArr3 = this.valueTable;
            V oldValue4 = vArr3[index3];
            vArr3[index3] = v;
            return oldValue4;
        }
        int i2 = this.capacity;
        int n = this.stashSize + i2;
        while (i2 < n) {
            if (keyTable2[i2] == i) {
                V[] vArr4 = this.valueTable;
                V oldValue5 = vArr4[i2];
                vArr4[i2] = v;
                return oldValue5;
            }
            i2++;
        }
        if (key1 == 0) {
            keyTable2[index1] = i;
            this.valueTable[index1] = v;
            int i3 = this.size;
            this.size = i3 + 1;
            if (i3 >= this.threshold) {
                resize(this.capacity << 1);
            }
            return null;
        } else if (key2 == 0) {
            keyTable2[index2] = i;
            this.valueTable[index2] = v;
            int i4 = this.size;
            this.size = i4 + 1;
            if (i4 >= this.threshold) {
                resize(this.capacity << 1);
            }
            return null;
        } else if (key3 == 0) {
            keyTable2[index3] = i;
            this.valueTable[index3] = v;
            int i5 = this.size;
            this.size = i5 + 1;
            if (i5 >= this.threshold) {
                resize(this.capacity << 1);
            }
            return null;
        } else {
            int i6 = key2;
            push(key, value, index1, key1, index2, key2, index3, key3);
            return null;
        }
    }

    public void putAll(IntMap<? extends V> map) {
        Iterator<Entry<? extends V>> it = map.entries().iterator();
        while (it.hasNext()) {
            Entry<? extends V> entry = it.next();
            put(entry.key, entry.value);
        }
    }

    private void putResize(int key, V value) {
        V v = value;
        if (key == 0) {
            this.zeroValue = v;
            this.hasZeroValue = true;
            return;
        }
        int index1 = key & this.mask;
        int[] iArr = this.keyTable;
        int key1 = iArr[index1];
        if (key1 == 0) {
            iArr[index1] = key;
            this.valueTable[index1] = v;
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
            this.valueTable[index2] = v;
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
            this.valueTable[index3] = v;
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

    private void push(int insertKey, V insertValue, int index1, int key1, int index2, int key2, int index3, int key3) {
        V evictedValue;
        int evictedKey;
        int[] keyTable2 = this.keyTable;
        V[] valueTable2 = this.valueTable;
        int mask2 = this.mask;
        int pushIterations2 = this.pushIterations;
        int insertKey2 = insertKey;
        V insertValue2 = insertValue;
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

    private void putStash(int key, V value) {
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

    public V get(int key) {
        if (key != 0) {
            int index = this.mask & key;
            if (this.keyTable[index] != key) {
                index = hash2(key);
                if (this.keyTable[index] != key) {
                    index = hash3(key);
                    if (this.keyTable[index] != key) {
                        return getStash(key, (Object) null);
                    }
                }
            }
            return this.valueTable[index];
        } else if (!this.hasZeroValue) {
            return null;
        } else {
            return this.zeroValue;
        }
    }

    public V get(int key, V defaultValue) {
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

    private V getStash(int key, V defaultValue) {
        int[] keyTable2 = this.keyTable;
        int i = this.capacity;
        int n = this.stashSize + i;
        while (i < n) {
            if (keyTable2[i] == key) {
                return this.valueTable[i];
            }
            i++;
        }
        return defaultValue;
    }

    public V remove(int key) {
        if (key != 0) {
            int index = this.mask & key;
            int[] iArr = this.keyTable;
            if (iArr[index] == key) {
                iArr[index] = 0;
                V[] vArr = this.valueTable;
                V oldValue = vArr[index];
                vArr[index] = null;
                this.size--;
                return oldValue;
            }
            int index2 = hash2(key);
            int[] iArr2 = this.keyTable;
            if (iArr2[index2] == key) {
                iArr2[index2] = 0;
                V[] vArr2 = this.valueTable;
                V oldValue2 = vArr2[index2];
                vArr2[index2] = null;
                this.size--;
                return oldValue2;
            }
            int index3 = hash3(key);
            int[] iArr3 = this.keyTable;
            if (iArr3[index3] != key) {
                return removeStash(key);
            }
            iArr3[index3] = 0;
            V[] vArr3 = this.valueTable;
            V oldValue3 = vArr3[index3];
            vArr3[index3] = null;
            this.size--;
            return oldValue3;
        } else if (!this.hasZeroValue) {
            return null;
        } else {
            V oldValue4 = this.zeroValue;
            this.zeroValue = null;
            this.hasZeroValue = false;
            this.size--;
            return oldValue4;
        }
    }

    /* access modifiers changed from: package-private */
    public V removeStash(int key) {
        int[] keyTable2 = this.keyTable;
        int i = this.capacity;
        int n = this.stashSize + i;
        while (i < n) {
            if (keyTable2[i] == key) {
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
            int[] iArr = this.keyTable;
            iArr[index] = iArr[lastIndex];
            V[] vArr = this.valueTable;
            vArr[index] = vArr[lastIndex];
            vArr[lastIndex] = null;
            return;
        }
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
        this.zeroValue = null;
        this.hasZeroValue = false;
        this.size = 0;
        resize(maximumCapacity);
    }

    public void clear() {
        if (this.size != 0) {
            int[] keyTable2 = this.keyTable;
            V[] valueTable2 = this.valueTable;
            int i = this.capacity + this.stashSize;
            while (true) {
                int i2 = i - 1;
                if (i > 0) {
                    keyTable2[i2] = 0;
                    valueTable2[i2] = null;
                    i = i2;
                } else {
                    this.size = 0;
                    this.stashSize = 0;
                    this.zeroValue = null;
                    this.hasZeroValue = false;
                    return;
                }
            }
        }
    }

    public boolean containsValue(Object value, boolean identity) {
        V[] valueTable2 = this.valueTable;
        if (value == null) {
            if (this.hasZeroValue && this.zeroValue == null) {
                return true;
            }
            int[] keyTable2 = this.keyTable;
            int i = this.capacity + this.stashSize;
            while (true) {
                int i2 = i - 1;
                if (i <= 0) {
                    return false;
                }
                if (keyTable2[i2] != 0 && valueTable2[i2] == null) {
                    return true;
                }
                i = i2;
            }
        } else if (identity) {
            if (value == this.zeroValue) {
                return true;
            }
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
        } else if (this.hasZeroValue && value.equals(this.zeroValue)) {
            return true;
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
            if (keyTable2[i] == key) {
                return true;
            }
            i++;
        }
        return false;
    }

    public int findKey(Object value, boolean identity, int notFound) {
        V[] valueTable2 = this.valueTable;
        if (value == null) {
            if (!this.hasZeroValue || this.zeroValue != null) {
                int[] keyTable2 = this.keyTable;
                int i = this.capacity + this.stashSize;
                while (true) {
                    int i2 = i - 1;
                    if (i <= 0) {
                        break;
                    } else if (keyTable2[i2] != 0 && valueTable2[i2] == null) {
                        return keyTable2[i2];
                    } else {
                        i = i2;
                    }
                }
            } else {
                return 0;
            }
        } else if (identity) {
            if (value != this.zeroValue) {
                int i3 = this.capacity + this.stashSize;
                while (true) {
                    int i4 = i3 - 1;
                    if (i3 <= 0) {
                        break;
                    } else if (valueTable2[i4] == value) {
                        return this.keyTable[i4];
                    } else {
                        i3 = i4;
                    }
                }
            } else {
                return 0;
            }
        } else if (!this.hasZeroValue || !value.equals(this.zeroValue)) {
            int i5 = this.capacity + this.stashSize;
            while (true) {
                int i6 = i5 - 1;
                if (i5 <= 0) {
                    break;
                } else if (value.equals(valueTable2[i6])) {
                    return this.keyTable[i6];
                } else {
                    i5 = i6;
                }
            }
        } else {
            return 0;
        }
        return notFound;
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
        V[] oldValueTable = this.valueTable;
        int i = this.stashCapacity;
        this.keyTable = new int[(newSize + i)];
        this.valueTable = (Object[]) new Object[(i + newSize)];
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
        V v;
        int h = 0;
        if (this.hasZeroValue && (v = this.zeroValue) != null) {
            h = 0 + v.hashCode();
        }
        int[] keyTable2 = this.keyTable;
        V[] valueTable2 = this.valueTable;
        int n = this.capacity + this.stashSize;
        for (int i = 0; i < n; i++) {
            int key = keyTable2[i];
            if (key != 0) {
                h += key * 31;
                V value = valueTable2[i];
                if (value != null) {
                    h += value.hashCode();
                }
            }
        }
        return h;
    }

    public boolean equals(Object obj) {
        boolean z;
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof IntMap)) {
            return false;
        }
        IntMap other = (IntMap) obj;
        if (other.size != this.size || other.hasZeroValue != (z = this.hasZeroValue)) {
            return false;
        }
        if (z) {
            V v = other.zeroValue;
            if (v == null) {
                if (this.zeroValue != null) {
                    return false;
                }
            } else if (!v.equals(this.zeroValue)) {
                return false;
            }
        }
        int[] keyTable2 = this.keyTable;
        V[] valueTable2 = this.valueTable;
        int n = this.capacity + this.stashSize;
        for (int i = 0; i < n; i++) {
            int key = keyTable2[i];
            if (key != 0) {
                V value = valueTable2[i];
                if (value == null) {
                    if (other.get(key, ObjectMap.dummy) != null) {
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
        boolean z;
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof IntMap)) {
            return false;
        }
        IntMap other = (IntMap) obj;
        if (other.size != this.size || other.hasZeroValue != (z = this.hasZeroValue)) {
            return false;
        }
        if (z && this.zeroValue != other.zeroValue) {
            return false;
        }
        int[] keyTable2 = this.keyTable;
        V[] valueTable2 = this.valueTable;
        int n = this.capacity + this.stashSize;
        for (int i = 0; i < n; i++) {
            int key = keyTable2[i];
            if (key != 0 && valueTable2[i] != other.get(key, ObjectMap.dummy)) {
                return false;
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
            java.lang.String r0 = "[]"
            return r0
        L_0x0007:
            com.badlogic.gdx.utils.StringBuilder r0 = new com.badlogic.gdx.utils.StringBuilder
            r1 = 32
            r0.<init>((int) r1)
            r1 = 91
            r0.append((char) r1)
            int[] r1 = r7.keyTable
            V[] r2 = r7.valueTable
            int r3 = r1.length
            boolean r4 = r7.hasZeroValue
            r5 = 61
            if (r4 == 0) goto L_0x0029
            java.lang.String r4 = "0="
            r0.append((java.lang.String) r4)
            V r4 = r7.zeroValue
            r0.append((java.lang.Object) r4)
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
            r0.append((java.lang.Object) r6)
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
            r0.append((java.lang.Object) r6)
            goto L_0x003e
        L_0x0059:
            r3 = 93
            r0.append((char) r3)
            java.lang.String r3 = r0.toString()
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.utils.IntMap.toString():java.lang.String");
    }

    public Iterator<Entry<V>> iterator() {
        return entries();
    }

    public Entries<V> entries() {
        if (Collections.allocateIterators) {
            return new Entries<>(this);
        }
        if (this.entries1 == null) {
            this.entries1 = new Entries(this);
            this.entries2 = new Entries(this);
        }
        if (!this.entries1.valid) {
            this.entries1.reset();
            Entries<V> entries = this.entries1;
            entries.valid = true;
            this.entries2.valid = false;
            return entries;
        }
        this.entries2.reset();
        Entries<V> entries3 = this.entries2;
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

    public static class Entry<V> {
        public int key;
        public V value;

        public String toString() {
            return this.key + "=" + this.value;
        }
    }

    private static class MapIterator<V> {
        static final int INDEX_ILLEGAL = -2;
        static final int INDEX_ZERO = -1;
        int currentIndex;
        public boolean hasNext;
        final IntMap<V> map;
        int nextIndex;
        boolean valid = true;

        public MapIterator(IntMap<V> map2) {
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
                    this.map.valueTable[this.currentIndex] = null;
                }
            } else {
                IntMap<V> intMap = this.map;
                intMap.zeroValue = null;
                intMap.hasZeroValue = false;
            }
            this.currentIndex = INDEX_ILLEGAL;
            IntMap<V> intMap2 = this.map;
            intMap2.size--;
        }
    }

    public static class Entries<V> extends MapIterator<V> implements Iterable<Entry<V>>, Iterator<Entry<V>> {
        private Entry<V> entry = new Entry<>();

        public /* bridge */ /* synthetic */ void reset() {
            super.reset();
        }

        public Entries(IntMap map) {
            super(map);
        }

        public Entry<V> next() {
            if (!this.hasNext) {
                throw new NoSuchElementException();
            } else if (this.valid) {
                int[] keyTable = this.map.keyTable;
                if (this.nextIndex == -1) {
                    Entry<V> entry2 = this.entry;
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

        public Iterator<Entry<V>> iterator() {
            return this;
        }

        public void remove() {
            super.remove();
        }
    }

    public static class Values<V> extends MapIterator<V> implements Iterable<V>, Iterator<V> {
        public /* bridge */ /* synthetic */ void reset() {
            super.reset();
        }

        public Values(IntMap<V> map) {
            super(map);
        }

        public boolean hasNext() {
            if (this.valid) {
                return this.hasNext;
            }
            throw new GdxRuntimeException("#iterator() cannot be used nested.");
        }

        public V next() {
            V value;
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

        public Iterator<V> iterator() {
            return this;
        }

        public Array<V> toArray() {
            Array array = new Array(true, this.map.size);
            while (this.hasNext) {
                array.add(next());
            }
            return array;
        }

        public void remove() {
            super.remove();
        }
    }

    public static class Keys extends MapIterator {
        public /* bridge */ /* synthetic */ void remove() {
            super.remove();
        }

        public /* bridge */ /* synthetic */ void reset() {
            super.reset();
        }

        public Keys(IntMap map) {
            super(map);
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
