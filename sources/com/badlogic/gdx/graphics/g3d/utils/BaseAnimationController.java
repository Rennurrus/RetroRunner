package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Animation;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodeAnimation;
import com.badlogic.gdx.graphics.g3d.model.NodeKeyframe;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;
import java.util.Iterator;

public class BaseAnimationController {
    private static final Transform tmpT = new Transform();
    private static final ObjectMap<Node, Transform> transforms = new ObjectMap<>();
    private boolean applying = false;
    public final ModelInstance target;
    private final Pool<Transform> transformPool = new Pool<Transform>() {
        /* access modifiers changed from: protected */
        public Transform newObject() {
            return new Transform();
        }
    };

    public static final class Transform implements Pool.Poolable {
        public final Quaternion rotation = new Quaternion();
        public final Vector3 scale = new Vector3(1.0f, 1.0f, 1.0f);
        public final Vector3 translation = new Vector3();

        public Transform idt() {
            this.translation.set(0.0f, 0.0f, 0.0f);
            this.rotation.idt();
            this.scale.set(1.0f, 1.0f, 1.0f);
            return this;
        }

        public Transform set(Vector3 t, Quaternion r, Vector3 s) {
            this.translation.set(t);
            this.rotation.set(r);
            this.scale.set(s);
            return this;
        }

        public Transform set(Transform other) {
            return set(other.translation, other.rotation, other.scale);
        }

        public Transform lerp(Transform target, float alpha) {
            return lerp(target.translation, target.rotation, target.scale, alpha);
        }

        public Transform lerp(Vector3 targetT, Quaternion targetR, Vector3 targetS, float alpha) {
            this.translation.lerp(targetT, alpha);
            this.rotation.slerp(targetR, alpha);
            this.scale.lerp(targetS, alpha);
            return this;
        }

        public Matrix4 toMatrix4(Matrix4 out) {
            return out.set(this.translation, this.rotation, this.scale);
        }

        public void reset() {
            idt();
        }

        public String toString() {
            return this.translation.toString() + " - " + this.rotation.toString() + " - " + this.scale.toString();
        }
    }

    public BaseAnimationController(ModelInstance target2) {
        this.target = target2;
    }

    /* access modifiers changed from: protected */
    public void begin() {
        if (!this.applying) {
            this.applying = true;
            return;
        }
        throw new GdxRuntimeException("You must call end() after each call to being()");
    }

    /* access modifiers changed from: protected */
    public void apply(Animation animation, float time, float weight) {
        if (this.applying) {
            applyAnimation(transforms, this.transformPool, weight, animation, time);
            return;
        }
        throw new GdxRuntimeException("You must call begin() before adding an animation");
    }

    /* access modifiers changed from: protected */
    public void end() {
        if (this.applying) {
            ObjectMap.Entries<Node, Transform> it = transforms.entries().iterator();
            while (it.hasNext()) {
                ObjectMap.Entry<Node, Transform> entry = (ObjectMap.Entry) it.next();
                ((Transform) entry.value).toMatrix4(((Node) entry.key).localTransform);
                this.transformPool.free(entry.value);
            }
            transforms.clear();
            this.target.calculateTransforms();
            this.applying = false;
            return;
        }
        throw new GdxRuntimeException("You must call begin() first");
    }

    /* access modifiers changed from: protected */
    public void applyAnimation(Animation animation, float time) {
        if (!this.applying) {
            applyAnimation((ObjectMap<Node, Transform>) null, (Pool<Transform>) null, 1.0f, animation, time);
            this.target.calculateTransforms();
            return;
        }
        throw new GdxRuntimeException("Call end() first");
    }

    /* access modifiers changed from: protected */
    public void applyAnimations(Animation anim1, float time1, Animation anim2, float time2, float weight) {
        if (anim2 == null || weight == 0.0f) {
            applyAnimation(anim1, time1);
        } else if (anim1 == null || weight == 1.0f) {
            applyAnimation(anim2, time2);
        } else if (!this.applying) {
            begin();
            apply(anim1, time1, 1.0f);
            apply(anim2, time2, weight);
            end();
        } else {
            throw new GdxRuntimeException("Call end() first");
        }
    }

    static final <T> int getFirstKeyframeIndexAtTime(Array<NodeKeyframe<T>> arr, float time) {
        int lastIndex = arr.size - 1;
        if (lastIndex <= 0 || time < arr.get(0).keytime || time > arr.get(lastIndex).keytime) {
            return 0;
        }
        int minIndex = 0;
        int maxIndex = lastIndex;
        while (minIndex < maxIndex) {
            int i = (minIndex + maxIndex) / 2;
            if (time > arr.get(i + 1).keytime) {
                minIndex = i + 1;
            } else if (time >= arr.get(i).keytime) {
                return i;
            } else {
                maxIndex = i - 1;
            }
        }
        return minIndex;
    }

    private static final Vector3 getTranslationAtTime(NodeAnimation nodeAnim, float time, Vector3 out) {
        if (nodeAnim.translation == null) {
            return out.set(nodeAnim.node.translation);
        }
        if (nodeAnim.translation.size == 1) {
            return out.set((Vector3) nodeAnim.translation.get(0).value);
        }
        int index = getFirstKeyframeIndexAtTime(nodeAnim.translation, time);
        NodeKeyframe firstKeyframe = nodeAnim.translation.get(index);
        out.set((Vector3) firstKeyframe.value);
        int index2 = index + 1;
        if (index2 < nodeAnim.translation.size) {
            NodeKeyframe<Vector3> secondKeyframe = nodeAnim.translation.get(index2);
            out.lerp((Vector3) secondKeyframe.value, (time - firstKeyframe.keytime) / (secondKeyframe.keytime - firstKeyframe.keytime));
        }
        return out;
    }

    private static final Quaternion getRotationAtTime(NodeAnimation nodeAnim, float time, Quaternion out) {
        if (nodeAnim.rotation == null) {
            return out.set(nodeAnim.node.rotation);
        }
        if (nodeAnim.rotation.size == 1) {
            return out.set((Quaternion) nodeAnim.rotation.get(0).value);
        }
        int index = getFirstKeyframeIndexAtTime(nodeAnim.rotation, time);
        NodeKeyframe firstKeyframe = nodeAnim.rotation.get(index);
        out.set((Quaternion) firstKeyframe.value);
        int index2 = index + 1;
        if (index2 < nodeAnim.rotation.size) {
            NodeKeyframe<Quaternion> secondKeyframe = nodeAnim.rotation.get(index2);
            out.slerp((Quaternion) secondKeyframe.value, (time - firstKeyframe.keytime) / (secondKeyframe.keytime - firstKeyframe.keytime));
        }
        return out;
    }

    private static final Vector3 getScalingAtTime(NodeAnimation nodeAnim, float time, Vector3 out) {
        if (nodeAnim.scaling == null) {
            return out.set(nodeAnim.node.scale);
        }
        if (nodeAnim.scaling.size == 1) {
            return out.set((Vector3) nodeAnim.scaling.get(0).value);
        }
        int index = getFirstKeyframeIndexAtTime(nodeAnim.scaling, time);
        NodeKeyframe firstKeyframe = nodeAnim.scaling.get(index);
        out.set((Vector3) firstKeyframe.value);
        int index2 = index + 1;
        if (index2 < nodeAnim.scaling.size) {
            NodeKeyframe<Vector3> secondKeyframe = nodeAnim.scaling.get(index2);
            out.lerp((Vector3) secondKeyframe.value, (time - firstKeyframe.keytime) / (secondKeyframe.keytime - firstKeyframe.keytime));
        }
        return out;
    }

    private static final Transform getNodeAnimationTransform(NodeAnimation nodeAnim, float time) {
        Transform transform = tmpT;
        getTranslationAtTime(nodeAnim, time, transform.translation);
        getRotationAtTime(nodeAnim, time, transform.rotation);
        getScalingAtTime(nodeAnim, time, transform.scale);
        return transform;
    }

    private static final void applyNodeAnimationDirectly(NodeAnimation nodeAnim, float time) {
        Node node = nodeAnim.node;
        node.isAnimated = true;
        getNodeAnimationTransform(nodeAnim, time).toMatrix4(node.localTransform);
    }

    private static final void applyNodeAnimationBlending(NodeAnimation nodeAnim, ObjectMap<Node, Transform> out, Pool<Transform> pool, float alpha, float time) {
        Node node = nodeAnim.node;
        node.isAnimated = true;
        Transform transform = getNodeAnimationTransform(nodeAnim, time);
        Transform t = out.get(node, null);
        if (t != null) {
            if (alpha > 0.999999f) {
                t.set(transform);
            } else {
                t.lerp(transform, alpha);
            }
        } else if (alpha > 0.999999f) {
            out.put(node, pool.obtain().set(transform));
        } else {
            out.put(node, pool.obtain().set(node.translation, node.rotation, node.scale).lerp(transform, alpha));
        }
    }

    protected static void applyAnimation(ObjectMap<Node, Transform> out, Pool<Transform> pool, float alpha, Animation animation, float time) {
        if (out == null) {
            Iterator<NodeAnimation> it = animation.nodeAnimations.iterator();
            while (it.hasNext()) {
                applyNodeAnimationDirectly(it.next(), time);
            }
            return;
        }
        ObjectMap.Keys<Node> it2 = out.keys().iterator();
        while (it2.hasNext()) {
            ((Node) it2.next()).isAnimated = false;
        }
        Iterator<NodeAnimation> it3 = animation.nodeAnimations.iterator();
        while (it3.hasNext()) {
            applyNodeAnimationBlending(it3.next(), out, pool, alpha, time);
        }
        ObjectMap.Entries<Node, Transform> it4 = out.entries().iterator();
        while (it4.hasNext()) {
            ObjectMap.Entry<Node, Transform> e = (ObjectMap.Entry) it4.next();
            if (!((Node) e.key).isAnimated) {
                ((Node) e.key).isAnimated = true;
                ((Transform) e.value).lerp(((Node) e.key).translation, ((Node) e.key).rotation, ((Node) e.key).scale, alpha);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void removeAnimation(Animation animation) {
        Iterator<NodeAnimation> it = animation.nodeAnimations.iterator();
        while (it.hasNext()) {
            it.next().node.isAnimated = false;
        }
    }
}
