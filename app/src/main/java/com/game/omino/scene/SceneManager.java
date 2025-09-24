package com.game.omino.scene;

public class SceneManager {
    private static Scene current;

    public static void setScene(Scene scene) {
        if (current != null) current.onExit();
        current = scene;
        if (current != null) current.onEnter();
    }

    public static void update() {
        if (current != null) current.update();
    }

    public static void render() {
        if (current != null) current.render();
    }
}


