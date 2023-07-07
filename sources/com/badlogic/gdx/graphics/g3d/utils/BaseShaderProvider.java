package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.utils.Array;
import java.util.Iterator;

public abstract class BaseShaderProvider implements ShaderProvider {
    protected Array<Shader> shaders = new Array<>();

    /* access modifiers changed from: protected */
    public abstract Shader createShader(Renderable renderable);

    public Shader getShader(Renderable renderable) {
        Shader suggestedShader = renderable.shader;
        if (suggestedShader != null && suggestedShader.canRender(renderable)) {
            return suggestedShader;
        }
        Iterator<Shader> it = this.shaders.iterator();
        while (it.hasNext()) {
            Shader shader = it.next();
            if (shader.canRender(renderable)) {
                return shader;
            }
        }
        Shader shader2 = createShader(renderable);
        shader2.init();
        this.shaders.add(shader2);
        return shader2;
    }

    public void dispose() {
        Iterator<Shader> it = this.shaders.iterator();
        while (it.hasNext()) {
            it.next().dispose();
        }
        this.shaders.clear();
    }
}
