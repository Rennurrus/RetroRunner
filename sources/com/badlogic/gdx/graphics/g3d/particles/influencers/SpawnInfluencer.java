package com.badlogic.gdx.graphics.g3d.particles.influencers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.particles.ParallelArray;
import com.badlogic.gdx.graphics.g3d.particles.ParticleChannels;
import com.badlogic.gdx.graphics.g3d.particles.ResourceData;
import com.badlogic.gdx.graphics.g3d.particles.values.PointSpawnShapeValue;
import com.badlogic.gdx.graphics.g3d.particles.values.SpawnShapeValue;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class SpawnInfluencer extends Influencer {
    ParallelArray.FloatChannel positionChannel;
    public SpawnShapeValue spawnShapeValue;

    public SpawnInfluencer() {
        this.spawnShapeValue = new PointSpawnShapeValue();
    }

    public SpawnInfluencer(SpawnShapeValue spawnShapeValue2) {
        this.spawnShapeValue = spawnShapeValue2;
    }

    public SpawnInfluencer(SpawnInfluencer source) {
        this.spawnShapeValue = source.spawnShapeValue.copy();
    }

    public void init() {
        this.spawnShapeValue.init();
    }

    public void allocateChannels() {
        this.positionChannel = (ParallelArray.FloatChannel) this.controller.particles.addChannel(ParticleChannels.Position);
    }

    public void start() {
        this.spawnShapeValue.start();
    }

    public void activateParticles(int startIndex, int count) {
        int i = this.positionChannel.strideSize * startIndex;
        int c = (this.positionChannel.strideSize * count) + i;
        while (i < c) {
            this.spawnShapeValue.spawn(TMP_V1, this.controller.emitter.percent);
            TMP_V1.mul(this.controller.transform);
            this.positionChannel.data[i + 0] = TMP_V1.x;
            this.positionChannel.data[i + 1] = TMP_V1.y;
            this.positionChannel.data[i + 2] = TMP_V1.z;
            i += this.positionChannel.strideSize;
        }
    }

    public SpawnInfluencer copy() {
        return new SpawnInfluencer(this);
    }

    public void write(Json json) {
        json.writeValue("spawnShape", (Object) this.spawnShapeValue, SpawnShapeValue.class);
    }

    public void read(Json json, JsonValue jsonData) {
        this.spawnShapeValue = (SpawnShapeValue) json.readValue("spawnShape", SpawnShapeValue.class, jsonData);
    }

    public void save(AssetManager manager, ResourceData data) {
        this.spawnShapeValue.save(manager, data);
    }

    public void load(AssetManager manager, ResourceData data) {
        this.spawnShapeValue.load(manager, data);
    }
}
