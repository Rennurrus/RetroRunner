package com.badlogic.gdx.scenes.scene2d;

import com.badlogic.gdx.math.Vector2;

public class InputEvent extends Event {
    private int button;
    private char character;
    private int keyCode;
    private int pointer;
    private Actor relatedActor;
    private int scrollAmount;
    private float stageX;
    private float stageY;
    private Type type;

    public enum Type {
        touchDown,
        touchUp,
        touchDragged,
        mouseMoved,
        enter,
        exit,
        scrolled,
        keyDown,
        keyUp,
        keyTyped
    }

    public void reset() {
        super.reset();
        this.relatedActor = null;
        this.button = -1;
    }

    public float getStageX() {
        return this.stageX;
    }

    public void setStageX(float stageX2) {
        this.stageX = stageX2;
    }

    public float getStageY() {
        return this.stageY;
    }

    public void setStageY(float stageY2) {
        this.stageY = stageY2;
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type2) {
        this.type = type2;
    }

    public int getPointer() {
        return this.pointer;
    }

    public void setPointer(int pointer2) {
        this.pointer = pointer2;
    }

    public int getButton() {
        return this.button;
    }

    public void setButton(int button2) {
        this.button = button2;
    }

    public int getKeyCode() {
        return this.keyCode;
    }

    public void setKeyCode(int keyCode2) {
        this.keyCode = keyCode2;
    }

    public char getCharacter() {
        return this.character;
    }

    public void setCharacter(char character2) {
        this.character = character2;
    }

    public int getScrollAmount() {
        return this.scrollAmount;
    }

    public void setScrollAmount(int scrollAmount2) {
        this.scrollAmount = scrollAmount2;
    }

    public Actor getRelatedActor() {
        return this.relatedActor;
    }

    public void setRelatedActor(Actor relatedActor2) {
        this.relatedActor = relatedActor2;
    }

    public Vector2 toCoordinates(Actor actor, Vector2 actorCoords) {
        actorCoords.set(this.stageX, this.stageY);
        actor.stageToLocalCoordinates(actorCoords);
        return actorCoords;
    }

    public boolean isTouchFocusCancel() {
        return this.stageX == -2.14748365E9f || this.stageY == -2.14748365E9f;
    }

    public String toString() {
        return this.type.toString();
    }
}
