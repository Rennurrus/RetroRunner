package com.badlogic.gdx.physics.box2d;

public interface ContactFilter {
    boolean shouldCollide(Fixture fixture, Fixture fixture2);
}
