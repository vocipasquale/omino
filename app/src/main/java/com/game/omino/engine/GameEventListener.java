package com.game.omino.engine;

public interface GameEventListener {
    void onGameOver();
    void onLifeLost();
    void onLevelPassed();

    void onGameFinished();

}
