package com.game.omino.render;

import android.content.res.AssetManager;
import android.opengl.GLSurfaceView;
import com.game.omino.engine.GameWorld;
import com.game.omino.entities.Omino;
import com.game.omino.scene.*;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.egl.EGLConfig;

public class GameRenderer implements GLSurfaceView.Renderer {

    static {
        System.loadLibrary("omino");
    }


    private final AssetManager assetManager;
    private final AtomicBoolean initialized = new AtomicBoolean(false);

    public GameRenderer(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        if (!initialized.get()) {
            nativeInit(assetManager);
            initialized.set(true);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        nativeResize(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        float deltaTime = 1f / 60f; // fisso per ora

        // logica e rendering passano dal SceneManager
        SceneManager.update(deltaTime);
        SceneManager.render();
    }

    public static void renderWorld(GameWorld world) {
        // se la scena è PlayScene -> world aggiornato, passo a C++
        if (SceneManager.getCurrent() instanceof PlayScene) {
            // Passaggio dati a C++
            Omino o = GameWorld.getInstance().getOmino();
            nativeSetOminoPositions(o.getX(), o.getY(), o.getCurrentAnimation());
            nativeSetMattonePositions(GameWorld.getInstance().getLevel().getMattonePositionsFlat());
            nativeSetScalaPositions(GameWorld.getInstance().getLevel().getScalaPositionsFlat());

            // render nativo OpenGL
            nativeRender();
        }
    }

    // JNI stubs
    private static native void nativeInit(AssetManager assetManager);
    private static native void nativeResize(int width, int height);
    private static native void nativeRender();

    // JNI helpers da implementare in C++:
    private static native void nativeSetOminoPositions(float x, float y, String currentAnimation);
    private static native void nativeSetMattonePositions(float[] positions);
    private static native void nativeSetScalaPositions(float[] positions);
}


