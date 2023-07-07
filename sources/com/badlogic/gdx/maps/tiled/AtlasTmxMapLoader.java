package com.badlogic.gdx.maps.tiled;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.ImageResolver;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.BaseTmxMapLoader;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.XmlReader;
import java.util.Iterator;

public class AtlasTmxMapLoader extends BaseTmxMapLoader<AtlasTiledMapLoaderParameters> {
    protected AtlasResolver atlasResolver;
    protected Array<Texture> trackedTextures = new Array<>();

    public static class AtlasTiledMapLoaderParameters extends BaseTmxMapLoader.Parameters {
        public boolean forceTextureFilters = false;
    }

    private interface AtlasResolver extends ImageResolver {
        TextureAtlas getAtlas();

        public static class DirectAtlasResolver implements AtlasResolver {
            private final TextureAtlas atlas;

            public DirectAtlasResolver(TextureAtlas atlas2) {
                this.atlas = atlas2;
            }

            public TextureAtlas getAtlas() {
                return this.atlas;
            }

            public TextureRegion getImage(String name) {
                return this.atlas.findRegion(name);
            }
        }

        public static class AssetManagerAtlasResolver implements AtlasResolver {
            private final AssetManager assetManager;
            private final String atlasName;

            public AssetManagerAtlasResolver(AssetManager assetManager2, String atlasName2) {
                this.assetManager = assetManager2;
                this.atlasName = atlasName2;
            }

            public TextureAtlas getAtlas() {
                return (TextureAtlas) this.assetManager.get(this.atlasName, TextureAtlas.class);
            }

            public TextureRegion getImage(String name) {
                return getAtlas().findRegion(name);
            }
        }
    }

    public AtlasTmxMapLoader() {
        super(new InternalFileHandleResolver());
    }

    public AtlasTmxMapLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    public TiledMap load(String fileName) {
        return load(fileName, new AtlasTiledMapLoaderParameters());
    }

    public TiledMap load(String fileName, AtlasTiledMapLoaderParameters parameter) {
        FileHandle tmxFile = resolve(fileName);
        this.root = this.xml.parse(tmxFile);
        TextureAtlas atlas = new TextureAtlas(getAtlasFileHandle(tmxFile));
        this.atlasResolver = new AtlasResolver.DirectAtlasResolver(atlas);
        TiledMap map = loadTiledMap(tmxFile, parameter, this.atlasResolver);
        map.setOwnedResources(new Array((T[]) new TextureAtlas[]{atlas}));
        setTextureFilters(parameter.textureMinFilter, parameter.textureMagFilter);
        return map;
    }

    public void loadAsync(AssetManager manager, String fileName, FileHandle tmxFile, AtlasTiledMapLoaderParameters parameter) {
        this.atlasResolver = new AtlasResolver.AssetManagerAtlasResolver(manager, getAtlasFileHandle(tmxFile).path());
        this.map = loadTiledMap(tmxFile, parameter, this.atlasResolver);
    }

    public TiledMap loadSync(AssetManager manager, String fileName, FileHandle file, AtlasTiledMapLoaderParameters parameter) {
        if (parameter != null) {
            setTextureFilters(parameter.textureMinFilter, parameter.textureMagFilter);
        }
        return this.map;
    }

    /* access modifiers changed from: protected */
    public Array<AssetDescriptor> getDependencyAssetDescriptors(FileHandle tmxFile, TextureLoader.TextureParameter textureParameter) {
        Array<AssetDescriptor> descriptors = new Array<>();
        FileHandle atlasFileHandle = getAtlasFileHandle(tmxFile);
        if (atlasFileHandle != null) {
            descriptors.add(new AssetDescriptor(atlasFileHandle, TextureAtlas.class));
        }
        return descriptors;
    }

    /* access modifiers changed from: protected */
    public void addStaticTiles(FileHandle tmxFile, ImageResolver imageResolver, TiledMapTileSet tileSet, XmlReader.Element element, Array<XmlReader.Element> tileElements, String name, int firstgid, int tilewidth, int tileheight, int spacing, int margin, String source, int offsetX, int offsetY, String imageSource, int imageWidth, int imageHeight, FileHandle image) {
        int i = firstgid;
        int i2 = offsetX;
        int i3 = offsetY;
        String str = imageSource;
        TextureAtlas atlas = this.atlasResolver.getAtlas();
        String regionsName = name;
        ObjectSet.ObjectSetIterator<Texture> it = atlas.getTextures().iterator();
        while (it.hasNext()) {
            this.trackedTextures.add((Texture) it.next());
        }
        MapProperties props = tileSet.getProperties();
        props.put("imagesource", str);
        props.put("imagewidth", Integer.valueOf(imageWidth));
        props.put("imageheight", Integer.valueOf(imageHeight));
        props.put("tilewidth", Integer.valueOf(tilewidth));
        props.put("tileheight", Integer.valueOf(tileheight));
        props.put("margin", Integer.valueOf(margin));
        props.put("spacing", Integer.valueOf(spacing));
        if (str != null && imageSource.length() > 0) {
            int lastgid = (((imageWidth / tilewidth) * (imageHeight / tileheight)) + i) - 1;
            Iterator<TextureAtlas.AtlasRegion> it2 = atlas.findRegions(regionsName).iterator();
            while (it2.hasNext()) {
                TextureAtlas.AtlasRegion region = it2.next();
                if (region != null) {
                    int tileId = i + region.index;
                    if (tileId < i || tileId > lastgid) {
                        TextureAtlas.AtlasRegion atlasRegion = region;
                    } else {
                        int i4 = tileId;
                        TextureAtlas.AtlasRegion atlasRegion2 = region;
                        addStaticTiledMapTile(tileSet, region, tileId, (float) i2, (float) i3);
                    }
                }
            }
        }
        Iterator<XmlReader.Element> it3 = tileElements.iterator();
        while (it3.hasNext()) {
            XmlReader.Element tileElement = it3.next();
            int tileId2 = i + tileElement.getIntAttribute("id", 0);
            if (tileSet.getTile(tileId2) == null) {
                XmlReader.Element imageElement = tileElement.getChildByName("image");
                if (imageElement != null) {
                    String regionName = imageElement.getAttribute("source");
                    String regionName2 = regionName.substring(0, regionName.lastIndexOf(46));
                    TextureAtlas.AtlasRegion region2 = atlas.findRegion(regionName2);
                    if (region2 != null) {
                        String str2 = regionName2;
                        XmlReader.Element element2 = imageElement;
                        int i5 = tileId2;
                        addStaticTiledMapTile(tileSet, region2, tileId2, (float) i2, (float) i3);
                    } else {
                        XmlReader.Element element3 = imageElement;
                        int i6 = tileId2;
                        throw new GdxRuntimeException("Tileset atlasRegion not found: " + regionName2);
                    }
                } else {
                    int i7 = tileId2;
                }
            }
        }
    }

    private FileHandle getAtlasFileHandle(FileHandle tmxFile) {
        XmlReader.Element properties = this.root.getChildByName("properties");
        String atlasFilePath = null;
        if (properties != null) {
            Iterator<XmlReader.Element> it = properties.getChildrenByName("property").iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                XmlReader.Element property = it.next();
                if (property.getAttribute("name").startsWith("atlas")) {
                    atlasFilePath = property.getAttribute("value");
                    break;
                }
            }
        }
        if (atlasFilePath != null) {
            FileHandle fileHandle = getRelativeFileHandle(tmxFile, atlasFilePath);
            if (fileHandle.exists()) {
                return fileHandle;
            }
            throw new GdxRuntimeException("The 'atlas' file could not be found: '" + atlasFilePath + "'");
        }
        throw new GdxRuntimeException("The map is missing the 'atlas' property");
    }

    private void setTextureFilters(Texture.TextureFilter min, Texture.TextureFilter mag) {
        Iterator<Texture> it = this.trackedTextures.iterator();
        while (it.hasNext()) {
            it.next().setFilter(min, mag);
        }
        this.trackedTextures.clear();
    }
}
