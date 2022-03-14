package com.isoterik.racken.test;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.isoterik.racken.GameObject;
import com.isoterik.racken.Scene;
import com.isoterik.racken._2d.components.debug.BoxDebugRenderer;
import com.isoterik.racken.actor.ActorGameObjectMapper;

public class ActorMappingTest extends Scene {
    public ActorMappingTest() {
        setBackgroundColor(Color.BLACK);
        setRenderDebugLines(true);

        GameObject gameObject = GameObject.newInstance();
        ActorGameObjectMapper actorGameObjectMapper = new ActorGameObjectMapper();
        gameObject.addComponent(actorGameObjectMapper);
        gameObject.addComponent(new BoxDebugRenderer());
        addGameObject(gameObject);

        Actor actor = actorGameObjectMapper.getActor();
        actor.setSize(100, 100);
        actor.setPosition(100, 100);
        actor.setOrigin(50, 50);

        Action rotateAction = Actions.rotateBy(10, 0.5f);
        actor.addAction(Actions.forever(rotateAction));
    }
}
