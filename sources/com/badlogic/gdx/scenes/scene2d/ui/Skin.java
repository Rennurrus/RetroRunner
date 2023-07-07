package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.SerializationException;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.twi.game.BuildConfig;

public class Skin implements Disposable {
    private static final Class[] defaultTagClasses = {BitmapFont.class, Color.class, TintedDrawable.class, NinePatchDrawable.class, SpriteDrawable.class, TextureRegionDrawable.class, TiledDrawable.class, Button.ButtonStyle.class, CheckBox.CheckBoxStyle.class, ImageButton.ImageButtonStyle.class, ImageTextButton.ImageTextButtonStyle.class, Label.LabelStyle.class, List.ListStyle.class, ProgressBar.ProgressBarStyle.class, ScrollPane.ScrollPaneStyle.class, SelectBox.SelectBoxStyle.class, Slider.SliderStyle.class, SplitPane.SplitPaneStyle.class, TextButton.TextButtonStyle.class, TextField.TextFieldStyle.class, TextTooltip.TextTooltipStyle.class, Touchpad.TouchpadStyle.class, Tree.TreeStyle.class, Window.WindowStyle.class};
    TextureAtlas atlas;
    private final ObjectMap<String, Class> jsonClassTags = new ObjectMap<>(defaultTagClasses.length);
    ObjectMap<Class, ObjectMap<String, Object>> resources = new ObjectMap<>();

    public static class TintedDrawable {
        public Color color;
        public String name;
    }

    public Skin() {
        for (Class c : defaultTagClasses) {
            this.jsonClassTags.put(c.getSimpleName(), c);
        }
    }

    public Skin(FileHandle skinFile) {
        for (Class c : defaultTagClasses) {
            this.jsonClassTags.put(c.getSimpleName(), c);
        }
        FileHandle atlasFile = skinFile.sibling(skinFile.nameWithoutExtension() + ".atlas");
        if (atlasFile.exists()) {
            this.atlas = new TextureAtlas(atlasFile);
            addRegions(this.atlas);
        }
        load(skinFile);
    }

    public Skin(FileHandle skinFile, TextureAtlas atlas2) {
        for (Class c : defaultTagClasses) {
            this.jsonClassTags.put(c.getSimpleName(), c);
        }
        this.atlas = atlas2;
        addRegions(atlas2);
        load(skinFile);
    }

    public Skin(TextureAtlas atlas2) {
        for (Class c : defaultTagClasses) {
            this.jsonClassTags.put(c.getSimpleName(), c);
        }
        this.atlas = atlas2;
        addRegions(atlas2);
    }

    public void load(FileHandle skinFile) {
        try {
            getJsonLoader(skinFile).fromJson(Skin.class, skinFile);
        } catch (SerializationException ex) {
            throw new SerializationException("Error reading file: " + skinFile, ex);
        }
    }

    public void addRegions(TextureAtlas atlas2) {
        Array<TextureAtlas.AtlasRegion> regions = atlas2.getRegions();
        int n = regions.size;
        for (int i = 0; i < n; i++) {
            TextureAtlas.AtlasRegion region = regions.get(i);
            String name = region.name;
            if (region.index != -1) {
                name = name + "_" + region.index;
            }
            add(name, region, TextureRegion.class);
        }
    }

    public void add(String name, Object resource) {
        add(name, resource, resource.getClass());
    }

    public void add(String name, Object resource, Class type) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null.");
        } else if (resource != null) {
            ObjectMap<String, Object> typeResources = this.resources.get(type);
            if (typeResources == null) {
                typeResources = new ObjectMap<>((type == TextureRegion.class || type == Drawable.class || type == Sprite.class) ? 256 : 64);
                this.resources.put(type, typeResources);
            }
            typeResources.put(name, resource);
        } else {
            throw new IllegalArgumentException("resource cannot be null.");
        }
    }

    public void remove(String name, Class type) {
        if (name != null) {
            this.resources.get(type).remove(name);
            return;
        }
        throw new IllegalArgumentException("name cannot be null.");
    }

    public <T> T get(Class<T> type) {
        return get("default", type);
    }

    public <T> T get(String name, Class<T> type) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null.");
        } else if (type == null) {
            throw new IllegalArgumentException("type cannot be null.");
        } else if (type == Drawable.class) {
            return getDrawable(name);
        } else {
            if (type == TextureRegion.class) {
                return getRegion(name);
            }
            if (type == NinePatch.class) {
                return getPatch(name);
            }
            if (type == Sprite.class) {
                return getSprite(name);
            }
            ObjectMap<String, Object> typeResources = this.resources.get(type);
            if (typeResources != null) {
                Object resource = typeResources.get(name);
                if (resource != null) {
                    return resource;
                }
                throw new GdxRuntimeException("No " + type.getName() + " registered with name: " + name);
            }
            throw new GdxRuntimeException("No " + type.getName() + " registered with name: " + name);
        }
    }

    public <T> T optional(String name, Class<T> type) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null.");
        } else if (type != null) {
            ObjectMap<String, Object> typeResources = this.resources.get(type);
            if (typeResources == null) {
                return null;
            }
            return typeResources.get(name);
        } else {
            throw new IllegalArgumentException("type cannot be null.");
        }
    }

    public boolean has(String name, Class type) {
        ObjectMap<String, Object> typeResources = this.resources.get(type);
        if (typeResources == null) {
            return false;
        }
        return typeResources.containsKey(name);
    }

    public <T> ObjectMap<String, T> getAll(Class<T> type) {
        return this.resources.get(type);
    }

    public Color getColor(String name) {
        return (Color) get(name, Color.class);
    }

    public BitmapFont getFont(String name) {
        return (BitmapFont) get(name, BitmapFont.class);
    }

    public TextureRegion getRegion(String name) {
        TextureRegion region = (TextureRegion) optional(name, TextureRegion.class);
        if (region != null) {
            return region;
        }
        Texture texture = (Texture) optional(name, Texture.class);
        if (texture != null) {
            TextureRegion region2 = new TextureRegion(texture);
            add(name, region2, TextureRegion.class);
            return region2;
        }
        throw new GdxRuntimeException("No TextureRegion or Texture registered with name: " + name);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v5, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v5, resolved type: com.badlogic.gdx.graphics.g2d.TextureRegion} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.badlogic.gdx.utils.Array<com.badlogic.gdx.graphics.g2d.TextureRegion> getRegions(java.lang.String r7) {
        /*
            r6 = this;
            r0 = 0
            r1 = 0
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r7)
            java.lang.String r3 = "_"
            r2.append(r3)
            int r4 = r1 + 1
            r2.append(r1)
            java.lang.String r1 = r2.toString()
            java.lang.Class<com.badlogic.gdx.graphics.g2d.TextureRegion> r2 = com.badlogic.gdx.graphics.g2d.TextureRegion.class
            java.lang.Object r1 = r6.optional(r1, r2)
            com.badlogic.gdx.graphics.g2d.TextureRegion r1 = (com.badlogic.gdx.graphics.g2d.TextureRegion) r1
            if (r1 == 0) goto L_0x004c
            com.badlogic.gdx.utils.Array r2 = new com.badlogic.gdx.utils.Array
            r2.<init>()
            r0 = r2
        L_0x0028:
            if (r1 == 0) goto L_0x004c
            r0.add(r1)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r7)
            r2.append(r3)
            int r5 = r4 + 1
            r2.append(r4)
            java.lang.String r2 = r2.toString()
            java.lang.Class<com.badlogic.gdx.graphics.g2d.TextureRegion> r4 = com.badlogic.gdx.graphics.g2d.TextureRegion.class
            java.lang.Object r2 = r6.optional(r2, r4)
            r1 = r2
            com.badlogic.gdx.graphics.g2d.TextureRegion r1 = (com.badlogic.gdx.graphics.g2d.TextureRegion) r1
            r4 = r5
            goto L_0x0028
        L_0x004c:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.scenes.scene2d.ui.Skin.getRegions(java.lang.String):com.badlogic.gdx.utils.Array");
    }

    public TiledDrawable getTiledDrawable(String name) {
        TiledDrawable tiled = (TiledDrawable) optional(name, TiledDrawable.class);
        if (tiled != null) {
            return tiled;
        }
        TiledDrawable tiled2 = new TiledDrawable(getRegion(name));
        tiled2.setName(name);
        add(name, tiled2, TiledDrawable.class);
        return tiled2;
    }

    public NinePatch getPatch(String name) {
        int[] splits;
        NinePatch patch = (NinePatch) optional(name, NinePatch.class);
        if (patch != null) {
            return patch;
        }
        try {
            TextureRegion region = getRegion(name);
            if ((region instanceof TextureAtlas.AtlasRegion) && (splits = ((TextureAtlas.AtlasRegion) region).splits) != null) {
                patch = new NinePatch(region, splits[0], splits[1], splits[2], splits[3]);
                int[] pads = ((TextureAtlas.AtlasRegion) region).pads;
                if (pads != null) {
                    patch.setPadding((float) pads[0], (float) pads[1], (float) pads[2], (float) pads[3]);
                }
            }
            if (patch == null) {
                patch = new NinePatch(region);
            }
            add(name, patch, NinePatch.class);
            return patch;
        } catch (GdxRuntimeException e) {
            throw new GdxRuntimeException("No NinePatch, TextureRegion, or Texture registered with name: " + name);
        }
    }

    public Sprite getSprite(String name) {
        Sprite sprite = (Sprite) optional(name, Sprite.class);
        if (sprite != null) {
            return sprite;
        }
        try {
            TextureRegion textureRegion = getRegion(name);
            if (textureRegion instanceof TextureAtlas.AtlasRegion) {
                TextureAtlas.AtlasRegion region = (TextureAtlas.AtlasRegion) textureRegion;
                if (!(!region.rotate && region.packedWidth == region.originalWidth && region.packedHeight == region.originalHeight)) {
                    sprite = new TextureAtlas.AtlasSprite(region);
                }
            }
            if (sprite == null) {
                sprite = new Sprite(textureRegion);
            }
            add(name, sprite, Sprite.class);
            return sprite;
        } catch (GdxRuntimeException e) {
            throw new GdxRuntimeException("No NinePatch, TextureRegion, or Texture registered with name: " + name);
        }
    }

    public Drawable getDrawable(String name) {
        Drawable drawable = (Drawable) optional(name, Drawable.class);
        if (drawable != null) {
            return drawable;
        }
        try {
            TextureRegion textureRegion = getRegion(name);
            if (textureRegion instanceof TextureAtlas.AtlasRegion) {
                TextureAtlas.AtlasRegion region = (TextureAtlas.AtlasRegion) textureRegion;
                if (region.splits != null) {
                    drawable = new NinePatchDrawable(getPatch(name));
                } else if (!(!region.rotate && region.packedWidth == region.originalWidth && region.packedHeight == region.originalHeight)) {
                    drawable = new SpriteDrawable(getSprite(name));
                }
            }
            if (drawable == null) {
                drawable = new TextureRegionDrawable(textureRegion);
            }
        } catch (GdxRuntimeException e) {
        }
        if (drawable == null) {
            NinePatch patch = (NinePatch) optional(name, NinePatch.class);
            if (patch != null) {
                drawable = new NinePatchDrawable(patch);
            } else {
                Sprite sprite = (Sprite) optional(name, Sprite.class);
                if (sprite != null) {
                    drawable = new SpriteDrawable(sprite);
                } else {
                    throw new GdxRuntimeException("No Drawable, NinePatch, TextureRegion, Texture, or Sprite registered with name: " + name);
                }
            }
        }
        if (drawable instanceof BaseDrawable) {
            ((BaseDrawable) drawable).setName(name);
        }
        add(name, drawable, Drawable.class);
        return drawable;
    }

    public String find(Object resource) {
        if (resource != null) {
            ObjectMap<String, Object> typeResources = this.resources.get(resource.getClass());
            if (typeResources == null) {
                return null;
            }
            return typeResources.findKey(resource, true);
        }
        throw new IllegalArgumentException("style cannot be null.");
    }

    public Drawable newDrawable(String name) {
        return newDrawable(getDrawable(name));
    }

    public Drawable newDrawable(String name, float r, float g, float b, float a) {
        return newDrawable(getDrawable(name), new Color(r, g, b, a));
    }

    public Drawable newDrawable(String name, Color tint) {
        return newDrawable(getDrawable(name), tint);
    }

    public Drawable newDrawable(Drawable drawable) {
        if (drawable instanceof TiledDrawable) {
            return new TiledDrawable((TextureRegionDrawable) (TiledDrawable) drawable);
        }
        if (drawable instanceof TextureRegionDrawable) {
            return new TextureRegionDrawable((TextureRegionDrawable) drawable);
        }
        if (drawable instanceof NinePatchDrawable) {
            return new NinePatchDrawable((NinePatchDrawable) drawable);
        }
        if (drawable instanceof SpriteDrawable) {
            return new SpriteDrawable((SpriteDrawable) drawable);
        }
        throw new GdxRuntimeException("Unable to copy, unknown drawable type: " + drawable.getClass());
    }

    public Drawable newDrawable(Drawable drawable, float r, float g, float b, float a) {
        return newDrawable(drawable, new Color(r, g, b, a));
    }

    public Drawable newDrawable(Drawable drawable, Color tint) {
        Drawable newDrawable;
        if (drawable instanceof TextureRegionDrawable) {
            newDrawable = ((TextureRegionDrawable) drawable).tint(tint);
        } else if (drawable instanceof NinePatchDrawable) {
            newDrawable = ((NinePatchDrawable) drawable).tint(tint);
        } else if (drawable instanceof SpriteDrawable) {
            newDrawable = ((SpriteDrawable) drawable).tint(tint);
        } else {
            throw new GdxRuntimeException("Unable to copy, unknown drawable type: " + drawable.getClass());
        }
        if (newDrawable instanceof BaseDrawable) {
            BaseDrawable named = (BaseDrawable) newDrawable;
            if (drawable instanceof BaseDrawable) {
                named.setName(((BaseDrawable) drawable).getName() + " (" + tint + ")");
            } else {
                named.setName(" (" + tint + ")");
            }
        }
        return newDrawable;
    }

    public void setEnabled(Actor actor, boolean enabled) {
        Method method = findMethod(actor.getClass(), "getStyle");
        if (method != null) {
            try {
                Object style = method.invoke(actor, new Object[0]);
                String name = find(style);
                if (name != null) {
                    StringBuilder sb = new StringBuilder();
                    String str = BuildConfig.FLAVOR;
                    sb.append(name.replace("-disabled", str));
                    if (!enabled) {
                        str = "-disabled";
                    }
                    sb.append(str);
                    Object style2 = get(sb.toString(), style.getClass());
                    Method method2 = findMethod(actor.getClass(), "setStyle");
                    if (method2 != null) {
                        try {
                            method2.invoke(actor, style2);
                        } catch (Exception e) {
                        }
                    }
                }
            } catch (Exception e2) {
            }
        }
    }

    public TextureAtlas getAtlas() {
        return this.atlas;
    }

    public void dispose() {
        TextureAtlas textureAtlas = this.atlas;
        if (textureAtlas != null) {
            textureAtlas.dispose();
        }
        ObjectMap.Values<ObjectMap<String, Object>> it = this.resources.values().iterator();
        while (it.hasNext()) {
            ObjectMap.Values<Object> it2 = ((ObjectMap) it.next()).values().iterator();
            while (it2.hasNext()) {
                Object resource = it2.next();
                if (resource instanceof Disposable) {
                    ((Disposable) resource).dispose();
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public Json getJsonLoader(final FileHandle skinFile) {
        Json json = new Json() {
            private static final String parentFieldName = "parent";

            public <T> T readValue(Class<T> type, Class elementType, JsonValue jsonData) {
                if (jsonData == null || !jsonData.isString() || ClassReflection.isAssignableFrom(CharSequence.class, type)) {
                    return super.readValue(type, elementType, jsonData);
                }
                return Skin.this.get(jsonData.asString(), type);
            }

            /* access modifiers changed from: protected */
            public boolean ignoreUnknownField(Class type, String fieldName) {
                return fieldName.equals(parentFieldName);
            }

            public void readFields(Object object, JsonValue jsonMap) {
                if (jsonMap.has(parentFieldName)) {
                    String parentName = (String) readValue(parentFieldName, String.class, jsonMap);
                    Class parentType = object.getClass();
                    do {
                        try {
                            copyFields(Skin.this.get(parentName, parentType), object);
                        } catch (GdxRuntimeException e) {
                            parentType = parentType.getSuperclass();
                            if (parentType == Object.class) {
                                SerializationException se = new SerializationException("Unable to find parent resource with name: " + parentName);
                                se.addTrace(jsonMap.child.trace());
                                throw se;
                            }
                        }
                    } while (parentType == Object.class);
                    SerializationException se2 = new SerializationException("Unable to find parent resource with name: " + parentName);
                    se2.addTrace(jsonMap.child.trace());
                    throw se2;
                }
                super.readFields(object, jsonMap);
            }
        };
        json.setTypeName((String) null);
        json.setUsePrototypes(false);
        json.setSerializer(Skin.class, new Json.ReadOnlySerializer<Skin>() {
            public Skin read(Json json, JsonValue typeToValueMap, Class ignored) {
                JsonValue valueMap = typeToValueMap.child;
                while (valueMap != null) {
                    try {
                        Class type = json.getClass(valueMap.name());
                        if (type == null) {
                            type = ClassReflection.forName(valueMap.name());
                        }
                        readNamedObjects(json, type, valueMap);
                        valueMap = valueMap.next;
                    } catch (ReflectionException ex) {
                        throw new SerializationException((Throwable) ex);
                    }
                }
                return this;
            }

            private void readNamedObjects(Json json, Class type, JsonValue valueMap) {
                Class addType = type == TintedDrawable.class ? Drawable.class : type;
                for (JsonValue valueEntry = valueMap.child; valueEntry != null; valueEntry = valueEntry.next) {
                    Object object = json.readValue(type, valueEntry);
                    if (object != null) {
                        try {
                            Skin.this.add(valueEntry.name, object, addType);
                            if (addType != Drawable.class && ClassReflection.isAssignableFrom(Drawable.class, addType)) {
                                Skin.this.add(valueEntry.name, object, Drawable.class);
                            }
                        } catch (Exception ex) {
                            throw new SerializationException("Error reading " + ClassReflection.getSimpleName(type) + ": " + valueEntry.name, ex);
                        }
                    }
                }
            }
        });
        json.setSerializer(BitmapFont.class, new Json.ReadOnlySerializer<BitmapFont>() {
            public BitmapFont read(Json json, JsonValue jsonData, Class type) {
                BitmapFont font;
                String path = (String) json.readValue("file", String.class, jsonData);
                int scaledSize = ((Integer) json.readValue("scaledSize", Integer.TYPE, -1, jsonData)).intValue();
                Boolean flip = (Boolean) json.readValue("flip", Boolean.class, false, jsonData);
                Boolean markupEnabled = (Boolean) json.readValue("markupEnabled", Boolean.class, false, jsonData);
                FileHandle fontFile = skinFile.parent().child(path);
                if (!fontFile.exists()) {
                    fontFile = Gdx.files.internal(path);
                }
                if (fontFile.exists()) {
                    String regionName = fontFile.nameWithoutExtension();
                    try {
                        Array<TextureRegion> regions = this.getRegions(regionName);
                        if (regions != null) {
                            font = new BitmapFont(new BitmapFont.BitmapFontData(fontFile, flip.booleanValue()), regions, true);
                        } else {
                            TextureRegion region = (TextureRegion) this.optional(regionName, TextureRegion.class);
                            if (region != null) {
                                font = new BitmapFont(fontFile, region, flip.booleanValue());
                            } else {
                                FileHandle parent = fontFile.parent();
                                FileHandle imageFile = parent.child(regionName + ".png");
                                if (imageFile.exists()) {
                                    font = new BitmapFont(fontFile, imageFile, flip.booleanValue());
                                } else {
                                    font = new BitmapFont(fontFile, flip.booleanValue());
                                }
                            }
                        }
                        font.getData().markupEnabled = markupEnabled.booleanValue();
                        if (scaledSize != -1) {
                            font.getData().setScale(((float) scaledSize) / font.getCapHeight());
                        }
                        return font;
                    } catch (RuntimeException ex) {
                        throw new SerializationException("Error loading bitmap font: " + fontFile, ex);
                    }
                } else {
                    throw new SerializationException("Font file not found: " + fontFile);
                }
            }
        });
        json.setSerializer(Color.class, new Json.ReadOnlySerializer<Color>() {
            public Color read(Json json, JsonValue jsonData, Class type) {
                if (jsonData.isString()) {
                    return (Color) Skin.this.get(jsonData.asString(), Color.class);
                }
                String hex = (String) json.readValue("hex", String.class, null, jsonData);
                if (hex != null) {
                    return Color.valueOf(hex);
                }
                return new Color(((Float) json.readValue("r", Float.TYPE, Float.valueOf(0.0f), jsonData)).floatValue(), ((Float) json.readValue("g", Float.TYPE, Float.valueOf(0.0f), jsonData)).floatValue(), ((Float) json.readValue("b", Float.TYPE, Float.valueOf(0.0f), jsonData)).floatValue(), ((Float) json.readValue("a", Float.TYPE, Float.valueOf(1.0f), jsonData)).floatValue());
            }
        });
        json.setSerializer(TintedDrawable.class, new Json.ReadOnlySerializer() {
            public Object read(Json json, JsonValue jsonData, Class type) {
                String name = (String) json.readValue("name", String.class, jsonData);
                Color color = (Color) json.readValue("color", Color.class, jsonData);
                if (color != null) {
                    Drawable drawable = Skin.this.newDrawable(name, color);
                    if (drawable instanceof BaseDrawable) {
                        ((BaseDrawable) drawable).setName(jsonData.name + " (" + name + ", " + color + ")");
                    }
                    return drawable;
                }
                throw new SerializationException("TintedDrawable missing color: " + jsonData);
            }
        });
        ObjectMap.Entries<String, Class> it = this.jsonClassTags.iterator();
        while (it.hasNext()) {
            ObjectMap.Entry<String, Class> entry = (ObjectMap.Entry) it.next();
            json.addClassTag((String) entry.key, (Class) entry.value);
        }
        return json;
    }

    public ObjectMap<String, Class> getJsonClassTags() {
        return this.jsonClassTags;
    }

    private static Method findMethod(Class type, String name) {
        for (Method method : ClassReflection.getMethods(type)) {
            if (method.getName().equals(name)) {
                return method;
            }
        }
        return null;
    }
}
