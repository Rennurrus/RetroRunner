package com.badlogic.gdx.utils;

import com.badlogic.gdx.Gdx;

public class Logger {
    public static final int DEBUG = 3;
    public static final int ERROR = 1;
    public static final int INFO = 2;
    public static final int NONE = 0;
    private int level;
    private final String tag;

    public Logger(String tag2) {
        this(tag2, 1);
    }

    public Logger(String tag2, int level2) {
        this.tag = tag2;
        this.level = level2;
    }

    public void debug(String message) {
        if (this.level >= 3) {
            Gdx.app.debug(this.tag, message);
        }
    }

    public void debug(String message, Exception exception) {
        if (this.level >= 3) {
            Gdx.app.debug(this.tag, message, exception);
        }
    }

    public void info(String message) {
        if (this.level >= 2) {
            Gdx.app.log(this.tag, message);
        }
    }

    public void info(String message, Exception exception) {
        if (this.level >= 2) {
            Gdx.app.log(this.tag, message, exception);
        }
    }

    public void error(String message) {
        if (this.level >= 1) {
            Gdx.app.error(this.tag, message);
        }
    }

    public void error(String message, Throwable exception) {
        if (this.level >= 1) {
            Gdx.app.error(this.tag, message, exception);
        }
    }

    public void setLevel(int level2) {
        this.level = level2;
    }

    public int getLevel() {
        return this.level;
    }
}
