package com.twi.game.Managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.twi.game.MainGame;

public class PlayScreenButtonManager {
    public float Height_ratio = ((float) (Gdx.graphics.getHeight() / MainGame.HEIGHT));
    public float MonitorHeight = ((float) Gdx.graphics.getHeight());
    public float MonitorWidth = ((float) Gdx.graphics.getWidth());
    public float Width_ratio = ((float) (Gdx.graphics.getWidth() / MainGame.WIDTH));
    /* access modifiers changed from: private */
    public Button continueButton = new Button("Continue", 400.0f, 240.0f);
    /* access modifiers changed from: private */
    public boolean isContinueOn = false;
    /* access modifiers changed from: private */
    public boolean isMenuOn = false;
    public boolean isMenuTouch;
    /* access modifiers changed from: private */
    public boolean isNextTrackOn = false;
    /* access modifiers changed from: private */
    public boolean isNoOn = false;
    /* access modifiers changed from: private */
    public boolean isPauseOn = false;
    /* access modifiers changed from: private */
    public boolean isYesOn = false;
    /* access modifiers changed from: private */
    public Button menuButton = new Button("Main menu", 400.0f, 80.0f);
    /* access modifiers changed from: private */
    public Button nextTrackButton = new Button("Next track", 400.0f, 160.0f);
    /* access modifiers changed from: private */
    public Button noButton;
    /* access modifiers changed from: private */
    public Button pauseButton = new Button("Pause", 0.0f, 0.0f);
    private Texture paused = new Texture("GameScreen/paused.png");
    private Vector3 positionShotButton = new Vector3(this.Width_ratio * 10.0f, this.Height_ratio * 10.0f, 0.0f);
    private Texture shot_button = new Texture("GameScreen/Buttons/shot-button.png");
    /* access modifiers changed from: private */
    public Button yesButton;
    private Texture youSure = new Texture("GameScreen/youSure.png");

    public PlayScreenButtonManager(Stage stage) {
        Button button = this.pauseButton;
        button.setPosition((800.0f - button.getTextButtonClass().getWidth()) - 30.0f, (480.0f - this.pauseButton.getTextButtonClass().getHeight()) - 10.0f);
        this.yesButton = new Button("Yes", 266.0f, 160.0f);
        this.noButton = new Button("No", 533.0f, 160.0f);
        this.continueButton.getTextButtonClass().addListener(new ClickListener() {
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                boolean unused = PlayScreenButtonManager.this.isContinueOn = true;
                PlayScreenButtonManager.this.pauseButton.getTextButtonClass().setTouchable(Touchable.enabled);
                PlayScreenButtonManager.this.continueButton.getTextButtonClass().setTouchable(Touchable.disabled);
                PlayScreenButtonManager.this.menuButton.getTextButtonClass().setTouchable(Touchable.disabled);
                PlayScreenButtonManager.this.nextTrackButton.getTextButtonClass().setTouchable(Touchable.disabled);
                super.touchUp(event, x, y, pointer, button);
            }
        });
        this.pauseButton.getTextButtonClass().addListener(new ClickListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                boolean unused = PlayScreenButtonManager.this.isPauseOn = true;
                PlayScreenButtonManager.this.continueButton.getTextButtonClass().setTouchable(Touchable.enabled);
                PlayScreenButtonManager.this.menuButton.getTextButtonClass().setTouchable(Touchable.enabled);
                PlayScreenButtonManager.this.nextTrackButton.getTextButtonClass().setTouchable(Touchable.enabled);
                PlayScreenButtonManager.this.pauseButton.getTextButtonClass().setTouchable(Touchable.disabled);
                super.touchUp(event, x, y, pointer, button);
                return true;
            }
        });
        this.nextTrackButton.getTextButtonClass().addListener(new ClickListener() {
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                boolean unused = PlayScreenButtonManager.this.isNextTrackOn = true;
                super.touchUp(event, x, y, pointer, button);
            }
        });
        this.menuButton.getTextButtonClass().addListener(new ClickListener() {
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                PlayScreenButtonManager.this.pauseButton.getTextButtonClass().setTouchable(Touchable.disabled);
                PlayScreenButtonManager.this.continueButton.getTextButtonClass().setTouchable(Touchable.disabled);
                PlayScreenButtonManager.this.nextTrackButton.getTextButtonClass().setTouchable(Touchable.disabled);
                PlayScreenButtonManager.this.menuButton.getTextButtonClass().setTouchable(Touchable.disabled);
                PlayScreenButtonManager.this.yesButton.getTextButtonClass().setTouchable(Touchable.enabled);
                PlayScreenButtonManager.this.noButton.getTextButtonClass().setTouchable(Touchable.enabled);
                boolean unused = PlayScreenButtonManager.this.isMenuOn = true;
                super.touchUp(event, x, y, pointer, button);
            }
        });
        this.yesButton.getTextButtonClass().addListener(new ClickListener() {
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                boolean unused = PlayScreenButtonManager.this.isYesOn = true;
                super.touchUp(event, x, y, pointer, button);
            }
        });
        this.noButton.getTextButtonClass().addListener(new ClickListener() {
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                boolean unused = PlayScreenButtonManager.this.isNoOn = true;
                System.out.println("no");
                PlayScreenButtonManager.this.continueButton.getTextButtonClass().setTouchable(Touchable.enabled);
                PlayScreenButtonManager.this.nextTrackButton.getTextButtonClass().setTouchable(Touchable.enabled);
                PlayScreenButtonManager.this.menuButton.getTextButtonClass().setTouchable(Touchable.enabled);
                PlayScreenButtonManager.this.yesButton.getTextButtonClass().setTouchable(Touchable.disabled);
                PlayScreenButtonManager.this.noButton.getTextButtonClass().setTouchable(Touchable.disabled);
                super.touchUp(event, x, y, pointer, button);
            }
        });
        stage.addActor(this.pauseButton.getTextButtonClass());
        stage.addActor(this.menuButton.getTextButtonClass());
        stage.addActor(this.continueButton.getTextButtonClass());
        stage.addActor(this.nextTrackButton.getTextButtonClass());
        stage.addActor(this.yesButton.getTextButtonClass());
        stage.addActor(this.noButton.getTextButtonClass());
        Gdx.input.setInputProcessor(stage);
    }

    public void render(SpriteBatch SpB, boolean menuMode) {
        if (!this.isPauseOn) {
            this.pauseButton.render(SpB);
            SpB.draw(this.shot_button, 10.0f, 10.0f);
        } else if (!this.isMenuOn) {
            this.menuButton.render(SpB);
            this.nextTrackButton.render(SpB);
            this.continueButton.render(SpB);
            Texture texture = this.paused;
            SpB.draw(texture, (float) (400 - (texture.getWidth() / 2)), (float) ((320 - (this.paused.getHeight() / 2)) + 20));
        } else {
            Texture texture2 = this.youSure;
            SpB.draw(texture2, (float) (400 - (texture2.getWidth() / 2)), (float) ((320 - (this.youSure.getHeight() / 2)) + 20));
            this.yesButton.render(SpB);
            this.noButton.render(SpB);
        }
    }

    public void setMenuButton(boolean exitOn) {
        this.isMenuOn = exitOn;
    }

    public void setContinueButton(boolean exitOn) {
        this.isContinueOn = exitOn;
    }

    public void setNoOn(boolean noOn) {
        this.isNoOn = noOn;
    }

    public void setPauseOn(boolean backOn) {
        this.isPauseOn = backOn;
    }

    public void setNextTrackOn(boolean creditsOn) {
        this.isNextTrackOn = creditsOn;
    }

    public boolean isNextTrackOn() {
        return this.isNextTrackOn;
    }

    public boolean isContinueOn() {
        return this.isContinueOn;
    }

    public boolean isMenuOn() {
        return this.isMenuOn;
    }

    public boolean isPauseOn() {
        return this.isPauseOn;
    }

    public boolean isYesOn() {
        return this.isYesOn;
    }

    public boolean isNoOn() {
        return this.isNoOn;
    }

    public boolean isTouchTheShotButton(int x, int y, boolean menuMode) {
        if (menuMode || ((float) x) < this.positionShotButton.x || ((float) x) > this.positionShotButton.x + (((float) this.shot_button.getWidth()) * this.Width_ratio) || this.MonitorHeight - ((float) y) < this.positionShotButton.y || this.MonitorHeight - ((float) y) > this.positionShotButton.y + (((float) this.shot_button.getHeight()) * this.Height_ratio)) {
            return false;
        }
        return true;
    }

    public void dispose() {
        this.shot_button.dispose();
        this.youSure.dispose();
        this.paused.dispose();
        this.yesButton.dispose();
        this.noButton.dispose();
        this.continueButton.dispose();
        this.pauseButton.dispose();
        this.nextTrackButton.dispose();
        this.menuButton.dispose();
    }
}
