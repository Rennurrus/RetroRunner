package com.badlogic.gdx.scenes.scene2d.actions;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class ParallelAction extends Action {
    Array<Action> actions = new Array<>(4);
    private boolean complete;

    public ParallelAction() {
    }

    public ParallelAction(Action action1) {
        addAction(action1);
    }

    public ParallelAction(Action action1, Action action2) {
        addAction(action1);
        addAction(action2);
    }

    public ParallelAction(Action action1, Action action2, Action action3) {
        addAction(action1);
        addAction(action2);
        addAction(action3);
    }

    public ParallelAction(Action action1, Action action2, Action action3, Action action4) {
        addAction(action1);
        addAction(action2);
        addAction(action3);
        addAction(action4);
    }

    public ParallelAction(Action action1, Action action2, Action action3, Action action4, Action action5) {
        addAction(action1);
        addAction(action2);
        addAction(action3);
        addAction(action4);
        addAction(action5);
    }

    public boolean act(float delta) {
        if (this.complete) {
            return true;
        }
        this.complete = true;
        Pool pool = getPool();
        setPool((Pool) null);
        try {
            Array<Action> actions2 = this.actions;
            int n = actions2.size;
            for (int i = 0; i < n && this.actor != null; i++) {
                Action currentAction = actions2.get(i);
                if (currentAction.getActor() != null && !currentAction.act(delta)) {
                    this.complete = false;
                }
                if (this.actor == null) {
                    return true;
                }
            }
            boolean z = this.complete;
            setPool(pool);
            return z;
        } finally {
            setPool(pool);
        }
    }

    public void restart() {
        this.complete = false;
        Array<Action> actions2 = this.actions;
        int n = actions2.size;
        for (int i = 0; i < n; i++) {
            actions2.get(i).restart();
        }
    }

    public void reset() {
        super.reset();
        this.actions.clear();
    }

    public void addAction(Action action) {
        this.actions.add(action);
        if (this.actor != null) {
            action.setActor(this.actor);
        }
    }

    public void setActor(Actor actor) {
        Array<Action> actions2 = this.actions;
        int n = actions2.size;
        for (int i = 0; i < n; i++) {
            actions2.get(i).setActor(actor);
        }
        super.setActor(actor);
    }

    public Array<Action> getActions() {
        return this.actions;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder(64);
        buffer.append(super.toString());
        buffer.append('(');
        Array<Action> actions2 = this.actions;
        int n = actions2.size;
        for (int i = 0; i < n; i++) {
            if (i > 0) {
                buffer.append(", ");
            }
            buffer.append(actions2.get(i));
        }
        buffer.append(')');
        return buffer.toString();
    }
}
