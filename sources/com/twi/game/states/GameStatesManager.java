package com.twi.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.Stack;

public class GameStatesManager {
    private Stack<State> states = new Stack<>();

    public void push(State state, boolean PlayScreen) {
        this.states.push(state);
    }

    public void pop() {
        this.states.pop().dispose();
        System.out.println(this.states.size());
        if (this.states.empty()) {
            Gdx.app.exit();
            System.exit(-1);
        }
    }

    public void set(State state) {
        this.states.pop().dispose();
        this.states.push(state);
    }

    public void update(float dTime) {
        this.states.peek().update(dTime);
    }

    public void render(SpriteBatch SpB) {
        if (!this.states.empty()) {
            this.states.peek().render(SpB);
        }
    }

    public void pause() {
        if (!this.states.empty()) {
            this.states.peek().pause();
        }
    }

    public void resume() {
        this.states.peek().resume();
    }
}
