package com.isoterik.racken.test;

import com.isoterik.racken.Component;
import com.isoterik.racken.GameObject;
import com.isoterik.racken.Scene;
import com.isoterik.racken.Transform;

public class WorldLocalTransformTest extends Scene {
    GameObject parent, child;

    public WorldLocalTransformTest() {
        parent = GameObject.newInstance("Parent");
        child = GameObject.newInstance("Child");

        parent.addChild(child);
        addGameObject(parent);

        parent.addComponent(new TestLogger());
        child.addComponent(new TestLogger());

        parent.addComponent(new Component() {
            @Override
            public void update(float deltaTime) {
                gameObject.transform.translate(1, 1);
            }
        });
    }

    private static class TestLogger extends Component {
        @Override
        public void postUpdate(float deltaTime) {
            Transform transform = gameObject.transform;

            System.out.printf("%s: X->%f, Y->%f%n", gameObject.getTag(), transform.world().getX(),
                    transform.world().getY());
        }
    }
}





























