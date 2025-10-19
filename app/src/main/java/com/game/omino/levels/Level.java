package com.game.omino.levels;

import android.util.Log;
import com.game.omino.entities.*;
import com.game.omino.scene.tiles.*;
import com.game.omino.utils.DataUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.game.omino.utils.Constants.*;

public class Level {
    private List<List<Tile>> tiles = new ArrayList<>(); //new Tile[SCREEN_HEIGHT][SCREEN_WIDTH];
    private List<ScalaTile> scale = new ArrayList<>(); //le scal in un livello non cambiano mai!
    Map<Integer, MattoneTile> mattoniErasing = new HashMap<>();
    private List<Nemico> nemici;
    private List<CassaTile> casse = new ArrayList<>();
    private PortaTile porta;


    private Omino omino;

    public Level(Omino omino, List<Nemico> nemici, Tile[][] tilesMatrix) {
        this.nemici = nemici;
        this.omino = omino;
        setTilesMatrix(tilesMatrix);
    }


    private void setTilesMatrix(Tile[][] tilesMatrix) {
        MattoneTile mattoneInstance;
        ScalaTile scalaInstance;
        NullTile nullInstance;
        PortaTile portaInstance;
        CassaTile cassaInstance;
        String riga = "";

        for (int r = 0; r < TILES_4_COLUMN; r++) {
            List<Tile> row = new ArrayList<>();
            for (int c = 0; c < TILES_4_ROW; c++) {
                if (tilesMatrix[r][c] instanceof MattoneTile) {
                    mattoneInstance = (MattoneTile) tilesMatrix[r][c];
                    row.add(mattoneInstance);
                } else if (tilesMatrix[r][c] instanceof ScalaTile) {
                    scalaInstance = (ScalaTile) tilesMatrix[r][c];
                    row.add(scalaInstance);
                    scale.add(scalaInstance);//temporaneo fino a quando non capisco come usare la matrice...
                } else if (tilesMatrix[r][c] instanceof NullTile) {
                    nullInstance = (NullTile) tilesMatrix[r][c];
                    row.add(nullInstance);
                }else if (tilesMatrix[r][c] instanceof PortaTile) {
                    portaInstance = (PortaTile) tilesMatrix[r][c];
                    porta = portaInstance;
                    row.add(portaInstance);
                }else if (tilesMatrix[r][c] instanceof CassaTile) {
                    cassaInstance = (CassaTile) tilesMatrix[r][c];
                    row.add(cassaInstance);
                    casse.add(cassaInstance);
                }
            }
            tiles.add(row);
        }


        for (List<Tile> rowLog : tiles) {
            riga = "";
            for (Tile t : rowLog) {
                if (t instanceof MattoneTile) {
                    riga += "@ (" + t.getY() + ", " + t.getX() + ")\t\t";
                } else if (t instanceof ScalaTile) {
                    riga += "# (" + t.getY() + ", " + t.getX() + ")\t\t";
                } else if (t instanceof NullTile) {
                    riga += "- (" + t.getY() + ", " + t.getX() + ")\t\t";
                }
            }
            Log.i("riga: ", riga);
        }


    }

//    public float[] getMattonePositionsFlat() {
//        List<MattoneTile> mattoni = getMattoni();
//        float[] arr = new float[mattoni.size() * 2];
//        for (int i = 0; i < mattoni.size(); i++) {
//            arr[i * 2] = mattoni.get(i).getX();
//            arr[i * 2 + 1] = mattoni.get(i).getY();
//        }
//        return arr;
//    }

    public DataUtil[] getMattonePositionsFlat(){
        List<MattoneTile> mattoni = getMattoni();
        DataUtil[] result = new DataUtil[mattoni.size()];
        MattoneTile matt;
        for (int m=0; m<mattoni.size(); m++){
            matt = mattoni.get(m);
            result[m]=new DataUtil(matt.getId(), matt.getX(), matt.getY(), matt.getCurrentAnimation());
        }
        return result;
    }

    public DataUtil[] getMattoneAnimationsFlat(){
        List<MattoneTile> mattoni = getMattoniErasingList();
        DataUtil[] result = new DataUtil[mattoni.size()];
        MattoneTile matt;
        for (int m=0; m<mattoni.size(); m++){
            matt = mattoni.get(m);
            result[m]=new DataUtil(matt.getId(), matt.getX(), matt.getY(), matt.getCurrentAnimation());
        }
        return result;
    }

    private List<MattoneTile> getMattoniErasingList() {
       return new ArrayList<>(mattoniErasing.values());
    }


    public float[] getScalaPositionsFlat() {
        float[] arr = new float[scale.size() * 2];
        for (int i = 0; i < scale.size(); i++) {
            arr[i * 2] = scale.get(i).getX();
            arr[i * 2 + 1] = scale.get(i).getY();
        }
        return arr;
    }

    public float[] getCassaPositionsFlat() {
        float[] arr = new float[casse.size() * 2];
        for (int i = 0; i < casse.size(); i++) {
            arr[i * 2] = casse.get(i).getX();
            arr[i * 2 + 1] = casse.get(i).getY();
        }
        return arr;
    }

    public DataUtil[] getNemiciPositionsFlat(){
        DataUtil[] result = new DataUtil[nemici.size()];
        Nemico nem;
        for (int n=0; n<nemici.size(); n++){
            nem = nemici.get(n);
            result[n]=new DataUtil(nem.getId(), nem.getX(), nem.getY(), nem.getCurrentAnimation());
        }
        return result;
    }

    public PortaTile getPorta(){
        return porta;
    }

    public List<Tile> getRow(int y) {
        return tiles.get(y / TILE_SIZE);
    }

    public List<Tile> getColumn(int x) {
        List<Tile> col = new ArrayList<>();
        for (List<Tile> row : tiles) {
            col.add(row.get(x / TILE_SIZE));
        }
        return col;
    }

    /**
     * restituisce le quattro tile che contengono la tile (y, x).
     *
     * @param y
     * @param x
     * @return
     */
    public List<Tile> getArea(int y, int x) {
        List<Tile> area = new ArrayList<>();
        area.add(tiles.get(y / TILE_SIZE).get(x / TILE_SIZE));
        area.add(tiles.get(y / TILE_SIZE).get((x + TILE_SIZE) / TILE_SIZE));
        area.add(tiles.get(((y + TILE_SIZE) / TILE_SIZE)).get(x / TILE_SIZE));
        area.add(tiles.get(((y + TILE_SIZE) / TILE_SIZE)).get((x + TILE_SIZE) / TILE_SIZE));
        return area;
    }

    public boolean isFreeArea(int y, int x) {
        boolean result = true;
        List<Tile> area = getArea(y, x);
        for (int t = 0; t < area.size() && result; t++) {
            result = area.get(t) instanceof NullTile;
        }
        return result;
    }


    public Tile[] getTilesDown(Entity entity) {
        Tile[] result = new Tile[2];
        List<Tile> row = getRow((entity.getY() + entity.getH()));
        int c = 0;

        for (int t = 0; t < row.size() && c < 2; t++) {
            if (Math.abs(entity.getX() - row.get(t).getX()) < TILE_SIZE) {
                result[c++] = row.get(t);
            }
        }

        return result;
    }

    /**
     * restituisce due ScaleTile sovrapposte da entity.
     *
     * @param entity
     * @return
     */
    public Tile[] getTilesOverlapping(Entity entity) {
        int t = 0;
        Tile[] result = new Tile[2];
        List<Tile> area = getArea(entity.getY(), entity.getX());
        for (int i = 0; i < area.size(); i++) {
            if (area.get(i) instanceof ScalaTile
                    && ((Math.abs(entity.getX() - area.get(i).getX()) < TILE_SIZE / 5)
                    || (Math.abs(entity.getX() + TILE_SIZE - area.get(i).getX()) < TILE_SIZE / 5))) {
                result[t++] = area.get(i);
            }
        }
        return result;
    }


    /**
     * dati y e x in input restituisce la Tile solo se y e x
     * sono le esatte coordinate della tile, altrimenti null.
     *
     * @param y
     * @param x
     * @return
     */
    public Tile getExactTile(int y, int x) {
        if (y % TILE_SIZE == 0 && x % TILE_SIZE == 0) { //y e x multipli di TILE_SIZE
            return tiles.get(y).get(x);
        }
        return null;
    }


    public Omino getOmino() {
        return omino;
    }

    private List<MattoneTile> getMattoni() {
        List<MattoneTile> result = new ArrayList<>();
        tiles.forEach(r -> {
            r.forEach(c -> {
                if (c instanceof MattoneTile) {
                    result.add((MattoneTile) c);
                }
            });
        });
        return result;
    }

    public MattoneTile getMattoneErasing(Integer id) {
        return mattoniErasing.get(id);
    }

    public MattoneTile addMattoneErasing(Integer id, MattoneTile mattone) {
        return mattoniErasing.put(id, mattone);
    }

    public MattoneTile removeMattoneErasing(Integer id){
        return mattoniErasing.remove(id);
    }

    public List<Nemico> getNemici() {
        return nemici;
    }


}