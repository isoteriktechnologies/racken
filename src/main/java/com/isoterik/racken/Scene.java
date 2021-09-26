package com.isoterik.racken;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.isoterik.racken._2d.GameCamera2d;
import com.isoterik.racken._2d.components.renderer.SpriteRenderer;
import com.isoterik.racken.input.InputManager;
import com.isoterik.racken.utils.GameWorldUnits;

/**
 * A Scene contains the {@link GameObject}s of your game. Think of each Scene as a unique level of your game.
 * Every scene has its own {@link InputManager} for managing input.
 * <p>
 * A {@link GameCamera} is used to display a portion of the scene or the whole scene at a time. While its possible to use multiple cameras, scenes currently
 * support only one main camera for projection.
 * <p>
 *
 * GameObjects are processed top-down; game objects added first are processed first (this can be used to manipulate how GameObjects are rendered.)
 * <p>
 * Every scene has a {@link Stage} instance for working with UI elements. The stage is already setup to update, receive input and render; you don't have do these yourself.
 *
 * @author imranabdulmalik
 */
public class Scene {
    /** A reference to the shared instance of {@link Racken} */
    protected Racken racken;

    /** The default {@link GameWorldUnits} used for this scene */
    protected GameWorldUnits gameWorldUnits;

    /** The input manager for handling input. */
    protected final InputManager input;

    /* For components iteration that needs the current delta time */
    private float deltaTime;

    // These iteration listeners prevent us from creating new instances every time!
    protected GameObject.ComponentIterationListener startIter, pauseIter, preRenderIter, postRenderIter,
            resumeIter, preUpdateIter, updateIter, resizeIter, postUpdateIter, renderIter,
            debugLineIter, debugFilledIter, debugPointIter, destroyIter;

    // The state of the Scene
    private boolean isActive;

    /** {@link com.badlogic.gdx.scenes.scene2d.Stage} instance used for managing UI elements */
    protected Stage uiCanvas;

    /** ShapeRenderer for debug drawings */
    protected ShapeRenderer shapeRenderer;

    /** This flag determines whether custom debug renderings should be done. */
    protected boolean renderCustomDebugLines;

    /** Determines whether this stack can be stacked. */
    protected boolean stackable = true;

    /** The main camera for this scene */
    protected GameCamera mainCamera;

    private int resizedWidth, resizedHeight;

    /** The game objects in this scene */
    protected final SnapshotArray<GameObject> gameObjects = new SnapshotArray<>(GameObject.class);

    /** The cameras of this scene */
    protected final Array<GameCamera> cameras = new Array<>();

    /** The clear color */
    protected Color backgroundColor;

    /**
     * Creates a new instance.
     */
    public Scene() {
        racken = Racken.instance();

        onCreate();

        gameWorldUnits = new GameWorldUnits(racken.defaultSettings.VIEWPORT_WIDTH, racken.defaultSettings.VIEWPORT_HEIGHT,
                racken.defaultSettings.PIXELS_PER_UNIT);

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

        debugLineIter = component -> {
            if (component.isEnabled())
                component.renderShapeLine(shapeRenderer);
        };

        debugFilledIter = component -> {
            if (component.isEnabled())
                component.renderShapeFilled(shapeRenderer);
        };

        debugPointIter = component -> {
            if (component.isEnabled())
                component.renderShapePoint(shapeRenderer);
        };

        destroyIter = Component::destroy;

        this.backgroundColor = new Color(1, 0, 0, 1);

        mainCamera = new GameCamera2d();
        addCamera(mainCamera);

        setupUICanvas(new StretchViewport(gameWorldUnits.getScreenWidth(),
                gameWorldUnits.getScreenHeight()));

        shapeRenderer = new ShapeRenderer();
    }

    /**
     * This is called during construction before instance fields are initialized. This is useful for setting default properties
     * that will be used during construction.
     *
     * <strong>Only {@link #racken} is not null, most instance fields are not initialized yet, it is not safe to make use of them here!</strong>
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
     * Stackable scenes are scenes that can be added to a stack when the {@link SceneManager} switches scenes. Instances of stackable scenes are always retained and can
     * be switched back to using the same instance. Scenes that are not stackable are disposed as soon as the scene manager switches from them.
     * <p>
     * <strong>A good rule of thumb:</strong>
     * <ul>
     *     <li>
     *         If the scene takes a considerable amount of time to load resources and the scene is very likely to be returned to then it may be a
     *         good choice to make it stackable. That way the resources are loaded only once.
     *     </li>
     *     <li>
     *         If the scene is very resource intensive and other scenes need to be loaded then it may be a good
     *         idea to NOT make it stackable. That way the resources allocated by that scene is disposed as soon as it is no longer needed.
     *     </li>
     *     <li>
     *         If the scene is a UI scene (like a main menu scene) then it may be a good idea to make it stackable since UI scenes are usually visited many times.
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
     * @param renderCustomDebugLines whether custom debug lines are rendered
     */
    public void setRenderDebugLines(boolean renderCustomDebugLines)
    { this.renderCustomDebugLines = renderCustomDebugLines; }

    /**
     *
     * @return whether custom debug lines are rendered or not
     */
    public boolean isRenderDebugLines()
    { return renderCustomDebugLines; }

    /**
     * By default, the ui canvas (an instance of {@link Stage}) is setup with an {@link com.badlogic.gdx.utils.viewport.StretchViewport}.
     * Use this method to change the viewport to your desired viewport.
     * @param viewport a viewport for scaling UI elements
     */
    public void setupUICanvas(Viewport viewport) {
        if (uiCanvas != null)
            input.getInputMultiplexer().removeProcessor(uiCanvas);

        uiCanvas = new Stage(viewport);
        input.getInputMultiplexer().addProcessor(uiCanvas);
    }

    /**
     *
     * @return the {@link Stage} used for managing UI elements.
     */
    public Stage getUICanvas()
    { return uiCanvas; }

    /**
     * A scene becomes active when the scene is resumed. It goes back to an inactive state when the scene is paused.
     * @return whether this scene is active or not
     */
    public boolean isActive()
    { return isActive; }

    /**
     * Returns the InputManager for this scene
     * @return the input manager for this scene
     */
    public InputManager getInput()
    { return input; }

    /**
     * Changes the camera used for projecting this scene.
     * @param mainCamera the {@link GameCamera} for projecting this scene.
     */
    public void setMainCamera(GameCamera mainCamera) {
        removeMainCamera();
        this.mainCamera = mainCamera;
        addCamera(mainCamera);
    }

    /**
     * Returns the main camera used for projecting this scene.
     * @return the main camera used for projecting this scene.
     */
    public GameCamera getMainCamera()
    { return mainCamera; }

    /**
     * Adds a camera to this scene
     * @param camera the camera to add
     */
    public void addCamera(GameCamera camera) {
        if (! cameras.contains(camera, true))
            cameras.add(camera);
    }

    /**
     * Removes a camera from this scene
     * @param camera the camera to remove
     */
    public void removeCamera(GameCamera camera) {
        cameras.removeValue(camera, true);
    }

    /**
     * Removes the main camera of this scene.
     * This will dispose all resources used by the camera too.
     */
    public void removeMainCamera() {
        if (mainCamera != null) {
            mainCamera.__destroy();
            cameras.removeValue(mainCamera, true);
        }
    }

    /**
     * Calls the given iteration listener on every game objects in this scene
     * @param iterationListener the iteration listener
     */
    public void forEachGameObject(GameObject.GameObjectIterationListener iterationListener) {
        GameObject[] array = gameObjects.begin();

        for (GameObject gameObject : array)
            if (gameObject != null)
                iterationListener.onIterate(gameObject);

        gameObjects.end();
    }

    /**
     * Adds a game object to this scene given a layer to add it to.
     * @param gameObject the game object to add
     * @throws IllegalStateException if the game object is a child of another game object
     */
    public void addGameObject(GameObject gameObject) {
        if (gameObject.hasParent())
            throw new IllegalStateException("A GameObject with a parent cannot be added directly to the Scene, " +
                    "the parent should be added instead: " + "GameObject's Tag=" + gameObject.getTag());

        gameObject.__setHostScene(this);
        gameObjects.add(gameObject);
        gameObject.forEachComponent(startIter);
    }

    /**
     * Removes a game object from the default layer.
     * @param gameObject the game object to remove.
     * @return true if the game object was removed. false otherwise.
     */
    public boolean removeGameObject(GameObject gameObject) {
        gameObject.__removeFromScene();
        gameObject.__setHostScene(null);
        return gameObjects.removeValue(gameObject, true);
    }

    /**
     * Returns all the game objects added to this scene
     * @return all the game objects added to this scene
     */
    public SnapshotArray<GameObject> getGameObjects() {
        return gameObjects;
    }

    /**
     * Finds the first gameObject with the given tag.
     * @param tag the gameObject's tag.
     * @return the first gameObject with the given tag or null if none found.
     */
    public GameObject findGameObject(String tag) {
        for (GameObject gameObject : gameObjects) {
            if (gameObject.sameTag(tag))
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

        for (GameObject gameObject : gameObjects) {
            if (gameObject.sameTag(tag))
                out.add(gameObject);
        }

        return out;
    }

    /**
     * Sets the color used for clearing the scene every frame.
     * @param backgroundColor color used for clearing the scene every frame
     */
    public void setBackgroundColor(Color backgroundColor)
    { this.backgroundColor = backgroundColor; }

    /**
     * Returns the color used for clearing the scene every frame
     * @return color used for clearing the scene every frame
     */
    public Color getBackgroundColor()
    { return backgroundColor; }

    /**
     * Called when the screen is resized.
     * <strong>DO NOT CALL THIS METHOD!</strong>
     * @param width the new width
     * @param height the new height
     */
    public void __resize(int width, int height) {
        this.resizedWidth = width;
        this.resizedHeight = height;

        for (GameCamera camera : cameras)
            camera.__resize(width, height);

        forEachGameObject(gameObject -> gameObject.forEachComponent(resizeIter));

        uiCanvas.getViewport().update(width, height, true);
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

    private void updateComponents() {
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

        uiCanvas.act(deltaTime);
    }

    /**
     * Called when this scene is rendered.
     * <strong>DO NOT CALL THIS METHOD!</strong>
     */
    public void __render() {
        // Render
        render();

        // Render debug drawings
        if (renderCustomDebugLines)
            renderDebugDrawings();

        // Draw the UI
        uiCanvas.draw();
    }

    protected void render() {
        ScreenUtils.clear(backgroundColor);

        // Before Render
        forEachGameObject(gameObject -> gameObject.forEachComponent(preRenderIter));

        // Render
        for (GameCamera camera : cameras) {
            camera.__preRender();

            forEachGameObject(gameObject -> gameObject.forEachComponent(component -> {
                if (component.getRenderCamera() == camera)
                    component.render();
            }));

            camera.__postRender();
        }

        // After Render
        forEachGameObject(gameObject -> gameObject.forEachComponent(postRenderIter));
    }

    protected void renderDebugDrawings() {
        if (mainCamera == null)
            return;

        shapeRenderer.setProjectionMatrix(getMainCamera().getCamera().combined);

        // Filled
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        forEachGameObject(gameObject -> gameObject.forEachComponent(debugFilledIter));
        shapeRenderer.end();

        // Line
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        forEachGameObject(gameObject -> gameObject.forEachComponent(debugLineIter));
        shapeRenderer.end();

        // Point
        shapeRenderer.begin(ShapeRenderer.ShapeType.Point);
        forEachGameObject(gameObject -> gameObject.forEachComponent(debugPointIter));
        shapeRenderer.end();
    }

    /**
     * Called when this scene is getting destroyed.
     * <strong>DO NOT CALL THIS METHOD!</strong>
     */
    public void __destroy() {
        forEachGameObject(gameObject -> gameObject.forEachComponent(destroyIter));

        uiCanvas.dispose();

        for (GameCamera camera : cameras)
            camera.__destroy();
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
}