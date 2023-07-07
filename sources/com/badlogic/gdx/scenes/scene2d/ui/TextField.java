package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils;
import com.badlogic.gdx.utils.Clipboard;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.Timer;
import com.twi.game.BuildConfig;

public class TextField extends Widget implements Disableable {
    private static final char BACKSPACE = '\b';
    private static final char BULLET = '';
    private static final char DELETE = '';
    protected static final char ENTER_ANDROID = '\n';
    protected static final char ENTER_DESKTOP = '\r';
    private static final char TAB = '\t';
    public static float keyRepeatInitialTime = 0.4f;
    public static float keyRepeatTime = 0.1f;
    private static final Vector2 tmp1 = new Vector2();
    private static final Vector2 tmp2 = new Vector2();
    private static final Vector2 tmp3 = new Vector2();
    final Timer.Task blinkTask;
    float blinkTime;
    Clipboard clipboard;
    protected int cursor;
    boolean cursorOn;
    boolean disabled;
    protected CharSequence displayText;
    TextFieldFilter filter;
    boolean focusTraversal;
    boolean focused;
    protected float fontOffset;
    protected final FloatArray glyphPositions;
    protected boolean hasSelection;
    InputListener inputListener;
    final KeyRepeatTask keyRepeatTask;
    OnscreenKeyboard keyboard;
    long lastChangeTime;
    protected final GlyphLayout layout;
    TextFieldListener listener;
    private int maxLength;
    private String messageText;
    boolean onlyFontChars;
    private StringBuilder passwordBuffer;
    private char passwordCharacter;
    boolean passwordMode;
    boolean programmaticChangeEvents;
    float renderOffset;
    protected int selectionStart;
    private float selectionWidth;
    private float selectionX;
    TextFieldStyle style;
    protected String text;
    private int textHAlign;
    protected float textHeight;
    protected float textOffset;
    String undoText;
    private int visibleTextEnd;
    private int visibleTextStart;
    protected boolean writeEnters;

    public interface OnscreenKeyboard {
        void show(boolean z);
    }

    public interface TextFieldListener {
        void keyTyped(TextField textField, char c);
    }

    public TextField(String text2, Skin skin) {
        this(text2, (TextFieldStyle) skin.get(TextFieldStyle.class));
    }

    public TextField(String text2, Skin skin, String styleName) {
        this(text2, (TextFieldStyle) skin.get(styleName, TextFieldStyle.class));
    }

    public TextField(String text2, TextFieldStyle style2) {
        this.layout = new GlyphLayout();
        this.glyphPositions = new FloatArray();
        this.keyboard = new DefaultOnscreenKeyboard();
        this.focusTraversal = true;
        this.onlyFontChars = true;
        this.textHAlign = 8;
        this.undoText = BuildConfig.FLAVOR;
        this.passwordCharacter = BULLET;
        this.maxLength = 0;
        this.blinkTime = 0.32f;
        this.blinkTask = new Timer.Task() {
            public void run() {
                TextField textField = TextField.this;
                textField.cursorOn = !textField.cursorOn;
                Gdx.graphics.requestRendering();
            }
        };
        this.keyRepeatTask = new KeyRepeatTask();
        setStyle(style2);
        this.clipboard = Gdx.app.getClipboard();
        initialize();
        setText(text2);
        setSize(getPrefWidth(), getPrefHeight());
    }

    /* access modifiers changed from: protected */
    public void initialize() {
        InputListener createInputListener = createInputListener();
        this.inputListener = createInputListener;
        addListener(createInputListener);
    }

    /* access modifiers changed from: protected */
    public InputListener createInputListener() {
        return new TextFieldClickListener();
    }

    /* access modifiers changed from: protected */
    public int letterUnderCursor(float x) {
        float x2 = x - (((this.textOffset + this.fontOffset) - this.style.font.getData().cursorX) - this.glyphPositions.get(this.visibleTextStart));
        if (getBackgroundDrawable() != null) {
            x2 -= this.style.background.getLeftWidth();
        }
        int n = this.glyphPositions.size;
        float[] glyphPositions2 = this.glyphPositions.items;
        int i = 1;
        while (i < n) {
            if (glyphPositions2[i] <= x2) {
                i++;
            } else if (glyphPositions2[i] - x2 <= x2 - glyphPositions2[i - 1]) {
                return i;
            } else {
                return i - 1;
            }
        }
        return n - 1;
    }

    /* access modifiers changed from: protected */
    public boolean isWordCharacter(char c) {
        return Character.isLetterOrDigit(c);
    }

    /* access modifiers changed from: protected */
    public int[] wordUnderCursor(int at) {
        String text2 = this.text;
        int start = at;
        int right = text2.length();
        int left = 0;
        int index = start;
        if (at >= text2.length()) {
            left = text2.length();
            right = 0;
        } else {
            while (true) {
                if (index >= right) {
                    break;
                } else if (!isWordCharacter(text2.charAt(index))) {
                    right = index;
                    break;
                } else {
                    index++;
                }
            }
            int index2 = start - 1;
            while (true) {
                if (index2 <= -1) {
                    break;
                } else if (!isWordCharacter(text2.charAt(index2))) {
                    left = index2 + 1;
                    break;
                } else {
                    index2--;
                }
            }
        }
        return new int[]{left, right};
    }

    /* access modifiers changed from: package-private */
    public int[] wordUnderCursor(float x) {
        return wordUnderCursor(letterUnderCursor(x));
    }

    /* access modifiers changed from: package-private */
    public boolean withinMaxLength(int size) {
        int i = this.maxLength;
        return i <= 0 || size < i;
    }

    public void setMaxLength(int maxLength2) {
        this.maxLength = maxLength2;
    }

    public int getMaxLength() {
        return this.maxLength;
    }

    public void setOnlyFontChars(boolean onlyFontChars2) {
        this.onlyFontChars = onlyFontChars2;
    }

    public void setStyle(TextFieldStyle style2) {
        if (style2 != null) {
            this.style = style2;
            this.textHeight = style2.font.getCapHeight() - (style2.font.getDescent() * 2.0f);
            invalidateHierarchy();
            return;
        }
        throw new IllegalArgumentException("style cannot be null.");
    }

    public TextFieldStyle getStyle() {
        return this.style;
    }

    /* access modifiers changed from: protected */
    public void calculateOffsets() {
        float visibleWidth = getWidth();
        Drawable background = getBackgroundDrawable();
        if (background != null) {
            visibleWidth -= background.getLeftWidth() + background.getRightWidth();
        }
        int glyphCount = this.glyphPositions.size;
        float[] glyphPositions2 = this.glyphPositions.items;
        float f = glyphPositions2[Math.max(0, this.cursor - 1)];
        float f2 = this.renderOffset;
        float distance = f + f2;
        if (distance <= 0.0f) {
            this.renderOffset = f2 - distance;
        } else {
            float minX = glyphPositions2[Math.min(glyphCount - 1, this.cursor + 1)] - visibleWidth;
            if ((-this.renderOffset) < minX) {
                this.renderOffset = -minX;
            }
        }
        float maxOffset = 0.0f;
        float width = glyphPositions2[glyphCount - 1];
        for (int i = glyphCount - 2; i >= 0; i--) {
            float x = glyphPositions2[i];
            if (width - x > visibleWidth) {
                break;
            }
            maxOffset = x;
        }
        if ((-this.renderOffset) > maxOffset) {
            this.renderOffset = -maxOffset;
        }
        this.visibleTextStart = 0;
        float startX = 0.0f;
        int i2 = 0;
        while (true) {
            if (i2 >= glyphCount) {
                break;
            } else if (glyphPositions2[i2] >= (-this.renderOffset)) {
                this.visibleTextStart = i2;
                startX = glyphPositions2[i2];
                break;
            } else {
                i2++;
            }
        }
        int end = this.visibleTextStart + 1;
        float endX = visibleWidth - this.renderOffset;
        int n = Math.min(this.displayText.length(), glyphCount);
        while (end <= n && glyphPositions2[end] <= endX) {
            end++;
        }
        this.visibleTextEnd = Math.max(0, end - 1);
        int i3 = this.textHAlign;
        if ((i3 & 8) == 0) {
            this.textOffset = visibleWidth - (glyphPositions2[this.visibleTextEnd] - startX);
            if ((i3 & 1) != 0) {
                this.textOffset = (float) Math.round(this.textOffset * 0.5f);
            }
        } else {
            this.textOffset = this.renderOffset + startX;
        }
        if (this.hasSelection) {
            int minIndex = Math.min(this.cursor, this.selectionStart);
            int maxIndex = Math.max(this.cursor, this.selectionStart);
            float minX2 = Math.max(glyphPositions2[minIndex] - glyphPositions2[this.visibleTextStart], -this.textOffset);
            float maxX = Math.min(glyphPositions2[maxIndex] - glyphPositions2[this.visibleTextStart], visibleWidth - this.textOffset);
            this.selectionX = minX2;
            float f3 = visibleWidth;
            this.selectionWidth = (maxX - minX2) - this.style.font.getData().cursorX;
            return;
        }
    }

    private Drawable getBackgroundDrawable() {
        boolean focused2 = hasKeyboardFocus();
        if (!this.disabled || this.style.disabledBackground == null) {
            return (!focused2 || this.style.focusedBackground == null) ? this.style.background : this.style.focusedBackground;
        }
        return this.style.disabledBackground;
    }

    public void draw(Batch batch, float parentAlpha) {
        Batch batch2 = batch;
        boolean focused2 = hasKeyboardFocus();
        if (focused2 != this.focused) {
            this.focused = focused2;
            this.blinkTask.cancel();
            this.cursorOn = focused2;
            if (focused2) {
                Timer.Task task = this.blinkTask;
                float f = this.blinkTime;
                Timer.schedule(task, f, f);
            } else {
                this.keyRepeatTask.cancel();
            }
        } else if (!focused2) {
            this.cursorOn = false;
        }
        BitmapFont font = this.style.font;
        Color fontColor = (!this.disabled || this.style.disabledFontColor == null) ? (!focused2 || this.style.focusedFontColor == null) ? this.style.fontColor : this.style.focusedFontColor : this.style.disabledFontColor;
        Drawable selection = this.style.selection;
        Drawable cursorPatch = this.style.cursor;
        Drawable background = getBackgroundDrawable();
        Color color = getColor();
        float x = getX();
        float y = getY();
        float width = getWidth();
        float height = getHeight();
        batch2.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        float bgLeftWidth = 0.0f;
        float bgRightWidth = 0.0f;
        if (background != null) {
            background.draw(batch, x, y, width, height);
            bgLeftWidth = background.getLeftWidth();
            bgRightWidth = background.getRightWidth();
        }
        float textY = getTextY(font, background);
        calculateOffsets();
        if (focused2 && this.hasSelection && selection != null) {
            drawSelection(selection, batch, font, x + bgLeftWidth, y + textY);
        }
        float yOffset = font.isFlipped() ? -this.textHeight : 0.0f;
        if (this.displayText.length() != 0) {
            font.setColor(fontColor.r, fontColor.g, fontColor.b, fontColor.a * color.a * parentAlpha);
            drawText(batch2, font, x + bgLeftWidth, y + textY + yOffset);
        } else if (!focused2 && this.messageText != null) {
            BitmapFont messageFont = this.style.messageFont != null ? this.style.messageFont : font;
            if (this.style.messageFontColor != null) {
                messageFont.setColor(this.style.messageFontColor.r, this.style.messageFontColor.g, this.style.messageFontColor.b, this.style.messageFontColor.a * color.a * parentAlpha);
            } else {
                messageFont.setColor(0.7f, 0.7f, 0.7f, color.a * parentAlpha);
            }
            BitmapFont bitmapFont = messageFont;
            drawMessageText(batch, messageFont, x + bgLeftWidth, y + textY + yOffset, (width - bgLeftWidth) - bgRightWidth);
        }
        if (!this.disabled && this.cursorOn && cursorPatch != null) {
            drawCursor(cursorPatch, batch, font, x + bgLeftWidth, y + textY);
        }
    }

    /* access modifiers changed from: protected */
    public float getTextY(BitmapFont font, Drawable background) {
        float textY;
        float height = getHeight();
        float textY2 = (this.textHeight / 2.0f) + font.getDescent();
        if (background != null) {
            float bottom = background.getBottomHeight();
            textY = (((height - background.getTopHeight()) - bottom) / 2.0f) + textY2 + bottom;
        } else {
            textY = textY2 + (height / 2.0f);
        }
        if (font.usesIntegerPositions()) {
            return (float) ((int) textY);
        }
        return textY;
    }

    /* access modifiers changed from: protected */
    public void drawSelection(Drawable selection, Batch batch, BitmapFont font, float x, float y) {
        selection.draw(batch, this.textOffset + x + this.selectionX + this.fontOffset, (y - this.textHeight) - font.getDescent(), this.selectionWidth, this.textHeight);
    }

    /* access modifiers changed from: protected */
    public void drawText(Batch batch, BitmapFont font, float x, float y) {
        font.draw(batch, this.displayText, x + this.textOffset, y, this.visibleTextStart, this.visibleTextEnd, 0.0f, 8, false);
    }

    /* access modifiers changed from: protected */
    public void drawMessageText(Batch batch, BitmapFont font, float x, float y, float maxWidth) {
        String str = this.messageText;
        font.draw(batch, str, x, y, 0, str.length(), maxWidth, this.textHAlign, false, "...");
    }

    /* access modifiers changed from: protected */
    public void drawCursor(Drawable cursorPatch, Batch batch, BitmapFont font, float x, float y) {
        cursorPatch.draw(batch, (((this.textOffset + x) + this.glyphPositions.get(this.cursor)) - this.glyphPositions.get(this.visibleTextStart)) + this.fontOffset + font.getData().cursorX, (y - this.textHeight) - font.getDescent(), cursorPatch.getMinWidth(), this.textHeight);
    }

    /* access modifiers changed from: package-private */
    public void updateDisplayText() {
        BitmapFont font = this.style.font;
        BitmapFont.BitmapFontData data = font.getData();
        String text2 = this.text;
        int textLength = text2.length();
        StringBuilder buffer = new StringBuilder();
        int i = 0;
        while (true) {
            char c = ' ';
            if (i >= textLength) {
                break;
            }
            char c2 = text2.charAt(i);
            if (data.hasGlyph(c2)) {
                c = c2;
            }
            buffer.append(c);
            i++;
        }
        String newDisplayText = buffer.toString();
        if (!this.passwordMode || !data.hasGlyph(this.passwordCharacter)) {
            this.displayText = newDisplayText;
        } else {
            if (this.passwordBuffer == null) {
                this.passwordBuffer = new StringBuilder(newDisplayText.length());
            }
            if (this.passwordBuffer.length() > textLength) {
                this.passwordBuffer.setLength(textLength);
            } else {
                for (int i2 = this.passwordBuffer.length(); i2 < textLength; i2++) {
                    this.passwordBuffer.append(this.passwordCharacter);
                }
            }
            this.displayText = this.passwordBuffer;
        }
        this.layout.setText(font, this.displayText.toString().replace(ENTER_DESKTOP, ' ').replace(ENTER_ANDROID, ' '));
        this.glyphPositions.clear();
        float x = 0.0f;
        if (this.layout.runs.size > 0) {
            FloatArray xAdvances = this.layout.runs.first().xAdvances;
            this.fontOffset = xAdvances.first();
            int n = xAdvances.size;
            for (int i3 = 1; i3 < n; i3++) {
                this.glyphPositions.add(x);
                x += xAdvances.get(i3);
            }
        } else {
            this.fontOffset = 0.0f;
        }
        this.glyphPositions.add(x);
        this.visibleTextStart = Math.min(this.visibleTextStart, this.glyphPositions.size - 1);
        this.visibleTextEnd = MathUtils.clamp(this.visibleTextEnd, this.visibleTextStart, this.glyphPositions.size - 1);
        if (this.selectionStart > newDisplayText.length()) {
            this.selectionStart = textLength;
        }
    }

    public void copy() {
        if (this.hasSelection && !this.passwordMode) {
            this.clipboard.setContents(this.text.substring(Math.min(this.cursor, this.selectionStart), Math.max(this.cursor, this.selectionStart)));
        }
    }

    public void cut() {
        cut(this.programmaticChangeEvents);
    }

    /* access modifiers changed from: package-private */
    public void cut(boolean fireChangeEvent) {
        if (this.hasSelection && !this.passwordMode) {
            copy();
            this.cursor = delete(fireChangeEvent);
            updateDisplayText();
        }
    }

    /* access modifiers changed from: package-private */
    public void paste(String content, boolean fireChangeEvent) {
        TextFieldFilter textFieldFilter;
        if (content != null) {
            StringBuilder buffer = new StringBuilder();
            int textLength = this.text.length();
            if (this.hasSelection) {
                textLength -= Math.abs(this.cursor - this.selectionStart);
            }
            BitmapFont.BitmapFontData data = this.style.font.getData();
            int n = content.length();
            for (int i = 0; i < n && withinMaxLength(buffer.length() + textLength); i++) {
                char c = content.charAt(i);
                if ((this.writeEnters && (c == 10 || c == 13)) || !(c == 13 || c == 10 || ((this.onlyFontChars && !data.hasGlyph(c)) || ((textFieldFilter = this.filter) != null && !textFieldFilter.acceptChar(this, c))))) {
                    buffer.append(c);
                }
            }
            String content2 = buffer.toString();
            if (this.hasSelection) {
                this.cursor = delete(fireChangeEvent);
            }
            if (fireChangeEvent) {
                String str = this.text;
                changeText(str, insert(this.cursor, content2, str));
            } else {
                this.text = insert(this.cursor, content2, this.text);
            }
            updateDisplayText();
            this.cursor += content2.length();
        }
    }

    /* access modifiers changed from: package-private */
    public String insert(int position, CharSequence text2, String to) {
        if (to.length() == 0) {
            return text2.toString();
        }
        return to.substring(0, position) + text2 + to.substring(position, to.length());
    }

    /* access modifiers changed from: package-private */
    public int delete(boolean fireChangeEvent) {
        int from = this.selectionStart;
        int to = this.cursor;
        int minIndex = Math.min(from, to);
        int maxIndex = Math.max(from, to);
        StringBuilder sb = new StringBuilder();
        String str = BuildConfig.FLAVOR;
        sb.append(minIndex > 0 ? this.text.substring(0, minIndex) : str);
        if (maxIndex < this.text.length()) {
            String str2 = this.text;
            str = str2.substring(maxIndex, str2.length());
        }
        sb.append(str);
        String newText = sb.toString();
        if (fireChangeEvent) {
            changeText(this.text, newText);
        } else {
            this.text = newText;
        }
        clearSelection();
        return minIndex;
    }

    public void next(boolean up) {
        Stage stage = getStage();
        if (stage != null) {
            TextField current = this;
            Vector2 currentCoords = current.getParent().localToStageCoordinates(tmp2.set(current.getX(), current.getY()));
            Vector2 bestCoords = tmp1;
            while (true) {
                TextField textField = current.findNextTextField(stage.getActors(), (TextField) null, bestCoords, currentCoords, up);
                if (textField == null) {
                    if (up) {
                        currentCoords.set(-3.4028235E38f, -3.4028235E38f);
                    } else {
                        currentCoords.set(Float.MAX_VALUE, Float.MAX_VALUE);
                    }
                    textField = current.findNextTextField(stage.getActors(), (TextField) null, bestCoords, currentCoords, up);
                }
                if (textField == null) {
                    Gdx.input.setOnscreenKeyboardVisible(false);
                    return;
                } else if (stage.setKeyboardFocus(textField)) {
                    textField.selectAll();
                    return;
                } else {
                    current = textField;
                    currentCoords.set(bestCoords);
                }
            }
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v1, resolved type: com.badlogic.gdx.scenes.scene2d.Actor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v4, resolved type: com.badlogic.gdx.scenes.scene2d.Group} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v8, resolved type: com.badlogic.gdx.scenes.scene2d.ui.TextField} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v4, resolved type: com.badlogic.gdx.scenes.scene2d.ui.TextField} */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x009c, code lost:
        if (((r2.y > r7.y) ^ r21) != false) goto L_0x00a1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x00b9, code lost:
        if (((r2.x < r7.x) ^ r21) != false) goto L_0x00bd;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x006d  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0081 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0087  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x00a4  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x00c0  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x00e2 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.badlogic.gdx.scenes.scene2d.ui.TextField findNextTextField(com.badlogic.gdx.utils.Array<com.badlogic.gdx.scenes.scene2d.Actor> r17, com.badlogic.gdx.scenes.scene2d.ui.TextField r18, com.badlogic.gdx.math.Vector2 r19, com.badlogic.gdx.math.Vector2 r20, boolean r21) {
        /*
            r16 = this;
            r0 = r17
            r7 = r19
            r8 = r20
            r1 = 0
            int r9 = r0.size
            r11 = r18
            r10 = r1
        L_0x000c:
            if (r10 >= r9) goto L_0x00e6
            java.lang.Object r1 = r0.get(r10)
            r12 = r1
            com.badlogic.gdx.scenes.scene2d.Actor r12 = (com.badlogic.gdx.scenes.scene2d.Actor) r12
            boolean r1 = r12 instanceof com.badlogic.gdx.scenes.scene2d.ui.TextField
            if (r1 == 0) goto L_0x00c7
            r13 = r16
            if (r12 != r13) goto L_0x001f
            goto L_0x00e2
        L_0x001f:
            r1 = r12
            com.badlogic.gdx.scenes.scene2d.ui.TextField r1 = (com.badlogic.gdx.scenes.scene2d.ui.TextField) r1
            boolean r2 = r1.isDisabled()
            if (r2 != 0) goto L_0x00e2
            boolean r2 = r1.focusTraversal
            if (r2 == 0) goto L_0x00e2
            boolean r2 = r1.ancestorsVisible()
            if (r2 != 0) goto L_0x0034
            goto L_0x00e2
        L_0x0034:
            com.badlogic.gdx.scenes.scene2d.Group r2 = r12.getParent()
            com.badlogic.gdx.math.Vector2 r3 = tmp3
            float r4 = r12.getX()
            float r5 = r12.getY()
            com.badlogic.gdx.math.Vector2 r3 = r3.set(r4, r5)
            com.badlogic.gdx.math.Vector2 r2 = r2.localToStageCoordinates(r3)
            float r3 = r2.y
            float r4 = r8.y
            r6 = 1
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 == 0) goto L_0x0064
            float r3 = r2.y
            float r4 = r8.y
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 >= 0) goto L_0x005d
            r3 = 1
            goto L_0x005e
        L_0x005d:
            r3 = 0
        L_0x005e:
            r3 = r3 ^ r21
            if (r3 == 0) goto L_0x0064
            r3 = 1
            goto L_0x0065
        L_0x0064:
            r3 = 0
        L_0x0065:
            float r4 = r2.y
            float r14 = r8.y
            int r4 = (r4 > r14 ? 1 : (r4 == r14 ? 0 : -1))
            if (r4 != 0) goto L_0x007e
            float r4 = r2.x
            float r14 = r8.x
            int r4 = (r4 > r14 ? 1 : (r4 == r14 ? 0 : -1))
            if (r4 <= 0) goto L_0x0077
            r4 = 1
            goto L_0x0078
        L_0x0077:
            r4 = 0
        L_0x0078:
            r4 = r4 ^ r21
            if (r4 == 0) goto L_0x007e
            r4 = 1
            goto L_0x007f
        L_0x007e:
            r4 = 0
        L_0x007f:
            if (r3 != 0) goto L_0x0085
            if (r4 != 0) goto L_0x0085
            goto L_0x00e2
        L_0x0085:
            if (r11 == 0) goto L_0x00a1
            float r14 = r2.y
            float r15 = r7.y
            int r14 = (r14 > r15 ? 1 : (r14 == r15 ? 0 : -1))
            if (r14 == 0) goto L_0x009f
            float r14 = r2.y
            float r15 = r7.y
            int r14 = (r14 > r15 ? 1 : (r14 == r15 ? 0 : -1))
            if (r14 <= 0) goto L_0x0099
            r14 = 1
            goto L_0x009a
        L_0x0099:
            r14 = 0
        L_0x009a:
            r14 = r14 ^ r21
            if (r14 == 0) goto L_0x009f
            goto L_0x00a1
        L_0x009f:
            r14 = 0
            goto L_0x00a2
        L_0x00a1:
            r14 = 1
        L_0x00a2:
            if (r14 != 0) goto L_0x00be
            float r15 = r2.y
            float r5 = r7.y
            int r5 = (r15 > r5 ? 1 : (r15 == r5 ? 0 : -1))
            if (r5 != 0) goto L_0x00bc
            float r5 = r2.x
            float r15 = r7.x
            int r5 = (r5 > r15 ? 1 : (r5 == r15 ? 0 : -1))
            if (r5 >= 0) goto L_0x00b6
            r5 = 1
            goto L_0x00b7
        L_0x00b6:
            r5 = 0
        L_0x00b7:
            r5 = r5 ^ r21
            if (r5 == 0) goto L_0x00bc
            goto L_0x00bd
        L_0x00bc:
            r6 = 0
        L_0x00bd:
            r14 = r6
        L_0x00be:
            if (r14 == 0) goto L_0x00c6
            r11 = r12
            com.badlogic.gdx.scenes.scene2d.ui.TextField r11 = (com.badlogic.gdx.scenes.scene2d.ui.TextField) r11
            r7.set((com.badlogic.gdx.math.Vector2) r2)
        L_0x00c6:
            goto L_0x00e2
        L_0x00c7:
            r13 = r16
            boolean r1 = r12 instanceof com.badlogic.gdx.scenes.scene2d.Group
            if (r1 == 0) goto L_0x00c6
            r1 = r12
            com.badlogic.gdx.scenes.scene2d.Group r1 = (com.badlogic.gdx.scenes.scene2d.Group) r1
            com.badlogic.gdx.utils.SnapshotArray r2 = r1.getChildren()
            r1 = r16
            r3 = r11
            r4 = r19
            r5 = r20
            r6 = r21
            com.badlogic.gdx.scenes.scene2d.ui.TextField r1 = r1.findNextTextField(r2, r3, r4, r5, r6)
            r11 = r1
        L_0x00e2:
            int r10 = r10 + 1
            goto L_0x000c
        L_0x00e6:
            r13 = r16
            return r11
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.scenes.scene2d.ui.TextField.findNextTextField(com.badlogic.gdx.utils.Array, com.badlogic.gdx.scenes.scene2d.ui.TextField, com.badlogic.gdx.math.Vector2, com.badlogic.gdx.math.Vector2, boolean):com.badlogic.gdx.scenes.scene2d.ui.TextField");
    }

    public InputListener getDefaultInputListener() {
        return this.inputListener;
    }

    public void setTextFieldListener(TextFieldListener listener2) {
        this.listener = listener2;
    }

    public void setTextFieldFilter(TextFieldFilter filter2) {
        this.filter = filter2;
    }

    public TextFieldFilter getTextFieldFilter() {
        return this.filter;
    }

    public void setFocusTraversal(boolean focusTraversal2) {
        this.focusTraversal = focusTraversal2;
    }

    public String getMessageText() {
        return this.messageText;
    }

    public void setMessageText(String messageText2) {
        this.messageText = messageText2;
    }

    public void appendText(String str) {
        if (str == null) {
            str = BuildConfig.FLAVOR;
        }
        clearSelection();
        this.cursor = this.text.length();
        paste(str, this.programmaticChangeEvents);
    }

    public void setText(String str) {
        if (str == null) {
            str = BuildConfig.FLAVOR;
        }
        if (!str.equals(this.text)) {
            clearSelection();
            String oldText = this.text;
            this.text = BuildConfig.FLAVOR;
            paste(str, false);
            if (this.programmaticChangeEvents) {
                changeText(oldText, this.text);
            }
            this.cursor = 0;
        }
    }

    public String getText() {
        return this.text;
    }

    /* access modifiers changed from: package-private */
    public boolean changeText(String oldText, String newText) {
        if (newText.equals(oldText)) {
            return false;
        }
        this.text = newText;
        ChangeListener.ChangeEvent changeEvent = (ChangeListener.ChangeEvent) Pools.obtain(ChangeListener.ChangeEvent.class);
        boolean cancelled = fire(changeEvent);
        this.text = cancelled ? oldText : newText;
        Pools.free(changeEvent);
        return !cancelled;
    }

    public void setProgrammaticChangeEvents(boolean programmaticChangeEvents2) {
        this.programmaticChangeEvents = programmaticChangeEvents2;
    }

    public boolean getProgrammaticChangeEvents() {
        return this.programmaticChangeEvents;
    }

    public int getSelectionStart() {
        return this.selectionStart;
    }

    public String getSelection() {
        return this.hasSelection ? this.text.substring(Math.min(this.selectionStart, this.cursor), Math.max(this.selectionStart, this.cursor)) : BuildConfig.FLAVOR;
    }

    public void setSelection(int selectionStart2, int selectionEnd) {
        if (selectionStart2 < 0) {
            throw new IllegalArgumentException("selectionStart must be >= 0");
        } else if (selectionEnd >= 0) {
            int selectionStart3 = Math.min(this.text.length(), selectionStart2);
            int selectionEnd2 = Math.min(this.text.length(), selectionEnd);
            if (selectionEnd2 == selectionStart3) {
                clearSelection();
                return;
            }
            if (selectionEnd2 < selectionStart3) {
                int temp = selectionEnd2;
                selectionEnd2 = selectionStart3;
                selectionStart3 = temp;
            }
            this.hasSelection = true;
            this.selectionStart = selectionStart3;
            this.cursor = selectionEnd2;
        } else {
            throw new IllegalArgumentException("selectionEnd must be >= 0");
        }
    }

    public void selectAll() {
        setSelection(0, this.text.length());
    }

    public void clearSelection() {
        this.hasSelection = false;
    }

    public void setCursorPosition(int cursorPosition) {
        if (cursorPosition >= 0) {
            clearSelection();
            this.cursor = Math.min(cursorPosition, this.text.length());
            return;
        }
        throw new IllegalArgumentException("cursorPosition must be >= 0");
    }

    public int getCursorPosition() {
        return this.cursor;
    }

    public OnscreenKeyboard getOnscreenKeyboard() {
        return this.keyboard;
    }

    public void setOnscreenKeyboard(OnscreenKeyboard keyboard2) {
        this.keyboard = keyboard2;
    }

    public void setClipboard(Clipboard clipboard2) {
        this.clipboard = clipboard2;
    }

    public float getPrefWidth() {
        return 150.0f;
    }

    public float getPrefHeight() {
        float topAndBottom = 0.0f;
        float minHeight = 0.0f;
        if (this.style.background != null) {
            topAndBottom = Math.max(0.0f, this.style.background.getBottomHeight() + this.style.background.getTopHeight());
            minHeight = Math.max(0.0f, this.style.background.getMinHeight());
        }
        if (this.style.focusedBackground != null) {
            topAndBottom = Math.max(topAndBottom, this.style.focusedBackground.getBottomHeight() + this.style.focusedBackground.getTopHeight());
            minHeight = Math.max(minHeight, this.style.focusedBackground.getMinHeight());
        }
        if (this.style.disabledBackground != null) {
            topAndBottom = Math.max(topAndBottom, this.style.disabledBackground.getBottomHeight() + this.style.disabledBackground.getTopHeight());
            minHeight = Math.max(minHeight, this.style.disabledBackground.getMinHeight());
        }
        return Math.max(this.textHeight + topAndBottom, minHeight);
    }

    public void setAlignment(int alignment) {
        this.textHAlign = alignment;
    }

    public int getAlignment() {
        return this.textHAlign;
    }

    public void setPasswordMode(boolean passwordMode2) {
        this.passwordMode = passwordMode2;
        updateDisplayText();
    }

    public boolean isPasswordMode() {
        return this.passwordMode;
    }

    public void setPasswordCharacter(char passwordCharacter2) {
        this.passwordCharacter = passwordCharacter2;
        if (this.passwordMode) {
            updateDisplayText();
        }
    }

    public void setBlinkTime(float blinkTime2) {
        this.blinkTime = blinkTime2;
    }

    public void setDisabled(boolean disabled2) {
        this.disabled = disabled2;
    }

    public boolean isDisabled() {
        return this.disabled;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:7:0x0011  */
    /* JADX WARNING: Removed duplicated region for block: B:9:0x001a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void moveCursor(boolean r4, boolean r5) {
        /*
            r3 = this;
            r0 = 0
            if (r4 == 0) goto L_0x000a
            java.lang.String r1 = r3.text
            int r1 = r1.length()
            goto L_0x000b
        L_0x000a:
            r1 = 0
        L_0x000b:
            if (r4 == 0) goto L_0x000e
            goto L_0x000f
        L_0x000e:
            r0 = -1
        L_0x000f:
            if (r4 == 0) goto L_0x001a
            int r2 = r3.cursor
            int r2 = r2 + 1
            r3.cursor = r2
            if (r2 >= r1) goto L_0x002c
            goto L_0x0022
        L_0x001a:
            int r2 = r3.cursor
            int r2 = r2 + -1
            r3.cursor = r2
            if (r2 <= r1) goto L_0x002c
        L_0x0022:
            if (r5 == 0) goto L_0x002c
            int r2 = r3.cursor
            boolean r2 = r3.continueCursor(r2, r0)
            if (r2 != 0) goto L_0x000f
        L_0x002c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.scenes.scene2d.ui.TextField.moveCursor(boolean, boolean):void");
    }

    /* access modifiers changed from: protected */
    public boolean continueCursor(int index, int offset) {
        return isWordCharacter(this.text.charAt(index + offset));
    }

    class KeyRepeatTask extends Timer.Task {
        int keycode;

        KeyRepeatTask() {
        }

        public void run() {
            TextField.this.inputListener.keyDown((InputEvent) null, this.keycode);
        }
    }

    public interface TextFieldFilter {
        boolean acceptChar(TextField textField, char c);

        public static class DigitsOnlyFilter implements TextFieldFilter {
            public boolean acceptChar(TextField textField, char c) {
                return Character.isDigit(c);
            }
        }
    }

    public static class DefaultOnscreenKeyboard implements OnscreenKeyboard {
        public void show(boolean visible) {
            Gdx.input.setOnscreenKeyboardVisible(visible);
        }
    }

    public class TextFieldClickListener extends ClickListener {
        public TextFieldClickListener() {
        }

        public void clicked(InputEvent event, float x, float y) {
            int count = getTapCount() % 4;
            if (count == 0) {
                TextField.this.clearSelection();
            }
            if (count == 2) {
                int[] array = TextField.this.wordUnderCursor(x);
                TextField.this.setSelection(array[0], array[1]);
            }
            if (count == 3) {
                TextField.this.selectAll();
            }
        }

        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            if (!super.touchDown(event, x, y, pointer, button)) {
                return false;
            }
            if (pointer == 0 && button != 0) {
                return false;
            }
            if (TextField.this.disabled) {
                return true;
            }
            setCursorPosition(x, y);
            TextField textField = TextField.this;
            textField.selectionStart = textField.cursor;
            Stage stage = TextField.this.getStage();
            if (stage != null) {
                stage.setKeyboardFocus(TextField.this);
            }
            TextField.this.keyboard.show(true);
            TextField.this.hasSelection = true;
            return true;
        }

        public void touchDragged(InputEvent event, float x, float y, int pointer) {
            super.touchDragged(event, x, y, pointer);
            setCursorPosition(x, y);
        }

        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            if (TextField.this.selectionStart == TextField.this.cursor) {
                TextField.this.hasSelection = false;
            }
            super.touchUp(event, x, y, pointer, button);
        }

        /* access modifiers changed from: protected */
        public void setCursorPosition(float x, float y) {
            TextField textField = TextField.this;
            textField.cursor = textField.letterUnderCursor(x);
            TextField textField2 = TextField.this;
            textField2.cursorOn = textField2.focused;
            TextField.this.blinkTask.cancel();
            if (TextField.this.focused) {
                Timer.schedule(TextField.this.blinkTask, TextField.this.blinkTime, TextField.this.blinkTime);
            }
        }

        /* access modifiers changed from: protected */
        public void goHome(boolean jump) {
            TextField.this.cursor = 0;
        }

        /* access modifiers changed from: protected */
        public void goEnd(boolean jump) {
            TextField textField = TextField.this;
            textField.cursor = textField.text.length();
        }

        public boolean keyDown(InputEvent event, int keycode) {
            boolean handled;
            if (TextField.this.disabled) {
                return false;
            }
            TextField textField = TextField.this;
            textField.cursorOn = textField.focused;
            TextField.this.blinkTask.cancel();
            if (TextField.this.focused) {
                Timer.schedule(TextField.this.blinkTask, TextField.this.blinkTime, TextField.this.blinkTime);
            }
            if (!TextField.this.hasKeyboardFocus()) {
                return false;
            }
            boolean repeat = false;
            boolean ctrl = UIUtils.ctrl();
            boolean jump = ctrl && !TextField.this.passwordMode;
            boolean handled2 = true;
            if (ctrl) {
                if (keycode != 29) {
                    if (keycode != 31) {
                        if (keycode == 50) {
                            TextField textField2 = TextField.this;
                            textField2.paste(textField2.clipboard.getContents(), true);
                            repeat = true;
                        } else if (keycode == 52) {
                            TextField.this.cut(true);
                            return true;
                        } else if (keycode == 54) {
                            String oldText = TextField.this.text;
                            TextField textField3 = TextField.this;
                            textField3.setText(textField3.undoText);
                            TextField textField4 = TextField.this;
                            textField4.undoText = oldText;
                            textField4.updateDisplayText();
                            return true;
                        } else if (keycode != 133) {
                            handled2 = false;
                        }
                    }
                    TextField.this.copy();
                    return true;
                }
                TextField.this.selectAll();
                return true;
            }
            if (UIUtils.shift()) {
                if (keycode == 112) {
                    TextField.this.cut(true);
                } else if (keycode == 133) {
                    TextField textField5 = TextField.this;
                    textField5.paste(textField5.clipboard.getContents(), true);
                }
                int temp = TextField.this.cursor;
                if (keycode == 3) {
                    goHome(jump);
                    handled = true;
                } else if (keycode == 132) {
                    goEnd(jump);
                    handled = true;
                } else if (keycode == 21) {
                    TextField.this.moveCursor(false, jump);
                    repeat = true;
                    handled = true;
                } else if (keycode == 22) {
                    TextField.this.moveCursor(true, jump);
                    repeat = true;
                    handled = true;
                }
                if (!TextField.this.hasSelection) {
                    TextField textField6 = TextField.this;
                    textField6.selectionStart = temp;
                    textField6.hasSelection = true;
                }
            } else if (keycode == 3) {
                goHome(jump);
                TextField.this.clearSelection();
                handled = true;
            } else if (keycode == 132) {
                goEnd(jump);
                TextField.this.clearSelection();
                handled = true;
            } else if (keycode == 21) {
                TextField.this.moveCursor(false, jump);
                TextField.this.clearSelection();
                repeat = true;
                handled = true;
            } else if (keycode == 22) {
                TextField.this.moveCursor(true, jump);
                TextField.this.clearSelection();
                repeat = true;
                handled = true;
            }
            TextField textField7 = TextField.this;
            textField7.cursor = MathUtils.clamp(textField7.cursor, 0, TextField.this.text.length());
            if (repeat) {
                scheduleKeyRepeatTask(keycode);
            }
            return handled;
        }

        /* access modifiers changed from: protected */
        public void scheduleKeyRepeatTask(int keycode) {
            if (!TextField.this.keyRepeatTask.isScheduled() || TextField.this.keyRepeatTask.keycode != keycode) {
                TextField.this.keyRepeatTask.keycode = keycode;
                TextField.this.keyRepeatTask.cancel();
                Timer.schedule(TextField.this.keyRepeatTask, TextField.keyRepeatInitialTime, TextField.keyRepeatTime);
            }
        }

        public boolean keyUp(InputEvent event, int keycode) {
            if (TextField.this.disabled) {
                return false;
            }
            TextField.this.keyRepeatTask.cancel();
            return true;
        }

        public boolean keyTyped(InputEvent event, char character) {
            char c = character;
            if (TextField.this.disabled) {
                return false;
            }
            if (c != 13) {
                switch (c) {
                    case 8:
                    case 9:
                    case 10:
                        break;
                    default:
                        if (c < ' ') {
                            return false;
                        }
                        break;
                }
            }
            if (!TextField.this.hasKeyboardFocus()) {
                return false;
            }
            if (UIUtils.isMac && Gdx.input.isKeyPressed(63)) {
                return true;
            }
            if ((c == 9 || c == 10) && TextField.this.focusTraversal) {
                TextField.this.next(UIUtils.shift());
            } else {
                boolean delete = c == 127;
                boolean backspace = c == 8;
                boolean enter = c == 13 || c == 10;
                boolean add = enter ? TextField.this.writeEnters : !TextField.this.onlyFontChars || TextField.this.style.font.getData().hasGlyph(c);
                boolean remove = backspace || delete;
                if (add || remove) {
                    String oldText = TextField.this.text;
                    int oldCursor = TextField.this.cursor;
                    if (remove) {
                        if (TextField.this.hasSelection) {
                            TextField textField = TextField.this;
                            textField.cursor = textField.delete(false);
                        } else {
                            if (backspace && TextField.this.cursor > 0) {
                                TextField textField2 = TextField.this;
                                StringBuilder sb = new StringBuilder();
                                sb.append(TextField.this.text.substring(0, TextField.this.cursor - 1));
                                String str = TextField.this.text;
                                TextField textField3 = TextField.this;
                                int i = textField3.cursor;
                                textField3.cursor = i - 1;
                                sb.append(str.substring(i));
                                textField2.text = sb.toString();
                                TextField.this.renderOffset = 0.0f;
                            }
                            if (delete && TextField.this.cursor < TextField.this.text.length()) {
                                TextField textField4 = TextField.this;
                                textField4.text = TextField.this.text.substring(0, TextField.this.cursor) + TextField.this.text.substring(TextField.this.cursor + 1);
                            }
                        }
                    }
                    if (add && !remove) {
                        if (!enter && TextField.this.filter != null && !TextField.this.filter.acceptChar(TextField.this, c)) {
                            return true;
                        }
                        TextField textField5 = TextField.this;
                        if (!textField5.withinMaxLength(textField5.text.length())) {
                            return true;
                        }
                        if (TextField.this.hasSelection) {
                            TextField textField6 = TextField.this;
                            textField6.cursor = textField6.delete(false);
                        }
                        String insertion = enter ? "\n" : String.valueOf(character);
                        TextField textField7 = TextField.this;
                        int i2 = textField7.cursor;
                        textField7.cursor = i2 + 1;
                        textField7.text = textField7.insert(i2, insertion, TextField.this.text);
                    }
                    String tempUndoText = TextField.this.undoText;
                    TextField textField8 = TextField.this;
                    if (textField8.changeText(oldText, textField8.text)) {
                        long time = System.currentTimeMillis();
                        boolean z = enter;
                        String str2 = tempUndoText;
                        if (time - 750 > TextField.this.lastChangeTime) {
                            TextField.this.undoText = oldText;
                        }
                        TextField.this.lastChangeTime = time;
                    } else {
                        String str3 = tempUndoText;
                        TextField.this.cursor = oldCursor;
                    }
                    TextField.this.updateDisplayText();
                }
            }
            if (TextField.this.listener == null) {
                return true;
            }
            TextField.this.listener.keyTyped(TextField.this, c);
            return true;
        }
    }

    public static class TextFieldStyle {
        public Drawable background;
        public Drawable cursor;
        public Drawable disabledBackground;
        public Color disabledFontColor;
        public Drawable focusedBackground;
        public Color focusedFontColor;
        public BitmapFont font;
        public Color fontColor;
        public BitmapFont messageFont;
        public Color messageFontColor;
        public Drawable selection;

        public TextFieldStyle() {
        }

        public TextFieldStyle(BitmapFont font2, Color fontColor2, Drawable cursor2, Drawable selection2, Drawable background2) {
            this.background = background2;
            this.cursor = cursor2;
            this.font = font2;
            this.fontColor = fontColor2;
            this.selection = selection2;
        }

        public TextFieldStyle(TextFieldStyle style) {
            this.messageFont = style.messageFont;
            Color color = style.messageFontColor;
            if (color != null) {
                this.messageFontColor = new Color(color);
            }
            this.background = style.background;
            this.focusedBackground = style.focusedBackground;
            this.disabledBackground = style.disabledBackground;
            this.cursor = style.cursor;
            this.font = style.font;
            Color color2 = style.fontColor;
            if (color2 != null) {
                this.fontColor = new Color(color2);
            }
            Color color3 = style.focusedFontColor;
            if (color3 != null) {
                this.focusedFontColor = new Color(color3);
            }
            Color color4 = style.disabledFontColor;
            if (color4 != null) {
                this.disabledFontColor = new Color(color4);
            }
            this.selection = style.selection;
        }
    }
}
