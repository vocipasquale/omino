package com.game.omino.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public  class LevelLoader {

    public String readAssetFile(Context context, String filename) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open(filename))
            );
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
