package com.twi.game;

import android.os.Bundle;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class AndroidLauncher extends AndroidApplication {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useGyroscope = false;
        config.hideStatusBar = false;
        config.useCompass = false;
        config.useAccelerometer = false;
        config.useImmersiveMode = true;
        initialize(new MainGame(), config);
    }
}
