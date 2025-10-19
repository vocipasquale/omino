package com.game.omino.scene.tiles;

public class PortaTile extends Tile{
    private int id;
    public static final String PORTA_CHIUSA = "porta_chiusa";
    public static final String PORTA_APERTA = "porta_aperta";

    private String currentAn = PORTA_CHIUSA;

    public PortaTile(int x, int y, int w, int h) {
        super(x, y, w, h);
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
