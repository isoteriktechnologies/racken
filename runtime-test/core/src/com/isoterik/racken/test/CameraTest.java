package com.isoterik.racken.test;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.isoterik.racken.GameObject;
import com.isoterik.racken.Scene;
import com.isoterik.racken._2d.components.renderer.TiledMapRenderer;
import com.isoterik.racken.asset.GameAssetsLoader;

public class CameraTest extends Scene {
    private TiledMapRenderer tiledMapRenderer;

    @Override
    protected void onCreate() {
//        GameAssetsLoader gameAssetsLoader = racken.assets;
//        gameAssetsLoader.enqueueAsset("maps/tiled.tmx", TiledMap.class);
//        gameAssetsLoader.loadAssetsNow();
//        tiledMapRenderer = new TiledMapRenderer(gameAssetsLoader.getAsset("maps/tiled.tmx", TiledMap.class),
//                1/21f);
//
//        racken.defaultSettings.VIEWPORT_WIDTH = tiledMapRenderer.mapWidth/2f;
//        racken.defaultSettings.VIEWPORT_HEIGHT = tiledMapRenderer.mapHeight/2f;
//        racken.defaultSettings.PIXELS_PER_UNIT = 100;
    }

    public CameraTest() {
        setBackgroundColor(Color.BLACK);

//        GameObject tiledMapObject = GameObject.newInstance("TiledMap");
//        addGameObject(tiledMapObject);
//        tiledMapObject.addComponent(tiledMapRenderer);

        GameObject object = newSpriteObject(new Texture("badlogic.jpg"));
        addGameObject(object);
    }
}


























