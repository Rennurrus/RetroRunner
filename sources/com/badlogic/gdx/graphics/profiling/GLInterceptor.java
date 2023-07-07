package com.badlogic.gdx.graphics.profiling;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.FloatCounter;

public abstract class GLInterceptor implements GL20 {
    protected int calls;
    protected int drawCalls;
    protected GLProfiler glProfiler;
    protected int shaderSwitches;
    protected int textureBindings;
    protected final FloatCounter vertexCount = new FloatCounter(0);

    protected GLInterceptor(GLProfiler profiler) {
        this.glProfiler = profiler;
    }

    public static String resolveErrorNumber(int error) {
        switch (error) {
            case GL20.GL_INVALID_ENUM:
                return "GL_INVALID_ENUM";
            case GL20.GL_INVALID_VALUE:
                return "GL_INVALID_VALUE";
            case GL20.GL_INVALID_OPERATION:
                return "GL_INVALID_OPERATION";
            case GL20.GL_OUT_OF_MEMORY:
                return "GL_OUT_OF_MEMORY";
            case GL20.GL_INVALID_FRAMEBUFFER_OPERATION:
                return "GL_INVALID_FRAMEBUFFER_OPERATION";
            default:
                return "number " + error;
        }
    }

    public int getCalls() {
        return this.calls;
    }

    public int getTextureBindings() {
        return this.textureBindings;
    }

    public int getDrawCalls() {
        return this.drawCalls;
    }

    public int getShaderSwitches() {
        return this.shaderSwitches;
    }

    public FloatCounter getVertexCount() {
        return this.vertexCount;
    }

    public void reset() {
        this.calls = 0;
        this.textureBindings = 0;
        this.drawCalls = 0;
        this.shaderSwitches = 0;
        this.vertexCount.reset();
    }
}
