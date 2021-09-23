package com.isoterik.racken.test;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.isoterik.racken.GameObject;
import com.isoterik.racken.Scene;
import com.isoterik.racken._2d.GameCamera2d;
import com.isoterik.racken._2d.components.debug.BoxDebugRenderer;
import com.isoterik.racken._2d.components.renderer.SpriteRenderer;
import com.isoterik.racken.util.GameWorldUnits;

public class CameraTest extends Scene {
    public CameraTest() {
        Texture texture = new Texture("badlogic.jpg");
        GameObject obj1 = newSpriteObject(texture);

        addGameObject(obj1);

        GameWorldUnits gw = new GameWorldUnits(1280f, 360f, 100f);
        GameCamera2d cam1 = new GameCamera2d(gw);

        GameObject cam1Obj = GameObject.newInstance("Cam1");
        cam1Obj.addComponent(cam1);
        addGameObject(cam1Obj);

        GameCamera2d cam2 = new GameCamera2d(gw);
        cam2.getCamera().position.set(0, gw.toWorldUnit(360f), 0);

        GameObject cam2Obj = GameObject.newInstance("Cam2");
        cam2Obj.addComponent(cam2);
        addGameObject(cam2Obj);

        GameObject cam1Marker = GameObject.newInstance();
        cam1Marker.transform.setSize(gw.getWorldWidth(), gw.getWorldHeight()/2f);
        cam1Marker.addComponent(new BoxDebugRenderer());

        GameObject cam2Marker = GameObject.newInstance();
        cam2Marker.transform.setSize(gw.getWorldWidth(), gw.getWorldHeight()/2f);
        cam2Marker.transform.position.set(cam2.getCamera().position);
        cam2Marker.addComponent(new BoxDebugRenderer().setColor(Color.BLUE));

        addGameObject(cam1Marker);
        addGameObject(cam2Marker);

        obj1.getComponent(SpriteRenderer.class).setGameCamera(cam1);
        setRenderDebugLines(true);
    }

    @Override
    protected void onCreate() {
        //racken.defaultSettings.VIEWPORT_HEIGHT = 720;
    }
}
