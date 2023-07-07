package com.badlogic.gdx.physics.box2d;

import com.badlogic.gdx.math.Vector2;

public class Manifold {
    long addr;
    final Vector2 localNormal = new Vector2();
    final Vector2 localPoint = new Vector2();
    final ManifoldPoint[] points = {new ManifoldPoint(), new ManifoldPoint()};
    final float[] tmpFloat = new float[4];
    final int[] tmpInt = new int[2];

    public enum ManifoldType {
        Circle,
        FaceA,
        FaceB
    }

    private native void jniGetLocalNormal(long j, float[] fArr);

    private native void jniGetLocalPoint(long j, float[] fArr);

    private native int jniGetPoint(long j, float[] fArr, int i);

    private native int jniGetPointCount(long j);

    private native int jniGetType(long j);

    protected Manifold(long addr2) {
        this.addr = addr2;
    }

    public ManifoldType getType() {
        int type = jniGetType(this.addr);
        if (type == 0) {
            return ManifoldType.Circle;
        }
        if (type == 1) {
            return ManifoldType.FaceA;
        }
        if (type == 2) {
            return ManifoldType.FaceB;
        }
        return ManifoldType.Circle;
    }

    public int getPointCount() {
        return jniGetPointCount(this.addr);
    }

    public Vector2 getLocalNormal() {
        jniGetLocalNormal(this.addr, this.tmpFloat);
        Vector2 vector2 = this.localNormal;
        float[] fArr = this.tmpFloat;
        vector2.set(fArr[0], fArr[1]);
        return this.localNormal;
    }

    public Vector2 getLocalPoint() {
        jniGetLocalPoint(this.addr, this.tmpFloat);
        Vector2 vector2 = this.localPoint;
        float[] fArr = this.tmpFloat;
        vector2.set(fArr[0], fArr[1]);
        return this.localPoint;
    }

    public ManifoldPoint[] getPoints() {
        int count = jniGetPointCount(this.addr);
        for (int i = 0; i < count; i++) {
            int contactID = jniGetPoint(this.addr, this.tmpFloat, i);
            ManifoldPoint point = this.points[i];
            point.contactID = contactID;
            Vector2 vector2 = point.localPoint;
            float[] fArr = this.tmpFloat;
            vector2.set(fArr[0], fArr[1]);
            float[] fArr2 = this.tmpFloat;
            point.normalImpulse = fArr2[2];
            point.tangentImpulse = fArr2[3];
        }
        return this.points;
    }

    public class ManifoldPoint {
        public int contactID = 0;
        public final Vector2 localPoint = new Vector2();
        public float normalImpulse;
        public float tangentImpulse;

        public ManifoldPoint() {
        }

        public String toString() {
            return "id: " + this.contactID + ", " + this.localPoint + ", " + this.normalImpulse + ", " + this.tangentImpulse;
        }
    }
}
