package com.twi.game.Managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class MenuMusicManager {
    private int lastLastMusic = 0;
    private int lastMusic = 0;
    private Music music;
    private boolean stop = false;

    public MenuMusicManager() {
        int rand = ((int) (Math.random() * 3.0d)) + 1;
        if (rand == 1) {
            this.music = Gdx.audio.newMusic(Gdx.files.internal("Music/Menu/1.ogg"));
            this.lastMusic = 1;
        } else if (rand == 2) {
            this.music = Gdx.audio.newMusic(Gdx.files.internal("Music/Menu/2.ogg"));
            this.lastMusic = 2;
        } else if (rand == 3) {
            this.music = Gdx.audio.newMusic(Gdx.files.internal("Music/Menu/3.ogg"));
            this.lastMusic = 3;
        }
        this.music.setVolume(0.25f);
    }

    public void play() {
        this.music.play();
    }

    public void stop() {
        this.music.stop();
    }

    public void stop(boolean f) {
        this.music.stop();
        this.stop = true;
    }

    public boolean IsMusicStop() {
        return !this.music.isPlaying();
    }

    public void next() {
        int rand = ((int) (Math.random() * 3.0d)) + 1;
        while (true) {
            if (rand != this.lastMusic && rand != this.lastLastMusic) {
                break;
            }
            rand = ((int) (Math.random() * 3.0d)) + 1;
        }
        if (rand == 1) {
            this.music = Gdx.audio.newMusic(Gdx.files.internal("Music/Menu/1.ogg"));
            this.lastLastMusic = this.lastMusic;
            this.lastMusic = 1;
        } else if (rand == 2) {
            this.music = Gdx.audio.newMusic(Gdx.files.internal("Music/Menu/2.ogg"));
            this.lastLastMusic = this.lastMusic;
            this.lastMusic = 2;
        } else if (rand == 3) {
            this.music = Gdx.audio.newMusic(Gdx.files.internal("Music/Menu/3.ogg"));
            this.lastLastMusic = this.lastMusic;
            this.lastMusic = 3;
        }
        this.music.setVolume(0.25f);
    }

    public void choiceNext() {
        if (IsMusicStop() && !this.stop) {
            next();
            play();
        }
    }

    public void dispoce() {
        this.music.dispose();
    }
}
