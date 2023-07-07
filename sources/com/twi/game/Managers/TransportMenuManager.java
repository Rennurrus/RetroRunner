package com.twi.game.Managers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.twi.game.game_objects.BackGroundMenuObject;
import java.util.ArrayList;
import java.util.Iterator;

public class TransportMenuManager implements ManagerIntergace {
    private static final int TRANSPORT_NUM = 4;
    private int speed = 5;
    private ArrayList<BackGroundMenuObject> transport = new ArrayList<>();
    private ArrayList<BackGroundMenuObject> transport_i = new ArrayList<>();

    public TransportMenuManager() {
        for (int i = 0; i < 4; i++) {
            this.transport.add(new BackGroundMenuObject(1200, ((int) ((Math.random() * 4.0d) + 1.0d)) * 96, false));
            this.transport_i.add(new BackGroundMenuObject(-400, (((int) ((Math.random() * 4.0d) + 1.0d)) * 96) - 100, true));
        }
    }

    public void update() {
        Iterator<BackGroundMenuObject> it = this.transport.iterator();
        while (it.hasNext()) {
            it.next().update();
        }
        Iterator<BackGroundMenuObject> it2 = this.transport_i.iterator();
        while (it2.hasNext()) {
            it2.next().update();
        }
        for (int i = 0; i < 4; i++) {
            if (this.transport.get(i).isOutOfScreen()) {
                this.transport.get(i).dispose();
                this.transport.remove(i);
                this.transport.add(new BackGroundMenuObject(1200, ((int) ((Math.random() * 4.0d) + 1.0d)) * 96, false));
            }
            if (this.transport_i.get(i).isOutOfScreen()) {
                this.transport_i.get(i).dispose();
                this.transport_i.remove(i);
                this.transport_i.add(new BackGroundMenuObject(-400, (((int) ((Math.random() * 4.0d) + 1.0d)) * 96) - 100, true));
            }
        }
    }

    public void render(SpriteBatch SpB) {
        Iterator<BackGroundMenuObject> it = this.transport.iterator();
        while (it.hasNext()) {
            BackGroundMenuObject A = it.next();
            SpB.draw(A.getTexture(), A.getPosition().x, A.getPosition().y);
        }
        Iterator<BackGroundMenuObject> it2 = this.transport_i.iterator();
        while (it2.hasNext()) {
            BackGroundMenuObject B = it2.next();
            SpB.draw(B.getTexture(), B.getPosition().x, B.getPosition().y);
        }
    }

    public void dispose() {
        Iterator<BackGroundMenuObject> it = this.transport.iterator();
        while (it.hasNext()) {
            it.next().dispose();
        }
        Iterator<BackGroundMenuObject> it2 = this.transport_i.iterator();
        while (it2.hasNext()) {
            it2.next().dispose();
        }
    }
}
