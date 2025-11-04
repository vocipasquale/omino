package com.game.omino.engine;

import android.os.Handler;
import android.util.Log;
import com.game.omino.entities.Entity;
import com.game.omino.entities.Nemico;
import com.game.omino.entities.Omino;
import com.game.omino.levels.Level;
import com.game.omino.scene.tiles.*;

import java.util.List;

import static com.game.omino.utils.Constants.*;

public class GameWorld {

    private static GameWorld instance;

    private Level level;

    private Omino omino;

    private static int SCORE = 0;

    private static int OMINO_LIVES = 3;
    private GameEventListener listener;


    public static GameWorld getInstance() {
        if (instance == null) instance = new GameWorld();
        return instance;
    }

    public void setGameEventListener(GameEventListener listener) {
        this.listener = listener; // Imposta il listener
    }

    public static void reset() {
        SCORE = 0;
        OMINO_LIVES = 3;
    }

    public Omino getOmino() {
        return omino;
    }

    public String getOminoLives(){
        return ""+ OMINO_LIVES;
    }

    public Level getLevel() {
        return level;
    }

    public void update(float deltaTime) {
        //supera confine inferiore...
        if (omino.getY() > SCREEN_HEIGHT - omino.getH()) {
            omino.setY(SCREEN_HEIGHT - omino.getH());
            return;
        }
        //sfora a sinistra...
        if(omino.getX()<-TILE_SIZE+TOLERANCE){
            omino.setX(SCREEN_WIDTH-TILE_SIZE);
        }

        //sfora a destra
        if(omino.getX()>SCREEN_WIDTH){
            omino.setX(0);
        }
        //applico gravità se l'area è libera...
        gravita(omino);

        //preleva cassa
        CassaTile cassa = checkOverCassa(omino);
        if (cassa != null) {
            level.removeCassa(cassa);
            omino.addCassa(cassa);
            SCORE += CASSA_SCORE;
            listener.onScoreUpdate(getOminoScore());
        }

        //aumento di una vita...
        if (SCORE > 0 && SCORE % CASSA_SCORE_LIFE == 0) {
            OMINO_LIVES++;
            listener.onLiveUpdate(getOminoLives());
        }


        for (Nemico n : level.getNemici()) {
            gravita(n);
            n.avvicinaOmino();

            //preleva cassa
            cassa = checkOverCassa(n);
            if (cassa != null) {
                level.removeCassa(cassa);
                n.setCassa(cassa);
            }

            //omino incontra un nemico
            if (Math.abs(omino.getY() - n.getY()) < TOLERANCE && Math.abs(omino.getX() - n.getX()) < TOLERANCE) {
                entityKilled(omino);
            }
        }


        if (level.getNumCasse() == omino.numCasse()) {//omino ha raccolto tutte le casse del livello?
            if (level.getPorta() != null) {//se non è l'ultimo livello
                level.getPorta().apri();
                if (Math.abs(level.getPorta().getY() - omino.getY()) < TOLERANCE && Math.abs(omino.getX() - level.getPorta().getX()) < TOLERANCE) {//se omino entra nella porta aperta...

                    listener.onLevelPassed();
                }
            } else {//omino ha raccolto tutte le casse ed ha finito il gioco!
                listener.onGameFinished();
            }

        }


    }

//    private boolean isLevelPassed() {
//        PortaTile p = level.getPorta();
//        return p.isAperta()
//                && Math.abs(p.getY() - omino.getY()) < TOLERANCE && Math.abs(omino.getX() - p.getX()) < TOLERANCE;
//    }

    private CassaTile checkOverCassa(Entity entity) {
        List<CassaTile> casse = level.getCasse();
        CassaTile cassa = null;
        for (int c = 0; c < casse.size() && cassa == null; c++) {
            cassa = casse.get(c);
            if (entity.getY() != cassa.getY() || Math.abs(entity.getX() - cassa.getX()) > TOLERANCE) {
                cassa = null;
            }
        }
        return cassa;
    }


    private void gravita(Entity entity) {
        List<Tile> area = level.getArea(entity.getY() + entity.getH(), entity.getX());
        Tile t;

        boolean trovato = false;
        for (int i = 0; i < 2 && !trovato; i++) {
            t = area.get(i);
            if ((t instanceof NullTile || t instanceof CassaTile) && level.getNemiciDown(entity.getY(), entity.getX())==null) {
                if (Math.abs(t.getX() - entity.getX()) <= TILE_SIZE / 4) {
                    entity.setX(t.getX()); //allineamento
                    entity.setFalling(true);
                    entity.setY(entity.getY() + GRAVITY);
                    trovato = true;
                }
            } else {//Mattonetile o ScalaTile
                if (entity.isFalling()) {//se è in caduta in prossimità del suolo
                    entity.setY(t.getY() - entity.getH());
                    entity.setFalling(false);
                }
            }
        }

        if(entity.isFalling()) {
            Nemico n = level.getNemiciDown(entity.getY(), entity.getX());
            if(n != null){
                entity.setY(n.getY() - entity.getH());
                entity.setFalling(false);
            }
        }
    }

    private void entityKilled(Entity entity) {
        if (entity instanceof Omino) {
            // OMINO DEAD!!!!!
            Log.w("GameWorld", "OMINO MOOOOORTOOOOO!!!!!!!.");
            OMINO_LIVES--;
            listener.onLiveUpdate(""+ OMINO_LIVES);

            //effetto sonoro...

            if (OMINO_LIVES <= 0) {
                //GAME OVER
                listener.onGameOver();
            } else {
                omino.reborn();
            }

        } else if (entity instanceof Nemico) {
            Nemico n = (Nemico) entity;

            if (n.getCassa() != null) {
                n.getCassa().setY(n.getY()-n.getH());
                n.getCassa().setX(n.getX());
                level.addCassa(n.getCassa());
                n.setCassa(null);
            }

            (n).reborn(); //dopo il ripristino dell'eventuale cassa!!!
        }

    }

    public void loadLevel(Level level) {
        this.level = level;
        this.omino = level.getOmino();
    }


    public void cancelTile(boolean dx) {
        List<Tile> rowDown = level.getRow(omino.getY() + omino.getH());//riga sotto omino
        MattoneTile m = null;
        for (Tile t : rowDown) {
            if (t instanceof MattoneTile) {
                if (dx) {
                    if (Math.abs(omino.getX() + omino.getW() - t.getX()) <= TOLERANCE) {
                        m = (MattoneTile) t;
                    }
                } else {
                    if (Math.abs(t.getX() + t.getW() - omino.getX()) <= TOLERANCE) {
                        m = (MattoneTile) t;
                    }
                }
            }
        }

        if (m != null) {
            if (level.getRow(omino.getY()).get(m.getX() / TILE_SIZE) instanceof NullTile) {
                rowDown.set(m.getX() / TILE_SIZE, new NullTile(m.getX(), m.getY(), m.getW(), m.getH()));
                m.setCurrentAnimation(dx?MattoneTile.MATTONE_ERASING_DX_TX:MattoneTile.MATTONE_ERASING_SX_TX);
                level.addMattoneErasing(m.getId(), m);

                Handler handler = new Handler();
                MattoneTile finalM = m;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //ripristino mattone...
                        finalM.setCurrentAnimation(MattoneTile.MATTONE_TX);
                        level.addMattoneErasing(finalM.getId(), finalM);
                        rowDown.set(finalM.getX() / TILE_SIZE, finalM);

                        if (checkEntityInside(omino, finalM)) {
                            entityKilled(omino);
                        }

                        for (Nemico n : level.getNemici()) {
                            if (checkEntityInside(n, finalM)) {
                                entityKilled(n);
                            }
                        }

                    }

                    private boolean checkEntityInside(Entity entity, MattoneTile mattone) {
                        return entity.getY() == mattone.getY() && entity.getX() == mattone.getX();
                    }
                }, SEC_ERASE_MATTONE);
            }
        }

    }

    public String getOminoScore() {
        return ""+SCORE;
    }
}
