package com.isoterik.racken.actor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.isoterik.racken.Component;
import com.isoterik.racken.GameObject;
import com.isoterik.racken.Transform;
import com.isoterik.racken.utils.GameWorldUnits;

/**
 * A mapper that maps the transforms of a {@link GameObject} and an {@link Actor}.
 * The mapping direction is defined by {@link MappingType}.
 * <p>
 * It uses {@link GameWorldUnits} for unit conversions. If no custom converter is provided, it will attempt to use the
 * default one of the scene.
 * {@link Actor}s are assumed to be using pixel units!
 * <p>
 * {@link Actor}s that are not added to a {@link com.badlogic.gdx.scenes.scene2d.Stage} yet will be updated automatically
 * before mapping.
 * <p>
 * The mapping is done in the {@link #postUpdate(float)} phase of the component. This allows other components to update
 * before the mapping. If you want to change the phase where the mapping is done, you can extend this component and
 * call the {@link #map(float)} method in whatever phase you like (don't forget to override the default chosen phase to do
 * nothing!)
 */
public class ActorGameObjectMapper extends Component {
    protected Actor actor;

    protected GameWorldUnits unitConverter;

    protected MappingType mappingType = MappingType.MAP_FROM_ACTOR;

    /**
     * The mapping direction.
     * <ul>
     *     <li>{@link MappingType#MAP_FROM_ACTOR}: Use this if you want to map the actor's transform to the gameObject</li>
     *     <li>{@link MappingType#MAP_FROM_GAME_OBJECT}: Use this if you want to map the gameObject's transform to the actor</li>
     * </ul>
     */
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

    /**
     * Gets the current actor
     * @return the current actor
     */
    public Actor getActor() {
        return actor;
    }

    /**
     * Sets the current actor
     * @param actor the actor
     */
    public void setActor(Actor actor) {
        this.actor = actor;
    }

    /**
     * Returns the current unit converter.
     * @return the current unit converter.
     */
    public GameWorldUnits getUnitConverter() {
        return unitConverter;
    }

    /**
     * Sets the current unit converter.
     * @param unitConverter the converter
     */
    public void setUnitConverter(GameWorldUnits unitConverter) {
        this.unitConverter = unitConverter;
    }

    /**
     * Returns the current mapping type
     * @return the current mapping type
     */
    public MappingType getMappingType() {
        return mappingType;
    }

    /**
     * Sets the current mapping type
     * @param mappingType the mapping type
     */
    public void setMappingType(MappingType mappingType) {
        this.mappingType = mappingType;
    }

    /**
     * Handles the mapping.
     * @param deltaTime the delta time
     */
    protected void map(float deltaTime) {
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

    @Override
    public void postUpdate(float deltaTime) {
        map(deltaTime);
    }
}
