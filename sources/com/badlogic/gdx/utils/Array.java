package com.badlogic.gdx.utils;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Predicate;
import com.badlogic.gdx.utils.reflect.ArrayReflection;
import com.twi.game.BuildConfig;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Array<T> implements Iterable<T> {
    public T[] items;
    private ArrayIterable iterable;
    public boolean ordered;
    private Predicate.PredicateIterable<T> predicateIterable;
    public int size;

    public Array() {
        this(true, 16);
    }

    public Array(int capacity) {
        this(true, capacity);
    }

    public Array(boolean ordered2, int capacity) {
        this.ordered = ordered2;
        this.items = (Object[]) new Object[capacity];
    }

    public Array(boolean ordered2, int capacity, Class arrayType) {
        this.ordered = ordered2;
        this.items = (Object[]) ArrayReflection.newInstance(arrayType, capacity);
    }

    public Array(Class arrayType) {
        this(true, 16, arrayType);
    }

    public Array(Array<? extends T> array) {
        this(array.ordered, array.size, array.items.getClass().getComponentType());
        this.size = array.size;
        System.arraycopy(array.items, 0, this.items, 0, this.size);
    }

    public Array(T[] array) {
        this(true, array, 0, array.length);
    }

    public Array(boolean ordered2, T[] array, int start, int count) {
        this(ordered2, count, array.getClass().getComponentType());
        this.size = count;
        System.arraycopy(array, start, this.items, 0, this.size);
    }

    public void add(T value) {
        T[] items2 = this.items;
        int i = this.size;
        if (i == items2.length) {
            items2 = resize(Math.max(8, (int) (((float) i) * 1.75f)));
        }
        int i2 = this.size;
        this.size = i2 + 1;
        items2[i2] = value;
    }

    public void add(T value1, T value2) {
        T[] items2 = this.items;
        int i = this.size;
        if (i + 1 >= items2.length) {
            items2 = resize(Math.max(8, (int) (((float) i) * 1.75f)));
        }
        int i2 = this.size;
        items2[i2] = value1;
        items2[i2 + 1] = value2;
        this.size = i2 + 2;
    }

    public void add(T value1, T value2, T value3) {
        T[] items2 = this.items;
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

    public void add(T value1, T value2, T value3, T value4) {
        T[] items2 = this.items;
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

    public void addAll(Array<? extends T> array) {
        addAll(array.items, 0, array.size);
    }

    public void addAll(Array<? extends T> array, int start, int count) {
        if (start + count <= array.size) {
            addAll((T[]) (Object[]) array.items, start, count);
            return;
        }
        throw new IllegalArgumentException("start + count must be <= size: " + start + " + " + count + " <= " + array.size);
    }

    public void addAll(T... array) {
        addAll(array, 0, array.length);
    }

    public void addAll(T[] array, int start, int count) {
        T[] items2 = this.items;
        int sizeNeeded = this.size + count;
        if (sizeNeeded > items2.length) {
            items2 = resize(Math.max(8, (int) (((float) sizeNeeded) * 1.75f)));
        }
        System.arraycopy(array, start, items2, this.size, count);
        this.size += count;
    }

    public T get(int index) {
        if (index < this.size) {
            return this.items[index];
        }
        throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + this.size);
    }

    public void set(int index, T value) {
        if (index < this.size) {
            this.items[index] = value;
            return;
        }
        throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + this.size);
    }

    public void insert(int index, T value) {
        int i = this.size;
        if (index <= i) {
            T[] items2 = this.items;
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
            T[] items2 = this.items;
            T firstValue = items2[first];
            items2[first] = items2[second];
            items2[second] = firstValue;
        } else {
            throw new IndexOutOfBoundsException("second can't be >= size: " + second + " >= " + this.size);
        }
    }

    public boolean contains(T value, boolean identity) {
        T[] items2 = this.items;
        int i = this.size - 1;
        if (identity || value == null) {
            while (i >= 0) {
                int i2 = i - 1;
                if (items2[i] == value) {
                    return true;
                }
                i = i2;
            }
            return false;
        }
        while (i >= 0) {
            int i3 = i - 1;
            if (value.equals(items2[i])) {
                return true;
            }
            i = i3;
        }
        return false;
    }

    public boolean containsAll(Array<? extends T> values, boolean identity) {
        T[] items2 = values.items;
        int n = values.size;
        for (int i = 0; i < n; i++) {
            if (!contains(items2[i], identity)) {
                return false;
            }
        }
        return true;
    }

    public boolean containsAny(Array<? extends T> values, boolean identity) {
        T[] items2 = values.items;
        int n = values.size;
        for (int i = 0; i < n; i++) {
            if (contains(items2[i], identity)) {
                return true;
            }
        }
        return false;
    }

    public int indexOf(T value, boolean identity) {
        T[] items2 = this.items;
        if (identity || value == null) {
            int n = this.size;
            for (int i = 0; i < n; i++) {
                if (items2[i] == value) {
                    return i;
                }
            }
            return -1;
        }
        int n2 = this.size;
        for (int i2 = 0; i2 < n2; i2++) {
            if (value.equals(items2[i2])) {
                return i2;
            }
        }
        return -1;
    }

    public int lastIndexOf(T value, boolean identity) {
        T[] items2 = this.items;
        if (identity || value == null) {
            for (int i = this.size - 1; i >= 0; i--) {
                if (items2[i] == value) {
                    return i;
                }
            }
            return -1;
        }
        for (int i2 = this.size - 1; i2 >= 0; i2--) {
            if (value.equals(items2[i2])) {
                return i2;
            }
        }
        return -1;
    }

    public boolean removeValue(T value, boolean identity) {
        T[] items2 = this.items;
        if (identity || value == null) {
            int n = this.size;
            for (int i = 0; i < n; i++) {
                if (items2[i] == value) {
                    removeIndex(i);
                    return true;
                }
            }
            return false;
        }
        int n2 = this.size;
        for (int i2 = 0; i2 < n2; i2++) {
            if (value.equals(items2[i2])) {
                removeIndex(i2);
                return true;
            }
        }
        return false;
    }

    public T removeIndex(int index) {
        int i = this.size;
        if (index < i) {
            T[] items2 = this.items;
            T value = items2[index];
            this.size = i - 1;
            if (this.ordered) {
                System.arraycopy(items2, index + 1, items2, index, this.size - index);
            } else {
                items2[index] = items2[this.size];
            }
            items2[this.size] = null;
            return value;
        }
        throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + this.size);
    }

    public void removeRange(int start, int end) {
        int n = this.size;
        if (end >= n) {
            throw new IndexOutOfBoundsException("end can't be >= size: " + end + " >= " + this.size);
        } else if (start <= end) {
            T[] items2 = this.items;
            int count = (end - start) + 1;
            int lastIndex = n - count;
            if (this.ordered) {
                System.arraycopy(items2, start + count, items2, start, n - (start + count));
            } else {
                int i = Math.max(lastIndex, end + 1);
                System.arraycopy(items2, i, items2, start, n - i);
            }
            for (int i2 = lastIndex; i2 < n; i2++) {
                items2[i2] = null;
            }
            this.size = n - count;
        } else {
            throw new IndexOutOfBoundsException("start can't be > end: " + start + " > " + end);
        }
    }

    public boolean removeAll(Array<? extends T> array, boolean identity) {
        int size2 = this.size;
        int startSize = size2;
        T[] items2 = this.items;
        if (identity) {
            int n = array.size;
            for (int i = 0; i < n; i++) {
                T item = array.get(i);
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
        } else {
            int n2 = array.size;
            for (int i2 = 0; i2 < n2; i2++) {
                T item2 = array.get(i2);
                int ii2 = 0;
                while (true) {
                    if (ii2 >= size2) {
                        break;
                    } else if (item2.equals(items2[ii2])) {
                        removeIndex(ii2);
                        size2--;
                        break;
                    } else {
                        ii2++;
                    }
                }
            }
        }
        return size2 != startSize;
    }

    public T pop() {
        int i = this.size;
        if (i != 0) {
            this.size = i - 1;
            T[] tArr = this.items;
            int i2 = this.size;
            T item = tArr[i2];
            tArr[i2] = null;
            return item;
        }
        throw new IllegalStateException("Array is empty.");
    }

    public T peek() {
        int i = this.size;
        if (i != 0) {
            return this.items[i - 1];
        }
        throw new IllegalStateException("Array is empty.");
    }

    public T first() {
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
        T[] items2 = this.items;
        int n = this.size;
        for (int i = 0; i < n; i++) {
            items2[i] = null;
        }
        this.size = 0;
    }

    public T[] shrink() {
        int length = this.items.length;
        int i = this.size;
        if (length != i) {
            resize(i);
        }
        return this.items;
    }

    public T[] ensureCapacity(int additionalCapacity) {
        if (additionalCapacity >= 0) {
            int sizeNeeded = this.size + additionalCapacity;
            if (sizeNeeded > this.items.length) {
                resize(Math.max(8, sizeNeeded));
            }
            return this.items;
        }
        throw new IllegalArgumentException("additionalCapacity must be >= 0: " + additionalCapacity);
    }

    public T[] setSize(int newSize) {
        truncate(newSize);
        if (newSize > this.items.length) {
            resize(Math.max(8, newSize));
        }
        this.size = newSize;
        return this.items;
    }

    /* access modifiers changed from: protected */
    public T[] resize(int newSize) {
        T[] items2 = this.items;
        T[] newItems = (Object[]) ArrayReflection.newInstance(items2.getClass().getComponentType(), newSize);
        System.arraycopy(items2, 0, newItems, 0, Math.min(this.size, newItems.length));
        this.items = newItems;
        return newItems;
    }

    public void sort() {
        Sort.instance().sort(this.items, 0, this.size);
    }

    public void sort(Comparator<? super T> comparator) {
        Sort.instance().sort(this.items, comparator, 0, this.size);
    }

    public T selectRanked(Comparator<T> comparator, int kthLowest) {
        if (kthLowest >= 1) {
            return Select.instance().select(this.items, comparator, kthLowest, this.size);
        }
        throw new GdxRuntimeException("nth_lowest must be greater than 0, 1 = first, 2 = second...");
    }

    public int selectRankedIndex(Comparator<T> comparator, int kthLowest) {
        if (kthLowest >= 1) {
            return Select.instance().selectIndex(this.items, comparator, kthLowest, this.size);
        }
        throw new GdxRuntimeException("nth_lowest must be greater than 0, 1 = first, 2 = second...");
    }

    public void reverse() {
        T[] items2 = this.items;
        int i = this.size;
        int lastIndex = i - 1;
        int n = i / 2;
        for (int i2 = 0; i2 < n; i2++) {
            int ii = lastIndex - i2;
            T temp = items2[i2];
            items2[i2] = items2[ii];
            items2[ii] = temp;
        }
    }

    public void shuffle() {
        T[] items2 = this.items;
        for (int i = this.size - 1; i >= 0; i--) {
            int ii = MathUtils.random(i);
            T temp = items2[i];
            items2[i] = items2[ii];
            items2[ii] = temp;
        }
    }

    public Iterator<T> iterator() {
        if (Collections.allocateIterators) {
            return new ArrayIterator(this, true);
        }
        if (this.iterable == null) {
            this.iterable = new ArrayIterable(this);
        }
        return this.iterable.iterator();
    }

    public Iterable<T> select(Predicate<T> predicate) {
        if (Collections.allocateIterators) {
            new Predicate.PredicateIterable(this, predicate);
        }
        Predicate.PredicateIterable<T> predicateIterable2 = this.predicateIterable;
        if (predicateIterable2 == null) {
            this.predicateIterable = new Predicate.PredicateIterable<>(this, predicate);
        } else {
            predicateIterable2.set(this, predicate);
        }
        return this.predicateIterable;
    }

    public void truncate(int newSize) {
        if (newSize < 0) {
            throw new IllegalArgumentException("newSize must be >= 0: " + newSize);
        } else if (this.size > newSize) {
            for (int i = newSize; i < this.size; i++) {
                this.items[i] = null;
            }
            this.size = newSize;
        }
    }

    public T random() {
        int i = this.size;
        if (i == 0) {
            return null;
        }
        return this.items[MathUtils.random(0, i - 1)];
    }

    public T[] toArray() {
        return toArray(this.items.getClass().getComponentType());
    }

    public <V> V[] toArray(Class<V> type) {
        V[] result = (Object[]) ArrayReflection.newInstance(type, this.size);
        System.arraycopy(this.items, 0, result, 0, this.size);
        return result;
    }

    public int hashCode() {
        if (!this.ordered) {
            return super.hashCode();
        }
        Object[] items2 = this.items;
        int h = 1;
        int n = this.size;
        for (int i = 0; i < n; i++) {
            h *= 31;
            Object item = items2[i];
            if (item != null) {
                h += item.hashCode();
            }
        }
        return h;
    }

    public boolean equals(Object object) {
        int n;
        if (object == this) {
            return true;
        }
        if (!this.ordered || !(object instanceof Array)) {
            return false;
        }
        Array array = (Array) object;
        if (!array.ordered || (n = this.size) != array.size) {
            return false;
        }
        Object[] items1 = this.items;
        Object[] items2 = array.items;
        int i = 0;
        while (i < n) {
            Object o1 = items1[i];
            Object o2 = items2[i];
            if (o1 == null) {
                if (o2 == null) {
                    i++;
                }
            } else if (o1.equals(o2)) {
                i++;
            }
            return false;
        }
        return true;
    }

    public boolean equalsIdentity(Object object) {
        int n;
        if (object == this) {
            return true;
        }
        if (!this.ordered || !(object instanceof Array)) {
            return false;
        }
        Array array = (Array) object;
        if (!array.ordered || (n = this.size) != array.size) {
            return false;
        }
        Object[] items1 = this.items;
        Object[] items2 = array.items;
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
        T[] items2 = this.items;
        StringBuilder buffer = new StringBuilder(32);
        buffer.append('[');
        buffer.append((Object) items2[0]);
        for (int i = 1; i < this.size; i++) {
            buffer.append(", ");
            buffer.append((Object) items2[i]);
        }
        buffer.append(']');
        return buffer.toString();
    }

    public String toString(String separator) {
        if (this.size == 0) {
            return BuildConfig.FLAVOR;
        }
        T[] items2 = this.items;
        StringBuilder buffer = new StringBuilder(32);
        buffer.append((Object) items2[0]);
        for (int i = 1; i < this.size; i++) {
            buffer.append(separator);
            buffer.append((Object) items2[i]);
        }
        return buffer.toString();
    }

    public static <T> Array<T> of(Class<T> arrayType) {
        return new Array<>((Class) arrayType);
    }

    public static <T> Array<T> of(boolean ordered2, int capacity, Class<T> arrayType) {
        return new Array<>(ordered2, capacity, arrayType);
    }

    public static <T> Array<T> with(T... array) {
        return new Array<>(array);
    }

    public static class ArrayIterator<T> implements Iterator<T>, Iterable<T> {
        private final boolean allowRemove;
        private final Array<T> array;
        int index;
        boolean valid;

        public ArrayIterator(Array<T> array2) {
            this(array2, true);
        }

        public ArrayIterator(Array<T> array2, boolean allowRemove2) {
            this.valid = true;
            this.array = array2;
            this.allowRemove = allowRemove2;
        }

        public boolean hasNext() {
            if (this.valid) {
                return this.index < this.array.size;
            }
            throw new GdxRuntimeException("#iterator() cannot be used nested.");
        }

        public T next() {
            if (this.index >= this.array.size) {
                throw new NoSuchElementException(String.valueOf(this.index));
            } else if (this.valid) {
                T[] tArr = this.array.items;
                int i = this.index;
                this.index = i + 1;
                return tArr[i];
            } else {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
        }

        public void remove() {
            if (this.allowRemove) {
                this.index--;
                this.array.removeIndex(this.index);
                return;
            }
            throw new GdxRuntimeException("Remove not allowed.");
        }

        public void reset() {
            this.index = 0;
        }

        public Iterator<T> iterator() {
            return this;
        }
    }

    public static class ArrayIterable<T> implements Iterable<T> {
        private final boolean allowRemove;
        private final Array<T> array;
        private ArrayIterator iterator1;
        private ArrayIterator iterator2;

        public ArrayIterable(Array<T> array2) {
            this(array2, true);
        }

        public ArrayIterable(Array<T> array2, boolean allowRemove2) {
            this.array = array2;
            this.allowRemove = allowRemove2;
        }

        public Iterator<T> iterator() {
            if (Collections.allocateIterators) {
                return new ArrayIterator(this.array, this.allowRemove);
            }
            if (this.iterator1 == null) {
                this.iterator1 = new ArrayIterator(this.array, this.allowRemove);
                this.iterator2 = new ArrayIterator(this.array, this.allowRemove);
            }
            if (!this.iterator1.valid) {
                ArrayIterator arrayIterator = this.iterator1;
                arrayIterator.index = 0;
                arrayIterator.valid = true;
                this.iterator2.valid = false;
                return arrayIterator;
            }
            ArrayIterator arrayIterator2 = this.iterator2;
            arrayIterator2.index = 0;
            arrayIterator2.valid = true;
            this.iterator1.valid = false;
            return arrayIterator2;
        }
    }
}
