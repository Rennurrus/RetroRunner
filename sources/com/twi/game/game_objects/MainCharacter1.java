package com.twi.game.game_objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;
import com.twi.game.states.PlayState;
import java.io.PrintStream;

public class MainCharacter1 {
    public static float GRAVITY = -25.0f;
    public static boolean gameState = false;
    private int EP = 0;
    private int HP = 4;
    private Rectangle borders = new Rectangle();
    private boolean dead = false;
    private Texture epTexture;
    private boolean hit_marker = false;
    private Texture hpTexture;
    private Sound jumpSound;
    private double lastCrouchTime;
    private double lastHitTime = 0.0d;
    private long lastJumpTime;
    public PlayerBullets playerBullets;
    private Vector3 position;
    private float saveV;
    private int saveY = 0;
    private boolean shield = false;
    private Sound shieldCharge;
    private Texture shieldTexture;
    private Sound shot_sound;
    private int state = 0;
    private Texture texture;
    private Vector3 velosity;

    public MainCharacter1(int x, int y, boolean gameState2) {
        gameState = gameState2;
        this.shot_sound = Gdx.audio.newSound(Gdx.files.internal("Player/ShotSound/E.ogg"));
        this.shieldCharge = Gdx.audio.newSound(Gdx.files.internal("Player/ShieldSounds/shield_charge-up.ogg"));
        this.jumpSound = Gdx.audio.newSound(Gdx.files.internal("jump.ogg"));
        this.texture = new Texture("Player/Idle/idle-1.png");
        this.hpTexture = new Texture("Player/HP/hp-4.png");
        this.shieldTexture = new Texture("Player/HP/shield.png");
        this.epTexture = new Texture("Player/EP/energy-0.png");
        this.playerBullets = new PlayerBullets();
        this.position = new Vector3((float) x, (float) y, 0.0f);
        this.velosity = new Vector3(0.0f, 0.0f, 0.0f);
        this.lastJumpTime = TimeUtils.nanoTime();
        this.lastCrouchTime = 0.0d;
        this.lastHitTime = 0.0d;
        this.borders.set((float) ((this.texture.getWidth() / 3) + x), (float) ((this.texture.getHeight() / 3) + y), (float) (this.texture.getWidth() / 3), (float) (this.texture.getHeight() / 3));
    }

    public void setDefault() {
        this.texture.dispose();
        this.jumpSound.dispose();
        this.hpTexture.dispose();
        this.epTexture.dispose();
        this.shieldTexture.dispose();
        this.HP = 4;
        this.EP = 0;
        this.shield = false;
        this.hit_marker = false;
        this.lastHitTime = 0.0d;
        this.texture = new Texture("Player/Idle/idle-1.png");
        this.hpTexture = new Texture("Player/HP/hp-4.png");
        this.shieldTexture = new Texture("Player/HP/shield.png");
        this.epTexture = new Texture("Player/EP/energy-0.png");
        this.jumpSound = Gdx.audio.newSound(Gdx.files.internal("jump.ogg"));
    }

    public void render(SpriteBatch SpB) {
        if (this.shield) {
            SpB.draw(this.shieldTexture, 20.0f, (float) ((480 - this.hpTexture.getHeight()) - 20));
        }
        Texture texture2 = this.epTexture;
        SpB.draw(texture2, 20.0f, (float) ((480 - texture2.getHeight()) - 20));
        Texture texture3 = this.hpTexture;
        SpB.draw(texture3, 20.0f, (float) ((480 - texture3.getHeight()) - 20));
        SpB.draw(this.texture, this.position.x, this.position.y);
        this.playerBullets.render(SpB);
    }

    public void update(float dTime) {
        int i = this.saveY;
        boolean z = true;
        if (i <= 0) {
            if (PlayState.getGamePaused()) {
                GRAVITY = 0.0f;
                this.velosity.y = this.saveV;
            } else {
                GRAVITY = -25.0f;
                this.velosity.add(0.0f, GRAVITY, 0.0f);
                this.velosity.scl(dTime);
                this.position.add(0.0f, this.velosity.y, 0.0f);
                if (this.position.y < 50.0f) {
                    this.position.y = 50.0f;
                }
            }
            this.borders.set(this.position.x + ((float) (this.texture.getWidth() / 3)), this.position.y + ((float) (this.texture.getHeight() / 3)), (float) (this.texture.getWidth() / 3), (float) (this.texture.getHeight() / 3));
            this.velosity.scl(1.0f / dTime);
            if (PlayState.crouch) {
                boolean z2 = this.lastCrouchTime < 0.5d;
                if (!PlayState.getGameState()) {
                    z = false;
                }
                if ((!z2 || !z) || this.position.y != 50.0f) {
                    PlayState.crouch = false;
                    this.lastCrouchTime = 0.0d;
                } else {
                    double d = this.lastCrouchTime;
                    double d2 = (double) dTime;
                    Double.isNaN(d2);
                    this.lastCrouchTime = d + d2;
                    this.position.y = 40.0f;
                    this.texture.dispose();
                    this.texture = new Texture("Player/Crouch/crouch.png");
                    this.borders.set(this.position.x + ((float) (this.texture.getWidth() / 3)), this.position.y, (float) (this.texture.getWidth() / 3), (float) (this.texture.getHeight() / 2));
                }
            }
            if (this.hit_marker) {
                double d3 = this.lastHitTime;
                if (d3 < 0.2d) {
                    double d4 = (double) dTime;
                    Double.isNaN(d4);
                    this.lastHitTime = d3 + d4;
                    this.texture.dispose();
                    this.texture = new Texture("Player/Hurt/hurt.png");
                    this.borders.set(800.0f, 480.0f, (float) (this.texture.getWidth() / 3), (float) (this.texture.getHeight() / 3));
                } else {
                    this.hit_marker = false;
                    this.lastHitTime = 0.0d;
                    this.borders.set(this.position.x + ((float) (this.texture.getWidth() / 3)), this.position.y + ((float) (this.texture.getHeight() / 3)), (float) (this.texture.getWidth() / 3), (float) (this.texture.getHeight() / 3));
                }
            }
            if (!this.hit_marker) {
                animation();
            }
            this.playerBullets.update();
            return;
        }
        this.saveY = i - 1;
    }

    public void animation() {
        if (!Isdead() && !PlayState.getGameState() && !PlayState.getGamePaused() && TimeUtils.nanoTime() - this.lastJumpTime > 100000000) {
            int i = this.state;
            if (i == 0) {
                this.texture.dispose();
                this.texture = new Texture("Player/Idle/idle-1.png");
                this.state = 1;
                this.lastJumpTime = TimeUtils.nanoTime();
            } else if (i == 1) {
                this.texture.dispose();
                this.texture = new Texture("Player/Idle/idle-2.png");
                this.state = 2;
                this.lastJumpTime = TimeUtils.nanoTime();
            } else if (i == 2) {
                this.texture.dispose();
                this.texture = new Texture("Player/Idle/idle-3.png");
                this.state = 3;
                this.lastJumpTime = TimeUtils.nanoTime();
            } else if (i == 3) {
                this.texture.dispose();
                this.texture = new Texture("Player/Idle/idle-4.png");
                this.state = 0;
                this.lastJumpTime = TimeUtils.nanoTime();
            }
        }
        if (PlayState.getGameState() && this.position.y == 50.0f && TimeUtils.nanoTime() - this.lastJumpTime > 50000000) {
            int i2 = this.state;
            if (i2 == 0 || i2 == 8) {
                this.texture.dispose();
                this.texture = new Texture("Player/run-1.png");
                this.state = 1;
                this.lastJumpTime = TimeUtils.nanoTime();
            } else if (i2 == 1) {
                this.texture.dispose();
                this.texture = new Texture("Player/run-2.png");
                this.state = 2;
                this.lastJumpTime = TimeUtils.nanoTime();
            } else if (i2 == 2) {
                this.texture.dispose();
                this.texture = new Texture("Player/run-3.png");
                this.state = 3;
                this.lastJumpTime = TimeUtils.nanoTime();
            } else if (i2 == 3) {
                this.texture.dispose();
                this.texture = new Texture("Player/run-4.png");
                this.state = 4;
                this.lastJumpTime = TimeUtils.nanoTime();
            } else if (i2 == 4) {
                this.texture.dispose();
                this.texture = new Texture("Player/run-5.png");
                this.state = 5;
                this.lastJumpTime = TimeUtils.nanoTime();
            } else if (i2 == 5) {
                this.texture.dispose();
                this.texture = new Texture("Player/run-6.png");
                this.state = 6;
                this.lastJumpTime = TimeUtils.nanoTime();
            } else if (i2 == 6) {
                this.texture.dispose();
                this.texture = new Texture("Player/run-7.png");
                this.state = 7;
                this.lastJumpTime = TimeUtils.nanoTime();
            } else if (i2 == 7) {
                this.texture.dispose();
                this.texture = new Texture("Player/run-8.png");
                this.state = 8;
                this.lastJumpTime = TimeUtils.nanoTime();
            }
        }
        if (PlayState.getGameState() && this.position.y > 50.0f && TimeUtils.nanoTime() - this.lastJumpTime > 50000000) {
            int i3 = this.state;
            if (i3 == 0) {
                this.texture.dispose();
                this.texture = new Texture("Player/Jump/jump-1.png");
                this.state = 1;
                this.lastJumpTime = TimeUtils.nanoTime();
            } else if (i3 == 1) {
                this.texture.dispose();
                this.texture = new Texture("Player/Jump/jump-2.png");
                this.state = 2;
                this.lastJumpTime = TimeUtils.nanoTime();
            } else if (i3 == 2) {
                this.texture.dispose();
                this.texture = new Texture("Player/Jump/jump-3.png");
                this.state = 3;
                this.lastJumpTime = TimeUtils.nanoTime();
            } else if (i3 == 3 || i3 == 4) {
                this.texture.dispose();
                this.texture = new Texture("Player/Jump/jump-4.png");
                this.state = 4;
                this.lastJumpTime = TimeUtils.nanoTime();
            }
        }
    }

    public void jump() {
        if (this.position.y == 50.0f) {
            this.jumpSound.play();
            this.state = 0;
            this.velosity.y = (float) (520 - (this.EP * 30));
            PrintStream printStream = System.out;
            printStream.println(this.EP + " " + this.velosity.y);
        }
    }

    public void hit() {
        this.hit_marker = true;
        if (this.shield) {
            this.shieldCharge.dispose();
            this.shieldCharge = Gdx.audio.newSound(Gdx.files.internal("Player/ShieldSounds/shield_charge-down.ogg"));
            this.shieldCharge.play(10.0f);
            this.shield = false;
        } else {
            this.HP--;
        }
        int i = this.HP;
        if (i == 0) {
            this.hpTexture.dispose();
            this.hpTexture = new Texture("Player/HP/hp-0.png");
        } else if (i == 1) {
            this.hpTexture.dispose();
            this.hpTexture = new Texture("Player/HP/hp-1.png");
        } else if (i == 2) {
            this.hpTexture.dispose();
            this.hpTexture = new Texture("Player/HP/hp-2.png");
        } else if (i == 3) {
            this.hpTexture.dispose();
            this.hpTexture = new Texture("Player/HP/hp-3.png");
        } else if (i == 4) {
            this.hpTexture.dispose();
            this.hpTexture = new Texture("Player/HP/hp-4.png");
        }
        if (this.HP == 0) {
            Isdead(true);
        }
    }

    public boolean Isdead() {
        return this.dead;
    }

    public boolean Isdead(boolean f) {
        this.dead = f;
        return this.dead;
    }

    public void dead() {
        this.texture.dispose();
        this.texture = new Texture("Player/Hurt/hurt.png");
        gameState = false;
    }

    public void add_energy() {
        this.EP++;
        if (this.EP == 4 && !this.shield) {
            this.shieldCharge.dispose();
            this.shieldCharge = Gdx.audio.newSound(Gdx.files.internal("Player/ShieldSounds/shield_charge-up.ogg"));
            this.shieldCharge.play(5.0f);
            this.shield = true;
            this.EP = 0;
        }
        if (this.EP == 5 && this.shield) {
            this.EP = 4;
        }
        update_energy();
    }

    public void update_energy() {
        int i = this.EP;
        if (i == 0) {
            this.epTexture.dispose();
            this.epTexture = new Texture("Player/EP/energy-0.png");
        } else if (i == 1) {
            this.epTexture.dispose();
            this.epTexture = new Texture("Player/EP/energy-1.png");
        } else if (i == 2) {
            this.epTexture.dispose();
            this.epTexture = new Texture("Player/EP/energy-2.png");
        } else if (i == 3) {
            this.epTexture.dispose();
            this.epTexture = new Texture("Player/EP/energy-3.png");
        } else if (i == 4) {
            this.epTexture.dispose();
            this.epTexture = new Texture("Player/EP/energy-4.png");
        }
    }

    public void shot() {
        if (this.EP > 0) {
            this.shot_sound.play();
            this.playerBullets.shot(((int) this.position.x) + (this.texture.getWidth() / 3), ((int) this.position.y) + (this.texture.getHeight() / 3));
            this.EP--;
            update_energy();
        }
    }

    public static void setGameState(boolean f) {
        gameState = f;
    }

    public Texture getTexture() {
        return this.texture;
    }

    public PlayerBullets getPlayerBullets() {
        return this.playerBullets;
    }

    public Vector3 getPosition() {
        return this.position;
    }

    public Rectangle getBorders() {
        return this.borders;
    }

    public int getHP() {
        return this.HP;
    }

    public PlayerBullets getPlayBullets() {
        return this.playerBullets;
    }

    public void pause() {
        this.saveY = 5;
        this.saveV = this.velosity.y;
        this.velosity.scl(17.0f);
    }

    public void resume() {
        this.velosity = new Vector3(0.0f, this.saveV, 0.0f);
    }

    public void loadVelosity() {
        for (int i = 0; i < 5; i++) {
            this.velosity.set(0.0f, this.saveV, 0.0f);
        }
    }

    public void dispose() {
        this.texture.dispose();
        this.jumpSound.dispose();
        this.hpTexture.dispose();
        this.epTexture.dispose();
        this.shot_sound.dispose();
        this.shieldCharge.dispose();
        this.shieldTexture.dispose();
    }
}
