package com.twi.game.game_objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.twi.game.states.PlayState;

public class GroundTexture {
    private boolean gameState;
    private Vector3 position;
    private boolean save = false;
    private float saveSpeed;
    private float speed = 5.0f;
    private Texture texture;

    public GroundTexture(int x, int y, float speed2) {
        this.position = new Vector3((float) x, (float) y, 0.0f);
        this.speed = speed2;
        this.saveSpeed = speed2;
        this.texture = new Texture("test.png");
    }

    public void update() {
        this.gameState = PlayState.getGameState();
        if (this.gameState) {
            this.speed = this.saveSpeed;
            this.save = false;
        } else {
            if (!this.save) {
                this.saveSpeed = this.speed;
                this.save = true;
            }
            this.speed = 0.0f;
        }
        this.position.x -= this.speed;
    }

    public boolean isOutOfScreen() {
        return (this.position.x + 1330.0f) + (this.speed - 5.0f) < 0.0f;
    }

    public void setSpeed(float x) {
        this.speed = x;
        this.saveSpeed = x;
    }

    public void dispose() {
        this.texture.dispose();
    }

    public Texture getTexture() {
        return this.texture;
    }

    public Vector3 getPosition() {
        return this.position;
    }
}
