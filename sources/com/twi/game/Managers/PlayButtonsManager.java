package com.twi.game.Managers;

import com.twi.game.states.State;

public class PlayButtonsManager {

    /* renamed from: continгeButton  reason: contains not printable characters */
    private Button f0contineButton = new Button("Continue", 400.0f, 145.0f);
    private boolean isContinueOn = false;
    private boolean isMenuOn = false;
    private boolean isNextTrackOn = false;
    private boolean isPauseOn = false;
    private Button menuBatton = new Button("Menu", 266.0f, 160.0f);
    private Button nextTrackButton = new Button("Next track", 400.0f, 68.0f);
    private Button pauseButton = new Button("Pause", 0.0f, 0.0f);

    public PlayButtonsManager(State state) {
    }
}
