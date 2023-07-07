package com.twi.game.Managers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.net.HttpStatus;
import com.twi.game.MainGame;
import com.twi.game.game_objects.Enemies.DroneObstacle;
import com.twi.game.game_objects.EnemiesOnTheGround;
import com.twi.game.game_objects.Enemy;
import com.twi.game.game_objects.EnergyBonus;
import com.twi.game.game_objects.PlayerBullets;
import com.twi.game.states.PlayState;
import java.util.ArrayList;
import java.util.Iterator;

public class EnemiesManager extends Enemy implements ManagerIntergace {
    private static final int DRONS_NUM = 2;
    private static final int ENEMIES_NUM = 4;
    private static final int ENERGY_NUM = 2;
    private ArrayList<DroneObstacle> drons = new ArrayList<>();
    private ArrayList<EnemiesOnTheGround> enemies = new ArrayList<>();
    private ArrayList<EnergyBonus> energy = new ArrayList<>();
    private float speed = 5.0f;

    public EnemiesManager() {
        for (int i = 0; i < 4; i++) {
            if (i == 0) {
                this.enemies.add(new EnemiesOnTheGround(((((int) (Math.random() * 10.0d)) + 1) * 30) + HttpStatus.SC_BAD_REQUEST, 50, this.speed));
            } else {
                ArrayList<EnemiesOnTheGround> arrayList = this.enemies;
                int width = ((int) arrayList.get(i - 1).getPosition().x) + this.enemies.get(i - 1).getTexture().getWidth() + HttpStatus.SC_OK + (((int) ((Math.random() * 3.0d) + 1.0d)) * 40);
                float f = this.speed;
                arrayList.add(new EnemiesOnTheGround(width + (((int) (f - 5.0f)) * 30), 50, f));
            }
        }
        for (int i2 = 0; i2 < 2; i2++) {
            if (i2 == 0) {
                this.energy.add(new EnergyBonus((((int) (Math.random() * 3.0d)) + 1) * MainGame.WIDTH, 50, this.speed));
            } else {
                ArrayList<EnergyBonus> arrayList2 = this.energy;
                ArrayList<EnergyBonus> arrayList3 = this.energy;
                arrayList2.add(new EnergyBonus(((int) arrayList2.get(arrayList2.size() - 1).getPosition().x) + arrayList3.get(arrayList3.size() - 1).getTexture().getWidth() + (((int) ((Math.random() * 3.0d) + 4.0d)) * MainGame.WIDTH) + (((int) (this.speed - 5.0f)) * 10), ((int) ((Math.random() * 3.0d) + 1.0d)) * 20, this.speed));
            }
        }
        for (int i3 = 0; i3 < 2; i3++) {
            if (i3 == 0) {
                this.drons.add(new DroneObstacle((((int) (Math.random() * 3.0d)) + 1) * MainGame.WIDTH, ((((int) (Math.random() * 3.0d)) + 1) * 30) + 20, this.speed + 3.0f));
            } else {
                ArrayList<DroneObstacle> arrayList4 = this.drons;
                arrayList4.add(new DroneObstacle(((int) arrayList4.get(i3 - 1).getPosition().x) + this.drons.get(i3 - 1).getTexture().getWidth() + 2400 + (((int) ((Math.random() * 3.0d) + 1.0d)) * HttpStatus.SC_INTERNAL_SERVER_ERROR) + (((int) (this.speed - 5.0f)) * 30), ((((int) (Math.random() * 3.0d)) + 1) * 30) + 20, this.speed + 3.0f));
            }
        }
    }

    public void update() {
        Iterator<EnemiesOnTheGround> it = this.enemies.iterator();
        while (it.hasNext()) {
            it.next().animation();
        }
        Iterator<EnergyBonus> it2 = this.energy.iterator();
        while (it2.hasNext()) {
            it2.next().animation();
        }
        if (PlayState.getGameState()) {
            Iterator<EnemiesOnTheGround> it3 = this.enemies.iterator();
            while (it3.hasNext()) {
                it3.next().moving(true);
            }
            if (PlayState.score > 20) {
                Iterator<DroneObstacle> it4 = this.drons.iterator();
                while (it4.hasNext()) {
                    it4.next().moving(true);
                }
            }
        } else {
            Iterator<EnemiesOnTheGround> it5 = this.enemies.iterator();
            while (it5.hasNext()) {
                it5.next().moving(false);
            }
            Iterator<DroneObstacle> it6 = this.drons.iterator();
            while (it6.hasNext()) {
                it6.next().moving(false);
            }
        }
        if (PlayState.getGameState()) {
            Iterator<EnergyBonus> it7 = this.energy.iterator();
            while (it7.hasNext()) {
                it7.next().moving(true);
            }
        } else {
            Iterator<EnergyBonus> it8 = this.energy.iterator();
            while (it8.hasNext()) {
                it8.next().moving(false);
            }
        }
        if (this.enemies.get(0).IsOutOfScreen()) {
            this.enemies.get(0).dispose();
            this.enemies.remove(0);
            ArrayList<EnemiesOnTheGround> arrayList = this.enemies;
            ArrayList<EnemiesOnTheGround> arrayList2 = this.enemies;
            int width = ((int) arrayList.get(arrayList.size() - 1).getPosition().x) + arrayList2.get(arrayList2.size() - 1).getTexture().getWidth() + HttpStatus.SC_OK + (((int) ((Math.random() * 3.0d) + 1.0d)) * 40);
            float f = this.speed;
            arrayList.add(new EnemiesOnTheGround(width + (((int) (f - 5.0f)) * 30), 50, f));
        }
        if (this.energy.get(0).isOutOfScreen()) {
            this.energy.get(0).dispose();
            this.energy.remove(0);
            ArrayList<EnergyBonus> arrayList3 = this.energy;
            ArrayList<EnergyBonus> arrayList4 = this.energy;
            arrayList3.add(new EnergyBonus(((int) arrayList3.get(arrayList3.size() - 1).getPosition().x) + arrayList4.get(arrayList4.size() - 1).getTexture().getWidth() + (((int) ((Math.random() * 3.0d) + 4.0d)) * MainGame.WIDTH) + (((int) (this.speed - 5.0f)) * 10), ((int) ((Math.random() * 3.0d) + 1.0d)) * 20, this.speed));
        }
        if (this.drons.get(0).isOutOfScreen()) {
            this.drons.get(0).dispose();
            this.drons.remove(0);
            ArrayList<DroneObstacle> arrayList5 = this.drons;
            ArrayList<DroneObstacle> arrayList6 = this.drons;
            arrayList5.add(new DroneObstacle(((int) arrayList5.get(arrayList5.size() - 1).getPosition().x) + arrayList6.get(arrayList6.size() - 1).getTexture().getWidth() + 2400 + (((int) ((Math.random() * 3.0d) + 1.0d)) * HttpStatus.SC_INTERNAL_SERVER_ERROR) + (((int) (this.speed - 5.0f)) * 30), ((((int) (Math.random() * 3.0d)) + 1) * 40) + 20, this.speed + 3.0f));
        }
    }

    public boolean contact(Rectangle player) {
        Iterator<EnemiesOnTheGround> it = this.enemies.iterator();
        while (it.hasNext()) {
            if (it.next().contact(player)) {
                return true;
            }
        }
        return false;
    }

    public boolean contact_drone(Rectangle player) {
        Iterator<DroneObstacle> it = this.drons.iterator();
        while (it.hasNext()) {
            if (it.next().contact(player)) {
                return true;
            }
        }
        return false;
    }

    public void BulletContact(PlayerBullets playerBullets) {
        for (int j = 0; j < playerBullets.playerBullets.size(); j++) {
            for (int i = 0; i < this.enemies.size(); i++) {
                if (contact(playerBullets.playerBullets.get(j).getHit_box())) {
                    this.enemies.get(idContact(playerBullets.playerBullets.get(j).getHit_box())).dispose();
                    ArrayList<EnemiesOnTheGround> arrayList = this.enemies;
                    ArrayList<EnemiesOnTheGround> arrayList2 = this.enemies;
                    int width = ((int) arrayList.get(arrayList.size() - 1).getPosition().x) + arrayList2.get(arrayList2.size() - 1).getTexture().getWidth() + HttpStatus.SC_OK + (((int) ((Math.random() * 3.0d) + 1.0d)) * 40);
                    float f = this.speed;
                    arrayList.add(new EnemiesOnTheGround(width + (((int) (f - 5.0f)) * 30), 50, f));
                    this.enemies.remove(idContact(playerBullets.playerBullets.get(j).getHit_box()));
                    playerBullets.destroy(j);
                    if (playerBullets.playerBullets.size() == j) {
                        break;
                    }
                }
            }
            for (int i2 = 0; i2 < this.drons.size(); i2++) {
                if (j < playerBullets.playerBullets.size() && contact_drone(playerBullets.playerBullets.get(j).getHit_box())) {
                    this.drons.get(idContact_drone(playerBullets.playerBullets.get(j).getHit_box())).dispose();
                    ArrayList<DroneObstacle> arrayList3 = this.drons;
                    ArrayList<DroneObstacle> arrayList4 = this.drons;
                    arrayList3.add(new DroneObstacle(((int) arrayList3.get(arrayList3.size() - 1).getPosition().x) + arrayList4.get(arrayList4.size() - 1).getTexture().getWidth() + 2400 + (((int) ((Math.random() * 3.0d) + 1.0d)) * HttpStatus.SC_INTERNAL_SERVER_ERROR) + (((int) (this.speed - 5.0f)) * 30), ((((int) (Math.random() * 3.0d)) + 1) * 40) + 20, this.speed + 3.0f));
                    this.drons.remove(idContact_drone(playerBullets.playerBullets.get(j).getHit_box()));
                    playerBullets.destroy(j);
                    if (playerBullets.playerBullets.size() == j) {
                        break;
                    }
                }
            }
        }
    }

    public boolean bonus_contact(Rectangle player) {
        Iterator<EnergyBonus> it = this.energy.iterator();
        while (it.hasNext()) {
            if (it.next().contact(player)) {
                return true;
            }
        }
        return false;
    }

    public int idContact(Rectangle player) {
        Iterator<EnemiesOnTheGround> it = this.enemies.iterator();
        while (it.hasNext()) {
            EnemiesOnTheGround enemy = it.next();
            if (enemy.contact(player)) {
                return this.enemies.indexOf(enemy);
            }
        }
        return -1;
    }

    public int idContact_drone(Rectangle player) {
        Iterator<DroneObstacle> it = this.drons.iterator();
        while (it.hasNext()) {
            DroneObstacle drone = it.next();
            if (drone.contact(player)) {
                return this.drons.indexOf(drone);
            }
        }
        return -1;
    }

    public int idBonusContact(Rectangle player) {
        Iterator<EnergyBonus> it = this.energy.iterator();
        while (it.hasNext()) {
            EnergyBonus nrj = it.next();
            if (nrj.contact(player)) {
                return this.energy.indexOf(nrj);
            }
        }
        return -1;
    }

    public void destroy(Rectangle player) {
        this.enemies.get(idContact(player)).dispose();
        this.enemies.remove(idContact(player));
        ArrayList<EnemiesOnTheGround> arrayList = this.enemies;
        ArrayList<EnemiesOnTheGround> arrayList2 = this.enemies;
        int width = ((int) arrayList.get(arrayList.size() - 1).getPosition().x) + arrayList2.get(arrayList2.size() - 1).getTexture().getWidth() + HttpStatus.SC_OK + (((int) ((Math.random() * 3.0d) + 1.0d)) * 40);
        float f = this.speed;
        arrayList.add(new EnemiesOnTheGround(width + (((int) (f - 5.0f)) * 30), 50, f));
    }

    public void destroy_drone(Rectangle player) {
        this.drons.get(idContact_drone(player)).dispose();
        this.drons.remove(idContact_drone(player));
        ArrayList<DroneObstacle> arrayList = this.drons;
        ArrayList<DroneObstacle> arrayList2 = this.drons;
        arrayList.add(new DroneObstacle(((int) arrayList.get(arrayList.size() - 1).getPosition().x) + arrayList2.get(arrayList2.size() - 1).getTexture().getWidth() + 2400 + (((int) ((Math.random() * 3.0d) + 1.0d)) * HttpStatus.SC_INTERNAL_SERVER_ERROR) + (((int) (this.speed - 5.0f)) * 30), ((((int) (Math.random() * 3.0d)) + 1) * 40) + 20, this.speed + 3.0f));
    }

    public void destroy_bonus(Rectangle player) {
        this.energy.get(idBonusContact(player)).dispose();
        this.energy.remove(idBonusContact(player));
        ArrayList<EnergyBonus> arrayList = this.energy;
        ArrayList<EnergyBonus> arrayList2 = this.energy;
        arrayList.add(new EnergyBonus(((int) arrayList.get(arrayList.size() - 1).getPosition().x) + arrayList2.get(arrayList2.size() - 1).getTexture().getWidth() + (((int) ((Math.random() * 3.0d) + 4.0d)) * MainGame.WIDTH) + (((int) (this.speed - 5.0f)) * 10), ((int) ((Math.random() * 3.0d) + 1.0d)) * 20, this.speed));
    }

    public void render(SpriteBatch SpB) {
        Iterator<EnemiesOnTheGround> it = this.enemies.iterator();
        while (it.hasNext()) {
            EnemiesOnTheGround enemy = it.next();
            SpB.draw(enemy.getTexture(), enemy.getPosition().x, enemy.getPosition().y);
        }
        Iterator<EnergyBonus> it2 = this.energy.iterator();
        while (it2.hasNext()) {
            EnergyBonus nrj = it2.next();
            SpB.draw(nrj.getTexture(), nrj.getPosition().x, nrj.getPosition().y);
        }
        Iterator<DroneObstacle> it3 = this.drons.iterator();
        while (it3.hasNext()) {
            DroneObstacle drone = it3.next();
            SpB.draw(drone.getTexture(), drone.getPosition().x, drone.getPosition().y);
        }
    }

    public void addSpeed(float x) {
        this.speed += x;
        Iterator<EnemiesOnTheGround> it = this.enemies.iterator();
        while (it.hasNext()) {
            it.next().setSpeed(this.speed);
        }
        Iterator<EnergyBonus> it2 = this.energy.iterator();
        while (it2.hasNext()) {
            it2.next().setSpeed(this.speed);
        }
    }

    public void dispose() {
        Iterator<EnemiesOnTheGround> it = this.enemies.iterator();
        while (it.hasNext()) {
            it.next().dispose();
        }
        Iterator<EnergyBonus> it2 = this.energy.iterator();
        while (it2.hasNext()) {
            it2.next().dispose();
        }
        Iterator<DroneObstacle> it3 = this.drons.iterator();
        while (it3.hasNext()) {
            it3.next().dispose();
        }
    }
}
