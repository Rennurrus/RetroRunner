package com.badlogic.gdx.graphics.g3d.utils.shapebuilders;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class BoxShapeBuilder extends BaseShapeBuilder {
    public static void build(MeshPartBuilder builder, BoundingBox box) {
        builder.box(box.getCorner000(obtainV3()), box.getCorner010(obtainV3()), box.getCorner100(obtainV3()), box.getCorner110(obtainV3()), box.getCorner001(obtainV3()), box.getCorner011(obtainV3()), box.getCorner101(obtainV3()), box.getCorner111(obtainV3()));
        freeAll();
    }

    public static void build(MeshPartBuilder builder, MeshPartBuilder.VertexInfo corner000, MeshPartBuilder.VertexInfo corner010, MeshPartBuilder.VertexInfo corner100, MeshPartBuilder.VertexInfo corner110, MeshPartBuilder.VertexInfo corner001, MeshPartBuilder.VertexInfo corner011, MeshPartBuilder.VertexInfo corner101, MeshPartBuilder.VertexInfo corner111) {
        MeshPartBuilder meshPartBuilder = builder;
        meshPartBuilder.ensureVertices(8);
        short i000 = builder.vertex(corner000);
        short i100 = meshPartBuilder.vertex(corner100);
        short i110 = meshPartBuilder.vertex(corner110);
        short i010 = meshPartBuilder.vertex(corner010);
        short i001 = meshPartBuilder.vertex(corner001);
        short i101 = meshPartBuilder.vertex(corner101);
        short i111 = meshPartBuilder.vertex(corner111);
        short i011 = meshPartBuilder.vertex(corner011);
        int primitiveType = builder.getPrimitiveType();
        if (primitiveType == 1) {
            meshPartBuilder.ensureIndices(24);
            meshPartBuilder.rect(i000, i100, i110, i010);
            meshPartBuilder.rect(i101, i001, i011, i111);
            short i0112 = i011;
            short i1112 = i111;
            short i1012 = i101;
            short s = i001;
            short s2 = i010;
            builder.index(i000, i001, i010, i0112, i110, i1112, i100, i1012);
            short s3 = i0112;
            short s4 = i1112;
            short s5 = i1012;
            return;
        }
        short i0113 = i011;
        short i1113 = i111;
        short i1013 = i101;
        short i0012 = i001;
        short i0102 = i010;
        if (primitiveType == 0) {
            meshPartBuilder.ensureRectangleIndices(2);
            meshPartBuilder.rect(i000, i100, i110, i0102);
            meshPartBuilder.rect(i1013, i0012, i0113, i1113);
            return;
        }
        short i0114 = i0113;
        short i1114 = i1113;
        short i1014 = i1013;
        meshPartBuilder.ensureRectangleIndices(6);
        meshPartBuilder.rect(i000, i100, i110, i0102);
        meshPartBuilder.rect(i1014, i0012, i0114, i1114);
        meshPartBuilder.rect(i000, i0102, i0114, i0012);
        meshPartBuilder.rect(i1014, i1114, i110, i100);
        meshPartBuilder.rect(i1014, i100, i000, i0012);
        meshPartBuilder.rect(i110, i1114, i0114, i0102);
    }

    public static void build(MeshPartBuilder builder, Vector3 corner000, Vector3 corner010, Vector3 corner100, Vector3 corner110, Vector3 corner001, Vector3 corner011, Vector3 corner101, Vector3 corner111) {
        MeshPartBuilder meshPartBuilder = builder;
        Vector3 vector3 = corner000;
        Vector3 vector32 = corner010;
        Vector3 vector33 = corner100;
        Vector3 vector34 = corner110;
        Vector3 vector35 = corner001;
        Vector3 vector36 = corner011;
        Vector3 vector37 = corner101;
        Vector3 vector38 = corner111;
        if ((builder.getAttributes().getMask() & 408) == 0) {
            Vector3 vector39 = vector38;
            Vector3 vector310 = vector37;
            build(builder, vertTmp1.set(vector3, (Vector3) null, (Color) null, (Vector2) null), vertTmp2.set(vector32, (Vector3) null, (Color) null, (Vector2) null), vertTmp3.set(vector33, (Vector3) null, (Color) null, (Vector2) null), vertTmp4.set(vector34, (Vector3) null, (Color) null, (Vector2) null), vertTmp5.set(vector35, (Vector3) null, (Color) null, (Vector2) null), vertTmp6.set(vector36, (Vector3) null, (Color) null, (Vector2) null), vertTmp7.set(vector37, (Vector3) null, (Color) null, (Vector2) null), vertTmp8.set(vector38, (Vector3) null, (Color) null, (Vector2) null));
            Vector3 vector311 = corner100;
            Vector3 vector312 = corner011;
            return;
        }
        Vector3 vector313 = vector38;
        meshPartBuilder.ensureVertices(24);
        meshPartBuilder.ensureRectangleIndices(6);
        Vector3 nor = tmpV1.set(vector3).lerp(vector34, 0.5f).sub(tmpV2.set(vector35).lerp(vector313, 0.5f)).nor();
        MeshPartBuilder meshPartBuilder2 = builder;
        meshPartBuilder2.rect(corner000, corner010, corner110, corner100, nor);
        meshPartBuilder2.rect(corner011, corner001, corner101, corner111, nor.scl(-1.0f));
        Vector3 nor2 = tmpV1.set(vector3).lerp(vector37, 0.5f).sub(tmpV2.set(vector32).lerp(vector313, 0.5f)).nor();
        MeshPartBuilder meshPartBuilder3 = builder;
        meshPartBuilder3.rect(corner001, corner000, corner100, corner101, nor2);
        Vector3 vector314 = corner011;
        meshPartBuilder3.rect(corner010, vector314, corner111, corner110, nor2.scl(-1.0f));
        Vector3 nor3 = tmpV1.set(vector3).lerp(corner011, 0.5f).sub(tmpV2.set(corner100).lerp(vector313, 0.5f)).nor();
        MeshPartBuilder meshPartBuilder4 = builder;
        meshPartBuilder4.rect(corner001, vector314, corner010, corner000, nor3);
        meshPartBuilder4.rect(corner100, corner110, corner111, corner101, nor3.scl(-1.0f));
    }

    public static void build(MeshPartBuilder builder, Matrix4 transform) {
        build(builder, obtainV3().set(-0.5f, -0.5f, -0.5f).mul(transform), obtainV3().set(-0.5f, 0.5f, -0.5f).mul(transform), obtainV3().set(0.5f, -0.5f, -0.5f).mul(transform), obtainV3().set(0.5f, 0.5f, -0.5f).mul(transform), obtainV3().set(-0.5f, -0.5f, 0.5f).mul(transform), obtainV3().set(-0.5f, 0.5f, 0.5f).mul(transform), obtainV3().set(0.5f, -0.5f, 0.5f).mul(transform), obtainV3().set(0.5f, 0.5f, 0.5f).mul(transform));
        freeAll();
    }

    public static void build(MeshPartBuilder builder, float width, float height, float depth) {
        build(builder, 0.0f, 0.0f, 0.0f, width, height, depth);
    }

    public static void build(MeshPartBuilder builder, float x, float y, float z, float width, float height, float depth) {
        float hw = width * 0.5f;
        float hh = height * 0.5f;
        float hd = 0.5f * depth;
        float x0 = x - hw;
        float y0 = y - hh;
        float z0 = z - hd;
        float x1 = x + hw;
        float y1 = y + hh;
        float z1 = z + hd;
        build(builder, obtainV3().set(x0, y0, z0), obtainV3().set(x0, y1, z0), obtainV3().set(x1, y0, z0), obtainV3().set(x1, y1, z0), obtainV3().set(x0, y0, z1), obtainV3().set(x0, y1, z1), obtainV3().set(x1, y0, z1), obtainV3().set(x1, y1, z1));
        freeAll();
    }
}
