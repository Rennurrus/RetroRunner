package com.badlogic.gdx;

import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.TimeUtils;

public class InputEventQueue implements InputProcessor {
    private static final int KEY_DOWN = 0;
    private static final int KEY_TYPED = 2;
    private static final int KEY_UP = 1;
    private static final int MOUSE_MOVED = 6;
    private static final int SCROLLED = 7;
    private static final int SKIP = -1;
    private static final int TOUCH_DOWN = 3;
    private static final int TOUCH_DRAGGED = 5;
    private static final int TOUCH_UP = 4;
    private long currentEventTime;
    private final IntArray processingQueue = new IntArray();
    private InputProcessor processor;
    private final IntArray queue = new IntArray();

    public InputEventQueue() {
    }

    public InputEventQueue(InputProcessor processor2) {
        this.processor = processor2;
    }

    public void setProcessor(InputProcessor processor2) {
        this.processor = processor2;
    }

    public InputProcessor getProcessor() {
        return this.processor;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0024, code lost:
        if (r2 >= r3) goto L_0x00bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0026, code lost:
        r4 = r2 + 1;
        r2 = r0[r2];
        r5 = r4 + 1;
        r4 = r5 + 1;
        r12.currentEventTime = (((long) r0[r4]) << 32) | (((long) r0[r5]) & 4294967295L);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0040, code lost:
        switch(r2) {
            case -1: goto L_0x00b7;
            case 0: goto L_0x00ae;
            case 1: goto L_0x00a5;
            case 2: goto L_0x009b;
            case 3: goto L_0x0086;
            case 4: goto L_0x0071;
            case 5: goto L_0x0060;
            case 6: goto L_0x0053;
            case 7: goto L_0x0049;
            default: goto L_0x0043;
        };
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0048, code lost:
        throw new java.lang.RuntimeException();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0049, code lost:
        r1.scrolled(r0[r4]);
        r2 = r4 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0053, code lost:
        r5 = r4 + 1;
        r1.mouseMoved(r0[r4], r0[r5]);
        r2 = r5 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0060, code lost:
        r5 = r4 + 1;
        r6 = r5 + 1;
        r1.touchDragged(r0[r4], r0[r5], r0[r6]);
        r2 = r6 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0071, code lost:
        r5 = r4 + 1;
        r6 = r5 + 1;
        r7 = r6 + 1;
        r1.touchUp(r0[r4], r0[r5], r0[r6], r0[r7]);
        r2 = r7 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0086, code lost:
        r5 = r4 + 1;
        r6 = r5 + 1;
        r7 = r6 + 1;
        r1.touchDown(r0[r4], r0[r5], r0[r6], r0[r7]);
        r2 = r7 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x009b, code lost:
        r1.keyTyped((char) r0[r4]);
        r2 = r4 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x00a5, code lost:
        r1.keyUp(r0[r4]);
        r2 = r4 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x00ae, code lost:
        r1.keyDown(r0[r4]);
        r2 = r4 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x00b7, code lost:
        r2 = r4 + r0[r4];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x00bd, code lost:
        r12.processingQueue.clear();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x00c2, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0019, code lost:
        r0 = r12.processingQueue.items;
        r1 = r12.processor;
        r2 = 0;
        r3 = r12.processingQueue.size;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void drain() {
        /*
            r12 = this;
            monitor-enter(r12)
            com.badlogic.gdx.InputProcessor r0 = r12.processor     // Catch:{ all -> 0x00c3 }
            if (r0 != 0) goto L_0x000c
            com.badlogic.gdx.utils.IntArray r0 = r12.queue     // Catch:{ all -> 0x00c3 }
            r0.clear()     // Catch:{ all -> 0x00c3 }
            monitor-exit(r12)     // Catch:{ all -> 0x00c3 }
            return
        L_0x000c:
            com.badlogic.gdx.utils.IntArray r0 = r12.processingQueue     // Catch:{ all -> 0x00c3 }
            com.badlogic.gdx.utils.IntArray r1 = r12.queue     // Catch:{ all -> 0x00c3 }
            r0.addAll((com.badlogic.gdx.utils.IntArray) r1)     // Catch:{ all -> 0x00c3 }
            com.badlogic.gdx.utils.IntArray r0 = r12.queue     // Catch:{ all -> 0x00c3 }
            r0.clear()     // Catch:{ all -> 0x00c3 }
            monitor-exit(r12)     // Catch:{ all -> 0x00c3 }
            com.badlogic.gdx.utils.IntArray r0 = r12.processingQueue
            int[] r0 = r0.items
            com.badlogic.gdx.InputProcessor r1 = r12.processor
            r2 = 0
            com.badlogic.gdx.utils.IntArray r3 = r12.processingQueue
            int r3 = r3.size
        L_0x0024:
            if (r2 >= r3) goto L_0x00bd
            int r4 = r2 + 1
            r2 = r0[r2]
            int r5 = r4 + 1
            r4 = r0[r4]
            long r6 = (long) r4
            r4 = 32
            long r6 = r6 << r4
            int r4 = r5 + 1
            r5 = r0[r5]
            long r8 = (long) r5
            r10 = 4294967295(0xffffffff, double:2.1219957905E-314)
            long r8 = r8 & r10
            long r6 = r6 | r8
            r12.currentEventTime = r6
            switch(r2) {
                case -1: goto L_0x00b7;
                case 0: goto L_0x00ae;
                case 1: goto L_0x00a5;
                case 2: goto L_0x009b;
                case 3: goto L_0x0086;
                case 4: goto L_0x0071;
                case 5: goto L_0x0060;
                case 6: goto L_0x0053;
                case 7: goto L_0x0049;
                default: goto L_0x0043;
            }
        L_0x0043:
            java.lang.RuntimeException r5 = new java.lang.RuntimeException
            r5.<init>()
            throw r5
        L_0x0049:
            int r5 = r4 + 1
            r4 = r0[r4]
            r1.scrolled(r4)
            r2 = r5
            goto L_0x00bb
        L_0x0053:
            int r5 = r4 + 1
            r4 = r0[r4]
            int r6 = r5 + 1
            r5 = r0[r5]
            r1.mouseMoved(r4, r5)
            r2 = r6
            goto L_0x00bb
        L_0x0060:
            int r5 = r4 + 1
            r4 = r0[r4]
            int r6 = r5 + 1
            r5 = r0[r5]
            int r7 = r6 + 1
            r6 = r0[r6]
            r1.touchDragged(r4, r5, r6)
            r2 = r7
            goto L_0x00bb
        L_0x0071:
            int r5 = r4 + 1
            r4 = r0[r4]
            int r6 = r5 + 1
            r5 = r0[r5]
            int r7 = r6 + 1
            r6 = r0[r6]
            int r8 = r7 + 1
            r7 = r0[r7]
            r1.touchUp(r4, r5, r6, r7)
            r2 = r8
            goto L_0x00bb
        L_0x0086:
            int r5 = r4 + 1
            r4 = r0[r4]
            int r6 = r5 + 1
            r5 = r0[r5]
            int r7 = r6 + 1
            r6 = r0[r6]
            int r8 = r7 + 1
            r7 = r0[r7]
            r1.touchDown(r4, r5, r6, r7)
            r2 = r8
            goto L_0x00bb
        L_0x009b:
            int r5 = r4 + 1
            r4 = r0[r4]
            char r4 = (char) r4
            r1.keyTyped(r4)
            r2 = r5
            goto L_0x00bb
        L_0x00a5:
            int r5 = r4 + 1
            r4 = r0[r4]
            r1.keyUp(r4)
            r2 = r5
            goto L_0x00bb
        L_0x00ae:
            int r5 = r4 + 1
            r4 = r0[r4]
            r1.keyDown(r4)
            r2 = r5
            goto L_0x00bb
        L_0x00b7:
            r5 = r0[r4]
            int r4 = r4 + r5
            r2 = r4
        L_0x00bb:
            goto L_0x0024
        L_0x00bd:
            com.badlogic.gdx.utils.IntArray r2 = r12.processingQueue
            r2.clear()
            return
        L_0x00c3:
            r0 = move-exception
            monitor-exit(r12)     // Catch:{ all -> 0x00c3 }
            goto L_0x00c7
        L_0x00c6:
            throw r0
        L_0x00c7:
            goto L_0x00c6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.InputEventQueue.drain():void");
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    private synchronized int next(int nextType, int i) {
        int[] q = this.queue.items;
        int n = this.queue.size;
        while (i < n) {
            int type = q[i];
            if (type == nextType) {
                return i;
            }
            int i2 = i + 3;
            switch (type) {
                case -1:
                    i = i2 + q[i2];
                    break;
                case 0:
                    i = i2 + 1;
                    break;
                case 1:
                    i = i2 + 1;
                    break;
                case 2:
                    i = i2 + 1;
                    break;
                case 3:
                    i = i2 + 4;
                    break;
                case 4:
                    i = i2 + 4;
                    break;
                case 5:
                    i = i2 + 3;
                    break;
                case 6:
                    i = i2 + 2;
                    break;
                case 7:
                    i = i2 + 1;
                    break;
                default:
                    throw new RuntimeException();
            }
        }
        return -1;
    }

    private void queueTime() {
        long time = TimeUtils.nanoTime();
        this.queue.add((int) (time >> 32));
        this.queue.add((int) time);
    }

    public synchronized boolean keyDown(int keycode) {
        this.queue.add(0);
        queueTime();
        this.queue.add(keycode);
        return false;
    }

    public synchronized boolean keyUp(int keycode) {
        this.queue.add(1);
        queueTime();
        this.queue.add(keycode);
        return false;
    }

    public synchronized boolean keyTyped(char character) {
        this.queue.add(2);
        queueTime();
        this.queue.add(character);
        return false;
    }

    public synchronized boolean touchDown(int screenX, int screenY, int pointer, int button) {
        this.queue.add(3);
        queueTime();
        this.queue.add(screenX);
        this.queue.add(screenY);
        this.queue.add(pointer);
        this.queue.add(button);
        return false;
    }

    public synchronized boolean touchUp(int screenX, int screenY, int pointer, int button) {
        this.queue.add(4);
        queueTime();
        this.queue.add(screenX);
        this.queue.add(screenY);
        this.queue.add(pointer);
        this.queue.add(button);
        return false;
    }

    public synchronized boolean touchDragged(int screenX, int screenY, int pointer) {
        int i = next(5, 0);
        while (i >= 0) {
            if (this.queue.get(i + 5) == pointer) {
                this.queue.set(i, -1);
                this.queue.set(i + 3, 3);
            }
            i = next(5, i + 6);
        }
        this.queue.add(5);
        queueTime();
        this.queue.add(screenX);
        this.queue.add(screenY);
        this.queue.add(pointer);
        return false;
    }

    public synchronized boolean mouseMoved(int screenX, int screenY) {
        int i = next(6, 0);
        while (i >= 0) {
            this.queue.set(i, -1);
            this.queue.set(i + 3, 2);
            i = next(6, i + 5);
        }
        this.queue.add(6);
        queueTime();
        this.queue.add(screenX);
        this.queue.add(screenY);
        return false;
    }

    public synchronized boolean scrolled(int amount) {
        this.queue.add(7);
        queueTime();
        this.queue.add(amount);
        return false;
    }

    public long getCurrentEventTime() {
        return this.currentEventTime;
    }
}
