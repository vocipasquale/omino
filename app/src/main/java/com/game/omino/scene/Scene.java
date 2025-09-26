package com.game.omino.scene;

public interface Scene {
    void onEnter();
    void onExit();
    void update(float deltaTime);
    void render();
}


