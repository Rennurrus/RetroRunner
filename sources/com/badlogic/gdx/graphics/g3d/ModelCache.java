package com.badlogic.gdx.graphics.g3d;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.RenderableSorter;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.FlushablePool;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pool;
import java.util.Comparator;
import java.util.Iterator;

public class ModelCache implements Disposable, RenderableProvider {
    private boolean building;
    private Camera camera;
    private Array<Renderable> items;
    private MeshBuilder meshBuilder;
    private FlushablePool<MeshPart> meshPartPool;
    private MeshPool meshPool;
    private Array<Renderable> renderables;
    private FlushablePool<Renderable> renderablesPool;
    private RenderableSorter sorter;
    private Array<Renderable> tmp;

    public interface MeshPool extends Disposable {
        void flush();

        Mesh obtain(VertexAttributes vertexAttributes, int i, int i2);
    }

    public static class SimpleMeshPool implements MeshPool {
        private Array<Mesh> freeMeshes = new Array<>();
        private Array<Mesh> usedMeshes = new Array<>();

        public void flush() {
            this.freeMeshes.addAll(this.usedMeshes);
            this.usedMeshes.clear();
        }

        public Mesh obtain(VertexAttributes vertexAttributes, int vertexCount, int indexCount) {
            int i = 0;
            int n = this.freeMeshes.size;
            while (i < n) {
                Mesh mesh = this.freeMeshes.get(i);
                if (!mesh.getVertexAttributes().equals(vertexAttributes) || mesh.getMaxVertices() < vertexCount || mesh.getMaxIndices() < indexCount) {
                    i++;
                } else {
                    this.freeMeshes.removeIndex(i);
                    this.usedMeshes.add(mesh);
                    return mesh;
                }
            }
            Mesh result = new Mesh(false, (int) GL20.GL_COVERAGE_BUFFER_BIT_NV, Math.max(GL20.GL_COVERAGE_BUFFER_BIT_NV, 1 << (32 - Integer.numberOfLeadingZeros(indexCount - 1))), vertexAttributes);
            this.usedMeshes.add(result);
            return result;
        }

        public void dispose() {
            Iterator<Mesh> it = this.usedMeshes.iterator();
            while (it.hasNext()) {
                it.next().dispose();
            }
            this.usedMeshes.clear();
            Iterator<Mesh> it2 = this.freeMeshes.iterator();
            while (it2.hasNext()) {
                it2.next().dispose();
            }
            this.freeMeshes.clear();
        }
    }

    public static class TightMeshPool implements MeshPool {
        private Array<Mesh> freeMeshes = new Array<>();
        private Array<Mesh> usedMeshes = new Array<>();

        public void flush() {
            this.freeMeshes.addAll(this.usedMeshes);
            this.usedMeshes.clear();
        }

        public Mesh obtain(VertexAttributes vertexAttributes, int vertexCount, int indexCount) {
            int n = this.freeMeshes.size;
            for (int i = 0; i < n; i++) {
                Mesh mesh = this.freeMeshes.get(i);
                if (mesh.getVertexAttributes().equals(vertexAttributes) && mesh.getMaxVertices() == vertexCount && mesh.getMaxIndices() == indexCount) {
                    this.freeMeshes.removeIndex(i);
                    this.usedMeshes.add(mesh);
                    return mesh;
                }
            }
            Mesh result = new Mesh(true, vertexCount, indexCount, vertexAttributes);
            this.usedMeshes.add(result);
            return result;
        }

        public void dispose() {
            Iterator<Mesh> it = this.usedMeshes.iterator();
            while (it.hasNext()) {
                it.next().dispose();
            }
            this.usedMeshes.clear();
            Iterator<Mesh> it2 = this.freeMeshes.iterator();
            while (it2.hasNext()) {
                it2.next().dispose();
            }
            this.freeMeshes.clear();
        }
    }

    public static class Sorter implements RenderableSorter, Comparator<Renderable> {
        public void sort(Camera camera, Array<Renderable> renderables) {
            renderables.sort(this);
        }

        public int compare(Renderable arg0, Renderable arg1) {
            int vc = arg0.meshPart.mesh.getVertexAttributes().compareTo(arg1.meshPart.mesh.getVertexAttributes());
            if (vc != 0) {
                return vc;
            }
            int mc = arg0.material.compareTo((Attributes) arg1.material);
            if (mc == 0) {
                return arg0.meshPart.primitiveType - arg1.meshPart.primitiveType;
            }
            return mc;
        }
    }

    public ModelCache() {
        this(new Sorter(), new SimpleMeshPool());
    }

    public ModelCache(RenderableSorter sorter2, MeshPool meshPool2) {
        this.renderables = new Array<>();
        this.renderablesPool = new FlushablePool<Renderable>() {
            /* access modifiers changed from: protected */
            public Renderable newObject() {
                return new Renderable();
            }
        };
        this.meshPartPool = new FlushablePool<MeshPart>() {
            /* access modifiers changed from: protected */
            public MeshPart newObject() {
                return new MeshPart();
            }
        };
        this.items = new Array<>();
        this.tmp = new Array<>();
        this.sorter = sorter2;
        this.meshPool = meshPool2;
        this.meshBuilder = new MeshBuilder();
    }

    public void begin() {
        begin((Camera) null);
    }

    public void begin(Camera camera2) {
        if (!this.building) {
            this.building = true;
            this.camera = camera2;
            this.renderablesPool.flush();
            this.renderables.clear();
            this.items.clear();
            this.meshPartPool.flush();
            this.meshPool.flush();
            return;
        }
        throw new GdxRuntimeException("Call end() after calling begin()");
    }

    private Renderable obtainRenderable(Material material, int primitiveType) {
        Renderable result = this.renderablesPool.obtain();
        result.bones = null;
        result.environment = null;
        result.material = material;
        result.meshPart.mesh = null;
        result.meshPart.offset = 0;
        result.meshPart.size = 0;
        result.meshPart.primitiveType = primitiveType;
        result.meshPart.center.set(0.0f, 0.0f, 0.0f);
        result.meshPart.halfExtents.set(0.0f, 0.0f, 0.0f);
        result.meshPart.radius = -1.0f;
        result.shader = null;
        result.userData = null;
        result.worldTransform.idt();
        return result;
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x00a6  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0122  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void end() {
        /*
            r23 = this;
            r0 = r23
            boolean r1 = r0.building
            if (r1 == 0) goto L_0x019c
            r1 = 0
            r0.building = r1
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.graphics.g3d.Renderable> r2 = r0.items
            int r2 = r2.size
            if (r2 != 0) goto L_0x0010
            return
        L_0x0010:
            com.badlogic.gdx.graphics.g3d.utils.RenderableSorter r2 = r0.sorter
            com.badlogic.gdx.graphics.Camera r3 = r0.camera
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.graphics.g3d.Renderable> r4 = r0.items
            r2.sort(r3, r4)
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.graphics.g3d.Renderable> r2 = r0.items
            int r2 = r2.size
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.graphics.g3d.Renderable> r3 = r0.renderables
            int r3 = r3.size
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.graphics.g3d.Renderable> r4 = r0.items
            java.lang.Object r4 = r4.get(r1)
            com.badlogic.gdx.graphics.g3d.Renderable r4 = (com.badlogic.gdx.graphics.g3d.Renderable) r4
            com.badlogic.gdx.graphics.g3d.model.MeshPart r5 = r4.meshPart
            com.badlogic.gdx.graphics.Mesh r5 = r5.mesh
            com.badlogic.gdx.graphics.VertexAttributes r5 = r5.getVertexAttributes()
            com.badlogic.gdx.graphics.g3d.Material r6 = r4.material
            com.badlogic.gdx.graphics.g3d.model.MeshPart r7 = r4.meshPart
            int r7 = r7.primitiveType
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.graphics.g3d.Renderable> r8 = r0.renderables
            int r8 = r8.size
            com.badlogic.gdx.graphics.g3d.utils.MeshBuilder r9 = r0.meshBuilder
            r9.begin((com.badlogic.gdx.graphics.VertexAttributes) r5)
            com.badlogic.gdx.graphics.g3d.utils.MeshBuilder r9 = r0.meshBuilder
            com.badlogic.gdx.utils.FlushablePool<com.badlogic.gdx.graphics.g3d.model.MeshPart> r10 = r0.meshPartPool
            java.lang.Object r10 = r10.obtain()
            com.badlogic.gdx.graphics.g3d.model.MeshPart r10 = (com.badlogic.gdx.graphics.g3d.model.MeshPart) r10
            java.lang.String r11 = ""
            com.badlogic.gdx.graphics.g3d.model.MeshPart r9 = r9.part(r11, r7, r10)
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.graphics.g3d.Renderable> r10 = r0.renderables
            com.badlogic.gdx.graphics.g3d.Renderable r12 = r0.obtainRenderable(r6, r7)
            r10.add(r12)
            r10 = 0
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.graphics.g3d.Renderable> r12 = r0.items
            int r12 = r12.size
        L_0x005e:
            if (r10 >= r12) goto L_0x014f
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.graphics.g3d.Renderable> r14 = r0.items
            java.lang.Object r14 = r14.get(r10)
            com.badlogic.gdx.graphics.g3d.Renderable r14 = (com.badlogic.gdx.graphics.g3d.Renderable) r14
            com.badlogic.gdx.graphics.g3d.model.MeshPart r15 = r14.meshPart
            com.badlogic.gdx.graphics.Mesh r15 = r15.mesh
            com.badlogic.gdx.graphics.VertexAttributes r15 = r15.getVertexAttributes()
            com.badlogic.gdx.graphics.g3d.Material r1 = r14.material
            com.badlogic.gdx.graphics.g3d.model.MeshPart r13 = r14.meshPart
            int r13 = r13.primitiveType
            boolean r17 = r15.equals(r5)
            if (r17 == 0) goto L_0x0091
            r17 = r2
            com.badlogic.gdx.graphics.g3d.model.MeshPart r2 = r14.meshPart
            int r2 = r2.size
            r18 = r3
            com.badlogic.gdx.graphics.g3d.utils.MeshBuilder r3 = r0.meshBuilder
            int r3 = r3.getNumVertices()
            int r2 = r2 + r3
            r3 = 32767(0x7fff, float:4.5916E-41)
            if (r2 >= r3) goto L_0x0095
            r2 = 1
            goto L_0x0096
        L_0x0091:
            r17 = r2
            r18 = r3
        L_0x0095:
            r2 = 0
        L_0x0096:
            if (r2 == 0) goto L_0x00a3
            if (r13 != r7) goto L_0x00a3
            r3 = 1
            boolean r19 = r1.same(r6, r3)
            if (r19 == 0) goto L_0x00a3
            r3 = 1
            goto L_0x00a4
        L_0x00a3:
            r3 = 0
        L_0x00a4:
            if (r3 != 0) goto L_0x0122
            if (r2 != 0) goto L_0x00e3
            r19 = r2
            com.badlogic.gdx.graphics.g3d.utils.MeshBuilder r2 = r0.meshBuilder
            r20 = r3
            com.badlogic.gdx.graphics.g3d.ModelCache$MeshPool r3 = r0.meshPool
            r21 = r4
            int r4 = r2.getNumVertices()
            r22 = r6
            com.badlogic.gdx.graphics.g3d.utils.MeshBuilder r6 = r0.meshBuilder
            int r6 = r6.getNumIndices()
            com.badlogic.gdx.graphics.Mesh r3 = r3.obtain(r5, r4, r6)
            com.badlogic.gdx.graphics.Mesh r2 = r2.end(r3)
        L_0x00c6:
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.graphics.g3d.Renderable> r3 = r0.renderables
            int r3 = r3.size
            if (r8 >= r3) goto L_0x00dc
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.graphics.g3d.Renderable> r3 = r0.renderables
            int r4 = r8 + 1
            java.lang.Object r3 = r3.get(r8)
            com.badlogic.gdx.graphics.g3d.Renderable r3 = (com.badlogic.gdx.graphics.g3d.Renderable) r3
            com.badlogic.gdx.graphics.g3d.model.MeshPart r3 = r3.meshPart
            r3.mesh = r2
            r8 = r4
            goto L_0x00c6
        L_0x00dc:
            com.badlogic.gdx.graphics.g3d.utils.MeshBuilder r3 = r0.meshBuilder
            r5 = r15
            r3.begin((com.badlogic.gdx.graphics.VertexAttributes) r15)
            goto L_0x00eb
        L_0x00e3:
            r19 = r2
            r20 = r3
            r21 = r4
            r22 = r6
        L_0x00eb:
            com.badlogic.gdx.graphics.g3d.utils.MeshBuilder r2 = r0.meshBuilder
            com.badlogic.gdx.utils.FlushablePool<com.badlogic.gdx.graphics.g3d.model.MeshPart> r3 = r0.meshPartPool
            java.lang.Object r3 = r3.obtain()
            com.badlogic.gdx.graphics.g3d.model.MeshPart r3 = (com.badlogic.gdx.graphics.g3d.model.MeshPart) r3
            com.badlogic.gdx.graphics.g3d.model.MeshPart r2 = r2.part(r11, r13, r3)
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.graphics.g3d.Renderable> r3 = r0.renderables
            int r4 = r3.size
            r6 = 1
            int r4 = r4 - r6
            java.lang.Object r3 = r3.get(r4)
            com.badlogic.gdx.graphics.g3d.Renderable r3 = (com.badlogic.gdx.graphics.g3d.Renderable) r3
            com.badlogic.gdx.graphics.g3d.model.MeshPart r4 = r3.meshPart
            int r6 = r9.offset
            r4.offset = r6
            com.badlogic.gdx.graphics.g3d.model.MeshPart r4 = r3.meshPart
            int r6 = r9.size
            r4.size = r6
            r4 = r2
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.graphics.g3d.Renderable> r6 = r0.renderables
            r9 = r1
            r7 = r13
            r16 = r2
            com.badlogic.gdx.graphics.g3d.Renderable r2 = r0.obtainRenderable(r1, r13)
            r6.add(r2)
            r6 = r9
            r9 = r4
            goto L_0x012a
        L_0x0122:
            r19 = r2
            r20 = r3
            r21 = r4
            r22 = r6
        L_0x012a:
            com.badlogic.gdx.graphics.g3d.utils.MeshBuilder r2 = r0.meshBuilder
            com.badlogic.gdx.math.Matrix4 r3 = r14.worldTransform
            r2.setVertexTransform(r3)
            com.badlogic.gdx.graphics.g3d.utils.MeshBuilder r2 = r0.meshBuilder
            com.badlogic.gdx.graphics.g3d.model.MeshPart r3 = r14.meshPart
            com.badlogic.gdx.graphics.Mesh r3 = r3.mesh
            com.badlogic.gdx.graphics.g3d.model.MeshPart r4 = r14.meshPart
            int r4 = r4.offset
            r16 = r1
            com.badlogic.gdx.graphics.g3d.model.MeshPart r1 = r14.meshPart
            int r1 = r1.size
            r2.addMesh(r3, r4, r1)
            int r10 = r10 + 1
            r2 = r17
            r3 = r18
            r4 = r21
            r1 = 0
            goto L_0x005e
        L_0x014f:
            r17 = r2
            r18 = r3
            r21 = r4
            r22 = r6
            com.badlogic.gdx.graphics.g3d.utils.MeshBuilder r1 = r0.meshBuilder
            com.badlogic.gdx.graphics.g3d.ModelCache$MeshPool r2 = r0.meshPool
            int r3 = r1.getNumVertices()
            com.badlogic.gdx.graphics.g3d.utils.MeshBuilder r4 = r0.meshBuilder
            int r4 = r4.getNumIndices()
            com.badlogic.gdx.graphics.Mesh r2 = r2.obtain(r5, r3, r4)
            com.badlogic.gdx.graphics.Mesh r1 = r1.end(r2)
        L_0x016d:
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.graphics.g3d.Renderable> r2 = r0.renderables
            int r2 = r2.size
            if (r8 >= r2) goto L_0x0183
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.graphics.g3d.Renderable> r2 = r0.renderables
            int r3 = r8 + 1
            java.lang.Object r2 = r2.get(r8)
            com.badlogic.gdx.graphics.g3d.Renderable r2 = (com.badlogic.gdx.graphics.g3d.Renderable) r2
            com.badlogic.gdx.graphics.g3d.model.MeshPart r2 = r2.meshPart
            r2.mesh = r1
            r8 = r3
            goto L_0x016d
        L_0x0183:
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.graphics.g3d.Renderable> r2 = r0.renderables
            int r3 = r2.size
            r4 = 1
            int r3 = r3 - r4
            java.lang.Object r2 = r2.get(r3)
            com.badlogic.gdx.graphics.g3d.Renderable r2 = (com.badlogic.gdx.graphics.g3d.Renderable) r2
            com.badlogic.gdx.graphics.g3d.model.MeshPart r3 = r2.meshPart
            int r4 = r9.offset
            r3.offset = r4
            com.badlogic.gdx.graphics.g3d.model.MeshPart r3 = r2.meshPart
            int r4 = r9.size
            r3.size = r4
            return
        L_0x019c:
            com.badlogic.gdx.utils.GdxRuntimeException r1 = new com.badlogic.gdx.utils.GdxRuntimeException
            java.lang.String r2 = "Call begin() prior to calling end()"
            r1.<init>((java.lang.String) r2)
            goto L_0x01a5
        L_0x01a4:
            throw r1
        L_0x01a5:
            goto L_0x01a4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.graphics.g3d.ModelCache.end():void");
    }

    public void add(Renderable renderable) {
        if (!this.building) {
            throw new GdxRuntimeException("Can only add items to the ModelCache in between .begin() and .end()");
        } else if (renderable.bones == null) {
            this.items.add(renderable);
        } else {
            this.renderables.add(renderable);
        }
    }

    public void add(RenderableProvider renderableProvider) {
        renderableProvider.getRenderables(this.tmp, this.renderablesPool);
        int n = this.tmp.size;
        for (int i = 0; i < n; i++) {
            add(this.tmp.get(i));
        }
        this.tmp.clear();
    }

    public <T extends RenderableProvider> void add(Iterable<T> renderableProviders) {
        for (T renderableProvider : renderableProviders) {
            add((RenderableProvider) renderableProvider);
        }
    }

    public void getRenderables(Array<Renderable> renderables2, Pool<Renderable> pool) {
        if (!this.building) {
            Iterator<Renderable> it = this.renderables.iterator();
            while (it.hasNext()) {
                Renderable r = it.next();
                r.shader = null;
                r.environment = null;
            }
            renderables2.addAll((Array<? extends Renderable>) this.renderables);
            return;
        }
        throw new GdxRuntimeException("Cannot render a ModelCache in between .begin() and .end()");
    }

    public void dispose() {
        if (!this.building) {
            this.meshPool.dispose();
            return;
        }
        throw new GdxRuntimeException("Cannot dispose a ModelCache in between .begin() and .end()");
    }
}
