package com.badlogic.gdx.backends.android;

import android.media.MediaPlayer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import java.io.IOException;

public class AndroidMusic implements Music, MediaPlayer.OnCompletionListener {
    private final AndroidAudio audio;
    private boolean isPrepared = true;
    protected Music.OnCompletionListener onCompletionListener;
    private MediaPlayer player;
    private float volume = 1.0f;
    protected boolean wasPlaying = false;

    AndroidMusic(AndroidAudio audio2, MediaPlayer player2) {
        this.audio = audio2;
        this.player = player2;
        this.onCompletionListener = null;
        this.player.setOnCompletionListener(this);
    }

    public void dispose() {
        MediaPlayer mediaPlayer = this.player;
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
                this.player = null;
                this.onCompletionListener = null;
                synchronized (this.audio.musics) {
                    this.audio.musics.remove(this);
                }
            } catch (Throwable th) {
                try {
                    Gdx.app.log("AndroidMusic", "error while disposing AndroidMusic instance, non-fatal");
                    this.player = null;
                    this.onCompletionListener = null;
                    synchronized (this.audio.musics) {
                        this.audio.musics.remove(this);
                    }
                } catch (Throwable th2) {
                    this.player = null;
                    this.onCompletionListener = null;
                    synchronized (this.audio.musics) {
                        this.audio.musics.remove(this);
                        throw th2;
                    }
                }
            }
        }
    }

    public boolean isLooping() {
        MediaPlayer mediaPlayer = this.player;
        if (mediaPlayer == null) {
            return false;
        }
        try {
            return mediaPlayer.isLooping();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isPlaying() {
        MediaPlayer mediaPlayer = this.player;
        if (mediaPlayer == null) {
            return false;
        }
        try {
            return mediaPlayer.isPlaying();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void pause() {
        MediaPlayer mediaPlayer = this.player;
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    this.player.pause();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.wasPlaying = false;
        }
    }

    public void play() {
        MediaPlayer mediaPlayer = this.player;
        if (mediaPlayer != null) {
            try {
                if (!mediaPlayer.isPlaying()) {
                    try {
                        if (!this.isPrepared) {
                            this.player.prepare();
                            this.isPrepared = true;
                        }
                        this.player.start();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
            } catch (Exception e3) {
                e3.printStackTrace();
            }
        }
    }

    public void setLooping(boolean isLooping) {
        MediaPlayer mediaPlayer = this.player;
        if (mediaPlayer != null) {
            mediaPlayer.setLooping(isLooping);
        }
    }

    public void setVolume(float volume2) {
        MediaPlayer mediaPlayer = this.player;
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume2, volume2);
            this.volume = volume2;
        }
    }

    public float getVolume() {
        return this.volume;
    }

    public void setPan(float pan, float volume2) {
        if (this.player != null) {
            float leftVolume = volume2;
            float rightVolume = volume2;
            if (pan < 0.0f) {
                rightVolume *= 1.0f - Math.abs(pan);
            } else if (pan > 0.0f) {
                leftVolume *= 1.0f - Math.abs(pan);
            }
            this.player.setVolume(leftVolume, rightVolume);
            this.volume = volume2;
        }
    }

    public void stop() {
        MediaPlayer mediaPlayer = this.player;
        if (mediaPlayer != null) {
            if (this.isPrepared) {
                mediaPlayer.seekTo(0);
            }
            this.player.stop();
            this.isPrepared = false;
        }
    }

    public void setPosition(float position) {
        MediaPlayer mediaPlayer = this.player;
        if (mediaPlayer != null) {
            try {
                if (!this.isPrepared) {
                    mediaPlayer.prepare();
                    this.isPrepared = true;
                }
                this.player.seekTo((int) (1000.0f * position));
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    public float getPosition() {
        MediaPlayer mediaPlayer = this.player;
        if (mediaPlayer == null) {
            return 0.0f;
        }
        return ((float) mediaPlayer.getCurrentPosition()) / 1000.0f;
    }

    public float getDuration() {
        MediaPlayer mediaPlayer = this.player;
        if (mediaPlayer == null) {
            return 0.0f;
        }
        return ((float) mediaPlayer.getDuration()) / 1000.0f;
    }

    public void setOnCompletionListener(Music.OnCompletionListener listener) {
        this.onCompletionListener = listener;
    }

    public void onCompletion(MediaPlayer mp) {
        if (this.onCompletionListener != null) {
            Gdx.app.postRunnable(new Runnable() {
                public void run() {
                    AndroidMusic.this.onCompletionListener.onCompletion(AndroidMusic.this);
                }
            });
        }
    }
}
