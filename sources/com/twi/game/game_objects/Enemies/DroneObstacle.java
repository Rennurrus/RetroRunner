package com.twi.game.game_objects.Enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.twi.game.game_objects.Enemy;
import java.util.Random;

public class DroneObstacle extends Enemy {
    private Rectangle borders = new Rectangle();
    private Random rand = new Random();
    private boolean save = false;
    private float saveSpeed;
    private float speed;

    public DroneObstacle(int x, int y, float speed2) {
        this.texture = new Texture("GameScreen/Enemies/Drone/drone-1.png");
        this.borders.set((float) x, (float) y, (float) this.texture.getWidth(), (float) this.texture.getHeight());
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
        this.borders.set(this.position.x + ((float) (this.texture.getWidth() / 3)), this.position.y, (float) (this.texture.getWidth() / 2), (float) (this.texture.getHeight() / 2));
    }

    public boolean contact(Rectangle player) {
        return player.overlaps(this.borders);
    }

    public boolean isOutOfScreen() {
        return this.position.x + 50.0f < 0.0f;
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
