package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.StreamUtils;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.Iterator;

public class ParticleEffect implements Disposable {
    private BoundingBox bounds;
    private final Array<ParticleEmitter> emitters;
    protected float motionScale;
    private boolean ownsTexture;
    protected float xSizeScale;
    protected float ySizeScale;

    public ParticleEffect() {
        this.xSizeScale = 1.0f;
        this.ySizeScale = 1.0f;
        this.motionScale = 1.0f;
        this.emitters = new Array<>(8);
    }

    public ParticleEffect(ParticleEffect effect) {
        this.xSizeScale = 1.0f;
        this.ySizeScale = 1.0f;
        this.motionScale = 1.0f;
        this.emitters = new Array<>(true, effect.emitters.size);
        int n = effect.emitters.size;
        for (int i = 0; i < n; i++) {
            this.emitters.add(newEmitter(effect.emitters.get(i)));
        }
    }

    public void start() {
        int n = this.emitters.size;
        for (int i = 0; i < n; i++) {
            this.emitters.get(i).start();
        }
    }

    public void reset() {
        reset(true);
    }

    public void reset(boolean resetScaling) {
        int n = this.emitters.size;
        for (int i = 0; i < n; i++) {
            this.emitters.get(i).reset();
        }
        if (!resetScaling) {
            return;
        }
        if (this.xSizeScale != 1.0f || this.ySizeScale != 1.0f || this.motionScale != 1.0f) {
            scaleEffect(1.0f / this.xSizeScale, 1.0f / this.ySizeScale, 1.0f / this.motionScale);
            this.motionScale = 1.0f;
            this.ySizeScale = 1.0f;
            this.xSizeScale = 1.0f;
        }
    }

    public void update(float delta) {
        int n = this.emitters.size;
        for (int i = 0; i < n; i++) {
            this.emitters.get(i).update(delta);
        }
    }

    public void draw(Batch spriteBatch) {
        int n = this.emitters.size;
        for (int i = 0; i < n; i++) {
            this.emitters.get(i).draw(spriteBatch);
        }
    }

    public void draw(Batch spriteBatch, float delta) {
        int n = this.emitters.size;
        for (int i = 0; i < n; i++) {
            this.emitters.get(i).draw(spriteBatch, delta);
        }
    }

    public void allowCompletion() {
        int n = this.emitters.size;
        for (int i = 0; i < n; i++) {
            this.emitters.get(i).allowCompletion();
        }
    }

    public boolean isComplete() {
        int n = this.emitters.size;
        for (int i = 0; i < n; i++) {
            if (!this.emitters.get(i).isComplete()) {
                return false;
            }
        }
        return true;
    }

    public void setDuration(int duration) {
        int n = this.emitters.size;
        for (int i = 0; i < n; i++) {
            ParticleEmitter emitter = this.emitters.get(i);
            emitter.setContinuous(false);
            emitter.duration = (float) duration;
            emitter.durationTimer = 0.0f;
        }
    }

    public void setPosition(float x, float y) {
        int n = this.emitters.size;
        for (int i = 0; i < n; i++) {
            this.emitters.get(i).setPosition(x, y);
        }
    }

    public void setFlip(boolean flipX, boolean flipY) {
        int n = this.emitters.size;
        for (int i = 0; i < n; i++) {
            this.emitters.get(i).setFlip(flipX, flipY);
        }
    }

    public void flipY() {
        int n = this.emitters.size;
        for (int i = 0; i < n; i++) {
            this.emitters.get(i).flipY();
        }
    }

    public Array<ParticleEmitter> getEmitters() {
        return this.emitters;
    }

    public ParticleEmitter findEmitter(String name) {
        int n = this.emitters.size;
        for (int i = 0; i < n; i++) {
            ParticleEmitter emitter = this.emitters.get(i);
            if (emitter.getName().equals(name)) {
                return emitter;
            }
        }
        return null;
    }

    public void save(Writer output) throws IOException {
        int index = 0;
        int i = 0;
        int n = this.emitters.size;
        while (i < n) {
            ParticleEmitter emitter = this.emitters.get(i);
            int index2 = index + 1;
            if (index > 0) {
                output.write("\n");
            }
            emitter.save(output);
            i++;
            index = index2;
        }
    }

    public void load(FileHandle effectFile, FileHandle imagesDir) {
        loadEmitters(effectFile);
        loadEmitterImages(imagesDir);
    }

    public void load(FileHandle effectFile, TextureAtlas atlas) {
        load(effectFile, atlas, (String) null);
    }

    public void load(FileHandle effectFile, TextureAtlas atlas, String atlasPrefix) {
        loadEmitters(effectFile);
        loadEmitterImages(atlas, atlasPrefix);
    }

    public void loadEmitters(FileHandle effectFile) {
        InputStream input = effectFile.read();
        this.emitters.clear();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(input), GL20.GL_NEVER);
            do {
                this.emitters.add(newEmitter(reader));
            } while (reader.readLine() != null);
            StreamUtils.closeQuietly(reader);
        } catch (IOException ex) {
            throw new GdxRuntimeException("Error loading effect: " + effectFile, ex);
        } catch (Throwable th) {
            StreamUtils.closeQuietly((Closeable) null);
            throw th;
        }
    }

    public void loadEmitterImages(TextureAtlas atlas) {
        loadEmitterImages(atlas, (String) null);
    }

    public void loadEmitterImages(TextureAtlas atlas, String atlasPrefix) {
        int n = this.emitters.size;
        for (int i = 0; i < n; i++) {
            ParticleEmitter emitter = this.emitters.get(i);
            if (emitter.getImagePaths().size != 0) {
                Array<Sprite> sprites = new Array<>();
                Iterator<String> it = emitter.getImagePaths().iterator();
                while (it.hasNext()) {
                    String imageName = new File(it.next().replace('\\', '/')).getName();
                    int lastDotIndex = imageName.lastIndexOf(46);
                    if (lastDotIndex != -1) {
                        imageName = imageName.substring(0, lastDotIndex);
                    }
                    if (atlasPrefix != null) {
                        imageName = atlasPrefix + imageName;
                    }
                    Sprite sprite = atlas.createSprite(imageName);
                    if (sprite != null) {
                        sprites.add(sprite);
                    } else {
                        throw new IllegalArgumentException("SpriteSheet missing image: " + imageName);
                    }
                }
                emitter.setSprites(sprites);
            }
        }
    }

    public void loadEmitterImages(FileHandle imagesDir) {
        this.ownsTexture = true;
        ObjectMap<String, Sprite> loadedSprites = new ObjectMap<>(this.emitters.size);
        int n = this.emitters.size;
        for (int i = 0; i < n; i++) {
            ParticleEmitter emitter = this.emitters.get(i);
            if (emitter.getImagePaths().size != 0) {
                Array<Sprite> sprites = new Array<>();
                Iterator<String> it = emitter.getImagePaths().iterator();
                while (it.hasNext()) {
                    String imageName = new File(it.next().replace('\\', '/')).getName();
                    Sprite sprite = loadedSprites.get(imageName);
                    if (sprite == null) {
                        sprite = new Sprite(loadTexture(imagesDir.child(imageName)));
                        loadedSprites.put(imageName, sprite);
                    }
                    sprites.add(sprite);
                }
                emitter.setSprites(sprites);
            }
        }
    }

    /* access modifiers changed from: protected */
    public ParticleEmitter newEmitter(BufferedReader reader) throws IOException {
        return new ParticleEmitter(reader);
    }

    /* access modifiers changed from: protected */
    public ParticleEmitter newEmitter(ParticleEmitter emitter) {
        return new ParticleEmitter(emitter);
    }

    /* access modifiers changed from: protected */
    public Texture loadTexture(FileHandle file) {
        return new Texture(file, false);
    }

    public void dispose() {
        if (this.ownsTexture) {
            int n = this.emitters.size;
            for (int i = 0; i < n; i++) {
                Iterator<Sprite> it = this.emitters.get(i).getSprites().iterator();
                while (it.hasNext()) {
                    it.next().getTexture().dispose();
                }
            }
        }
    }

    public BoundingBox getBoundingBox() {
        if (this.bounds == null) {
            this.bounds = new BoundingBox();
        }
        BoundingBox bounds2 = this.bounds;
        bounds2.inf();
        Iterator<ParticleEmitter> it = this.emitters.iterator();
        while (it.hasNext()) {
            bounds2.ext(it.next().getBoundingBox());
        }
        return bounds2;
    }

    public void scaleEffect(float scaleFactor) {
        scaleEffect(scaleFactor, scaleFactor, scaleFactor);
    }

    public void scaleEffect(float scaleFactor, float motionScaleFactor) {
        scaleEffect(scaleFactor, scaleFactor, motionScaleFactor);
    }

    public void scaleEffect(float xSizeScaleFactor, float ySizeScaleFactor, float motionScaleFactor) {
        this.xSizeScale *= xSizeScaleFactor;
        this.ySizeScale *= ySizeScaleFactor;
        this.motionScale *= motionScaleFactor;
        Iterator<ParticleEmitter> it = this.emitters.iterator();
        while (it.hasNext()) {
            ParticleEmitter particleEmitter = it.next();
            particleEmitter.scaleSize(xSizeScaleFactor, ySizeScaleFactor);
            particleEmitter.scaleMotion(motionScaleFactor);
        }
    }

    public void setEmittersCleanUpBlendFunction(boolean cleanUpBlendFunction) {
        int n = this.emitters.size;
        for (int i = 0; i < n; i++) {
            this.emitters.get(i).setCleansUpBlendFunction(cleanUpBlendFunction);
        }
    }
}
