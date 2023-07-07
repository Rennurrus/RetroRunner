package com.badlogic.gdx.math;

import com.badlogic.gdx.utils.Array;
import java.util.Iterator;

public class CumulativeDistribution<T> {
    private Array<CumulativeDistribution<T>.CumulativeValue> values = new Array<>(false, 10, CumulativeValue.class);

    public class CumulativeValue {
        public float frequency;
        public float interval;
        public T value;

        public CumulativeValue(T value2, float frequency2, float interval2) {
            this.value = value2;
            this.frequency = frequency2;
            this.interval = interval2;
        }
    }

    public void add(T value, float intervalSize) {
        this.values.add(new CumulativeValue(value, 0.0f, intervalSize));
    }

    public void add(T value) {
        this.values.add(new CumulativeValue(value, 0.0f, 0.0f));
    }

    public void generate() {
        float sum = 0.0f;
        for (int i = 0; i < this.values.size; i++) {
            sum += ((CumulativeValue[]) this.values.items)[i].interval;
            ((CumulativeValue[]) this.values.items)[i].frequency = sum;
        }
    }

    public void generateNormalized() {
        float sum = 0.0f;
        for (int i = 0; i < this.values.size; i++) {
            sum += ((CumulativeValue[]) this.values.items)[i].interval;
        }
        float intervalSum = 0.0f;
        for (int i2 = 0; i2 < this.values.size; i2++) {
            intervalSum += ((CumulativeValue[]) this.values.items)[i2].interval / sum;
            ((CumulativeValue[]) this.values.items)[i2].frequency = intervalSum;
        }
    }

    public void generateUniform() {
        float freq = 1.0f / ((float) this.values.size);
        for (int i = 0; i < this.values.size; i++) {
            ((CumulativeValue[]) this.values.items)[i].interval = freq;
            ((CumulativeValue[]) this.values.items)[i].frequency = ((float) (i + 1)) * freq;
        }
    }

    public T value(float probability) {
        int imax = this.values.size - 1;
        int imin = 0;
        while (imin <= imax) {
            int imid = ((imax - imin) / 2) + imin;
            CumulativeDistribution<T>.CumulativeValue value = ((CumulativeValue[]) this.values.items)[imid];
            if (probability >= value.frequency) {
                if (probability <= value.frequency) {
                    break;
                }
                imin = imid + 1;
            } else {
                imax = imid - 1;
            }
        }
        return ((CumulativeValue[]) this.values.items)[imin].value;
    }

    public T value() {
        return value(MathUtils.random());
    }

    public int size() {
        return this.values.size;
    }

    public float getInterval(int index) {
        return ((CumulativeValue[]) this.values.items)[index].interval;
    }

    public T getValue(int index) {
        return ((CumulativeValue[]) this.values.items)[index].value;
    }

    public void setInterval(T obj, float intervalSize) {
        Iterator<CumulativeDistribution<T>.CumulativeValue> it = this.values.iterator();
        while (it.hasNext()) {
            CumulativeDistribution<T>.CumulativeValue value = it.next();
            if (value.value == obj) {
                value.interval = intervalSize;
                return;
            }
        }
    }

    public void setInterval(int index, float intervalSize) {
        ((CumulativeValue[]) this.values.items)[index].interval = intervalSize;
    }

    public void clear() {
        this.values.clear();
    }
}
