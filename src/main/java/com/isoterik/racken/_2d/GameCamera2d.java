package com.isoterik.racken._2d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.isoterik.racken.GameCamera;
import com.isoterik.racken.Racken;
import com.isoterik.racken.utils.GameWorldUnits;

/**
 * A game camera that wraps an {@link OrthographicCamera} for rendering in 2D space.
 *
 * @author isoteriksoftware
 */
public class GameCamera2d extends GameCamera {
    protected SpriteBatch spriteBatch;

    protected Color backgroundColor;

    /**
     * Creates a new instance given a viewport.
     * * <strong>Note:</strong> an {@link OrthographicCamera} will be created if it doesn't exist.
     * @param viewport the viewport
     */
    public GameCamera2d(Viewport viewport) {
        super(viewport);
        spriteBatch = new SpriteBatch();
        this.backgroundColor = new Color(1, 0, 0, 1);
        centerCameraOnResize = true;
    }

    /**
     * Creates a new instance given an instance of {@link GameWorldUnits} for unit conversions. The viewport defaults to an {@link ExtendViewport}.
     * @param gameWorldUnits an instance of {@link GameWorldUnits}
     */
    public GameCamera2d(GameWorldUnits gameWorldUnits) {
        this(new ExtendViewport(gameWorldUnits.getWorldWidth(), gameWorldUnits.getWorldHeight()));
    }

    /**
     * Creates a new scene. The screen dimensions are taken from {@link Racken#defaultSettings}
     * The viewport defaults to an {@link ExtendViewport}.
     */
    public GameCamera2d() {
        this(new GameWorldUnits(Racken.instance().defaultSettings.VIEWPORT_WIDTH, Racken.instance().defaultSettings.VIEWPORT_HEIGHT,
                Racken.instance().defaultSettings.PIXELS_PER_UNIT));
    }

    /**
     * Sets the sprite batch used for rendering
     * @param spriteBatch the sprite batch
     */
    public void setSpriteBatch(SpriteBatch spriteBatch)
    { this.spriteBatch = spriteBatch; }

    /**
     *
     * @return the sprite batch used for rendering
     */
    public SpriteBatch getSpriteBatch()
    { return spriteBatch; }

    /**
     * Sets the color used for clearing the scene every frame.
     * @param backgroundColor color used for clearing the scene every frame
     */
    public void setBackgroundColor(Color backgroundColor)
    { this.backgroundColor = backgroundColor; }

    /**
     *
     * @return color used for clearing the scene every frame
     */
    public Color getBackgroundColor()
    { return backgroundColor; }

    @Override
    public OrthographicCamera getCamera()
    { return (OrthographicCamera)camera; }

    @Override
    public void setup(Viewport viewport) {
        super.setup(viewport);

        if (camera == null || !(camera instanceof PerspectiveCamera)) {
            camera = new OrthographicCamera(viewport.getWorldWidth(), viewport.getWorldHeight());
            getCamera().setToOrtho(false, viewport.getWorldWidth(), viewport.getWorldHeight());
            viewport.setCamera(camera);
            camera.update();
        }

        viewport.apply(centerCameraOnResize);
    }

    @Override
    public void attach() {
        if (spriteBatch == null)
            spriteBatch = new SpriteBatch();
    }

    @Override
    public void detach() {
        if (spriteBatch != null) {
            spriteBatch.dispose();
            spriteBatch = null;
        }
    }

    @Override
    public void destroy() {
        if (spriteBatch != null) {
            spriteBatch.dispose();
            spriteBatch = null;
        }
    }

    @Override
    public void preRender() {
        ScreenUtils.clear(backgroundColor);
        camera.update();
        viewport.apply(centerCameraOnResize);
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
    }

    @Override
    public void postRender() {
        spriteBatch.end();
    }
}





























