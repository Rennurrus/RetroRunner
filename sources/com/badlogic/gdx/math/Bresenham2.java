package com.badlogic.gdx.math;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class Bresenham2 {
    private final Array<GridPoint2> points = new Array<>();
    private final Pool<GridPoint2> pool = new Pool<GridPoint2>() {
        /* access modifiers changed from: protected */
        public GridPoint2 newObject() {
            return new GridPoint2();
        }
    };

    public Array<GridPoint2> line(GridPoint2 start, GridPoint2 end) {
        return line(start.x, start.y, end.x, end.y);
    }

    public Array<GridPoint2> line(int startX, int startY, int endX, int endY) {
        this.pool.freeAll(this.points);
        this.points.clear();
        return line(startX, startY, endX, endY, this.pool, this.points);
    }

    public Array<GridPoint2> line(int startX, int startY, int endX, int endY, Pool<GridPoint2> pool2, Array<GridPoint2> output) {
        Array<GridPoint2> array = output;
        int w = endX - startX;
        int h = endY - startY;
        int dx1 = 0;
        int dy1 = 0;
        int dx2 = 0;
        int dy2 = 0;
        if (w < 0) {
            dx1 = -1;
            dx2 = -1;
        } else if (w > 0) {
            dx1 = 1;
            dx2 = 1;
        }
        if (h < 0) {
            dy1 = -1;
        } else if (h > 0) {
            dy1 = 1;
        }
        int longest = Math.abs(w);
        int shortest = Math.abs(h);
        if (longest <= shortest) {
            longest = Math.abs(h);
            shortest = Math.abs(w);
            if (h < 0) {
                dy2 = -1;
            } else if (h > 0) {
                dy2 = 1;
            }
            dx2 = 0;
        }
        int startY2 = startY;
        int numerator = longest >> 1;
        int startX2 = startX;
        for (int i = 0; i <= longest; i++) {
            GridPoint2 point = pool2.obtain();
            point.set(startX2, startY2);
            array.add(point);
            numerator += shortest;
            if (numerator > longest) {
                numerator -= longest;
                startX2 += dx1;
                startY2 += dy1;
            } else {
                startX2 += dx2;
                startY2 += dy2;
            }
        }
        return array;
    }
}
