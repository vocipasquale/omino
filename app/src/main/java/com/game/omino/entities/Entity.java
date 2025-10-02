package com.game.omino.entities;

import com.game.omino.scene.tiles.ScalaTile;

public abstract class Entity {
    protected int x; //posizione X
    protected int y; //posizione Y
    protected int w; //larghezza
    protected int h; //altezza

    protected boolean falling = false; //sta cadendo
    protected boolean onMattone = false;
    protected boolean onScala = false;
    protected ScalaTile overlappingScala = null; //sovrapposto ad una tile scala

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

    public boolean isOnMattone() {
        return onMattone;
    }

    public void setOnMattone(boolean onMattone) {
        this.onMattone = onMattone;
    }

    public boolean isOnScala() {
        return onScala;
    }

    public void setOnScala(boolean onScala) {
        this.onScala = onScala;
    }

    public ScalaTile getOverlappingScala() {
        return overlappingScala;
    }

    public void setOverlappingScala(ScalaTile overlappingScala) {
        this.overlappingScala = overlappingScala;
    }
}
