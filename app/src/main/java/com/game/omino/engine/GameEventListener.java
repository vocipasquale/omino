package com.game.omino.engine;

public interface GameEventListener {
    void onGameOver();
    void onLiveUpdate(String s);
    void onLevelPassed();
    void onGameFinished();
    void onScoreUpdate(String score);

}
