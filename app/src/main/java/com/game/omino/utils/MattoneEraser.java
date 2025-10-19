package com.game.omino.utils;

import com.game.omino.scene.tiles.NullTile;
import com.game.omino.scene.tiles.Tile;

import java.util.List;

import static com.game.omino.utils.Constants.TILE_SIZE;

public class MattoneEraser implements Runnable{
    private Tile m;
    private int x;
    private List<Tile> row;

    public MattoneEraser(Tile m, int x, List<Tile> row) {
        this.m = m;
        this.x = x;
        this.row = row;
    }

    @Override
    public void run() {

       // new Thread(new MattoneEraser(tFD, x, rowDown)).start();//





    }
}
