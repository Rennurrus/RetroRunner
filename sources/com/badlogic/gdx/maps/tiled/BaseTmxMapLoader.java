package com.badlogic.gdx.maps.tiled;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.ImageResolver;
import com.badlogic.gdx.maps.MapGroupLayer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.BaseTmxMapLoader.Parameters;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.SerializationException;
import com.badlogic.gdx.utils.StreamUtils;
import com.badlogic.gdx.utils.XmlReader;
import com.twi.game.BuildConfig;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

public abstract class BaseTmxMapLoader<P extends Parameters> extends AsynchronousAssetLoader<TiledMap, P> {
    protected static final int FLAG_FLIP_DIAGONALLY = 536870912;
    protected static final int FLAG_FLIP_HORIZONTALLY = Integer.MIN_VALUE;
    protected static final int FLAG_FLIP_VERTICALLY = 1073741824;
    protected static final int MASK_CLEAR = -536870912;
    protected boolean convertObjectToTileSpace;
    protected boolean flipY = true;
    protected TiledMap map;
    protected int mapHeightInPixels;
    protected int mapTileHeight;
    protected int mapTileWidth;
    protected int mapWidthInPixels;
    protected XmlReader.Element root;
    protected XmlReader xml = new XmlReader();

    public static class Parameters extends AssetLoaderParameters<TiledMap> {
        public boolean convertObjectToTileSpace = false;
        public boolean flipY = true;
        public boolean generateMipMaps = false;
        public Texture.TextureFilter textureMagFilter = Texture.TextureFilter.Nearest;
        public Texture.TextureFilter textureMinFilter = Texture.TextureFilter.Nearest;
    }

    /* access modifiers changed from: protected */
    public abstract void addStaticTiles(FileHandle fileHandle, ImageResolver imageResolver, TiledMapTileSet tiledMapTileSet, XmlReader.Element element, Array<XmlReader.Element> array, String str, int i, int i2, int i3, int i4, int i5, String str2, int i6, int i7, String str3, int i8, int i9, FileHandle fileHandle2);

    /* access modifiers changed from: protected */
    public abstract Array<AssetDescriptor> getDependencyAssetDescriptors(FileHandle fileHandle, TextureLoader.TextureParameter textureParameter);

    public BaseTmxMapLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle tmxFile, P parameter) {
        this.root = this.xml.parse(tmxFile);
        TextureLoader.TextureParameter textureParameter = new TextureLoader.TextureParameter();
        if (parameter != null) {
            textureParameter.genMipMaps = parameter.generateMipMaps;
            textureParameter.minFilter = parameter.textureMinFilter;
            textureParameter.magFilter = parameter.textureMagFilter;
        }
        return getDependencyAssetDescriptors(tmxFile, textureParameter);
    }

    /* access modifiers changed from: protected */
    public TiledMap loadTiledMap(FileHandle tmxFile, P parameter, ImageResolver imageResolver) {
        P p = parameter;
        this.map = new TiledMap();
        if (p != null) {
            this.convertObjectToTileSpace = p.convertObjectToTileSpace;
            this.flipY = p.flipY;
        } else {
            this.convertObjectToTileSpace = false;
            this.flipY = true;
        }
        String mapOrientation = this.root.getAttribute("orientation", (String) null);
        int mapWidth = this.root.getIntAttribute("width", 0);
        int mapHeight = this.root.getIntAttribute("height", 0);
        int tileWidth = this.root.getIntAttribute("tilewidth", 0);
        int tileHeight = this.root.getIntAttribute("tileheight", 0);
        int hexSideLength = this.root.getIntAttribute("hexsidelength", 0);
        String staggerAxis = this.root.getAttribute("staggeraxis", (String) null);
        String str = "staggeraxis";
        String str2 = "staggerindex";
        String staggerIndex = this.root.getAttribute("staggerindex", (String) null);
        String mapBackgroundColor = this.root.getAttribute("backgroundcolor", (String) null);
        MapProperties mapProperties = this.map.getProperties();
        if (mapOrientation != null) {
            mapProperties.put("orientation", mapOrientation);
        }
        mapProperties.put("width", Integer.valueOf(mapWidth));
        mapProperties.put("height", Integer.valueOf(mapHeight));
        mapProperties.put("tilewidth", Integer.valueOf(tileWidth));
        mapProperties.put("tileheight", Integer.valueOf(tileHeight));
        mapProperties.put("hexsidelength", Integer.valueOf(hexSideLength));
        if (staggerAxis != null) {
            mapProperties.put(str, staggerAxis);
        }
        if (staggerIndex != null) {
            mapProperties.put(str2, staggerIndex);
        }
        if (mapBackgroundColor != null) {
            mapProperties.put("backgroundcolor", mapBackgroundColor);
        }
        this.mapTileWidth = tileWidth;
        this.mapTileHeight = tileHeight;
        this.mapWidthInPixels = mapWidth * tileWidth;
        this.mapHeightInPixels = mapHeight * tileHeight;
        if (mapOrientation != null && "staggered".equals(mapOrientation) && mapHeight > 1) {
            this.mapWidthInPixels += tileWidth / 2;
            this.mapHeightInPixels = (this.mapHeightInPixels / 2) + (tileHeight / 2);
        }
        XmlReader.Element properties = this.root.getChildByName("properties");
        if (properties != null) {
            loadProperties(this.map.getProperties(), properties);
        }
        for (Iterator<XmlReader.Element> it = this.root.getChildrenByName("tileset").iterator(); it.hasNext(); it = it) {
            XmlReader.Element element = it.next();
            loadTileSet(element, tmxFile, imageResolver);
            this.root.removeChild(element);
        }
        FileHandle fileHandle = tmxFile;
        ImageResolver imageResolver2 = imageResolver;
        int i = 0;
        int j = this.root.getChildCount();
        while (i < j) {
            String staggerAxis2 = staggerAxis;
            XmlReader.Element element2 = this.root.getChild(i);
            TiledMap tiledMap = this.map;
            loadLayer(tiledMap, tiledMap.getLayers(), element2, tmxFile, imageResolver);
            i++;
            staggerAxis = staggerAxis2;
            j = j;
            mapProperties = mapProperties;
            mapBackgroundColor = mapBackgroundColor;
        }
        int i2 = i;
        String str3 = staggerAxis;
        MapProperties mapProperties2 = mapProperties;
        int i3 = j;
        String str4 = mapBackgroundColor;
        return this.map;
    }

    /* access modifiers changed from: protected */
    public void loadLayer(TiledMap map2, MapLayers parentLayers, XmlReader.Element element, FileHandle tmxFile, ImageResolver imageResolver) {
        String name = element.getName();
        if (name.equals("group")) {
            loadLayerGroup(map2, parentLayers, element, tmxFile, imageResolver);
        } else if (name.equals("layer")) {
            loadTileLayer(map2, parentLayers, element);
        } else if (name.equals("objectgroup")) {
            loadObjectGroup(map2, parentLayers, element);
        } else if (name.equals("imagelayer")) {
            loadImageLayer(map2, parentLayers, element, tmxFile, imageResolver);
        }
    }

    /* access modifiers changed from: protected */
    public void loadLayerGroup(TiledMap map2, MapLayers parentLayers, XmlReader.Element element, FileHandle tmxFile, ImageResolver imageResolver) {
        XmlReader.Element element2 = element;
        if (element.getName().equals("group")) {
            MapGroupLayer groupLayer = new MapGroupLayer();
            loadBasicLayerInfo(groupLayer, element2);
            XmlReader.Element properties = element2.getChildByName("properties");
            if (properties != null) {
                loadProperties(groupLayer.getProperties(), properties);
            }
            int j = element.getChildCount();
            for (int i = 0; i < j; i++) {
                loadLayer(map2, groupLayer.getLayers(), element2.getChild(i), tmxFile, imageResolver);
            }
            Iterator<MapLayer> it = groupLayer.getLayers().iterator();
            while (it.hasNext()) {
                it.next().setParent(groupLayer);
            }
            MapLayers mapLayers = parentLayers;
            parentLayers.add(groupLayer);
            return;
        }
        MapLayers mapLayers2 = parentLayers;
    }

    /* access modifiers changed from: protected */
    public void loadTileLayer(TiledMap map2, MapLayers parentLayers, XmlReader.Element element) {
        int width;
        XmlReader.Element element2 = element;
        if (element.getName().equals("layer")) {
            int width2 = element2.getIntAttribute("width", 0);
            int height = element2.getIntAttribute("height", 0);
            TiledMapTileLayer layer = new TiledMapTileLayer(width2, height, ((Integer) map2.getProperties().get("tilewidth", Integer.class)).intValue(), ((Integer) map2.getProperties().get("tileheight", Integer.class)).intValue());
            loadBasicLayerInfo(layer, element2);
            int[] ids = getTileIds(element2, width2, height);
            TiledMapTileSets tilesets = map2.getTileSets();
            for (int y = 0; y < height; y++) {
                int x = 0;
                while (x < width2) {
                    int id = ids[(y * width2) + x];
                    boolean flipDiagonally = true;
                    boolean flipHorizontally = (FLAG_FLIP_HORIZONTALLY & id) != 0;
                    boolean flipVertically = (FLAG_FLIP_VERTICALLY & id) != 0;
                    if ((id & FLAG_FLIP_DIAGONALLY) == 0) {
                        flipDiagonally = false;
                    }
                    TiledMapTile tile = tilesets.getTile(id & 536870911);
                    if (tile != null) {
                        width = width2;
                        TiledMapTileLayer.Cell cell = createTileLayerCell(flipHorizontally, flipVertically, flipDiagonally);
                        cell.setTile(tile);
                        TiledMapTile tiledMapTile = tile;
                        layer.setCell(x, this.flipY ? (height - 1) - y : y, cell);
                    } else {
                        width = width2;
                    }
                    x++;
                    width2 = width;
                }
            }
            XmlReader.Element properties = element2.getChildByName("properties");
            if (properties != null) {
                loadProperties(layer.getProperties(), properties);
            }
            parentLayers.add(layer);
            return;
        }
        MapLayers mapLayers = parentLayers;
    }

    /* access modifiers changed from: protected */
    public void loadObjectGroup(TiledMap map2, MapLayers parentLayers, XmlReader.Element element) {
        if (element.getName().equals("objectgroup")) {
            MapLayer layer = new MapLayer();
            loadBasicLayerInfo(layer, element);
            XmlReader.Element properties = element.getChildByName("properties");
            if (properties != null) {
                loadProperties(layer.getProperties(), properties);
            }
            Iterator<XmlReader.Element> it = element.getChildrenByName("object").iterator();
            while (it.hasNext()) {
                loadObject(map2, layer, it.next());
            }
            parentLayers.add(layer);
        }
    }

    /* access modifiers changed from: protected */
    public void loadImageLayer(TiledMap map2, MapLayers parentLayers, XmlReader.Element element, FileHandle tmxFile, ImageResolver imageResolver) {
        float x;
        float y;
        if (element.getName().equals("imagelayer")) {
            if (element.hasAttribute("offsetx")) {
                x = Float.parseFloat(element.getAttribute("offsetx", "0"));
            } else {
                x = Float.parseFloat(element.getAttribute("x", "0"));
            }
            if (element.hasAttribute("offsety")) {
                y = Float.parseFloat(element.getAttribute("offsety", "0"));
            } else {
                y = Float.parseFloat(element.getAttribute("y", "0"));
            }
            if (this.flipY) {
                y = ((float) this.mapHeightInPixels) - y;
            }
            TextureRegion texture = null;
            XmlReader.Element image = element.getChildByName("image");
            if (image != null) {
                texture = imageResolver.getImage(getRelativeFileHandle(tmxFile, image.getAttribute("source")).path());
                y -= (float) texture.getRegionHeight();
            }
            TiledMapImageLayer layer = new TiledMapImageLayer(texture, x, y);
            loadBasicLayerInfo(layer, element);
            XmlReader.Element properties = element.getChildByName("properties");
            if (properties != null) {
                loadProperties(layer.getProperties(), properties);
            }
            parentLayers.add(layer);
        }
    }

    /* access modifiers changed from: protected */
    public void loadBasicLayerInfo(MapLayer layer, XmlReader.Element element) {
        String name = element.getAttribute("name", (String) null);
        float opacity = Float.parseFloat(element.getAttribute("opacity", BuildConfig.VERSION_NAME));
        boolean visible = true;
        if (element.getIntAttribute("visible", 1) != 1) {
            visible = false;
        }
        float offsetX = element.getFloatAttribute("offsetx", 0.0f);
        float offsetY = element.getFloatAttribute("offsety", 0.0f);
        layer.setName(name);
        layer.setOpacity(opacity);
        layer.setVisible(visible);
        layer.setOffsetX(offsetX);
        layer.setOffsetY(offsetY);
    }

    /* access modifiers changed from: protected */
    public void loadObject(TiledMap map2, MapLayer layer, XmlReader.Element element) {
        loadObject(map2, layer.getObjects(), element, (float) this.mapHeightInPixels);
    }

    /* access modifiers changed from: protected */
    public void loadObject(TiledMap map2, TiledMapTile tile, XmlReader.Element element) {
        loadObject(map2, tile.getObjects(), element, (float) tile.getTextureRegion().getRegionHeight());
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x0155  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x0206  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x021d  */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x0235  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x0245  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x0255  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x0268  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x0276  */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x02ab  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x02b7  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void loadObject(com.badlogic.gdx.maps.tiled.TiledMap r29, com.badlogic.gdx.maps.MapObjects r30, com.badlogic.gdx.utils.XmlReader.Element r31, float r32) {
        /*
            r28 = this;
            r0 = r28
            r1 = r31
            java.lang.String r2 = r31.getName()
            java.lang.String r3 = "object"
            boolean r2 = r2.equals(r3)
            if (r2 == 0) goto L_0x02c4
            r2 = 0
            boolean r3 = r0.convertObjectToTileSpace
            r4 = 1065353216(0x3f800000, float:1.0)
            if (r3 == 0) goto L_0x001d
            int r3 = r0.mapTileWidth
            float r3 = (float) r3
            float r3 = r4 / r3
            goto L_0x001f
        L_0x001d:
            r3 = 1065353216(0x3f800000, float:1.0)
        L_0x001f:
            boolean r5 = r0.convertObjectToTileSpace
            if (r5 == 0) goto L_0x0027
            int r5 = r0.mapTileHeight
            float r5 = (float) r5
            float r4 = r4 / r5
        L_0x0027:
            java.lang.String r5 = "x"
            r6 = 0
            float r7 = r1.getFloatAttribute(r5, r6)
            float r7 = r7 * r3
            boolean r8 = r0.flipY
            java.lang.String r9 = "y"
            if (r8 == 0) goto L_0x003d
            float r8 = r1.getFloatAttribute(r9, r6)
            float r8 = r32 - r8
            goto L_0x0041
        L_0x003d:
            float r8 = r1.getFloatAttribute(r9, r6)
        L_0x0041:
            float r8 = r8 * r4
            java.lang.String r10 = "width"
            float r11 = r1.getFloatAttribute(r10, r6)
            float r11 = r11 * r3
            java.lang.String r12 = "height"
            float r13 = r1.getFloatAttribute(r12, r6)
            float r13 = r13 * r4
            int r14 = r31.getChildCount()
            if (r14 <= 0) goto L_0x014c
            r14 = 0
            java.lang.String r6 = "polygon"
            com.badlogic.gdx.utils.XmlReader$Element r6 = r1.getChildByName(r6)
            r14 = r6
            java.lang.String r15 = " "
            r19 = r2
            java.lang.String r2 = "points"
            if (r6 == 0) goto L_0x00c9
            java.lang.String r2 = r14.getAttribute(r2)
            java.lang.String[] r2 = r2.split(r15)
            int r6 = r2.length
            int r6 = r6 * 2
            float[] r6 = new float[r6]
            r15 = 0
        L_0x0077:
            r20 = r14
            int r14 = r2.length
            if (r15 >= r14) goto L_0x00b7
            r14 = r2[r15]
            r21 = r2
            java.lang.String r2 = ","
            java.lang.String[] r2 = r14.split(r2)
            int r14 = r15 * 2
            r18 = 0
            r22 = r2[r18]
            float r22 = java.lang.Float.parseFloat(r22)
            float r22 = r22 * r3
            r6[r14] = r22
            int r14 = r15 * 2
            r17 = 1
            int r14 = r14 + 1
            r22 = r2[r17]
            float r22 = java.lang.Float.parseFloat(r22)
            float r22 = r22 * r4
            r23 = r2
            boolean r2 = r0.flipY
            if (r2 == 0) goto L_0x00aa
            r2 = -1
            goto L_0x00ab
        L_0x00aa:
            r2 = 1
        L_0x00ab:
            float r2 = (float) r2
            float r22 = r22 * r2
            r6[r14] = r22
            int r15 = r15 + 1
            r14 = r20
            r2 = r21
            goto L_0x0077
        L_0x00b7:
            r21 = r2
            com.badlogic.gdx.math.Polygon r2 = new com.badlogic.gdx.math.Polygon
            r2.<init>(r6)
            r2.setPosition(r7, r8)
            com.badlogic.gdx.maps.objects.PolygonMapObject r14 = new com.badlogic.gdx.maps.objects.PolygonMapObject
            r14.<init>((com.badlogic.gdx.math.Polygon) r2)
            r2 = r14
            goto L_0x0150
        L_0x00c9:
            r20 = r14
            java.lang.String r6 = "polyline"
            com.badlogic.gdx.utils.XmlReader$Element r6 = r1.getChildByName(r6)
            r14 = r6
            if (r6 == 0) goto L_0x0133
            java.lang.String r2 = r14.getAttribute(r2)
            java.lang.String[] r2 = r2.split(r15)
            int r6 = r2.length
            int r6 = r6 * 2
            float[] r6 = new float[r6]
            r15 = 0
        L_0x00e2:
            r20 = r14
            int r14 = r2.length
            if (r15 >= r14) goto L_0x0122
            r14 = r2[r15]
            r21 = r2
            java.lang.String r2 = ","
            java.lang.String[] r2 = r14.split(r2)
            int r14 = r15 * 2
            r18 = 0
            r22 = r2[r18]
            float r22 = java.lang.Float.parseFloat(r22)
            float r22 = r22 * r3
            r6[r14] = r22
            int r14 = r15 * 2
            r17 = 1
            int r14 = r14 + 1
            r22 = r2[r17]
            float r22 = java.lang.Float.parseFloat(r22)
            float r22 = r22 * r4
            r23 = r2
            boolean r2 = r0.flipY
            if (r2 == 0) goto L_0x0115
            r2 = -1
            goto L_0x0116
        L_0x0115:
            r2 = 1
        L_0x0116:
            float r2 = (float) r2
            float r22 = r22 * r2
            r6[r14] = r22
            int r15 = r15 + 1
            r14 = r20
            r2 = r21
            goto L_0x00e2
        L_0x0122:
            r21 = r2
            com.badlogic.gdx.math.Polyline r2 = new com.badlogic.gdx.math.Polyline
            r2.<init>(r6)
            r2.setPosition(r7, r8)
            com.badlogic.gdx.maps.objects.PolylineMapObject r14 = new com.badlogic.gdx.maps.objects.PolylineMapObject
            r14.<init>((com.badlogic.gdx.math.Polyline) r2)
            r2 = r14
            goto L_0x0150
        L_0x0133:
            r20 = r14
            java.lang.String r2 = "ellipse"
            com.badlogic.gdx.utils.XmlReader$Element r2 = r1.getChildByName(r2)
            r6 = r2
            if (r2 == 0) goto L_0x014e
            com.badlogic.gdx.maps.objects.EllipseMapObject r2 = new com.badlogic.gdx.maps.objects.EllipseMapObject
            boolean r14 = r0.flipY
            if (r14 == 0) goto L_0x0147
            float r14 = r8 - r13
            goto L_0x0148
        L_0x0147:
            r14 = r8
        L_0x0148:
            r2.<init>(r7, r14, r11, r13)
            goto L_0x0150
        L_0x014c:
            r19 = r2
        L_0x014e:
            r2 = r19
        L_0x0150:
            java.lang.String r6 = "rotation"
            r14 = 0
            if (r2 != 0) goto L_0x0206
            r15 = 0
            r19 = r2
            java.lang.String r2 = "gid"
            java.lang.String r20 = r1.getAttribute(r2, r14)
            r15 = r20
            if (r20 == 0) goto L_0x01f1
            r20 = r5
            r21 = r6
            long r5 = java.lang.Long.parseLong(r15)
            int r6 = (int) r5
            r5 = -2147483648(0xffffffff80000000, float:-0.0)
            r5 = r5 & r6
            if (r5 == 0) goto L_0x0172
            r5 = 1
            goto L_0x0173
        L_0x0172:
            r5 = 0
        L_0x0173:
            r22 = 1073741824(0x40000000, float:2.0)
            r22 = r6 & r22
            if (r22 == 0) goto L_0x017c
            r22 = 1
            goto L_0x017e
        L_0x017c:
            r22 = 0
        L_0x017e:
            r23 = r22
            com.badlogic.gdx.maps.tiled.TiledMapTileSets r14 = r29.getTileSets()
            r24 = 536870911(0x1fffffff, float:1.0842021E-19)
            r25 = r15
            r15 = r6 & r24
            com.badlogic.gdx.maps.tiled.TiledMapTile r14 = r14.getTile(r15)
            com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject r15 = new com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject
            r24 = r9
            r9 = r23
            r15.<init>(r14, r5, r9)
            com.badlogic.gdx.graphics.g2d.TextureRegion r23 = r15.getTextureRegion()
            r26 = r5
            com.badlogic.gdx.maps.MapProperties r5 = r15.getProperties()
            r27 = r9
            java.lang.Integer r9 = java.lang.Integer.valueOf(r6)
            r5.put(r2, r9)
            r15.setX(r7)
            boolean r2 = r0.flipY
            if (r2 == 0) goto L_0x01b4
            r2 = r8
            goto L_0x01b6
        L_0x01b4:
            float r2 = r8 - r13
        L_0x01b6:
            r15.setY(r2)
            int r2 = r23.getRegionWidth()
            float r2 = (float) r2
            float r2 = r1.getFloatAttribute(r10, r2)
            int r5 = r23.getRegionHeight()
            float r5 = (float) r5
            float r5 = r1.getFloatAttribute(r12, r5)
            int r9 = r23.getRegionWidth()
            float r9 = (float) r9
            float r9 = r2 / r9
            float r9 = r9 * r3
            r15.setScaleX(r9)
            int r9 = r23.getRegionHeight()
            float r9 = (float) r9
            float r9 = r5 / r9
            float r9 = r9 * r4
            r15.setScaleY(r9)
            r16 = r2
            r9 = r21
            r2 = 0
            float r2 = r1.getFloatAttribute(r9, r2)
            r15.setRotation(r2)
            r2 = r15
            goto L_0x020d
        L_0x01f1:
            r20 = r5
            r24 = r9
            r25 = r15
            r9 = r6
            com.badlogic.gdx.maps.objects.RectangleMapObject r2 = new com.badlogic.gdx.maps.objects.RectangleMapObject
            boolean r5 = r0.flipY
            if (r5 == 0) goto L_0x0201
            float r5 = r8 - r13
            goto L_0x0202
        L_0x0201:
            r5 = r8
        L_0x0202:
            r2.<init>(r7, r5, r11, r13)
            goto L_0x020d
        L_0x0206:
            r19 = r2
            r20 = r5
            r24 = r9
            r9 = r6
        L_0x020d:
            java.lang.String r5 = "name"
            r6 = 0
            java.lang.String r5 = r1.getAttribute(r5, r6)
            r2.setName(r5)
            java.lang.String r5 = r1.getAttribute(r9, r6)
            if (r5 == 0) goto L_0x022c
            com.badlogic.gdx.maps.MapProperties r6 = r2.getProperties()
            float r14 = java.lang.Float.parseFloat(r5)
            java.lang.Float r14 = java.lang.Float.valueOf(r14)
            r6.put(r9, r14)
        L_0x022c:
            java.lang.String r6 = "type"
            r9 = 0
            java.lang.String r9 = r1.getAttribute(r6, r9)
            if (r9 == 0) goto L_0x023c
            com.badlogic.gdx.maps.MapProperties r14 = r2.getProperties()
            r14.put(r6, r9)
        L_0x023c:
            java.lang.String r6 = "id"
            r14 = 0
            int r6 = r1.getIntAttribute(r6, r14)
            if (r6 == 0) goto L_0x0255
            com.badlogic.gdx.maps.MapProperties r15 = r2.getProperties()
            java.lang.Integer r14 = java.lang.Integer.valueOf(r6)
            r16 = r3
            java.lang.String r3 = "id"
            r15.put(r3, r14)
            goto L_0x0257
        L_0x0255:
            r16 = r3
        L_0x0257:
            com.badlogic.gdx.maps.MapProperties r3 = r2.getProperties()
            java.lang.Float r14 = java.lang.Float.valueOf(r7)
            r15 = r20
            r3.put(r15, r14)
            boolean r3 = r2 instanceof com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject
            if (r3 == 0) goto L_0x0276
            com.badlogic.gdx.maps.MapProperties r3 = r2.getProperties()
            java.lang.Float r14 = java.lang.Float.valueOf(r8)
            r15 = r24
            r3.put(r15, r14)
            goto L_0x028b
        L_0x0276:
            r15 = r24
            com.badlogic.gdx.maps.MapProperties r3 = r2.getProperties()
            boolean r14 = r0.flipY
            if (r14 == 0) goto L_0x0283
            float r14 = r8 - r13
            goto L_0x0284
        L_0x0283:
            r14 = r8
        L_0x0284:
            java.lang.Float r14 = java.lang.Float.valueOf(r14)
            r3.put(r15, r14)
        L_0x028b:
            com.badlogic.gdx.maps.MapProperties r3 = r2.getProperties()
            java.lang.Float r14 = java.lang.Float.valueOf(r11)
            r3.put(r10, r14)
            com.badlogic.gdx.maps.MapProperties r3 = r2.getProperties()
            java.lang.Float r10 = java.lang.Float.valueOf(r13)
            r3.put(r12, r10)
            java.lang.String r3 = "visible"
            r10 = 1
            int r3 = r1.getIntAttribute(r3, r10)
            if (r3 != r10) goto L_0x02ab
            goto L_0x02ac
        L_0x02ab:
            r10 = 0
        L_0x02ac:
            r2.setVisible(r10)
            java.lang.String r3 = "properties"
            com.badlogic.gdx.utils.XmlReader$Element r3 = r1.getChildByName(r3)
            if (r3 == 0) goto L_0x02be
            com.badlogic.gdx.maps.MapProperties r10 = r2.getProperties()
            r0.loadProperties(r10, r3)
        L_0x02be:
            r10 = r30
            r10.add(r2)
            goto L_0x02c6
        L_0x02c4:
            r10 = r30
        L_0x02c6:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.maps.tiled.BaseTmxMapLoader.loadObject(com.badlogic.gdx.maps.tiled.TiledMap, com.badlogic.gdx.maps.MapObjects, com.badlogic.gdx.utils.XmlReader$Element, float):void");
    }

    /* access modifiers changed from: protected */
    public void loadProperties(MapProperties properties, XmlReader.Element element) {
        if (element != null && element.getName().equals("properties")) {
            Iterator<XmlReader.Element> it = element.getChildrenByName("property").iterator();
            while (it.hasNext()) {
                XmlReader.Element property = it.next();
                String name = property.getAttribute("name", (String) null);
                String value = property.getAttribute("value", (String) null);
                String type = property.getAttribute("type", (String) null);
                if (value == null) {
                    value = property.getText();
                }
                properties.put(name, castProperty(name, value, type));
            }
        }
    }

    private Object castProperty(String name, String value, String type) {
        if (type == null) {
            return value;
        }
        if (type.equals("int")) {
            return Integer.valueOf(value);
        }
        if (type.equals("float")) {
            return Float.valueOf(value);
        }
        if (type.equals("bool")) {
            return Boolean.valueOf(value);
        }
        if (type.equals("color")) {
            String opaqueColor = value.substring(3);
            String alpha = value.substring(1, 3);
            return Color.valueOf(opaqueColor + alpha);
        }
        throw new GdxRuntimeException("Wrong type given for property " + name + ", given : " + type + ", supported : string, bool, int, float, color");
    }

    /* access modifiers changed from: protected */
    public TiledMapTileLayer.Cell createTileLayerCell(boolean flipHorizontally, boolean flipVertically, boolean flipDiagonally) {
        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        if (!flipDiagonally) {
            cell.setFlipHorizontally(flipHorizontally);
            cell.setFlipVertically(flipVertically);
        } else if (flipHorizontally && flipVertically) {
            cell.setFlipHorizontally(true);
            cell.setRotation(3);
        } else if (flipHorizontally) {
            cell.setRotation(3);
        } else if (flipVertically) {
            cell.setRotation(1);
        } else {
            cell.setFlipVertically(true);
            cell.setRotation(3);
        }
        return cell;
    }

    public static int[] getTileIds(XmlReader.Element element, int width, int height) {
        InputStream is;
        XmlReader.Element data = element.getChildByName("data");
        String encoding = data.getAttribute("encoding", (String) null);
        if (encoding != null) {
            int[] ids = new int[(width * height)];
            if (encoding.equals("csv")) {
                String[] array = data.getText().split(",");
                for (int i = 0; i < array.length; i++) {
                    ids[i] = (int) Long.parseLong(array[i].trim());
                }
            } else if (encoding.equals("base64")) {
                try {
                    String compression = data.getAttribute("compression", (String) null);
                    byte[] bytes = Base64Coder.decode(data.getText());
                    if (compression == null) {
                        is = new ByteArrayInputStream(bytes);
                    } else if (compression.equals("gzip")) {
                        is = new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(bytes), bytes.length));
                    } else if (compression.equals("zlib")) {
                        is = new BufferedInputStream(new InflaterInputStream(new ByteArrayInputStream(bytes)));
                    } else {
                        throw new GdxRuntimeException("Unrecognised compression (" + compression + ") for TMX Layer Data");
                    }
                    byte[] temp = new byte[4];
                    for (int y = 0; y < height; y++) {
                        int x = 0;
                        while (x < width) {
                            int read = is.read(temp);
                            while (true) {
                                if (read >= temp.length) {
                                    break;
                                }
                                int curr = is.read(temp, read, temp.length - read);
                                if (curr == -1) {
                                    break;
                                }
                                read += curr;
                            }
                            if (read == temp.length) {
                                ids[(y * width) + x] = unsignedByteToInt(temp[0]) | (unsignedByteToInt(temp[1]) << 8) | (unsignedByteToInt(temp[2]) << 16) | (unsignedByteToInt(temp[3]) << 24);
                                x++;
                            } else {
                                throw new GdxRuntimeException("Error Reading TMX Layer Data: Premature end of tile data");
                            }
                        }
                    }
                    StreamUtils.closeQuietly(is);
                } catch (IOException e) {
                    throw new GdxRuntimeException("Error Reading TMX Layer Data - IOException: " + e.getMessage());
                } catch (Throwable th) {
                    StreamUtils.closeQuietly((Closeable) null);
                    throw th;
                }
            } else {
                throw new GdxRuntimeException("Unrecognised encoding (" + encoding + ") for TMX Layer Data");
            }
            return ids;
        }
        throw new GdxRuntimeException("Unsupported encoding (XML) for TMX Layer Data");
    }

    protected static int unsignedByteToInt(byte b) {
        return b & 255;
    }

    protected static FileHandle getRelativeFileHandle(FileHandle file, String path) {
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

    /* access modifiers changed from: protected */
    public void loadTileSet(XmlReader.Element element, FileHandle tmxFile, ImageResolver imageResolver) {
        int imageWidth;
        FileHandle image;
        String imageSource;
        int imageHeight;
        XmlReader.Element element2;
        int offsetY;
        int offsetX;
        int firstgid;
        XmlReader.Element element3 = element;
        FileHandle fileHandle = tmxFile;
        if (element.getName().equals("tileset")) {
            String imageSource2 = BuildConfig.FLAVOR;
            int imageWidth2 = 0;
            int imageHeight2 = 0;
            FileHandle image2 = null;
            String source = element3.getAttribute("source", (String) null);
            if (source != null) {
                FileHandle tsx = getRelativeFileHandle(fileHandle, source);
                try {
                    XmlReader.Element element4 = this.xml.parse(tsx);
                    try {
                        XmlReader.Element imageElement = element4.getChildByName("image");
                        if (imageElement != null) {
                            imageSource2 = imageElement.getAttribute("source");
                            imageWidth2 = imageElement.getIntAttribute("width", 0);
                            imageHeight2 = imageElement.getIntAttribute("height", 0);
                            image2 = getRelativeFileHandle(tsx, imageSource2);
                        }
                        element2 = element4;
                        imageSource = imageSource2;
                        imageWidth = imageWidth2;
                        imageHeight = imageHeight2;
                        image = image2;
                    } catch (SerializationException e) {
                        throw new GdxRuntimeException("Error parsing external tileset.");
                    }
                } catch (SerializationException e2) {
                    throw new GdxRuntimeException("Error parsing external tileset.");
                }
            } else {
                XmlReader.Element imageElement2 = element3.getChildByName("image");
                if (imageElement2 != null) {
                    String imageSource3 = imageElement2.getAttribute("source");
                    element2 = element3;
                    imageSource = imageSource3;
                    imageWidth = imageElement2.getIntAttribute("width", 0);
                    imageHeight = imageElement2.getIntAttribute("height", 0);
                    image = getRelativeFileHandle(fileHandle, imageSource3);
                } else {
                    element2 = element3;
                    imageSource = imageSource2;
                    imageWidth = 0;
                    imageHeight = 0;
                    image = null;
                }
            }
            String name = element2.get("name", (String) null);
            int firstgid2 = element2.getIntAttribute("firstgid", 1);
            int tilewidth = element2.getIntAttribute("tilewidth", 0);
            int tileheight = element2.getIntAttribute("tileheight", 0);
            int spacing = element2.getIntAttribute("spacing", 0);
            int margin = element2.getIntAttribute("margin", 0);
            XmlReader.Element offset = element2.getChildByName("tileoffset");
            if (offset != null) {
                offsetX = offset.getIntAttribute("x", 0);
                offsetY = offset.getIntAttribute("y", 0);
            } else {
                offsetX = 0;
                offsetY = 0;
            }
            TiledMapTileSet tileSet = new TiledMapTileSet();
            tileSet.setName(name);
            MapProperties tileSetProperties = tileSet.getProperties();
            XmlReader.Element properties = element2.getChildByName("properties");
            if (properties != null) {
                loadProperties(tileSetProperties, properties);
            }
            tileSetProperties.put("firstgid", Integer.valueOf(firstgid2));
            Array<XmlReader.Element> tileElements = element2.getChildrenByName("tile");
            XmlReader.Element element5 = properties;
            MapProperties mapProperties = tileSetProperties;
            String str = name;
            XmlReader.Element element6 = offset;
            TiledMapTileSet tileSet2 = tileSet;
            XmlReader.Element element7 = element2;
            int firstgid3 = firstgid2;
            String str2 = source;
            addStaticTiles(tmxFile, imageResolver, tileSet, element2, tileElements, name, firstgid2, tilewidth, tileheight, spacing, margin, source, offsetX, offsetY, imageSource, imageWidth, imageHeight, image);
            Iterator<XmlReader.Element> it = tileElements.iterator();
            while (it.hasNext()) {
                XmlReader.Element tileElement = it.next();
                TiledMapTileSet tileSet3 = tileSet2;
                TiledMapTile tile = tileSet3.getTile(firstgid3 + tileElement.getIntAttribute("id", 0));
                if (tile != null) {
                    addTileProperties(tile, tileElement);
                    addTileObjectGroup(tile, tileElement);
                    firstgid = firstgid3;
                    addAnimatedTile(tileSet3, tile, tileElement, firstgid);
                } else {
                    firstgid = firstgid3;
                }
                tileSet2 = tileSet3;
                firstgid3 = firstgid;
            }
            int i = firstgid3;
            this.map.getTileSets().addTileSet(tileSet2);
            XmlReader.Element element8 = element7;
            return;
        }
    }

    /* access modifiers changed from: protected */
    public void addTileProperties(TiledMapTile tile, XmlReader.Element tileElement) {
        String terrain = tileElement.getAttribute("terrain", (String) null);
        if (terrain != null) {
            tile.getProperties().put("terrain", terrain);
        }
        String probability = tileElement.getAttribute("probability", (String) null);
        if (probability != null) {
            tile.getProperties().put("probability", probability);
        }
        XmlReader.Element properties = tileElement.getChildByName("properties");
        if (properties != null) {
            loadProperties(tile.getProperties(), properties);
        }
    }

    /* access modifiers changed from: protected */
    public void addTileObjectGroup(TiledMapTile tile, XmlReader.Element tileElement) {
        XmlReader.Element objectgroupElement = tileElement.getChildByName("objectgroup");
        if (objectgroupElement != null) {
            Iterator<XmlReader.Element> it = objectgroupElement.getChildrenByName("object").iterator();
            while (it.hasNext()) {
                loadObject(this.map, tile, it.next());
            }
        }
    }

    /* access modifiers changed from: protected */
    public void addAnimatedTile(TiledMapTileSet tileSet, TiledMapTile tile, XmlReader.Element tileElement, int firstgid) {
        XmlReader.Element animationElement = tileElement.getChildByName("animation");
        if (animationElement != null) {
            Array<StaticTiledMapTile> staticTiles = new Array<>();
            IntArray intervals = new IntArray();
            Iterator<XmlReader.Element> it = animationElement.getChildrenByName("frame").iterator();
            while (it.hasNext()) {
                XmlReader.Element frameElement = it.next();
                staticTiles.add((StaticTiledMapTile) tileSet.getTile(frameElement.getIntAttribute("tileid") + firstgid));
                intervals.add(frameElement.getIntAttribute("duration"));
            }
            AnimatedTiledMapTile animatedTile = new AnimatedTiledMapTile(intervals, staticTiles);
            animatedTile.setId(tile.getId());
            tileSet.putTile(tile.getId(), animatedTile);
        }
    }

    /* access modifiers changed from: protected */
    public void addStaticTiledMapTile(TiledMapTileSet tileSet, TextureRegion textureRegion, int tileId, float offsetX, float offsetY) {
        TiledMapTile tile = new StaticTiledMapTile(textureRegion);
        tile.setId(tileId);
        tile.setOffsetX(offsetX);
        tile.setOffsetY(this.flipY ? -offsetY : offsetY);
        tileSet.putTile(tileId, tile);
    }
}
