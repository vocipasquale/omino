package com.game.omino.levels;

import android.util.DisplayMetrics;
import com.game.omino.entities.Omino;
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

        //piano 0
        for(int i=TILE_SIZE * 0; i<SCREEN_WIDTH-TILE_SIZE; i+=TILE_SIZE) {
            MattoneTile mattone = new MattoneTile(i, SCREEN_HEIGHT - TILE_SIZE, TILE_SIZE, TILE_SIZE);
            mattoni.add(mattone);
        }

        //piano 1
        for(int i=TILE_SIZE * 1; i<SCREEN_WIDTH-TILE_SIZE; i+=TILE_SIZE) {
            MattoneTile mattone = new MattoneTile(i, SCREEN_HEIGHT - (TILE_SIZE*2), TILE_SIZE, TILE_SIZE);
            mattoni.add(mattone);
        }

        //piano 2
        for(int i=TILE_SIZE * 2; i<SCREEN_WIDTH-(TILE_SIZE*3); i+=TILE_SIZE) {
            MattoneTile mattone = new MattoneTile(i, SCREEN_HEIGHT - (TILE_SIZE*3), TILE_SIZE, TILE_SIZE);
            mattoni.add(mattone);
        }

        //piano 3
        for(int i=TILE_SIZE * 3; i<SCREEN_WIDTH-(TILE_SIZE*3); i+=TILE_SIZE) {
            MattoneTile mattone = new MattoneTile(i, SCREEN_HEIGHT - (TILE_SIZE*4), TILE_SIZE, TILE_SIZE);
            mattoni.add(mattone);
        }

        ScalaTile scala = new ScalaTile(SCREEN_WIDTH/2, (SCREEN_HEIGHT/2)+TILE_SIZE*5, TILE_SIZE, TILE_SIZE);
        ScalaTile scala2 = new ScalaTile(SCREEN_WIDTH/2, (SCREEN_HEIGHT/2)+TILE_SIZE*6, TILE_SIZE, TILE_SIZE);
        ScalaTile scala3 = new ScalaTile(SCREEN_WIDTH/2, (SCREEN_HEIGHT/2)+(TILE_SIZE*7), TILE_SIZE, TILE_SIZE);
        ScalaTile scala4 = new ScalaTile(SCREEN_WIDTH/2, (SCREEN_HEIGHT/2)+(TILE_SIZE*8), TILE_SIZE, TILE_SIZE);
        ScalaTile scala5 = new ScalaTile(SCREEN_WIDTH/2, (SCREEN_HEIGHT/2)+(TILE_SIZE*9), TILE_SIZE, TILE_SIZE);

        scale.add(scala);
        scale.add(scala2);
        scale.add(scala3);
        scale.add(scala4);
        scale.add(scala5);

        omino = new Omino(TILE_SIZE * 8, SCREEN_HEIGHT - (TILE_SIZE*15), TILE_SIZE, TILE_SIZE);


    }


}
