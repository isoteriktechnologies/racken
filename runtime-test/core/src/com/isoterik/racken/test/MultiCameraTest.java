package com.isoterik.racken.test;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.isoterik.racken.Component;
import com.isoterik.racken.GameObject;
import com.isoterik.racken.Scene;
import com.isoterik.racken._2d.GameCamera2d;
import com.isoterik.racken._2d.components.debug.BoxDebugRenderer;
import com.isoterik.racken._2d.components.renderer.TiledMapRenderer;
import com.isoterik.racken.asset.GameAssetsLoader;
import com.isoterik.racken.input.IKeyListener;
import com.isoterik.racken.input.KeyEventData;
import com.isoterik.racken.input.KeyTrigger;

public class MultiCameraTest extends Scene {
    private GameCamera2d cam1, cam2;

    public MultiCameraTest() {
        setBackgroundColor(Color.BLACK);
        setRenderDebugLines(true);

        float hw = gameWorldUnits.getWorldWidth()/2f;
        float hh = gameWorldUnits.getScreenHeight()/2f;

        cam1 = new GameCamera2d(new ExtendViewport(hw, hh, hw, hh));
        cam2 = new GameCamera2d(new ExtendViewport(hw, hh, hw, hh));
        addCamera(cam1);
        addCamera(cam2);
        removeMainCamera();

        GameObject gameManager = GameObject.newInstance();
        GameObject g1 = GameObject.newInstance("Cam1 Object");
        GameObject g2 = GameObject.newInstance("Cam2 Object");

        gameManager.transform.setSize(gameWorldUnits.getWorldWidth(), gameWorldUnits.getWorldHeight());
        g1.transform.setSize(hw, hh);
        g2.transform.setSize(hw, hh);
        g2.transform.setX(hw);

        g1.addComponent(new BoxDebugRenderer().setColor(Color.RED).setShapeType(ShapeRenderer.ShapeType.Filled));
        g2.addComponent(new BoxDebugRenderer().setColor(Color.BLUE).setShapeType(ShapeRenderer.ShapeType.Filled));

        gameManager.addChildren(g1, g2);
        addGameObject(gameManager);

        input.addKeyListener(KeyTrigger.keyDownTrigger(Input.Keys.RIGHT).setPolled(true), (mappingName, keyEventData) -> {
            cam1.getCamera().translate(0.1f, 0, 0);
        });
    }
}