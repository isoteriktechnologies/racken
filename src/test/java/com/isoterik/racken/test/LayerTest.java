package com.isoterik.racken.test;

import com.badlogic.gdx.utils.Array;
import com.isoterik.racken.GameObject;
import com.isoterik.racken.Layer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LayerTest {
    Layer layer1 = new Layer("layer1");
    Layer layer2 = new Layer("layer2");

    @Test
    public void testAddAndRemoveGameObjects() {
        layer1.clear();

        layer1.addGameObject(GameObject.newInstance());
        layer1.addGameObject(GameObject.newInstance());

        assertEquals(2, layer1.size());

        layer1.removeGameObject(layer1.getGameObjects(null).first());

        assertEquals(1, layer1.size());
    }

    @Test
    public void testFetchLayerGameObjects() {
        layer1.clear();

        layer1.addGameObject(GameObject.newInstance());
        layer1.addGameObject(GameObject.newInstance());

        assertEquals(2, layer1.size());

        Array<GameObject> gameObjects = null; // this is intentional
        gameObjects = layer1.getGameObjects(gameObjects);

        assertEquals(2, gameObjects.size);

        Array<GameObject> gameObjects2 = new Array<>();
        Array<GameObject> temp = layer1.getGameObjects(gameObjects2);

        assertSame(gameObjects2, temp);
        assertEquals(2, gameObjects2.size);
    }
}


















