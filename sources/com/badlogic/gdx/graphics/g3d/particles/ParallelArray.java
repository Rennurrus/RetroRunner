package com.badlogic.gdx.graphics.g3d.particles;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.reflect.ArrayReflection;
import java.util.Iterator;

public class ParallelArray {
    Array<Channel> arrays = new Array<>(false, 2, Channel.class);
    public int capacity;
    public int size;

    public interface ChannelInitializer<T extends Channel> {
        void init(T t);
    }

    public static class ChannelDescriptor {
        public int count;
        public int id;
        public Class<?> type;

        public ChannelDescriptor(int id2, Class<?> type2, int count2) {
            this.id = id2;
            this.type = type2;
            this.count = count2;
        }
    }

    public abstract class Channel {
        public Object data;
        public int id;
        public int strideSize;

        public abstract void add(int i, Object... objArr);

        /* access modifiers changed from: protected */
        public abstract void setCapacity(int i);

        public abstract void swap(int i, int i2);

        public Channel(int id2, Object data2, int strideSize2) {
            this.id = id2;
            this.strideSize = strideSize2;
            this.data = data2;
        }
    }

    public class FloatChannel extends Channel {
        public float[] data = ((float[]) this.data);

        public FloatChannel(int id, int strideSize, int size) {
            super(id, new float[(size * strideSize)], strideSize);
        }

        public void add(int index, Object... objects) {
            int i = this.strideSize * ParallelArray.this.size;
            int c = this.strideSize + i;
            int k = 0;
            while (i < c) {
                this.data[i] = objects[k].floatValue();
                i++;
                k++;
            }
        }

        public void swap(int i, int k) {
            int i2 = this.strideSize * i;
            int k2 = this.strideSize * k;
            int c = this.strideSize + i2;
            while (i2 < c) {
                float[] fArr = this.data;
                float t = fArr[i2];
                fArr[i2] = fArr[k2];
                fArr[k2] = t;
                i2++;
                k2++;
            }
        }

        public void setCapacity(int requiredCapacity) {
            float[] newData = new float[(this.strideSize * requiredCapacity)];
            float[] fArr = this.data;
            System.arraycopy(fArr, 0, newData, 0, Math.min(fArr.length, newData.length));
            this.data = newData;
            this.data = newData;
        }
    }

    public class IntChannel extends Channel {
        public int[] data = ((int[]) this.data);

        public IntChannel(int id, int strideSize, int size) {
            super(id, new int[(size * strideSize)], strideSize);
        }

        public void add(int index, Object... objects) {
            int i = this.strideSize * ParallelArray.this.size;
            int c = this.strideSize + i;
            int k = 0;
            while (i < c) {
                this.data[i] = objects[k].intValue();
                i++;
                k++;
            }
        }

        public void swap(int i, int k) {
            int i2 = this.strideSize * i;
            int k2 = this.strideSize * k;
            int c = this.strideSize + i2;
            while (i2 < c) {
                int[] iArr = this.data;
                int t = iArr[i2];
                iArr[i2] = iArr[k2];
                iArr[k2] = t;
                i2++;
                k2++;
            }
        }

        public void setCapacity(int requiredCapacity) {
            int[] newData = new int[(this.strideSize * requiredCapacity)];
            int[] iArr = this.data;
            System.arraycopy(iArr, 0, newData, 0, Math.min(iArr.length, newData.length));
            this.data = newData;
            this.data = newData;
        }
    }

    public class ObjectChannel<T> extends Channel {
        Class<T> componentType;
        public T[] data = ((Object[]) this.data);

        public ObjectChannel(int id, int strideSize, int size, Class<T> type) {
            super(id, ArrayReflection.newInstance(type, size * strideSize), strideSize);
            this.componentType = type;
        }

        public void add(int index, Object... objects) {
            int i = this.strideSize * ParallelArray.this.size;
            int c = this.strideSize + i;
            int k = 0;
            while (i < c) {
                this.data[i] = objects[k];
                i++;
                k++;
            }
        }

        public void swap(int i, int k) {
            int i2 = this.strideSize * i;
            int k2 = this.strideSize * k;
            int c = this.strideSize + i2;
            while (i2 < c) {
                T[] tArr = this.data;
                T t = tArr[i2];
                tArr[i2] = tArr[k2];
                tArr[k2] = t;
                i2++;
                k2++;
            }
        }

        public void setCapacity(int requiredCapacity) {
            T[] newData = (Object[]) ArrayReflection.newInstance(this.componentType, this.strideSize * requiredCapacity);
            T[] tArr = this.data;
            System.arraycopy(tArr, 0, newData, 0, Math.min(tArr.length, newData.length));
            this.data = newData;
            this.data = newData;
        }
    }

    public ParallelArray(int capacity2) {
        this.capacity = capacity2;
        this.size = 0;
    }

    public <T extends Channel> T addChannel(ChannelDescriptor channelDescriptor) {
        return addChannel(channelDescriptor, (ChannelInitializer) null);
    }

    public <T extends Channel> T addChannel(ChannelDescriptor channelDescriptor, ChannelInitializer<T> initializer) {
        T channel = getChannel(channelDescriptor);
        if (channel == null) {
            channel = allocateChannel(channelDescriptor);
            if (initializer != null) {
                initializer.init(channel);
            }
            this.arrays.add(channel);
        }
        return channel;
    }

    private <T extends Channel> T allocateChannel(ChannelDescriptor channelDescriptor) {
        if (channelDescriptor.type == Float.TYPE) {
            return new FloatChannel(channelDescriptor.id, channelDescriptor.count, this.capacity);
        }
        if (channelDescriptor.type == Integer.TYPE) {
            return new IntChannel(channelDescriptor.id, channelDescriptor.count, this.capacity);
        }
        return new ObjectChannel(channelDescriptor.id, channelDescriptor.count, this.capacity, channelDescriptor.type);
    }

    public <T> void removeArray(int id) {
        this.arrays.removeIndex(findIndex(id));
    }

    private int findIndex(int id) {
        for (int i = 0; i < this.arrays.size; i++) {
            if (((Channel[]) this.arrays.items)[i].id == id) {
                return i;
            }
        }
        return -1;
    }

    public void addElement(Object... values) {
        if (this.size != this.capacity) {
            int k = 0;
            Iterator<Channel> it = this.arrays.iterator();
            while (it.hasNext()) {
                Channel strideArray = it.next();
                strideArray.add(k, values);
                k += strideArray.strideSize;
            }
            this.size++;
            return;
        }
        throw new GdxRuntimeException("Capacity reached, cannot add other elements");
    }

    public void removeElement(int index) {
        int last = this.size - 1;
        Iterator<Channel> it = this.arrays.iterator();
        while (it.hasNext()) {
            it.next().swap(index, last);
        }
        this.size = last;
    }

    public <T extends Channel> T getChannel(ChannelDescriptor descriptor) {
        Iterator<Channel> it = this.arrays.iterator();
        while (it.hasNext()) {
            Channel array = it.next();
            if (array.id == descriptor.id) {
                return array;
            }
        }
        return null;
    }

    public void clear() {
        this.arrays.clear();
        this.size = 0;
    }

    public void setCapacity(int requiredCapacity) {
        if (this.capacity != requiredCapacity) {
            Iterator<Channel> it = this.arrays.iterator();
            while (it.hasNext()) {
                it.next().setCapacity(requiredCapacity);
            }
            this.capacity = requiredCapacity;
        }
    }
}
