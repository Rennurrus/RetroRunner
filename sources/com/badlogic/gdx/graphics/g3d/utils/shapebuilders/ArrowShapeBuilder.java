package com.badlogic.gdx.graphics.g3d.utils.shapebuilders;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class ArrowShapeBuilder extends BaseShapeBuilder {
    public static void build(MeshPartBuilder builder, float x1, float y1, float z1, float x2, float y2, float z2, float capLength, float stemThickness, int divisions) {
        MeshPartBuilder meshPartBuilder = builder;
        float f = x1;
        float f2 = y1;
        float f3 = z1;
        int i = divisions;
        Vector3 begin = obtainV3().set(f, f2, f3);
        Vector3 end = obtainV3().set(x2, y2, z2);
        float length = begin.dst(end);
        float coneHeight = length * capLength;
        double d = (double) coneHeight;
        double sqrt = Math.sqrt(0.3333333432674408d);
        Double.isNaN(d);
        float coneDiameter = ((float) (d * sqrt)) * 2.0f;
        float stemLength = length - coneHeight;
        float stemDiameter = coneDiameter * stemThickness;
        Vector3 up = obtainV3().set(end).sub(begin).nor();
        Vector3 forward = obtainV3().set(up).crs(Vector3.Z);
        if (forward.isZero()) {
            forward.set(Vector3.X);
        }
        forward.crs(up).nor();
        Vector3 left = obtainV3().set(up).crs(forward).nor();
        Vector3 direction = obtainV3().set(end).sub(begin).nor();
        Vector3 vector3 = begin;
        Matrix4 userTransform = meshPartBuilder.getVertexTransform(obtainM4());
        Vector3 vector32 = end;
        Matrix4 transform = obtainM4();
        float f4 = length;
        float[] val = transform.val;
        float coneHeight2 = coneHeight;
        val[0] = left.x;
        val[4] = up.x;
        val[8] = forward.x;
        val[1] = left.y;
        val[5] = up.y;
        val[9] = forward.y;
        val[2] = left.z;
        val[6] = up.z;
        val[10] = forward.z;
        Matrix4 temp = obtainM4();
        Vector3 vector33 = forward;
        transform.setTranslation(obtainV3().set(direction).scl(stemLength / 2.0f).add(f, f2, f3));
        meshPartBuilder.setVertexTransform(temp.set(transform).mul(userTransform));
        CylinderShapeBuilder.build(meshPartBuilder, stemDiameter, stemLength, stemDiameter, i);
        transform.setTranslation(obtainV3().set(direction).scl(stemLength).add(f, f2, f3));
        meshPartBuilder.setVertexTransform(temp.set(transform).mul(userTransform));
        float coneDiameter2 = coneDiameter;
        ConeShapeBuilder.build(meshPartBuilder, coneDiameter2, coneHeight2, coneDiameter2, i);
        meshPartBuilder.setVertexTransform(userTransform);
        freeAll();
    }
}
