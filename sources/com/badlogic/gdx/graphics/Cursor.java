package com.badlogic.gdx.graphics;

import com.badlogic.gdx.utils.Disposable;

public interface Cursor extends Disposable {

    public enum SystemCursor {
        Arrow,
        Ibeam,
        Crosshair,
        Hand,
        HorizontalResize,
        VerticalResize
    }
}
