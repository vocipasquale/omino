package com.game.omino.entities;

import com.game.omino.engine.GameWorld;
import com.game.omino.scene.tiles.MattoneTile;
import com.game.omino.scene.tiles.NullTile;
import com.game.omino.scene.tiles.ScalaTile;
import com.game.omino.scene.tiles.Tile;

import java.util.List;

import static com.game.omino.utils.Constants.STEP;
import static com.game.omino.utils.Constants.TILE_SIZE;

public class Omino extends Entity {
    public Omino(int x, int y, int w, int h){
        super(x, y, w, h);
    }

    @Override
    public void su() {
        setTilesOverlapping(GameWorld.getInstance().getLevel().getTilesOverlapping(this));
        if(tilesOverlapping[0] != null) {
            x = tilesOverlapping[0].getX(); //tilesOverlapping[0] scalaTile superiore
            if(GameWorld.getInstance().getLevel().isFreeArea(y-STEP, tilesOverlapping[0].getX())){
               y = tilesOverlapping[0].getY() - h;
            }else{
                y -= STEP;
            }
        }
    }

    /**
     * si muove di uno STEP solo se sotto (a meno di |Xo-Xt|<TILE_SIZE/5) c'è una scala
     */
    @Override
    public void giu() {
            List<Tile> area = GameWorld.getInstance().getLevel().getArea(y+h+STEP, x+(w/2));
            Tile t = area.get(0); //nullsafe!
            if(Math.abs(x-t.getX())<TILE_SIZE/5){
                if(!(t instanceof MattoneTile)){//se non è mattone può essere solo ScalaTile o NullTile
                    x = t.getX(); //allineamento...
                    y += STEP;
                }
            }

    }

    @Override
    public void sinistra() {
        if(!falling){
            List<Tile> area = GameWorld.getInstance().getLevel().getArea(y, x-STEP);
            if(area.get(0) instanceof MattoneTile){
                x = area.get(0).getX()+w;
            }else{
                x -= STEP;
            }
        }

    }

    @Override
    public void destra() {
        if(!falling){
            List<Tile> area = GameWorld.getInstance().getLevel().getArea(y, x+w+STEP);
            if(area.get(0) instanceof MattoneTile){
                x = area.get(0).getX()-w;
            }else{
                x += STEP;
            }
        }

    }

}
