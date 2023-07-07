package com.badlogic.gdx.backends.android;

import android.util.Log;
import com.badlogic.gdx.ApplicationLogger;

public class AndroidApplicationLogger implements ApplicationLogger {
    public void log(String tag, String message) {
        Log.i(tag, message);
    }

    public void log(String tag, String message, Throwable exception) {
        Log.i(tag, message, exception);
    }

    public void error(String tag, String message) {
        Log.e(tag, message);
    }

    public void error(String tag, String message, Throwable exception) {
        Log.e(tag, message, exception);
    }

    public void debug(String tag, String message) {
        Log.d(tag, message);
    }

    public void debug(String tag, String message, Throwable exception) {
        Log.d(tag, message, exception);
    }
}
