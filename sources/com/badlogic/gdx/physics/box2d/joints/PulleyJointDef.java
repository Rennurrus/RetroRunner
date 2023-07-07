package com.badlogic.gdx.physics.box2d.joints;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.JointDef;

public class PulleyJointDef extends JointDef {
    private static final float minPulleyLength = 2.0f;
    public final Vector2 groundAnchorA = new Vector2(-1.0f, 1.0f);
    public final Vector2 groundAnchorB = new Vector2(1.0f, 1.0f);
    public float lengthA = 0.0f;
    public float lengthB = 0.0f;
    public final Vector2 localAnchorA = new Vector2(-1.0f, 0.0f);
    public final Vector2 localAnchorB = new Vector2(1.0f, 0.0f);
    public float ratio = 1.0f;

    public PulleyJointDef() {
        this.type = JointDef.JointType.PulleyJoint;
        this.collideConnected = true;
    }

    public void initialize(Body bodyA, Body bodyB, Vector2 groundAnchorA2, Vector2 groundAnchorB2, Vector2 anchorA, Vector2 anchorB, float ratio2) {
        this.bodyA = bodyA;
        this.bodyB = bodyB;
        this.groundAnchorA.set(groundAnchorA2);
        this.groundAnchorB.set(groundAnchorB2);
        this.localAnchorA.set(bodyA.getLocalPoint(anchorA));
        this.localAnchorB.set(bodyB.getLocalPoint(anchorB));
        this.lengthA = anchorA.dst(groundAnchorA2);
        this.lengthB = anchorB.dst(groundAnchorB2);
        this.ratio = ratio2;
        float f = this.lengthA + (this.lengthB * ratio2);
    }
}
