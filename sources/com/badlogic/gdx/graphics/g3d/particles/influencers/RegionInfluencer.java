package com.badlogic.gdx.graphics.g3d.particles.influencers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.particles.ParallelArray;
import com.badlogic.gdx.graphics.g3d.particles.ParticleChannels;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public abstract class RegionInfluencer extends Influencer {
    ParallelArray.FloatChannel regionChannel;
    public Array<AspectTextureRegion> regions;

    public static class Single extends RegionInfluencer {
        public Single() {
        }

        public Single(Single regionInfluencer) {
            super((RegionInfluencer) regionInfluencer);
        }

        public Single(TextureRegion textureRegion) {
            super(textureRegion);
        }

        public Single(Texture texture) {
            super(texture);
        }

        public void init() {
            AspectTextureRegion region = ((AspectTextureRegion[]) this.regions.items)[0];
            int i = 0;
            int c = this.controller.emitter.maxParticleCount * this.regionChannel.strideSize;
            while (i < c) {
                this.regionChannel.data[i + 0] = region.u;
                this.regionChannel.data[i + 1] = region.v;
                this.regionChannel.data[i + 2] = region.u2;
                this.regionChannel.data[i + 3] = region.v2;
                this.regionChannel.data[i + 4] = 0.5f;
                this.regionChannel.data[i + 5] = region.halfInvAspectRatio;
                i += this.regionChannel.strideSize;
            }
        }

        public Single copy() {
            return new Single(this);
        }
    }

    public static class Random extends RegionInfluencer {
        public Random() {
        }

        public Random(Random regionInfluencer) {
            super((RegionInfluencer) regionInfluencer);
        }

        public Random(TextureRegion textureRegion) {
            super(textureRegion);
        }

        public Random(Texture texture) {
            super(texture);
        }

        public void activateParticles(int startIndex, int count) {
            int i = this.regionChannel.strideSize * startIndex;
            int c = (this.regionChannel.strideSize * count) + i;
            while (i < c) {
                AspectTextureRegion region = (AspectTextureRegion) this.regions.random();
                this.regionChannel.data[i + 0] = region.u;
                this.regionChannel.data[i + 1] = region.v;
                this.regionChannel.data[i + 2] = region.u2;
                this.regionChannel.data[i + 3] = region.v2;
                this.regionChannel.data[i + 4] = 0.5f;
                this.regionChannel.data[i + 5] = region.halfInvAspectRatio;
                i += this.regionChannel.strideSize;
            }
        }

        public Random copy() {
            return new Random(this);
        }
    }

    public static class Animated extends RegionInfluencer {
        ParallelArray.FloatChannel lifeChannel;

        public Animated() {
        }

        public Animated(Animated regionInfluencer) {
            super((RegionInfluencer) regionInfluencer);
        }

        public Animated(TextureRegion textureRegion) {
            super(textureRegion);
        }

        public Animated(Texture texture) {
            super(texture);
        }

        public void allocateChannels() {
            RegionInfluencer.super.allocateChannels();
            this.lifeChannel = (ParallelArray.FloatChannel) this.controller.particles.addChannel(ParticleChannels.Life);
        }

        public void update() {
            int i = 0;
            int l = 2;
            int c = this.controller.particles.size * this.regionChannel.strideSize;
            while (i < c) {
                AspectTextureRegion region = (AspectTextureRegion) this.regions.get((int) (this.lifeChannel.data[l] * ((float) (this.regions.size - 1))));
                this.regionChannel.data[i + 0] = region.u;
                this.regionChannel.data[i + 1] = region.v;
                this.regionChannel.data[i + 2] = region.u2;
                this.regionChannel.data[i + 3] = region.v2;
                this.regionChannel.data[i + 4] = 0.5f;
                this.regionChannel.data[i + 5] = region.halfInvAspectRatio;
                i += this.regionChannel.strideSize;
                l += this.lifeChannel.strideSize;
            }
        }

        public Animated copy() {
            return new Animated(this);
        }
    }

    public static class AspectTextureRegion {
        public float halfInvAspectRatio;
        public float u;
        public float u2;
        public float v;
        public float v2;

        public AspectTextureRegion() {
        }

        public AspectTextureRegion(AspectTextureRegion aspectTextureRegion) {
            set(aspectTextureRegion);
        }

        public AspectTextureRegion(TextureRegion region) {
            set(region);
        }

        public void set(TextureRegion region) {
            this.u = region.getU();
            this.v = region.getV();
            this.u2 = region.getU2();
            this.v2 = region.getV2();
            this.halfInvAspectRatio = (((float) region.getRegionHeight()) / ((float) region.getRegionWidth())) * 0.5f;
        }

        public void set(AspectTextureRegion aspectTextureRegion) {
            this.u = aspectTextureRegion.u;
            this.v = aspectTextureRegion.v;
            this.u2 = aspectTextureRegion.u2;
            this.v2 = aspectTextureRegion.v2;
            this.halfInvAspectRatio = aspectTextureRegion.halfInvAspectRatio;
        }
    }

    public RegionInfluencer(int regionsCount) {
        this.regions = new Array<>(false, regionsCount, AspectTextureRegion.class);
    }

    public RegionInfluencer() {
        this(1);
        AspectTextureRegion aspectRegion = new AspectTextureRegion();
        aspectRegion.v = 0.0f;
        aspectRegion.u = 0.0f;
        aspectRegion.v2 = 1.0f;
        aspectRegion.u2 = 1.0f;
        aspectRegion.halfInvAspectRatio = 0.5f;
        this.regions.add(aspectRegion);
    }

    public RegionInfluencer(TextureRegion... regions2) {
        this.regions = new Array<>(false, regions2.length, AspectTextureRegion.class);
        add(regions2);
    }

    public RegionInfluencer(Texture texture) {
        this(new TextureRegion(texture));
    }

    public RegionInfluencer(RegionInfluencer regionInfluencer) {
        this(regionInfluencer.regions.size);
        this.regions.ensureCapacity(regionInfluencer.regions.size);
        for (int i = 0; i < regionInfluencer.regions.size; i++) {
            this.regions.add(new AspectTextureRegion(regionInfluencer.regions.get(i)));
        }
    }

    public void add(TextureRegion... regions2) {
        this.regions.ensureCapacity(regions2.length);
        for (TextureRegion region : regions2) {
            this.regions.add(new AspectTextureRegion(region));
        }
    }

    public void clear() {
        this.regions.clear();
    }

    public void allocateChannels() {
        this.regionChannel = (ParallelArray.FloatChannel) this.controller.particles.addChannel(ParticleChannels.TextureRegion);
    }

    public void write(Json json) {
        json.writeValue("regions", this.regions, Array.class, AspectTextureRegion.class);
    }

    public void read(Json json, JsonValue jsonData) {
        this.regions.clear();
        this.regions.addAll((Array) json.readValue("regions", Array.class, AspectTextureRegion.class, jsonData));
    }
}
