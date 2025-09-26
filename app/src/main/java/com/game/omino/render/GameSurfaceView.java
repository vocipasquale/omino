package com.game.omino.render;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.opengl.GLSurfaceView;
import com.game.omino.engine.GameLoop;
import com.game.omino.input.TouchInput;

import static com.game.omino.utils.Constants.SCREEN_HEIGHT;
import static com.game.omino.utils.Constants.SCREEN_WIDTH;

public class GameSurfaceView extends GLSurfaceView {

    private final GameRenderer gameRenderer;
    private final GameLoop gameLoop;

    // Costruttore per creazione programmatica
    public GameSurfaceView(Context context) {
        super(context);
        gameRenderer = new GameRenderer(context.getAssets());
        gameLoop = new GameLoop(this);
        init(context);
    }

    // Costruttore richiesto per layout XML
    public GameSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        gameRenderer = new GameRenderer(context.getAssets());
        gameLoop = new GameLoop(this);
        init(context);
    }

    private void init(Context context) {
        setEGLContextClientVersion(2);
        setRenderer(gameRenderer);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        TouchInput.handleTouch(event);
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
