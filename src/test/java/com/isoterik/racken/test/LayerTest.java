package com.isoterik.racken.test;

import com.badlogic.gdx.utils.Array;
import com.isoterik.racken.GameObject;
import com.isoterik.racken.Layer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LayerTest {
    Layer layer = new Layer("layer1");

    @Test
    public void testAddAndRemoveGameObjects() {
        layer.clear();

        layer.addGameObject(GameObject.newInstance());
        layer.addGameObject(GameObject.newInstance());

        assertEquals(2, layer.size());

        layer.removeGameObject(layer.getGameObjects(null).first());

        assertEquals(1, layer.size());
    }

    @Test
    public void testFetchLayerGameObjects() {
        layer.clear();

        layer.addGameObject(GameObject.newInstance());
        layer.addGameObject(GameObject.newInstance());

        assertEquals(2, layer.size());

        Array<GameObject> gameObjects = null; // this is intentional
        gameObjects = layer.getGameObjects(gameObjects);

        assertEquals(2, gameObjects.size);

        Array<GameObject> gameObjects2 = new Array<>();
        Array<GameObject> temp = layer.getGameObjects(gameObjects2);

        assertSame(gameObjects2, temp);
        assertEquals(2, gameObjects2.size);
    }
}


















