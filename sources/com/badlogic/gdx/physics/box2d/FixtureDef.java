package com.badlogic.gdx.physics.box2d;

public class FixtureDef {
    public float density = 0.0f;
    public final Filter filter = new Filter();
    public float friction = 0.2f;
    public boolean isSensor = false;
    public float restitution = 0.0f;
    public Shape shape;
}
