package com.badlogic.gdx.scenes.scene2d.utils;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.TimeUtils;

public class ClickListener extends InputListener {
    public static float visualPressedDuration = 0.1f;
    private int button;
    private boolean cancelled;
    private long lastTapTime;
    private boolean over;
    private boolean pressed;
    private int pressedButton = -1;
    private int pressedPointer = -1;
    private int tapCount;
    private long tapCountInterval = 400000000;
    private float tapSquareSize = 14.0f;
    private float touchDownX = -1.0f;
    private float touchDownY = -1.0f;
    private long visualPressedTime;

    public ClickListener() {
    }

    public ClickListener(int button2) {
        this.button = button2;
    }

    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button2) {
        int i;
        if (this.pressed) {
            return false;
        }
        if (pointer == 0 && (i = this.button) != -1 && button2 != i) {
            return false;
        }
        this.pressed = true;
        this.pressedPointer = pointer;
        this.pressedButton = button2;
        this.touchDownX = x;
        this.touchDownY = y;
        setVisualPressed(true);
        return true;
    }

    public void touchDragged(InputEvent event, float x, float y, int pointer) {
        if (pointer == this.pressedPointer && !this.cancelled) {
            this.pressed = isOver(event.getListenerActor(), x, y);
            if (!this.pressed) {
                invalidateTapSquare();
            }
        }
    }

    public void touchUp(InputEvent event, float x, float y, int pointer, int button2) {
        int i;
        if (pointer == this.pressedPointer) {
            if (!this.cancelled) {
                boolean touchUpOver = isOver(event.getListenerActor(), x, y);
                if (touchUpOver && pointer == 0 && (i = this.button) != -1 && button2 != i) {
                    touchUpOver = false;
                }
                if (touchUpOver) {
                    long time = TimeUtils.nanoTime();
                    if (time - this.lastTapTime > this.tapCountInterval) {
                        this.tapCount = 0;
                    }
                    this.tapCount++;
                    this.lastTapTime = time;
                    clicked(event, x, y);
                }
            }
            this.pressed = false;
            this.pressedPointer = -1;
            this.pressedButton = -1;
            this.cancelled = false;
        }
    }

    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
        if (pointer == -1 && !this.cancelled) {
            this.over = true;
        }
    }

    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
        if (pointer == -1 && !this.cancelled) {
            this.over = false;
        }
    }

    public void cancel() {
        if (this.pressedPointer != -1) {
            this.cancelled = true;
            this.pressed = false;
        }
    }

    public void clicked(InputEvent event, float x, float y) {
    }

    public boolean isOver(Actor actor, float x, float y) {
        Actor hit = actor.hit(x, y, true);
        if (hit == null || !hit.isDescendantOf(actor)) {
            return inTapSquare(x, y);
        }
        return true;
    }

    public boolean inTapSquare(float x, float y) {
        if ((this.touchDownX != -1.0f || this.touchDownY != -1.0f) && Math.abs(x - this.touchDownX) < this.tapSquareSize && Math.abs(y - this.touchDownY) < this.tapSquareSize) {
            return true;
        }
        return false;
    }

    public boolean inTapSquare() {
        return this.touchDownX != -1.0f;
    }

    public void invalidateTapSquare() {
        this.touchDownX = -1.0f;
        this.touchDownY = -1.0f;
    }

    public boolean isPressed() {
        return this.pressed;
    }

    public boolean isVisualPressed() {
        if (this.pressed) {
            return true;
        }
        long j = this.visualPressedTime;
        if (j <= 0) {
            return false;
        }
        if (j > TimeUtils.millis()) {
            return true;
        }
        this.visualPressedTime = 0;
        return false;
    }

    public void setVisualPressed(boolean visualPressed) {
        if (visualPressed) {
            this.visualPressedTime = TimeUtils.millis() + ((long) (visualPressedDuration * 1000.0f));
        } else {
            this.visualPressedTime = 0;
        }
    }

    public boolean isOver() {
        return this.over || this.pressed;
    }

    public void setTapSquareSize(float halfTapSquareSize) {
        this.tapSquareSize = halfTapSquareSize;
    }

    public float getTapSquareSize() {
        return this.tapSquareSize;
    }

    public void setTapCountInterval(float tapCountInterval2) {
        this.tapCountInterval = (long) (1.0E9f * tapCountInterval2);
    }

    public int getTapCount() {
        return this.tapCount;
    }

    public void setTapCount(int tapCount2) {
        this.tapCount = tapCount2;
    }

    public float getTouchDownX() {
        return this.touchDownX;
    }

    public float getTouchDownY() {
        return this.touchDownY;
    }

    public int getPressedButton() {
        return this.pressedButton;
    }

    public int getPressedPointer() {
        return this.pressedPointer;
    }

    public int getButton() {
        return this.button;
    }

    public void setButton(int button2) {
        this.button = button2;
    }
}
