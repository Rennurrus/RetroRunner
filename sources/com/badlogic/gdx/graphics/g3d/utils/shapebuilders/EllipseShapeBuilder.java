package com.badlogic.gdx.graphics.g3d.utils.shapebuilders;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class EllipseShapeBuilder extends BaseShapeBuilder {
    public static void build(MeshPartBuilder builder, float radius, int divisions, float centerX, float centerY, float centerZ, float normalX, float normalY, float normalZ) {
        build(builder, radius, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ, 0.0f, 360.0f);
    }

    public static void build(MeshPartBuilder builder, float radius, int divisions, Vector3 center, Vector3 normal) {
        build(builder, radius, divisions, center.x, center.y, center.z, normal.x, normal.y, normal.z);
    }

    public static void build(MeshPartBuilder builder, float radius, int divisions, Vector3 center, Vector3 normal, Vector3 tangent, Vector3 binormal) {
        Vector3 vector3 = center;
        Vector3 vector32 = normal;
        Vector3 vector33 = tangent;
        Vector3 vector34 = binormal;
        float f = vector3.x;
        float f2 = vector3.y;
        float f3 = vector3.z;
        float f4 = vector32.x;
        float f5 = vector32.y;
        float f6 = vector32.z;
        float f7 = vector33.x;
        float f8 = vector33.y;
        float f9 = vector33.z;
        float f10 = vector34.x;
        float f11 = vector34.y;
        float f12 = f11;
        float f13 = f10;
        build(builder, radius, divisions, f, f2, f3, f4, f5, f6, f7, f8, f9, f13, f12, vector34.z);
    }

    public static void build(MeshPartBuilder builder, float radius, int divisions, float centerX, float centerY, float centerZ, float normalX, float normalY, float normalZ, float tangentX, float tangentY, float tangentZ, float binormalX, float binormalY, float binormalZ) {
        build(builder, radius, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ, tangentX, tangentY, tangentZ, binormalX, binormalY, binormalZ, 0.0f, 360.0f);
    }

    public static void build(MeshPartBuilder builder, float radius, int divisions, float centerX, float centerY, float centerZ, float normalX, float normalY, float normalZ, float angleFrom, float angleTo) {
        build(builder, radius * 2.0f, radius * 2.0f, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ, angleFrom, angleTo);
    }

    public static void build(MeshPartBuilder builder, float radius, int divisions, Vector3 center, Vector3 normal, float angleFrom, float angleTo) {
        Vector3 vector3 = center;
        Vector3 vector32 = normal;
        build(builder, radius, divisions, vector3.x, vector3.y, vector3.z, vector32.x, vector32.y, vector32.z, angleFrom, angleTo);
    }

    public static void build(MeshPartBuilder builder, float radius, int divisions, Vector3 center, Vector3 normal, Vector3 tangent, Vector3 binormal, float angleFrom, float angleTo) {
        Vector3 vector3 = center;
        Vector3 vector32 = normal;
        Vector3 vector33 = tangent;
        Vector3 vector34 = binormal;
        build(builder, radius, divisions, vector3.x, vector3.y, vector3.z, vector32.x, vector32.y, vector32.z, vector33.x, vector33.y, vector33.z, vector34.x, vector34.y, vector34.z, angleFrom, angleTo);
    }

    public static void build(MeshPartBuilder builder, float radius, int divisions, float centerX, float centerY, float centerZ, float normalX, float normalY, float normalZ, float tangentX, float tangentY, float tangentZ, float binormalX, float binormalY, float binormalZ, float angleFrom, float angleTo) {
        build(builder, radius * 2.0f, 2.0f * radius, 0.0f, 0.0f, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ, tangentX, tangentY, tangentZ, binormalX, binormalY, binormalZ, angleFrom, angleTo);
    }

    public static void build(MeshPartBuilder builder, float width, float height, int divisions, float centerX, float centerY, float centerZ, float normalX, float normalY, float normalZ) {
        build(builder, width, height, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ, 0.0f, 360.0f);
    }

    public static void build(MeshPartBuilder builder, float width, float height, int divisions, Vector3 center, Vector3 normal) {
        build(builder, width, height, divisions, center.x, center.y, center.z, normal.x, normal.y, normal.z);
    }

    public static void build(MeshPartBuilder builder, float width, float height, int divisions, Vector3 center, Vector3 normal, Vector3 tangent, Vector3 binormal) {
        Vector3 vector3 = center;
        Vector3 vector32 = normal;
        Vector3 vector33 = tangent;
        Vector3 vector34 = binormal;
        float f = vector3.x;
        float f2 = vector3.y;
        float f3 = vector3.z;
        float f4 = vector32.x;
        float f5 = vector32.y;
        float f6 = vector32.z;
        float f7 = vector33.x;
        float f8 = vector33.y;
        float f9 = vector33.z;
        float f10 = vector34.x;
        float f11 = vector34.y;
        float f12 = f11;
        float f13 = f10;
        float f14 = f9;
        build(builder, width, height, divisions, f, f2, f3, f4, f5, f6, f7, f8, f14, f13, f12, vector34.z);
    }

    public static void build(MeshPartBuilder builder, float width, float height, int divisions, float centerX, float centerY, float centerZ, float normalX, float normalY, float normalZ, float tangentX, float tangentY, float tangentZ, float binormalX, float binormalY, float binormalZ) {
        build(builder, width, height, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ, tangentX, tangentY, tangentZ, binormalX, binormalY, binormalZ, 0.0f, 360.0f);
    }

    public static void build(MeshPartBuilder builder, float width, float height, int divisions, float centerX, float centerY, float centerZ, float normalX, float normalY, float normalZ, float angleFrom, float angleTo) {
        build(builder, width, height, 0.0f, 0.0f, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ, angleFrom, angleTo);
    }

    public static void build(MeshPartBuilder builder, float width, float height, int divisions, Vector3 center, Vector3 normal, float angleFrom, float angleTo) {
        Vector3 vector3 = center;
        Vector3 vector32 = normal;
        build(builder, width, height, 0.0f, 0.0f, divisions, vector3.x, vector3.y, vector3.z, vector32.x, vector32.y, vector32.z, angleFrom, angleTo);
    }

    public static void build(MeshPartBuilder builder, float width, float height, int divisions, Vector3 center, Vector3 normal, Vector3 tangent, Vector3 binormal, float angleFrom, float angleTo) {
        Vector3 vector3 = center;
        Vector3 vector32 = normal;
        Vector3 vector33 = tangent;
        Vector3 vector34 = binormal;
        build(builder, width, height, 0.0f, 0.0f, divisions, vector3.x, vector3.y, vector3.z, vector32.x, vector32.y, vector32.z, vector33.x, vector33.y, vector33.z, vector34.x, vector34.y, vector34.z, angleFrom, angleTo);
    }

    public static void build(MeshPartBuilder builder, float width, float height, int divisions, float centerX, float centerY, float centerZ, float normalX, float normalY, float normalZ, float tangentX, float tangentY, float tangentZ, float binormalX, float binormalY, float binormalZ, float angleFrom, float angleTo) {
        build(builder, width, height, 0.0f, 0.0f, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ, tangentX, tangentY, tangentZ, binormalX, binormalY, binormalZ, angleFrom, angleTo);
    }

    public static void build(MeshPartBuilder builder, float width, float height, float innerWidth, float innerHeight, int divisions, float centerX, float centerY, float centerZ, float normalX, float normalY, float normalZ, float angleFrom, float angleTo) {
        float f = normalX;
        float f2 = normalY;
        float f3 = normalZ;
        tmpV1.set(f, f2, f3).crs(0.0f, 0.0f, 1.0f);
        tmpV2.set(f, f2, f3).crs(0.0f, 1.0f, 0.0f);
        if (tmpV2.len2() > tmpV1.len2()) {
            tmpV1.set(tmpV2);
        }
        tmpV2.set(tmpV1.nor()).crs(f, f2, f3).nor();
        build(builder, width, height, innerWidth, innerHeight, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ, tmpV1.x, tmpV1.y, tmpV1.z, tmpV2.x, tmpV2.y, tmpV2.z, angleFrom, angleTo);
    }

    public static void build(MeshPartBuilder builder, float width, float height, float innerWidth, float innerHeight, int divisions, float centerX, float centerY, float centerZ, float normalX, float normalY, float normalZ) {
        build(builder, width, height, innerWidth, innerHeight, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ, 0.0f, 360.0f);
    }

    public static void build(MeshPartBuilder builder, float width, float height, float innerWidth, float innerHeight, int divisions, Vector3 center, Vector3 normal) {
        Vector3 vector3 = center;
        Vector3 vector32 = normal;
        build(builder, width, height, innerWidth, innerHeight, divisions, vector3.x, vector3.y, vector3.z, vector32.x, vector32.y, vector32.z, 0.0f, 360.0f);
    }

    public static void build(MeshPartBuilder builder, float width, float height, float innerWidth, float innerHeight, int divisions, float centerX, float centerY, float centerZ, float normalX, float normalY, float normalZ, float tangentX, float tangentY, float tangentZ, float binormalX, float binormalY, float binormalZ, float angleFrom, float angleTo) {
        Vector3 syIn;
        Vector3 syIn2;
        short i4;
        short i3;
        short i2;
        MeshPartBuilder meshPartBuilder = builder;
        int i = divisions;
        float f = centerX;
        float f2 = centerY;
        float f3 = centerZ;
        float f4 = normalX;
        float f5 = normalY;
        float f6 = normalZ;
        float f7 = tangentX;
        float f8 = tangentY;
        float f9 = tangentZ;
        float f10 = binormalX;
        float f11 = binormalY;
        float f12 = binormalZ;
        if (innerWidth <= 0.0f || innerHeight <= 0.0f) {
            meshPartBuilder.ensureVertices(i + 2);
            meshPartBuilder.ensureTriangleIndices(i);
        } else if (innerWidth == width && innerHeight == height) {
            meshPartBuilder.ensureVertices(i + 1);
            meshPartBuilder.ensureIndices(i + 1);
            if (builder.getPrimitiveType() != 1) {
                throw new GdxRuntimeException("Incorrect primitive type : expect GL_LINES because innerWidth == width && innerHeight == height");
            }
        } else {
            meshPartBuilder.ensureVertices((i + 1) * 2);
            meshPartBuilder.ensureRectangleIndices(i + 1);
        }
        float ao = angleFrom * 0.017453292f;
        float step = ((angleTo - angleFrom) * 0.017453292f) / ((float) i);
        Vector3 sxEx = tmpV1.set(f7, f8, f9).scl(width * 0.5f);
        Vector3 syEx = tmpV2.set(f10, f11, f12).scl(height * 0.5f);
        Vector3 sxIn = tmpV3.set(f7, f8, f9).scl(innerWidth * 0.5f);
        Vector3 syIn3 = tmpV4.set(f10, f11, f12).scl(innerHeight * 0.5f);
        MeshPartBuilder.VertexInfo currIn = vertTmp3.set((Vector3) null, (Vector3) null, (Color) null, (Vector2) null);
        currIn.hasNormal = true;
        currIn.hasPosition = true;
        currIn.hasUV = true;
        currIn.uv.set(0.5f, 0.5f);
        currIn.position.set(f, f2, f3);
        currIn.normal.set(f4, f5, f6);
        MeshPartBuilder.VertexInfo currEx = vertTmp4.set((Vector3) null, (Vector3) null, (Color) null, (Vector2) null);
        currEx.hasNormal = true;
        currEx.hasPosition = true;
        currEx.hasUV = true;
        currEx.uv.set(0.5f, 0.5f);
        currEx.position.set(f, f2, f3);
        currEx.normal.set(f4, f5, f6);
        short center = meshPartBuilder.vertex(currEx);
        float us = (innerWidth / width) * 0.5f;
        float vs = (innerHeight / height) * 0.5f;
        short i22 = 0;
        short i1 = 0;
        short i42 = 0;
        int i5 = 0;
        while (i5 <= divisions) {
            float angle = ao + (((float) i5) * step);
            float x = MathUtils.cos(angle);
            float y = MathUtils.sin(angle);
            short center2 = center;
            Vector3 sxEx2 = sxEx;
            short i32 = i1;
            short i43 = i42;
            Vector3 syIn4 = syIn3;
            Vector3 sxEx3 = sxEx2;
            currEx.position.set(f, f2, f3).add((sxEx2.x * x) + (syEx.x * y), (sxEx2.y * x) + (syEx.y * y), (sxEx2.z * x) + (syEx.z * y));
            currEx.uv.set((x * 0.5f) + 0.5f, (y * 0.5f) + 0.5f);
            short i12 = meshPartBuilder.vertex(currEx);
            if (innerWidth <= 0.0f) {
                i3 = i32;
                i4 = i43;
                syIn2 = syIn4;
                syIn = syEx;
            } else if (innerHeight <= 0.0f) {
                i3 = i32;
                i4 = i43;
                syIn2 = syIn4;
                syIn = syEx;
            } else if (innerWidth == width && innerHeight == height) {
                if (i5 != 0) {
                    meshPartBuilder.line(i12, i22);
                }
                i22 = i12;
                i3 = i32;
                i4 = i43;
                syIn2 = syIn4;
                syIn = syEx;
                i2 = center2;
                i5++;
                float f13 = binormalY;
                center = i2;
                i1 = i3;
                i42 = i4;
                syIn3 = syIn2;
                syEx = syIn;
                sxEx = sxEx3;
                f = centerX;
                float f14 = binormalZ;
            } else {
                syIn2 = syIn4;
                syIn = syEx;
                currIn.position.set(f, f2, f3).add((sxIn.x * x) + (syIn2.x * y), (sxIn.y * x) + (syIn2.y * y), (sxIn.z * x) + (syIn2.z * y));
                currIn.uv.set((us * x) + 0.5f, (vs * y) + 0.5f);
                short i23 = i12;
                short i13 = meshPartBuilder.vertex(currIn);
                if (i5 != 0) {
                    meshPartBuilder.rect(i13, i23, i43, i32);
                } else {
                    short s = i43;
                }
                i22 = i23;
                i3 = i13;
                i4 = i22;
                i2 = center2;
                i5++;
                float f132 = binormalY;
                center = i2;
                i1 = i3;
                i42 = i4;
                syIn3 = syIn2;
                syEx = syIn;
                sxEx = sxEx3;
                f = centerX;
                float f142 = binormalZ;
            }
            if (i5 != 0) {
                i2 = center2;
                meshPartBuilder.triangle(i12, i22, i2);
            } else {
                i2 = center2;
            }
            i22 = i12;
            i5++;
            float f1322 = binormalY;
            center = i2;
            i1 = i3;
            i42 = i4;
            syIn3 = syIn2;
            syEx = syIn;
            sxEx = sxEx3;
            f = centerX;
            float f1422 = binormalZ;
        }
        short s2 = i1;
        Vector3 vector3 = syIn3;
        short s3 = center;
        Vector3 vector32 = sxEx;
        short s4 = i42;
    }
}
