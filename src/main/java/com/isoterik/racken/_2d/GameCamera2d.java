package com.isoterik.racken._2d;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.isoterik.racken.GameCamera;
import com.isoterik.racken.Racken;
import com.isoterik.racken.utils.GameWorldUnits;

/**
 * A game camera that wraps an {@link OrthographicCamera} for rendering in 2D space.
 *
 * @author imranabdulmalik
 */
public class GameCamera2d extends GameCamera {
    protected SpriteBatch spriteBatch;

    /**
     * Creates a new instance given a viewport.
     * * <strong>Note:</strong> an {@link OrthographicCamera} will be created if it doesn't exist.
     * @param viewport the viewport
     */
    public GameCamera2d(Viewport viewport) {
        super(viewport);
        spriteBatch = new SpriteBatch();
        centerCameraOnResize = true;
    }

    /**
     * Creates a new instance given an instance of {@link GameWorldUnits} for unit conversions.
     * The viewport defaults to a {@link FitViewport}.
     * @param gameWorldUnits an instance of {@link GameWorldUnits}
     */
    public GameCamera2d(GameWorldUnits gameWorldUnits) {
        this(new FitViewport(gameWorldUnits.getWorldWidth(), gameWorldUnits.getWorldHeight()));
    }

    /**
     * Creates a new scene. The screen dimensions are taken from {@link Racken#defaultSettings}
     * The viewport defaults to a {@link FitViewport}.
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

    @Override
    public OrthographicCamera getCamera()
    { return (OrthographicCamera)camera; }

    @Override
    public void setup(Viewport viewport) {
        if (camera == null || !(camera instanceof OrthographicCamera)) {
            camera = new OrthographicCamera(viewport.getWorldWidth(), viewport.getWorldHeight());
            getCamera().setToOrtho(false, viewport.getWorldWidth(), viewport.getWorldHeight());
            viewport.setCamera(camera);
            camera.update();
        }

        super.setup(viewport);
    }

    public void __destroy() {
        if (spriteBatch != null) {
            spriteBatch.dispose();
            spriteBatch = null;
        }
    }

    @Override
    public void __preRender() {
        super.__preRender();

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
    }

    public void __postRender() {
        spriteBatch.end();
    }
}
