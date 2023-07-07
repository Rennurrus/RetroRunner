package com.badlogic.gdx.scenes.scene2d.utils;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.utils.Timer;

public class DragScrollListener extends DragListener {
    static final Vector2 tmpCoords = new Vector2();
    Interpolation interpolation = Interpolation.exp5In;
    float maxSpeed = 75.0f;
    float minSpeed = 15.0f;
    long rampTime = 1750;
    private ScrollPane scroll;
    private Timer.Task scrollDown;
    private Timer.Task scrollUp;
    long startTime;
    float tickSecs = 0.05f;

    public DragScrollListener(final ScrollPane scroll2) {
        this.scroll = scroll2;
        this.scrollUp = new Timer.Task() {
            public void run() {
                DragScrollListener.this.scroll(scroll2.getScrollY() - DragScrollListener.this.getScrollPixels());
            }
        };
        this.scrollDown = new Timer.Task() {
            public void run() {
                DragScrollListener.this.scroll(scroll2.getScrollY() + DragScrollListener.this.getScrollPixels());
            }
        };
    }

    public void setup(float minSpeedPixels, float maxSpeedPixels, float tickSecs2, float rampSecs) {
        this.minSpeed = minSpeedPixels;
        this.maxSpeed = maxSpeedPixels;
        this.tickSecs = tickSecs2;
        this.rampTime = (long) (1000.0f * rampSecs);
    }

    /* access modifiers changed from: package-private */
    public float getScrollPixels() {
        return this.interpolation.apply(this.minSpeed, this.maxSpeed, Math.min(1.0f, ((float) (System.currentTimeMillis() - this.startTime)) / ((float) this.rampTime)));
    }

    public void drag(InputEvent event, float x, float y, int pointer) {
        event.getListenerActor().localToActorCoordinates(this.scroll, tmpCoords.set(x, y));
        if (tmpCoords.y >= this.scroll.getHeight()) {
            this.scrollDown.cancel();
            if (!this.scrollUp.isScheduled()) {
                this.startTime = System.currentTimeMillis();
                Timer.Task task = this.scrollUp;
                float f = this.tickSecs;
                Timer.schedule(task, f, f);
            }
        } else if (tmpCoords.y < 0.0f) {
            this.scrollUp.cancel();
            if (!this.scrollDown.isScheduled()) {
                this.startTime = System.currentTimeMillis();
                Timer.Task task2 = this.scrollDown;
                float f2 = this.tickSecs;
                Timer.schedule(task2, f2, f2);
            }
        } else {
            this.scrollUp.cancel();
            this.scrollDown.cancel();
        }
    }

    public void dragStop(InputEvent event, float x, float y, int pointer) {
        this.scrollUp.cancel();
        this.scrollDown.cancel();
    }

    /* access modifiers changed from: protected */
    public void scroll(float y) {
        this.scroll.setScrollY(y);
    }
}
