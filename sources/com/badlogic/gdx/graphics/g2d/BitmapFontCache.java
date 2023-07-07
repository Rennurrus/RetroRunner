package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.NumberUtils;
import com.badlogic.gdx.utils.Pools;

public class BitmapFontCache {
    private static final Color tempColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);
    private final Color color;
    private float currentTint;
    private final BitmapFont font;
    private int glyphCount;
    private int[] idx;
    private boolean integer;
    private final Array<GlyphLayout> layouts;
    private IntArray[] pageGlyphIndices;
    private float[][] pageVertices;
    private final Array<GlyphLayout> pooledLayouts;
    private int[] tempGlyphCount;
    private float x;
    private float y;

    public BitmapFontCache(BitmapFont font2) {
        this(font2, font2.usesIntegerPositions());
    }

    public BitmapFontCache(BitmapFont font2, boolean integer2) {
        this.layouts = new Array<>();
        this.pooledLayouts = new Array<>();
        this.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        this.font = font2;
        this.integer = integer2;
        int pageCount = font2.regions.size;
        if (pageCount != 0) {
            this.pageVertices = new float[pageCount][];
            this.idx = new int[pageCount];
            if (pageCount > 1) {
                this.pageGlyphIndices = new IntArray[pageCount];
                int n = this.pageGlyphIndices.length;
                for (int i = 0; i < n; i++) {
                    this.pageGlyphIndices[i] = new IntArray();
                }
            }
            this.tempGlyphCount = new int[pageCount];
            return;
        }
        throw new IllegalArgumentException("The specified font must contain at least one texture page.");
    }

    public void setPosition(float x2, float y2) {
        translate(x2 - this.x, y2 - this.y);
    }

    public void translate(float xAmount, float yAmount) {
        if (xAmount != 0.0f || yAmount != 0.0f) {
            if (this.integer) {
                xAmount = (float) Math.round(xAmount);
                yAmount = (float) Math.round(yAmount);
            }
            this.x += xAmount;
            this.y += yAmount;
            float[][] pageVertices2 = this.pageVertices;
            int n = pageVertices2.length;
            for (int i = 0; i < n; i++) {
                float[] vertices = pageVertices2[i];
                int nn = this.idx[i];
                for (int ii = 0; ii < nn; ii += 5) {
                    vertices[ii] = vertices[ii] + xAmount;
                    int i2 = ii + 1;
                    vertices[i2] = vertices[i2] + yAmount;
                }
            }
        }
    }

    public void tint(Color tint) {
        int[] tempGlyphCount2;
        BitmapFontCache bitmapFontCache = this;
        float newTint = tint.toFloatBits();
        if (bitmapFontCache.currentTint != newTint) {
            bitmapFontCache.currentTint = newTint;
            int[] tempGlyphCount3 = bitmapFontCache.tempGlyphCount;
            int n = tempGlyphCount3.length;
            for (int i = 0; i < n; i++) {
                tempGlyphCount3[i] = 0;
            }
            int i2 = 0;
            int n2 = bitmapFontCache.layouts.size;
            while (i2 < n2) {
                GlyphLayout layout = bitmapFontCache.layouts.get(i2);
                int ii = 0;
                int nn = layout.runs.size;
                while (ii < nn) {
                    GlyphLayout.GlyphRun run = layout.runs.get(ii);
                    Array<BitmapFont.Glyph> glyphs = run.glyphs;
                    float colorFloat = tempColor.set(run.color).mul(tint).toFloatBits();
                    int iii = 0;
                    int nnn = glyphs.size;
                    while (iii < nnn) {
                        int page = glyphs.get(iii).page;
                        float newTint2 = newTint;
                        int offset = (tempGlyphCount3[page] * 20) + 2;
                        tempGlyphCount3[page] = tempGlyphCount3[page] + 1;
                        float[] vertices = bitmapFontCache.pageVertices[page];
                        int v = 0;
                        while (true) {
                            tempGlyphCount2 = tempGlyphCount3;
                            if (v >= 20) {
                                break;
                            }
                            vertices[offset + v] = colorFloat;
                            v += 5;
                            tempGlyphCount3 = tempGlyphCount2;
                        }
                        iii++;
                        bitmapFontCache = this;
                        newTint = newTint2;
                        tempGlyphCount3 = tempGlyphCount2;
                    }
                    int[] iArr = tempGlyphCount3;
                    ii++;
                    bitmapFontCache = this;
                }
                Color color2 = tint;
                float f = newTint;
                int[] iArr2 = tempGlyphCount3;
                i2++;
                bitmapFontCache = this;
            }
            Color color3 = tint;
            float f2 = newTint;
            int[] iArr3 = tempGlyphCount3;
        }
    }

    public void setAlphas(float alpha) {
        int alphaBits = ((int) (254.0f * alpha)) << 24;
        float prev = 0.0f;
        float newColor = 0.0f;
        int length = this.pageVertices.length;
        for (int j = 0; j < length; j++) {
            float[] vertices = this.pageVertices[j];
            int n = this.idx[j];
            for (int i = 2; i < n; i += 5) {
                float c = vertices[i];
                if (c != prev || i == 2) {
                    prev = c;
                    newColor = NumberUtils.intToFloatColor((16777215 & NumberUtils.floatToIntColor(c)) | alphaBits);
                    vertices[i] = newColor;
                } else {
                    vertices[i] = newColor;
                }
            }
        }
    }

    public void setColors(float color2) {
        int length = this.pageVertices.length;
        for (int j = 0; j < length; j++) {
            float[] vertices = this.pageVertices[j];
            int n = this.idx[j];
            for (int i = 2; i < n; i += 5) {
                vertices[i] = color2;
            }
        }
    }

    public void setColors(Color tint) {
        setColors(tint.toFloatBits());
    }

    public void setColors(float r, float g, float b, float a) {
        setColors(NumberUtils.intToFloatColor(((int) (255.0f * r)) | (((int) (a * 255.0f)) << 24) | (((int) (b * 255.0f)) << 16) | (((int) (g * 255.0f)) << 8)));
    }

    public void setColors(Color tint, int start, int end) {
        setColors(tint.toFloatBits(), start, end);
    }

    public void setColors(float color2, int start, int end) {
        float[][] fArr = this.pageVertices;
        if (fArr.length == 1) {
            float[] vertices = fArr[0];
            int n = end * 20;
            for (int i = (start * 20) + 2; i < n; i += 5) {
                vertices[i] = color2;
            }
            return;
        }
        int pageCount = fArr.length;
        for (int i2 = 0; i2 < pageCount; i2++) {
            float[] vertices2 = this.pageVertices[i2];
            IntArray glyphIndices = this.pageGlyphIndices[i2];
            int n2 = glyphIndices.size;
            for (int j = 0; j < n2; j++) {
                int glyphIndex = glyphIndices.items[j];
                if (glyphIndex >= end) {
                    break;
                }
                if (glyphIndex >= start) {
                    for (int off = 0; off < 20; off += 5) {
                        vertices2[(j * 20) + 2 + off] = color2;
                    }
                }
            }
        }
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color2) {
        this.color.set(color2);
    }

    public void setColor(float r, float g, float b, float a) {
        this.color.set(r, g, b, a);
    }

    public void draw(Batch spriteBatch) {
        Array<TextureRegion> regions = this.font.getRegions();
        int n = this.pageVertices.length;
        for (int j = 0; j < n; j++) {
            if (this.idx[j] > 0) {
                spriteBatch.draw(regions.get(j).getTexture(), this.pageVertices[j], 0, this.idx[j]);
            }
        }
    }

    public void draw(Batch spriteBatch, int start, int end) {
        int glyphIndex;
        if (this.pageVertices.length == 1) {
            spriteBatch.draw(this.font.getRegion().getTexture(), this.pageVertices[0], start * 20, (end - start) * 20);
            return;
        }
        Array<TextureRegion> regions = this.font.getRegions();
        int pageCount = this.pageVertices.length;
        for (int i = 0; i < pageCount; i++) {
            int offset = -1;
            int count = 0;
            IntArray glyphIndices = this.pageGlyphIndices[i];
            int ii = 0;
            int n = glyphIndices.size;
            while (ii < n && (glyphIndex = glyphIndices.get(ii)) < end) {
                if (offset == -1 && glyphIndex >= start) {
                    offset = ii;
                }
                if (glyphIndex >= start) {
                    count++;
                }
                ii++;
            }
            if (!(offset == -1 || count == 0)) {
                spriteBatch.draw(regions.get(i).getTexture(), this.pageVertices[i], offset * 20, count * 20);
            }
        }
    }

    public void draw(Batch spriteBatch, float alphaModulation) {
        if (alphaModulation == 1.0f) {
            draw(spriteBatch);
            return;
        }
        Color color2 = getColor();
        float oldAlpha = color2.a;
        color2.a *= alphaModulation;
        setColors(color2);
        draw(spriteBatch);
        color2.a = oldAlpha;
        setColors(color2);
    }

    public void clear() {
        this.x = 0.0f;
        this.y = 0.0f;
        Pools.freeAll(this.pooledLayouts, true);
        this.pooledLayouts.clear();
        this.layouts.clear();
        int n = this.idx.length;
        for (int i = 0; i < n; i++) {
            IntArray[] intArrayArr = this.pageGlyphIndices;
            if (intArrayArr != null) {
                intArrayArr[i].clear();
            }
            this.idx[i] = 0;
        }
    }

    private void requireGlyphs(GlyphLayout layout) {
        if (this.pageVertices.length == 1) {
            int newGlyphCount = 0;
            int n = layout.runs.size;
            for (int i = 0; i < n; i++) {
                newGlyphCount += layout.runs.get(i).glyphs.size;
            }
            requirePageGlyphs(0, newGlyphCount);
            return;
        }
        int[] tempGlyphCount2 = this.tempGlyphCount;
        int n2 = tempGlyphCount2.length;
        for (int i2 = 0; i2 < n2; i2++) {
            tempGlyphCount2[i2] = 0;
        }
        int n3 = layout.runs.size;
        for (int i3 = 0; i3 < n3; i3++) {
            Array<BitmapFont.Glyph> glyphs = layout.runs.get(i3).glyphs;
            int nn = glyphs.size;
            for (int ii = 0; ii < nn; ii++) {
                int i4 = glyphs.get(ii).page;
                tempGlyphCount2[i4] = tempGlyphCount2[i4] + 1;
            }
        }
        int n4 = tempGlyphCount2.length;
        for (int i5 = 0; i5 < n4; i5++) {
            requirePageGlyphs(i5, tempGlyphCount2[i5]);
        }
    }

    private void requirePageGlyphs(int page, int glyphCount2) {
        IntArray[] intArrayArr = this.pageGlyphIndices;
        if (intArrayArr != null && glyphCount2 > intArrayArr[page].items.length) {
            IntArray[] intArrayArr2 = this.pageGlyphIndices;
            intArrayArr2[page].ensureCapacity(glyphCount2 - intArrayArr2[page].items.length);
        }
        int[] iArr = this.idx;
        int vertexCount = iArr[page] + (glyphCount2 * 20);
        float[][] fArr = this.pageVertices;
        float[] vertices = fArr[page];
        if (vertices == null) {
            fArr[page] = new float[vertexCount];
        } else if (vertices.length < vertexCount) {
            float[] newVertices = new float[vertexCount];
            System.arraycopy(vertices, 0, newVertices, 0, iArr[page]);
            this.pageVertices[page] = newVertices;
        }
    }

    private void addToCache(GlyphLayout layout, float x2, float y2) {
        GlyphLayout glyphLayout = layout;
        int pageCount = this.font.regions.size;
        float[][] fArr = this.pageVertices;
        if (fArr.length < pageCount) {
            float[][] newPageVertices = new float[pageCount][];
            System.arraycopy(fArr, 0, newPageVertices, 0, fArr.length);
            this.pageVertices = newPageVertices;
            int[] newIdx = new int[pageCount];
            int[] iArr = this.idx;
            System.arraycopy(iArr, 0, newIdx, 0, iArr.length);
            this.idx = newIdx;
            IntArray[] newPageGlyphIndices = new IntArray[pageCount];
            int pageGlyphIndicesLength = 0;
            IntArray[] intArrayArr = this.pageGlyphIndices;
            if (intArrayArr != null) {
                pageGlyphIndicesLength = intArrayArr.length;
                System.arraycopy(intArrayArr, 0, newPageGlyphIndices, 0, intArrayArr.length);
            }
            for (int i = pageGlyphIndicesLength; i < pageCount; i++) {
                newPageGlyphIndices[i] = new IntArray();
            }
            this.pageGlyphIndices = newPageGlyphIndices;
            this.tempGlyphCount = new int[pageCount];
        }
        this.layouts.add(glyphLayout);
        requireGlyphs(layout);
        int n = glyphLayout.runs.size;
        for (int i2 = 0; i2 < n; i2++) {
            GlyphLayout.GlyphRun run = glyphLayout.runs.get(i2);
            Array<BitmapFont.Glyph> glyphs = run.glyphs;
            FloatArray xAdvances = run.xAdvances;
            float color2 = run.color.toFloatBits();
            float gx = x2 + run.x;
            float gy = y2 + run.y;
            int nn = glyphs.size;
            for (int ii = 0; ii < nn; ii++) {
                gx += xAdvances.get(ii);
                addGlyph(glyphs.get(ii), gx, gy, color2);
            }
        }
        this.currentTint = Color.WHITE_FLOAT_BITS;
    }

    private void addGlyph(BitmapFont.Glyph glyph, float x2, float y2, float color2) {
        BitmapFont.Glyph glyph2 = glyph;
        float scaleX = this.font.data.scaleX;
        float scaleY = this.font.data.scaleY;
        float x3 = x2 + (((float) glyph2.xoffset) * scaleX);
        float y3 = y2 + (((float) glyph2.yoffset) * scaleY);
        float width = ((float) glyph2.width) * scaleX;
        float height = ((float) glyph2.height) * scaleY;
        float u = glyph2.u;
        float u2 = glyph2.u2;
        float v = glyph2.v;
        float v2 = glyph2.v2;
        if (this.integer) {
            x3 = (float) Math.round(x3);
            y3 = (float) Math.round(y3);
            width = (float) Math.round(width);
            height = (float) Math.round(height);
        }
        float x22 = x3 + width;
        float y22 = y3 + height;
        int page = glyph2.page;
        int[] iArr = this.idx;
        int idx2 = iArr[page];
        iArr[page] = iArr[page] + 20;
        IntArray[] intArrayArr = this.pageGlyphIndices;
        if (intArrayArr != null) {
            IntArray intArray = intArrayArr[page];
            int i = this.glyphCount;
            float f = scaleX;
            this.glyphCount = i + 1;
            intArray.add(i);
        }
        float[] vertices = this.pageVertices[page];
        int idx3 = idx2 + 1;
        vertices[idx2] = x3;
        int idx4 = idx3 + 1;
        vertices[idx3] = y3;
        int idx5 = idx4 + 1;
        vertices[idx4] = color2;
        int idx6 = idx5 + 1;
        vertices[idx5] = u;
        int idx7 = idx6 + 1;
        vertices[idx6] = v;
        int idx8 = idx7 + 1;
        vertices[idx7] = x3;
        int idx9 = idx8 + 1;
        vertices[idx8] = y22;
        int idx10 = idx9 + 1;
        vertices[idx9] = color2;
        int idx11 = idx10 + 1;
        vertices[idx10] = u;
        int idx12 = idx11 + 1;
        vertices[idx11] = v2;
        int idx13 = idx12 + 1;
        vertices[idx12] = x22;
        int idx14 = idx13 + 1;
        vertices[idx13] = y22;
        int idx15 = idx14 + 1;
        vertices[idx14] = color2;
        int idx16 = idx15 + 1;
        vertices[idx15] = u2;
        int idx17 = idx16 + 1;
        vertices[idx16] = v2;
        int idx18 = idx17 + 1;
        vertices[idx17] = x22;
        int idx19 = idx18 + 1;
        vertices[idx18] = y3;
        int idx20 = idx19 + 1;
        vertices[idx19] = color2;
        vertices[idx20] = u2;
        vertices[idx20 + 1] = v;
    }

    public GlyphLayout setText(CharSequence str, float x2, float y2) {
        clear();
        return addText(str, x2, y2, 0, str.length(), 0.0f, 8, false);
    }

    public GlyphLayout setText(CharSequence str, float x2, float y2, float targetWidth, int halign, boolean wrap) {
        clear();
        return addText(str, x2, y2, 0, str.length(), targetWidth, halign, wrap);
    }

    public GlyphLayout setText(CharSequence str, float x2, float y2, int start, int end, float targetWidth, int halign, boolean wrap) {
        clear();
        return addText(str, x2, y2, start, end, targetWidth, halign, wrap);
    }

    public GlyphLayout setText(CharSequence str, float x2, float y2, int start, int end, float targetWidth, int halign, boolean wrap, String truncate) {
        clear();
        return addText(str, x2, y2, start, end, targetWidth, halign, wrap, truncate);
    }

    public void setText(GlyphLayout layout, float x2, float y2) {
        clear();
        addText(layout, x2, y2);
    }

    public GlyphLayout addText(CharSequence str, float x2, float y2) {
        return addText(str, x2, y2, 0, str.length(), 0.0f, 8, false, (String) null);
    }

    public GlyphLayout addText(CharSequence str, float x2, float y2, float targetWidth, int halign, boolean wrap) {
        return addText(str, x2, y2, 0, str.length(), targetWidth, halign, wrap, (String) null);
    }

    public GlyphLayout addText(CharSequence str, float x2, float y2, int start, int end, float targetWidth, int halign, boolean wrap) {
        return addText(str, x2, y2, start, end, targetWidth, halign, wrap, (String) null);
    }

    public GlyphLayout addText(CharSequence str, float x2, float y2, int start, int end, float targetWidth, int halign, boolean wrap, String truncate) {
        GlyphLayout layout = (GlyphLayout) Pools.obtain(GlyphLayout.class);
        this.pooledLayouts.add(layout);
        layout.setText(this.font, str, start, end, this.color, targetWidth, halign, wrap, truncate);
        float f = x2;
        float f2 = y2;
        addText(layout, x2, y2);
        return layout;
    }

    public void addText(GlyphLayout layout, float x2, float y2) {
        addToCache(layout, x2, this.font.data.ascent + y2);
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public BitmapFont getFont() {
        return this.font;
    }

    public void setUseIntegerPositions(boolean use) {
        this.integer = use;
    }

    public boolean usesIntegerPositions() {
        return this.integer;
    }

    public float[] getVertices() {
        return getVertices(0);
    }

    public float[] getVertices(int page) {
        return this.pageVertices[page];
    }

    public int getVertexCount(int page) {
        return this.idx[page];
    }

    public Array<GlyphLayout> getLayouts() {
        return this.layouts;
    }
}
