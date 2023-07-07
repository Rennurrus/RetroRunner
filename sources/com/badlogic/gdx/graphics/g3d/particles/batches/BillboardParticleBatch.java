package com.badlogic.gdx.graphics.g3d.particles.batches;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.particles.ParallelArray;
import com.badlogic.gdx.graphics.g3d.particles.ParticleShader;
import com.badlogic.gdx.graphics.g3d.particles.ResourceData;
import com.badlogic.gdx.graphics.g3d.particles.renderers.BillboardControllerRenderData;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import java.util.Iterator;

public class BillboardParticleBatch extends BufferedParticleBatch<BillboardControllerRenderData> {
    private static final VertexAttributes CPU_ATTRIBUTES = new VertexAttributes(new VertexAttribute(1, 3, ShaderProgram.POSITION_ATTRIBUTE), new VertexAttribute(16, 2, "a_texCoord0"), new VertexAttribute(2, 4, ShaderProgram.COLOR_ATTRIBUTE));
    private static final int CPU_COLOR_OFFSET = ((short) (CPU_ATTRIBUTES.findByUsage(2).offset / 4));
    private static final int CPU_POSITION_OFFSET = ((short) (CPU_ATTRIBUTES.findByUsage(1).offset / 4));
    private static final int CPU_UV_OFFSET = ((short) (CPU_ATTRIBUTES.findByUsage(16).offset / 4));
    private static final int CPU_VERTEX_SIZE = (CPU_ATTRIBUTES.vertexSize / 4);
    private static final VertexAttributes GPU_ATTRIBUTES = new VertexAttributes(new VertexAttribute(1, 3, ShaderProgram.POSITION_ATTRIBUTE), new VertexAttribute(16, 2, "a_texCoord0"), new VertexAttribute(2, 4, ShaderProgram.COLOR_ATTRIBUTE), new VertexAttribute(512, 4, "a_sizeAndRotation"));
    private static final int GPU_COLOR_OFFSET = ((short) (GPU_ATTRIBUTES.findByUsage(2).offset / 4));
    private static final int GPU_POSITION_OFFSET = ((short) (GPU_ATTRIBUTES.findByUsage(1).offset / 4));
    private static final int GPU_SIZE_ROTATION_OFFSET = ((short) (GPU_ATTRIBUTES.findByUsage(512).offset / 4));
    private static final int GPU_UV_OFFSET = ((short) (GPU_ATTRIBUTES.findByUsage(16).offset / 4));
    private static final int GPU_VERTEX_SIZE = (GPU_ATTRIBUTES.vertexSize / 4);
    private static final int MAX_PARTICLES_PER_MESH = 8191;
    private static final int MAX_VERTICES_PER_MESH = 32764;
    protected static final Matrix3 TMP_M3 = new Matrix3();
    protected static final Vector3 TMP_V1 = new Vector3();
    protected static final Vector3 TMP_V2 = new Vector3();
    protected static final Vector3 TMP_V3 = new Vector3();
    protected static final Vector3 TMP_V4 = new Vector3();
    protected static final Vector3 TMP_V5 = new Vector3();
    protected static final Vector3 TMP_V6 = new Vector3();
    protected static final int directionUsage = 1024;
    protected static final int sizeAndRotationUsage = 512;
    protected BlendingAttribute blendingAttribute;
    private VertexAttributes currentAttributes;
    private int currentVertexSize;
    protected DepthTestAttribute depthTestAttribute;
    private short[] indices;
    protected ParticleShader.AlignMode mode;
    private RenderablePool renderablePool;
    private Array<Renderable> renderables;
    Shader shader;
    protected Texture texture;
    protected boolean useGPU;
    private float[] vertices;

    private class RenderablePool extends Pool<Renderable> {
        public RenderablePool() {
        }

        public Renderable newObject() {
            return BillboardParticleBatch.this.allocRenderable();
        }
    }

    public static class Config {
        ParticleShader.AlignMode mode;
        boolean useGPU;

        public Config() {
        }

        public Config(boolean useGPU2, ParticleShader.AlignMode mode2) {
            this.useGPU = useGPU2;
            this.mode = mode2;
        }
    }

    public BillboardParticleBatch(ParticleShader.AlignMode mode2, boolean useGPU2, int capacity, BlendingAttribute blendingAttribute2, DepthTestAttribute depthTestAttribute2) {
        super(BillboardControllerRenderData.class);
        this.currentVertexSize = 0;
        this.useGPU = false;
        this.mode = ParticleShader.AlignMode.Screen;
        this.renderables = new Array<>();
        this.renderablePool = new RenderablePool();
        this.blendingAttribute = blendingAttribute2;
        this.depthTestAttribute = depthTestAttribute2;
        if (this.blendingAttribute == null) {
            this.blendingAttribute = new BlendingAttribute(1, GL20.GL_ONE_MINUS_SRC_ALPHA, 1.0f);
        }
        if (this.depthTestAttribute == null) {
            this.depthTestAttribute = new DepthTestAttribute(GL20.GL_LEQUAL, false);
        }
        allocIndices();
        initRenderData();
        ensureCapacity(capacity);
        setUseGpu(useGPU2);
        setAlignMode(mode2);
    }

    public BillboardParticleBatch(ParticleShader.AlignMode mode2, boolean useGPU2, int capacity) {
        this(mode2, useGPU2, capacity, (BlendingAttribute) null, (DepthTestAttribute) null);
    }

    public BillboardParticleBatch() {
        this(ParticleShader.AlignMode.Screen, false, 100);
    }

    public BillboardParticleBatch(int capacity) {
        this(ParticleShader.AlignMode.Screen, false, capacity);
    }

    public void allocParticlesData(int capacity) {
        this.vertices = new float[(this.currentVertexSize * 4 * capacity)];
        allocRenderables(capacity);
    }

    /* access modifiers changed from: protected */
    public Renderable allocRenderable() {
        Renderable renderable = new Renderable();
        renderable.meshPart.primitiveType = 4;
        renderable.meshPart.offset = 0;
        renderable.material = new Material(this.blendingAttribute, this.depthTestAttribute, TextureAttribute.createDiffuse(this.texture));
        renderable.meshPart.mesh = new Mesh(false, (int) MAX_VERTICES_PER_MESH, 49146, this.currentAttributes);
        renderable.meshPart.mesh.setIndices(this.indices);
        renderable.shader = this.shader;
        return renderable;
    }

    private void allocIndices() {
        this.indices = new short[49146];
        int i = 0;
        int vertex = 0;
        while (i < 49146) {
            short[] sArr = this.indices;
            sArr[i] = (short) vertex;
            sArr[i + 1] = (short) (vertex + 1);
            sArr[i + 2] = (short) (vertex + 2);
            sArr[i + 3] = (short) (vertex + 2);
            sArr[i + 4] = (short) (vertex + 3);
            sArr[i + 5] = (short) vertex;
            i += 6;
            vertex += 4;
        }
    }

    private void allocRenderables(int capacity) {
        int meshCount = MathUtils.ceil((float) (capacity / MAX_PARTICLES_PER_MESH));
        int free = this.renderablePool.getFree();
        if (free < meshCount) {
            int left = meshCount - free;
            for (int i = 0; i < left; i++) {
                RenderablePool renderablePool2 = this.renderablePool;
                renderablePool2.free(renderablePool2.newObject());
            }
        }
    }

    /* access modifiers changed from: protected */
    public Shader getShader(Renderable renderable) {
        Shader shader2 = this.useGPU ? new ParticleShader(renderable, new ParticleShader.Config(this.mode)) : new DefaultShader(renderable);
        shader2.init();
        return shader2;
    }

    private void allocShader() {
        Renderable newRenderable = allocRenderable();
        Shader shader2 = getShader(newRenderable);
        newRenderable.shader = shader2;
        this.shader = shader2;
        this.renderablePool.free(newRenderable);
    }

    private void clearRenderablesPool() {
        this.renderablePool.freeAll(this.renderables);
        int free = this.renderablePool.getFree();
        for (int i = 0; i < free; i++) {
            ((Renderable) this.renderablePool.obtain()).meshPart.mesh.dispose();
        }
        this.renderables.clear();
    }

    public void setVertexData() {
        if (this.useGPU) {
            this.currentAttributes = GPU_ATTRIBUTES;
            this.currentVertexSize = GPU_VERTEX_SIZE;
            return;
        }
        this.currentAttributes = CPU_ATTRIBUTES;
        this.currentVertexSize = CPU_VERTEX_SIZE;
    }

    private void initRenderData() {
        setVertexData();
        clearRenderablesPool();
        allocShader();
        resetCapacity();
    }

    public void setAlignMode(ParticleShader.AlignMode mode2) {
        if (mode2 != this.mode) {
            this.mode = mode2;
            if (this.useGPU) {
                initRenderData();
                allocRenderables(this.bufferedParticlesCount);
            }
        }
    }

    public ParticleShader.AlignMode getAlignMode() {
        return this.mode;
    }

    public void setUseGpu(boolean useGPU2) {
        if (this.useGPU != useGPU2) {
            this.useGPU = useGPU2;
            initRenderData();
            allocRenderables(this.bufferedParticlesCount);
        }
    }

    public boolean isUseGPU() {
        return this.useGPU;
    }

    public void setTexture(Texture texture2) {
        this.renderablePool.freeAll(this.renderables);
        this.renderables.clear();
        int free = this.renderablePool.getFree();
        for (int i = 0; i < free; i++) {
            ((TextureAttribute) ((Renderable) this.renderablePool.obtain()).material.get(TextureAttribute.Diffuse)).textureDescription.texture = texture2;
        }
        this.texture = texture2;
    }

    public Texture getTexture() {
        return this.texture;
    }

    public void begin() {
        super.begin();
        this.renderablePool.freeAll(this.renderables);
        this.renderables.clear();
    }

    private static void putVertex(float[] vertices2, int offset, float x, float y, float z, float u, float v, float scaleX, float scaleY, float cosRotation, float sinRotation, float r, float g, float b, float a) {
        int i = GPU_POSITION_OFFSET;
        vertices2[offset + i] = x;
        vertices2[offset + i + 1] = y;
        vertices2[i + offset + 2] = z;
        int i2 = GPU_UV_OFFSET;
        vertices2[offset + i2] = u;
        vertices2[i2 + offset + 1] = v;
        int i3 = GPU_SIZE_ROTATION_OFFSET;
        vertices2[offset + i3] = scaleX;
        vertices2[offset + i3 + 1] = scaleY;
        vertices2[offset + i3 + 2] = cosRotation;
        vertices2[i3 + offset + 3] = sinRotation;
        int i4 = GPU_COLOR_OFFSET;
        vertices2[offset + i4] = r;
        vertices2[offset + i4 + 1] = g;
        vertices2[offset + i4 + 2] = b;
        vertices2[i4 + offset + 3] = a;
    }

    private static void putVertex(float[] vertices2, int offset, Vector3 p, float u, float v, float r, float g, float b, float a) {
        vertices2[CPU_POSITION_OFFSET + offset] = p.x;
        vertices2[CPU_POSITION_OFFSET + offset + 1] = p.y;
        vertices2[CPU_POSITION_OFFSET + offset + 2] = p.z;
        int i = CPU_UV_OFFSET;
        vertices2[offset + i] = u;
        vertices2[i + offset + 1] = v;
        int i2 = CPU_COLOR_OFFSET;
        vertices2[offset + i2] = r;
        vertices2[offset + i2 + 1] = g;
        vertices2[offset + i2 + 2] = b;
        vertices2[i2 + offset + 3] = a;
    }

    private void fillVerticesGPU(int[] particlesOffset) {
        int tp = 0;
        Iterator it = this.renderData.iterator();
        while (it.hasNext()) {
            BillboardControllerRenderData data = (BillboardControllerRenderData) it.next();
            ParallelArray.FloatChannel scaleChannel = data.scaleChannel;
            ParallelArray.FloatChannel regionChannel = data.regionChannel;
            ParallelArray.FloatChannel positionChannel = data.positionChannel;
            ParallelArray.FloatChannel colorChannel = data.colorChannel;
            ParallelArray.FloatChannel rotationChannel = data.rotationChannel;
            int p = 0;
            int c = data.controller.particles.size;
            while (p < c) {
                int baseOffset = particlesOffset[tp] * this.currentVertexSize * 4;
                float scale = scaleChannel.data[scaleChannel.strideSize * p];
                int regionOffset = p * regionChannel.strideSize;
                int positionOffset = p * positionChannel.strideSize;
                int colorOffset = p * colorChannel.strideSize;
                int rotationOffset = p * rotationChannel.strideSize;
                float px = positionChannel.data[positionOffset + 0];
                float py = positionChannel.data[positionOffset + 1];
                float pz = positionChannel.data[positionOffset + 2];
                float u = regionChannel.data[regionOffset + 0];
                float v = regionChannel.data[regionOffset + 1];
                float u2 = regionChannel.data[regionOffset + 2];
                float v2 = regionChannel.data[regionOffset + 3];
                float sx = regionChannel.data[regionOffset + 4] * scale;
                float sy = regionChannel.data[regionOffset + 5] * scale;
                float r = colorChannel.data[colorOffset + 0];
                float g = colorChannel.data[colorOffset + 1];
                float b = colorChannel.data[colorOffset + 2];
                float a = colorChannel.data[colorOffset + 3];
                float cosRotation = rotationChannel.data[rotationOffset + 0];
                float sinRotation = rotationChannel.data[rotationOffset + 1];
                Iterator it2 = it;
                float f = -sx;
                BillboardControllerRenderData data2 = data;
                float sy2 = sy;
                float sx2 = sx;
                putVertex(this.vertices, baseOffset, px, py, pz, u, v2, f, -sy, cosRotation, sinRotation, r, g, b, a);
                int baseOffset2 = this.currentVertexSize + baseOffset;
                float f2 = px;
                float f3 = py;
                float f4 = pz;
                float f5 = u2;
                float f6 = sx2;
                float f7 = cosRotation;
                float f8 = sinRotation;
                float f9 = r;
                float f10 = g;
                float f11 = b;
                float f12 = a;
                putVertex(this.vertices, baseOffset2, f2, f3, f4, f5, v2, f6, -sy2, f7, f8, f9, f10, f11, f12);
                int baseOffset3 = baseOffset2 + this.currentVertexSize;
                float f13 = v;
                float f14 = sy2;
                putVertex(this.vertices, baseOffset3, f2, f3, f4, f5, f13, f6, f14, f7, f8, f9, f10, f11, f12);
                putVertex(this.vertices, baseOffset3 + this.currentVertexSize, f2, f3, f4, u, f13, -sx2, f14, f7, f8, f9, f10, f11, f12);
                int i = this.currentVertexSize;
                p++;
                tp++;
                it = it2;
                data = data2;
            }
            Iterator it3 = it;
            BillboardControllerRenderData billboardControllerRenderData = data;
        }
    }

    private void fillVerticesToViewPointCPU(int[] particlesOffset) {
        int tp = 0;
        Iterator it = this.renderData.iterator();
        while (it.hasNext()) {
            BillboardControllerRenderData data = (BillboardControllerRenderData) it.next();
            ParallelArray.FloatChannel scaleChannel = data.scaleChannel;
            ParallelArray.FloatChannel regionChannel = data.regionChannel;
            ParallelArray.FloatChannel positionChannel = data.positionChannel;
            ParallelArray.FloatChannel colorChannel = data.colorChannel;
            ParallelArray.FloatChannel rotationChannel = data.rotationChannel;
            int baseOffset = 0;
            int c = data.controller.particles.size;
            while (baseOffset < c) {
                int baseOffset2 = particlesOffset[tp] * this.currentVertexSize * 4;
                float scale = scaleChannel.data[scaleChannel.strideSize * baseOffset];
                int regionOffset = baseOffset * regionChannel.strideSize;
                int positionOffset = baseOffset * positionChannel.strideSize;
                int colorOffset = baseOffset * colorChannel.strideSize;
                int rotationOffset = baseOffset * rotationChannel.strideSize;
                float px = positionChannel.data[positionOffset + 0];
                float py = positionChannel.data[positionOffset + 1];
                float pz = positionChannel.data[positionOffset + 2];
                float u = regionChannel.data[regionOffset + 0];
                float v = regionChannel.data[regionOffset + 1];
                float u2 = regionChannel.data[regionOffset + 2];
                float v2 = regionChannel.data[regionOffset + 3];
                float sx = regionChannel.data[regionOffset + 4] * scale;
                Iterator it2 = it;
                float sy = regionChannel.data[regionOffset + 5] * scale;
                BillboardControllerRenderData data2 = data;
                float r = colorChannel.data[colorOffset + 0];
                ParallelArray.FloatChannel scaleChannel2 = scaleChannel;
                float g = colorChannel.data[colorOffset + 1];
                ParallelArray.FloatChannel regionChannel2 = regionChannel;
                float b = colorChannel.data[colorOffset + 2];
                ParallelArray.FloatChannel positionChannel2 = positionChannel;
                float a = colorChannel.data[colorOffset + 3];
                ParallelArray.FloatChannel colorChannel2 = colorChannel;
                float cosRotation = rotationChannel.data[rotationOffset + 0];
                int c2 = c;
                float sinRotation = rotationChannel.data[rotationOffset + 1];
                ParallelArray.FloatChannel rotationChannel2 = rotationChannel;
                int tp2 = tp;
                Vector3 look = TMP_V3.set(this.camera.position).sub(px, py, pz).nor();
                int p = baseOffset;
                Vector3 right = TMP_V1.set(this.camera.up).crs(look).nor();
                Vector3 up = TMP_V2.set(look).crs(right);
                right.scl(sx);
                up.scl(sy);
                if (cosRotation != 1.0f) {
                    float f = sy;
                    TMP_M3.setToRotation(look, cosRotation, sinRotation);
                    float[] fArr = this.vertices;
                    Vector3 vector3 = look;
                    float f2 = cosRotation;
                    Vector3 vector32 = right;
                    Vector3 vector33 = up;
                    float f3 = sinRotation;
                    float f4 = sx;
                    float[] fArr2 = fArr;
                    float pz2 = pz;
                    float py2 = py;
                    Vector3 add = TMP_V6.set((-TMP_V1.x) - TMP_V2.x, (-TMP_V1.y) - TMP_V2.y, (-TMP_V1.z) - TMP_V2.z).mul(TMP_M3).add(px, py, pz);
                    float px2 = px;
                    putVertex(fArr2, baseOffset2, add, u, v2, r, g, b, a);
                    int baseOffset3 = this.currentVertexSize + baseOffset2;
                    float f5 = u2;
                    float f6 = r;
                    float f7 = g;
                    float f8 = b;
                    float f9 = a;
                    putVertex(this.vertices, baseOffset3, TMP_V6.set(TMP_V1.x - TMP_V2.x, TMP_V1.y - TMP_V2.y, TMP_V1.z - TMP_V2.z).mul(TMP_M3).add(px2, py2, pz2), f5, v2, f6, f7, f8, f9);
                    int baseOffset4 = baseOffset3 + this.currentVertexSize;
                    float f10 = v;
                    putVertex(this.vertices, baseOffset4, TMP_V6.set(TMP_V1.x + TMP_V2.x, TMP_V1.y + TMP_V2.y, TMP_V1.z + TMP_V2.z).mul(TMP_M3).add(px2, py2, pz2), f5, f10, f6, f7, f8, f9);
                    int baseOffset5 = baseOffset4 + this.currentVertexSize;
                    putVertex(this.vertices, baseOffset5, TMP_V6.set((-TMP_V1.x) + TMP_V2.x, (-TMP_V1.y) + TMP_V2.y, (-TMP_V1.z) + TMP_V2.z).mul(TMP_M3).add(px2, py2, pz2), u, f10, f6, f7, f8, f9);
                } else {
                    float f11 = sy;
                    float f12 = cosRotation;
                    Vector3 vector34 = right;
                    Vector3 vector35 = up;
                    float f13 = sinRotation;
                    float cosRotation2 = sx;
                    float sy2 = pz;
                    float py3 = py;
                    float px3 = px;
                    putVertex(this.vertices, baseOffset2, TMP_V6.set(((-TMP_V1.x) - TMP_V2.x) + px3, ((-TMP_V1.y) - TMP_V2.y) + py3, ((-TMP_V1.z) - TMP_V2.z) + sy2), u, v2, r, g, b, a);
                    int baseOffset6 = this.currentVertexSize + baseOffset2;
                    float f14 = u2;
                    float f15 = r;
                    float f16 = g;
                    float f17 = b;
                    float f18 = a;
                    putVertex(this.vertices, baseOffset6, TMP_V6.set((TMP_V1.x - TMP_V2.x) + px3, (TMP_V1.y - TMP_V2.y) + py3, (TMP_V1.z - TMP_V2.z) + sy2), f14, v2, f15, f16, f17, f18);
                    int baseOffset7 = baseOffset6 + this.currentVertexSize;
                    float f19 = v;
                    putVertex(this.vertices, baseOffset7, TMP_V6.set(TMP_V1.x + TMP_V2.x + px3, TMP_V1.y + TMP_V2.y + py3, TMP_V1.z + TMP_V2.z + sy2), f14, f19, f15, f16, f17, f18);
                    int baseOffset8 = baseOffset7 + this.currentVertexSize;
                    putVertex(this.vertices, baseOffset8, TMP_V6.set((-TMP_V1.x) + TMP_V2.x + px3, (-TMP_V1.y) + TMP_V2.y + py3, (-TMP_V1.z) + TMP_V2.z + sy2), u, f19, f15, f16, f17, f18);
                }
                baseOffset = p + 1;
                tp = tp2 + 1;
                it = it2;
                data = data2;
                scaleChannel = scaleChannel2;
                regionChannel = regionChannel2;
                positionChannel = positionChannel2;
                colorChannel = colorChannel2;
                c = c2;
                rotationChannel = rotationChannel2;
            }
            int i = tp;
            Iterator it3 = it;
            BillboardControllerRenderData billboardControllerRenderData = data;
            ParallelArray.FloatChannel floatChannel = scaleChannel;
            ParallelArray.FloatChannel floatChannel2 = regionChannel;
            ParallelArray.FloatChannel floatChannel3 = positionChannel;
            ParallelArray.FloatChannel floatChannel4 = colorChannel;
            ParallelArray.FloatChannel floatChannel5 = rotationChannel;
            int i2 = baseOffset;
            int i3 = c;
        }
    }

    private void fillVerticesToScreenCPU(int[] particlesOffset) {
        Vector3 up;
        Vector3 right;
        Vector3 look;
        Vector3 look2 = TMP_V3.set(this.camera.direction).scl(-1.0f);
        Vector3 right2 = TMP_V4.set(this.camera.up).crs(look2).nor();
        Vector3 up2 = this.camera.up;
        int tp = 0;
        Iterator it = this.renderData.iterator();
        while (it.hasNext()) {
            BillboardControllerRenderData data = (BillboardControllerRenderData) it.next();
            ParallelArray.FloatChannel scaleChannel = data.scaleChannel;
            ParallelArray.FloatChannel regionChannel = data.regionChannel;
            ParallelArray.FloatChannel positionChannel = data.positionChannel;
            ParallelArray.FloatChannel colorChannel = data.colorChannel;
            ParallelArray.FloatChannel rotationChannel = data.rotationChannel;
            int p = 0;
            int c = data.controller.particles.size;
            while (p < c) {
                int baseOffset = particlesOffset[tp] * this.currentVertexSize * 4;
                Iterator it2 = it;
                float scale = scaleChannel.data[scaleChannel.strideSize * p];
                int regionOffset = p * regionChannel.strideSize;
                int positionOffset = p * positionChannel.strideSize;
                int colorOffset = p * colorChannel.strideSize;
                int rotationOffset = p * rotationChannel.strideSize;
                float px = positionChannel.data[positionOffset + 0];
                BillboardControllerRenderData data2 = data;
                float py = positionChannel.data[positionOffset + 1];
                ParallelArray.FloatChannel scaleChannel2 = scaleChannel;
                float pz = positionChannel.data[positionOffset + 2];
                ParallelArray.FloatChannel positionChannel2 = positionChannel;
                float u = regionChannel.data[regionOffset + 0];
                int c2 = c;
                float v = regionChannel.data[regionOffset + 1];
                int tp2 = tp;
                float u2 = regionChannel.data[regionOffset + 2];
                int p2 = p;
                float v2 = regionChannel.data[regionOffset + 3];
                float v3 = v;
                float sx = regionChannel.data[regionOffset + 4] * scale;
                float u22 = u2;
                float sy = regionChannel.data[regionOffset + 5] * scale;
                float f = scale;
                float r = colorChannel.data[colorOffset + 0];
                ParallelArray.FloatChannel regionChannel2 = regionChannel;
                float g = colorChannel.data[colorOffset + 1];
                float b = colorChannel.data[colorOffset + 2];
                float a = colorChannel.data[colorOffset + 3];
                ParallelArray.FloatChannel colorChannel2 = colorChannel;
                float cosRotation = rotationChannel.data[rotationOffset + 0];
                float a2 = a;
                float sinRotation = rotationChannel.data[rotationOffset + 1];
                ParallelArray.FloatChannel rotationChannel2 = rotationChannel;
                TMP_V1.set(right2).scl(sx);
                TMP_V2.set(up2).scl(sy);
                if (cosRotation != 1.0f) {
                    TMP_M3.setToRotation(look2, cosRotation, sinRotation);
                    look = look2;
                    right = right2;
                    up = up2;
                    float f2 = sy;
                    float f3 = sinRotation;
                    float px2 = px;
                    putVertex(this.vertices, baseOffset, TMP_V6.set((-TMP_V1.x) - TMP_V2.x, (-TMP_V1.y) - TMP_V2.y, (-TMP_V1.z) - TMP_V2.z).mul(TMP_M3).add(px, py, pz), u, v2, r, g, b, a2);
                    int baseOffset2 = this.currentVertexSize + baseOffset;
                    float f4 = u22;
                    float f5 = r;
                    float f6 = g;
                    float f7 = b;
                    float f8 = a2;
                    putVertex(this.vertices, baseOffset2, TMP_V6.set(TMP_V1.x - TMP_V2.x, TMP_V1.y - TMP_V2.y, TMP_V1.z - TMP_V2.z).mul(TMP_M3).add(px2, py, pz), f4, v2, f5, f6, f7, f8);
                    int baseOffset3 = baseOffset2 + this.currentVertexSize;
                    float f9 = v3;
                    putVertex(this.vertices, baseOffset3, TMP_V6.set(TMP_V1.x + TMP_V2.x, TMP_V1.y + TMP_V2.y, TMP_V1.z + TMP_V2.z).mul(TMP_M3).add(px2, py, pz), f4, f9, f5, f6, f7, f8);
                    int baseOffset4 = baseOffset3 + this.currentVertexSize;
                    putVertex(this.vertices, baseOffset4, TMP_V6.set((-TMP_V1.x) + TMP_V2.x, (-TMP_V1.y) + TMP_V2.y, (-TMP_V1.z) + TMP_V2.z).mul(TMP_M3).add(px2, py, pz), u, f9, f5, f6, f7, f8);
                } else {
                    look = look2;
                    right = right2;
                    up = up2;
                    float f10 = sy;
                    float f11 = sinRotation;
                    float px3 = px;
                    putVertex(this.vertices, baseOffset, TMP_V6.set(((-TMP_V1.x) - TMP_V2.x) + px3, ((-TMP_V1.y) - TMP_V2.y) + py, ((-TMP_V1.z) - TMP_V2.z) + pz), u, v2, r, g, b, a2);
                    int baseOffset5 = this.currentVertexSize + baseOffset;
                    float f12 = u22;
                    float f13 = r;
                    float f14 = g;
                    float f15 = b;
                    float f16 = a2;
                    putVertex(this.vertices, baseOffset5, TMP_V6.set((TMP_V1.x - TMP_V2.x) + px3, (TMP_V1.y - TMP_V2.y) + py, (TMP_V1.z - TMP_V2.z) + pz), f12, v2, f13, f14, f15, f16);
                    int baseOffset6 = baseOffset5 + this.currentVertexSize;
                    float f17 = v3;
                    putVertex(this.vertices, baseOffset6, TMP_V6.set(TMP_V1.x + TMP_V2.x + px3, TMP_V1.y + TMP_V2.y + py, TMP_V1.z + TMP_V2.z + pz), f12, f17, f13, f14, f15, f16);
                    int baseOffset7 = baseOffset6 + this.currentVertexSize;
                    putVertex(this.vertices, baseOffset7, TMP_V6.set((-TMP_V1.x) + TMP_V2.x + px3, (-TMP_V1.y) + TMP_V2.y + py, (-TMP_V1.z) + TMP_V2.z + pz), u, f17, f13, f14, f15, f16);
                }
                p = p2 + 1;
                tp = tp2 + 1;
                it = it2;
                data = data2;
                scaleChannel = scaleChannel2;
                positionChannel = positionChannel2;
                c = c2;
                regionChannel = regionChannel2;
                colorChannel = colorChannel2;
                rotationChannel = rotationChannel2;
                look2 = look;
                right2 = right;
                up2 = up;
            }
            Vector3 vector3 = right2;
            Vector3 vector32 = up2;
            int i = tp;
            Iterator it3 = it;
            BillboardControllerRenderData billboardControllerRenderData = data;
            ParallelArray.FloatChannel floatChannel = scaleChannel;
            ParallelArray.FloatChannel floatChannel2 = regionChannel;
            ParallelArray.FloatChannel floatChannel3 = positionChannel;
            ParallelArray.FloatChannel floatChannel4 = colorChannel;
            ParallelArray.FloatChannel floatChannel5 = rotationChannel;
            int i2 = p;
            int i3 = c;
        }
        Vector3 vector33 = right2;
        Vector3 vector34 = up2;
    }

    /* access modifiers changed from: protected */
    public void flush(int[] offsets) {
        if (this.useGPU) {
            fillVerticesGPU(offsets);
        } else if (this.mode == ParticleShader.AlignMode.Screen) {
            fillVerticesToScreenCPU(offsets);
        } else if (this.mode == ParticleShader.AlignMode.ViewPoint) {
            fillVerticesToViewPointCPU(offsets);
        }
        int vCount = this.bufferedParticlesCount * 4;
        int v = 0;
        while (v < vCount) {
            int addedVertexCount = Math.min(vCount - v, MAX_VERTICES_PER_MESH);
            Renderable renderable = (Renderable) this.renderablePool.obtain();
            renderable.meshPart.size = (addedVertexCount / 4) * 6;
            Mesh mesh = renderable.meshPart.mesh;
            float[] fArr = this.vertices;
            int i = this.currentVertexSize;
            mesh.setVertices(fArr, i * v, i * addedVertexCount);
            renderable.meshPart.update();
            this.renderables.add(renderable);
            v += addedVertexCount;
        }
    }

    public void getRenderables(Array<Renderable> renderables2, Pool<Renderable> pool) {
        Iterator<Renderable> it = this.renderables.iterator();
        while (it.hasNext()) {
            renderables2.add(pool.obtain().set(it.next()));
        }
    }

    public void save(AssetManager manager, ResourceData resources) {
        ResourceData.SaveData data = resources.createSaveData("billboardBatch");
        data.save("cfg", new Config(this.useGPU, this.mode));
        data.saveAsset(manager.getAssetFileName(this.texture), Texture.class);
    }

    public void load(AssetManager manager, ResourceData resources) {
        ResourceData.SaveData data = resources.getSaveData("billboardBatch");
        if (data != null) {
            setTexture((Texture) manager.get(data.loadAsset()));
            Config cfg = (Config) data.load("cfg");
            setUseGpu(cfg.useGPU);
            setAlignMode(cfg.mode);
        }
    }
}
