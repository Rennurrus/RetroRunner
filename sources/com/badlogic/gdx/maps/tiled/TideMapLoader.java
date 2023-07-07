package com.badlogic.gdx.maps.tiled;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.ImageResolver;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.XmlReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;

public class TideMapLoader extends SynchronousAssetLoader<TiledMap, Parameters> {
    private XmlReader.Element root;
    private XmlReader xml = new XmlReader();

    public static class Parameters extends AssetLoaderParameters<TiledMap> {
    }

    public TideMapLoader() {
        super(new InternalFileHandleResolver());
    }

    public TideMapLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    public TiledMap load(String fileName) {
        try {
            FileHandle tideFile = resolve(fileName);
            this.root = this.xml.parse(tideFile);
            ObjectMap<String, Texture> textures = new ObjectMap<>();
            Iterator<FileHandle> it = loadTileSheets(this.root, tideFile).iterator();
            while (it.hasNext()) {
                FileHandle textureFile = it.next();
                textures.put(textureFile.path(), new Texture(textureFile));
            }
            TiledMap map = loadMap(this.root, tideFile, new ImageResolver.DirectImageResolver(textures));
            map.setOwnedResources(textures.values().toArray());
            return map;
        } catch (IOException e) {
            throw new GdxRuntimeException("Couldn't load tilemap '" + fileName + "'", e);
        }
    }

    public TiledMap load(AssetManager assetManager, String fileName, FileHandle tideFile, Parameters parameter) {
        try {
            return loadMap(this.root, tideFile, new ImageResolver.AssetManagerImageResolver(assetManager));
        } catch (Exception e) {
            throw new GdxRuntimeException("Couldn't load tilemap '" + fileName + "'", e);
        }
    }

    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle tmxFile, Parameters parameter) {
        Array<AssetDescriptor> dependencies = new Array<>();
        try {
            this.root = this.xml.parse(tmxFile);
            Iterator<FileHandle> it = loadTileSheets(this.root, tmxFile).iterator();
            while (it.hasNext()) {
                dependencies.add(new AssetDescriptor(it.next().path(), Texture.class));
            }
            return dependencies;
        } catch (IOException e) {
            throw new GdxRuntimeException("Couldn't load tilemap '" + fileName + "'", e);
        }
    }

    private TiledMap loadMap(XmlReader.Element root2, FileHandle tmxFile, ImageResolver imageResolver) {
        TiledMap map = new TiledMap();
        XmlReader.Element properties = root2.getChildByName("Properties");
        if (properties != null) {
            loadProperties(map.getProperties(), properties);
        }
        Iterator<XmlReader.Element> it = root2.getChildByName("TileSheets").getChildrenByName("TileSheet").iterator();
        while (it.hasNext()) {
            loadTileSheet(map, it.next(), tmxFile, imageResolver);
        }
        Iterator<XmlReader.Element> it2 = root2.getChildByName("Layers").getChildrenByName("Layer").iterator();
        while (it2.hasNext()) {
            loadLayer(map, it2.next());
        }
        return map;
    }

    private Array<FileHandle> loadTileSheets(XmlReader.Element root2, FileHandle tideFile) throws IOException {
        Array<FileHandle> images = new Array<>();
        Iterator<XmlReader.Element> it = root2.getChildByName("TileSheets").getChildrenByName("TileSheet").iterator();
        while (it.hasNext()) {
            images.add(getRelativeFileHandle(tideFile, it.next().getChildByName("ImageSource").getText()));
        }
        return images;
    }

    private void loadTileSheet(TiledMap map, XmlReader.Element element, FileHandle tideFile, ImageResolver imageResolver) {
        XmlReader.Element element2 = element;
        if (element.getName().equals("TileSheet")) {
            String id = element2.getAttribute("Id");
            String description = element2.getChildByName("Description").getText();
            String imageSource = element2.getChildByName("ImageSource").getText();
            XmlReader.Element alignment = element2.getChildByName("Alignment");
            String sheetSize = alignment.getAttribute("SheetSize");
            String tileSize = alignment.getAttribute("TileSize");
            String margin = alignment.getAttribute("Margin");
            String spacing = alignment.getAttribute("Spacing");
            String[] sheetSizeParts = sheetSize.split(" x ");
            int parseInt = Integer.parseInt(sheetSizeParts[0]);
            int parseInt2 = Integer.parseInt(sheetSizeParts[1]);
            String[] tileSizeParts = tileSize.split(" x ");
            int tileSizeX = Integer.parseInt(tileSizeParts[0]);
            int tileSizeY = Integer.parseInt(tileSizeParts[1]);
            String[] marginParts = margin.split(" x ");
            int marginX = Integer.parseInt(marginParts[0]);
            int marginY = Integer.parseInt(marginParts[1]);
            String[] spacingParts = margin.split(" x ");
            int spacingX = Integer.parseInt(spacingParts[0]);
            int spacingY = Integer.parseInt(spacingParts[1]);
            String str = description;
            String str2 = imageSource;
            TextureRegion texture = imageResolver.getImage(getRelativeFileHandle(tideFile, imageSource).path());
            TiledMapTileSets tilesets = map.getTileSets();
            Iterator<TiledMapTileSet> it = tilesets.iterator();
            int firstgid = 1;
            while (it.hasNext() != 0) {
                firstgid += it.next().size();
            }
            XmlReader.Element element3 = alignment;
            TiledMapTileSet tileset = new TiledMapTileSet();
            tileset.setName(id);
            String str3 = id;
            String str4 = sheetSize;
            String str5 = tileSize;
            tileset.getProperties().put("firstgid", Integer.valueOf(firstgid));
            int stopWidth = texture.getRegionWidth() - tileSizeX;
            int stopHeight = texture.getRegionHeight() - tileSizeY;
            int gid = firstgid;
            int y = marginY;
            while (y <= stopHeight) {
                int stopHeight2 = stopHeight;
                String margin2 = margin;
                int gid2 = gid;
                int x = marginX;
                while (x <= stopWidth) {
                    int stopWidth2 = stopWidth;
                    TiledMapTile tile = new StaticTiledMapTile(new TextureRegion(texture, x, y, tileSizeX, tileSizeY));
                    tile.setId(gid2);
                    tileset.putTile(gid2, tile);
                    x += tileSizeX + spacingX;
                    gid2++;
                    stopWidth = stopWidth2;
                    spacing = spacing;
                }
                String str6 = spacing;
                y += tileSizeY + spacingY;
                gid = gid2;
                stopHeight = stopHeight2;
                margin = margin2;
                stopWidth = stopWidth;
            }
            int i = stopHeight;
            String str7 = margin;
            String str8 = spacing;
            XmlReader.Element properties = element2.getChildByName("Properties");
            if (properties != null) {
                loadProperties(tileset.getProperties(), properties);
            }
            tilesets.addTileSet(tileset);
            return;
        }
    }

    private void loadLayer(TiledMap map, XmlReader.Element element) {
        Array<XmlReader.Element> rows;
        XmlReader.Element tileArray;
        int layerSizeY;
        String str;
        TiledMapTileSet currentTileSet;
        Array<StaticTiledMapTile> frameTiles;
        XmlReader.Element element2 = element;
        if (element.getName().equals("Layer")) {
            String id = element2.getAttribute("Id");
            String visible = element2.getAttribute("Visible");
            XmlReader.Element dimensions = element2.getChildByName("Dimensions");
            String layerSize = dimensions.getAttribute("LayerSize");
            String tileSize = dimensions.getAttribute("TileSize");
            String[] layerSizeParts = layerSize.split(" x ");
            int layerSizeX = Integer.parseInt(layerSizeParts[0]);
            int layerSizeY2 = Integer.parseInt(layerSizeParts[1]);
            String[] tileSizeParts = tileSize.split(" x ");
            int tileSizeX = Integer.parseInt(tileSizeParts[0]);
            int tileSizeY = Integer.parseInt(tileSizeParts[1]);
            TiledMapTileLayer layer = new TiledMapTileLayer(layerSizeX, layerSizeY2, tileSizeX, tileSizeY);
            layer.setName(id);
            layer.setVisible(visible.equalsIgnoreCase("True"));
            XmlReader.Element tileArray2 = element2.getChildByName("TileArray");
            Array<XmlReader.Element> rows2 = tileArray2.getChildrenByName("Row");
            TiledMapTileSets tilesets = map.getTileSets();
            TiledMapTileSet currentTileSet2 = null;
            int firstgid = 0;
            String str2 = id;
            int y = rows2.size;
            String str3 = visible;
            int row = 0;
            while (row < y) {
                XmlReader.Element dimensions2 = dimensions;
                XmlReader.Element currentRow = rows2.get(row);
                int rowCount = y;
                int y2 = (y - 1) - row;
                String layerSize2 = layerSize;
                int childCount = currentRow.getChildCount();
                String tileSize2 = tileSize;
                int child = 0;
                String[] layerSizeParts2 = layerSizeParts;
                TiledMapTileSet currentTileSet3 = currentTileSet2;
                String[] tileSizeParts2 = tileSizeParts;
                int x = 0;
                while (child < childCount) {
                    int childCount2 = childCount;
                    XmlReader.Element currentChild = currentRow.getChild(child);
                    XmlReader.Element currentRow2 = currentRow;
                    String name = currentChild.getName();
                    int tileSizeX2 = tileSizeX;
                    String str4 = "TileSheet";
                    int layerSizeX2 = layerSizeX;
                    int tileSizeY2 = tileSizeY;
                    if (name.equals(str4)) {
                        currentTileSet3 = tilesets.getTileSet(currentChild.getAttribute("Ref"));
                        firstgid = ((Integer) currentTileSet3.getProperties().get("firstgid", Integer.class)).intValue();
                        layerSizeY = layerSizeY2;
                        tileArray = tileArray2;
                        rows = rows2;
                    } else {
                        layerSizeY = layerSizeY2;
                        if (name.equals("Null")) {
                            x += currentChild.getIntAttribute("Count");
                            tileArray = tileArray2;
                            rows = rows2;
                        } else {
                            tileArray = tileArray2;
                            if (name.equals("Static")) {
                                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                                cell.setTile(currentTileSet3.getTile(firstgid + currentChild.getIntAttribute("Index")));
                                layer.setCell(x, y2, cell);
                                x++;
                                rows = rows2;
                            } else {
                                TiledMapTileSet currentTileSet4 = currentTileSet3;
                                if (name.equals("Animated")) {
                                    int interval = currentChild.getInt("Interval");
                                    String str5 = name;
                                    XmlReader.Element frames = currentChild.getChildByName("Frames");
                                    Array<StaticTiledMapTile> frameTiles2 = new Array<>();
                                    XmlReader.Element element3 = currentChild;
                                    int frameChildCount = frames.getChildCount();
                                    rows = rows2;
                                    TiledMapTileSet currentTileSet5 = currentTileSet4;
                                    int frameChild = 0;
                                    while (frameChild < frameChildCount) {
                                        int frameChildCount2 = frameChildCount;
                                        XmlReader.Element frame = frames.getChild(frameChild);
                                        XmlReader.Element frames2 = frames;
                                        String frameName = frame.getName();
                                        if (frameName.equals(str4)) {
                                            str = str4;
                                            TiledMapTileSet currentTileSet6 = tilesets.getTileSet(frame.getAttribute("Ref"));
                                            currentTileSet = currentTileSet6;
                                            firstgid = ((Integer) currentTileSet6.getProperties().get("firstgid", Integer.class)).intValue();
                                            frameTiles = frameTiles2;
                                        } else {
                                            str = str4;
                                            if (frameName.equals("Static")) {
                                                currentTileSet = currentTileSet5;
                                                frameTiles = frameTiles2;
                                                frameTiles.add((StaticTiledMapTile) currentTileSet5.getTile(firstgid + frame.getIntAttribute("Index")));
                                            } else {
                                                currentTileSet = currentTileSet5;
                                                frameTiles = frameTiles2;
                                            }
                                        }
                                        frameChild++;
                                        frameTiles2 = frameTiles;
                                        currentTileSet5 = currentTileSet;
                                        frameChildCount = frameChildCount2;
                                        frames = frames2;
                                        str4 = str;
                                    }
                                    XmlReader.Element element4 = frames;
                                    int i = frameChildCount;
                                    TiledMapTileLayer.Cell cell2 = new TiledMapTileLayer.Cell();
                                    cell2.setTile(new AnimatedTiledMapTile(((float) interval) / 1000.0f, frameTiles2));
                                    layer.setCell(x, y2, cell2);
                                    x++;
                                    currentTileSet3 = currentTileSet5;
                                } else {
                                    XmlReader.Element element5 = currentChild;
                                    rows = rows2;
                                    currentTileSet3 = currentTileSet4;
                                }
                            }
                        }
                    }
                    child++;
                    XmlReader.Element element6 = element;
                    childCount = childCount2;
                    currentRow = currentRow2;
                    tileSizeX = tileSizeX2;
                    layerSizeX = layerSizeX2;
                    tileSizeY = tileSizeY2;
                    layerSizeY2 = layerSizeY;
                    tileArray2 = tileArray;
                    rows2 = rows;
                }
                int i2 = childCount;
                currentTileSet2 = currentTileSet3;
                int i3 = tileSizeX;
                int i4 = layerSizeX;
                int i5 = tileSizeY;
                int i6 = layerSizeY2;
                XmlReader.Element element7 = tileArray2;
                Array<XmlReader.Element> array = rows2;
                row++;
                XmlReader.Element element8 = element;
                tileSizeParts = tileSizeParts2;
                dimensions = dimensions2;
                y = rowCount;
                layerSizeParts = layerSizeParts2;
                layerSize = layerSize2;
                tileSize = tileSize2;
            }
            int rowCount2 = y;
            XmlReader.Element element9 = dimensions;
            String str6 = layerSize;
            String str7 = tileSize;
            String[] strArr = tileSizeParts;
            String[] strArr2 = layerSizeParts;
            int i7 = tileSizeX;
            int i8 = layerSizeX;
            int i9 = tileSizeY;
            int i10 = layerSizeY2;
            XmlReader.Element element10 = tileArray2;
            Array<XmlReader.Element> array2 = rows2;
            XmlReader.Element properties = element.getChildByName("Properties");
            if (properties != null) {
                loadProperties(layer.getProperties(), properties);
            }
            map.getLayers().add(layer);
            return;
        }
        XmlReader.Element element11 = element2;
    }

    private void loadProperties(MapProperties properties, XmlReader.Element element) {
        if (element.getName().equals("Properties")) {
            Iterator<XmlReader.Element> it = element.getChildrenByName("Property").iterator();
            while (it.hasNext()) {
                XmlReader.Element property = it.next();
                String key = property.getAttribute("Key", (String) null);
                String type = property.getAttribute("Type", (String) null);
                String value = property.getText();
                if (type.equals("Int32")) {
                    properties.put(key, Integer.valueOf(Integer.parseInt(value)));
                } else if (type.equals("String")) {
                    properties.put(key, value);
                } else if (type.equals("Boolean")) {
                    properties.put(key, Boolean.valueOf(value.equalsIgnoreCase("true")));
                } else {
                    properties.put(key, value);
                }
            }
        }
    }

    private static FileHandle getRelativeFileHandle(FileHandle file, String path) {
        StringTokenizer tokenizer = new StringTokenizer(path, "\\/");
        FileHandle result = file.parent();
        while (tokenizer.hasMoreElements()) {
            String token = tokenizer.nextToken();
            if (token.equals("..")) {
                result = result.parent();
            } else {
                result = result.child(token);
            }
        }
        return result;
    }
}
