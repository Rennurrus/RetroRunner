package com.twi.game.states;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public abstract class State {
    protected OrthographicCamera camera = new OrthographicCamera();
    protected GameStatesManager gsm;
    protected Vector3 mouse = new Vector3();

    public abstract void dispose();

    /* access modifiers changed from: protected */
    public abstract void handleInput();

    public abstract void pause();

    public abstract void render(SpriteBatch spriteBatch);

    public abstract void resume();

    public abstract void update(float f);

    public State(GameStatesManager gsm2) {
        this.gsm = gsm2;
    }
}
