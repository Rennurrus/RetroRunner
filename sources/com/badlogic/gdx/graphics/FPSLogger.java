package com.badlogic.gdx.graphics;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.TimeUtils;

public class FPSLogger {
    long startTime = TimeUtils.nanoTime();

    public void log() {
        if (TimeUtils.nanoTime() - this.startTime > 1000000000) {
            Application application = Gdx.app;
            application.log("FPSLogger", "fps: " + Gdx.graphics.getFramesPerSecond());
            this.startTime = TimeUtils.nanoTime();
        }
    }
}
