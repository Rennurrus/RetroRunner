package com.badlogic.gdx.utils;

public class SortedIntList<E> implements Iterable<Node<E>> {
    Node<E> first;
    private SortedIntList<E>.Iterator iterator;
    private NodePool<E> nodePool = new NodePool<>();
    int size = 0;

    public static class Node<E> {
        public int index;
        protected Node<E> n;
        protected Node<E> p;
        public E value;
    }

    public E insert(int index, E value) {
        if (this.first != null) {
            Node<E> c = this.first;
            while (c.n != null && c.n.index <= index) {
                c = c.n;
            }
            if (index > c.index) {
                c.n = this.nodePool.obtain(c, c.n, value, index);
                if (c.n.n != null) {
                    c.n.n.p = c.n;
                }
                this.size++;
            } else if (index < c.index) {
                Node<E> newFirst = this.nodePool.obtain((Node) null, this.first, value, index);
                this.first.p = newFirst;
                this.first = newFirst;
                this.size++;
            } else {
                c.value = value;
            }
        } else {
            this.first = this.nodePool.obtain((Node) null, (Node) null, value, index);
            this.size++;
        }
        return null;
    }

    public E get(int index) {
        if (this.first == null) {
            return null;
        }
        Node<E> c = this.first;
        while (c.n != null && c.index < index) {
            c = c.n;
        }
        if (c.index == index) {
            return c.value;
        }
        return null;
    }

    public void clear() {
        while (true) {
            Node<E> node = this.first;
            if (node != null) {
                this.nodePool.free(node);
                this.first = this.first.n;
            } else {
                this.size = 0;
                return;
            }
        }
    }

    public int size() {
        return this.size;
    }

    public boolean notEmpty() {
        return this.size > 0;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public java.util.Iterator<Node<E>> iterator() {
        if (this.iterator == null) {
            this.iterator = new Iterator();
        }
        return this.iterator.reset();
    }

    class Iterator implements java.util.Iterator<Node<E>> {
        private Node<E> position;
        private Node<E> previousPosition;

        Iterator() {
        }

        public boolean hasNext() {
            return this.position != null;
        }

        public Node<E> next() {
            Node<E> node = this.position;
            this.previousPosition = node;
            this.position = node.n;
            return this.previousPosition;
        }

        public void remove() {
            Node<E> node = this.previousPosition;
            if (node != null) {
                if (node == SortedIntList.this.first) {
                    SortedIntList.this.first = this.position;
                } else {
                    Node<E> node2 = this.previousPosition.p;
                    Node<E> node3 = this.position;
                    node2.n = node3;
                    if (node3 != null) {
                        node3.p = this.previousPosition.p;
                    }
                }
                SortedIntList sortedIntList = SortedIntList.this;
                sortedIntList.size--;
            }
        }

        public SortedIntList<E>.Iterator reset() {
            this.position = SortedIntList.this.first;
            this.previousPosition = null;
            return this;
        }
    }

    static class NodePool<E> extends Pool<Node<E>> {
        NodePool() {
        }

        /* access modifiers changed from: protected */
        public Node<E> newObject() {
            return new Node<>();
        }

        public Node<E> obtain(Node<E> p, Node<E> n, E value, int index) {
            Node<E> newNode = (Node) super.obtain();
            newNode.p = p;
            newNode.n = n;
            newNode.value = value;
            newNode.index = index;
            return newNode;
        }
    }
}
