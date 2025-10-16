package com.game.omino.engine;

import android.os.Handler;
import android.util.Log;
import com.game.omino.entities.Entity;
import com.game.omino.entities.Nemico;
import com.game.omino.entities.Omino;
import com.game.omino.levels.Level;
import com.game.omino.scene.tiles.MattoneTile;
import com.game.omino.scene.tiles.NullTile;
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

    public Omino getOmino() {
        return omino;
    }

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
        gravita(omino);

        for (Nemico n : level.getNemici()) {
            gravita(n);
            n.aggiorna();
        }

        //se collide con nemico
        if (false) {
            //...
        }

    }

    private void gravita(Entity entity) {
        List<Tile> area = level.getArea(entity.getY() + entity.getH(), entity.getX());
        Tile t;

        boolean trovato = false;
        for (int i = 0; i < 2 && !trovato; i++) {
            t = area.get(i);
            if (t instanceof NullTile) {
                if (Math.abs(t.getX() - entity.getX()) <= TILE_SIZE / 4) {
                    entity.setX(t.getX()); //allineamento
                    entity.setFalling(true);
                    entity.setY(entity.getY() + GRAVITY);
                    trovato = true;
                }
            } else {//Mattonetile o ScalaTile
                if (entity.isFalling()) {//se è in caduta in prossimità del suolo
                    entity.setY(t.getY() - entity.getH());
                    entity.setFalling(false);
                }
            }
        }

    }


    public void loadLevel(Level level) {
        this.level = level;
        this.omino = level.getOmino();
    }


    public void cancelTile(boolean dx) {
        List<Tile> rowDown = level.getRow(omino.getY()+ omino.getH());//riga sotto omino
        List<MattoneTile> mattoniSotto = new ArrayList<>();
        MattoneTile m = null;
        for (Tile t: rowDown){
            if(t instanceof MattoneTile){
                if(dx){
                    if(Math.abs(omino.getX()+ omino.getW()- t.getX())<=TOLERANCE){
                        m = (MattoneTile) t;
                    }
                }else {
                    if(Math.abs(t.getX()+t.getW()- omino.getX())<=TOLERANCE){
                        m = (MattoneTile) t;
                    }
                }
            }
        }

        if(m!=null){
            if(level.getRow(omino.getY()).get(m.getX()/TILE_SIZE) instanceof NullTile){
                //sostituisce MattoneTile con NullTile dopo un intervallo
                rowDown.set(m.getX()/TILE_SIZE, new NullTile(m.getX(), m.getY(), m.getW(), m.getH()));

                Handler handler = new Handler();
                MattoneTile finalM = m;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //ripristino mattone...
                        rowDown.set(finalM.getX()/TILE_SIZE, finalM);
                        //Log.d("GameWorld: cancella mattone: ", "" + System.currentTimeMillis());
                    }
                }, SEC_ERASE_MATTONE);
            }
        }

    }
}
