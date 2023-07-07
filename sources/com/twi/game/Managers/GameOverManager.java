package com.twi.game.Managers;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameOverManager {
    private Texture[] avatar = new Texture[10];
    private Texture black = new Texture("GameOver/Glitch/1.png");
    private Texture[] gameover = new Texture[10];
    private Texture[] glitch = new Texture[12];
    private boolean isGlitchEffectOff = false;
    private long lastAvatarTime = 0;
    private float lastGlitchTime2 = 0.0f;
    private int lastSound;
    private boolean play = false;
    public Music sound;
    private int stateAvatar = 1;
    private int stateGlitch = 1;

    public GameOverManager() {
        for (int i = 0; i < 12; i++) {
            Texture[] textureArr = this.glitch;
            textureArr[i] = new Texture("GameOver/Glitch/" + (i + 1) + ".png");
        }
        for (int i2 = 0; i2 < 10; i2++) {
            Texture[] textureArr2 = this.avatar;
            textureArr2[i2] = new Texture("GameOver/Avatar/" + (i2 + 1) + ".png");
        }
        this.sound = Gdx.audio.newMusic(Gdx.files.internal("GameOver/Sounds/4.ogg"));
        this.sound.setVolume(2.0f);
        this.lastSound = (int) ((Math.random() * 25.0d) + 1.0d);
    }

    public void setDefault() {
        if (this.sound.isPlaying()) {
            this.sound.stop();
        }
        this.lastAvatarTime = 0;
        this.lastGlitchTime2 = 0.0f;
        this.stateAvatar = 1;
        this.stateGlitch = 1;
        this.play = false;
        this.isGlitchEffectOff = false;
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:0x0026  */
    /* JADX WARNING: Removed duplicated region for block: B:19:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void update(float r9) {
        /*
            r8 = this;
            boolean r0 = r8.isGlitchEffectOff
            r1 = 0
            r2 = 1
            if (r0 != 0) goto L_0x0020
            float r0 = r8.lastGlitchTime2
            double r3 = (double) r0
            r5 = 4602678819172646912(0x3fe0000000000000, double:0.5)
            int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r7 >= 0) goto L_0x0020
            float r0 = r0 + r9
            r8.lastGlitchTime2 = r0
            int r0 = r8.stateGlitch
            int r0 = r0 + r2
            r8.stateGlitch = r0
            int r0 = r8.stateGlitch
            r3 = 10
            if (r0 <= r3) goto L_0x0022
            r8.stateGlitch = r1
            goto L_0x0022
        L_0x0020:
            r8.isGlitchEffectOff = r2
        L_0x0022:
            boolean r0 = r8.isGlitchEffectOff
            if (r0 != r2) goto L_0x004c
            long r3 = r8.lastAvatarTime
            r5 = 3
            int r0 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r0 >= 0) goto L_0x004c
            float r0 = (float) r3
            float r0 = r0 + r9
            long r3 = (long) r0
            r8.lastAvatarTime = r3
            int r0 = r8.stateAvatar
            int r0 = r0 + r2
            r8.stateAvatar = r0
            int r0 = r8.stateAvatar
            r2 = 9
            if (r0 <= r2) goto L_0x0040
            r8.stateAvatar = r1
        L_0x0040:
            long r0 = r8.lastAvatarTime
            r2 = 1
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 <= 0) goto L_0x004c
            r0 = 0
            r8.lastAvatarTime = r0
        L_0x004c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.twi.game.Managers.GameOverManager.update(float):void");
    }

    public void render(SpriteBatch SpB) {
        if (!this.isGlitchEffectOff) {
            SpB.draw(this.glitch[this.stateGlitch], 0.0f, 0.0f);
            return;
        }
        SpB.draw(this.black, 0.0f, 0.0f);
        SpB.draw(this.avatar[this.stateAvatar], 0.0f, 0.0f);
        if (this.lastGlitchTime2 > 0.5f && !this.play) {
            this.sound.play();
            choise();
            this.play = true;
        }
    }

    public boolean isGlitchEffectOff() {
        return this.isGlitchEffectOff;
    }

    public void choise() {
        int rand = (int) ((Math.random() * 25.0d) + 1.0d);
        while (true) {
            if (rand == this.lastSound || rand == 17 || rand == 2) {
                rand = (int) ((Math.random() * 25.0d) + 1.0d);
            } else {
                this.sound.dispose();
                Audio audio = Gdx.audio;
                Files files = Gdx.files;
                this.sound = audio.newMusic(files.internal("GameOver/Sounds/" + rand + ".ogg"));
                this.lastSound = rand;
                this.sound.setVolume(2.0f);
                this.sound.play();
                return;
            }
        }
    }

    public void dispose() {
        for (Texture av : this.avatar) {
            av.dispose();
        }
        for (Texture gl : this.glitch) {
            gl.dispose();
        }
        this.black.dispose();
        this.sound.dispose();
    }
}
