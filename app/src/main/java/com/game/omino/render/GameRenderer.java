package com.game.omino.render;

import android.content.res.AssetManager;
import android.opengl.GLSurfaceView;

import java.util.concurrent.atomic.AtomicBoolean;

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
    public void onSurfaceCreated(javax.microedition.khronos.opengles.GL10 gl, javax.microedition.khronos.egl.EGLConfig config) {
        if (!initialized.get()) {
            nativeInit(assetManager);
            initialized.set(true);
        }
    }

    @Override
    public void onSurfaceChanged(javax.microedition.khronos.opengles.GL10 gl, int width, int height) {
        nativeResize(width, height);
    }

    @Override
    public void onDrawFrame(javax.microedition.khronos.opengles.GL10 gl) {
        nativeUpdate();
        nativeRender();
    }

    // JNI stubs
    private static native void nativeInit(AssetManager assetManager);
    private static native void nativeResize(int width, int height);
    private static native void nativeUpdate();
    private static native void nativeRender();
}


