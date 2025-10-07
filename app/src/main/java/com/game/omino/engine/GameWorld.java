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



       //applico gravità se l'area è libera...
        if (level.isFreeArea(omino.getY()+(-1)*GRAVITY, omino.getX())) {
            omino.setFalling(true);
            omino.setY(omino.getY() + (-1)*GRAVITY); //(gravità negativa)
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


}
