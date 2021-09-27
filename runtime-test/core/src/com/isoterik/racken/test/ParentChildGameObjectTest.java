package com.isoterik.racken.test;

import com.badlogic.gdx.graphics.Texture;
import com.isoterik.racken.Component;
import com.isoterik.racken.GameObject;
import com.isoterik.racken.Scene;
import com.isoterik.racken.input.ITouchListener;
import com.isoterik.racken.input.TouchEventData;
import com.isoterik.racken.input.TouchTrigger;

public class ParentChildGameObjectTest extends Scene {
    public ParentChildGameObjectTest() {
        Texture texture = new Texture("badlogic.jpg");
        GameObject parent = newSpriteObject(texture);
        GameObject child = newSpriteObject(texture);
        parent.transform.setOriginPercent(1f);
        child.transform.size.scl(0.5f);
        //child.transform.setOriginPercent(0.5f);
        //child.transform.setRotation(-45);
        //child.transform.setPosition(parent.transform.getWidth()/2f, parent.transform.getHeight()/2f);

        parent.addChild(child);
        addGameObject(parent);

        input.addTouchListener(TouchTrigger.touchDownTrigger().setPolled(true), (mappingName, touchEventData) -> {
            parent.transform.setPosition(touchEventData.touchX, touchEventData.touchY);
        });

        parent.addComponent(new Component() {
            @Override
            public void update(float deltaTime) {
                parent.transform.rotate(-10 * deltaTime);
            }
        });
    }
}
