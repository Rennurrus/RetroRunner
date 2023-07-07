package com.twi.game.Managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MenuButtonsManager {
    /* access modifiers changed from: private */
    public Button backButton = new Button("Back", 0.0f, 0.0f);
    /* access modifiers changed from: private */
    public Button creditsButton = new Button("Credits", 400.0f, 145.0f);
    /* access modifiers changed from: private */
    public Button exitButton = new Button("Exit", 400.0f, 68.0f);
    /* access modifiers changed from: private */
    public boolean isBackOn = false;
    /* access modifiers changed from: private */
    public boolean isCreditsOn = false;
    /* access modifiers changed from: private */
    public boolean isExitOn = false;
    /* access modifiers changed from: private */
    public boolean isNoOn = false;
    /* access modifiers changed from: private */
    public boolean isPlayOn = false;
    /* access modifiers changed from: private */
    public boolean isYesOn = false;
    /* access modifiers changed from: private */
    public Button noButton = new Button("No", 533.0f, 160.0f);
    /* access modifiers changed from: private */
    public Button playButton = new Button("Play", 400.0f, 220.0f);
    /* access modifiers changed from: private */
    public Button yesButton = new Button("Yes", 266.0f, 160.0f);

    public MenuButtonsManager(Stage stage) {
        Button button = this.backButton;
        button.setPosition((800.0f - button.getTextButtonClass().getWidth()) - 20.0f, (480.0f - this.backButton.getTextButtonClass().getHeight()) - 20.0f);
        this.creditsButton.getTextButtonClass().addListener(new ClickListener() {
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                boolean unused = MenuButtonsManager.this.isCreditsOn = true;
                MenuButtonsManager.this.backButton.getTextButtonClass().setTouchable(Touchable.enabled);
                MenuButtonsManager.this.playButton.getTextButtonClass().setTouchable(Touchable.disabled);
                MenuButtonsManager.this.creditsButton.getTextButtonClass().setTouchable(Touchable.disabled);
                MenuButtonsManager.this.exitButton.getTextButtonClass().setTouchable(Touchable.disabled);
                MenuButtonsManager.this.yesButton.getTextButtonClass().setTouchable(Touchable.disabled);
                MenuButtonsManager.this.noButton.getTextButtonClass().setTouchable(Touchable.disabled);
                super.touchUp(event, x, y, pointer, button);
            }
        });
        this.playButton.getTextButtonClass().addListener(new ClickListener() {
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                boolean unused = MenuButtonsManager.this.isPlayOn = true;
                super.touchUp(event, x, y, pointer, button);
            }
        });
        this.exitButton.getTextButtonClass().addListener(new ClickListener() {
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                MenuButtonsManager.this.backButton.getTextButtonClass().setTouchable(Touchable.disabled);
                MenuButtonsManager.this.playButton.getTextButtonClass().setTouchable(Touchable.disabled);
                MenuButtonsManager.this.creditsButton.getTextButtonClass().setTouchable(Touchable.disabled);
                MenuButtonsManager.this.exitButton.getTextButtonClass().setTouchable(Touchable.disabled);
                MenuButtonsManager.this.yesButton.getTextButtonClass().setTouchable(Touchable.enabled);
                MenuButtonsManager.this.noButton.getTextButtonClass().setTouchable(Touchable.enabled);
                boolean unused = MenuButtonsManager.this.isExitOn = true;
                super.touchUp(event, x, y, pointer, button);
            }
        });
        this.backButton.getTextButtonClass().addListener(new ClickListener() {
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                boolean unused = MenuButtonsManager.this.isBackOn = true;
                MenuButtonsManager.this.backButton.getTextButtonClass().setTouchable(Touchable.disabled);
                MenuButtonsManager.this.playButton.getTextButtonClass().setTouchable(Touchable.enabled);
                MenuButtonsManager.this.creditsButton.getTextButtonClass().setTouchable(Touchable.enabled);
                MenuButtonsManager.this.exitButton.getTextButtonClass().setTouchable(Touchable.enabled);
                MenuButtonsManager.this.yesButton.getTextButtonClass().setTouchable(Touchable.enabled);
                MenuButtonsManager.this.noButton.getTextButtonClass().setTouchable(Touchable.enabled);
                super.touchUp(event, x, y, pointer, button);
            }
        });
        this.yesButton.getTextButtonClass().addListener(new ClickListener() {
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                boolean unused = MenuButtonsManager.this.isYesOn = true;
                super.touchUp(event, x, y, pointer, button);
            }
        });
        this.noButton.getTextButtonClass().addListener(new ClickListener() {
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                boolean unused = MenuButtonsManager.this.isNoOn = true;
                MenuButtonsManager.this.backButton.getTextButtonClass().setTouchable(Touchable.enabled);
                MenuButtonsManager.this.playButton.getTextButtonClass().setTouchable(Touchable.enabled);
                MenuButtonsManager.this.creditsButton.getTextButtonClass().setTouchable(Touchable.enabled);
                MenuButtonsManager.this.exitButton.getTextButtonClass().setTouchable(Touchable.enabled);
                MenuButtonsManager.this.yesButton.getTextButtonClass().setTouchable(Touchable.disabled);
                MenuButtonsManager.this.noButton.getTextButtonClass().setTouchable(Touchable.disabled);
                super.touchUp(event, x, y, pointer, button);
            }
        });
        stage.addActor(this.creditsButton.getTextButtonClass());
        stage.addActor(this.playButton.getTextButtonClass());
        stage.addActor(this.exitButton.getTextButtonClass());
        stage.addActor(this.backButton.getTextButtonClass());
        stage.addActor(this.yesButton.getTextButtonClass());
        stage.addActor(this.noButton.getTextButtonClass());
        Gdx.input.setInputProcessor(stage);
    }

    public void render(SpriteBatch SpB) {
        if (!this.isCreditsOn && !this.isExitOn) {
            this.creditsButton.render(SpB);
            this.playButton.render(SpB);
            this.exitButton.render(SpB);
        } else if (this.isExitOn) {
            this.yesButton.render(SpB);
            this.noButton.render(SpB);
        } else if (this.isCreditsOn) {
            this.backButton.render(SpB);
        }
    }

    public void setExitOn(boolean exitOn) {
        this.isExitOn = exitOn;
    }

    public void setNoOn(boolean noOn) {
        this.isNoOn = noOn;
    }

    public void setBackOn(boolean backOn) {
        this.isBackOn = backOn;
    }

    public void setCreditsOn(boolean creditsOn) {
        this.isCreditsOn = creditsOn;
    }

    public boolean isCreditsOn() {
        return this.isCreditsOn;
    }

    public boolean isPlayOn() {
        return this.isPlayOn;
    }

    public boolean isExitOn() {
        return this.isExitOn;
    }

    public boolean isBackOn() {
        return this.isBackOn;
    }

    public boolean isYesOn() {
        return this.isYesOn;
    }

    public boolean isNoOn() {
        return this.isNoOn;
    }

    public void dispose() {
        this.creditsButton.dispose();
    }
}
