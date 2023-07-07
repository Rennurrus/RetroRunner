package com.badlogic.gdx.utils;

import java.util.Iterator;

public interface Predicate<T> {
    boolean evaluate(T t);

    public static class PredicateIterator<T> implements Iterator<T> {
        public boolean end;
        public Iterator<T> iterator;
        public T next;
        public boolean peeked;
        public Predicate<T> predicate;

        public PredicateIterator(Iterable<T> iterable, Predicate<T> predicate2) {
            this(iterable.iterator(), predicate2);
        }

        public PredicateIterator(Iterator<T> iterator2, Predicate<T> predicate2) {
            this.end = false;
            this.peeked = false;
            this.next = null;
            set(iterator2, predicate2);
        }

        public void set(Iterable<T> iterable, Predicate<T> predicate2) {
            set(iterable.iterator(), predicate2);
        }

        public void set(Iterator<T> iterator2, Predicate<T> predicate2) {
            this.iterator = iterator2;
            this.predicate = predicate2;
            this.peeked = false;
            this.end = false;
            this.next = null;
        }

        public boolean hasNext() {
            if (this.end) {
                return false;
            }
            if (this.next != null) {
                return true;
            }
            this.peeked = true;
            while (this.iterator.hasNext()) {
                T n = this.iterator.next();
                if (this.predicate.evaluate(n)) {
                    this.next = n;
                    return true;
                }
            }
            this.end = true;
            return false;
        }

        public T next() {
            if (this.next == null && !hasNext()) {
                return null;
            }
            T result = this.next;
            this.next = null;
            this.peeked = false;
            return result;
        }

        public void remove() {
            if (!this.peeked) {
                this.iterator.remove();
                return;
            }
            throw new GdxRuntimeException("Cannot remove between a call to hasNext() and next().");
        }
    }

    public static class PredicateIterable<T> implements Iterable<T> {
        public Iterable<T> iterable;
        public PredicateIterator<T> iterator = null;
        public Predicate<T> predicate;

        public PredicateIterable(Iterable<T> iterable2, Predicate<T> predicate2) {
            set(iterable2, predicate2);
        }

        public void set(Iterable<T> iterable2, Predicate<T> predicate2) {
            this.iterable = iterable2;
            this.predicate = predicate2;
        }

        public Iterator<T> iterator() {
            if (Collections.allocateIterators) {
                return new PredicateIterator(this.iterable.iterator(), this.predicate);
            }
            PredicateIterator<T> predicateIterator = this.iterator;
            if (predicateIterator == null) {
                this.iterator = new PredicateIterator<>(this.iterable.iterator(), this.predicate);
            } else {
                predicateIterator.set(this.iterable.iterator(), this.predicate);
            }
            return this.iterator;
        }
    }
}
