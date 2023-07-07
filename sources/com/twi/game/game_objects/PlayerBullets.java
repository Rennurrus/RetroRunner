package com.twi.game.game_objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.ArrayList;
import java.util.Iterator;

public class PlayerBullets {
    public ArrayList<PlayerBullet> playerBullets = new ArrayList<>();

    public void shot(int x, int y) {
        this.playerBullets.add(new PlayerBullet(x, y));
    }

    public void update() {
        if (!this.playerBullets.isEmpty()) {
            for (int j = 0; j < this.playerBullets.size(); j++) {
                this.playerBullets.get(j).update();
                if (this.playerBullets.get(j).isOutOfScreen()) {
                    this.playerBullets.get(j).dispose();
                    this.playerBullets.remove(j);
                    if (this.playerBullets.size() == j) {
                        return;
                    }
                }
            }
        }
    }

    public void render(SpriteBatch SpB) {
        if (!this.playerBullets.isEmpty()) {
            Iterator<PlayerBullet> it = this.playerBullets.iterator();
            while (it.hasNext()) {
                it.next().render(SpB);
            }
        }
    }

    public void destroy(int index) {
        System.out.println(index);
        this.playerBullets.get(index).dispose();
        this.playerBullets.remove(index);
    }

    public void dispose() {
        Iterator<PlayerBullet> it = this.playerBullets.iterator();
        while (it.hasNext()) {
            it.next().dispose();
        }
    }
}
