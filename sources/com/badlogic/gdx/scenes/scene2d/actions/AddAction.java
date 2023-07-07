package com.badlogic.gdx.scenes.scene2d.actions;

import com.badlogic.gdx.scenes.scene2d.Action;

public class AddAction extends Action {
    private Action action;

    public boolean act(float delta) {
        this.target.addAction(this.action);
        return true;
    }

    public Action getAction() {
        return this.action;
    }

    public void setAction(Action action2) {
        this.action = action2;
    }

    public void restart() {
        Action action2 = this.action;
        if (action2 != null) {
            action2.restart();
        }
    }

    public void reset() {
        super.reset();
        this.action = null;
    }
}
