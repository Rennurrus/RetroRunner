package com.twi.game.game_objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class PlayerBullet {
    public Rectangle hit_box;
    public Vector3 position;
    public int state;
    public Texture texture = new Texture("Player/Shot/shot-1.png");

    public PlayerBullet(int x, int y) {
        this.hit_box = new Rectangle((float) x, (float) y, (float) this.texture.getWidth(), (float) this.texture.getHeight());
        this.state = 1;
        this.position = new Vector3((float) x, (float) y, 0.0f);
    }

    public void update() {
        this.position.x += 10.0f;
        this.hit_box.set(this.position.x, this.position.y, (float) this.texture.getWidth(), (float) this.texture.getHeight());
        this.state++;
        if (this.state > 3) {
            this.state = 1;
        }
        this.texture.dispose();
        this.texture = new Texture("Player/Shot/shot-" + this.state + ".png");
    }

    public void render(SpriteBatch SpB) {
        SpB.draw(this.texture, this.position.x, this.position.y);
    }

    public Rectangle getHit_box() {
        return this.hit_box;
    }

    public boolean isOutOfScreen() {
        return this.position.x > 810.0f;
    }

    public void dispose() {
        this.texture.dispose();
    }
}
