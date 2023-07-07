package com.badlogic.gdx.utils;

import com.badlogic.gdx.utils.reflect.ArrayReflection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Queue<T> implements Iterable<T> {
    protected int head;
    private QueueIterable iterable;
    public int size;
    protected int tail;
    protected T[] values;

    public Queue() {
        this(16);
    }

    public Queue(int initialSize) {
        this.head = 0;
        this.tail = 0;
        this.size = 0;
        this.values = (Object[]) new Object[initialSize];
    }

    public Queue(int initialSize, Class<T> type) {
        this.head = 0;
        this.tail = 0;
        this.size = 0;
        this.values = (Object[]) ArrayReflection.newInstance(type, initialSize);
    }

    public void addLast(T object) {
        T[] values2 = this.values;
        if (this.size == values2.length) {
            resize(values2.length << 1);
            values2 = this.values;
        }
        int i = this.tail;
        this.tail = i + 1;
        values2[i] = object;
        if (this.tail == values2.length) {
            this.tail = 0;
        }
        this.size++;
    }

    public void addFirst(T object) {
        T[] values2 = this.values;
        if (this.size == values2.length) {
            resize(values2.length << 1);
            values2 = this.values;
        }
        int head2 = this.head - 1;
        if (head2 == -1) {
            head2 = values2.length - 1;
        }
        values2[head2] = object;
        this.head = head2;
        this.size++;
    }

    public void ensureCapacity(int additional) {
        int needed = this.size + additional;
        if (this.values.length < needed) {
            resize(needed);
        }
    }

    /* access modifiers changed from: protected */
    public void resize(int newSize) {
        T[] values2 = this.values;
        int head2 = this.head;
        int tail2 = this.tail;
        T[] newArray = (Object[]) ArrayReflection.newInstance(values2.getClass().getComponentType(), newSize);
        if (head2 < tail2) {
            System.arraycopy(values2, head2, newArray, 0, tail2 - head2);
        } else if (this.size > 0) {
            int rest = values2.length - head2;
            System.arraycopy(values2, head2, newArray, 0, rest);
            System.arraycopy(values2, 0, newArray, rest, tail2);
        }
        this.values = newArray;
        this.head = 0;
        this.tail = this.size;
    }

    public T removeFirst() {
        if (this.size != 0) {
            T[] values2 = this.values;
            int i = this.head;
            T result = values2[i];
            values2[i] = null;
            this.head = i + 1;
            if (this.head == values2.length) {
                this.head = 0;
            }
            this.size--;
            return result;
        }
        throw new NoSuchElementException("Queue is empty.");
    }

    public T removeLast() {
        if (this.size != 0) {
            T[] values2 = this.values;
            int tail2 = this.tail - 1;
            if (tail2 == -1) {
                tail2 = values2.length - 1;
            }
            T result = values2[tail2];
            values2[tail2] = null;
            this.tail = tail2;
            this.size--;
            return result;
        }
        throw new NoSuchElementException("Queue is empty.");
    }

    public int indexOf(T value, boolean identity) {
        if (this.size == 0) {
            return -1;
        }
        T[] values2 = this.values;
        int head2 = this.head;
        int tail2 = this.tail;
        if (identity || value == null) {
            if (head2 < tail2) {
                for (int i = head2; i < tail2; i++) {
                    if (values2[i] == value) {
                        return i - head2;
                    }
                }
            } else {
                int n = values2.length;
                for (int i2 = head2; i2 < n; i2++) {
                    if (values2[i2] == value) {
                        return i2 - head2;
                    }
                }
                for (int i3 = 0; i3 < tail2; i3++) {
                    if (values2[i3] == value) {
                        return (values2.length + i3) - head2;
                    }
                }
            }
        } else if (head2 < tail2) {
            for (int i4 = head2; i4 < tail2; i4++) {
                if (value.equals(values2[i4])) {
                    return i4 - head2;
                }
            }
        } else {
            int n2 = values2.length;
            for (int i5 = head2; i5 < n2; i5++) {
                if (value.equals(values2[i5])) {
                    return i5 - head2;
                }
            }
            for (int i6 = 0; i6 < tail2; i6++) {
                if (value.equals(values2[i6])) {
                    return (values2.length + i6) - head2;
                }
            }
        }
        return -1;
    }

    public boolean removeValue(T value, boolean identity) {
        int index = indexOf(value, identity);
        if (index == -1) {
            return false;
        }
        removeIndex(index);
        return true;
    }

    public T removeIndex(int index) {
        T value;
        if (index < 0) {
            throw new IndexOutOfBoundsException("index can't be < 0: " + index);
        } else if (index < this.size) {
            T[] values2 = this.values;
            int head2 = this.head;
            int tail2 = this.tail;
            int index2 = index + head2;
            if (head2 < tail2) {
                value = values2[index2];
                System.arraycopy(values2, index2 + 1, values2, index2, tail2 - index2);
                values2[tail2] = null;
                this.tail--;
            } else if (index2 >= values2.length) {
                int index3 = index2 - values2.length;
                value = values2[index3];
                System.arraycopy(values2, index3 + 1, values2, index3, tail2 - index3);
                this.tail--;
            } else {
                value = values2[index2];
                System.arraycopy(values2, head2, values2, head2 + 1, index2 - head2);
                values2[head2] = null;
                this.head++;
                if (this.head == values2.length) {
                    this.head = 0;
                }
            }
            this.size--;
            return value;
        } else {
            throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + this.size);
        }
    }

    public boolean notEmpty() {
        return this.size > 0;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public T first() {
        if (this.size != 0) {
            return this.values[this.head];
        }
        throw new NoSuchElementException("Queue is empty.");
    }

    public T last() {
        if (this.size != 0) {
            T[] values2 = this.values;
            int tail2 = this.tail - 1;
            if (tail2 == -1) {
                tail2 = values2.length - 1;
            }
            return values2[tail2];
        }
        throw new NoSuchElementException("Queue is empty.");
    }

    public T get(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("index can't be < 0: " + index);
        } else if (index < this.size) {
            T[] values2 = this.values;
            int i = this.head + index;
            if (i >= values2.length) {
                i -= values2.length;
            }
            return values2[i];
        } else {
            throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + this.size);
        }
    }

    public void clear() {
        if (this.size != 0) {
            T[] values2 = this.values;
            int head2 = this.head;
            int tail2 = this.tail;
            if (head2 < tail2) {
                for (int i = head2; i < tail2; i++) {
                    values2[i] = null;
                }
            } else {
                for (int i2 = head2; i2 < values2.length; i2++) {
                    values2[i2] = null;
                }
                for (int i3 = 0; i3 < tail2; i3++) {
                    values2[i3] = null;
                }
            }
            this.head = 0;
            this.tail = 0;
            this.size = 0;
        }
    }

    public Iterator<T> iterator() {
        if (Collections.allocateIterators) {
            return new QueueIterator(this, true);
        }
        if (this.iterable == null) {
            this.iterable = new QueueIterable(this);
        }
        return this.iterable.iterator();
    }

    public String toString() {
        if (this.size == 0) {
            return "[]";
        }
        T[] values2 = this.values;
        int head2 = this.head;
        int tail2 = this.tail;
        StringBuilder sb = new StringBuilder(64);
        sb.append('[');
        sb.append((Object) values2[head2]);
        for (int i = (head2 + 1) % values2.length; i != tail2; i = (i + 1) % values2.length) {
            sb.append(", ").append((Object) values2[i]);
        }
        sb.append(']');
        return sb.toString();
    }

    public int hashCode() {
        int size2 = this.size;
        T[] values2 = this.values;
        int backingLength = values2.length;
        int index = this.head;
        int hash = size2 + 1;
        for (int s = 0; s < size2; s++) {
            T value = values2[index];
            hash *= 31;
            if (value != null) {
                hash += value.hashCode();
            }
            index++;
            if (index == backingLength) {
                index = 0;
            }
        }
        return hash;
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x0039  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x003c  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x003d A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean equals(java.lang.Object r15) {
        /*
            r14 = this;
            r0 = 1
            if (r14 != r15) goto L_0x0004
            return r0
        L_0x0004:
            r1 = 0
            if (r15 == 0) goto L_0x0041
            boolean r2 = r15 instanceof com.badlogic.gdx.utils.Queue
            if (r2 != 0) goto L_0x000c
            goto L_0x0041
        L_0x000c:
            r2 = r15
            com.badlogic.gdx.utils.Queue r2 = (com.badlogic.gdx.utils.Queue) r2
            int r3 = r14.size
            int r4 = r2.size
            if (r4 == r3) goto L_0x0016
            return r1
        L_0x0016:
            T[] r4 = r14.values
            int r5 = r4.length
            T[] r6 = r2.values
            int r7 = r6.length
            int r8 = r14.head
            int r9 = r2.head
            r10 = 0
        L_0x0021:
            if (r10 >= r3) goto L_0x0040
            r11 = r4[r8]
            r12 = r6[r9]
            if (r11 != 0) goto L_0x002c
            if (r12 != 0) goto L_0x0032
            goto L_0x0033
        L_0x002c:
            boolean r13 = r11.equals(r12)
            if (r13 != 0) goto L_0x0033
        L_0x0032:
            return r1
        L_0x0033:
            int r8 = r8 + 1
            int r9 = r9 + 1
            if (r8 != r5) goto L_0x003a
            r8 = 0
        L_0x003a:
            if (r9 != r7) goto L_0x003d
            r9 = 0
        L_0x003d:
            int r10 = r10 + 1
            goto L_0x0021
        L_0x0040:
            return r0
        L_0x0041:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.utils.Queue.equals(java.lang.Object):boolean");
    }

    public boolean equalsIdentity(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof Queue)) {
            return false;
        }
        Queue<?> q = (Queue) o;
        int size2 = this.size;
        if (q.size != size2) {
            return false;
        }
        Object[] myValues = this.values;
        int myBackingLength = myValues.length;
        Object[] itsValues = q.values;
        int itsBackingLength = itsValues.length;
        int myIndex = this.head;
        int itsIndex = q.head;
        for (int s = 0; s < size2; s++) {
            if (myValues[myIndex] != itsValues[itsIndex]) {
                return false;
            }
            myIndex++;
            itsIndex++;
            if (myIndex == myBackingLength) {
                myIndex = 0;
            }
            if (itsIndex == itsBackingLength) {
                itsIndex = 0;
            }
        }
        return true;
    }

    public static class QueueIterator<T> implements Iterator<T>, Iterable<T> {
        private final boolean allowRemove;
        int index;
        private final Queue<T> queue;
        boolean valid;

        public QueueIterator(Queue<T> queue2) {
            this(queue2, true);
        }

        public QueueIterator(Queue<T> queue2, boolean allowRemove2) {
            this.valid = true;
            this.queue = queue2;
            this.allowRemove = allowRemove2;
        }

        public boolean hasNext() {
            if (this.valid) {
                return this.index < this.queue.size;
            }
            throw new GdxRuntimeException("#iterator() cannot be used nested.");
        }

        public T next() {
            if (this.index >= this.queue.size) {
                throw new NoSuchElementException(String.valueOf(this.index));
            } else if (this.valid) {
                Queue<T> queue2 = this.queue;
                int i = this.index;
                this.index = i + 1;
                return queue2.get(i);
            } else {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
        }

        public void remove() {
            if (this.allowRemove) {
                this.index--;
                this.queue.removeIndex(this.index);
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

    public static class QueueIterable<T> implements Iterable<T> {
        private final boolean allowRemove;
        private QueueIterator iterator1;
        private QueueIterator iterator2;
        private final Queue<T> queue;

        public QueueIterable(Queue<T> queue2) {
            this(queue2, true);
        }

        public QueueIterable(Queue<T> queue2, boolean allowRemove2) {
            this.queue = queue2;
            this.allowRemove = allowRemove2;
        }

        public Iterator<T> iterator() {
            if (Collections.allocateIterators) {
                return new QueueIterator(this.queue, this.allowRemove);
            }
            if (this.iterator1 == null) {
                this.iterator1 = new QueueIterator(this.queue, this.allowRemove);
                this.iterator2 = new QueueIterator(this.queue, this.allowRemove);
            }
            if (!this.iterator1.valid) {
                QueueIterator queueIterator = this.iterator1;
                queueIterator.index = 0;
                queueIterator.valid = true;
                this.iterator2.valid = false;
                return queueIterator;
            }
            QueueIterator queueIterator2 = this.iterator2;
            queueIterator2.index = 0;
            queueIterator2.valid = true;
            this.iterator1.valid = false;
            return queueIterator2;
        }
    }
}
