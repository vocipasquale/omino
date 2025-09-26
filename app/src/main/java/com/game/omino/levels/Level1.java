package com.game.omino.levels;

import android.util.DisplayMetrics;
import com.game.omino.entities.*;
import com.game.omino.scene.tiles.MattoneTile;
import com.game.omino.scene.tiles.ScalaTile;
import com.game.omino.scene.tiles.Tile;

import java.util.ArrayList;
import java.util.List;

public class Level1 extends Level {

    private final List<Tile> tiles;
    private final List<Nemico> enemies;


    public Level1() {
        tiles = new ArrayList<>();
        enemies = new ArrayList<>();


    }


    public List<Tile> getTiles() {
        return tiles;
    }

    @Override
    void setMetrics() {
       DisplayMetrics metrics = new DisplayMetrics();
       wScreen = metrics.widthPixels;
       hScreen = metrics.heightPixels;
    }

    @Override
    public List<Nemico> getNemici() {
        return enemies;
    }

    @Override
    public Omino getOmino() {
        return omino;
    }
}
