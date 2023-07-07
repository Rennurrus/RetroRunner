package com.badlogic.gdx.maps.tiled;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.ImageResolver;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.BaseTmxMapLoader;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.XmlReader;
import java.util.Iterator;

public class TmxMapLoader extends BaseTmxMapLoader<Parameters> {

    public static class Parameters extends BaseTmxMapLoader.Parameters {
    }

    public TmxMapLoader() {
        super(new InternalFileHandleResolver());
    }

    public TmxMapLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    public TiledMap load(String fileName) {
        return load(fileName, new Parameters());
    }

    public TiledMap load(String fileName, Parameters parameter) {
        FileHandle tmxFile = resolve(fileName);
        this.root = this.xml.parse(tmxFile);
        ObjectMap<String, Texture> textures = new ObjectMap<>();
        Iterator<FileHandle> it = getDependencyFileHandles(tmxFile).iterator();
        while (it.hasNext()) {
            FileHandle textureFile = it.next();
            Texture texture = new Texture(textureFile, parameter.generateMipMaps);
            texture.setFilter(parameter.textureMinFilter, parameter.textureMagFilter);
            textures.put(textureFile.path(), texture);
        }
        TiledMap map = loadTiledMap(tmxFile, parameter, new ImageResolver.DirectImageResolver(textures));
        map.setOwnedResources(textures.values().toArray());
        return map;
    }

    public void loadAsync(AssetManager manager, String fileName, FileHandle tmxFile, Parameters parameter) {
        this.map = loadTiledMap(tmxFile, parameter, new ImageResolver.AssetManagerImageResolver(manager));
    }

    public TiledMap loadSync(AssetManager manager, String fileName, FileHandle file, Parameters parameter) {
        return this.map;
    }

    /* access modifiers changed from: protected */
    public Array<AssetDescriptor> getDependencyAssetDescriptors(FileHandle tmxFile, TextureLoader.TextureParameter textureParameter) {
        Array<AssetDescriptor> descriptors = new Array<>();
        Iterator<FileHandle> it = getDependencyFileHandles(tmxFile).iterator();
        while (it.hasNext()) {
            descriptors.add(new AssetDescriptor(it.next(), Texture.class, textureParameter));
        }
        return descriptors;
    }

    private Array<FileHandle> getDependencyFileHandles(FileHandle tmxFile) {
        Array<FileHandle> fileHandles = new Array<>();
        Iterator<XmlReader.Element> it = this.root.getChildrenByName("tileset").iterator();
        while (it.hasNext()) {
            XmlReader.Element tileset = it.next();
            String source = tileset.getAttribute("source", (String) null);
            if (source != null) {
                FileHandle tsxFile = getRelativeFileHandle(tmxFile, source);
                XmlReader.Element tileset2 = this.xml.parse(tsxFile);
                if (tileset2.getChildByName("image") != null) {
                    fileHandles.add(getRelativeFileHandle(tsxFile, tileset2.getChildByName("image").getAttribute("source")));
                } else {
                    Iterator<XmlReader.Element> it2 = tileset2.getChildrenByName("tile").iterator();
                    while (it2.hasNext()) {
                        fileHandles.add(getRelativeFileHandle(tsxFile, it2.next().getChildByName("image").getAttribute("source")));
                    }
                }
            } else if (tileset.getChildByName("image") != null) {
                fileHandles.add(getRelativeFileHandle(tmxFile, tileset.getChildByName("image").getAttribute("source")));
            } else {
                Iterator<XmlReader.Element> it3 = tileset.getChildrenByName("tile").iterator();
                while (it3.hasNext()) {
                    fileHandles.add(getRelativeFileHandle(tmxFile, it3.next().getChildByName("image").getAttribute("source")));
                }
            }
        }
        Iterator<XmlReader.Element> it4 = this.root.getChildrenByName("imagelayer").iterator();
        while (it4.hasNext()) {
            String source2 = it4.next().getChildByName("image").getAttribute("source", (String) null);
            if (source2 != null) {
                fileHandles.add(getRelativeFileHandle(tmxFile, source2));
            }
        }
        return fileHandles;
    }

    /* access modifiers changed from: protected */
    public void addStaticTiles(FileHandle tmxFile, ImageResolver imageResolver, TiledMapTileSet tileSet, XmlReader.Element element, Array<XmlReader.Element> tileElements, String name, int firstgid, int tilewidth, int tileheight, int spacing, int margin, String source, int offsetX, int offsetY, String imageSource, int imageWidth, int imageHeight, FileHandle image) {
        FileHandle fileHandle = tmxFile;
        ImageResolver imageResolver2 = imageResolver;
        String str = source;
        int i = offsetX;
        int i2 = offsetY;
        MapProperties props = tileSet.getProperties();
        if (image != null) {
            TextureRegion texture = imageResolver2.getImage(image.path());
            props.put("imagesource", imageSource);
            props.put("imagewidth", Integer.valueOf(imageWidth));
            props.put("imageheight", Integer.valueOf(imageHeight));
            props.put("tilewidth", Integer.valueOf(tilewidth));
            props.put("tileheight", Integer.valueOf(tileheight));
            props.put("margin", Integer.valueOf(margin));
            props.put("spacing", Integer.valueOf(spacing));
            int stopWidth = texture.getRegionWidth() - tilewidth;
            int stopHeight = texture.getRegionHeight() - tileheight;
            int id = firstgid;
            int y = margin;
            while (y <= stopHeight) {
                int id2 = id;
                int x = margin;
                while (x <= stopWidth) {
                    addStaticTiledMapTile(tileSet, new TextureRegion(texture, x, y, tilewidth, tileheight), id2, (float) i, (float) i2);
                    x += tilewidth + spacing;
                    id2++;
                    y = y;
                }
                int i3 = x;
                y += tileheight + spacing;
                id = id2;
            }
            int i4 = y;
            FileHandle fileHandle2 = image;
            return;
        }
        String str2 = imageSource;
        Iterator<XmlReader.Element> it = tileElements.iterator();
        FileHandle image2 = image;
        while (it.hasNext()) {
            XmlReader.Element tileElement = it.next();
            XmlReader.Element imageElement = tileElement.getChildByName("image");
            if (imageElement != null) {
                String imageSource2 = imageElement.getAttribute("source");
                if (str != null) {
                    image2 = getRelativeFileHandle(getRelativeFileHandle(fileHandle, str), imageSource2);
                    String str3 = imageSource2;
                } else {
                    image2 = getRelativeFileHandle(fileHandle, imageSource2);
                    String str4 = imageSource2;
                }
            }
            addStaticTiledMapTile(tileSet, imageResolver2.getImage(image2.path()), firstgid + tileElement.getIntAttribute("id"), (float) i, (float) i2);
        }
    }
}
