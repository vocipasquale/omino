package com.game.omino.entities;

import com.game.omino.scene.tiles.MattoneTile;
import com.game.omino.scene.tiles.ScalaTile;

import static com.game.omino.utils.Constants.TILE_SIZE;

public class Omino extends Entity {
    public Omino(float x, float y, int w, int h){
        super(x, y, w, h);
    }

    public boolean isOnTopOf(MattoneTile mattone) {
        // semplice controllo bounding box
        return Math.abs(this.x - mattone.x) < TILE_SIZE &&
                Math.abs(this.y - (mattone.y + TILE_SIZE)) < 2;
    }

    public boolean isOverlapping(ScalaTile scala) {
        // bounding box overlap
        return Math.abs(this.x - scala.x) < TILE_SIZE &&
                Math.abs(this.y - scala.y) < TILE_SIZE;
    }
}
