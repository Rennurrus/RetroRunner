package com.badlogic.gdx.scenes.scene2d.actions;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.utils.Pool;

public class RunnableAction extends Action {
    private boolean ran;
    private Runnable runnable;

    public boolean act(float delta) {
        if (!this.ran) {
            this.ran = true;
            run();
        }
        return true;
    }

    public void run() {
        Pool pool = getPool();
        setPool((Pool) null);
        try {
            this.runnable.run();
        } finally {
            setPool(pool);
        }
    }

    public void restart() {
        this.ran = false;
    }

    public void reset() {
        super.reset();
        this.runnable = null;
    }

    public Runnable getRunnable() {
        return this.runnable;
    }

    public void setRunnable(Runnable runnable2) {
        this.runnable = runnable2;
    }
}
