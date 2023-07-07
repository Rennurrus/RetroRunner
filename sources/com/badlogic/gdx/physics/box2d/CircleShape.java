package com.badlogic.gdx.physics.box2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape;

public class CircleShape extends Shape {
    private final Vector2 position = new Vector2();
    private final float[] tmp = new float[2];

    private native void jniGetPosition(long j, float[] fArr);

    private native void jniSetPosition(long j, float f, float f2);

    private native long newCircleShape();

    public CircleShape() {
        this.addr = newCircleShape();
    }

    protected CircleShape(long addr) {
        this.addr = addr;
    }

    public Shape.Type getType() {
        return Shape.Type.Circle;
    }

    public Vector2 getPosition() {
        jniGetPosition(this.addr, this.tmp);
        Vector2 vector2 = this.position;
        float[] fArr = this.tmp;
        vector2.x = fArr[0];
        vector2.y = fArr[1];
        return vector2;
    }

    public void setPosition(Vector2 position2) {
        jniSetPosition(this.addr, position2.x, position2.y);
    }
}
