package com.badlogic.gdx.math;

import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class Bezier<T extends Vector<T>> implements Path<T> {
    public Array<T> points = new Array<>();
    private T tmp;
    private T tmp2;
    private T tmp3;

    public static <T extends Vector<T>> T linear(T out, float t, T p0, T p1, T tmp4) {
        return out.set(p0).scl(1.0f - t).add(tmp4.set(p1).scl(t));
    }

    public static <T extends Vector<T>> T linear_derivative(T out, float t, T p0, T p1, T t2) {
        return out.set(p1).sub(p0);
    }

    public static <T extends Vector<T>> T quadratic(T out, float t, T p0, T p1, T p2, T tmp4) {
        float dt = 1.0f - t;
        return out.set(p0).scl(dt * dt).add(tmp4.set(p1).scl(2.0f * dt * t)).add(tmp4.set(p2).scl(t * t));
    }

    public static <T extends Vector<T>> T quadratic_derivative(T out, float t, T p0, T p1, T p2, T tmp4) {
        float f = 1.0f - t;
        return out.set(p1).sub(p0).scl(2.0f).scl(1.0f - t).add(tmp4.set(p2).sub(p1).scl(t).scl(2.0f));
    }

    public static <T extends Vector<T>> T cubic(T out, float t, T p0, T p1, T p2, T p3, T tmp4) {
        float dt = 1.0f - t;
        float dt2 = dt * dt;
        float t2 = t * t;
        return out.set(p0).scl(dt2 * dt).add(tmp4.set(p1).scl(dt2 * 3.0f * t)).add(tmp4.set(p2).scl(3.0f * dt * t2)).add(tmp4.set(p3).scl(t2 * t));
    }

    public static <T extends Vector<T>> T cubic_derivative(T out, float t, T p0, T p1, T p2, T p3, T tmp4) {
        float dt = 1.0f - t;
        return out.set(p1).sub(p0).scl(dt * dt * 3.0f).add(tmp4.set(p2).sub(p1).scl(dt * t * 6.0f)).add(tmp4.set(p3).sub(p2).scl(3.0f * t * t));
    }

    public Bezier() {
    }

    public Bezier(T... points2) {
        set(points2);
    }

    public Bezier(T[] points2, int offset, int length) {
        set(points2, offset, length);
    }

    public Bezier(Array<T> points2, int offset, int length) {
        set(points2, offset, length);
    }

    public Bezier set(T... points2) {
        return set(points2, 0, points2.length);
    }

    public Bezier set(T[] points2, int offset, int length) {
        if (length < 2 || length > 4) {
            throw new GdxRuntimeException("Only first, second and third degree Bezier curves are supported.");
        }
        if (this.tmp == null) {
            this.tmp = points2[0].cpy();
        }
        if (this.tmp2 == null) {
            this.tmp2 = points2[0].cpy();
        }
        if (this.tmp3 == null) {
            this.tmp3 = points2[0].cpy();
        }
        this.points.clear();
        this.points.addAll(points2, offset, length);
        return this;
    }

    public Bezier set(Array<T> points2, int offset, int length) {
        if (length < 2 || length > 4) {
            throw new GdxRuntimeException("Only first, second and third degree Bezier curves are supported.");
        }
        if (this.tmp == null) {
            this.tmp = ((Vector) points2.get(0)).cpy();
        }
        if (this.tmp2 == null) {
            this.tmp2 = ((Vector) points2.get(0)).cpy();
        }
        if (this.tmp3 == null) {
            this.tmp3 = ((Vector) points2.get(0)).cpy();
        }
        this.points.clear();
        this.points.addAll(points2, offset, length);
        return this;
    }

    public T valueAt(T out, float t) {
        int n = this.points.size;
        if (n == 2) {
            linear(out, t, (Vector) this.points.get(0), (Vector) this.points.get(1), this.tmp);
        } else if (n == 3) {
            quadratic(out, t, (Vector) this.points.get(0), (Vector) this.points.get(1), (Vector) this.points.get(2), this.tmp);
        } else if (n == 4) {
            cubic(out, t, (Vector) this.points.get(0), (Vector) this.points.get(1), (Vector) this.points.get(2), (Vector) this.points.get(3), this.tmp);
        }
        return out;
    }

    public T derivativeAt(T out, float t) {
        int n = this.points.size;
        if (n == 2) {
            linear_derivative(out, t, (Vector) this.points.get(0), (Vector) this.points.get(1), this.tmp);
        } else if (n == 3) {
            quadratic_derivative(out, t, (Vector) this.points.get(0), (Vector) this.points.get(1), (Vector) this.points.get(2), this.tmp);
        } else if (n == 4) {
            cubic_derivative(out, t, (Vector) this.points.get(0), (Vector) this.points.get(1), (Vector) this.points.get(2), (Vector) this.points.get(3), this.tmp);
        }
        return out;
    }

    public float approximate(T v) {
        T p1 = (Vector) this.points.get(0);
        Array<T> array = this.points;
        T p2 = (Vector) array.get(array.size - 1);
        T p3 = v;
        float l1Sqr = p1.dst2(p2);
        float l2Sqr = p3.dst2(p2);
        float l3Sqr = p3.dst2(p1);
        float l1 = (float) Math.sqrt((double) l1Sqr);
        return MathUtils.clamp((l1 - (((l2Sqr + l1Sqr) - l3Sqr) / (2.0f * l1))) / l1, 0.0f, 1.0f);
    }

    public float locate(T v) {
        return approximate(v);
    }

    public float approxLength(int samples) {
        float tempLength = 0.0f;
        for (int i = 0; i < samples; i++) {
            this.tmp2.set(this.tmp3);
            valueAt(this.tmp3, ((float) i) / (((float) samples) - 1.0f));
            if (i > 0) {
                tempLength += this.tmp2.dst(this.tmp3);
            }
        }
        return tempLength;
    }
}
