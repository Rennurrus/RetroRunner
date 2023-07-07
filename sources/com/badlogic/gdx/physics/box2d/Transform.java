package com.badlogic.gdx.physics.box2d;

import com.badlogic.gdx.math.Vector2;

public class Transform {
    public static final int COS = 2;
    public static final int POS_X = 0;
    public static final int POS_Y = 1;
    public static final int SIN = 3;
    private Vector2 orientation = new Vector2();
    private Vector2 position = new Vector2();
    public float[] vals = new float[4];

    public Transform() {
    }

    public Transform(Vector2 position2, float angle) {
        setPosition(position2);
        setRotation(angle);
    }

    public Transform(Vector2 position2, Vector2 orientation2) {
        setPosition(position2);
        setOrientation(orientation2);
    }

    public Vector2 mul(Vector2 v) {
        float[] fArr = this.vals;
        float x = fArr[0] + (fArr[2] * v.x) + ((-this.vals[3]) * v.y);
        float[] fArr2 = this.vals;
        v.x = x;
        v.y = fArr2[1] + (fArr2[3] * v.x) + (this.vals[2] * v.y);
        return v;
    }

    public Vector2 getPosition() {
        Vector2 vector2 = this.position;
        float[] fArr = this.vals;
        return vector2.set(fArr[0], fArr[1]);
    }

    public void setRotation(float angle) {
        float[] fArr = this.vals;
        fArr[2] = (float) Math.cos((double) angle);
        fArr[3] = (float) Math.sin((double) angle);
    }

    public float getRotation() {
        float[] fArr = this.vals;
        return (float) Math.atan2((double) fArr[3], (double) fArr[2]);
    }

    public Vector2 getOrientation() {
        Vector2 vector2 = this.orientation;
        float[] fArr = this.vals;
        return vector2.set(fArr[2], fArr[3]);
    }

    public void setOrientation(Vector2 orientation2) {
        this.vals[2] = orientation2.x;
        this.vals[3] = orientation2.y;
    }

    public void setPosition(Vector2 pos) {
        this.vals[0] = pos.x;
        this.vals[1] = pos.y;
    }
}
