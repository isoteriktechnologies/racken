package com.isoterik.racken;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.utils.reflect.ClassReflection;

/**
 * A GameObject represents an entity in the game. A GameObject can't do anything on its own; you have to give it
 * properties before it can do anything.
 * A GameObject is a container; we have to add pieces to it to make into a character, a tree, a spaceship or whatever
 * else you would like it to be. Each piece is called a {@link Component}.
 * <p>
 *
 * Every GameObject has a {@link Transform} component attached automatically and cannot be removed. This is because the
 * Transform defines where the GameObject is located and how it is rotated and scaled; without a Transform, the
 * GameObject would not have a location in the game world.
 * <p>
 *
 * To create game objects, use the static factory methods: {@link #newInstance(String)} and {@link #newInstance()}
 *
 * @see Component
 *
 * @author imranabdulmalik
 */
public final class GameObject {
    private final SnapshotArray<Component> components;
    private final SnapshotArray<GameObject> children = new SnapshotArray<>(GameObject.class);

    public Transform transform;
    private String tag;
    private Scene hostScene;

    private GameObject parent;

    private GameObject()
    { this("Untagged"); }

    private GameObject(String tag) {
        components = new SnapshotArray<>(Component.class);

        transform = new Transform();
        transform.__setGameObject(this);
        components.add(transform);

        this.tag = tag;
    }

    public void addChildren(GameObject... children) {
        for (GameObject child : children)
            addChild(child);
    }

    public void addChild(GameObject child) {
        if (!children.contains(child, true)) {
            children.add(child);
            child.setParent(this);
        }
    }

    public boolean removeChild(GameObject child) {
        boolean removed = children.removeValue(child, true);
        if (removed)
            child.setParent(null);

        return removed;
    }

    public void clearChildren() {
        children.clear();
    }

    public SnapshotArray<GameObject> getChildren() {
        return children;
    }

    /**
     * Sets the scene where this game object resides.
     * This method is called internally by the system. Do not call it directly!
     * @param hostScene the host scene
     */
    public void __setHostScene(Scene hostScene) {
        this.hostScene = hostScene;
        for (Component comp : components) {
            comp.__setHostScene(hostScene);
            comp.setRenderCamera(hostScene.getMainCamera());
        }

        forEachChild(gameObject -> gameObject.__setHostScene(hostScene));
    }

    public void setParent(GameObject parent) {
        this.parent = parent;
    }

    public GameObject getParent() {
        return parent;
    }

    public boolean hasParent() {
        return parent != null;
    }

    /**
     *
     * @return the scene where this game object resides
     */
    public Scene getHostScene()
    { return hostScene; }

    /**
     * Sets the tag for this game object. It is not required to be unique.
     * @param tag the tag
     */
    public void setTag(String tag)
    { this.tag = tag; }

    /**
     *
     * @return the tag for this game object
     */
    public String getTag()
    { return tag; }

    /**
     * Called when this game object is removed from a scene.
     * DO NOT CALL THIS METHOD!
     */
    public void __removeFromScene() {
        for (Component comp : components)
            comp.stop();

        forEachChild(GameObject::__removeFromScene);
    }

    /**
     * Adds a component to this game object.
     * @param component the component
     */
    public void addComponent(Component component) {
        if (components.contains(component, true))
            return;

        component.__setGameObject(this);
        component.attach();

        for (Component comp : components)
            comp.componentAdded(component);

        // If this game object is already added to a scene then we need to alert the component
        if (hostScene != null) {
            component.__setHostScene(hostScene);
            component.setRenderCamera(hostScene.getMainCamera());
            component.start();
        }

        components.add(component);
    }

    /**
     * Removes a component attached to this game object.
     * <strong>Note:</strong> a component can remove itself.
     * @param component the component to remove.
     * @return true if the component was removed. false otherwise
     */
    public boolean removeComponent(Component component) {
        if (components.contains(component, true) &&
                components.removeValue(component, true)) {
            for (Component comp : components)
                comp.componentRemoved(component);

            // detach
            component.detach();

            component.__setGameObject(null);
            return true;
        }

        return false;
    }

    /**
     * Removes the first component found for a particular type that is attached to this host game object.
     * <strong>Note:</strong> a component can remove itself.
     * @param componentClass the class of the component
     * @param <T> the type of component
     * @return true if a component of such type is removed. false otherwise
     */
    public <T extends Component> boolean removeComponent(Class<T> componentClass)
    { return removeComponent(getComponent(componentClass)); }

    public <T extends Component> void removeComponents(Class<T> componentClass) {
        for (Component c : components) {
            if (ClassReflection.isAssignableFrom(componentClass, c.getClass()))
                removeComponent(c);
        }
    }

    /**
     * Gets a component of a particular type that is attached to this game object.
     * <strong>Note:</strong> a component can remove itself.
     * @param componentClass the class of the component
     * @param <T> the type of component
     * @return the component. null if not found
     */
    public <T extends Component> T getComponent(Class<T> componentClass) {
        for (Component c : components) {
            if (ClassReflection.isAssignableFrom(componentClass, c.getClass()))
                return (T)c;
        }

        return null;
    }

    /**
     * Gets components of a particular type that is attached to this game object.
     * @param componentClass the class of the component
     * @param <T> the type of component
     * @return the components found or empty list if none found
     */
    public <T extends Component> Array<T> getComponents(Class<T> componentClass) {
        Array<T> comps = new Array<>();

        for (Component c : components) {
            if (ClassReflection.isAssignableFrom(componentClass, c.getClass()))
                comps.add((T)c);
        }

        return comps;
    }

    /**
     * Returns all the components attached to this game object.
     * @return all the components attached to this game object.
     */
    public SnapshotArray<Component> getComponents()
    { return components; }

    /**
     * Checks if a component of a particular type is attached to the host game object.
     * @param componentClass the class of the component
     * @param <T> the type of component
     * @return true if a component of such type exists. false otherwise
     */
    public <T extends Component> boolean hasComponent(Class<T> componentClass)
    { return getComponent(componentClass) != null; }

    /**
     * Checks if a component is attached to this game object.
     * @param component the component to check.
     * @return true if a component of such type exists. false otherwise
     */
    public boolean hasComponent(Component component)
    { return components.contains(component, true); }

    /**
     * Calls the given IterationListener on all components attached to this game object.
     * This method is used internally by the system. While it is safe to call it, you usually don't need to.
     * @param iterationListener the iteration listener
     */
    public void forEachComponent(ComponentIterationListener iterationListener) {
        Component[] array = components.begin();

        for (Component component : array)
            if (component != null)
                iterationListener.onIterate(component);

        components.end();

        forEachChild(gameObject -> gameObject.forEachComponent(iterationListener));
    }

    /**
     * Calls the given iteration listener on every child of this game object
     * @param iterationListener the iteration listener
     */
    public void forEachChild(GameObjectIterationListener iterationListener) {
        GameObject[] array = children.begin();

        for (GameObject gameObject : array)
            if (gameObject != null)
                iterationListener.onIterate(gameObject);

        children.end();
    }

    /**
     * Checks if the provided tag equals the current tag of this gameObject.
     * @param otherTag the tag to compare to.
     * @return true if the tags are similar. false otherwise
     */
    public boolean sameTag(String otherTag) {
        return this.tag.equals(otherTag);
    }

    /**
     * An iteration listener that can be used to iterate the components of a {@link GameObject}.
     */
    public interface ComponentIterationListener {
        void onIterate(Component component);
    }

    /**
     * Creates a new {@link GameObject} given a tag.
     * @param tag the tag for the game object
     * @return the created game object
     */
    public static GameObject newInstance(String tag)
    { return new GameObject(tag); }

    /**
     * Creates a new {@link GameObject} using 'Untagged' as the default tag.
     * @return the created game object
     */
    public static GameObject newInstance()
    { return new GameObject(); }

    /**
     * An iteration listener for processing game objects
     */
    public interface GameObjectIterationListener {
        void onIterate(GameObject gameObject);
    }
}
