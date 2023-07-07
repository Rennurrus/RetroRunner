package com.twi.game.game_objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.TimeUtils;
import com.twi.game.states.PlayState;

public class EnergyBonus extends Enemy implements Pool.Poolable {
    private Rectangle borders = new Rectangle();
    private long lastTime = TimeUtils.nanoTime();
    private boolean save = false;
    private float saveSpeed;
    private float speed;
    private int state = 1;

    public EnergyBonus(int x, int y, float speed2) {
        this.texture = new Texture("GameScreen/energy-bonus.png");
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
        this.borders.set(this.position.x, this.position.y, (float) this.texture.getWidth(), (float) this.texture.getHeight());
    }

    public void animation() {
        if (PlayState.getGameState() && TimeUtils.nanoTime() - this.lastTime > 50000000) {
            int i = this.state;
            if (i == 10) {
                this.position.y -= 1.0f;
                this.borders.set(this.position.x, this.position.y, (float) this.texture.getWidth(), (float) this.texture.getHeight());
                this.state = 1;
                this.lastTime = TimeUtils.nanoTime();
            } else if (i == 1) {
                this.position.y -= 1.0f;
                this.borders.set(this.position.x, this.position.y, (float) this.texture.getWidth(), (float) this.texture.getHeight());
                this.state = 2;
                this.lastTime = TimeUtils.nanoTime();
            } else if (i == 2) {
                this.position.y -= 1.0f;
                this.borders.set(this.position.x, this.position.y, (float) this.texture.getWidth(), (float) this.texture.getHeight());
                this.state = 3;
                this.lastTime = TimeUtils.nanoTime();
            } else if (i == 3) {
                this.position.y -= 1.0f;
                this.borders.set(this.position.x, this.position.y, (float) this.texture.getWidth(), (float) this.texture.getHeight());
                this.state = 4;
                this.lastTime = TimeUtils.nanoTime();
            } else if (i == 4) {
                this.position.y += 1.0f;
                this.borders.set(this.position.x, this.position.y, (float) this.texture.getWidth(), (float) this.texture.getHeight());
                this.state = 5;
                this.lastTime = TimeUtils.nanoTime();
            } else if (i == 5) {
                this.position.y += 1.0f;
                this.borders.set(this.position.x, this.position.y, (float) this.texture.getWidth(), (float) this.texture.getHeight());
                this.state = 6;
                this.lastTime = TimeUtils.nanoTime();
            } else if (i == 6) {
                this.position.y += 1.0f;
                this.borders.set(this.position.x, this.position.y, (float) this.texture.getWidth(), (float) this.texture.getHeight());
                this.state = 7;
                this.lastTime = TimeUtils.nanoTime();
            } else if (i == 7) {
                this.position.y += 1.0f;
                this.borders.set(this.position.x, this.position.y, (float) this.texture.getWidth(), (float) this.texture.getHeight());
                this.state = 8;
                this.lastTime = TimeUtils.nanoTime();
            } else if (i == 8) {
                this.position.y += 1.0f;
                this.borders.set(this.position.x, this.position.y, (float) this.texture.getWidth(), (float) this.texture.getHeight());
                this.state = 9;
                this.lastTime = TimeUtils.nanoTime();
            } else if (i == 9) {
                this.position.y += 1.0f;
                this.borders.set(this.position.x, this.position.y, (float) this.texture.getWidth(), (float) this.texture.getHeight());
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

    public void reset() {
    }
}
