package com.badlogic.gdx.utils;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.LifecycleListener;

public class Timer {
    static TimerThread thread;
    static final Object threadLock = new Object();
    final Array<Task> tasks = new Array<>(false, 8);

    public static Timer instance() {
        Timer timer;
        synchronized (threadLock) {
            TimerThread thread2 = thread();
            if (thread2.instance == null) {
                thread2.instance = new Timer();
            }
            timer = thread2.instance;
        }
        return timer;
    }

    private static TimerThread thread() {
        TimerThread timerThread;
        synchronized (threadLock) {
            if (thread == null || thread.files != Gdx.files) {
                if (thread != null) {
                    thread.dispose();
                }
                thread = new TimerThread();
            }
            timerThread = thread;
        }
        return timerThread;
    }

    public Timer() {
        start();
    }

    public Task postTask(Task task) {
        return scheduleTask(task, 0.0f, 0.0f, 0);
    }

    public Task scheduleTask(Task task, float delaySeconds) {
        return scheduleTask(task, delaySeconds, 0.0f, 0);
    }

    public Task scheduleTask(Task task, float delaySeconds, float intervalSeconds) {
        return scheduleTask(task, delaySeconds, intervalSeconds, -1);
    }

    public Task scheduleTask(Task task, float delaySeconds, float intervalSeconds, int repeatCount) {
        synchronized (this) {
            synchronized (task) {
                if (task.timer == null) {
                    task.timer = this;
                    task.executeTimeMillis = (System.nanoTime() / 1000000) + ((long) (delaySeconds * 1000.0f));
                    task.intervalMillis = (long) (1000.0f * intervalSeconds);
                    task.repeatCount = repeatCount;
                    this.tasks.add(task);
                } else {
                    throw new IllegalArgumentException("The same task may not be scheduled twice.");
                }
            }
        }
        synchronized (threadLock) {
            threadLock.notifyAll();
        }
        return task;
    }

    public void stop() {
        synchronized (threadLock) {
            thread().instances.removeValue(this, true);
        }
    }

    public void start() {
        synchronized (threadLock) {
            Array<Timer> instances = thread().instances;
            if (!instances.contains(this, true)) {
                instances.add(this);
                threadLock.notifyAll();
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:17:0x001f, code lost:
        r3 = th;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void clear() {
        /*
            r5 = this;
            monitor-enter(r5)
            r0 = 0
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.utils.Timer$Task> r1 = r5.tasks     // Catch:{ all -> 0x0028 }
            int r1 = r1.size     // Catch:{ all -> 0x0028 }
        L_0x0006:
            if (r0 >= r1) goto L_0x0021
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.utils.Timer$Task> r2 = r5.tasks     // Catch:{ all -> 0x0028 }
            java.lang.Object r2 = r2.get(r0)     // Catch:{ all -> 0x0028 }
            com.badlogic.gdx.utils.Timer$Task r2 = (com.badlogic.gdx.utils.Timer.Task) r2     // Catch:{ all -> 0x0028 }
            monitor-enter(r2)     // Catch:{ all -> 0x0028 }
            r3 = 0
            r2.executeTimeMillis = r3     // Catch:{ all -> 0x001c }
            r3 = 0
            r2.timer = r3     // Catch:{ all -> 0x001c }
            monitor-exit(r2)     // Catch:{ all -> 0x001c }
            int r0 = r0 + 1
            goto L_0x0006
        L_0x001c:
            r3 = move-exception
        L_0x001d:
            monitor-exit(r2)     // Catch:{ all -> 0x001f }
            throw r3     // Catch:{ all -> 0x0028 }
        L_0x001f:
            r3 = move-exception
            goto L_0x001d
        L_0x0021:
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.utils.Timer$Task> r0 = r5.tasks     // Catch:{ all -> 0x0028 }
            r0.clear()     // Catch:{ all -> 0x0028 }
            monitor-exit(r5)
            return
        L_0x0028:
            r0 = move-exception
            monitor-exit(r5)
            goto L_0x002c
        L_0x002b:
            throw r0
        L_0x002c:
            goto L_0x002b
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.utils.Timer.clear():void");
    }

    public synchronized boolean isEmpty() {
        return this.tasks.size == 0;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0054, code lost:
        r3 = th;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized long update(long r7, long r9) {
        /*
            r6 = this;
            monitor-enter(r6)
            r0 = 0
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.utils.Timer$Task> r1 = r6.tasks     // Catch:{ all -> 0x0058 }
            int r1 = r1.size     // Catch:{ all -> 0x0058 }
        L_0x0006:
            if (r0 >= r1) goto L_0x0056
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.utils.Timer$Task> r2 = r6.tasks     // Catch:{ all -> 0x0058 }
            java.lang.Object r2 = r2.get(r0)     // Catch:{ all -> 0x0058 }
            com.badlogic.gdx.utils.Timer$Task r2 = (com.badlogic.gdx.utils.Timer.Task) r2     // Catch:{ all -> 0x0058 }
            monitor-enter(r2)     // Catch:{ all -> 0x0058 }
            long r3 = r2.executeTimeMillis     // Catch:{ all -> 0x0051 }
            int r5 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r5 <= 0) goto L_0x0021
            long r3 = r2.executeTimeMillis     // Catch:{ all -> 0x0051 }
            long r3 = r3 - r7
            long r3 = java.lang.Math.min(r9, r3)     // Catch:{ all -> 0x0051 }
            r9 = r3
            monitor-exit(r2)     // Catch:{ all -> 0x0051 }
            goto L_0x004e
        L_0x0021:
            int r3 = r2.repeatCount     // Catch:{ all -> 0x0051 }
            if (r3 != 0) goto L_0x0032
            r3 = 0
            r2.timer = r3     // Catch:{ all -> 0x0051 }
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.utils.Timer$Task> r3 = r6.tasks     // Catch:{ all -> 0x0051 }
            r3.removeIndex(r0)     // Catch:{ all -> 0x0051 }
            int r0 = r0 + -1
            int r1 = r1 + -1
            goto L_0x0048
        L_0x0032:
            long r3 = r2.intervalMillis     // Catch:{ all -> 0x0051 }
            long r3 = r3 + r7
            r2.executeTimeMillis = r3     // Catch:{ all -> 0x0051 }
            long r3 = r2.intervalMillis     // Catch:{ all -> 0x0051 }
            long r3 = java.lang.Math.min(r9, r3)     // Catch:{ all -> 0x0051 }
            r9 = r3
            int r3 = r2.repeatCount     // Catch:{ all -> 0x0051 }
            if (r3 <= 0) goto L_0x0048
            int r3 = r2.repeatCount     // Catch:{ all -> 0x0051 }
            int r3 = r3 + -1
            r2.repeatCount = r3     // Catch:{ all -> 0x0051 }
        L_0x0048:
            com.badlogic.gdx.Application r3 = r2.app     // Catch:{ all -> 0x0051 }
            r3.postRunnable(r2)     // Catch:{ all -> 0x0051 }
            monitor-exit(r2)     // Catch:{ all -> 0x0051 }
        L_0x004e:
            int r0 = r0 + 1
            goto L_0x0006
        L_0x0051:
            r3 = move-exception
        L_0x0052:
            monitor-exit(r2)     // Catch:{ all -> 0x0054 }
            throw r3     // Catch:{ all -> 0x0058 }
        L_0x0054:
            r3 = move-exception
            goto L_0x0052
        L_0x0056:
            monitor-exit(r6)
            return r9
        L_0x0058:
            r7 = move-exception
            monitor-exit(r6)
            goto L_0x005c
        L_0x005b:
            throw r7
        L_0x005c:
            goto L_0x005b
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.utils.Timer.update(long, long):long");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x001d, code lost:
        r3 = th;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void delay(long r6) {
        /*
            r5 = this;
            monitor-enter(r5)
            r0 = 0
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.utils.Timer$Task> r1 = r5.tasks     // Catch:{ all -> 0x0021 }
            int r1 = r1.size     // Catch:{ all -> 0x0021 }
        L_0x0006:
            if (r0 >= r1) goto L_0x001f
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.utils.Timer$Task> r2 = r5.tasks     // Catch:{ all -> 0x0021 }
            java.lang.Object r2 = r2.get(r0)     // Catch:{ all -> 0x0021 }
            com.badlogic.gdx.utils.Timer$Task r2 = (com.badlogic.gdx.utils.Timer.Task) r2     // Catch:{ all -> 0x0021 }
            monitor-enter(r2)     // Catch:{ all -> 0x0021 }
            long r3 = r2.executeTimeMillis     // Catch:{ all -> 0x001a }
            long r3 = r3 + r6
            r2.executeTimeMillis = r3     // Catch:{ all -> 0x001a }
            monitor-exit(r2)     // Catch:{ all -> 0x001a }
            int r0 = r0 + 1
            goto L_0x0006
        L_0x001a:
            r3 = move-exception
        L_0x001b:
            monitor-exit(r2)     // Catch:{ all -> 0x001d }
            throw r3     // Catch:{ all -> 0x0021 }
        L_0x001d:
            r3 = move-exception
            goto L_0x001b
        L_0x001f:
            monitor-exit(r5)
            return
        L_0x0021:
            r6 = move-exception
            monitor-exit(r5)
            goto L_0x0025
        L_0x0024:
            throw r6
        L_0x0025:
            goto L_0x0024
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.utils.Timer.delay(long):void");
    }

    public static Task post(Task task) {
        return instance().postTask(task);
    }

    public static Task schedule(Task task, float delaySeconds) {
        return instance().scheduleTask(task, delaySeconds);
    }

    public static Task schedule(Task task, float delaySeconds, float intervalSeconds) {
        return instance().scheduleTask(task, delaySeconds, intervalSeconds);
    }

    public static Task schedule(Task task, float delaySeconds, float intervalSeconds, int repeatCount) {
        return instance().scheduleTask(task, delaySeconds, intervalSeconds, repeatCount);
    }

    public static abstract class Task implements Runnable {
        final Application app = Gdx.app;
        long executeTimeMillis;
        long intervalMillis;
        int repeatCount;
        volatile Timer timer;

        public abstract void run();

        public Task() {
            if (this.app == null) {
                throw new IllegalStateException("Gdx.app not available.");
            }
        }

        public void cancel() {
            Timer timer2 = this.timer;
            if (timer2 != null) {
                synchronized (timer2) {
                    synchronized (this) {
                        this.executeTimeMillis = 0;
                        this.timer = null;
                        timer2.tasks.removeValue(this, true);
                    }
                }
                return;
            }
            synchronized (this) {
                this.executeTimeMillis = 0;
                this.timer = null;
            }
        }

        public boolean isScheduled() {
            return this.timer != null;
        }

        public synchronized long getExecuteTimeMillis() {
            return this.executeTimeMillis;
        }
    }

    static class TimerThread implements Runnable, LifecycleListener {
        final Files files = Gdx.files;
        Timer instance;
        final Array<Timer> instances = new Array<>(1);
        private long pauseMillis;

        public TimerThread() {
            Gdx.app.addLifecycleListener(this);
            resume();
            Thread thread = new Thread(this, "Timer");
            thread.setDaemon(true);
            thread.start();
        }

        public void run() {
            while (true) {
                synchronized (Timer.threadLock) {
                    if (Timer.thread != this) {
                        break;
                    } else if (this.files != Gdx.files) {
                        break;
                    } else {
                        long waitMillis = 5000;
                        if (this.pauseMillis == 0) {
                            long timeMillis = System.nanoTime() / 1000000;
                            int i = 0;
                            int n = this.instances.size;
                            while (i < n) {
                                try {
                                    waitMillis = this.instances.get(i).update(timeMillis, waitMillis);
                                    i++;
                                } catch (Throwable ex) {
                                    throw new GdxRuntimeException("Task failed: " + this.instances.get(i).getClass().getName(), ex);
                                }
                            }
                        }
                        if (Timer.thread == this && this.files == Gdx.files) {
                            if (waitMillis > 0) {
                                try {
                                    Timer.threadLock.wait(waitMillis);
                                } catch (InterruptedException e) {
                                }
                            }
                        }
                    }
                }
            }
            dispose();
        }

        public void resume() {
            synchronized (Timer.threadLock) {
                long delayMillis = (System.nanoTime() / 1000000) - this.pauseMillis;
                int n = this.instances.size;
                for (int i = 0; i < n; i++) {
                    this.instances.get(i).delay(delayMillis);
                }
                this.pauseMillis = 0;
                Timer.threadLock.notifyAll();
            }
        }

        public void pause() {
            synchronized (Timer.threadLock) {
                this.pauseMillis = System.nanoTime() / 1000000;
                Timer.threadLock.notifyAll();
            }
        }

        public void dispose() {
            synchronized (Timer.threadLock) {
                if (Timer.thread == this) {
                    Timer.thread = null;
                }
                this.instances.clear();
                Timer.threadLock.notifyAll();
            }
            Gdx.app.removeLifecycleListener(this);
        }
    }
}
