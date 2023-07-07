package com.badlogic.gdx.math;

import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ShortArray;

public class EarClippingTriangulator {
    private static final int CONCAVE = -1;
    private static final int CONVEX = 1;
    private short[] indices;
    private final ShortArray indicesArray = new ShortArray();
    private final ShortArray triangles = new ShortArray();
    private int vertexCount;
    private final IntArray vertexTypes = new IntArray();
    private float[] vertices;

    public ShortArray computeTriangles(FloatArray vertices2) {
        return computeTriangles(vertices2.items, 0, vertices2.size);
    }

    public ShortArray computeTriangles(float[] vertices2) {
        return computeTriangles(vertices2, 0, vertices2.length);
    }

    public ShortArray computeTriangles(float[] vertices2, int offset, int count) {
        this.vertices = vertices2;
        int vertexCount2 = count / 2;
        this.vertexCount = vertexCount2;
        int vertexOffset = offset / 2;
        ShortArray indicesArray2 = this.indicesArray;
        indicesArray2.clear();
        indicesArray2.ensureCapacity(vertexCount2);
        indicesArray2.size = vertexCount2;
        short[] indices2 = indicesArray2.items;
        this.indices = indices2;
        if (GeometryUtils.isClockwise(vertices2, offset, count)) {
            for (short i = 0; i < vertexCount2; i = (short) (i + 1)) {
                indices2[i] = (short) (vertexOffset + i);
            }
        } else {
            int n = vertexCount2 - 1;
            for (int i2 = 0; i2 < vertexCount2; i2++) {
                indices2[i2] = (short) ((vertexOffset + n) - i2);
            }
        }
        IntArray vertexTypes2 = this.vertexTypes;
        vertexTypes2.clear();
        vertexTypes2.ensureCapacity(vertexCount2);
        int n2 = vertexCount2;
        for (int i3 = 0; i3 < n2; i3++) {
            vertexTypes2.add(classifyVertex(i3));
        }
        ShortArray triangles2 = this.triangles;
        triangles2.clear();
        triangles2.ensureCapacity(Math.max(0, vertexCount2 - 2) * 3);
        triangulate();
        return triangles2;
    }

    private void triangulate() {
        int i;
        int[] vertexTypes2 = this.vertexTypes.items;
        while (true) {
            i = this.vertexCount;
            int nextIndex = 0;
            if (i <= 3) {
                break;
            }
            int earTipIndex = findEarTip();
            cutEarTip(earTipIndex);
            int previousIndex = previousIndex(earTipIndex);
            if (earTipIndex != this.vertexCount) {
                nextIndex = earTipIndex;
            }
            vertexTypes2[previousIndex] = classifyVertex(previousIndex);
            vertexTypes2[nextIndex] = classifyVertex(nextIndex);
        }
        if (i == 3) {
            ShortArray triangles2 = this.triangles;
            short[] indices2 = this.indices;
            triangles2.add(indices2[0]);
            triangles2.add(indices2[1]);
            triangles2.add(indices2[2]);
        }
    }

    private int classifyVertex(int index) {
        short[] indices2 = this.indices;
        int previous = indices2[previousIndex(index)] * 2;
        int current = indices2[index] * 2;
        int next = indices2[nextIndex(index)] * 2;
        float[] vertices2 = this.vertices;
        return computeSpannedAreaSign(vertices2[previous], vertices2[previous + 1], vertices2[current], vertices2[current + 1], vertices2[next], vertices2[next + 1]);
    }

    private int findEarTip() {
        int vertexCount2 = this.vertexCount;
        for (int i = 0; i < vertexCount2; i++) {
            if (isEarTip(i)) {
                return i;
            }
        }
        int[] vertexTypes2 = this.vertexTypes.items;
        for (int i2 = 0; i2 < vertexCount2; i2++) {
            if (vertexTypes2[i2] != -1) {
                return i2;
            }
        }
        return 0;
    }

    private boolean isEarTip(int earTipIndex) {
        int i;
        int[] vertexTypes2 = this.vertexTypes.items;
        if (vertexTypes2[earTipIndex] == -1) {
            return false;
        }
        int previousIndex = previousIndex(earTipIndex);
        int nextIndex = nextIndex(earTipIndex);
        short[] indices2 = this.indices;
        int p1 = indices2[previousIndex] * 2;
        int p2 = indices2[earTipIndex] * 2;
        int p3 = indices2[nextIndex] * 2;
        float[] vertices2 = this.vertices;
        float p1x = vertices2[p1];
        float p1y = vertices2[p1 + 1];
        float p2x = vertices2[p2];
        float p2y = vertices2[p2 + 1];
        float p3x = vertices2[p3];
        float p3y = vertices2[p3 + 1];
        int i2 = nextIndex(nextIndex);
        while (i2 != previousIndex) {
            if (vertexTypes2[i2] != 1) {
                int v = indices2[i2] * 2;
                float vx = vertices2[v];
                float vy = vertices2[v + 1];
                i = i2;
                if (computeSpannedAreaSign(p3x, p3y, p1x, p1y, vx, vy) >= 0 && computeSpannedAreaSign(p1x, p1y, p2x, p2y, vx, vy) >= 0 && computeSpannedAreaSign(p2x, p2y, p3x, p3y, vx, vy) >= 0) {
                    return false;
                }
            } else {
                i = i2;
            }
            i2 = nextIndex(i);
        }
        int i3 = i2;
        return true;
    }

    private void cutEarTip(int earTipIndex) {
        short[] indices2 = this.indices;
        ShortArray triangles2 = this.triangles;
        triangles2.add(indices2[previousIndex(earTipIndex)]);
        triangles2.add(indices2[earTipIndex]);
        triangles2.add(indices2[nextIndex(earTipIndex)]);
        this.indicesArray.removeIndex(earTipIndex);
        this.vertexTypes.removeIndex(earTipIndex);
        this.vertexCount--;
    }

    private int previousIndex(int index) {
        return (index == 0 ? this.vertexCount : index) - 1;
    }

    private int nextIndex(int index) {
        return (index + 1) % this.vertexCount;
    }

    private static int computeSpannedAreaSign(float p1x, float p1y, float p2x, float p2y, float p3x, float p3y) {
        return (int) Math.signum(((p3y - p2y) * p1x) + ((p1y - p3y) * p2x) + ((p2y - p1y) * p3x));
    }
}
