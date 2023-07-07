package com.badlogic.gdx.scenes.scene2d.actions;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Pool;
import com.twi.game.BuildConfig;

public abstract class DelegateAction extends Action {
    protected Action action;

    /* access modifiers changed from: protected */
    public abstract boolean delegate(float f);

    public void setAction(Action action2) {
        this.action = action2;
    }

    public Action getAction() {
        return this.action;
    }

    public final boolean act(float delta) {
        Pool pool = getPool();
        setPool((Pool) null);
        try {
            return delegate(delta);
        } finally {
            setPool(pool);
        }
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

    public void setActor(Actor actor) {
        Action action2 = this.action;
        if (action2 != null) {
            action2.setActor(actor);
        }
        super.setActor(actor);
    }

    public void setTarget(Actor target) {
        Action action2 = this.action;
        if (action2 != null) {
            action2.setTarget(target);
        }
        super.setTarget(target);
    }

    public String toString() {
        String str;
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        if (this.action == null) {
            str = BuildConfig.FLAVOR;
        } else {
            str = "(" + this.action + ")";
        }
        sb.append(str);
        return sb.toString();
    }
}
