package com.badlogic.gdx.backends.android;

import android.media.AudioManager;
import android.media.SoundPool;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.IntArray;

final class AndroidSound implements Sound {
    final AudioManager manager;
    final int soundId;
    final SoundPool soundPool;
    final IntArray streamIds = new IntArray(8);

    AndroidSound(SoundPool pool, AudioManager manager2, int soundId2) {
        this.soundPool = pool;
        this.manager = manager2;
        this.soundId = soundId2;
    }

    public void dispose() {
        this.soundPool.unload(this.soundId);
    }

    public long play() {
        return play(1.0f);
    }

    public long play(float volume) {
        if (this.streamIds.size == 8) {
            this.streamIds.pop();
        }
        int streamId = this.soundPool.play(this.soundId, volume, volume, 1, 0, 1.0f);
        if (streamId == 0) {
            return -1;
        }
        this.streamIds.insert(0, streamId);
        return (long) streamId;
    }

    public void stop() {
        int n = this.streamIds.size;
        for (int i = 0; i < n; i++) {
            this.soundPool.stop(this.streamIds.get(i));
        }
    }

    public void stop(long soundId2) {
        this.soundPool.stop((int) soundId2);
    }

    public void pause() {
        this.soundPool.autoPause();
    }

    public void pause(long soundId2) {
        this.soundPool.pause((int) soundId2);
    }

    public void resume() {
        this.soundPool.autoResume();
    }

    public void resume(long soundId2) {
        this.soundPool.resume((int) soundId2);
    }

    public void setPitch(long soundId2, float pitch) {
        this.soundPool.setRate((int) soundId2, pitch);
    }

    public void setVolume(long soundId2, float volume) {
        this.soundPool.setVolume((int) soundId2, volume, volume);
    }

    public long loop() {
        return loop(1.0f);
    }

    public long loop(float volume) {
        if (this.streamIds.size == 8) {
            this.streamIds.pop();
        }
        int streamId = this.soundPool.play(this.soundId, volume, volume, 1, -1, 1.0f);
        if (streamId == 0) {
            return -1;
        }
        this.streamIds.insert(0, streamId);
        return (long) streamId;
    }

    public void setLooping(long soundId2, boolean looping) {
        this.soundPool.setLoop((int) soundId2, looping ? -1 : 0);
    }

    public void setPan(long soundId2, float pan, float volume) {
        float leftVolume = volume;
        float rightVolume = volume;
        if (pan < 0.0f) {
            rightVolume *= 1.0f - Math.abs(pan);
        } else if (pan > 0.0f) {
            leftVolume *= 1.0f - Math.abs(pan);
        }
        this.soundPool.setVolume((int) soundId2, leftVolume, rightVolume);
    }

    public long play(float volume, float pitch, float pan) {
        if (this.streamIds.size == 8) {
            this.streamIds.pop();
        }
        float leftVolume = volume;
        float rightVolume = volume;
        if (pan < 0.0f) {
            rightVolume *= 1.0f - Math.abs(pan);
        } else if (pan > 0.0f) {
            leftVolume *= 1.0f - Math.abs(pan);
        }
        int streamId = this.soundPool.play(this.soundId, leftVolume, rightVolume, 1, 0, pitch);
        if (streamId == 0) {
            return -1;
        }
        this.streamIds.insert(0, streamId);
        return (long) streamId;
    }

    public long loop(float volume, float pitch, float pan) {
        if (this.streamIds.size == 8) {
            this.streamIds.pop();
        }
        float leftVolume = volume;
        float rightVolume = volume;
        if (pan < 0.0f) {
            rightVolume *= 1.0f - Math.abs(pan);
        } else if (pan > 0.0f) {
            leftVolume *= 1.0f - Math.abs(pan);
        }
        int streamId = this.soundPool.play(this.soundId, leftVolume, rightVolume, 1, -1, pitch);
        if (streamId == 0) {
            return -1;
        }
        this.streamIds.insert(0, streamId);
        return (long) streamId;
    }
}
