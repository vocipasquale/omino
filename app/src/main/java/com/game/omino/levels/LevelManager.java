package com.game.omino.levels;

import android.content.Context;

public class LevelManager {

    private static Level currentLevel;
    private static String CURRENT_LEVEL_NUM = "0";

    public static Level startLevel(Context context) throws Exception {
        if(currentLevel == null){
            currentLevel = LevelLoader.loadLevelFromFile(context, CURRENT_LEVEL_NUM);
        }

        return currentLevel;
    }

    public static void stopLevel(){

    }

    public static void startNextLevel(){

    }


    private void loadLevel(String level){

    }
}
