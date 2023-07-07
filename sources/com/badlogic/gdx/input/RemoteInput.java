package com.badlogic.gdx.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class RemoteInput implements Runnable, Input {
    public static int DEFAULT_PORT = 8190;
    private static final int MAX_TOUCHES = 20;
    private float[] accel;
    private float[] compass;
    private boolean connected;
    int[] deltaX;
    int[] deltaY;
    private float[] gyrate;
    public final String[] ips;
    boolean[] isTouched;
    boolean[] justPressedKeys;
    boolean justTouched;
    int keyCount;
    boolean keyJustPressed;
    boolean[] keys;
    private RemoteInputListener listener;
    private boolean multiTouch;
    private final int port;
    InputProcessor processor;
    private float remoteHeight;
    private float remoteWidth;
    private ServerSocket serverSocket;
    int[] touchX;
    int[] touchY;

    public interface RemoteInputListener {
        void onConnected();

        void onDisconnected();
    }

    class KeyEvent {
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

    class TouchEvent {
        static final int TOUCH_DOWN = 0;
        static final int TOUCH_DRAGGED = 2;
        static final int TOUCH_UP = 1;
        int pointer;
        long timeStamp;
        int type;
        int x;
        int y;

        TouchEvent() {
        }
    }

    class EventTrigger implements Runnable {
        KeyEvent keyEvent;
        TouchEvent touchEvent;

        public EventTrigger(TouchEvent touchEvent2, KeyEvent keyEvent2) {
            this.touchEvent = touchEvent2;
            this.keyEvent = keyEvent2;
        }

        public void run() {
            RemoteInput remoteInput = RemoteInput.this;
            remoteInput.justTouched = false;
            if (remoteInput.keyJustPressed) {
                RemoteInput.this.keyJustPressed = false;
                for (int i = 0; i < RemoteInput.this.justPressedKeys.length; i++) {
                    RemoteInput.this.justPressedKeys[i] = false;
                }
            }
            if (RemoteInput.this.processor != null) {
                TouchEvent touchEvent2 = this.touchEvent;
                if (touchEvent2 != null) {
                    int i2 = touchEvent2.type;
                    if (i2 == 0) {
                        RemoteInput.this.deltaX[this.touchEvent.pointer] = 0;
                        RemoteInput.this.deltaY[this.touchEvent.pointer] = 0;
                        RemoteInput.this.processor.touchDown(this.touchEvent.x, this.touchEvent.y, this.touchEvent.pointer, 0);
                        RemoteInput.this.isTouched[this.touchEvent.pointer] = true;
                        RemoteInput.this.justTouched = true;
                    } else if (i2 == 1) {
                        RemoteInput.this.deltaX[this.touchEvent.pointer] = 0;
                        RemoteInput.this.deltaY[this.touchEvent.pointer] = 0;
                        RemoteInput.this.processor.touchUp(this.touchEvent.x, this.touchEvent.y, this.touchEvent.pointer, 0);
                        RemoteInput.this.isTouched[this.touchEvent.pointer] = false;
                    } else if (i2 == 2) {
                        RemoteInput.this.deltaX[this.touchEvent.pointer] = this.touchEvent.x - RemoteInput.this.touchX[this.touchEvent.pointer];
                        RemoteInput.this.deltaY[this.touchEvent.pointer] = this.touchEvent.y - RemoteInput.this.touchY[this.touchEvent.pointer];
                        RemoteInput.this.processor.touchDragged(this.touchEvent.x, this.touchEvent.y, this.touchEvent.pointer);
                    }
                    RemoteInput.this.touchX[this.touchEvent.pointer] = this.touchEvent.x;
                    RemoteInput.this.touchY[this.touchEvent.pointer] = this.touchEvent.y;
                }
                KeyEvent keyEvent2 = this.keyEvent;
                if (keyEvent2 != null) {
                    int i3 = keyEvent2.type;
                    if (i3 == 0) {
                        RemoteInput.this.processor.keyDown(this.keyEvent.keyCode);
                        if (!RemoteInput.this.keys[this.keyEvent.keyCode]) {
                            RemoteInput.this.keyCount++;
                            RemoteInput.this.keys[this.keyEvent.keyCode] = true;
                        }
                        RemoteInput remoteInput2 = RemoteInput.this;
                        remoteInput2.keyJustPressed = true;
                        remoteInput2.justPressedKeys[this.keyEvent.keyCode] = true;
                    } else if (i3 == 1) {
                        RemoteInput.this.processor.keyUp(this.keyEvent.keyCode);
                        if (RemoteInput.this.keys[this.keyEvent.keyCode]) {
                            RemoteInput.this.keyCount--;
                            RemoteInput.this.keys[this.keyEvent.keyCode] = false;
                        }
                    } else if (i3 == 2) {
                        RemoteInput.this.processor.keyTyped(this.keyEvent.keyChar);
                    }
                }
            } else {
                TouchEvent touchEvent3 = this.touchEvent;
                if (touchEvent3 != null) {
                    int i4 = touchEvent3.type;
                    if (i4 == 0) {
                        RemoteInput.this.deltaX[this.touchEvent.pointer] = 0;
                        RemoteInput.this.deltaY[this.touchEvent.pointer] = 0;
                        RemoteInput.this.isTouched[this.touchEvent.pointer] = true;
                        RemoteInput.this.justTouched = true;
                    } else if (i4 == 1) {
                        RemoteInput.this.deltaX[this.touchEvent.pointer] = 0;
                        RemoteInput.this.deltaY[this.touchEvent.pointer] = 0;
                        RemoteInput.this.isTouched[this.touchEvent.pointer] = false;
                    } else if (i4 == 2) {
                        RemoteInput.this.deltaX[this.touchEvent.pointer] = this.touchEvent.x - RemoteInput.this.touchX[this.touchEvent.pointer];
                        RemoteInput.this.deltaY[this.touchEvent.pointer] = this.touchEvent.y - RemoteInput.this.touchY[this.touchEvent.pointer];
                    }
                    RemoteInput.this.touchX[this.touchEvent.pointer] = this.touchEvent.x;
                    RemoteInput.this.touchY[this.touchEvent.pointer] = this.touchEvent.y;
                }
                KeyEvent keyEvent3 = this.keyEvent;
                if (keyEvent3 != null) {
                    if (keyEvent3.type == 0) {
                        if (!RemoteInput.this.keys[this.keyEvent.keyCode]) {
                            RemoteInput.this.keyCount++;
                            RemoteInput.this.keys[this.keyEvent.keyCode] = true;
                        }
                        RemoteInput remoteInput3 = RemoteInput.this;
                        remoteInput3.keyJustPressed = true;
                        remoteInput3.justPressedKeys[this.keyEvent.keyCode] = true;
                    }
                    if (this.keyEvent.type == 1 && RemoteInput.this.keys[this.keyEvent.keyCode]) {
                        RemoteInput.this.keyCount--;
                        RemoteInput.this.keys[this.keyEvent.keyCode] = false;
                    }
                }
            }
        }
    }

    public RemoteInput() {
        this(DEFAULT_PORT);
    }

    public RemoteInput(RemoteInputListener listener2) {
        this(DEFAULT_PORT, listener2);
    }

    public RemoteInput(int port2) {
        this(port2, (RemoteInputListener) null);
    }

    public RemoteInput(int port2, RemoteInputListener listener2) {
        this.accel = new float[3];
        this.gyrate = new float[3];
        this.compass = new float[3];
        this.multiTouch = false;
        this.remoteWidth = 0.0f;
        this.remoteHeight = 0.0f;
        this.connected = false;
        this.keyCount = 0;
        this.keys = new boolean[256];
        this.keyJustPressed = false;
        this.justPressedKeys = new boolean[256];
        this.deltaX = new int[20];
        this.deltaY = new int[20];
        this.touchX = new int[20];
        this.touchY = new int[20];
        this.isTouched = new boolean[20];
        this.justTouched = false;
        this.processor = null;
        this.listener = listener2;
        try {
            this.port = port2;
            this.serverSocket = new ServerSocket(port2);
            Thread thread = new Thread(this);
            thread.setDaemon(true);
            thread.start();
            InetAddress[] allByName = InetAddress.getAllByName(InetAddress.getLocalHost().getHostName());
            this.ips = new String[allByName.length];
            for (int i = 0; i < allByName.length; i++) {
                this.ips[i] = allByName[i].getHostAddress();
            }
        } catch (Exception e) {
            throw new GdxRuntimeException("Couldn't open listening socket at port '" + port2 + "'", e);
        }
    }

    public void run() {
        while (true) {
            try {
                this.connected = false;
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (this.listener != null) {
            this.listener.onDisconnected();
        }
        PrintStream printStream = System.out;
        printStream.println("listening, port " + this.port);
        Socket socket = this.serverSocket.accept();
        socket.setTcpNoDelay(true);
        socket.setSoTimeout(3000);
        this.connected = true;
        if (this.listener != null) {
            this.listener.onConnected();
        }
        DataInputStream in = new DataInputStream(socket.getInputStream());
        this.multiTouch = in.readBoolean();
        while (true) {
            KeyEvent keyEvent = null;
            TouchEvent touchEvent = null;
            switch (in.readInt()) {
                case 0:
                    keyEvent = new KeyEvent();
                    keyEvent.keyCode = in.readInt();
                    keyEvent.type = 0;
                    break;
                case 1:
                    keyEvent = new KeyEvent();
                    keyEvent.keyCode = in.readInt();
                    keyEvent.type = 1;
                    break;
                case 2:
                    keyEvent = new KeyEvent();
                    keyEvent.keyChar = in.readChar();
                    keyEvent.type = 2;
                    break;
                case 3:
                    touchEvent = new TouchEvent();
                    touchEvent.x = (int) ((((float) in.readInt()) / this.remoteWidth) * ((float) Gdx.graphics.getWidth()));
                    touchEvent.y = (int) ((((float) in.readInt()) / this.remoteHeight) * ((float) Gdx.graphics.getHeight()));
                    touchEvent.pointer = in.readInt();
                    touchEvent.type = 0;
                    break;
                case 4:
                    touchEvent = new TouchEvent();
                    touchEvent.x = (int) ((((float) in.readInt()) / this.remoteWidth) * ((float) Gdx.graphics.getWidth()));
                    touchEvent.y = (int) ((((float) in.readInt()) / this.remoteHeight) * ((float) Gdx.graphics.getHeight()));
                    touchEvent.pointer = in.readInt();
                    touchEvent.type = 1;
                    break;
                case 5:
                    touchEvent = new TouchEvent();
                    touchEvent.x = (int) ((((float) in.readInt()) / this.remoteWidth) * ((float) Gdx.graphics.getWidth()));
                    touchEvent.y = (int) ((((float) in.readInt()) / this.remoteHeight) * ((float) Gdx.graphics.getHeight()));
                    touchEvent.pointer = in.readInt();
                    touchEvent.type = 2;
                    break;
                case 6:
                    this.accel[0] = in.readFloat();
                    this.accel[1] = in.readFloat();
                    this.accel[2] = in.readFloat();
                    break;
                case 7:
                    this.compass[0] = in.readFloat();
                    this.compass[1] = in.readFloat();
                    this.compass[2] = in.readFloat();
                    break;
                case 8:
                    this.remoteWidth = in.readFloat();
                    this.remoteHeight = in.readFloat();
                    break;
                case 9:
                    this.gyrate[0] = in.readFloat();
                    this.gyrate[1] = in.readFloat();
                    this.gyrate[2] = in.readFloat();
                    break;
            }
            Gdx.app.postRunnable(new EventTrigger(touchEvent, keyEvent));
        }
    }

    public boolean isConnected() {
        return this.connected;
    }

    public float getAccelerometerX() {
        return this.accel[0];
    }

    public float getAccelerometerY() {
        return this.accel[1];
    }

    public float getAccelerometerZ() {
        return this.accel[2];
    }

    public float getGyroscopeX() {
        return this.gyrate[0];
    }

    public float getGyroscopeY() {
        return this.gyrate[1];
    }

    public float getGyroscopeZ() {
        return this.gyrate[2];
    }

    public int getMaxPointers() {
        return 20;
    }

    public int getX() {
        return this.touchX[0];
    }

    public int getX(int pointer) {
        return this.touchX[pointer];
    }

    public int getY() {
        return this.touchY[0];
    }

    public int getY(int pointer) {
        return this.touchY[pointer];
    }

    public boolean isTouched() {
        return this.isTouched[0];
    }

    public boolean justTouched() {
        return this.justTouched;
    }

    public boolean isTouched(int pointer) {
        return this.isTouched[pointer];
    }

    public float getPressure() {
        return getPressure(0);
    }

    public float getPressure(int pointer) {
        return isTouched(pointer) ? 1.0f : 0.0f;
    }

    public boolean isButtonPressed(int button) {
        if (button != 0) {
            return false;
        }
        int i = 0;
        while (true) {
            boolean[] zArr = this.isTouched;
            if (i >= zArr.length) {
                return false;
            }
            if (zArr[i]) {
                return true;
            }
            i++;
        }
    }

    public boolean isButtonJustPressed(int button) {
        return button == 0 && this.justTouched;
    }

    public boolean isKeyPressed(int key) {
        if (key == -1) {
            if (this.keyCount > 0) {
                return true;
            }
            return false;
        } else if (key < 0 || key > 255) {
            return false;
        } else {
            return this.keys[key];
        }
    }

    public boolean isKeyJustPressed(int key) {
        if (key == -1) {
            return this.keyJustPressed;
        }
        if (key < 0 || key > 255) {
            return false;
        }
        return this.justPressedKeys[key];
    }

    public void getTextInput(Input.TextInputListener listener2, String title, String text, String hint) {
        Gdx.app.getInput().getTextInput(listener2, title, text, hint);
    }

    public void setOnscreenKeyboardVisible(boolean visible) {
    }

    public void vibrate(int milliseconds) {
    }

    public void vibrate(long[] pattern, int repeat) {
    }

    public void cancelVibrate() {
    }

    public float getAzimuth() {
        return this.compass[0];
    }

    public float getPitch() {
        return this.compass[1];
    }

    public float getRoll() {
        return this.compass[2];
    }

    public void setCatchBackKey(boolean catchBack) {
    }

    public boolean isCatchBackKey() {
        return false;
    }

    public void setCatchMenuKey(boolean catchMenu) {
    }

    public boolean isCatchMenuKey() {
        return false;
    }

    public void setCatchKey(int keycode, boolean catchKey) {
    }

    public boolean isCatchKey(int keycode) {
        return false;
    }

    public void setInputProcessor(InputProcessor processor2) {
        this.processor = processor2;
    }

    public InputProcessor getInputProcessor() {
        return this.processor;
    }

    public String[] getIPs() {
        return this.ips;
    }

    public boolean isPeripheralAvailable(Input.Peripheral peripheral) {
        if (peripheral == Input.Peripheral.Accelerometer || peripheral == Input.Peripheral.Compass) {
            return true;
        }
        if (peripheral == Input.Peripheral.MultitouchScreen) {
            return this.multiTouch;
        }
        return false;
    }

    public int getRotation() {
        return 0;
    }

    public Input.Orientation getNativeOrientation() {
        return Input.Orientation.Landscape;
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
        return 0;
    }

    public void getRotationMatrix(float[] matrix) {
    }
}
