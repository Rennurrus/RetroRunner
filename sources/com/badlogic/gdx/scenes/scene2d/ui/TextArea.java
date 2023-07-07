package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;

public class TextArea extends TextField {
    int cursorLine;
    int firstLineShowing;
    private String lastText;
    IntArray linesBreak;
    private int linesShowing;
    float moveOffset;
    private float prefRows;

    public TextArea(String text, Skin skin) {
        super(text, skin);
    }

    public TextArea(String text, Skin skin, String styleName) {
        super(text, skin, styleName);
    }

    public TextArea(String text, TextField.TextFieldStyle style) {
        super(text, style);
    }

    /* access modifiers changed from: protected */
    public void initialize() {
        super.initialize();
        this.writeEnters = true;
        this.linesBreak = new IntArray();
        this.cursorLine = 0;
        this.firstLineShowing = 0;
        this.moveOffset = -1.0f;
        this.linesShowing = 0;
    }

    /* access modifiers changed from: protected */
    public int letterUnderCursor(float x) {
        if (this.linesBreak.size <= 0) {
            return 0;
        }
        if (this.cursorLine * 2 >= this.linesBreak.size) {
            return this.text.length();
        }
        float[] glyphPositions = this.glyphPositions.items;
        int start = this.linesBreak.items[this.cursorLine * 2];
        float x2 = x + glyphPositions[start];
        int end = this.linesBreak.items[(this.cursorLine * 2) + 1];
        int i = start;
        while (i < end && glyphPositions[i] <= x2) {
            i++;
        }
        if (i <= 0 || glyphPositions[i] - x2 > x2 - glyphPositions[i - 1]) {
            return Math.max(0, i - 1);
        }
        return i;
    }

    public void setPrefRows(float prefRows2) {
        this.prefRows = prefRows2;
    }

    public float getPrefHeight() {
        if (this.prefRows <= 0.0f) {
            return super.getPrefHeight();
        }
        float prefHeight = this.textHeight * this.prefRows;
        if (this.style.background != null) {
            return Math.max(this.style.background.getBottomHeight() + prefHeight + this.style.background.getTopHeight(), this.style.background.getMinHeight());
        }
        return prefHeight;
    }

    public int getLines() {
        return (this.linesBreak.size / 2) + (newLineAtEnd() ? 1 : 0);
    }

    public boolean newLineAtEnd() {
        if (this.text.length() == 0 || (this.text.charAt(this.text.length() - 1) != 10 && this.text.charAt(this.text.length() - 1) != 13)) {
            return false;
        }
        return true;
    }

    public void moveCursorLine(int line) {
        if (line < 0) {
            this.cursorLine = 0;
            this.cursor = 0;
            this.moveOffset = -1.0f;
        } else if (line >= getLines()) {
            int newLine = getLines() - 1;
            this.cursor = this.text.length();
            if (line > getLines() || newLine == this.cursorLine) {
                this.moveOffset = -1.0f;
            }
            this.cursorLine = newLine;
        } else if (line != this.cursorLine) {
            float f = 0.0f;
            if (this.moveOffset < 0.0f) {
                if (this.linesBreak.size > this.cursorLine * 2) {
                    f = this.glyphPositions.get(this.cursor) - this.glyphPositions.get(this.linesBreak.get(this.cursorLine * 2));
                }
                this.moveOffset = f;
            }
            this.cursorLine = line;
            this.cursor = this.cursorLine * 2 >= this.linesBreak.size ? this.text.length() : this.linesBreak.get(this.cursorLine * 2);
            while (this.cursor < this.text.length() && this.cursor <= this.linesBreak.get((this.cursorLine * 2) + 1) - 1 && this.glyphPositions.get(this.cursor) - this.glyphPositions.get(this.linesBreak.get(this.cursorLine * 2)) < this.moveOffset) {
                this.cursor++;
            }
            showCursor();
        }
    }

    /* access modifiers changed from: package-private */
    public void updateCurrentLine() {
        int index = calculateCurrentLineIndex(this.cursor);
        int line = index / 2;
        if ((index % 2 == 0 || index + 1 >= this.linesBreak.size || this.cursor != this.linesBreak.items[index] || this.linesBreak.items[index + 1] != this.linesBreak.items[index]) && (line < this.linesBreak.size / 2 || this.text.length() == 0 || this.text.charAt(this.text.length() - 1) == 10 || this.text.charAt(this.text.length() - 1) == 13)) {
            this.cursorLine = line;
        }
        updateFirstLineShowing();
    }

    /* access modifiers changed from: package-private */
    public void showCursor() {
        updateCurrentLine();
        updateFirstLineShowing();
    }

    /* access modifiers changed from: package-private */
    public void updateFirstLineShowing() {
        int i = this.cursorLine;
        int i2 = this.firstLineShowing;
        if (i != i2) {
            int step = i >= i2 ? 1 : -1;
            while (true) {
                int i3 = this.firstLineShowing;
                int i4 = this.cursorLine;
                if (i3 > i4 || (i3 + this.linesShowing) - 1 < i4) {
                    this.firstLineShowing += step;
                } else {
                    return;
                }
            }
        }
    }

    private int calculateCurrentLineIndex(int cursor) {
        int index = 0;
        while (index < this.linesBreak.size && cursor > this.linesBreak.items[index]) {
            index++;
        }
        return index;
    }

    /* access modifiers changed from: protected */
    public void sizeChanged() {
        this.lastText = null;
        BitmapFont font = this.style.font;
        Drawable background = this.style.background;
        this.linesShowing = (int) Math.floor((double) ((getHeight() - (background == null ? 0.0f : background.getBottomHeight() + background.getTopHeight())) / font.getLineHeight()));
    }

    /* access modifiers changed from: protected */
    public float getTextY(BitmapFont font, Drawable background) {
        float textY = getHeight();
        if (background != null) {
            return (float) ((int) (textY - background.getTopHeight()));
        }
        return textY;
    }

    /* access modifiers changed from: protected */
    public void drawSelection(Drawable selection, Batch batch, BitmapFont font, float x, float y) {
        int i = this.firstLineShowing * 2;
        float offsetY = 0.0f;
        int minIndex = Math.min(this.cursor, this.selectionStart);
        int maxIndex = Math.max(this.cursor, this.selectionStart);
        while (i + 1 < this.linesBreak.size && i < (this.firstLineShowing + this.linesShowing) * 2) {
            int lineStart = this.linesBreak.get(i);
            int lineEnd = this.linesBreak.get(i + 1);
            if ((minIndex >= lineStart || minIndex >= lineEnd || maxIndex >= lineStart || maxIndex >= lineEnd) && (minIndex <= lineStart || minIndex <= lineEnd || maxIndex <= lineStart || maxIndex <= lineEnd)) {
                int start = Math.max(this.linesBreak.get(i), minIndex);
                int end = Math.min(this.linesBreak.get(i + 1), maxIndex);
                float selectionX = this.glyphPositions.get(start) - this.glyphPositions.get(this.linesBreak.get(i));
                selection.draw(batch, x + selectionX + this.fontOffset, ((y - this.textHeight) - font.getDescent()) - offsetY, this.glyphPositions.get(end) - this.glyphPositions.get(start), font.getLineHeight());
            }
            offsetY += font.getLineHeight();
            i += 2;
        }
    }

    /* access modifiers changed from: protected */
    public void drawText(Batch batch, BitmapFont font, float x, float y) {
        float offsetY = 0.0f;
        int i = this.firstLineShowing * 2;
        while (i < (this.firstLineShowing + this.linesShowing) * 2 && i < this.linesBreak.size) {
            BitmapFont bitmapFont = font;
            Batch batch2 = batch;
            float f = x;
            bitmapFont.draw(batch2, this.displayText, f, y + offsetY, this.linesBreak.items[i], this.linesBreak.items[i + 1], 0.0f, 8, false);
            offsetY -= font.getLineHeight();
            i += 2;
        }
    }

    /* access modifiers changed from: protected */
    public void drawCursor(Drawable cursorPatch, Batch batch, BitmapFont font, float x, float y) {
        float textOffset;
        if (this.cursor >= this.glyphPositions.size || this.cursorLine * 2 >= this.linesBreak.size) {
            textOffset = 0.0f;
        } else {
            textOffset = this.glyphPositions.get(this.cursor) - this.glyphPositions.get(this.linesBreak.items[this.cursorLine * 2]);
        }
        cursorPatch.draw(batch, x + textOffset + this.fontOffset + font.getData().cursorX, (y - (font.getDescent() / 2.0f)) - (((float) ((this.cursorLine - this.firstLineShowing) + 1)) * font.getLineHeight()), cursorPatch.getMinWidth(), font.getLineHeight());
    }

    /* access modifiers changed from: protected */
    public void calculateOffsets() {
        super.calculateOffsets();
        if (!this.text.equals(this.lastText)) {
            this.lastText = this.text;
            BitmapFont font = this.style.font;
            float maxWidthLine = getWidth() - (this.style.background != null ? this.style.background.getLeftWidth() + this.style.background.getRightWidth() : 0.0f);
            this.linesBreak.clear();
            int lineStart = 0;
            int lastSpace = 0;
            Pool<GlyphLayout> layoutPool = Pools.get(GlyphLayout.class);
            GlyphLayout layout = layoutPool.obtain();
            for (int i = 0; i < this.text.length(); i++) {
                char lastCharacter = this.text.charAt(i);
                if (lastCharacter == 13 || lastCharacter == 10) {
                    this.linesBreak.add(lineStart);
                    this.linesBreak.add(i);
                    lineStart = i + 1;
                } else {
                    lastSpace = continueCursor(i, 0) ? lastSpace : i;
                    layout.setText(font, this.text.subSequence(lineStart, i + 1));
                    if (layout.width > maxWidthLine) {
                        if (lineStart >= lastSpace) {
                            lastSpace = i - 1;
                        }
                        this.linesBreak.add(lineStart);
                        this.linesBreak.add(lastSpace + 1);
                        lineStart = lastSpace + 1;
                        lastSpace = lineStart;
                    }
                }
            }
            layoutPool.free(layout);
            if (lineStart < this.text.length()) {
                this.linesBreak.add(lineStart);
                this.linesBreak.add(this.text.length());
            }
            showCursor();
        }
    }

    /* access modifiers changed from: protected */
    public InputListener createInputListener() {
        return new TextAreaListener();
    }

    public void setSelection(int selectionStart, int selectionEnd) {
        super.setSelection(selectionStart, selectionEnd);
        updateCurrentLine();
    }

    /* access modifiers changed from: protected */
    public void moveCursor(boolean forward, boolean jump) {
        int count = forward ? 1 : -1;
        int index = (this.cursorLine * 2) + count;
        if (index < 0 || index + 1 >= this.linesBreak.size || this.linesBreak.items[index] != this.cursor || this.linesBreak.items[index + 1] != this.cursor) {
            super.moveCursor(forward, jump);
        } else {
            this.cursorLine += count;
            if (jump) {
                super.moveCursor(forward, jump);
            }
            showCursor();
        }
        updateCurrentLine();
    }

    /* access modifiers changed from: protected */
    public boolean continueCursor(int index, int offset) {
        int pos = calculateCurrentLineIndex(index + offset);
        return super.continueCursor(index, offset) && (pos < 0 || pos >= this.linesBreak.size + -2 || this.linesBreak.items[pos + 1] != index || this.linesBreak.items[pos + 1] == this.linesBreak.items[pos + 2]);
    }

    public int getCursorLine() {
        return this.cursorLine;
    }

    public int getFirstLineShowing() {
        return this.firstLineShowing;
    }

    public int getLinesShowing() {
        return this.linesShowing;
    }

    public float getCursorX() {
        return this.textOffset + this.fontOffset + this.style.font.getData().cursorX;
    }

    public float getCursorY() {
        BitmapFont font = this.style.font;
        return -(((-font.getDescent()) / 2.0f) - (((float) ((this.cursorLine - this.firstLineShowing) + 1)) * font.getLineHeight()));
    }

    public class TextAreaListener extends TextField.TextFieldClickListener {
        public TextAreaListener() {
            super();
        }

        /* access modifiers changed from: protected */
        public void setCursorPosition(float x, float y) {
            TextArea textArea = TextArea.this;
            textArea.moveOffset = -1.0f;
            Drawable background = textArea.style.background;
            BitmapFont font = TextArea.this.style.font;
            float height = TextArea.this.getHeight();
            if (background != null) {
                height -= background.getTopHeight();
                x -= background.getLeftWidth();
            }
            float x2 = Math.max(0.0f, x);
            if (background != null) {
                y -= background.getTopHeight();
            }
            TextArea.this.cursorLine = ((int) Math.floor((double) ((height - y) / font.getLineHeight()))) + TextArea.this.firstLineShowing;
            TextArea textArea2 = TextArea.this;
            textArea2.cursorLine = Math.max(0, Math.min(textArea2.cursorLine, TextArea.this.getLines() - 1));
            super.setCursorPosition(x2, y);
            TextArea.this.updateCurrentLine();
        }

        public boolean keyDown(InputEvent event, int keycode) {
            boolean result = super.keyDown(event, keycode);
            if (!TextArea.this.hasKeyboardFocus()) {
                return result;
            }
            boolean repeat = false;
            boolean shift = Gdx.input.isKeyPressed(59) || Gdx.input.isKeyPressed(60);
            if (keycode == 20) {
                if (!shift) {
                    TextArea.this.clearSelection();
                } else if (!TextArea.this.hasSelection) {
                    TextArea textArea = TextArea.this;
                    textArea.selectionStart = textArea.cursor;
                    TextArea.this.hasSelection = true;
                }
                TextArea textArea2 = TextArea.this;
                textArea2.moveCursorLine(textArea2.cursorLine + 1);
                repeat = true;
            } else if (keycode == 19) {
                if (!shift) {
                    TextArea.this.clearSelection();
                } else if (!TextArea.this.hasSelection) {
                    TextArea textArea3 = TextArea.this;
                    textArea3.selectionStart = textArea3.cursor;
                    TextArea.this.hasSelection = true;
                }
                TextArea textArea4 = TextArea.this;
                textArea4.moveCursorLine(textArea4.cursorLine - 1);
                repeat = true;
            } else {
                TextArea.this.moveOffset = -1.0f;
            }
            if (repeat) {
                scheduleKeyRepeatTask(keycode);
            }
            TextArea.this.showCursor();
            return true;
        }

        public boolean keyTyped(InputEvent event, char character) {
            boolean result = super.keyTyped(event, character);
            TextArea.this.showCursor();
            return result;
        }

        /* access modifiers changed from: protected */
        public void goHome(boolean jump) {
            if (jump) {
                TextArea.this.cursor = 0;
            } else if (TextArea.this.cursorLine * 2 < TextArea.this.linesBreak.size) {
                TextArea textArea = TextArea.this;
                textArea.cursor = textArea.linesBreak.get(TextArea.this.cursorLine * 2);
            }
        }

        /* access modifiers changed from: protected */
        public void goEnd(boolean jump) {
            if (jump || TextArea.this.cursorLine >= TextArea.this.getLines()) {
                TextArea textArea = TextArea.this;
                textArea.cursor = textArea.text.length();
            } else if ((TextArea.this.cursorLine * 2) + 1 < TextArea.this.linesBreak.size) {
                TextArea textArea2 = TextArea.this;
                textArea2.cursor = textArea2.linesBreak.get((TextArea.this.cursorLine * 2) + 1);
            }
        }
    }
}
