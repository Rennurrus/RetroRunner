package com.badlogic.gdx.backends.android;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Debug;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.ApplicationLogger;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.android.surfaceview.FillResolutionStrategy;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Clipboard;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.SnapshotArray;
import java.lang.reflect.Method;

public class AndroidApplication extends Activity implements AndroidApplicationBase {
    private final Array<AndroidEventListener> androidEventListeners = new Array<>();
    protected ApplicationLogger applicationLogger;
    protected AndroidAudio audio;
    protected AndroidClipboard clipboard;
    protected final Array<Runnable> executedRunnables = new Array<>();
    protected AndroidFiles files;
    protected boolean firstResume = true;
    protected AndroidGraphics graphics;
    public Handler handler;
    protected boolean hideStatusBar = false;
    protected AndroidInput input;
    private boolean isWaitingForAudio = false;
    protected final SnapshotArray<LifecycleListener> lifecycleListeners = new SnapshotArray<>(LifecycleListener.class);
    protected ApplicationListener listener;
    protected int logLevel = 2;
    protected AndroidNet net;
    protected final Array<Runnable> runnables = new Array<>();
    protected boolean useImmersiveMode = false;
    private int wasFocusChanged = -1;

    static {
        GdxNativesLoader.load();
    }

    public void initialize(ApplicationListener listener2) {
        initialize(listener2, new AndroidApplicationConfiguration());
    }

    public void initialize(ApplicationListener listener2, AndroidApplicationConfiguration config) {
        init(listener2, config, false);
    }

    public View initializeForView(ApplicationListener listener2) {
        return initializeForView(listener2, new AndroidApplicationConfiguration());
    }

    public View initializeForView(ApplicationListener listener2, AndroidApplicationConfiguration config) {
        init(listener2, config, true);
        return this.graphics.getView();
    }

    private void init(ApplicationListener listener2, AndroidApplicationConfiguration config, boolean isForView) {
        if (getVersion() >= 9) {
            setApplicationLogger(new AndroidApplicationLogger());
            this.graphics = new AndroidGraphics(this, config, config.resolutionStrategy == null ? new FillResolutionStrategy() : config.resolutionStrategy);
            this.input = AndroidInputFactory.newAndroidInput(this, this, this.graphics.view, config);
            this.audio = new AndroidAudio(this, config);
            getFilesDir();
            this.files = new AndroidFiles(getAssets(), getFilesDir().getAbsolutePath());
            this.net = new AndroidNet(this, config);
            this.listener = listener2;
            this.handler = new Handler();
            this.useImmersiveMode = config.useImmersiveMode;
            this.hideStatusBar = config.hideStatusBar;
            this.clipboard = new AndroidClipboard(this);
            addLifecycleListener(new LifecycleListener() {
                public void resume() {
                }

                public void pause() {
                    AndroidApplication.this.audio.pause();
                }

                public void dispose() {
                    AndroidApplication.this.audio.dispose();
                }
            });
            Gdx.app = this;
            Gdx.input = getInput();
            Gdx.audio = getAudio();
            Gdx.files = getFiles();
            Gdx.graphics = getGraphics();
            Gdx.net = getNet();
            if (!isForView) {
                try {
                    requestWindowFeature(1);
                } catch (Exception ex) {
                    log("AndroidApplication", "Content already displayed, cannot request FEATURE_NO_TITLE", ex);
                }
                getWindow().setFlags(GL20.GL_STENCIL_BUFFER_BIT, GL20.GL_STENCIL_BUFFER_BIT);
                getWindow().clearFlags(2048);
                setContentView(this.graphics.getView(), createLayoutParams());
            }
            createWakeLock(config.useWakelock);
            hideStatusBar(this.hideStatusBar);
            useImmersiveMode(this.useImmersiveMode);
            if (this.useImmersiveMode && getVersion() >= 19) {
                try {
                    Class<?> vlistener = Class.forName("com.badlogic.gdx.backends.android.AndroidVisibilityListener");
                    vlistener.getDeclaredMethod("createListener", new Class[]{AndroidApplicationBase.class}).invoke(vlistener.newInstance(), new Object[]{this});
                } catch (Exception e) {
                    log("AndroidApplication", "Failed to create AndroidVisibilityListener", e);
                }
            }
            if (getResources().getConfiguration().keyboard != 1) {
                getInput().keyboardAvailable = true;
                return;
            }
            return;
        }
        throw new GdxRuntimeException("LibGDX requires Android API Level 9 or later.");
    }

    /* access modifiers changed from: protected */
    public FrameLayout.LayoutParams createLayoutParams() {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, -1);
        layoutParams.gravity = 17;
        return layoutParams;
    }

    /* access modifiers changed from: protected */
    public void createWakeLock(boolean use) {
        if (use) {
            getWindow().addFlags(128);
        }
    }

    /* access modifiers changed from: protected */
    public void hideStatusBar(boolean hide) {
        if (hide && getVersion() >= 11) {
            View rootView = getWindow().getDecorView();
            Class<View> cls = View.class;
            try {
                Method m = cls.getMethod("setSystemUiVisibility", new Class[]{Integer.TYPE});
                if (getVersion() <= 13) {
                    m.invoke(rootView, new Object[]{0});
                }
                m.invoke(rootView, new Object[]{1});
            } catch (Exception e) {
                log("AndroidApplication", "Can't hide status bar", e);
            }
        }
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        useImmersiveMode(this.useImmersiveMode);
        hideStatusBar(this.hideStatusBar);
        if (hasFocus) {
            this.wasFocusChanged = 1;
            if (this.isWaitingForAudio) {
                this.audio.resume();
                this.isWaitingForAudio = false;
                return;
            }
            return;
        }
        this.wasFocusChanged = 0;
    }

    @TargetApi(19)
    public void useImmersiveMode(boolean use) {
        if (use && getVersion() >= 19) {
            View view = getWindow().getDecorView();
            Class<View> cls = View.class;
            try {
                cls.getMethod("setSystemUiVisibility", new Class[]{Integer.TYPE}).invoke(view, new Object[]{5894});
            } catch (Exception e) {
                log("AndroidApplication", "Can't set immersive mode", e);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        boolean isContinuous = this.graphics.isContinuousRendering();
        boolean isContinuousEnforced = AndroidGraphics.enforceContinuousRendering;
        AndroidGraphics.enforceContinuousRendering = true;
        this.graphics.setContinuousRendering(true);
        this.graphics.pause();
        this.input.onPause();
        if (isFinishing()) {
            this.graphics.clearManagedCaches();
            this.graphics.destroy();
        }
        AndroidGraphics.enforceContinuousRendering = isContinuousEnforced;
        this.graphics.setContinuousRendering(isContinuous);
        this.graphics.onPauseGLSurfaceView();
        super.onPause();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        Gdx.app = this;
        Gdx.input = getInput();
        Gdx.audio = getAudio();
        Gdx.files = getFiles();
        Gdx.graphics = getGraphics();
        Gdx.net = getNet();
        this.input.onResume();
        AndroidGraphics androidGraphics = this.graphics;
        if (androidGraphics != null) {
            androidGraphics.onResumeGLSurfaceView();
        }
        if (!this.firstResume) {
            this.graphics.resume();
        } else {
            this.firstResume = false;
        }
        this.isWaitingForAudio = true;
        int i = this.wasFocusChanged;
        if (i == 1 || i == -1) {
            this.audio.resume();
            this.isWaitingForAudio = false;
        }
        super.onResume();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
    }

    public ApplicationListener getApplicationListener() {
        return this.listener;
    }

    public Audio getAudio() {
        return this.audio;
    }

    public Files getFiles() {
        return this.files;
    }

    public Graphics getGraphics() {
        return this.graphics;
    }

    public AndroidInput getInput() {
        return this.input;
    }

    public Net getNet() {
        return this.net;
    }

    public Application.ApplicationType getType() {
        return Application.ApplicationType.Android;
    }

    public int getVersion() {
        return Build.VERSION.SDK_INT;
    }

    public long getJavaHeap() {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }

    public long getNativeHeap() {
        return Debug.getNativeHeapAllocatedSize();
    }

    public Preferences getPreferences(String name) {
        return new AndroidPreferences(getSharedPreferences(name, 0));
    }

    public Clipboard getClipboard() {
        return this.clipboard;
    }

    public void postRunnable(Runnable runnable) {
        synchronized (this.runnables) {
            this.runnables.add(runnable);
            Gdx.graphics.requestRendering();
        }
    }

    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        boolean keyboardAvailable = false;
        if (config.hardKeyboardHidden == 1) {
            keyboardAvailable = true;
        }
        this.input.keyboardAvailable = keyboardAvailable;
    }

    public void exit() {
        this.handler.post(new Runnable() {
            public void run() {
                AndroidApplication.this.finish();
            }
        });
    }

    public void debug(String tag, String message) {
        if (this.logLevel >= 3) {
            getApplicationLogger().debug(tag, message);
        }
    }

    public void debug(String tag, String message, Throwable exception) {
        if (this.logLevel >= 3) {
            getApplicationLogger().debug(tag, message, exception);
        }
    }

    public void log(String tag, String message) {
        if (this.logLevel >= 2) {
            getApplicationLogger().log(tag, message);
        }
    }

    public void log(String tag, String message, Throwable exception) {
        if (this.logLevel >= 2) {
            getApplicationLogger().log(tag, message, exception);
        }
    }

    public void error(String tag, String message) {
        if (this.logLevel >= 1) {
            getApplicationLogger().error(tag, message);
        }
    }

    public void error(String tag, String message, Throwable exception) {
        if (this.logLevel >= 1) {
            getApplicationLogger().error(tag, message, exception);
        }
    }

    public void setLogLevel(int logLevel2) {
        this.logLevel = logLevel2;
    }

    public int getLogLevel() {
        return this.logLevel;
    }

    public void setApplicationLogger(ApplicationLogger applicationLogger2) {
        this.applicationLogger = applicationLogger2;
    }

    public ApplicationLogger getApplicationLogger() {
        return this.applicationLogger;
    }

    public void addLifecycleListener(LifecycleListener listener2) {
        synchronized (this.lifecycleListeners) {
            this.lifecycleListeners.add(listener2);
        }
    }

    public void removeLifecycleListener(LifecycleListener listener2) {
        synchronized (this.lifecycleListeners) {
            this.lifecycleListeners.removeValue(listener2, true);
        }
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        synchronized (this.androidEventListeners) {
            for (int i = 0; i < this.androidEventListeners.size; i++) {
                this.androidEventListeners.get(i).onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    public void addAndroidEventListener(AndroidEventListener listener2) {
        synchronized (this.androidEventListeners) {
            this.androidEventListeners.add(listener2);
        }
    }

    public void removeAndroidEventListener(AndroidEventListener listener2) {
        synchronized (this.androidEventListeners) {
            this.androidEventListeners.removeValue(listener2, true);
        }
    }

    public Context getContext() {
        return this;
    }

    public Array<Runnable> getRunnables() {
        return this.runnables;
    }

    public Array<Runnable> getExecutedRunnables() {
        return this.executedRunnables;
    }

    public SnapshotArray<LifecycleListener> getLifecycleListeners() {
        return this.lifecycleListeners;
    }

    public Window getApplicationWindow() {
        return getWindow();
    }

    public Handler getHandler() {
        return this.handler;
    }
}
