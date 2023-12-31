package com.badlogic.gdx.math;

import java.io.Serializable;

public class Plane implements Serializable {
    private static final long serialVersionUID = -1240652082930747866L;
    public float d = 0.0f;
    public final Vector3 normal = new Vector3();

    public enum PlaneSide {
        OnPlane,
        Back,
        Front
    }

    public Plane() {
    }

    public Plane(Vector3 normal2, float d2) {
        this.normal.set(normal2).nor();
        this.d = d2;
    }

    public Plane(Vector3 normal2, Vector3 point) {
        this.normal.set(normal2).nor();
        this.d = -this.normal.dot(point);
    }

    public Plane(Vector3 point1, Vector3 point2, Vector3 point3) {
        set(point1, point2, point3);
    }

    public void set(Vector3 point1, Vector3 point2, Vector3 point3) {
        this.normal.set(point1).sub(point2).crs(point2.x - point3.x, point2.y - point3.y, point2.z - point3.z).nor();
        this.d = -point1.dot(this.normal);
    }

    public void set(float nx, float ny, float nz, float d2) {
        this.normal.set(nx, ny, nz);
        this.d = d2;
    }

    public float distance(Vector3 point) {
        return this.normal.dot(point) + this.d;
    }

    public PlaneSide testPoint(Vector3 point) {
        float dist = this.normal.dot(point) + this.d;
        if (dist == 0.0f) {
            return PlaneSide.OnPlane;
        }
        if (dist < 0.0f) {
            return PlaneSide.Back;
        }
        return PlaneSide.Front;
    }

    public PlaneSide testPoint(float x, float y, float z) {
        float dist = this.normal.dot(x, y, z) + this.d;
        if (dist == 0.0f) {
            return PlaneSide.OnPlane;
        }
        if (dist < 0.0f) {
            return PlaneSide.Back;
        }
        return PlaneSide.Front;
    }

    public boolean isFrontFacing(Vector3 direction) {
        return this.normal.dot(direction) <= 0.0f;
    }

    public Vector3 getNormal() {
        return this.normal;
    }

    public float getD() {
        return this.d;
    }

    public void set(Vector3 point, Vector3 normal2) {
        this.normal.set(normal2);
        this.d = -point.dot(normal2);
    }

    public void set(float pointX, float pointY, float pointZ, float norX, float norY, float norZ) {
        this.normal.set(norX, norY, norZ);
        this.d = -((pointX * norX) + (pointY * norY) + (pointZ * norZ));
    }

    public void set(Plane plane) {
        this.normal.set(plane.normal);
        this.d = plane.d;
    }

    public String toString() {
        return this.normal.toString() + ", " + this.d;
    }
}
