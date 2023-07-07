package com.badlogic.gdx.math;

import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.utils.Array;

public class BSpline<T extends Vector<T>> implements Path<T> {
    private static final float d6 = 0.16666667f;
    public boolean continuous;
    public T[] controlPoints;
    public int degree;
    public Array<T> knots;
    public int spanCount;
    private T tmp;
    private T tmp2;
    private T tmp3;

    public static <T extends Vector<T>> T cubic(T out, float t, T[] points, boolean continuous2, T tmp4) {
        int n = continuous2 ? points.length : points.length - 3;
        float u = ((float) n) * t;
        int i = t >= 1.0f ? n - 1 : (int) u;
        return cubic(out, i, u - ((float) i), points, continuous2, tmp4);
    }

    public static <T extends Vector<T>> T cubic_derivative(T out, float t, T[] points, boolean continuous2, T tmp4) {
        int n = continuous2 ? points.length : points.length - 3;
        float u = ((float) n) * t;
        int i = t >= 1.0f ? n - 1 : (int) u;
        return cubic(out, i, u - ((float) i), points, continuous2, tmp4);
    }

    public static <T extends Vector<T>> T cubic(T out, int i, float u, T[] points, boolean continuous2, T tmp4) {
        int n = points.length;
        float dt = 1.0f - u;
        float t2 = u * u;
        float t3 = t2 * u;
        out.set(points[i]).scl((((t3 * 3.0f) - (6.0f * t2)) + 4.0f) * d6);
        if (continuous2 || i > 0) {
            out.add(tmp4.set(points[((n + i) - 1) % n]).scl(dt * dt * dt * d6));
        }
        if (continuous2 || i < n - 1) {
            out.add(tmp4.set(points[(i + 1) % n]).scl(((-3.0f * t3) + (t2 * 3.0f) + (3.0f * u) + 1.0f) * d6));
        }
        if (continuous2 || i < n - 2) {
            out.add(tmp4.set(points[(i + 2) % n]).scl(d6 * t3));
        }
        return out;
    }

    public static <T extends Vector<T>> T cubic_derivative(T out, int i, float u, T[] points, boolean continuous2, T tmp4) {
        int n = points.length;
        float dt = 1.0f - u;
        float t2 = u * u;
        float f = t2 * u;
        out.set(points[i]).scl((1.5f * t2) - (2.0f * u));
        if (continuous2 || i > 0) {
            out.add(tmp4.set(points[((n + i) - 1) % n]).scl(-0.5f * dt * dt));
        }
        if (continuous2 || i < n - 1) {
            out.add(tmp4.set(points[(i + 1) % n]).scl((-1.5f * t2) + u + 0.5f));
        }
        if (continuous2 || i < n - 2) {
            out.add(tmp4.set(points[(i + 2) % n]).scl(0.5f * t2));
        }
        return out;
    }

    public static <T extends Vector<T>> T calculate(T out, float t, T[] points, int degree2, boolean continuous2, T tmp4) {
        int n = continuous2 ? points.length : points.length - degree2;
        float u = ((float) n) * t;
        int i = t >= 1.0f ? n - 1 : (int) u;
        return calculate(out, i, u - ((float) i), points, degree2, continuous2, tmp4);
    }

    public static <T extends Vector<T>> T derivative(T out, float t, T[] points, int degree2, boolean continuous2, T tmp4) {
        int n = continuous2 ? points.length : points.length - degree2;
        float u = ((float) n) * t;
        int i = t >= 1.0f ? n - 1 : (int) u;
        return derivative(out, i, u - ((float) i), points, degree2, continuous2, tmp4);
    }

    public static <T extends Vector<T>> T calculate(T out, int i, float u, T[] points, int degree2, boolean continuous2, T tmp4) {
        if (degree2 != 3) {
            return out;
        }
        return cubic(out, i, u, points, continuous2, tmp4);
    }

    public static <T extends Vector<T>> T derivative(T out, int i, float u, T[] points, int degree2, boolean continuous2, T tmp4) {
        if (degree2 != 3) {
            return out;
        }
        return cubic_derivative(out, i, u, points, continuous2, tmp4);
    }

    public BSpline() {
    }

    public BSpline(T[] controlPoints2, int degree2, boolean continuous2) {
        set(controlPoints2, degree2, continuous2);
    }

    public BSpline set(T[] controlPoints2, int degree2, boolean continuous2) {
        if (this.tmp == null) {
            this.tmp = controlPoints2[0].cpy();
        }
        if (this.tmp2 == null) {
            this.tmp2 = controlPoints2[0].cpy();
        }
        if (this.tmp3 == null) {
            this.tmp3 = controlPoints2[0].cpy();
        }
        this.controlPoints = controlPoints2;
        this.degree = degree2;
        this.continuous = continuous2;
        this.spanCount = continuous2 ? controlPoints2.length : controlPoints2.length - degree2;
        Array<T> array = this.knots;
        if (array == null) {
            this.knots = new Array<>(this.spanCount);
        } else {
            array.clear();
            this.knots.ensureCapacity(this.spanCount);
        }
        for (int i = 0; i < this.spanCount; i++) {
            this.knots.add(calculate(controlPoints2[0].cpy(), continuous2 ? i : (int) (((float) i) + (((float) degree2) * 0.5f)), 0.0f, controlPoints2, degree2, continuous2, this.tmp));
        }
        return this;
    }

    public T valueAt(T out, float t) {
        int n = this.spanCount;
        float u = ((float) n) * t;
        int i = t >= 1.0f ? n - 1 : (int) u;
        return valueAt(out, i, u - ((float) i));
    }

    public T valueAt(T out, int span, float u) {
        return calculate(out, this.continuous ? span : ((int) (((float) this.degree) * 0.5f)) + span, u, this.controlPoints, this.degree, this.continuous, this.tmp);
    }

    public T derivativeAt(T out, float t) {
        int n = this.spanCount;
        float u = ((float) n) * t;
        int i = t >= 1.0f ? n - 1 : (int) u;
        return derivativeAt(out, i, u - ((float) i));
    }

    public T derivativeAt(T out, int span, float u) {
        return derivative(out, this.continuous ? span : ((int) (((float) this.degree) * 0.5f)) + span, u, this.controlPoints, this.degree, this.continuous, this.tmp);
    }

    public int nearest(T in) {
        return nearest(in, 0, this.spanCount);
    }

    public int nearest(T in, int start, int count) {
        while (start < 0) {
            start += this.spanCount;
        }
        int result = start % this.spanCount;
        float dst = in.dst2((Vector) this.knots.get(result));
        for (int i = 1; i < count; i++) {
            int idx = (start + i) % this.spanCount;
            float d = in.dst2((Vector) this.knots.get(idx));
            if (d < dst) {
                dst = d;
                result = idx;
            }
        }
        return result;
    }

    public float approximate(T v) {
        return approximate(v, nearest(v));
    }

    public float approximate(T in, int start, int count) {
        return approximate(in, nearest(in, start, count));
    }

    public float approximate(T in, int near) {
        T P3;
        T P2;
        T P1;
        T t = in;
        int n = near;
        T nearest = (Vector) this.knots.get(n);
        T previous = (Vector) this.knots.get(n > 0 ? n - 1 : this.spanCount - 1);
        T next = (Vector) this.knots.get((n + 1) % this.spanCount);
        if (t.dst2(next) < t.dst2(previous)) {
            P1 = nearest;
            P2 = next;
            P3 = in;
        } else {
            P1 = previous;
            P2 = nearest;
            P3 = in;
            n = n > 0 ? n - 1 : this.spanCount - 1;
        }
        float L1Sqr = P1.dst2(P2);
        float L2Sqr = P3.dst2(P2);
        float L3Sqr = P3.dst2(P1);
        float L1 = (float) Math.sqrt((double) L1Sqr);
        T t2 = nearest;
        T t3 = previous;
        return (((float) n) + MathUtils.clamp((L1 - (((L2Sqr + L1Sqr) - L3Sqr) / (2.0f * L1))) / L1, 0.0f, 1.0f)) / ((float) this.spanCount);
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
