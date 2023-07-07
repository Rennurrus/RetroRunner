package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ArrayReflection;

public class Animation<T> {
    private float animationDuration;
    private float frameDuration;
    T[] keyFrames;
    private int lastFrameNumber;
    private float lastStateTime;
    private PlayMode playMode;

    public enum PlayMode {
        NORMAL,
        REVERSED,
        LOOP,
        LOOP_REVERSED,
        LOOP_PINGPONG,
        LOOP_RANDOM
    }

    public Animation(float frameDuration2, Array<? extends T> keyFrames2) {
        this.playMode = PlayMode.NORMAL;
        this.frameDuration = frameDuration2;
        T[] frames = (Object[]) ArrayReflection.newInstance(keyFrames2.items.getClass().getComponentType(), keyFrames2.size);
        int n = keyFrames2.size;
        for (int i = 0; i < n; i++) {
            frames[i] = keyFrames2.get(i);
        }
        setKeyFrames(frames);
    }

    public Animation(float frameDuration2, Array<? extends T> keyFrames2, PlayMode playMode2) {
        this(frameDuration2, keyFrames2);
        setPlayMode(playMode2);
    }

    public Animation(float frameDuration2, T... keyFrames2) {
        this.playMode = PlayMode.NORMAL;
        this.frameDuration = frameDuration2;
        setKeyFrames(keyFrames2);
    }

    public T getKeyFrame(float stateTime, boolean looping) {
        PlayMode oldPlayMode = this.playMode;
        if (!looping || !(this.playMode == PlayMode.NORMAL || this.playMode == PlayMode.REVERSED)) {
            if (!(looping || this.playMode == PlayMode.NORMAL || this.playMode == PlayMode.REVERSED)) {
                if (this.playMode == PlayMode.LOOP_REVERSED) {
                    this.playMode = PlayMode.REVERSED;
                } else {
                    this.playMode = PlayMode.LOOP;
                }
            }
        } else if (this.playMode == PlayMode.NORMAL) {
            this.playMode = PlayMode.LOOP;
        } else {
            this.playMode = PlayMode.LOOP_REVERSED;
        }
        T frame = getKeyFrame(stateTime);
        this.playMode = oldPlayMode;
        return frame;
    }

    public T getKeyFrame(float stateTime) {
        return this.keyFrames[getKeyFrameIndex(stateTime)];
    }

    public int getKeyFrameIndex(float stateTime) {
        if (this.keyFrames.length == 1) {
            return 0;
        }
        int frameNumber = (int) (stateTime / this.frameDuration);
        switch (this.playMode) {
            case NORMAL:
                frameNumber = Math.min(this.keyFrames.length - 1, frameNumber);
                break;
            case LOOP:
                frameNumber %= this.keyFrames.length;
                break;
            case LOOP_PINGPONG:
                T[] tArr = this.keyFrames;
                frameNumber %= (tArr.length * 2) - 2;
                if (frameNumber >= tArr.length) {
                    frameNumber = (tArr.length - 2) - (frameNumber - tArr.length);
                    break;
                }
                break;
            case LOOP_RANDOM:
                if (((int) (this.lastStateTime / this.frameDuration)) == frameNumber) {
                    frameNumber = this.lastFrameNumber;
                    break;
                } else {
                    frameNumber = MathUtils.random(this.keyFrames.length - 1);
                    break;
                }
            case REVERSED:
                frameNumber = Math.max((this.keyFrames.length - frameNumber) - 1, 0);
                break;
            case LOOP_REVERSED:
                T[] tArr2 = this.keyFrames;
                frameNumber = (tArr2.length - (frameNumber % tArr2.length)) - 1;
                break;
        }
        this.lastFrameNumber = frameNumber;
        this.lastStateTime = stateTime;
        return frameNumber;
    }

    public T[] getKeyFrames() {
        return this.keyFrames;
    }

    /* access modifiers changed from: protected */
    public void setKeyFrames(T... keyFrames2) {
        this.keyFrames = keyFrames2;
        this.animationDuration = ((float) keyFrames2.length) * this.frameDuration;
    }

    public PlayMode getPlayMode() {
        return this.playMode;
    }

    public void setPlayMode(PlayMode playMode2) {
        this.playMode = playMode2;
    }

    public boolean isAnimationFinished(float stateTime) {
        return this.keyFrames.length - 1 < ((int) (stateTime / this.frameDuration));
    }

    public void setFrameDuration(float frameDuration2) {
        this.frameDuration = frameDuration2;
        this.animationDuration = ((float) this.keyFrames.length) * frameDuration2;
    }

    public float getFrameDuration() {
        return this.frameDuration;
    }

    public float getAnimationDuration() {
        return this.animationDuration;
    }
}
