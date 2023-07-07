package com.badlogic.gdx.utils;

import java.util.Comparator;

public class DelayedRemovalArray<T> extends Array<T> {
    private int clear;
    private int iterating;
    private IntArray remove = new IntArray(0);

    public DelayedRemovalArray() {
    }

    public DelayedRemovalArray(Array array) {
        super(array);
    }

    public DelayedRemovalArray(boolean ordered, int capacity, Class arrayType) {
        super(ordered, capacity, arrayType);
    }

    public DelayedRemovalArray(boolean ordered, int capacity) {
        super(ordered, capacity);
    }

    public DelayedRemovalArray(boolean ordered, T[] array, int startIndex, int count) {
        super(ordered, array, startIndex, count);
    }

    public DelayedRemovalArray(Class arrayType) {
        super(arrayType);
    }

    public DelayedRemovalArray(int capacity) {
        super(capacity);
    }

    public DelayedRemovalArray(T[] array) {
        super(array);
    }

    public void begin() {
        this.iterating++;
    }

    public void end() {
        int i = this.iterating;
        if (i != 0) {
            this.iterating = i - 1;
            if (this.iterating == 0) {
                int i2 = this.clear;
                if (i2 <= 0 || i2 != this.size) {
                    int n = this.remove.size;
                    for (int i3 = 0; i3 < n; i3++) {
                        int index = this.remove.pop();
                        if (index >= this.clear) {
                            removeIndex(index);
                        }
                    }
                    for (int i4 = this.clear - 1; i4 >= 0; i4--) {
                        removeIndex(i4);
                    }
                } else {
                    this.remove.clear();
                    clear();
                }
                this.clear = 0;
                return;
            }
            return;
        }
        throw new IllegalStateException("begin must be called before end.");
    }

    private void remove(int index) {
        if (index >= this.clear) {
            int i = 0;
            int n = this.remove.size;
            while (i < n) {
                int removeIndex = this.remove.get(i);
                if (index != removeIndex) {
                    if (index < removeIndex) {
                        this.remove.insert(i, index);
                        return;
                    }
                    i++;
                } else {
                    return;
                }
            }
            this.remove.add(index);
        }
    }

    public boolean removeValue(T value, boolean identity) {
        if (this.iterating <= 0) {
            return super.removeValue(value, identity);
        }
        int index = indexOf(value, identity);
        if (index == -1) {
            return false;
        }
        remove(index);
        return true;
    }

    public T removeIndex(int index) {
        if (this.iterating <= 0) {
            return super.removeIndex(index);
        }
        remove(index);
        return get(index);
    }

    public void removeRange(int start, int end) {
        if (this.iterating > 0) {
            for (int i = end; i >= start; i--) {
                remove(i);
            }
            return;
        }
        super.removeRange(start, end);
    }

    public void clear() {
        if (this.iterating > 0) {
            this.clear = this.size;
        } else {
            super.clear();
        }
    }

    public void set(int index, T value) {
        if (this.iterating <= 0) {
            super.set(index, value);
            return;
        }
        throw new IllegalStateException("Invalid between begin/end.");
    }

    public void insert(int index, T value) {
        if (this.iterating <= 0) {
            super.insert(index, value);
            return;
        }
        throw new IllegalStateException("Invalid between begin/end.");
    }

    public void swap(int first, int second) {
        if (this.iterating <= 0) {
            super.swap(first, second);
            return;
        }
        throw new IllegalStateException("Invalid between begin/end.");
    }

    public T pop() {
        if (this.iterating <= 0) {
            return super.pop();
        }
        throw new IllegalStateException("Invalid between begin/end.");
    }

    public void sort() {
        if (this.iterating <= 0) {
            super.sort();
            return;
        }
        throw new IllegalStateException("Invalid between begin/end.");
    }

    public void sort(Comparator<? super T> comparator) {
        if (this.iterating <= 0) {
            super.sort(comparator);
            return;
        }
        throw new IllegalStateException("Invalid between begin/end.");
    }

    public void reverse() {
        if (this.iterating <= 0) {
            super.reverse();
            return;
        }
        throw new IllegalStateException("Invalid between begin/end.");
    }

    public void shuffle() {
        if (this.iterating <= 0) {
            super.shuffle();
            return;
        }
        throw new IllegalStateException("Invalid between begin/end.");
    }

    public void truncate(int newSize) {
        if (this.iterating <= 0) {
            super.truncate(newSize);
            return;
        }
        throw new IllegalStateException("Invalid between begin/end.");
    }

    public T[] setSize(int newSize) {
        if (this.iterating <= 0) {
            return super.setSize(newSize);
        }
        throw new IllegalStateException("Invalid between begin/end.");
    }

    public static <T> DelayedRemovalArray<T> with(T... array) {
        return new DelayedRemovalArray<>(array);
    }
}
