package com.twi.game.Managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.twi.game.states.PlayState;

public class TextManager implements ManagerIntergace {
    static BitmapFont font;

    public TextManager() {
        font = new BitmapFont(Gdx.files.internal("GameScreen/Text/1.fnt"));
        font.setColor(Color.YELLOW);
        font.getData().setScale(0.53333336f);
    }

    public void update() {
    }

    public void render(SpriteBatch SpB) {
        GlyphLayout glyphLayout = new GlyphLayout();
        GlyphLayout glyphLayout2 = new GlyphLayout();
        BitmapFont bitmapFont = font;
        glyphLayout.setText(bitmapFont, "Score: " + PlayState.score);
        BitmapFont bitmapFont2 = font;
        glyphLayout2.setText(bitmapFont2, "High Score: " + PlayState.highScore);
        font.draw((Batch) SpB, glyphLayout, 400.0f - (glyphLayout.width / 2.0f), 432.0f);
        font.draw((Batch) SpB, glyphLayout2, 400.0f - (glyphLayout2.width / 2.0f), 456.0f);
    }

    public void dispose() {
        font.dispose();
    }
}
