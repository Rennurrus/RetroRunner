package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class BitmapFont implements Disposable {
    private static final int LOG2_PAGE_SIZE = 9;
    private static final int PAGES = 128;
    private static final int PAGE_SIZE = 512;
    private final BitmapFontCache cache;
    final BitmapFontData data;
    private boolean flipped;
    boolean integer;
    private boolean ownsTexture;
    Array<TextureRegion> regions;

    public BitmapFont() {
        this(Gdx.files.classpath("com/badlogic/gdx/utils/arial-15.fnt"), Gdx.files.classpath("com/badlogic/gdx/utils/arial-15.png"), false, true);
    }

    public BitmapFont(boolean flip) {
        this(Gdx.files.classpath("com/badlogic/gdx/utils/arial-15.fnt"), Gdx.files.classpath("com/badlogic/gdx/utils/arial-15.png"), flip, true);
    }

    public BitmapFont(FileHandle fontFile, TextureRegion region) {
        this(fontFile, region, false);
    }

    public BitmapFont(FileHandle fontFile, TextureRegion region, boolean flip) {
        this(new BitmapFontData(fontFile, flip), region, true);
    }

    public BitmapFont(FileHandle fontFile) {
        this(fontFile, false);
    }

    public BitmapFont(FileHandle fontFile, boolean flip) {
        this(new BitmapFontData(fontFile, flip), (TextureRegion) null, true);
    }

    public BitmapFont(FileHandle fontFile, FileHandle imageFile, boolean flip) {
        this(fontFile, imageFile, flip, true);
    }

    public BitmapFont(FileHandle fontFile, FileHandle imageFile, boolean flip, boolean integer2) {
        this(new BitmapFontData(fontFile, flip), new TextureRegion(new Texture(imageFile, false)), integer2);
        this.ownsTexture = true;
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public BitmapFont(com.badlogic.gdx.graphics.g2d.BitmapFont.BitmapFontData r3, com.badlogic.gdx.graphics.g2d.TextureRegion r4, boolean r5) {
        /*
            r2 = this;
            if (r4 == 0) goto L_0x000d
            r0 = 1
            com.badlogic.gdx.graphics.g2d.TextureRegion[] r0 = new com.badlogic.gdx.graphics.g2d.TextureRegion[r0]
            r1 = 0
            r0[r1] = r4
            com.badlogic.gdx.utils.Array r0 = com.badlogic.gdx.utils.Array.with(r0)
            goto L_0x000e
        L_0x000d:
            r0 = 0
        L_0x000e:
            r2.<init>((com.badlogic.gdx.graphics.g2d.BitmapFont.BitmapFontData) r3, (com.badlogic.gdx.utils.Array<com.badlogic.gdx.graphics.g2d.TextureRegion>) r0, (boolean) r5)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.graphics.g2d.BitmapFont.<init>(com.badlogic.gdx.graphics.g2d.BitmapFont$BitmapFontData, com.badlogic.gdx.graphics.g2d.TextureRegion, boolean):void");
    }

    public BitmapFont(BitmapFontData data2, Array<TextureRegion> pageRegions, boolean integer2) {
        FileHandle file;
        this.flipped = data2.flipped;
        this.data = data2;
        this.integer = integer2;
        if (pageRegions != null && pageRegions.size != 0) {
            this.regions = pageRegions;
            this.ownsTexture = false;
        } else if (data2.imagePaths != null) {
            int n = data2.imagePaths.length;
            this.regions = new Array<>(n);
            for (int i = 0; i < n; i++) {
                if (data2.fontFile == null) {
                    file = Gdx.files.internal(data2.imagePaths[i]);
                } else {
                    file = Gdx.files.getFileHandle(data2.imagePaths[i], data2.fontFile.type());
                }
                this.regions.add(new TextureRegion(new Texture(file, false)));
            }
            this.ownsTexture = true;
        } else {
            throw new IllegalArgumentException("If no regions are specified, the font data must have an images path.");
        }
        this.cache = newFontCache();
        load(data2);
    }

    /* access modifiers changed from: protected */
    public void load(BitmapFontData data2) {
        for (Glyph[] page : data2.glyphs) {
            if (page != null) {
                for (Glyph glyph : page) {
                    if (glyph != null) {
                        data2.setGlyphRegion(glyph, this.regions.get(glyph.page));
                    }
                }
            }
        }
        if (data2.missingGlyph != null) {
            data2.setGlyphRegion(data2.missingGlyph, this.regions.get(data2.missingGlyph.page));
        }
    }

    public GlyphLayout draw(Batch batch, CharSequence str, float x, float y) {
        this.cache.clear();
        GlyphLayout layout = this.cache.addText(str, x, y);
        this.cache.draw(batch);
        return layout;
    }

    public GlyphLayout draw(Batch batch, CharSequence str, float x, float y, float targetWidth, int halign, boolean wrap) {
        this.cache.clear();
        GlyphLayout layout = this.cache.addText(str, x, y, targetWidth, halign, wrap);
        this.cache.draw(batch);
        return layout;
    }

    public GlyphLayout draw(Batch batch, CharSequence str, float x, float y, int start, int end, float targetWidth, int halign, boolean wrap) {
        this.cache.clear();
        GlyphLayout layout = this.cache.addText(str, x, y, start, end, targetWidth, halign, wrap);
        Batch batch2 = batch;
        this.cache.draw(batch);
        return layout;
    }

    public GlyphLayout draw(Batch batch, CharSequence str, float x, float y, int start, int end, float targetWidth, int halign, boolean wrap, String truncate) {
        this.cache.clear();
        GlyphLayout layout = this.cache.addText(str, x, y, start, end, targetWidth, halign, wrap, truncate);
        Batch batch2 = batch;
        this.cache.draw(batch);
        return layout;
    }

    public void draw(Batch batch, GlyphLayout layout, float x, float y) {
        this.cache.clear();
        this.cache.addText(layout, x, y);
        this.cache.draw(batch);
    }

    public Color getColor() {
        return this.cache.getColor();
    }

    public void setColor(Color color) {
        this.cache.getColor().set(color);
    }

    public void setColor(float r, float g, float b, float a) {
        this.cache.getColor().set(r, g, b, a);
    }

    public float getScaleX() {
        return this.data.scaleX;
    }

    public float getScaleY() {
        return this.data.scaleY;
    }

    public TextureRegion getRegion() {
        return this.regions.first();
    }

    public Array<TextureRegion> getRegions() {
        return this.regions;
    }

    public TextureRegion getRegion(int index) {
        return this.regions.get(index);
    }

    public float getLineHeight() {
        return this.data.lineHeight;
    }

    public float getSpaceXadvance() {
        return this.data.spaceXadvance;
    }

    public float getXHeight() {
        return this.data.xHeight;
    }

    public float getCapHeight() {
        return this.data.capHeight;
    }

    public float getAscent() {
        return this.data.ascent;
    }

    public float getDescent() {
        return this.data.descent;
    }

    public boolean isFlipped() {
        return this.flipped;
    }

    public void dispose() {
        if (this.ownsTexture) {
            for (int i = 0; i < this.regions.size; i++) {
                this.regions.get(i).getTexture().dispose();
            }
        }
    }

    public void setFixedWidthGlyphs(CharSequence glyphs) {
        BitmapFontData data2 = this.data;
        int maxAdvance = 0;
        int end = glyphs.length();
        for (int index = 0; index < end; index++) {
            Glyph g = data2.getGlyph(glyphs.charAt(index));
            if (g != null && g.xadvance > maxAdvance) {
                maxAdvance = g.xadvance;
            }
        }
        int end2 = glyphs.length();
        for (int index2 = 0; index2 < end2; index2++) {
            Glyph g2 = data2.getGlyph(glyphs.charAt(index2));
            if (g2 != null) {
                g2.xoffset += Math.round((float) ((maxAdvance - g2.xadvance) / 2));
                g2.xadvance = maxAdvance;
                g2.kerning = null;
                g2.fixedWidth = true;
            }
        }
    }

    public void setUseIntegerPositions(boolean integer2) {
        this.integer = integer2;
        this.cache.setUseIntegerPositions(integer2);
    }

    public boolean usesIntegerPositions() {
        return this.integer;
    }

    public BitmapFontCache getCache() {
        return this.cache;
    }

    public BitmapFontData getData() {
        return this.data;
    }

    public boolean ownsTexture() {
        return this.ownsTexture;
    }

    public void setOwnsTexture(boolean ownsTexture2) {
        this.ownsTexture = ownsTexture2;
    }

    public BitmapFontCache newFontCache() {
        return new BitmapFontCache(this, this.integer);
    }

    public String toString() {
        return this.data.name != null ? this.data.name : super.toString();
    }

    public static class Glyph {
        public boolean fixedWidth;
        public int height;
        public int id;
        public byte[][] kerning;
        public int page = 0;
        public int srcX;
        public int srcY;
        public float u;
        public float u2;
        public float v;
        public float v2;
        public int width;
        public int xadvance;
        public int xoffset;
        public int yoffset;

        public int getKerning(char ch) {
            byte[] page2;
            byte[][] bArr = this.kerning;
            if (bArr == null || (page2 = bArr[ch >>> 9]) == null) {
                return 0;
            }
            return page2[ch & 511];
        }

        public void setKerning(int ch, int value) {
            if (this.kerning == null) {
                this.kerning = new byte[128][];
            }
            byte[][] bArr = this.kerning;
            byte[] page2 = bArr[ch >>> 9];
            if (page2 == null) {
                byte[] bArr2 = new byte[512];
                page2 = bArr2;
                bArr[ch >>> 9] = bArr2;
            }
            page2[ch & 511] = (byte) value;
        }

        public String toString() {
            return Character.toString((char) this.id);
        }
    }

    static int indexOf(CharSequence text, char ch, int start) {
        int n = text.length();
        while (start < n) {
            if (text.charAt(start) == ch) {
                return start;
            }
            start++;
        }
        return n;
    }

    public static class BitmapFontData {
        public float ascent;
        public float blankLineScale = 1.0f;
        public char[] breakChars;
        public char[] capChars = {'M', 'N', 'B', 'D', 'C', 'E', 'F', 'K', 'A', 'G', 'H', 'I', 'J', 'L', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        public float capHeight = 1.0f;
        public float cursorX;
        public float descent;
        public float down;
        public boolean flipped;
        public FileHandle fontFile;
        public final Glyph[][] glyphs = new Glyph[128][];
        public String[] imagePaths;
        public float lineHeight;
        public boolean markupEnabled;
        public Glyph missingGlyph;
        public String name;
        public float padBottom;
        public float padLeft;
        public float padRight;
        public float padTop;
        public float scaleX = 1.0f;
        public float scaleY = 1.0f;
        public float spaceXadvance;
        public char[] xChars = {'x', 'e', 'a', 'o', 'n', 's', 'r', 'c', 'u', 'm', 'v', 'w', 'z'};
        public float xHeight = 1.0f;

        public BitmapFontData() {
        }

        public BitmapFontData(FileHandle fontFile2, boolean flip) {
            this.fontFile = fontFile2;
            this.flipped = flip;
            load(fontFile2, flip);
        }

        /* JADX WARNING: Removed duplicated region for block: B:101:0x0279 A[SYNTHETIC, Splitter:B:101:0x0279] */
        /* JADX WARNING: Removed duplicated region for block: B:107:0x029b A[SYNTHETIC, Splitter:B:107:0x029b] */
        /* JADX WARNING: Removed duplicated region for block: B:113:0x02b3 A[Catch:{ NumberFormatException -> 0x0124, Exception -> 0x00de, all -> 0x00d6 }] */
        /* JADX WARNING: Removed duplicated region for block: B:115:0x02ba  */
        /* JADX WARNING: Removed duplicated region for block: B:120:0x02cd A[SYNTHETIC, Splitter:B:120:0x02cd] */
        /* JADX WARNING: Removed duplicated region for block: B:128:0x02e9 A[SYNTHETIC, Splitter:B:128:0x02e9] */
        /* JADX WARNING: Removed duplicated region for block: B:152:0x0353 A[Catch:{ Exception -> 0x0516 }] */
        /* JADX WARNING: Removed duplicated region for block: B:155:0x0370 A[Catch:{ Exception -> 0x0516 }] */
        /* JADX WARNING: Removed duplicated region for block: B:157:0x037c A[Catch:{ Exception -> 0x0516 }] */
        /* JADX WARNING: Removed duplicated region for block: B:158:0x038f  */
        /* JADX WARNING: Removed duplicated region for block: B:252:0x02b1 A[EDGE_INSN: B:252:0x02b1->B:112:0x02b1 ?: BREAK  , SYNTHETIC] */
        /* JADX WARNING: Removed duplicated region for block: B:254:0x02e5 A[SYNTHETIC] */
        /* JADX WARNING: Removed duplicated region for block: B:92:0x024a A[SYNTHETIC, Splitter:B:92:0x024a] */
        /* JADX WARNING: Removed duplicated region for block: B:97:0x0271  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void load(com.badlogic.gdx.files.FileHandle r30, boolean r31) {
            /*
                r29 = this;
                r1 = r29
                java.lang.String[] r2 = r1.imagePaths
                if (r2 != 0) goto L_0x0548
                java.lang.String r2 = r30.nameWithoutExtension()
                r1.name = r2
                java.io.BufferedReader r2 = new java.io.BufferedReader
                java.io.InputStreamReader r3 = new java.io.InputStreamReader
                java.io.InputStream r4 = r30.read()
                r3.<init>(r4)
                r4 = 512(0x200, float:7.175E-43)
                r2.<init>(r3, r4)
                java.lang.String r3 = r2.readLine()     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                if (r3 == 0) goto L_0x050c
                java.lang.String r4 = "padding="
                int r4 = r3.indexOf(r4)     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                int r4 = r4 + 8
                java.lang.String r4 = r3.substring(r4)     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                r3 = r4
                r4 = 32
                int r5 = r3.indexOf(r4)     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                r6 = 0
                java.lang.String r5 = r3.substring(r6, r5)     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                java.lang.String r7 = ","
                r8 = 4
                java.lang.String[] r5 = r5.split(r7, r8)     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                int r7 = r5.length     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                if (r7 != r8) goto L_0x0500
                r7 = r5[r6]     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                int r7 = java.lang.Integer.parseInt(r7)     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                float r7 = (float) r7     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                r1.padTop = r7     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                r7 = 1
                r8 = r5[r7]     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                int r8 = java.lang.Integer.parseInt(r8)     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                float r8 = (float) r8     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                r1.padRight = r8     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                r8 = 2
                r9 = r5[r8]     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                int r9 = java.lang.Integer.parseInt(r9)     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                float r9 = (float) r9     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                r1.padBottom = r9     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                r9 = 3
                r10 = r5[r9]     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                int r10 = java.lang.Integer.parseInt(r10)     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                float r10 = (float) r10     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                r1.padLeft = r10     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                float r10 = r1.padTop     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                float r11 = r1.padBottom     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                float r10 = r10 + r11
                java.lang.String r11 = r2.readLine()     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                r3 = r11
                if (r3 == 0) goto L_0x04f4
                java.lang.String r11 = " "
                r12 = 9
                java.lang.String[] r11 = r3.split(r11, r12)     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                int r12 = r11.length     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                if (r12 < r9) goto L_0x04e6
                r9 = r11[r7]     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                java.lang.String r12 = "lineHeight="
                boolean r9 = r9.startsWith(r12)     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                if (r9 == 0) goto L_0x04d8
                r9 = r11[r7]     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                r12 = 11
                java.lang.String r9 = r9.substring(r12)     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                int r9 = java.lang.Integer.parseInt(r9)     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                float r9 = (float) r9     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                r1.lineHeight = r9     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                r9 = r11[r8]     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                java.lang.String r12 = "base="
                boolean r9 = r9.startsWith(r12)     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                if (r9 == 0) goto L_0x04ca
                r8 = r11[r8]     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                r9 = 5
                java.lang.String r8 = r8.substring(r9)     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                int r8 = java.lang.Integer.parseInt(r8)     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                float r8 = (float) r8     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                r12 = 1
                int r13 = r11.length     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                r14 = 6
                if (r13 < r14) goto L_0x00e4
                r13 = r11[r9]     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                if (r13 == 0) goto L_0x00e4
                r13 = r11[r9]     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                java.lang.String r15 = "pages="
                boolean r13 = r13.startsWith(r15)     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                if (r13 == 0) goto L_0x00e4
                r9 = r11[r9]     // Catch:{ NumberFormatException -> 0x00d4 }
                java.lang.String r9 = r9.substring(r14)     // Catch:{ NumberFormatException -> 0x00d4 }
                int r9 = java.lang.Integer.parseInt(r9)     // Catch:{ NumberFormatException -> 0x00d4 }
                int r9 = java.lang.Math.max(r7, r9)     // Catch:{ NumberFormatException -> 0x00d4 }
                r12 = r9
                goto L_0x00e4
            L_0x00d4:
                r0 = move-exception
                goto L_0x00e4
            L_0x00d6:
                r0 = move-exception
                r5 = r30
                r22 = r2
                r2 = r0
                goto L_0x0544
            L_0x00de:
                r0 = move-exception
                r22 = r2
                r2 = r0
                goto L_0x0524
            L_0x00e4:
                java.lang.String[] r9 = new java.lang.String[r12]     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                r1.imagePaths = r9     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                r9 = 0
            L_0x00e9:
                if (r9 >= r12) goto L_0x0182
                java.lang.String r13 = r2.readLine()     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                r3 = r13
                if (r3 == 0) goto L_0x017a
                java.lang.String r13 = ".*id=(\\d+)"
                java.util.regex.Pattern r13 = java.util.regex.Pattern.compile(r13)     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                java.util.regex.Matcher r13 = r13.matcher(r3)     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                boolean r14 = r13.find()     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                if (r14 == 0) goto L_0x013d
                java.lang.String r14 = r13.group(r7)     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                int r15 = java.lang.Integer.parseInt(r14)     // Catch:{ NumberFormatException -> 0x0124 }
                if (r15 != r9) goto L_0x010d
                goto L_0x013d
            L_0x010d:
                com.badlogic.gdx.utils.GdxRuntimeException r4 = new com.badlogic.gdx.utils.GdxRuntimeException     // Catch:{ NumberFormatException -> 0x0124 }
                java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ NumberFormatException -> 0x0124 }
                r6.<init>()     // Catch:{ NumberFormatException -> 0x0124 }
                java.lang.String r7 = "Page IDs must be indices starting at 0: "
                r6.append(r7)     // Catch:{ NumberFormatException -> 0x0124 }
                r6.append(r14)     // Catch:{ NumberFormatException -> 0x0124 }
                java.lang.String r6 = r6.toString()     // Catch:{ NumberFormatException -> 0x0124 }
                r4.<init>((java.lang.String) r6)     // Catch:{ NumberFormatException -> 0x0124 }
                throw r4     // Catch:{ NumberFormatException -> 0x0124 }
            L_0x0124:
                r0 = move-exception
                r4 = r0
                com.badlogic.gdx.utils.GdxRuntimeException r6 = new com.badlogic.gdx.utils.GdxRuntimeException     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                r7.<init>()     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                java.lang.String r15 = "Invalid page id: "
                r7.append(r15)     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                r7.append(r14)     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                r6.<init>(r7, r4)     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                throw r6     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
            L_0x013d:
                java.lang.String r14 = ".*file=\"?([^\"]+)\"?"
                java.util.regex.Pattern r14 = java.util.regex.Pattern.compile(r14)     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                java.util.regex.Matcher r14 = r14.matcher(r3)     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                r13 = r14
                boolean r14 = r13.find()     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                if (r14 == 0) goto L_0x0172
                java.lang.String r14 = r13.group(r7)     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                java.lang.String[] r15 = r1.imagePaths     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                com.badlogic.gdx.files.FileHandle r6 = r30.parent()     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                com.badlogic.gdx.files.FileHandle r6 = r6.child(r14)     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                java.lang.String r6 = r6.path()     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                java.lang.String r7 = "\\\\"
                java.lang.String r4 = "/"
                java.lang.String r4 = r6.replaceAll(r7, r4)     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                r15[r9] = r4     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                int r9 = r9 + 1
                r4 = 32
                r6 = 0
                r7 = 1
                goto L_0x00e9
            L_0x0172:
                com.badlogic.gdx.utils.GdxRuntimeException r4 = new com.badlogic.gdx.utils.GdxRuntimeException     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                java.lang.String r6 = "Missing: file"
                r4.<init>((java.lang.String) r6)     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                throw r4     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
            L_0x017a:
                com.badlogic.gdx.utils.GdxRuntimeException r4 = new com.badlogic.gdx.utils.GdxRuntimeException     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                java.lang.String r6 = "Missing additional page definitions."
                r4.<init>((java.lang.String) r6)     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                throw r4     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
            L_0x0182:
                r4 = 0
                r1.descent = r4     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
            L_0x0185:
                java.lang.String r4 = r2.readLine()     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                r3 = r4
                java.lang.String r4 = "metrics "
                r6 = 65535(0xffff, float:9.1834E-41)
                java.lang.String r7 = " ="
                if (r3 != 0) goto L_0x0194
                goto L_0x01a3
            L_0x0194:
                java.lang.String r9 = "kernings "
                boolean r9 = r3.startsWith(r9)     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                if (r9 == 0) goto L_0x019d
                goto L_0x01a3
            L_0x019d:
                boolean r9 = r3.startsWith(r4)     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                if (r9 == 0) goto L_0x03ed
            L_0x01a3:
                float r9 = r1.descent     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                float r13 = r1.padBottom     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                float r9 = r9 + r13
                r1.descent = r9     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
            L_0x01aa:
                java.lang.String r9 = r2.readLine()     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                r3 = r9
                if (r3 != 0) goto L_0x01b2
                goto L_0x01ba
            L_0x01b2:
                java.lang.String r9 = "kerning "
                boolean r9 = r3.startsWith(r9)     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                if (r9 != 0) goto L_0x0398
            L_0x01ba:
                r6 = 0
                r9 = 0
                r13 = 0
                r14 = 0
                r15 = 0
                r16 = 0
                r18 = 0
                r19 = 0
                if (r3 == 0) goto L_0x0236
                boolean r4 = r3.startsWith(r4)     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                if (r4 == 0) goto L_0x0236
                r6 = 1
                java.util.StringTokenizer r4 = new java.util.StringTokenizer     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                r4.<init>(r3, r7)     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                r4.nextToken()     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                r4.nextToken()     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                java.lang.String r7 = r4.nextToken()     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                float r7 = java.lang.Float.parseFloat(r7)     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                r9 = r7
                r4.nextToken()     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                java.lang.String r7 = r4.nextToken()     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                float r7 = java.lang.Float.parseFloat(r7)     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                r13 = r7
                r4.nextToken()     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                java.lang.String r7 = r4.nextToken()     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                float r7 = java.lang.Float.parseFloat(r7)     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                r14 = r7
                r4.nextToken()     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                java.lang.String r7 = r4.nextToken()     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                float r7 = java.lang.Float.parseFloat(r7)     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                r15 = r7
                r4.nextToken()     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                java.lang.String r7 = r4.nextToken()     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                float r7 = java.lang.Float.parseFloat(r7)     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                r16 = r7
                r4.nextToken()     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                java.lang.String r7 = r4.nextToken()     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                float r7 = java.lang.Float.parseFloat(r7)     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                r18 = r7
                r4.nextToken()     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                java.lang.String r7 = r4.nextToken()     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                float r7 = java.lang.Float.parseFloat(r7)     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                r19 = r7
                r4 = r16
                r7 = r18
                r16 = r5
                r5 = r19
                goto L_0x023e
            L_0x0236:
                r4 = r16
                r7 = r18
                r16 = r5
                r5 = r19
            L_0x023e:
                r18 = r11
                r11 = 32
                com.badlogic.gdx.graphics.g2d.BitmapFont$Glyph r19 = r1.getGlyph(r11)     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                r11 = r19
                if (r11 != 0) goto L_0x0271
                com.badlogic.gdx.graphics.g2d.BitmapFont$Glyph r19 = new com.badlogic.gdx.graphics.g2d.BitmapFont$Glyph     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                r19.<init>()     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                r11 = r19
                r19 = r12
                r12 = 32
                r11.id = r12     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                r12 = 108(0x6c, float:1.51E-43)
                com.badlogic.gdx.graphics.g2d.BitmapFont$Glyph r12 = r1.getGlyph(r12)     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                if (r12 != 0) goto L_0x0265
                com.badlogic.gdx.graphics.g2d.BitmapFont$Glyph r20 = r29.getFirstGlyph()     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                r12 = r20
            L_0x0265:
                r20 = r3
                int r3 = r12.xadvance     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                r11.xadvance = r3     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                r3 = 32
                r1.setGlyph(r3, r11)     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                goto L_0x0275
            L_0x0271:
                r20 = r3
                r19 = r12
            L_0x0275:
                int r3 = r11.width     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                if (r3 != 0) goto L_0x028b
                float r3 = r1.padLeft     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                int r12 = r11.xadvance     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                float r12 = (float) r12     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                float r3 = r3 + r12
                float r12 = r1.padRight     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                float r3 = r3 + r12
                int r3 = (int) r3     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                r11.width = r3     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                float r3 = r1.padLeft     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                float r3 = -r3
                int r3 = (int) r3     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                r11.xoffset = r3     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
            L_0x028b:
                int r3 = r11.xadvance     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                float r3 = (float) r3     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                r1.spaceXadvance = r3     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                r3 = 0
                char[] r12 = r1.xChars     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                r17 = r3
                int r3 = r12.length     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                r21 = r11
                r11 = 0
            L_0x0299:
                if (r11 >= r3) goto L_0x02b1
                char r22 = r12[r11]     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                r23 = r22
                r22 = r3
                r3 = r23
                com.badlogic.gdx.graphics.g2d.BitmapFont$Glyph r23 = r1.getGlyph(r3)     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                r17 = r23
                if (r17 == 0) goto L_0x02ac
                goto L_0x02b1
            L_0x02ac:
                int r11 = r11 + 1
                r3 = r22
                goto L_0x0299
            L_0x02b1:
                if (r17 != 0) goto L_0x02ba
                com.badlogic.gdx.graphics.g2d.BitmapFont$Glyph r3 = r29.getFirstGlyph()     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                r17 = r3
                goto L_0x02bc
            L_0x02ba:
                r3 = r17
            L_0x02bc:
                int r11 = r3.height     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                float r11 = (float) r11     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                float r11 = r11 - r10
                r1.xHeight = r11     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                r11 = 0
                char[] r12 = r1.capChars     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                r17 = r3
                int r3 = r12.length     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                r22 = r11
                r11 = 0
            L_0x02cb:
                if (r11 >= r3) goto L_0x02e5
                char r23 = r12[r11]     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                r24 = r23
                r23 = r3
                r3 = r24
                com.badlogic.gdx.graphics.g2d.BitmapFont$Glyph r24 = r1.getGlyph(r3)     // Catch:{ Exception -> 0x00de, all -> 0x00d6 }
                r22 = r24
                if (r22 == 0) goto L_0x02e0
                r3 = r22
                goto L_0x02e7
            L_0x02e0:
                int r11 = r11 + 1
                r3 = r23
                goto L_0x02cb
            L_0x02e5:
                r3 = r22
            L_0x02e7:
                if (r3 != 0) goto L_0x0353
                com.badlogic.gdx.graphics.g2d.BitmapFont$Glyph[][] r11 = r1.glyphs     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                int r12 = r11.length     // Catch:{ Exception -> 0x0520, all -> 0x0519 }
                r22 = r2
                r2 = 0
            L_0x02ef:
                if (r2 >= r12) goto L_0x034e
                r23 = r11[r2]     // Catch:{ Exception -> 0x0516 }
                r24 = r23
                r23 = r11
                r11 = r24
                if (r11 != 0) goto L_0x0302
                r25 = r5
                r28 = r7
                r24 = r12
                goto L_0x0343
            L_0x0302:
                r24 = r12
                int r12 = r11.length     // Catch:{ Exception -> 0x0516 }
                r25 = r5
                r5 = 0
            L_0x0308:
                if (r5 >= r12) goto L_0x033f
                r26 = r11[r5]     // Catch:{ Exception -> 0x0516 }
                r27 = r26
                r26 = r11
                r11 = r27
                if (r11 == 0) goto L_0x0332
                r27 = r12
                int r12 = r11.height     // Catch:{ Exception -> 0x0516 }
                if (r12 == 0) goto L_0x032f
                int r12 = r11.width     // Catch:{ Exception -> 0x0516 }
                if (r12 != 0) goto L_0x0321
                r28 = r7
                goto L_0x0336
            L_0x0321:
                float r12 = r1.capHeight     // Catch:{ Exception -> 0x0516 }
                r28 = r7
                int r7 = r11.height     // Catch:{ Exception -> 0x0516 }
                float r7 = (float) r7     // Catch:{ Exception -> 0x0516 }
                float r7 = java.lang.Math.max(r12, r7)     // Catch:{ Exception -> 0x0516 }
                r1.capHeight = r7     // Catch:{ Exception -> 0x0516 }
                goto L_0x0336
            L_0x032f:
                r28 = r7
                goto L_0x0336
            L_0x0332:
                r28 = r7
                r27 = r12
            L_0x0336:
                int r5 = r5 + 1
                r11 = r26
                r12 = r27
                r7 = r28
                goto L_0x0308
            L_0x033f:
                r28 = r7
                r26 = r11
            L_0x0343:
                int r2 = r2 + 1
                r11 = r23
                r12 = r24
                r5 = r25
                r7 = r28
                goto L_0x02ef
            L_0x034e:
                r25 = r5
                r28 = r7
                goto L_0x035e
            L_0x0353:
                r22 = r2
                r25 = r5
                r28 = r7
                int r2 = r3.height     // Catch:{ Exception -> 0x0516 }
                float r2 = (float) r2     // Catch:{ Exception -> 0x0516 }
                r1.capHeight = r2     // Catch:{ Exception -> 0x0516 }
            L_0x035e:
                float r2 = r1.capHeight     // Catch:{ Exception -> 0x0516 }
                float r2 = r2 - r10
                r1.capHeight = r2     // Catch:{ Exception -> 0x0516 }
                float r2 = r1.capHeight     // Catch:{ Exception -> 0x0516 }
                float r2 = r8 - r2
                r1.ascent = r2     // Catch:{ Exception -> 0x0516 }
                float r2 = r1.lineHeight     // Catch:{ Exception -> 0x0516 }
                float r2 = -r2
                r1.down = r2     // Catch:{ Exception -> 0x0516 }
                if (r31 == 0) goto L_0x037a
                float r2 = r1.ascent     // Catch:{ Exception -> 0x0516 }
                float r2 = -r2
                r1.ascent = r2     // Catch:{ Exception -> 0x0516 }
                float r2 = r1.down     // Catch:{ Exception -> 0x0516 }
                float r2 = -r2
                r1.down = r2     // Catch:{ Exception -> 0x0516 }
            L_0x037a:
                if (r6 == 0) goto L_0x038f
                r1.ascent = r9     // Catch:{ Exception -> 0x0516 }
                r1.descent = r13     // Catch:{ Exception -> 0x0516 }
                r1.down = r14     // Catch:{ Exception -> 0x0516 }
                r1.capHeight = r15     // Catch:{ Exception -> 0x0516 }
                r1.lineHeight = r4     // Catch:{ Exception -> 0x0516 }
                r2 = r28
                r1.spaceXadvance = r2     // Catch:{ Exception -> 0x0516 }
                r5 = r25
                r1.xHeight = r5     // Catch:{ Exception -> 0x0516 }
                goto L_0x0393
            L_0x038f:
                r5 = r25
                r2 = r28
            L_0x0393:
                com.badlogic.gdx.utils.StreamUtils.closeQuietly(r22)
                return
            L_0x0398:
                r22 = r2
                r20 = r3
                r16 = r5
                r18 = r11
                r19 = r12
                r3 = 32
                java.util.StringTokenizer r2 = new java.util.StringTokenizer     // Catch:{ Exception -> 0x0516 }
                r5 = r20
                r2.<init>(r5, r7)     // Catch:{ Exception -> 0x0516 }
                r2.nextToken()     // Catch:{ Exception -> 0x0516 }
                r2.nextToken()     // Catch:{ Exception -> 0x0516 }
                java.lang.String r9 = r2.nextToken()     // Catch:{ Exception -> 0x0516 }
                int r9 = java.lang.Integer.parseInt(r9)     // Catch:{ Exception -> 0x0516 }
                r2.nextToken()     // Catch:{ Exception -> 0x0516 }
                java.lang.String r11 = r2.nextToken()     // Catch:{ Exception -> 0x0516 }
                int r11 = java.lang.Integer.parseInt(r11)     // Catch:{ Exception -> 0x0516 }
                if (r9 < 0) goto L_0x03e2
                if (r9 > r6) goto L_0x03e2
                if (r11 < 0) goto L_0x03e2
                if (r11 <= r6) goto L_0x03cd
                goto L_0x03e2
            L_0x03cd:
                char r12 = (char) r9     // Catch:{ Exception -> 0x0516 }
                com.badlogic.gdx.graphics.g2d.BitmapFont$Glyph r12 = r1.getGlyph(r12)     // Catch:{ Exception -> 0x0516 }
                r2.nextToken()     // Catch:{ Exception -> 0x0516 }
                java.lang.String r13 = r2.nextToken()     // Catch:{ Exception -> 0x0516 }
                int r13 = java.lang.Integer.parseInt(r13)     // Catch:{ Exception -> 0x0516 }
                if (r12 == 0) goto L_0x03e2
                r12.setKerning(r11, r13)     // Catch:{ Exception -> 0x0516 }
            L_0x03e2:
                r3 = r5
                r5 = r16
                r11 = r18
                r12 = r19
                r2 = r22
                goto L_0x01aa
            L_0x03ed:
                r22 = r2
                r16 = r5
                r18 = r11
                r19 = r12
                r2 = 32
                java.lang.String r4 = "char "
                boolean r4 = r3.startsWith(r4)     // Catch:{ Exception -> 0x0516 }
                if (r4 != 0) goto L_0x0401
                goto L_0x04c0
            L_0x0401:
                com.badlogic.gdx.graphics.g2d.BitmapFont$Glyph r4 = new com.badlogic.gdx.graphics.g2d.BitmapFont$Glyph     // Catch:{ Exception -> 0x0516 }
                r4.<init>()     // Catch:{ Exception -> 0x0516 }
                java.util.StringTokenizer r5 = new java.util.StringTokenizer     // Catch:{ Exception -> 0x0516 }
                r5.<init>(r3, r7)     // Catch:{ Exception -> 0x0516 }
                r5.nextToken()     // Catch:{ Exception -> 0x0516 }
                r5.nextToken()     // Catch:{ Exception -> 0x0516 }
                java.lang.String r7 = r5.nextToken()     // Catch:{ Exception -> 0x0516 }
                int r7 = java.lang.Integer.parseInt(r7)     // Catch:{ Exception -> 0x0516 }
                if (r7 > 0) goto L_0x041e
                r1.missingGlyph = r4     // Catch:{ Exception -> 0x0516 }
                goto L_0x0423
            L_0x041e:
                if (r7 > r6) goto L_0x04c0
                r1.setGlyph(r7, r4)     // Catch:{ Exception -> 0x0516 }
            L_0x0423:
                r4.id = r7     // Catch:{ Exception -> 0x0516 }
                r5.nextToken()     // Catch:{ Exception -> 0x0516 }
                java.lang.String r6 = r5.nextToken()     // Catch:{ Exception -> 0x0516 }
                int r6 = java.lang.Integer.parseInt(r6)     // Catch:{ Exception -> 0x0516 }
                r4.srcX = r6     // Catch:{ Exception -> 0x0516 }
                r5.nextToken()     // Catch:{ Exception -> 0x0516 }
                java.lang.String r6 = r5.nextToken()     // Catch:{ Exception -> 0x0516 }
                int r6 = java.lang.Integer.parseInt(r6)     // Catch:{ Exception -> 0x0516 }
                r4.srcY = r6     // Catch:{ Exception -> 0x0516 }
                r5.nextToken()     // Catch:{ Exception -> 0x0516 }
                java.lang.String r6 = r5.nextToken()     // Catch:{ Exception -> 0x0516 }
                int r6 = java.lang.Integer.parseInt(r6)     // Catch:{ Exception -> 0x0516 }
                r4.width = r6     // Catch:{ Exception -> 0x0516 }
                r5.nextToken()     // Catch:{ Exception -> 0x0516 }
                java.lang.String r6 = r5.nextToken()     // Catch:{ Exception -> 0x0516 }
                int r6 = java.lang.Integer.parseInt(r6)     // Catch:{ Exception -> 0x0516 }
                r4.height = r6     // Catch:{ Exception -> 0x0516 }
                r5.nextToken()     // Catch:{ Exception -> 0x0516 }
                java.lang.String r6 = r5.nextToken()     // Catch:{ Exception -> 0x0516 }
                int r6 = java.lang.Integer.parseInt(r6)     // Catch:{ Exception -> 0x0516 }
                r4.xoffset = r6     // Catch:{ Exception -> 0x0516 }
                r5.nextToken()     // Catch:{ Exception -> 0x0516 }
                if (r31 == 0) goto L_0x0476
                java.lang.String r6 = r5.nextToken()     // Catch:{ Exception -> 0x0516 }
                int r6 = java.lang.Integer.parseInt(r6)     // Catch:{ Exception -> 0x0516 }
                r4.yoffset = r6     // Catch:{ Exception -> 0x0516 }
                goto L_0x0484
            L_0x0476:
                int r6 = r4.height     // Catch:{ Exception -> 0x0516 }
                java.lang.String r9 = r5.nextToken()     // Catch:{ Exception -> 0x0516 }
                int r9 = java.lang.Integer.parseInt(r9)     // Catch:{ Exception -> 0x0516 }
                int r6 = r6 + r9
                int r6 = -r6
                r4.yoffset = r6     // Catch:{ Exception -> 0x0516 }
            L_0x0484:
                r5.nextToken()     // Catch:{ Exception -> 0x0516 }
                java.lang.String r6 = r5.nextToken()     // Catch:{ Exception -> 0x0516 }
                int r6 = java.lang.Integer.parseInt(r6)     // Catch:{ Exception -> 0x0516 }
                r4.xadvance = r6     // Catch:{ Exception -> 0x0516 }
                boolean r6 = r5.hasMoreTokens()     // Catch:{ Exception -> 0x0516 }
                if (r6 == 0) goto L_0x049a
                r5.nextToken()     // Catch:{ Exception -> 0x0516 }
            L_0x049a:
                boolean r6 = r5.hasMoreTokens()     // Catch:{ Exception -> 0x0516 }
                if (r6 == 0) goto L_0x04ac
                java.lang.String r6 = r5.nextToken()     // Catch:{ NumberFormatException -> 0x04ab }
                int r6 = java.lang.Integer.parseInt(r6)     // Catch:{ NumberFormatException -> 0x04ab }
                r4.page = r6     // Catch:{ NumberFormatException -> 0x04ab }
                goto L_0x04ac
            L_0x04ab:
                r0 = move-exception
            L_0x04ac:
                int r6 = r4.width     // Catch:{ Exception -> 0x0516 }
                if (r6 <= 0) goto L_0x04c0
                int r6 = r4.height     // Catch:{ Exception -> 0x0516 }
                if (r6 <= 0) goto L_0x04c0
                int r6 = r4.yoffset     // Catch:{ Exception -> 0x0516 }
                float r6 = (float) r6     // Catch:{ Exception -> 0x0516 }
                float r6 = r6 + r8
                float r9 = r1.descent     // Catch:{ Exception -> 0x0516 }
                float r6 = java.lang.Math.min(r6, r9)     // Catch:{ Exception -> 0x0516 }
                r1.descent = r6     // Catch:{ Exception -> 0x0516 }
            L_0x04c0:
                r5 = r16
                r11 = r18
                r12 = r19
                r2 = r22
                goto L_0x0185
            L_0x04ca:
                r22 = r2
                r16 = r5
                r18 = r11
                com.badlogic.gdx.utils.GdxRuntimeException r2 = new com.badlogic.gdx.utils.GdxRuntimeException     // Catch:{ Exception -> 0x0516 }
                java.lang.String r4 = "Missing: base"
                r2.<init>((java.lang.String) r4)     // Catch:{ Exception -> 0x0516 }
                throw r2     // Catch:{ Exception -> 0x0516 }
            L_0x04d8:
                r22 = r2
                r16 = r5
                r18 = r11
                com.badlogic.gdx.utils.GdxRuntimeException r2 = new com.badlogic.gdx.utils.GdxRuntimeException     // Catch:{ Exception -> 0x0516 }
                java.lang.String r4 = "Missing: lineHeight"
                r2.<init>((java.lang.String) r4)     // Catch:{ Exception -> 0x0516 }
                throw r2     // Catch:{ Exception -> 0x0516 }
            L_0x04e6:
                r22 = r2
                r16 = r5
                r18 = r11
                com.badlogic.gdx.utils.GdxRuntimeException r2 = new com.badlogic.gdx.utils.GdxRuntimeException     // Catch:{ Exception -> 0x0516 }
                java.lang.String r4 = "Invalid common header."
                r2.<init>((java.lang.String) r4)     // Catch:{ Exception -> 0x0516 }
                throw r2     // Catch:{ Exception -> 0x0516 }
            L_0x04f4:
                r22 = r2
                r16 = r5
                com.badlogic.gdx.utils.GdxRuntimeException r2 = new com.badlogic.gdx.utils.GdxRuntimeException     // Catch:{ Exception -> 0x0516 }
                java.lang.String r4 = "Missing common header."
                r2.<init>((java.lang.String) r4)     // Catch:{ Exception -> 0x0516 }
                throw r2     // Catch:{ Exception -> 0x0516 }
            L_0x0500:
                r22 = r2
                r16 = r5
                com.badlogic.gdx.utils.GdxRuntimeException r2 = new com.badlogic.gdx.utils.GdxRuntimeException     // Catch:{ Exception -> 0x0516 }
                java.lang.String r4 = "Invalid padding."
                r2.<init>((java.lang.String) r4)     // Catch:{ Exception -> 0x0516 }
                throw r2     // Catch:{ Exception -> 0x0516 }
            L_0x050c:
                r22 = r2
                com.badlogic.gdx.utils.GdxRuntimeException r2 = new com.badlogic.gdx.utils.GdxRuntimeException     // Catch:{ Exception -> 0x0516 }
                java.lang.String r4 = "File is empty."
                r2.<init>((java.lang.String) r4)     // Catch:{ Exception -> 0x0516 }
                throw r2     // Catch:{ Exception -> 0x0516 }
            L_0x0516:
                r0 = move-exception
                r2 = r0
                goto L_0x0524
            L_0x0519:
                r0 = move-exception
                r22 = r2
                r5 = r30
                r2 = r0
                goto L_0x0544
            L_0x0520:
                r0 = move-exception
                r22 = r2
                r2 = r0
            L_0x0524:
                com.badlogic.gdx.utils.GdxRuntimeException r3 = new com.badlogic.gdx.utils.GdxRuntimeException     // Catch:{ all -> 0x0540 }
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0540 }
                r4.<init>()     // Catch:{ all -> 0x0540 }
                java.lang.String r5 = "Error loading font file: "
                r4.append(r5)     // Catch:{ all -> 0x0540 }
                r5 = r30
                r4.append(r5)     // Catch:{ all -> 0x053d }
                java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x053d }
                r3.<init>(r4, r2)     // Catch:{ all -> 0x053d }
                throw r3     // Catch:{ all -> 0x053d }
            L_0x053d:
                r0 = move-exception
                r2 = r0
                goto L_0x0544
            L_0x0540:
                r0 = move-exception
                r5 = r30
                r2 = r0
            L_0x0544:
                com.badlogic.gdx.utils.StreamUtils.closeQuietly(r22)
                throw r2
            L_0x0548:
                r5 = r30
                java.lang.IllegalStateException r2 = new java.lang.IllegalStateException
                java.lang.String r3 = "Already loaded."
                r2.<init>(r3)
                goto L_0x0553
            L_0x0552:
                throw r2
            L_0x0553:
                goto L_0x0552
            */
            throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.graphics.g2d.BitmapFont.BitmapFontData.load(com.badlogic.gdx.files.FileHandle, boolean):void");
        }

        public void setGlyphRegion(Glyph glyph, TextureRegion region) {
            Glyph glyph2 = glyph;
            TextureRegion textureRegion = region;
            Texture texture = region.getTexture();
            float invTexWidth = 1.0f / ((float) texture.getWidth());
            float invTexHeight = 1.0f / ((float) texture.getHeight());
            float offsetX = 0.0f;
            float offsetY = 0.0f;
            float u = textureRegion.u;
            float v = textureRegion.v;
            float regionWidth = (float) region.getRegionWidth();
            float regionHeight = (float) region.getRegionHeight();
            if (textureRegion instanceof TextureAtlas.AtlasRegion) {
                TextureAtlas.AtlasRegion atlasRegion = (TextureAtlas.AtlasRegion) textureRegion;
                offsetX = atlasRegion.offsetX;
                offsetY = ((float) (atlasRegion.originalHeight - atlasRegion.packedHeight)) - atlasRegion.offsetY;
            }
            float x = (float) glyph2.srcX;
            float x2 = (float) (glyph2.srcX + glyph2.width);
            float y = (float) glyph2.srcY;
            float y2 = (float) (glyph2.srcY + glyph2.height);
            if (offsetX > 0.0f) {
                x -= offsetX;
                if (x < 0.0f) {
                    glyph2.width = (int) (((float) glyph2.width) + x);
                    glyph2.xoffset = (int) (((float) glyph2.xoffset) - x);
                    x = 0.0f;
                }
                x2 -= offsetX;
                if (x2 > regionWidth) {
                    glyph2.width = (int) (((float) glyph2.width) - (x2 - regionWidth));
                    x2 = regionWidth;
                }
            }
            if (offsetY > 0.0f) {
                y -= offsetY;
                if (y < 0.0f) {
                    glyph2.height = (int) (((float) glyph2.height) + y);
                    if (glyph2.height < 0) {
                        glyph2.height = 0;
                    }
                    y = 0.0f;
                }
                y2 -= offsetY;
                if (y2 > regionHeight) {
                    float amount = y2 - regionHeight;
                    glyph2.height = (int) (((float) glyph2.height) - amount);
                    glyph2.yoffset = (int) (((float) glyph2.yoffset) + amount);
                    y2 = regionHeight;
                }
            }
            glyph2.u = (x * invTexWidth) + u;
            glyph2.u2 = (x2 * invTexWidth) + u;
            if (this.flipped) {
                glyph2.v = (y * invTexHeight) + v;
                glyph2.v2 = (y2 * invTexHeight) + v;
                return;
            }
            glyph2.v2 = (y * invTexHeight) + v;
            glyph2.v = (y2 * invTexHeight) + v;
        }

        public void setLineHeight(float height) {
            this.lineHeight = this.scaleY * height;
            this.down = this.flipped ? this.lineHeight : -this.lineHeight;
        }

        public void setGlyph(int ch, Glyph glyph) {
            Glyph[][] glyphArr = this.glyphs;
            Glyph[] page = glyphArr[ch / 512];
            if (page == null) {
                Glyph[] glyphArr2 = new Glyph[512];
                page = glyphArr2;
                glyphArr[ch / 512] = glyphArr2;
            }
            page[ch & 511] = glyph;
        }

        public Glyph getFirstGlyph() {
            for (Glyph[] page : this.glyphs) {
                if (page != null) {
                    for (Glyph glyph : page) {
                        if (glyph != null && glyph.height != 0 && glyph.width != 0) {
                            return glyph;
                        }
                    }
                    continue;
                }
            }
            throw new GdxRuntimeException("No glyphs found.");
        }

        public boolean hasGlyph(char ch) {
            if (this.missingGlyph == null && getGlyph(ch) == null) {
                return false;
            }
            return true;
        }

        public Glyph getGlyph(char ch) {
            Glyph[] page = this.glyphs[ch / 512];
            if (page != null) {
                return page[ch & 511];
            }
            return null;
        }

        public void getGlyphs(GlyphLayout.GlyphRun run, CharSequence str, int start, int end, Glyph lastGlyph) {
            boolean markupEnabled2 = this.markupEnabled;
            float scaleX2 = this.scaleX;
            Glyph missingGlyph2 = this.missingGlyph;
            Array<Glyph> glyphs2 = run.glyphs;
            FloatArray xAdvances = run.xAdvances;
            glyphs2.ensureCapacity(end - start);
            xAdvances.ensureCapacity((end - start) + 1);
            while (start < end) {
                int start2 = start + 1;
                char ch = str.charAt(start);
                if (ch != 13) {
                    Glyph glyph = getGlyph(ch);
                    if (glyph == null) {
                        if (missingGlyph2 != null) {
                            glyph = missingGlyph2;
                        }
                    }
                    glyphs2.add(glyph);
                    if (lastGlyph == null) {
                        xAdvances.add(glyph.fixedWidth ? 0.0f : (((float) (-glyph.xoffset)) * scaleX2) - this.padLeft);
                    } else {
                        xAdvances.add(((float) (lastGlyph.xadvance + lastGlyph.getKerning(ch))) * scaleX2);
                    }
                    lastGlyph = glyph;
                    if (!markupEnabled2 || ch != '[' || start2 >= end || str.charAt(start2) != '[') {
                        start = start2;
                    } else {
                        start = start2 + 1;
                    }
                }
                start = start2;
            }
            if (lastGlyph != null) {
                xAdvances.add(lastGlyph.fixedWidth ? ((float) lastGlyph.xadvance) * scaleX2 : (((float) (lastGlyph.width + lastGlyph.xoffset)) * scaleX2) - this.padRight);
            }
        }

        public int getWrapIndex(Array<Glyph> glyphs2, int start) {
            int i = start - 1;
            char ch = (char) glyphs2.get(i).id;
            if (isWhitespace(ch)) {
                return i;
            }
            if (isBreakChar(ch)) {
                i--;
            }
            while (i > 0) {
                char ch2 = (char) glyphs2.get(i).id;
                if (isBreakChar(ch2)) {
                    return i + 1;
                }
                if (isWhitespace(ch2)) {
                    return i + 1;
                }
                i--;
            }
            return 0;
        }

        public boolean isBreakChar(char c) {
            char[] cArr = this.breakChars;
            if (cArr == null) {
                return false;
            }
            for (char br : cArr) {
                if (c == br) {
                    return true;
                }
            }
            return false;
        }

        public boolean isWhitespace(char c) {
            if (c == 9 || c == 10 || c == 13 || c == ' ') {
                return true;
            }
            return false;
        }

        public String getImagePath(int index) {
            return this.imagePaths[index];
        }

        public String[] getImagePaths() {
            return this.imagePaths;
        }

        public FileHandle getFontFile() {
            return this.fontFile;
        }

        public void setScale(float scaleX2, float scaleY2) {
            if (scaleX2 == 0.0f) {
                throw new IllegalArgumentException("scaleX cannot be 0.");
            } else if (scaleY2 != 0.0f) {
                float x = scaleX2 / this.scaleX;
                float y = scaleY2 / this.scaleY;
                this.lineHeight *= y;
                this.spaceXadvance *= x;
                this.xHeight *= y;
                this.capHeight *= y;
                this.ascent *= y;
                this.descent *= y;
                this.down *= y;
                this.padLeft *= x;
                this.padRight *= x;
                this.padTop *= y;
                this.padBottom *= y;
                this.scaleX = scaleX2;
                this.scaleY = scaleY2;
            } else {
                throw new IllegalArgumentException("scaleY cannot be 0.");
            }
        }

        public void setScale(float scaleXY) {
            setScale(scaleXY, scaleXY);
        }

        public void scale(float amount) {
            setScale(this.scaleX + amount, this.scaleY + amount);
        }

        public String toString() {
            String str = this.name;
            return str != null ? str : super.toString();
        }
    }
}
