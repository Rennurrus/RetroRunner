package com.badlogic.gdx.backends.android.surfaceview;

import android.content.Context;
import android.opengl.GLDebugHelper;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.badlogic.gdx.graphics.GL20;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;

public class GLSurfaceViewAPI18 extends SurfaceView implements SurfaceHolder.Callback {
    public static final int DEBUG_CHECK_GL_ERROR = 1;
    public static final int DEBUG_LOG_GL_CALLS = 2;
    private static final boolean LOG_ATTACH_DETACH = false;
    private static final boolean LOG_EGL = false;
    private static final boolean LOG_PAUSE_RESUME = false;
    private static final boolean LOG_RENDERER = false;
    private static final boolean LOG_RENDERER_DRAW_FRAME = false;
    private static final boolean LOG_SURFACE = false;
    private static final boolean LOG_THREADS = false;
    public static final int RENDERMODE_CONTINUOUSLY = 1;
    public static final int RENDERMODE_WHEN_DIRTY = 0;
    private static final String TAG = "GLSurfaceViewAPI18";
    /* access modifiers changed from: private */
    public static final GLThreadManager sGLThreadManager = new GLThreadManager();
    /* access modifiers changed from: private */
    public int mDebugFlags;
    private boolean mDetached;
    /* access modifiers changed from: private */
    public GLSurfaceView.EGLConfigChooser mEGLConfigChooser;
    /* access modifiers changed from: private */
    public int mEGLContextClientVersion;
    /* access modifiers changed from: private */
    public EGLContextFactory mEGLContextFactory;
    /* access modifiers changed from: private */
    public EGLWindowSurfaceFactory mEGLWindowSurfaceFactory;
    private GLThread mGLThread;
    /* access modifiers changed from: private */
    public GLWrapper mGLWrapper;
    /* access modifiers changed from: private */
    public boolean mPreserveEGLContextOnPause;
    /* access modifiers changed from: private */
    public GLSurfaceView.Renderer mRenderer;
    private final WeakReference<GLSurfaceViewAPI18> mThisWeakRef = new WeakReference<>(this);

    public interface EGLContextFactory {
        EGLContext createContext(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig);

        void destroyContext(EGL10 egl10, EGLDisplay eGLDisplay, EGLContext eGLContext);
    }

    public interface EGLWindowSurfaceFactory {
        EGLSurface createWindowSurface(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig, Object obj);

        void destroySurface(EGL10 egl10, EGLDisplay eGLDisplay, EGLSurface eGLSurface);
    }

    public interface GLWrapper {
        GL wrap(GL gl);
    }

    public GLSurfaceViewAPI18(Context context) {
        super(context);
        init();
    }

    public GLSurfaceViewAPI18(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        try {
            if (this.mGLThread != null) {
                this.mGLThread.requestExitAndWait();
            }
        } finally {
            super.finalize();
        }
    }

    private void init() {
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        if (Build.VERSION.SDK_INT <= 8) {
            holder.setFormat(4);
        }
    }

    public void setGLWrapper(GLWrapper glWrapper) {
        this.mGLWrapper = glWrapper;
    }

    public void setDebugFlags(int debugFlags) {
        this.mDebugFlags = debugFlags;
    }

    public int getDebugFlags() {
        return this.mDebugFlags;
    }

    public void setPreserveEGLContextOnPause(boolean preserveOnPause) {
        this.mPreserveEGLContextOnPause = preserveOnPause;
    }

    public boolean getPreserveEGLContextOnPause() {
        return this.mPreserveEGLContextOnPause;
    }

    public void setRenderer(GLSurfaceView.Renderer renderer) {
        checkRenderThreadState();
        if (this.mEGLConfigChooser == null) {
            this.mEGLConfigChooser = new SimpleEGLConfigChooser(true);
        }
        if (this.mEGLContextFactory == null) {
            this.mEGLContextFactory = new DefaultContextFactory();
        }
        if (this.mEGLWindowSurfaceFactory == null) {
            this.mEGLWindowSurfaceFactory = new DefaultWindowSurfaceFactory();
        }
        this.mRenderer = renderer;
        this.mGLThread = new GLThread(this.mThisWeakRef);
        this.mGLThread.start();
    }

    public void setEGLContextFactory(EGLContextFactory factory) {
        checkRenderThreadState();
        this.mEGLContextFactory = factory;
    }

    public void setEGLWindowSurfaceFactory(EGLWindowSurfaceFactory factory) {
        checkRenderThreadState();
        this.mEGLWindowSurfaceFactory = factory;
    }

    public void setEGLConfigChooser(GLSurfaceView.EGLConfigChooser configChooser) {
        checkRenderThreadState();
        this.mEGLConfigChooser = configChooser;
    }

    public void setEGLConfigChooser(boolean needDepth) {
        setEGLConfigChooser((GLSurfaceView.EGLConfigChooser) new SimpleEGLConfigChooser(needDepth));
    }

    public void setEGLConfigChooser(int redSize, int greenSize, int blueSize, int alphaSize, int depthSize, int stencilSize) {
        setEGLConfigChooser((GLSurfaceView.EGLConfigChooser) new ComponentSizeChooser(redSize, greenSize, blueSize, alphaSize, depthSize, stencilSize));
    }

    public void setEGLContextClientVersion(int version) {
        checkRenderThreadState();
        this.mEGLContextClientVersion = version;
    }

    public void setRenderMode(int renderMode) {
        this.mGLThread.setRenderMode(renderMode);
    }

    public int getRenderMode() {
        return this.mGLThread.getRenderMode();
    }

    public void requestRender() {
        this.mGLThread.requestRender();
    }

    public void surfaceCreated(SurfaceHolder holder) {
        this.mGLThread.surfaceCreated();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        this.mGLThread.surfaceDestroyed();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        this.mGLThread.onWindowResize(w, h);
    }

    public void onPause() {
        this.mGLThread.onPause();
    }

    public void onResume() {
        this.mGLThread.onResume();
    }

    public void queueEvent(Runnable r) {
        this.mGLThread.queueEvent(r);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mDetached && this.mRenderer != null) {
            int renderMode = 1;
            GLThread gLThread = this.mGLThread;
            if (gLThread != null) {
                renderMode = gLThread.getRenderMode();
            }
            this.mGLThread = new GLThread(this.mThisWeakRef);
            if (renderMode != 1) {
                this.mGLThread.setRenderMode(renderMode);
            }
            this.mGLThread.start();
        }
        this.mDetached = false;
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        GLThread gLThread = this.mGLThread;
        if (gLThread != null) {
            gLThread.requestExitAndWait();
        }
        this.mDetached = true;
        super.onDetachedFromWindow();
    }

    private class DefaultContextFactory implements EGLContextFactory {
        private int EGL_CONTEXT_CLIENT_VERSION;

        private DefaultContextFactory() {
            this.EGL_CONTEXT_CLIENT_VERSION = 12440;
        }

        public EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig config) {
            return egl.eglCreateContext(display, config, EGL10.EGL_NO_CONTEXT, GLSurfaceViewAPI18.this.mEGLContextClientVersion != 0 ? new int[]{this.EGL_CONTEXT_CLIENT_VERSION, GLSurfaceViewAPI18.this.mEGLContextClientVersion, 12344} : null);
        }

        public void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context) {
            if (!egl.eglDestroyContext(display, context)) {
                Log.e("DefaultContextFactory", "display:" + display + " context: " + context);
                EglHelper.throwEglException("eglDestroyContex", egl.eglGetError());
            }
        }
    }

    private static class DefaultWindowSurfaceFactory implements EGLWindowSurfaceFactory {
        private DefaultWindowSurfaceFactory() {
        }

        public EGLSurface createWindowSurface(EGL10 egl, EGLDisplay display, EGLConfig config, Object nativeWindow) {
            try {
                return egl.eglCreateWindowSurface(display, config, nativeWindow, (int[]) null);
            } catch (IllegalArgumentException e) {
                Log.e(GLSurfaceViewAPI18.TAG, "eglCreateWindowSurface", e);
                return null;
            }
        }

        public void destroySurface(EGL10 egl, EGLDisplay display, EGLSurface surface) {
            egl.eglDestroySurface(display, surface);
        }
    }

    private abstract class BaseConfigChooser implements GLSurfaceView.EGLConfigChooser {
        protected int[] mConfigSpec;

        /* access modifiers changed from: package-private */
        public abstract EGLConfig chooseConfig(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig[] eGLConfigArr);

        public BaseConfigChooser(int[] configSpec) {
            this.mConfigSpec = filterConfigSpec(configSpec);
        }

        public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
            int[] num_config = new int[1];
            if (egl.eglChooseConfig(display, this.mConfigSpec, (EGLConfig[]) null, 0, num_config)) {
                int numConfigs = num_config[0];
                if (numConfigs > 0) {
                    EGLConfig[] configs = new EGLConfig[numConfigs];
                    if (egl.eglChooseConfig(display, this.mConfigSpec, configs, numConfigs, num_config)) {
                        EGLConfig config = chooseConfig(egl, display, configs);
                        if (config != null) {
                            return config;
                        }
                        throw new IllegalArgumentException("No config chosen");
                    }
                    throw new IllegalArgumentException("eglChooseConfig#2 failed");
                }
                throw new IllegalArgumentException("No configs match configSpec");
            }
            throw new IllegalArgumentException("eglChooseConfig failed");
        }

        private int[] filterConfigSpec(int[] configSpec) {
            if (GLSurfaceViewAPI18.this.mEGLContextClientVersion != 2) {
                return configSpec;
            }
            int len = configSpec.length;
            int[] newConfigSpec = new int[(len + 2)];
            System.arraycopy(configSpec, 0, newConfigSpec, 0, len - 1);
            newConfigSpec[len - 1] = 12352;
            newConfigSpec[len] = 4;
            newConfigSpec[len + 1] = 12344;
            return newConfigSpec;
        }
    }

    private class ComponentSizeChooser extends BaseConfigChooser {
        protected int mAlphaSize;
        protected int mBlueSize;
        protected int mDepthSize;
        protected int mGreenSize;
        protected int mRedSize;
        protected int mStencilSize;
        private int[] mValue = new int[1];

        public ComponentSizeChooser(int redSize, int greenSize, int blueSize, int alphaSize, int depthSize, int stencilSize) {
            super(new int[]{12324, redSize, 12323, greenSize, 12322, blueSize, 12321, alphaSize, 12325, depthSize, 12326, stencilSize, 12344});
            this.mRedSize = redSize;
            this.mGreenSize = greenSize;
            this.mBlueSize = blueSize;
            this.mAlphaSize = alphaSize;
            this.mDepthSize = depthSize;
            this.mStencilSize = stencilSize;
        }

        public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display, EGLConfig[] configs) {
            for (EGLConfig config : configs) {
                EGL10 egl10 = egl;
                EGLDisplay eGLDisplay = display;
                EGLConfig eGLConfig = config;
                int d = findConfigAttrib(egl10, eGLDisplay, eGLConfig, 12325, 0);
                int s = findConfigAttrib(egl10, eGLDisplay, eGLConfig, 12326, 0);
                if (d >= this.mDepthSize && s >= this.mStencilSize) {
                    EGL10 egl102 = egl;
                    EGLDisplay eGLDisplay2 = display;
                    EGLConfig eGLConfig2 = config;
                    int r = findConfigAttrib(egl102, eGLDisplay2, eGLConfig2, 12324, 0);
                    int g = findConfigAttrib(egl102, eGLDisplay2, eGLConfig2, 12323, 0);
                    int b = findConfigAttrib(egl102, eGLDisplay2, eGLConfig2, 12322, 0);
                    int a = findConfigAttrib(egl102, eGLDisplay2, eGLConfig2, 12321, 0);
                    if (r == this.mRedSize && g == this.mGreenSize && b == this.mBlueSize && a == this.mAlphaSize) {
                        return config;
                    }
                }
            }
            return null;
        }

        private int findConfigAttrib(EGL10 egl, EGLDisplay display, EGLConfig config, int attribute, int defaultValue) {
            if (egl.eglGetConfigAttrib(display, config, attribute, this.mValue)) {
                return this.mValue[0];
            }
            return defaultValue;
        }
    }

    private class SimpleEGLConfigChooser extends ComponentSizeChooser {
        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        public SimpleEGLConfigChooser(boolean withDepthBuffer) {
            super(8, 8, 8, 0, withDepthBuffer ? 16 : 0, 0);
        }
    }

    private static class EglHelper {
        EGL10 mEgl;
        EGLConfig mEglConfig;
        EGLContext mEglContext;
        EGLDisplay mEglDisplay;
        EGLSurface mEglSurface;
        private WeakReference<GLSurfaceViewAPI18> mGLSurfaceViewWeakRef;

        public EglHelper(WeakReference<GLSurfaceViewAPI18> glSurfaceViewWeakRef) {
            this.mGLSurfaceViewWeakRef = glSurfaceViewWeakRef;
        }

        public void start() {
            this.mEgl = (EGL10) EGLContext.getEGL();
            this.mEglDisplay = this.mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
            if (this.mEglDisplay != EGL10.EGL_NO_DISPLAY) {
                if (this.mEgl.eglInitialize(this.mEglDisplay, new int[2])) {
                    GLSurfaceViewAPI18 view = (GLSurfaceViewAPI18) this.mGLSurfaceViewWeakRef.get();
                    if (view == null) {
                        this.mEglConfig = null;
                        this.mEglContext = null;
                    } else {
                        this.mEglConfig = view.mEGLConfigChooser.chooseConfig(this.mEgl, this.mEglDisplay);
                        this.mEglContext = view.mEGLContextFactory.createContext(this.mEgl, this.mEglDisplay, this.mEglConfig);
                    }
                    EGLContext eGLContext = this.mEglContext;
                    if (eGLContext == null || eGLContext == EGL10.EGL_NO_CONTEXT) {
                        this.mEglContext = null;
                        throwEglException("createContext");
                    }
                    this.mEglSurface = null;
                    return;
                }
                throw new RuntimeException("eglInitialize failed");
            }
            throw new RuntimeException("eglGetDisplay failed");
        }

        public boolean createSurface() {
            if (this.mEgl == null) {
                throw new RuntimeException("egl not initialized");
            } else if (this.mEglDisplay == null) {
                throw new RuntimeException("eglDisplay not initialized");
            } else if (this.mEglConfig != null) {
                destroySurfaceImp();
                GLSurfaceViewAPI18 view = (GLSurfaceViewAPI18) this.mGLSurfaceViewWeakRef.get();
                if (view != null) {
                    this.mEglSurface = view.mEGLWindowSurfaceFactory.createWindowSurface(this.mEgl, this.mEglDisplay, this.mEglConfig, view.getHolder());
                } else {
                    this.mEglSurface = null;
                }
                EGLSurface eGLSurface = this.mEglSurface;
                if (eGLSurface == null || eGLSurface == EGL10.EGL_NO_SURFACE) {
                    if (this.mEgl.eglGetError() == 12299) {
                        Log.e("EglHelper", "createWindowSurface returned EGL_BAD_NATIVE_WINDOW.");
                    }
                    return false;
                }
                EGL10 egl10 = this.mEgl;
                EGLDisplay eGLDisplay = this.mEglDisplay;
                EGLSurface eGLSurface2 = this.mEglSurface;
                if (egl10.eglMakeCurrent(eGLDisplay, eGLSurface2, eGLSurface2, this.mEglContext)) {
                    return true;
                }
                logEglErrorAsWarning("EGLHelper", "eglMakeCurrent", this.mEgl.eglGetError());
                return false;
            } else {
                throw new RuntimeException("mEglConfig not initialized");
            }
        }

        /* access modifiers changed from: package-private */
        public GL createGL() {
            GL gl = this.mEglContext.getGL();
            GLSurfaceViewAPI18 view = (GLSurfaceViewAPI18) this.mGLSurfaceViewWeakRef.get();
            if (view == null) {
                return gl;
            }
            if (view.mGLWrapper != null) {
                gl = view.mGLWrapper.wrap(gl);
            }
            if ((view.mDebugFlags & 3) == 0) {
                return gl;
            }
            int configFlags = 0;
            Writer log = null;
            if ((view.mDebugFlags & 1) != 0) {
                configFlags = 0 | 1;
            }
            if ((view.mDebugFlags & 2) != 0) {
                log = new LogWriter();
            }
            return GLDebugHelper.wrap(gl, configFlags, log);
        }

        public int swap() {
            if (!this.mEgl.eglSwapBuffers(this.mEglDisplay, this.mEglSurface)) {
                return this.mEgl.eglGetError();
            }
            return 12288;
        }

        public void destroySurface() {
            destroySurfaceImp();
        }

        private void destroySurfaceImp() {
            EGLSurface eGLSurface = this.mEglSurface;
            if (eGLSurface != null && eGLSurface != EGL10.EGL_NO_SURFACE) {
                this.mEgl.eglMakeCurrent(this.mEglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
                GLSurfaceViewAPI18 view = (GLSurfaceViewAPI18) this.mGLSurfaceViewWeakRef.get();
                if (view != null) {
                    view.mEGLWindowSurfaceFactory.destroySurface(this.mEgl, this.mEglDisplay, this.mEglSurface);
                }
                this.mEglSurface = null;
            }
        }

        public void finish() {
            if (this.mEglContext != null) {
                GLSurfaceViewAPI18 view = (GLSurfaceViewAPI18) this.mGLSurfaceViewWeakRef.get();
                if (view != null) {
                    view.mEGLContextFactory.destroyContext(this.mEgl, this.mEglDisplay, this.mEglContext);
                }
                this.mEglContext = null;
            }
            EGLDisplay eGLDisplay = this.mEglDisplay;
            if (eGLDisplay != null) {
                this.mEgl.eglTerminate(eGLDisplay);
                this.mEglDisplay = null;
            }
        }

        private void throwEglException(String function) {
            throwEglException(function, this.mEgl.eglGetError());
        }

        public static void throwEglException(String function, int error) {
            throw new RuntimeException(formatEglError(function, error));
        }

        public static void logEglErrorAsWarning(String tag, String function, int error) {
            Log.w(tag, formatEglError(function, error));
        }

        private static String getErrorString(int error) {
            switch (error) {
                case 12288:
                    return "EGL_SUCCESS";
                case 12289:
                    return "EGL_NOT_INITIALIZED";
                case 12290:
                    return "EGL_BAD_ACCESS";
                case 12291:
                    return "EGL_BAD_ALLOC";
                case 12292:
                    return "EGL_BAD_ATTRIBUTE";
                case 12293:
                    return "EGL_BAD_CONFIG";
                case 12294:
                    return "EGL_BAD_CONTEXT";
                case 12295:
                    return "EGL_BAD_CURRENT_SURFACE";
                case 12296:
                    return "EGL_BAD_DISPLAY";
                case 12297:
                    return "EGL_BAD_MATCH";
                case 12298:
                    return "EGL_BAD_NATIVE_PIXMAP";
                case 12299:
                    return "EGL_BAD_NATIVE_WINDOW";
                case 12300:
                    return "EGL_BAD_PARAMETER";
                case 12301:
                    return "EGL_BAD_SURFACE";
                case 12302:
                    return "EGL_CONTEXT_LOST";
                default:
                    return "0x" + Integer.toHexString(error);
            }
        }

        public static String formatEglError(String function, int error) {
            return function + " failed: " + getErrorString(error);
        }
    }

    static class GLThread extends Thread {
        private EglHelper mEglHelper;
        private ArrayList<Runnable> mEventQueue = new ArrayList<>();
        /* access modifiers changed from: private */
        public boolean mExited;
        private boolean mFinishedCreatingEglSurface;
        private WeakReference<GLSurfaceViewAPI18> mGLSurfaceViewWeakRef;
        private boolean mHasSurface;
        private boolean mHaveEglContext;
        private boolean mHaveEglSurface;
        private int mHeight = 0;
        private boolean mPaused;
        private boolean mRenderComplete;
        private int mRenderMode = 1;
        private boolean mRequestPaused;
        private boolean mRequestRender = true;
        private boolean mShouldExit;
        private boolean mShouldReleaseEglContext;
        private boolean mSizeChanged = true;
        private boolean mSurfaceIsBad;
        private boolean mWaitingForSurface;
        private int mWidth = 0;

        GLThread(WeakReference<GLSurfaceViewAPI18> glSurfaceViewWeakRef) {
            this.mGLSurfaceViewWeakRef = glSurfaceViewWeakRef;
        }

        public void run() {
            setName("GLThread " + getId());
            try {
                guardedRun();
            } catch (InterruptedException e) {
            } catch (Throwable th) {
                GLSurfaceViewAPI18.sGLThreadManager.threadExiting(this);
                throw th;
            }
            GLSurfaceViewAPI18.sGLThreadManager.threadExiting(this);
        }

        private void stopEglSurfaceLocked() {
            if (this.mHaveEglSurface) {
                this.mHaveEglSurface = false;
                this.mEglHelper.destroySurface();
            }
        }

        private void stopEglContextLocked() {
            if (this.mHaveEglContext) {
                this.mEglHelper.finish();
                this.mHaveEglContext = false;
                GLSurfaceViewAPI18.sGLThreadManager.releaseEglContextLocked(this);
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:101:?, code lost:
            r1.mFinishedCreatingEglSurface = true;
            com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18.access$800().notifyAll();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:102:0x017f, code lost:
            monitor-exit(r14);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:103:0x0180, code lost:
            r4 = false;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:108:0x0185, code lost:
            r14 = com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18.access$800();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:109:0x0189, code lost:
            monitor-enter(r14);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:112:?, code lost:
            r1.mFinishedCreatingEglSurface = true;
            r1.mSurfaceIsBad = true;
            com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18.access$800().notifyAll();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:113:0x0196, code lost:
            monitor-exit(r14);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:119:0x019c, code lost:
            if (r5 == false) goto L_0x01af;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:120:0x019e, code lost:
            r2 = (javax.microedition.khronos.opengles.GL10) r1.mEglHelper.createGL();
            com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18.access$800().checkGLDriver(r2);
            r5 = false;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:121:0x01af, code lost:
            if (r3 == false) goto L_0x01c8;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:122:0x01b1, code lost:
            r14 = (com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18) r1.mGLSurfaceViewWeakRef.get();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:123:0x01b9, code lost:
            if (r14 == null) goto L_0x01c6;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:124:0x01bb, code lost:
            com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18.access$1000(r14).onSurfaceCreated(r2, r1.mEglHelper.mEglConfig);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:125:0x01c6, code lost:
            r3 = false;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:126:0x01c8, code lost:
            if (r7 == false) goto L_0x01dc;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:127:0x01ca, code lost:
            r0 = (com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18) r1.mGLSurfaceViewWeakRef.get();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:128:0x01d2, code lost:
            if (r0 == null) goto L_0x01db;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:129:0x01d4, code lost:
            com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18.access$1000(r0).onSurfaceChanged(r2, r11, r12);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:130:0x01db, code lost:
            r7 = false;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:131:0x01dc, code lost:
            r0 = (com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18) r1.mGLSurfaceViewWeakRef.get();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:132:0x01e4, code lost:
            if (r0 == null) goto L_0x01ed;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:133:0x01e6, code lost:
            com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18.access$1000(r0).onDrawFrame(r2);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:134:0x01ed, code lost:
            r14 = r1.mEglHelper.swap();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:135:0x01f6, code lost:
            if (r14 == 12288) goto L_0x021b;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:137:0x01fa, code lost:
            if (r14 == 12302) goto L_0x0217;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:138:0x01fc, code lost:
            com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18.EglHelper.logEglErrorAsWarning("GLThread", "eglSwapBuffers", r14);
            r15 = com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18.access$800();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:139:0x0207, code lost:
            monitor-enter(r15);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:142:?, code lost:
            r1.mSurfaceIsBad = true;
            com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18.access$800().notifyAll();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:143:0x0212, code lost:
            monitor-exit(r15);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:149:0x0217, code lost:
            r6 = true;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:150:0x021b, code lost:
            if (r8 == false) goto L_0x021f;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:151:0x021d, code lost:
            r9 = true;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:91:0x015e, code lost:
            if (r13 == null) goto L_0x0166;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:93:?, code lost:
            r13.run();
            r13 = null;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:94:0x0166, code lost:
            if (r4 == false) goto L_0x019c;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:96:0x016e, code lost:
            if (r1.mEglHelper.createSurface() == false) goto L_0x0185;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:97:0x0170, code lost:
            r14 = com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18.access$800();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:98:0x0174, code lost:
            monitor-enter(r14);
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void guardedRun() throws java.lang.InterruptedException {
            /*
                r18 = this;
                r1 = r18
                com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18$EglHelper r0 = new com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18$EglHelper
                java.lang.ref.WeakReference<com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18> r2 = r1.mGLSurfaceViewWeakRef
                r0.<init>(r2)
                r1.mEglHelper = r0
                r0 = 0
                r1.mHaveEglContext = r0
                r1.mHaveEglSurface = r0
                r2 = 0
                r3 = 0
                r4 = 0
                r5 = 0
                r6 = 0
                r7 = 0
                r8 = 0
                r9 = 0
                r10 = 0
                r11 = 0
                r12 = 0
                r13 = 0
            L_0x001c:
                com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18$GLThreadManager r14 = com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18.sGLThreadManager     // Catch:{ all -> 0x022f }
                monitor-enter(r14)     // Catch:{ all -> 0x022f }
            L_0x0021:
                boolean r15 = r1.mShouldExit     // Catch:{ all -> 0x022c }
                if (r15 == 0) goto L_0x0036
                monitor-exit(r14)     // Catch:{ all -> 0x022c }
                com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18$GLThreadManager r15 = com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18.sGLThreadManager
                monitor-enter(r15)
                r18.stopEglSurfaceLocked()     // Catch:{ all -> 0x0033 }
                r18.stopEglContextLocked()     // Catch:{ all -> 0x0033 }
                monitor-exit(r15)     // Catch:{ all -> 0x0033 }
                return
            L_0x0033:
                r0 = move-exception
                monitor-exit(r15)     // Catch:{ all -> 0x0033 }
                throw r0
            L_0x0036:
                java.util.ArrayList<java.lang.Runnable> r15 = r1.mEventQueue     // Catch:{ all -> 0x022c }
                boolean r15 = r15.isEmpty()     // Catch:{ all -> 0x022c }
                if (r15 != 0) goto L_0x004c
                java.util.ArrayList<java.lang.Runnable> r15 = r1.mEventQueue     // Catch:{ all -> 0x022c }
                r0 = 0
                java.lang.Object r15 = r15.remove(r0)     // Catch:{ all -> 0x022c }
                java.lang.Runnable r15 = (java.lang.Runnable) r15     // Catch:{ all -> 0x022c }
                r0 = r15
                r13 = r0
                r0 = 0
                goto L_0x015d
            L_0x004c:
                r0 = 0
                boolean r15 = r1.mPaused     // Catch:{ all -> 0x022c }
                r16 = r0
                boolean r0 = r1.mRequestPaused     // Catch:{ all -> 0x022c }
                if (r15 == r0) goto L_0x0064
                boolean r0 = r1.mRequestPaused     // Catch:{ all -> 0x022c }
                boolean r15 = r1.mRequestPaused     // Catch:{ all -> 0x022c }
                r1.mPaused = r15     // Catch:{ all -> 0x022c }
                com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18$GLThreadManager r15 = com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18.sGLThreadManager     // Catch:{ all -> 0x022c }
                r15.notifyAll()     // Catch:{ all -> 0x022c }
                r16 = r0
            L_0x0064:
                boolean r0 = r1.mShouldReleaseEglContext     // Catch:{ all -> 0x022c }
                if (r0 == 0) goto L_0x0072
                r18.stopEglSurfaceLocked()     // Catch:{ all -> 0x022c }
                r18.stopEglContextLocked()     // Catch:{ all -> 0x022c }
                r0 = 0
                r1.mShouldReleaseEglContext = r0     // Catch:{ all -> 0x022c }
                r10 = 1
            L_0x0072:
                if (r6 == 0) goto L_0x007c
                r18.stopEglSurfaceLocked()     // Catch:{ all -> 0x022c }
                r18.stopEglContextLocked()     // Catch:{ all -> 0x022c }
                r0 = 0
                r6 = r0
            L_0x007c:
                if (r16 == 0) goto L_0x0085
                boolean r0 = r1.mHaveEglSurface     // Catch:{ all -> 0x022c }
                if (r0 == 0) goto L_0x0085
                r18.stopEglSurfaceLocked()     // Catch:{ all -> 0x022c }
            L_0x0085:
                if (r16 == 0) goto L_0x00ab
                boolean r0 = r1.mHaveEglContext     // Catch:{ all -> 0x022c }
                if (r0 == 0) goto L_0x00ab
                java.lang.ref.WeakReference<com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18> r0 = r1.mGLSurfaceViewWeakRef     // Catch:{ all -> 0x022c }
                java.lang.Object r0 = r0.get()     // Catch:{ all -> 0x022c }
                com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18 r0 = (com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18) r0     // Catch:{ all -> 0x022c }
                if (r0 != 0) goto L_0x0097
                r15 = 0
                goto L_0x009b
            L_0x0097:
                boolean r15 = r0.mPreserveEGLContextOnPause     // Catch:{ all -> 0x022c }
            L_0x009b:
                if (r15 == 0) goto L_0x00a8
                com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18$GLThreadManager r17 = com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18.sGLThreadManager     // Catch:{ all -> 0x022c }
                boolean r17 = r17.shouldReleaseEGLContextWhenPausing()     // Catch:{ all -> 0x022c }
                if (r17 == 0) goto L_0x00ab
            L_0x00a8:
                r18.stopEglContextLocked()     // Catch:{ all -> 0x022c }
            L_0x00ab:
                if (r16 == 0) goto L_0x00bc
                com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18$GLThreadManager r0 = com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18.sGLThreadManager     // Catch:{ all -> 0x022c }
                boolean r0 = r0.shouldTerminateEGLWhenPausing()     // Catch:{ all -> 0x022c }
                if (r0 == 0) goto L_0x00bc
                com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18$EglHelper r0 = r1.mEglHelper     // Catch:{ all -> 0x022c }
                r0.finish()     // Catch:{ all -> 0x022c }
            L_0x00bc:
                boolean r0 = r1.mHasSurface     // Catch:{ all -> 0x022c }
                if (r0 != 0) goto L_0x00d8
                boolean r0 = r1.mWaitingForSurface     // Catch:{ all -> 0x022c }
                if (r0 != 0) goto L_0x00d8
                boolean r0 = r1.mHaveEglSurface     // Catch:{ all -> 0x022c }
                if (r0 == 0) goto L_0x00cb
                r18.stopEglSurfaceLocked()     // Catch:{ all -> 0x022c }
            L_0x00cb:
                r0 = 1
                r1.mWaitingForSurface = r0     // Catch:{ all -> 0x022c }
                r0 = 0
                r1.mSurfaceIsBad = r0     // Catch:{ all -> 0x022c }
                com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18$GLThreadManager r0 = com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18.sGLThreadManager     // Catch:{ all -> 0x022c }
                r0.notifyAll()     // Catch:{ all -> 0x022c }
            L_0x00d8:
                boolean r0 = r1.mHasSurface     // Catch:{ all -> 0x022c }
                if (r0 == 0) goto L_0x00ea
                boolean r0 = r1.mWaitingForSurface     // Catch:{ all -> 0x022c }
                if (r0 == 0) goto L_0x00ea
                r0 = 0
                r1.mWaitingForSurface = r0     // Catch:{ all -> 0x022c }
                com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18$GLThreadManager r0 = com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18.sGLThreadManager     // Catch:{ all -> 0x022c }
                r0.notifyAll()     // Catch:{ all -> 0x022c }
            L_0x00ea:
                if (r9 == 0) goto L_0x00f8
                r8 = 0
                r9 = 0
                r0 = 1
                r1.mRenderComplete = r0     // Catch:{ all -> 0x022c }
                com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18$GLThreadManager r0 = com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18.sGLThreadManager     // Catch:{ all -> 0x022c }
                r0.notifyAll()     // Catch:{ all -> 0x022c }
            L_0x00f8:
                boolean r0 = r18.readyToDraw()     // Catch:{ all -> 0x022c }
                if (r0 == 0) goto L_0x0222
                boolean r0 = r1.mHaveEglContext     // Catch:{ all -> 0x022c }
                if (r0 != 0) goto L_0x012d
                if (r10 == 0) goto L_0x0107
                r0 = 0
                r10 = r0
                goto L_0x012d
            L_0x0107:
                com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18$GLThreadManager r0 = com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18.sGLThreadManager     // Catch:{ all -> 0x022c }
                boolean r0 = r0.tryAcquireEglContextLocked(r1)     // Catch:{ all -> 0x022c }
                if (r0 == 0) goto L_0x012d
                com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18$EglHelper r0 = r1.mEglHelper     // Catch:{ RuntimeException -> 0x0123 }
                r0.start()     // Catch:{ RuntimeException -> 0x0123 }
                r0 = 1
                r1.mHaveEglContext = r0     // Catch:{ all -> 0x022c }
                r3 = 1
                com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18$GLThreadManager r0 = com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18.sGLThreadManager     // Catch:{ all -> 0x022c }
                r0.notifyAll()     // Catch:{ all -> 0x022c }
                goto L_0x012d
            L_0x0123:
                r0 = move-exception
                com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18$GLThreadManager r15 = com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18.sGLThreadManager     // Catch:{ all -> 0x022c }
                r15.releaseEglContextLocked(r1)     // Catch:{ all -> 0x022c }
                throw r0     // Catch:{ all -> 0x022c }
            L_0x012d:
                boolean r0 = r1.mHaveEglContext     // Catch:{ all -> 0x022c }
                if (r0 == 0) goto L_0x013e
                boolean r0 = r1.mHaveEglSurface     // Catch:{ all -> 0x022c }
                if (r0 != 0) goto L_0x013e
                r0 = 1
                r1.mHaveEglSurface = r0     // Catch:{ all -> 0x022c }
                r0 = 1
                r4 = 1
                r5 = 1
                r7 = r5
                r5 = r4
                r4 = r0
            L_0x013e:
                boolean r0 = r1.mHaveEglSurface     // Catch:{ all -> 0x022c }
                if (r0 == 0) goto L_0x0222
                boolean r0 = r1.mSizeChanged     // Catch:{ all -> 0x022c }
                if (r0 == 0) goto L_0x0152
                r7 = 1
                int r0 = r1.mWidth     // Catch:{ all -> 0x022c }
                r11 = r0
                int r0 = r1.mHeight     // Catch:{ all -> 0x022c }
                r12 = r0
                r8 = 1
                r4 = 1
                r0 = 0
                r1.mSizeChanged = r0     // Catch:{ all -> 0x022c }
            L_0x0152:
                r0 = 0
                r1.mRequestRender = r0     // Catch:{ all -> 0x022c }
                com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18$GLThreadManager r15 = com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18.sGLThreadManager     // Catch:{ all -> 0x022c }
                r15.notifyAll()     // Catch:{ all -> 0x022c }
            L_0x015d:
                monitor-exit(r14)     // Catch:{ all -> 0x022c }
                if (r13 == 0) goto L_0x0166
                r13.run()     // Catch:{ all -> 0x022f }
                r13 = 0
                goto L_0x001c
            L_0x0166:
                if (r4 == 0) goto L_0x019c
                com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18$EglHelper r14 = r1.mEglHelper     // Catch:{ all -> 0x022f }
                boolean r14 = r14.createSurface()     // Catch:{ all -> 0x022f }
                if (r14 == 0) goto L_0x0185
                com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18$GLThreadManager r14 = com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18.sGLThreadManager     // Catch:{ all -> 0x022f }
                monitor-enter(r14)     // Catch:{ all -> 0x022f }
                r15 = 1
                r1.mFinishedCreatingEglSurface = r15     // Catch:{ all -> 0x0182 }
                com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18$GLThreadManager r15 = com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18.sGLThreadManager     // Catch:{ all -> 0x0182 }
                r15.notifyAll()     // Catch:{ all -> 0x0182 }
                monitor-exit(r14)     // Catch:{ all -> 0x0182 }
                r4 = 0
                goto L_0x019c
            L_0x0182:
                r0 = move-exception
                monitor-exit(r14)     // Catch:{ all -> 0x0182 }
                throw r0     // Catch:{ all -> 0x022f }
            L_0x0185:
                com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18$GLThreadManager r14 = com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18.sGLThreadManager     // Catch:{ all -> 0x022f }
                monitor-enter(r14)     // Catch:{ all -> 0x022f }
                r15 = 1
                r1.mFinishedCreatingEglSurface = r15     // Catch:{ all -> 0x0199 }
                r1.mSurfaceIsBad = r15     // Catch:{ all -> 0x0199 }
                com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18$GLThreadManager r15 = com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18.sGLThreadManager     // Catch:{ all -> 0x0199 }
                r15.notifyAll()     // Catch:{ all -> 0x0199 }
                monitor-exit(r14)     // Catch:{ all -> 0x0199 }
                goto L_0x001c
            L_0x0199:
                r0 = move-exception
                monitor-exit(r14)     // Catch:{ all -> 0x0199 }
                throw r0     // Catch:{ all -> 0x022f }
            L_0x019c:
                if (r5 == 0) goto L_0x01af
                com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18$EglHelper r14 = r1.mEglHelper     // Catch:{ all -> 0x022f }
                javax.microedition.khronos.opengles.GL r14 = r14.createGL()     // Catch:{ all -> 0x022f }
                javax.microedition.khronos.opengles.GL10 r14 = (javax.microedition.khronos.opengles.GL10) r14     // Catch:{ all -> 0x022f }
                r2 = r14
                com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18$GLThreadManager r14 = com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18.sGLThreadManager     // Catch:{ all -> 0x022f }
                r14.checkGLDriver(r2)     // Catch:{ all -> 0x022f }
                r5 = 0
            L_0x01af:
                if (r3 == 0) goto L_0x01c8
                java.lang.ref.WeakReference<com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18> r14 = r1.mGLSurfaceViewWeakRef     // Catch:{ all -> 0x022f }
                java.lang.Object r14 = r14.get()     // Catch:{ all -> 0x022f }
                com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18 r14 = (com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18) r14     // Catch:{ all -> 0x022f }
                if (r14 == 0) goto L_0x01c6
                android.opengl.GLSurfaceView$Renderer r15 = r14.mRenderer     // Catch:{ all -> 0x022f }
                com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18$EglHelper r0 = r1.mEglHelper     // Catch:{ all -> 0x022f }
                javax.microedition.khronos.egl.EGLConfig r0 = r0.mEglConfig     // Catch:{ all -> 0x022f }
                r15.onSurfaceCreated(r2, r0)     // Catch:{ all -> 0x022f }
            L_0x01c6:
                r0 = 0
                r3 = r0
            L_0x01c8:
                if (r7 == 0) goto L_0x01dc
                java.lang.ref.WeakReference<com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18> r0 = r1.mGLSurfaceViewWeakRef     // Catch:{ all -> 0x022f }
                java.lang.Object r0 = r0.get()     // Catch:{ all -> 0x022f }
                com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18 r0 = (com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18) r0     // Catch:{ all -> 0x022f }
                if (r0 == 0) goto L_0x01db
                android.opengl.GLSurfaceView$Renderer r14 = r0.mRenderer     // Catch:{ all -> 0x022f }
                r14.onSurfaceChanged(r2, r11, r12)     // Catch:{ all -> 0x022f }
            L_0x01db:
                r7 = 0
            L_0x01dc:
                java.lang.ref.WeakReference<com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18> r0 = r1.mGLSurfaceViewWeakRef     // Catch:{ all -> 0x022f }
                java.lang.Object r0 = r0.get()     // Catch:{ all -> 0x022f }
                com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18 r0 = (com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18) r0     // Catch:{ all -> 0x022f }
                if (r0 == 0) goto L_0x01ed
                android.opengl.GLSurfaceView$Renderer r14 = r0.mRenderer     // Catch:{ all -> 0x022f }
                r14.onDrawFrame(r2)     // Catch:{ all -> 0x022f }
            L_0x01ed:
                com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18$EglHelper r0 = r1.mEglHelper     // Catch:{ all -> 0x022f }
                int r0 = r0.swap()     // Catch:{ all -> 0x022f }
                r14 = r0
                r0 = 12288(0x3000, float:1.7219E-41)
                if (r14 == r0) goto L_0x021a
                r0 = 12302(0x300e, float:1.7239E-41)
                if (r14 == r0) goto L_0x0217
                java.lang.String r0 = "GLThread"
                java.lang.String r15 = "eglSwapBuffers"
                com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18.EglHelper.logEglErrorAsWarning(r0, r15, r14)     // Catch:{ all -> 0x022f }
                com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18$GLThreadManager r15 = com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18.sGLThreadManager     // Catch:{ all -> 0x022f }
                monitor-enter(r15)     // Catch:{ all -> 0x022f }
                r0 = 1
                r1.mSurfaceIsBad = r0     // Catch:{ all -> 0x0214 }
                com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18$GLThreadManager r0 = com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18.sGLThreadManager     // Catch:{ all -> 0x0214 }
                r0.notifyAll()     // Catch:{ all -> 0x0214 }
                monitor-exit(r15)     // Catch:{ all -> 0x0214 }
                goto L_0x021b
            L_0x0214:
                r0 = move-exception
                monitor-exit(r15)     // Catch:{ all -> 0x0214 }
                throw r0     // Catch:{ all -> 0x022f }
            L_0x0217:
                r0 = 1
                r6 = r0
                goto L_0x021b
            L_0x021a:
            L_0x021b:
                if (r8 == 0) goto L_0x021f
                r0 = 1
                r9 = r0
            L_0x021f:
                r0 = 0
                goto L_0x001c
            L_0x0222:
                com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18$GLThreadManager r0 = com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18.sGLThreadManager     // Catch:{ all -> 0x022c }
                r0.wait()     // Catch:{ all -> 0x022c }
                r0 = 0
                goto L_0x0021
            L_0x022c:
                r0 = move-exception
                monitor-exit(r14)     // Catch:{ all -> 0x022c }
                throw r0     // Catch:{ all -> 0x022f }
            L_0x022f:
                r0 = move-exception
                com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18$GLThreadManager r2 = com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18.sGLThreadManager
                monitor-enter(r2)
                r18.stopEglSurfaceLocked()     // Catch:{ all -> 0x023d }
                r18.stopEglContextLocked()     // Catch:{ all -> 0x023d }
                monitor-exit(r2)     // Catch:{ all -> 0x023d }
                throw r0
            L_0x023d:
                r0 = move-exception
                monitor-exit(r2)     // Catch:{ all -> 0x023d }
                goto L_0x0241
            L_0x0240:
                throw r0
            L_0x0241:
                goto L_0x0240
            */
            throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18.GLThread.guardedRun():void");
        }

        public boolean ableToDraw() {
            return this.mHaveEglContext && this.mHaveEglSurface && readyToDraw();
        }

        private boolean readyToDraw() {
            return !this.mPaused && this.mHasSurface && !this.mSurfaceIsBad && this.mWidth > 0 && this.mHeight > 0 && (this.mRequestRender || this.mRenderMode == 1);
        }

        public void setRenderMode(int renderMode) {
            if (renderMode < 0 || renderMode > 1) {
                throw new IllegalArgumentException("renderMode");
            }
            synchronized (GLSurfaceViewAPI18.sGLThreadManager) {
                this.mRenderMode = renderMode;
                GLSurfaceViewAPI18.sGLThreadManager.notifyAll();
            }
        }

        public int getRenderMode() {
            int i;
            synchronized (GLSurfaceViewAPI18.sGLThreadManager) {
                i = this.mRenderMode;
            }
            return i;
        }

        public void requestRender() {
            synchronized (GLSurfaceViewAPI18.sGLThreadManager) {
                this.mRequestRender = true;
                GLSurfaceViewAPI18.sGLThreadManager.notifyAll();
            }
        }

        public void surfaceCreated() {
            synchronized (GLSurfaceViewAPI18.sGLThreadManager) {
                this.mHasSurface = true;
                this.mFinishedCreatingEglSurface = false;
                GLSurfaceViewAPI18.sGLThreadManager.notifyAll();
                while (this.mWaitingForSurface && !this.mFinishedCreatingEglSurface && !this.mExited) {
                    try {
                        GLSurfaceViewAPI18.sGLThreadManager.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void surfaceDestroyed() {
            synchronized (GLSurfaceViewAPI18.sGLThreadManager) {
                this.mHasSurface = false;
                GLSurfaceViewAPI18.sGLThreadManager.notifyAll();
                while (!this.mWaitingForSurface && !this.mExited) {
                    try {
                        GLSurfaceViewAPI18.sGLThreadManager.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void onPause() {
            synchronized (GLSurfaceViewAPI18.sGLThreadManager) {
                this.mRequestPaused = true;
                GLSurfaceViewAPI18.sGLThreadManager.notifyAll();
                while (!this.mExited && !this.mPaused) {
                    try {
                        GLSurfaceViewAPI18.sGLThreadManager.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void onResume() {
            synchronized (GLSurfaceViewAPI18.sGLThreadManager) {
                this.mRequestPaused = false;
                this.mRequestRender = true;
                this.mRenderComplete = false;
                GLSurfaceViewAPI18.sGLThreadManager.notifyAll();
                while (!this.mExited && this.mPaused && !this.mRenderComplete) {
                    try {
                        GLSurfaceViewAPI18.sGLThreadManager.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void onWindowResize(int w, int h) {
            synchronized (GLSurfaceViewAPI18.sGLThreadManager) {
                this.mWidth = w;
                this.mHeight = h;
                this.mSizeChanged = true;
                this.mRequestRender = true;
                this.mRenderComplete = false;
                GLSurfaceViewAPI18.sGLThreadManager.notifyAll();
                while (!this.mExited && !this.mPaused && !this.mRenderComplete && ableToDraw()) {
                    try {
                        GLSurfaceViewAPI18.sGLThreadManager.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void requestExitAndWait() {
            synchronized (GLSurfaceViewAPI18.sGLThreadManager) {
                this.mShouldExit = true;
                GLSurfaceViewAPI18.sGLThreadManager.notifyAll();
                while (!this.mExited) {
                    try {
                        GLSurfaceViewAPI18.sGLThreadManager.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void requestReleaseEglContextLocked() {
            this.mShouldReleaseEglContext = true;
            GLSurfaceViewAPI18.sGLThreadManager.notifyAll();
        }

        public void queueEvent(Runnable r) {
            if (r != null) {
                synchronized (GLSurfaceViewAPI18.sGLThreadManager) {
                    this.mEventQueue.add(r);
                    GLSurfaceViewAPI18.sGLThreadManager.notifyAll();
                }
                return;
            }
            throw new IllegalArgumentException("r must not be null");
        }
    }

    static class LogWriter extends Writer {
        private StringBuilder mBuilder = new StringBuilder();

        LogWriter() {
        }

        public void close() {
            flushBuilder();
        }

        public void flush() {
            flushBuilder();
        }

        public void write(char[] buf, int offset, int count) {
            for (int i = 0; i < count; i++) {
                char c = buf[offset + i];
                if (c == 10) {
                    flushBuilder();
                } else {
                    this.mBuilder.append(c);
                }
            }
        }

        private void flushBuilder() {
            if (this.mBuilder.length() > 0) {
                Log.v("GLSurfaceView", this.mBuilder.toString());
                StringBuilder sb = this.mBuilder;
                sb.delete(0, sb.length());
            }
        }
    }

    private void checkRenderThreadState() {
        if (this.mGLThread != null) {
            throw new IllegalStateException("setRenderer has already been called for this instance.");
        }
    }

    private static class GLThreadManager {
        private static String TAG = "GLThreadManager";
        private static final int kGLES_20 = 131072;
        private static final String kMSM7K_RENDERER_PREFIX = "Q3Dimension MSM7500 ";
        private GLThread mEglOwner;
        private boolean mGLESDriverCheckComplete;
        private int mGLESVersion;
        private boolean mGLESVersionCheckComplete;
        private boolean mLimitedGLESContexts;
        private boolean mMultipleGLESContextsAllowed;

        private GLThreadManager() {
        }

        public synchronized void threadExiting(GLThread thread) {
            boolean unused = thread.mExited = true;
            if (this.mEglOwner == thread) {
                this.mEglOwner = null;
            }
            notifyAll();
        }

        public boolean tryAcquireEglContextLocked(GLThread thread) {
            GLThread gLThread = this.mEglOwner;
            if (gLThread == thread || gLThread == null) {
                this.mEglOwner = thread;
                notifyAll();
                return true;
            }
            checkGLESVersion();
            if (this.mMultipleGLESContextsAllowed) {
                return true;
            }
            GLThread gLThread2 = this.mEglOwner;
            if (gLThread2 == null) {
                return false;
            }
            gLThread2.requestReleaseEglContextLocked();
            return false;
        }

        public void releaseEglContextLocked(GLThread thread) {
            if (this.mEglOwner == thread) {
                this.mEglOwner = null;
            }
            notifyAll();
        }

        public synchronized boolean shouldReleaseEGLContextWhenPausing() {
            return this.mLimitedGLESContexts;
        }

        public synchronized boolean shouldTerminateEGLWhenPausing() {
            checkGLESVersion();
            return !this.mMultipleGLESContextsAllowed;
        }

        public synchronized void checkGLDriver(GL10 gl) {
            if (!this.mGLESDriverCheckComplete) {
                checkGLESVersion();
                String renderer = gl.glGetString(GL20.GL_RENDERER);
                boolean z = false;
                if (this.mGLESVersion < kGLES_20) {
                    this.mMultipleGLESContextsAllowed = !renderer.startsWith(kMSM7K_RENDERER_PREFIX);
                    notifyAll();
                }
                if (!this.mMultipleGLESContextsAllowed) {
                    z = true;
                }
                this.mLimitedGLESContexts = z;
                this.mGLESDriverCheckComplete = true;
            }
        }

        private void checkGLESVersion() {
            if (!this.mGLESVersionCheckComplete) {
                this.mGLESVersion = kGLES_20;
                if (this.mGLESVersion >= kGLES_20) {
                    this.mMultipleGLESContextsAllowed = true;
                }
                this.mGLESVersionCheckComplete = true;
            }
        }
    }
}
