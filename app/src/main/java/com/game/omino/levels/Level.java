package com.game.omino.levels;

import com.game.omino.entities.*;
import com.game.omino.scene.tiles.*;

import java.util.List;

public abstract class Level {
    protected List<MattoneTile> mattoni;
    protected List<ScalaTile> scale;
    protected List<Nemico> nemici;

    protected Omino omino;

    protected int wScreen = 0;
    protected int hScreen = 0;

    abstract void setMetrics();


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