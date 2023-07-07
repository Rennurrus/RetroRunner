package com.badlogic.gdx.backends.android;

import android.content.Context;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidInput;

public class AndroidMultiTouchHandler implements AndroidTouchHandler {
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:144:0x02d3, code lost:
        com.badlogic.gdx.Gdx.app.getGraphics().requestRendering();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:145:0x02dc, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onTouch(android.view.MotionEvent r25, com.badlogic.gdx.backends.android.AndroidInput r26) {
        /*
            r24 = this;
            r1 = r25
            r11 = r26
            int r0 = r25.getAction()
            r12 = r0 & 255(0xff, float:3.57E-43)
            int r0 = r25.getAction()
            r2 = 65280(0xff00, float:9.1477E-41)
            r0 = r0 & r2
            int r13 = r0 >> 8
            int r14 = r1.getPointerId(r13)
            r2 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            long r15 = java.lang.System.nanoTime()
            monitor-enter(r26)
            r0 = 0
            r9 = 20
            r10 = -1
            r17 = 0
            switch(r12) {
                case 0: goto L_0x020f;
                case 1: goto L_0x0177;
                case 2: goto L_0x0058;
                case 3: goto L_0x002c;
                case 4: goto L_0x0177;
                case 5: goto L_0x020f;
                case 6: goto L_0x0177;
                default: goto L_0x002a;
            }
        L_0x002a:
            goto L_0x02d2
        L_0x002c:
            r6 = r17
        L_0x002e:
            int[] r7 = r11.realId     // Catch:{ all -> 0x02dd }
            int r7 = r7.length     // Catch:{ all -> 0x02dd }
            if (r6 >= r7) goto L_0x0056
            int[] r7 = r11.realId     // Catch:{ all -> 0x02dd }
            r7[r6] = r10     // Catch:{ all -> 0x02dd }
            int[] r7 = r11.touchX     // Catch:{ all -> 0x02dd }
            r7[r6] = r17     // Catch:{ all -> 0x02dd }
            int[] r7 = r11.touchY     // Catch:{ all -> 0x02dd }
            r7[r6] = r17     // Catch:{ all -> 0x02dd }
            int[] r7 = r11.deltaX     // Catch:{ all -> 0x02dd }
            r7[r6] = r17     // Catch:{ all -> 0x02dd }
            int[] r7 = r11.deltaY     // Catch:{ all -> 0x02dd }
            r7[r6] = r17     // Catch:{ all -> 0x02dd }
            boolean[] r7 = r11.touched     // Catch:{ all -> 0x02dd }
            r7[r6] = r17     // Catch:{ all -> 0x02dd }
            int[] r7 = r11.button     // Catch:{ all -> 0x02dd }
            r7[r6] = r17     // Catch:{ all -> 0x02dd }
            float[] r7 = r11.pressure     // Catch:{ all -> 0x02dd }
            r7[r6] = r0     // Catch:{ all -> 0x02dd }
            int r6 = r6 + 1
            goto L_0x002e
        L_0x0056:
            goto L_0x02d2
        L_0x0058:
            int r0 = r25.getPointerCount()     // Catch:{ all -> 0x02dd }
            r6 = 0
            r23 = r3
            r3 = r2
            r2 = r13
            r13 = r6
            r6 = r5
            r5 = r4
            r4 = r23
        L_0x0066:
            if (r13 >= r0) goto L_0x016e
            r8 = r13
            int r2 = r1.getPointerId(r8)     // Catch:{ all -> 0x0165 }
            r14 = r2
            float r2 = r1.getX(r8)     // Catch:{ all -> 0x0165 }
            int r7 = (int) r2
            float r2 = r1.getY(r8)     // Catch:{ all -> 0x0159 }
            int r4 = (int) r2
            int r2 = r11.lookUpPointerIndex(r14)     // Catch:{ all -> 0x014a }
            r5 = r2
            if (r5 != r10) goto L_0x008b
            r22 = r0
            r18 = r4
            r17 = r5
            r20 = r7
            r7 = r8
            r0 = -1
            goto L_0x010e
        L_0x008b:
            if (r5 < r9) goto L_0x0094
            r3 = r4
            r4 = r5
            r5 = r6
            r2 = r7
            r13 = r8
            goto L_0x02d2
        L_0x0094:
            int[] r2 = r11.button     // Catch:{ all -> 0x0138 }
            r2 = r2[r5]     // Catch:{ all -> 0x0138 }
            r6 = r2
            if (r6 == r10) goto L_0x00c9
            r17 = 2
            r2 = r24
            r3 = r26
            r18 = r4
            r4 = r17
            r17 = r5
            r5 = r7
            r19 = r6
            r6 = r18
            r20 = r7
            r7 = r17
            r21 = r8
            r8 = r19
            r22 = r0
            r0 = -1
            r9 = r15
            r2.postTouchEvent(r3, r4, r5, r6, r7, r8, r9)     // Catch:{ all -> 0x00bc }
            goto L_0x00e6
        L_0x00bc:
            r0 = move-exception
            r4 = r17
            r3 = r18
            r5 = r19
            r2 = r20
            r13 = r21
            goto L_0x02de
        L_0x00c9:
            r22 = r0
            r18 = r4
            r17 = r5
            r19 = r6
            r20 = r7
            r21 = r8
            r0 = -1
            r4 = 4
            r8 = 0
            r2 = r24
            r3 = r26
            r5 = r20
            r6 = r18
            r7 = r17
            r9 = r15
            r2.postTouchEvent(r3, r4, r5, r6, r7, r8, r9)     // Catch:{ all -> 0x012a }
        L_0x00e6:
            int[] r2 = r11.deltaX     // Catch:{ all -> 0x012a }
            int[] r3 = r11.touchX     // Catch:{ all -> 0x012a }
            r3 = r3[r17]     // Catch:{ all -> 0x012a }
            int r7 = r20 - r3
            r2[r17] = r7     // Catch:{ all -> 0x012a }
            int[] r2 = r11.deltaY     // Catch:{ all -> 0x012a }
            int[] r3 = r11.touchY     // Catch:{ all -> 0x012a }
            r3 = r3[r17]     // Catch:{ all -> 0x012a }
            int r4 = r18 - r3
            r2[r17] = r4     // Catch:{ all -> 0x012a }
            int[] r2 = r11.touchX     // Catch:{ all -> 0x012a }
            r2[r17] = r20     // Catch:{ all -> 0x012a }
            int[] r2 = r11.touchY     // Catch:{ all -> 0x012a }
            r2[r17] = r18     // Catch:{ all -> 0x012a }
            float[] r2 = r11.pressure     // Catch:{ all -> 0x012a }
            r7 = r21
            float r3 = r1.getPressure(r7)     // Catch:{ all -> 0x011e }
            r2[r17] = r3     // Catch:{ all -> 0x011e }
            r6 = r19
        L_0x010e:
            int r13 = r13 + 1
            r2 = r7
            r5 = r17
            r4 = r18
            r3 = r20
            r0 = r22
            r9 = 20
            r10 = -1
            goto L_0x0066
        L_0x011e:
            r0 = move-exception
            r13 = r7
            r4 = r17
            r3 = r18
            r5 = r19
            r2 = r20
            goto L_0x02de
        L_0x012a:
            r0 = move-exception
            r7 = r21
            r13 = r7
            r4 = r17
            r3 = r18
            r5 = r19
            r2 = r20
            goto L_0x02de
        L_0x0138:
            r0 = move-exception
            r18 = r4
            r17 = r5
            r20 = r7
            r7 = r8
            r5 = r6
            r13 = r7
            r4 = r17
            r3 = r18
            r2 = r20
            goto L_0x02de
        L_0x014a:
            r0 = move-exception
            r18 = r4
            r20 = r7
            r7 = r8
            r4 = r5
            r5 = r6
            r13 = r7
            r3 = r18
            r2 = r20
            goto L_0x02de
        L_0x0159:
            r0 = move-exception
            r20 = r7
            r7 = r8
            r3 = r4
            r4 = r5
            r5 = r6
            r13 = r7
            r2 = r20
            goto L_0x02de
        L_0x0165:
            r0 = move-exception
            r7 = r8
            r2 = r3
            r3 = r4
            r4 = r5
            r5 = r6
            r13 = r7
            goto L_0x02de
        L_0x016e:
            r22 = r0
            r13 = r2
            r2 = r3
            r3 = r4
            r4 = r5
            r5 = r6
            goto L_0x02d2
        L_0x0177:
            r0 = -1
            r18 = 0
            int r6 = r11.lookUpPointerIndex(r14)     // Catch:{ all -> 0x02dd }
            r9 = r6
            if (r9 != r0) goto L_0x0182
            goto L_0x0186
        L_0x0182:
            r4 = 20
            if (r9 < r4) goto L_0x0189
        L_0x0186:
            r4 = r9
            goto L_0x02d2
        L_0x0189:
            int[] r4 = r11.realId     // Catch:{ all -> 0x0208 }
            r4[r9] = r0     // Catch:{ all -> 0x0208 }
            float r4 = r1.getX(r13)     // Catch:{ all -> 0x0208 }
            int r10 = (int) r4
            float r2 = r1.getY(r13)     // Catch:{ all -> 0x01fd }
            int r8 = (int) r2
            int[] r2 = r11.button     // Catch:{ all -> 0x01ee }
            r2 = r2[r9]     // Catch:{ all -> 0x01ee }
            r7 = r2
            if (r7 == r0) goto L_0x01b5
            r4 = 1
            r2 = r24
            r3 = r26
            r5 = r10
            r6 = r8
            r19 = r7
            r7 = r9
            r20 = r8
            r8 = r19
            r21 = r9
            r22 = r10
            r9 = r15
            r2.postTouchEvent(r3, r4, r5, r6, r7, r8, r9)     // Catch:{ all -> 0x01e3 }
            goto L_0x01bd
        L_0x01b5:
            r19 = r7
            r20 = r8
            r21 = r9
            r22 = r10
        L_0x01bd:
            int[] r0 = r11.touchX     // Catch:{ all -> 0x01e3 }
            r0[r21] = r22     // Catch:{ all -> 0x01e3 }
            int[] r0 = r11.touchY     // Catch:{ all -> 0x01e3 }
            r0[r21] = r20     // Catch:{ all -> 0x01e3 }
            int[] r0 = r11.deltaX     // Catch:{ all -> 0x01e3 }
            r0[r21] = r17     // Catch:{ all -> 0x01e3 }
            int[] r0 = r11.deltaY     // Catch:{ all -> 0x01e3 }
            r0[r21] = r17     // Catch:{ all -> 0x01e3 }
            boolean[] r0 = r11.touched     // Catch:{ all -> 0x01e3 }
            r0[r21] = r17     // Catch:{ all -> 0x01e3 }
            int[] r0 = r11.button     // Catch:{ all -> 0x01e3 }
            r0[r21] = r17     // Catch:{ all -> 0x01e3 }
            float[] r0 = r11.pressure     // Catch:{ all -> 0x01e3 }
            r0[r21] = r18     // Catch:{ all -> 0x01e3 }
            r5 = r19
            r3 = r20
            r4 = r21
            r2 = r22
            goto L_0x02d2
        L_0x01e3:
            r0 = move-exception
            r5 = r19
            r3 = r20
            r4 = r21
            r2 = r22
            goto L_0x02de
        L_0x01ee:
            r0 = move-exception
            r20 = r8
            r21 = r9
            r22 = r10
            r3 = r20
            r4 = r21
            r2 = r22
            goto L_0x02de
        L_0x01fd:
            r0 = move-exception
            r21 = r9
            r22 = r10
            r4 = r21
            r2 = r22
            goto L_0x02de
        L_0x0208:
            r0 = move-exception
            r21 = r9
            r4 = r21
            goto L_0x02de
        L_0x020f:
            r0 = -1
            int r6 = r26.getFreePointerIndex()     // Catch:{ all -> 0x02dd }
            r9 = r6
            r4 = 20
            if (r9 < r4) goto L_0x021c
            r4 = r9
            goto L_0x02d2
        L_0x021c:
            int[] r4 = r11.realId     // Catch:{ all -> 0x02cc }
            r4[r9] = r14     // Catch:{ all -> 0x02cc }
            float r4 = r1.getX(r13)     // Catch:{ all -> 0x02cc }
            int r10 = (int) r4
            float r2 = r1.getY(r13)     // Catch:{ all -> 0x02c2 }
            int r8 = (int) r2
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x02b4 }
            r3 = 14
            if (r2 < r3) goto L_0x0246
            int r2 = r25.getButtonState()     // Catch:{ all -> 0x023e }
            r7 = r24
            int r2 = r7.toGdxButton(r2)     // Catch:{ all -> 0x023c }
            r6 = r2
            goto L_0x0249
        L_0x023c:
            r0 = move-exception
            goto L_0x0241
        L_0x023e:
            r0 = move-exception
            r7 = r24
        L_0x0241:
            r3 = r8
            r4 = r9
            r2 = r10
            goto L_0x02de
        L_0x0246:
            r7 = r24
            r6 = r5
        L_0x0249:
            if (r6 == r0) goto L_0x026d
            r4 = 0
            r2 = r24
            r3 = r26
            r5 = r10
            r18 = r6
            r6 = r8
            r7 = r9
            r19 = r8
            r8 = r18
            r20 = r9
            r21 = r10
            r9 = r15
            r2.postTouchEvent(r3, r4, r5, r6, r7, r8, r9)     // Catch:{ all -> 0x0262 }
            goto L_0x0275
        L_0x0262:
            r0 = move-exception
            r5 = r18
            r3 = r19
            r4 = r20
            r2 = r21
            goto L_0x02de
        L_0x026d:
            r18 = r6
            r19 = r8
            r20 = r9
            r21 = r10
        L_0x0275:
            int[] r2 = r11.touchX     // Catch:{ all -> 0x02aa }
            r2[r20] = r21     // Catch:{ all -> 0x02aa }
            int[] r2 = r11.touchY     // Catch:{ all -> 0x02aa }
            r2[r20] = r19     // Catch:{ all -> 0x02aa }
            int[] r2 = r11.deltaX     // Catch:{ all -> 0x02aa }
            r2[r20] = r17     // Catch:{ all -> 0x02aa }
            int[] r2 = r11.deltaY     // Catch:{ all -> 0x02aa }
            r2[r20] = r17     // Catch:{ all -> 0x02aa }
            boolean[] r2 = r11.touched     // Catch:{ all -> 0x02aa }
            r5 = r18
            if (r5 == r0) goto L_0x028d
            r17 = 1
        L_0x028d:
            r2[r20] = r17     // Catch:{ all -> 0x02a2 }
            int[] r0 = r11.button     // Catch:{ all -> 0x02a2 }
            r0[r20] = r5     // Catch:{ all -> 0x02a2 }
            float[] r0 = r11.pressure     // Catch:{ all -> 0x02a2 }
            float r2 = r1.getPressure(r13)     // Catch:{ all -> 0x02a2 }
            r0[r20] = r2     // Catch:{ all -> 0x02a2 }
            r3 = r19
            r4 = r20
            r2 = r21
            goto L_0x02d2
        L_0x02a2:
            r0 = move-exception
            r3 = r19
            r4 = r20
            r2 = r21
            goto L_0x02de
        L_0x02aa:
            r0 = move-exception
            r5 = r18
            r3 = r19
            r4 = r20
            r2 = r21
            goto L_0x02de
        L_0x02b4:
            r0 = move-exception
            r19 = r8
            r20 = r9
            r21 = r10
            r3 = r19
            r4 = r20
            r2 = r21
            goto L_0x02de
        L_0x02c2:
            r0 = move-exception
            r20 = r9
            r21 = r10
            r4 = r20
            r2 = r21
            goto L_0x02de
        L_0x02cc:
            r0 = move-exception
            r20 = r9
            r4 = r20
            goto L_0x02de
        L_0x02d2:
            monitor-exit(r26)     // Catch:{ all -> 0x02dd }
            com.badlogic.gdx.Application r0 = com.badlogic.gdx.Gdx.app
            com.badlogic.gdx.Graphics r0 = r0.getGraphics()
            r0.requestRendering()
            return
        L_0x02dd:
            r0 = move-exception
        L_0x02de:
            monitor-exit(r26)     // Catch:{ all -> 0x02dd }
            goto L_0x02e1
        L_0x02e0:
            throw r0
        L_0x02e1:
            goto L_0x02e0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.backends.android.AndroidMultiTouchHandler.onTouch(android.view.MotionEvent, com.badlogic.gdx.backends.android.AndroidInput):void");
    }

    private void logAction(int action, int pointer) {
        String actionStr;
        if (action == 0) {
            actionStr = "DOWN";
        } else if (action == 5) {
            actionStr = "POINTER DOWN";
        } else if (action == 1) {
            actionStr = "UP";
        } else if (action == 6) {
            actionStr = "POINTER UP";
        } else if (action == 4) {
            actionStr = "OUTSIDE";
        } else if (action == 3) {
            actionStr = "CANCEL";
        } else if (action == 2) {
            actionStr = "MOVE";
        } else {
            actionStr = "UNKNOWN (" + action + ")";
        }
        Gdx.app.log("AndroidMultiTouchHandler", "action " + actionStr + ", Android pointer id: " + pointer);
    }

    private int toGdxButton(int button) {
        if (button == 0 || button == 1) {
            return 0;
        }
        if (button == 2) {
            return 1;
        }
        if (button == 4) {
            return 2;
        }
        if (button == 8) {
            return 3;
        }
        if (button == 16) {
            return 4;
        }
        return -1;
    }

    private void postTouchEvent(AndroidInput input, int type, int x, int y, int pointer, int button, long timeStamp) {
        AndroidInput.TouchEvent event = input.usedTouchEvents.obtain();
        event.timeStamp = timeStamp;
        event.pointer = pointer;
        event.x = x;
        event.y = y;
        event.type = type;
        event.button = button;
        input.touchEvents.add(event);
    }

    public boolean supportsMultitouch(Context activity) {
        return activity.getPackageManager().hasSystemFeature("android.hardware.touchscreen.multitouch");
    }
}
