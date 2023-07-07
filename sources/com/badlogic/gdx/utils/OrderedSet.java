package com.badlogic.gdx.utils;

import com.badlogic.gdx.utils.ObjectSet;
import java.util.NoSuchElementException;

public class OrderedSet<T> extends ObjectSet<T> {
    final Array<T> items;
    OrderedSetIterator iterator1;
    OrderedSetIterator iterator2;

    public OrderedSet() {
        this.items = new Array<>();
    }

    public OrderedSet(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
        this.items = new Array<>(this.capacity);
    }

    public OrderedSet(int initialCapacity) {
        super(initialCapacity);
        this.items = new Array<>(this.capacity);
    }

    public OrderedSet(OrderedSet<? extends T> set) {
        super(set);
        this.items = new Array<>(this.capacity);
        this.items.addAll(set.items);
    }

    public boolean add(T key) {
        if (!super.add(key)) {
            return false;
        }
        this.items.add(key);
        return true;
    }

    public boolean add(T key, int index) {
        if (!super.add(key)) {
            this.items.removeValue(key, true);
            this.items.insert(index, key);
            return false;
        }
        this.items.insert(index, key);
        return true;
    }

    public boolean remove(T key) {
        if (!super.remove(key)) {
            return false;
        }
        this.items.removeValue(key, false);
        return true;
    }

    public T removeIndex(int index) {
        T key = this.items.removeIndex(index);
        super.remove(key);
        return key;
    }

    public void clear(int maximumCapacity) {
        this.items.clear();
        super.clear(maximumCapacity);
    }

    public void clear() {
        this.items.clear();
        super.clear();
    }

    public Array<T> orderedItems() {
        return this.items;
    }

    public OrderedSetIterator<T> iterator() {
        if (Collections.allocateIterators) {
            return new OrderedSetIterator<>(this);
        }
        if (this.iterator1 == null) {
            this.iterator1 = new OrderedSetIterator(this);
            this.iterator2 = new OrderedSetIterator(this);
        }
        if (!this.iterator1.valid) {
            this.iterator1.reset();
            OrderedSetIterator<T> orderedSetIterator = this.iterator1;
            orderedSetIterator.valid = true;
            this.iterator2.valid = false;
            return orderedSetIterator;
        }
        this.iterator2.reset();
        OrderedSetIterator<T> orderedSetIterator2 = this.iterator2;
        orderedSetIterator2.valid = true;
        this.iterator1.valid = false;
        return orderedSetIterator2;
    }

    public String toString() {
        if (this.size == 0) {
            return "{}";
        }
        T[] items2 = this.items.items;
        StringBuilder buffer = new StringBuilder(32);
        buffer.append('{');
        buffer.append((Object) items2[0]);
        for (int i = 1; i < this.size; i++) {
            buffer.append(", ");
            buffer.append((Object) items2[i]);
        }
        buffer.append('}');
        return buffer.toString();
    }

    public String toString(String separator) {
        return this.items.toString(separator);
    }

    public static class OrderedSetIterator<T> extends ObjectSet.ObjectSetIterator<T> {
        private Array<T> items;

        public OrderedSetIterator(OrderedSet<T> set) {
            super(set);
            this.items = set.items;
        }

        public void reset() {
            boolean z = false;
            this.nextIndex = 0;
            if (this.set.size > 0) {
                z = true;
            }
            this.hasNext = z;
        }

        public T next() {
            if (!this.hasNext) {
                throw new NoSuchElementException();
            } else if (this.valid) {
                T key = this.items.get(this.nextIndex);
                boolean z = true;
                this.nextIndex++;
                if (this.nextIndex >= this.set.size) {
                    z = false;
                }
                this.hasNext = z;
                return key;
            } else {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
        }

        public void remove() {
            if (this.nextIndex >= 0) {
                this.nextIndex--;
                ((OrderedSet) this.set).removeIndex(this.nextIndex);
                return;
            }
            throw new IllegalStateException("next must be called before remove.");
        }
    }
}
