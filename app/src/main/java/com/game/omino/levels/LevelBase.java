package com.game.omino.levels;

import android.util.DisplayMetrics;
import android.util.Log;
import com.game.omino.entities.Omino;
import com.game.omino.entities.Nemico;
import com.game.omino.scene.tiles.MattoneTile;
import com.game.omino.scene.tiles.ScalaTile;
import com.game.omino.scene.tiles.Tile;

import java.util.ArrayList;
import java.util.List;

import static com.game.omino.utils.Constants.*;

public class LevelBase extends Level {

    /**
     *     0,0- - - - >
     *      |
     *      |
     *      v
     *
     */


    public LevelBase() {
        mattoni = new ArrayList<>();
        scale = new ArrayList<>();
        nemici = new ArrayList<>();

        setMetrics();

        //piano 0
        for(int i=TILE_SIZE * 0; i<SCREEN_WIDTH-TILE_SIZE; i+=TILE_SIZE) {
            MattoneTile mattone = new MattoneTile(i, SCREEN_HEIGHT - TILE_SIZE, TILE_SIZE, TILE_SIZE);
            mattoni.add(mattone);
        }

        //piano 1
        for(int i=TILE_SIZE * 0; i<SCREEN_WIDTH-TILE_SIZE; i+=TILE_SIZE) {
            MattoneTile mattone = new MattoneTile(i, SCREEN_HEIGHT - (TILE_SIZE*2), TILE_SIZE, TILE_SIZE);
            mattoni.add(mattone);
        }

        //piano 2
        for(int i=TILE_SIZE * 3; i<SCREEN_WIDTH-(TILE_SIZE*3); i+=TILE_SIZE) {
            MattoneTile mattone = new MattoneTile(i, SCREEN_HEIGHT - (TILE_SIZE*3), TILE_SIZE, TILE_SIZE);
            mattoni.add(mattone);
        }

        //piano 3
        for(int i=TILE_SIZE * 3; i<SCREEN_WIDTH-(TILE_SIZE*3); i+=TILE_SIZE) {
            MattoneTile mattone = new MattoneTile(i, SCREEN_HEIGHT - (TILE_SIZE*4), TILE_SIZE, TILE_SIZE);
            mattoni.add(mattone);
        }

        omino = new Omino((SCREEN_WIDTH/2) - TILE_SIZE, (SCREEN_HEIGHT/2) - TILE_SIZE, TILE_SIZE, TILE_SIZE);

        scale = new ArrayList<>();

    }

    @Override
    void setMetrics() {
        DisplayMetrics metrics = new DisplayMetrics();
        wScreen = metrics.widthPixels;
        hScreen = metrics.heightPixels;
    }
}
