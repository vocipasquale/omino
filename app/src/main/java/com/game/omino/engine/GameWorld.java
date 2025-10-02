package com.game.omino.engine;

import android.util.Log;
import com.game.omino.entities.Entity;
import com.game.omino.entities.Nemico;
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

        int idxOverLapp1 = (((int) omino.getY())/TILE_SIZE)*TILE_SIZE;
        int idxOverLapp2 = ((((int) omino.getY())/TILE_SIZE)+1)*TILE_SIZE;
        int idxOnTiles = (int) (omino.getY()+omino.getH());

        ScalaTile scala;
        scala = getOverlappingScala(omino, getScale(level.getTiles()[idxOverLapp1]));
        if(scala==null){
            scala = getOverlappingScala(omino, getScale(level.getTiles()[idxOverLapp2]));
        }

        omino.setOverlappingScala(scala);
        omino.setOnMattone(isOnTiles(omino, getMattoni(level.getTiles()[idxOnTiles])));
        omino.setOnScala(isOnTiles(omino, getScale(level.getTiles()[idxOnTiles])));

        //Log.i("omino", omino.toString());

        //cammina su tiles o è su una scala, applico gravità non sta cadendo...
        if (omino.isOnMattone() || omino.isOnScala() || omino.getOverlappingScala() != null) {
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

    public boolean isOnTiles(Entity entity, List<? extends Tile> tiles){
        boolean trovato = false;

        for (int t=0; t<tiles.size()&& !trovato; t++){
            //Log.i("isOnTiles", ""+tiles.get(t).getY());
            trovato = tiles.get(t).getY() == entity.getY() + entity.getH()
                    &&
                    Math.abs(entity.getX() - tiles.get(t).getX()) < tiles.get(t).getW();
        }
        //if(trovato){
            //Log.i("su tile", "tile ");
        //}
        return trovato;
    }

    public ScalaTile getOverlappingScala(Entity entity, List<? extends Tile> scale) {
        ScalaTile scalaTile = null;
        boolean trovato = false;
        Log.i("getOverlappingScala", "scale size: "+scale.size());

        for (int s=0; s<scale.size()&& !trovato; s++){
            trovato =
                    Math.abs(entity.getX() - scale.get(s).getX()) <= MIN_DIST_OVERLLAPPING_SCALA
                        &&
                    Math.abs(entity.getY() - scale.get(s).getY()) <= entity.getH();
            if(trovato){
                scalaTile = (ScalaTile) scale.get(s);
                Log.i("sovrapposto", "scala:"+scalaTile);
            }
        }
        return scalaTile;
    }

    private List<Tile> getGiu(Entity entity){
        List<Tile> result = new ArrayList<>();

        //tutte le tiles "immediatamente sotto all'entità
        Tile[] riga = level.getTiles()[(int) (entity.getY()+entity.getY()) + 1];

        //DA OTTIMIZZARE
        for(int r=0; r<riga.length; r++){
            if(entity.getX() - riga[r].getX() <= riga[r].getW()
                &&
               entity.getX()+ entity.getW() > riga[r].getX()
               &&
                    entity.getX()+ entity.getW() < riga[r].getX() + riga[r].getW()
            )
            result.add(riga[r]);
        }

        return result;
    }

    public void loadLevel(Level level) {
        this.level = level;
        this.omino = level.getOmino();
    }


}
