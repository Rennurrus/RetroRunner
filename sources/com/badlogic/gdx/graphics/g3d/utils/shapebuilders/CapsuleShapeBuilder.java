package com.badlogic.gdx.graphics.g3d.utils.shapebuilders;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class CapsuleShapeBuilder extends BaseShapeBuilder {
    public static void build(MeshPartBuilder builder, float radius, float height, int divisions) {
        if (height >= radius * 2.0f) {
            float d = 2.0f * radius;
            float f = d;
            CylinderShapeBuilder.build(builder, d, height - d, f, divisions, 0.0f, 360.0f, false);
            float f2 = d;
            float f3 = d;
            int i = divisions;
            int i2 = divisions;
            SphereShapeBuilder.build(builder, matTmp1.setToTranslation(0.0f, (height - d) * 0.5f, 0.0f), f2, f, f3, i, i2, 0.0f, 360.0f, 0.0f, 90.0f);
            SphereShapeBuilder.build(builder, matTmp1.setToTranslation(0.0f, (height - d) * -0.5f, 0.0f), f2, f, f3, i, i2, 0.0f, 360.0f, 90.0f, 180.0f);
            return;
        }
        throw new GdxRuntimeException("Height must be at least twice the radius");
    }
}
