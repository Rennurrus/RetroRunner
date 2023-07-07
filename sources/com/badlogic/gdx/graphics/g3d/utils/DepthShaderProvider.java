package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DepthShader;

public class DepthShaderProvider extends BaseShaderProvider {
    public final DepthShader.Config config;

    public DepthShaderProvider(DepthShader.Config config2) {
        this.config = config2 == null ? new DepthShader.Config() : config2;
    }

    public DepthShaderProvider(String vertexShader, String fragmentShader) {
        this(new DepthShader.Config(vertexShader, fragmentShader));
    }

    public DepthShaderProvider(FileHandle vertexShader, FileHandle fragmentShader) {
        this(vertexShader.readString(), fragmentShader.readString());
    }

    public DepthShaderProvider() {
        this((DepthShader.Config) null);
    }

    /* access modifiers changed from: protected */
    public Shader createShader(Renderable renderable) {
        return new DepthShader(renderable, this.config);
    }
}
