package com.badlogic.gdx.scenes.scene2d.actions;

import com.badlogic.gdx.scenes.scene2d.Action;

public class VisibleAction extends Action {
    private boolean visible;

    public boolean act(float delta) {
        this.target.setVisible(this.visible);
        return true;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible2) {
        this.visible = visible2;
    }
}
