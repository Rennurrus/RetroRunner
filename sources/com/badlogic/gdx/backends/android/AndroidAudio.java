package com.badlogic.gdx.backends.android;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.AudioRecorder;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class AndroidAudio implements Audio {
    private final AudioManager manager;
    protected final List<AndroidMusic> musics = new ArrayList();
    private final SoundPool soundPool;

    public AndroidAudio(Context context, AndroidApplicationConfiguration config) {
        if (!config.disableAudio) {
            if (Build.VERSION.SDK_INT >= 21) {
                this.soundPool = new SoundPool.Builder().setAudioAttributes(new AudioAttributes.Builder().setUsage(14).setContentType(4).build()).setMaxStreams(config.maxSimultaneousSounds).build();
            } else {
                this.soundPool = new SoundPool(config.maxSimultaneousSounds, 3, 0);
            }
            this.manager = (AudioManager) context.getSystemService("audio");
            if (context instanceof Activity) {
                ((Activity) context).setVolumeControlStream(3);
                return;
            }
            return;
        }
        this.soundPool = null;
        this.manager = null;
    }

    /* access modifiers changed from: protected */
    public void pause() {
        if (this.soundPool != null) {
            synchronized (this.musics) {
                for (AndroidMusic music : this.musics) {
                    if (music.isPlaying()) {
                        music.pause();
                        music.wasPlaying = true;
                    } else {
                        music.wasPlaying = false;
                    }
                }
            }
            this.soundPool.autoPause();
        }
    }

    /* access modifiers changed from: protected */
    public void resume() {
        if (this.soundPool != null) {
            synchronized (this.musics) {
                for (int i = 0; i < this.musics.size(); i++) {
                    if (this.musics.get(i).wasPlaying) {
                        this.musics.get(i).play();
                    }
                }
            }
            this.soundPool.autoResume();
        }
    }

    public AudioDevice newAudioDevice(int samplingRate, boolean isMono) {
        if (this.soundPool != null) {
            return new AndroidAudioDevice(samplingRate, isMono);
        }
        throw new GdxRuntimeException("Android audio is not enabled by the application config.");
    }

    public Music newMusic(FileHandle file) {
        if (this.soundPool != null) {
            AndroidFileHandle aHandle = (AndroidFileHandle) file;
            MediaPlayer mediaPlayer = new MediaPlayer();
            if (aHandle.type() == Files.FileType.Internal) {
                try {
                    AssetFileDescriptor descriptor = aHandle.getAssetFileDescriptor();
                    mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                    descriptor.close();
                    mediaPlayer.prepare();
                    AndroidMusic music = new AndroidMusic(this, mediaPlayer);
                    synchronized (this.musics) {
                        this.musics.add(music);
                    }
                    return music;
                } catch (Exception ex) {
                    throw new GdxRuntimeException("Error loading audio file: " + file + "\nNote: Internal audio files must be placed in the assets directory.", ex);
                }
            } else {
                try {
                    mediaPlayer.setDataSource(aHandle.file().getPath());
                    mediaPlayer.prepare();
                    AndroidMusic music2 = new AndroidMusic(this, mediaPlayer);
                    synchronized (this.musics) {
                        this.musics.add(music2);
                    }
                    return music2;
                } catch (Exception ex2) {
                    throw new GdxRuntimeException("Error loading audio file: " + file, ex2);
                }
            }
        } else {
            throw new GdxRuntimeException("Android audio is not enabled by the application config.");
        }
    }

    public Music newMusic(FileDescriptor fd) {
        if (this.soundPool != null) {
            MediaPlayer mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(fd);
                mediaPlayer.prepare();
                AndroidMusic music = new AndroidMusic(this, mediaPlayer);
                synchronized (this.musics) {
                    this.musics.add(music);
                }
                return music;
            } catch (Exception ex) {
                throw new GdxRuntimeException("Error loading audio from FileDescriptor", ex);
            }
        } else {
            throw new GdxRuntimeException("Android audio is not enabled by the application config.");
        }
    }

    public Sound newSound(FileHandle file) {
        if (this.soundPool != null) {
            AndroidFileHandle aHandle = (AndroidFileHandle) file;
            if (aHandle.type() == Files.FileType.Internal) {
                try {
                    AssetFileDescriptor descriptor = aHandle.getAssetFileDescriptor();
                    AndroidSound sound = new AndroidSound(this.soundPool, this.manager, this.soundPool.load(descriptor, 1));
                    descriptor.close();
                    return sound;
                } catch (IOException ex) {
                    throw new GdxRuntimeException("Error loading audio file: " + file + "\nNote: Internal audio files must be placed in the assets directory.", ex);
                }
            } else {
                try {
                    return new AndroidSound(this.soundPool, this.manager, this.soundPool.load(aHandle.file().getPath(), 1));
                } catch (Exception ex2) {
                    throw new GdxRuntimeException("Error loading audio file: " + file, ex2);
                }
            }
        } else {
            throw new GdxRuntimeException("Android audio is not enabled by the application config.");
        }
    }

    public AudioRecorder newAudioRecorder(int samplingRate, boolean isMono) {
        if (this.soundPool != null) {
            return new AndroidAudioRecorder(samplingRate, isMono);
        }
        throw new GdxRuntimeException("Android audio is not enabled by the application config.");
    }

    public void dispose() {
        if (this.soundPool != null) {
            synchronized (this.musics) {
                Iterator<AndroidMusic> it = new ArrayList<>(this.musics).iterator();
                while (it.hasNext()) {
                    it.next().dispose();
                }
            }
            this.soundPool.release();
        }
    }
}
