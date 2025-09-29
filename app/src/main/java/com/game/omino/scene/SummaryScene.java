package com.game.omino.scene;

import android.util.Log;

public class SummaryScene implements Scene {
    private final boolean success;

    public SummaryScene(boolean success) {
        this.success = success;
    }

    @Override
    public void onEnter() {
        Log.d("SummaryScene", success ? "Livello completato!" : "Game Over!");
    }

    @Override
    public void onExit() {
        // cleanup se necessario
    }

    @Override
    public void update(float deltaTime) {
        // esempio: dopo un input o un timer torniamo a un nuovo livello
        // SceneManager.setScene(new PlayScene());
    }

    @Override
    public void render() {
        // per ora solo debug, in futuro disegno con native
    }
}
