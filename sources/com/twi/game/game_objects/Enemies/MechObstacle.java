package com.twi.game.game_objects.Enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;
import com.twi.game.game_objects.Enemy;
import com.twi.game.states.PlayState;
import java.util.Random;

public class MechObstacle extends Enemy {
    private Rectangle borders = new Rectangle();
    private long lastTime = TimeUtils.nanoTime();
    private Random rand = new Random();
    private boolean save = false;
    private float saveSpeed;
    private float speed;
    private int state = 1;

    public MechObstacle(int x, int y, float speed2) {
        this.texture = new Texture("GameScreen/Enemies/Mech/mech-1.png");
        this.borders.set((float) x, (float) y, (float) (this.texture.getWidth() / 3), (float) (this.texture.getHeight() / 2));
        this.position = new Vector3((float) x, (float) y, 0.0f);
        this.speed = speed2;
        this.saveSpeed = speed2;
    }

    public void moving(boolean gameState) {
        if (gameState) {
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
        this.borders.set(this.position.x + ((float) (this.texture.getWidth() / 3)), this.position.y + ((float) (this.texture.getHeight() / 3)), (float) (this.texture.getWidth() / 3), (float) (this.texture.getHeight() / 2));
    }

    public void animation() {
        if (PlayState.getGameState() && TimeUtils.nanoTime() - this.lastTime > 75000000) {
            int i = this.state;
            if (i == 10) {
                this.texture.dispose();
                this.texture = new Texture("GameScreen/Enemies/Mech/mech-1.png");
                this.state = 1;
                this.lastTime = TimeUtils.nanoTime();
            } else if (i == 1) {
                this.texture.dispose();
                this.texture = new Texture("GameScreen/Enemies/Mech/mech-2.png");
                this.state = 2;
                this.lastTime = TimeUtils.nanoTime();
            } else if (i == 2) {
                this.texture.dispose();
                this.texture = new Texture("GameScreen/Enemies/Mech/mech-3.png");
                this.state = 3;
                this.lastTime = TimeUtils.nanoTime();
            } else if (i == 3) {
                this.texture.dispose();
                this.texture = new Texture("GameScreen/Enemies/Mech/mech-4.png");
                this.state = 4;
                this.lastTime = TimeUtils.nanoTime();
            } else if (i == 4) {
                this.texture.dispose();
                this.texture = new Texture("GameScreen/Enemies/Mech/mech-5.png");
                this.state = 5;
                this.lastTime = TimeUtils.nanoTime();
            } else if (i == 5) {
                this.texture.dispose();
                this.texture = new Texture("GameScreen/Enemies/Mech/mech-6.png");
                this.state = 6;
                this.lastTime = TimeUtils.nanoTime();
            } else if (i == 6) {
                this.texture.dispose();
                this.texture = new Texture("GameScreen/Enemies/Mech/mech-7.png");
                this.state = 7;
                this.lastTime = TimeUtils.nanoTime();
            } else if (i == 7) {
                this.texture.dispose();
                this.texture = new Texture("GameScreen/Enemies/Mech/mech-8.png");
                this.state = 8;
                this.lastTime = TimeUtils.nanoTime();
            } else if (i == 8) {
                this.texture.dispose();
                this.texture = new Texture("GameScreen/Enemies/Mech/mech-9.png");
                this.state = 9;
                this.lastTime = TimeUtils.nanoTime();
            } else if (i == 9) {
                this.texture.dispose();
                this.texture = new Texture("GameScreen/Enemies/Mech/mech-10.png");
                this.state = 10;
                this.lastTime = TimeUtils.nanoTime();
            }
        }
    }

    public boolean contact(Rectangle player) {
        return player.overlaps(this.borders);
    }

    public boolean isOutOfScreen() {
        return this.position.x + 100.0f < 0.0f;
    }

    public Texture getTexture() {
        return this.texture;
    }

    public Vector3 getPosition() {
        return this.position;
    }

    public void setSpeed(float x) {
        this.speed = x;
        this.saveSpeed = this.speed;
    }

    public void dispose() {
        this.texture.dispose();
    }
}
