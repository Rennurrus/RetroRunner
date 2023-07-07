package com.badlogic.gdx;

public abstract class Game implements ApplicationListener {
    protected Screen screen;

    public void dispose() {
        Screen screen2 = this.screen;
        if (screen2 != null) {
            screen2.hide();
        }
    }

    public void pause() {
        Screen screen2 = this.screen;
        if (screen2 != null) {
            screen2.pause();
        }
    }

    public void resume() {
        Screen screen2 = this.screen;
        if (screen2 != null) {
            screen2.resume();
        }
    }

    public void render() {
        Screen screen2 = this.screen;
        if (screen2 != null) {
            screen2.render(Gdx.graphics.getDeltaTime());
        }
    }

    public void resize(int width, int height) {
        Screen screen2 = this.screen;
        if (screen2 != null) {
            screen2.resize(width, height);
        }
    }

    public void setScreen(Screen screen2) {
        Screen screen3 = this.screen;
        if (screen3 != null) {
            screen3.hide();
        }
        this.screen = screen2;
        Screen screen4 = this.screen;
        if (screen4 != null) {
            screen4.show();
            this.screen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
    }

    public Screen getScreen() {
        return this.screen;
    }
}
