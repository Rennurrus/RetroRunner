package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.OrderedMap;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PixmapPacker implements Disposable {
    static Pattern indexPattern = Pattern.compile("(.+)_(\\d+)$");
    int alphaThreshold;
    private Color c;
    boolean disposed;
    boolean duplicateBorder;
    PackStrategy packStrategy;
    boolean packToTexture;
    int padding;
    Pixmap.Format pageFormat;
    int pageHeight;
    int pageWidth;
    final Array<Page> pages;
    boolean stripWhitespaceX;
    boolean stripWhitespaceY;
    Color transparentColor;

    public interface PackStrategy {
        Page pack(PixmapPacker pixmapPacker, String str, Rectangle rectangle);

        void sort(Array<Pixmap> array);
    }

    public PixmapPacker(int pageWidth2, int pageHeight2, Pixmap.Format pageFormat2, int padding2, boolean duplicateBorder2) {
        this(pageWidth2, pageHeight2, pageFormat2, padding2, duplicateBorder2, false, false, new GuillotineStrategy());
    }

    public PixmapPacker(int pageWidth2, int pageHeight2, Pixmap.Format pageFormat2, int padding2, boolean duplicateBorder2, PackStrategy packStrategy2) {
        this(pageWidth2, pageHeight2, pageFormat2, padding2, duplicateBorder2, false, false, packStrategy2);
    }

    public PixmapPacker(int pageWidth2, int pageHeight2, Pixmap.Format pageFormat2, int padding2, boolean duplicateBorder2, boolean stripWhitespaceX2, boolean stripWhitespaceY2, PackStrategy packStrategy2) {
        this.transparentColor = new Color(0.0f, 0.0f, 0.0f, 0.0f);
        this.pages = new Array<>();
        this.c = new Color();
        this.pageWidth = pageWidth2;
        this.pageHeight = pageHeight2;
        this.pageFormat = pageFormat2;
        this.padding = padding2;
        this.duplicateBorder = duplicateBorder2;
        this.stripWhitespaceX = stripWhitespaceX2;
        this.stripWhitespaceY = stripWhitespaceY2;
        this.packStrategy = packStrategy2;
    }

    public void sort(Array<Pixmap> images) {
        this.packStrategy.sort(images);
    }

    public synchronized Rectangle pack(Pixmap image) {
        return pack((String) null, image);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:106:0x02d7, code lost:
        return r14;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized com.badlogic.gdx.math.Rectangle pack(java.lang.String r37, com.badlogic.gdx.graphics.Pixmap r38) {
        /*
            r36 = this;
            r1 = r36
            r0 = r37
            r10 = r38
            monitor-enter(r36)
            boolean r2 = r1.disposed     // Catch:{ all -> 0x02fb }
            if (r2 == 0) goto L_0x000e
            r2 = 0
            monitor-exit(r36)
            return r2
        L_0x000e:
            if (r0 == 0) goto L_0x002e
            com.badlogic.gdx.math.Rectangle r2 = r36.getRect(r37)     // Catch:{ all -> 0x02fb }
            if (r2 != 0) goto L_0x0017
            goto L_0x002e
        L_0x0017:
            com.badlogic.gdx.utils.GdxRuntimeException r2 = new com.badlogic.gdx.utils.GdxRuntimeException     // Catch:{ all -> 0x02fb }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x02fb }
            r3.<init>()     // Catch:{ all -> 0x02fb }
            java.lang.String r4 = "Pixmap has already been packed with name: "
            r3.append(r4)     // Catch:{ all -> 0x02fb }
            r3.append(r0)     // Catch:{ all -> 0x02fb }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x02fb }
            r2.<init>((java.lang.String) r3)     // Catch:{ all -> 0x02fb }
            throw r2     // Catch:{ all -> 0x02fb }
        L_0x002e:
            r11 = 0
            r12 = 1
            if (r0 == 0) goto L_0x003c
            java.lang.String r2 = ".9"
            boolean r2 = r0.endsWith(r2)     // Catch:{ all -> 0x02fb }
            if (r2 == 0) goto L_0x003c
            r2 = 1
            goto L_0x003d
        L_0x003c:
            r2 = 0
        L_0x003d:
            r13 = r2
            r2 = 0
            if (r13 == 0) goto L_0x00a1
            com.badlogic.gdx.graphics.g2d.PixmapPacker$PixmapPackerRectangle r3 = new com.badlogic.gdx.graphics.g2d.PixmapPacker$PixmapPackerRectangle     // Catch:{ all -> 0x02fb }
            int r4 = r38.getWidth()     // Catch:{ all -> 0x02fb }
            int r4 = r4 + -2
            int r5 = r38.getHeight()     // Catch:{ all -> 0x02fb }
            int r5 = r5 + -2
            r3.<init>(r11, r11, r4, r5)     // Catch:{ all -> 0x02fb }
            r14 = r3
            com.badlogic.gdx.graphics.Pixmap r3 = new com.badlogic.gdx.graphics.Pixmap     // Catch:{ all -> 0x02fb }
            int r4 = r38.getWidth()     // Catch:{ all -> 0x02fb }
            int r4 = r4 + -2
            int r5 = r38.getHeight()     // Catch:{ all -> 0x02fb }
            int r5 = r5 + -2
            com.badlogic.gdx.graphics.Pixmap$Format r6 = r38.getFormat()     // Catch:{ all -> 0x02fb }
            r3.<init>((int) r4, (int) r5, (com.badlogic.gdx.graphics.Pixmap.Format) r6)     // Catch:{ all -> 0x02fb }
            r15 = r3
            com.badlogic.gdx.graphics.Pixmap$Blending r2 = com.badlogic.gdx.graphics.Pixmap.Blending.None     // Catch:{ all -> 0x02fb }
            r15.setBlending(r2)     // Catch:{ all -> 0x02fb }
            int[] r2 = r1.getSplits(r10)     // Catch:{ all -> 0x02fb }
            r14.splits = r2     // Catch:{ all -> 0x02fb }
            int[] r2 = r14.splits     // Catch:{ all -> 0x02fb }
            int[] r2 = r1.getPads(r10, r2)     // Catch:{ all -> 0x02fb }
            r14.pads = r2     // Catch:{ all -> 0x02fb }
            r4 = 0
            r5 = 0
            r6 = 1
            r7 = 1
            int r2 = r38.getWidth()     // Catch:{ all -> 0x02fb }
            int r8 = r2 + -1
            int r2 = r38.getHeight()     // Catch:{ all -> 0x02fb }
            int r9 = r2 + -1
            r2 = r15
            r3 = r38
            r2.drawPixmap(r3, r4, r5, r6, r7, r8, r9)     // Catch:{ all -> 0x02fb }
            r2 = r15
            java.lang.String r3 = "\\."
            java.lang.String[] r3 = r0.split(r3)     // Catch:{ all -> 0x02fb }
            r3 = r3[r11]     // Catch:{ all -> 0x02fb }
            r0 = r3
            r25 = r15
            r15 = r2
            goto L_0x019b
        L_0x00a1:
            boolean r3 = r1.stripWhitespaceX     // Catch:{ all -> 0x02fb }
            if (r3 != 0) goto L_0x00bd
            boolean r3 = r1.stripWhitespaceY     // Catch:{ all -> 0x02fb }
            if (r3 == 0) goto L_0x00aa
            goto L_0x00bd
        L_0x00aa:
            com.badlogic.gdx.graphics.g2d.PixmapPacker$PixmapPackerRectangle r3 = new com.badlogic.gdx.graphics.g2d.PixmapPacker$PixmapPackerRectangle     // Catch:{ all -> 0x02fb }
            int r4 = r38.getWidth()     // Catch:{ all -> 0x02fb }
            int r5 = r38.getHeight()     // Catch:{ all -> 0x02fb }
            r3.<init>(r11, r11, r4, r5)     // Catch:{ all -> 0x02fb }
            r14 = r3
            r25 = r2
            r15 = r10
            goto L_0x019b
        L_0x00bd:
            int r22 = r38.getWidth()     // Catch:{ all -> 0x02fb }
            int r23 = r38.getHeight()     // Catch:{ all -> 0x02fb }
            r3 = 0
            int r4 = r38.getHeight()     // Catch:{ all -> 0x02fb }
            boolean r5 = r1.stripWhitespaceY     // Catch:{ all -> 0x02fb }
            if (r5 == 0) goto L_0x0112
            r5 = r11
        L_0x00cf:
            int r6 = r38.getHeight()     // Catch:{ all -> 0x02fb }
            if (r5 >= r6) goto L_0x00ef
            r6 = r11
        L_0x00d6:
            int r7 = r38.getWidth()     // Catch:{ all -> 0x02fb }
            if (r6 >= r7) goto L_0x00ea
            int r7 = r10.getPixel(r6, r5)     // Catch:{ all -> 0x02fb }
            r8 = r7 & 255(0xff, float:3.57E-43)
            int r9 = r1.alphaThreshold     // Catch:{ all -> 0x02fb }
            if (r8 <= r9) goto L_0x00e7
            goto L_0x00ef
        L_0x00e7:
            int r6 = r6 + 1
            goto L_0x00d6
        L_0x00ea:
            int r3 = r3 + 1
            int r5 = r5 + 1
            goto L_0x00cf
        L_0x00ef:
            int r5 = r38.getHeight()     // Catch:{ all -> 0x02fb }
        L_0x00f3:
            int r5 = r5 + -1
            if (r5 < r3) goto L_0x010f
            r6 = r11
        L_0x00f8:
            int r7 = r38.getWidth()     // Catch:{ all -> 0x02fb }
            if (r6 >= r7) goto L_0x010c
            int r7 = r10.getPixel(r6, r5)     // Catch:{ all -> 0x02fb }
            r8 = r7 & 255(0xff, float:3.57E-43)
            int r9 = r1.alphaThreshold     // Catch:{ all -> 0x02fb }
            if (r8 <= r9) goto L_0x0109
            goto L_0x010f
        L_0x0109:
            int r6 = r6 + 1
            goto L_0x00f8
        L_0x010c:
            int r4 = r4 + -1
            goto L_0x00f3
        L_0x010f:
            r14 = r3
            r15 = r4
            goto L_0x0114
        L_0x0112:
            r14 = r3
            r15 = r4
        L_0x0114:
            r3 = 0
            int r4 = r38.getWidth()     // Catch:{ all -> 0x02fb }
            boolean r5 = r1.stripWhitespaceX     // Catch:{ all -> 0x02fb }
            if (r5 == 0) goto L_0x015a
            r5 = r11
        L_0x011e:
            int r6 = r38.getWidth()     // Catch:{ all -> 0x02fb }
            if (r5 >= r6) goto L_0x013a
            r6 = r14
        L_0x0125:
            if (r6 >= r15) goto L_0x0135
            int r7 = r10.getPixel(r5, r6)     // Catch:{ all -> 0x02fb }
            r8 = r7 & 255(0xff, float:3.57E-43)
            int r9 = r1.alphaThreshold     // Catch:{ all -> 0x02fb }
            if (r8 <= r9) goto L_0x0132
            goto L_0x013a
        L_0x0132:
            int r6 = r6 + 1
            goto L_0x0125
        L_0x0135:
            int r3 = r3 + 1
            int r5 = r5 + 1
            goto L_0x011e
        L_0x013a:
            int r5 = r38.getWidth()     // Catch:{ all -> 0x02fb }
        L_0x013e:
            int r5 = r5 + -1
            if (r5 < r3) goto L_0x0156
            r6 = r14
        L_0x0143:
            if (r6 >= r15) goto L_0x0153
            int r7 = r10.getPixel(r5, r6)     // Catch:{ all -> 0x02fb }
            r8 = r7 & 255(0xff, float:3.57E-43)
            int r9 = r1.alphaThreshold     // Catch:{ all -> 0x02fb }
            if (r8 <= r9) goto L_0x0150
            goto L_0x0156
        L_0x0150:
            int r6 = r6 + 1
            goto L_0x0143
        L_0x0153:
            int r4 = r4 + -1
            goto L_0x013e
        L_0x0156:
            r24 = r3
            r11 = r4
            goto L_0x015d
        L_0x015a:
            r24 = r3
            r11 = r4
        L_0x015d:
            int r9 = r11 - r24
            int r8 = r15 - r14
            com.badlogic.gdx.graphics.Pixmap r3 = new com.badlogic.gdx.graphics.Pixmap     // Catch:{ all -> 0x02fb }
            com.badlogic.gdx.graphics.Pixmap$Format r4 = r38.getFormat()     // Catch:{ all -> 0x02fb }
            r3.<init>((int) r9, (int) r8, (com.badlogic.gdx.graphics.Pixmap.Format) r4)     // Catch:{ all -> 0x02fb }
            r7 = r3
            com.badlogic.gdx.graphics.Pixmap$Blending r2 = com.badlogic.gdx.graphics.Pixmap.Blending.None     // Catch:{ all -> 0x02fb }
            r7.setBlending(r2)     // Catch:{ all -> 0x02fb }
            r4 = 0
            r5 = 0
            r2 = r7
            r3 = r38
            r6 = r24
            r25 = r7
            r7 = r14
            r26 = r8
            r8 = r9
            r27 = r9
            r9 = r26
            r2.drawPixmap(r3, r4, r5, r6, r7, r8, r9)     // Catch:{ all -> 0x02fb }
            r2 = r25
            com.badlogic.gdx.graphics.g2d.PixmapPacker$PixmapPackerRectangle r3 = new com.badlogic.gdx.graphics.g2d.PixmapPacker$PixmapPackerRectangle     // Catch:{ all -> 0x02fb }
            r16 = 0
            r17 = 0
            r4 = r15
            r15 = r3
            r18 = r27
            r19 = r26
            r20 = r24
            r21 = r14
            r15.<init>(r16, r17, r18, r19, r20, r21, r22, r23)     // Catch:{ all -> 0x02fb }
            r14 = r3
            r15 = r2
        L_0x019b:
            float r2 = r14.getWidth()     // Catch:{ all -> 0x02fb }
            int r3 = r1.pageWidth     // Catch:{ all -> 0x02fb }
            float r3 = (float) r3     // Catch:{ all -> 0x02fb }
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 > 0) goto L_0x02d8
            float r2 = r14.getHeight()     // Catch:{ all -> 0x02fb }
            int r3 = r1.pageHeight     // Catch:{ all -> 0x02fb }
            float r3 = (float) r3     // Catch:{ all -> 0x02fb }
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 <= 0) goto L_0x01b5
            r20 = r13
            goto L_0x02da
        L_0x01b5:
            com.badlogic.gdx.graphics.g2d.PixmapPacker$PackStrategy r2 = r1.packStrategy     // Catch:{ all -> 0x02fb }
            com.badlogic.gdx.graphics.g2d.PixmapPacker$Page r2 = r2.pack(r1, r0, r14)     // Catch:{ all -> 0x02fb }
            r11 = r2
            if (r0 == 0) goto L_0x01c8
            com.badlogic.gdx.utils.OrderedMap<java.lang.String, com.badlogic.gdx.graphics.g2d.PixmapPacker$PixmapPackerRectangle> r2 = r11.rects     // Catch:{ all -> 0x02fb }
            r2.put(r0, r14)     // Catch:{ all -> 0x02fb }
            com.badlogic.gdx.utils.Array<java.lang.String> r2 = r11.addedRects     // Catch:{ all -> 0x02fb }
            r2.add(r0)     // Catch:{ all -> 0x02fb }
        L_0x01c8:
            float r2 = r14.x     // Catch:{ all -> 0x02fb }
            int r10 = (int) r2     // Catch:{ all -> 0x02fb }
            float r2 = r14.y     // Catch:{ all -> 0x02fb }
            int r9 = (int) r2     // Catch:{ all -> 0x02fb }
            float r2 = r14.width     // Catch:{ all -> 0x02fb }
            int r8 = (int) r2     // Catch:{ all -> 0x02fb }
            float r2 = r14.height     // Catch:{ all -> 0x02fb }
            int r7 = (int) r2     // Catch:{ all -> 0x02fb }
            boolean r2 = r1.packToTexture     // Catch:{ all -> 0x02fb }
            if (r2 == 0) goto L_0x020b
            boolean r2 = r1.duplicateBorder     // Catch:{ all -> 0x02fb }
            if (r2 != 0) goto L_0x020b
            com.badlogic.gdx.graphics.Texture r2 = r11.texture     // Catch:{ all -> 0x02fb }
            if (r2 == 0) goto L_0x020b
            boolean r2 = r11.dirty     // Catch:{ all -> 0x02fb }
            if (r2 != 0) goto L_0x020b
            com.badlogic.gdx.graphics.Texture r2 = r11.texture     // Catch:{ all -> 0x02fb }
            r2.bind()     // Catch:{ all -> 0x02fb }
            com.badlogic.gdx.graphics.GL20 r26 = com.badlogic.gdx.Gdx.gl     // Catch:{ all -> 0x02fb }
            com.badlogic.gdx.graphics.Texture r2 = r11.texture     // Catch:{ all -> 0x02fb }
            int r2 = r2.glTarget     // Catch:{ all -> 0x02fb }
            r28 = 0
            int r33 = r15.getGLFormat()     // Catch:{ all -> 0x02fb }
            int r34 = r15.getGLType()     // Catch:{ all -> 0x02fb }
            java.nio.ByteBuffer r35 = r15.getPixels()     // Catch:{ all -> 0x02fb }
            r27 = r2
            r29 = r10
            r30 = r9
            r31 = r8
            r32 = r7
            r26.glTexSubImage2D(r27, r28, r29, r30, r31, r32, r33, r34, r35)     // Catch:{ all -> 0x02fb }
            goto L_0x020d
        L_0x020b:
            r11.dirty = r12     // Catch:{ all -> 0x02fb }
        L_0x020d:
            com.badlogic.gdx.graphics.Pixmap r2 = r11.image     // Catch:{ all -> 0x02fb }
            r2.drawPixmap(r15, r10, r9)     // Catch:{ all -> 0x02fb }
            boolean r2 = r1.duplicateBorder     // Catch:{ all -> 0x02fb }
            if (r2 == 0) goto L_0x02c6
            int r2 = r15.getWidth()     // Catch:{ all -> 0x02fb }
            r12 = r2
            int r2 = r15.getHeight()     // Catch:{ all -> 0x02fb }
            r16 = r2
            com.badlogic.gdx.graphics.Pixmap r2 = r11.image     // Catch:{ all -> 0x02fb }
            r4 = 0
            r5 = 0
            r6 = 1
            r17 = 1
            int r18 = r10 + -1
            int r19 = r9 + -1
            r20 = 1
            r21 = 1
            r3 = r15
            r22 = r7
            r7 = r17
            r17 = r8
            r8 = r18
            r18 = r9
            r9 = r19
            r19 = r10
            r10 = r20
            r20 = r13
            r13 = r11
            r11 = r21
            r2.drawPixmap(r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ all -> 0x02fb }
            com.badlogic.gdx.graphics.Pixmap r2 = r13.image     // Catch:{ all -> 0x02fb }
            int r4 = r12 + -1
            r5 = 0
            r6 = 1
            r7 = 1
            int r8 = r19 + r17
            int r9 = r18 + -1
            r10 = 1
            r11 = 1
            r3 = r15
            r2.drawPixmap(r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ all -> 0x02fb }
            com.badlogic.gdx.graphics.Pixmap r2 = r13.image     // Catch:{ all -> 0x02fb }
            r4 = 0
            int r5 = r16 + -1
            r6 = 1
            r7 = 1
            int r8 = r19 + -1
            int r9 = r18 + r22
            r10 = 1
            r11 = 1
            r3 = r15
            r2.drawPixmap(r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ all -> 0x02fb }
            com.badlogic.gdx.graphics.Pixmap r2 = r13.image     // Catch:{ all -> 0x02fb }
            int r4 = r12 + -1
            int r5 = r16 + -1
            r6 = 1
            r7 = 1
            int r8 = r19 + r17
            int r9 = r18 + r22
            r10 = 1
            r11 = 1
            r3 = r15
            r2.drawPixmap(r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ all -> 0x02fb }
            com.badlogic.gdx.graphics.Pixmap r2 = r13.image     // Catch:{ all -> 0x02fb }
            r4 = 0
            r5 = 0
            r7 = 1
            int r9 = r18 + -1
            r11 = 1
            r3 = r15
            r6 = r12
            r8 = r19
            r10 = r17
            r2.drawPixmap(r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ all -> 0x02fb }
            com.badlogic.gdx.graphics.Pixmap r2 = r13.image     // Catch:{ all -> 0x02fb }
            r4 = 0
            int r5 = r16 + -1
            r7 = 1
            int r9 = r18 + r22
            r11 = 1
            r3 = r15
            r6 = r12
            r8 = r19
            r10 = r17
            r2.drawPixmap(r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ all -> 0x02fb }
            com.badlogic.gdx.graphics.Pixmap r2 = r13.image     // Catch:{ all -> 0x02fb }
            r4 = 0
            r5 = 0
            r6 = 1
            int r8 = r19 + -1
            r10 = 1
            r3 = r15
            r7 = r16
            r9 = r18
            r11 = r22
            r2.drawPixmap(r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ all -> 0x02fb }
            com.badlogic.gdx.graphics.Pixmap r2 = r13.image     // Catch:{ all -> 0x02fb }
            int r4 = r12 + -1
            r5 = 0
            r6 = 1
            int r8 = r19 + r17
            r10 = 1
            r3 = r15
            r7 = r16
            r9 = r18
            r11 = r22
            r2.drawPixmap(r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ all -> 0x02fb }
            goto L_0x02d1
        L_0x02c6:
            r22 = r7
            r17 = r8
            r18 = r9
            r19 = r10
            r20 = r13
            r13 = r11
        L_0x02d1:
            if (r25 == 0) goto L_0x02d6
            r25.dispose()     // Catch:{ all -> 0x02fb }
        L_0x02d6:
            monitor-exit(r36)
            return r14
        L_0x02d8:
            r20 = r13
        L_0x02da:
            if (r0 != 0) goto L_0x02e4
            com.badlogic.gdx.utils.GdxRuntimeException r2 = new com.badlogic.gdx.utils.GdxRuntimeException     // Catch:{ all -> 0x02fb }
            java.lang.String r3 = "Page size too small for pixmap."
            r2.<init>((java.lang.String) r3)     // Catch:{ all -> 0x02fb }
            throw r2     // Catch:{ all -> 0x02fb }
        L_0x02e4:
            com.badlogic.gdx.utils.GdxRuntimeException r2 = new com.badlogic.gdx.utils.GdxRuntimeException     // Catch:{ all -> 0x02fb }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x02fb }
            r3.<init>()     // Catch:{ all -> 0x02fb }
            java.lang.String r4 = "Page size too small for pixmap: "
            r3.append(r4)     // Catch:{ all -> 0x02fb }
            r3.append(r0)     // Catch:{ all -> 0x02fb }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x02fb }
            r2.<init>((java.lang.String) r3)     // Catch:{ all -> 0x02fb }
            throw r2     // Catch:{ all -> 0x02fb }
        L_0x02fb:
            r0 = move-exception
            monitor-exit(r36)
            goto L_0x02ff
        L_0x02fe:
            throw r0
        L_0x02ff:
            goto L_0x02fe
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.graphics.g2d.PixmapPacker.pack(java.lang.String, com.badlogic.gdx.graphics.Pixmap):com.badlogic.gdx.math.Rectangle");
    }

    public Array<Page> getPages() {
        return this.pages;
    }

    public synchronized Rectangle getRect(String name) {
        Iterator<Page> it = this.pages.iterator();
        while (it.hasNext()) {
            Rectangle rect = it.next().rects.get(name);
            if (rect != null) {
                return rect;
            }
        }
        return null;
    }

    public synchronized Page getPage(String name) {
        Iterator<Page> it = this.pages.iterator();
        while (it.hasNext()) {
            Page page = it.next();
            if (page.rects.get(name) != null) {
                return page;
            }
        }
        return null;
    }

    public synchronized int getPageIndex(String name) {
        for (int i = 0; i < this.pages.size; i++) {
            if (this.pages.get(i).rects.get(name) != null) {
                return i;
            }
        }
        return -1;
    }

    public synchronized void dispose() {
        Iterator<Page> it = this.pages.iterator();
        while (it.hasNext()) {
            Page page = it.next();
            if (page.texture == null) {
                page.image.dispose();
            }
        }
        this.disposed = true;
    }

    public synchronized TextureAtlas generateTextureAtlas(Texture.TextureFilter minFilter, Texture.TextureFilter magFilter, boolean useMipMaps) {
        TextureAtlas atlas;
        atlas = new TextureAtlas();
        updateTextureAtlas(atlas, minFilter, magFilter, useMipMaps);
        return atlas;
    }

    public synchronized void updateTextureAtlas(TextureAtlas atlas, Texture.TextureFilter minFilter, Texture.TextureFilter magFilter, boolean useMipMaps) {
        updateTextureAtlas(atlas, minFilter, magFilter, useMipMaps, true);
    }

    public synchronized void updateTextureAtlas(TextureAtlas atlas, Texture.TextureFilter minFilter, Texture.TextureFilter magFilter, boolean useMipMaps, boolean useIndexes) {
        synchronized (this) {
            updatePageTextures(minFilter, magFilter, useMipMaps);
            Iterator<Page> it = this.pages.iterator();
            while (it.hasNext()) {
                Page page = it.next();
                if (page.addedRects.size > 0) {
                    Iterator<String> it2 = page.addedRects.iterator();
                    while (it2.hasNext()) {
                        String name = it2.next();
                        PixmapPackerRectangle rect = page.rects.get(name);
                        TextureAtlas.AtlasRegion region = new TextureAtlas.AtlasRegion(page.texture, (int) rect.x, (int) rect.y, (int) rect.width, (int) rect.height);
                        if (rect.splits != null) {
                            region.splits = rect.splits;
                            region.pads = rect.pads;
                        }
                        int imageIndex = -1;
                        String imageName = name;
                        if (useIndexes) {
                            Matcher matcher = indexPattern.matcher(imageName);
                            if (matcher.matches()) {
                                imageName = matcher.group(1);
                                imageIndex = Integer.parseInt(matcher.group(2));
                            }
                        }
                        region.name = imageName;
                        region.index = imageIndex;
                        region.offsetX = (float) rect.offsetX;
                        region.offsetY = (float) ((int) ((((float) rect.originalHeight) - rect.height) - ((float) rect.offsetY)));
                        region.originalWidth = rect.originalWidth;
                        region.originalHeight = rect.originalHeight;
                        atlas.getRegions().add(region);
                    }
                    page.addedRects.clear();
                    atlas.getTextures().add(page.texture);
                }
            }
        }
    }

    public synchronized void updateTextureRegions(Array<TextureRegion> regions, Texture.TextureFilter minFilter, Texture.TextureFilter magFilter, boolean useMipMaps) {
        updatePageTextures(minFilter, magFilter, useMipMaps);
        while (regions.size < this.pages.size) {
            regions.add(new TextureRegion(this.pages.get(regions.size).texture));
        }
    }

    public synchronized void updatePageTextures(Texture.TextureFilter minFilter, Texture.TextureFilter magFilter, boolean useMipMaps) {
        Iterator<Page> it = this.pages.iterator();
        while (it.hasNext()) {
            it.next().updateTexture(minFilter, magFilter, useMipMaps);
        }
    }

    public int getPageWidth() {
        return this.pageWidth;
    }

    public void setPageWidth(int pageWidth2) {
        this.pageWidth = pageWidth2;
    }

    public int getPageHeight() {
        return this.pageHeight;
    }

    public void setPageHeight(int pageHeight2) {
        this.pageHeight = pageHeight2;
    }

    public Pixmap.Format getPageFormat() {
        return this.pageFormat;
    }

    public void setPageFormat(Pixmap.Format pageFormat2) {
        this.pageFormat = pageFormat2;
    }

    public int getPadding() {
        return this.padding;
    }

    public void setPadding(int padding2) {
        this.padding = padding2;
    }

    public boolean getDuplicateBorder() {
        return this.duplicateBorder;
    }

    public void setDuplicateBorder(boolean duplicateBorder2) {
        this.duplicateBorder = duplicateBorder2;
    }

    public boolean getPackToTexture() {
        return this.packToTexture;
    }

    public void setPackToTexture(boolean packToTexture2) {
        this.packToTexture = packToTexture2;
    }

    public static class Page {
        final Array<String> addedRects = new Array<>();
        boolean dirty;
        Pixmap image;
        OrderedMap<String, PixmapPackerRectangle> rects = new OrderedMap<>();
        Texture texture;

        public Page(PixmapPacker packer) {
            this.image = new Pixmap(packer.pageWidth, packer.pageHeight, packer.pageFormat);
            this.image.setBlending(Pixmap.Blending.None);
            this.image.setColor(packer.getTransparentColor());
            this.image.fill();
        }

        public Pixmap getPixmap() {
            return this.image;
        }

        public OrderedMap<String, PixmapPackerRectangle> getRects() {
            return this.rects;
        }

        public Texture getTexture() {
            return this.texture;
        }

        public boolean updateTexture(Texture.TextureFilter minFilter, Texture.TextureFilter magFilter, boolean useMipMaps) {
            Texture texture2 = this.texture;
            if (texture2 == null) {
                Pixmap pixmap = this.image;
                this.texture = new Texture(new PixmapTextureData(pixmap, pixmap.getFormat(), useMipMaps, false, true)) {
                    public void dispose() {
                        super.dispose();
                        Page.this.image.dispose();
                    }
                };
                this.texture.setFilter(minFilter, magFilter);
            } else if (!this.dirty) {
                return false;
            } else {
                texture2.load(texture2.getTextureData());
            }
            this.dirty = false;
            return true;
        }
    }

    public static class GuillotineStrategy implements PackStrategy {
        Comparator<Pixmap> comparator;

        public void sort(Array<Pixmap> pixmaps) {
            if (this.comparator == null) {
                this.comparator = new Comparator<Pixmap>() {
                    public int compare(Pixmap o1, Pixmap o2) {
                        return Math.max(o1.getWidth(), o1.getHeight()) - Math.max(o2.getWidth(), o2.getHeight());
                    }
                };
            }
            pixmaps.sort(this.comparator);
        }

        public Page pack(PixmapPacker packer, String name, Rectangle rect) {
            GuillotinePage page;
            if (packer.pages.size == 0) {
                page = new GuillotinePage(packer);
                packer.pages.add(page);
            } else {
                page = (GuillotinePage) packer.pages.peek();
            }
            int padding = packer.padding;
            rect.width += (float) padding;
            rect.height += (float) padding;
            Node node = insert(page.root, rect);
            if (node == null) {
                page = new GuillotinePage(packer);
                packer.pages.add(page);
                node = insert(page.root, rect);
            }
            node.full = true;
            rect.set(node.rect.x, node.rect.y, node.rect.width - ((float) padding), node.rect.height - ((float) padding));
            return page;
        }

        private Node insert(Node node, Rectangle rect) {
            if (!node.full && node.leftChild != null && node.rightChild != null) {
                Node newNode = insert(node.leftChild, rect);
                if (newNode == null) {
                    return insert(node.rightChild, rect);
                }
                return newNode;
            } else if (node.full) {
                return null;
            } else {
                if (node.rect.width == rect.width && node.rect.height == rect.height) {
                    return node;
                }
                if (node.rect.width < rect.width || node.rect.height < rect.height) {
                    return null;
                }
                node.leftChild = new Node();
                node.rightChild = new Node();
                if (((int) node.rect.width) - ((int) rect.width) > ((int) node.rect.height) - ((int) rect.height)) {
                    node.leftChild.rect.x = node.rect.x;
                    node.leftChild.rect.y = node.rect.y;
                    node.leftChild.rect.width = rect.width;
                    node.leftChild.rect.height = node.rect.height;
                    node.rightChild.rect.x = node.rect.x + rect.width;
                    node.rightChild.rect.y = node.rect.y;
                    node.rightChild.rect.width = node.rect.width - rect.width;
                    node.rightChild.rect.height = node.rect.height;
                } else {
                    node.leftChild.rect.x = node.rect.x;
                    node.leftChild.rect.y = node.rect.y;
                    node.leftChild.rect.width = node.rect.width;
                    node.leftChild.rect.height = rect.height;
                    node.rightChild.rect.x = node.rect.x;
                    node.rightChild.rect.y = node.rect.y + rect.height;
                    node.rightChild.rect.width = node.rect.width;
                    node.rightChild.rect.height = node.rect.height - rect.height;
                }
                return insert(node.leftChild, rect);
            }
        }

        static final class Node {
            public boolean full;
            public Node leftChild;
            public final Rectangle rect = new Rectangle();
            public Node rightChild;

            Node() {
            }
        }

        static class GuillotinePage extends Page {
            Node root = new Node();

            public GuillotinePage(PixmapPacker packer) {
                super(packer);
                this.root.rect.x = (float) packer.padding;
                this.root.rect.y = (float) packer.padding;
                this.root.rect.width = (float) (packer.pageWidth - (packer.padding * 2));
                this.root.rect.height = (float) (packer.pageHeight - (packer.padding * 2));
            }
        }
    }

    public static class SkylineStrategy implements PackStrategy {
        Comparator<Pixmap> comparator;

        public void sort(Array<Pixmap> images) {
            if (this.comparator == null) {
                this.comparator = new Comparator<Pixmap>() {
                    public int compare(Pixmap o1, Pixmap o2) {
                        return o1.getHeight() - o2.getHeight();
                    }
                };
            }
            images.sort(this.comparator);
        }

        public Page pack(PixmapPacker packer, String name, Rectangle rect) {
            PixmapPacker pixmapPacker = packer;
            Rectangle rectangle = rect;
            int padding = pixmapPacker.padding;
            int pageWidth = pixmapPacker.pageWidth - (padding * 2);
            int pageHeight = pixmapPacker.pageHeight - (padding * 2);
            int rectWidth = ((int) rectangle.width) + padding;
            int rectHeight = ((int) rectangle.height) + padding;
            int n = pixmapPacker.pages.size;
            for (int i = 0; i < n; i++) {
                SkylinePage page = (SkylinePage) pixmapPacker.pages.get(i);
                SkylinePage.Row bestRow = null;
                int nn = page.rows.size - 1;
                for (int ii = 0; ii < nn; ii++) {
                    SkylinePage.Row row = page.rows.get(ii);
                    if (row.x + rectWidth < pageWidth && row.y + rectHeight < pageHeight && rectHeight <= row.height && (bestRow == null || row.height < bestRow.height)) {
                        bestRow = row;
                    }
                }
                if (bestRow == null) {
                    SkylinePage.Row row2 = page.rows.peek();
                    if (row2.y + rectHeight >= pageHeight) {
                        continue;
                    } else if (row2.x + rectWidth < pageWidth) {
                        row2.height = Math.max(row2.height, rectHeight);
                        bestRow = row2;
                    } else if (row2.y + row2.height + rectHeight < pageHeight) {
                        bestRow = new SkylinePage.Row();
                        bestRow.y = row2.y + row2.height;
                        bestRow.height = rectHeight;
                        page.rows.add(bestRow);
                    }
                }
                if (bestRow != null) {
                    rectangle.x = (float) bestRow.x;
                    rectangle.y = (float) bestRow.y;
                    bestRow.x += rectWidth;
                    return page;
                }
            }
            SkylinePage page2 = new SkylinePage(pixmapPacker);
            pixmapPacker.pages.add(page2);
            SkylinePage.Row row3 = new SkylinePage.Row();
            row3.x = padding + rectWidth;
            row3.y = padding;
            row3.height = rectHeight;
            page2.rows.add(row3);
            rectangle.x = (float) padding;
            rectangle.y = (float) padding;
            return page2;
        }

        static class SkylinePage extends Page {
            Array<Row> rows = new Array<>();

            public SkylinePage(PixmapPacker packer) {
                super(packer);
            }

            static class Row {
                int height;
                int x;
                int y;

                Row() {
                }
            }
        }
    }

    public Color getTransparentColor() {
        return this.transparentColor;
    }

    public void setTransparentColor(Color color) {
        this.transparentColor.set(color);
    }

    private int[] getSplits(Pixmap raster) {
        int endX;
        int endY;
        int startX = getSplitPoint(raster, 1, 0, true, true);
        int endX2 = getSplitPoint(raster, startX, 0, false, true);
        int startY = getSplitPoint(raster, 0, 1, true, false);
        int endY2 = getSplitPoint(raster, 0, startY, false, false);
        getSplitPoint(raster, endX2 + 1, 0, true, true);
        getSplitPoint(raster, 0, endY2 + 1, true, false);
        if (startX == 0 && endX2 == 0 && startY == 0 && endY2 == 0) {
            return null;
        }
        if (startX != 0) {
            startX--;
            endX = (raster.getWidth() - 2) - (endX2 - 1);
        } else {
            endX = raster.getWidth() - 2;
        }
        if (startY != 0) {
            startY--;
            endY = (raster.getHeight() - 2) - (endY2 - 1);
        } else {
            endY = raster.getHeight() - 2;
        }
        return new int[]{startX, endX, startY, endY};
    }

    private int[] getPads(Pixmap raster, int[] splits) {
        int endX;
        int endY;
        int[] iArr = splits;
        int bottom = raster.getHeight() - 1;
        int right = raster.getWidth() - 1;
        int startX = getSplitPoint(raster, 1, bottom, true, true);
        int startY = getSplitPoint(raster, right, 1, true, false);
        int endX2 = 0;
        int endY2 = 0;
        if (startX != 0) {
            endX2 = getSplitPoint(raster, startX + 1, bottom, false, true);
        }
        int endX3 = endX2;
        if (startY != 0) {
            endY2 = getSplitPoint(raster, right, startY + 1, false, false);
        }
        getSplitPoint(raster, endX3 + 1, bottom, true, true);
        getSplitPoint(raster, right, endY2 + 1, true, false);
        if (startX == 0 && endX3 == 0 && startY == 0 && endY2 == 0) {
            return null;
        }
        if (startX == 0 && endX3 == 0) {
            startX = -1;
            endX = -1;
        } else if (startX > 0) {
            startX--;
            endX = (raster.getWidth() - 2) - (endX3 - 1);
        } else {
            endX = raster.getWidth() - 2;
        }
        if (startY == 0 && endY2 == 0) {
            startY = -1;
            endY = -1;
        } else if (startY > 0) {
            startY--;
            endY = (raster.getHeight() - 2) - (endY2 - 1);
        } else {
            endY = raster.getHeight() - 2;
        }
        int[] pads = {startX, endX, startY, endY};
        if (iArr == null || !Arrays.equals(pads, iArr)) {
            return pads;
        }
        return null;
    }

    private int getSplitPoint(Pixmap raster, int startX, int startY, boolean startPoint, boolean xAxis) {
        int[] rgba = new int[4];
        int end = xAxis ? raster.getWidth() : raster.getHeight();
        int breakA = startPoint ? 255 : 0;
        int x = startX;
        int y = startY;
        for (int next = xAxis ? startX : startY; next != end; next++) {
            if (xAxis) {
                x = next;
            } else {
                y = next;
            }
            this.c.set(raster.getPixel(x, y));
            rgba[0] = (int) (this.c.r * 255.0f);
            rgba[1] = (int) (this.c.g * 255.0f);
            rgba[2] = (int) (this.c.b * 255.0f);
            rgba[3] = (int) (this.c.a * 255.0f);
            if (rgba[3] == breakA) {
                return next;
            }
            if (!startPoint && !(rgba[0] == 0 && rgba[1] == 0 && rgba[2] == 0 && rgba[3] == 255)) {
                PrintStream printStream = System.out;
                printStream.println(x + "  " + y + " " + rgba + " ");
            }
        }
        Pixmap pixmap = raster;
        return 0;
    }

    public static class PixmapPackerRectangle extends Rectangle {
        int offsetX;
        int offsetY;
        int originalHeight;
        int originalWidth;
        int[] pads;
        int[] splits;

        PixmapPackerRectangle(int x, int y, int width, int height) {
            super((float) x, (float) y, (float) width, (float) height);
            this.offsetX = 0;
            this.offsetY = 0;
            this.originalWidth = width;
            this.originalHeight = height;
        }

        PixmapPackerRectangle(int x, int y, int width, int height, int left, int top, int originalWidth2, int originalHeight2) {
            super((float) x, (float) y, (float) width, (float) height);
            this.offsetX = left;
            this.offsetY = top;
            this.originalWidth = originalWidth2;
            this.originalHeight = originalHeight2;
        }
    }
}
