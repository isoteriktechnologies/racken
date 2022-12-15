package com.isoterik.racken;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;

/**
 * The Transform component determines the position, rotation, scale, size and origin of a {@link GameObject}.
 * <p>
 * The coordinate values can be in either local coordinates or world coordinates. For GameObjects with no parent, the
 * coordinates are always in world coordinates. But for GameObjects that have parents, the coordinates are always local
 * to their parents. To grab the world coordinates, use {@link #world()}
 *
 * @author imranabdulmalik
 */
public class Transform extends Component {
    /** The position of the host game object */
    public final Vector3 position;

    /** The scale of the host game object */
    public final Vector3 scale;

    /** The size of the host game object */
    public final Vector3 size;

    /** The origin of the host game object */
    public final Vector3 origin;

    /** The orientation of the host game object */
    public final Vector3 rotation;

    private Transform world;

    protected final Vector3 temp = new Vector3();

    /**
     * Creates a new instance.
     */
    public Transform() {
        position = new Vector3(0, 0, 0);
        scale    = new Vector3(1, 1, 1);
        size     = new Vector3(0, 0, 0);
        origin   = new Vector3(0, 0,  0);
        rotation = new Vector3(0, 0, 0);
    }

    /**
     * Sets the origin of the host game object.
     * @param originX origin on the x-axis
     * @param originY origin on the y-axis
     * @param originZ origin on the z-axis
     */
    public void setOrigin(float originX, float originY, float originZ)
    { this.origin.set(originX, originY, originZ); }

    /**
     * Sets the origin of the host game object.
     * @param originX origin on the x-axis
     * @param originY origin on the y-axis
     */
    public void setOrigin(float originX, float originY)
    { setOrigin(originX, originY, origin.z); }

    /**
     * Sets the origin by taking a percentage of the dimension
     * @param widthPercent the percentage of the width to use
     * @param heightPercent the percentage of the height to use
     * @param depthPercent the percentage of the depth to use
     */
    public void setOriginPercent(float widthPercent, float heightPercent, float depthPercent) {
        origin.set(size.x * widthPercent, size.y * heightPercent, size.z * depthPercent);
    }

    /**
     * Sets the origin by taking a percentage of the dimension
     * @param widthPercent the percentage of the width to use
     * @param heightPercent the percentage of the height to use
     */
    public void setOriginPercent(float widthPercent, float heightPercent) {
        setOriginPercent(widthPercent, heightPercent, 0f);
    }

    /**
     * Sets the origin by taking a percentage of the dimension
     * @param percent the percentage of the dimension to use
     */
    public void setOriginPercent(float percent) {
        setOriginPercent(percent, percent, percent);
    }

    /**
     * Sets the size of the host game object.
     * @param width the width
     * @param height the height
     * @param depth the depth
     */
    public void setSize(float width, float height, float depth)
    { this.size.set(width, height, depth); }

    /**
     * Sets the size of the host game object.
     * @param width the width
     * @param height the height
     */
    public void setSize(float width, float height)
    { setSize(width, height, size.z); }

    public void setWidth(float width) {
        this.size.x = width;
    }

    public void setHeight(float height) {
        this.size.y = height;
    }

    public void setDepth(float depth) {
        this.size.z = depth;
    }

    /**
     * Sets the position of the host game object.
     * @param x position on the x-axis
     * @param y position on the y-axis
     * @param z position on the z-axis
     */
    public void setPosition(float x, float y, float z)
    { this.position.set(x, y, z); }

    /**
     * Sets the position of the host game object.
     * @param x position on the x-axis
     * @param y position on the y-axis
     */
    public void setPosition(float x, float y)
    { setPosition(x, y, position.z); }

    public void setX(float x) {
        this.position.x = x;
    }

    public void setY(float y) {
        this.position.y = y;
    }

    public void setZ(float z) {
        this.position.z = z;
    }

    /**
     * Sets the scale of the host game object.
     * @param scaleX scale on the x-axis
     * @param scaleY scale on the y-axis
     * @param scaleZ scale on the z-axis
     */
    public void setScale(float scaleX, float scaleY, float scaleZ)
    { this.scale.set(scaleX, scaleY, scaleZ); }

    /**
     * Sets the scale of the host game object.
     * @param scaleX scale on the x-axis
     * @param scaleY scale on the y-axis
     */
    public void setScale(float scaleX, float scaleY)
    { setScale(scaleX, scaleY, scale.z); }

    public void setScaleX(float scaleX) {
        this.scale.x = scaleX;
    }

    public void setScaleY(float scaleY) {
        this.scale.y = scaleY;
    }

    public void setScaleZ(float scaleZ) {
        this.scale.z = scaleZ;
    }

    /**
     * Sets the rotation of the host game object.
     * @param rotationX rotation around the x-axis
     * @param rotationY rotation around the y-axis
     * @param rotationZ rotation around the z-axis
     */
    public void setRotation(float rotationX, float rotationY, float rotationZ)
    { this.rotation.set(rotationX, rotationY, rotationZ); }

    /**
     * Sets the rotation around the y-axis. Useful for 2D game objects.
     * @param rotation rotation around the z-axis
     */
    public void setRotation(float rotation)
    { setRotation(0, 0, rotation); }

    /**
     *
     * @return the rotation vector
     */
    public Vector3 getRotationVector()
    { return this.rotation; }

    /**
     *
     * @return rotation around the z-axis
     */
    public float getRotation()
    { return rotation.z; }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }

    public float getZ() {
        return position.z;
    }

    public float getScaleX() {
        return scale.x;
    }

    public float getScaleY() {
        return scale.y;
    }

    public float getScaleZ() {
        return scale.z;
    }

    public float getWidth() {
        return size.x;
    }

    public float getHeight() {
        return size.y;
    }

    public float getDepth() {
        return size.z;
    }

    public float getOriginX() {
        return origin.x;
    }

    public float getOriginY() {
        return origin.y;
    }

    public float getOriginZ() {
        return origin.x;
    }

    public float getRotationX() {
        return rotation.x;
    }

    public float getRotationY() {
        return rotation.y;
    }

    public float getRotationZ() {
        return rotation.z;
    }

    /**
     * Copies this transform to another given transform
     * @param transform the transform to copy to
     */
    public void copyInto(Transform transform) {
        if (transform == null)
            throw new IllegalArgumentException("Target transform cannot be null");

        transform.position.set(position);
        transform.size.set(size);
        transform.origin.set(origin);
        transform.rotation.set(rotation);
        transform.scale.set(scale);
    }

    /**
     * Returns the transform of the host game object in world coordinates.
     * If the host game object is not a child of another game object, then the current transform is copied and returned.
     * If the host game object is a child of another game object, then the transform will be converted from the
     * local parent's coordinates to world coordinates.
     * @param out the output transform. if null is provided, a new transform will be created
     * @return the transform of the host game object in world coordinates.
     */
    public Transform getWorldTransform(Transform out) {
        if (out == null)
            out = new Transform();

        copyInto(out);

        if (gameObject != null && gameObject.hasParent()) {
            GameObject parent = gameObject.getParent();
            Transform parentTransform = parent.transform;

            out.position.add(parentTransform.position);
            out.rotation.add(parentTransform.rotation);
            out.scale.add(parentTransform.scale);
        }

        return out;
    }

    /**
     * Returns the current transform of the host game object in world coordinates.
     * @return the current transform of the host game object in world coordinates
     */
    public Transform world() {
        if (world == null)
            world = new Transform();

        // Convert from local coordinates to world coordinates
        getWorldTransform(world);
        return world;
    }

    /**
     * Translates the host game object.
     * @param x change on the x-axis
     * @param y change on the y-axis
     * @param z change on the z-axis
     */
    public void translate(float x, float y, float z)
    { this.position.add(x, y, z); }

    /**
     * Translates the host game object.
     * @param x change on the x-axis
     * @param y change on the y-axis
     */
    public void translate(float x, float y)
    { translate(x, y, 0);}

    /**
     * Rotates the host game object around the z-axis
     * @param degAngle change in angle (in degrees)
     */
    public void rotate(float degAngle)
    { this.rotation.z += degAngle; }

    /**
     *
     * @return the diagonal of the host game object
     */
    public float calcDiagonal() {
        float w = size.x;
        float h = size.y;
        float d = size.z;
        float scaleX = scale.x;
        float scaleY = scale.y;
        float scaleZ = scale.z;

        return (float)Math.sqrt(Math.pow(w * scaleX, 2f) +
                Math.pow(h * scaleY, 2f) + Math.pow(d * scaleZ, 2f));
    }

    /**
     * Determines whether the transform is currently within a visible area of a given camera.
     * @param camera the camera
     * @return true if the game can be seen by the camera. false otherwise
     */
    public boolean isInCameraFrustum(Camera camera) {
        float x = position.x;
        float y = position.y;
        float z = position.z;
        float w = size.x;
        float h = size.y;
        float d = size.z;

        temp.set(x + w * .5f, y + h *.5f, z + d * .5f);
        return camera.frustum.sphereInFrustum(temp,
                calcDiagonal() * .5f);
    }

    @Override
    public void attach() {
        if (hasComponent(Transform.class))
            throw new UnsupportedOperationException("There can only be one Transform component for a GameObject!");
    }

    @Override
    public void detach() {
        throw new UnsupportedOperationException("You cannot detach the Transform component!");
    }

    @Override
    public void addComponent(Component component) {
        if (component instanceof Transform && hasComponent(Transform.class))
            throw new UnsupportedOperationException("There can only be one Transform component for a GameObject!");
    }
}