package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class ParticleEffectPool extends Pool<PooledEffect> {
    private final ParticleEffect effect;

    public ParticleEffectPool(ParticleEffect effect2, int initialCapacity, int max) {
        super(initialCapacity, max);
        this.effect = effect2;
    }

    /* access modifiers changed from: protected */
    public PooledEffect newObject() {
        PooledEffect pooledEffect = new PooledEffect(this.effect);
        pooledEffect.start();
        return pooledEffect;
    }

    public void free(PooledEffect effect2) {
        super.free(effect2);
        effect2.reset(false);
        if (effect2.xSizeScale != this.effect.xSizeScale || effect2.ySizeScale != this.effect.ySizeScale || effect2.motionScale != this.effect.motionScale) {
            Array<ParticleEmitter> emitters = effect2.getEmitters();
            Array<ParticleEmitter> templateEmitters = this.effect.getEmitters();
            for (int i = 0; i < emitters.size; i++) {
                ParticleEmitter emitter = emitters.get(i);
                ParticleEmitter templateEmitter = templateEmitters.get(i);
                emitter.matchSize(templateEmitter);
                emitter.matchMotion(templateEmitter);
            }
            effect2.xSizeScale = this.effect.xSizeScale;
            effect2.ySizeScale = this.effect.ySizeScale;
            effect2.motionScale = this.effect.motionScale;
        }
    }

    public class PooledEffect extends ParticleEffect {
        PooledEffect(ParticleEffect effect) {
            super(effect);
        }

        public void free() {
            ParticleEffectPool.this.free(this);
        }
    }
}
