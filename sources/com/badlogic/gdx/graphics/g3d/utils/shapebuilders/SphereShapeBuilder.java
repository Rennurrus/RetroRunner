package com.badlogic.gdx.graphics.g3d.utils.shapebuilders;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ShortArray;

public class SphereShapeBuilder extends BaseShapeBuilder {
    private static final Matrix3 normalTransform = new Matrix3();
    private static final ShortArray tmpIndices = new ShortArray();

    public static void build(MeshPartBuilder builder, float width, float height, float depth, int divisionsU, int divisionsV) {
        build(builder, width, height, depth, divisionsU, divisionsV, 0.0f, 360.0f, 0.0f, 180.0f);
    }

    @Deprecated
    public static void build(MeshPartBuilder builder, Matrix4 transform, float width, float height, float depth, int divisionsU, int divisionsV) {
        build(builder, transform, width, height, depth, divisionsU, divisionsV, 0.0f, 360.0f, 0.0f, 180.0f);
    }

    public static void build(MeshPartBuilder builder, float width, float height, float depth, int divisionsU, int divisionsV, float angleUFrom, float angleUTo, float angleVFrom, float angleVTo) {
        build(builder, matTmp1.idt(), width, height, depth, divisionsU, divisionsV, angleUFrom, angleUTo, angleVFrom, angleVTo);
    }

    @Deprecated
    public static void build(MeshPartBuilder builder, Matrix4 transform, float width, float height, float depth, int divisionsU, int divisionsV, float angleUFrom, float angleUTo, float angleVFrom, float angleVTo) {
        float h;
        float v;
        MeshPartBuilder meshPartBuilder = builder;
        int i = divisionsU;
        int i2 = divisionsV;
        float hw = width * 0.5f;
        float hh = height * 0.5f;
        float hd = 0.5f * depth;
        float auo = angleUFrom * 0.017453292f;
        float stepU = ((angleUTo - angleUFrom) * 0.017453292f) / ((float) i);
        float avo = angleVFrom * 0.017453292f;
        float u = ((angleVTo - angleVFrom) * 0.017453292f) / ((float) i2);
        float us = 1.0f / ((float) i);
        float vs = 1.0f / ((float) i2);
        MeshPartBuilder.VertexInfo curr1 = vertTmp3.set((Vector3) null, (Vector3) null, (Color) null, (Vector2) null);
        curr1.hasNormal = true;
        curr1.hasPosition = true;
        curr1.hasUV = true;
        normalTransform.set(transform);
        int s = i + 3;
        tmpIndices.clear();
        tmpIndices.ensureCapacity(i * 2);
        tmpIndices.size = s;
        int tempOffset = 0;
        meshPartBuilder.ensureVertices((i2 + 1) * (i + 1));
        meshPartBuilder.ensureRectangleIndices(i);
        int iv = 0;
        while (iv <= i2) {
            int tempOffset2 = tempOffset;
            float angleV = avo + (((float) iv) * u);
            float v2 = ((float) iv) * vs;
            float t = MathUtils.sin(angleV);
            float h2 = MathUtils.cos(angleV) * hh;
            float hh2 = hh;
            int iu = 0;
            float avo2 = avo;
            int tempOffset3 = tempOffset2;
            while (iu <= i) {
                float stepV = u;
                float angleU = auo + (((float) iu) * stepU);
                float u2 = 1.0f - (((float) iu) * us);
                float us2 = us;
                float hw2 = hw;
                float hd2 = hd;
                curr1.position.set(MathUtils.cos(angleU) * hw * t, h2, MathUtils.sin(angleU) * hd * t);
                curr1.normal.set(curr1.position).mul(normalTransform).nor();
                curr1.position.mul(transform);
                curr1.uv.set(u2, v2);
                tmpIndices.set(tempOffset3, meshPartBuilder.vertex(curr1));
                int o = tempOffset3 + s;
                if (iv <= 0 || iu <= 0) {
                    v = v2;
                    h = h2;
                } else {
                    v = v2;
                    h = h2;
                    meshPartBuilder.rect(tmpIndices.get(tempOffset3), tmpIndices.get((o - 1) % s), tmpIndices.get((o - (i + 2)) % s), tmpIndices.get((o - (i + 1)) % s));
                }
                tempOffset3 = (tempOffset3 + 1) % tmpIndices.size;
                iu++;
                i = divisionsU;
                float f = u2;
                u = stepV;
                us = us2;
                hw = hw2;
                hd = hd2;
                v2 = v;
                h2 = h;
            }
            float v3 = v2;
            float f2 = h2;
            float f3 = hd;
            float f4 = hw;
            float f5 = us;
            float f6 = u;
            iv++;
            i = divisionsU;
            i2 = divisionsV;
            tempOffset = tempOffset3;
            avo = avo2;
            hh = hh2;
            float f7 = v3;
        }
        int i3 = tempOffset;
        float f8 = hd;
        float f9 = hw;
        float f10 = hh;
        float f11 = us;
        float f12 = avo;
        float f13 = u;
    }
}
