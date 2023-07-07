package com.twi.game.game_objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.twi.game.states.PlayState;

public class BackGroundObject {
    private boolean gameState = PlayState.getGameState();
    private int num;
    private Vector3 position;
    private boolean save = false;
    private float saveSpeed;
    private float speed;
    private Texture texture;

    public BackGroundObject(int x, int y, float speed2, int num2) {
        this.num = num2;
        this.position = new Vector3((float) x, (float) y, 0.0f);
        if (num2 == 1) {
            int rand = (int) ((Math.random() * 3.0d) + 1.0d);
            if (rand == 1) {
                this.texture = new Texture("GameScreen/BackgroundBonus/1.png");
                this.speed = 1.0f;
            } else if (rand == 2) {
                this.texture = new Texture("GameScreen/BackgroundBonus/2.png");
                this.speed = 1.0f;
            } else if (rand == 3) {
                this.texture = new Texture("GameScreen/BackgroundBonus/2-3.png");
                this.speed = 1.0f;
            }
        } else if (num2 == 2) {
            int rand2 = (int) ((Math.random() * 4.0d) + 1.0d);
            if (rand2 == 1) {
                this.texture = new Texture("GameScreen/BackgroundBonus/3.png");
                this.speed = speed2;
            } else if (rand2 == 2) {
                this.texture = new Texture("GameScreen/BackgroundBonus/4.png");
                this.speed = speed2;
            } else if (rand2 == 3) {
                this.texture = new Texture("GameScreen/BackgroundBonus/5.png");
                this.speed = speed2;
            } else if (rand2 == 4) {
                this.texture = new Texture("GameScreen/BackgroundBonus/6.png");
                this.speed = speed2;
            }
        } else {
            int rand3 = (int) ((Math.random() * 5.0d) + 1.0d);
            if (rand3 == 1) {
                this.texture = new Texture("GameScreen/BackgroundBonus/7.png");
                this.speed = speed2;
            } else if (rand3 == 2) {
                this.texture = new Texture("GameScreen/BackgroundBonus/8.png");
                this.speed = speed2;
            } else if (rand3 == 3) {
                this.texture = new Texture("GameScreen/BackgroundBonus/9.png");
                this.speed = speed2;
            } else if (rand3 == 4) {
                this.texture = new Texture("GameScreen/BackgroundBonus/10.png");
                this.speed = speed2;
            } else if (rand3 == 5) {
                this.texture = new Texture("GameScreen/BackgroundBonus/11.png");
                this.speed = speed2;
            }
        }
        this.saveSpeed = speed2;
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
        return this.position.x + 400.0f < 0.0f;
    }

    public void setSpeed(float x) {
        if (this.num != 1) {
            this.speed = x;
            this.saveSpeed = this.speed;
        }
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
