package com.isoterik.racken;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.isoterik.racken.input.InputManager;

/**
 * A Component is a functional piece of a {@link GameObject}. Every component is an isolated functionality that can be
 * attached to a {@link GameObject} to give that functionality to that particular game object.
 * To give functionality to a {@link GameObject}, you attach different components to it.
 * <p>
 * Component instances are not reusable but several instances of one component can be attached to different game objects to share the same functionality.
 *
 * @see GameObject
 *
 * @author imranabdulmalik
 */
public class Component {
    protected GameObject gameObject;
    protected Scene scene;
    protected InputManager input;
    protected GameCamera renderCamera;

    protected boolean enabled = true;

    /**
     * Called when the component is attached to a {@link GameObject}.
     * <strong>Note:</strong> At this point it is guaranteed that a game object exists for this component but it
     * is not guaranteed that the game object has been added to a {@link Scene} yet!
     */
    public void attach() {}

    /**
     * Called when the host game object is added to a {@link Scene}.
     * If the game object is already added to a scene before this component gets attached, this method will still be called (immediately after {@link #attach()})
     * It is safe to make scene related calls here because a {@link Scene} instance exists. This is where you will typically do all initializations.
     */
    public void start() {}

    /**
     * Called when the component should resume.
     * This is where you will typically resume music playbacks.
     */
    public void resume() {}

    /**
     * Called before other components are updated.
     * @param deltaTime the time difference between the current frame and the previous frame.
     */
    public void preUpdate(float deltaTime) {}

    /**
     * Called when the component should update.
     * @param deltaTime the time difference between the current frame and the previous frame.
     */
    public void update(float deltaTime) {}

    /**
     * Called after all the components of the host game object are updated.
     * This is useful for tasks that depends on the updated state of game objects.
     * @param deltaTime the time difference between the current frame and the previous frame.
     */
    public void postUpdate(float deltaTime) {}

    /**
     * Called when the screen is resized.
     * @param newScreenWidth the new screen width (in pixels)
     * @param newScreenHeight the new screen height (in pixels)
     */
    public void resize(int newScreenWidth, int newScreenHeight) {}

    /**
     * Called before other component renders.
     */
    public void preRender() {}

    /**
     * Called when the component should render.
     */
    public void render() {}

    /**
     * Called after other component renders.
     */
    public void postRender() {}

    /**
     * Called when the component should render debug drawings of type {@link com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType#Line}
     * @param shapeRenderer a shape renderer to draw with
     */
    public void renderShapeLine(ShapeRenderer shapeRenderer) {}

    /**
     * Called when the component should render debug drawings of type {@link com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType#Filled}
     * @param shapeRenderer a shape renderer to draw with
     */
    public void renderShapeFilled(ShapeRenderer shapeRenderer) {}

    /**
     * Called when the component should render debug drawings of type {@link com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType#Point}
     * @param shapeRenderer a shape renderer to draw with
     */
    public void renderShapePoint(ShapeRenderer shapeRenderer) {}

    /**
     * Called when the component should pause.
     * This is where you'll typically pause music playbacks.
     */
    public void pause() {}

    /**
     * Called when the component is getting detached from the host game object.
     * The {@link GameObject} instance will become null after this method completes so this is the last place to communicate with the game object.
     * It is usually a good idea to dispose component-allocated resources here. Resources disposed here are usually the ones allocated in {@link #attach()}
     */
    public void detach() {}

    /**
     * Called when the host game object is removed from a {@link Scene}.
     * {@link #scene} and {@link #input} instances becomes invalid after this method executes.
     */
    public void stop() {}

    /**
     * Called when the component is getting destroyed.
     * You should dispose all scene wide resources here.
     */
    public void destroy() {}

    /**
     * Returns the camera this component renders with
     * @return the camera this component renders with
     */
    public GameCamera getRenderCamera() {
        return renderCamera;
    }

    /**
     * Sets the camera this component renders with
     * @param renderCamera the camera
     */
    public void setRenderCamera(GameCamera renderCamera) {
        this.renderCamera = renderCamera;
    }

    /**
     * Called when a new component is attached to the host game object.
     * <strong>Note:</strong> the new component will be added once this method completes and every other component is notified.
     * @param component the new component
     */
    public void componentAdded(Component component) {}

    /**
     * Called when an existing component is getting detached from the host game object.
     * <strong>Note:</strong> the new component will be detached once this method completes and every other component is notified.
     * @param component the new component
     */
    public void componentRemoved(Component component) {}

    /**
     * Components can be disabled. This determines if it is enabled or not
     * @param enabled is it enabled?
     */
    public void setEnabled(boolean enabled)
    { this.enabled = enabled; }

    /**
     *
     * @return whether this component is enabled or not
     */
    public boolean isEnabled()
    { return enabled; }

    /**
     * Sets the host {@link GameObject}.
     * This method is called internally by the system and should never be called directly!
     * @param gameObject host game object
     */
    public void __setGameObject(GameObject gameObject)
    { this.gameObject = gameObject; }

    /**
     * Sets the host {@link Scene}.
     * This method is called internally by the system and should never be called directly!
     * @param scene the scene where the host game object resides
     */
    public void __setHostScene(Scene scene) {
        this.scene = scene;

        if (scene != null)
            this.input = scene.getInput();
        else
            this.input = null;
    }

    /**
     *
     * @return the host game object
     */
    public GameObject getGameObject()
    { return gameObject; }

    /**
     * Adds a component to the host game object.
     * @param component the component
     */
    public void addComponent(Component component) {
        if (gameObject != null)
            gameObject.addComponent(component);
    }

    /**
     * Gets a component of a particular type that is attached to the host game object.
     * <strong>Note:</strong> If there many components of the requested type, only the first one found will be returned. To get everything, use {@link #getComponents(Class)} instead.
     * @param componentClass the class of the component
     * @param <T> the type of component
     * @return the component found or null if none found
     */
    public <T extends Component> T getComponent(Class<T> componentClass) {
        if (gameObject != null)
            return gameObject.getComponent(componentClass);
        return null;
    }

    /**
     * Gets components of a particular type that is attached to the host game object.
     * @param componentClass the class of the component
     * @param <T> the type of component
     * @return the components found or empty list if none found
     */
    public <T extends Component> Array<T> getComponents(Class<T> componentClass) {
        if (gameObject != null)
            return gameObject.getComponents(componentClass);
        return null;
    }

    /**
     *
     * @return all the components attached to the host game object including this one.
     */
    public Array<Component> getComponents() {
        if (gameObject != null)
            return gameObject.getComponents();
        return null;
    }

    /**
     * Checks if a component of a particular type is attached to the host game object.
     * @param componentClass the class of the component
     * @param <T> the type of component
     * @return true if a component of such type exists. false otherwise
     */
    public <T extends Component> boolean hasComponent(Class<T> componentClass) {
        if (gameObject != null)
            return gameObject.hasComponent(componentClass);
        return false;
    }

    /**
     * Checks if a component is attached to the host game object.
     * @param component the component to check.
     * @return true if a component of such type exists. false otherwise
     */
    public boolean hasComponent(Component component) {
        if (gameObject != null)
            return gameObject.hasComponent(component);
        return false;
    }

    /**
     * Removes the first component found for a particular type that is attached to the host game object.
     * <strong>Note:</strong> a component can remove itself.
     * @param componentClass the class of the component
     * @param <T> the type of component
     * @return true if a component of such type is removed. false otherwise
     */
    public <T extends Component> boolean removeComponent(Class<T> componentClass) {
        if (gameObject != null)
            return gameObject.removeComponent(componentClass);
        return false;
    }

    /**
     * Removes all component of a particular type that is attached to the host game object.
     * <strong>Note:</strong> a component can remove itself.
     * @param componentClass the class of the component
     * @param <T> the type of component
     */
    public <T extends Component> void removeComponents(Class<T> componentClass) {
        if (gameObject != null)
            gameObject.removeComponents(componentClass);
    }

    /**
     * Removes a component attached to the host game object.
     * <strong>Note:</strong> a component can remove itself.
     * @param component the component to remove.
     * @return true if the component was removed. false otherwise
     */
    public boolean removeComponent(Component component) {
        if (gameObject != null)
            return gameObject.removeComponent(component);
        return false;
    }

    /**
     * Removes a game object from the host scene.
     * This has no effect if there is no existing valid {@link Scene}.
     * @param gameObject the game object.
     * @return whether the game object was removed.
     */
    public boolean removeGameObject(GameObject gameObject) {
        if (scene != null) {
            return scene.removeGameObject(gameObject);
        }

        return false;
    }

    /**
     * Adds a game object to the host scene.
     * This has no effect if there is no existing valid {@link Scene}.
     * @param gameObject the game object to add.
     */
    public void addGameObject(GameObject gameObject) {
        if (scene != null)
            scene.addGameObject(gameObject);
    }

    /**
     * Finds the first gameObject with the given tag.
     * @param tag the gameObject's tag.
     * @return the first gameObject with the given tag or null if none found or no scene yet.
     */
    public GameObject findGameObject(String tag) {
        if (scene != null)
            return scene.findGameObject(tag);

        return null;
    }

    /**
     * Calls the given iteration listener on every game objects in the scene.
     * This has no effect if there is no existing valid {@link Scene}.
     * @param iterationListener the iteration listener
     */
    public void forEachGameObject(GameObject.GameObjectIterationListener iterationListener) {
        if (scene != null)
            scene.forEachGameObject(iterationListener);
    }
}
