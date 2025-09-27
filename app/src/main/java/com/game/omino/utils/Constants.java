package com.game.omino.utils;

public interface Constants {
    public static final int TILE_SIZE = 64;
    public static final int STEP = TILE_SIZE/3;
    public static final long REPEAT_DELAY_MS = 80L;  // intervallo movimento continuo (ms)
    public static final float GRAVITY = -9.8f;
    public static final int SCREEN_WIDTH = 18 * TILE_SIZE;
    public static final int SCREEN_HEIGHT = 29 * TILE_SIZE;

}
