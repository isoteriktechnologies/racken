package com.isoterik.racken.actor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.isoterik.racken.Component;
import com.isoterik.racken.GameObject;
import com.isoterik.racken.Transform;
import com.isoterik.racken.utils.GameWorldUnits;

public class ActorGameObjectMapper extends Component {
    protected Actor actor;

    protected GameWorldUnits unitConverter;

    protected MappingType mappingType = MappingType.MAP_FROM_ACTOR;

    public enum MappingType {
        MAP_FROM_GAME_OBJECT, MAP_FROM_ACTOR
    }

    public ActorGameObjectMapper(Actor actor, GameWorldUnits unitConverter) {
        this.actor = actor;
        this.unitConverter = unitConverter;
    }

    public ActorGameObjectMapper(Actor actor) {
        this(actor, null);
    }

    public ActorGameObjectMapper(GameWorldUnits unitConverter) {
        this(new Actor(), unitConverter);
    }

    public ActorGameObjectMapper() {
        this(new Actor());
    }

    protected GameWorldUnits resolveUnitConverter() {
        if (unitConverter == null) {
            if (scene != null)
                return scene.getGameWorldUnits();
            return null;
        }

        return unitConverter;
    }

    public Actor getActor() {
        return actor;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    public GameWorldUnits getUnitConverter() {
        return unitConverter;
    }

    public void setUnitConverter(GameWorldUnits unitConverter) {
        this.unitConverter = unitConverter;
    }

    public MappingType getMappingType() {
        return mappingType;
    }

    public void setMappingType(MappingType mappingType) {
        this.mappingType = mappingType;
    }

    @Override
    public void postUpdate(float deltaTime) {
        GameWorldUnits converter = resolveUnitConverter();

        if (converter != null && actor != null) {
            if (actor.getStage() == null)
                actor.act(deltaTime);

            Transform transform = gameObject.transform;

            if (mappingType == MappingType.MAP_FROM_ACTOR) {
                transform.setX(converter.toWorldUnit(actor.getX()));
                transform.setY(converter.toWorldUnit(actor.getY()));
                transform.setWidth(converter.toWorldUnit(actor.getWidth()));
                transform.setHeight(converter.toWorldUnit(actor.getHeight()));
                transform.setScale(actor.getScaleX(), actor.getScaleY());
                transform.setOrigin(converter.toWorldUnit(actor.getOriginX()),
                        converter.toWorldUnit(actor.getOriginY()));
                transform.setRotation(actor.getRotation());
            } else {
                actor.setX(converter.toPixels(transform.getX()));
                actor.setY(converter.toPixels(transform.getY()));
                actor.setWidth(converter.toPixels(transform.getWidth()));
                actor.setHeight(converter.toPixels(transform.getHeight()));
                actor.setScale(transform.getScaleX(), transform.getScaleY());
                actor.setOrigin(converter.toPixels(transform.getOriginX()),
                        converter.toPixels(transform.getOriginY()));
                actor.setRotation(transform.getRotation());
            }
        }
    }
}
