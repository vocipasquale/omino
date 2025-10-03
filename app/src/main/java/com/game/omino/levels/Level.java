package com.game.omino.levels;

import android.util.Log;
import com.game.omino.entities.*;
import com.game.omino.scene.tiles.*;

import java.util.ArrayList;
import java.util.List;

import static com.game.omino.utils.Constants.*;

public class Level {
    private Tile[][] tiles = new Tile[SCREEN_HEIGHT][SCREEN_WIDTH];
    private List<MattoneTile> mattoni = new ArrayList<>();
    private List<ScalaTile> scale = new ArrayList<>();
    private List<Nemico> nemici = new ArrayList<>();

    private Omino omino = new Omino(0,0,TILE_SIZE,TILE_SIZE);

    public void setTilesMatrix(Tile[][] tilesMatrix){
        MattoneTile mattoneInstance;
        ScalaTile scalaInstance;
        NullTile nullInstance;
        String riga = "";

        //init
        for(int r=0; r<SCREEN_HEIGHT; r++){
            for(int c=0; c<SCREEN_WIDTH; c++){
                tiles[r][c]=null;
            }
        }

        for (int r=0; r<TILES_4_COLUMN; r++){
            riga="";
            for(int c=0; c<TILES_4_ROW; c++) {
                if(tilesMatrix[r][c] instanceof MattoneTile){
                    mattoneInstance = (MattoneTile) tilesMatrix[r][c];
                    tiles[mattoneInstance.getY()][mattoneInstance.getX()]=mattoneInstance;
                    mattoni.add(mattoneInstance); //temporaneo fino a quando non capisco come usare la matrice...
                    riga+="@ ("+mattoneInstance.getY()+", "+mattoneInstance.getX()+")\t\t";
                } else if(tilesMatrix[r][c] instanceof ScalaTile){
                    scalaInstance = (ScalaTile) tilesMatrix[r][c];
                    tiles[scalaInstance.getY()][scalaInstance.getX()]=scalaInstance;
                    scale.add(scalaInstance);//temporaneo fino a quando non capisco come usare la matrice...
                    riga+="# ("+scalaInstance.getY()+", "+scalaInstance.getX()+")\t\t";
                } else if(tilesMatrix[r][c] instanceof NullTile){
                    nullInstance = (NullTile) tilesMatrix[r][c];
                    tiles[nullInstance.getY()][nullInstance.getX()]=nullInstance;
                    riga+="- ("+nullInstance.getY()+", "+nullInstance.getX()+")\t\t";
                }
            }
            Log.i("riga: ", riga);
        }
    }

    public float[] getMattonePositionsFlat() {
        float[] arr = new float[mattoni.size() * 2];
        for (int i = 0; i < mattoni.size(); i++) {
            arr[i * 2] = mattoni.get(i).getX();
            arr[i * 2 + 1] = mattoni.get(i).getY();
        }
        return arr;
    }

    public float[] getScalaPositionsFlat() {
        float[] arr = new float[scale.size() * 2];
        for (int i = 0; i < scale.size(); i++) {
            arr[i * 2] = scale.get(i).getX();
            arr[i * 2 + 1] = scale.get(i).getY();
        }
        return arr;
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    public List<MattoneTile> getMattoni(){
        return mattoni;
    }
    public List<ScalaTile> getScale(){
        return scale;
    }

    public List<Nemico> getNemici(){
        return nemici;
    }

    public Omino getOmino(){
        return omino;
    }
}