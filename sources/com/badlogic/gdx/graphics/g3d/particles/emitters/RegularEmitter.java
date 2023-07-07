package com.badlogic.gdx.graphics.g3d.particles.emitters;

import com.badlogic.gdx.graphics.g3d.particles.ParallelArray;
import com.badlogic.gdx.graphics.g3d.particles.ParticleChannels;
import com.badlogic.gdx.graphics.g3d.particles.ParticleControllerComponent;
import com.badlogic.gdx.graphics.g3d.particles.values.RangedNumericValue;
import com.badlogic.gdx.graphics.g3d.particles.values.ScaledNumericValue;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class RegularEmitter extends Emitter implements Json.Serializable {
    private boolean continuous;
    protected float delay;
    protected float delayTimer;
    public RangedNumericValue delayValue;
    protected float duration;
    protected float durationTimer;
    public RangedNumericValue durationValue;
    protected int emission;
    protected int emissionDelta;
    protected int emissionDiff;
    private EmissionMode emissionMode;
    public ScaledNumericValue emissionValue;
    protected int life;
    private ParallelArray.FloatChannel lifeChannel;
    protected int lifeDiff;
    protected int lifeOffset;
    protected int lifeOffsetDiff;
    public ScaledNumericValue lifeOffsetValue;
    public ScaledNumericValue lifeValue;

    public enum EmissionMode {
        Enabled,
        EnabledUntilCycleEnd,
        Disabled
    }

    public RegularEmitter() {
        this.delayValue = new RangedNumericValue();
        this.durationValue = new RangedNumericValue();
        this.lifeOffsetValue = new ScaledNumericValue();
        this.lifeValue = new ScaledNumericValue();
        this.emissionValue = new ScaledNumericValue();
        this.durationValue.setActive(true);
        this.emissionValue.setActive(true);
        this.lifeValue.setActive(true);
        this.continuous = true;
        this.emissionMode = EmissionMode.Enabled;
    }

    public RegularEmitter(RegularEmitter regularEmitter) {
        this();
        set(regularEmitter);
    }

    public void allocateChannels() {
        this.lifeChannel = (ParallelArray.FloatChannel) this.controller.particles.addChannel(ParticleChannels.Life);
    }

    public void start() {
        this.delay = this.delayValue.active ? this.delayValue.newLowValue() : 0.0f;
        this.delayTimer = 0.0f;
        this.durationTimer = 0.0f;
        this.duration = this.durationValue.newLowValue();
        this.percent = this.durationTimer / this.duration;
        this.emission = (int) this.emissionValue.newLowValue();
        this.emissionDiff = (int) this.emissionValue.newHighValue();
        if (!this.emissionValue.isRelative()) {
            this.emissionDiff -= this.emission;
        }
        this.life = (int) this.lifeValue.newLowValue();
        this.lifeDiff = (int) this.lifeValue.newHighValue();
        if (!this.lifeValue.isRelative()) {
            this.lifeDiff -= this.life;
        }
        this.lifeOffset = this.lifeOffsetValue.active ? (int) this.lifeOffsetValue.newLowValue() : 0;
        this.lifeOffsetDiff = (int) this.lifeOffsetValue.newHighValue();
        if (!this.lifeOffsetValue.isRelative()) {
            this.lifeOffsetDiff -= this.lifeOffset;
        }
    }

    public void init() {
        super.init();
        this.emissionDelta = 0;
        this.durationTimer = this.duration;
    }

    public void activateParticles(int startIndex, int count) {
        int currentTotaLife = this.life + ((int) (((float) this.lifeDiff) * this.lifeValue.getScale(this.percent)));
        int currentLife = currentTotaLife;
        int offsetTime = (int) (((float) this.lifeOffset) + (((float) this.lifeOffsetDiff) * this.lifeOffsetValue.getScale(this.percent)));
        if (offsetTime > 0) {
            if (offsetTime >= currentLife) {
                offsetTime = currentLife - 1;
            }
            currentLife -= offsetTime;
        }
        float lifePercent = 1.0f - (((float) currentLife) / ((float) currentTotaLife));
        int i = this.lifeChannel.strideSize * startIndex;
        int c = (this.lifeChannel.strideSize * count) + i;
        while (i < c) {
            this.lifeChannel.data[i + 0] = (float) currentLife;
            this.lifeChannel.data[i + 1] = (float) currentTotaLife;
            this.lifeChannel.data[i + 2] = lifePercent;
            i += this.lifeChannel.strideSize;
        }
    }

    public void update() {
        float deltaMillis = this.controller.deltaTime * 1000.0f;
        float f = this.delayTimer;
        if (f < this.delay) {
            this.delayTimer = f + deltaMillis;
        } else {
            boolean emit = this.emissionMode != EmissionMode.Disabled;
            float f2 = this.durationTimer;
            float f3 = this.duration;
            if (f2 < f3) {
                this.durationTimer = f2 + deltaMillis;
                this.percent = this.durationTimer / f3;
            } else if (!this.continuous || !emit || this.emissionMode != EmissionMode.Enabled) {
                emit = false;
            } else {
                this.controller.start();
            }
            if (emit) {
                this.emissionDelta = (int) (((float) this.emissionDelta) + deltaMillis);
                float emissionTime = ((float) this.emission) + (((float) this.emissionDiff) * this.emissionValue.getScale(this.percent));
                if (emissionTime > 0.0f) {
                    float emissionTime2 = 1000.0f / emissionTime;
                    int i = this.emissionDelta;
                    if (((float) i) >= emissionTime2) {
                        int emitCount = Math.min((int) (((float) i) / emissionTime2), this.maxParticleCount - this.controller.particles.size);
                        this.emissionDelta = (int) (((float) this.emissionDelta) - (((float) emitCount) * emissionTime2));
                        this.emissionDelta = (int) (((float) this.emissionDelta) % emissionTime2);
                        addParticles(emitCount);
                    }
                }
                if (this.controller.particles.size < this.minParticleCount) {
                    addParticles(this.minParticleCount - this.controller.particles.size);
                }
            }
        }
        int activeParticles = this.controller.particles.size;
        int i2 = 0;
        int k = 0;
        while (i2 < this.controller.particles.size) {
            float[] fArr = this.lifeChannel.data;
            int i3 = k + 0;
            float f4 = fArr[i3] - deltaMillis;
            fArr[i3] = f4;
            if (f4 <= 0.0f) {
                this.controller.particles.removeElement(i2);
            } else {
                this.lifeChannel.data[k + 2] = 1.0f - (this.lifeChannel.data[k + 0] / this.lifeChannel.data[k + 1]);
                i2++;
                k += this.lifeChannel.strideSize;
            }
        }
        if (this.controller.particles.size < activeParticles) {
            this.controller.killParticles(this.controller.particles.size, activeParticles - this.controller.particles.size);
        }
    }

    private void addParticles(int count) {
        int count2 = Math.min(count, this.maxParticleCount - this.controller.particles.size);
        if (count2 > 0) {
            this.controller.activateParticles(this.controller.particles.size, count2);
            this.controller.particles.size += count2;
        }
    }

    public ScaledNumericValue getLife() {
        return this.lifeValue;
    }

    public ScaledNumericValue getEmission() {
        return this.emissionValue;
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

    public boolean isContinuous() {
        return this.continuous;
    }

    public void setContinuous(boolean continuous2) {
        this.continuous = continuous2;
    }

    public EmissionMode getEmissionMode() {
        return this.emissionMode;
    }

    public void setEmissionMode(EmissionMode emissionMode2) {
        this.emissionMode = emissionMode2;
    }

    public boolean isComplete() {
        if (this.delayTimer >= this.delay && this.durationTimer >= this.duration && this.controller.particles.size == 0) {
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

    public void set(RegularEmitter emitter) {
        super.set(emitter);
        this.delayValue.load(emitter.delayValue);
        this.durationValue.load(emitter.durationValue);
        this.lifeOffsetValue.load(emitter.lifeOffsetValue);
        this.lifeValue.load(emitter.lifeValue);
        this.emissionValue.load(emitter.emissionValue);
        this.emission = emitter.emission;
        this.emissionDiff = emitter.emissionDiff;
        this.emissionDelta = emitter.emissionDelta;
        this.lifeOffset = emitter.lifeOffset;
        this.lifeOffsetDiff = emitter.lifeOffsetDiff;
        this.life = emitter.life;
        this.lifeDiff = emitter.lifeDiff;
        this.duration = emitter.duration;
        this.delay = emitter.delay;
        this.durationTimer = emitter.durationTimer;
        this.delayTimer = emitter.delayTimer;
        this.continuous = emitter.continuous;
    }

    public ParticleControllerComponent copy() {
        return new RegularEmitter(this);
    }

    public void write(Json json) {
        super.write(json);
        json.writeValue("continous", (Object) Boolean.valueOf(this.continuous));
        json.writeValue("emission", (Object) this.emissionValue);
        json.writeValue("delay", (Object) this.delayValue);
        json.writeValue("duration", (Object) this.durationValue);
        json.writeValue("life", (Object) this.lifeValue);
        json.writeValue("lifeOffset", (Object) this.lifeOffsetValue);
    }

    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
        this.continuous = ((Boolean) json.readValue("continous", Boolean.TYPE, jsonData)).booleanValue();
        this.emissionValue = (ScaledNumericValue) json.readValue("emission", ScaledNumericValue.class, jsonData);
        this.delayValue = (RangedNumericValue) json.readValue("delay", RangedNumericValue.class, jsonData);
        this.durationValue = (RangedNumericValue) json.readValue("duration", RangedNumericValue.class, jsonData);
        this.lifeValue = (ScaledNumericValue) json.readValue("life", ScaledNumericValue.class, jsonData);
        this.lifeOffsetValue = (ScaledNumericValue) json.readValue("lifeOffset", ScaledNumericValue.class, jsonData);
    }
}
