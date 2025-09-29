package com.game.omino.levels;

import android.content.Context;
import com.game.omino.engine.GameLoop;

public class LevelManager {

    private final Context context;
    private static Level currentLevel;

    public LevelManager(Context context) {
        this.context = context;

        /**
         * gestire la selezione dei livelli
         */
        loadLevel("0");

    }

    public static void startLevel(){

    }

    public static void stopLevel(){

    }

    public static void startNextLevel(){

    }

    public static Level getCurrentLevel() {
        return currentLevel;
    }


    private void loadLevel(String level){
        
    }
}
