package com.game.omino.input;

import android.view.MotionEvent;

public class TouchInput {
    static {
        System.loadLibrary("omino");
    }

    public static void handleTouch(MotionEvent event) {
        int action = event.getActionMasked();
        float x = event.getX();
        float y = event.getY();
        nativeTouch(x, y, action);
    }

    private static native void nativeTouch(float x, float y, int action);
}


