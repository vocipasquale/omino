package com.game.omino.utils;

public interface Constants {
    public static final int TILE_SIZE = 64;
    public static final int STEP = TILE_SIZE/3;
    public static final long REPEAT_DELAY_MS = 80L;  // intervallo movimento continuo (ms)
    public static final float GRAVITY = (-1)*STEP/5; //lascio il -1 per ricordarmi della "gravità" (-9.8f);
    public static final int TILES_4_ROW = 18;
    public static final int TILES_4_COLUMN = 29;
    public static final int SCREEN_WIDTH = TILES_4_ROW * TILE_SIZE;
    public static final int SCREEN_HEIGHT = TILES_4_COLUMN * TILE_SIZE;
    public static final int MIN_DIST_OVERLLAPPING_SCALA = TILE_SIZE/5; //+ o - il 10%

}
