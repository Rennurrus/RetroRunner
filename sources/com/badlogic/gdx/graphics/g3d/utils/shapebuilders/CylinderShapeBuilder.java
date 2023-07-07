package com.badlogic.gdx.graphics.g3d.utils.shapebuilders;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class CylinderShapeBuilder extends BaseShapeBuilder {
    public static void build(MeshPartBuilder builder, float width, float height, float depth, int divisions) {
        build(builder, width, height, depth, divisions, 0.0f, 360.0f);
    }

    public static void build(MeshPartBuilder builder, float width, float height, float depth, int divisions, float angleFrom, float angleTo) {
        build(builder, width, height, depth, divisions, angleFrom, angleTo, true);
    }

    public static void build(MeshPartBuilder builder, float width, float height, float depth, int divisions, float angleFrom, float angleTo, boolean close) {
        MeshPartBuilder meshPartBuilder = builder;
        int i = divisions;
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
        MeshPartBuilder.VertexInfo curr2 = vertTmp4.set((Vector3) null, (Vector3) null, (Color) null, (Vector2) null);
        curr2.hasNormal = true;
        curr2.hasPosition = true;
        curr2.hasUV = true;
        meshPartBuilder.ensureVertices((i + 1) * 2);
        meshPartBuilder.ensureRectangleIndices(i);
        int i2 = 0;
        short i3 = 0;
        short i4 = 0;
        while (i2 <= i) {
            float angle = ao + (((float) i2) * step);
            float u = 1.0f - (((float) i2) * us);
            curr1.position.set(MathUtils.cos(angle) * hw, 0.0f, MathUtils.sin(angle) * hd);
            curr1.normal.set(curr1.position).nor();
            curr1.position.y = -hh;
            curr1.uv.set(u, 1.0f);
            curr2.position.set(curr1.position);
            curr2.normal.set(curr1.normal);
            curr2.position.y = hh;
            curr2.uv.set(u, 0.0f);
            short i22 = meshPartBuilder.vertex(curr1);
            short i1 = meshPartBuilder.vertex(curr2);
            if (i2 != 0) {
                meshPartBuilder.rect(i3, i1, i22, i4);
            }
            i4 = i22;
            i3 = i1;
            i2++;
            float f = u;
        }
        if (close) {
            short s = i4;
            MeshPartBuilder.VertexInfo vertexInfo = curr2;
            MeshPartBuilder.VertexInfo vertexInfo2 = curr1;
            short s2 = i3;
            float f2 = width;
            float hh2 = hh;
            float hh3 = depth;
            EllipseShapeBuilder.build(builder, f2, hh3, 0.0f, 0.0f, divisions, 0.0f, hh2, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, angleFrom, angleTo);
            EllipseShapeBuilder.build(builder, f2, hh3, 0.0f, 0.0f, divisions, 0.0f, -hh2, 0.0f, 0.0f, -1.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 180.0f - angleTo, 180.0f - angleFrom);
            return;
        }
        float f3 = hh;
        short s3 = i4;
        MeshPartBuilder.VertexInfo vertexInfo3 = curr2;
        MeshPartBuilder.VertexInfo vertexInfo4 = curr1;
    }
}
