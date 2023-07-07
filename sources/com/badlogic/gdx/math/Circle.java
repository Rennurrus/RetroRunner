package com.badlogic.gdx.math;

import com.badlogic.gdx.utils.NumberUtils;
import java.io.Serializable;

public class Circle implements Serializable, Shape2D {
    public float radius;
    public float x;
    public float y;

    public Circle() {
    }

    public Circle(float x2, float y2, float radius2) {
        this.x = x2;
        this.y = y2;
        this.radius = radius2;
    }

    public Circle(Vector2 position, float radius2) {
        this.x = position.x;
        this.y = position.y;
        this.radius = radius2;
    }

    public Circle(Circle circle) {
        this.x = circle.x;
        this.y = circle.y;
        this.radius = circle.radius;
    }

    public Circle(Vector2 center, Vector2 edge) {
        this.x = center.x;
        this.y = center.y;
        this.radius = Vector2.len(center.x - edge.x, center.y - edge.y);
    }

    public void set(float x2, float y2, float radius2) {
        this.x = x2;
        this.y = y2;
        this.radius = radius2;
    }

    public void set(Vector2 position, float radius2) {
        this.x = position.x;
        this.y = position.y;
        this.radius = radius2;
    }

    public void set(Circle circle) {
        this.x = circle.x;
        this.y = circle.y;
        this.radius = circle.radius;
    }

    public void set(Vector2 center, Vector2 edge) {
        this.x = center.x;
        this.y = center.y;
        this.radius = Vector2.len(center.x - edge.x, center.y - edge.y);
    }

    public void setPosition(Vector2 position) {
        this.x = position.x;
        this.y = position.y;
    }

    public void setPosition(float x2, float y2) {
        this.x = x2;
        this.y = y2;
    }

    public void setX(float x2) {
        this.x = x2;
    }

    public void setY(float y2) {
        this.y = y2;
    }

    public void setRadius(float radius2) {
        this.radius = radius2;
    }

    public boolean contains(float x2, float y2) {
        float x3 = this.x - x2;
        float y3 = this.y - y2;
        float f = (x3 * x3) + (y3 * y3);
        float f2 = this.radius;
        return f <= f2 * f2;
    }

    public boolean contains(Vector2 point) {
        float dx = this.x - point.x;
        float dy = this.y - point.y;
        float f = (dx * dx) + (dy * dy);
        float f2 = this.radius;
        return f <= f2 * f2;
    }

    public boolean contains(Circle c) {
        float f = this.radius;
        float f2 = c.radius;
        float radiusDiff = f - f2;
        if (radiusDiff < 0.0f) {
            return false;
        }
        float dx = this.x - c.x;
        float dy = this.y - c.y;
        float dst = (dx * dx) + (dy * dy);
        float radiusSum = f + f2;
        if (radiusDiff * radiusDiff < dst || dst >= radiusSum * radiusSum) {
            return false;
        }
        return true;
    }

    public boolean overlaps(Circle c) {
        float dx = this.x - c.x;
        float dy = this.y - c.y;
        float distance = (dx * dx) + (dy * dy);
        float radiusSum = this.radius + c.radius;
        return distance < radiusSum * radiusSum;
    }

    public String toString() {
        return this.x + "," + this.y + "," + this.radius;
    }

    public float circumference() {
        return this.radius * 6.2831855f;
    }

    public float area() {
        float f = this.radius;
        return f * f * 3.1415927f;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || o.getClass() != getClass()) {
            return false;
        }
        Circle c = (Circle) o;
        if (this.x == c.x && this.y == c.y && this.radius == c.radius) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return (((((1 * 41) + NumberUtils.floatToRawIntBits(this.radius)) * 41) + NumberUtils.floatToRawIntBits(this.x)) * 41) + NumberUtils.floatToRawIntBits(this.y);
    }
}
