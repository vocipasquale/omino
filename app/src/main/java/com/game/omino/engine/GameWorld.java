package com.game.omino.engine;

import android.util.Log;
import com.game.omino.entities.Entity;
import com.game.omino.entities.Omino;
import com.game.omino.levels.Level;
import com.game.omino.scene.tiles.MattoneTile;
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


        omino.setTilesOverlapping(getTilesOverlapping(omino));
        omino.setTilesDown(getTilesDown(omino));


        //Log.i("omino", omino.toString());

        //cammina su tiles o è su una scala, applico gravità non sta cadendo...
        if (omino.isOnTiles() || omino.isOverTiles()) {
            omino.setFalling(false);
        }else{//applico gravità...
            omino.setFalling(true);
            omino.setY(omino.getY() - GRAVITY);
        }



        //se collide con nemico
        if(false){
            //...
        }


    }

    private List<? extends Tile> getMattoni(Tile[] tiles) {
        List<MattoneTile> result = new ArrayList<>();
        for(Tile t: tiles){
            if(t instanceof MattoneTile){
                result.add((MattoneTile) t);
            }
        }
        return result;
    }

    private List<? extends Tile> getScale(Tile[] tiles) {
        List<ScalaTile> result = new ArrayList<>();
        for(Tile t: tiles){
            if(t instanceof ScalaTile){
                result.add((ScalaTile) t);
            }
        }
        return result;
    }

    public Tile[] getTilesDown(Entity entity){
        Tile[] result = new Tile[2];
        Tile[] tiles = level.getTiles()[entity.getY()+entity.getH()];
        int c = 0;

        if(tiles != null) {


            for (int t = 0; t < tiles.length && c < 2; t += TILE_SIZE) {
                if (tiles[t].getY() == entity.getY() + entity.getH()
                        && Math.abs(entity.getX() - tiles[t].getX()) < tiles[t].getW()) {
                    result[c++] = tiles[t];
                }
            }
        }
        return result;
    }

    public Tile[] getTilesOverlapping(Entity entity) {
        int idxOverLapp1 = (entity.getY()/TILE_SIZE)*TILE_SIZE;
        int idxOverLapp2 = ((entity.getY()/TILE_SIZE)+1)*TILE_SIZE;


        Tile[] result = new Tile[2];
        Tile[] tiles = new Tile[TILES_4_ROW*2];

        for(int i=0; i<TILES_4_ROW; i++){
           tiles[i]=level.getTiles()[idxOverLapp1][i*TILE_SIZE];
        }
        for(int i=TILES_4_ROW; i<TILES_4_ROW*2; i++){
           tiles[i]=level.getTiles()[idxOverLapp2][(i-TILES_4_ROW)*TILE_SIZE];
        }

      //  System.arraycopy(level.getTiles()[idxOverLapp1], 0, tiles, 0, TILES_4_ROW);
      //  System.arraycopy(level.getTiles()[idxOverLapp2], 0, tiles, TILES_4_ROW, TILES_4_ROW);

        int t = 0;
        for (int s=0; s<tiles.length && t<2; s++){
            if(Math.abs(entity.getY())-tiles[s].getY() < TILE_SIZE){
                result[t++] = tiles[s];
            }
        }
        return result;
    }

//    private List<Tile> getGiu(Entity entity){
//        List<Tile> result = new ArrayList<>();
//
//        //tutte le tiles "immediatamente sotto all'entità
//        Tile[] riga = level.getTiles()[entity.getY()+entity.getY() + 1];
//
//        //DA OTTIMIZZARE
//        for(int r=0; r<riga.length; r++){
//            if(entity.getX() - riga[r].getX() <= riga[r].getW()
//                &&
//               entity.getX()+ entity.getW() > riga[r].getX()
//               &&
//                    entity.getX()+ entity.getW() < riga[r].getX() + riga[r].getW()
//            )
//            result.add(riga[r]);
//        }
//
//        return result;
//    }

    public void loadLevel(Level level) {
        this.level = level;
        this.omino = level.getOmino();
    }


}
