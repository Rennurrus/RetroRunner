package com.twi.game.Managers;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class PlayScreenMusicManager {
    private int lastLastMusic = 0;
    private int lastMusic = 0;
    private Music music;

    public PlayScreenMusicManager() {
        Audio audio = Gdx.audio;
        Files files = Gdx.files;
        this.music = audio.newMusic(files.internal("Music/PlayScreen/" + (((int) (Math.random() * 15.0d)) + 1) + ".ogg"));
        this.music.setVolume(0.25f);
        this.music.stop();
    }

    public void play() {
        this.music.play();
    }

    public void stop() {
        this.music.stop();
    }

    public boolean IsMusicStop() {
        return !this.music.isPlaying();
    }

    public void next() {
        stop();
        this.music.dispose();
        int rand = ((int) (Math.random() * 15.0d)) + 1;
        while (true) {
            if (rand == this.lastMusic || rand == this.lastLastMusic) {
                rand = ((int) (Math.random() * 15.0d)) + 1;
            } else {
                Audio audio = Gdx.audio;
                Files files = Gdx.files;
                this.music = audio.newMusic(files.internal("Music/PlayScreen/" + rand + ".ogg"));
                this.lastLastMusic = this.lastMusic;
                this.lastMusic = rand;
                this.music.setVolume(0.25f);
                return;
            }
        }
    }

    public void setVolume(float x) {
        this.music.setVolume(x);
    }

    public void choiceNext() {
        if (IsMusicStop()) {
            next();
            play();
        }
    }

    public void dispoce() {
        this.music.dispose();
    }
}
