package com.badlogic.gdx.graphics.g3d.particles;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.twi.game.BuildConfig;
import java.util.Iterator;

public class ParticleShader extends BaseShader {
    static final Vector3 TMP_VECTOR3 = new Vector3();
    private static String defaultFragmentShader = null;
    private static String defaultVertexShader = null;
    protected static long implementedFlags = (BlendingAttribute.Type | TextureAttribute.Diffuse);
    private static final long optionalAttributes = (IntAttribute.CullFace | DepthTestAttribute.Type);
    protected final Config config;
    Material currentMaterial;
    private long materialMask;
    private Renderable renderable;
    private long vertexMask;

    public enum AlignMode {
        Screen,
        ViewPoint
    }

    public static class Inputs {
        public static final BaseShader.Uniform cameraInvDirection = new BaseShader.Uniform("u_cameraInvDirection");
        public static final BaseShader.Uniform cameraRight = new BaseShader.Uniform("u_cameraRight");
        public static final BaseShader.Uniform regionSize = new BaseShader.Uniform("u_regionSize");
        public static final BaseShader.Uniform screenWidth = new BaseShader.Uniform("u_screenWidth");
    }

    public enum ParticleType {
        Billboard,
        Point
    }

    public static class Setters {
        public static final BaseShader.Setter cameraInvDirection = new BaseShader.Setter() {
            public boolean isGlobal(BaseShader shader, int inputID) {
                return true;
            }

            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, ParticleShader.TMP_VECTOR3.set(-shader.camera.direction.x, -shader.camera.direction.y, -shader.camera.direction.z).nor());
            }
        };
        public static final BaseShader.Setter cameraPosition = new BaseShader.Setter() {
            public boolean isGlobal(BaseShader shader, int inputID) {
                return true;
            }

            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, shader.camera.position);
            }
        };
        public static final BaseShader.Setter cameraRight = new BaseShader.Setter() {
            public boolean isGlobal(BaseShader shader, int inputID) {
                return true;
            }

            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, ParticleShader.TMP_VECTOR3.set(shader.camera.direction).crs(shader.camera.up).nor());
            }
        };
        public static final BaseShader.Setter cameraUp = new BaseShader.Setter() {
            public boolean isGlobal(BaseShader shader, int inputID) {
                return true;
            }

            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, ParticleShader.TMP_VECTOR3.set(shader.camera.up).nor());
            }
        };
        public static final BaseShader.Setter screenWidth = new BaseShader.Setter() {
            public boolean isGlobal(BaseShader shader, int inputID) {
                return true;
            }

            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, (float) Gdx.graphics.getWidth());
            }
        };
        public static final BaseShader.Setter worldViewTrans = new BaseShader.Setter() {
            final Matrix4 temp = new Matrix4();

            public boolean isGlobal(BaseShader shader, int inputID) {
                return false;
            }

            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, this.temp.set(shader.camera.view).mul(renderable.worldTransform));
            }
        };
    }

    public static class Config {
        public AlignMode align = AlignMode.Screen;
        public int defaultCullFace = -1;
        public int defaultDepthFunc = -1;
        public String fragmentShader = null;
        public boolean ignoreUnimplemented = true;
        public ParticleType type = ParticleType.Billboard;
        public String vertexShader = null;

        public Config() {
        }

        public Config(AlignMode align2, ParticleType type2) {
            this.align = align2;
            this.type = type2;
        }

        public Config(AlignMode align2) {
            this.align = align2;
        }

        public Config(ParticleType type2) {
            this.type = type2;
        }

        public Config(String vertexShader2, String fragmentShader2) {
            this.vertexShader = vertexShader2;
            this.fragmentShader = fragmentShader2;
        }
    }

    public static String getDefaultVertexShader() {
        if (defaultVertexShader == null) {
            defaultVertexShader = Gdx.files.classpath("com/badlogic/gdx/graphics/g3d/particles/particles.vertex.glsl").readString();
        }
        return defaultVertexShader;
    }

    public static String getDefaultFragmentShader() {
        if (defaultFragmentShader == null) {
            defaultFragmentShader = Gdx.files.classpath("com/badlogic/gdx/graphics/g3d/particles/particles.fragment.glsl").readString();
        }
        return defaultFragmentShader;
    }

    public ParticleShader(Renderable renderable2) {
        this(renderable2, new Config());
    }

    public ParticleShader(Renderable renderable2, Config config2) {
        this(renderable2, config2, createPrefix(renderable2, config2));
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public ParticleShader(com.badlogic.gdx.graphics.g3d.Renderable r8, com.badlogic.gdx.graphics.g3d.particles.ParticleShader.Config r9, java.lang.String r10) {
        /*
            r7 = this;
            java.lang.String r0 = r9.vertexShader
            if (r0 == 0) goto L_0x0007
            java.lang.String r0 = r9.vertexShader
            goto L_0x000b
        L_0x0007:
            java.lang.String r0 = getDefaultVertexShader()
        L_0x000b:
            r5 = r0
            java.lang.String r0 = r9.fragmentShader
            if (r0 == 0) goto L_0x0014
            java.lang.String r0 = r9.fragmentShader
            r6 = r0
            goto L_0x0019
        L_0x0014:
            java.lang.String r0 = getDefaultFragmentShader()
            r6 = r0
        L_0x0019:
            r1 = r7
            r2 = r8
            r3 = r9
            r4 = r10
            r1.<init>(r2, r3, r4, r5, r6)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.graphics.g3d.particles.ParticleShader.<init>(com.badlogic.gdx.graphics.g3d.Renderable, com.badlogic.gdx.graphics.g3d.particles.ParticleShader$Config, java.lang.String):void");
    }

    public ParticleShader(Renderable renderable2, Config config2, String prefix, String vertexShader, String fragmentShader) {
        this(renderable2, config2, new ShaderProgram(prefix + vertexShader, prefix + fragmentShader));
    }

    public ParticleShader(Renderable renderable2, Config config2, ShaderProgram shaderProgram) {
        this.config = config2;
        this.program = shaderProgram;
        this.renderable = renderable2;
        this.materialMask = renderable2.material.getMask() | optionalAttributes;
        this.vertexMask = renderable2.meshPart.mesh.getVertexAttributes().getMask();
        if (!config2.ignoreUnimplemented) {
            long j = implementedFlags;
            long j2 = this.materialMask;
            if ((j & j2) != j2) {
                throw new GdxRuntimeException("Some attributes not implemented yet (" + this.materialMask + ")");
            }
        }
        register(DefaultShader.Inputs.viewTrans, DefaultShader.Setters.viewTrans);
        register(DefaultShader.Inputs.projViewTrans, DefaultShader.Setters.projViewTrans);
        register(DefaultShader.Inputs.projTrans, DefaultShader.Setters.projTrans);
        register(Inputs.screenWidth, Setters.screenWidth);
        register(DefaultShader.Inputs.cameraUp, Setters.cameraUp);
        register(Inputs.cameraRight, Setters.cameraRight);
        register(Inputs.cameraInvDirection, Setters.cameraInvDirection);
        register(DefaultShader.Inputs.cameraPosition, Setters.cameraPosition);
        register(DefaultShader.Inputs.diffuseTexture, DefaultShader.Setters.diffuseTexture);
    }

    public void init() {
        ShaderProgram program = this.program;
        this.program = null;
        init(program, this.renderable);
        this.renderable = null;
    }

    public static String createPrefix(Renderable renderable2, Config config2) {
        String prefix;
        if (Gdx.app.getType() == Application.ApplicationType.Desktop) {
            prefix = BuildConfig.FLAVOR + "#version 120\n";
        } else {
            prefix = BuildConfig.FLAVOR + "#version 100\n";
        }
        if (config2.type != ParticleType.Billboard) {
            return prefix;
        }
        String prefix2 = prefix + "#define billboard\n";
        if (config2.align == AlignMode.Screen) {
            return prefix2 + "#define screenFacing\n";
        } else if (config2.align != AlignMode.ViewPoint) {
            return prefix2;
        } else {
            return prefix2 + "#define viewPointFacing\n";
        }
    }

    public boolean canRender(Renderable renderable2) {
        return this.materialMask == (renderable2.material.getMask() | optionalAttributes) && this.vertexMask == renderable2.meshPart.mesh.getVertexAttributes().getMask();
    }

    public int compareTo(Shader other) {
        if (other == null) {
            return -1;
        }
        return other == this ? 0 : 0;
    }

    public boolean equals(Object obj) {
        return (obj instanceof ParticleShader) && equals((ParticleShader) obj);
    }

    public boolean equals(ParticleShader obj) {
        return obj == this;
    }

    public void begin(Camera camera, RenderContext context) {
        super.begin(camera, context);
    }

    public void render(Renderable renderable2) {
        if (!renderable2.material.has(BlendingAttribute.Type)) {
            this.context.setBlending(false, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }
        bindMaterial(renderable2);
        super.render(renderable2);
    }

    public void end() {
        this.currentMaterial = null;
        super.end();
    }

    /* access modifiers changed from: protected */
    public void bindMaterial(Renderable renderable2) {
        if (this.currentMaterial != renderable2.material) {
            int cullFace = this.config.defaultCullFace == -1 ? GL20.GL_BACK : this.config.defaultCullFace;
            int depthFunc = this.config.defaultDepthFunc == -1 ? GL20.GL_LEQUAL : this.config.defaultDepthFunc;
            float depthRangeNear = 0.0f;
            float depthRangeFar = 1.0f;
            boolean depthMask = true;
            this.currentMaterial = renderable2.material;
            Iterator<Attribute> it = this.currentMaterial.iterator();
            while (it.hasNext()) {
                Attribute attr = it.next();
                long t = attr.type;
                if (BlendingAttribute.is(t)) {
                    this.context.setBlending(true, ((BlendingAttribute) attr).sourceFunction, ((BlendingAttribute) attr).destFunction);
                } else if ((DepthTestAttribute.Type & t) == DepthTestAttribute.Type) {
                    DepthTestAttribute dta = (DepthTestAttribute) attr;
                    depthFunc = dta.depthFunc;
                    depthRangeNear = dta.depthRangeNear;
                    depthRangeFar = dta.depthRangeFar;
                    depthMask = dta.depthMask;
                } else if (!this.config.ignoreUnimplemented) {
                    throw new GdxRuntimeException("Unknown material attribute: " + attr.toString());
                }
            }
            this.context.setCullFace(cullFace);
            this.context.setDepthTest(depthFunc, depthRangeNear, depthRangeFar);
            this.context.setDepthMask(depthMask);
        }
    }

    public void dispose() {
        this.program.dispose();
        super.dispose();
    }

    public int getDefaultCullFace() {
        return this.config.defaultCullFace == -1 ? GL20.GL_BACK : this.config.defaultCullFace;
    }

    public void setDefaultCullFace(int cullFace) {
        this.config.defaultCullFace = cullFace;
    }

    public int getDefaultDepthFunc() {
        return this.config.defaultDepthFunc == -1 ? GL20.GL_LEQUAL : this.config.defaultDepthFunc;
    }

    public void setDefaultDepthFunc(int depthFunc) {
        this.config.defaultDepthFunc = depthFunc;
    }
}
