package com.badlogic.gdx.graphics;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.glutils.FileTextureData;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Texture extends GLTexture {
    private static AssetManager assetManager;
    static final Map<Application, Array<Texture>> managedTextures = new HashMap();
    TextureData data;

    public enum TextureFilter {
        Nearest(GL20.GL_NEAREST),
        Linear(GL20.GL_LINEAR),
        MipMap(GL20.GL_LINEAR_MIPMAP_LINEAR),
        MipMapNearestNearest(GL20.GL_NEAREST_MIPMAP_NEAREST),
        MipMapLinearNearest(GL20.GL_LINEAR_MIPMAP_NEAREST),
        MipMapNearestLinear(GL20.GL_NEAREST_MIPMAP_LINEAR),
        MipMapLinearLinear(GL20.GL_LINEAR_MIPMAP_LINEAR);
        
        final int glEnum;

        private TextureFilter(int glEnum2) {
            this.glEnum = glEnum2;
        }

        public boolean isMipMap() {
            int i = this.glEnum;
            return (i == 9728 || i == 9729) ? false : true;
        }

        public int getGLEnum() {
            return this.glEnum;
        }
    }

    public enum TextureWrap {
        MirroredRepeat(GL20.GL_MIRRORED_REPEAT),
        ClampToEdge(GL20.GL_CLAMP_TO_EDGE),
        Repeat(GL20.GL_REPEAT);
        
        final int glEnum;

        private TextureWrap(int glEnum2) {
            this.glEnum = glEnum2;
        }

        public int getGLEnum() {
            return this.glEnum;
        }
    }

    public Texture(String internalPath) {
        this(Gdx.files.internal(internalPath));
    }

    public Texture(FileHandle file) {
        this(file, (Pixmap.Format) null, false);
    }

    public Texture(FileHandle file, boolean useMipMaps) {
        this(file, (Pixmap.Format) null, useMipMaps);
    }

    public Texture(FileHandle file, Pixmap.Format format, boolean useMipMaps) {
        this(TextureData.Factory.loadFromFile(file, format, useMipMaps));
    }

    public Texture(Pixmap pixmap) {
        this((TextureData) new PixmapTextureData(pixmap, (Pixmap.Format) null, false, false));
    }

    public Texture(Pixmap pixmap, boolean useMipMaps) {
        this((TextureData) new PixmapTextureData(pixmap, (Pixmap.Format) null, useMipMaps, false));
    }

    public Texture(Pixmap pixmap, Pixmap.Format format, boolean useMipMaps) {
        this((TextureData) new PixmapTextureData(pixmap, format, useMipMaps, false));
    }

    public Texture(int width, int height, Pixmap.Format format) {
        this((TextureData) new PixmapTextureData(new Pixmap(width, height, format), (Pixmap.Format) null, false, true));
    }

    public Texture(TextureData data2) {
        this((int) GL20.GL_TEXTURE_2D, Gdx.gl.glGenTexture(), data2);
    }

    protected Texture(int glTarget, int glHandle, TextureData data2) {
        super(glTarget, glHandle);
        load(data2);
        if (data2.isManaged()) {
            addManagedTexture(Gdx.app, this);
        }
    }

    public void load(TextureData data2) {
        if (this.data == null || data2.isManaged() == this.data.isManaged()) {
            this.data = data2;
            if (!data2.isPrepared()) {
                data2.prepare();
            }
            bind();
            uploadImageData(GL20.GL_TEXTURE_2D, data2);
            unsafeSetFilter(this.minFilter, this.magFilter, true);
            unsafeSetWrap(this.uWrap, this.vWrap, true);
            Gdx.gl.glBindTexture(this.glTarget, 0);
            return;
        }
        throw new GdxRuntimeException("New data must have the same managed status as the old data");
    }

    /* access modifiers changed from: protected */
    public void reload() {
        if (isManaged()) {
            this.glHandle = Gdx.gl.glGenTexture();
            load(this.data);
            return;
        }
        throw new GdxRuntimeException("Tried to reload unmanaged Texture");
    }

    public void draw(Pixmap pixmap, int x, int y) {
        if (!this.data.isManaged()) {
            bind();
            Gdx.gl.glTexSubImage2D(this.glTarget, 0, x, y, pixmap.getWidth(), pixmap.getHeight(), pixmap.getGLFormat(), pixmap.getGLType(), pixmap.getPixels());
            return;
        }
        throw new GdxRuntimeException("can't draw to a managed texture");
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

    public TextureData getTextureData() {
        return this.data;
    }

    public boolean isManaged() {
        return this.data.isManaged();
    }

    public void dispose() {
        if (this.glHandle != 0) {
            delete();
            if (this.data.isManaged() && managedTextures.get(Gdx.app) != null) {
                managedTextures.get(Gdx.app).removeValue(this, true);
            }
        }
    }

    public String toString() {
        TextureData textureData = this.data;
        if (textureData instanceof FileTextureData) {
            return textureData.toString();
        }
        return super.toString();
    }

    private static void addManagedTexture(Application app, Texture texture) {
        Array<Texture> managedTextureArray = managedTextures.get(app);
        if (managedTextureArray == null) {
            managedTextureArray = new Array<>();
        }
        managedTextureArray.add(texture);
        managedTextures.put(app, managedTextureArray);
    }

    public static void clearAllTextures(Application app) {
        managedTextures.remove(app);
    }

    public static void invalidateAllTextures(Application app) {
        Array<Texture> managedTextureArray = managedTextures.get(app);
        if (managedTextureArray != null) {
            AssetManager assetManager2 = assetManager;
            if (assetManager2 == null) {
                for (int i = 0; i < managedTextureArray.size; i++) {
                    managedTextureArray.get(i).reload();
                }
                return;
            }
            assetManager2.finishLoading();
            Array<Texture> textures = new Array<>(managedTextureArray);
            Iterator<Texture> it = textures.iterator();
            while (it.hasNext()) {
                Texture texture = it.next();
                String fileName = assetManager.getAssetFileName(texture);
                if (fileName == null) {
                    texture.reload();
                } else {
                    final int refCount = assetManager.getReferenceCount(fileName);
                    assetManager.setReferenceCount(fileName, 0);
                    texture.glHandle = 0;
                    TextureLoader.TextureParameter params = new TextureLoader.TextureParameter();
                    params.textureData = texture.getTextureData();
                    params.minFilter = texture.getMinFilter();
                    params.magFilter = texture.getMagFilter();
                    params.wrapU = texture.getUWrap();
                    params.wrapV = texture.getVWrap();
                    params.genMipMaps = texture.data.useMipMaps();
                    params.texture = texture;
                    params.loadedCallback = new AssetLoaderParameters.LoadedCallback() {
                        public void finishedLoading(AssetManager assetManager, String fileName, Class type) {
                            assetManager.setReferenceCount(fileName, refCount);
                        }
                    };
                    assetManager.unload(fileName);
                    texture.glHandle = Gdx.gl.glGenTexture();
                    assetManager.load(fileName, Texture.class, params);
                }
            }
            managedTextureArray.clear();
            managedTextureArray.addAll((Array<? extends Texture>) textures);
        }
    }

    public static void setAssetManager(AssetManager manager) {
        assetManager = manager;
    }

    public static String getManagedStatus() {
        StringBuilder builder = new StringBuilder();
        builder.append("Managed textures/app: { ");
        for (Application app : managedTextures.keySet()) {
            builder.append(managedTextures.get(app).size);
            builder.append(" ");
        }
        builder.append("}");
        return builder.toString();
    }

    public static int getNumManagedTextures() {
        return managedTextures.get(Gdx.app).size;
    }
}
