package com.badlogic.gdx.math.collision;

import com.badlogic.gdx.math.Vector3;
import java.io.Serializable;

public class Segment implements Serializable {
    private static final long serialVersionUID = 2739667069736519602L;
    public final Vector3 a = new Vector3();
    public final Vector3 b = new Vector3();

    public Segment(Vector3 a2, Vector3 b2) {
        this.a.set(a2);
        this.b.set(b2);
    }

    public Segment(float aX, float aY, float aZ, float bX, float bY, float bZ) {
        this.a.set(aX, aY, aZ);
        this.b.set(bX, bY, bZ);
    }

    public float len() {
        return this.a.dst(this.b);
    }

    public float len2() {
        return this.a.dst2(this.b);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || o.getClass() != getClass()) {
            return false;
        }
        Segment s = (Segment) o;
        if (!this.a.equals(s.a) || !this.b.equals(s.b)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return (((1 * 71) + this.a.hashCode()) * 71) + this.b.hashCode();
    }
}
