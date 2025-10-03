package com.game.omino.entities;

import com.game.omino.engine.GameWorld;
import com.game.omino.scene.tiles.MattoneTile;
import com.game.omino.scene.tiles.ScalaTile;

import static com.game.omino.utils.Constants.STEP;
import static com.game.omino.utils.Constants.TILE_SIZE;

public class Omino extends Entity {
    public Omino(int x, int y, int w, int h){
        super(x, y, w, h);
    }

    @Override
    public void su() {
        if(isOverTiles()) {
            x = tilesOverlapping[0].getX(); //tilesOverlapping[0] scalaTile superiore
            y -= STEP;
        }
    }

    @Override
    public void giu() {
        if(isOverTiles()){
            x = tilesOverlapping[1].getX(); //tilesOverlapping[1] scalaTile inferiore
            if(isOverTiles() && !isOnTiles()) {
                y += STEP;
            }
        }
    }

    @Override
    public void sinistra() {
        if(!falling) {
            x -= STEP;
        }
    }

    @Override
    public void destra() {
        if(!falling) {
            x += STEP;
        }
    }

}
