package com.badlogic.gdx.graphics.g3d.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.CubemapAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DirectionalLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.PointLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.SpotLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.AmbientCubemap;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.environment.SpotLight;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.twi.game.BuildConfig;
import java.util.Iterator;

public class DefaultShader extends BaseShader {
    @Deprecated
    public static int defaultCullFace = GL20.GL_BACK;
    @Deprecated
    public static int defaultDepthFunc = GL20.GL_LEQUAL;
    private static String defaultFragmentShader = null;
    private static String defaultVertexShader = null;
    protected static long implementedFlags = ((((BlendingAttribute.Type | TextureAttribute.Diffuse) | ColorAttribute.Diffuse) | ColorAttribute.Specular) | FloatAttribute.Shininess);
    private static final long optionalAttributes = (IntAttribute.CullFace | DepthTestAttribute.Type);
    private static final Attributes tmpAttributes = new Attributes();
    protected final AmbientCubemap ambientCubemap;
    protected final long attributesMask;
    protected final Config config;
    protected int dirLightsColorOffset;
    protected int dirLightsDirectionOffset;
    protected int dirLightsLoc;
    protected int dirLightsSize;
    protected final DirectionalLight[] directionalLights;
    protected final boolean environmentCubemap;
    protected final boolean lighting;
    private boolean lightsSet;
    private final Matrix3 normalMatrix;
    protected final PointLight[] pointLights;
    protected int pointLightsColorOffset;
    protected int pointLightsIntensityOffset;
    protected int pointLightsLoc;
    protected int pointLightsPositionOffset;
    protected int pointLightsSize;
    private Renderable renderable;
    protected final boolean shadowMap;
    protected final SpotLight[] spotLights;
    protected int spotLightsColorOffset;
    protected int spotLightsCutoffAngleOffset;
    protected int spotLightsDirectionOffset;
    protected int spotLightsExponentOffset;
    protected int spotLightsIntensityOffset;
    protected int spotLightsLoc;
    protected int spotLightsPositionOffset;
    protected int spotLightsSize;
    private float time;
    private final Vector3 tmpV1;
    public final int u_alphaTest;
    protected final int u_ambientCubemap;
    public final int u_ambientTexture;
    public final int u_ambientUVTransform;
    public final int u_bones;
    public final int u_cameraDirection;
    public final int u_cameraNearFar;
    public final int u_cameraPosition;
    public final int u_cameraUp;
    public final int u_diffuseColor;
    public final int u_diffuseTexture;
    public final int u_diffuseUVTransform;
    protected final int u_dirLights0color;
    protected final int u_dirLights0direction;
    protected final int u_dirLights1color;
    public final int u_emissiveColor;
    public final int u_emissiveTexture;
    public final int u_emissiveUVTransform;
    protected final int u_environmentCubemap;
    protected final int u_fogColor;
    public final int u_normalMatrix;
    public final int u_normalTexture;
    public final int u_normalUVTransform;
    public final int u_opacity;
    protected final int u_pointLights0color;
    protected final int u_pointLights0intensity;
    protected final int u_pointLights0position;
    protected final int u_pointLights1color;
    public final int u_projTrans;
    public final int u_projViewTrans;
    public final int u_projViewWorldTrans;
    public final int u_reflectionColor;
    public final int u_reflectionTexture;
    public final int u_reflectionUVTransform;
    protected final int u_shadowMapProjViewTrans;
    protected final int u_shadowPCFOffset;
    protected final int u_shadowTexture;
    public final int u_shininess;
    public final int u_specularColor;
    public final int u_specularTexture;
    public final int u_specularUVTransform;
    protected final int u_spotLights0color;
    protected final int u_spotLights0cutoffAngle;
    protected final int u_spotLights0direction;
    protected final int u_spotLights0exponent;
    protected final int u_spotLights0intensity;
    protected final int u_spotLights0position;
    protected final int u_spotLights1color;
    public final int u_time;
    public final int u_viewTrans;
    public final int u_viewWorldTrans;
    public final int u_worldTrans;
    private final long vertexMask;

    public static class Inputs {
        public static final BaseShader.Uniform alphaTest = new BaseShader.Uniform("u_alphaTest");
        public static final BaseShader.Uniform ambientCube = new BaseShader.Uniform("u_ambientCubemap");
        public static final BaseShader.Uniform ambientTexture = new BaseShader.Uniform("u_ambientTexture", TextureAttribute.Ambient);
        public static final BaseShader.Uniform ambientUVTransform = new BaseShader.Uniform("u_ambientUVTransform", TextureAttribute.Ambient);
        public static final BaseShader.Uniform bones = new BaseShader.Uniform("u_bones");
        public static final BaseShader.Uniform cameraDirection = new BaseShader.Uniform("u_cameraDirection");
        public static final BaseShader.Uniform cameraNearFar = new BaseShader.Uniform("u_cameraNearFar");
        public static final BaseShader.Uniform cameraPosition = new BaseShader.Uniform("u_cameraPosition");
        public static final BaseShader.Uniform cameraUp = new BaseShader.Uniform("u_cameraUp");
        public static final BaseShader.Uniform diffuseColor = new BaseShader.Uniform("u_diffuseColor", ColorAttribute.Diffuse);
        public static final BaseShader.Uniform diffuseTexture = new BaseShader.Uniform("u_diffuseTexture", TextureAttribute.Diffuse);
        public static final BaseShader.Uniform diffuseUVTransform = new BaseShader.Uniform("u_diffuseUVTransform", TextureAttribute.Diffuse);
        public static final BaseShader.Uniform dirLights = new BaseShader.Uniform("u_dirLights");
        public static final BaseShader.Uniform emissiveColor = new BaseShader.Uniform("u_emissiveColor", ColorAttribute.Emissive);
        public static final BaseShader.Uniform emissiveTexture = new BaseShader.Uniform("u_emissiveTexture", TextureAttribute.Emissive);
        public static final BaseShader.Uniform emissiveUVTransform = new BaseShader.Uniform("u_emissiveUVTransform", TextureAttribute.Emissive);
        public static final BaseShader.Uniform environmentCubemap = new BaseShader.Uniform("u_environmentCubemap");
        public static final BaseShader.Uniform normalMatrix = new BaseShader.Uniform("u_normalMatrix");
        public static final BaseShader.Uniform normalTexture = new BaseShader.Uniform("u_normalTexture", TextureAttribute.Normal);
        public static final BaseShader.Uniform normalUVTransform = new BaseShader.Uniform("u_normalUVTransform", TextureAttribute.Normal);
        public static final BaseShader.Uniform opacity = new BaseShader.Uniform("u_opacity", BlendingAttribute.Type);
        public static final BaseShader.Uniform pointLights = new BaseShader.Uniform("u_pointLights");
        public static final BaseShader.Uniform projTrans = new BaseShader.Uniform("u_projTrans");
        public static final BaseShader.Uniform projViewTrans = new BaseShader.Uniform("u_projViewTrans");
        public static final BaseShader.Uniform projViewWorldTrans = new BaseShader.Uniform("u_projViewWorldTrans");
        public static final BaseShader.Uniform reflectionColor = new BaseShader.Uniform("u_reflectionColor", ColorAttribute.Reflection);
        public static final BaseShader.Uniform reflectionTexture = new BaseShader.Uniform("u_reflectionTexture", TextureAttribute.Reflection);
        public static final BaseShader.Uniform reflectionUVTransform = new BaseShader.Uniform("u_reflectionUVTransform", TextureAttribute.Reflection);
        public static final BaseShader.Uniform shininess = new BaseShader.Uniform("u_shininess", FloatAttribute.Shininess);
        public static final BaseShader.Uniform specularColor = new BaseShader.Uniform("u_specularColor", ColorAttribute.Specular);
        public static final BaseShader.Uniform specularTexture = new BaseShader.Uniform("u_specularTexture", TextureAttribute.Specular);
        public static final BaseShader.Uniform specularUVTransform = new BaseShader.Uniform("u_specularUVTransform", TextureAttribute.Specular);
        public static final BaseShader.Uniform spotLights = new BaseShader.Uniform("u_spotLights");
        public static final BaseShader.Uniform viewTrans = new BaseShader.Uniform("u_viewTrans");
        public static final BaseShader.Uniform viewWorldTrans = new BaseShader.Uniform("u_viewWorldTrans");
        public static final BaseShader.Uniform worldTrans = new BaseShader.Uniform("u_worldTrans");
    }

    public static class Config {
        public int defaultCullFace = -1;
        public int defaultDepthFunc = -1;
        public String fragmentShader = null;
        public boolean ignoreUnimplemented = true;
        public int numBones = 12;
        public int numDirectionalLights = 2;
        public int numPointLights = 5;
        public int numSpotLights = 0;
        public String vertexShader = null;

        public Config() {
        }

        public Config(String vertexShader2, String fragmentShader2) {
            this.vertexShader = vertexShader2;
            this.fragmentShader = fragmentShader2;
        }
    }

    public static class Setters {
        public static final BaseShader.Setter ambientTexture = new BaseShader.LocalSetter() {
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, shader.context.textureBinder.bind((TextureDescriptor) ((TextureAttribute) combinedAttributes.get(TextureAttribute.Ambient)).textureDescription));
            }
        };
        public static final BaseShader.Setter ambientUVTransform = new BaseShader.LocalSetter() {
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                TextureAttribute ta = (TextureAttribute) combinedAttributes.get(TextureAttribute.Ambient);
                shader.set(inputID, ta.offsetU, ta.offsetV, ta.scaleU, ta.scaleV);
            }
        };
        public static final BaseShader.Setter cameraDirection = new BaseShader.GlobalSetter() {
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, shader.camera.direction);
            }
        };
        public static final BaseShader.Setter cameraNearFar = new BaseShader.GlobalSetter() {
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, shader.camera.near, shader.camera.far);
            }
        };
        public static final BaseShader.Setter cameraPosition = new BaseShader.GlobalSetter() {
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, shader.camera.position.x, shader.camera.position.y, shader.camera.position.z, 1.1881f / (shader.camera.far * shader.camera.far));
            }
        };
        public static final BaseShader.Setter cameraUp = new BaseShader.GlobalSetter() {
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, shader.camera.up);
            }
        };
        public static final BaseShader.Setter diffuseColor = new BaseShader.LocalSetter() {
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, ((ColorAttribute) combinedAttributes.get(ColorAttribute.Diffuse)).color);
            }
        };
        public static final BaseShader.Setter diffuseTexture = new BaseShader.LocalSetter() {
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, shader.context.textureBinder.bind((TextureDescriptor) ((TextureAttribute) combinedAttributes.get(TextureAttribute.Diffuse)).textureDescription));
            }
        };
        public static final BaseShader.Setter diffuseUVTransform = new BaseShader.LocalSetter() {
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                TextureAttribute ta = (TextureAttribute) combinedAttributes.get(TextureAttribute.Diffuse);
                shader.set(inputID, ta.offsetU, ta.offsetV, ta.scaleU, ta.scaleV);
            }
        };
        public static final BaseShader.Setter emissiveColor = new BaseShader.LocalSetter() {
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, ((ColorAttribute) combinedAttributes.get(ColorAttribute.Emissive)).color);
            }
        };
        public static final BaseShader.Setter emissiveTexture = new BaseShader.LocalSetter() {
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, shader.context.textureBinder.bind((TextureDescriptor) ((TextureAttribute) combinedAttributes.get(TextureAttribute.Emissive)).textureDescription));
            }
        };
        public static final BaseShader.Setter emissiveUVTransform = new BaseShader.LocalSetter() {
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                TextureAttribute ta = (TextureAttribute) combinedAttributes.get(TextureAttribute.Emissive);
                shader.set(inputID, ta.offsetU, ta.offsetV, ta.scaleU, ta.scaleV);
            }
        };
        public static final BaseShader.Setter environmentCubemap = new BaseShader.LocalSetter() {
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                if (combinedAttributes.has(CubemapAttribute.EnvironmentMap)) {
                    shader.set(inputID, shader.context.textureBinder.bind((TextureDescriptor) ((CubemapAttribute) combinedAttributes.get(CubemapAttribute.EnvironmentMap)).textureDescription));
                }
            }
        };
        public static final BaseShader.Setter normalMatrix = new BaseShader.LocalSetter() {
            private final Matrix3 tmpM = new Matrix3();

            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, this.tmpM.set(renderable.worldTransform).inv().transpose());
            }
        };
        public static final BaseShader.Setter normalTexture = new BaseShader.LocalSetter() {
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, shader.context.textureBinder.bind((TextureDescriptor) ((TextureAttribute) combinedAttributes.get(TextureAttribute.Normal)).textureDescription));
            }
        };
        public static final BaseShader.Setter normalUVTransform = new BaseShader.LocalSetter() {
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                TextureAttribute ta = (TextureAttribute) combinedAttributes.get(TextureAttribute.Normal);
                shader.set(inputID, ta.offsetU, ta.offsetV, ta.scaleU, ta.scaleV);
            }
        };
        public static final BaseShader.Setter projTrans = new BaseShader.GlobalSetter() {
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, shader.camera.projection);
            }
        };
        public static final BaseShader.Setter projViewTrans = new BaseShader.GlobalSetter() {
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, shader.camera.combined);
            }
        };
        public static final BaseShader.Setter projViewWorldTrans = new BaseShader.LocalSetter() {
            final Matrix4 temp = new Matrix4();

            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, this.temp.set(shader.camera.combined).mul(renderable.worldTransform));
            }
        };
        public static final BaseShader.Setter reflectionColor = new BaseShader.LocalSetter() {
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, ((ColorAttribute) combinedAttributes.get(ColorAttribute.Reflection)).color);
            }
        };
        public static final BaseShader.Setter reflectionTexture = new BaseShader.LocalSetter() {
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, shader.context.textureBinder.bind((TextureDescriptor) ((TextureAttribute) combinedAttributes.get(TextureAttribute.Reflection)).textureDescription));
            }
        };
        public static final BaseShader.Setter reflectionUVTransform = new BaseShader.LocalSetter() {
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                TextureAttribute ta = (TextureAttribute) combinedAttributes.get(TextureAttribute.Reflection);
                shader.set(inputID, ta.offsetU, ta.offsetV, ta.scaleU, ta.scaleV);
            }
        };
        public static final BaseShader.Setter shininess = new BaseShader.LocalSetter() {
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, ((FloatAttribute) combinedAttributes.get(FloatAttribute.Shininess)).value);
            }
        };
        public static final BaseShader.Setter specularColor = new BaseShader.LocalSetter() {
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, ((ColorAttribute) combinedAttributes.get(ColorAttribute.Specular)).color);
            }
        };
        public static final BaseShader.Setter specularTexture = new BaseShader.LocalSetter() {
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, shader.context.textureBinder.bind((TextureDescriptor) ((TextureAttribute) combinedAttributes.get(TextureAttribute.Specular)).textureDescription));
            }
        };
        public static final BaseShader.Setter specularUVTransform = new BaseShader.LocalSetter() {
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                TextureAttribute ta = (TextureAttribute) combinedAttributes.get(TextureAttribute.Specular);
                shader.set(inputID, ta.offsetU, ta.offsetV, ta.scaleU, ta.scaleV);
            }
        };
        public static final BaseShader.Setter viewTrans = new BaseShader.GlobalSetter() {
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, shader.camera.view);
            }
        };
        public static final BaseShader.Setter viewWorldTrans = new BaseShader.LocalSetter() {
            final Matrix4 temp = new Matrix4();

            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, this.temp.set(shader.camera.view).mul(renderable.worldTransform));
            }
        };
        public static final BaseShader.Setter worldTrans = new BaseShader.LocalSetter() {
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, renderable.worldTransform);
            }
        };

        public static class Bones extends BaseShader.LocalSetter {
            private static final Matrix4 idtMatrix = new Matrix4();
            public final float[] bones;

            public Bones(int numBones) {
                this.bones = new float[(numBones * 16)];
            }

            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                int i = 0;
                while (true) {
                    float[] fArr = this.bones;
                    if (i < fArr.length) {
                        int idx = i / 16;
                        fArr[i] = (renderable.bones == null || idx >= renderable.bones.length || renderable.bones[idx] == null) ? idtMatrix.val[i % 16] : renderable.bones[idx].val[i % 16];
                        i++;
                    } else {
                        ShaderProgram shaderProgram = shader.program;
                        int loc = shader.loc(inputID);
                        float[] fArr2 = this.bones;
                        shaderProgram.setUniformMatrix4fv(loc, fArr2, 0, fArr2.length);
                        return;
                    }
                }
            }
        }

        public static class ACubemap extends BaseShader.LocalSetter {
            private static final float[] ones = {1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f};
            private static final Vector3 tmpV1 = new Vector3();
            private final AmbientCubemap cacheAmbientCubemap = new AmbientCubemap();
            public final int dirLightsOffset;
            public final int pointLightsOffset;

            public ACubemap(int dirLightsOffset2, int pointLightsOffset2) {
                this.dirLightsOffset = dirLightsOffset2;
                this.pointLightsOffset = pointLightsOffset2;
            }

            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                if (renderable.environment == null) {
                    ShaderProgram shaderProgram = shader.program;
                    int loc = shader.loc(inputID);
                    float[] fArr = ones;
                    shaderProgram.setUniform3fv(loc, fArr, 0, fArr.length);
                    return;
                }
                renderable.worldTransform.getTranslation(tmpV1);
                if (combinedAttributes.has(ColorAttribute.AmbientLight)) {
                    this.cacheAmbientCubemap.set(((ColorAttribute) combinedAttributes.get(ColorAttribute.AmbientLight)).color);
                }
                if (combinedAttributes.has(DirectionalLightsAttribute.Type)) {
                    Array<DirectionalLight> lights = ((DirectionalLightsAttribute) combinedAttributes.get(DirectionalLightsAttribute.Type)).lights;
                    for (int i = this.dirLightsOffset; i < lights.size; i++) {
                        this.cacheAmbientCubemap.add(lights.get(i).color, lights.get(i).direction);
                    }
                }
                if (combinedAttributes.has(PointLightsAttribute.Type)) {
                    Array<PointLight> lights2 = ((PointLightsAttribute) combinedAttributes.get(PointLightsAttribute.Type)).lights;
                    for (int i2 = this.pointLightsOffset; i2 < lights2.size; i2++) {
                        this.cacheAmbientCubemap.add(lights2.get(i2).color, lights2.get(i2).position, tmpV1, lights2.get(i2).intensity);
                    }
                }
                this.cacheAmbientCubemap.clamp();
                shader.program.setUniform3fv(shader.loc(inputID), this.cacheAmbientCubemap.data, 0, this.cacheAmbientCubemap.data.length);
            }
        }
    }

    public static String getDefaultVertexShader() {
        if (defaultVertexShader == null) {
            defaultVertexShader = Gdx.files.classpath("com/badlogic/gdx/graphics/g3d/shaders/default.vertex.glsl").readString();
        }
        return defaultVertexShader;
    }

    public static String getDefaultFragmentShader() {
        if (defaultFragmentShader == null) {
            defaultFragmentShader = Gdx.files.classpath("com/badlogic/gdx/graphics/g3d/shaders/default.fragment.glsl").readString();
        }
        return defaultFragmentShader;
    }

    public DefaultShader(Renderable renderable2) {
        this(renderable2, new Config());
    }

    public DefaultShader(Renderable renderable2, Config config2) {
        this(renderable2, config2, createPrefix(renderable2, config2));
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public DefaultShader(com.badlogic.gdx.graphics.g3d.Renderable r8, com.badlogic.gdx.graphics.g3d.shaders.DefaultShader.Config r9, java.lang.String r10) {
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
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.graphics.g3d.shaders.DefaultShader.<init>(com.badlogic.gdx.graphics.g3d.Renderable, com.badlogic.gdx.graphics.g3d.shaders.DefaultShader$Config, java.lang.String):void");
    }

    public DefaultShader(Renderable renderable2, Config config2, String prefix, String vertexShader, String fragmentShader) {
        this(renderable2, config2, new ShaderProgram(prefix + vertexShader, prefix + fragmentShader));
    }

    public DefaultShader(Renderable renderable2, Config config2, ShaderProgram shaderProgram) {
        this.u_dirLights0color = register(new BaseShader.Uniform("u_dirLights[0].color"));
        this.u_dirLights0direction = register(new BaseShader.Uniform("u_dirLights[0].direction"));
        this.u_dirLights1color = register(new BaseShader.Uniform("u_dirLights[1].color"));
        this.u_pointLights0color = register(new BaseShader.Uniform("u_pointLights[0].color"));
        this.u_pointLights0position = register(new BaseShader.Uniform("u_pointLights[0].position"));
        this.u_pointLights0intensity = register(new BaseShader.Uniform("u_pointLights[0].intensity"));
        this.u_pointLights1color = register(new BaseShader.Uniform("u_pointLights[1].color"));
        this.u_spotLights0color = register(new BaseShader.Uniform("u_spotLights[0].color"));
        this.u_spotLights0position = register(new BaseShader.Uniform("u_spotLights[0].position"));
        this.u_spotLights0intensity = register(new BaseShader.Uniform("u_spotLights[0].intensity"));
        this.u_spotLights0direction = register(new BaseShader.Uniform("u_spotLights[0].direction"));
        this.u_spotLights0cutoffAngle = register(new BaseShader.Uniform("u_spotLights[0].cutoffAngle"));
        this.u_spotLights0exponent = register(new BaseShader.Uniform("u_spotLights[0].exponent"));
        this.u_spotLights1color = register(new BaseShader.Uniform("u_spotLights[1].color"));
        this.u_fogColor = register(new BaseShader.Uniform("u_fogColor"));
        this.u_shadowMapProjViewTrans = register(new BaseShader.Uniform("u_shadowMapProjViewTrans"));
        this.u_shadowTexture = register(new BaseShader.Uniform("u_shadowTexture"));
        this.u_shadowPCFOffset = register(new BaseShader.Uniform("u_shadowPCFOffset"));
        this.ambientCubemap = new AmbientCubemap();
        this.normalMatrix = new Matrix3();
        this.tmpV1 = new Vector3();
        Attributes attributes = combineAttributes(renderable2);
        this.config = config2;
        this.program = shaderProgram;
        boolean z = true;
        int i = 0;
        this.lighting = renderable2.environment != null;
        this.environmentCubemap = attributes.has(CubemapAttribute.EnvironmentMap) || (this.lighting && attributes.has(CubemapAttribute.EnvironmentMap));
        this.shadowMap = (!this.lighting || renderable2.environment.shadowMap == null) ? false : z;
        this.renderable = renderable2;
        this.attributesMask = attributes.getMask() | optionalAttributes;
        this.vertexMask = renderable2.meshPart.mesh.getVertexAttributes().getMaskWithSizePacked();
        this.directionalLights = new DirectionalLight[((!this.lighting || config2.numDirectionalLights <= 0) ? 0 : config2.numDirectionalLights)];
        int i2 = 0;
        while (true) {
            DirectionalLight[] directionalLightArr = this.directionalLights;
            if (i2 >= directionalLightArr.length) {
                break;
            }
            directionalLightArr[i2] = new DirectionalLight();
            i2++;
        }
        this.pointLights = new PointLight[((this.lighting == 0 || config2.numPointLights <= 0) ? 0 : config2.numPointLights)];
        int i3 = 0;
        while (true) {
            PointLight[] pointLightArr = this.pointLights;
            if (i3 >= pointLightArr.length) {
                break;
            }
            pointLightArr[i3] = new PointLight();
            i3++;
        }
        if (this.lighting != 0 && config2.numSpotLights > 0) {
            i = config2.numSpotLights;
        }
        this.spotLights = new SpotLight[i];
        int i4 = 0;
        while (true) {
            SpotLight[] spotLightArr = this.spotLights;
            if (i4 >= spotLightArr.length) {
                break;
            }
            spotLightArr[i4] = new SpotLight();
            i4++;
        }
        if (config2.ignoreUnimplemented == 0) {
            long j = implementedFlags;
            long j2 = this.attributesMask;
            if ((j & j2) != j2) {
                throw new GdxRuntimeException("Some attributes not implemented yet (" + this.attributesMask + ")");
            }
        }
        this.u_projTrans = register(Inputs.projTrans, Setters.projTrans);
        this.u_viewTrans = register(Inputs.viewTrans, Setters.viewTrans);
        this.u_projViewTrans = register(Inputs.projViewTrans, Setters.projViewTrans);
        this.u_cameraPosition = register(Inputs.cameraPosition, Setters.cameraPosition);
        this.u_cameraDirection = register(Inputs.cameraDirection, Setters.cameraDirection);
        this.u_cameraUp = register(Inputs.cameraUp, Setters.cameraUp);
        this.u_cameraNearFar = register(Inputs.cameraNearFar, Setters.cameraNearFar);
        this.u_time = register(new BaseShader.Uniform("u_time"));
        this.u_worldTrans = register(Inputs.worldTrans, Setters.worldTrans);
        this.u_viewWorldTrans = register(Inputs.viewWorldTrans, Setters.viewWorldTrans);
        this.u_projViewWorldTrans = register(Inputs.projViewWorldTrans, Setters.projViewWorldTrans);
        this.u_normalMatrix = register(Inputs.normalMatrix, Setters.normalMatrix);
        int i5 = -1;
        this.u_bones = (renderable2.bones == null || config2.numBones <= 0) ? -1 : register(Inputs.bones, (BaseShader.Setter) new Setters.Bones(config2.numBones));
        this.u_shininess = register(Inputs.shininess, Setters.shininess);
        this.u_opacity = register(Inputs.opacity);
        this.u_diffuseColor = register(Inputs.diffuseColor, Setters.diffuseColor);
        this.u_diffuseTexture = register(Inputs.diffuseTexture, Setters.diffuseTexture);
        this.u_diffuseUVTransform = register(Inputs.diffuseUVTransform, Setters.diffuseUVTransform);
        this.u_specularColor = register(Inputs.specularColor, Setters.specularColor);
        this.u_specularTexture = register(Inputs.specularTexture, Setters.specularTexture);
        this.u_specularUVTransform = register(Inputs.specularUVTransform, Setters.specularUVTransform);
        this.u_emissiveColor = register(Inputs.emissiveColor, Setters.emissiveColor);
        this.u_emissiveTexture = register(Inputs.emissiveTexture, Setters.emissiveTexture);
        this.u_emissiveUVTransform = register(Inputs.emissiveUVTransform, Setters.emissiveUVTransform);
        this.u_reflectionColor = register(Inputs.reflectionColor, Setters.reflectionColor);
        this.u_reflectionTexture = register(Inputs.reflectionTexture, Setters.reflectionTexture);
        this.u_reflectionUVTransform = register(Inputs.reflectionUVTransform, Setters.reflectionUVTransform);
        this.u_normalTexture = register(Inputs.normalTexture, Setters.normalTexture);
        this.u_normalUVTransform = register(Inputs.normalUVTransform, Setters.normalUVTransform);
        this.u_ambientTexture = register(Inputs.ambientTexture, Setters.ambientTexture);
        this.u_ambientUVTransform = register(Inputs.ambientUVTransform, Setters.ambientUVTransform);
        this.u_alphaTest = register(Inputs.alphaTest);
        this.u_ambientCubemap = this.lighting ? register(Inputs.ambientCube, (BaseShader.Setter) new Setters.ACubemap(config2.numDirectionalLights, config2.numPointLights)) : -1;
        this.u_environmentCubemap = this.environmentCubemap ? register(Inputs.environmentCubemap, Setters.environmentCubemap) : i5;
    }

    public void init() {
        ShaderProgram program = this.program;
        this.program = null;
        init(program, this.renderable);
        this.renderable = null;
        this.dirLightsLoc = loc(this.u_dirLights0color);
        this.dirLightsColorOffset = loc(this.u_dirLights0color) - this.dirLightsLoc;
        this.dirLightsDirectionOffset = loc(this.u_dirLights0direction) - this.dirLightsLoc;
        this.dirLightsSize = loc(this.u_dirLights1color) - this.dirLightsLoc;
        if (this.dirLightsSize < 0) {
            this.dirLightsSize = 0;
        }
        this.pointLightsLoc = loc(this.u_pointLights0color);
        this.pointLightsColorOffset = loc(this.u_pointLights0color) - this.pointLightsLoc;
        this.pointLightsPositionOffset = loc(this.u_pointLights0position) - this.pointLightsLoc;
        int i = -1;
        this.pointLightsIntensityOffset = has(this.u_pointLights0intensity) ? loc(this.u_pointLights0intensity) - this.pointLightsLoc : -1;
        this.pointLightsSize = loc(this.u_pointLights1color) - this.pointLightsLoc;
        if (this.pointLightsSize < 0) {
            this.pointLightsSize = 0;
        }
        this.spotLightsLoc = loc(this.u_spotLights0color);
        this.spotLightsColorOffset = loc(this.u_spotLights0color) - this.spotLightsLoc;
        this.spotLightsPositionOffset = loc(this.u_spotLights0position) - this.spotLightsLoc;
        this.spotLightsDirectionOffset = loc(this.u_spotLights0direction) - this.spotLightsLoc;
        if (has(this.u_spotLights0intensity)) {
            i = loc(this.u_spotLights0intensity) - this.spotLightsLoc;
        }
        this.spotLightsIntensityOffset = i;
        this.spotLightsCutoffAngleOffset = loc(this.u_spotLights0cutoffAngle) - this.spotLightsLoc;
        this.spotLightsExponentOffset = loc(this.u_spotLights0exponent) - this.spotLightsLoc;
        this.spotLightsSize = loc(this.u_spotLights1color) - this.spotLightsLoc;
        if (this.spotLightsSize < 0) {
            this.spotLightsSize = 0;
        }
    }

    private static final boolean and(long mask, long flag) {
        return (mask & flag) == flag;
    }

    private static final boolean or(long mask, long flag) {
        return (mask & flag) != 0;
    }

    private static final Attributes combineAttributes(Renderable renderable2) {
        tmpAttributes.clear();
        if (renderable2.environment != null) {
            tmpAttributes.set((Iterable<Attribute>) renderable2.environment);
        }
        if (renderable2.material != null) {
            tmpAttributes.set((Iterable<Attribute>) renderable2.material);
        }
        return tmpAttributes;
    }

    private static final long combineAttributeMasks(Renderable renderable2) {
        long mask = 0;
        if (renderable2.environment != null) {
            mask = 0 | renderable2.environment.getMask();
        }
        if (renderable2.material != null) {
            return mask | renderable2.material.getMask();
        }
        return mask;
    }

    public static String createPrefix(Renderable renderable2, Config config2) {
        String prefix;
        Attributes attributes = combineAttributes(renderable2);
        String prefix2 = BuildConfig.FLAVOR;
        long attributesMask2 = attributes.getMask();
        long vertexMask2 = renderable2.meshPart.mesh.getVertexAttributes().getMask();
        if (and(vertexMask2, 1)) {
            prefix2 = prefix2 + "#define positionFlag\n";
        }
        if (or(vertexMask2, 6)) {
            prefix2 = prefix2 + "#define colorFlag\n";
        }
        if (and(vertexMask2, 256)) {
            prefix2 = prefix2 + "#define binormalFlag\n";
        }
        if (and(vertexMask2, 128)) {
            prefix2 = prefix2 + "#define tangentFlag\n";
        }
        if (and(vertexMask2, 8)) {
            prefix2 = prefix2 + "#define normalFlag\n";
        }
        if ((and(vertexMask2, 8) || and(vertexMask2, 384)) && renderable2.environment != null) {
            prefix = ((((prefix + "#define lightingFlag\n") + "#define ambientCubemapFlag\n") + "#define numDirectionalLights " + config2.numDirectionalLights + "\n") + "#define numPointLights " + config2.numPointLights + "\n") + "#define numSpotLights " + config2.numSpotLights + "\n";
            if (attributes.has(ColorAttribute.Fog)) {
                prefix = prefix + "#define fogFlag\n";
            }
            if (renderable2.environment.shadowMap != null) {
                prefix = prefix + "#define shadowMapFlag\n";
            }
            if (attributes.has(CubemapAttribute.EnvironmentMap)) {
                prefix = prefix + "#define environmentCubemapFlag\n";
            }
        }
        int n = renderable2.meshPart.mesh.getVertexAttributes().size();
        for (int i = 0; i < n; i++) {
            VertexAttribute attr = renderable2.meshPart.mesh.getVertexAttributes().get(i);
            if (attr.usage == 64) {
                prefix = prefix + "#define boneWeight" + attr.unit + "Flag\n";
            } else if (attr.usage == 16) {
                prefix = prefix + "#define texCoord" + attr.unit + "Flag\n";
            }
        }
        if ((BlendingAttribute.Type & attributesMask2) == BlendingAttribute.Type) {
            prefix = prefix + "#define blendedFlag\n";
        }
        if ((TextureAttribute.Diffuse & attributesMask2) == TextureAttribute.Diffuse) {
            prefix = (prefix + "#define diffuseTextureFlag\n") + "#define diffuseTextureCoord texCoord0\n";
        }
        if ((TextureAttribute.Specular & attributesMask2) == TextureAttribute.Specular) {
            prefix = (prefix + "#define specularTextureFlag\n") + "#define specularTextureCoord texCoord0\n";
        }
        if ((TextureAttribute.Normal & attributesMask2) == TextureAttribute.Normal) {
            prefix = (prefix + "#define normalTextureFlag\n") + "#define normalTextureCoord texCoord0\n";
        }
        if ((TextureAttribute.Emissive & attributesMask2) == TextureAttribute.Emissive) {
            prefix = (prefix + "#define emissiveTextureFlag\n") + "#define emissiveTextureCoord texCoord0\n";
        }
        if ((TextureAttribute.Reflection & attributesMask2) == TextureAttribute.Reflection) {
            prefix = (prefix + "#define reflectionTextureFlag\n") + "#define reflectionTextureCoord texCoord0\n";
        }
        if ((TextureAttribute.Ambient & attributesMask2) == TextureAttribute.Ambient) {
            prefix = (prefix + "#define ambientTextureFlag\n") + "#define ambientTextureCoord texCoord0\n";
        }
        if ((ColorAttribute.Diffuse & attributesMask2) == ColorAttribute.Diffuse) {
            prefix = prefix + "#define diffuseColorFlag\n";
        }
        if ((ColorAttribute.Specular & attributesMask2) == ColorAttribute.Specular) {
            prefix = prefix + "#define specularColorFlag\n";
        }
        if ((ColorAttribute.Emissive & attributesMask2) == ColorAttribute.Emissive) {
            prefix = prefix + "#define emissiveColorFlag\n";
        }
        if ((ColorAttribute.Reflection & attributesMask2) == ColorAttribute.Reflection) {
            prefix = prefix + "#define reflectionColorFlag\n";
        }
        if ((FloatAttribute.Shininess & attributesMask2) == FloatAttribute.Shininess) {
            prefix = prefix + "#define shininessFlag\n";
        }
        if ((FloatAttribute.AlphaTest & attributesMask2) == FloatAttribute.AlphaTest) {
            prefix = prefix + "#define alphaTestFlag\n";
        }
        if (renderable2.bones == null || config2.numBones <= 0) {
            return prefix;
        }
        return prefix + "#define numBones " + config2.numBones + "\n";
    }

    public boolean canRender(Renderable renderable2) {
        if (this.attributesMask == (optionalAttributes | combineAttributeMasks(renderable2)) && this.vertexMask == renderable2.meshPart.mesh.getVertexAttributes().getMaskWithSizePacked()) {
            if ((renderable2.environment != null) == this.lighting) {
                return true;
            }
        }
        return false;
    }

    public int compareTo(Shader other) {
        if (other == null) {
            return -1;
        }
        return other == this ? 0 : 0;
    }

    public boolean equals(Object obj) {
        return (obj instanceof DefaultShader) && equals((DefaultShader) obj);
    }

    public boolean equals(DefaultShader obj) {
        return obj == this;
    }

    public void begin(Camera camera, RenderContext context) {
        super.begin(camera, context);
        for (DirectionalLight dirLight : this.directionalLights) {
            dirLight.set(0.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f);
        }
        for (PointLight pointLight : this.pointLights) {
            pointLight.set(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        }
        for (SpotLight spotLight : this.spotLights) {
            spotLight.set(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f);
        }
        this.lightsSet = false;
        if (has(this.u_time)) {
            int i = this.u_time;
            float deltaTime = this.time + Gdx.graphics.getDeltaTime();
            this.time = deltaTime;
            set(i, deltaTime);
        }
    }

    public void render(Renderable renderable2, Attributes combinedAttributes) {
        if (!combinedAttributes.has(BlendingAttribute.Type)) {
            this.context.setBlending(false, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }
        bindMaterial(combinedAttributes);
        if (this.lighting) {
            bindLights(renderable2, combinedAttributes);
        }
        super.render(renderable2, combinedAttributes);
    }

    public void end() {
        super.end();
    }

    /* access modifiers changed from: protected */
    public void bindMaterial(Attributes attributes) {
        int cullFace = this.config.defaultCullFace == -1 ? defaultCullFace : this.config.defaultCullFace;
        int depthFunc = this.config.defaultDepthFunc == -1 ? defaultDepthFunc : this.config.defaultDepthFunc;
        float depthRangeNear = 0.0f;
        float depthRangeFar = 1.0f;
        boolean depthMask = true;
        Iterator<Attribute> it = attributes.iterator();
        while (it.hasNext()) {
            Attribute attr = it.next();
            long t = attr.type;
            if (BlendingAttribute.is(t)) {
                this.context.setBlending(true, ((BlendingAttribute) attr).sourceFunction, ((BlendingAttribute) attr).destFunction);
                set(this.u_opacity, ((BlendingAttribute) attr).opacity);
            } else if ((IntAttribute.CullFace & t) == IntAttribute.CullFace) {
                cullFace = ((IntAttribute) attr).value;
            } else if ((FloatAttribute.AlphaTest & t) == FloatAttribute.AlphaTest) {
                set(this.u_alphaTest, ((FloatAttribute) attr).value);
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

    /* access modifiers changed from: protected */
    public void bindLights(Renderable renderable2, Attributes attributes) {
        float f;
        DirectionalLightsAttribute dla;
        float f2;
        Attributes attributes2 = attributes;
        Environment lights = renderable2.environment;
        DirectionalLightsAttribute dla2 = (DirectionalLightsAttribute) attributes2.get(DirectionalLightsAttribute.class, DirectionalLightsAttribute.Type);
        Array<SpotLight> spots = null;
        Array<DirectionalLight> dirs = dla2 == null ? null : dla2.lights;
        PointLightsAttribute pla = (PointLightsAttribute) attributes2.get(PointLightsAttribute.class, PointLightsAttribute.Type);
        Array<PointLight> points = pla == null ? null : pla.lights;
        SpotLightsAttribute sla = (SpotLightsAttribute) attributes2.get(SpotLightsAttribute.class, SpotLightsAttribute.Type);
        if (sla != null) {
            spots = sla.lights;
        }
        float f3 = 1.0f;
        float f4 = 0.0f;
        if (this.dirLightsLoc >= 0) {
            int i = 0;
            while (i < this.directionalLights.length) {
                if (dirs == null || i >= dirs.size) {
                    if (this.lightsSet && this.directionalLights[i].color.r == f4 && this.directionalLights[i].color.g == f4 && this.directionalLights[i].color.b == f4) {
                        i++;
                        Renderable renderable3 = renderable2;
                        f3 = 1.0f;
                        f4 = 0.0f;
                    } else {
                        this.directionalLights[i].color.set(f4, f4, f4, f3);
                    }
                } else if (!this.lightsSet || !this.directionalLights[i].equals(dirs.get(i))) {
                    this.directionalLights[i].set(dirs.get(i));
                } else {
                    i++;
                    Renderable renderable32 = renderable2;
                    f3 = 1.0f;
                    f4 = 0.0f;
                }
                int idx = this.dirLightsLoc + (this.dirLightsSize * i);
                this.program.setUniformf(this.dirLightsColorOffset + idx, this.directionalLights[i].color.r, this.directionalLights[i].color.g, this.directionalLights[i].color.b);
                this.program.setUniformf(this.dirLightsDirectionOffset + idx, this.directionalLights[i].direction.x, this.directionalLights[i].direction.y, this.directionalLights[i].direction.z);
                if (this.dirLightsSize <= 0) {
                    break;
                }
                i++;
                Renderable renderable322 = renderable2;
                f3 = 1.0f;
                f4 = 0.0f;
            }
        }
        if (this.pointLightsLoc >= 0) {
            int i2 = 0;
            while (true) {
                if (i2 >= this.pointLights.length) {
                    break;
                }
                if (points == null || i2 >= points.size) {
                    if (this.lightsSet) {
                        f2 = 0.0f;
                        if (this.pointLights[i2].intensity == 0.0f) {
                            dla = dla2;
                            i2++;
                            dla2 = dla;
                        }
                    } else {
                        f2 = 0.0f;
                    }
                    this.pointLights[i2].intensity = f2;
                } else if (!this.lightsSet || !this.pointLights[i2].equals(points.get(i2))) {
                    this.pointLights[i2].set(points.get(i2));
                } else {
                    dla = dla2;
                    i2++;
                    dla2 = dla;
                }
                int idx2 = this.pointLightsLoc + (this.pointLightsSize * i2);
                dla = dla2;
                this.program.setUniformf(this.pointLightsColorOffset + idx2, this.pointLights[i2].color.r * this.pointLights[i2].intensity, this.pointLights[i2].color.g * this.pointLights[i2].intensity, this.pointLights[i2].color.b * this.pointLights[i2].intensity);
                this.program.setUniformf(this.pointLightsPositionOffset + idx2, this.pointLights[i2].position.x, this.pointLights[i2].position.y, this.pointLights[i2].position.z);
                if (this.pointLightsIntensityOffset >= 0) {
                    this.program.setUniformf(this.pointLightsIntensityOffset + idx2, this.pointLights[i2].intensity);
                }
                if (this.pointLightsSize <= 0) {
                    break;
                }
                i2++;
                dla2 = dla;
            }
        }
        if (this.spotLightsLoc >= 0) {
            for (int i3 = 0; i3 < this.spotLights.length; i3++) {
                if (spots == null || i3 >= spots.size) {
                    if (this.lightsSet) {
                        f = 0.0f;
                        if (this.spotLights[i3].intensity == 0.0f) {
                            continue;
                        }
                    } else {
                        f = 0.0f;
                    }
                    this.spotLights[i3].intensity = f;
                } else if (!this.lightsSet || !this.spotLights[i3].equals(spots.get(i3))) {
                    this.spotLights[i3].set(spots.get(i3));
                }
                int idx3 = this.spotLightsLoc + (this.spotLightsSize * i3);
                this.program.setUniformf(this.spotLightsColorOffset + idx3, this.spotLights[i3].color.r * this.spotLights[i3].intensity, this.spotLights[i3].color.g * this.spotLights[i3].intensity, this.spotLights[i3].color.b * this.spotLights[i3].intensity);
                this.program.setUniformf(this.spotLightsPositionOffset + idx3, this.spotLights[i3].position);
                this.program.setUniformf(this.spotLightsDirectionOffset + idx3, this.spotLights[i3].direction);
                this.program.setUniformf(this.spotLightsCutoffAngleOffset + idx3, this.spotLights[i3].cutoffAngle);
                this.program.setUniformf(this.spotLightsExponentOffset + idx3, this.spotLights[i3].exponent);
                if (this.spotLightsIntensityOffset >= 0) {
                    this.program.setUniformf(this.spotLightsIntensityOffset + idx3, this.spotLights[i3].intensity);
                }
                if (this.spotLightsSize <= 0) {
                    break;
                }
            }
        }
        if (attributes2.has(ColorAttribute.Fog)) {
            set(this.u_fogColor, ((ColorAttribute) attributes2.get(ColorAttribute.Fog)).color);
        }
        if (!(lights == null || lights.shadowMap == null)) {
            set(this.u_shadowMapProjViewTrans, lights.shadowMap.getProjViewTrans());
            set(this.u_shadowTexture, lights.shadowMap.getDepthMap());
            set(this.u_shadowPCFOffset, 1.0f / (((float) lights.shadowMap.getDepthMap().texture.getWidth()) * 2.0f));
        }
        this.lightsSet = true;
    }

    public void dispose() {
        this.program.dispose();
        super.dispose();
    }

    public int getDefaultCullFace() {
        return this.config.defaultCullFace == -1 ? defaultCullFace : this.config.defaultCullFace;
    }

    public void setDefaultCullFace(int cullFace) {
        this.config.defaultCullFace = cullFace;
    }

    public int getDefaultDepthFunc() {
        return this.config.defaultDepthFunc == -1 ? defaultDepthFunc : this.config.defaultDepthFunc;
    }

    public void setDefaultDepthFunc(int depthFunc) {
        this.config.defaultDepthFunc = depthFunc;
    }
}
