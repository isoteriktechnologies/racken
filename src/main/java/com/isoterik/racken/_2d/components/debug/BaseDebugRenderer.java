package com.isoterik.racken._2d.components.debug;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.isoterik.racken.Component;

/**
 * The base class for all debug renderers.
 *
 * @author isoteriksoftware
 */
public abstract class BaseDebugRenderer extends Component {
	public static final ShapeRenderer.ShapeType LINE_SHAPE
		= ShapeRenderer.ShapeType.Line;
	public static final ShapeRenderer.ShapeType FILLED_SHAPE
		= ShapeRenderer.ShapeType.Filled;
	public static final ShapeRenderer.ShapeType POINT_SHAPE
		= ShapeRenderer.ShapeType.Point;
		
	protected ShapeRenderer.ShapeType shapeType;
	
	protected Color color;

	public BaseDebugRenderer(ShapeRenderer.ShapeType shapeType, Color color) {
		this.shapeType = shapeType;
		this.color = color;
	}
	
	public BaseDebugRenderer(ShapeRenderer.ShapeType shapeType) {
		this(shapeType, Color.WHITE);
	}
	
	public BaseDebugRenderer() {
		this(LINE_SHAPE);
	}

	/**
	 * Sets the color to use for rendering
	 * @param color the color
	 * @return this instance for chaining
	 */
	public BaseDebugRenderer setColor(Color color) {
		this.color = color;
		return this;
	}

	/**
	 *
	 * @return the color used for rendering
	 */
	public Color getColor()
	{ return color; }

	/**
	 * Sets the shape type to used for rendering
	 * @param shapeType the shape type
	 * @return this instance for chaining
	 */
	public BaseDebugRenderer setShapeType(ShapeRenderer.ShapeType shapeType) {
		this.shapeType = shapeType;
		return this;
	}

	/**
	 *
	 * @return the shape type to used for rendering
	 */
	public ShapeRenderer.ShapeType getShapeType()
	{ return shapeType; }

	@Override
	public void renderShapeLine(ShapeRenderer shapeRenderer) {
		if (shapeType == LINE_SHAPE) {
			shapeRenderer.setColor(color);
			draw(shapeRenderer);
		}
	}

	@Override
	public void renderShapeFilled(ShapeRenderer shapeRenderer) {
		if (shapeType == FILLED_SHAPE) {
			shapeRenderer.setColor(color);
			draw(shapeRenderer);
		}
	}

	@Override
	public void renderShapePoint(ShapeRenderer shapeRenderer) {
		if (shapeType == POINT_SHAPE) {
			shapeRenderer.setColor(color);
			draw(shapeRenderer);
		}
	}

	/**
	 * Concrete subclasses must implement this method to define how they are rendered.
	 * <strong>Note:</strong> this method is only called for shape type supported by the subclass. This means a subclass can disallow change of shape type if it
	 * only supports a particular type of shape
	 * @param shapeRenderer a {@link ShapeRenderer} that can be used for rendering shapes
	 */
	public abstract void draw(ShapeRenderer shapeRenderer);
}
