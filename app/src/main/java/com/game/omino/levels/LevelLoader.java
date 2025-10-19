package com.game.omino.levels;

import android.content.Context;
import com.game.omino.entities.Nemico;
import com.game.omino.entities.Omino;
import com.game.omino.scene.tiles.MattoneTile;
import com.game.omino.scene.tiles.NullTile;
import com.game.omino.scene.tiles.ScalaTile;
import com.game.omino.scene.tiles.Tile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.game.omino.utils.Constants.*;

public  class LevelLoader {

    private static List<String> readAssetFile(Context context, String filename) {
        List<String> result = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open(filename))
            );
            String line;
            while ((line = reader.readLine()) != null) {
                result.add(line);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Level loadLevelFromFile(Context context, String levelNum) throws Exception {
        List<String> righe = readAssetFile(context, "levels/level_"+levelNum);
        Tile[][] matrix = new Tile[TILES_4_COLUMN][TILES_4_ROW];
        String riga = "";
        Omino omino= new Omino(0, 0, 0, 0);
        List<Nemico> nemici = new ArrayList<>();
        int m=1;

        for (int r=0; r<righe.size(); r++){
            riga = righe.get(r);
            for(int c=0; c<riga.length(); c++) {
                matrix[r][c] = decodeChar(riga.charAt(c), r, c, omino, nemici, m++);
            }
        }

        return new Level(omino, nemici, matrix);
    }

    private static Tile decodeChar(char c, int y, int x, Omino omino, List<Nemico> nemici, int m) throws Exception {
        if(c == '#'){
            return new ScalaTile(x*TILE_SIZE,y*TILE_SIZE,TILE_SIZE,TILE_SIZE);
        } else if(c == '@'){
            return new MattoneTile(m, x*TILE_SIZE,y*TILE_SIZE,TILE_SIZE,TILE_SIZE);
        }else if(c == '-'){
            return new NullTile(x*TILE_SIZE,y*TILE_SIZE,TILE_SIZE,TILE_SIZE);
        }else if(c == 'O'){
            omino.setX(x*TILE_SIZE);
            omino.setY(y*TILE_SIZE);
            omino.setH(TILE_SIZE);
            omino.setW(TILE_SIZE);
            return new NullTile(x*TILE_SIZE,y*TILE_SIZE,TILE_SIZE,TILE_SIZE);
        }else if(c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6'
                || c == '7' || c == '8' || c == '9'){
            nemici.add(new Nemico(c, x*TILE_SIZE,y*TILE_SIZE,TILE_SIZE,TILE_SIZE, omino));
            return new NullTile(x*TILE_SIZE,y*TILE_SIZE,TILE_SIZE,TILE_SIZE);
        }else {
            throw new Exception("LevelLoader: carattere non gestito --> "+c);
        }
    }
}
