package com.game.omino.entities;

import com.game.omino.scene.tiles.NullTile;
import com.game.omino.scene.tiles.ScalaTile;
import com.game.omino.scene.tiles.Tile;

public abstract class Entity {
    protected int x; //posizione X
    protected int y; //posizione Y
    protected int w; //larghezza
    protected int h; //altezza

    protected Tile[] tilesDown = new Tile[2]; //[0] tile sotto a sinistra, [1] tile sotto a destra
    protected Tile tilesUp;
    protected Tile tilesRight;
    protected Tile tilesLeft;
    protected Tile[] tilesOverlapping = new Tile[2]; //[0] tile sopra (Yt < Ye), [1] tile sotto (Y

    protected boolean falling = false; //sta cadendo
   // protected boolean onMattone = false;
   // protected boolean onScala = false;


    public Entity(int x, int y, int w, int h){
        this.x=x;
        this.y=y;
        this.w=w;
        this.h=h;
    }

    abstract void su();
    abstract void giu();
    abstract void sinistra();
    abstract void destra();

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public boolean isFalling() {
        return falling;
    }

    public void setFalling(boolean falling) {
        this.falling = falling;
    }


    public void setTilesOverlapping(Tile[] overlappingScala) {
        this.tilesOverlapping = overlappingScala;
    }


    public Tile[] getTilesDown() {
        return tilesDown;
    }

    public void setTilesDown(Tile[] tilesDown) {
        this.tilesDown = tilesDown;
    }

    public Tile getTilesUp() {
        return tilesUp;
    }

    public void setTilesUp(Tile tilesUp) {
        this.tilesUp = tilesUp;
    }

    public Tile getTilesRight() {
        return tilesRight;
    }

    public void setTilesRight(Tile tilesRight) {
        this.tilesRight = tilesRight;
    }

    public Tile getTilesLeft() {
        return tilesLeft;
    }

    public void setTilesLeft(Tile tilesLeft) {
        this.tilesLeft = tilesLeft;
    }

    public boolean isOnTiles() {
        return tilesDown[0] != null && !(tilesDown[0] instanceof NullTile)
                ||
                tilesDown[1] != null && !(tilesDown[1] instanceof NullTile);
    }

    public boolean isOverTiles() {
        return tilesOverlapping[0] != null && !(tilesOverlapping[0] instanceof NullTile)
                ||
                tilesOverlapping[1] != null && !(tilesOverlapping[1] instanceof NullTile);
    }
}
