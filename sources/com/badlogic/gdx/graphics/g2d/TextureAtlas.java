package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.StreamUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.Iterator;

public class TextureAtlas implements Disposable {
    static final Comparator<TextureAtlasData.Region> indexComparator = new Comparator<TextureAtlasData.Region>() {
        public int compare(TextureAtlasData.Region region1, TextureAtlasData.Region region2) {
            int i1 = region1.index;
            if (i1 == -1) {
                i1 = Integer.MAX_VALUE;
            }
            int i2 = region2.index;
            if (i2 == -1) {
                i2 = Integer.MAX_VALUE;
            }
            return i1 - i2;
        }
    };
    static final String[] tuple = new String[4];
    private final Array<AtlasRegion> regions;
    private final ObjectSet<Texture> textures;

    public static class TextureAtlasData {
        final Array<Page> pages = new Array<>();
        final Array<Region> regions = new Array<>();

        public static class Region {
            public int degrees;
            public boolean flip;
            public int height;
            public int index;
            public int left;
            public String name;
            public float offsetX;
            public float offsetY;
            public int originalHeight;
            public int originalWidth;
            public int[] pads;
            public Page page;
            public boolean rotate;
            public int[] splits;
            public int top;
            public int width;
        }

        public static class Page {
            public final Pixmap.Format format;
            public final float height;
            public final Texture.TextureFilter magFilter;
            public final Texture.TextureFilter minFilter;
            public Texture texture;
            public final FileHandle textureFile;
            public final Texture.TextureWrap uWrap;
            public final boolean useMipMaps;
            public final Texture.TextureWrap vWrap;
            public final float width;

            public Page(FileHandle handle, float width2, float height2, boolean useMipMaps2, Pixmap.Format format2, Texture.TextureFilter minFilter2, Texture.TextureFilter magFilter2, Texture.TextureWrap uWrap2, Texture.TextureWrap vWrap2) {
                this.width = width2;
                this.height = height2;
                this.textureFile = handle;
                this.useMipMaps = useMipMaps2;
                this.format = format2;
                this.minFilter = minFilter2;
                this.magFilter = magFilter2;
                this.uWrap = uWrap2;
                this.vWrap = vWrap2;
            }
        }

        public TextureAtlasData(FileHandle packFile, FileHandle imagesDir, boolean flip) {
            int degrees;
            float height;
            float width;
            Texture.TextureWrap repeatY;
            BufferedReader reader = new BufferedReader(new InputStreamReader(packFile.read()), 64);
            Page pageImage = null;
            while (true) {
                try {
                    String line = reader.readLine();
                    if (line == null) {
                        StreamUtils.closeQuietly(reader);
                        this.regions.sort(TextureAtlas.indexComparator);
                        return;
                    } else if (line.trim().length() == 0) {
                        pageImage = null;
                        FileHandle fileHandle = imagesDir;
                    } else if (pageImage == null) {
                        try {
                            FileHandle file = imagesDir.child(line);
                            if (TextureAtlas.readTuple(reader) == 2) {
                                width = (float) Integer.parseInt(TextureAtlas.tuple[0]);
                                TextureAtlas.readTuple(reader);
                                height = (float) Integer.parseInt(TextureAtlas.tuple[1]);
                            } else {
                                width = 0.0f;
                                height = 0.0f;
                            }
                            Pixmap.Format format = Pixmap.Format.valueOf(TextureAtlas.tuple[0]);
                            TextureAtlas.readTuple(reader);
                            Texture.TextureFilter min = Texture.TextureFilter.valueOf(TextureAtlas.tuple[0]);
                            Texture.TextureFilter max = Texture.TextureFilter.valueOf(TextureAtlas.tuple[1]);
                            String direction = TextureAtlas.readValue(reader);
                            Texture.TextureWrap repeatX = Texture.TextureWrap.ClampToEdge;
                            Texture.TextureWrap repeatY2 = Texture.TextureWrap.ClampToEdge;
                            if (direction.equals("x")) {
                                repeatX = Texture.TextureWrap.Repeat;
                                repeatY = repeatY2;
                            } else if (direction.equals("y")) {
                                repeatY = Texture.TextureWrap.Repeat;
                            } else if (direction.equals("xy")) {
                                repeatX = Texture.TextureWrap.Repeat;
                                repeatY = Texture.TextureWrap.Repeat;
                            } else {
                                repeatY = repeatY2;
                            }
                            pageImage = new Page(file, width, height, min.isMipMap(), format, min, max, repeatX, repeatY);
                            this.pages.add(pageImage);
                        } catch (Exception e) {
                            ex = e;
                            try {
                                StringBuilder sb = new StringBuilder();
                                sb.append("Error reading pack file: ");
                                try {
                                    sb.append(packFile);
                                    throw new GdxRuntimeException(sb.toString(), ex);
                                } catch (Throwable th) {
                                    ex = th;
                                    StreamUtils.closeQuietly(reader);
                                    throw ex;
                                }
                            } catch (Throwable th2) {
                                ex = th2;
                                FileHandle fileHandle2 = packFile;
                                StreamUtils.closeQuietly(reader);
                                throw ex;
                            }
                        }
                    } else {
                        FileHandle fileHandle3 = imagesDir;
                        String rotateValue = TextureAtlas.readValue(reader);
                        if (rotateValue.equalsIgnoreCase("true")) {
                            degrees = 90;
                        } else if (rotateValue.equalsIgnoreCase("false")) {
                            degrees = 0;
                        } else {
                            degrees = Integer.valueOf(rotateValue).intValue();
                        }
                        TextureAtlas.readTuple(reader);
                        int left = Integer.parseInt(TextureAtlas.tuple[0]);
                        int top = Integer.parseInt(TextureAtlas.tuple[1]);
                        TextureAtlas.readTuple(reader);
                        int width2 = Integer.parseInt(TextureAtlas.tuple[0]);
                        int height2 = Integer.parseInt(TextureAtlas.tuple[1]);
                        Region region = new Region();
                        region.page = pageImage;
                        region.left = left;
                        region.top = top;
                        region.width = width2;
                        region.height = height2;
                        region.name = line;
                        region.rotate = degrees == 90;
                        region.degrees = degrees;
                        if (TextureAtlas.readTuple(reader) == 4) {
                            region.splits = new int[]{Integer.parseInt(TextureAtlas.tuple[0]), Integer.parseInt(TextureAtlas.tuple[1]), Integer.parseInt(TextureAtlas.tuple[2]), Integer.parseInt(TextureAtlas.tuple[3])};
                            if (TextureAtlas.readTuple(reader) == 4) {
                                region.pads = new int[]{Integer.parseInt(TextureAtlas.tuple[0]), Integer.parseInt(TextureAtlas.tuple[1]), Integer.parseInt(TextureAtlas.tuple[2]), Integer.parseInt(TextureAtlas.tuple[3])};
                                TextureAtlas.readTuple(reader);
                            }
                        }
                        region.originalWidth = Integer.parseInt(TextureAtlas.tuple[0]);
                        region.originalHeight = Integer.parseInt(TextureAtlas.tuple[1]);
                        TextureAtlas.readTuple(reader);
                        region.offsetX = (float) Integer.parseInt(TextureAtlas.tuple[0]);
                        region.offsetY = (float) Integer.parseInt(TextureAtlas.tuple[1]);
                        region.index = Integer.parseInt(TextureAtlas.readValue(reader));
                        if (flip) {
                            region.flip = true;
                        }
                        this.regions.add(region);
                    }
                } catch (Exception e2) {
                    ex = e2;
                    FileHandle fileHandle4 = imagesDir;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Error reading pack file: ");
                    sb2.append(packFile);
                    throw new GdxRuntimeException(sb2.toString(), ex);
                } catch (Throwable th3) {
                    ex = th3;
                    FileHandle fileHandle5 = imagesDir;
                    FileHandle fileHandle22 = packFile;
                    StreamUtils.closeQuietly(reader);
                    throw ex;
                }
            }
        }

        public Array<Page> getPages() {
            return this.pages;
        }

        public Array<Region> getRegions() {
            return this.regions;
        }
    }

    public TextureAtlas() {
        this.textures = new ObjectSet<>(4);
        this.regions = new Array<>();
    }

    public TextureAtlas(String internalPackFile) {
        this(Gdx.files.internal(internalPackFile));
    }

    public TextureAtlas(FileHandle packFile) {
        this(packFile, packFile.parent());
    }

    public TextureAtlas(FileHandle packFile, boolean flip) {
        this(packFile, packFile.parent(), flip);
    }

    public TextureAtlas(FileHandle packFile, FileHandle imagesDir) {
        this(packFile, imagesDir, false);
    }

    public TextureAtlas(FileHandle packFile, FileHandle imagesDir, boolean flip) {
        this(new TextureAtlasData(packFile, imagesDir, flip));
    }

    public TextureAtlas(TextureAtlasData data) {
        this.textures = new ObjectSet<>(4);
        this.regions = new Array<>();
        if (data != null) {
            load(data);
        }
    }

    private void load(TextureAtlasData data) {
        Texture texture;
        ObjectMap<TextureAtlasData.Page, Texture> pageToTexture = new ObjectMap<>();
        Iterator<TextureAtlasData.Page> it = data.pages.iterator();
        while (it.hasNext()) {
            TextureAtlasData.Page page = it.next();
            if (page.texture == null) {
                texture = new Texture(page.textureFile, page.format, page.useMipMaps);
                texture.setFilter(page.minFilter, page.magFilter);
                texture.setWrap(page.uWrap, page.vWrap);
            } else {
                texture = page.texture;
                texture.setFilter(page.minFilter, page.magFilter);
                texture.setWrap(page.uWrap, page.vWrap);
            }
            this.textures.add(texture);
            pageToTexture.put(page, texture);
        }
        Iterator<TextureAtlasData.Region> it2 = data.regions.iterator();
        while (it2.hasNext()) {
            TextureAtlasData.Region region = it2.next();
            int width = region.width;
            int height = region.height;
            AtlasRegion atlasRegion = new AtlasRegion(pageToTexture.get(region.page), region.left, region.top, region.rotate ? height : width, region.rotate ? width : height);
            atlasRegion.index = region.index;
            atlasRegion.name = region.name;
            atlasRegion.offsetX = region.offsetX;
            atlasRegion.offsetY = region.offsetY;
            atlasRegion.originalHeight = region.originalHeight;
            atlasRegion.originalWidth = region.originalWidth;
            atlasRegion.rotate = region.rotate;
            atlasRegion.degrees = region.degrees;
            atlasRegion.splits = region.splits;
            atlasRegion.pads = region.pads;
            if (region.flip) {
                atlasRegion.flip(false, true);
            }
            this.regions.add(atlasRegion);
        }
    }

    public AtlasRegion addRegion(String name, Texture texture, int x, int y, int width, int height) {
        this.textures.add(texture);
        AtlasRegion atlasRegion = new AtlasRegion(texture, x, y, width, height);
        atlasRegion.name = name;
        atlasRegion.index = -1;
        this.regions.add(atlasRegion);
        return atlasRegion;
    }

    public AtlasRegion addRegion(String name, TextureRegion textureRegion) {
        this.textures.add(textureRegion.texture);
        AtlasRegion region = new AtlasRegion(textureRegion);
        region.name = name;
        region.index = -1;
        this.regions.add(region);
        return region;
    }

    public Array<AtlasRegion> getRegions() {
        return this.regions;
    }

    public AtlasRegion findRegion(String name) {
        int n = this.regions.size;
        for (int i = 0; i < n; i++) {
            if (this.regions.get(i).name.equals(name)) {
                return this.regions.get(i);
            }
        }
        return null;
    }

    public AtlasRegion findRegion(String name, int index) {
        int n = this.regions.size;
        for (int i = 0; i < n; i++) {
            AtlasRegion region = this.regions.get(i);
            if (region.name.equals(name) && region.index == index) {
                return region;
            }
        }
        return null;
    }

    public Array<AtlasRegion> findRegions(String name) {
        Array<AtlasRegion> matched = new Array<>(AtlasRegion.class);
        int n = this.regions.size;
        for (int i = 0; i < n; i++) {
            AtlasRegion region = this.regions.get(i);
            if (region.name.equals(name)) {
                matched.add(new AtlasRegion(region));
            }
        }
        return matched;
    }

    public Array<Sprite> createSprites() {
        Array sprites = new Array(true, this.regions.size, Sprite.class);
        int n = this.regions.size;
        for (int i = 0; i < n; i++) {
            sprites.add(newSprite(this.regions.get(i)));
        }
        return sprites;
    }

    public Sprite createSprite(String name) {
        int n = this.regions.size;
        for (int i = 0; i < n; i++) {
            if (this.regions.get(i).name.equals(name)) {
                return newSprite(this.regions.get(i));
            }
        }
        return null;
    }

    public Sprite createSprite(String name, int index) {
        int n = this.regions.size;
        for (int i = 0; i < n; i++) {
            AtlasRegion region = this.regions.get(i);
            if (region.name.equals(name) && region.index == index) {
                return newSprite(this.regions.get(i));
            }
        }
        return null;
    }

    public Array<Sprite> createSprites(String name) {
        Array<Sprite> matched = new Array<>(Sprite.class);
        int n = this.regions.size;
        for (int i = 0; i < n; i++) {
            AtlasRegion region = this.regions.get(i);
            if (region.name.equals(name)) {
                matched.add(newSprite(region));
            }
        }
        return matched;
    }

    private Sprite newSprite(AtlasRegion region) {
        if (region.packedWidth != region.originalWidth || region.packedHeight != region.originalHeight) {
            return new AtlasSprite(region);
        }
        if (!region.rotate) {
            return new Sprite((TextureRegion) region);
        }
        Sprite sprite = new Sprite((TextureRegion) region);
        sprite.setBounds(0.0f, 0.0f, (float) region.getRegionHeight(), (float) region.getRegionWidth());
        sprite.rotate90(true);
        return sprite;
    }

    public NinePatch createPatch(String name) {
        String str = name;
        int n = this.regions.size;
        for (int i = 0; i < n; i++) {
            AtlasRegion region = this.regions.get(i);
            if (region.name.equals(str)) {
                int[] splits = region.splits;
                if (splits != null) {
                    NinePatch patch = new NinePatch((TextureRegion) region, splits[0], splits[1], splits[2], splits[3]);
                    if (region.pads != null) {
                        patch.setPadding((float) region.pads[0], (float) region.pads[1], (float) region.pads[2], (float) region.pads[3]);
                    }
                    return patch;
                }
                throw new IllegalArgumentException("Region does not have ninepatch splits: " + str);
            }
        }
        return null;
    }

    public ObjectSet<Texture> getTextures() {
        return this.textures;
    }

    public void dispose() {
        ObjectSet.ObjectSetIterator<Texture> it = this.textures.iterator();
        while (it.hasNext()) {
            ((Texture) it.next()).dispose();
        }
        this.textures.clear(0);
    }

    static String readValue(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        int colon = line.indexOf(58);
        if (colon != -1) {
            return line.substring(colon + 1).trim();
        }
        throw new GdxRuntimeException("Invalid line: " + line);
    }

    static int readTuple(BufferedReader reader) throws IOException {
        int comma;
        String line = reader.readLine();
        int colon = line.indexOf(58);
        if (colon != -1) {
            int lastMatch = colon + 1;
            int i = 0;
            while (i < 3 && (comma = line.indexOf(44, lastMatch)) != -1) {
                tuple[i] = line.substring(lastMatch, comma).trim();
                lastMatch = comma + 1;
                i++;
            }
            tuple[i] = line.substring(lastMatch).trim();
            return i + 1;
        }
        throw new GdxRuntimeException("Invalid line: " + line);
    }

    public static class AtlasRegion extends TextureRegion {
        public int degrees;
        public int index;
        public String name;
        public float offsetX;
        public float offsetY;
        public int originalHeight;
        public int originalWidth;
        public int packedHeight;
        public int packedWidth;
        public int[] pads;
        public boolean rotate;
        public int[] splits;

        public AtlasRegion(Texture texture, int x, int y, int width, int height) {
            super(texture, x, y, width, height);
            this.originalWidth = width;
            this.originalHeight = height;
            this.packedWidth = width;
            this.packedHeight = height;
        }

        public AtlasRegion(AtlasRegion region) {
            setRegion((TextureRegion) region);
            this.index = region.index;
            this.name = region.name;
            this.offsetX = region.offsetX;
            this.offsetY = region.offsetY;
            this.packedWidth = region.packedWidth;
            this.packedHeight = region.packedHeight;
            this.originalWidth = region.originalWidth;
            this.originalHeight = region.originalHeight;
            this.rotate = region.rotate;
            this.degrees = region.degrees;
            this.splits = region.splits;
        }

        public AtlasRegion(TextureRegion region) {
            setRegion(region);
            this.packedWidth = region.getRegionWidth();
            this.packedHeight = region.getRegionHeight();
            this.originalWidth = this.packedWidth;
            this.originalHeight = this.packedHeight;
        }

        public void flip(boolean x, boolean y) {
            super.flip(x, y);
            if (x) {
                this.offsetX = (((float) this.originalWidth) - this.offsetX) - getRotatedPackedWidth();
            }
            if (y) {
                this.offsetY = (((float) this.originalHeight) - this.offsetY) - getRotatedPackedHeight();
            }
        }

        public float getRotatedPackedWidth() {
            return (float) (this.rotate ? this.packedHeight : this.packedWidth);
        }

        public float getRotatedPackedHeight() {
            return (float) (this.rotate ? this.packedWidth : this.packedHeight);
        }

        public String toString() {
            return this.name;
        }
    }

    public static class AtlasSprite extends Sprite {
        float originalOffsetX;
        float originalOffsetY;
        final AtlasRegion region;

        public AtlasSprite(AtlasRegion region2) {
            this.region = new AtlasRegion(region2);
            this.originalOffsetX = region2.offsetX;
            this.originalOffsetY = region2.offsetY;
            setRegion((TextureRegion) region2);
            setOrigin(((float) region2.originalWidth) / 2.0f, ((float) region2.originalHeight) / 2.0f);
            int width = region2.getRegionWidth();
            int height = region2.getRegionHeight();
            if (region2.rotate) {
                super.rotate90(true);
                super.setBounds(region2.offsetX, region2.offsetY, (float) height, (float) width);
            } else {
                super.setBounds(region2.offsetX, region2.offsetY, (float) width, (float) height);
            }
            setColor(1.0f, 1.0f, 1.0f, 1.0f);
        }

        public AtlasSprite(AtlasSprite sprite) {
            this.region = sprite.region;
            this.originalOffsetX = sprite.originalOffsetX;
            this.originalOffsetY = sprite.originalOffsetY;
            set(sprite);
        }

        public void setPosition(float x, float y) {
            super.setPosition(this.region.offsetX + x, this.region.offsetY + y);
        }

        public void setX(float x) {
            super.setX(this.region.offsetX + x);
        }

        public void setY(float y) {
            super.setY(this.region.offsetY + y);
        }

        public void setBounds(float x, float y, float width, float height) {
            float widthRatio = width / ((float) this.region.originalWidth);
            float heightRatio = height / ((float) this.region.originalHeight);
            AtlasRegion atlasRegion = this.region;
            atlasRegion.offsetX = this.originalOffsetX * widthRatio;
            atlasRegion.offsetY = this.originalOffsetY * heightRatio;
            super.setBounds(this.region.offsetX + x, this.region.offsetY + y, ((float) (atlasRegion.rotate ? this.region.packedHeight : this.region.packedWidth)) * widthRatio, ((float) (this.region.rotate ? this.region.packedWidth : this.region.packedHeight)) * heightRatio);
        }

        public void setSize(float width, float height) {
            setBounds(getX(), getY(), width, height);
        }

        public void setOrigin(float originX, float originY) {
            super.setOrigin(originX - this.region.offsetX, originY - this.region.offsetY);
        }

        public void setOriginCenter() {
            super.setOrigin((this.width / 2.0f) - this.region.offsetX, (this.height / 2.0f) - this.region.offsetY);
        }

        public void flip(boolean x, boolean y) {
            if (this.region.rotate) {
                super.flip(y, x);
            } else {
                super.flip(x, y);
            }
            float oldOriginX = getOriginX();
            float oldOriginY = getOriginY();
            float oldOffsetX = this.region.offsetX;
            float oldOffsetY = this.region.offsetY;
            float widthRatio = getWidthRatio();
            float heightRatio = getHeightRatio();
            AtlasRegion atlasRegion = this.region;
            atlasRegion.offsetX = this.originalOffsetX;
            atlasRegion.offsetY = this.originalOffsetY;
            atlasRegion.flip(x, y);
            this.originalOffsetX = this.region.offsetX;
            this.originalOffsetY = this.region.offsetY;
            this.region.offsetX *= widthRatio;
            this.region.offsetY *= heightRatio;
            translate(this.region.offsetX - oldOffsetX, this.region.offsetY - oldOffsetY);
            setOrigin(oldOriginX, oldOriginY);
        }

        public void rotate90(boolean clockwise) {
            super.rotate90(clockwise);
            float oldOriginX = getOriginX();
            float oldOriginY = getOriginY();
            float oldOffsetX = this.region.offsetX;
            float oldOffsetY = this.region.offsetY;
            float widthRatio = getWidthRatio();
            float heightRatio = getHeightRatio();
            if (clockwise) {
                AtlasRegion atlasRegion = this.region;
                atlasRegion.offsetX = oldOffsetY;
                atlasRegion.offsetY = ((((float) atlasRegion.originalHeight) * heightRatio) - oldOffsetX) - (((float) this.region.packedWidth) * widthRatio);
            } else {
                AtlasRegion atlasRegion2 = this.region;
                atlasRegion2.offsetX = ((((float) atlasRegion2.originalWidth) * widthRatio) - oldOffsetY) - (((float) this.region.packedHeight) * heightRatio);
                this.region.offsetY = oldOffsetX;
            }
            translate(this.region.offsetX - oldOffsetX, this.region.offsetY - oldOffsetY);
            setOrigin(oldOriginX, oldOriginY);
        }

        public float getX() {
            return super.getX() - this.region.offsetX;
        }

        public float getY() {
            return super.getY() - this.region.offsetY;
        }

        public float getOriginX() {
            return super.getOriginX() + this.region.offsetX;
        }

        public float getOriginY() {
            return super.getOriginY() + this.region.offsetY;
        }

        public float getWidth() {
            return (super.getWidth() / this.region.getRotatedPackedWidth()) * ((float) this.region.originalWidth);
        }

        public float getHeight() {
            return (super.getHeight() / this.region.getRotatedPackedHeight()) * ((float) this.region.originalHeight);
        }

        public float getWidthRatio() {
            return super.getWidth() / this.region.getRotatedPackedWidth();
        }

        public float getHeightRatio() {
            return super.getHeight() / this.region.getRotatedPackedHeight();
        }

        public AtlasRegion getAtlasRegion() {
            return this.region;
        }

        public String toString() {
            return this.region.toString();
        }
    }
}
