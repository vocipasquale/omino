package com.game.omino.render;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import com.game.omino.engine.GameLoop;
import com.game.omino.input.TouchInput;

public class GameSurfaceView extends GLSurfaceView {

    private final GameRenderer gameRenderer;
    private final GameLoop gameLoop;

    public GameSurfaceView(Context context) {
        super(context);

        setEGLContextClientVersion(2);
        gameRenderer = new GameRenderer(context.getAssets());
        setRenderer(gameRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        gameLoop = new GameLoop(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        TouchInput.handleTouch(event);
        // facciamo il render al prossimo frame dopo l'input
        requestRender();
        return true;
    }

    public void onResumeLoop() {
        onResume();
        gameLoop.start();
    }

    public void onPauseLoop() {
        gameLoop.stop();
        onPause();
    }
}