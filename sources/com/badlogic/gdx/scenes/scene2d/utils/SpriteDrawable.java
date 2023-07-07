package com.badlogic.gdx.scenes.scene2d.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class SpriteDrawable extends BaseDrawable implements TransformDrawable {
    private static final Color temp = new Color();
    private Sprite sprite;

    public SpriteDrawable() {
    }

    public SpriteDrawable(Sprite sprite2) {
        setSprite(sprite2);
    }

    public SpriteDrawable(SpriteDrawable drawable) {
        super(drawable);
        setSprite(drawable.sprite);
    }

    public void draw(Batch batch, float x, float y, float width, float height) {
        Color spriteColor = this.sprite.getColor();
        temp.set(spriteColor);
        this.sprite.setColor(spriteColor.mul(batch.getColor()));
        this.sprite.setRotation(0.0f);
        this.sprite.setScale(1.0f, 1.0f);
        this.sprite.setBounds(x, y, width, height);
        this.sprite.draw(batch);
        this.sprite.setColor(temp);
    }

    public void draw(Batch batch, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation) {
        Color spriteColor = this.sprite.getColor();
        temp.set(spriteColor);
        this.sprite.setColor(spriteColor.mul(batch.getColor()));
        this.sprite.setOrigin(originX, originY);
        this.sprite.setRotation(rotation);
        this.sprite.setScale(scaleX, scaleY);
        this.sprite.setBounds(x, y, width, height);
        this.sprite.draw(batch);
        this.sprite.setColor(temp);
    }

    public void setSprite(Sprite sprite2) {
        this.sprite = sprite2;
        setMinWidth(sprite2.getWidth());
        setMinHeight(sprite2.getHeight());
    }

    public Sprite getSprite() {
        return this.sprite;
    }

    public SpriteDrawable tint(Color tint) {
        Sprite newSprite;
        Sprite sprite2 = this.sprite;
        if (sprite2 instanceof TextureAtlas.AtlasSprite) {
            newSprite = new TextureAtlas.AtlasSprite((TextureAtlas.AtlasSprite) sprite2);
        } else {
            newSprite = new Sprite(sprite2);
        }
        newSprite.setColor(tint);
        newSprite.setSize(getMinWidth(), getMinHeight());
        SpriteDrawable drawable = new SpriteDrawable(newSprite);
        drawable.setLeftWidth(getLeftWidth());
        drawable.setRightWidth(getRightWidth());
        drawable.setTopHeight(getTopHeight());
        drawable.setBottomHeight(getBottomHeight());
        return drawable;
    }
}
