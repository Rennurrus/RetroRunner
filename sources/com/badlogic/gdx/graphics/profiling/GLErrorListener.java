package com.badlogic.gdx.graphics.profiling;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.GdxRuntimeException;

public interface GLErrorListener {
    public static final GLErrorListener LOGGING_LISTENER = new GLErrorListener() {
        public void onError(int error) {
            String place = null;
            try {
                StackTraceElement[] stack = Thread.currentThread().getStackTrace();
                int i = 0;
                while (true) {
                    if (i >= stack.length) {
                        break;
                    } else if (!"check".equals(stack[i].getMethodName())) {
                        i++;
                    } else if (i + 1 < stack.length) {
                        place = stack[i + 1].getMethodName();
                    }
                }
            } catch (Exception e) {
            }
            if (place != null) {
                Application application = Gdx.app;
                application.error("GLProfiler", "Error " + GLInterceptor.resolveErrorNumber(error) + " from " + place);
                return;
            }
            Application application2 = Gdx.app;
            application2.error("GLProfiler", "Error " + GLInterceptor.resolveErrorNumber(error) + " at: ", new Exception());
        }
    };
    public static final GLErrorListener THROWING_LISTENER = new GLErrorListener() {
        public void onError(int error) {
            throw new GdxRuntimeException("GLProfiler: Got GL error " + GLInterceptor.resolveErrorNumber(error));
        }
    };

    void onError(int i);
}
