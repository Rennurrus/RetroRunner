package com.badlogic.gdx.graphics;

import com.badlogic.gdx.utils.Collections;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class VertexAttributes implements Iterable<VertexAttribute>, Comparable<VertexAttributes> {
    private final VertexAttribute[] attributes;
    private ReadonlyIterable<VertexAttribute> iterable;
    private long mask = -1;
    public final int vertexSize;

    public static final class Usage {
        public static final int BiNormal = 256;
        public static final int BoneWeight = 64;
        public static final int ColorPacked = 4;
        public static final int ColorUnpacked = 2;
        public static final int Generic = 32;
        public static final int Normal = 8;
        public static final int Position = 1;
        public static final int Tangent = 128;
        public static final int TextureCoordinates = 16;
    }

    public VertexAttributes(VertexAttribute... attributes2) {
        if (attributes2.length != 0) {
            VertexAttribute[] list = new VertexAttribute[attributes2.length];
            for (int i = 0; i < attributes2.length; i++) {
                list[i] = attributes2[i];
            }
            this.attributes = list;
            this.vertexSize = calculateOffsets();
            return;
        }
        throw new IllegalArgumentException("attributes must be >= 1");
    }

    public int getOffset(int usage, int defaultIfNotFound) {
        VertexAttribute vertexAttribute = findByUsage(usage);
        if (vertexAttribute == null) {
            return defaultIfNotFound;
        }
        return vertexAttribute.offset / 4;
    }

    public int getOffset(int usage) {
        return getOffset(usage, 0);
    }

    public VertexAttribute findByUsage(int usage) {
        int len = size();
        for (int i = 0; i < len; i++) {
            if (get(i).usage == usage) {
                return get(i);
            }
        }
        return null;
    }

    private int calculateOffsets() {
        int count = 0;
        int i = 0;
        while (true) {
            VertexAttribute[] vertexAttributeArr = this.attributes;
            if (i >= vertexAttributeArr.length) {
                return count;
            }
            VertexAttribute attribute = vertexAttributeArr[i];
            attribute.offset = count;
            count += attribute.getSizeInBytes();
            i++;
        }
    }

    public int size() {
        return this.attributes.length;
    }

    public VertexAttribute get(int index) {
        return this.attributes[index];
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < this.attributes.length; i++) {
            builder.append("(");
            builder.append(this.attributes[i].alias);
            builder.append(", ");
            builder.append(this.attributes[i].usage);
            builder.append(", ");
            builder.append(this.attributes[i].numComponents);
            builder.append(", ");
            builder.append(this.attributes[i].offset);
            builder.append(")");
            builder.append("\n");
        }
        builder.append("]");
        return builder.toString();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof VertexAttributes)) {
            return false;
        }
        VertexAttributes other = (VertexAttributes) obj;
        if (this.attributes.length != other.attributes.length) {
            return false;
        }
        int i = 0;
        while (true) {
            VertexAttribute[] vertexAttributeArr = this.attributes;
            if (i >= vertexAttributeArr.length) {
                return true;
            }
            if (!vertexAttributeArr[i].equals(other.attributes[i])) {
                return false;
            }
            i++;
        }
    }

    public int hashCode() {
        long result = (long) (this.attributes.length * 61);
        int i = 0;
        while (true) {
            VertexAttribute[] vertexAttributeArr = this.attributes;
            if (i >= vertexAttributeArr.length) {
                return (int) ((result >> 32) ^ result);
            }
            result = (61 * result) + ((long) vertexAttributeArr[i].hashCode());
            i++;
        }
    }

    public long getMask() {
        if (this.mask == -1) {
            long result = 0;
            int i = 0;
            while (true) {
                VertexAttribute[] vertexAttributeArr = this.attributes;
                if (i >= vertexAttributeArr.length) {
                    break;
                }
                result |= (long) vertexAttributeArr[i].usage;
                i++;
            }
            this.mask = result;
        }
        return this.mask;
    }

    public long getMaskWithSizePacked() {
        return getMask() | (((long) this.attributes.length) << 32);
    }

    public int compareTo(VertexAttributes o) {
        VertexAttribute[] vertexAttributeArr = this.attributes;
        int length = vertexAttributeArr.length;
        VertexAttribute[] vertexAttributeArr2 = o.attributes;
        if (length != vertexAttributeArr2.length) {
            return vertexAttributeArr.length - vertexAttributeArr2.length;
        }
        long m1 = getMask();
        long m2 = o.getMask();
        if (m1 != m2) {
            return m1 < m2 ? -1 : 1;
        }
        int i = this.attributes.length - 1;
        while (i >= 0) {
            VertexAttribute va0 = this.attributes[i];
            VertexAttribute va1 = o.attributes[i];
            if (va0.usage != va1.usage) {
                return va0.usage - va1.usage;
            }
            if (va0.unit != va1.unit) {
                return va0.unit - va1.unit;
            }
            if (va0.numComponents != va1.numComponents) {
                return va0.numComponents - va1.numComponents;
            }
            if (va0.normalized != va1.normalized) {
                if (va0.normalized) {
                    return 1;
                }
                return -1;
            } else if (va0.type != va1.type) {
                return va0.type - va1.type;
            } else {
                i--;
            }
        }
        return 0;
    }

    public Iterator<VertexAttribute> iterator() {
        if (this.iterable == null) {
            this.iterable = new ReadonlyIterable<>(this.attributes);
        }
        return this.iterable.iterator();
    }

    private static class ReadonlyIterator<T> implements Iterator<T>, Iterable<T> {
        private final T[] array;
        int index;
        boolean valid = true;

        public ReadonlyIterator(T[] array2) {
            this.array = array2;
        }

        public boolean hasNext() {
            if (this.valid) {
                return this.index < this.array.length;
            }
            throw new GdxRuntimeException("#iterator() cannot be used nested.");
        }

        public T next() {
            int i = this.index;
            T[] tArr = this.array;
            if (i >= tArr.length) {
                throw new NoSuchElementException(String.valueOf(i));
            } else if (this.valid) {
                this.index = i + 1;
                return tArr[i];
            } else {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
        }

        public void remove() {
            throw new GdxRuntimeException("Remove not allowed.");
        }

        public void reset() {
            this.index = 0;
        }

        public Iterator<T> iterator() {
            return this;
        }
    }

    private static class ReadonlyIterable<T> implements Iterable<T> {
        private final T[] array;
        private ReadonlyIterator iterator1;
        private ReadonlyIterator iterator2;

        public ReadonlyIterable(T[] array2) {
            this.array = array2;
        }

        public Iterator<T> iterator() {
            if (Collections.allocateIterators) {
                return new ReadonlyIterator(this.array);
            }
            if (this.iterator1 == null) {
                this.iterator1 = new ReadonlyIterator(this.array);
                this.iterator2 = new ReadonlyIterator(this.array);
            }
            if (!this.iterator1.valid) {
                ReadonlyIterator readonlyIterator = this.iterator1;
                readonlyIterator.index = 0;
                readonlyIterator.valid = true;
                this.iterator2.valid = false;
                return readonlyIterator;
            }
            ReadonlyIterator readonlyIterator2 = this.iterator2;
            readonlyIterator2.index = 0;
            readonlyIterator2.valid = true;
            this.iterator1.valid = false;
            return readonlyIterator2;
        }
    }
}
