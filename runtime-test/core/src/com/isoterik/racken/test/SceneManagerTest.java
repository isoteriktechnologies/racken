package com.isoterik.racken.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.isoterik.racken.Component;
import com.isoterik.racken.GameObject;
import com.isoterik.racken.Scene;

public class SceneManagerTest extends Scene {
    public SceneManagerTest() {
        GameObject box = newSpriteObject(new Texture(Gdx.files.internal("badlogic.jpg")));

        addGameObject(box);

        box.addComponent(new Component() {
            @Override
            public void render() {
                System.out.println("render");
            }

            @Override
            public void resize(int newScreenWidth, int newScreenHeight) {
                System.out.println("resize");
            }
        });
    }
}
