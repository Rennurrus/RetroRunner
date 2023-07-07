package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import java.util.Comparator;

public class DefaultRenderableSorter implements RenderableSorter, Comparator<Renderable> {
    private Camera camera;
    private final Vector3 tmpV1 = new Vector3();
    private final Vector3 tmpV2 = new Vector3();

    public void sort(Camera camera2, Array<Renderable> renderables) {
        this.camera = camera2;
        renderables.sort(this);
    }

    private Vector3 getTranslation(Matrix4 worldTransform, Vector3 center, Vector3 output) {
        if (center.isZero()) {
            worldTransform.getTranslation(output);
        } else if (!worldTransform.hasRotationOrScaling()) {
            worldTransform.getTranslation(output).add(center);
        } else {
            output.set(center).mul(worldTransform);
        }
        return output;
    }

    public int compare(Renderable o1, Renderable o2) {
        int result = 0;
        boolean b1 = o1.material.has(BlendingAttribute.Type) && ((BlendingAttribute) o1.material.get(BlendingAttribute.Type)).blended;
        if (b1 == (o2.material.has(BlendingAttribute.Type) && ((BlendingAttribute) o2.material.get(BlendingAttribute.Type)).blended)) {
            getTranslation(o1.worldTransform, o1.meshPart.center, this.tmpV1);
            getTranslation(o2.worldTransform, o2.meshPart.center, this.tmpV2);
            float dst = (float) (((int) (this.camera.position.dst2(this.tmpV1) * 1000.0f)) - ((int) (this.camera.position.dst2(this.tmpV2) * 1000.0f)));
            if (dst < 0.0f) {
                result = -1;
            } else if (dst > 0.0f) {
                result = 1;
            }
            return b1 ? -result : result;
        } else if (b1) {
            return 1;
        } else {
            return -1;
        }
    }
}
