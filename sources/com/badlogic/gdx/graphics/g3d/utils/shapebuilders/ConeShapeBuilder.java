package com.badlogic.gdx.graphics.g3d.utils.shapebuilders;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class ConeShapeBuilder extends BaseShapeBuilder {
    public static void build(MeshPartBuilder builder, float width, float height, float depth, int divisions) {
        build(builder, width, height, depth, divisions, 0.0f, 360.0f);
    }

    public static void build(MeshPartBuilder builder, float width, float height, float depth, int divisions, float angleFrom, float angleTo) {
        build(builder, width, height, depth, divisions, angleFrom, angleTo, true);
    }

    public static void build(MeshPartBuilder builder, float width, float height, float depth, int divisions, float angleFrom, float angleTo, boolean close) {
        MeshPartBuilder meshPartBuilder = builder;
        int i = divisions;
        meshPartBuilder.ensureVertices(i + 2);
        meshPartBuilder.ensureTriangleIndices(i);
        float hw = width * 0.5f;
        float hh = height * 0.5f;
        float hd = depth * 0.5f;
        float ao = angleFrom * 0.017453292f;
        float step = ((angleTo - angleFrom) * 0.017453292f) / ((float) i);
        float us = 1.0f / ((float) i);
        MeshPartBuilder.VertexInfo curr1 = vertTmp3.set((Vector3) null, (Vector3) null, (Color) null, (Vector2) null);
        curr1.hasNormal = true;
        curr1.hasPosition = true;
        curr1.hasUV = true;
        MeshPartBuilder.VertexInfo curr2 = vertTmp4.set((Vector3) null, (Vector3) null, (Color) null, (Vector2) null).setPos(0.0f, hh, 0.0f).setNor(0.0f, 1.0f, 0.0f).setUV(0.5f, 0.0f);
        short base = meshPartBuilder.vertex(curr2);
        int i2 = 0;
        short i22 = 0;
        while (i2 <= i) {
            float angle = ao + (((float) i2) * step);
            float u = 1.0f - (((float) i2) * us);
            curr1.position.set(MathUtils.cos(angle) * hw, 0.0f, MathUtils.sin(angle) * hd);
            curr1.normal.set(curr1.position).nor();
            curr1.position.y = -hh;
            curr1.uv.set(u, 1.0f);
            short i1 = meshPartBuilder.vertex(curr1);
            if (i2 != 0) {
                meshPartBuilder.triangle(base, i1, i22);
            }
            i22 = i1;
            i2++;
            float f = u;
        }
        if (close) {
            short s = i22;
            short s2 = base;
            MeshPartBuilder.VertexInfo vertexInfo = curr1;
            MeshPartBuilder.VertexInfo vertexInfo2 = curr2;
            float f2 = hh;
            EllipseShapeBuilder.build(builder, width, depth, 0.0f, 0.0f, divisions, 0.0f, -hh, 0.0f, 0.0f, -1.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 180.0f - angleTo, 180.0f - angleFrom);
            return;
        }
        float f3 = hh;
        short s3 = i22;
        short s4 = base;
        MeshPartBuilder.VertexInfo vertexInfo3 = curr1;
    }
}
