package com.badlogic.gdx.assets;

import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader;
import com.badlogic.gdx.assets.loaders.CubemapLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.I18NBundleLoader;
import com.badlogic.gdx.assets.loaders.MusicLoader;
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader;
import com.badlogic.gdx.assets.loaders.PixmapLoader;
import com.badlogic.gdx.assets.loaders.ShaderProgramLoader;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.assets.loaders.SoundLoader;
import com.badlogic.gdx.assets.loaders.TextureAtlasLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonRegionLoader;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.UBJsonReader;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.badlogic.gdx.utils.async.ThreadUtils;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.twi.game.BuildConfig;
import java.util.Iterator;
import java.util.Stack;

public class AssetManager implements Disposable {
    final ObjectMap<String, Array<String>> assetDependencies;
    final ObjectMap<String, Class> assetTypes;
    final ObjectMap<Class, ObjectMap<String, RefCountedContainer>> assets;
    final AsyncExecutor executor;
    final ObjectSet<String> injected;
    AssetErrorListener listener;
    final Array<AssetDescriptor> loadQueue;
    int loaded;
    final ObjectMap<Class, ObjectMap<String, AssetLoader>> loaders;
    Logger log;
    int peakTasks;
    final FileHandleResolver resolver;
    final Stack<AssetLoadingTask> tasks;
    int toLoad;

    public AssetManager() {
        this(new InternalFileHandleResolver());
    }

    public AssetManager(FileHandleResolver resolver2) {
        this(resolver2, true);
    }

    public AssetManager(FileHandleResolver resolver2, boolean defaultLoaders) {
        this.assets = new ObjectMap<>();
        this.assetTypes = new ObjectMap<>();
        this.assetDependencies = new ObjectMap<>();
        this.injected = new ObjectSet<>();
        this.loaders = new ObjectMap<>();
        this.loadQueue = new Array<>();
        this.tasks = new Stack<>();
        this.listener = null;
        this.loaded = 0;
        this.toLoad = 0;
        this.peakTasks = 0;
        this.log = new Logger("AssetManager", 0);
        this.resolver = resolver2;
        if (defaultLoaders) {
            setLoader(BitmapFont.class, new BitmapFontLoader(resolver2));
            setLoader(Music.class, new MusicLoader(resolver2));
            setLoader(Pixmap.class, new PixmapLoader(resolver2));
            setLoader(Sound.class, new SoundLoader(resolver2));
            setLoader(TextureAtlas.class, new TextureAtlasLoader(resolver2));
            setLoader(Texture.class, new TextureLoader(resolver2));
            setLoader(Skin.class, new SkinLoader(resolver2));
            setLoader(ParticleEffect.class, new ParticleEffectLoader(resolver2));
            setLoader(com.badlogic.gdx.graphics.g3d.particles.ParticleEffect.class, new com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader(resolver2));
            setLoader(PolygonRegion.class, new PolygonRegionLoader(resolver2));
            setLoader(I18NBundle.class, new I18NBundleLoader(resolver2));
            setLoader(Model.class, ".g3dj", new G3dModelLoader(new JsonReader(), resolver2));
            setLoader(Model.class, ".g3db", new G3dModelLoader(new UBJsonReader(), resolver2));
            setLoader(Model.class, ".obj", new ObjLoader(resolver2));
            setLoader(ShaderProgram.class, new ShaderProgramLoader(resolver2));
            setLoader(Cubemap.class, new CubemapLoader(resolver2));
        }
        this.executor = new AsyncExecutor(1, "AssetManager");
    }

    public FileHandleResolver getFileHandleResolver() {
        return this.resolver;
    }

    public synchronized <T> T get(String fileName) {
        T asset;
        Class<T> type = this.assetTypes.get(fileName);
        if (type != null) {
            ObjectMap<String, RefCountedContainer> assetsByType = this.assets.get(type);
            if (assetsByType != null) {
                RefCountedContainer assetContainer = assetsByType.get(fileName);
                if (assetContainer != null) {
                    asset = assetContainer.getObject(type);
                    if (asset == null) {
                        throw new GdxRuntimeException("Asset not loaded: " + fileName);
                    }
                } else {
                    throw new GdxRuntimeException("Asset not loaded: " + fileName);
                }
            } else {
                throw new GdxRuntimeException("Asset not loaded: " + fileName);
            }
        } else {
            throw new GdxRuntimeException("Asset not loaded: " + fileName);
        }
        return asset;
    }

    public synchronized <T> T get(String fileName, Class<T> type) {
        T asset;
        ObjectMap<String, RefCountedContainer> assetsByType = this.assets.get(type);
        if (assetsByType != null) {
            RefCountedContainer assetContainer = assetsByType.get(fileName);
            if (assetContainer != null) {
                asset = assetContainer.getObject(type);
                if (asset == null) {
                    throw new GdxRuntimeException("Asset not loaded: " + fileName);
                }
            } else {
                throw new GdxRuntimeException("Asset not loaded: " + fileName);
            }
        } else {
            throw new GdxRuntimeException("Asset not loaded: " + fileName);
        }
        return asset;
    }

    public synchronized <T> Array<T> getAll(Class<T> type, Array<T> out) {
        ObjectMap<String, RefCountedContainer> assetsByType = this.assets.get(type);
        if (assetsByType != null) {
            ObjectMap.Entries<String, RefCountedContainer> it = assetsByType.entries().iterator();
            while (it.hasNext()) {
                out.add(((RefCountedContainer) ((ObjectMap.Entry) it.next()).value).getObject(type));
            }
        }
        return out;
    }

    public synchronized <T> T get(AssetDescriptor<T> assetDescriptor) {
        return get(assetDescriptor.fileName, assetDescriptor.type);
    }

    public synchronized boolean contains(String fileName) {
        if (this.tasks.size() > 0 && ((AssetLoadingTask) this.tasks.firstElement()).assetDesc.fileName.equals(fileName)) {
            return true;
        }
        for (int i = 0; i < this.loadQueue.size; i++) {
            if (this.loadQueue.get(i).fileName.equals(fileName)) {
                return true;
            }
        }
        return isLoaded(fileName);
    }

    public synchronized boolean contains(String fileName, Class type) {
        if (this.tasks.size() > 0) {
            AssetDescriptor assetDesc = ((AssetLoadingTask) this.tasks.firstElement()).assetDesc;
            if (assetDesc.type == type && assetDesc.fileName.equals(fileName)) {
                return true;
            }
        }
        for (int i = 0; i < this.loadQueue.size; i++) {
            AssetDescriptor assetDesc2 = this.loadQueue.get(i);
            if (assetDesc2.type == type && assetDesc2.fileName.equals(fileName)) {
                return true;
            }
        }
        return isLoaded(fileName, type);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:45:0x011b, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void unload(java.lang.String r8) {
        /*
            r7 = this;
            monitor-enter(r7)
            java.util.Stack<com.badlogic.gdx.assets.AssetLoadingTask> r0 = r7.tasks     // Catch:{ all -> 0x0133 }
            int r0 = r0.size()     // Catch:{ all -> 0x0133 }
            r1 = 1
            if (r0 <= 0) goto L_0x0036
            java.util.Stack<com.badlogic.gdx.assets.AssetLoadingTask> r0 = r7.tasks     // Catch:{ all -> 0x0133 }
            java.lang.Object r0 = r0.firstElement()     // Catch:{ all -> 0x0133 }
            com.badlogic.gdx.assets.AssetLoadingTask r0 = (com.badlogic.gdx.assets.AssetLoadingTask) r0     // Catch:{ all -> 0x0133 }
            com.badlogic.gdx.assets.AssetDescriptor r2 = r0.assetDesc     // Catch:{ all -> 0x0133 }
            java.lang.String r2 = r2.fileName     // Catch:{ all -> 0x0133 }
            boolean r2 = r2.equals(r8)     // Catch:{ all -> 0x0133 }
            if (r2 == 0) goto L_0x0036
            r0.cancel = r1     // Catch:{ all -> 0x0133 }
            com.badlogic.gdx.utils.Logger r1 = r7.log     // Catch:{ all -> 0x0133 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0133 }
            r2.<init>()     // Catch:{ all -> 0x0133 }
            java.lang.String r3 = "Unload (from tasks): "
            r2.append(r3)     // Catch:{ all -> 0x0133 }
            r2.append(r8)     // Catch:{ all -> 0x0133 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0133 }
            r1.info(r2)     // Catch:{ all -> 0x0133 }
            monitor-exit(r7)
            return
        L_0x0036:
            r0 = -1
            r2 = 0
        L_0x0038:
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.assets.AssetDescriptor> r3 = r7.loadQueue     // Catch:{ all -> 0x0133 }
            int r3 = r3.size     // Catch:{ all -> 0x0133 }
            if (r2 >= r3) goto L_0x0053
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.assets.AssetDescriptor> r3 = r7.loadQueue     // Catch:{ all -> 0x0133 }
            java.lang.Object r3 = r3.get(r2)     // Catch:{ all -> 0x0133 }
            com.badlogic.gdx.assets.AssetDescriptor r3 = (com.badlogic.gdx.assets.AssetDescriptor) r3     // Catch:{ all -> 0x0133 }
            java.lang.String r3 = r3.fileName     // Catch:{ all -> 0x0133 }
            boolean r3 = r3.equals(r8)     // Catch:{ all -> 0x0133 }
            if (r3 == 0) goto L_0x0050
            r0 = r2
            goto L_0x0053
        L_0x0050:
            int r2 = r2 + 1
            goto L_0x0038
        L_0x0053:
            r2 = -1
            if (r0 == r2) goto L_0x0078
            int r2 = r7.toLoad     // Catch:{ all -> 0x0133 }
            int r2 = r2 - r1
            r7.toLoad = r2     // Catch:{ all -> 0x0133 }
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.assets.AssetDescriptor> r1 = r7.loadQueue     // Catch:{ all -> 0x0133 }
            r1.removeIndex(r0)     // Catch:{ all -> 0x0133 }
            com.badlogic.gdx.utils.Logger r1 = r7.log     // Catch:{ all -> 0x0133 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0133 }
            r2.<init>()     // Catch:{ all -> 0x0133 }
            java.lang.String r3 = "Unload (from queue): "
            r2.append(r3)     // Catch:{ all -> 0x0133 }
            r2.append(r8)     // Catch:{ all -> 0x0133 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0133 }
            r1.info(r2)     // Catch:{ all -> 0x0133 }
            monitor-exit(r7)
            return
        L_0x0078:
            com.badlogic.gdx.utils.ObjectMap<java.lang.String, java.lang.Class> r1 = r7.assetTypes     // Catch:{ all -> 0x0133 }
            java.lang.Object r1 = r1.get(r8)     // Catch:{ all -> 0x0133 }
            java.lang.Class r1 = (java.lang.Class) r1     // Catch:{ all -> 0x0133 }
            if (r1 == 0) goto L_0x011c
            com.badlogic.gdx.utils.ObjectMap<java.lang.Class, com.badlogic.gdx.utils.ObjectMap<java.lang.String, com.badlogic.gdx.assets.RefCountedContainer>> r2 = r7.assets     // Catch:{ all -> 0x0133 }
            java.lang.Object r2 = r2.get(r1)     // Catch:{ all -> 0x0133 }
            com.badlogic.gdx.utils.ObjectMap r2 = (com.badlogic.gdx.utils.ObjectMap) r2     // Catch:{ all -> 0x0133 }
            java.lang.Object r2 = r2.get(r8)     // Catch:{ all -> 0x0133 }
            com.badlogic.gdx.assets.RefCountedContainer r2 = (com.badlogic.gdx.assets.RefCountedContainer) r2     // Catch:{ all -> 0x0133 }
            r2.decRefCount()     // Catch:{ all -> 0x0133 }
            int r3 = r2.getRefCount()     // Catch:{ all -> 0x0133 }
            if (r3 > 0) goto L_0x00d5
            com.badlogic.gdx.utils.Logger r3 = r7.log     // Catch:{ all -> 0x0133 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0133 }
            r4.<init>()     // Catch:{ all -> 0x0133 }
            java.lang.String r5 = "Unload (dispose): "
            r4.append(r5)     // Catch:{ all -> 0x0133 }
            r4.append(r8)     // Catch:{ all -> 0x0133 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0133 }
            r3.info(r4)     // Catch:{ all -> 0x0133 }
            java.lang.Class<java.lang.Object> r3 = java.lang.Object.class
            java.lang.Object r3 = r2.getObject(r3)     // Catch:{ all -> 0x0133 }
            boolean r3 = r3 instanceof com.badlogic.gdx.utils.Disposable     // Catch:{ all -> 0x0133 }
            if (r3 == 0) goto L_0x00c4
            java.lang.Class<java.lang.Object> r3 = java.lang.Object.class
            java.lang.Object r3 = r2.getObject(r3)     // Catch:{ all -> 0x0133 }
            com.badlogic.gdx.utils.Disposable r3 = (com.badlogic.gdx.utils.Disposable) r3     // Catch:{ all -> 0x0133 }
            r3.dispose()     // Catch:{ all -> 0x0133 }
        L_0x00c4:
            com.badlogic.gdx.utils.ObjectMap<java.lang.String, java.lang.Class> r3 = r7.assetTypes     // Catch:{ all -> 0x0133 }
            r3.remove(r8)     // Catch:{ all -> 0x0133 }
            com.badlogic.gdx.utils.ObjectMap<java.lang.Class, com.badlogic.gdx.utils.ObjectMap<java.lang.String, com.badlogic.gdx.assets.RefCountedContainer>> r3 = r7.assets     // Catch:{ all -> 0x0133 }
            java.lang.Object r3 = r3.get(r1)     // Catch:{ all -> 0x0133 }
            com.badlogic.gdx.utils.ObjectMap r3 = (com.badlogic.gdx.utils.ObjectMap) r3     // Catch:{ all -> 0x0133 }
            r3.remove(r8)     // Catch:{ all -> 0x0133 }
            goto L_0x00eb
        L_0x00d5:
            com.badlogic.gdx.utils.Logger r3 = r7.log     // Catch:{ all -> 0x0133 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0133 }
            r4.<init>()     // Catch:{ all -> 0x0133 }
            java.lang.String r5 = "Unload (decrement): "
            r4.append(r5)     // Catch:{ all -> 0x0133 }
            r4.append(r8)     // Catch:{ all -> 0x0133 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0133 }
            r3.info(r4)     // Catch:{ all -> 0x0133 }
        L_0x00eb:
            com.badlogic.gdx.utils.ObjectMap<java.lang.String, com.badlogic.gdx.utils.Array<java.lang.String>> r3 = r7.assetDependencies     // Catch:{ all -> 0x0133 }
            java.lang.Object r3 = r3.get(r8)     // Catch:{ all -> 0x0133 }
            com.badlogic.gdx.utils.Array r3 = (com.badlogic.gdx.utils.Array) r3     // Catch:{ all -> 0x0133 }
            if (r3 == 0) goto L_0x010f
            java.util.Iterator r4 = r3.iterator()     // Catch:{ all -> 0x0133 }
        L_0x00f9:
            boolean r5 = r4.hasNext()     // Catch:{ all -> 0x0133 }
            if (r5 == 0) goto L_0x010f
            java.lang.Object r5 = r4.next()     // Catch:{ all -> 0x0133 }
            java.lang.String r5 = (java.lang.String) r5     // Catch:{ all -> 0x0133 }
            boolean r6 = r7.isLoaded((java.lang.String) r5)     // Catch:{ all -> 0x0133 }
            if (r6 == 0) goto L_0x010e
            r7.unload(r5)     // Catch:{ all -> 0x0133 }
        L_0x010e:
            goto L_0x00f9
        L_0x010f:
            int r4 = r2.getRefCount()     // Catch:{ all -> 0x0133 }
            if (r4 > 0) goto L_0x011a
            com.badlogic.gdx.utils.ObjectMap<java.lang.String, com.badlogic.gdx.utils.Array<java.lang.String>> r4 = r7.assetDependencies     // Catch:{ all -> 0x0133 }
            r4.remove(r8)     // Catch:{ all -> 0x0133 }
        L_0x011a:
            monitor-exit(r7)
            return
        L_0x011c:
            com.badlogic.gdx.utils.GdxRuntimeException r2 = new com.badlogic.gdx.utils.GdxRuntimeException     // Catch:{ all -> 0x0133 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0133 }
            r3.<init>()     // Catch:{ all -> 0x0133 }
            java.lang.String r4 = "Asset not loaded: "
            r3.append(r4)     // Catch:{ all -> 0x0133 }
            r3.append(r8)     // Catch:{ all -> 0x0133 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0133 }
            r2.<init>((java.lang.String) r3)     // Catch:{ all -> 0x0133 }
            throw r2     // Catch:{ all -> 0x0133 }
        L_0x0133:
            r8 = move-exception
            monitor-exit(r7)
            goto L_0x0137
        L_0x0136:
            throw r8
        L_0x0137:
            goto L_0x0136
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.assets.AssetManager.unload(java.lang.String):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:11:0x0020  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized <T> boolean containsAsset(T r7) {
        /*
            r6 = this;
            monitor-enter(r6)
            com.badlogic.gdx.utils.ObjectMap<java.lang.Class, com.badlogic.gdx.utils.ObjectMap<java.lang.String, com.badlogic.gdx.assets.RefCountedContainer>> r0 = r6.assets     // Catch:{ all -> 0x0041 }
            java.lang.Class r1 = r7.getClass()     // Catch:{ all -> 0x0041 }
            java.lang.Object r0 = r0.get(r1)     // Catch:{ all -> 0x0041 }
            com.badlogic.gdx.utils.ObjectMap r0 = (com.badlogic.gdx.utils.ObjectMap) r0     // Catch:{ all -> 0x0041 }
            r1 = 0
            if (r0 != 0) goto L_0x0012
            monitor-exit(r6)
            return r1
        L_0x0012:
            com.badlogic.gdx.utils.ObjectMap$Keys r2 = r0.keys()     // Catch:{ all -> 0x0041 }
            com.badlogic.gdx.utils.ObjectMap$Keys r2 = r2.iterator()     // Catch:{ all -> 0x0041 }
        L_0x001a:
            boolean r3 = r2.hasNext()     // Catch:{ all -> 0x0041 }
            if (r3 == 0) goto L_0x003f
            java.lang.Object r3 = r2.next()     // Catch:{ all -> 0x0041 }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ all -> 0x0041 }
            java.lang.Object r4 = r0.get(r3)     // Catch:{ all -> 0x0041 }
            com.badlogic.gdx.assets.RefCountedContainer r4 = (com.badlogic.gdx.assets.RefCountedContainer) r4     // Catch:{ all -> 0x0041 }
            java.lang.Class<java.lang.Object> r5 = java.lang.Object.class
            java.lang.Object r4 = r4.getObject(r5)     // Catch:{ all -> 0x0041 }
            if (r4 == r7) goto L_0x003c
            boolean r5 = r7.equals(r4)     // Catch:{ all -> 0x0041 }
            if (r5 == 0) goto L_0x003b
            goto L_0x003c
        L_0x003b:
            goto L_0x001a
        L_0x003c:
            r1 = 1
            monitor-exit(r6)
            return r1
        L_0x003f:
            monitor-exit(r6)
            return r1
        L_0x0041:
            r7 = move-exception
            monitor-exit(r6)
            goto L_0x0045
        L_0x0044:
            throw r7
        L_0x0045:
            goto L_0x0044
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.assets.AssetManager.containsAsset(java.lang.Object):boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x004a, code lost:
        return r4;
     */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x000b A[LOOP:0: B:3:0x000b->B:23:0x000b, LOOP_END, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x002d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized <T> java.lang.String getAssetFileName(T r8) {
        /*
            r7 = this;
            monitor-enter(r7)
            com.badlogic.gdx.utils.ObjectMap<java.lang.Class, com.badlogic.gdx.utils.ObjectMap<java.lang.String, com.badlogic.gdx.assets.RefCountedContainer>> r0 = r7.assets     // Catch:{ all -> 0x004f }
            com.badlogic.gdx.utils.ObjectMap$Keys r0 = r0.keys()     // Catch:{ all -> 0x004f }
            com.badlogic.gdx.utils.ObjectMap$Keys r0 = r0.iterator()     // Catch:{ all -> 0x004f }
        L_0x000b:
            boolean r1 = r0.hasNext()     // Catch:{ all -> 0x004f }
            if (r1 == 0) goto L_0x004c
            java.lang.Object r1 = r0.next()     // Catch:{ all -> 0x004f }
            java.lang.Class r1 = (java.lang.Class) r1     // Catch:{ all -> 0x004f }
            com.badlogic.gdx.utils.ObjectMap<java.lang.Class, com.badlogic.gdx.utils.ObjectMap<java.lang.String, com.badlogic.gdx.assets.RefCountedContainer>> r2 = r7.assets     // Catch:{ all -> 0x004f }
            java.lang.Object r2 = r2.get(r1)     // Catch:{ all -> 0x004f }
            com.badlogic.gdx.utils.ObjectMap r2 = (com.badlogic.gdx.utils.ObjectMap) r2     // Catch:{ all -> 0x004f }
            com.badlogic.gdx.utils.ObjectMap$Keys r3 = r2.keys()     // Catch:{ all -> 0x004f }
            com.badlogic.gdx.utils.ObjectMap$Keys r3 = r3.iterator()     // Catch:{ all -> 0x004f }
        L_0x0027:
            boolean r4 = r3.hasNext()     // Catch:{ all -> 0x004f }
            if (r4 == 0) goto L_0x004b
            java.lang.Object r4 = r3.next()     // Catch:{ all -> 0x004f }
            java.lang.String r4 = (java.lang.String) r4     // Catch:{ all -> 0x004f }
            java.lang.Object r5 = r2.get(r4)     // Catch:{ all -> 0x004f }
            com.badlogic.gdx.assets.RefCountedContainer r5 = (com.badlogic.gdx.assets.RefCountedContainer) r5     // Catch:{ all -> 0x004f }
            java.lang.Class<java.lang.Object> r6 = java.lang.Object.class
            java.lang.Object r5 = r5.getObject(r6)     // Catch:{ all -> 0x004f }
            if (r5 == r8) goto L_0x0049
            boolean r6 = r8.equals(r5)     // Catch:{ all -> 0x004f }
            if (r6 == 0) goto L_0x0048
            goto L_0x0049
        L_0x0048:
            goto L_0x0027
        L_0x0049:
            monitor-exit(r7)
            return r4
        L_0x004b:
            goto L_0x000b
        L_0x004c:
            r0 = 0
            monitor-exit(r7)
            return r0
        L_0x004f:
            r8 = move-exception
            monitor-exit(r7)
            goto L_0x0053
        L_0x0052:
            throw r8
        L_0x0053:
            goto L_0x0052
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.assets.AssetManager.getAssetFileName(java.lang.Object):java.lang.String");
    }

    public synchronized boolean isLoaded(AssetDescriptor assetDesc) {
        return isLoaded(assetDesc.fileName);
    }

    public synchronized boolean isLoaded(String fileName) {
        if (fileName == null) {
            return false;
        }
        return this.assetTypes.containsKey(fileName);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0020, code lost:
        return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean isLoaded(java.lang.String r5, java.lang.Class r6) {
        /*
            r4 = this;
            monitor-enter(r4)
            com.badlogic.gdx.utils.ObjectMap<java.lang.Class, com.badlogic.gdx.utils.ObjectMap<java.lang.String, com.badlogic.gdx.assets.RefCountedContainer>> r0 = r4.assets     // Catch:{ all -> 0x0021 }
            java.lang.Object r0 = r0.get(r6)     // Catch:{ all -> 0x0021 }
            com.badlogic.gdx.utils.ObjectMap r0 = (com.badlogic.gdx.utils.ObjectMap) r0     // Catch:{ all -> 0x0021 }
            r1 = 0
            if (r0 != 0) goto L_0x000e
            monitor-exit(r4)
            return r1
        L_0x000e:
            java.lang.Object r2 = r0.get(r5)     // Catch:{ all -> 0x0021 }
            com.badlogic.gdx.assets.RefCountedContainer r2 = (com.badlogic.gdx.assets.RefCountedContainer) r2     // Catch:{ all -> 0x0021 }
            if (r2 != 0) goto L_0x0018
            monitor-exit(r4)
            return r1
        L_0x0018:
            java.lang.Object r3 = r2.getObject(r6)     // Catch:{ all -> 0x0021 }
            if (r3 == 0) goto L_0x001f
            r1 = 1
        L_0x001f:
            monitor-exit(r4)
            return r1
        L_0x0021:
            r5 = move-exception
            monitor-exit(r4)
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.assets.AssetManager.isLoaded(java.lang.String, java.lang.Class):boolean");
    }

    public <T> AssetLoader getLoader(Class<T> type) {
        return getLoader(type, (String) null);
    }

    public <T> AssetLoader getLoader(Class<T> type, String fileName) {
        ObjectMap<String, AssetLoader> loaders2 = this.loaders.get(type);
        if (loaders2 == null || loaders2.size < 1) {
            return null;
        }
        if (fileName == null) {
            return loaders2.get(BuildConfig.FLAVOR);
        }
        AssetLoader result = null;
        int l = -1;
        ObjectMap.Entries<String, AssetLoader> it = loaders2.entries().iterator();
        while (it.hasNext()) {
            ObjectMap.Entry<String, AssetLoader> entry = (ObjectMap.Entry) it.next();
            if (((String) entry.key).length() > l && fileName.endsWith((String) entry.key)) {
                result = entry.value;
                l = ((String) entry.key).length();
            }
        }
        return result;
    }

    public synchronized <T> void load(String fileName, Class<T> type) {
        load(fileName, type, (AssetLoaderParameters) null);
    }

    public synchronized <T> void load(String fileName, Class<T> type, AssetLoaderParameters<T> parameter) {
        if (getLoader(type, fileName) != null) {
            if (this.loadQueue.size == 0) {
                this.loaded = 0;
                this.toLoad = 0;
                this.peakTasks = 0;
            }
            for (int i = 0; i < this.loadQueue.size; i++) {
                AssetDescriptor desc = this.loadQueue.get(i);
                if (desc.fileName.equals(fileName)) {
                    if (!desc.type.equals(type)) {
                        throw new GdxRuntimeException("Asset with name '" + fileName + "' already in preload queue, but has different type (expected: " + ClassReflection.getSimpleName(type) + ", found: " + ClassReflection.getSimpleName(desc.type) + ")");
                    }
                }
            }
            for (int i2 = 0; i2 < this.tasks.size(); i2++) {
                AssetDescriptor desc2 = ((AssetLoadingTask) this.tasks.get(i2)).assetDesc;
                if (desc2.fileName.equals(fileName)) {
                    if (!desc2.type.equals(type)) {
                        throw new GdxRuntimeException("Asset with name '" + fileName + "' already in task list, but has different type (expected: " + ClassReflection.getSimpleName(type) + ", found: " + ClassReflection.getSimpleName(desc2.type) + ")");
                    }
                }
            }
            Class otherType = this.assetTypes.get(fileName);
            if (otherType != null) {
                if (!otherType.equals(type)) {
                    throw new GdxRuntimeException("Asset with name '" + fileName + "' already loaded, but has different type (expected: " + ClassReflection.getSimpleName(type) + ", found: " + ClassReflection.getSimpleName(otherType) + ")");
                }
            }
            this.toLoad++;
            AssetDescriptor assetDesc = new AssetDescriptor(fileName, type, parameter);
            this.loadQueue.add(assetDesc);
            this.log.debug("Queued: " + assetDesc);
        } else {
            throw new GdxRuntimeException("No loader for type: " + ClassReflection.getSimpleName(type));
        }
    }

    public synchronized void load(AssetDescriptor desc) {
        load(desc.fileName, desc.type, desc.params);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:23:0x003d, code lost:
        return r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean update() {
        /*
            r4 = this;
            monitor-enter(r4)
            r0 = 0
            r1 = 1
            java.util.Stack<com.badlogic.gdx.assets.AssetLoadingTask> r2 = r4.tasks     // Catch:{ Throwable -> 0x0040 }
            int r2 = r2.size()     // Catch:{ Throwable -> 0x0040 }
            if (r2 != 0) goto L_0x0027
        L_0x000b:
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.assets.AssetDescriptor> r2 = r4.loadQueue     // Catch:{ Throwable -> 0x0040 }
            int r2 = r2.size     // Catch:{ Throwable -> 0x0040 }
            if (r2 == 0) goto L_0x001d
            java.util.Stack<com.badlogic.gdx.assets.AssetLoadingTask> r2 = r4.tasks     // Catch:{ Throwable -> 0x0040 }
            int r2 = r2.size()     // Catch:{ Throwable -> 0x0040 }
            if (r2 != 0) goto L_0x001d
            r4.nextTask()     // Catch:{ Throwable -> 0x0040 }
            goto L_0x000b
        L_0x001d:
            java.util.Stack<com.badlogic.gdx.assets.AssetLoadingTask> r2 = r4.tasks     // Catch:{ Throwable -> 0x0040 }
            int r2 = r2.size()     // Catch:{ Throwable -> 0x0040 }
            if (r2 != 0) goto L_0x0027
            monitor-exit(r4)
            return r1
        L_0x0027:
            boolean r2 = r4.updateTask()     // Catch:{ Throwable -> 0x0040 }
            if (r2 == 0) goto L_0x003c
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.assets.AssetDescriptor> r2 = r4.loadQueue     // Catch:{ Throwable -> 0x0040 }
            int r2 = r2.size     // Catch:{ Throwable -> 0x0040 }
            if (r2 != 0) goto L_0x003c
            java.util.Stack<com.badlogic.gdx.assets.AssetLoadingTask> r2 = r4.tasks     // Catch:{ Throwable -> 0x0040 }
            int r2 = r2.size()     // Catch:{ Throwable -> 0x0040 }
            if (r2 != 0) goto L_0x003c
            r0 = 1
        L_0x003c:
            monitor-exit(r4)
            return r0
        L_0x003e:
            r0 = move-exception
            goto L_0x004d
        L_0x0040:
            r2 = move-exception
            r4.handleTaskError(r2)     // Catch:{ all -> 0x003e }
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.assets.AssetDescriptor> r3 = r4.loadQueue     // Catch:{ all -> 0x003e }
            int r3 = r3.size     // Catch:{ all -> 0x003e }
            if (r3 != 0) goto L_0x004b
            r0 = 1
        L_0x004b:
            monitor-exit(r4)
            return r0
        L_0x004d:
            monitor-exit(r4)
            goto L_0x0050
        L_0x004f:
            throw r0
        L_0x0050:
            goto L_0x004f
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.assets.AssetManager.update():boolean");
    }

    public boolean update(int millis) {
        boolean done;
        long endTime = TimeUtils.millis() + ((long) millis);
        while (true) {
            done = update();
            if (done || TimeUtils.millis() > endTime) {
                return done;
            }
            ThreadUtils.yield();
        }
        return done;
    }

    public synchronized boolean isFinished() {
        return this.loadQueue.size == 0 && this.tasks.size() == 0;
    }

    public void finishLoading() {
        this.log.debug("Waiting for loading to complete...");
        while (!update()) {
            ThreadUtils.yield();
        }
        this.log.debug("Loading complete.");
    }

    public <T> T finishLoadingAsset(AssetDescriptor assetDesc) {
        return finishLoadingAsset(assetDesc.fileName);
    }

    public <T> T finishLoadingAsset(String fileName) {
        ObjectMap<String, RefCountedContainer> assetsByType;
        RefCountedContainer assetContainer;
        T asset;
        Logger logger = this.log;
        logger.debug("Waiting for asset to be loaded: " + fileName);
        while (true) {
            synchronized (this) {
                Class<T> type = this.assetTypes.get(fileName);
                if (type == null || (assetsByType = this.assets.get(type)) == null || (assetContainer = assetsByType.get(fileName)) == null || (asset = assetContainer.getObject(type)) == null) {
                    update();
                } else {
                    Logger logger2 = this.log;
                    logger2.debug("Asset loaded: " + fileName);
                    return asset;
                }
            }
            ThreadUtils.yield();
        }
        while (true) {
        }
    }

    /* access modifiers changed from: package-private */
    public synchronized void injectDependencies(String parentAssetFilename, Array<AssetDescriptor> dependendAssetDescs) {
        ObjectSet<String> injected2 = this.injected;
        Iterator<AssetDescriptor> it = dependendAssetDescs.iterator();
        while (it.hasNext()) {
            AssetDescriptor desc = it.next();
            if (!injected2.contains(desc.fileName)) {
                injected2.add(desc.fileName);
                injectDependency(parentAssetFilename, desc);
            }
        }
        injected2.clear(32);
    }

    private synchronized void injectDependency(String parentAssetFilename, AssetDescriptor dependendAssetDesc) {
        Array<String> dependencies = this.assetDependencies.get(parentAssetFilename);
        if (dependencies == null) {
            dependencies = new Array<>();
            this.assetDependencies.put(parentAssetFilename, dependencies);
        }
        dependencies.add(dependendAssetDesc.fileName);
        if (isLoaded(dependendAssetDesc.fileName)) {
            Logger logger = this.log;
            logger.debug("Dependency already loaded: " + dependendAssetDesc);
            ((RefCountedContainer) this.assets.get(this.assetTypes.get(dependendAssetDesc.fileName)).get(dependendAssetDesc.fileName)).incRefCount();
            incrementRefCountedDependencies(dependendAssetDesc.fileName);
        } else {
            Logger logger2 = this.log;
            logger2.info("Loading dependency: " + dependendAssetDesc);
            addTask(dependendAssetDesc);
        }
    }

    private void nextTask() {
        AssetDescriptor assetDesc = this.loadQueue.removeIndex(0);
        if (isLoaded(assetDesc.fileName)) {
            this.log.debug("Already loaded: " + assetDesc);
            ((RefCountedContainer) this.assets.get(this.assetTypes.get(assetDesc.fileName)).get(assetDesc.fileName)).incRefCount();
            incrementRefCountedDependencies(assetDesc.fileName);
            if (!(assetDesc.params == null || assetDesc.params.loadedCallback == null)) {
                assetDesc.params.loadedCallback.finishedLoading(this, assetDesc.fileName, assetDesc.type);
            }
            this.loaded++;
            return;
        }
        this.log.info("Loading: " + assetDesc);
        addTask(assetDesc);
    }

    private void addTask(AssetDescriptor assetDesc) {
        AssetLoader loader = getLoader(assetDesc.type, assetDesc.fileName);
        if (loader != null) {
            this.tasks.push(new AssetLoadingTask(this, assetDesc, loader, this.executor));
            this.peakTasks++;
            return;
        }
        throw new GdxRuntimeException("No loader for type: " + ClassReflection.getSimpleName(assetDesc.type));
    }

    /* access modifiers changed from: protected */
    public <T> void addAsset(String fileName, Class<T> type, T asset) {
        this.assetTypes.put(fileName, type);
        ObjectMap<String, RefCountedContainer> typeToAssets = this.assets.get(type);
        if (typeToAssets == null) {
            typeToAssets = new ObjectMap<>();
            this.assets.put(type, typeToAssets);
        }
        typeToAssets.put(fileName, new RefCountedContainer(asset));
    }

    private boolean updateTask() {
        AssetLoadingTask task = this.tasks.peek();
        boolean complete = true;
        try {
            complete = task.cancel || task.update();
        } catch (RuntimeException ex) {
            task.cancel = true;
            taskFailed(task.assetDesc, ex);
        }
        if (!complete) {
            return false;
        }
        if (this.tasks.size() == 1) {
            this.loaded++;
            this.peakTasks = 0;
        }
        this.tasks.pop();
        if (task.cancel) {
            return true;
        }
        addAsset(task.assetDesc.fileName, task.assetDesc.type, task.getAsset());
        if (!(task.assetDesc.params == null || task.assetDesc.params.loadedCallback == null)) {
            task.assetDesc.params.loadedCallback.finishedLoading(this, task.assetDesc.fileName, task.assetDesc.type);
        }
        long endTime = TimeUtils.nanoTime();
        this.log.debug("Loaded: " + (((float) (endTime - task.startTime)) / 1000000.0f) + "ms " + task.assetDesc);
        return true;
    }

    /* access modifiers changed from: protected */
    public void taskFailed(AssetDescriptor assetDesc, RuntimeException ex) {
        throw ex;
    }

    private void incrementRefCountedDependencies(String parent) {
        Array<String> dependencies = this.assetDependencies.get(parent);
        if (dependencies != null) {
            Iterator<String> it = dependencies.iterator();
            while (it.hasNext()) {
                String dependency = it.next();
                ((RefCountedContainer) this.assets.get(this.assetTypes.get(dependency)).get(dependency)).incRefCount();
                incrementRefCountedDependencies(dependency);
            }
        }
    }

    private void handleTaskError(Throwable t) {
        this.log.error("Error loading asset.", t);
        if (!this.tasks.isEmpty()) {
            AssetLoadingTask task = this.tasks.pop();
            AssetDescriptor assetDesc = task.assetDesc;
            if (task.dependenciesLoaded && task.dependencies != null) {
                Iterator<AssetDescriptor> it = task.dependencies.iterator();
                while (it.hasNext()) {
                    unload(it.next().fileName);
                }
            }
            this.tasks.clear();
            AssetErrorListener assetErrorListener = this.listener;
            if (assetErrorListener != null) {
                assetErrorListener.error(assetDesc, t);
                return;
            }
            throw new GdxRuntimeException(t);
        }
        throw new GdxRuntimeException(t);
    }

    public synchronized <T, P extends AssetLoaderParameters<T>> void setLoader(Class<T> type, AssetLoader<T, P> loader) {
        setLoader(type, (String) null, loader);
    }

    public synchronized <T, P extends AssetLoaderParameters<T>> void setLoader(Class<T> type, String suffix, AssetLoader<T, P> loader) {
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null.");
        } else if (loader != null) {
            Logger logger = this.log;
            logger.debug("Loader set: " + ClassReflection.getSimpleName(type) + " -> " + ClassReflection.getSimpleName(loader.getClass()));
            ObjectMap<String, AssetLoader> loaders2 = this.loaders.get(type);
            if (loaders2 == null) {
                ObjectMap<Class, ObjectMap<String, AssetLoader>> objectMap = this.loaders;
                ObjectMap<String, AssetLoader> objectMap2 = new ObjectMap<>();
                loaders2 = objectMap2;
                objectMap.put(type, objectMap2);
            }
            loaders2.put(suffix == null ? BuildConfig.FLAVOR : suffix, loader);
        } else {
            throw new IllegalArgumentException("loader cannot be null.");
        }
    }

    public synchronized int getLoadedAssets() {
        return this.assetTypes.size;
    }

    public synchronized int getQueuedAssets() {
        return this.loadQueue.size + this.tasks.size();
    }

    public synchronized float getProgress() {
        if (this.toLoad == 0) {
            return 1.0f;
        }
        float fractionalLoaded = (float) this.loaded;
        if (this.peakTasks > 0) {
            fractionalLoaded += ((float) (this.peakTasks - this.tasks.size())) / ((float) this.peakTasks);
        }
        return Math.min(1.0f, fractionalLoaded / ((float) this.toLoad));
    }

    public synchronized void setErrorListener(AssetErrorListener listener2) {
        this.listener = listener2;
    }

    public synchronized void dispose() {
        this.log.debug("Disposing.");
        clear();
        this.executor.dispose();
    }

    public synchronized void clear() {
        this.loadQueue.clear();
        while (!update()) {
        }
        ObjectIntMap<String> dependencyCount = new ObjectIntMap<>();
        while (this.assetTypes.size > 0) {
            dependencyCount.clear();
            Array<String> assets2 = this.assetTypes.keys().toArray();
            Iterator<String> it = assets2.iterator();
            while (it.hasNext()) {
                dependencyCount.put(it.next(), 0);
            }
            Iterator<String> it2 = assets2.iterator();
            while (it2.hasNext()) {
                Array<String> dependencies = this.assetDependencies.get(it2.next());
                if (dependencies != null) {
                    Iterator<String> it3 = dependencies.iterator();
                    while (it3.hasNext()) {
                        String dependency = it3.next();
                        dependencyCount.put(dependency, dependencyCount.get(dependency, 0) + 1);
                    }
                }
            }
            Iterator<String> it4 = assets2.iterator();
            while (it4.hasNext()) {
                String asset = it4.next();
                if (dependencyCount.get(asset, 0) == 0) {
                    unload(asset);
                }
            }
        }
        this.assets.clear();
        this.assetTypes.clear();
        this.assetDependencies.clear();
        this.loaded = 0;
        this.toLoad = 0;
        this.peakTasks = 0;
        this.loadQueue.clear();
        this.tasks.clear();
    }

    public Logger getLogger() {
        return this.log;
    }

    public void setLogger(Logger logger) {
        this.log = logger;
    }

    public synchronized int getReferenceCount(String fileName) {
        Class type;
        type = this.assetTypes.get(fileName);
        if (type != null) {
        } else {
            throw new GdxRuntimeException("Asset not loaded: " + fileName);
        }
        return ((RefCountedContainer) this.assets.get(type).get(fileName)).getRefCount();
    }

    public synchronized void setReferenceCount(String fileName, int refCount) {
        Class type = this.assetTypes.get(fileName);
        if (type != null) {
            ((RefCountedContainer) this.assets.get(type).get(fileName)).setRefCount(refCount);
        } else {
            throw new GdxRuntimeException("Asset not loaded: " + fileName);
        }
    }

    public synchronized String getDiagnostics() {
        StringBuilder sb;
        sb = new StringBuilder(256);
        ObjectMap.Keys<String> it = this.assetTypes.keys().iterator();
        while (it.hasNext()) {
            String fileName = (String) it.next();
            if (sb.length() > 0) {
                sb.append("\n");
            }
            sb.append(fileName);
            sb.append(", ");
            Class type = this.assetTypes.get(fileName);
            Array<String> dependencies = this.assetDependencies.get(fileName);
            sb.append(ClassReflection.getSimpleName(type));
            sb.append(", refs: ");
            sb.append(((RefCountedContainer) this.assets.get(type).get(fileName)).getRefCount());
            if (dependencies != null) {
                sb.append(", deps: [");
                Iterator<String> it2 = dependencies.iterator();
                while (it2.hasNext()) {
                    sb.append(it2.next());
                    sb.append(",");
                }
                sb.append("]");
            }
        }
        return sb.toString();
    }

    public synchronized Array<String> getAssetNames() {
        return this.assetTypes.keys().toArray();
    }

    public synchronized Array<String> getDependencies(String fileName) {
        return this.assetDependencies.get(fileName);
    }

    public synchronized Class getAssetType(String fileName) {
        return this.assetTypes.get(fileName);
    }
}
