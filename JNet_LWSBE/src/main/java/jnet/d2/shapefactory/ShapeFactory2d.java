package jnet.d2.shapefactory;

import java.util.ArrayList;
import java.util.List;

import jnet.d2.shapefactory.Shape2d.ConstrainDefinition2d;
import jnet.d2.shapefactory.Shape2d.ParticleDefinition2d;
import jnet.util.Vec2d;

/**
 * The ShapeFactroy is used to combine multiple ShapeBuilders (IShapeParts) to a single complex Shape.
 * @author M_Marvin
 *
 */
public class ShapeFactory2d {
	
	protected List<IShapePart2d> shapeParts;
	
	/**
	 * Creates a new ShapeFactory and starts a new Shape build
	 */
	public ShapeFactory2d() {
		this.shapeParts = new ArrayList<IShapePart2d>();
	}
	
	/**
	 * Add custom shape builder
	 * @param shape The custom Shape builder
	 * @return The ShapeFactory to append more methods
	 */
	public ShapeFactory2d addShape(IShapePart2d shape) {
		this.shapeParts.add(shape);
		return this;
	}
	
	/**
	 * Adds a triangle to the Shape
	 * @param xa X position of corner A
	 * @param ya Y position of corner A
	 * @param xb X position of corner B
	 * @param yb Y position of corner B
	 * @param xc X position of corner C
	 * @param yc Y position of corner C
	 * @return The ShapeFactory to append more methods
	 */
	public ShapeFactory2d addTriangle(float xa, float ya, float xb, float yb, float xc, float yc) {
		addShape(new ShapeTriangle2d(new Vec2d(xa, ya), new Vec2d(xb, yb), new Vec2d(xc, yc)));
		return this;
	}
	
	/**
	 * Adds a rectangle with cross-beams (constrains) to the Shape
	 * @param xa X position of corner A
	 * @param ya Y position of corner A
	 * @param xb X position of corner B
	 * @param yb Y position of corner B
	 * @return The ShapeFactory to append more methods
	 */
	public ShapeFactory2d addShapeRectangleCross(float xa, float ya, float xb, float yb) {
		addShape(new ShapeRectangle2d(new Vec2d(xa, ya), new Vec2d(xb, yb), true));
		return this;
	}
	
	/**
	 * Adds a rectangle (without cross-beams) (constrains) to the Shape
	 * A rectangle without cross beams is not stable, and collapses under force
	 * @param xa X position of corner A
	 * @param ya Y position of corner A
	 * @param xb X position of corner B
	 * @param yb Y position of corner B
	 * @return The ShapeFactory to append more methods
	 */
	public ShapeFactory2d addShapeRectangle(float xa, float ya, float xb, float yb) {
		addShape(new ShapeRectangle2d(new Vec2d(xa, ya), new Vec2d(xb, yb), false));
		return this;
	}
	
	/**
	 * Completes the current build and returns a Shape that is like a definition of a SoftBody and can create multiple identical SoftBodys.
	 * @return A Shape representing the SoftBody
	 */
	public Shape2d build() {
		
		Shape2d shape = new Shape2d();
		
		for (IShapePart2d shapePart : this.shapeParts) {
			List<ConstrainDefinition2d> partConstrains = shapePart.getConstrains();
			for (ConstrainDefinition2d partConstrain : partConstrains) {
				
				ParticleDefinition2d pointA = partConstrain.pointA;
				shape.getConstrains().forEach((constrain) -> {
					if (constrain.pointA.pos.equals(pointA.pos)) constrain.pointA = pointA;
					if (constrain.pointB.pos.equals(pointA.pos)) constrain.pointB = pointA;
				});
				
				ParticleDefinition2d pointB = partConstrain.pointB;
				shape.getConstrains().forEach((constrain) -> {
					if (constrain.pointA.pos.equals(pointB.pos)) constrain.pointA = pointB;
					if (constrain.pointB.pos.equals(pointB.pos)) constrain.pointB = pointB;
				});
				
				if (!shape.getConstrains().contains(partConstrain)) shape.getConstrains().add(partConstrain);
				
			}
		}
		
		for (ConstrainDefinition2d constrain : shape.getConstrains()) {
			if (!shape.getParticles().contains(constrain.pointA)) shape.getParticles().add(constrain.pointA);
			if (!shape.getParticles().contains(constrain.pointB)) shape.getParticles().add(constrain.pointB);
		}
		
		return shape;
	}
	
}
