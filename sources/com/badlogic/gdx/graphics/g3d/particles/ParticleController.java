package com.badlogic.gdx.graphics.g3d.particles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.particles.ParallelArray;
import com.badlogic.gdx.graphics.g3d.particles.ResourceData;
import com.badlogic.gdx.graphics.g3d.particles.emitters.Emitter;
import com.badlogic.gdx.graphics.g3d.particles.influencers.Influencer;
import com.badlogic.gdx.graphics.g3d.particles.renderers.ParticleControllerRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import java.util.Iterator;

public class ParticleController implements Json.Serializable, ResourceData.Configurable {
    protected static final float DEFAULT_TIME_STEP = 0.016666668f;
    protected BoundingBox boundingBox;
    public float deltaTime;
    public float deltaTimeSqr;
    public Emitter emitter;
    public Array<Influencer> influencers;
    public String name;
    public ParticleChannels particleChannels;
    public ParallelArray particles;
    public ParticleControllerRenderer<?, ?> renderer;
    public Vector3 scale;
    public Matrix4 transform;

    public ParticleController() {
        this.transform = new Matrix4();
        this.scale = new Vector3(1.0f, 1.0f, 1.0f);
        this.influencers = new Array<>(true, 3, Influencer.class);
        setTimeStep(DEFAULT_TIME_STEP);
    }

    public ParticleController(String name2, Emitter emitter2, ParticleControllerRenderer<?, ?> renderer2, Influencer... influencers2) {
        this();
        this.name = name2;
        this.emitter = emitter2;
        this.renderer = renderer2;
        this.particleChannels = new ParticleChannels();
        this.influencers = new Array<>((T[]) influencers2);
    }

    private void setTimeStep(float timeStep) {
        this.deltaTime = timeStep;
        float f = this.deltaTime;
        this.deltaTimeSqr = f * f;
    }

    public void setTransform(Matrix4 transform2) {
        this.transform.set(transform2);
        transform2.getScale(this.scale);
    }

    public void setTransform(float x, float y, float z, float qx, float qy, float qz, float qw, float scale2) {
        this.transform.set(x, y, z, qx, qy, qz, qw, scale2, scale2, scale2);
        float f = scale2;
        this.scale.set(f, f, f);
    }

    public void rotate(Quaternion rotation) {
        this.transform.rotate(rotation);
    }

    public void rotate(Vector3 axis, float angle) {
        this.transform.rotate(axis, angle);
    }

    public void translate(Vector3 translation) {
        this.transform.translate(translation);
    }

    public void setTranslation(Vector3 translation) {
        this.transform.setTranslation(translation);
    }

    public void scale(float scaleX, float scaleY, float scaleZ) {
        this.transform.scale(scaleX, scaleY, scaleZ);
        this.transform.getScale(this.scale);
    }

    public void scale(Vector3 scale2) {
        scale(scale2.x, scale2.y, scale2.z);
    }

    public void mul(Matrix4 transform2) {
        this.transform.mul(transform2);
        this.transform.getScale(this.scale);
    }

    public void getTransform(Matrix4 transform2) {
        transform2.set(this.transform);
    }

    public boolean isComplete() {
        return this.emitter.isComplete();
    }

    public void init() {
        bind();
        if (this.particles != null) {
            end();
            this.particleChannels.resetIds();
        }
        allocateChannels(this.emitter.maxParticleCount);
        this.emitter.init();
        Iterator<Influencer> it = this.influencers.iterator();
        while (it.hasNext()) {
            it.next().init();
        }
        this.renderer.init();
    }

    /* access modifiers changed from: protected */
    public void allocateChannels(int maxParticleCount) {
        this.particles = new ParallelArray(maxParticleCount);
        this.emitter.allocateChannels();
        Iterator<Influencer> it = this.influencers.iterator();
        while (it.hasNext()) {
            it.next().allocateChannels();
        }
        this.renderer.allocateChannels();
    }

    /* access modifiers changed from: protected */
    public void bind() {
        this.emitter.set(this);
        Iterator<Influencer> it = this.influencers.iterator();
        while (it.hasNext()) {
            it.next().set(this);
        }
        this.renderer.set(this);
    }

    public void start() {
        this.emitter.start();
        Iterator<Influencer> it = this.influencers.iterator();
        while (it.hasNext()) {
            it.next().start();
        }
    }

    public void reset() {
        end();
        start();
    }

    public void end() {
        Iterator<Influencer> it = this.influencers.iterator();
        while (it.hasNext()) {
            it.next().end();
        }
        this.emitter.end();
    }

    public void activateParticles(int startIndex, int count) {
        this.emitter.activateParticles(startIndex, count);
        Iterator<Influencer> it = this.influencers.iterator();
        while (it.hasNext()) {
            it.next().activateParticles(startIndex, count);
        }
    }

    public void killParticles(int startIndex, int count) {
        this.emitter.killParticles(startIndex, count);
        Iterator<Influencer> it = this.influencers.iterator();
        while (it.hasNext()) {
            it.next().killParticles(startIndex, count);
        }
    }

    public void update() {
        update(Gdx.graphics.getDeltaTime());
    }

    public void update(float deltaTime2) {
        setTimeStep(deltaTime2);
        this.emitter.update();
        Iterator<Influencer> it = this.influencers.iterator();
        while (it.hasNext()) {
            it.next().update();
        }
    }

    public void draw() {
        if (this.particles.size > 0) {
            this.renderer.update();
        }
    }

    public ParticleController copy() {
        Emitter emitter2 = (Emitter) this.emitter.copy();
        Influencer[] influencers2 = new Influencer[this.influencers.size];
        int i = 0;
        Iterator<Influencer> it = this.influencers.iterator();
        while (it.hasNext()) {
            influencers2[i] = (Influencer) it.next().copy();
            i++;
        }
        return new ParticleController(new String(this.name), emitter2, (ParticleControllerRenderer) this.renderer.copy(), influencers2);
    }

    public void dispose() {
        this.emitter.dispose();
        Iterator<Influencer> it = this.influencers.iterator();
        while (it.hasNext()) {
            it.next().dispose();
        }
    }

    public BoundingBox getBoundingBox() {
        if (this.boundingBox == null) {
            this.boundingBox = new BoundingBox();
        }
        calculateBoundingBox();
        return this.boundingBox;
    }

    /* access modifiers changed from: protected */
    public void calculateBoundingBox() {
        this.boundingBox.clr();
        ParallelArray.FloatChannel positionChannel = (ParallelArray.FloatChannel) this.particles.getChannel(ParticleChannels.Position);
        int c = positionChannel.strideSize * this.particles.size;
        for (int pos = 0; pos < c; pos += positionChannel.strideSize) {
            this.boundingBox.ext(positionChannel.data[pos + 0], positionChannel.data[pos + 1], positionChannel.data[pos + 2]);
        }
    }

    private <K extends Influencer> int findIndex(Class<K> type) {
        for (int i = 0; i < this.influencers.size; i++) {
            if (ClassReflection.isAssignableFrom(type, this.influencers.get(i).getClass())) {
                return i;
            }
        }
        return -1;
    }

    public <K extends Influencer> K findInfluencer(Class<K> influencerClass) {
        int index = findIndex(influencerClass);
        if (index > -1) {
            return (Influencer) this.influencers.get(index);
        }
        return null;
    }

    public <K extends Influencer> void removeInfluencer(Class<K> type) {
        int index = findIndex(type);
        if (index > -1) {
            this.influencers.removeIndex(index);
        }
    }

    public <K extends Influencer> boolean replaceInfluencer(Class<K> type, K newInfluencer) {
        int index = findIndex(type);
        if (index <= -1) {
            return false;
        }
        this.influencers.insert(index, newInfluencer);
        this.influencers.removeIndex(index + 1);
        return true;
    }

    public void write(Json json) {
        json.writeValue("name", (Object) this.name);
        json.writeValue("emitter", (Object) this.emitter, Emitter.class);
        json.writeValue("influencers", this.influencers, Array.class, Influencer.class);
        json.writeValue("renderer", (Object) this.renderer, ParticleControllerRenderer.class);
    }

    public void read(Json json, JsonValue jsonMap) {
        this.name = (String) json.readValue("name", String.class, jsonMap);
        this.emitter = (Emitter) json.readValue("emitter", Emitter.class, jsonMap);
        this.influencers.addAll((Array) json.readValue("influencers", Array.class, Influencer.class, jsonMap));
        this.renderer = (ParticleControllerRenderer) json.readValue("renderer", ParticleControllerRenderer.class, jsonMap);
    }

    public void save(AssetManager manager, ResourceData data) {
        this.emitter.save(manager, data);
        Iterator<Influencer> it = this.influencers.iterator();
        while (it.hasNext()) {
            it.next().save(manager, data);
        }
        this.renderer.save(manager, data);
    }

    public void load(AssetManager manager, ResourceData data) {
        this.emitter.load(manager, data);
        Iterator<Influencer> it = this.influencers.iterator();
        while (it.hasNext()) {
            it.next().load(manager, data);
        }
        this.renderer.load(manager, data);
    }
}
