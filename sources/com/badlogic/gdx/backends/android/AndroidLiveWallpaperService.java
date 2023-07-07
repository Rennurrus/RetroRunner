package com.badlogic.gdx.backends.android;

import android.os.Build;
import android.os.Bundle;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.GdxNativesLoader;

public abstract class AndroidLiveWallpaperService extends WallpaperService {
    static boolean DEBUG = false;
    static final String TAG = "WallpaperService";
    protected volatile AndroidLiveWallpaper app = null;
    protected int engines = 0;
    protected volatile boolean isPreviewNotified = false;
    protected volatile AndroidWallpaperEngine linkedEngine = null;
    protected volatile boolean notifiedPreviewState = false;
    volatile int[] sync = new int[0];
    protected SurfaceHolder.Callback view = null;
    protected int viewFormat;
    protected int viewHeight;
    protected int viewWidth;
    protected int visibleEngines = 0;

    static {
        GdxNativesLoader.load();
    }

    /* access modifiers changed from: protected */
    public void setLinkedEngine(AndroidWallpaperEngine linkedEngine2) {
        synchronized (this.sync) {
            this.linkedEngine = linkedEngine2;
        }
    }

    public void onCreate() {
        if (DEBUG) {
            Log.d(TAG, " > AndroidLiveWallpaperService - onCreate() " + hashCode());
        }
        Log.i(TAG, "service created");
        super.onCreate();
    }

    public WallpaperService.Engine onCreateEngine() {
        if (DEBUG) {
            Log.d(TAG, " > AndroidLiveWallpaperService - onCreateEngine()");
        }
        Log.i(TAG, "engine created");
        return new AndroidWallpaperEngine();
    }

    public void onCreateApplication() {
        if (DEBUG) {
            Log.d(TAG, " > AndroidLiveWallpaperService - onCreateApplication()");
        }
    }

    public void initialize(ApplicationListener listener) {
        initialize(listener, new AndroidApplicationConfiguration());
    }

    public void initialize(ApplicationListener listener, AndroidApplicationConfiguration config) {
        if (DEBUG) {
            Log.d(TAG, " > AndroidLiveWallpaperService - initialize()");
        }
        this.app.initialize(listener, config);
        if (config.getTouchEventsForLiveWallpaper && Integer.parseInt(Build.VERSION.SDK) >= 7) {
            this.linkedEngine.setTouchEventsEnabled(true);
        }
    }

    public SurfaceHolder getSurfaceHolder() {
        if (DEBUG) {
            Log.d(TAG, " > AndroidLiveWallpaperService - getSurfaceHolder()");
        }
        synchronized (this.sync) {
            if (this.linkedEngine == null) {
                return null;
            }
            SurfaceHolder surfaceHolder = this.linkedEngine.getSurfaceHolder();
            return surfaceHolder;
        }
    }

    public void onDeepPauseApplication() {
        if (DEBUG) {
            Log.d(TAG, " > AndroidLiveWallpaperService - onDeepPauseApplication()");
        }
        if (this.app != null) {
            this.app.graphics.clearManagedCaches();
        }
    }

    public void onDestroy() {
        if (DEBUG) {
            Log.d(TAG, " > AndroidLiveWallpaperService - onDestroy() " + hashCode());
        }
        Log.i(TAG, "service destroyed");
        super.onDestroy();
        if (this.app != null) {
            this.app.onDestroy();
            this.app = null;
            this.view = null;
        }
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        Log.i(TAG, "service finalized");
        super.finalize();
    }

    public AndroidLiveWallpaper getLiveWallpaper() {
        return this.app;
    }

    public WindowManager getWindowManager() {
        return (WindowManager) getSystemService("window");
    }

    public class AndroidWallpaperEngine extends WallpaperService.Engine {
        protected int engineFormat;
        protected int engineHeight;
        protected boolean engineIsVisible = false;
        protected int engineWidth;
        boolean iconDropConsumed = true;
        boolean offsetsConsumed = true;
        int xIconDrop;
        float xOffset = 0.0f;
        float xOffsetStep = 0.0f;
        int xPixelOffset = 0;
        int yIconDrop;
        float yOffset = 0.0f;
        float yOffsetStep = 0.0f;
        int yPixelOffset = 0;

        public AndroidWallpaperEngine() {
            super(AndroidLiveWallpaperService.this);
            if (AndroidLiveWallpaperService.DEBUG) {
                Log.d(AndroidLiveWallpaperService.TAG, " > AndroidWallpaperEngine() " + hashCode());
            }
        }

        public void onCreate(SurfaceHolder surfaceHolder) {
            if (AndroidLiveWallpaperService.DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append(" > AndroidWallpaperEngine - onCreate() ");
                sb.append(hashCode());
                sb.append(" running: ");
                sb.append(AndroidLiveWallpaperService.this.engines);
                sb.append(", linked: ");
                sb.append(AndroidLiveWallpaperService.this.linkedEngine == this);
                sb.append(", thread: ");
                sb.append(Thread.currentThread().toString());
                Log.d(AndroidLiveWallpaperService.TAG, sb.toString());
            }
            super.onCreate(surfaceHolder);
        }

        public void onSurfaceCreated(SurfaceHolder holder) {
            AndroidLiveWallpaperService.this.engines++;
            AndroidLiveWallpaperService.this.setLinkedEngine(this);
            if (AndroidLiveWallpaperService.DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append(" > AndroidWallpaperEngine - onSurfaceCreated() ");
                sb.append(hashCode());
                sb.append(", running: ");
                sb.append(AndroidLiveWallpaperService.this.engines);
                sb.append(", linked: ");
                sb.append(AndroidLiveWallpaperService.this.linkedEngine == this);
                Log.d(AndroidLiveWallpaperService.TAG, sb.toString());
            }
            Log.i(AndroidLiveWallpaperService.TAG, "engine surface created");
            super.onSurfaceCreated(holder);
            if (AndroidLiveWallpaperService.this.engines == 1) {
                AndroidLiveWallpaperService.this.visibleEngines = 0;
            }
            if (AndroidLiveWallpaperService.this.engines == 1 && AndroidLiveWallpaperService.this.app == null) {
                AndroidLiveWallpaperService androidLiveWallpaperService = AndroidLiveWallpaperService.this;
                androidLiveWallpaperService.viewFormat = 0;
                androidLiveWallpaperService.viewWidth = 0;
                androidLiveWallpaperService.viewHeight = 0;
                androidLiveWallpaperService.app = new AndroidLiveWallpaper(androidLiveWallpaperService);
                AndroidLiveWallpaperService.this.onCreateApplication();
                if (AndroidLiveWallpaperService.this.app.graphics == null) {
                    throw new Error("You must override 'AndroidLiveWallpaperService.onCreateApplication' method and call 'initialize' from its body.");
                }
            }
            AndroidLiveWallpaperService androidLiveWallpaperService2 = AndroidLiveWallpaperService.this;
            androidLiveWallpaperService2.view = (SurfaceHolder.Callback) androidLiveWallpaperService2.app.graphics.view;
            getSurfaceHolder().removeCallback(AndroidLiveWallpaperService.this.view);
            this.engineFormat = AndroidLiveWallpaperService.this.viewFormat;
            this.engineWidth = AndroidLiveWallpaperService.this.viewWidth;
            this.engineHeight = AndroidLiveWallpaperService.this.viewHeight;
            if (AndroidLiveWallpaperService.this.engines == 1) {
                AndroidLiveWallpaperService.this.view.surfaceCreated(holder);
            } else {
                AndroidLiveWallpaperService.this.view.surfaceDestroyed(holder);
                notifySurfaceChanged(this.engineFormat, this.engineWidth, this.engineHeight, false);
                AndroidLiveWallpaperService.this.view.surfaceCreated(holder);
            }
            notifyPreviewState();
            notifyOffsetsChanged();
            if (!Gdx.graphics.isContinuousRendering()) {
                Gdx.graphics.requestRendering();
            }
        }

        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (AndroidLiveWallpaperService.DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append(" > AndroidWallpaperEngine - onSurfaceChanged() isPreview: ");
                sb.append(isPreview());
                sb.append(", ");
                sb.append(hashCode());
                sb.append(", running: ");
                sb.append(AndroidLiveWallpaperService.this.engines);
                sb.append(", linked: ");
                sb.append(AndroidLiveWallpaperService.this.linkedEngine == this);
                sb.append(", sufcace valid: ");
                sb.append(getSurfaceHolder().getSurface().isValid());
                Log.d(AndroidLiveWallpaperService.TAG, sb.toString());
            }
            Log.i(AndroidLiveWallpaperService.TAG, "engine surface changed");
            super.onSurfaceChanged(holder, format, width, height);
            notifySurfaceChanged(format, width, height, true);
        }

        private void notifySurfaceChanged(int format, int width, int height, boolean forceUpdate) {
            if (forceUpdate || format != AndroidLiveWallpaperService.this.viewFormat || width != AndroidLiveWallpaperService.this.viewWidth || height != AndroidLiveWallpaperService.this.viewHeight) {
                this.engineFormat = format;
                this.engineWidth = width;
                this.engineHeight = height;
                if (AndroidLiveWallpaperService.this.linkedEngine == this) {
                    AndroidLiveWallpaperService androidLiveWallpaperService = AndroidLiveWallpaperService.this;
                    androidLiveWallpaperService.viewFormat = this.engineFormat;
                    androidLiveWallpaperService.viewWidth = this.engineWidth;
                    androidLiveWallpaperService.viewHeight = this.engineHeight;
                    androidLiveWallpaperService.view.surfaceChanged(getSurfaceHolder(), AndroidLiveWallpaperService.this.viewFormat, AndroidLiveWallpaperService.this.viewWidth, AndroidLiveWallpaperService.this.viewHeight);
                } else if (AndroidLiveWallpaperService.DEBUG) {
                    Log.d(AndroidLiveWallpaperService.TAG, " > engine is not active, skipping surfaceChanged event");
                }
            } else if (AndroidLiveWallpaperService.DEBUG) {
                Log.d(AndroidLiveWallpaperService.TAG, " > surface is current, skipping surfaceChanged event");
            }
        }

        public void onVisibilityChanged(boolean visible) {
            boolean reportedVisible = isVisible();
            if (AndroidLiveWallpaperService.DEBUG) {
                Log.d(AndroidLiveWallpaperService.TAG, " > AndroidWallpaperEngine - onVisibilityChanged(paramVisible: " + visible + " reportedVisible: " + reportedVisible + ") " + hashCode() + ", sufcace valid: " + getSurfaceHolder().getSurface().isValid());
            }
            super.onVisibilityChanged(visible);
            if (reportedVisible || !visible) {
                notifyVisibilityChanged(visible);
            } else if (AndroidLiveWallpaperService.DEBUG) {
                Log.d(AndroidLiveWallpaperService.TAG, " > fake visibilityChanged event! Android WallpaperService likes do that!");
            }
        }

        private void notifyVisibilityChanged(boolean visible) {
            if (this.engineIsVisible != visible) {
                this.engineIsVisible = visible;
                if (this.engineIsVisible) {
                    onResume();
                } else {
                    onPause();
                }
            } else if (AndroidLiveWallpaperService.DEBUG) {
                Log.d(AndroidLiveWallpaperService.TAG, " > visible state is current, skipping visibilityChanged event!");
            }
        }

        public void onResume() {
            AndroidLiveWallpaperService.this.visibleEngines++;
            if (AndroidLiveWallpaperService.DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append(" > AndroidWallpaperEngine - onResume() ");
                sb.append(hashCode());
                sb.append(", running: ");
                sb.append(AndroidLiveWallpaperService.this.engines);
                sb.append(", linked: ");
                sb.append(AndroidLiveWallpaperService.this.linkedEngine == this);
                sb.append(", visible: ");
                sb.append(AndroidLiveWallpaperService.this.visibleEngines);
                Log.d(AndroidLiveWallpaperService.TAG, sb.toString());
            }
            Log.i(AndroidLiveWallpaperService.TAG, "engine resumed");
            if (AndroidLiveWallpaperService.this.linkedEngine != null) {
                if (AndroidLiveWallpaperService.this.linkedEngine != this) {
                    AndroidLiveWallpaperService.this.setLinkedEngine(this);
                    AndroidLiveWallpaperService.this.view.surfaceDestroyed(getSurfaceHolder());
                    notifySurfaceChanged(this.engineFormat, this.engineWidth, this.engineHeight, false);
                    AndroidLiveWallpaperService.this.view.surfaceCreated(getSurfaceHolder());
                } else {
                    notifySurfaceChanged(this.engineFormat, this.engineWidth, this.engineHeight, false);
                }
                if (AndroidLiveWallpaperService.this.visibleEngines == 1) {
                    AndroidLiveWallpaperService.this.app.onResume();
                }
                notifyPreviewState();
                notifyOffsetsChanged();
                if (!Gdx.graphics.isContinuousRendering()) {
                    Gdx.graphics.requestRendering();
                }
            }
        }

        public void onPause() {
            AndroidLiveWallpaperService.this.visibleEngines--;
            if (AndroidLiveWallpaperService.DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append(" > AndroidWallpaperEngine - onPause() ");
                sb.append(hashCode());
                sb.append(", running: ");
                sb.append(AndroidLiveWallpaperService.this.engines);
                sb.append(", linked: ");
                sb.append(AndroidLiveWallpaperService.this.linkedEngine == this);
                sb.append(", visible: ");
                sb.append(AndroidLiveWallpaperService.this.visibleEngines);
                Log.d(AndroidLiveWallpaperService.TAG, sb.toString());
            }
            Log.i(AndroidLiveWallpaperService.TAG, "engine paused");
            if (AndroidLiveWallpaperService.this.visibleEngines >= AndroidLiveWallpaperService.this.engines) {
                Log.e(AndroidLiveWallpaperService.TAG, "wallpaper lifecycle error, counted too many visible engines! repairing..");
                AndroidLiveWallpaperService androidLiveWallpaperService = AndroidLiveWallpaperService.this;
                androidLiveWallpaperService.visibleEngines = Math.max(androidLiveWallpaperService.engines - 1, 0);
            }
            if (AndroidLiveWallpaperService.this.linkedEngine != null && AndroidLiveWallpaperService.this.visibleEngines == 0) {
                AndroidLiveWallpaperService.this.app.onPause();
            }
            if (AndroidLiveWallpaperService.DEBUG) {
                Log.d(AndroidLiveWallpaperService.TAG, " > AndroidWallpaperEngine - onPause() done!");
            }
        }

        public void onSurfaceDestroyed(SurfaceHolder holder) {
            boolean z = true;
            AndroidLiveWallpaperService.this.engines--;
            if (AndroidLiveWallpaperService.DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append(" > AndroidWallpaperEngine - onSurfaceDestroyed() ");
                sb.append(hashCode());
                sb.append(", running: ");
                sb.append(AndroidLiveWallpaperService.this.engines);
                sb.append(" ,linked: ");
                if (AndroidLiveWallpaperService.this.linkedEngine != this) {
                    z = false;
                }
                sb.append(z);
                sb.append(", isVisible: ");
                sb.append(this.engineIsVisible);
                Log.d(AndroidLiveWallpaperService.TAG, sb.toString());
            }
            Log.i(AndroidLiveWallpaperService.TAG, "engine surface destroyed");
            if (AndroidLiveWallpaperService.this.engines == 0) {
                AndroidLiveWallpaperService.this.onDeepPauseApplication();
            }
            if (AndroidLiveWallpaperService.this.linkedEngine == this && AndroidLiveWallpaperService.this.view != null) {
                AndroidLiveWallpaperService.this.view.surfaceDestroyed(holder);
            }
            this.engineFormat = 0;
            this.engineWidth = 0;
            this.engineHeight = 0;
            if (AndroidLiveWallpaperService.this.engines == 0) {
                AndroidLiveWallpaperService.this.linkedEngine = null;
            }
            super.onSurfaceDestroyed(holder);
        }

        public void onDestroy() {
            super.onDestroy();
        }

        public Bundle onCommand(String pAction, int pX, int pY, int pZ, Bundle pExtras, boolean pResultRequested) {
            if (AndroidLiveWallpaperService.DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append(" > AndroidWallpaperEngine - onCommand(");
                sb.append(pAction);
                sb.append(" ");
                sb.append(pX);
                sb.append(" ");
                sb.append(pY);
                sb.append(" ");
                sb.append(pZ);
                sb.append(" ");
                sb.append(pExtras);
                sb.append(" ");
                sb.append(pResultRequested);
                sb.append("), linked: ");
                sb.append(AndroidLiveWallpaperService.this.linkedEngine == this);
                Log.d(AndroidLiveWallpaperService.TAG, sb.toString());
            }
            if (pAction.equals("android.home.drop")) {
                this.iconDropConsumed = false;
                this.xIconDrop = pX;
                this.yIconDrop = pY;
                notifyIconDropped();
            }
            return super.onCommand(pAction, pX, pY, pZ, pExtras, pResultRequested);
        }

        /* access modifiers changed from: protected */
        public void notifyIconDropped() {
            if (AndroidLiveWallpaperService.this.linkedEngine == this && (AndroidLiveWallpaperService.this.app.listener instanceof AndroidWallpaperListener) && !this.iconDropConsumed) {
                this.iconDropConsumed = true;
                AndroidLiveWallpaperService.this.app.postRunnable(new Runnable() {
                    public void run() {
                        boolean isCurrent;
                        synchronized (AndroidLiveWallpaperService.this.sync) {
                            isCurrent = AndroidLiveWallpaperService.this.linkedEngine == AndroidWallpaperEngine.this;
                        }
                        if (isCurrent) {
                            ((AndroidWallpaperListener) AndroidLiveWallpaperService.this.app.listener).iconDropped(AndroidWallpaperEngine.this.xIconDrop, AndroidWallpaperEngine.this.yIconDrop);
                        }
                    }
                });
            }
        }

        public void onTouchEvent(MotionEvent event) {
            if (AndroidLiveWallpaperService.this.linkedEngine == this) {
                AndroidLiveWallpaperService.this.app.input.onTouch((View) null, event);
            }
        }

        public void onOffsetsChanged(float xOffset2, float yOffset2, float xOffsetStep2, float yOffsetStep2, int xPixelOffset2, int yPixelOffset2) {
            this.offsetsConsumed = false;
            this.xOffset = xOffset2;
            this.yOffset = yOffset2;
            this.xOffsetStep = xOffsetStep2;
            this.yOffsetStep = yOffsetStep2;
            this.xPixelOffset = xPixelOffset2;
            this.yPixelOffset = yPixelOffset2;
            notifyOffsetsChanged();
            if (!Gdx.graphics.isContinuousRendering()) {
                Gdx.graphics.requestRendering();
            }
            super.onOffsetsChanged(xOffset2, yOffset2, xOffsetStep2, yOffsetStep2, xPixelOffset2, yPixelOffset2);
        }

        /* access modifiers changed from: protected */
        public void notifyOffsetsChanged() {
            if (AndroidLiveWallpaperService.this.linkedEngine == this && (AndroidLiveWallpaperService.this.app.listener instanceof AndroidWallpaperListener) && !this.offsetsConsumed) {
                this.offsetsConsumed = true;
                AndroidLiveWallpaperService.this.app.postRunnable(new Runnable() {
                    public void run() {
                        boolean isCurrent;
                        synchronized (AndroidLiveWallpaperService.this.sync) {
                            isCurrent = AndroidLiveWallpaperService.this.linkedEngine == AndroidWallpaperEngine.this;
                        }
                        if (isCurrent) {
                            ((AndroidWallpaperListener) AndroidLiveWallpaperService.this.app.listener).offsetChange(AndroidWallpaperEngine.this.xOffset, AndroidWallpaperEngine.this.yOffset, AndroidWallpaperEngine.this.xOffsetStep, AndroidWallpaperEngine.this.yOffsetStep, AndroidWallpaperEngine.this.xPixelOffset, AndroidWallpaperEngine.this.yPixelOffset);
                        }
                    }
                });
            }
        }

        /* access modifiers changed from: protected */
        public void notifyPreviewState() {
            if (AndroidLiveWallpaperService.this.linkedEngine == this && (AndroidLiveWallpaperService.this.app.listener instanceof AndroidWallpaperListener)) {
                final boolean currentPreviewState = AndroidLiveWallpaperService.this.linkedEngine.isPreview();
                AndroidLiveWallpaperService.this.app.postRunnable(new Runnable() {
                    public void run() {
                        AndroidLiveWallpaper currentApp;
                        boolean shouldNotify = false;
                        synchronized (AndroidLiveWallpaperService.this.sync) {
                            if (!AndroidLiveWallpaperService.this.isPreviewNotified || AndroidLiveWallpaperService.this.notifiedPreviewState != currentPreviewState) {
                                AndroidLiveWallpaperService.this.notifiedPreviewState = currentPreviewState;
                                AndroidLiveWallpaperService.this.isPreviewNotified = true;
                                shouldNotify = true;
                            }
                        }
                        if (shouldNotify && (currentApp = AndroidLiveWallpaperService.this.app) != null) {
                            ((AndroidWallpaperListener) currentApp.listener).previewStateChange(currentPreviewState);
                        }
                    }
                });
            }
        }
    }
}
