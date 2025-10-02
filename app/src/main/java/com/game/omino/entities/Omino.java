package com.game.omino.entities;

import com.game.omino.engine.GameWorld;
import com.game.omino.scene.tiles.MattoneTile;
import com.game.omino.scene.tiles.ScalaTile;

import static com.game.omino.utils.Constants.STEP;
import static com.game.omino.utils.Constants.TILE_SIZE;

public class Omino extends Entity {
    public Omino(float x, float y, int w, int h){
        super(x, y, w, h);
    }

    @Override
    public void su() {
        if(overlappingScala!=null) {
            x = overlappingScala.getX();
            y -= STEP;
        }
    }

    @Override
    public void giu() {
        if(overlappingScala!=null){
            x = overlappingScala.getX();
            if(overlappingScala!=null && !onMattone) {
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
