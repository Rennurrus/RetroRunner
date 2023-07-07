package com.badlogic.gdx.math;

import com.badlogic.gdx.utils.BooleanArray;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ShortArray;

public class DelaunayTriangulator {
    private static final int COMPLETE = 1;
    private static final float EPSILON = 1.0E-6f;
    private static final int INCOMPLETE = 2;
    private static final int INSIDE = 0;
    private final Vector2 centroid = new Vector2();
    private final BooleanArray complete = new BooleanArray(false, 16);
    private final IntArray edges = new IntArray();
    private final ShortArray originalIndices = new ShortArray(false, 0);
    private final IntArray quicksortStack = new IntArray();
    private float[] sortedPoints;
    private final float[] superTriangle = new float[6];
    private final ShortArray triangles = new ShortArray(false, 16);

    public ShortArray computeTriangles(FloatArray points, boolean sorted) {
        return computeTriangles(points.items, 0, points.size, sorted);
    }

    public ShortArray computeTriangles(float[] polygon, boolean sorted) {
        return computeTriangles(polygon, 0, polygon.length, sorted);
    }

    public ShortArray computeTriangles(float[] points, int offset, int count, boolean sorted) {
        float[] points2;
        int offset2;
        int pointIndex;
        float[] points3;
        int offset3;
        float[] superTriangle2;
        int pointIndex2;
        int end;
        short[] trianglesArray;
        boolean[] completeArray;
        IntArray edges2;
        int triangleIndex;
        BooleanArray complete2;
        ShortArray triangles2;
        float x1;
        float y1;
        float x2;
        float y2;
        float x3;
        float y3;
        int i = count;
        if (i <= 32767) {
            ShortArray triangles3 = this.triangles;
            triangles3.clear();
            if (i < 6) {
                return triangles3;
            }
            triangles3.ensureCapacity(i);
            if (!sorted) {
                float[] fArr = this.sortedPoints;
                if (fArr == null || fArr.length < i) {
                    this.sortedPoints = new float[i];
                }
                System.arraycopy(points, offset, this.sortedPoints, 0, i);
                float[] points4 = this.sortedPoints;
                sort(points4, i);
                points2 = points4;
                offset2 = 0;
            } else {
                points2 = points;
                offset2 = offset;
            }
            int end2 = offset2 + i;
            float xmin = points2[0];
            int i2 = 1;
            float ymin = points2[1];
            int i3 = offset2 + 2;
            float xmin2 = xmin;
            float ymin2 = ymin;
            float xmax = xmin;
            float ymax = ymin;
            while (i3 < end2) {
                float value = points2[i3];
                if (value < xmin2) {
                    xmin2 = value;
                }
                if (value > xmax) {
                    xmax = value;
                }
                int i4 = i3 + 1;
                float value2 = points2[i4];
                if (value2 < ymin2) {
                    ymin2 = value2;
                }
                if (value2 > ymax) {
                    ymax = value2;
                }
                i3 = i4 + 1;
            }
            float dx = xmax - xmin2;
            float dy = ymax - ymin2;
            float dmax = (dx > dy ? dx : dy) * 20.0f;
            float xmid = (xmax + xmin2) / 2.0f;
            float ymid = (ymax + ymin2) / 2.0f;
            float[] superTriangle3 = this.superTriangle;
            superTriangle3[0] = xmid - dmax;
            superTriangle3[1] = ymid - dmax;
            superTriangle3[2] = xmid;
            superTriangle3[3] = ymid + dmax;
            superTriangle3[4] = xmid + dmax;
            superTriangle3[5] = ymid - dmax;
            IntArray edges3 = this.edges;
            edges3.ensureCapacity(i / 2);
            BooleanArray complete3 = this.complete;
            complete3.clear();
            complete3.ensureCapacity(i);
            triangles3.add(end2);
            triangles3.add(end2 + 2);
            triangles3.add(end2 + 4);
            complete3.add(false);
            int pointIndex3 = offset2;
            while (pointIndex3 < end2) {
                float x = points2[pointIndex3];
                float y = points2[pointIndex3 + 1];
                short[] trianglesArray2 = triangles3.items;
                boolean[] completeArray2 = complete3.items;
                int triangleIndex2 = triangles3.size - i2;
                while (triangleIndex2 >= 0) {
                    int completeIndex = triangleIndex2 / 3;
                    if (completeArray2[completeIndex]) {
                        triangleIndex = triangleIndex2;
                        completeArray = completeArray2;
                        trianglesArray = trianglesArray2;
                        pointIndex2 = pointIndex3;
                        complete2 = complete3;
                        superTriangle2 = superTriangle3;
                        triangles2 = triangles3;
                        offset3 = offset2;
                        points3 = points2;
                        end = end2;
                        edges2 = edges3;
                    } else {
                        short p1 = trianglesArray2[triangleIndex2 - 2];
                        short p2 = trianglesArray2[triangleIndex2 - 1];
                        offset3 = offset2;
                        short p3 = trianglesArray2[triangleIndex2];
                        if (p1 >= end2) {
                            int i5 = p1 - end2;
                            float f = superTriangle3[i5];
                            x1 = superTriangle3[i5 + 1];
                            y1 = f;
                        } else {
                            y1 = points2[p1];
                            x1 = points2[p1 + 1];
                        }
                        if (p2 >= end2) {
                            int i6 = p2 - end2;
                            float f2 = superTriangle3[i6];
                            x2 = superTriangle3[i6 + 1];
                            y2 = f2;
                        } else {
                            y2 = points2[p2];
                            x2 = points2[p2 + 1];
                        }
                        if (p3 >= end2) {
                            int i7 = p3 - end2;
                            float f3 = superTriangle3[i7];
                            x3 = superTriangle3[i7 + 1];
                            y3 = f3;
                        } else {
                            y3 = points2[p3];
                            x3 = points2[p3 + 1];
                        }
                        points3 = points2;
                        int completeIndex2 = completeIndex;
                        triangleIndex = triangleIndex2;
                        completeArray = completeArray2;
                        trianglesArray = trianglesArray2;
                        end = end2;
                        pointIndex2 = pointIndex3;
                        BooleanArray complete4 = complete3;
                        edges2 = edges3;
                        superTriangle2 = superTriangle3;
                        ShortArray triangles4 = triangles3;
                        int circumCircle = circumCircle(x, y, y1, x1, y2, x2, y3, x3);
                        if (circumCircle == 0) {
                            edges2.add(p1, p2, p2, p3);
                            edges2.add(p3, p1);
                            triangles2 = triangles4;
                            triangles2.removeRange(triangleIndex - 2, triangleIndex);
                            complete2 = complete4;
                            complete2.removeIndex(completeIndex2);
                        } else if (circumCircle != 1) {
                            complete2 = complete4;
                            triangles2 = triangles4;
                        } else {
                            completeArray[completeIndex2] = true;
                            complete2 = complete4;
                            triangles2 = triangles4;
                        }
                    }
                    int i8 = triangleIndex - 3;
                    offset2 = offset3;
                    points2 = points3;
                    int i9 = count;
                    triangles3 = triangles2;
                    complete3 = complete2;
                    triangleIndex2 = i8;
                    edges3 = edges2;
                    completeArray2 = completeArray;
                    trianglesArray2 = trianglesArray;
                    end2 = end;
                    pointIndex3 = pointIndex2;
                    superTriangle3 = superTriangle2;
                }
                int i10 = triangleIndex2;
                boolean[] zArr = completeArray2;
                short[] sArr = trianglesArray2;
                int pointIndex4 = pointIndex3;
                BooleanArray complete5 = complete3;
                float[] superTriangle4 = superTriangle3;
                ShortArray triangles5 = triangles3;
                int offset4 = offset2;
                float[] points5 = points2;
                int end3 = end2;
                IntArray edges4 = edges3;
                int[] edgesArray = edges4.items;
                int i11 = 0;
                int n = edges4.size;
                while (i11 < n) {
                    int p12 = edgesArray[i11];
                    if (p12 == -1) {
                        pointIndex = pointIndex4;
                    } else {
                        int p22 = edgesArray[i11 + 1];
                        boolean skip = false;
                        for (int ii = i11 + 2; ii < n; ii += 2) {
                            if (p12 == edgesArray[ii + 1] && p22 == edgesArray[ii]) {
                                skip = true;
                                edgesArray[ii] = -1;
                            }
                        }
                        if (skip) {
                            pointIndex = pointIndex4;
                        } else {
                            triangles5.add(p12);
                            triangles5.add(edgesArray[i11 + 1]);
                            pointIndex = pointIndex4;
                            triangles5.add(pointIndex);
                            complete5.add(false);
                        }
                    }
                    i11 += 2;
                    pointIndex4 = pointIndex;
                }
                edges4.clear();
                pointIndex3 = pointIndex4 + 2;
                offset2 = offset4;
                points2 = points5;
                int i12 = count;
                triangles3 = triangles5;
                complete3 = complete5;
                edges3 = edges4;
                end2 = end3;
                superTriangle3 = superTriangle4;
                i2 = 1;
            }
            float[] fArr2 = superTriangle3;
            ShortArray triangles6 = triangles3;
            int offset5 = offset2;
            float[] fArr3 = points2;
            int end4 = end2;
            IntArray intArray = edges3;
            int i13 = pointIndex3;
            short[] trianglesArray3 = triangles6.items;
            int i14 = triangles6.size - 1;
            while (i14 >= 0) {
                int end5 = end4;
                if (trianglesArray3[i14] >= end5 || trianglesArray3[i14 - 1] >= end5 || trianglesArray3[i14 - 2] >= end5) {
                    triangles6.removeIndex(i14);
                    triangles6.removeIndex(i14 - 1);
                    triangles6.removeIndex(i14 - 2);
                }
                i14 -= 3;
                end4 = end5;
            }
            if (!sorted) {
                short[] originalIndicesArray = this.originalIndices.items;
                int n2 = triangles6.size;
                for (int i15 = 0; i15 < n2; i15++) {
                    trianglesArray3[i15] = (short) (originalIndicesArray[trianglesArray3[i15] / 2] * 2);
                }
            }
            if (offset5 == 0) {
                int n3 = triangles6.size;
                for (int i16 = 0; i16 < n3; i16++) {
                    trianglesArray3[i16] = (short) (trianglesArray3[i16] / 2);
                }
            } else {
                int n4 = triangles6.size;
                for (int i17 = 0; i17 < n4; i17++) {
                    trianglesArray3[i17] = (short) ((trianglesArray3[i17] - offset5) / 2);
                }
            }
            return triangles6;
        }
        float[] fArr4 = points;
        int i18 = offset;
        throw new IllegalArgumentException("count must be <= 32767");
    }

    private int circumCircle(float xp, float yp, float x1, float y1, float x2, float y2, float x3, float y3) {
        float xc;
        float xc2;
        float y1y2 = Math.abs(y1 - y2);
        float y2y3 = Math.abs(y2 - y3);
        if (y1y2 >= 1.0E-6f) {
            float m1 = (-(x2 - x1)) / (y2 - y1);
            float mx1 = (x1 + x2) / 2.0f;
            float my1 = (y1 + y2) / 2.0f;
            if (y2y3 < 1.0E-6f) {
                xc = (x3 + x2) / 2.0f;
                xc2 = ((xc - mx1) * m1) + my1;
            } else {
                float m2 = (-(x3 - x2)) / (y3 - y2);
                float xc3 = ((((m1 * mx1) - (m2 * ((x2 + x3) / 2.0f))) + ((y2 + y3) / 2.0f)) - my1) / (m1 - m2);
                xc = xc3;
                xc2 = ((xc3 - mx1) * m1) + my1;
            }
        } else if (y2y3 < 1.0E-6f) {
            return 2;
        } else {
            xc = (x2 + x1) / 2.0f;
            xc2 = ((xc - ((x2 + x3) / 2.0f)) * ((-(x3 - x2)) / (y3 - y2))) + ((y2 + y3) / 2.0f);
        }
        float dx = x2 - xc;
        float dy = y2 - xc2;
        float rsqr = (dx * dx) + (dy * dy);
        float dx2 = xp - xc;
        float dx3 = dx2 * dx2;
        float dy2 = yp - xc2;
        if (((dy2 * dy2) + dx3) - rsqr <= 1.0E-6f) {
            return 0;
        }
        if (xp <= xc || dx3 <= rsqr) {
            return 2;
        }
        return 1;
    }

    private void sort(float[] values, int count) {
        int pointCount = count / 2;
        this.originalIndices.clear();
        this.originalIndices.ensureCapacity(pointCount);
        short[] originalIndicesArray = this.originalIndices.items;
        for (short i = 0; i < pointCount; i = (short) (i + 1)) {
            originalIndicesArray[i] = i;
        }
        IntArray stack = this.quicksortStack;
        stack.add(0);
        stack.add((count - 1) - 1);
        while (stack.size > 0) {
            int upper = stack.pop();
            int lower = stack.pop();
            if (upper > lower) {
                int i2 = quicksortPartition(values, lower, upper, originalIndicesArray);
                if (i2 - lower > upper - i2) {
                    stack.add(lower);
                    stack.add(i2 - 2);
                }
                stack.add(i2 + 2);
                stack.add(upper);
                if (upper - i2 >= i2 - lower) {
                    stack.add(lower);
                    stack.add(i2 - 2);
                }
            }
        }
    }

    private int quicksortPartition(float[] values, int lower, int upper, short[] originalIndices2) {
        float value = values[lower];
        int up = upper;
        int down = lower + 2;
        while (down < up) {
            while (down < up && values[down] <= value) {
                down += 2;
            }
            while (values[up] > value) {
                up -= 2;
            }
            if (down < up) {
                float tempValue = values[down];
                values[down] = values[up];
                values[up] = tempValue;
                float tempValue2 = values[down + 1];
                values[down + 1] = values[up + 1];
                values[up + 1] = tempValue2;
                short tempIndex = originalIndices2[down / 2];
                originalIndices2[down / 2] = originalIndices2[up / 2];
                originalIndices2[up / 2] = tempIndex;
            }
        }
        values[lower] = values[up];
        values[up] = value;
        float tempValue3 = values[lower + 1];
        values[lower + 1] = values[up + 1];
        values[up + 1] = tempValue3;
        short tempIndex2 = originalIndices2[lower / 2];
        originalIndices2[lower / 2] = originalIndices2[up / 2];
        originalIndices2[up / 2] = tempIndex2;
        return up;
    }

    public void trim(ShortArray triangles2, float[] points, float[] hull, int offset, int count) {
        ShortArray shortArray = triangles2;
        short[] trianglesArray = shortArray.items;
        for (int i = shortArray.size - 1; i >= 0; i -= 3) {
            int p1 = trianglesArray[i - 2] * 2;
            int p2 = trianglesArray[i - 1] * 2;
            int p3 = trianglesArray[i] * 2;
            GeometryUtils.triangleCentroid(points[p1], points[p1 + 1], points[p2], points[p2 + 1], points[p3], points[p3 + 1], this.centroid);
            if (!Intersector.isPointInPolygon(hull, offset, count, this.centroid.x, this.centroid.y)) {
                triangles2.removeIndex(i);
                triangles2.removeIndex(i - 1);
                triangles2.removeIndex(i - 2);
            }
        }
        float[] fArr = hull;
        int i2 = offset;
        int i3 = count;
    }
}
