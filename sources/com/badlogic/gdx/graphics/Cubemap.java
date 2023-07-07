package com.badlogic.gdx.graphics;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.CubemapLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.glutils.FacedCubemapData;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Cubemap extends GLTexture {
    private static AssetManager assetManager;
    static final Map<Application, Array<Cubemap>> managedCubemaps = new HashMap();
    protected CubemapData data;

    public enum CubemapSide {
        PositiveX(0, GL20.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f, 0.0f),
        NegativeX(1, GL20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0.0f, -1.0f, 0.0f, -1.0f, 0.0f, 0.0f),
        PositiveY(2, GL20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f),
        NegativeY(3, GL20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0.0f, 0.0f, -1.0f, 0.0f, -1.0f, 0.0f),
        PositiveZ(4, GL20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f),
        NegativeZ(5, GL20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, -1.0f);
        
        public final Vector3 direction;
        public final int glEnum;
        public final int index;
        public final Vector3 up;

        private CubemapSide(int index2, int glEnum2, float upX, float upY, float upZ, float directionX, float directionY, float directionZ) {
            this.index = index2;
            this.glEnum = glEnum2;
            this.up = new Vector3(upX, upY, upZ);
            this.direction = new Vector3(directionX, directionY, directionZ);
        }

        public int getGLEnum() {
            return this.glEnum;
        }

        public Vector3 getUp(Vector3 out) {
            return out.set(this.up);
        }

        public Vector3 getDirection(Vector3 out) {
            return out.set(this.direction);
        }
    }

    public Cubemap(CubemapData data2) {
        super(GL20.GL_TEXTURE_CUBE_MAP);
        this.data = data2;
        load(data2);
    }

    public Cubemap(FileHandle positiveX, FileHandle negativeX, FileHandle positiveY, FileHandle negativeY, FileHandle positiveZ, FileHandle negativeZ) {
        this(positiveX, negativeX, positiveY, negativeY, positiveZ, negativeZ, false);
    }

    public Cubemap(FileHandle positiveX, FileHandle negativeX, FileHandle positiveY, FileHandle negativeY, FileHandle positiveZ, FileHandle negativeZ, boolean useMipMaps) {
        this(TextureData.Factory.loadFromFile(positiveX, useMipMaps), TextureData.Factory.loadFromFile(negativeX, useMipMaps), TextureData.Factory.loadFromFile(positiveY, useMipMaps), TextureData.Factory.loadFromFile(negativeY, useMipMaps), TextureData.Factory.loadFromFile(positiveZ, useMipMaps), TextureData.Factory.loadFromFile(negativeZ, useMipMaps));
    }

    public Cubemap(Pixmap positiveX, Pixmap negativeX, Pixmap positiveY, Pixmap negativeY, Pixmap positiveZ, Pixmap negativeZ) {
        this(positiveX, negativeX, positiveY, negativeY, positiveZ, negativeZ, false);
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public Cubemap(com.badlogic.gdx.graphics.Pixmap r18, com.badlogic.gdx.graphics.Pixmap r19, com.badlogic.gdx.graphics.Pixmap r20, com.badlogic.gdx.graphics.Pixmap r21, com.badlogic.gdx.graphics.Pixmap r22, com.badlogic.gdx.graphics.Pixmap r23, boolean r24) {
        /*
            r17 = this;
            r0 = r18
            r1 = r19
            r2 = r20
            r3 = r21
            r4 = r22
            r5 = r23
            r6 = r24
            r7 = 0
            r8 = 0
            if (r0 != 0) goto L_0x0014
            r11 = r8
            goto L_0x001a
        L_0x0014:
            com.badlogic.gdx.graphics.glutils.PixmapTextureData r9 = new com.badlogic.gdx.graphics.glutils.PixmapTextureData
            r9.<init>(r0, r8, r6, r7)
            r11 = r9
        L_0x001a:
            if (r1 != 0) goto L_0x001e
            r12 = r8
            goto L_0x0024
        L_0x001e:
            com.badlogic.gdx.graphics.glutils.PixmapTextureData r9 = new com.badlogic.gdx.graphics.glutils.PixmapTextureData
            r9.<init>(r1, r8, r6, r7)
            r12 = r9
        L_0x0024:
            if (r2 != 0) goto L_0x0028
            r13 = r8
            goto L_0x002e
        L_0x0028:
            com.badlogic.gdx.graphics.glutils.PixmapTextureData r9 = new com.badlogic.gdx.graphics.glutils.PixmapTextureData
            r9.<init>(r2, r8, r6, r7)
            r13 = r9
        L_0x002e:
            if (r3 != 0) goto L_0x0032
            r14 = r8
            goto L_0x0038
        L_0x0032:
            com.badlogic.gdx.graphics.glutils.PixmapTextureData r9 = new com.badlogic.gdx.graphics.glutils.PixmapTextureData
            r9.<init>(r3, r8, r6, r7)
            r14 = r9
        L_0x0038:
            if (r4 != 0) goto L_0x003c
            r15 = r8
            goto L_0x0042
        L_0x003c:
            com.badlogic.gdx.graphics.glutils.PixmapTextureData r9 = new com.badlogic.gdx.graphics.glutils.PixmapTextureData
            r9.<init>(r4, r8, r6, r7)
            r15 = r9
        L_0x0042:
            if (r5 != 0) goto L_0x0047
            r16 = r8
            goto L_0x004e
        L_0x0047:
            com.badlogic.gdx.graphics.glutils.PixmapTextureData r9 = new com.badlogic.gdx.graphics.glutils.PixmapTextureData
            r9.<init>(r5, r8, r6, r7)
            r16 = r9
        L_0x004e:
            r10 = r17
            r10.<init>((com.badlogic.gdx.graphics.TextureData) r11, (com.badlogic.gdx.graphics.TextureData) r12, (com.badlogic.gdx.graphics.TextureData) r13, (com.badlogic.gdx.graphics.TextureData) r14, (com.badlogic.gdx.graphics.TextureData) r15, (com.badlogic.gdx.graphics.TextureData) r16)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.graphics.Cubemap.<init>(com.badlogic.gdx.graphics.Pixmap, com.badlogic.gdx.graphics.Pixmap, com.badlogic.gdx.graphics.Pixmap, com.badlogic.gdx.graphics.Pixmap, com.badlogic.gdx.graphics.Pixmap, com.badlogic.gdx.graphics.Pixmap, boolean):void");
    }

    public Cubemap(int width, int height, int depth, Pixmap.Format format) {
        this((TextureData) new PixmapTextureData(new Pixmap(depth, height, format), (Pixmap.Format) null, false, true), (TextureData) new PixmapTextureData(new Pixmap(depth, height, format), (Pixmap.Format) null, false, true), (TextureData) new PixmapTextureData(new Pixmap(width, depth, format), (Pixmap.Format) null, false, true), (TextureData) new PixmapTextureData(new Pixmap(width, depth, format), (Pixmap.Format) null, false, true), (TextureData) new PixmapTextureData(new Pixmap(width, height, format), (Pixmap.Format) null, false, true), (TextureData) new PixmapTextureData(new Pixmap(width, height, format), (Pixmap.Format) null, false, true));
    }

    public Cubemap(TextureData positiveX, TextureData negativeX, TextureData positiveY, TextureData negativeY, TextureData positiveZ, TextureData negativeZ) {
        super(GL20.GL_TEXTURE_CUBE_MAP);
        this.minFilter = Texture.TextureFilter.Nearest;
        this.magFilter = Texture.TextureFilter.Nearest;
        this.uWrap = Texture.TextureWrap.ClampToEdge;
        this.vWrap = Texture.TextureWrap.ClampToEdge;
        this.data = new FacedCubemapData(positiveX, negativeX, positiveY, negativeY, positiveZ, negativeZ);
        load(this.data);
    }

    public void load(CubemapData data2) {
        if (!data2.isPrepared()) {
            data2.prepare();
        }
        bind();
        unsafeSetFilter(this.minFilter, this.magFilter, true);
        unsafeSetWrap(this.uWrap, this.vWrap, true);
        data2.consumeCubemapData();
        Gdx.gl.glBindTexture(this.glTarget, 0);
    }

    public CubemapData getCubemapData() {
        return this.data;
    }

    public boolean isManaged() {
        return this.data.isManaged();
    }

    /* access modifiers changed from: protected */
    public void reload() {
        if (isManaged()) {
            this.glHandle = Gdx.gl.glGenTexture();
            load(this.data);
            return;
        }
        throw new GdxRuntimeException("Tried to reload an unmanaged Cubemap");
    }

    public int getWidth() {
        return this.data.getWidth();
    }

    public int getHeight() {
        return this.data.getHeight();
    }

    public int getDepth() {
        return 0;
    }

    public void dispose() {
        if (this.glHandle != 0) {
            delete();
            if (this.data.isManaged() && managedCubemaps.get(Gdx.app) != null) {
                managedCubemaps.get(Gdx.app).removeValue(this, true);
            }
        }
    }

    private static void addManagedCubemap(Application app, Cubemap cubemap) {
        Array<Cubemap> managedCubemapArray = managedCubemaps.get(app);
        if (managedCubemapArray == null) {
            managedCubemapArray = new Array<>();
        }
        managedCubemapArray.add(cubemap);
        managedCubemaps.put(app, managedCubemapArray);
    }

    public static void clearAllCubemaps(Application app) {
        managedCubemaps.remove(app);
    }

    public static void invalidateAllCubemaps(Application app) {
        Array<Cubemap> managedCubemapArray = managedCubemaps.get(app);
        if (managedCubemapArray != null) {
            AssetManager assetManager2 = assetManager;
            if (assetManager2 == null) {
                for (int i = 0; i < managedCubemapArray.size; i++) {
                    managedCubemapArray.get(i).reload();
                }
                return;
            }
            assetManager2.finishLoading();
            Array<Cubemap> cubemaps = new Array<>(managedCubemapArray);
            Iterator<Cubemap> it = cubemaps.iterator();
            while (it.hasNext()) {
                Cubemap cubemap = it.next();
                String fileName = assetManager.getAssetFileName(cubemap);
                if (fileName == null) {
                    cubemap.reload();
                } else {
                    final int refCount = assetManager.getReferenceCount(fileName);
                    assetManager.setReferenceCount(fileName, 0);
                    cubemap.glHandle = 0;
                    CubemapLoader.CubemapParameter params = new CubemapLoader.CubemapParameter();
                    params.cubemapData = cubemap.getCubemapData();
                    params.minFilter = cubemap.getMinFilter();
                    params.magFilter = cubemap.getMagFilter();
                    params.wrapU = cubemap.getUWrap();
                    params.wrapV = cubemap.getVWrap();
                    params.cubemap = cubemap;
                    params.loadedCallback = new AssetLoaderParameters.LoadedCallback() {
                        public void finishedLoading(AssetManager assetManager, String fileName, Class type) {
                            assetManager.setReferenceCount(fileName, refCount);
                        }
                    };
                    assetManager.unload(fileName);
                    cubemap.glHandle = Gdx.gl.glGenTexture();
                    assetManager.load(fileName, Cubemap.class, params);
                }
            }
            managedCubemapArray.clear();
            managedCubemapArray.addAll((Array<? extends Cubemap>) cubemaps);
        }
    }

    public static void setAssetManager(AssetManager manager) {
        assetManager = manager;
    }

    public static String getManagedStatus() {
        StringBuilder builder = new StringBuilder();
        builder.append("Managed cubemap/app: { ");
        for (Application app : managedCubemaps.keySet()) {
            builder.append(managedCubemaps.get(app).size);
            builder.append(" ");
        }
        builder.append("}");
        return builder.toString();
    }

    public static int getNumManagedCubemaps() {
        return managedCubemaps.get(Gdx.app).size;
    }
}
