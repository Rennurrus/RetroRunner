package com.twi.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.twi.game.states.GameStatesManager;
import com.twi.game.states.MenuState;

public class MainGame extends ApplicationAdapter {
    public static final int HEIGHT = 480;
    public static final int WIDTH = 800;
    private SpriteBatch batch;
    private GameStatesManager gsm;

    public void create() {
        this.batch = new SpriteBatch();
        this.gsm = new GameStatesManager();
        Gdx.gl.glClearColor(255.0f, 255.0f, 255.0f, 1.0f);
        GameStatesManager gameStatesManager = this.gsm;
        gameStatesManager.push(new MenuState(gameStatesManager), false);
    }

    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        this.gsm.update(Gdx.graphics.getDeltaTime());
        this.gsm.render(this.batch);
    }

    public void pause() {
        this.gsm.pause();
    }

    public void resume() {
        this.gsm.resume();
    }
}
