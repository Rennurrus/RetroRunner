package com.twi.game.game_objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;

public class BackGroundMenuObject {
    private boolean i;
    private Vector3 position;
    private int speed;
    private int speed_ration = ((int) ((Math.random() * 3.0d) + 1.0d));
    private Texture texture;

    public BackGroundMenuObject(int x, int y, boolean i2) {
        this.i = i2;
        this.position = new Vector3((float) x, (float) y, 0.0f);
        int rand = (int) ((Math.random() * 20.0d) + 1.0d);
        if (!i2) {
            if (rand < 3) {
                this.texture = new Texture("Menu/BackgroundBonus/v-police.png");
                this.speed = 7;
            } else if (rand >= 3 && rand < 5) {
                this.texture = new Texture("Menu/BackgroundBonus/v-green-i.png");
                this.speed = 6;
            } else if (rand >= 5 && rand < 7) {
                this.texture = new Texture("Menu/BackgroundBonus/v-magenta-i.png");
                this.speed = 6;
            } else if (rand >= 7 && rand < 9) {
                this.texture = new Texture("Menu/BackgroundBonus/v-goldblack-i.png");
                this.speed = 4;
            } else if (rand >= 9 && rand < 11) {
                this.texture = new Texture("Menu/BackgroundBonus/v-brow-i.png");
                this.speed = 8;
            } else if (rand >= 11 && rand < 13) {
                this.texture = new Texture("Menu/BackgroundBonus/v-red.png");
                this.speed = 5;
            } else if (rand >= 13 && rand < 15) {
                this.texture = new Texture("Menu/BackgroundBonus/v-yellow.png");
                this.speed = 10;
            } else if (rand >= 15 && rand < 17) {
                this.texture = new Texture("Menu/BackgroundBonus/v-redwhite-i.png");
                this.speed = 5;
            } else if (rand == 18 || rand == 17) {
                this.texture = new Texture("Menu/BackgroundBonus/v-purple-i.png");
                this.speed = 4;
            } else if (rand == 19) {
                this.texture = new Texture("Menu/BackgroundBonus/v-truck.png");
                this.speed = 3;
            } else if (rand == 20) {
                this.texture = new Texture("Menu/BackgroundBonus/v-blue.png");
                this.speed = 1;
            }
        } else if (rand < 3) {
            this.texture = new Texture("Menu/BackgroundBonus/v-police-i.png");
            this.speed = -7;
        } else if (rand >= 3 && rand < 5) {
            this.texture = new Texture("Menu/BackgroundBonus/v-green.png");
            this.speed = -6;
        } else if (rand >= 5 && rand < 7) {
            this.texture = new Texture("Menu/BackgroundBonus/v-magenta.png");
            this.speed = -6;
        } else if (rand >= 7 && rand < 9) {
            this.texture = new Texture("Menu/BackgroundBonus/v-goldblack.png");
            this.speed = -4;
        } else if (rand >= 9 && rand < 11) {
            this.texture = new Texture("Menu/BackgroundBonus/v-brow.png");
            this.speed = -8;
        } else if (rand >= 11 && rand < 13) {
            this.texture = new Texture("Menu/BackgroundBonus/v-red-i.png");
            this.speed = -5;
        } else if (rand >= 13 && rand < 15) {
            this.texture = new Texture("Menu/BackgroundBonus/v-yellow-i.png");
            this.speed = -10;
        } else if (rand >= 15 && rand < 17) {
            this.texture = new Texture("Menu/BackgroundBonus/v-redwhite.png");
            this.speed = -5;
        } else if (rand == 18 || rand == 17) {
            this.texture = new Texture("Menu/BackgroundBonus/v-purple.png");
            this.speed = -4;
        } else if (rand == 19) {
            this.texture = new Texture("Menu/BackgroundBonus/v-truck-i.png");
            this.speed = -3;
        } else if (rand == 20) {
            this.texture = new Texture("Menu/BackgroundBonus/v-blue-i.png");
            this.speed = -1;
        }
    }

    public void update() {
        this.position.x -= (float) (this.speed * this.speed_ration);
    }

    public boolean isOutOfScreen() {
        if (!this.i) {
            if (this.position.x + ((float) this.texture.getWidth()) + 10.0f < 0.0f) {
                return true;
            }
            return false;
        } else if (this.position.x + 10.0f > 800.0f) {
            return true;
        } else {
            return false;
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
