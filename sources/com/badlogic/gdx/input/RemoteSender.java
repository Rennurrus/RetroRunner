package com.badlogic.gdx.input;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import java.io.DataOutputStream;
import java.net.Socket;

public class RemoteSender implements InputProcessor {
    public static final int ACCEL = 6;
    public static final int COMPASS = 7;
    public static final int GYRO = 9;
    public static final int KEY_DOWN = 0;
    public static final int KEY_TYPED = 2;
    public static final int KEY_UP = 1;
    public static final int SIZE = 8;
    public static final int TOUCH_DOWN = 3;
    public static final int TOUCH_DRAGGED = 5;
    public static final int TOUCH_UP = 4;
    private boolean connected = false;
    private DataOutputStream out;

    public RemoteSender(String ip, int port) {
        try {
            Socket socket = new Socket(ip, port);
            socket.setTcpNoDelay(true);
            socket.setSoTimeout(3000);
            this.out = new DataOutputStream(socket.getOutputStream());
            this.out.writeBoolean(Gdx.input.isPeripheralAvailable(Input.Peripheral.MultitouchScreen));
            this.connected = true;
            Gdx.input.setInputProcessor(this);
        } catch (Exception e) {
            Application application = Gdx.app;
            application.log("RemoteSender", "couldn't connect to " + ip + ":" + port);
        }
    }

    public void sendUpdate() {
        synchronized (this) {
            if (this.connected) {
                try {
                    this.out.writeInt(6);
                    this.out.writeFloat(Gdx.input.getAccelerometerX());
                    this.out.writeFloat(Gdx.input.getAccelerometerY());
                    this.out.writeFloat(Gdx.input.getAccelerometerZ());
                    this.out.writeInt(7);
                    this.out.writeFloat(Gdx.input.getAzimuth());
                    this.out.writeFloat(Gdx.input.getPitch());
                    this.out.writeFloat(Gdx.input.getRoll());
                    this.out.writeInt(8);
                    this.out.writeFloat((float) Gdx.graphics.getWidth());
                    this.out.writeFloat((float) Gdx.graphics.getHeight());
                    this.out.writeInt(9);
                    this.out.writeFloat(Gdx.input.getGyroscopeX());
                    this.out.writeFloat(Gdx.input.getGyroscopeY());
                    this.out.writeFloat(Gdx.input.getGyroscopeZ());
                } catch (Throwable th) {
                    this.out = null;
                    this.connected = false;
                }
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0015, code lost:
        monitor-enter(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:?, code lost:
        r2.connected = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:?, code lost:
        r2.out.writeInt(0);
        r2.out.writeInt(r3);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean keyDown(int r3) {
        /*
            r2 = this;
            monitor-enter(r2)
            boolean r0 = r2.connected     // Catch:{ all -> 0x001d }
            r1 = 0
            if (r0 != 0) goto L_0x0008
            monitor-exit(r2)     // Catch:{ all -> 0x001d }
            return r1
        L_0x0008:
            monitor-exit(r2)     // Catch:{ all -> 0x001d }
            java.io.DataOutputStream r0 = r2.out     // Catch:{ Throwable -> 0x0014 }
            r0.writeInt(r1)     // Catch:{ Throwable -> 0x0014 }
            java.io.DataOutputStream r0 = r2.out     // Catch:{ Throwable -> 0x0014 }
            r0.writeInt(r3)     // Catch:{ Throwable -> 0x0014 }
            goto L_0x0019
        L_0x0014:
            r0 = move-exception
            monitor-enter(r2)
            r2.connected = r1     // Catch:{ all -> 0x001a }
            monitor-exit(r2)     // Catch:{ all -> 0x001a }
        L_0x0019:
            return r1
        L_0x001a:
            r1 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x001a }
            throw r1
        L_0x001d:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x001d }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.input.RemoteSender.keyDown(int):boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0016, code lost:
        monitor-enter(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:?, code lost:
        r3.connected = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:?, code lost:
        r3.out.writeInt(1);
        r3.out.writeInt(r4);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean keyUp(int r4) {
        /*
            r3 = this;
            monitor-enter(r3)
            boolean r0 = r3.connected     // Catch:{ all -> 0x001e }
            r1 = 0
            if (r0 != 0) goto L_0x0008
            monitor-exit(r3)     // Catch:{ all -> 0x001e }
            return r1
        L_0x0008:
            monitor-exit(r3)     // Catch:{ all -> 0x001e }
            java.io.DataOutputStream r0 = r3.out     // Catch:{ Throwable -> 0x0015 }
            r2 = 1
            r0.writeInt(r2)     // Catch:{ Throwable -> 0x0015 }
            java.io.DataOutputStream r0 = r3.out     // Catch:{ Throwable -> 0x0015 }
            r0.writeInt(r4)     // Catch:{ Throwable -> 0x0015 }
            goto L_0x001a
        L_0x0015:
            r0 = move-exception
            monitor-enter(r3)
            r3.connected = r1     // Catch:{ all -> 0x001b }
            monitor-exit(r3)     // Catch:{ all -> 0x001b }
        L_0x001a:
            return r1
        L_0x001b:
            r1 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x001b }
            throw r1
        L_0x001e:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x001e }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.input.RemoteSender.keyUp(int):boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0016, code lost:
        monitor-enter(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:?, code lost:
        r3.connected = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:?, code lost:
        r3.out.writeInt(2);
        r3.out.writeChar(r4);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean keyTyped(char r4) {
        /*
            r3 = this;
            monitor-enter(r3)
            boolean r0 = r3.connected     // Catch:{ all -> 0x001e }
            r1 = 0
            if (r0 != 0) goto L_0x0008
            monitor-exit(r3)     // Catch:{ all -> 0x001e }
            return r1
        L_0x0008:
            monitor-exit(r3)     // Catch:{ all -> 0x001e }
            java.io.DataOutputStream r0 = r3.out     // Catch:{ Throwable -> 0x0015 }
            r2 = 2
            r0.writeInt(r2)     // Catch:{ Throwable -> 0x0015 }
            java.io.DataOutputStream r0 = r3.out     // Catch:{ Throwable -> 0x0015 }
            r0.writeChar(r4)     // Catch:{ Throwable -> 0x0015 }
            goto L_0x001a
        L_0x0015:
            r0 = move-exception
            monitor-enter(r3)
            r3.connected = r1     // Catch:{ all -> 0x001b }
            monitor-exit(r3)     // Catch:{ all -> 0x001b }
        L_0x001a:
            return r1
        L_0x001b:
            r1 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x001b }
            throw r1
        L_0x001e:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x001e }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.input.RemoteSender.keyTyped(char):boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0020, code lost:
        monitor-enter(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:?, code lost:
        r3.connected = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:?, code lost:
        r3.out.writeInt(3);
        r3.out.writeInt(r4);
        r3.out.writeInt(r5);
        r3.out.writeInt(r6);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean touchDown(int r4, int r5, int r6, int r7) {
        /*
            r3 = this;
            monitor-enter(r3)
            boolean r0 = r3.connected     // Catch:{ all -> 0x0028 }
            r1 = 0
            if (r0 != 0) goto L_0x0008
            monitor-exit(r3)     // Catch:{ all -> 0x0028 }
            return r1
        L_0x0008:
            monitor-exit(r3)     // Catch:{ all -> 0x0028 }
            java.io.DataOutputStream r0 = r3.out     // Catch:{ Throwable -> 0x001f }
            r2 = 3
            r0.writeInt(r2)     // Catch:{ Throwable -> 0x001f }
            java.io.DataOutputStream r0 = r3.out     // Catch:{ Throwable -> 0x001f }
            r0.writeInt(r4)     // Catch:{ Throwable -> 0x001f }
            java.io.DataOutputStream r0 = r3.out     // Catch:{ Throwable -> 0x001f }
            r0.writeInt(r5)     // Catch:{ Throwable -> 0x001f }
            java.io.DataOutputStream r0 = r3.out     // Catch:{ Throwable -> 0x001f }
            r0.writeInt(r6)     // Catch:{ Throwable -> 0x001f }
            goto L_0x0024
        L_0x001f:
            r0 = move-exception
            monitor-enter(r3)
            r3.connected = r1     // Catch:{ all -> 0x0025 }
            monitor-exit(r3)     // Catch:{ all -> 0x0025 }
        L_0x0024:
            return r1
        L_0x0025:
            r1 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x0025 }
            throw r1
        L_0x0028:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x0028 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.input.RemoteSender.touchDown(int, int, int, int):boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0020, code lost:
        monitor-enter(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:?, code lost:
        r3.connected = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:?, code lost:
        r3.out.writeInt(4);
        r3.out.writeInt(r4);
        r3.out.writeInt(r5);
        r3.out.writeInt(r6);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean touchUp(int r4, int r5, int r6, int r7) {
        /*
            r3 = this;
            monitor-enter(r3)
            boolean r0 = r3.connected     // Catch:{ all -> 0x0028 }
            r1 = 0
            if (r0 != 0) goto L_0x0008
            monitor-exit(r3)     // Catch:{ all -> 0x0028 }
            return r1
        L_0x0008:
            monitor-exit(r3)     // Catch:{ all -> 0x0028 }
            java.io.DataOutputStream r0 = r3.out     // Catch:{ Throwable -> 0x001f }
            r2 = 4
            r0.writeInt(r2)     // Catch:{ Throwable -> 0x001f }
            java.io.DataOutputStream r0 = r3.out     // Catch:{ Throwable -> 0x001f }
            r0.writeInt(r4)     // Catch:{ Throwable -> 0x001f }
            java.io.DataOutputStream r0 = r3.out     // Catch:{ Throwable -> 0x001f }
            r0.writeInt(r5)     // Catch:{ Throwable -> 0x001f }
            java.io.DataOutputStream r0 = r3.out     // Catch:{ Throwable -> 0x001f }
            r0.writeInt(r6)     // Catch:{ Throwable -> 0x001f }
            goto L_0x0024
        L_0x001f:
            r0 = move-exception
            monitor-enter(r3)
            r3.connected = r1     // Catch:{ all -> 0x0025 }
            monitor-exit(r3)     // Catch:{ all -> 0x0025 }
        L_0x0024:
            return r1
        L_0x0025:
            r1 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x0025 }
            throw r1
        L_0x0028:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x0028 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.input.RemoteSender.touchUp(int, int, int, int):boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0020, code lost:
        monitor-enter(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:?, code lost:
        r3.connected = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:?, code lost:
        r3.out.writeInt(5);
        r3.out.writeInt(r4);
        r3.out.writeInt(r5);
        r3.out.writeInt(r6);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean touchDragged(int r4, int r5, int r6) {
        /*
            r3 = this;
            monitor-enter(r3)
            boolean r0 = r3.connected     // Catch:{ all -> 0x0028 }
            r1 = 0
            if (r0 != 0) goto L_0x0008
            monitor-exit(r3)     // Catch:{ all -> 0x0028 }
            return r1
        L_0x0008:
            monitor-exit(r3)     // Catch:{ all -> 0x0028 }
            java.io.DataOutputStream r0 = r3.out     // Catch:{ Throwable -> 0x001f }
            r2 = 5
            r0.writeInt(r2)     // Catch:{ Throwable -> 0x001f }
            java.io.DataOutputStream r0 = r3.out     // Catch:{ Throwable -> 0x001f }
            r0.writeInt(r4)     // Catch:{ Throwable -> 0x001f }
            java.io.DataOutputStream r0 = r3.out     // Catch:{ Throwable -> 0x001f }
            r0.writeInt(r5)     // Catch:{ Throwable -> 0x001f }
            java.io.DataOutputStream r0 = r3.out     // Catch:{ Throwable -> 0x001f }
            r0.writeInt(r6)     // Catch:{ Throwable -> 0x001f }
            goto L_0x0024
        L_0x001f:
            r0 = move-exception
            monitor-enter(r3)
            r3.connected = r1     // Catch:{ all -> 0x0025 }
            monitor-exit(r3)     // Catch:{ all -> 0x0025 }
        L_0x0024:
            return r1
        L_0x0025:
            r1 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x0025 }
            throw r1
        L_0x0028:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x0028 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.input.RemoteSender.touchDragged(int, int, int):boolean");
    }

    public boolean mouseMoved(int x, int y) {
        return false;
    }

    public boolean scrolled(int amount) {
        return false;
    }

    public boolean isConnected() {
        boolean z;
        synchronized (this) {
            z = this.connected;
        }
        return z;
    }
}
