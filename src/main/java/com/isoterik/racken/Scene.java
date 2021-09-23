package com.isoterik.racken;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.isoterik.racken._2d.GameCamera2d;
import com.isoterik.racken._2d.components.renderer.SpriteRenderer;
import com.isoterik.racken.input.InputManager;
import com.isoterik.racken.util.GameWorldUnits;
import com.isoterik.racken.util.PoolableArrayIterator;

/**
 * A Scene contains the {@link GameObject}s of your game. Think of each Scene as a unique level of your game.
 * Every scene has its own {@link InputManager} for managing input.
 * <p>
 * A {@link GameCamera} is used to display a portion of the scene or the whole scene at a time.
 * <p>
 *
 * {@link GameObject}s are manged using {@link Layer}s.
 * Layers are processed top-down; layers added first are processed first (this can be used to manipulate how GameObjects
 * are rendered)
 * A default layer is provided, so you don't have to use layers if you don't need to.
 * <p>
 * Every scene has a {@link Stage} instance for working with UI elements. The stage is already setup to update, receive
 * input and render; you don't have do these yourself.
 *
 * @author isoterik
 */
public class Scene {
    /** A reference to the shared instance of {@link Racken} */
    protected Racken racken;

    /** The name of the default layer. Use this to add {@link GameObject}s to the default layer. */
    public static final String DEFAULT_LAYER = "RACKEN_DEFAULT_LAYER";

    private final Layer defaultLayer;
    protected Array<Layer> layers;

    /** The main camera object used for projecting a portion of the scene. */
    protected GameObject mainCameraHost;

    /** The default {@link GameWorldUnits} used for this scene */
    protected GameWorldUnits gameWorldUnits;

    /** The input manager for handling input. */
    protected final InputManager input;

    /* For components iteration that needs the current delta time */
    private float deltaTime;

    // These iteration listeners prevent us from creating new instances every time!
    protected GameObject.ComponentIterationListener startIter, pauseIter, preRenderIter, postRenderIter,
            resumeIter, preUpdateIter, updateIter, resizeIter, postUpdateIter, renderIter,
            renderShapeLinedIter, renderShapeFilledIter, renderShapePointIter, destroyIter;

    // The state of the Scene
    private boolean isActive;

    /** {@link com.badlogic.gdx.scenes.scene2d.Stage} instance used for managing UI elements */
    protected Stage uiStage;

    /** ShapeRenderer for debug drawings */
    protected ShapeRenderer shapeRenderer;
    protected GameCamera shapeRendererCamera;

    /** This flag determines whether debug renderings should be rendered. */
    protected boolean renderDebugLines;

    /** Determines whether this scene can be stacked. */
    protected boolean stackable = true;

    private int resizedWidth, resizedHeight;

    // An array of game objects
    private Array<GameObject> gameObjects = new Array<>();

    private final GameObjectIteratorPool gameObjectIteratorPool = new GameObjectIteratorPool(gameObjects);

    /**
     * Creates a new instance.
     */
    public Scene() {
        racken = Racken.instance();

        onCreate();

        gameWorldUnits = new GameWorldUnits(racken.defaultSettings.VIEWPORT_WIDTH, racken.defaultSettings.VIEWPORT_HEIGHT,
                racken.defaultSettings.PIXELS_PER_UNIT);

        defaultLayer = new Layer(DEFAULT_LAYER);
        layers = new Array<>();
        layers.add(defaultLayer);

        input = new InputManager(this);

        startIter = component -> {
            if (component.isEnabled())
                component.start();
        };

        resumeIter = component -> {
            if (component.isEnabled())
                component.resume();
        };

        pauseIter = component -> {
            if (component.isEnabled())
                component.pause();
        };

        resizeIter = component -> {
            if (component.isEnabled())
                component.resize(resizedWidth, resizedHeight);
        };

        preUpdateIter = component -> {
            if (component.isEnabled())
                component.preUpdate(deltaTime);
        };

        updateIter = component -> {
            if (component.isEnabled())
                component.update(deltaTime);
        };

        postUpdateIter = component -> {
            if (component.isEnabled())
                component.postUpdate(deltaTime);
        };

        preRenderIter = component -> {
            if (component.isEnabled())
                component.preRender();
        };

        renderIter = component -> {
            if (component.isEnabled())
                component.render();
        };

        postRenderIter = component -> {
            if (component.isEnabled())
                component.postRender();
        };

        renderShapeLinedIter = component -> {
            if (component.isEnabled())
                component.renderShapeLined(shapeRenderer);
        };

        renderShapeFilledIter = component -> {
            if (component.isEnabled())
                component.renderShapeFilled(shapeRenderer);
        };

        renderShapePointIter = component -> {
            if (component.isEnabled())
                component.renderShapePoint(shapeRenderer);
        };

        destroyIter = Component::destroy;

        GameCamera camera = new GameCamera2d();
        shapeRendererCamera = camera;

        mainCameraHost = GameObject.newInstance("MainCamera");
        mainCameraHost.addComponent(camera);
        addGameObject(mainCameraHost);

        setupCanvas(new StretchViewport(gameWorldUnits.getScreenWidth(),
                gameWorldUnits.getScreenHeight()));

        shapeRenderer = new ShapeRenderer();
    }

    /**
     * This is called during construction before instance fields are initialized. This is useful for setting default
     * properties that will be used during construction.
     *
     * <strong>Most instance fields are not initialized yet, it is not safe to make use of them here!</strong>
     */
    protected void onCreate() {
    }

    /**
     * @return the current {@link GameWorldUnits} instance.
     */
    public GameWorldUnits getGameWorldUnits() {
        return gameWorldUnits;
    }

    /**
     *
     * @return whether this scene can be stacked
     */
    public boolean isStackable()
    { return stackable; }

    /**
     * Stackable scenes are scenes that can be added to a stack when the {@link SceneManager} switches scenes.
     * Instances of stackable scenes are always retained and can be switched back to using the same instance.
     * Scenes that are not stackable are disposed as soon as the scene manager switches from them.
     * <p>
     * <strong>A good rule of thumb:</strong>
     * <ul>
     *     <li>
     *         If the scene takes a considerable amount of time to load resources and the scene is very likely to be
     *         returned to then it may be a good choice to make it stackable.
     *         That way the resources are loaded only once.
     *     </li>
     *     <li>
     *         If the scene is very resource intensive and other scenes need to be loaded then it may be a good
     *         idea to NOT make it stackable. That way the resources allocated by that scene is disposed as soon as it
     *         is no longer needed.
     *     </li>
     *     <li>
     *         If the scene is a UI scene (like a menu scene) then it may be a good idea to make it stackable
     *         since UI scenes are usually visited many times.
     *     </li>
     * </ul>
     * <p>
     *
     * <strong>Scenes are stackable by default</strong>
     * @param stackable whether this scene should be stackable
     */
    public void setStackable(boolean stackable)
    { this.stackable = stackable; }

    /**
     * Custom debug lines can be rendered around game objects. This is useful for debugging purposes.
     * This is also useful for tracking invisible game objects (game objects that are not rendered).
     * Use this method to decide if those debug lines should be rendered or not.
     * @param renderDebugLines whether custom debug lines are rendered
     */
    public void setRenderDebugLines(boolean renderDebugLines)
    { this.renderDebugLines = renderDebugLines; }

    /**
     *
     * @return whether custom debug lines are rendered or not
     */
    public boolean isRenderDebugLines()
    { return renderDebugLines; }

    /**
     * By default, the ui stage (an instance of {@link Stage}) is set up with an {@link com.badlogic.gdx.utils.viewport.StretchViewport}.
     * Use this method to change the viewport to your desired viewport.
     * @param viewport a viewport for scaling UI elements
     */
    public void setupCanvas(Viewport viewport) {
        if (uiStage != null)
            input.getInputMultiplexer().removeProcessor(uiStage);

        uiStage = new Stage(viewport);
        input.getInputMultiplexer().addProcessor(uiStage);
    }

    /**
     *
     * @return the {@link Stage} used for managing UI elements.
     */
    public Stage getUIStage()
    { return uiStage; }

    /**
     * A scene becomes active when the scene is resumed. It goes back to an inactive state when the scene is paused.
     * @return whether this scene is active or not
     */
    public boolean isActive()
    { return isActive; }

    /**
     *
     * @return the input manager for this scene
     */
    public InputManager getInput()
    { return input; }

    /**
     * Changes the camera used for projecting this scene. This only changes the attached {@link GameCamera} and not the
     * GameObject itself
     * @param mainCamera the {@link GameCamera} for projecting this scene.
     */
    public void setupMainCamera(GameCamera mainCamera) {
        this.mainCameraHost.removeComponent(getMainCamera());
        this.mainCameraHost.addComponent(mainCamera);
    }

    /**
     *
     * @return the main camera used for projecting this scene.
     */
    public GameCamera getMainCamera()
    { return mainCameraHost.getComponent(GameCamera.class); }

    /**
     * Finds a layer, given the name.
     * @param name the name of the layer to find.
     * @return the layer if found or null if not found
     */
    public Layer findLayer(String name) {
        for (Layer layer : layers) {
            if (layer.getName().equals(name))
                return layer;
        }

        return null;
    }

    /**
     *
     * @return the default layer for this scene.
     */
    public Layer getDefaultLayer()
    { return defaultLayer; }

    /**
     * Checks if a given layer is one of the layers of this scene.
     * @param layer the layer to check
     * @return true if the layer exists. false otherwise
     */
    public boolean hasLayer(Layer layer)
    { return layers.contains(layer, true); }

    /**
     * Checks if a layer with a given name is one of the layers of this scene.
     * @param layerName the name of the layer.
     * @return true if the layer exists. false otherwise.
     */
    public boolean hasLayer(String layerName) {
        for (Layer layer : layers) {
            if (layer.getName().equals(layerName))
                return true;
        }

        return false;
    }

    /**
     * Adds a new layer to this scene
     * @param layer the layer to add
     */
    public void addLayer(Layer layer)
    { layers.add(layer); }

    /**
     * Removes a given layer from this scene.
     * <strong>This will also remove all game objects that belongs to the layer!</strong>
     * @param layer the layer to remove
     * @throws IllegalArgumentException if the layer is the default layer for this scene.
     */
    public void removeLayer(Layer layer) throws IllegalArgumentException {
        if (layer == defaultLayer)
            throw new IllegalArgumentException("You cannot remove the default layer!");

        layers.removeValue(layer, true);
    }

    /**
     * Removes a layer from this scene given the name of the layer to remove.
     * <strong>This will also remove all game objects that belongs to the layer!</strong>
     * @param layerName the name of the layer to remove
     * @throws IllegalArgumentException if the layer is the default layer for this scene.
     */
    public void removeLayer(String layerName) throws IllegalArgumentException {
        if (layerName.equals(DEFAULT_LAYER))
            throw new IllegalArgumentException("You cannot remove the default layer!");

        Layer layer = findLayer(layerName);
        if (layer != null)
            layers.removeValue(layer, true);
    }

    /**
     *
     * @return the layers of this scene
     */
    public Array<Layer> getLayers()
    { return layers; }

    /**
     * Adds a game object to this scene given a layer to add it to.
     * @param gameObject the game object to add
     * @param layer the layer to add the game object to
     * @throws IllegalArgumentException if the given layer does not exist in this scene
     */
    public void addGameObject(GameObject gameObject, Layer layer) throws IllegalArgumentException {
        if (!hasLayer(layer))
            throw new IllegalArgumentException("This layer does not exist in this scene");

        gameObject.__setHostScene(this);
        layer.addGameObject(gameObject);

        gameObject.forEachComponent(startIter);
    }

    /**
     * Adds a game object to this scene given the name of a layer to add it to.
     * @param gameObject the game object to add
     * @param layerName the name of the layer to add the game object to
     * @throws IllegalArgumentException if there is no existing layer with such name
     */
    public void addGameObject(GameObject gameObject, String layerName) throws IllegalArgumentException {
        Layer layer = findLayer(layerName);
        if (layer == null)
            throw new IllegalArgumentException("This layer does not exist in this scene");

        gameObject.__setHostScene(this);
        layer.addGameObject(gameObject);

        gameObject.forEachComponent(startIter);
    }

    /**
     * Adds a game object to this scene. The game object is added to the default layer.
     * @param gameObject the game object to add.
     */
    public void addGameObject(GameObject gameObject) {
        gameObject.__setHostScene(this);
        defaultLayer.addGameObject(gameObject);

        gameObject.forEachComponent(startIter);
    }

    /**
     * Removes a game object from this scene given the layer where the game object belongs to.
     * @param gameObject the game object to remove
     * @param layer the layer
     * @return true if the game object was removed. false otherwise.
     */
    public boolean removeGameObject(GameObject gameObject, Layer layer) {
        if (!hasLayer(layer))
            return false;

        gameObject.__removeFromScene();
        gameObject.__setHostScene(null);
        return layer.removeGameObject(gameObject);
    }

    /**
     * Removes a game object from this scene given the name of the name where the game objects belongs to.
     * @param gameObject the game object to remove
     * @param layerName the name of the layer
     * @return true if the game object was removed. false otherwise.
     */
    public boolean removeGameObject(GameObject gameObject, String layerName) {
        Layer layer = findLayer(layerName);
        if (layer == null)
            return false;

        gameObject.__removeFromScene();
        gameObject.__setHostScene(null);
        return layer.removeGameObject(gameObject);
    }

    /**
     * Removes a game object from the default layer.
     * @param gameObject the game object to remove.
     * @return true if the game object was removed. false otherwise.
     */
    public boolean removeGameObject(GameObject gameObject) {
        gameObject.__removeFromScene();
        gameObject.__setHostScene(null);
        return defaultLayer.removeGameObject(gameObject);
    }

    /**
     * Returns all the game objects in this scene
     * @param out an output array (can be null)
     * @return all the game objects in this scene
     */
    public Array<GameObject> getGameObjects(Array<GameObject> out) {
        if (out == null)
            out = new Array<>();

        Array<GameObject> temp = new Array<>();
        for (Layer layer : layers) {
            temp.clear();
            out.addAll(layer.getGameObjects(temp));
        }

        return out;
    }

    /**
     * Finds the first gameObject with the given tag.
     * @param tag the gameObject's tag.
     * @return the first gameObject with the given tag or null if none found.
     */
    public GameObject findGameObject(String tag) {
        for (Layer layer : layers) {
            GameObject gameObject = layer.findGameObject(tag);
            if (gameObject != null)
                return gameObject;
        }

        return null;
    }

    /**
     * Finds all gameObjects with the given tag.
     * @param tag the gameObjects tag.
     * @param out the output array (can be null)
     * @return all gameObjects with the given tag or an empty array if none found.
     */
    public Array<GameObject> findGameObjects(String tag, Array<GameObject> out) {
        if (out == null)
            out = new Array<>();

        Array<GameObject> temp = new Array<>();
        for (Layer layer : layers) {
            temp.clear();
            out.addAll(layer.findGameObjects(tag, temp));
        }

        return out;
    }

    protected GameObject findGameObject(String tag, Layer layer) {
        if (layer == null)
            return null;

        return layer.findGameObject(tag);
    }

    protected Array<GameObject> findGameObjects(String tag, Layer layer, Array<GameObject> out) {
        if (layer == null)
            return new Array<>();

        if (out == null)
            out = new Array<>();

        return layer.findGameObjects(tag, out);
    }

    /**
     * Given a layer's name, finds the first gameObject with the given tag.
     * @param tag the gameObject's tag.
     * @param layerName the layer's name
     * @return the first gameObject with the given tag or null if neither the gameObject or the layer exists.
     */
    public GameObject findGameObject(String tag, String layerName) {
        return findGameObject(tag, findLayer(layerName));
    }

    /**
     * Given a layer's name, finds all gameObjects with the given tag.
     * @param tag the gameObject's tag.
     * @param layerName the layer's name
     * @return all gameObjects with the given tag or an empty array if neither the gameObject or the layer exists.
     */
    public Array<GameObject> findGameObjects(String tag, String layerName, Array<GameObject> out) {
        if (out == null)
            out = new Array<>();

        return findGameObjects(tag, findLayer(layerName), out);
    }

    /**
     * Sets the background color of this scene if it uses a {@link GameCamera2d}
     * @param color the background color
     */
    public void setBackgroundColor(Color color) {
        GameCamera camera = getMainCamera();
        
        if (camera instanceof GameCamera2d)
            ((GameCamera2d)camera).setBackgroundColor(color);
    }

    /**
     * Calls the given iteration listener on all game objects in this scene
     * This is the recommended way to iterate through the game objects in a scene
     * @param iterationListener the iteration listener
     */
    public void forEachGameObject(GameObjectIterationListener iterationListener) {
        fetchGameObjects();
        PoolableArrayIterator<GameObject> iterator = gameObjectIteratorPool.obtain();

        while (iterator.hasNext())
            iterationListener.onIterate(iterator.next());

        gameObjectIteratorPool.free(iterator);
    }

    private void fetchGameObjects() {
        gameObjects.clear();
        gameObjects = getGameObjects(gameObjects);
    }

    /**
     * Called when the screen is resized.
     * <strong>DO NOT CALL THIS METHOD!</strong>
     * @param width the new width
     * @param height the new height
     */
    public void __resize(int width, int height) {
        this.resizedWidth = width;
        this.resizedHeight = height;

        forEachGameObject(gameObject -> gameObject.forEachComponent(resizeIter));

        uiStage.getViewport().update(width, height, true);
    }

    /**
     * Called when the scene is resumed.
     * <strong>DO NOT CALL THIS METHOD!</strong>
     */
    public void __resume() {
        isActive = true;

        forEachGameObject(gameObject -> gameObject.forEachComponent(resumeIter));
    }

    /**
     * Called when this scene is paused.
     * <strong>DO NOT CALL THIS METHOD!</strong>
     */
    public void __pause() {
        isActive = false;

        forEachGameObject(gameObject -> gameObject.forEachComponent(pauseIter));
    }

    protected void updateComponents() {
        forEachGameObject(gameObject -> gameObject.forEachComponent(preUpdateIter));
        forEachGameObject(gameObject -> gameObject.forEachComponent(updateIter));
        forEachGameObject(gameObject -> gameObject.forEachComponent(postUpdateIter));
    }

    /**
     * Called when this scene is updated.
     * <strong>DO NOT CALL THIS METHOD!</strong>
     * @param deltaTime the time difference between this frame and the previous frame
     */
    public void __update(final float deltaTime) {
        this.deltaTime = deltaTime;

        input.__update();
        updateComponents();
        uiStage.act(deltaTime);
    }

    /**
     * Called when this scene is rendered.
     * <strong>DO NOT CALL THIS METHOD!</strong>
     */
    public void __render() {
        // Render
        render();

        // Render debug drawings
        if (renderDebugLines)
            renderDebugDrawings();

        // Draw the UI
        uiStage.draw();
    }

    protected void render() {
        // Before Render
        forEachGameObject(gameObject -> gameObject.forEachComponent(preRenderIter));

        // Render
        forEachGameObject(gameObject -> gameObject.forEachComponent(renderIter));

        // After Render
        forEachGameObject(gameObject -> gameObject.forEachComponent(postRenderIter));
    }

    protected void renderDebugDrawings() {
        shapeRenderer.setProjectionMatrix(shapeRendererCamera.getCamera().combined);

        // Filled
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        forEachGameObject(gameObject -> gameObject.forEachComponent(renderShapeFilledIter));
        shapeRenderer.end();

        // Line
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        forEachGameObject(gameObject -> gameObject.forEachComponent(renderShapeLinedIter));
        shapeRenderer.end();

        // Point
        shapeRenderer.begin(ShapeRenderer.ShapeType.Point);
        forEachGameObject(gameObject -> gameObject.forEachComponent(renderShapePointIter));
        shapeRenderer.end();
    }

    /**
     * Called when this scene is getting destroyed.
     * <strong>DO NOT CALL THIS METHOD!</strong>
     */
    public void __destroy() {
        forEachGameObject(gameObject -> gameObject.forEachComponent(destroyIter));

        uiStage.dispose();
    }

    /**
     * Called when a transition was made from this scene to another scene.
     * <strong>This will still be called even when there was no transition animation set to enter this scene!</strong>
     * @param nextScene the scene that was transitioned to
     */
    public void transitionedFromThisScene(Scene nextScene) {}

    /**
     * Called when a transition was made from another scene to this scene.
     * <strong>This will still be called even when there was no transition animation set to enter this scene!</strong>
     * @param previousScene the scene that was transitioned from
     */
    public void transitionedToThisScene(Scene previousScene) {}

    /**
     * Called when this scene needs to pause before scenes are switched.
     * This is useful for pausing stuffs that you don't need to be active during transition.
     * <strong>This will be called even if no transition animation is set.</strong>
     */
    public void pauseForTransition() {}

    /**
     * A convenient method for quickly creating a game object that renders a sprite ({@link TextureRegion}).
     * <strong>The returned game object is not added to the scene; you have to add it yourself!</strong>
     * @param tag a tag for the game object.
     * @param sprite a {@link TextureRegion} to render
     * @param gameWorldUnits a {@link GameWorldUnits} instance used for converting the sprite's pixel dimensions to world units
     * @return the created game object
     */
    public GameObject newSpriteObject(String tag, TextureRegion sprite,
                                      GameWorldUnits gameWorldUnits) {
        GameObject go = GameObject.newInstance(tag);
        SpriteRenderer sr = new SpriteRenderer(sprite, gameWorldUnits);
        go.addComponent(sr);

        return go;
    }

    /**
     * A convenient method for quickly creating a game object that renders a sprite ({@link TextureRegion}).
     * The current {@link GameWorldUnits} instance will be used for unit conversions.
     * <strong>The returned game object is not added to the scene; you have to add it yourself!</strong>
     * @param tag a tag for the game object.
     * @param sprite a {@link TextureRegion} to render
     * @return the created game object
     */
    public GameObject newSpriteObject(String tag, TextureRegion sprite)
    { return newSpriteObject(tag, sprite, gameWorldUnits); }

    /**
     * A convenient method for quickly creating a game object that renders a sprite ({@link TextureRegion}).
     * <strong>The returned game object is not added to the scene; you have to add it yourself!</strong>
     * @param sprite a {@link TextureRegion} to render
     * @param gameWorldUnits a {@link GameWorldUnits} instance used for converting the sprite's pixel dimensions to world units
     * @return the created game object
     */
    public GameObject newSpriteObject(TextureRegion sprite, GameWorldUnits gameWorldUnits)
    { return newSpriteObject("Untagged", sprite, gameWorldUnits); }

    /**
     * A convenient method for quickly creating a game object that renders a sprite ({@link TextureRegion}).
     * The current {@link GameWorldUnits} instance will be used for unit conversions.
     * <strong>The returned game object is not added to the scene; you have to add it yourself!</strong>
     * @param sprite a {@link TextureRegion} to render
     * @return the created game object
     */
    public GameObject newSpriteObject(TextureRegion sprite)
    { return newSpriteObject("Untagged", sprite); }

    /**
     * A convenient method for quickly creating a game object that renders a sprite ({@link TextureRegion}).
     * <strong>The returned game object is not added to the scene; you have to add it yourself!</strong>
     * @param tag a tag for the game object.
     * @param sprite a {@link Texture} to render. <strong>The entire texture will be rendered!</strong>
     * @param gameWorldUnits a {@link GameWorldUnits} instance used for converting the sprite's pixel dimensions to world units
     * @return the created game object
     */
    public GameObject newSpriteObject(String tag, Texture sprite, GameWorldUnits gameWorldUnits)
    { return newSpriteObject(tag, new TextureRegion(sprite), gameWorldUnits); }

    /**
     * A convenient method for quickly creating a game object that renders a sprite ({@link TextureRegion}).
     * The current {@link GameWorldUnits} instance will be used for unit conversions.
     * <strong>The returned game object is not added to the scene; you have to add it yourself!</strong>
     * @param tag a tag for the game object.
     * @param sprite a {@link Texture} to render. <strong>The entire texture will be rendered!</strong>
     * @return the created game object
     */
    public GameObject newSpriteObject(String tag, Texture sprite)
    { return newSpriteObject(tag, sprite, gameWorldUnits); }

    /**
     * A convenient method for quickly creating a game object that renders a sprite ({@link TextureRegion}).
     * The current {@link GameWorldUnits} instance will be used for unit conversions.
     * <strong>The returned game object is not added to the scene; you have to add it yourself!</strong>
     * @param sprite a {@link Texture} to render. <strong>The entire texture will be rendered!</strong>
     * @return the created game object
     */
    public GameObject newSpriteObject(Texture sprite)
    { return newSpriteObject("Untagged", sprite); }

    /**
     * An iteration listener that can be used to iterate through the game objects of a scene.
     */
    public interface GameObjectIterationListener {
        void onIterate(GameObject gameObject);
    }

    private static class GameObjectIteratorPool extends Pool<PoolableArrayIterator<GameObject>> {
        private final Array<GameObject> gameObjectArray;

        public  GameObjectIteratorPool(Array<GameObject> gameObjectArray) {
            this.gameObjectArray = gameObjectArray;
        }

        @Override
        protected PoolableArrayIterator<GameObject> newObject() {
            return new PoolableArrayIterator<>(gameObjectArray);
        }
    }
}
