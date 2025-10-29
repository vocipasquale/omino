package com.game.omino.utils;

public interface Constants {
    public static final int TILE_SIZE = 64;
    public static final int STEP = TILE_SIZE/3;
    public static final int STEP_NEMICO = 2;
    public static final long REPEAT_DELAY_MS = 80L;  // intervallo movimento continuo (ms)
    public static final int GRAVITY = STEP/5;
    public static final int TILES_4_ROW = 18;
    public static final int TILES_4_COLUMN = 27;
    public static final int SCREEN_WIDTH = TILES_4_ROW * TILE_SIZE;
    public static final int SCREEN_HEIGHT = TILES_4_COLUMN * TILE_SIZE;
    public static final int TOLERANCE = 20; //20 pixel
    public static final long SEC_ERASE_MATTONE = 10000; //numero di millisecondi per il quale il mattone deve rimanere cancellato
    public static final int CASSA_SCORE = 100;
    public static final int CASSA_SCORE_LIFE = CASSA_SCORE * 100;

}
