package com.badlogic.gdx.backends.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.IntSet;
import com.badlogic.gdx.utils.Pool;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class AndroidInput implements Input, View.OnKeyListener, View.OnTouchListener {
    public static final int NUM_TOUCHES = 20;
    public static final int SUPPORTED_KEYS = 260;
    final float[] R = new float[9];
    public boolean accelerometerAvailable = false;
    private SensorEventListener accelerometerListener;
    protected final float[] accelerometerValues = new float[3];
    final Application app;
    private float azimuth = 0.0f;
    int[] button = new int[20];
    private boolean compassAvailable = false;
    private SensorEventListener compassListener;
    private final AndroidApplicationConfiguration config;
    final Context context;
    private long currentEventTimeStamp = 0;
    int[] deltaX = new int[20];
    int[] deltaY = new int[20];
    public boolean gyroscopeAvailable = false;
    private SensorEventListener gyroscopeListener;
    protected final float[] gyroscopeValues = new float[3];
    private Handler handle;
    final boolean hasMultitouch;
    private float inclination = 0.0f;
    private boolean[] justPressedButtons = new boolean[20];
    private boolean[] justPressedKeys = new boolean[SUPPORTED_KEYS];
    private boolean justTouched = false;
    private int keyCount = 0;
    ArrayList<KeyEvent> keyEvents = new ArrayList<>();
    private boolean keyJustPressed = false;
    ArrayList<View.OnKeyListener> keyListeners = new ArrayList<>();
    boolean keyboardAvailable;
    private boolean[] keys = new boolean[SUPPORTED_KEYS];
    private IntSet keysToCatch = new IntSet();
    protected final float[] magneticFieldValues = new float[3];
    private SensorManager manager;
    protected final Input.Orientation nativeOrientation;
    private final AndroidOnscreenKeyboard onscreenKeyboard;
    final float[] orientation = new float[3];
    private float pitch = 0.0f;
    float[] pressure = new float[20];
    private InputProcessor processor;
    int[] realId = new int[20];
    boolean requestFocus = true;
    private float roll = 0.0f;
    private boolean rotationVectorAvailable = false;
    private SensorEventListener rotationVectorListener;
    protected final float[] rotationVectorValues = new float[3];
    private int sleepTime = 0;
    private String text = null;
    private Input.TextInputListener textListener = null;
    ArrayList<TouchEvent> touchEvents = new ArrayList<>();
    protected final AndroidTouchHandler touchHandler;
    int[] touchX = new int[20];
    int[] touchY = new int[20];
    boolean[] touched = new boolean[20];
    Pool<KeyEvent> usedKeyEvents = new Pool<KeyEvent>(16, 1000) {
        /* access modifiers changed from: protected */
        public KeyEvent newObject() {
            return new KeyEvent();
        }
    };
    Pool<TouchEvent> usedTouchEvents = new Pool<TouchEvent>(16, 1000) {
        /* access modifiers changed from: protected */
        public TouchEvent newObject() {
            return new TouchEvent();
        }
    };
    protected final Vibrator vibrator;

    static class KeyEvent {
        static final int KEY_DOWN = 0;
        static final int KEY_TYPED = 2;
        static final int KEY_UP = 1;
        char keyChar;
        int keyCode;
        long timeStamp;
        int type;

        KeyEvent() {
        }
    }

    static class TouchEvent {
        static final int TOUCH_DOWN = 0;
        static final int TOUCH_DRAGGED = 2;
        static final int TOUCH_MOVED = 4;
        static final int TOUCH_SCROLLED = 3;
        static final int TOUCH_UP = 1;
        int button;
        int pointer;
        int scrollAmount;
        long timeStamp;
        int type;
        int x;
        int y;

        TouchEvent() {
        }
    }

    public AndroidInput(Application activity, Context context2, Object view, AndroidApplicationConfiguration config2) {
        if (view instanceof View) {
            View v = (View) view;
            v.setOnKeyListener(this);
            v.setOnTouchListener(this);
            v.setFocusable(true);
            v.setFocusableInTouchMode(true);
            v.requestFocus();
        }
        this.config = config2;
        this.onscreenKeyboard = new AndroidOnscreenKeyboard(context2, new Handler(), this);
        int i = 0;
        while (true) {
            int[] iArr = this.realId;
            if (i >= iArr.length) {
                break;
            }
            iArr[i] = -1;
            i++;
        }
        this.handle = new Handler();
        this.app = activity;
        this.context = context2;
        this.sleepTime = config2.touchSleepTime;
        this.touchHandler = new AndroidMultiTouchHandler();
        this.hasMultitouch = this.touchHandler.supportsMultitouch(context2);
        this.vibrator = (Vibrator) context2.getSystemService("vibrator");
        int rotation = getRotation();
        Graphics.DisplayMode mode = this.app.getGraphics().getDisplayMode();
        if (((rotation == 0 || rotation == 180) && mode.width >= mode.height) || ((rotation == 90 || rotation == 270) && mode.width <= mode.height)) {
            this.nativeOrientation = Input.Orientation.Landscape;
        } else {
            this.nativeOrientation = Input.Orientation.Portrait;
        }
        this.keysToCatch.add(255);
    }

    public float getAccelerometerX() {
        return this.accelerometerValues[0];
    }

    public float getAccelerometerY() {
        return this.accelerometerValues[1];
    }

    public float getAccelerometerZ() {
        return this.accelerometerValues[2];
    }

    public float getGyroscopeX() {
        return this.gyroscopeValues[0];
    }

    public float getGyroscopeY() {
        return this.gyroscopeValues[1];
    }

    public float getGyroscopeZ() {
        return this.gyroscopeValues[2];
    }

    public void getTextInput(Input.TextInputListener listener, String title, String text2, String hint) {
        final String str = title;
        final String str2 = hint;
        final String str3 = text2;
        final Input.TextInputListener textInputListener = listener;
        this.handle.post(new Runnable() {
            public void run() {
                AlertDialog.Builder alert = new AlertDialog.Builder(AndroidInput.this.context);
                alert.setTitle(str);
                final EditText input = new EditText(AndroidInput.this.context);
                input.setHint(str2);
                input.setText(str3);
                input.setSingleLine();
                alert.setView(input);
                alert.setPositiveButton(AndroidInput.this.context.getString(17039370), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Gdx.app.postRunnable(new Runnable() {
                            public void run() {
                                textInputListener.input(input.getText().toString());
                            }
                        });
                    }
                });
                alert.setNegativeButton(AndroidInput.this.context.getString(17039360), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Gdx.app.postRunnable(new Runnable() {
                            public void run() {
                                textInputListener.canceled();
                            }
                        });
                    }
                });
                alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface arg0) {
                        Gdx.app.postRunnable(new Runnable() {
                            public void run() {
                                textInputListener.canceled();
                            }
                        });
                    }
                });
                alert.show();
            }
        });
    }

    public int getMaxPointers() {
        return 20;
    }

    public int getX() {
        int i;
        synchronized (this) {
            i = this.touchX[0];
        }
        return i;
    }

    public int getY() {
        int i;
        synchronized (this) {
            i = this.touchY[0];
        }
        return i;
    }

    public int getX(int pointer) {
        int i;
        synchronized (this) {
            i = this.touchX[pointer];
        }
        return i;
    }

    public int getY(int pointer) {
        int i;
        synchronized (this) {
            i = this.touchY[pointer];
        }
        return i;
    }

    public boolean isTouched(int pointer) {
        boolean z;
        synchronized (this) {
            z = this.touched[pointer];
        }
        return z;
    }

    public float getPressure() {
        return getPressure(0);
    }

    public float getPressure(int pointer) {
        return this.pressure[pointer];
    }

    /* JADX WARNING: Code restructure failed: missing block: B:20:0x001e, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x000b, code lost:
        return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean isKeyPressed(int r3) {
        /*
            r2 = this;
            monitor-enter(r2)
            r0 = -1
            r1 = 0
            if (r3 != r0) goto L_0x000e
            int r0 = r2.keyCount     // Catch:{ all -> 0x000c }
            if (r0 <= 0) goto L_0x000a
            r1 = 1
        L_0x000a:
            monitor-exit(r2)
            return r1
        L_0x000c:
            r3 = move-exception
            goto L_0x001b
        L_0x000e:
            if (r3 < 0) goto L_0x001d
            r0 = 260(0x104, float:3.64E-43)
            if (r3 < r0) goto L_0x0015
            goto L_0x001d
        L_0x0015:
            boolean[] r0 = r2.keys     // Catch:{ all -> 0x000c }
            boolean r0 = r0[r3]     // Catch:{ all -> 0x000c }
            monitor-exit(r2)
            return r0
        L_0x001b:
            monitor-exit(r2)
            throw r3
        L_0x001d:
            monitor-exit(r2)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.backends.android.AndroidInput.isKeyPressed(int):boolean");
    }

    public synchronized boolean isKeyJustPressed(int key) {
        if (key == -1) {
            return this.keyJustPressed;
        } else if (key < 0 || key >= 260) {
            return false;
        } else {
            return this.justPressedKeys[key];
        }
    }

    public boolean isTouched() {
        synchronized (this) {
            if (this.hasMultitouch) {
                for (int pointer = 0; pointer < 20; pointer++) {
                    if (this.touched[pointer]) {
                        return true;
                    }
                }
            }
            boolean z = this.touched[0];
            return z;
        }
    }

    public void setInputProcessor(InputProcessor processor2) {
        synchronized (this) {
            this.processor = processor2;
        }
    }

    /* access modifiers changed from: package-private */
    public void processEvents() {
        synchronized (this) {
            if (this.justTouched) {
                this.justTouched = false;
                for (int i = 0; i < this.justPressedButtons.length; i++) {
                    this.justPressedButtons[i] = false;
                }
            }
            if (this.keyJustPressed != 0) {
                this.keyJustPressed = false;
                for (int i2 = 0; i2 < this.justPressedKeys.length; i2++) {
                    this.justPressedKeys[i2] = false;
                }
            }
            if (this.processor != null) {
                InputProcessor processor2 = this.processor;
                int len = this.keyEvents.size();
                for (int i3 = 0; i3 < len; i3++) {
                    KeyEvent e = this.keyEvents.get(i3);
                    this.currentEventTimeStamp = e.timeStamp;
                    int i4 = e.type;
                    if (i4 == 0) {
                        processor2.keyDown(e.keyCode);
                        this.keyJustPressed = true;
                        this.justPressedKeys[e.keyCode] = true;
                    } else if (i4 == 1) {
                        processor2.keyUp(e.keyCode);
                    } else if (i4 == 2) {
                        processor2.keyTyped(e.keyChar);
                    }
                    this.usedKeyEvents.free(e);
                }
                int len2 = this.touchEvents.size();
                for (int i5 = 0; i5 < len2; i5++) {
                    TouchEvent e2 = this.touchEvents.get(i5);
                    this.currentEventTimeStamp = e2.timeStamp;
                    int i6 = e2.type;
                    if (i6 == 0) {
                        processor2.touchDown(e2.x, e2.y, e2.pointer, e2.button);
                        this.justTouched = true;
                        this.justPressedButtons[e2.button] = true;
                    } else if (i6 == 1) {
                        processor2.touchUp(e2.x, e2.y, e2.pointer, e2.button);
                    } else if (i6 == 2) {
                        processor2.touchDragged(e2.x, e2.y, e2.pointer);
                    } else if (i6 == 3) {
                        processor2.scrolled(e2.scrollAmount);
                    } else if (i6 == 4) {
                        processor2.mouseMoved(e2.x, e2.y);
                    }
                    this.usedTouchEvents.free(e2);
                }
            } else {
                int len3 = this.touchEvents.size();
                for (int i7 = 0; i7 < len3; i7++) {
                    TouchEvent e3 = this.touchEvents.get(i7);
                    if (e3.type == 0) {
                        this.justTouched = true;
                    }
                    this.usedTouchEvents.free(e3);
                }
                int len4 = this.keyEvents.size();
                for (int i8 = 0; i8 < len4; i8++) {
                    this.usedKeyEvents.free(this.keyEvents.get(i8));
                }
            }
            if (this.touchEvents.isEmpty()) {
                for (int i9 = 0; i9 < this.deltaX.length; i9++) {
                    this.deltaX[0] = 0;
                    this.deltaY[0] = 0;
                }
            }
            this.keyEvents.clear();
            this.touchEvents.clear();
        }
    }

    public boolean onTouch(View view, MotionEvent event) {
        if (this.requestFocus && view != null) {
            view.setFocusableInTouchMode(true);
            view.requestFocus();
            this.requestFocus = false;
        }
        this.touchHandler.onTouch(event, this);
        int i = this.sleepTime;
        if (i != 0) {
            try {
                Thread.sleep((long) i);
            } catch (InterruptedException e) {
            }
        }
        return true;
    }

    public void onTap(int x, int y) {
        postTap(x, y);
    }

    public void onDrop(int x, int y) {
        postTap(x, y);
    }

    /* access modifiers changed from: protected */
    public void postTap(int x, int y) {
        synchronized (this) {
            TouchEvent event = this.usedTouchEvents.obtain();
            event.timeStamp = System.nanoTime();
            event.pointer = 0;
            event.x = x;
            event.y = y;
            event.type = 0;
            this.touchEvents.add(event);
            TouchEvent event2 = this.usedTouchEvents.obtain();
            event2.timeStamp = System.nanoTime();
            event2.pointer = 0;
            event2.x = x;
            event2.y = y;
            event2.type = 1;
            this.touchEvents.add(event2);
        }
        Gdx.app.getGraphics().requestRendering();
    }

    public boolean onKey(View v, int keyCode, android.view.KeyEvent e) {
        int n = this.keyListeners.size();
        for (int i = 0; i < n; i++) {
            if (this.keyListeners.get(i).onKey(v, keyCode, e)) {
                return true;
            }
        }
        if (e.getAction() == 0 && e.getRepeatCount() > 0) {
            return this.keysToCatch.contains(keyCode);
        }
        synchronized (this) {
            if (e.getKeyCode() == 0 && e.getAction() == 2) {
                String chars = e.getCharacters();
                for (int i2 = 0; i2 < chars.length(); i2++) {
                    KeyEvent event = this.usedKeyEvents.obtain();
                    event.timeStamp = System.nanoTime();
                    event.keyCode = 0;
                    event.keyChar = chars.charAt(i2);
                    event.type = 2;
                    this.keyEvents.add(event);
                }
                return false;
            }
            char character = (char) e.getUnicodeChar();
            if (keyCode == 67) {
                character = 8;
            }
            if (e.getKeyCode() >= 0) {
                if (e.getKeyCode() < 260) {
                    int action = e.getAction();
                    if (action == 0) {
                        KeyEvent event2 = this.usedKeyEvents.obtain();
                        event2.timeStamp = System.nanoTime();
                        event2.keyChar = 0;
                        event2.keyCode = e.getKeyCode();
                        event2.type = 0;
                        if (keyCode == 4 && e.isAltPressed()) {
                            keyCode = 255;
                            event2.keyCode = 255;
                        }
                        this.keyEvents.add(event2);
                        if (!this.keys[event2.keyCode]) {
                            this.keyCount++;
                            this.keys[event2.keyCode] = true;
                        }
                    } else if (action == 1) {
                        long timeStamp = System.nanoTime();
                        KeyEvent event3 = this.usedKeyEvents.obtain();
                        event3.timeStamp = timeStamp;
                        event3.keyChar = 0;
                        event3.keyCode = e.getKeyCode();
                        event3.type = 1;
                        if (keyCode == 4 && e.isAltPressed()) {
                            keyCode = 255;
                            event3.keyCode = 255;
                        }
                        this.keyEvents.add(event3);
                        KeyEvent event4 = this.usedKeyEvents.obtain();
                        event4.timeStamp = timeStamp;
                        event4.keyChar = character;
                        event4.keyCode = 0;
                        event4.type = 2;
                        this.keyEvents.add(event4);
                        if (keyCode == 255) {
                            if (this.keys[255]) {
                                this.keyCount--;
                                this.keys[255] = false;
                            }
                        } else if (this.keys[e.getKeyCode()]) {
                            this.keyCount--;
                            this.keys[e.getKeyCode()] = false;
                        }
                    }
                    this.app.getGraphics().requestRendering();
                    return this.keysToCatch.contains(keyCode);
                }
            }
            return false;
        }
    }

    public void setOnscreenKeyboardVisible(final boolean visible) {
        this.handle.post(new Runnable() {
            public void run() {
                InputMethodManager manager = (InputMethodManager) AndroidInput.this.context.getSystemService("input_method");
                if (visible) {
                    View view = ((AndroidGraphics) AndroidInput.this.app.getGraphics()).getView();
                    view.setFocusable(true);
                    view.setFocusableInTouchMode(true);
                    manager.showSoftInput(((AndroidGraphics) AndroidInput.this.app.getGraphics()).getView(), 0);
                    return;
                }
                manager.hideSoftInputFromWindow(((AndroidGraphics) AndroidInput.this.app.getGraphics()).getView().getWindowToken(), 0);
            }
        });
    }

    public void setCatchBackKey(boolean catchBack) {
        setCatchKey(4, catchBack);
    }

    public boolean isCatchBackKey() {
        return this.keysToCatch.contains(4);
    }

    public void setCatchMenuKey(boolean catchMenu) {
        setCatchKey(82, catchMenu);
    }

    public boolean isCatchMenuKey() {
        return this.keysToCatch.contains(82);
    }

    public void setCatchKey(int keycode, boolean catchKey) {
        if (!catchKey) {
            this.keysToCatch.remove(keycode);
        } else if (catchKey) {
            this.keysToCatch.add(keycode);
        }
    }

    public boolean isCatchKey(int keycode) {
        return this.keysToCatch.contains(this.keyCount);
    }

    public void vibrate(int milliseconds) {
        if (Build.VERSION.SDK_INT >= 26) {
            this.vibrator.vibrate(VibrationEffect.createOneShot((long) milliseconds, -1));
        } else {
            this.vibrator.vibrate((long) milliseconds);
        }
    }

    public void vibrate(long[] pattern, int repeat) {
        if (Build.VERSION.SDK_INT >= 26) {
            this.vibrator.vibrate(VibrationEffect.createWaveform(pattern, repeat));
        } else {
            this.vibrator.vibrate(pattern, repeat);
        }
    }

    public void cancelVibrate() {
        this.vibrator.cancel();
    }

    public boolean justTouched() {
        return this.justTouched;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:21:0x002c, code lost:
        return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isButtonPressed(int r4) {
        /*
            r3 = this;
            monitor-enter(r3)
            boolean r0 = r3.hasMultitouch     // Catch:{ all -> 0x002d }
            r1 = 1
            if (r0 == 0) goto L_0x001c
            r0 = 0
        L_0x0007:
            r2 = 20
            if (r0 >= r2) goto L_0x001c
            boolean[] r2 = r3.touched     // Catch:{ all -> 0x002d }
            boolean r2 = r2[r0]     // Catch:{ all -> 0x002d }
            if (r2 == 0) goto L_0x0019
            int[] r2 = r3.button     // Catch:{ all -> 0x002d }
            r2 = r2[r0]     // Catch:{ all -> 0x002d }
            if (r2 != r4) goto L_0x0019
            monitor-exit(r3)     // Catch:{ all -> 0x002d }
            return r1
        L_0x0019:
            int r0 = r0 + 1
            goto L_0x0007
        L_0x001c:
            boolean[] r0 = r3.touched     // Catch:{ all -> 0x002d }
            r2 = 0
            boolean r0 = r0[r2]     // Catch:{ all -> 0x002d }
            if (r0 == 0) goto L_0x002a
            int[] r0 = r3.button     // Catch:{ all -> 0x002d }
            r0 = r0[r2]     // Catch:{ all -> 0x002d }
            if (r0 != r4) goto L_0x002a
            goto L_0x002b
        L_0x002a:
            r1 = 0
        L_0x002b:
            monitor-exit(r3)     // Catch:{ all -> 0x002d }
            return r1
        L_0x002d:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x002d }
            goto L_0x0031
        L_0x0030:
            throw r0
        L_0x0031:
            goto L_0x0030
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.backends.android.AndroidInput.isButtonPressed(int):boolean");
    }

    public boolean isButtonJustPressed(int button2) {
        if (button2 < 0 || button2 > 20) {
            return false;
        }
        return this.justPressedButtons[button2];
    }

    private void updateOrientation() {
        if (this.rotationVectorAvailable) {
            SensorManager.getRotationMatrixFromVector(this.R, this.rotationVectorValues);
        } else if (!SensorManager.getRotationMatrix(this.R, (float[]) null, this.accelerometerValues, this.magneticFieldValues)) {
            return;
        }
        SensorManager.getOrientation(this.R, this.orientation);
        this.azimuth = (float) Math.toDegrees((double) this.orientation[0]);
        this.pitch = (float) Math.toDegrees((double) this.orientation[1]);
        this.roll = (float) Math.toDegrees((double) this.orientation[2]);
    }

    public void getRotationMatrix(float[] matrix) {
        if (this.rotationVectorAvailable) {
            SensorManager.getRotationMatrixFromVector(matrix, this.rotationVectorValues);
        } else {
            SensorManager.getRotationMatrix(matrix, (float[]) null, this.accelerometerValues, this.magneticFieldValues);
        }
    }

    public float getAzimuth() {
        if (!this.compassAvailable && !this.rotationVectorAvailable) {
            return 0.0f;
        }
        updateOrientation();
        return this.azimuth;
    }

    public float getPitch() {
        if (!this.compassAvailable && !this.rotationVectorAvailable) {
            return 0.0f;
        }
        updateOrientation();
        return this.pitch;
    }

    public float getRoll() {
        if (!this.compassAvailable && !this.rotationVectorAvailable) {
            return 0.0f;
        }
        updateOrientation();
        return this.roll;
    }

    /* access modifiers changed from: package-private */
    public void registerSensorListeners() {
        if (this.config.useAccelerometer) {
            this.manager = (SensorManager) this.context.getSystemService("sensor");
            if (this.manager.getSensorList(1).isEmpty()) {
                this.accelerometerAvailable = false;
            } else {
                this.accelerometerListener = new SensorListener();
                this.accelerometerAvailable = this.manager.registerListener(this.accelerometerListener, this.manager.getSensorList(1).get(0), this.config.sensorDelay);
            }
        } else {
            this.accelerometerAvailable = false;
        }
        if (this.config.useGyroscope) {
            this.manager = (SensorManager) this.context.getSystemService("sensor");
            if (this.manager.getSensorList(4).isEmpty()) {
                this.gyroscopeAvailable = false;
            } else {
                this.gyroscopeListener = new SensorListener();
                this.gyroscopeAvailable = this.manager.registerListener(this.gyroscopeListener, this.manager.getSensorList(4).get(0), this.config.sensorDelay);
            }
        } else {
            this.gyroscopeAvailable = false;
        }
        this.rotationVectorAvailable = false;
        if (this.config.useRotationVectorSensor) {
            if (this.manager == null) {
                this.manager = (SensorManager) this.context.getSystemService("sensor");
            }
            List<Sensor> rotationVectorSensors = this.manager.getSensorList(11);
            if (!rotationVectorSensors.isEmpty()) {
                this.rotationVectorListener = new SensorListener();
                Iterator<Sensor> it = rotationVectorSensors.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    Sensor sensor = it.next();
                    if (sensor.getVendor().equals("Google Inc.") && sensor.getVersion() == 3) {
                        this.rotationVectorAvailable = this.manager.registerListener(this.rotationVectorListener, sensor, this.config.sensorDelay);
                        break;
                    }
                }
                if (!this.rotationVectorAvailable) {
                    this.rotationVectorAvailable = this.manager.registerListener(this.rotationVectorListener, rotationVectorSensors.get(0), this.config.sensorDelay);
                }
            }
        }
        if (!this.config.useCompass || this.rotationVectorAvailable) {
            this.compassAvailable = false;
        } else {
            if (this.manager == null) {
                this.manager = (SensorManager) this.context.getSystemService("sensor");
            }
            Sensor sensor2 = this.manager.getDefaultSensor(2);
            if (sensor2 != null) {
                this.compassAvailable = this.accelerometerAvailable;
                if (this.compassAvailable) {
                    this.compassListener = new SensorListener();
                    this.compassAvailable = this.manager.registerListener(this.compassListener, sensor2, this.config.sensorDelay);
                }
            } else {
                this.compassAvailable = false;
            }
        }
        Gdx.app.log("AndroidInput", "sensor listener setup");
    }

    /* access modifiers changed from: package-private */
    public void unregisterSensorListeners() {
        SensorManager sensorManager = this.manager;
        if (sensorManager != null) {
            SensorEventListener sensorEventListener = this.accelerometerListener;
            if (sensorEventListener != null) {
                sensorManager.unregisterListener(sensorEventListener);
                this.accelerometerListener = null;
            }
            SensorEventListener sensorEventListener2 = this.gyroscopeListener;
            if (sensorEventListener2 != null) {
                this.manager.unregisterListener(sensorEventListener2);
                this.gyroscopeListener = null;
            }
            SensorEventListener sensorEventListener3 = this.rotationVectorListener;
            if (sensorEventListener3 != null) {
                this.manager.unregisterListener(sensorEventListener3);
                this.rotationVectorListener = null;
            }
            SensorEventListener sensorEventListener4 = this.compassListener;
            if (sensorEventListener4 != null) {
                this.manager.unregisterListener(sensorEventListener4);
                this.compassListener = null;
            }
            this.manager = null;
        }
        Gdx.app.log("AndroidInput", "sensor listener tear down");
    }

    public InputProcessor getInputProcessor() {
        return this.processor;
    }

    public boolean isPeripheralAvailable(Input.Peripheral peripheral) {
        if (peripheral == Input.Peripheral.Accelerometer) {
            return this.accelerometerAvailable;
        }
        if (peripheral == Input.Peripheral.Gyroscope) {
            return this.gyroscopeAvailable;
        }
        if (peripheral == Input.Peripheral.Compass) {
            return this.compassAvailable;
        }
        if (peripheral == Input.Peripheral.HardwareKeyboard) {
            return this.keyboardAvailable;
        }
        if (peripheral == Input.Peripheral.OnscreenKeyboard) {
            return true;
        }
        if (peripheral == Input.Peripheral.Vibrator) {
            if (Build.VERSION.SDK_INT >= 11) {
                Vibrator vibrator2 = this.vibrator;
                if (vibrator2 == null || !vibrator2.hasVibrator()) {
                    return false;
                }
                return true;
            } else if (this.vibrator != null) {
                return true;
            } else {
                return false;
            }
        } else if (peripheral == Input.Peripheral.MultitouchScreen) {
            return this.hasMultitouch;
        } else {
            if (peripheral == Input.Peripheral.RotationVector) {
                return this.rotationVectorAvailable;
            }
            if (peripheral == Input.Peripheral.Pressure) {
                return true;
            }
            return false;
        }
    }

    public int getFreePointerIndex() {
        int len = this.realId.length;
        for (int i = 0; i < len; i++) {
            if (this.realId[i] == -1) {
                return i;
            }
        }
        this.realId = resize(this.realId);
        this.touchX = resize(this.touchX);
        this.touchY = resize(this.touchY);
        this.deltaX = resize(this.deltaX);
        this.deltaY = resize(this.deltaY);
        this.touched = resize(this.touched);
        this.button = resize(this.button);
        return len;
    }

    private int[] resize(int[] orig) {
        int[] tmp = new int[(orig.length + 2)];
        System.arraycopy(orig, 0, tmp, 0, orig.length);
        return tmp;
    }

    private boolean[] resize(boolean[] orig) {
        boolean[] tmp = new boolean[(orig.length + 2)];
        System.arraycopy(orig, 0, tmp, 0, orig.length);
        return tmp;
    }

    public int lookUpPointerIndex(int pointerId) {
        int len = this.realId.length;
        for (int i = 0; i < len; i++) {
            if (this.realId[i] == pointerId) {
                return i;
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int i2 = 0; i2 < len; i2++) {
            sb.append(i2 + ":" + this.realId[i2] + " ");
        }
        Application application = Gdx.app;
        application.log("AndroidInput", "Pointer ID lookup failed: " + pointerId + ", " + sb.toString());
        return -1;
    }

    public int getRotation() {
        int orientation2;
        Context context2 = this.context;
        if (context2 instanceof Activity) {
            orientation2 = ((Activity) context2).getWindowManager().getDefaultDisplay().getRotation();
        } else {
            orientation2 = ((WindowManager) context2.getSystemService("window")).getDefaultDisplay().getRotation();
        }
        if (orientation2 == 0) {
            return 0;
        }
        if (orientation2 == 1) {
            return 90;
        }
        if (orientation2 == 2) {
            return 180;
        }
        if (orientation2 != 3) {
            return 0;
        }
        return 270;
    }

    public Input.Orientation getNativeOrientation() {
        return this.nativeOrientation;
    }

    public void setCursorCatched(boolean catched) {
    }

    public boolean isCursorCatched() {
        return false;
    }

    public int getDeltaX() {
        return this.deltaX[0];
    }

    public int getDeltaX(int pointer) {
        return this.deltaX[pointer];
    }

    public int getDeltaY() {
        return this.deltaY[0];
    }

    public int getDeltaY(int pointer) {
        return this.deltaY[pointer];
    }

    public void setCursorPosition(int x, int y) {
    }

    public long getCurrentEventTime() {
        return this.currentEventTimeStamp;
    }

    public void addKeyListener(View.OnKeyListener listener) {
        this.keyListeners.add(listener);
    }

    public void onPause() {
        unregisterSensorListeners();
        Arrays.fill(this.realId, -1);
        Arrays.fill(this.touched, false);
    }

    public void onResume() {
        registerSensorListeners();
    }

    private class SensorListener implements SensorEventListener {
        public SensorListener() {
        }

        public void onAccuracyChanged(Sensor arg0, int arg1) {
        }

        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == 1) {
                if (AndroidInput.this.nativeOrientation == Input.Orientation.Portrait) {
                    System.arraycopy(event.values, 0, AndroidInput.this.accelerometerValues, 0, AndroidInput.this.accelerometerValues.length);
                } else {
                    AndroidInput.this.accelerometerValues[0] = event.values[1];
                    AndroidInput.this.accelerometerValues[1] = -event.values[0];
                    AndroidInput.this.accelerometerValues[2] = event.values[2];
                }
            }
            if (event.sensor.getType() == 2) {
                System.arraycopy(event.values, 0, AndroidInput.this.magneticFieldValues, 0, AndroidInput.this.magneticFieldValues.length);
            }
            if (event.sensor.getType() == 4) {
                if (AndroidInput.this.nativeOrientation == Input.Orientation.Portrait) {
                    System.arraycopy(event.values, 0, AndroidInput.this.gyroscopeValues, 0, AndroidInput.this.gyroscopeValues.length);
                } else {
                    AndroidInput.this.gyroscopeValues[0] = event.values[1];
                    AndroidInput.this.gyroscopeValues[1] = -event.values[0];
                    AndroidInput.this.gyroscopeValues[2] = event.values[2];
                }
            }
            if (event.sensor.getType() != 11) {
                return;
            }
            if (AndroidInput.this.nativeOrientation == Input.Orientation.Portrait) {
                System.arraycopy(event.values, 0, AndroidInput.this.rotationVectorValues, 0, AndroidInput.this.rotationVectorValues.length);
                return;
            }
            AndroidInput.this.rotationVectorValues[0] = event.values[1];
            AndroidInput.this.rotationVectorValues[1] = -event.values[0];
            AndroidInput.this.rotationVectorValues[2] = event.values[2];
        }
    }
}
