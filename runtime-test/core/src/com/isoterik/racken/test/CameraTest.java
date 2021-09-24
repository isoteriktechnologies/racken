package com.isoterik.racken.test;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.isoterik.racken.Component;
import com.isoterik.racken.GameObject;
import com.isoterik.racken.Scene;
import com.isoterik.racken._2d.GameCamera2d;
import com.isoterik.racken._2d.components.renderer.TiledMapRenderer;
import com.isoterik.racken.asset.GameAssetsLoader;
import com.isoterik.racken.input.IKeyListener;
import com.isoterik.racken.input.KeyEventData;
import com.isoterik.racken.input.KeyTrigger;

public class CameraTest extends Scene {
    private TiledMapRenderer tiledMapRenderer;

    private GameCamera2d cam1;

    @Override
    protected void onCreate() {
        GameAssetsLoader gameAssetsLoader = racken.assets;
        gameAssetsLoader.enqueueAsset("maps/tiled.tmx", TiledMap.class);
        gameAssetsLoader.loadAssetsNow();
        tiledMapRenderer = new TiledMapRenderer(gameAssetsLoader.getAsset("maps/tiled.tmx", TiledMap.class),
                1/21f);

        racken.defaultSettings.VIEWPORT_WIDTH = tiledMapRenderer.mapWidth/2f;
        racken.defaultSettings.VIEWPORT_HEIGHT = tiledMapRenderer.mapHeight/2f;
        racken.defaultSettings.PIXELS_PER_UNIT = 21;
    }

    public CameraTest() {
        setBackgroundColor(Color.BLACK);

        GameObject tiledMapObject = GameObject.newInstance("TiledMap");
        addGameObject(tiledMapObject);
        tiledMapObject.addComponent(tiledMapRenderer);

        cam1 = new GameCamera2d(gameWorldUnits);
        addCamera(cam1);

        tiledMapRenderer.setRenderCamera(cam1);

        GameObject gameManager = GameObject.newInstance();

        input.addKeyListener(KeyTrigger.keyDownTrigger(Input.Keys.RIGHT).setPolled(true), (mappingName, keyEventData) -> {
            cam1.getCamera().translate(0.1f, 0, 0);
        });
    }
}


























