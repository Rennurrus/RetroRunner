package com.badlogic.gdx.physics.box2d;

public interface ContactListener {
    void beginContact(Contact contact);

    void endContact(Contact contact);

    void postSolve(Contact contact, ContactImpulse contactImpulse);

    void preSolve(Contact contact, Manifold manifold);
}
