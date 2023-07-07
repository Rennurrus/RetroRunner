package com.badlogic.gdx.scenes.scene2d.actions;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.EventListener;

public class AddListenerAction extends Action {
    private boolean capture;
    private EventListener listener;

    public boolean act(float delta) {
        if (this.capture) {
            this.target.addCaptureListener(this.listener);
            return true;
        }
        this.target.addListener(this.listener);
        return true;
    }

    public EventListener getListener() {
        return this.listener;
    }

    public void setListener(EventListener listener2) {
        this.listener = listener2;
    }

    public boolean getCapture() {
        return this.capture;
    }

    public void setCapture(boolean capture2) {
        this.capture = capture2;
    }

    public void reset() {
        super.reset();
        this.listener = null;
    }
}
