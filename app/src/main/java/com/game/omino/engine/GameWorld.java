package com.game.omino.engine;

import com.game.omino.entities.Entity;
import com.game.omino.entities.Nemico;
import com.game.omino.entities.Omino;
import com.game.omino.levels.Level;
import com.game.omino.levels.LevelBase;
import com.game.omino.render.GameRenderer;
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
        //se non cammina su tiles...
        if (!isOnTiles(omino)) {
            omino.setY(omino.getY() - GRAVITY);
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

    public boolean isOnTiles(Entity entity){
//        int cont = 0;
//        List<Tile> allTiles = new ArrayList<>();
//        allTiles.addAll(mattoni);
//        allTiles.addAll(scale);
//        for (Tile tile: allTiles){
//            if(tile.y == entity.getY() + entity.getH()
//                    && Math.abs(entity.getX() - tile.x) < tile.w){
//                cont++;
//            }
//        }
//        return cont > 0;

        boolean trovato = false;
        List<Tile> allTiles = new ArrayList<>();
        allTiles.addAll(mattoni);
        allTiles.addAll(scale);
        for (int t=0; t<allTiles.size()&& !trovato; t++){
            trovato = allTiles.get(t).y == entity.getY() + entity.getH()
                    &&
                     Math.abs(entity.getX() - allTiles.get(t).x) < allTiles.get(t).w;

        }
        return trovato;
    }


    // Getter per JNI
    public Omino getOmino() { return omino; }

    public float[] getMattonePositionsFlat() {
        float[] arr = new float[mattoni.size() * 2];
        for (int i = 0; i < mattoni.size(); i++) {
            arr[i * 2] = mattoni.get(i).x;
            arr[i * 2 + 1] = mattoni.get(i).y;
        }
        return arr;
    }

    public float[] getScalaPositionsFlat() {
        float[] arr = new float[scale.size() * 2];
        for (int i = 0; i < scale.size(); i++) {
            arr[i * 2] = scale.get(i).x;
            arr[i * 2 + 1] = scale.get(i).y;
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
