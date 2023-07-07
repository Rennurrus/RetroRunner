package com.badlogic.gdx.math;

import java.io.Serializable;

public class GridPoint2 implements Serializable {
    private static final long serialVersionUID = -4019969926331717380L;
    public int x;
    public int y;

    public GridPoint2() {
    }

    public GridPoint2(int x2, int y2) {
        this.x = x2;
        this.y = y2;
    }

    public GridPoint2(GridPoint2 point) {
        this.x = point.x;
        this.y = point.y;
    }

    public GridPoint2 set(GridPoint2 point) {
        this.x = point.x;
        this.y = point.y;
        return this;
    }

    public GridPoint2 set(int x2, int y2) {
        this.x = x2;
        this.y = y2;
        return this;
    }

    public float dst2(GridPoint2 other) {
        int xd = other.x - this.x;
        int yd = other.y - this.y;
        return (float) ((xd * xd) + (yd * yd));
    }

    public float dst2(int x2, int y2) {
        int xd = x2 - this.x;
        int yd = y2 - this.y;
        return (float) ((xd * xd) + (yd * yd));
    }

    public float dst(GridPoint2 other) {
        int xd = other.x - this.x;
        int yd = other.y - this.y;
        return (float) Math.sqrt((double) ((xd * xd) + (yd * yd)));
    }

    public float dst(int x2, int y2) {
        int xd = x2 - this.x;
        int yd = y2 - this.y;
        return (float) Math.sqrt((double) ((xd * xd) + (yd * yd)));
    }

    public GridPoint2 add(GridPoint2 other) {
        this.x += other.x;
        this.y += other.y;
        return this;
    }

    public GridPoint2 add(int x2, int y2) {
        this.x += x2;
        this.y += y2;
        return this;
    }

    public GridPoint2 sub(GridPoint2 other) {
        this.x -= other.x;
        this.y -= other.y;
        return this;
    }

    public GridPoint2 sub(int x2, int y2) {
        this.x -= x2;
        this.y -= y2;
        return this;
    }

    public GridPoint2 cpy() {
        return new GridPoint2(this);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || o.getClass() != getClass()) {
            return false;
        }
        GridPoint2 g = (GridPoint2) o;
        if (this.x == g.x && this.y == g.y) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return (((1 * 53) + this.x) * 53) + this.y;
    }

    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }
}
