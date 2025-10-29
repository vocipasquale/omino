package com.game.omino.scene.tiles;

public class PortaTile extends Tile{
    private int id;
    public static final String PORTA_CHIUSA = "porta_chiusa";
    public static final String PORTA_APERTA = "porta_aperta";

    private String currentAn = PORTA_CHIUSA;

    private boolean aperta = false;

    public PortaTile(int x, int y, int w, int h) {
        super(x, y, w, h);
        chiudi();
    }

    public int getId() {
        return id;
    }

    public String getCurrentAnimation() {
        return currentAn;
    }

//    public void setCurrentAnimation(String currentAn) {
//        this.currentAn=currentAn;
//    }

    public boolean isAperta(){ return aperta; }

    public void chiudi(){
        aperta = false;
        currentAn = PORTA_CHIUSA;
    }
    public void apri(){
        aperta = true;
        currentAn = PORTA_APERTA;
    }
}
