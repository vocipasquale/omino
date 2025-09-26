package com.game.omino.engine;

import com.game.omino.entities.Nemico;
import com.game.omino.entities.Omino;
import com.game.omino.levels.Level;
import com.game.omino.levels.LevelBase;
import com.game.omino.render.GameRenderer;
import com.game.omino.scene.tiles.MattoneTile;
import com.game.omino.scene.tiles.ScalaTile;
import com.game.omino.scene.tiles.Tile;

import java.util.ArrayList;
import java.util.List;

import static com.game.omino.utils.Constants.SCREEN_HEIGHT;
import static com.game.omino.utils.Constants.TILE_SIZE;

public class GameWorld {

    private static GameWorld instance;

    private static final float GRAVITY = -9.8f;

    private List<MattoneTile> mattoni;
    private List<ScalaTile> scale;
    private List<Nemico> nemici;
    private Omino omino;


    private GameWorld() {
        loadLevel(new LevelBase());
    }

    public static GameWorld getInstance() {
        if (instance == null) instance = new GameWorld();
        return instance;
    }

    public void update(float deltaTime) {
        if (!isOnMattone() && !isOnScala()) {
           // omino.velocityY += GRAVITY * deltaTime;
            // omino.y -= omino.velocityY;
        } else {
            omino.velocityY = 0;
        }

        if (omino.y > SCREEN_HEIGHT - TILE_SIZE) {
            omino.y = SCREEN_HEIGHT - TILE_SIZE;
            omino.velocityY = 0;
        }
    }

    private boolean isOnMattone() {
        for (MattoneTile mattone : mattoni) {
            if (omino.isOnTopOf(mattone)) return true;
        }
        return false;
    }

    private boolean isOnScala() {
        for (ScalaTile scala : scale) {
            if (omino.isOverlapping(scala)) return true;
        }
        return false;
    }

    // Getter per JNI
    public Omino getOmino() { return omino; }

    public float[] getMattonePositionsFlat() {
        float[] arr = new float[mattoni.size() * 2];
        for (int i = 0; i < mattoni.size(); i++) {
            arr[i * 2] = mattoni.get(i).x;
            arr[i * 2 + 1] = mattoni.get(i).y;
        }
        return arr;
    }

    public float[] getScalaPositionsFlat() {
        float[] arr = new float[scale.size() * 2];
        for (int i = 0; i < scale.size(); i++) {
            arr[i * 2] = scale.get(i).x;
            arr[i * 2 + 1] = scale.get(i).y;
        }
        return arr;
    }

    public void loadLevel(Level level) {
        this.omino = level.getOmino();
        this.mattoni = level.getMattoni();
        this.scale = level.getScale();
        this.nemici = level.getNemici();
    }

}
