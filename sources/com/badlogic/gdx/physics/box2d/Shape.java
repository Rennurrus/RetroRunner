package com.badlogic.gdx.physics.box2d;

public abstract class Shape {
    protected long addr;

    public enum Type {
        Circle,
        Edge,
        Polygon,
        Chain
    }

    private native void jniDispose(long j);

    private native int jniGetChildCount(long j);

    private native float jniGetRadius(long j);

    protected static native int jniGetType(long j);

    private native void jniSetRadius(long j, float f);

    public abstract Type getType();

    public float getRadius() {
        return jniGetRadius(this.addr);
    }

    public void setRadius(float radius) {
        jniSetRadius(this.addr, radius);
    }

    public void dispose() {
        jniDispose(this.addr);
    }

    public int getChildCount() {
        return jniGetChildCount(this.addr);
    }
}
