package com.game.omino.entities;

import com.game.omino.engine.GameWorld;
import com.game.omino.scene.tiles.MattoneTile;
import com.game.omino.scene.tiles.ScalaTile;
import com.game.omino.scene.tiles.Tile;

import java.util.List;

import static com.game.omino.utils.Constants.*;
import static com.game.omino.utils.Constants.STEP;

public class Nemico extends Entity {

    private static final String IDLE_DX_TX = "nemico_idle_dx";
    private static final String IDLE_SX_TX = "nemico_idle_sx";
    private static final String IDLE_UP_TX = "nemico_idle_up";
    private static final String RUN_DX_TX = "nemico_run_dx";
    private static final String RUN_SX_TX = "nemico_run_sx";
    private static final String RUN_UP_TX = "nemico_run_up";
    private static final String RUN_DOWN_TX = "nemico_run_down";
    private static final String FALLING_TX = "nemico_falling";
    private static final String NULL_TX = "sprite_null";

    private int id=0;


    private Omino omino; // Riferimento all'istanza di Omino
    private boolean dx = true;


    public Nemico(char charId, int x, int y, int w, int h, Omino omino) {
        super("", x, y, w, h);
        id = Character.getNumericValue(charId);
        this.omino = omino; // Inizializza il riferimento a Omino

        currentAnimation = IDLE_DX_TX;
    }

    public int getId(){
        return id;
    }

    @Override
    public void su() {
        if (!falling) {
            setTilesOverlapping(GameWorld.getInstance().getLevel().getTilesOverlapping(this));
            if (tilesOverlapping[0] != null) {
                x = tilesOverlapping[0].getX(); //tilesOverlapping[0] scalaTile superiore
                if (GameWorld.getInstance().getLevel().isFreeArea(y - STEP_NEMICO, tilesOverlapping[0].getX())) {
                    y = tilesOverlapping[0].getY() - h;
                } else {
                    y -= STEP_NEMICO;
                }
                currentAnimation = RUN_UP_TX;
            }
        } else {
            currentAnimation = FALLING_TX;
        }
    }

    @Override
    public void giu() {
        if (!falling) {
            List<Tile> area = GameWorld.getInstance().getLevel().getArea(y + h + STEP_NEMICO, x + (w / 2));
            Tile t = area.get(0); //nullsafe!
            if (Math.abs(x - t.getX()) < TILE_SIZE / 5) {
                if (!(t instanceof MattoneTile)) {//se non è mattone può essere solo ScalaTile o NullTile
                    x = t.getX(); //allineamento...
                    y += STEP_NEMICO;
                    currentAnimation = RUN_DOWN_TX;
                }else{
                    y = t.getY()-h; //mi fermo sul mattone
                }
            }
        } else {
            currentAnimation = FALLING_TX;
        }
    }

    @Override
    public void sinistra() {
        if (!falling) {
            List<Tile> area = GameWorld.getInstance().getLevel().getArea(y+h/2, x - STEP_NEMICO);
            if (area.get(0) instanceof MattoneTile) {
                x = area.get(0).getX() + w;
            } else {
                x -= STEP_NEMICO;
            }
            currentAnimation = RUN_SX_TX;
            dx = false;
        } else {
            currentAnimation = FALLING_TX;
        }

    }

    @Override
    public void destra() {
        if (!falling) {
            List<Tile> area = GameWorld.getInstance().getLevel().getArea(y+h/2, x + w + STEP_NEMICO);
            if (area.get(0) instanceof MattoneTile) {
                x = area.get(0).getX() - w;
            } else {
                x += STEP_NEMICO;
            }
            currentAnimation = RUN_DX_TX;
            dx = true;
        } else {
            currentAnimation = FALLING_TX;
        }
    }

    // Puoi implementare metodi per fermare il nemico se necessario
    @Override
    public void stopSu() {
        // Logica per fermare il movimento verso Omino
    }

    @Override
    public void stopGiu() {
        // Logica per fermare il movimento verso Omino
    }

    @Override
    public void stopDestra() {
        // Logica per fermare il movimento verso Omino
    }

    @Override
    public void stopSinistra() {
        // Logica per fermare il movimento verso Omino
    }

    // Metodo per aggiornare la posizione del nemico
//    public void aggiorna() {
//        int diff = omino.getY()-this.getY();
//        ScalaTile scala = null;
//        if (diff == 0) {//sullo stesso piano
//            if (omino.getX() < this.getX()) {
//                sinistra();
//            } else { //if (omino.getX() > this.getX()) {
//                destra();
//            }
//        }else if (diff < 0){//su piani diversi: Omino sopra
//            scala=cercaScala(1);//cerca scala per salire
//
//            if(scala!=null) {
//                if (scala.getX() > x) {
//                    destra();
//                } else if (scala.getX() < x) {
//                    sinistra();
//                } else {//sopra la scala o sovrapposto
//                    su();
//                }
//            }
//        }else {//su piani diversi: Omino sotto
//            scala=cercaScala(0);//cerca scala per scendere
//
//            if(scala!=null) {
//                if (scala.getX() > x) {
//                    destra();
//                } else if (scala.getX() < x) {
//                    sinistra();
//                } else {//sopra la scala o sovrapposto
//                    giu();
//                }
//            }
//        }
//
//    }

    public void aggiorna() {
        int diff = omino.getY() - this.getY();
        ScalaTile scala = null;

        if (diff == 0) { // sullo stesso piano
            // Muovi verso Omino
            if (omino.getX() < this.getX()) {
                sinistra();
            } else {
                destra();
            }
        } else { // su piani diversi
            // Determina la direzione per cercare la scala
            int direction = (diff < 0) ? 1 : 0; // 1 per salire, 0 per scendere
            scala = cercaScalaVicina(direction);

            if (scala != null) {
                // Muovi verso la scala o sali/scendi
                muoviVersoScala(scala, direction);
            }else{
                //RANDOM!!!!
            }
        }
    }

    private void muoviVersoScala(ScalaTile scala, int dir) {

     //   if (scala.getY() - y == 0) { //scala e nemico sullo stesso piano
            if (scala.getX() > x) {
                destra();
            } else if (scala.getX() < x) {
                sinistra();
            } else { // sopra la scala o sovrapposto
                if (dir==1) {
                    su(); // sali
                } else {
                    giu(); // scendi
                }
            }
//        }else{
//
//            if (dir==1) {
//                su(); // sali
//            } else {
//                giu(); // scendi
//            }
//        }
    }

    private ScalaTile cercaScalaVicina(int dir) {//dir==0 --> giù; dir==1 --> su
        List<Tile> row;
        ScalaTile scala=null;
        if(dir==0){
            row = GameWorld.getInstance().getLevel().getRow(y+h);
        }else {
            row = GameWorld.getInstance().getLevel().getRow(y%TILE_SIZE==0?y:y+h); //deve considerare la scala del proprio livello, se sta "uscendo da una scala in salita fa casini
        }

        for (Tile t: row){
            if(t instanceof ScalaTile){
                if(scala==null || (Math.abs(t.getX()-x)<Math.abs(scala.getX()-x))) {
                    scala = (ScalaTile) t;
                }
            }
        }
        return scala;
    }
}