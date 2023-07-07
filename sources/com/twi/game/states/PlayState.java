package com.twi.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.twi.game.Managers.EnemiesManager;
import com.twi.game.Managers.GameOverManager;
import com.twi.game.Managers.GroundManager;
import com.twi.game.Managers.PlayBackgroundManager;
import com.twi.game.Managers.PlayScreenButtonManager;
import com.twi.game.Managers.PlayScreenMusicManager;
import com.twi.game.Managers.TextManager;
import com.twi.game.game_objects.MainCharacter1;

public class PlayState extends State {
    public static boolean crouch = false;
    private static boolean gameOver = false;
    private static boolean gamePause = false;
    private static boolean gameState = false;
    public static int highScore;
    static Preferences prefs;
    public static int score = 0;
    private EnemiesManager EnM;
    private GameOverManager GOM;
    private GroundManager GrM;
    private MainCharacter1 MainCharter;
    private PlayBackgroundManager PBM;
    private PlayScreenButtonManager PSBM;
    public PlayScreenMusicManager PSMM;
    public TextManager TxM;
    private Sound death_sound;
    private Texture gameover_text = new Texture("game-over.png");
    private float lastScoreTime;
    private long lastTime;
    private boolean menuMode = false;
    private Texture replayButton = new Texture("Click-to-start.png");
    private Stage stage = new Stage(this.viewport);
    private Vector3 touch;
    private StretchViewport viewport = new StretchViewport(800.0f, 480.0f);

    public PlayState(GameStatesManager gsm) {
        super(gsm);
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, 800.0f, 480.0f);
        crouch = false;
        gameOver = false;
        gamePause = false;
        gameState = false;
        this.MainCharter = new MainCharacter1(Input.Keys.F7, 50, gameState);
        this.death_sound = Gdx.audio.newSound(Gdx.files.internal("dead.ogg"));
        this.lastTime = TimeUtils.nanoTime();
        this.lastScoreTime = 0.0f;
        score = 0;
        prefs = Gdx.app.getPreferences("My Preferences");
        highScore = prefs.getInteger("highscore");
        this.PSBM = new PlayScreenButtonManager(this.stage);
        this.EnM = new EnemiesManager();
        this.PBM = new PlayBackgroundManager();
        this.GrM = new GroundManager();
        this.PSMM = new PlayScreenMusicManager();
        this.TxM = new TextManager();
        this.GOM = new GameOverManager();
        this.PSMM.play();
    }

    public static boolean getGameState() {
        return gameState;
    }

    public static boolean getGamePaused() {
        return gamePause;
    }

    /* access modifiers changed from: protected */
    public void handleInput() {
        if (Gdx.input.justTouched()) {
            this.touch = new Vector3((float) Gdx.input.getX(), (float) Gdx.input.getY(), 0.0f);
            if (this.PSBM.isTouchTheShotButton((int) this.touch.x, (int) this.touch.y, this.menuMode)) {
                this.MainCharter.shot();
            }
            if (gamePause) {
                gamePause = false;
                this.MainCharter.loadVelosity();
                this.lastTime = TimeUtils.nanoTime();
            }
            if (gameOver && this.GOM.isGlitchEffectOff()) {
                gameOver = false;
                gameState = false;
                generate();
            } else if (!gameState && !gameOver) {
                this.lastTime = TimeUtils.nanoTime();
                gameState = true;
                MainCharacter1.setGameState(true);
            } else if (this.touch.x > ((float) (Gdx.graphics.getWidth() / 2)) && gameState) {
                this.MainCharter.jump();
            } else if (gameState && this.touch.x < ((float) (Gdx.graphics.getWidth() / 2)) && !this.PSBM.isTouchTheShotButton((int) this.touch.x, (int) this.touch.y, this.menuMode)) {
                crouch = true;
            }
        }
    }

    public void update(float dTime) {
        this.stage.act(dTime);
        this.MainCharter.update(dTime);
        if (gameState) {
            float f = this.lastScoreTime;
            if (f < 1.0f) {
                this.lastScoreTime = f + dTime;
            } else {
                score++;
                this.lastScoreTime = 0.0f;
            }
        }
        int i = highScore;
        int i2 = score;
        if (i < i2) {
            highScore = i2;
        }
        if (this.EnM.bonus_contact(this.MainCharter.getBorders()) && !this.MainCharter.Isdead()) {
            this.MainCharter.add_energy();
            this.EnM.destroy_bonus(this.MainCharter.getBorders());
        }
        this.EnM.BulletContact(this.MainCharter.getPlayerBullets());
        if ((this.EnM.contact_drone(this.MainCharter.getBorders()) || this.EnM.contact(this.MainCharter.getBorders())) && !this.MainCharter.Isdead()) {
            this.MainCharter.hit();
            this.death_sound.play();
            if (this.MainCharter.Isdead()) {
                this.PSMM.setVolume(0.01f);
                gameOver();
            } else {
                if (this.EnM.contact(this.MainCharter.getBorders())) {
                    this.EnM.destroy(this.MainCharter.getBorders());
                }
                if (this.EnM.contact_drone(this.MainCharter.getBorders())) {
                    this.EnM.destroy_drone(this.MainCharter.getBorders());
                }
            }
        }
        this.EnM.update();
        this.PBM.update();
        this.GrM.update();
        this.PSMM.choiceNext();
        if (!gamePause && gameState && ((double) (TimeUtils.nanoTime() - this.lastTime)) > 1.0E10d && this.MainCharter.getHP() > 1) {
            addSpeed();
            this.lastTime = TimeUtils.nanoTime();
        }
        if (gameOver) {
            this.GOM.update(dTime);
        }
        handleInput();
    }

    public void addSpeed() {
        this.GrM.addSpeed(0.5f);
        this.PBM.addSpeed(0.5f);
        this.EnM.addSpeed(0.5f);
    }

    public void gameOver() {
        gameState = false;
        crouch = false;
        this.MainCharter.dead();
        gameOver = true;
    }

    public void render(SpriteBatch SpB) {
        SpB.setProjectionMatrix(this.camera.combined);
        SpB.begin();
        this.PBM.render(SpB);
        this.GrM.render(SpB);
        this.EnM.render(SpB);
        this.MainCharter.render(SpB);
        this.PSBM.render(SpB, this.menuMode);
        if (this.PSBM.isPauseOn() && !gameOver) {
            this.menuMode = true;
            gamePause = true;
            gameState = false;
            this.MainCharter.loadVelosity();
            this.lastTime = TimeUtils.nanoTime();
        }
        if (this.PSBM.isNextTrackOn()) {
            this.PSMM.stop();
            this.PSMM.choiceNext();
            this.PSBM.setNextTrackOn(false);
        }
        if (this.PSBM.isYesOn()) {
            this.PSMM.stop();
            this.gsm.set(new MenuState(this.gsm));
        }
        if (this.PSBM.isMenuOn()) {
            this.PSBM.isMenuTouch = true;
        }
        if (this.PSBM.isContinueOn()) {
            this.menuMode = false;
            this.PSBM.setPauseOn(false);
            this.PSBM.setContinueButton(false);
        }
        if (this.PSBM.isNoOn()) {
            this.PSBM.setMenuButton(false);
            PlayScreenButtonManager playScreenButtonManager = this.PSBM;
            playScreenButtonManager.isMenuTouch = false;
            playScreenButtonManager.setNoOn(false);
        }
        if (this.PSBM.isYesOn()) {
            this.PSMM.stop();
            this.gsm.set(new MenuState(this.gsm));
        }
        if (gameOver && !this.menuMode) {
            this.GOM.render(SpB);
            prefs.putInteger("highscore", highScore);
            prefs.flush();
        } else if (gamePause && !this.menuMode) {
            Texture texture = this.replayButton;
            SpB.draw(texture, (float) (400 - (texture.getWidth() / 2)), (float) (240 - (this.replayButton.getHeight() / 2)));
        }
        if (!gameState && !gamePause && !gameOver) {
            Texture texture2 = this.replayButton;
            SpB.draw(texture2, (float) (400 - (texture2.getWidth() / 2)), (float) (240 - (this.replayButton.getHeight() / 2)));
        }
        this.TxM.render(SpB);
        SpB.end();
    }

    public void generate() {
        this.PSMM.setVolume(0.15f);
        this.replayButton.dispose();
        this.EnM.dispose();
        this.PBM.dispose();
        this.GrM.dispose();
        this.EnM.dispose();
        this.PBM.dispose();
        this.GrM.dispose();
        this.replayButton = new Texture("Click-to-start.png");
        gameOver = false;
        gamePause = false;
        gameState = false;
        crouch = false;
        this.lastScoreTime = 0.0f;
        score = 0;
        this.EnM = new EnemiesManager();
        this.PBM = new PlayBackgroundManager();
        this.GrM = new GroundManager();
        this.GOM.setDefault();
        this.MainCharter = new MainCharacter1(Input.Keys.F7, 50, gameState);
    }

    public void pause() {
        this.MainCharter.pause();
        this.replayButton = new Texture("Click-to-start.png");
        gamePause = true;
        gameState = false;
    }

    public void resume() {
        this.MainCharter.resume();
        this.PSMM.play();
    }

    public void dispose() {
        this.MainCharter.dispose();
        this.death_sound.dispose();
        this.replayButton.dispose();
        this.gameover_text.dispose();
        this.EnM.dispose();
        this.PBM.dispose();
        this.GrM.dispose();
        this.PSMM.dispoce();
        this.TxM.dispose();
        this.PSMM.dispoce();
        this.GOM.dispose();
    }
}
