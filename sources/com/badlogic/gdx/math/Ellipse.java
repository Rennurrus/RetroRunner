package com.badlogic.gdx.math;

import com.badlogic.gdx.utils.NumberUtils;
import java.io.Serializable;

public class Ellipse implements Serializable, Shape2D {
    private static final long serialVersionUID = 7381533206532032099L;
    public float height;
    public float width;
    public float x;
    public float y;

    public Ellipse() {
    }

    public Ellipse(Ellipse ellipse) {
        this.x = ellipse.x;
        this.y = ellipse.y;
        this.width = ellipse.width;
        this.height = ellipse.height;
    }

    public Ellipse(float x2, float y2, float width2, float height2) {
        this.x = x2;
        this.y = y2;
        this.width = width2;
        this.height = height2;
    }

    public Ellipse(Vector2 position, float width2, float height2) {
        this.x = position.x;
        this.y = position.y;
        this.width = width2;
        this.height = height2;
    }

    public Ellipse(Vector2 position, Vector2 size) {
        this.x = position.x;
        this.y = position.y;
        this.width = size.x;
        this.height = size.y;
    }

    public Ellipse(Circle circle) {
        this.x = circle.x;
        this.y = circle.y;
        this.width = circle.radius * 2.0f;
        this.height = circle.radius * 2.0f;
    }

    public boolean contains(float x2, float y2) {
        float x3 = x2 - this.x;
        float y3 = y2 - this.y;
        float f = this.width;
        float f2 = (x3 * x3) / (((f * 0.5f) * f) * 0.5f);
        float f3 = this.height;
        return f2 + ((y3 * y3) / (((f3 * 0.5f) * f3) * 0.5f)) <= 1.0f;
    }

    public boolean contains(Vector2 point) {
        return contains(point.x, point.y);
    }

    public void set(float x2, float y2, float width2, float height2) {
        this.x = x2;
        this.y = y2;
        this.width = width2;
        this.height = height2;
    }

    public void set(Ellipse ellipse) {
        this.x = ellipse.x;
        this.y = ellipse.y;
        this.width = ellipse.width;
        this.height = ellipse.height;
    }

    public void set(Circle circle) {
        this.x = circle.x;
        this.y = circle.y;
        this.width = circle.radius * 2.0f;
        this.height = circle.radius * 2.0f;
    }

    public void set(Vector2 position, Vector2 size) {
        this.x = position.x;
        this.y = position.y;
        this.width = size.x;
        this.height = size.y;
    }

    public Ellipse setPosition(Vector2 position) {
        this.x = position.x;
        this.y = position.y;
        return this;
    }

    public Ellipse setPosition(float x2, float y2) {
        this.x = x2;
        this.y = y2;
        return this;
    }

    public Ellipse setSize(float width2, float height2) {
        this.width = width2;
        this.height = height2;
        return this;
    }

    public float area() {
        return ((this.width * this.height) * 3.1415927f) / 4.0f;
    }

    public float circumference() {
        float a = this.width / 2.0f;
        float b = this.height / 2.0f;
        if (a * 3.0f <= b && b * 3.0f <= a) {
            return (float) (Math.sqrt((double) (((a * a) + (b * b)) / 2.0f)) * 6.2831854820251465d);
        }
        double d = (double) ((a + b) * 3.0f);
        double sqrt = Math.sqrt((double) (((a * 3.0f) + b) * ((3.0f * b) + a)));
        Double.isNaN(d);
        return (float) ((d - sqrt) * 3.1415927410125732d);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || o.getClass() != getClass()) {
            return false;
        }
        Ellipse e = (Ellipse) o;
        if (this.x == e.x && this.y == e.y && this.width == e.width && this.height == e.height) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return (((((((1 * 53) + NumberUtils.floatToRawIntBits(this.height)) * 53) + NumberUtils.floatToRawIntBits(this.width)) * 53) + NumberUtils.floatToRawIntBits(this.x)) * 53) + NumberUtils.floatToRawIntBits(this.y);
    }
}
