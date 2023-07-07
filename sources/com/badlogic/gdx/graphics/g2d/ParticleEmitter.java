package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Iterator;

public class ParticleEmitter {
    private static final int UPDATE_ANGLE = 2;
    private static final int UPDATE_GRAVITY = 32;
    private static final int UPDATE_ROTATION = 4;
    private static final int UPDATE_SCALE = 1;
    private static final int UPDATE_SPRITE = 128;
    private static final int UPDATE_TINT = 64;
    private static final int UPDATE_VELOCITY = 8;
    private static final int UPDATE_WIND = 16;
    private float accumulator;
    private boolean[] active;
    private int activeCount;
    private boolean additive = true;
    private boolean aligned;
    private boolean allowCompletion;
    private ScaledNumericValue angleValue = new ScaledNumericValue();
    private boolean attached;
    private boolean behind;
    private BoundingBox bounds;
    boolean cleansUpBlendFunction = true;
    private boolean continuous;
    private float delay;
    private float delayTimer;
    private RangedNumericValue delayValue = new RangedNumericValue();
    public float duration = 1.0f;
    public float durationTimer;
    private RangedNumericValue durationValue = new RangedNumericValue();
    private int emission;
    private int emissionDelta;
    private int emissionDiff;
    private ScaledNumericValue emissionValue = new ScaledNumericValue();
    private boolean firstUpdate;
    private boolean flipX;
    private boolean flipY;
    private ScaledNumericValue gravityValue = new ScaledNumericValue();
    private Array<String> imagePaths;
    private int life;
    private int lifeDiff;
    private int lifeOffset;
    private int lifeOffsetDiff;
    private IndependentScaledNumericValue lifeOffsetValue = new IndependentScaledNumericValue();
    private IndependentScaledNumericValue lifeValue = new IndependentScaledNumericValue();
    private int maxParticleCount = 4;
    private int minParticleCount;
    private RangedNumericValue[] motionValues;
    private String name;
    private Particle[] particles;
    private boolean premultipliedAlpha = false;
    private ScaledNumericValue rotationValue = new ScaledNumericValue();
    private float spawnHeight;
    private float spawnHeightDiff;
    private ScaledNumericValue spawnHeightValue = new ScaledNumericValue();
    private SpawnShapeValue spawnShapeValue = new SpawnShapeValue();
    private float spawnWidth;
    private float spawnWidthDiff;
    private ScaledNumericValue spawnWidthValue = new ScaledNumericValue();
    private SpriteMode spriteMode = SpriteMode.single;
    private Array<Sprite> sprites;
    private GradientColorValue tintValue = new GradientColorValue();
    private ScaledNumericValue transparencyValue = new ScaledNumericValue();
    private int updateFlags;
    private ScaledNumericValue velocityValue = new ScaledNumericValue();
    private ScaledNumericValue windValue = new ScaledNumericValue();
    private float x;
    private RangedNumericValue xOffsetValue = new ScaledNumericValue();
    private ScaledNumericValue xScaleValue = new ScaledNumericValue();
    private RangedNumericValue[] xSizeValues;
    private float y;
    private RangedNumericValue yOffsetValue = new ScaledNumericValue();
    private ScaledNumericValue yScaleValue = new ScaledNumericValue();
    private RangedNumericValue[] ySizeValues;

    public enum SpawnEllipseSide {
        both,
        top,
        bottom
    }

    public enum SpawnShape {
        point,
        line,
        square,
        ellipse
    }

    public enum SpriteMode {
        single,
        random,
        animated
    }

    public ParticleEmitter() {
        initialize();
    }

    public ParticleEmitter(BufferedReader reader) throws IOException {
        initialize();
        load(reader);
    }

    public ParticleEmitter(ParticleEmitter emitter) {
        this.sprites = new Array<>(emitter.sprites);
        this.name = emitter.name;
        this.imagePaths = new Array<>(emitter.imagePaths);
        setMaxParticleCount(emitter.maxParticleCount);
        this.minParticleCount = emitter.minParticleCount;
        this.delayValue.load(emitter.delayValue);
        this.durationValue.load(emitter.durationValue);
        this.emissionValue.load(emitter.emissionValue);
        this.lifeValue.load(emitter.lifeValue);
        this.lifeOffsetValue.load(emitter.lifeOffsetValue);
        this.xScaleValue.load(emitter.xScaleValue);
        this.yScaleValue.load(emitter.yScaleValue);
        this.rotationValue.load(emitter.rotationValue);
        this.velocityValue.load(emitter.velocityValue);
        this.angleValue.load(emitter.angleValue);
        this.windValue.load(emitter.windValue);
        this.gravityValue.load(emitter.gravityValue);
        this.transparencyValue.load(emitter.transparencyValue);
        this.tintValue.load(emitter.tintValue);
        this.xOffsetValue.load(emitter.xOffsetValue);
        this.yOffsetValue.load(emitter.yOffsetValue);
        this.spawnWidthValue.load(emitter.spawnWidthValue);
        this.spawnHeightValue.load(emitter.spawnHeightValue);
        this.spawnShapeValue.load(emitter.spawnShapeValue);
        this.attached = emitter.attached;
        this.continuous = emitter.continuous;
        this.aligned = emitter.aligned;
        this.behind = emitter.behind;
        this.additive = emitter.additive;
        this.premultipliedAlpha = emitter.premultipliedAlpha;
        this.cleansUpBlendFunction = emitter.cleansUpBlendFunction;
        this.spriteMode = emitter.spriteMode;
        setPosition(emitter.getX(), emitter.getY());
    }

    private void initialize() {
        this.sprites = new Array<>();
        this.imagePaths = new Array<>();
        this.durationValue.setAlwaysActive(true);
        this.emissionValue.setAlwaysActive(true);
        this.lifeValue.setAlwaysActive(true);
        this.xScaleValue.setAlwaysActive(true);
        this.transparencyValue.setAlwaysActive(true);
        this.spawnShapeValue.setAlwaysActive(true);
        this.spawnWidthValue.setAlwaysActive(true);
        this.spawnHeightValue.setAlwaysActive(true);
    }

    public void setMaxParticleCount(int maxParticleCount2) {
        this.maxParticleCount = maxParticleCount2;
        this.active = new boolean[maxParticleCount2];
        this.activeCount = 0;
        this.particles = new Particle[maxParticleCount2];
    }

    public void addParticle() {
        int activeCount2 = this.activeCount;
        if (activeCount2 != this.maxParticleCount) {
            boolean[] active2 = this.active;
            int n = active2.length;
            for (int i = 0; i < n; i++) {
                if (!active2[i]) {
                    activateParticle(i);
                    active2[i] = true;
                    this.activeCount = activeCount2 + 1;
                    return;
                }
            }
        }
    }

    public void addParticles(int count) {
        int count2 = Math.min(count, this.maxParticleCount - this.activeCount);
        if (count2 != 0) {
            boolean[] active2 = this.active;
            int index = 0;
            int n = active2.length;
            int i = 0;
            loop0:
            while (i < count2) {
                while (index < n) {
                    if (!active2[index]) {
                        activateParticle(index);
                        active2[index] = true;
                        i++;
                        index++;
                    } else {
                        index++;
                    }
                }
                break loop0;
            }
            this.activeCount += count2;
        }
    }

    public void update(float delta) {
        this.accumulator += delta * 1000.0f;
        float f = this.accumulator;
        if (f >= 1.0f) {
            int deltaMillis = (int) f;
            this.accumulator = f - ((float) deltaMillis);
            float f2 = this.delayTimer;
            if (f2 < this.delay) {
                this.delayTimer = f2 + ((float) deltaMillis);
            } else {
                boolean done = false;
                if (this.firstUpdate) {
                    this.firstUpdate = false;
                    addParticle();
                }
                float f3 = this.durationTimer;
                if (f3 < this.duration) {
                    this.durationTimer = f3 + ((float) deltaMillis);
                } else if (!this.continuous || this.allowCompletion) {
                    done = true;
                } else {
                    restart();
                }
                if (!done) {
                    this.emissionDelta += deltaMillis;
                    float emissionTime = ((float) this.emission) + (((float) this.emissionDiff) * this.emissionValue.getScale(this.durationTimer / this.duration));
                    if (emissionTime > 0.0f) {
                        float emissionTime2 = 1000.0f / emissionTime;
                        int i = this.emissionDelta;
                        if (((float) i) >= emissionTime2) {
                            int emitCount = Math.min((int) (((float) i) / emissionTime2), this.maxParticleCount - this.activeCount);
                            this.emissionDelta = (int) (((float) this.emissionDelta) - (((float) emitCount) * emissionTime2));
                            this.emissionDelta = (int) (((float) this.emissionDelta) % emissionTime2);
                            addParticles(emitCount);
                        }
                    }
                    int emitCount2 = this.activeCount;
                    int i2 = this.minParticleCount;
                    if (emitCount2 < i2) {
                        addParticles(i2 - emitCount2);
                    }
                }
            }
            boolean[] active2 = this.active;
            int activeCount2 = this.activeCount;
            Particle[] particles2 = this.particles;
            int n = active2.length;
            for (int i3 = 0; i3 < n; i3++) {
                if (active2[i3] && !updateParticle(particles2[i3], delta, deltaMillis)) {
                    active2[i3] = false;
                    activeCount2--;
                }
            }
            this.activeCount = activeCount2;
        }
    }

    public void draw(Batch batch) {
        if (this.premultipliedAlpha) {
            batch.setBlendFunction(1, GL20.GL_ONE_MINUS_SRC_ALPHA);
        } else if (this.additive) {
            batch.setBlendFunction(GL20.GL_SRC_ALPHA, 1);
        } else {
            batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }
        Particle[] particles2 = this.particles;
        boolean[] active2 = this.active;
        int n = active2.length;
        for (int i = 0; i < n; i++) {
            if (active2[i]) {
                particles2[i].draw(batch);
            }
        }
        if (this.cleansUpBlendFunction == 0) {
            return;
        }
        if (this.additive || this.premultipliedAlpha) {
            batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }
    }

    public void draw(Batch batch, float delta) {
        this.accumulator += delta * 1000.0f;
        float f = this.accumulator;
        if (f < 1.0f) {
            draw(batch);
            return;
        }
        int deltaMillis = (int) f;
        this.accumulator = f - ((float) deltaMillis);
        if (this.premultipliedAlpha) {
            batch.setBlendFunction(1, GL20.GL_ONE_MINUS_SRC_ALPHA);
        } else if (this.additive) {
            batch.setBlendFunction(GL20.GL_SRC_ALPHA, 1);
        } else {
            batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }
        Particle[] particles2 = this.particles;
        boolean[] active2 = this.active;
        int activeCount2 = this.activeCount;
        int n = active2.length;
        for (int i = 0; i < n; i++) {
            if (active2[i]) {
                Particle particle = particles2[i];
                if (updateParticle(particle, delta, deltaMillis)) {
                    particle.draw(batch);
                } else {
                    active2[i] = false;
                    activeCount2--;
                }
            }
        }
        this.activeCount = activeCount2;
        if (this.cleansUpBlendFunction && (this.additive || this.premultipliedAlpha)) {
            batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }
        float f2 = this.delayTimer;
        if (f2 < this.delay) {
            this.delayTimer = f2 + ((float) deltaMillis);
            return;
        }
        if (this.firstUpdate) {
            this.firstUpdate = false;
            addParticle();
        }
        float f3 = this.durationTimer;
        if (f3 < this.duration) {
            this.durationTimer = f3 + ((float) deltaMillis);
        } else if (this.continuous && !this.allowCompletion) {
            restart();
        } else {
            return;
        }
        this.emissionDelta += deltaMillis;
        float emissionTime = ((float) this.emission) + (((float) this.emissionDiff) * this.emissionValue.getScale(this.durationTimer / this.duration));
        if (emissionTime > 0.0f) {
            float emissionTime2 = 1000.0f / emissionTime;
            int i2 = this.emissionDelta;
            if (((float) i2) >= emissionTime2) {
                int emitCount = Math.min((int) (((float) i2) / emissionTime2), this.maxParticleCount - activeCount2);
                this.emissionDelta = (int) (((float) this.emissionDelta) - (((float) emitCount) * emissionTime2));
                this.emissionDelta = (int) (((float) this.emissionDelta) % emissionTime2);
                addParticles(emitCount);
            }
        }
        int emitCount2 = this.minParticleCount;
        if (activeCount2 < emitCount2) {
            addParticles(emitCount2 - activeCount2);
        }
    }

    public void start() {
        this.firstUpdate = true;
        this.allowCompletion = false;
        restart();
    }

    public void reset() {
        this.emissionDelta = 0;
        this.durationTimer = this.duration;
        boolean[] active2 = this.active;
        int n = active2.length;
        for (int i = 0; i < n; i++) {
            active2[i] = false;
        }
        this.activeCount = 0;
        start();
    }

    private void restart() {
        this.delay = this.delayValue.active ? this.delayValue.newLowValue() : 0.0f;
        this.delayTimer = 0.0f;
        this.durationTimer -= this.duration;
        this.duration = this.durationValue.newLowValue();
        this.emission = (int) this.emissionValue.newLowValue();
        this.emissionDiff = (int) this.emissionValue.newHighValue();
        if (!this.emissionValue.isRelative()) {
            this.emissionDiff -= this.emission;
        }
        if (!this.lifeValue.independent) {
            generateLifeValues();
        }
        if (!this.lifeOffsetValue.independent) {
            generateLifeOffsetValues();
        }
        this.spawnWidth = this.spawnWidthValue.newLowValue();
        this.spawnWidthDiff = this.spawnWidthValue.newHighValue();
        if (!this.spawnWidthValue.isRelative()) {
            this.spawnWidthDiff -= this.spawnWidth;
        }
        this.spawnHeight = this.spawnHeightValue.newLowValue();
        this.spawnHeightDiff = this.spawnHeightValue.newHighValue();
        if (!this.spawnHeightValue.isRelative()) {
            this.spawnHeightDiff -= this.spawnHeight;
        }
        this.updateFlags = 0;
        if (this.angleValue.active && this.angleValue.timeline.length > 1) {
            this.updateFlags |= 2;
        }
        if (this.velocityValue.active) {
            this.updateFlags |= 8;
        }
        if (this.xScaleValue.timeline.length > 1) {
            this.updateFlags |= 1;
        }
        if (this.yScaleValue.active && this.yScaleValue.timeline.length > 1) {
            this.updateFlags |= 1;
        }
        if (this.rotationValue.active && this.rotationValue.timeline.length > 1) {
            this.updateFlags |= 4;
        }
        if (this.windValue.active) {
            this.updateFlags |= 16;
        }
        if (this.gravityValue.active) {
            this.updateFlags |= 32;
        }
        if (this.tintValue.timeline.length > 1) {
            this.updateFlags |= 64;
        }
        if (this.spriteMode == SpriteMode.animated) {
            this.updateFlags |= 128;
        }
    }

    /* access modifiers changed from: protected */
    public Particle newParticle(Sprite sprite) {
        return new Particle(sprite);
    }

    /* access modifiers changed from: protected */
    public Particle[] getParticles() {
        return this.particles;
    }

    private void activateParticle(int index) {
        float scaleY;
        float px;
        float py;
        float spawnAngle;
        Sprite sprite = null;
        int i = AnonymousClass1.$SwitchMap$com$badlogic$gdx$graphics$g2d$ParticleEmitter$SpriteMode[this.spriteMode.ordinal()];
        if (i == 1 || i == 2) {
            sprite = this.sprites.first();
        } else if (i == 3) {
            sprite = this.sprites.random();
        }
        Particle[] particleArr = this.particles;
        Particle particle = particleArr[index];
        if (particle == null) {
            Particle newParticle = newParticle(sprite);
            particle = newParticle;
            particleArr[index] = newParticle;
            particle.flip(this.flipX, this.flipY);
        } else {
            particle.set(sprite);
        }
        float percent = this.durationTimer / this.duration;
        int updateFlags2 = this.updateFlags;
        if (this.lifeValue.independent) {
            generateLifeValues();
        }
        if (this.lifeOffsetValue.independent) {
            generateLifeOffsetValues();
        }
        int scale = this.life + ((int) (((float) this.lifeDiff) * this.lifeValue.getScale(percent)));
        particle.life = scale;
        particle.currentLife = scale;
        if (this.velocityValue.active) {
            particle.velocity = this.velocityValue.newLowValue();
            particle.velocityDiff = this.velocityValue.newHighValue();
            if (!this.velocityValue.isRelative()) {
                particle.velocityDiff -= particle.velocity;
            }
        }
        particle.angle = this.angleValue.newLowValue();
        particle.angleDiff = this.angleValue.newHighValue();
        if (!this.angleValue.isRelative()) {
            particle.angleDiff -= particle.angle;
        }
        float angle = 0.0f;
        if ((updateFlags2 & 2) == 0) {
            angle = particle.angle + (particle.angleDiff * this.angleValue.getScale(0.0f));
            particle.angle = angle;
            particle.angleCos = MathUtils.cosDeg(angle);
            particle.angleSin = MathUtils.sinDeg(angle);
        }
        float spriteWidth = sprite.getWidth();
        float spriteHeight = sprite.getHeight();
        particle.xScale = this.xScaleValue.newLowValue() / spriteWidth;
        particle.xScaleDiff = this.xScaleValue.newHighValue() / spriteWidth;
        if (!this.xScaleValue.isRelative()) {
            particle.xScaleDiff -= particle.xScale;
        }
        if (this.yScaleValue.active) {
            particle.yScale = this.yScaleValue.newLowValue() / spriteHeight;
            particle.yScaleDiff = this.yScaleValue.newHighValue() / spriteHeight;
            if (!this.yScaleValue.isRelative()) {
                particle.yScaleDiff -= particle.yScale;
            }
            particle.setScale(particle.xScale + (particle.xScaleDiff * this.xScaleValue.getScale(0.0f)), particle.yScale + (particle.yScaleDiff * this.yScaleValue.getScale(0.0f)));
        } else {
            particle.setScale(particle.xScale + (particle.xScaleDiff * this.xScaleValue.getScale(0.0f)));
        }
        if (this.rotationValue.active) {
            particle.rotation = this.rotationValue.newLowValue();
            particle.rotationDiff = this.rotationValue.newHighValue();
            if (!this.rotationValue.isRelative()) {
                particle.rotationDiff -= particle.rotation;
            }
            float rotation = particle.rotation + (particle.rotationDiff * this.rotationValue.getScale(0.0f));
            if (this.aligned) {
                rotation += angle;
            }
            particle.setRotation(rotation);
        }
        if (this.windValue.active) {
            particle.wind = this.windValue.newLowValue();
            particle.windDiff = this.windValue.newHighValue();
            if (!this.windValue.isRelative()) {
                particle.windDiff -= particle.wind;
            }
        }
        if (this.gravityValue.active) {
            particle.gravity = this.gravityValue.newLowValue();
            particle.gravityDiff = this.gravityValue.newHighValue();
            if (!this.gravityValue.isRelative()) {
                particle.gravityDiff -= particle.gravity;
            }
        }
        float[] color = particle.tint;
        if (color == null) {
            float[] fArr = new float[3];
            color = fArr;
            particle.tint = fArr;
        }
        float[] temp = this.tintValue.getColor(0.0f);
        color[0] = temp[0];
        color[1] = temp[1];
        color[2] = temp[2];
        particle.transparency = this.transparencyValue.newLowValue();
        particle.transparencyDiff = this.transparencyValue.newHighValue() - particle.transparency;
        float x2 = this.x;
        if (this.xOffsetValue.active) {
            x2 += this.xOffsetValue.newLowValue();
        }
        float y2 = this.y;
        if (this.yOffsetValue.active) {
            y2 += this.yOffsetValue.newLowValue();
        }
        int i2 = AnonymousClass1.$SwitchMap$com$badlogic$gdx$graphics$g2d$ParticleEmitter$SpawnShape[this.spawnShapeValue.shape.ordinal()];
        if (i2 != 1) {
            if (i2 == 2) {
                float width = this.spawnWidth + (this.spawnWidthDiff * this.spawnWidthValue.getScale(percent));
                float height = this.spawnHeight + (this.spawnHeightDiff * this.spawnHeightValue.getScale(percent));
                float radiusX = width / 2.0f;
                float radiusY = height / 2.0f;
                if (radiusX == 0.0f) {
                    float f = height;
                    float f2 = angle;
                } else if (radiusY == 0.0f) {
                    Sprite sprite2 = sprite;
                    float f3 = angle;
                } else {
                    float scaleY2 = radiusX / radiusY;
                    Sprite sprite3 = sprite;
                    if (this.spawnShapeValue.edges) {
                        float f4 = height;
                        int i3 = AnonymousClass1.$SwitchMap$com$badlogic$gdx$graphics$g2d$ParticleEmitter$SpawnEllipseSide[this.spawnShapeValue.side.ordinal()];
                        if (i3 == 1) {
                            spawnAngle = -MathUtils.random(179.0f);
                        } else if (i3 != 2) {
                            spawnAngle = MathUtils.random(360.0f);
                        } else {
                            spawnAngle = MathUtils.random(179.0f);
                        }
                        float cosDeg = MathUtils.cosDeg(spawnAngle);
                        float f5 = angle;
                        float angle2 = MathUtils.sinDeg(spawnAngle);
                        x2 += cosDeg * radiusX;
                        y2 += (angle2 * radiusX) / scaleY2;
                        if ((updateFlags2 & 2) == 0) {
                            particle.angle = spawnAngle;
                            particle.angleCos = cosDeg;
                            particle.angleSin = angle2;
                        }
                        scaleY = 2.0f;
                    } else {
                        float f6 = angle;
                        float radius2 = radiusX * radiusX;
                        do {
                            px = MathUtils.random(width) - radiusX;
                            py = MathUtils.random(width) - radiusX;
                        } while ((px * px) + (py * py) > radius2);
                        x2 += px;
                        y2 += py / scaleY2;
                        scaleY = 2.0f;
                    }
                }
            } else if (i2 != 3) {
                Sprite sprite4 = sprite;
                float f7 = angle;
            } else {
                float width2 = this.spawnWidth + (this.spawnWidthDiff * this.spawnWidthValue.getScale(percent));
                float height2 = this.spawnHeight + (this.spawnHeightDiff * this.spawnHeightValue.getScale(percent));
                if (width2 != 0.0f) {
                    float lineX = MathUtils.random() * width2;
                    x2 += lineX;
                    y2 += (height2 / width2) * lineX;
                    Sprite sprite5 = sprite;
                    float f8 = angle;
                    scaleY = 2.0f;
                } else {
                    y2 += MathUtils.random() * height2;
                    Sprite sprite6 = sprite;
                    float f9 = angle;
                    scaleY = 2.0f;
                }
            }
            scaleY = 2.0f;
        } else {
            float f10 = angle;
            float width3 = this.spawnWidth + (this.spawnWidthDiff * this.spawnWidthValue.getScale(percent));
            float height3 = this.spawnHeight + (this.spawnHeightDiff * this.spawnHeightValue.getScale(percent));
            scaleY = 2.0f;
            x2 += MathUtils.random(width3) - (width3 / 2.0f);
            y2 += MathUtils.random(height3) - (height3 / 2.0f);
        }
        particle.setBounds(x2 - (spriteWidth / scaleY), y2 - (spriteHeight / scaleY), spriteWidth, spriteHeight);
        int offsetTime = (int) (((float) this.lifeOffset) + (((float) this.lifeOffsetDiff) * this.lifeOffsetValue.getScale(percent)));
        if (offsetTime > 0) {
            if (offsetTime >= particle.currentLife) {
                offsetTime = particle.currentLife - 1;
            }
            updateParticle(particle, ((float) offsetTime) / 1000.0f, offsetTime);
        }
    }

    /* renamed from: com.badlogic.gdx.graphics.g2d.ParticleEmitter$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$badlogic$gdx$graphics$g2d$ParticleEmitter$SpawnEllipseSide = new int[SpawnEllipseSide.values().length];
        static final /* synthetic */ int[] $SwitchMap$com$badlogic$gdx$graphics$g2d$ParticleEmitter$SpawnShape = new int[SpawnShape.values().length];
        static final /* synthetic */ int[] $SwitchMap$com$badlogic$gdx$graphics$g2d$ParticleEmitter$SpriteMode = new int[SpriteMode.values().length];

        static {
            try {
                $SwitchMap$com$badlogic$gdx$graphics$g2d$ParticleEmitter$SpawnShape[SpawnShape.square.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$badlogic$gdx$graphics$g2d$ParticleEmitter$SpawnShape[SpawnShape.ellipse.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$badlogic$gdx$graphics$g2d$ParticleEmitter$SpawnShape[SpawnShape.line.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$badlogic$gdx$graphics$g2d$ParticleEmitter$SpawnEllipseSide[SpawnEllipseSide.top.ordinal()] = 1;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$badlogic$gdx$graphics$g2d$ParticleEmitter$SpawnEllipseSide[SpawnEllipseSide.bottom.ordinal()] = 2;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$badlogic$gdx$graphics$g2d$ParticleEmitter$SpriteMode[SpriteMode.single.ordinal()] = 1;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$badlogic$gdx$graphics$g2d$ParticleEmitter$SpriteMode[SpriteMode.animated.ordinal()] = 2;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$badlogic$gdx$graphics$g2d$ParticleEmitter$SpriteMode[SpriteMode.random.ordinal()] = 3;
            } catch (NoSuchFieldError e8) {
            }
        }
    }

    private boolean updateParticle(Particle particle, float delta, int deltaMillis) {
        float[] color;
        int frame;
        float velocityY;
        float velocityX;
        int life2 = particle.currentLife - deltaMillis;
        if (life2 <= 0) {
            return false;
        }
        particle.currentLife = life2;
        float alphaMultiplier = 1.0f;
        float percent = 1.0f - (((float) particle.currentLife) / ((float) particle.life));
        int updateFlags2 = this.updateFlags;
        if ((updateFlags2 & 1) != 0) {
            if (this.yScaleValue.active) {
                particle.setScale(particle.xScale + (particle.xScaleDiff * this.xScaleValue.getScale(percent)), particle.yScale + (particle.yScaleDiff * this.yScaleValue.getScale(percent)));
            } else {
                particle.setScale(particle.xScale + (particle.xScaleDiff * this.xScaleValue.getScale(percent)));
            }
        }
        if ((updateFlags2 & 8) != 0) {
            float velocity = (particle.velocity + (particle.velocityDiff * this.velocityValue.getScale(percent))) * delta;
            if ((updateFlags2 & 2) != 0) {
                float angle = particle.angle + (particle.angleDiff * this.angleValue.getScale(percent));
                velocityX = MathUtils.cosDeg(angle) * velocity;
                velocityY = MathUtils.sinDeg(angle) * velocity;
                if ((updateFlags2 & 4) != 0) {
                    float rotation = particle.rotation + (particle.rotationDiff * this.rotationValue.getScale(percent));
                    if (this.aligned) {
                        rotation += angle;
                    }
                    particle.setRotation(rotation);
                }
            } else {
                velocityX = velocity * particle.angleCos;
                velocityY = velocity * particle.angleSin;
                if (this.aligned || (updateFlags2 & 4) != 0) {
                    float rotation2 = particle.rotation + (particle.rotationDiff * this.rotationValue.getScale(percent));
                    if (this.aligned) {
                        rotation2 += particle.angle;
                    }
                    particle.setRotation(rotation2);
                }
            }
            if ((updateFlags2 & 16) != 0) {
                velocityX += (particle.wind + (particle.windDiff * this.windValue.getScale(percent))) * delta;
            }
            if ((updateFlags2 & 32) != 0) {
                velocityY += (particle.gravity + (particle.gravityDiff * this.gravityValue.getScale(percent))) * delta;
            }
            particle.translate(velocityX, velocityY);
        } else if ((updateFlags2 & 4) != 0) {
            particle.setRotation(particle.rotation + (particle.rotationDiff * this.rotationValue.getScale(percent)));
        }
        if ((updateFlags2 & 64) != 0) {
            color = this.tintValue.getColor(percent);
        } else {
            color = particle.tint;
        }
        if (this.premultipliedAlpha) {
            if (this.additive) {
                alphaMultiplier = 0.0f;
            }
            float a = particle.transparency + (particle.transparencyDiff * this.transparencyValue.getScale(percent));
            particle.setColor(color[0] * a, color[1] * a, color[2] * a, a * alphaMultiplier);
        } else {
            particle.setColor(color[0], color[1], color[2], particle.transparency + (particle.transparencyDiff * this.transparencyValue.getScale(percent)));
        }
        if (!((updateFlags2 & 128) == 0 || particle.frame == (frame = Math.min((int) (((float) this.sprites.size) * percent), this.sprites.size - 1)))) {
            Sprite sprite = this.sprites.get(frame);
            float prevSpriteWidth = particle.getWidth();
            float prevSpriteHeight = particle.getHeight();
            particle.setRegion((TextureRegion) sprite);
            particle.setSize(sprite.getWidth(), sprite.getHeight());
            particle.setOrigin(sprite.getOriginX(), sprite.getOriginY());
            particle.translate((prevSpriteWidth - sprite.getWidth()) / 2.0f, (prevSpriteHeight - sprite.getHeight()) / 2.0f);
            particle.frame = frame;
        }
        return true;
    }

    private void generateLifeValues() {
        this.life = (int) this.lifeValue.newLowValue();
        this.lifeDiff = (int) this.lifeValue.newHighValue();
        if (!this.lifeValue.isRelative()) {
            this.lifeDiff -= this.life;
        }
    }

    private void generateLifeOffsetValues() {
        this.lifeOffset = this.lifeOffsetValue.active ? (int) this.lifeOffsetValue.newLowValue() : 0;
        this.lifeOffsetDiff = (int) this.lifeOffsetValue.newHighValue();
        if (!this.lifeOffsetValue.isRelative()) {
            this.lifeOffsetDiff -= this.lifeOffset;
        }
    }

    public void setPosition(float x2, float y2) {
        if (this.attached) {
            float xAmount = x2 - this.x;
            float yAmount = y2 - this.y;
            boolean[] active2 = this.active;
            int n = active2.length;
            for (int i = 0; i < n; i++) {
                if (active2[i]) {
                    this.particles[i].translate(xAmount, yAmount);
                }
            }
        }
        this.x = x2;
        this.y = y2;
    }

    public void setSprites(Array<Sprite> sprites2) {
        this.sprites = sprites2;
        if (sprites2.size != 0) {
            int i = 0;
            int n = this.particles.length;
            while (i < n) {
                Particle particle = this.particles[i];
                if (particle != null) {
                    Sprite sprite = null;
                    int i2 = AnonymousClass1.$SwitchMap$com$badlogic$gdx$graphics$g2d$ParticleEmitter$SpriteMode[this.spriteMode.ordinal()];
                    if (i2 == 1) {
                        sprite = sprites2.first();
                    } else if (i2 == 2) {
                        particle.frame = Math.min((int) (((float) sprites2.size) * (1.0f - (((float) particle.currentLife) / ((float) particle.life)))), sprites2.size - 1);
                        sprite = sprites2.get(particle.frame);
                    } else if (i2 == 3) {
                        sprite = sprites2.random();
                    }
                    particle.setRegion((TextureRegion) sprite);
                    particle.setOrigin(sprite.getOriginX(), sprite.getOriginY());
                    i++;
                } else {
                    return;
                }
            }
        }
    }

    public void setSpriteMode(SpriteMode spriteMode2) {
        this.spriteMode = spriteMode2;
    }

    public void allowCompletion() {
        this.allowCompletion = true;
        this.durationTimer = this.duration;
    }

    public Array<Sprite> getSprites() {
        return this.sprites;
    }

    public SpriteMode getSpriteMode() {
        return this.spriteMode;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name2) {
        this.name = name2;
    }

    public ScaledNumericValue getLife() {
        return this.lifeValue;
    }

    public ScaledNumericValue getXScale() {
        return this.xScaleValue;
    }

    public ScaledNumericValue getYScale() {
        return this.yScaleValue;
    }

    public ScaledNumericValue getRotation() {
        return this.rotationValue;
    }

    public GradientColorValue getTint() {
        return this.tintValue;
    }

    public ScaledNumericValue getVelocity() {
        return this.velocityValue;
    }

    public ScaledNumericValue getWind() {
        return this.windValue;
    }

    public ScaledNumericValue getGravity() {
        return this.gravityValue;
    }

    public ScaledNumericValue getAngle() {
        return this.angleValue;
    }

    public ScaledNumericValue getEmission() {
        return this.emissionValue;
    }

    public ScaledNumericValue getTransparency() {
        return this.transparencyValue;
    }

    public RangedNumericValue getDuration() {
        return this.durationValue;
    }

    public RangedNumericValue getDelay() {
        return this.delayValue;
    }

    public ScaledNumericValue getLifeOffset() {
        return this.lifeOffsetValue;
    }

    public RangedNumericValue getXOffsetValue() {
        return this.xOffsetValue;
    }

    public RangedNumericValue getYOffsetValue() {
        return this.yOffsetValue;
    }

    public ScaledNumericValue getSpawnWidth() {
        return this.spawnWidthValue;
    }

    public ScaledNumericValue getSpawnHeight() {
        return this.spawnHeightValue;
    }

    public SpawnShapeValue getSpawnShape() {
        return this.spawnShapeValue;
    }

    public boolean isAttached() {
        return this.attached;
    }

    public void setAttached(boolean attached2) {
        this.attached = attached2;
    }

    public boolean isContinuous() {
        return this.continuous;
    }

    public void setContinuous(boolean continuous2) {
        this.continuous = continuous2;
    }

    public boolean isAligned() {
        return this.aligned;
    }

    public void setAligned(boolean aligned2) {
        this.aligned = aligned2;
    }

    public boolean isAdditive() {
        return this.additive;
    }

    public void setAdditive(boolean additive2) {
        this.additive = additive2;
    }

    public boolean cleansUpBlendFunction() {
        return this.cleansUpBlendFunction;
    }

    public void setCleansUpBlendFunction(boolean cleansUpBlendFunction2) {
        this.cleansUpBlendFunction = cleansUpBlendFunction2;
    }

    public boolean isBehind() {
        return this.behind;
    }

    public void setBehind(boolean behind2) {
        this.behind = behind2;
    }

    public boolean isPremultipliedAlpha() {
        return this.premultipliedAlpha;
    }

    public void setPremultipliedAlpha(boolean premultipliedAlpha2) {
        this.premultipliedAlpha = premultipliedAlpha2;
    }

    public int getMinParticleCount() {
        return this.minParticleCount;
    }

    public void setMinParticleCount(int minParticleCount2) {
        this.minParticleCount = minParticleCount2;
    }

    public int getMaxParticleCount() {
        return this.maxParticleCount;
    }

    public boolean isComplete() {
        if ((!this.continuous || this.allowCompletion) && this.delayTimer >= this.delay && this.durationTimer >= this.duration && this.activeCount == 0) {
            return true;
        }
        return false;
    }

    public float getPercentComplete() {
        if (this.delayTimer < this.delay) {
            return 0.0f;
        }
        return Math.min(1.0f, this.durationTimer / this.duration);
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public int getActiveCount() {
        return this.activeCount;
    }

    public Array<String> getImagePaths() {
        return this.imagePaths;
    }

    public void setImagePaths(Array<String> imagePaths2) {
        this.imagePaths = imagePaths2;
    }

    public void setFlip(boolean flipX2, boolean flipY2) {
        this.flipX = flipX2;
        this.flipY = flipY2;
        Particle[] particleArr = this.particles;
        if (particleArr != null) {
            int n = particleArr.length;
            for (int i = 0; i < n; i++) {
                Particle particle = this.particles[i];
                if (particle != null) {
                    particle.flip(flipX2, flipY2);
                }
            }
        }
    }

    public void flipY() {
        ScaledNumericValue scaledNumericValue = this.angleValue;
        scaledNumericValue.setHigh(-scaledNumericValue.getHighMin(), -this.angleValue.getHighMax());
        ScaledNumericValue scaledNumericValue2 = this.angleValue;
        scaledNumericValue2.setLow(-scaledNumericValue2.getLowMin(), -this.angleValue.getLowMax());
        ScaledNumericValue scaledNumericValue3 = this.gravityValue;
        scaledNumericValue3.setHigh(-scaledNumericValue3.getHighMin(), -this.gravityValue.getHighMax());
        ScaledNumericValue scaledNumericValue4 = this.gravityValue;
        scaledNumericValue4.setLow(-scaledNumericValue4.getLowMin(), -this.gravityValue.getLowMax());
        ScaledNumericValue scaledNumericValue5 = this.windValue;
        scaledNumericValue5.setHigh(-scaledNumericValue5.getHighMin(), -this.windValue.getHighMax());
        ScaledNumericValue scaledNumericValue6 = this.windValue;
        scaledNumericValue6.setLow(-scaledNumericValue6.getLowMin(), -this.windValue.getLowMax());
        ScaledNumericValue scaledNumericValue7 = this.rotationValue;
        scaledNumericValue7.setHigh(-scaledNumericValue7.getHighMin(), -this.rotationValue.getHighMax());
        ScaledNumericValue scaledNumericValue8 = this.rotationValue;
        scaledNumericValue8.setLow(-scaledNumericValue8.getLowMin(), -this.rotationValue.getLowMax());
        RangedNumericValue rangedNumericValue = this.yOffsetValue;
        rangedNumericValue.setLow(-rangedNumericValue.getLowMin(), -this.yOffsetValue.getLowMax());
    }

    public BoundingBox getBoundingBox() {
        if (this.bounds == null) {
            this.bounds = new BoundingBox();
        }
        Particle[] particles2 = this.particles;
        boolean[] active2 = this.active;
        BoundingBox bounds2 = this.bounds;
        bounds2.inf();
        int n = active2.length;
        for (int i = 0; i < n; i++) {
            if (active2[i]) {
                Rectangle r = particles2[i].getBoundingRectangle();
                bounds2.ext(r.x, r.y, 0.0f);
                bounds2.ext(r.x + r.width, r.y + r.height, 0.0f);
            }
        }
        return bounds2;
    }

    /* access modifiers changed from: protected */
    public RangedNumericValue[] getXSizeValues() {
        if (this.xSizeValues == null) {
            this.xSizeValues = new RangedNumericValue[3];
            RangedNumericValue[] rangedNumericValueArr = this.xSizeValues;
            rangedNumericValueArr[0] = this.xScaleValue;
            rangedNumericValueArr[1] = this.spawnWidthValue;
            rangedNumericValueArr[2] = this.xOffsetValue;
        }
        return this.xSizeValues;
    }

    /* access modifiers changed from: protected */
    public RangedNumericValue[] getYSizeValues() {
        if (this.ySizeValues == null) {
            this.ySizeValues = new RangedNumericValue[3];
            RangedNumericValue[] rangedNumericValueArr = this.ySizeValues;
            rangedNumericValueArr[0] = this.yScaleValue;
            rangedNumericValueArr[1] = this.spawnHeightValue;
            rangedNumericValueArr[2] = this.yOffsetValue;
        }
        return this.ySizeValues;
    }

    /* access modifiers changed from: protected */
    public RangedNumericValue[] getMotionValues() {
        if (this.motionValues == null) {
            this.motionValues = new RangedNumericValue[3];
            RangedNumericValue[] rangedNumericValueArr = this.motionValues;
            rangedNumericValueArr[0] = this.velocityValue;
            rangedNumericValueArr[1] = this.windValue;
            rangedNumericValueArr[2] = this.gravityValue;
        }
        return this.motionValues;
    }

    public void scaleSize(float scale) {
        if (scale != 1.0f) {
            scaleSize(scale, scale);
        }
    }

    public void scaleSize(float scaleX, float scaleY) {
        if (scaleX != 1.0f || scaleY != 1.0f) {
            for (RangedNumericValue value : getXSizeValues()) {
                value.scale(scaleX);
            }
            for (RangedNumericValue value2 : getYSizeValues()) {
                value2.scale(scaleY);
            }
        }
    }

    public void scaleMotion(float scale) {
        if (scale != 1.0f) {
            for (RangedNumericValue value : getMotionValues()) {
                value.scale(scale);
            }
        }
    }

    public void matchSize(ParticleEmitter template) {
        matchXSize(template);
        matchYSize(template);
    }

    public void matchXSize(ParticleEmitter template) {
        RangedNumericValue[] values = getXSizeValues();
        RangedNumericValue[] templateValues = template.getXSizeValues();
        for (int i = 0; i < values.length; i++) {
            values[i].set(templateValues[i]);
        }
    }

    public void matchYSize(ParticleEmitter template) {
        RangedNumericValue[] values = getYSizeValues();
        RangedNumericValue[] templateValues = template.getYSizeValues();
        for (int i = 0; i < values.length; i++) {
            values[i].set(templateValues[i]);
        }
    }

    public void matchMotion(ParticleEmitter template) {
        RangedNumericValue[] values = getMotionValues();
        RangedNumericValue[] templateValues = template.getMotionValues();
        for (int i = 0; i < values.length; i++) {
            values[i].set(templateValues[i]);
        }
    }

    public void save(Writer output) throws IOException {
        output.write(this.name + "\n");
        output.write("- Delay -\n");
        this.delayValue.save(output);
        output.write("- Duration - \n");
        this.durationValue.save(output);
        output.write("- Count - \n");
        output.write("min: " + this.minParticleCount + "\n");
        output.write("max: " + this.maxParticleCount + "\n");
        output.write("- Emission - \n");
        this.emissionValue.save(output);
        output.write("- Life - \n");
        this.lifeValue.save(output);
        output.write("- Life Offset - \n");
        this.lifeOffsetValue.save(output);
        output.write("- X Offset - \n");
        this.xOffsetValue.save(output);
        output.write("- Y Offset - \n");
        this.yOffsetValue.save(output);
        output.write("- Spawn Shape - \n");
        this.spawnShapeValue.save(output);
        output.write("- Spawn Width - \n");
        this.spawnWidthValue.save(output);
        output.write("- Spawn Height - \n");
        this.spawnHeightValue.save(output);
        output.write("- X Scale - \n");
        this.xScaleValue.save(output);
        output.write("- Y Scale - \n");
        this.yScaleValue.save(output);
        output.write("- Velocity - \n");
        this.velocityValue.save(output);
        output.write("- Angle - \n");
        this.angleValue.save(output);
        output.write("- Rotation - \n");
        this.rotationValue.save(output);
        output.write("- Wind - \n");
        this.windValue.save(output);
        output.write("- Gravity - \n");
        this.gravityValue.save(output);
        output.write("- Tint - \n");
        this.tintValue.save(output);
        output.write("- Transparency - \n");
        this.transparencyValue.save(output);
        output.write("- Options - \n");
        output.write("attached: " + this.attached + "\n");
        output.write("continuous: " + this.continuous + "\n");
        output.write("aligned: " + this.aligned + "\n");
        output.write("additive: " + this.additive + "\n");
        output.write("behind: " + this.behind + "\n");
        output.write("premultipliedAlpha: " + this.premultipliedAlpha + "\n");
        output.write("spriteMode: " + this.spriteMode.toString() + "\n");
        output.write("- Image Paths -\n");
        Iterator<String> it = this.imagePaths.iterator();
        while (it.hasNext()) {
            output.write(it.next() + "\n");
        }
        output.write("\n");
    }

    public void load(BufferedReader reader) throws IOException {
        try {
            this.name = readString(reader, "name");
            reader.readLine();
            this.delayValue.load(reader);
            reader.readLine();
            this.durationValue.load(reader);
            reader.readLine();
            setMinParticleCount(readInt(reader, "minParticleCount"));
            setMaxParticleCount(readInt(reader, "maxParticleCount"));
            reader.readLine();
            this.emissionValue.load(reader);
            reader.readLine();
            this.lifeValue.load(reader);
            reader.readLine();
            this.lifeOffsetValue.load(reader);
            reader.readLine();
            this.xOffsetValue.load(reader);
            reader.readLine();
            this.yOffsetValue.load(reader);
            reader.readLine();
            this.spawnShapeValue.load(reader);
            reader.readLine();
            this.spawnWidthValue.load(reader);
            reader.readLine();
            this.spawnHeightValue.load(reader);
            if (reader.readLine().trim().equals("- Scale -")) {
                this.xScaleValue.load(reader);
                this.yScaleValue.setActive(false);
            } else {
                this.xScaleValue.load(reader);
                reader.readLine();
                this.yScaleValue.load(reader);
            }
            reader.readLine();
            this.velocityValue.load(reader);
            reader.readLine();
            this.angleValue.load(reader);
            reader.readLine();
            this.rotationValue.load(reader);
            reader.readLine();
            this.windValue.load(reader);
            reader.readLine();
            this.gravityValue.load(reader);
            reader.readLine();
            this.tintValue.load(reader);
            reader.readLine();
            this.transparencyValue.load(reader);
            reader.readLine();
            this.attached = readBoolean(reader, "attached");
            this.continuous = readBoolean(reader, "continuous");
            this.aligned = readBoolean(reader, "aligned");
            this.additive = readBoolean(reader, "additive");
            this.behind = readBoolean(reader, "behind");
            String line = reader.readLine();
            if (line.startsWith("premultipliedAlpha")) {
                this.premultipliedAlpha = readBoolean(line);
                line = reader.readLine();
            }
            if (line.startsWith("spriteMode")) {
                this.spriteMode = SpriteMode.valueOf(readString(line));
                String line2 = reader.readLine();
            }
            Array<String> imagePaths2 = new Array<>();
            while (true) {
                String readLine = reader.readLine();
                String line3 = readLine;
                if (readLine == null || line3.isEmpty()) {
                    setImagePaths(imagePaths2);
                } else {
                    imagePaths2.add(line3);
                }
            }
            setImagePaths(imagePaths2);
        } catch (RuntimeException ex) {
            if (this.name == null) {
                throw ex;
            }
            throw new RuntimeException("Error parsing emitter: " + this.name, ex);
        }
    }

    static String readString(String line) throws IOException {
        return line.substring(line.indexOf(":") + 1).trim();
    }

    static String readString(BufferedReader reader, String name2) throws IOException {
        String line = reader.readLine();
        if (line != null) {
            return readString(line);
        }
        throw new IOException("Missing value: " + name2);
    }

    static boolean readBoolean(String line) throws IOException {
        return Boolean.parseBoolean(readString(line));
    }

    static boolean readBoolean(BufferedReader reader, String name2) throws IOException {
        return Boolean.parseBoolean(readString(reader, name2));
    }

    static int readInt(BufferedReader reader, String name2) throws IOException {
        return Integer.parseInt(readString(reader, name2));
    }

    static float readFloat(BufferedReader reader, String name2) throws IOException {
        return Float.parseFloat(readString(reader, name2));
    }

    public static class Particle extends Sprite {
        protected float angle;
        protected float angleCos;
        protected float angleDiff;
        protected float angleSin;
        protected int currentLife;
        protected int frame;
        protected float gravity;
        protected float gravityDiff;
        protected int life;
        protected float rotation;
        protected float rotationDiff;
        protected float[] tint;
        protected float transparency;
        protected float transparencyDiff;
        protected float velocity;
        protected float velocityDiff;
        protected float wind;
        protected float windDiff;
        protected float xScale;
        protected float xScaleDiff;
        protected float yScale;
        protected float yScaleDiff;

        public Particle(Sprite sprite) {
            super(sprite);
        }
    }

    public static class ParticleValue {
        boolean active;
        boolean alwaysActive;

        public void setAlwaysActive(boolean alwaysActive2) {
            this.alwaysActive = alwaysActive2;
        }

        public boolean isAlwaysActive() {
            return this.alwaysActive;
        }

        public boolean isActive() {
            return this.alwaysActive || this.active;
        }

        public void setActive(boolean active2) {
            this.active = active2;
        }

        public void save(Writer output) throws IOException {
            if (!this.alwaysActive) {
                output.write("active: " + this.active + "\n");
                return;
            }
            this.active = true;
        }

        public void load(BufferedReader reader) throws IOException {
            if (!this.alwaysActive) {
                this.active = ParticleEmitter.readBoolean(reader, "active");
            } else {
                this.active = true;
            }
        }

        public void load(ParticleValue value) {
            this.active = value.active;
            this.alwaysActive = value.alwaysActive;
        }
    }

    public static class NumericValue extends ParticleValue {
        private float value;

        public float getValue() {
            return this.value;
        }

        public void setValue(float value2) {
            this.value = value2;
        }

        public void save(Writer output) throws IOException {
            super.save(output);
            if (this.active) {
                output.write("value: " + this.value + "\n");
            }
        }

        public void load(BufferedReader reader) throws IOException {
            super.load(reader);
            if (this.active) {
                this.value = ParticleEmitter.readFloat(reader, "value");
            }
        }

        public void load(NumericValue value2) {
            super.load((ParticleValue) value2);
            this.value = value2.value;
        }
    }

    public static class RangedNumericValue extends ParticleValue {
        private float lowMax;
        private float lowMin;

        public float newLowValue() {
            float f = this.lowMin;
            return f + ((this.lowMax - f) * MathUtils.random());
        }

        public void setLow(float value) {
            this.lowMin = value;
            this.lowMax = value;
        }

        public void setLow(float min, float max) {
            this.lowMin = min;
            this.lowMax = max;
        }

        public float getLowMin() {
            return this.lowMin;
        }

        public void setLowMin(float lowMin2) {
            this.lowMin = lowMin2;
        }

        public float getLowMax() {
            return this.lowMax;
        }

        public void setLowMax(float lowMax2) {
            this.lowMax = lowMax2;
        }

        public void scale(float scale) {
            this.lowMin *= scale;
            this.lowMax *= scale;
        }

        public void set(RangedNumericValue value) {
            this.lowMin = value.lowMin;
            this.lowMax = value.lowMax;
        }

        public void save(Writer output) throws IOException {
            super.save(output);
            if (this.active) {
                output.write("lowMin: " + this.lowMin + "\n");
                output.write("lowMax: " + this.lowMax + "\n");
            }
        }

        public void load(BufferedReader reader) throws IOException {
            super.load(reader);
            if (this.active) {
                this.lowMin = ParticleEmitter.readFloat(reader, "lowMin");
                this.lowMax = ParticleEmitter.readFloat(reader, "lowMax");
            }
        }

        public void load(RangedNumericValue value) {
            super.load((ParticleValue) value);
            this.lowMax = value.lowMax;
            this.lowMin = value.lowMin;
        }
    }

    public static class ScaledNumericValue extends RangedNumericValue {
        private float highMax;
        private float highMin;
        private boolean relative;
        private float[] scaling = {1.0f};
        float[] timeline = {0.0f};

        public float newHighValue() {
            float f = this.highMin;
            return f + ((this.highMax - f) * MathUtils.random());
        }

        public void setHigh(float value) {
            this.highMin = value;
            this.highMax = value;
        }

        public void setHigh(float min, float max) {
            this.highMin = min;
            this.highMax = max;
        }

        public float getHighMin() {
            return this.highMin;
        }

        public void setHighMin(float highMin2) {
            this.highMin = highMin2;
        }

        public float getHighMax() {
            return this.highMax;
        }

        public void setHighMax(float highMax2) {
            this.highMax = highMax2;
        }

        public void scale(float scale) {
            super.scale(scale);
            this.highMin *= scale;
            this.highMax *= scale;
        }

        public void set(RangedNumericValue value) {
            if (value instanceof ScaledNumericValue) {
                set((ScaledNumericValue) value);
            } else {
                super.set(value);
            }
        }

        public void set(ScaledNumericValue value) {
            super.set(value);
            this.highMin = value.highMin;
            this.highMax = value.highMax;
            float[] fArr = this.scaling;
            int length = fArr.length;
            float[] fArr2 = value.scaling;
            if (length != fArr2.length) {
                this.scaling = Arrays.copyOf(fArr2, fArr2.length);
            } else {
                System.arraycopy(fArr2, 0, fArr, 0, fArr.length);
            }
            float[] fArr3 = this.timeline;
            int length2 = fArr3.length;
            float[] fArr4 = value.timeline;
            if (length2 != fArr4.length) {
                this.timeline = Arrays.copyOf(fArr4, fArr4.length);
            } else {
                System.arraycopy(fArr4, 0, fArr3, 0, fArr3.length);
            }
            this.relative = value.relative;
        }

        public float[] getScaling() {
            return this.scaling;
        }

        public void setScaling(float[] values) {
            this.scaling = values;
        }

        public float[] getTimeline() {
            return this.timeline;
        }

        public void setTimeline(float[] timeline2) {
            this.timeline = timeline2;
        }

        public boolean isRelative() {
            return this.relative;
        }

        public void setRelative(boolean relative2) {
            this.relative = relative2;
        }

        public float getScale(float percent) {
            int endIndex = -1;
            float[] timeline2 = this.timeline;
            int n = timeline2.length;
            int i = 1;
            while (true) {
                if (i >= n) {
                    break;
                } else if (timeline2[i] > percent) {
                    endIndex = i;
                    break;
                } else {
                    i++;
                }
            }
            if (endIndex == -1) {
                return this.scaling[n - 1];
            }
            float[] scaling2 = this.scaling;
            int startIndex = endIndex - 1;
            float startValue = scaling2[startIndex];
            float startTime = timeline2[startIndex];
            return ((scaling2[endIndex] - startValue) * ((percent - startTime) / (timeline2[endIndex] - startTime))) + startValue;
        }

        public void save(Writer output) throws IOException {
            super.save(output);
            if (this.active) {
                output.write("highMin: " + this.highMin + "\n");
                output.write("highMax: " + this.highMax + "\n");
                output.write("relative: " + this.relative + "\n");
                output.write("scalingCount: " + this.scaling.length + "\n");
                for (int i = 0; i < this.scaling.length; i++) {
                    output.write("scaling" + i + ": " + this.scaling[i] + "\n");
                }
                output.write("timelineCount: " + this.timeline.length + "\n");
                for (int i2 = 0; i2 < this.timeline.length; i2++) {
                    output.write("timeline" + i2 + ": " + this.timeline[i2] + "\n");
                }
            }
        }

        public void load(BufferedReader reader) throws IOException {
            super.load(reader);
            if (this.active) {
                this.highMin = ParticleEmitter.readFloat(reader, "highMin");
                this.highMax = ParticleEmitter.readFloat(reader, "highMax");
                this.relative = ParticleEmitter.readBoolean(reader, "relative");
                this.scaling = new float[ParticleEmitter.readInt(reader, "scalingCount")];
                int i = 0;
                while (true) {
                    float[] fArr = this.scaling;
                    if (i >= fArr.length) {
                        break;
                    }
                    fArr[i] = ParticleEmitter.readFloat(reader, "scaling" + i);
                    i++;
                }
                this.timeline = new float[ParticleEmitter.readInt(reader, "timelineCount")];
                int i2 = 0;
                while (true) {
                    float[] fArr2 = this.timeline;
                    if (i2 < fArr2.length) {
                        fArr2[i2] = ParticleEmitter.readFloat(reader, "timeline" + i2);
                        i2++;
                    } else {
                        return;
                    }
                }
            }
        }

        public void load(ScaledNumericValue value) {
            super.load((RangedNumericValue) value);
            this.highMax = value.highMax;
            this.highMin = value.highMin;
            this.scaling = new float[value.scaling.length];
            float[] fArr = value.scaling;
            float[] fArr2 = this.scaling;
            System.arraycopy(fArr, 0, fArr2, 0, fArr2.length);
            this.timeline = new float[value.timeline.length];
            float[] fArr3 = value.timeline;
            float[] fArr4 = this.timeline;
            System.arraycopy(fArr3, 0, fArr4, 0, fArr4.length);
            this.relative = value.relative;
        }
    }

    public static class IndependentScaledNumericValue extends ScaledNumericValue {
        boolean independent;

        public boolean isIndependent() {
            return this.independent;
        }

        public void setIndependent(boolean independent2) {
            this.independent = independent2;
        }

        public void set(RangedNumericValue value) {
            if (value instanceof IndependentScaledNumericValue) {
                set((IndependentScaledNumericValue) value);
            } else {
                super.set(value);
            }
        }

        public void set(ScaledNumericValue value) {
            if (value instanceof IndependentScaledNumericValue) {
                set((IndependentScaledNumericValue) value);
            } else {
                super.set(value);
            }
        }

        public void set(IndependentScaledNumericValue value) {
            super.set((ScaledNumericValue) value);
            this.independent = value.independent;
        }

        public void save(Writer output) throws IOException {
            super.save(output);
            output.write("independent: " + this.independent + "\n");
        }

        public void load(BufferedReader reader) throws IOException {
            super.load(reader);
            if (reader.markSupported()) {
                reader.mark(100);
            }
            String line = reader.readLine();
            if (line == null) {
                throw new IOException("Missing value: independent");
            } else if (line.contains("independent")) {
                this.independent = Boolean.parseBoolean(ParticleEmitter.readString(line));
            } else if (reader.markSupported()) {
                reader.reset();
            } else {
                Gdx.app.error("ParticleEmitter", "The loaded particle effect descriptor file uses an old invalid format. Please download the latest version of the Particle Editor tool and recreate the file by loading and saving it again.");
                throw new IOException("The loaded particle effect descriptor file uses an old invalid format. Please download the latest version of the Particle Editor tool and recreate the file by loading and saving it again.");
            }
        }

        public void load(IndependentScaledNumericValue value) {
            super.load((ScaledNumericValue) value);
            this.independent = value.independent;
        }
    }

    public static class GradientColorValue extends ParticleValue {
        private static float[] temp = new float[4];
        private float[] colors = {1.0f, 1.0f, 1.0f};
        float[] timeline = {0.0f};

        public GradientColorValue() {
            this.alwaysActive = true;
        }

        public float[] getTimeline() {
            return this.timeline;
        }

        public void setTimeline(float[] timeline2) {
            this.timeline = timeline2;
        }

        public float[] getColors() {
            return this.colors;
        }

        public void setColors(float[] colors2) {
            this.colors = colors2;
        }

        public float[] getColor(float percent) {
            int startIndex = 0;
            int endIndex = -1;
            float[] timeline2 = this.timeline;
            int n = timeline2.length;
            int i = 1;
            while (true) {
                if (i >= n) {
                    break;
                } else if (timeline2[i] > percent) {
                    endIndex = i;
                    break;
                } else {
                    startIndex = i;
                    i++;
                }
            }
            float startTime = timeline2[startIndex];
            int startIndex2 = startIndex * 3;
            float[] fArr = this.colors;
            float r1 = fArr[startIndex2];
            float g1 = fArr[startIndex2 + 1];
            float b1 = fArr[startIndex2 + 2];
            if (endIndex == -1) {
                float[] fArr2 = temp;
                fArr2[0] = r1;
                fArr2[1] = g1;
                fArr2[2] = b1;
                return fArr2;
            }
            float factor = (percent - startTime) / (timeline2[endIndex] - startTime);
            int endIndex2 = endIndex * 3;
            float[] fArr3 = temp;
            fArr3[0] = ((fArr[endIndex2] - r1) * factor) + r1;
            fArr3[1] = ((fArr[endIndex2 + 1] - g1) * factor) + g1;
            fArr3[2] = ((fArr[endIndex2 + 2] - b1) * factor) + b1;
            return fArr3;
        }

        public void save(Writer output) throws IOException {
            super.save(output);
            if (this.active) {
                output.write("colorsCount: " + this.colors.length + "\n");
                for (int i = 0; i < this.colors.length; i++) {
                    output.write("colors" + i + ": " + this.colors[i] + "\n");
                }
                output.write("timelineCount: " + this.timeline.length + "\n");
                for (int i2 = 0; i2 < this.timeline.length; i2++) {
                    output.write("timeline" + i2 + ": " + this.timeline[i2] + "\n");
                }
            }
        }

        public void load(BufferedReader reader) throws IOException {
            super.load(reader);
            if (this.active) {
                this.colors = new float[ParticleEmitter.readInt(reader, "colorsCount")];
                int i = 0;
                while (true) {
                    float[] fArr = this.colors;
                    if (i >= fArr.length) {
                        break;
                    }
                    fArr[i] = ParticleEmitter.readFloat(reader, "colors" + i);
                    i++;
                }
                this.timeline = new float[ParticleEmitter.readInt(reader, "timelineCount")];
                int i2 = 0;
                while (true) {
                    float[] fArr2 = this.timeline;
                    if (i2 < fArr2.length) {
                        fArr2[i2] = ParticleEmitter.readFloat(reader, "timeline" + i2);
                        i2++;
                    } else {
                        return;
                    }
                }
            }
        }

        public void load(GradientColorValue value) {
            super.load((ParticleValue) value);
            this.colors = new float[value.colors.length];
            float[] fArr = value.colors;
            float[] fArr2 = this.colors;
            System.arraycopy(fArr, 0, fArr2, 0, fArr2.length);
            this.timeline = new float[value.timeline.length];
            float[] fArr3 = value.timeline;
            float[] fArr4 = this.timeline;
            System.arraycopy(fArr3, 0, fArr4, 0, fArr4.length);
        }
    }

    public static class SpawnShapeValue extends ParticleValue {
        boolean edges;
        SpawnShape shape = SpawnShape.point;
        SpawnEllipseSide side = SpawnEllipseSide.both;

        public SpawnShape getShape() {
            return this.shape;
        }

        public void setShape(SpawnShape shape2) {
            this.shape = shape2;
        }

        public boolean isEdges() {
            return this.edges;
        }

        public void setEdges(boolean edges2) {
            this.edges = edges2;
        }

        public SpawnEllipseSide getSide() {
            return this.side;
        }

        public void setSide(SpawnEllipseSide side2) {
            this.side = side2;
        }

        public void save(Writer output) throws IOException {
            super.save(output);
            if (this.active) {
                output.write("shape: " + this.shape + "\n");
                if (this.shape == SpawnShape.ellipse) {
                    output.write("edges: " + this.edges + "\n");
                    output.write("side: " + this.side + "\n");
                }
            }
        }

        public void load(BufferedReader reader) throws IOException {
            super.load(reader);
            if (this.active) {
                this.shape = SpawnShape.valueOf(ParticleEmitter.readString(reader, "shape"));
                if (this.shape == SpawnShape.ellipse) {
                    this.edges = ParticleEmitter.readBoolean(reader, "edges");
                    this.side = SpawnEllipseSide.valueOf(ParticleEmitter.readString(reader, "side"));
                }
            }
        }

        public void load(SpawnShapeValue value) {
            super.load((ParticleValue) value);
            this.shape = value.shape;
            this.edges = value.edges;
            this.side = value.side;
        }
    }
}
