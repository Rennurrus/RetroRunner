package com.twi.game.Managers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.net.HttpStatus;
import com.twi.game.game_objects.BackGroundObject;
import java.util.ArrayList;
import java.util.Iterator;

public class PlayBackgroundManager implements ManagerIntergace {
    private static final int BONUS1_NUM = 1;
    private static final int BONUS2_NUM = 4;
    private static final int BONUS3_NUM = 8;
    private Texture background = new Texture("GameScreen/Background/background" + ((int) ((Math.random() * 10.0d) + 1.0d)) + ".png");
    private ArrayList<BackGroundObject> bonus = new ArrayList<>();
    private ArrayList<BackGroundObject> bonus_2 = new ArrayList<>();
    private ArrayList<BackGroundObject> bonus_3 = new ArrayList<>();
    private float speed = 5.0f;

    public PlayBackgroundManager() {
        double d = 10.0d;
        this.bonus.add(new BackGroundObject(900, 30, 1.0f, 1));
        for (int i = 0; i < 4; i++) {
            if (i == 0) {
                this.bonus_2.add(new BackGroundObject(((((int) (Math.random() * 10.0d)) + 1) * 30) + HttpStatus.SC_BAD_REQUEST, 80, this.speed, 2));
            } else {
                ArrayList<BackGroundObject> arrayList = this.bonus_2;
                arrayList.add(new BackGroundObject(((int) arrayList.get(i - 1).getPosition().x) + this.bonus_2.get(i - 1).getTexture().getWidth() + (((int) ((Math.random() * 4.0d) + 1.0d)) * HttpStatus.SC_OK), 80, this.speed, 2));
            }
        }
        int i2 = 0;
        while (i2 < 8) {
            if (i2 == 0) {
                this.bonus_3.add(new BackGroundObject(((((int) (Math.random() * d)) + 1) * 30) + HttpStatus.SC_BAD_REQUEST, 50, this.speed / 2.0f, 3));
            } else {
                ArrayList<BackGroundObject> arrayList2 = this.bonus_3;
                arrayList2.add(new BackGroundObject(((int) arrayList2.get(i2 - 1).getPosition().x) + this.bonus_3.get(i2 - 1).getTexture().getWidth() + (((int) ((Math.random() * 2.0d) + 1.0d)) * HttpStatus.SC_INTERNAL_SERVER_ERROR), 50, this.speed / 2.0f, 3));
            }
            i2++;
            d = 10.0d;
        }
    }

    public void update() {
        Iterator<BackGroundObject> it = this.bonus.iterator();
        while (it.hasNext()) {
            it.next().update();
        }
        Iterator<BackGroundObject> it2 = this.bonus_2.iterator();
        while (it2.hasNext()) {
            it2.next().update();
        }
        Iterator<BackGroundObject> it3 = this.bonus_3.iterator();
        while (it3.hasNext()) {
            it3.next().update();
        }
        for (int i = 0; i < 1; i++) {
            if (this.bonus.get(i).isOutOfScreen()) {
                this.bonus.get(i).dispose();
                this.bonus.add(new BackGroundObject(900, 30, 1.0f, 1));
                this.bonus.remove(i);
            }
        }
        for (int i2 = 0; i2 < 4; i2++) {
            if (this.bonus_2.get(i2).isOutOfScreen()) {
                this.bonus_2.get(i2).dispose();
                this.bonus_2.remove(i2);
                ArrayList<BackGroundObject> arrayList = this.bonus_2;
                ArrayList<BackGroundObject> arrayList2 = this.bonus_2;
                arrayList.add(new BackGroundObject(((int) arrayList.get(arrayList.size() - 1).getPosition().x) + arrayList2.get(arrayList2.size() - 1).getTexture().getWidth() + (((int) ((Math.random() * 4.0d) + 1.0d)) * HttpStatus.SC_OK), 80, this.speed, 2));
            }
        }
        for (int i3 = 0; i3 < 8; i3++) {
            if (this.bonus_3.get(i3).isOutOfScreen()) {
                this.bonus_3.get(i3).dispose();
                this.bonus_3.remove(i3);
                ArrayList<BackGroundObject> arrayList3 = this.bonus_3;
                ArrayList<BackGroundObject> arrayList4 = this.bonus_3;
                arrayList3.add(new BackGroundObject(((int) arrayList3.get(arrayList3.size() - 1).getPosition().x) + arrayList4.get(arrayList4.size() - 1).getTexture().getWidth() + (((int) ((Math.random() * 2.0d) + 1.0d)) * HttpStatus.SC_INTERNAL_SERVER_ERROR), 50, this.speed / 2.0f, 3));
            }
        }
    }

    public void render(SpriteBatch SpB) {
        SpB.draw(this.background, 0.0f, 0.0f);
        Iterator<BackGroundObject> it = this.bonus.iterator();
        while (it.hasNext()) {
            BackGroundObject A = it.next();
            SpB.draw(A.getTexture(), A.getPosition().x, A.getPosition().y);
        }
        Iterator<BackGroundObject> it2 = this.bonus_3.iterator();
        while (it2.hasNext()) {
            BackGroundObject C = it2.next();
            SpB.draw(C.getTexture(), C.getPosition().x, C.getPosition().y);
        }
        Iterator<BackGroundObject> it3 = this.bonus_2.iterator();
        while (it3.hasNext()) {
            BackGroundObject B = it3.next();
            SpB.draw(B.getTexture(), B.getPosition().x, B.getPosition().y);
        }
    }

    public void addSpeed(float x) {
        this.speed += x;
        Iterator<BackGroundObject> it = this.bonus_2.iterator();
        while (it.hasNext()) {
            it.next().setSpeed(this.speed);
        }
        Iterator<BackGroundObject> it2 = this.bonus_3.iterator();
        while (it2.hasNext()) {
            it2.next().setSpeed(this.speed / 2.0f);
        }
    }

    public void dispose() {
        Iterator<BackGroundObject> it = this.bonus.iterator();
        while (it.hasNext()) {
            it.next().dispose();
        }
        Iterator<BackGroundObject> it2 = this.bonus_2.iterator();
        while (it2.hasNext()) {
            it2.next().dispose();
        }
        Iterator<BackGroundObject> it3 = this.bonus_3.iterator();
        while (it3.hasNext()) {
            it3.next().dispose();
        }
    }
}
