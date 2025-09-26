package com.game.omino.scene;

import com.game.omino.engine.GameWorld;
import com.game.omino.levels.LevelBase;

public class PlayScene implements Scene {

    private final GameWorld world;

    public PlayScene() {
        world = GameWorld.getInstance();
        world.loadLevel(new LevelBase());
    }

    @Override
    public void onEnter() {
        // eventuale setup extra all'ingresso scena
    }

    @Override
    public void onExit() {
        // cleanup se necessario
    }

    @Override
    public void update(float deltaTime) {
        world.update(deltaTime);
    }

    @Override
    public void render() {
        // Il rendering è delegato al GameRenderer/Native
    }
}
