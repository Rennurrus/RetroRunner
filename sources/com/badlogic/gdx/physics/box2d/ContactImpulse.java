package com.badlogic.gdx.physics.box2d;

public class ContactImpulse {
    long addr;
    final float[] normalImpulses = new float[2];
    final float[] tangentImpulses = new float[2];
    float[] tmp = new float[2];
    final World world;

    private native int jniGetCount(long j);

    private native void jniGetNormalImpulses(long j, float[] fArr);

    private native void jniGetTangentImpulses(long j, float[] fArr);

    protected ContactImpulse(World world2, long addr2) {
        this.world = world2;
        this.addr = addr2;
    }

    public float[] getNormalImpulses() {
        jniGetNormalImpulses(this.addr, this.normalImpulses);
        return this.normalImpulses;
    }

    public float[] getTangentImpulses() {
        jniGetTangentImpulses(this.addr, this.tangentImpulses);
        return this.tangentImpulses;
    }

    public int getCount() {
        return jniGetCount(this.addr);
    }
}
