package com.isoterik.racken._2d.components.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.isoterik.racken.Component;
import com.isoterik.racken.GameCamera;
import com.isoterik.racken.Transform;
import com.isoterik.racken._2d.GameCamera2d;
import com.isoterik.racken.utils.GameWorldUnits;

/**
 * Renders sprites ({@link Texture} or {@link TextureRegion}) when attached to a gameObject.
 * It uses the default mainCamera of the scene by default but this can be changed.
 *
 * @author imranabdulmalik
 */
public class SpriteRenderer extends Component {
    protected TextureRegion sprite;

    protected Color color;

    protected boolean flipX, flipY;
    protected boolean cull;
    protected boolean visible;

    protected GameWorldUnits gameWorldUnits;

    /**
     * Creates a new instance given a sprite and {@link GameWorldUnits} to use for converting the sprite dimension to world units.
     * @param sprite an instance of {@link TextureRegion}
     * @param gameWorldUnits an instance of {@link GameWorldUnits}
     */
    public SpriteRenderer(TextureRegion sprite, GameWorldUnits gameWorldUnits) {
        this.gameWorldUnits = gameWorldUnits;

        this.sprite = sprite;
        this.color  = new Color(1f, 1f, 1f, 1f);
        flipX       = false;
        flipY       = false;
        cull        = true;
        visible     = true;
    }

    /**
     * Creates a new instance given a {@link TextureRegion} and {@link GameWorldUnits} to use for converting the sprite dimension to world units.
     * <strong>Note:</strong> the entire {@link Texture} will be used as the sprite. This constructor isn't useful if the {@link Texture} is a sprite sheet (atlas)
     * @param sprite an instance of {@link Texture}
     * @param gameWorldUnits an instance of {@link GameWorldUnits}
     */
    public SpriteRenderer(Texture sprite, GameWorldUnits gameWorldUnits)
    { this(new TextureRegion(sprite), gameWorldUnits); }

    /**
     * Changes the visibility of the sprite.
     * <strong>Note:</strong> this only affects the sprite and not the game object itself
     * @param visible whether sprite should be rendered or not
     */
    public void setVisible(boolean visible)
    { this.visible = visible; }

    /**
     *
     * @return whether sprite is currently rendered or not
     */
    public boolean isVisible()
    { return visible; }

    /**
     * Whether this sprite should be culled. When enabled, sprite will be rendered only if the game object can be seen by the camera.
     * This significantly reduces processor load. It is enabled by default
     * @param cull whether culling should be enabled for this renderer
     */
    public void setCull(boolean cull)
    { this.cull = cull; }

    /**
     *
     * @return whether culling is enabled
     */
    public boolean isCull()
    { return cull; }

    /**
     * Sets the sprite ({@link TextureRegion}) for this renderer. The host game object will be resized to fit the dimensions of the sprite.
     * The {@link GameWorldUnits} given will be used for converting the sprite dimension to world units.
     * @param sprite an instance of {@link TextureRegion}
     * @param gameWorldUnits an instance of {@link GameWorldUnits}
     */
    public void setSprite(TextureRegion sprite, GameWorldUnits gameWorldUnits) {
        this.sprite = sprite;
        this.gameWorldUnits = gameWorldUnits;
        setWorldSize();
    }

    /**
     * Sets the sprite ({@link TextureRegion}) for this renderer. The host game object will be resized to fit the dimensions of the sprite.
     * The default {@link GameWorldUnits} provided during construction will be used for converting the sprite dimension to world units.
     * @param sprite an instance of {@link TextureRegion}
     */
    public void setSprite(TextureRegion sprite)
    { setSprite(sprite, gameWorldUnits); }

    /**
     *
     * @return the sprite used for rendering
     */
    public TextureRegion getSprite()
    { return sprite; }

    /**
     * Use this to tint the color of the rendered sprite. Setting the color to {@link Color#WHITE} renders the original sprite with no tint
     * @param color the color to used for tinting the sprite
     */
    public void setColor(Color color)
    { this.color = color; }

    /**
     *
     * @return the color to used for tinting the sprite
     */
    public Color getColor()
    { return color; }

    /**
     * Sets the opacity of the rendered sprite on a scale of (0 - 1) where 0 means 0% opaque (completely transparent) and 1 means 100% opaque
     * @param opacity the opacity
     */
    public void setOpacity(float opacity)
    { color.a = opacity; }

    /**
     *
     * @return the opacity of the rendered sprite
     */
    public float getOpacity()
    { return color.a; }

    /**
     * Determines whether the sprite should be flipped horizontally. Useful for mirroring sprites
     * @param flipX whether the sprite should be flipped horizontally
     */
    public void setFlipX(boolean flipX)
    { this.flipX = flipX; }

    /**
     *
     * @return whether the sprite is flipped horizontally
     */
    public boolean isFlipX()
    { return flipX; }

    /**
     * Determines whether the sprite should be flipped vertically. Useful for mirroring sprites
     * @param flipY whether the sprite should be flipped vertically
     */
    public void setFlipY(boolean flipY)
    { this.flipY = flipY; }

    /**
     *
     * @return whether the sprite is flipped vertically
     */
    public boolean isFlipY()
    { return flipY; }

    /**
     * Converts the dimensions of the sprite to world units then use it as the dimension for the host game object.
     * The origin is also shifted to the center of the game object.
     * No change is made if there is currently no host game object.
     */
    public void setWorldSize() {
        if (gameObject == null)
            return;

        Vector2 worldSize = gameWorldUnits.toWorldUnit(sprite);
        gameObject.transform.setSize(worldSize.x, worldSize.y);
        gameObject.transform.setOrigin(worldSize.x * .5f,
                worldSize.y * .5f, 0);
    }

    /**
     * Once this component is attached, the host game object will be resized to fit the dimensions of the sprite.
     * {@inheritDoc}
     */
    @Override
    public void attach() {
        // Set up the world size of this sprite once we have a game object
        setWorldSize();
    }

    @Override
    public void render() {
        // Render only if visible
        if (!visible)
            return;

        // Only GameCamera2d can be used with this component
        if (renderCamera == null || !(renderCamera instanceof GameCamera2d))
            return;

        // If culling, the sprite should be rendered only if it can be seen by the camera
        if (cull) {
            if (gameObject.transform.isInCameraFrustum(renderCamera.getCamera())) {
                drawSprite((GameCamera2d) renderCamera);
            }
        }
        else {
            drawSprite((GameCamera2d) renderCamera);
        }
    }

    /**
     * Renders the sprite to the screen.
     * @param gameCamera the camera to use.
     */
    protected void drawSprite(GameCamera2d gameCamera) {
        SpriteBatch batch = gameCamera.getSpriteBatch();
        batch.setColor(color);

        Vector3 pos    = gameObject.transform.position;
        Vector3 size   = gameObject.transform.size;
        Vector3 scale  = gameObject.transform.scale;
        Vector3 origin = gameObject.transform.origin;
        float rotation = gameObject.transform.getRotation();

        // Draw the sprite
        batch.draw(sprite.getTexture(),
                pos.x, pos.y, origin.x, origin.y,
                size.x, size.y, scale.x, scale.y,
                rotation, sprite.getRegionX(), sprite.getRegionY(),
                sprite.getRegionWidth(), sprite.getRegionHeight(),
                flipX, flipY);
    }
}
