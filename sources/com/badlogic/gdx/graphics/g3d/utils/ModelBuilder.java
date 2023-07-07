package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.util.Iterator;

public class ModelBuilder {
    private Array<MeshBuilder> builders = new Array<>();
    private Model model;
    private Node node;
    private Matrix4 tmpTransform = new Matrix4();

    private MeshBuilder getBuilder(VertexAttributes attributes) {
        Iterator<MeshBuilder> it = this.builders.iterator();
        while (it.hasNext()) {
            MeshBuilder mb = it.next();
            if (mb.getAttributes().equals(attributes) && mb.lastIndex() < 16383) {
                return mb;
            }
        }
        MeshBuilder result = new MeshBuilder();
        result.begin(attributes);
        this.builders.add(result);
        return result;
    }

    public void begin() {
        if (this.model == null) {
            this.node = null;
            this.model = new Model();
            this.builders.clear();
            return;
        }
        throw new GdxRuntimeException("Call end() first");
    }

    public Model end() {
        if (this.model != null) {
            Model result = this.model;
            endnode();
            this.model = null;
            Iterator<MeshBuilder> it = this.builders.iterator();
            while (it.hasNext()) {
                it.next().end();
            }
            this.builders.clear();
            rebuildReferences(result);
            return result;
        }
        throw new GdxRuntimeException("Call begin() first");
    }

    private void endnode() {
        if (this.node != null) {
            this.node = null;
        }
    }

    /* access modifiers changed from: protected */
    public Node node(Node node2) {
        if (this.model != null) {
            endnode();
            this.model.nodes.add(node2);
            this.node = node2;
            return node2;
        }
        throw new GdxRuntimeException("Call begin() first");
    }

    public Node node() {
        Node node2 = new Node();
        node(node2);
        node2.id = "node" + this.model.nodes.size;
        return node2;
    }

    public Node node(String id, Model model2) {
        Node node2 = new Node();
        node2.id = id;
        node2.addChildren(model2.nodes);
        node(node2);
        for (Disposable disposable : model2.getManagedDisposables()) {
            manage(disposable);
        }
        return node2;
    }

    public void manage(Disposable disposable) {
        Model model2 = this.model;
        if (model2 != null) {
            model2.manageDisposable(disposable);
            return;
        }
        throw new GdxRuntimeException("Call begin() first");
    }

    public void part(MeshPart meshpart, Material material) {
        if (this.node == null) {
            node();
        }
        this.node.parts.add(new NodePart(meshpart, material));
    }

    public MeshPart part(String id, Mesh mesh, int primitiveType, int offset, int size, Material material) {
        MeshPart meshPart = new MeshPart();
        meshPart.id = id;
        meshPart.primitiveType = primitiveType;
        meshPart.mesh = mesh;
        meshPart.offset = offset;
        meshPart.size = size;
        part(meshPart, material);
        return meshPart;
    }

    public MeshPart part(String id, Mesh mesh, int primitiveType, Material material) {
        return part(id, mesh, primitiveType, 0, mesh.getNumIndices(), material);
    }

    public MeshPartBuilder part(String id, int primitiveType, VertexAttributes attributes, Material material) {
        MeshBuilder builder = getBuilder(attributes);
        part(builder.part(id, primitiveType), material);
        return builder;
    }

    public MeshPartBuilder part(String id, int primitiveType, long attributes, Material material) {
        return part(id, primitiveType, MeshBuilder.createAttributes(attributes), material);
    }

    public Model createBox(float width, float height, float depth, Material material, long attributes) {
        return createBox(width, height, depth, 4, material, attributes);
    }

    public Model createBox(float width, float height, float depth, int primitiveType, Material material, long attributes) {
        begin();
        part("box", primitiveType, attributes, material).box(width, height, depth);
        return end();
    }

    public Model createRect(float x00, float y00, float z00, float x10, float y10, float z10, float x11, float y11, float z11, float x01, float y01, float z01, float normalX, float normalY, float normalZ, Material material, long attributes) {
        return createRect(x00, y00, z00, x10, y10, z10, x11, y11, z11, x01, y01, z01, normalX, normalY, normalZ, 4, material, attributes);
    }

    public Model createRect(float x00, float y00, float z00, float x10, float y10, float z10, float x11, float y11, float z11, float x01, float y01, float z01, float normalX, float normalY, float normalZ, int primitiveType, Material material, long attributes) {
        begin();
        part("rect", primitiveType, attributes, material).rect(x00, y00, z00, x10, y10, z10, x11, y11, z11, x01, y01, z01, normalX, normalY, normalZ);
        return end();
    }

    public Model createCylinder(float width, float height, float depth, int divisions, Material material, long attributes) {
        return createCylinder(width, height, depth, divisions, 4, material, attributes);
    }

    public Model createCylinder(float width, float height, float depth, int divisions, int primitiveType, Material material, long attributes) {
        return createCylinder(width, height, depth, divisions, primitiveType, material, attributes, 0.0f, 360.0f);
    }

    public Model createCylinder(float width, float height, float depth, int divisions, Material material, long attributes, float angleFrom, float angleTo) {
        return createCylinder(width, height, depth, divisions, 4, material, attributes, angleFrom, angleTo);
    }

    public Model createCylinder(float width, float height, float depth, int divisions, int primitiveType, Material material, long attributes, float angleFrom, float angleTo) {
        begin();
        part("cylinder", primitiveType, attributes, material).cylinder(width, height, depth, divisions, angleFrom, angleTo);
        return end();
    }

    public Model createCone(float width, float height, float depth, int divisions, Material material, long attributes) {
        return createCone(width, height, depth, divisions, 4, material, attributes);
    }

    public Model createCone(float width, float height, float depth, int divisions, int primitiveType, Material material, long attributes) {
        return createCone(width, height, depth, divisions, primitiveType, material, attributes, 0.0f, 360.0f);
    }

    public Model createCone(float width, float height, float depth, int divisions, Material material, long attributes, float angleFrom, float angleTo) {
        return createCone(width, height, depth, divisions, 4, material, attributes, angleFrom, angleTo);
    }

    public Model createCone(float width, float height, float depth, int divisions, int primitiveType, Material material, long attributes, float angleFrom, float angleTo) {
        begin();
        part("cone", primitiveType, attributes, material).cone(width, height, depth, divisions, angleFrom, angleTo);
        return end();
    }

    public Model createSphere(float width, float height, float depth, int divisionsU, int divisionsV, Material material, long attributes) {
        return createSphere(width, height, depth, divisionsU, divisionsV, 4, material, attributes);
    }

    public Model createSphere(float width, float height, float depth, int divisionsU, int divisionsV, int primitiveType, Material material, long attributes) {
        return createSphere(width, height, depth, divisionsU, divisionsV, primitiveType, material, attributes, 0.0f, 360.0f, 0.0f, 180.0f);
    }

    public Model createSphere(float width, float height, float depth, int divisionsU, int divisionsV, Material material, long attributes, float angleUFrom, float angleUTo, float angleVFrom, float angleVTo) {
        return createSphere(width, height, depth, divisionsU, divisionsV, 4, material, attributes, angleUFrom, angleUTo, angleVFrom, angleVTo);
    }

    public Model createSphere(float width, float height, float depth, int divisionsU, int divisionsV, int primitiveType, Material material, long attributes, float angleUFrom, float angleUTo, float angleVFrom, float angleVTo) {
        begin();
        part("cylinder", primitiveType, attributes, material).sphere(width, height, depth, divisionsU, divisionsV, angleUFrom, angleUTo, angleVFrom, angleVTo);
        return end();
    }

    public Model createCapsule(float radius, float height, int divisions, Material material, long attributes) {
        return createCapsule(radius, height, divisions, 4, material, attributes);
    }

    public Model createCapsule(float radius, float height, int divisions, int primitiveType, Material material, long attributes) {
        begin();
        part("capsule", primitiveType, attributes, material).capsule(radius, height, divisions);
        return end();
    }

    public static void rebuildReferences(Model model2) {
        model2.materials.clear();
        model2.meshes.clear();
        model2.meshParts.clear();
        Iterator<Node> it = model2.nodes.iterator();
        while (it.hasNext()) {
            rebuildReferences(model2, it.next());
        }
    }

    private static void rebuildReferences(Model model2, Node node2) {
        Iterator<NodePart> it = node2.parts.iterator();
        while (it.hasNext()) {
            NodePart mpm = it.next();
            if (!model2.materials.contains(mpm.material, true)) {
                model2.materials.add(mpm.material);
            }
            if (!model2.meshParts.contains(mpm.meshPart, true)) {
                model2.meshParts.add(mpm.meshPart);
                if (!model2.meshes.contains(mpm.meshPart.mesh, true)) {
                    model2.meshes.add(mpm.meshPart.mesh);
                }
                model2.manageDisposable(mpm.meshPart.mesh);
            }
        }
        for (Node child : node2.getChildren()) {
            rebuildReferences(model2, child);
        }
    }

    public Model createXYZCoordinates(float axisLength, float capLength, float stemThickness, int divisions, int primitiveType, Material material, long attributes) {
        begin();
        Node node2 = node();
        MeshPartBuilder partBuilder = part("xyz", primitiveType, attributes, material);
        partBuilder.setColor(Color.RED);
        MeshPartBuilder meshPartBuilder = partBuilder;
        float f = capLength;
        float f2 = stemThickness;
        int i = divisions;
        meshPartBuilder.arrow(0.0f, 0.0f, 0.0f, axisLength, 0.0f, 0.0f, f, f2, i);
        partBuilder.setColor(Color.GREEN);
        meshPartBuilder.arrow(0.0f, 0.0f, 0.0f, 0.0f, axisLength, 0.0f, f, f2, i);
        partBuilder.setColor(Color.BLUE);
        meshPartBuilder.arrow(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, axisLength, f, f2, i);
        return end();
    }

    public Model createXYZCoordinates(float axisLength, Material material, long attributes) {
        return createXYZCoordinates(axisLength, 0.1f, 0.1f, 5, 4, material, attributes);
    }

    public Model createArrow(float x1, float y1, float z1, float x2, float y2, float z2, float capLength, float stemThickness, int divisions, int primitiveType, Material material, long attributes) {
        begin();
        part("arrow", primitiveType, attributes, material).arrow(x1, y1, z1, x2, y2, z2, capLength, stemThickness, divisions);
        return end();
    }

    public Model createArrow(Vector3 from, Vector3 to, Material material, long attributes) {
        Vector3 vector3 = from;
        Vector3 vector32 = to;
        return createArrow(vector3.x, vector3.y, vector3.z, vector32.x, vector32.y, vector32.z, 0.1f, 0.1f, 5, 4, material, attributes);
    }

    public Model createLineGrid(int xDivisions, int zDivisions, float xSize, float zSize, Material material, long attributes) {
        int i = xDivisions;
        int i2 = zDivisions;
        begin();
        MeshPartBuilder partBuilder = part("lines", 1, attributes, material);
        float hxlength = (((float) i) * xSize) / 2.0f;
        float hzlength = (((float) i2) * zSize) / 2.0f;
        float z1 = hzlength;
        float z2 = -hzlength;
        float x1 = -hxlength;
        float x2 = -hxlength;
        int i3 = 0;
        while (i3 <= i) {
            float f = z2;
            partBuilder.line(x1, 0.0f, z1, x2, 0.0f, z2);
            x1 += xSize;
            x2 += xSize;
            i3++;
        }
        int i4 = i3;
        float f2 = z2;
        float x12 = -hxlength;
        float x22 = hxlength;
        float z12 = -hzlength;
        float z22 = -hzlength;
        int j = 0;
        while (j <= i2) {
            partBuilder.line(x12, 0.0f, z12, x22, 0.0f, z22);
            z12 += zSize;
            z22 += zSize;
            j++;
            x12 = x12;
        }
        int i5 = j;
        float f3 = x12;
        return end();
    }
}
