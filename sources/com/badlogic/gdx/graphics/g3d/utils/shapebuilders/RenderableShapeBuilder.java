package com.badlogic.gdx.graphics.g3d.utils.shapebuilders;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FlushablePool;
import com.twi.game.BuildConfig;
import java.util.Iterator;

public class RenderableShapeBuilder extends BaseShapeBuilder {
    private static final int FLOAT_BYTES = 4;
    private static short[] indices;
    private static final Array<Renderable> renderables = new Array<>();
    private static final RenderablePool renderablesPool = new RenderablePool();
    private static float[] vertices;

    private static class RenderablePool extends FlushablePool<Renderable> {
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

    public static void buildNormals(MeshPartBuilder builder, RenderableProvider renderableProvider, float vectorSize) {
        buildNormals(builder, renderableProvider, vectorSize, tmpColor0.set(0.0f, 0.0f, 1.0f, 1.0f), tmpColor1.set(1.0f, 0.0f, 0.0f, 1.0f), tmpColor2.set(0.0f, 1.0f, 0.0f, 1.0f));
    }

    public static void buildNormals(MeshPartBuilder builder, RenderableProvider renderableProvider, float vectorSize, Color normalColor, Color tangentColor, Color binormalColor) {
        renderableProvider.getRenderables(renderables, renderablesPool);
        Iterator<Renderable> it = renderables.iterator();
        while (it.hasNext()) {
            buildNormals(builder, it.next(), vectorSize, normalColor, tangentColor, binormalColor);
        }
        renderablesPool.flush();
        renderables.clear();
    }

    public static void buildNormals(MeshPartBuilder builder, Renderable renderable, float vectorSize, Color normalColor, Color tangentColor, Color binormalColor) {
        int verticesQuantity;
        int verticesOffset;
        MeshPartBuilder meshPartBuilder = builder;
        Renderable renderable2 = renderable;
        float f = vectorSize;
        Mesh mesh = renderable2.meshPart.mesh;
        int positionOffset = -1;
        if (mesh.getVertexAttribute(1) != null) {
            positionOffset = mesh.getVertexAttribute(1).offset / 4;
        }
        int normalOffset = -1;
        if (mesh.getVertexAttribute(8) != null) {
            normalOffset = mesh.getVertexAttribute(8).offset / 4;
        }
        int tangentOffset = -1;
        if (mesh.getVertexAttribute(128) != null) {
            tangentOffset = mesh.getVertexAttribute(128).offset / 4;
        }
        int binormalOffset = -1;
        if (mesh.getVertexAttribute(256) != null) {
            binormalOffset = mesh.getVertexAttribute(256).offset / 4;
        }
        int attributesSize = mesh.getVertexSize() / 4;
        if (mesh.getNumIndices() > 0) {
            ensureIndicesCapacity(mesh.getNumIndices());
            mesh.getIndices(renderable2.meshPart.offset, renderable2.meshPart.size, indices, 0);
            short minVertice = minVerticeInIndices();
            verticesOffset = minVertice;
            verticesQuantity = maxVerticeInIndices() - minVertice;
        } else {
            verticesOffset = renderable2.meshPart.offset;
            verticesQuantity = renderable2.meshPart.size;
        }
        ensureVerticesCapacity(verticesQuantity * attributesSize);
        mesh.getVertices(verticesOffset * attributesSize, verticesQuantity * attributesSize, vertices, 0);
        int i = verticesOffset;
        while (i < verticesQuantity) {
            int id = i * attributesSize;
            Vector3 vector3 = tmpV0;
            float[] fArr = vertices;
            Mesh mesh2 = mesh;
            vector3.set(fArr[id + positionOffset], fArr[id + positionOffset + 1], fArr[id + positionOffset + 2]);
            if (normalOffset != -1) {
                Vector3 vector32 = tmpV1;
                float[] fArr2 = vertices;
                vector32.set(fArr2[id + normalOffset], fArr2[id + normalOffset + 1], fArr2[id + normalOffset + 2]);
                tmpV2.set(tmpV0).add(tmpV1.scl(f));
            }
            if (tangentOffset != -1) {
                Vector3 vector33 = tmpV3;
                float[] fArr3 = vertices;
                vector33.set(fArr3[id + tangentOffset], fArr3[id + tangentOffset + 1], fArr3[id + tangentOffset + 2]);
                tmpV4.set(tmpV0).add(tmpV3.scl(f));
            }
            if (binormalOffset != -1) {
                Vector3 vector34 = tmpV5;
                float[] fArr4 = vertices;
                vector34.set(fArr4[id + binormalOffset], fArr4[id + binormalOffset + 1], fArr4[id + binormalOffset + 2]);
                tmpV6.set(tmpV0).add(tmpV5.scl(f));
            }
            tmpV0.mul(renderable2.worldTransform);
            tmpV2.mul(renderable2.worldTransform);
            tmpV4.mul(renderable2.worldTransform);
            tmpV6.mul(renderable2.worldTransform);
            if (normalOffset != -1) {
                meshPartBuilder.setColor(normalColor);
                meshPartBuilder.line(tmpV0, tmpV2);
            } else {
                Color color = normalColor;
            }
            if (tangentOffset != -1) {
                meshPartBuilder.setColor(tangentColor);
                meshPartBuilder.line(tmpV0, tmpV4);
            } else {
                Color color2 = tangentColor;
            }
            if (binormalOffset != -1) {
                meshPartBuilder.setColor(binormalColor);
                meshPartBuilder.line(tmpV0, tmpV6);
            } else {
                Color color3 = binormalColor;
            }
            i++;
            renderable2 = renderable;
            mesh = mesh2;
        }
        Color color4 = normalColor;
        Color color5 = tangentColor;
        Mesh mesh3 = mesh;
        Color color6 = binormalColor;
    }

    private static void ensureVerticesCapacity(int capacity) {
        float[] fArr = vertices;
        if (fArr == null || fArr.length < capacity) {
            vertices = new float[capacity];
        }
    }

    private static void ensureIndicesCapacity(int capacity) {
        short[] sArr = indices;
        if (sArr == null || sArr.length < capacity) {
            indices = new short[capacity];
        }
    }

    private static short minVerticeInIndices() {
        short min = Short.MAX_VALUE;
        int i = 0;
        while (true) {
            short[] sArr = indices;
            if (i >= sArr.length) {
                return min;
            }
            if (sArr[i] < min) {
                min = sArr[i];
            }
            i++;
        }
    }

    private static short maxVerticeInIndices() {
        short max = Short.MIN_VALUE;
        int i = 0;
        while (true) {
            short[] sArr = indices;
            if (i >= sArr.length) {
                return max;
            }
            if (sArr[i] > max) {
                max = sArr[i];
            }
            i++;
        }
    }
}
