package com.game.omino.engine;

import com.game.omino.entities.Entity;
import com.game.omino.entities.Nemico;
import com.game.omino.entities.Omino;
import com.game.omino.levels.Level;
import com.game.omino.levels.LevelBase;
import com.game.omino.scene.tiles.MattoneTile;
import com.game.omino.scene.tiles.ScalaTile;
import com.game.omino.scene.tiles.Tile;

import java.util.ArrayList;
import java.util.List;

import static com.game.omino.utils.Constants.*;

public class GameWorld {

    private static GameWorld instance;

    private List<MattoneTile> mattoni;
    private List<ScalaTile> scale;
    private List<Nemico> nemici;
    private Omino omino;


    private GameWorld() {
        loadLevel(new LevelBase());
    }

    public static GameWorld getInstance() {
        if (instance == null) instance = new GameWorld();
        return instance;
    }

    public void update(float deltaTime) {
        //check sovrapposizione a una tile scala...
        omino.setOverlappingScala(getOverlappingScala(omino));
        omino.setOnMattone(isOnTiles(omino, mattoni));
        omino.setOnScala(isOnTiles(omino, scale));

        //se non cammina su tiles e non è su una scala, applico gravità
        if (!omino.isOnMattone() && !omino.isOnScala() && omino.getOverlappingScala() == null) {
            omino.setFalling(true);
            omino.setY(omino.getY() - GRAVITY);
        }else{
            //non sta cadendo...
            omino.setFalling(false);
        }



        //se collide con nemico
        if(false){
            //...
        }

        //supera confine inferiore...
        if (omino.getY() > SCREEN_HEIGHT - omino.getH()) {
            omino.setY(SCREEN_HEIGHT - omino.getH());
        }
    }

    public boolean isOnTiles(Entity entity, List<? extends Tile> tiles){
        boolean trovato = false;
        for (int t=0; t<tiles.size()&& !trovato; t++){
            trovato = tiles.get(t).getY() == entity.getY() + entity.getH()
                    &&
                    Math.abs(entity.getX() - tiles.get(t).getX()) < tiles.get(t).getW();
        }
        return trovato;
    }

    public ScalaTile getOverlappingScala(Omino omino) {
        ScalaTile scalaTile = null;
        boolean trovato = false;
        for (int s=0; s<scale.size()&& !trovato; s++){
            trovato =
                    Math.abs(omino.getX() - scale.get(s).getX()) <= MIN_DIST_OVERLLAPPING_SCALA
                        &&
                    Math.abs(omino.getY() - scale.get(s).getY()) <= omino.getH();
            if(trovato){
                scalaTile = scale.get(s);
            }
        }
        return scalaTile;
    }


    // Getter per JNI
    public Omino getOmino() { return omino; }

    public float[] getMattonePositionsFlat() {
        float[] arr = new float[mattoni.size() * 2];
        for (int i = 0; i < mattoni.size(); i++) {
            arr[i * 2] = mattoni.get(i).getX();
            arr[i * 2 + 1] = mattoni.get(i).getY();
        }
        return arr;
    }

    public float[] getScalaPositionsFlat() {
        float[] arr = new float[scale.size() * 2];
        for (int i = 0; i < scale.size(); i++) {
            arr[i * 2] = scale.get(i).getX();
            arr[i * 2 + 1] = scale.get(i).getY();
        }
        return arr;
    }

    public void loadLevel(Level level) {
        this.omino = level.getOmino();
        this.mattoni = level.getMattoni();
        this.scale = level.getScale();
        this.nemici = level.getNemici();
    }



}
