package com.isoterik.racken.test;

import com.isoterik.racken.Component;
import com.isoterik.racken.GameObject;
import com.isoterik.racken.Scene;

public class GameObjectHierarchyTest extends Scene {
    public GameObjectHierarchyTest() {
        GameObject parent = GameObject.newInstance("Parent");
        GameObject child = GameObject.newInstance("Child");

        parent.addChild(child);

        parent.addComponent(new ParentObjectComponent());
        child.addComponent(new ChildObjectComponent());

        addGameObject(parent);
    }

    static void print(String what) {
        System.out.println(what);
    }

    static class ParentObjectComponent extends Component {
        @Override
        public void attach() {
            print("Parent attach()");
        }

        @Override
        public void start() {
            print("Parent start()");
        }

        @Override
        public void resume() {
            print("Parent resume()");
        }

        @Override
        public void preUpdate(float deltaTime) {
            print("Parent preUpdate()");
        }

        @Override
        public void update(float deltaTime) {
            print("Parent update()");
        }

        @Override
        public void postUpdate(float deltaTime) {
            print("Parent postUpdate()");
        }
    }

    static class ChildObjectComponent extends Component {
        @Override
        public void attach() {
            print("Child attach()");
        }

        @Override
        public void start() {
            print("Child start()");
        }

        @Override
        public void resume() {
            print("Child resume()");
        }

        @Override
        public void preUpdate(float deltaTime) {
            print("Child preUpdate()");
        }

        @Override
        public void update(float deltaTime) {
            print("Child update()");
        }

        @Override
        public void postUpdate(float deltaTime) {
            print("Child postUpdate()");
        }
    }
}






















