package com.badlogic.gdx.graphics.g3d;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.utils.DefaultRenderableSorter;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.g3d.utils.RenderableSorter;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.FlushablePool;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.twi.game.BuildConfig;

public class ModelBatch implements Disposable {
    protected Camera camera;
    protected final RenderContext context;
    private final boolean ownContext;
    protected final Array<Renderable> renderables;
    protected final RenderablePool renderablesPool;
    protected final ShaderProvider shaderProvider;
    protected final RenderableSorter sorter;

    protected static class RenderablePool extends FlushablePool<Renderable> {
        protected RenderablePool() {
        }

        /* access modifiers changed from: protected */
        public Renderable newObject() {
            return new Renderable();
        }

        public Renderable obtain() {
            Renderable renderable = (Renderable) super.obtain();
            renderable.environment = null;
            renderable.material = null;
            renderable.meshPart.set(BuildConfig.FLAVOR, (Mesh) null, 0, 0, 0);
            renderable.shader = null;
            renderable.userData = null;
            return renderable;
        }
    }

    public ModelBatch(RenderContext context2, ShaderProvider shaderProvider2, RenderableSorter sorter2) {
        this.renderablesPool = new RenderablePool();
        this.renderables = new Array<>();
        this.sorter = sorter2 == null ? new DefaultRenderableSorter() : sorter2;
        this.ownContext = context2 == null;
        this.context = context2 == null ? new RenderContext(new DefaultTextureBinder(1, 1)) : context2;
        this.shaderProvider = shaderProvider2 == null ? new DefaultShaderProvider() : shaderProvider2;
    }

    public ModelBatch(RenderContext context2, ShaderProvider shaderProvider2) {
        this(context2, shaderProvider2, (RenderableSorter) null);
    }

    public ModelBatch(RenderContext context2, RenderableSorter sorter2) {
        this(context2, (ShaderProvider) null, sorter2);
    }

    public ModelBatch(RenderContext context2) {
        this(context2, (ShaderProvider) null, (RenderableSorter) null);
    }

    public ModelBatch(ShaderProvider shaderProvider2, RenderableSorter sorter2) {
        this((RenderContext) null, shaderProvider2, sorter2);
    }

    public ModelBatch(RenderableSorter sorter2) {
        this((RenderContext) null, (ShaderProvider) null, sorter2);
    }

    public ModelBatch(ShaderProvider shaderProvider2) {
        this((RenderContext) null, shaderProvider2, (RenderableSorter) null);
    }

    public ModelBatch(FileHandle vertexShader, FileHandle fragmentShader) {
        this((RenderContext) null, new DefaultShaderProvider(vertexShader, fragmentShader), (RenderableSorter) null);
    }

    public ModelBatch(String vertexShader, String fragmentShader) {
        this((RenderContext) null, new DefaultShaderProvider(vertexShader, fragmentShader), (RenderableSorter) null);
    }

    public ModelBatch() {
        this((RenderContext) null, (ShaderProvider) null, (RenderableSorter) null);
    }

    public void begin(Camera cam) {
        if (this.camera == null) {
            this.camera = cam;
            if (this.ownContext) {
                this.context.begin();
                return;
            }
            return;
        }
        throw new GdxRuntimeException("Call end() first.");
    }

    public void setCamera(Camera cam) {
        if (this.camera != null) {
            if (this.renderables.size > 0) {
                flush();
            }
            this.camera = cam;
            return;
        }
        throw new GdxRuntimeException("Call begin() first.");
    }

    public Camera getCamera() {
        return this.camera;
    }

    public boolean ownsRenderContext() {
        return this.ownContext;
    }

    public RenderContext getRenderContext() {
        return this.context;
    }

    public ShaderProvider getShaderProvider() {
        return this.shaderProvider;
    }

    public RenderableSorter getRenderableSorter() {
        return this.sorter;
    }

    public void flush() {
        this.sorter.sort(this.camera, this.renderables);
        Shader currentShader = null;
        for (int i = 0; i < this.renderables.size; i++) {
            Renderable renderable = this.renderables.get(i);
            if (currentShader != renderable.shader) {
                if (currentShader != null) {
                    currentShader.end();
                }
                currentShader = renderable.shader;
                currentShader.begin(this.camera, this.context);
            }
            currentShader.render(renderable);
        }
        if (currentShader != null) {
            currentShader.end();
        }
        this.renderablesPool.flush();
        this.renderables.clear();
    }

    public void end() {
        flush();
        if (this.ownContext) {
            this.context.end();
        }
        this.camera = null;
    }

    public void render(Renderable renderable) {
        renderable.shader = this.shaderProvider.getShader(renderable);
        this.renderables.add(renderable);
    }

    public void render(RenderableProvider renderableProvider) {
        int offset = this.renderables.size;
        renderableProvider.getRenderables(this.renderables, this.renderablesPool);
        for (int i = offset; i < this.renderables.size; i++) {
            Renderable renderable = this.renderables.get(i);
            renderable.shader = this.shaderProvider.getShader(renderable);
        }
    }

    public <T extends RenderableProvider> void render(Iterable<T> renderableProviders) {
        for (T renderableProvider : renderableProviders) {
            render((RenderableProvider) renderableProvider);
        }
    }

    public void render(RenderableProvider renderableProvider, Environment environment) {
        int offset = this.renderables.size;
        renderableProvider.getRenderables(this.renderables, this.renderablesPool);
        for (int i = offset; i < this.renderables.size; i++) {
            Renderable renderable = this.renderables.get(i);
            renderable.environment = environment;
            renderable.shader = this.shaderProvider.getShader(renderable);
        }
    }

    public <T extends RenderableProvider> void render(Iterable<T> renderableProviders, Environment environment) {
        for (T renderableProvider : renderableProviders) {
            render((RenderableProvider) renderableProvider, environment);
        }
    }

    public void render(RenderableProvider renderableProvider, Shader shader) {
        int offset = this.renderables.size;
        renderableProvider.getRenderables(this.renderables, this.renderablesPool);
        for (int i = offset; i < this.renderables.size; i++) {
            Renderable renderable = this.renderables.get(i);
            renderable.shader = shader;
            renderable.shader = this.shaderProvider.getShader(renderable);
        }
    }

    public <T extends RenderableProvider> void render(Iterable<T> renderableProviders, Shader shader) {
        for (T renderableProvider : renderableProviders) {
            render((RenderableProvider) renderableProvider, shader);
        }
    }

    public void render(RenderableProvider renderableProvider, Environment environment, Shader shader) {
        int offset = this.renderables.size;
        renderableProvider.getRenderables(this.renderables, this.renderablesPool);
        for (int i = offset; i < this.renderables.size; i++) {
            Renderable renderable = this.renderables.get(i);
            renderable.environment = environment;
            renderable.shader = shader;
            renderable.shader = this.shaderProvider.getShader(renderable);
        }
    }

    public <T extends RenderableProvider> void render(Iterable<T> renderableProviders, Environment environment, Shader shader) {
        for (T renderableProvider : renderableProviders) {
            render((RenderableProvider) renderableProvider, environment, shader);
        }
    }

    public void dispose() {
        this.shaderProvider.dispose();
    }
}
