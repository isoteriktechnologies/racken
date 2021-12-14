package com.isoterik.racken.test;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Timer;
import com.isoterik.racken.Scene;
import com.isoterik.racken.input.TouchTrigger;

public class SceneManagerTest extends Scene {
    Scene2 scene2;
    Scene3 scene3;

    public SceneManagerTest() {
        scene3 = new Scene3();
    }

    @Override
    public void transitionedToThisScene(Scene previousScene) {
        scene2 = new Scene2();

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                racken.setScene(scene2);
            }
        }, 3);
    }

    class Scene2 extends Scene {
        public Scene2() {
            setBackgroundColor(Color.BLUE);
            setStackable(false);
        }

        @Override
        public void transitionedToThisScene(Scene previousScene) {
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    racken.setScene(scene3);
                }
            }, 3);
        }
    }

    class Scene3 extends Scene {
        public Scene3() {
            setBackgroundColor(Color.BROWN);

            input.addTouchListener(TouchTrigger.touchDownTrigger(), (mappingName, touchEventData) -> {
                racken.sceneManager.revertToPreviousScene();
            });
        }
    }
}
