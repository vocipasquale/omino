package com.game.omino.entities;

public abstract class Entity {
    public float x; //posizione X
    public float y; //posizione Y
    public int w; //larghezza
    public int h; //altezza

    public float velocityX; //velocità asse X
    public float velocityY; //velocità asse Y

    public Entity(float x, float y, int w, int h){
        this.x=x;
        this.y=y;
        this.w=w;
        this.h=h;
        velocityX=0;
        velocityY=0;
    }
}
