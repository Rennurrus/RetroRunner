package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class RenderContext {
    private int blendDFactor;
    private int blendSFactor;
    private boolean blending;
    private int cullFace;
    private int depthFunc;
    private boolean depthMask;
    private float depthRangeFar;
    private float depthRangeNear;
    public final TextureBinder textureBinder;

    public RenderContext(TextureBinder textures) {
        this.textureBinder = textures;
    }

    public void begin() {
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        this.depthFunc = 0;
        Gdx.gl.glDepthMask(true);
        this.depthMask = true;
        Gdx.gl.glDisable(GL20.GL_BLEND);
        this.blending = false;
        Gdx.gl.glDisable(GL20.GL_CULL_FACE);
        this.blendDFactor = 0;
        this.blendSFactor = 0;
        this.cullFace = 0;
        this.textureBinder.begin();
    }

    public void end() {
        if (this.depthFunc != 0) {
            Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        }
        if (!this.depthMask) {
            Gdx.gl.glDepthMask(true);
        }
        if (this.blending) {
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
        if (this.cullFace > 0) {
            Gdx.gl.glDisable(GL20.GL_CULL_FACE);
        }
        this.textureBinder.end();
    }

    public void setDepthMask(boolean depthMask2) {
        if (this.depthMask != depthMask2) {
            GL20 gl20 = Gdx.gl;
            this.depthMask = depthMask2;
            gl20.glDepthMask(depthMask2);
        }
    }

    public void setDepthTest(int depthFunction) {
        setDepthTest(depthFunction, 0.0f, 1.0f);
    }

    public void setDepthTest(int depthFunction, float depthRangeNear2, float depthRangeFar2) {
        boolean enabled = true;
        boolean wasEnabled = this.depthFunc != 0;
        if (depthFunction == 0) {
            enabled = false;
        }
        if (this.depthFunc != depthFunction) {
            this.depthFunc = depthFunction;
            if (enabled) {
                Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
                Gdx.gl.glDepthFunc(depthFunction);
            } else {
                Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
            }
        }
        if (enabled) {
            if (!wasEnabled || this.depthFunc != depthFunction) {
                GL20 gl20 = Gdx.gl;
                this.depthFunc = depthFunction;
                gl20.glDepthFunc(depthFunction);
            }
            if (!wasEnabled || this.depthRangeNear != depthRangeNear2 || this.depthRangeFar != depthRangeFar2) {
                GL20 gl202 = Gdx.gl;
                this.depthRangeNear = depthRangeNear2;
                this.depthRangeFar = depthRangeFar2;
                gl202.glDepthRangef(depthRangeNear2, depthRangeFar2);
            }
        }
    }

    public void setBlending(boolean enabled, int sFactor, int dFactor) {
        if (enabled != this.blending) {
            this.blending = enabled;
            if (enabled) {
                Gdx.gl.glEnable(GL20.GL_BLEND);
            } else {
                Gdx.gl.glDisable(GL20.GL_BLEND);
            }
        }
        if (!enabled) {
            return;
        }
        if (this.blendSFactor != sFactor || this.blendDFactor != dFactor) {
            Gdx.gl.glBlendFunc(sFactor, dFactor);
            this.blendSFactor = sFactor;
            this.blendDFactor = dFactor;
        }
    }

    public void setCullFace(int face) {
        if (face != this.cullFace) {
            this.cullFace = face;
            if (face == 1028 || face == 1029 || face == 1032) {
                Gdx.gl.glEnable(GL20.GL_CULL_FACE);
                Gdx.gl.glCullFace(face);
                return;
            }
            Gdx.gl.glDisable(GL20.GL_CULL_FACE);
        }
    }
}
