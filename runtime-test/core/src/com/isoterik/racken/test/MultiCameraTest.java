package com.isoterik.racken.test;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.isoterik.racken.GameObject;
import com.isoterik.racken.Scene;
import com.isoterik.racken._2d.GameCamera2d;
import com.isoterik.racken._2d.components.debug.BoxDebugRenderer;
import com.isoterik.racken.input.KeyTrigger;

public class MultiCameraTest extends Scene {
    private GameCamera2d cam1, cam2;

    @Override
    protected void onCreate() {
        racken.defaultSettings.VIEWPORT_WIDTH = 1024;
        racken.defaultSettings.VIEWPORT_HEIGHT = 640;
    }

    public MultiCameraTest() {
        setBackgroundColor(Color.BLACK);
        setRenderDebugLines(true);

        float hw = gameWorldUnits.getWorldWidth();
        float hh = gameWorldUnits.getWorldHeight()/2f;

        cam1 = new GameCamera2d(new ExtendViewport(hw, hh, hw, hh));
        cam2 = new GameCamera2d(new ExtendViewport(hw, hh));
        cam1.setScreenBoundsRatio(0, 0, 1, .5f);
        cam2.setScreenBoundsRatio(0, .5f, 1, .5f);

        addCamera(cam1);
        addCamera(cam2);
        removeMainCamera();

        GameObject g1 = GameObject.newInstance("Cam1 Object");
        GameObject g2 = GameObject.newInstance("Cam2 Object");
        GameObject g3 = GameObject.newInstance("Cam2 Object2");

        g1.transform.setSize(hw, hh);
        g2.transform.setSize(hw, hh);
        g3.transform.setSize(1, 1);

        g1.addComponent(new BoxDebugRenderer().setColor(Color.RED).setShapeType(ShapeRenderer.ShapeType.Filled));
        g2.addComponent(new BoxDebugRenderer().setColor(Color.BLUE).setShapeType(ShapeRenderer.ShapeType.Filled));
        g3.addComponent(new BoxDebugRenderer().setColor(Color.GREEN).setShapeType(ShapeRenderer.ShapeType.Filled));

        g1.forEachComponent(component -> component.setRenderCamera(cam1));
        g2.forEachComponent(component -> component.setRenderCamera(cam2));
        g3.forEachComponent(component -> component.setRenderCamera(cam2));

        addGameObject(g1);
        addGameObject(g2);
        addGameObject(g3);

        input.addKeyListener(KeyTrigger.keyDownTrigger(Input.Keys.UP).setPolled(true), (mappingName, keyEventData) -> {
            cam2.getCamera().translate(0f, 0.1f, 0);
        });
    }
}