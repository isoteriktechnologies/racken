package com.isoterik.racken;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.isoterik.racken.utils.GameWorldUnits;

/**
 * A GameCamera encapsulates a {@link Camera} object. Concrete subclasses makes use of a specific kind of camera.
 * The class also uses {@link GameWorldUnits} object to define the visible regions of the world at a time (viewport) and also for unit conversions.
 *
 * @see com.isoterik.racken._2d.GameCamera2d
 *
 * @author imranabdulmalik
 */
public abstract class GameCamera {
    protected Camera camera;

    protected Viewport viewport;

    protected boolean centerCameraOnResize = true;

    /**
     * Creates a new instance given a viewport.
     * <strong>Note:</strong> the {@link Camera} is retrieved from the viewport passed.
     * @param viewport the viewport
     */
    public GameCamera(Viewport viewport) {
        this.viewport = viewport;
        setup(viewport);
    }

    /**
     * Creates a new instance given an instance of {@link GameWorldUnits} for viewport dimensions. The viewport defaults to an {@link ExtendViewport}.
     * @param gameWorldUnits an instance of {@link GameWorldUnits}
     * @param camera the camera to use
     */
    public GameCamera(GameWorldUnits gameWorldUnits, Camera camera)
    { this(new ExtendViewport(gameWorldUnits.getWorldWidth(), gameWorldUnits.getWorldHeight(), camera)); }

    /**
     * Changes the viewport used by this camera
     * @param viewport the new viewport
     */
    public void setup(Viewport viewport) {
        this.viewport = viewport;
        this.camera = viewport.getCamera();
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    /**
     * Returns the viewport
     * @return the viewport
     */
    public Viewport getViewport() {
        return viewport;
    }

    /**
     *
     * @return the viewport width
     */
    public float getViewportWidth()
    { return camera.viewportWidth; }

    /**
     *
     * @return the viewport height
     */
    public float getViewportHeight()
    { return camera.viewportHeight; }

    /**
     * @return true if the camera is centered when the screen is resized. false otherwise.
     */
    public boolean isCenterCameraOnResize() {
        return centerCameraOnResize;
    }

    /**
     * When set to true, the camera will be centered whenever the screen size changes
     * @param centerCameraOnResize if the camera should be centered
     */
    public void setCenterCameraOnResize(boolean centerCameraOnResize) {
        this.centerCameraOnResize = centerCameraOnResize;
    }

    /**
     *
     * @return the {@link Camera} object used internally
     */
    public Camera getCamera()
    { return camera; }

    /**
     * Called when the screen is resized
     * @param newScreenWidth the new screen width in pixels
     * @param newScreenHeight the new screen height in pixels
     */
    public void __resize(int newScreenWidth, int newScreenHeight) {
        viewport.update(newScreenWidth, newScreenHeight, centerCameraOnResize);
    }

    /**
     * Called when this camera should render
     */
    public abstract void __preRender();

    /**
     * Called when this camera should stop rendering
     */
    public abstract void __postRender();

    /**
     * Called when this camera should dispose any used resource
     */
    public abstract void __destroy();
}