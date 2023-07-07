package com.badlogic.gdx.physics.box2d;

import com.badlogic.gdx.math.Vector2;

public class BodyDef {
    public boolean active = true;
    public boolean allowSleep = true;
    public float angle = 0.0f;
    public float angularDamping = 0.0f;
    public float angularVelocity = 0.0f;
    public boolean awake = true;
    public boolean bullet = false;
    public boolean fixedRotation = false;
    public float gravityScale = 1.0f;
    public float linearDamping = 0.0f;
    public final Vector2 linearVelocity = new Vector2();
    public final Vector2 position = new Vector2();
    public BodyType type = BodyType.StaticBody;

    public enum BodyType {
        StaticBody(0),
        KinematicBody(1),
        DynamicBody(2);
        
        private int value;

        private BodyType(int value2) {
            this.value = value2;
        }

        public int getValue() {
            return this.value;
        }
    }
}
