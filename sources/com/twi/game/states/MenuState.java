package com.twi.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.twi.game.Managers.MenuButtonsManager;
import com.twi.game.Managers.MenuMusicManager;
import com.twi.game.Managers.TransportMenuManager;

public class MenuState extends State {
    private MenuMusicManager MMM = new MenuMusicManager();
    private TransportMenuManager TMM = new TransportMenuManager();
    private Texture background = new Texture("Menu/Background/" + ((int) ((Math.random() * 4.0d) + 1.0d)) + ".png");
    private Texture credits = new Texture("Menu/credits.png");
    private Texture gameName = new Texture("Menu/gamename.png");
    public boolean isCreditsOn;
    private MenuButtonsManager menuButtonsManager;
    public boolean question;
    private Stage stage;
    private StretchViewport viewport;
    private Texture youSure = new Texture("Menu/youSure.png");

    public MenuState(GameStatesManager gsm) {
        super(gsm);
        this.MMM.play();
        this.question = false;
        this.isCreditsOn = false;
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, 800.0f, 480.0f);
        this.viewport = new StretchViewport(800.0f, 480.0f);
        this.stage = new Stage(this.viewport);
        this.menuButtonsManager = new MenuButtonsManager(this.stage);
        Gdx.input.setInputProcessor(this.stage);
    }

    /* access modifiers changed from: protected */
    public void handleInput() {
    }

    public void update(float dTime) {
        handleInput();
        this.TMM.update();
        this.MMM.choiceNext();
        this.stage.act(dTime);
    }

    public void render(SpriteBatch SpB) {
        SpB.begin();
        SpB.setProjectionMatrix(this.camera.combined);
        SpB.draw(this.background, 0.0f, 0.0f);
        this.TMM.render(SpB);
        if (this.menuButtonsManager.isCreditsOn()) {
            this.isCreditsOn = true;
        }
        if (this.menuButtonsManager.isPlayOn()) {
            this.MMM.stop(true);
            this.gsm.set(new PlayState(this.gsm));
        }
        if (this.menuButtonsManager.isExitOn()) {
            this.question = true;
        }
        if (this.menuButtonsManager.isBackOn()) {
            this.isCreditsOn = false;
            this.menuButtonsManager.setBackOn(false);
            this.menuButtonsManager.setCreditsOn(false);
        }
        if (this.menuButtonsManager.isNoOn()) {
            this.question = false;
            this.menuButtonsManager.setExitOn(false);
            this.menuButtonsManager.setNoOn(false);
        }
        if (this.menuButtonsManager.isYesOn()) {
            this.gsm.pop();
        }
        if (this.isCreditsOn) {
            SpB.draw(this.credits, 0.0f, 0.0f);
        } else if (this.question) {
            Texture texture = this.youSure;
            SpB.draw(texture, (float) (400 - (texture.getWidth() / 2)), (float) ((320 - (this.youSure.getHeight() / 2)) + 20));
        } else {
            Texture texture2 = this.gameName;
            SpB.draw(texture2, (float) (400 - (texture2.getWidth() / 2)), (float) ((320 - (this.gameName.getHeight() / 2)) + 20));
        }
        this.menuButtonsManager.render(SpB);
        SpB.end();
    }

    public void dispose() {
        this.youSure.dispose();
        this.gameName.dispose();
        this.background.dispose();
        this.credits.dispose();
        this.TMM.dispose();
        this.MMM.dispoce();
        this.menuButtonsManager.dispose();
    }

    public void pause() {
    }

    public void resume() {
        this.MMM.play();
    }
}
