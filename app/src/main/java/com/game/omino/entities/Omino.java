package com.game.omino.entities;

import android.util.Log;
import com.game.omino.engine.GameWorld;
import com.game.omino.scene.tiles.MattoneTile;
import com.game.omino.scene.tiles.NullTile;
import com.game.omino.scene.tiles.ScalaTile;
import com.game.omino.scene.tiles.Tile;

import java.util.List;

import static com.game.omino.utils.Constants.STEP;
import static com.game.omino.utils.Constants.TILE_SIZE;

public class Omino extends Entity {

    private static final String IDLE_DX_TX = "omino_idle_dx";
    private static final String IDLE_SX_TX = "omino_idle_sx";
    private static final String IDLE_UP_TX = "omino_idle_up";
    private static final String RUN_DX_TX = "omino_run_dx";
    private static final String RUN_SX_TX = "omino_run_sx";
    private static final String RUN_UP_TX = "omino_run_up";
    private static final String RUN_DOWN_TX = "omino_run_down";
    private static final String FALLING_TX = "omino_falling";



    //private boolean dx = true;


    public Omino(int x, int y, int w, int h){
        super(IDLE_DX_TX, x, y, w, h);
        //dx = true;
    }

    @Override
    public void su() {
        if(!falling) {
            setTilesOverlapping(GameWorld.getInstance().getLevel().getTilesOverlapping(this));
            if (tilesOverlapping[0] != null) {
                x = tilesOverlapping[0].getX(); //tilesOverlapping[0] scalaTile superiore
                if (GameWorld.getInstance().getLevel().isFreeArea(y - STEP, tilesOverlapping[0].getX())) {
                    y = tilesOverlapping[0].getY() - h;
                } else {
                    y -= STEP;
                }
                currentAnimation = RUN_UP_TX;
            }
        }else {
            currentAnimation = FALLING_TX;
        }
    }

    /**
     * si muove di uno STEP solo se sotto (a meno di |Xo-Xt|<TILE_SIZE/5) c'è una scala
     */
    @Override
    public void giu() {
        if(!falling) {
            List<Tile> area = GameWorld.getInstance().getLevel().getArea(y + h + STEP, x + (w / 2));
            Tile t = area.get(0); //nullsafe!
            if (Math.abs(x - t.getX()) < TILE_SIZE / 5) {
                if (!(t instanceof MattoneTile)) {//se non è mattone può essere solo ScalaTile o NullTile
                    x = t.getX(); //allineamento...
                    y += STEP;
                    currentAnimation = RUN_DOWN_TX;
                }else{
                    y = t.getY()-h; //mi fermo sul mattone
                }
            }
        }else {
            currentAnimation = FALLING_TX;
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
            currentAnimation = RUN_SX_TX;
            //dx=false;
        }else {
            currentAnimation = FALLING_TX;
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
            currentAnimation = RUN_DX_TX;
            //dx=true;
        }else {
            currentAnimation = FALLING_TX;
        }
    }

    @Override
    public void stopSu() {
        currentAnimation = IDLE_UP_TX;
    }

    @Override
    public void stopGiu() {
        currentAnimation = IDLE_UP_TX;
    }

    @Override
    public void stopDestra() {
        currentAnimation = IDLE_DX_TX;
    }

    @Override
    public void stopSinistra() {
        currentAnimation = IDLE_SX_TX;
    }

    public void fire(boolean dx){
       // Log.d("Omino", "fireeeeeeeeeeeeeeeeeeeeeee");
       GameWorld.getInstance().cancelTile(dx);
    }

}
