package com.badlogic.gdx.graphics.g3d.decals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;
import java.util.Comparator;

public class CameraGroupStrategy implements GroupStrategy, Disposable {
    private static final int GROUP_BLEND = 1;
    private static final int GROUP_OPAQUE = 0;
    Pool<Array<Decal>> arrayPool;
    Camera camera;
    private final Comparator<Decal> cameraSorter;
    ObjectMap<DecalMaterial, Array<Decal>> materialGroups;
    ShaderProgram shader;
    Array<Array<Decal>> usedArrays;

    public CameraGroupStrategy(final Camera camera2) {
        this(camera2, new Comparator<Decal>() {
            public int compare(Decal o1, Decal o2) {
                return (int) Math.signum(Camera.this.position.dst(o2.position) - Camera.this.position.dst(o1.position));
            }
        });
    }

    public CameraGroupStrategy(Camera camera2, Comparator<Decal> sorter) {
        this.arrayPool = new Pool<Array<Decal>>(16) {
            /* access modifiers changed from: protected */
            public Array<Decal> newObject() {
                return new Array<>();
            }
        };
        this.usedArrays = new Array<>();
        this.materialGroups = new ObjectMap<>();
        this.camera = camera2;
        this.cameraSorter = sorter;
        createDefaultShader();
    }

    public void setCamera(Camera camera2) {
        this.camera = camera2;
    }

    public Camera getCamera() {
        return this.camera;
    }

    public int decideGroup(Decal decal) {
        return decal.getMaterial().isOpaque() ^ true ? 1 : 0;
    }

    public void beforeGroup(int group, Array<Decal> contents) {
        if (group == 1) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            contents.sort(this.cameraSorter);
            return;
        }
        int n = contents.size;
        for (int i = 0; i < n; i++) {
            Decal decal = contents.get(i);
            Array<Decal> materialGroup = this.materialGroups.get(decal.material);
            if (materialGroup == null) {
                materialGroup = this.arrayPool.obtain();
                materialGroup.clear();
                this.usedArrays.add(materialGroup);
                this.materialGroups.put(decal.material, materialGroup);
            }
            materialGroup.add(decal);
        }
        contents.clear();
        ObjectMap.Values<Array<Decal>> it = this.materialGroups.values().iterator();
        while (it.hasNext()) {
            contents.addAll((Array<? extends Decal>) (Array) it.next());
        }
        this.materialGroups.clear();
        this.arrayPool.freeAll(this.usedArrays);
        this.usedArrays.clear();
    }

    public void afterGroup(int group) {
        if (group == 1) {
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }

    public void beforeGroups() {
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        this.shader.begin();
        this.shader.setUniformMatrix("u_projectionViewMatrix", this.camera.combined);
        this.shader.setUniformi("u_texture", 0);
    }

    public void afterGroups() {
        this.shader.end();
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
    }

    private void createDefaultShader() {
        this.shader = new ShaderProgram("attribute vec4 a_position;\nattribute vec4 a_color;\nattribute vec2 a_texCoord0;\nuniform mat4 u_projectionViewMatrix;\nvarying vec4 v_color;\nvarying vec2 v_texCoords;\n\nvoid main()\n{\n   v_color = a_color;\n   v_color.a = v_color.a * (255.0/254.0);\n   v_texCoords = a_texCoord0;\n   gl_Position =  u_projectionViewMatrix * a_position;\n}\n", "#ifdef GL_ES\nprecision mediump float;\n#endif\nvarying vec4 v_color;\nvarying vec2 v_texCoords;\nuniform sampler2D u_texture;\nvoid main()\n{\n  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n}");
        if (!this.shader.isCompiled()) {
            throw new IllegalArgumentException("couldn't compile shader: " + this.shader.getLog());
        }
    }

    public ShaderProgram getGroupShader(int group) {
        return this.shader;
    }

    public void dispose() {
        ShaderProgram shaderProgram = this.shader;
        if (shaderProgram != null) {
            shaderProgram.dispose();
        }
    }
}
