package com.game.omino.scene.tiles;

public class MattoneTile extends Tile{
    private int id;
    public static final String MATTONE_TX = "mattone";
    public static final String MATTONE_ERASING_DX_TX = "mattone_erasing_dx";
    public static final String MATTONE_ERASING_SX_TX = "mattone_erasing_sx";

    private String currentAn = MATTONE_TX;


    public MattoneTile(int id, int x, int y, int w, int h) {
        super(x, y, w, h);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getCurrentAnimation() {
        return currentAn;
    }

    public void setCurrentAnimation(String currentAn) {
        this.currentAn=currentAn;
    }
}
