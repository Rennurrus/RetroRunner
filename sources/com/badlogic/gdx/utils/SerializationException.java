package com.badlogic.gdx.utils;

import com.badlogic.gdx.graphics.GL20;
import com.twi.game.BuildConfig;

public class SerializationException extends RuntimeException {
    private StringBuilder trace;

    public SerializationException() {
    }

    public SerializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public SerializationException(String message) {
        super(message);
    }

    public SerializationException(Throwable cause) {
        super(BuildConfig.FLAVOR, cause);
    }

    public boolean causedBy(Class type) {
        return causedBy(this, type);
    }

    private boolean causedBy(Throwable ex, Class type) {
        Throwable cause = ex.getCause();
        if (cause == null || cause == ex) {
            return false;
        }
        if (type.isAssignableFrom(cause.getClass())) {
            return true;
        }
        return causedBy(cause, type);
    }

    public String getMessage() {
        if (this.trace == null) {
            return super.getMessage();
        }
        StringBuilder sb = new StringBuilder((int) GL20.GL_NEVER);
        sb.append(super.getMessage());
        if (sb.length() > 0) {
            sb.append(10);
        }
        sb.append("Serialization trace:");
        sb.append(this.trace);
        return sb.toString();
    }

    public void addTrace(String info) {
        if (info != null) {
            if (this.trace == null) {
                this.trace = new StringBuilder((int) GL20.GL_NEVER);
            }
            this.trace.append(10);
            this.trace.append(info);
            return;
        }
        throw new IllegalArgumentException("info cannot be null.");
    }
}
