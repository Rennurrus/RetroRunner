package com.badlogic.gdx.backends.android;

import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Process;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.backends.android.surfaceview.GLSurfaceView20;
import com.badlogic.gdx.backends.android.surfaceview.GLSurfaceView20API18;
import com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewAPI18;
import com.badlogic.gdx.backends.android.surfaceview.GdxEglConfigChooser;
import com.badlogic.gdx.backends.android.surfaceview.ResolutionStrategy;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureArray;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.GLVersion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.WindowedMean;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.SnapshotArray;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

public class AndroidGraphics implements Graphics, GLSurfaceView.Renderer {
    private static final String LOG_TAG = "AndroidGraphics";
    static volatile boolean enforceContinuousRendering = false;
    AndroidApplicationBase app;
    private Graphics.BufferFormat bufferFormat;
    protected final AndroidApplicationConfiguration config;
    volatile boolean created;
    protected float deltaTime;
    private float density;
    volatile boolean destroy;
    EGLContext eglContext;
    String extensions;
    protected int fps;
    protected long frameId;
    protected long frameStart;
    protected int frames;
    GL20 gl20;
    GL30 gl30;
    GLVersion glVersion;
    int height;
    private boolean isContinuous;
    protected long lastFrameTime;
    protected WindowedMean mean;
    volatile boolean pause;
    private float ppcX;
    private float ppcY;
    private float ppiX;
    private float ppiY;
    volatile boolean resume;
    volatile boolean running;
    Object synch;
    int[] value;
    final View view;
    int width;

    public AndroidGraphics(AndroidApplicationBase application, AndroidApplicationConfiguration config2, ResolutionStrategy resolutionStrategy) {
        this(application, config2, resolutionStrategy, true);
    }

    public AndroidGraphics(AndroidApplicationBase application, AndroidApplicationConfiguration config2, ResolutionStrategy resolutionStrategy, boolean focusableView) {
        this.lastFrameTime = System.nanoTime();
        this.deltaTime = 0.0f;
        this.frameStart = System.nanoTime();
        this.frameId = -1;
        this.frames = 0;
        this.mean = new WindowedMean(5);
        this.created = false;
        this.running = false;
        this.pause = false;
        this.resume = false;
        this.destroy = false;
        this.ppiX = 0.0f;
        this.ppiY = 0.0f;
        this.ppcX = 0.0f;
        this.ppcY = 0.0f;
        this.density = 1.0f;
        this.bufferFormat = new Graphics.BufferFormat(5, 6, 5, 0, 16, 0, 0, false);
        this.isContinuous = true;
        this.value = new int[1];
        this.synch = new Object();
        this.config = config2;
        this.app = application;
        this.view = createGLSurfaceView(application, resolutionStrategy);
        preserveEGLContextOnPause();
        if (focusableView) {
            this.view.setFocusable(true);
            this.view.setFocusableInTouchMode(true);
        }
    }

    /* access modifiers changed from: protected */
    public void preserveEGLContextOnPause() {
        if ((Build.VERSION.SDK_INT >= 11 && (this.view instanceof GLSurfaceView20)) || (this.view instanceof GLSurfaceView20API18)) {
            try {
                this.view.getClass().getMethod("setPreserveEGLContextOnPause", new Class[]{Boolean.TYPE}).invoke(this.view, new Object[]{true});
            } catch (Exception e) {
                Gdx.app.log(LOG_TAG, "Method GLSurfaceView.setPreserveEGLContextOnPause not found");
            }
        }
    }

    /* access modifiers changed from: protected */
    public View createGLSurfaceView(AndroidApplicationBase application, ResolutionStrategy resolutionStrategy) {
        if (checkGL20()) {
            GLSurfaceView.EGLConfigChooser configChooser = getEglConfigChooser();
            if (Build.VERSION.SDK_INT > 10 || !this.config.useGLSurfaceView20API18) {
                GLSurfaceView20 view2 = new GLSurfaceView20(application.getContext(), resolutionStrategy, this.config.useGL30 ? 3 : 2);
                if (configChooser != null) {
                    view2.setEGLConfigChooser(configChooser);
                } else {
                    view2.setEGLConfigChooser(this.config.r, this.config.g, this.config.b, this.config.a, this.config.depth, this.config.stencil);
                }
                view2.setRenderer(this);
                return view2;
            }
            GLSurfaceView20API18 view3 = new GLSurfaceView20API18(application.getContext(), resolutionStrategy);
            if (configChooser != null) {
                view3.setEGLConfigChooser(configChooser);
            } else {
                view3.setEGLConfigChooser(this.config.r, this.config.g, this.config.b, this.config.a, this.config.depth, this.config.stencil);
            }
            view3.setRenderer(this);
            return view3;
        }
        throw new GdxRuntimeException("Libgdx requires OpenGL ES 2.0");
    }

    public void onPauseGLSurfaceView() {
        View view2 = this.view;
        if (view2 != null) {
            if (view2 instanceof GLSurfaceViewAPI18) {
                ((GLSurfaceViewAPI18) view2).onPause();
            }
            View view3 = this.view;
            if (view3 instanceof GLSurfaceView) {
                ((GLSurfaceView) view3).onPause();
            }
        }
    }

    public void onResumeGLSurfaceView() {
        View view2 = this.view;
        if (view2 != null) {
            if (view2 instanceof GLSurfaceViewAPI18) {
                ((GLSurfaceViewAPI18) view2).onResume();
            }
            View view3 = this.view;
            if (view3 instanceof GLSurfaceView) {
                ((GLSurfaceView) view3).onResume();
            }
        }
    }

    /* access modifiers changed from: protected */
    public GLSurfaceView.EGLConfigChooser getEglConfigChooser() {
        return new GdxEglConfigChooser(this.config.r, this.config.g, this.config.b, this.config.a, this.config.depth, this.config.stencil, this.config.numSamples);
    }

    /* access modifiers changed from: protected */
    public void updatePpi() {
        DisplayMetrics metrics = new DisplayMetrics();
        this.app.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        this.ppiX = metrics.xdpi;
        this.ppiY = metrics.ydpi;
        this.ppcX = metrics.xdpi / 2.54f;
        this.ppcY = metrics.ydpi / 2.54f;
        this.density = metrics.density;
    }

    /* access modifiers changed from: protected */
    public boolean checkGL20() {
        EGL10 egl = (EGL10) EGLContext.getEGL();
        EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        egl.eglInitialize(display, new int[2]);
        int[] num_config = new int[1];
        egl.eglChooseConfig(display, new int[]{12324, 4, 12323, 4, 12322, 4, 12352, 4, 12344}, new EGLConfig[10], 10, num_config);
        egl.eglTerminate(display);
        if (num_config[0] > 0) {
            return true;
        }
        return false;
    }

    public GL20 getGL20() {
        return this.gl20;
    }

    public void setGL20(GL20 gl202) {
        this.gl20 = gl202;
        if (this.gl30 == null) {
            Gdx.gl = gl202;
            Gdx.gl20 = gl202;
        }
    }

    public boolean isGL30Available() {
        return this.gl30 != null;
    }

    public GL30 getGL30() {
        return this.gl30;
    }

    public void setGL30(GL30 gl302) {
        this.gl30 = gl302;
        if (gl302 != null) {
            this.gl20 = gl302;
            GL20 gl202 = this.gl20;
            Gdx.gl = gl202;
            Gdx.gl20 = gl202;
            Gdx.gl30 = gl302;
        }
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    public int getBackBufferWidth() {
        return this.width;
    }

    public int getBackBufferHeight() {
        return this.height;
    }

    /* access modifiers changed from: protected */
    public void setupGL(GL10 gl) {
        this.glVersion = new GLVersion(Application.ApplicationType.Android, gl.glGetString(GL20.GL_VERSION), gl.glGetString(GL20.GL_VENDOR), gl.glGetString(GL20.GL_RENDERER));
        if (!this.config.useGL30 || this.glVersion.getMajorVersion() <= 2) {
            if (this.gl20 == null) {
                this.gl20 = new AndroidGL20();
                GL20 gl202 = this.gl20;
                Gdx.gl = gl202;
                Gdx.gl20 = gl202;
            } else {
                return;
            }
        } else if (this.gl30 == null) {
            AndroidGL30 androidGL30 = new AndroidGL30();
            this.gl30 = androidGL30;
            this.gl20 = androidGL30;
            GL30 gl302 = this.gl30;
            Gdx.gl = gl302;
            Gdx.gl20 = gl302;
            Gdx.gl30 = gl302;
        } else {
            return;
        }
        Application application = Gdx.app;
        application.log(LOG_TAG, "OGL renderer: " + gl.glGetString(GL20.GL_RENDERER));
        Application application2 = Gdx.app;
        application2.log(LOG_TAG, "OGL vendor: " + gl.glGetString(GL20.GL_VENDOR));
        Application application3 = Gdx.app;
        application3.log(LOG_TAG, "OGL version: " + gl.glGetString(GL20.GL_VERSION));
        Application application4 = Gdx.app;
        application4.log(LOG_TAG, "OGL extensions: " + gl.glGetString(GL20.GL_EXTENSIONS));
    }

    public void onSurfaceChanged(GL10 gl, int width2, int height2) {
        this.width = width2;
        this.height = height2;
        updatePpi();
        gl.glViewport(0, 0, this.width, this.height);
        if (!this.created) {
            this.app.getApplicationListener().create();
            this.created = true;
            synchronized (this) {
                this.running = true;
            }
        }
        this.app.getApplicationListener().resize(width2, height2);
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config2) {
        this.eglContext = ((EGL10) EGLContext.getEGL()).eglGetCurrentContext();
        setupGL(gl);
        logConfig(config2);
        updatePpi();
        Mesh.invalidateAllMeshes(this.app);
        Texture.invalidateAllTextures(this.app);
        Cubemap.invalidateAllCubemaps(this.app);
        TextureArray.invalidateAllTextureArrays(this.app);
        ShaderProgram.invalidateAllShaderPrograms(this.app);
        FrameBuffer.invalidateAllFrameBuffers(this.app);
        logManagedCachesStatus();
        Display display = this.app.getWindowManager().getDefaultDisplay();
        this.width = display.getWidth();
        this.height = display.getHeight();
        this.mean = new WindowedMean(5);
        this.lastFrameTime = System.nanoTime();
        gl.glViewport(0, 0, this.width, this.height);
    }

    /* access modifiers changed from: protected */
    public void logConfig(EGLConfig config2) {
        EGL10 egl = (EGL10) EGLContext.getEGL();
        EGL10 egl10 = egl;
        EGLDisplay eglGetDisplay = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        EGLConfig eGLConfig = config2;
        int r = getAttrib(egl10, eglGetDisplay, eGLConfig, 12324, 0);
        int g = getAttrib(egl10, eglGetDisplay, eGLConfig, 12323, 0);
        int b = getAttrib(egl10, eglGetDisplay, eGLConfig, 12322, 0);
        int a = getAttrib(egl10, eglGetDisplay, eGLConfig, 12321, 0);
        int d = getAttrib(egl10, eglGetDisplay, eGLConfig, 12325, 0);
        int s = getAttrib(egl10, eglGetDisplay, eGLConfig, 12326, 0);
        int samples = Math.max(getAttrib(egl10, eglGetDisplay, eGLConfig, 12337, 0), getAttrib(egl10, eglGetDisplay, eGLConfig, GdxEglConfigChooser.EGL_COVERAGE_SAMPLES_NV, 0));
        boolean coverageSample = getAttrib(egl10, eglGetDisplay, eGLConfig, GdxEglConfigChooser.EGL_COVERAGE_SAMPLES_NV, 0) != 0;
        Application application = Gdx.app;
        application.log(LOG_TAG, "framebuffer: (" + r + ", " + g + ", " + b + ", " + a + ")");
        Application application2 = Gdx.app;
        StringBuilder sb = new StringBuilder();
        sb.append("depthbuffer: (");
        sb.append(d);
        sb.append(")");
        application2.log(LOG_TAG, sb.toString());
        Application application3 = Gdx.app;
        application3.log(LOG_TAG, "stencilbuffer: (" + s + ")");
        Application application4 = Gdx.app;
        application4.log(LOG_TAG, "samples: (" + samples + ")");
        Application application5 = Gdx.app;
        application5.log(LOG_TAG, "coverage sampling: (" + coverageSample + ")");
        int d2 = d;
        int i = a;
        int i2 = b;
        int i3 = g;
        int i4 = r;
        this.bufferFormat = new Graphics.BufferFormat(r, g, b, a, d2, s, samples, coverageSample);
    }

    private int getAttrib(EGL10 egl, EGLDisplay display, EGLConfig config2, int attrib, int defValue) {
        if (egl.eglGetConfigAttrib(display, config2, attrib, this.value)) {
            return this.value[0];
        }
        return defValue;
    }

    /* access modifiers changed from: package-private */
    public void resume() {
        synchronized (this.synch) {
            this.running = true;
            this.resume = true;
        }
    }

    /* access modifiers changed from: package-private */
    public void pause() {
        synchronized (this.synch) {
            if (this.running) {
                this.running = false;
                this.pause = true;
                while (this.pause) {
                    try {
                        this.synch.wait(4000);
                        if (this.pause) {
                            Gdx.app.error(LOG_TAG, "waiting for pause synchronization took too long; assuming deadlock and killing");
                            Process.killProcess(Process.myPid());
                        }
                    } catch (InterruptedException e) {
                        Gdx.app.log(LOG_TAG, "waiting for pause synchronization failed!");
                    }
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void destroy() {
        synchronized (this.synch) {
            this.running = false;
            this.destroy = true;
            while (this.destroy) {
                try {
                    this.synch.wait();
                } catch (InterruptedException e) {
                    Gdx.app.log(LOG_TAG, "waiting for destroy synchronization failed!");
                }
            }
        }
    }

    public void onDrawFrame(GL10 gl) {
        boolean lrunning;
        boolean lpause;
        boolean ldestroy;
        boolean lresume;
        long time = System.nanoTime();
        this.deltaTime = ((float) (time - this.lastFrameTime)) / 1.0E9f;
        this.lastFrameTime = time;
        if (!this.resume) {
            this.mean.addValue(this.deltaTime);
        } else {
            this.deltaTime = 0.0f;
        }
        synchronized (this.synch) {
            lrunning = this.running;
            lpause = this.pause;
            ldestroy = this.destroy;
            lresume = this.resume;
            if (this.resume) {
                this.resume = false;
            }
            if (this.pause) {
                this.pause = false;
                this.synch.notifyAll();
            }
            if (this.destroy) {
                this.destroy = false;
                this.synch.notifyAll();
            }
        }
        if (lresume) {
            SnapshotArray<LifecycleListener> lifecycleListeners = this.app.getLifecycleListeners();
            synchronized (lifecycleListeners) {
                LifecycleListener[] listeners = (LifecycleListener[]) lifecycleListeners.begin();
                int n = lifecycleListeners.size;
                for (int i = 0; i < n; i++) {
                    listeners[i].resume();
                }
                lifecycleListeners.end();
            }
            this.app.getApplicationListener().resume();
            Gdx.app.log(LOG_TAG, "resumed");
        }
        if (lrunning) {
            synchronized (this.app.getRunnables()) {
                this.app.getExecutedRunnables().clear();
                this.app.getExecutedRunnables().addAll(this.app.getRunnables());
                this.app.getRunnables().clear();
            }
            for (int i2 = 0; i2 < this.app.getExecutedRunnables().size; i2++) {
                try {
                    this.app.getExecutedRunnables().get(i2).run();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
            this.app.getInput().processEvents();
            this.frameId++;
            this.app.getApplicationListener().render();
        }
        if (lpause) {
            SnapshotArray<LifecycleListener> lifecycleListeners2 = this.app.getLifecycleListeners();
            synchronized (lifecycleListeners2) {
                LifecycleListener[] listeners2 = (LifecycleListener[]) lifecycleListeners2.begin();
                int n2 = lifecycleListeners2.size;
                for (int i3 = 0; i3 < n2; i3++) {
                    listeners2[i3].pause();
                }
            }
            this.app.getApplicationListener().pause();
            Gdx.app.log(LOG_TAG, "paused");
        }
        if (ldestroy) {
            SnapshotArray<LifecycleListener> lifecycleListeners3 = this.app.getLifecycleListeners();
            synchronized (lifecycleListeners3) {
                LifecycleListener[] listeners3 = (LifecycleListener[]) lifecycleListeners3.begin();
                int n3 = lifecycleListeners3.size;
                for (int i4 = 0; i4 < n3; i4++) {
                    listeners3[i4].dispose();
                }
            }
            this.app.getApplicationListener().dispose();
            Gdx.app.log(LOG_TAG, "destroyed");
        }
        if (time - this.frameStart > 1000000000) {
            this.fps = this.frames;
            this.frames = 0;
            this.frameStart = time;
        }
        this.frames++;
    }

    public long getFrameId() {
        return this.frameId;
    }

    public float getDeltaTime() {
        return this.mean.getMean() == 0.0f ? this.deltaTime : this.mean.getMean();
    }

    public float getRawDeltaTime() {
        return this.deltaTime;
    }

    public Graphics.GraphicsType getType() {
        return Graphics.GraphicsType.AndroidGL;
    }

    public GLVersion getGLVersion() {
        return this.glVersion;
    }

    public int getFramesPerSecond() {
        return this.fps;
    }

    public void clearManagedCaches() {
        Mesh.clearAllMeshes(this.app);
        Texture.clearAllTextures(this.app);
        Cubemap.clearAllCubemaps(this.app);
        TextureArray.clearAllTextureArrays(this.app);
        ShaderProgram.clearAllShaderPrograms(this.app);
        FrameBuffer.clearAllFrameBuffers(this.app);
        logManagedCachesStatus();
    }

    /* access modifiers changed from: protected */
    public void logManagedCachesStatus() {
        Gdx.app.log(LOG_TAG, Mesh.getManagedStatus());
        Gdx.app.log(LOG_TAG, Texture.getManagedStatus());
        Gdx.app.log(LOG_TAG, Cubemap.getManagedStatus());
        Gdx.app.log(LOG_TAG, ShaderProgram.getManagedStatus());
        Gdx.app.log(LOG_TAG, FrameBuffer.getManagedStatus());
    }

    public View getView() {
        return this.view;
    }

    public float getPpiX() {
        return this.ppiX;
    }

    public float getPpiY() {
        return this.ppiY;
    }

    public float getPpcX() {
        return this.ppcX;
    }

    public float getPpcY() {
        return this.ppcY;
    }

    public float getDensity() {
        return this.density;
    }

    public boolean supportsDisplayModeChange() {
        return false;
    }

    public boolean setFullscreenMode(Graphics.DisplayMode displayMode) {
        return false;
    }

    public Graphics.Monitor getPrimaryMonitor() {
        return new AndroidMonitor(0, 0, "Primary Monitor");
    }

    public Graphics.Monitor getMonitor() {
        return getPrimaryMonitor();
    }

    public Graphics.Monitor[] getMonitors() {
        return new Graphics.Monitor[]{getPrimaryMonitor()};
    }

    public Graphics.DisplayMode[] getDisplayModes(Graphics.Monitor monitor) {
        return getDisplayModes();
    }

    public Graphics.DisplayMode getDisplayMode(Graphics.Monitor monitor) {
        return getDisplayMode();
    }

    public Graphics.DisplayMode[] getDisplayModes() {
        return new Graphics.DisplayMode[]{getDisplayMode()};
    }

    public boolean setWindowedMode(int width2, int height2) {
        return false;
    }

    public void setTitle(String title) {
    }

    public void setUndecorated(boolean undecorated) {
        this.app.getApplicationWindow().setFlags(GL20.GL_STENCIL_BUFFER_BIT, (int) undecorated);
    }

    public void setResizable(boolean resizable) {
    }

    public Graphics.DisplayMode getDisplayMode() {
        DisplayMetrics metrics = new DisplayMetrics();
        this.app.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return new AndroidDisplayMode(metrics.widthPixels, metrics.heightPixels, 0, 0);
    }

    public Graphics.BufferFormat getBufferFormat() {
        return this.bufferFormat;
    }

    public void setVSync(boolean vsync) {
    }

    public boolean supportsExtension(String extension) {
        if (this.extensions == null) {
            this.extensions = Gdx.gl.glGetString(GL20.GL_EXTENSIONS);
        }
        return this.extensions.contains(extension);
    }

    public void setContinuousRendering(boolean isContinuous2) {
        if (this.view != null) {
            this.isContinuous = enforceContinuousRendering || isContinuous2;
            int renderMode = this.isContinuous;
            View view2 = this.view;
            if (view2 instanceof GLSurfaceViewAPI18) {
                ((GLSurfaceViewAPI18) view2).setRenderMode(renderMode);
            }
            View view3 = this.view;
            if (view3 instanceof GLSurfaceView) {
                ((GLSurfaceView) view3).setRenderMode((int) renderMode);
            }
            this.mean.clear();
        }
    }

    public boolean isContinuousRendering() {
        return this.isContinuous;
    }

    public void requestRendering() {
        View view2 = this.view;
        if (view2 != null) {
            if (view2 instanceof GLSurfaceViewAPI18) {
                ((GLSurfaceViewAPI18) view2).requestRender();
            }
            View view3 = this.view;
            if (view3 instanceof GLSurfaceView) {
                ((GLSurfaceView) view3).requestRender();
            }
        }
    }

    public boolean isFullscreen() {
        return true;
    }

    public Cursor newCursor(Pixmap pixmap, int xHotspot, int yHotspot) {
        return null;
    }

    public void setCursor(Cursor cursor) {
    }

    public void setSystemCursor(Cursor.SystemCursor systemCursor) {
    }

    private class AndroidDisplayMode extends Graphics.DisplayMode {
        protected AndroidDisplayMode(int width, int height, int refreshRate, int bitsPerPixel) {
            super(width, height, refreshRate, bitsPerPixel);
        }
    }

    private class AndroidMonitor extends Graphics.Monitor {
        public AndroidMonitor(int virtualX, int virtualY, String name) {
            super(virtualX, virtualY, name);
        }
    }
}
