package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.twi.game.BuildConfig;

public class GlyphLayout implements Pool.Poolable {
    private final Array<Color> colorStack = new Array<>(4);
    public float height;
    public final Array<GlyphRun> runs = new Array<>();
    public float width;

    public GlyphLayout() {
    }

    public GlyphLayout(BitmapFont font, CharSequence str) {
        setText(font, str);
    }

    public GlyphLayout(BitmapFont font, CharSequence str, Color color, float targetWidth, int halign, boolean wrap) {
        setText(font, str, color, targetWidth, halign, wrap);
    }

    public GlyphLayout(BitmapFont font, CharSequence str, int start, int end, Color color, float targetWidth, int halign, boolean wrap, String truncate) {
        setText(font, str, start, end, color, targetWidth, halign, wrap, truncate);
    }

    public void setText(BitmapFont font, CharSequence str) {
        setText(font, str, 0, str.length(), font.getColor(), 0.0f, 8, false, (String) null);
    }

    public void setText(BitmapFont font, CharSequence str, Color color, float targetWidth, int halign, boolean wrap) {
        setText(font, str, 0, str.length(), color, targetWidth, halign, wrap, (String) null);
    }

    /* JADX WARNING: Removed duplicated region for block: B:134:0x044a  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x0466  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setText(com.badlogic.gdx.graphics.g2d.BitmapFont r36, java.lang.CharSequence r37, int r38, int r39, com.badlogic.gdx.graphics.Color r40, float r41, int r42, boolean r43, java.lang.String r44) {
        /*
            r35 = this;
            r7 = r35
            r8 = r37
            r9 = r39
            r10 = r36
            com.badlogic.gdx.graphics.g2d.BitmapFont$BitmapFontData r11 = r10.data
            if (r44 == 0) goto L_0x000f
            r0 = 1
            r12 = r0
            goto L_0x001e
        L_0x000f:
            float r0 = r11.spaceXadvance
            r1 = 1077936128(0x40400000, float:3.0)
            float r0 = r0 * r1
            int r0 = (r41 > r0 ? 1 : (r41 == r0 ? 0 : -1))
            if (r0 > 0) goto L_0x001c
            r0 = 0
            r12 = r0
            goto L_0x001e
        L_0x001c:
            r12 = r43
        L_0x001e:
            boolean r13 = r11.markupEnabled
            java.lang.Class<com.badlogic.gdx.graphics.g2d.GlyphLayout$GlyphRun> r0 = com.badlogic.gdx.graphics.g2d.GlyphLayout.GlyphRun.class
            com.badlogic.gdx.utils.Pool r14 = com.badlogic.gdx.utils.Pools.get(r0)
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.graphics.g2d.GlyphLayout$GlyphRun> r15 = r7.runs
            r14.freeAll(r15)
            r15.clear()
            r0 = 0
            r1 = 0
            r2 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.graphics.Color> r6 = r7.colorStack
            r16 = r40
            r43 = r0
            r0 = r40
            r6.add(r0)
            java.lang.Class<com.badlogic.gdx.graphics.Color> r17 = com.badlogic.gdx.graphics.Color.class
            com.badlogic.gdx.utils.Pool r10 = com.badlogic.gdx.utils.Pools.get(r17)
            r17 = r38
            r18 = r2
            r19 = r3
            r3 = r0
            r2 = r1
            r1 = r4
            r4 = r5
            r5 = r17
            r0 = r38
            r17 = r43
        L_0x0055:
            r20 = -1
            r21 = 0
            r43 = r12
            if (r0 != r9) goto L_0x0082
            if (r5 != r9) goto L_0x0077
            r8 = r1
            r23 = r2
            r24 = r3
            r31 = r5
            r26 = r13
            r20 = r16
            r1 = r17
            r2 = r19
            r16 = r0
            r19 = r4
            r13 = r6
            r0 = r18
            goto L_0x0201
        L_0x0077:
            r20 = r39
            r22 = r1
            r12 = r20
            r20 = r16
            r16 = r0
            goto L_0x00cc
        L_0x0082:
            int r12 = r0 + 1
            char r0 = r8.charAt(r0)
            r22 = r1
            r1 = 10
            if (r0 == r1) goto L_0x00c3
            r1 = 91
            if (r0 == r1) goto L_0x0093
            goto L_0x00ba
        L_0x0093:
            if (r13 == 0) goto L_0x00ba
            int r0 = r7.parseColorMarkup(r8, r12, r9, r10)
            if (r0 < 0) goto L_0x00af
            int r1 = r12 + -1
            int r20 = r0 + 1
            int r12 = r12 + r20
            java.lang.Object r20 = r6.peek()
            r16 = r20
            com.badlogic.gdx.graphics.Color r16 = (com.badlogic.gdx.graphics.Color) r16
            r20 = r16
            r16 = r12
            r12 = r1
            goto L_0x00cc
        L_0x00af:
            r1 = -2
            if (r0 != r1) goto L_0x00ba
            int r1 = r12 + 1
            r12 = r43
            r0 = r1
            r1 = r22
            goto L_0x0055
        L_0x00ba:
            r34 = r16
            r16 = r12
            r12 = r20
            r20 = r34
            goto L_0x00cc
        L_0x00c3:
            int r0 = r12 + -1
            r21 = 1
            r20 = r16
            r16 = r12
            r12 = r0
        L_0x00cc:
            r0 = -1
            if (r12 == r0) goto L_0x0479
            if (r12 == r5) goto L_0x0434
            java.lang.Object r0 = r14.obtain()
            r1 = r0
            com.badlogic.gdx.graphics.g2d.GlyphLayout$GlyphRun r1 = (com.badlogic.gdx.graphics.g2d.GlyphLayout.GlyphRun) r1
            com.badlogic.gdx.graphics.Color r0 = r1.color
            r0.set((com.badlogic.gdx.graphics.Color) r3)
            r0 = r11
            r23 = r1
            r8 = r22
            r22 = r6
            r6 = r2
            r2 = r37
            r24 = r3
            r3 = r5
            r25 = r4
            r4 = r12
            r26 = r13
            r13 = r5
            r5 = r25
            r0.getGlyphs(r1, r2, r3, r4, r5)
            r0 = r23
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.graphics.g2d.BitmapFont$Glyph> r1 = r0.glyphs
            int r1 = r1.size
            if (r1 != 0) goto L_0x010a
            r14.free(r0)
            r29 = r12
            r31 = r13
            r13 = r22
            r4 = r25
            goto L_0x0440
        L_0x010a:
            r4 = r25
            if (r4 == 0) goto L_0x012c
            boolean r1 = r4.fixedWidth
            if (r1 == 0) goto L_0x011a
            int r1 = r4.xadvance
            float r1 = (float) r1
            float r2 = r11.scaleX
            float r1 = r1 * r2
            goto L_0x0127
        L_0x011a:
            int r1 = r4.width
            int r2 = r4.xoffset
            int r1 = r1 + r2
            float r1 = (float) r1
            float r2 = r11.scaleX
            float r1 = r1 * r2
            float r2 = r11.padRight
            float r1 = r1 - r2
        L_0x0127:
            float r17 = r17 - r1
            r1 = r17
            goto L_0x012e
        L_0x012c:
            r1 = r17
        L_0x012e:
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.graphics.g2d.BitmapFont$Glyph> r2 = r0.glyphs
            java.lang.Object r2 = r2.peek()
            r4 = r2
            com.badlogic.gdx.graphics.g2d.BitmapFont$Glyph r4 = (com.badlogic.gdx.graphics.g2d.BitmapFont.Glyph) r4
            r0.x = r1
            r0.y = r6
            if (r21 != 0) goto L_0x013f
            if (r12 != r9) goto L_0x0142
        L_0x013f:
            r7.adjustLastGlyph(r11, r0)
        L_0x0142:
            r15.add(r0)
            com.badlogic.gdx.utils.FloatArray r2 = r0.xAdvances
            float[] r2 = r2.items
            com.badlogic.gdx.utils.FloatArray r3 = r0.xAdvances
            int r3 = r3.size
            if (r43 != 0) goto L_0x0171
            r5 = 0
            r17 = 0
            r34 = r17
            r17 = r4
            r4 = r5
            r5 = r34
        L_0x0159:
            if (r5 >= r3) goto L_0x0162
            r23 = r2[r5]
            float r4 = r4 + r23
            int r5 = r5 + 1
            goto L_0x0159
        L_0x0162:
            float r1 = r1 + r4
            r0.width = r4
            r23 = r6
            r29 = r12
            r31 = r13
            r6 = r18
            r13 = r22
            goto L_0x0448
        L_0x0171:
            r17 = r4
            r4 = 0
            r5 = r2[r4]
            float r1 = r1 + r5
            r5 = r2[r4]
            r0.width = r5
            r4 = 1
            if (r3 >= r4) goto L_0x018a
            r23 = r6
            r29 = r12
            r31 = r13
            r6 = r18
            r13 = r22
            goto L_0x0448
        L_0x018a:
            r5 = r2[r4]
            float r1 = r1 + r5
            float r5 = r0.width
            r23 = r2[r4]
            float r5 = r5 + r23
            r0.width = r5
            r4 = 2
            r5 = r3
            r23 = r6
            r3 = r18
            r25 = r19
            r18 = r1
            r6 = r4
            r19 = r17
            r4 = r0
            r17 = r2
        L_0x01a5:
            if (r6 >= r5) goto L_0x0422
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.graphics.g2d.BitmapFont$Glyph> r0 = r4.glyphs
            int r1 = r6 + -1
            java.lang.Object r0 = r0.get(r1)
            r2 = r0
            com.badlogic.gdx.graphics.g2d.BitmapFont$Glyph r2 = (com.badlogic.gdx.graphics.g2d.BitmapFont.Glyph) r2
            int r0 = r2.width
            int r1 = r2.xoffset
            int r0 = r0 + r1
            float r0 = (float) r0
            float r1 = r11.scaleX
            float r0 = r0 * r1
            float r1 = r11.padRight
            float r27 = r0 - r1
            float r0 = r18 + r27
            int r0 = (r0 > r41 ? 1 : (r0 == r41 ? 0 : -1))
            if (r0 > 0) goto L_0x01d9
            r0 = r17[r6]
            float r18 = r18 + r0
            float r0 = r4.width
            r1 = r17[r6]
            float r0 = r0 + r1
            r4.width = r0
            r29 = r12
            r31 = r13
            r13 = r22
            goto L_0x0416
        L_0x01d9:
            if (r44 == 0) goto L_0x02d7
            r0 = r35
            r1 = r11
            r28 = r2
            r2 = r4
            r9 = r3
            r3 = r41
            r29 = r12
            r12 = r4
            r4 = r44
            r30 = r5
            r5 = r6
            r31 = r13
            r13 = r22
            r22 = r9
            r9 = r6
            r6 = r14
            r0.truncate(r1, r2, r3, r4, r5, r6)
            float r0 = r12.x
            float r1 = r12.width
            float r0 = r0 + r1
            r1 = r0
            r0 = r22
            r2 = r25
        L_0x0201:
            float r0 = java.lang.Math.max(r0, r1)
            r3 = 1
            int r4 = r13.size
        L_0x0208:
            if (r3 >= r4) goto L_0x0214
            java.lang.Object r5 = r13.get(r3)
            r10.free(r5)
            int r3 = r3 + 1
            goto L_0x0208
        L_0x0214:
            r13.clear()
            r3 = r42 & 8
            if (r3 != 0) goto L_0x02a3
            r3 = r42 & 1
            if (r3 == 0) goto L_0x0221
            r3 = 1
            goto L_0x0222
        L_0x0221:
            r3 = 0
        L_0x0222:
            r4 = 0
            r5 = -822083584(0xffffffffcf000000, float:-2.14748365E9)
            r6 = 0
            int r9 = r15.size
            r12 = 0
        L_0x0229:
            r17 = 1073741824(0x40000000, float:2.0)
            if (r12 >= r9) goto L_0x027e
            java.lang.Object r18 = r15.get(r12)
            r21 = r1
            r1 = r18
            com.badlogic.gdx.graphics.g2d.GlyphLayout$GlyphRun r1 = (com.badlogic.gdx.graphics.g2d.GlyphLayout.GlyphRun) r1
            r38 = r6
            float r6 = r1.y
            int r6 = (r6 > r5 ? 1 : (r6 == r5 ? 0 : -1))
            if (r6 == 0) goto L_0x0266
            float r5 = r1.y
            float r6 = r41 - r4
            if (r3 == 0) goto L_0x0247
            float r6 = r6 / r17
        L_0x0247:
            r17 = r6
            r6 = r38
        L_0x024b:
            if (r6 >= r12) goto L_0x0262
            int r18 = r6 + 1
            java.lang.Object r6 = r15.get(r6)
            com.badlogic.gdx.graphics.g2d.GlyphLayout$GlyphRun r6 = (com.badlogic.gdx.graphics.g2d.GlyphLayout.GlyphRun) r6
            r22 = r5
            float r5 = r6.x
            float r5 = r5 + r17
            r6.x = r5
            r6 = r18
            r5 = r22
            goto L_0x024b
        L_0x0262:
            r22 = r5
            r4 = 0
            goto L_0x0268
        L_0x0266:
            r6 = r38
        L_0x0268:
            r38 = r5
            float r5 = r1.x
            r40 = r6
            float r6 = r1.width
            float r5 = r5 + r6
            float r4 = java.lang.Math.max(r4, r5)
            int r12 = r12 + 1
            r5 = r38
            r6 = r40
            r1 = r21
            goto L_0x0229
        L_0x027e:
            r21 = r1
            r38 = r6
            float r1 = r41 - r4
            if (r3 == 0) goto L_0x0288
            float r1 = r1 / r17
        L_0x0288:
            r6 = r1
            r1 = r38
        L_0x028b:
            if (r1 >= r9) goto L_0x02a0
            int r12 = r1 + 1
            java.lang.Object r1 = r15.get(r1)
            com.badlogic.gdx.graphics.g2d.GlyphLayout$GlyphRun r1 = (com.badlogic.gdx.graphics.g2d.GlyphLayout.GlyphRun) r1
            r38 = r3
            float r3 = r1.x
            float r3 = r3 + r6
            r1.x = r3
            r3 = r38
            r1 = r12
            goto L_0x028b
        L_0x02a0:
            r38 = r3
            goto L_0x02a5
        L_0x02a3:
            r21 = r1
        L_0x02a5:
            r7.width = r0
            boolean r1 = r11.flipped
            if (r1 == 0) goto L_0x02c0
            float r1 = r11.capHeight
            float r3 = (float) r2
            float r4 = r11.down
            float r3 = r3 * r4
            float r1 = r1 + r3
            float r3 = (float) r8
            float r4 = r11.down
            float r3 = r3 * r4
            float r4 = r11.blankLineScale
            float r3 = r3 * r4
            float r1 = r1 + r3
            r7.height = r1
            goto L_0x02d6
        L_0x02c0:
            float r1 = r11.capHeight
            float r3 = (float) r2
            float r4 = r11.down
            float r4 = -r4
            float r3 = r3 * r4
            float r1 = r1 + r3
            float r3 = (float) r8
            float r4 = r11.down
            float r4 = -r4
            float r3 = r3 * r4
            float r4 = r11.blankLineScale
            float r3 = r3 * r4
            float r1 = r1 + r3
            r7.height = r1
        L_0x02d6:
            return
        L_0x02d7:
            r28 = r2
            r30 = r5
            r9 = r6
            r29 = r12
            r31 = r13
            r13 = r22
            r22 = r3
            r12 = r4
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.graphics.g2d.BitmapFont$Glyph> r0 = r12.glyphs
            int r0 = r11.getWrapIndex(r0, r9)
            float r1 = r12.x
            r6 = 0
            int r1 = (r1 > r6 ? 1 : (r1 == r6 ? 0 : -1))
            if (r1 != 0) goto L_0x02f4
            if (r0 == 0) goto L_0x02fa
        L_0x02f4:
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.graphics.g2d.BitmapFont$Glyph> r1 = r12.glyphs
            int r1 = r1.size
            if (r0 < r1) goto L_0x02ff
        L_0x02fa:
            int r0 = r9 + -1
            r32 = r0
            goto L_0x0301
        L_0x02ff:
            r32 = r0
        L_0x0301:
            if (r32 != 0) goto L_0x03b3
            r0 = r12
            r12.width = r6
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.graphics.g2d.BitmapFont$Glyph> r1 = r12.glyphs
            int r1 = r1.size
            r2 = r32
        L_0x030c:
            if (r2 >= r1) goto L_0x0323
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.graphics.g2d.BitmapFont$Glyph> r3 = r12.glyphs
            java.lang.Object r3 = r3.get(r2)
            com.badlogic.gdx.graphics.g2d.BitmapFont$Glyph r3 = (com.badlogic.gdx.graphics.g2d.BitmapFont.Glyph) r3
            int r3 = r3.id
            char r3 = (char) r3
            boolean r3 = r11.isWhitespace(r3)
            if (r3 != 0) goto L_0x0320
            goto L_0x0323
        L_0x0320:
            int r2 = r2 + 1
            goto L_0x030c
        L_0x0323:
            if (r2 <= 0) goto L_0x0333
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.graphics.g2d.BitmapFont$Glyph> r1 = r12.glyphs
            int r3 = r2 + -1
            r4 = 0
            r1.removeRange(r4, r3)
            com.badlogic.gdx.utils.FloatArray r1 = r12.xAdvances
            r3 = 1
            r1.removeRange(r3, r2)
        L_0x0333:
            com.badlogic.gdx.utils.FloatArray r1 = r12.xAdvances
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.graphics.g2d.BitmapFont$Glyph> r3 = r12.glyphs
            java.lang.Object r3 = r3.first()
            com.badlogic.gdx.graphics.g2d.BitmapFont$Glyph r3 = (com.badlogic.gdx.graphics.g2d.BitmapFont.Glyph) r3
            int r3 = r3.xoffset
            int r3 = -r3
            float r3 = (float) r3
            float r4 = r11.scaleX
            float r3 = r3 * r4
            float r4 = r11.padLeft
            float r3 = r3 - r4
            r4 = 0
            r1.set(r4, r3)
            int r1 = r15.size
            r3 = 1
            if (r1 <= r3) goto L_0x03ad
            int r1 = r15.size
            int r1 = r1 + -2
            java.lang.Object r1 = r15.get(r1)
            com.badlogic.gdx.graphics.g2d.GlyphLayout$GlyphRun r1 = (com.badlogic.gdx.graphics.g2d.GlyphLayout.GlyphRun) r1
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.graphics.g2d.BitmapFont$Glyph> r4 = r1.glyphs
            int r4 = r4.size
            int r4 = r4 - r3
        L_0x0360:
            if (r4 <= 0) goto L_0x038b
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.graphics.g2d.BitmapFont$Glyph> r3 = r1.glyphs
            java.lang.Object r3 = r3.get(r4)
            com.badlogic.gdx.graphics.g2d.BitmapFont$Glyph r3 = (com.badlogic.gdx.graphics.g2d.BitmapFont.Glyph) r3
            int r5 = r3.id
            char r5 = (char) r5
            boolean r5 = r11.isWhitespace(r5)
            if (r5 != 0) goto L_0x0376
            r33 = r0
            goto L_0x038d
        L_0x0376:
            float r5 = r1.width
            com.badlogic.gdx.utils.FloatArray r6 = r1.xAdvances
            r33 = r0
            int r0 = r4 + 1
            float r0 = r6.get(r0)
            float r5 = r5 - r0
            r1.width = r5
            int r4 = r4 + -1
            r0 = r33
            r6 = 0
            goto L_0x0360
        L_0x038b:
            r33 = r0
        L_0x038d:
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.graphics.g2d.BitmapFont$Glyph> r0 = r1.glyphs
            int r3 = r4 + 1
            r0.truncate(r3)
            com.badlogic.gdx.utils.FloatArray r0 = r1.xAdvances
            int r3 = r4 + 2
            r0.truncate(r3)
            r7.adjustLastGlyph(r11, r1)
            float r0 = r1.x
            float r3 = r1.width
            float r0 = r0 + r3
            r6 = r22
            float r0 = java.lang.Math.max(r6, r0)
            r3 = r0
            r0 = r33
            goto L_0x03e4
        L_0x03ad:
            r33 = r0
            r6 = r22
            r3 = r6
            goto L_0x03e4
        L_0x03b3:
            r6 = r22
            r0 = r35
            r1 = r11
            r2 = r12
            r3 = r14
            r4 = r32
            r5 = r9
            com.badlogic.gdx.graphics.g2d.GlyphLayout$GlyphRun r0 = r0.wrap(r1, r2, r3, r4, r5)
            float r1 = r12.x
            float r2 = r12.width
            float r1 = r1 + r2
            float r1 = java.lang.Math.max(r6, r1)
            if (r0 != 0) goto L_0x03de
            r2 = 0
            float r3 = r11.down
            float r3 = r23 + r3
            int r4 = r25 + 1
            r5 = 0
            r6 = r1
            r1 = r2
            r23 = r3
            r19 = r4
            r17 = r5
            goto L_0x0448
        L_0x03de:
            r15.add(r0)
            r3 = r1
            r2 = r32
        L_0x03e4:
            com.badlogic.gdx.utils.FloatArray r1 = r0.xAdvances
            int r1 = r1.size
            com.badlogic.gdx.utils.FloatArray r4 = r0.xAdvances
            float[] r4 = r4.items
            r5 = 0
            r6 = r4[r5]
            r5 = 1
            if (r1 <= r5) goto L_0x03f6
            r17 = r4[r5]
            float r6 = r6 + r17
        L_0x03f6:
            float r5 = r0.width
            float r5 = r5 + r6
            r0.width = r5
            float r5 = r11.down
            float r5 = r23 + r5
            int r25 = r25 + 1
            r22 = r1
            r1 = 0
            r0.x = r1
            r0.y = r5
            r1 = 1
            r9 = r0
            r19 = 0
            r17 = r4
            r23 = r5
            r18 = r6
            r4 = r9
            r5 = r22
            r6 = r1
        L_0x0416:
            r0 = 1
            int r6 = r6 + r0
            r9 = r39
            r22 = r13
            r12 = r29
            r13 = r31
            goto L_0x01a5
        L_0x0422:
            r30 = r5
            r9 = r6
            r29 = r12
            r31 = r13
            r13 = r22
            r6 = r3
            r12 = r4
            r1 = r18
            r17 = r19
            r19 = r25
            goto L_0x0448
        L_0x0434:
            r24 = r3
            r31 = r5
            r29 = r12
            r26 = r13
            r8 = r22
            r13 = r6
            r6 = r2
        L_0x0440:
            r23 = r6
            r1 = r17
            r6 = r18
            r17 = r4
        L_0x0448:
            if (r21 == 0) goto L_0x0466
            float r6 = java.lang.Math.max(r6, r1)
            r1 = 0
            float r0 = r11.down
            r2 = r29
            r5 = r31
            if (r2 != r5) goto L_0x045f
            float r3 = r11.blankLineScale
            float r0 = r0 * r3
            int r3 = r8 + 1
            r8 = r3
            goto L_0x0461
        L_0x045f:
            int r19 = r19 + 1
        L_0x0461:
            float r23 = r23 + r0
            r3 = 0
            r4 = r3
            goto L_0x046c
        L_0x0466:
            r2 = r29
            r5 = r31
            r4 = r17
        L_0x046c:
            r5 = r16
            r0 = r20
            r3 = r0
            r17 = r1
            r18 = r6
            r1 = r8
            r2 = r23
            goto L_0x0484
        L_0x0479:
            r24 = r3
            r26 = r13
            r8 = r22
            r13 = r6
            r6 = r2
            r2 = r12
            r2 = r6
            r1 = r8
        L_0x0484:
            r8 = r37
            r9 = r39
            r12 = r43
            r6 = r13
            r0 = r16
            r16 = r20
            r13 = r26
            goto L_0x0055
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.graphics.g2d.GlyphLayout.setText(com.badlogic.gdx.graphics.g2d.BitmapFont, java.lang.CharSequence, int, int, com.badlogic.gdx.graphics.Color, float, int, boolean, java.lang.String):void");
    }

    private void truncate(BitmapFont.BitmapFontData fontData, GlyphRun run, float targetWidth, String truncate, int widthIndex, Pool<GlyphRun> glyphRunPool) {
        GlyphRun truncateRun = glyphRunPool.obtain();
        fontData.getGlyphs(truncateRun, truncate, 0, truncate.length(), (BitmapFont.Glyph) null);
        float truncateWidth = 0.0f;
        if (truncateRun.xAdvances.size > 0) {
            adjustLastGlyph(fontData, truncateRun);
            int n = truncateRun.xAdvances.size;
            for (int i = 1; i < n; i++) {
                truncateWidth += truncateRun.xAdvances.get(i);
            }
        }
        float targetWidth2 = targetWidth - truncateWidth;
        int count = 0;
        float width2 = run.x;
        while (true) {
            if (count >= run.xAdvances.size) {
                break;
            }
            float xAdvance = run.xAdvances.get(count);
            width2 += xAdvance;
            if (width2 > targetWidth2) {
                run.width = (width2 - run.x) - xAdvance;
                break;
            }
            count++;
        }
        if (count > 1) {
            run.glyphs.truncate(count - 1);
            run.xAdvances.truncate(count);
            adjustLastGlyph(fontData, run);
            if (truncateRun.xAdvances.size > 0) {
                run.xAdvances.addAll(truncateRun.xAdvances, 1, truncateRun.xAdvances.size - 1);
            }
        } else {
            run.glyphs.clear();
            run.xAdvances.clear();
            run.xAdvances.addAll(truncateRun.xAdvances);
            if (truncateRun.xAdvances.size > 0) {
                run.width += truncateRun.xAdvances.get(0);
            }
        }
        run.glyphs.addAll(truncateRun.glyphs);
        run.width += truncateWidth;
        glyphRunPool.free(truncateRun);
    }

    private GlyphRun wrap(BitmapFont.BitmapFontData fontData, GlyphRun first, Pool<GlyphRun> glyphRunPool, int wrapIndex, int widthIndex) {
        BitmapFont.BitmapFontData bitmapFontData = fontData;
        GlyphRun glyphRun = first;
        Array<BitmapFont.Glyph> glyphs2 = glyphRun.glyphs;
        int glyphCount = glyphRun.glyphs.size;
        FloatArray xAdvances2 = glyphRun.xAdvances;
        int firstEnd = wrapIndex;
        while (firstEnd > 0 && fontData.isWhitespace((char) glyphs2.get(firstEnd - 1).id)) {
            firstEnd--;
        }
        int secondStart = wrapIndex;
        while (secondStart < glyphCount && fontData.isWhitespace((char) glyphs2.get(secondStart).id)) {
            secondStart++;
        }
        int widthIndex2 = widthIndex;
        while (widthIndex2 < firstEnd) {
            glyphRun.width += xAdvances2.get(widthIndex2);
            widthIndex2++;
        }
        int n = firstEnd + 1;
        while (widthIndex2 > n) {
            widthIndex2--;
            glyphRun.width -= xAdvances2.get(widthIndex2);
        }
        GlyphRun second = null;
        if (secondStart < glyphCount) {
            second = glyphRunPool.obtain();
            second.color.set(glyphRun.color);
            Array<BitmapFont.Glyph> glyphs1 = second.glyphs;
            glyphs1.addAll((Array<? extends BitmapFont.Glyph>) glyphs2, 0, firstEnd);
            glyphs2.removeRange(0, secondStart - 1);
            glyphRun.glyphs = glyphs1;
            second.glyphs = glyphs2;
            FloatArray xAdvances1 = second.xAdvances;
            xAdvances1.addAll(xAdvances2, 0, firstEnd + 1);
            xAdvances2.removeRange(1, secondStart);
            xAdvances2.set(0, (((float) (-glyphs2.first().xoffset)) * bitmapFontData.scaleX) - bitmapFontData.padLeft);
            glyphRun.xAdvances = xAdvances1;
            second.xAdvances = xAdvances2;
        } else {
            glyphs2.truncate(firstEnd);
            xAdvances2.truncate(firstEnd + 1);
        }
        if (firstEnd == 0) {
            glyphRunPool.free(glyphRun);
            this.runs.pop();
        } else {
            Pool<GlyphRun> pool = glyphRunPool;
            adjustLastGlyph(fontData, first);
        }
        return second;
    }

    private void adjustLastGlyph(BitmapFont.BitmapFontData fontData, GlyphRun run) {
        BitmapFont.Glyph last = run.glyphs.peek();
        if (!last.fixedWidth) {
            float width2 = (((float) (last.width + last.xoffset)) * fontData.scaleX) - fontData.padRight;
            run.width += width2 - run.xAdvances.peek();
            run.xAdvances.set(run.xAdvances.size - 1, width2);
        }
    }

    private int parseColorMarkup(CharSequence str, int start, int end, Pool<Color> colorPool) {
        int i;
        int i2;
        if (start == end) {
            return -1;
        }
        char charAt = str.charAt(start);
        if (charAt == '#') {
            int colorInt = 0;
            int i3 = start + 1;
            while (true) {
                if (i3 >= end) {
                    break;
                }
                char ch = str.charAt(i3);
                if (ch != ']') {
                    if (ch >= '0' && ch <= '9') {
                        i2 = colorInt * 16;
                        i = ch - '0';
                    } else if (ch < 'a' || ch > 'f') {
                        if (ch < 'A' || ch > 'F') {
                            break;
                        }
                        i2 = colorInt * 16;
                        i = ch - '7';
                    } else {
                        i2 = colorInt * 16;
                        i = ch - 'W';
                    }
                    colorInt = i2 + i;
                    i3++;
                } else if (i3 >= start + 2 && i3 <= start + 9) {
                    if (i3 - start <= 7) {
                        for (int ii = 0; ii < 9 - (i3 - start); ii++) {
                            colorInt <<= 4;
                        }
                        colorInt |= 255;
                    }
                    Color color = colorPool.obtain();
                    this.colorStack.add(color);
                    Color.rgba8888ToColor(color, colorInt);
                    return i3 - start;
                }
            }
            return -1;
        } else if (charAt == '[') {
            return -2;
        } else {
            if (charAt != ']') {
                int colorStart = start;
                int i4 = start + 1;
                while (i4 < end) {
                    if (str.charAt(i4) != ']') {
                        i4++;
                    } else {
                        Color namedColor = Colors.get(str.subSequence(colorStart, i4).toString());
                        if (namedColor == null) {
                            return -1;
                        }
                        Color color2 = colorPool.obtain();
                        this.colorStack.add(color2);
                        color2.set(namedColor);
                        return i4 - start;
                    }
                }
                return -1;
            } else if (this.colorStack.size <= 1) {
                return 0;
            } else {
                colorPool.free(this.colorStack.pop());
                return 0;
            }
        }
    }

    public void reset() {
        Pools.get(GlyphRun.class).freeAll(this.runs);
        this.runs.clear();
        this.width = 0.0f;
        this.height = 0.0f;
    }

    public String toString() {
        if (this.runs.size == 0) {
            return BuildConfig.FLAVOR;
        }
        StringBuilder buffer = new StringBuilder(128);
        buffer.append(this.width);
        buffer.append('x');
        buffer.append(this.height);
        buffer.append(10);
        int n = this.runs.size;
        for (int i = 0; i < n; i++) {
            buffer.append(this.runs.get(i).toString());
            buffer.append(10);
        }
        buffer.setLength(buffer.length() - 1);
        return buffer.toString();
    }

    public static class GlyphRun implements Pool.Poolable {
        public final Color color = new Color();
        public Array<BitmapFont.Glyph> glyphs = new Array<>();
        public float width;
        public float x;
        public FloatArray xAdvances = new FloatArray();
        public float y;

        public void reset() {
            this.glyphs.clear();
            this.xAdvances.clear();
            this.width = 0.0f;
        }

        public String toString() {
            StringBuilder buffer = new StringBuilder(this.glyphs.size);
            Array<BitmapFont.Glyph> glyphs2 = this.glyphs;
            int n = glyphs2.size;
            for (int i = 0; i < n; i++) {
                buffer.append((char) glyphs2.get(i).id);
            }
            buffer.append(", #");
            buffer.append(this.color);
            buffer.append(", ");
            buffer.append(this.x);
            buffer.append(", ");
            buffer.append(this.y);
            buffer.append(", ");
            buffer.append(this.width);
            return buffer.toString();
        }
    }
}
