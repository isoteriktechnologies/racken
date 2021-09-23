package com.isoterik.racken;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.isoterik.racken._2d.scenes.transition.ISceneTransition;
import com.isoterik.racken.asset.GameAssetsLoader;
import com.isoterik.racken.audio.AudioManager;
import com.isoterik.racken.input.InputManager;

/**
 * Environment class holding references to {@link Application}, {@link Graphics}, {@link GameAssetsLoader}, {@link SceneManager} and
 * {@link AudioManager} instances.
 * The references are held in public final fields which allows access to all sub systems. Use {@link #instance()} to get the shared instance.
 *
 * @author isoteriksoftware
 */
public final class Racken {
    private static Racken instance;

    /** A reference to libGDXs {@link Application} instance. This is not a replacement or an extension of it. */
    public final Application app;

    /** A reference to libGDXs {@link Graphics} instance. This is not a replacement or an extension of it. */
    public final Graphics graphics;

    /** A reference to the shared instance of {@link GameAssetsLoader} for managing game assets. */
    public final GameAssetsLoader assets;

    /** A reference to the shared instance of {@link AudioManager} for managing game audio. */
    public final AudioManager audio;

    /** A reference to the shared instance of {@link SceneManager} for scene management. */
    public final SceneManager sceneManager;

    /** A reference to the shared instance of {@link DefaultSettings} */
    public final DefaultSettings defaultSettings;

    private float deltaTime;

    /**
     * Initializes MinGDX.
     * DO NOT CALL THIS METHOD
     */
    public static void __init()
    { instance = new Racken(); }

    /**
     *
     * @return an instance of {@link Racken}
     */
    public static Racken instance()
    { return instance; }

    private Racken() {
        app = Gdx.app;
        graphics = Gdx.graphics;

        AudioManager.__init();
        this.audio = AudioManager.instance();

        GameAssetsLoader.__init();
        this.assets = GameAssetsLoader.instance();

        SceneManager.__init();
        this.sceneManager = SceneManager.instance();

        InputManager.__initPools();

        defaultSettings = new DefaultSettings();
    }

    /**
     * A convenient method for quickly transitioning between scenes.
     * @param scene the scene to show next.
     * @param transition a transition to use. Can be null. If it is null then no transition is used.
     */
    public void setScene(Scene scene, ISceneTransition transition)
    { sceneManager.setCurrentScene(scene, transition); }

    /**
     * A convenient method for quickly switching scenes.
     * @param scene the scene to show next.
     */
    public void setScene(Scene scene)
    { setScene(scene, null); }

    /**
     * Terminates the application.
     */
    public void exit()
    { app.exit(); }

    /**
     * Called when the application is resumed.
     * DO NOT CALL THIS METHOD
     */
    public void __resume()
    { sceneManager.__resume(); }

    /**
     * Called when the application is paused.
     * DO NOT CALL THIS METHOD
     */
    public void __pause()
    { sceneManager.__pause(); }

    /**
     * Called when the screen size changes.
     * DO NOT CALL THIS METHOD
     * @param width the new width
     * @param height the new height
     */
    public void __resize(int width, int height)
    { sceneManager.__resize(width, height); }

    /**
     * Called when the application is ready to render.
     * DO NOT CALL THIS METHOD
     */
    public void __render()
    {
        deltaTime = Math.min(Gdx.graphics.getDeltaTime(),   1.0f / 60.0f);
        sceneManager.__render();
    }

    /**
     * Called when the application is getting destroyed.
     * DO NOT CALL THIS METHOD
     */
    public void __dispose() {
        sceneManager.__dispose();
        assets.__dispose();
    }

    /**
     *
     * @return the time difference between this frame and the previous frame.
     */
    public float getDeltaTime()
    { return deltaTime; }

    /**
     * This class defines default settings for some features of xgdx. The settings can always be changed.
     *
     * It is useful for setting values that don't change or is not expected to change often.
     */
    public static class DefaultSettings {
        /* Camera settings */
        /** The default viewport width (in pixels) of a camera. */
        public float VIEWPORT_WIDTH  = 1280f;

        /** The default viewport height (in pixels) of a camera. */
        public float VIEWPORT_HEIGHT = 720f;

        /** The default number of pixels that equals one world unit. */
        public float PIXELS_PER_UNIT = 100f;

        /** the default camera field of view of the height, in degrees, the field of view for the width will be calculated
            according to the aspect ratio. */
        public float CAMERA_NEAR = 1f;

        /** the default camera near clipping plane distance, has to be positive */
        public float CAMERA_FAR = 300f;

        /** the default camera far clipping plane distance, has to be positive */
        public float CAMERA_FIELD_OF_VIEW = 67f;
    }
}