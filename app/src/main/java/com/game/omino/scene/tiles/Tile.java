package com.game.omino.scene.tiles;

public abstract class Tile {
    public int x; //posizione X
    public int y; //posizione Y
    public int w; //larghezza
    public int h; //altezza

    public Tile(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

}
