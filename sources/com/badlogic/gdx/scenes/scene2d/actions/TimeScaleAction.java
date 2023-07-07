package com.badlogic.gdx.scenes.scene2d.actions;

public class TimeScaleAction extends DelegateAction {
    private float scale;

    /* access modifiers changed from: protected */
    public boolean delegate(float delta) {
        if (this.action == null) {
            return true;
        }
        return this.action.act(this.scale * delta);
    }

    public float getScale() {
        return this.scale;
    }

    public void setScale(float scale2) {
        this.scale = scale2;
    }
}
