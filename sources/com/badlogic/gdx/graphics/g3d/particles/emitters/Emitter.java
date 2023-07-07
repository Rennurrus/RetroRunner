package com.badlogic.gdx.graphics.g3d.particles.emitters;

import com.badlogic.gdx.graphics.g3d.particles.ParticleControllerComponent;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public abstract class Emitter extends ParticleControllerComponent implements Json.Serializable {
    public int maxParticleCount = 4;
    public int minParticleCount;
    public float percent;

    public Emitter(Emitter regularEmitter) {
        set(regularEmitter);
    }

    public Emitter() {
    }

    public void init() {
        this.controller.particles.size = 0;
    }

    public void end() {
        this.controller.particles.size = 0;
    }

    public boolean isComplete() {
        return this.percent >= 1.0f;
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

    public void setMaxParticleCount(int maxParticleCount2) {
        this.maxParticleCount = maxParticleCount2;
    }

    public void setParticleCount(int aMin, int aMax) {
        setMinParticleCount(aMin);
        setMaxParticleCount(aMax);
    }

    public void set(Emitter emitter) {
        this.minParticleCount = emitter.minParticleCount;
        this.maxParticleCount = emitter.maxParticleCount;
    }

    public void write(Json json) {
        json.writeValue("minParticleCount", (Object) Integer.valueOf(this.minParticleCount));
        json.writeValue("maxParticleCount", (Object) Integer.valueOf(this.maxParticleCount));
    }

    public void read(Json json, JsonValue jsonData) {
        this.minParticleCount = ((Integer) json.readValue("minParticleCount", Integer.TYPE, jsonData)).intValue();
        this.maxParticleCount = ((Integer) json.readValue("maxParticleCount", Integer.TYPE, jsonData)).intValue();
    }
}
