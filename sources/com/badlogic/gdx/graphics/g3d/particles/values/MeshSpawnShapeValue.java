package com.badlogic.gdx.graphics.g3d.particles.values;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.particles.ResourceData;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;

public abstract class MeshSpawnShapeValue extends SpawnShapeValue {
    protected Mesh mesh;
    protected Model model;

    public static class Triangle {
        float x1;
        float x2;
        float x3;
        float y1;
        float y2;
        float y3;
        float z1;
        float z2;
        float z3;

        public Triangle(float x12, float y12, float z12, float x22, float y22, float z22, float x32, float y32, float z32) {
            this.x1 = x12;
            this.y1 = y12;
            this.z1 = z12;
            this.x2 = x22;
            this.y2 = y22;
            this.z2 = z22;
            this.x3 = x32;
            this.y3 = y32;
            this.z3 = z32;
        }

        public static Vector3 pick(float x12, float y12, float z12, float x22, float y22, float z22, float x32, float y32, float z32, Vector3 vector) {
            float a = MathUtils.random();
            float b = MathUtils.random();
            return vector.set(((x22 - x12) * a) + x12 + ((x32 - x12) * b), ((y22 - y12) * a) + y12 + ((y32 - y12) * b), ((z22 - z12) * a) + z12 + ((z32 - z12) * b));
        }

        public Vector3 pick(Vector3 vector) {
            float a = MathUtils.random();
            float b = MathUtils.random();
            float f = this.x1;
            float f2 = this.y1;
            float f3 = this.z1;
            return vector.set(((this.x2 - f) * a) + f + ((this.x3 - f) * b), ((this.y2 - f2) * a) + f2 + ((this.y3 - f2) * b), ((this.z2 - f3) * a) + f3 + ((this.z3 - f3) * b));
        }
    }

    public MeshSpawnShapeValue(MeshSpawnShapeValue value) {
        super(value);
    }

    public MeshSpawnShapeValue() {
    }

    public void load(ParticleValue value) {
        super.load(value);
        MeshSpawnShapeValue spawnShapeValue = (MeshSpawnShapeValue) value;
        setMesh(spawnShapeValue.mesh, spawnShapeValue.model);
    }

    public void setMesh(Mesh mesh2, Model model2) {
        if (mesh2.getVertexAttribute(1) != null) {
            this.model = model2;
            this.mesh = mesh2;
            return;
        }
        throw new GdxRuntimeException("Mesh vertices must have Usage.Position");
    }

    public void setMesh(Mesh mesh2) {
        setMesh(mesh2, (Model) null);
    }

    public void save(AssetManager manager, ResourceData data) {
        if (this.model != null) {
            ResourceData.SaveData saveData = data.createSaveData();
            saveData.saveAsset(manager.getAssetFileName(this.model), Model.class);
            saveData.save("index", Integer.valueOf(this.model.meshes.indexOf(this.mesh, true)));
        }
    }

    public void load(AssetManager manager, ResourceData data) {
        ResourceData.SaveData saveData = data.getSaveData();
        AssetDescriptor descriptor = saveData.loadAsset();
        if (descriptor != null) {
            Model model2 = (Model) manager.get(descriptor);
            setMesh(model2.meshes.get(((Integer) saveData.load("index")).intValue()), model2);
        }
    }
}
