package com.game.omino.levels;

import android.content.Context;

public class LevelManager {
    private static LevelManager instance;
    private static Context context;

    private static int CURRENT_LEVEL_NUM = 0;

    private LevelManager(Context context){
        this.context = context;
    }

    public static LevelManager getInstance(Context context){
        if (instance == null){
            instance = new LevelManager(context);
        }
        return instance;
    }

    public static void stopLevel(){

    }

    public static Level startNextLevel()  throws Exception{
        CURRENT_LEVEL_NUM++;
        return LevelLoader.loadLevelFromFile(context, ""+CURRENT_LEVEL_NUM);
    }

    public static Level reloadCurrentLevel()  throws Exception{
        return LevelLoader.loadLevelFromFile(context, ""+CURRENT_LEVEL_NUM);
    }


    public void reset() {
        CURRENT_LEVEL_NUM=0;
    }
}
