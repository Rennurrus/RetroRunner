package com.twi.game.Managers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface ManagerIntergace {
    void dispose();

    void render(SpriteBatch spriteBatch);

    void update();
}
