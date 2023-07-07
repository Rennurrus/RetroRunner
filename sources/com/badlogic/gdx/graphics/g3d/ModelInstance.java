package com.badlogic.gdx.graphics.g3d;

import com.badlogic.gdx.graphics.g3d.model.Animation;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodeAnimation;
import com.badlogic.gdx.graphics.g3d.model.NodeKeyframe;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Pool;
import java.util.Iterator;

public class ModelInstance implements RenderableProvider {
    public static boolean defaultShareKeyframes = true;
    public final Array<Animation> animations;
    public final Array<Material> materials;
    public final Model model;
    public final Array<Node> nodes;
    public Matrix4 transform;
    public Object userData;

    public ModelInstance(Model model2) {
        this(model2, (String[]) null);
    }

    public ModelInstance(Model model2, String nodeId, boolean mergeTransform) {
        this(model2, (Matrix4) null, nodeId, false, false, mergeTransform);
    }

    public ModelInstance(Model model2, Matrix4 transform2, String nodeId, boolean mergeTransform) {
        this(model2, transform2, nodeId, false, false, mergeTransform);
    }

    public ModelInstance(Model model2, String nodeId, boolean parentTransform, boolean mergeTransform) {
        this(model2, (Matrix4) null, nodeId, true, parentTransform, mergeTransform);
    }

    public ModelInstance(Model model2, Matrix4 transform2, String nodeId, boolean parentTransform, boolean mergeTransform) {
        this(model2, transform2, nodeId, true, parentTransform, mergeTransform);
    }

    public ModelInstance(Model model2, String nodeId, boolean recursive, boolean parentTransform, boolean mergeTransform) {
        this(model2, (Matrix4) null, nodeId, recursive, parentTransform, mergeTransform);
    }

    public ModelInstance(Model model2, Matrix4 transform2, String nodeId, boolean recursive, boolean parentTransform, boolean mergeTransform) {
        this(model2, transform2, nodeId, recursive, parentTransform, mergeTransform, defaultShareKeyframes);
    }

    public ModelInstance(Model model2, Matrix4 transform2, String nodeId, boolean recursive, boolean parentTransform, boolean mergeTransform, boolean shareKeyframes) {
        this.materials = new Array<>();
        this.nodes = new Array<>();
        this.animations = new Array<>();
        this.model = model2;
        this.transform = transform2 == null ? new Matrix4() : transform2;
        Node node = model2.getNode(nodeId, recursive);
        Array<Node> array = this.nodes;
        Node copy = node.copy();
        Node copy2 = copy;
        array.add(copy);
        if (mergeTransform) {
            this.transform.mul(parentTransform ? node.globalTransform : node.localTransform);
            copy2.translation.set(0.0f, 0.0f, 0.0f);
            copy2.rotation.idt();
            copy2.scale.set(1.0f, 1.0f, 1.0f);
        } else if (parentTransform && copy2.hasParent()) {
            this.transform.mul(node.getParent().globalTransform);
        }
        invalidate();
        copyAnimations(model2.animations, shareKeyframes);
        calculateTransforms();
    }

    public ModelInstance(Model model2, String... rootNodeIds) {
        this(model2, (Matrix4) null, rootNodeIds);
    }

    public ModelInstance(Model model2, Matrix4 transform2, String... rootNodeIds) {
        this.materials = new Array<>();
        this.nodes = new Array<>();
        this.animations = new Array<>();
        this.model = model2;
        this.transform = transform2 == null ? new Matrix4() : transform2;
        if (rootNodeIds == null) {
            copyNodes(model2.nodes);
        } else {
            copyNodes(model2.nodes, rootNodeIds);
        }
        copyAnimations(model2.animations, defaultShareKeyframes);
        calculateTransforms();
    }

    public ModelInstance(Model model2, Array<String> rootNodeIds) {
        this(model2, (Matrix4) null, rootNodeIds);
    }

    public ModelInstance(Model model2, Matrix4 transform2, Array<String> rootNodeIds) {
        this(model2, transform2, rootNodeIds, defaultShareKeyframes);
    }

    public ModelInstance(Model model2, Matrix4 transform2, Array<String> rootNodeIds, boolean shareKeyframes) {
        this.materials = new Array<>();
        this.nodes = new Array<>();
        this.animations = new Array<>();
        this.model = model2;
        this.transform = transform2 == null ? new Matrix4() : transform2;
        copyNodes(model2.nodes, rootNodeIds);
        copyAnimations(model2.animations, shareKeyframes);
        calculateTransforms();
    }

    public ModelInstance(Model model2, Vector3 position) {
        this(model2);
        this.transform.setToTranslation(position);
    }

    public ModelInstance(Model model2, float x, float y, float z) {
        this(model2);
        this.transform.setToTranslation(x, y, z);
    }

    public ModelInstance(Model model2, Matrix4 transform2) {
        this(model2, transform2, (String[]) null);
    }

    public ModelInstance(ModelInstance copyFrom) {
        this(copyFrom, copyFrom.transform.cpy());
    }

    public ModelInstance(ModelInstance copyFrom, Matrix4 transform2) {
        this(copyFrom, transform2, defaultShareKeyframes);
    }

    public ModelInstance(ModelInstance copyFrom, Matrix4 transform2, boolean shareKeyframes) {
        this.materials = new Array<>();
        this.nodes = new Array<>();
        this.animations = new Array<>();
        this.model = copyFrom.model;
        this.transform = transform2 == null ? new Matrix4() : transform2;
        copyNodes(copyFrom.nodes);
        copyAnimations(copyFrom.animations, shareKeyframes);
        calculateTransforms();
    }

    public ModelInstance copy() {
        return new ModelInstance(this);
    }

    private void copyNodes(Array<Node> nodes2) {
        int n = nodes2.size;
        for (int i = 0; i < n; i++) {
            this.nodes.add(nodes2.get(i).copy());
        }
        invalidate();
    }

    private void copyNodes(Array<Node> nodes2, String... nodeIds) {
        int n = nodes2.size;
        for (int i = 0; i < n; i++) {
            Node node = nodes2.get(i);
            int length = nodeIds.length;
            int i2 = 0;
            while (true) {
                if (i2 >= length) {
                    break;
                } else if (nodeIds[i2].equals(node.id)) {
                    this.nodes.add(node.copy());
                    break;
                } else {
                    i2++;
                }
            }
        }
        invalidate();
    }

    private void copyNodes(Array<Node> nodes2, Array<String> nodeIds) {
        int n = nodes2.size;
        for (int i = 0; i < n; i++) {
            Node node = nodes2.get(i);
            Iterator<String> it = nodeIds.iterator();
            while (true) {
                if (it.hasNext()) {
                    if (it.next().equals(node.id)) {
                        this.nodes.add(node.copy());
                        break;
                    }
                } else {
                    break;
                }
            }
        }
        invalidate();
    }

    private void invalidate(Node node) {
        int n = node.parts.size;
        for (int i = 0; i < n; i++) {
            NodePart part = node.parts.get(i);
            ArrayMap<Node, Matrix4> bindPose = part.invBoneBindTransforms;
            if (bindPose != null) {
                for (int j = 0; j < bindPose.size; j++) {
                    ((Node[]) bindPose.keys)[j] = getNode(((Node[]) bindPose.keys)[j].id);
                }
            }
            if (!this.materials.contains(part.material, true)) {
                int midx = this.materials.indexOf(part.material, false);
                if (midx < 0) {
                    Array<Material> array = this.materials;
                    Material copy = part.material.copy();
                    part.material = copy;
                    array.add(copy);
                } else {
                    part.material = this.materials.get(midx);
                }
            }
        }
        int n2 = node.getChildCount();
        for (int i2 = 0; i2 < n2; i2++) {
            invalidate(node.getChild(i2));
        }
    }

    private void invalidate() {
        int n = this.nodes.size;
        for (int i = 0; i < n; i++) {
            invalidate(this.nodes.get(i));
        }
    }

    public void copyAnimations(Iterable<Animation> source) {
        for (Animation anim : source) {
            copyAnimation(anim, defaultShareKeyframes);
        }
    }

    public void copyAnimations(Iterable<Animation> source, boolean shareKeyframes) {
        for (Animation anim : source) {
            copyAnimation(anim, shareKeyframes);
        }
    }

    public void copyAnimation(Animation sourceAnim) {
        copyAnimation(sourceAnim, defaultShareKeyframes);
    }

    public void copyAnimation(Animation sourceAnim, boolean shareKeyframes) {
        Animation animation = new Animation();
        animation.id = sourceAnim.id;
        animation.duration = sourceAnim.duration;
        Iterator<NodeAnimation> it = sourceAnim.nodeAnimations.iterator();
        while (it.hasNext()) {
            NodeAnimation nanim = it.next();
            Node node = getNode(nanim.node.id);
            if (node != null) {
                NodeAnimation nodeAnim = new NodeAnimation();
                nodeAnim.node = node;
                if (shareKeyframes) {
                    nodeAnim.translation = nanim.translation;
                    nodeAnim.rotation = nanim.rotation;
                    nodeAnim.scaling = nanim.scaling;
                } else {
                    if (nanim.translation != null) {
                        nodeAnim.translation = new Array<>();
                        Iterator<NodeKeyframe<Vector3>> it2 = nanim.translation.iterator();
                        while (it2.hasNext()) {
                            NodeKeyframe<Vector3> kf = it2.next();
                            nodeAnim.translation.add(new NodeKeyframe(kf.keytime, kf.value));
                        }
                    }
                    if (nanim.rotation != null) {
                        nodeAnim.rotation = new Array<>();
                        Iterator<NodeKeyframe<Quaternion>> it3 = nanim.rotation.iterator();
                        while (it3.hasNext()) {
                            NodeKeyframe<Quaternion> kf2 = it3.next();
                            nodeAnim.rotation.add(new NodeKeyframe(kf2.keytime, kf2.value));
                        }
                    }
                    if (nanim.scaling != null) {
                        nodeAnim.scaling = new Array<>();
                        Iterator<NodeKeyframe<Vector3>> it4 = nanim.scaling.iterator();
                        while (it4.hasNext()) {
                            NodeKeyframe<Vector3> kf3 = it4.next();
                            nodeAnim.scaling.add(new NodeKeyframe(kf3.keytime, kf3.value));
                        }
                    }
                }
                if (nodeAnim.translation != null || nodeAnim.rotation != null || nodeAnim.scaling != null) {
                    animation.nodeAnimations.add(nodeAnim);
                }
            }
        }
        if (animation.nodeAnimations.size > 0) {
            this.animations.add(animation);
        }
    }

    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
        Iterator<Node> it = this.nodes.iterator();
        while (it.hasNext()) {
            getRenderables(it.next(), renderables, pool);
        }
    }

    public Renderable getRenderable(Renderable out) {
        return getRenderable(out, this.nodes.get(0));
    }

    public Renderable getRenderable(Renderable out, Node node) {
        return getRenderable(out, node, node.parts.get(0));
    }

    public Renderable getRenderable(Renderable out, Node node, NodePart nodePart) {
        nodePart.setRenderable(out);
        if (nodePart.bones == null && this.transform != null) {
            out.worldTransform.set(this.transform).mul(node.globalTransform);
        } else if (this.transform != null) {
            out.worldTransform.set(this.transform);
        } else {
            out.worldTransform.idt();
        }
        out.userData = this.userData;
        return out;
    }

    /* access modifiers changed from: protected */
    public void getRenderables(Node node, Array<Renderable> renderables, Pool<Renderable> pool) {
        if (node.parts.size > 0) {
            Iterator<NodePart> it = node.parts.iterator();
            while (it.hasNext()) {
                NodePart nodePart = it.next();
                if (nodePart.enabled) {
                    renderables.add(getRenderable(pool.obtain(), node, nodePart));
                }
            }
        }
        for (Node child : node.getChildren()) {
            getRenderables(child, renderables, pool);
        }
    }

    public void calculateTransforms() {
        int n = this.nodes.size;
        for (int i = 0; i < n; i++) {
            this.nodes.get(i).calculateTransforms(true);
        }
        for (int i2 = 0; i2 < n; i2++) {
            this.nodes.get(i2).calculateBoneTransforms(true);
        }
    }

    public BoundingBox calculateBoundingBox(BoundingBox out) {
        out.inf();
        return extendBoundingBox(out);
    }

    public BoundingBox extendBoundingBox(BoundingBox out) {
        int n = this.nodes.size;
        for (int i = 0; i < n; i++) {
            this.nodes.get(i).extendBoundingBox(out);
        }
        return out;
    }

    public Animation getAnimation(String id) {
        return getAnimation(id, false);
    }

    public Animation getAnimation(String id, boolean ignoreCase) {
        int n = this.animations.size;
        if (ignoreCase) {
            for (int i = 0; i < n; i++) {
                Animation animation = this.animations.get(i);
                Animation animation2 = animation;
                if (animation.id.equalsIgnoreCase(id)) {
                    return animation2;
                }
            }
            return null;
        }
        for (int i2 = 0; i2 < n; i2++) {
            Animation animation3 = this.animations.get(i2);
            Animation animation4 = animation3;
            if (animation3.id.equals(id)) {
                return animation4;
            }
        }
        return null;
    }

    public Material getMaterial(String id) {
        return getMaterial(id, true);
    }

    public Material getMaterial(String id, boolean ignoreCase) {
        int n = this.materials.size;
        if (ignoreCase) {
            for (int i = 0; i < n; i++) {
                Material material = this.materials.get(i);
                Material material2 = material;
                if (material.id.equalsIgnoreCase(id)) {
                    return material2;
                }
            }
            return null;
        }
        for (int i2 = 0; i2 < n; i2++) {
            Material material3 = this.materials.get(i2);
            Material material4 = material3;
            if (material3.id.equals(id)) {
                return material4;
            }
        }
        return null;
    }

    public Node getNode(String id) {
        return getNode(id, true);
    }

    public Node getNode(String id, boolean recursive) {
        return getNode(id, recursive, false);
    }

    public Node getNode(String id, boolean recursive, boolean ignoreCase) {
        return Node.getNode(this.nodes, id, recursive, ignoreCase);
    }
}
