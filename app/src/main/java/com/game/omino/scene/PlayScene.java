package com.game.omino.scene;

import com.game.omino.engine.GameWorld;
import com.game.omino.levels.Level;
import com.game.omino.levels.LevelBase;
import  com.game.omino.render.GameRenderer;

public class PlayScene implements Scene {

    private final Level level;

    public PlayScene(Level level) {
        this.level = level;
    }

    @Override
    public void onEnter() {
        GameWorld.getInstance().loadLevel(level);
    }

    @Override
    public void onExit() {
        // cleanup se necessario
    }

    @Override
    public void update(float deltaTime) {
        GameWorld.getInstance().update(deltaTime);

        // Condizioni di fine livello -> switch di scena


        // in futuro: se il livello è completato
        // SceneManager.setScene(new SummaryScene(true));
    }

    @Override
    public void render() {
        GameRenderer.renderWorld(GameWorld.getInstance());
    }
}
