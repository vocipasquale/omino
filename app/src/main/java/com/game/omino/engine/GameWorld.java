package com.game.omino.engine;

import android.util.Log;
import com.game.omino.entities.Entity;
import com.game.omino.entities.Omino;
import com.game.omino.levels.Level;
import com.game.omino.scene.tiles.MattoneTile;
import com.game.omino.scene.tiles.NullTile;
import com.game.omino.scene.tiles.ScalaTile;
import com.game.omino.scene.tiles.Tile;

import java.util.ArrayList;
import java.util.List;

import static com.game.omino.utils.Constants.*;

public class GameWorld {

    private static GameWorld instance;

    private Level level;

    private Omino omino;


    public static GameWorld getInstance() {
        if (instance == null) instance = new GameWorld();
        return instance;
    }

    public Omino getOmino() { return omino; }

    public Level getLevel() {
        return level;
    }

    public void update(float deltaTime) {
        //supera confine inferiore...
        if (omino.getY() > SCREEN_HEIGHT - omino.getH()) {
            omino.setY(SCREEN_HEIGHT - omino.getH());
            return;
        }
        // omino è dentro la schermata...



       //applico gravità se l'area è libera...
        Tile t = level.getArea(omino.getY()+ omino.getH(), omino.getX()).get(0);
        if(t != null){
            if(t instanceof NullTile){
                omino.setFalling(true);
                omino.setY(omino.getY() + GRAVITY);
            }else{ //Mattonetile o ScalaTile
                omino.setFalling(false);
                omino.setY(t.getY()-omino.getH());//se è in caduta in prossimità del suolo
            }
        }else{
            omino.setFalling(false);
        }




        //se collide con nemico
        if(false){
            //...
        }


    }




    public void loadLevel(Level level) {
        this.level = level;
        this.omino = level.getOmino();
    }


    public void cancelTile(int y, int x) {
//        List<Tile> col = level.getColumn(x/TILE_SIZE);
//        if(col != null){
//            Tile t = col.get(y/TILE_SIZE);
//            if(t instanceof MattoneTile && col.get((y-TILE_SIZE)/TILE_SIZE) instanceof NullTile){
//                col.set(y/TILE_SIZE, new NullTile(t.getX(), t.getY(), t.getW(), t.getH()));
//            }
//        }

        List<Tile> rowUp = level.getRow(y-TILE_SIZE);
        List<Tile> rowDown = level.getRow(y);
        if(rowDown != null){
            Tile tD = rowDown.get(x/TILE_SIZE);
            Tile tU = rowUp.get(x/TILE_SIZE);
            if(tD instanceof MattoneTile && tU instanceof NullTile) {
                rowDown.set(x/TILE_SIZE, new NullTile(tD.getX(), tD.getY(), tD.getW(), tD.getH()));
            }
        }
    }
}
