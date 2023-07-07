package com.badlogic.gdx.utils;

import com.badlogic.gdx.math.MathUtils;
import com.twi.game.BuildConfig;
import java.util.Arrays;

public class ByteArray {
    public byte[] items;
    public boolean ordered;
    public int size;

    public ByteArray() {
        this(true, 16);
    }

    public ByteArray(int capacity) {
        this(true, capacity);
    }

    public ByteArray(boolean ordered2, int capacity) {
        this.ordered = ordered2;
        this.items = new byte[capacity];
    }

    public ByteArray(ByteArray array) {
        this.ordered = array.ordered;
        this.size = array.size;
        int i = this.size;
        this.items = new byte[i];
        System.arraycopy(array.items, 0, this.items, 0, i);
    }

    public ByteArray(byte[] array) {
        this(true, array, 0, array.length);
    }

    public ByteArray(boolean ordered2, byte[] array, int startIndex, int count) {
        this(ordered2, count);
        this.size = count;
        System.arraycopy(array, startIndex, this.items, 0, count);
    }

    public void add(byte value) {
        byte[] items2 = this.items;
        int i = this.size;
        if (i == items2.length) {
            items2 = resize(Math.max(8, (int) (((float) i) * 1.75f)));
        }
        int i2 = this.size;
        this.size = i2 + 1;
        items2[i2] = value;
    }

    public void add(byte value1, byte value2) {
        byte[] items2 = this.items;
        int i = this.size;
        if (i + 1 >= items2.length) {
            items2 = resize(Math.max(8, (int) (((float) i) * 1.75f)));
        }
        int i2 = this.size;
        items2[i2] = value1;
        items2[i2 + 1] = value2;
        this.size = i2 + 2;
    }

    public void add(byte value1, byte value2, byte value3) {
        byte[] items2 = this.items;
        int i = this.size;
        if (i + 2 >= items2.length) {
            items2 = resize(Math.max(8, (int) (((float) i) * 1.75f)));
        }
        int i2 = this.size;
        items2[i2] = value1;
        items2[i2 + 1] = value2;
        items2[i2 + 2] = value3;
        this.size = i2 + 3;
    }

    public void add(byte value1, byte value2, byte value3, byte value4) {
        byte[] items2 = this.items;
        int i = this.size;
        if (i + 3 >= items2.length) {
            items2 = resize(Math.max(8, (int) (((float) i) * 1.8f)));
        }
        int i2 = this.size;
        items2[i2] = value1;
        items2[i2 + 1] = value2;
        items2[i2 + 2] = value3;
        items2[i2 + 3] = value4;
        this.size = i2 + 4;
    }

    public void addAll(ByteArray array) {
        addAll(array.items, 0, array.size);
    }

    public void addAll(ByteArray array, int offset, int length) {
        if (offset + length <= array.size) {
            addAll(array.items, offset, length);
            return;
        }
        throw new IllegalArgumentException("offset + length must be <= size: " + offset + " + " + length + " <= " + array.size);
    }

    public void addAll(byte... array) {
        addAll(array, 0, array.length);
    }

    public void addAll(byte[] array, int offset, int length) {
        byte[] items2 = this.items;
        int sizeNeeded = this.size + length;
        if (sizeNeeded > items2.length) {
            items2 = resize(Math.max(8, (int) (((float) sizeNeeded) * 1.75f)));
        }
        System.arraycopy(array, offset, items2, this.size, length);
        this.size += length;
    }

    public byte get(int index) {
        if (index < this.size) {
            return this.items[index];
        }
        throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + this.size);
    }

    public void set(int index, byte value) {
        if (index < this.size) {
            this.items[index] = value;
            return;
        }
        throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + this.size);
    }

    public void incr(int index, byte value) {
        if (index < this.size) {
            byte[] bArr = this.items;
            bArr[index] = (byte) (bArr[index] + value);
            return;
        }
        throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + this.size);
    }

    public void mul(int index, byte value) {
        if (index < this.size) {
            byte[] bArr = this.items;
            bArr[index] = (byte) (bArr[index] * value);
            return;
        }
        throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + this.size);
    }

    public void insert(int index, byte value) {
        int i = this.size;
        if (index <= i) {
            byte[] items2 = this.items;
            if (i == items2.length) {
                items2 = resize(Math.max(8, (int) (((float) i) * 1.75f)));
            }
            if (this.ordered) {
                System.arraycopy(items2, index, items2, index + 1, this.size - index);
            } else {
                items2[this.size] = items2[index];
            }
            this.size++;
            items2[index] = value;
            return;
        }
        throw new IndexOutOfBoundsException("index can't be > size: " + index + " > " + this.size);
    }

    public void swap(int first, int second) {
        int i = this.size;
        if (first >= i) {
            throw new IndexOutOfBoundsException("first can't be >= size: " + first + " >= " + this.size);
        } else if (second < i) {
            byte[] items2 = this.items;
            byte firstValue = items2[first];
            items2[first] = items2[second];
            items2[second] = firstValue;
        } else {
            throw new IndexOutOfBoundsException("second can't be >= size: " + second + " >= " + this.size);
        }
    }

    public boolean contains(byte value) {
        int i = this.size - 1;
        byte[] items2 = this.items;
        while (i >= 0) {
            int i2 = i - 1;
            if (items2[i] == value) {
                return true;
            }
            i = i2;
        }
        return false;
    }

    public int indexOf(byte value) {
        byte[] items2 = this.items;
        int n = this.size;
        for (int i = 0; i < n; i++) {
            if (items2[i] == value) {
                return i;
            }
        }
        return -1;
    }

    public int lastIndexOf(byte value) {
        byte[] items2 = this.items;
        for (int i = this.size - 1; i >= 0; i--) {
            if (items2[i] == value) {
                return i;
            }
        }
        return -1;
    }

    public boolean removeValue(byte value) {
        byte[] items2 = this.items;
        int n = this.size;
        for (int i = 0; i < n; i++) {
            if (items2[i] == value) {
                removeIndex(i);
                return true;
            }
        }
        return false;
    }

    public int removeIndex(int index) {
        int i = this.size;
        if (index < i) {
            byte[] items2 = this.items;
            byte value = items2[index];
            this.size = i - 1;
            if (this.ordered) {
                System.arraycopy(items2, index + 1, items2, index, this.size - index);
            } else {
                items2[index] = items2[this.size];
            }
            return value;
        }
        throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + this.size);
    }

    public void removeRange(int start, int end) {
        int n = this.size;
        if (end >= n) {
            throw new IndexOutOfBoundsException("end can't be >= size: " + end + " >= " + this.size);
        } else if (start <= end) {
            int count = (end - start) + 1;
            int lastIndex = n - count;
            if (this.ordered) {
                byte[] bArr = this.items;
                System.arraycopy(bArr, start + count, bArr, start, n - (start + count));
            } else {
                int i = Math.max(lastIndex, end + 1);
                byte[] bArr2 = this.items;
                System.arraycopy(bArr2, i, bArr2, start, n - i);
            }
            this.size = n - count;
        } else {
            throw new IndexOutOfBoundsException("start can't be > end: " + start + " > " + end);
        }
    }

    public boolean removeAll(ByteArray array) {
        int size2 = this.size;
        int startSize = size2;
        byte[] items2 = this.items;
        int n = array.size;
        for (int i = 0; i < n; i++) {
            int item = array.get(i);
            int ii = 0;
            while (true) {
                if (ii >= size2) {
                    break;
                } else if (item == items2[ii]) {
                    removeIndex(ii);
                    size2--;
                    break;
                } else {
                    ii++;
                }
            }
        }
        return size2 != startSize;
    }

    public byte pop() {
        byte[] bArr = this.items;
        int i = this.size - 1;
        this.size = i;
        return bArr[i];
    }

    public byte peek() {
        return this.items[this.size - 1];
    }

    public byte first() {
        if (this.size != 0) {
            return this.items[0];
        }
        throw new IllegalStateException("Array is empty.");
    }

    public boolean notEmpty() {
        return this.size > 0;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public void clear() {
        this.size = 0;
    }

    public byte[] shrink() {
        int length = this.items.length;
        int i = this.size;
        if (length != i) {
            resize(i);
        }
        return this.items;
    }

    public byte[] ensureCapacity(int additionalCapacity) {
        if (additionalCapacity >= 0) {
            int sizeNeeded = this.size + additionalCapacity;
            if (sizeNeeded > this.items.length) {
                resize(Math.max(8, sizeNeeded));
            }
            return this.items;
        }
        throw new IllegalArgumentException("additionalCapacity must be >= 0: " + additionalCapacity);
    }

    public byte[] setSize(int newSize) {
        if (newSize >= 0) {
            if (newSize > this.items.length) {
                resize(Math.max(8, newSize));
            }
            this.size = newSize;
            return this.items;
        }
        throw new IllegalArgumentException("newSize must be >= 0: " + newSize);
    }

    /* access modifiers changed from: protected */
    public byte[] resize(int newSize) {
        byte[] newItems = new byte[newSize];
        System.arraycopy(this.items, 0, newItems, 0, Math.min(this.size, newItems.length));
        this.items = newItems;
        return newItems;
    }

    public void sort() {
        Arrays.sort(this.items, 0, this.size);
    }

    public void reverse() {
        byte[] items2 = this.items;
        int i = this.size;
        int lastIndex = i - 1;
        int n = i / 2;
        for (int i2 = 0; i2 < n; i2++) {
            int ii = lastIndex - i2;
            byte temp = items2[i2];
            items2[i2] = items2[ii];
            items2[ii] = temp;
        }
    }

    public void shuffle() {
        byte[] items2 = this.items;
        for (int i = this.size - 1; i >= 0; i--) {
            int ii = MathUtils.random(i);
            byte temp = items2[i];
            items2[i] = items2[ii];
            items2[ii] = temp;
        }
    }

    public void truncate(int newSize) {
        if (this.size > newSize) {
            this.size = newSize;
        }
    }

    public byte random() {
        int i = this.size;
        if (i == 0) {
            return 0;
        }
        return this.items[MathUtils.random(0, i - 1)];
    }

    public byte[] toArray() {
        int i = this.size;
        byte[] array = new byte[i];
        System.arraycopy(this.items, 0, array, 0, i);
        return array;
    }

    public int hashCode() {
        if (!this.ordered) {
            return super.hashCode();
        }
        byte[] items2 = this.items;
        int h = 1;
        int n = this.size;
        for (int i = 0; i < n; i++) {
            h = (h * 31) + items2[i];
        }
        return h;
    }

    public boolean equals(Object object) {
        int n;
        if (object == this) {
            return true;
        }
        if (!this.ordered || !(object instanceof ByteArray)) {
            return false;
        }
        ByteArray array = (ByteArray) object;
        if (!array.ordered || (n = this.size) != array.size) {
            return false;
        }
        byte[] items1 = this.items;
        byte[] items2 = array.items;
        for (int i = 0; i < n; i++) {
            if (items1[i] != items2[i]) {
                return false;
            }
        }
        return true;
    }

    public String toString() {
        if (this.size == 0) {
            return "[]";
        }
        byte[] items2 = this.items;
        StringBuilder buffer = new StringBuilder(32);
        buffer.append('[');
        buffer.append((int) items2[0]);
        for (int i = 1; i < this.size; i++) {
            buffer.append(", ");
            buffer.append((int) items2[i]);
        }
        buffer.append(']');
        return buffer.toString();
    }

    public String toString(String separator) {
        if (this.size == 0) {
            return BuildConfig.FLAVOR;
        }
        byte[] items2 = this.items;
        StringBuilder buffer = new StringBuilder(32);
        buffer.append((int) items2[0]);
        for (int i = 1; i < this.size; i++) {
            buffer.append(separator);
            buffer.append((int) items2[i]);
        }
        return buffer.toString();
    }

    public static ByteArray with(byte... array) {
        return new ByteArray(array);
    }
}
