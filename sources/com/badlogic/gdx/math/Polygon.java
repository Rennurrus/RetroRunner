package com.badlogic.gdx.math;

public class Polygon implements Shape2D {
    private Rectangle bounds;
    private boolean dirty;
    private float[] localVertices;
    private float originX;
    private float originY;
    private float rotation;
    private float scaleX;
    private float scaleY;
    private float[] worldVertices;
    private float x;
    private float y;

    public Polygon() {
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.dirty = true;
        this.localVertices = new float[0];
    }

    public Polygon(float[] vertices) {
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.dirty = true;
        if (vertices.length >= 6) {
            this.localVertices = vertices;
            return;
        }
        throw new IllegalArgumentException("polygons must contain at least 3 points.");
    }

    public float[] getVertices() {
        return this.localVertices;
    }

    public float[] getTransformedVertices() {
        if (!this.dirty) {
            return this.worldVertices;
        }
        boolean scale = false;
        this.dirty = false;
        float[] localVertices2 = this.localVertices;
        float[] fArr = this.worldVertices;
        if (fArr == null || fArr.length != localVertices2.length) {
            this.worldVertices = new float[localVertices2.length];
        }
        float[] worldVertices2 = this.worldVertices;
        float positionX = this.x;
        float positionY = this.y;
        float originX2 = this.originX;
        float originY2 = this.originY;
        float scaleX2 = this.scaleX;
        float scaleY2 = this.scaleY;
        if (!(scaleX2 == 1.0f && scaleY2 == 1.0f)) {
            scale = true;
        }
        float rotation2 = this.rotation;
        float cos = MathUtils.cosDeg(rotation2);
        float sin = MathUtils.sinDeg(rotation2);
        int n = localVertices2.length;
        for (int i = 0; i < n; i += 2) {
            float x2 = localVertices2[i] - originX2;
            float y2 = localVertices2[i + 1] - originY2;
            if (scale) {
                x2 *= scaleX2;
                y2 *= scaleY2;
            }
            if (rotation2 != 0.0f) {
                float oldX = x2;
                x2 = (cos * x2) - (sin * y2);
                y2 = (sin * oldX) + (cos * y2);
            }
            worldVertices2[i] = positionX + x2 + originX2;
            worldVertices2[i + 1] = positionY + y2 + originY2;
        }
        return worldVertices2;
    }

    public void setOrigin(float originX2, float originY2) {
        this.originX = originX2;
        this.originY = originY2;
        this.dirty = true;
    }

    public void setPosition(float x2, float y2) {
        this.x = x2;
        this.y = y2;
        this.dirty = true;
    }

    public void setVertices(float[] vertices) {
        if (vertices.length >= 6) {
            this.localVertices = vertices;
            this.dirty = true;
            return;
        }
        throw new IllegalArgumentException("polygons must contain at least 3 points.");
    }

    public void translate(float x2, float y2) {
        this.x += x2;
        this.y += y2;
        this.dirty = true;
    }

    public void setRotation(float degrees) {
        this.rotation = degrees;
        this.dirty = true;
    }

    public void rotate(float degrees) {
        this.rotation += degrees;
        this.dirty = true;
    }

    public void setScale(float scaleX2, float scaleY2) {
        this.scaleX = scaleX2;
        this.scaleY = scaleY2;
        this.dirty = true;
    }

    public void scale(float amount) {
        this.scaleX += amount;
        this.scaleY += amount;
        this.dirty = true;
    }

    public void dirty() {
        this.dirty = true;
    }

    public float area() {
        float[] vertices = getTransformedVertices();
        return GeometryUtils.polygonArea(vertices, 0, vertices.length);
    }

    public Rectangle getBoundingRectangle() {
        float[] vertices = getTransformedVertices();
        float minX = vertices[0];
        float minY = vertices[1];
        float maxX = vertices[0];
        float maxY = vertices[1];
        int numFloats = vertices.length;
        for (int i = 2; i < numFloats; i += 2) {
            minX = minX > vertices[i] ? vertices[i] : minX;
            minY = minY > vertices[i + 1] ? vertices[i + 1] : minY;
            maxX = maxX < vertices[i] ? vertices[i] : maxX;
            maxY = maxY < vertices[i + 1] ? vertices[i + 1] : maxY;
        }
        if (this.bounds == null) {
            this.bounds = new Rectangle();
        }
        Rectangle rectangle = this.bounds;
        rectangle.x = minX;
        rectangle.y = minY;
        rectangle.width = maxX - minX;
        rectangle.height = maxY - minY;
        return rectangle;
    }

    public boolean contains(float x2, float y2) {
        float[] vertices = getTransformedVertices();
        int numFloats = vertices.length;
        int intersects = 0;
        for (int i = 0; i < numFloats; i += 2) {
            float x1 = vertices[i];
            float y1 = vertices[i + 1];
            float x22 = vertices[(i + 2) % numFloats];
            float y22 = vertices[(i + 3) % numFloats];
            if (((y1 <= y2 && y2 < y22) || (y22 <= y2 && y2 < y1)) && x2 < (((x22 - x1) / (y22 - y1)) * (y2 - y1)) + x1) {
                intersects++;
            }
        }
        return (intersects & 1) == 1;
    }

    public boolean contains(Vector2 point) {
        return contains(point.x, point.y);
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getOriginX() {
        return this.originX;
    }

    public float getOriginY() {
        return this.originY;
    }

    public float getRotation() {
        return this.rotation;
    }

    public float getScaleX() {
        return this.scaleX;
    }

    public float getScaleY() {
        return this.scaleY;
    }
}
