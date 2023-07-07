package com.twi.game.game_objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.twi.game.game_objects.Enemies.BipedalObstacle;
import com.twi.game.game_objects.Enemies.MechObstacle;
import com.twi.game.game_objects.Enemies.TurretObstacle;
import com.twi.game.states.PlayState;

public class EnemiesOnTheGround extends Enemy {
    private BipedalObstacle bipedal;
    private MechObstacle mec;
    private int rand;
    private TurretObstacle turret;

    public EnemiesOnTheGround(int x, int y, float speed) {
        if (PlayState.score <= 3) {
            this.rand = 1;
        }
        if (PlayState.score > 3) {
            this.rand = ((int) (Math.random() * 2.0d)) + 1;
        }
        if (PlayState.score > 9) {
            this.rand = ((int) (Math.random() * 3.0d)) + 1;
        }
        int i = this.rand;
        if (i == 1) {
            this.turret = new TurretObstacle(x, y, speed);
        } else if (i == 2) {
            this.bipedal = new BipedalObstacle(x, y, speed);
        } else if (i == 3) {
            this.mec = new MechObstacle(x, y, speed);
        }
    }

    public void animation() {
        int i = this.rand;
        if (i == 1) {
            this.turret.animation();
        } else if (i == 2) {
            this.bipedal.animation();
        } else if (i == 3) {
            this.mec.animation();
        }
    }

    public void moving(boolean gameState) {
        int i = this.rand;
        if (i == 1) {
            this.turret.moving(gameState);
        } else if (i == 2) {
            this.bipedal.moving(gameState);
        } else if (i == 3) {
            this.mec.moving(gameState);
        }
    }

    public Texture getTexture() {
        int i = this.rand;
        if (i == 1) {
            return this.turret.getTexture();
        }
        if (i == 2) {
            return this.bipedal.getTexture();
        }
        if (i != 3) {
            return null;
        }
        return this.mec.getTexture();
    }

    public Vector3 getPosition() {
        int i = this.rand;
        if (i == 1) {
            return this.turret.getPosition();
        }
        if (i == 2) {
            return this.bipedal.getPosition();
        }
        if (i != 3) {
            return null;
        }
        return this.mec.getPosition();
    }

    public boolean IsOutOfScreen() {
        int i = this.rand;
        if (i == 1) {
            return this.turret.isOutOfScreen();
        }
        if (i == 2) {
            return this.bipedal.isOutOfScreen();
        }
        if (i != 3) {
            return false;
        }
        return this.mec.isOutOfScreen();
    }

    public void setSpeed(float x) {
        int i = this.rand;
        if (i == 1) {
            this.turret.setSpeed(x);
        } else if (i == 2) {
            this.bipedal.setSpeed(x);
        } else if (i == 3) {
            this.mec.setSpeed(x);
        }
    }

    public boolean contact(Rectangle player) {
        int i = this.rand;
        if (i == 1) {
            return this.turret.contact(player);
        }
        if (i == 2) {
            return this.bipedal.contact(player);
        }
        if (i != 3) {
            return false;
        }
        return this.mec.contact(player);
    }

    public void dispose() {
        int i = this.rand;
        if (i == 1) {
            this.turret.dispose();
        } else if (i == 2) {
            this.bipedal.dispose();
        } else if (i == 3) {
            this.mec.dispose();
        }
    }
}
