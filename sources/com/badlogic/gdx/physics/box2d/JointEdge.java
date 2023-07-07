package com.badlogic.gdx.physics.box2d;

public class JointEdge {
    public final Joint joint;
    public final Body other;

    protected JointEdge(Body other2, Joint joint2) {
        this.other = other2;
        this.joint = joint2;
    }
}
