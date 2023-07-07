package com.badlogic.gdx.graphics.g3d.particles.renderers;

import com.badlogic.gdx.graphics.g3d.particles.ParticleController;
import com.badlogic.gdx.graphics.g3d.particles.ParticleControllerComponent;
import com.badlogic.gdx.graphics.g3d.particles.batches.ParticleBatch;
import com.badlogic.gdx.graphics.g3d.particles.renderers.ParticleControllerRenderData;

public abstract class ParticleControllerRenderer<D extends ParticleControllerRenderData, T extends ParticleBatch<D>> extends ParticleControllerComponent {
    protected T batch;
    protected D renderData;

    public abstract boolean isCompatible(ParticleBatch<?> particleBatch);

    protected ParticleControllerRenderer() {
    }

    protected ParticleControllerRenderer(D renderData2) {
        this.renderData = renderData2;
    }

    public void update() {
        this.batch.draw(this.renderData);
    }

    public boolean setBatch(ParticleBatch<?> batch2) {
        if (!isCompatible(batch2)) {
            return false;
        }
        this.batch = batch2;
        return true;
    }

    public void set(ParticleController particleController) {
        super.set(particleController);
        D d = this.renderData;
        if (d != null) {
            d.controller = this.controller;
        }
    }
}
