package com.twi.game.Managers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.twi.game.game_objects.GroundTexture;
import java.util.ArrayList;
import java.util.Iterator;

public class GroundManager implements ManagerIntergace {
    private static final int TRANSPORT_NUM = 2;
    private ArrayList<GroundTexture> ground = new ArrayList<>();
    private float speed = 5.0f;

    public GroundManager() {
        for (int i = 0; i < 2; i++) {
            this.ground.add(new GroundTexture((i * 1160) - 165, -80, this.speed));
        }
    }

    public void update() {
        Iterator<GroundTexture> it = this.ground.iterator();
        while (it.hasNext()) {
            it.next().update();
        }
        for (int i = 0; i < 2; i++) {
            if (this.ground.get(i).isOutOfScreen()) {
                this.ground.get(i).dispose();
                this.ground.remove(i);
                ArrayList<GroundTexture> arrayList = this.ground;
                arrayList.add(new GroundTexture((((int) arrayList.get(0).getPosition().x) + this.ground.get(0).getTexture().getWidth()) - 171, -80, this.speed));
            }
        }
    }

    public void addSpeed(float x) {
        this.speed += x;
        Iterator<GroundTexture> it = this.ground.iterator();
        while (it.hasNext()) {
            it.next().setSpeed(this.speed);
        }
    }

    public void render(SpriteBatch SpB) {
        Iterator<GroundTexture> it = this.ground.iterator();
        while (it.hasNext()) {
            GroundTexture A = it.next();
            SpB.draw(A.getTexture(), A.getPosition().x, A.getPosition().y);
        }
    }

    public void dispose() {
        Iterator<GroundTexture> it = this.ground.iterator();
        while (it.hasNext()) {
            it.next().dispose();
        }
    }
}
