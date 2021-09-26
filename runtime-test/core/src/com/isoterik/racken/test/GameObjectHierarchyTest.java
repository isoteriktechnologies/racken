package com.isoterik.racken.test;

import com.isoterik.racken.Component;
import com.isoterik.racken.GameObject;
import com.isoterik.racken.Scene;

public class GameObjectHierarchyTest extends Scene {
    public GameObjectHierarchyTest() {
        GameObject parent = GameObject.newInstance("Parent");
        GameObject parent2 = GameObject.newInstance("Parent2");
        GameObject child = GameObject.newInstance("Child");
        GameObject child2 = GameObject.newInstance("Child2");
        GameObject child3 = GameObject.newInstance("Child3");

        parent2.addChild(child);
        parent2.addChild(child2);
        parent.addChild(child3);
        parent.addChild(parent2);

        parent.addComponent(new LoggerComponent());
        parent2.addComponent(new LoggerComponent());
        child.addComponent(new LoggerComponent());
        child2.addComponent(new LoggerComponent());
        child3.addComponent(new LoggerComponent());

        addGameObject(parent);
    }

    static void print(String what) {
        System.out.println(what);
    }

    static class LoggerComponent extends Component {
        @Override
        public void attach() {
            print(gameObject.getTag() + " attach()");
        }

        @Override
        public void start() {
            print(gameObject.getTag() + " start()");
        }

        @Override
        public void resume() {
            print(gameObject.getTag() + " resume()");
        }

        @Override
        public void preUpdate(float deltaTime) {
            print(gameObject.getTag() + " preUpdate()");
        }

        @Override
        public void update(float deltaTime) {
            print(gameObject.getTag() + " update()");
        }

        @Override
        public void postUpdate(float deltaTime) {
            print(gameObject.getTag() + " postUpdate()");
        }
    }
}






















