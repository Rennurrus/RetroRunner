package com.twi.game.Managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class Button {
    private TextButton button;
    private BitmapFont font = new BitmapFont(Gdx.files.internal("GameScreen/Text/2.fnt"));
    private Skin skin = new Skin(Gdx.files.internal("skin/neon-ui.json"));

    public Button(String text_on_button, float x, float y) {
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = this.font;
        textButtonStyle.fontColor = Color.WHITE;
        textButtonStyle.downFontColor = Color.PINK;
        textButtonStyle.up = this.skin.getDrawable("button-over-c");
        textButtonStyle.down = this.skin.getDrawable("button-pressed-c");
        this.button = new TextButton(text_on_button, textButtonStyle);
        this.button.setTouchable(Touchable.enabled);
        TextButton textButton = this.button;
        textButton.setBounds(0.0f, 0.0f, textButton.getWidth() + 20.0f, this.button.getHeight());
        TextButton textButton2 = this.button;
        textButton2.setPosition(x - (textButton2.getWidth() / 2.0f), y - (this.button.getHeight() / 2.0f));
    }

    public void setBounds(float x, float y, float width, float heigth) {
        this.button.setBounds(x, y, width, heigth);
    }

    public void setPosition(float x, float y) {
        this.button.setPosition(x, y);
    }

    public TextButton getTextButtonClass() {
        return this.button;
    }

    public void render(SpriteBatch SpB) {
        this.button.draw(SpB, 1.0f);
    }

    public void dispose() {
        this.font.dispose();
        this.skin.dispose();
    }
}
