package com.game.omino.engine;

import android.opengl.GLSurfaceView;

public class GameLoop implements Runnable {

    private static final int TARGET_FPS = 60;
    private static final long FRAME_TIME_MS = 1000L / TARGET_FPS;

    private final GLSurfaceView surfaceView;

    private Thread loopThread;
    private volatile boolean running = false;

    public GameLoop(GLSurfaceView surfaceView) { this.surfaceView = surfaceView; }

    public void start() {
        if (running) return;
        running = true;
        loopThread = new Thread(this, "GameLoop");
        loopThread.start();
    }

    public void stop() {
        running = false;
        if (loopThread != null) {
            try {
                loopThread.join();
            } catch (InterruptedException ignored) {}
        }
    }

    @Override
    public void run() {
        long last = System.currentTimeMillis();
        while (running) {
            long now = System.currentTimeMillis();
            long elapsed = now - last;
            if (elapsed < FRAME_TIME_MS) {
                try { Thread.sleep(FRAME_TIME_MS - elapsed); } catch (InterruptedException ignored) {}
            }
            last = System.currentTimeMillis();

            surfaceView.requestRender();
        }
    }
}


