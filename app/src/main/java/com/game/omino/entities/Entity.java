package com.game.omino.entities;

public abstract class Entity {
    protected float x; //posizione X
    protected float y; //posizione Y
    protected int w; //larghezza
    protected int h; //altezza

    public Entity(float x, float y, int w, int h){
        this.x=x;
        this.y=y;
        this.w=w;
        this.h=h;
    }

    abstract void su();
    abstract void giu();
    abstract void sinistra();
    abstract void destra();

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
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
}
