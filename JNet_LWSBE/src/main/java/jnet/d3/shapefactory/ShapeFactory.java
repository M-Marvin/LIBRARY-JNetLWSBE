package jnet.d3.shapefactory;

import java.util.ArrayList;
import java.util.List;

import jnet.d3.shapefactory.Shape.ConstrainDefinition;
import jnet.d3.shapefactory.Shape.ParticleDefinition;
import jnet.util.Vec3d;

/**
 * The ShapeFactroy is used to combine multiple ShapeBuilders (IShapeParts) to a single complex Shape.
 * @author M_Marvin
 *
 */
public class ShapeFactory {
	
	protected List<IShapePart> shapeParts;
	
	/**
	 * Creates a new ShapeFactory and starts a new Shape build
	 */
	public ShapeFactory() {
		this.shapeParts = new ArrayList<IShapePart>();
	}
	
	/**
	 * Add custom shape builder
	 * @param shape The custom Shape builder
	 * @return The ShapeFactory to append more methods
	 */
	public ShapeFactory addShape(IShapePart shape) {
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
	public ShapeFactory addTriangle(float xa, float ya, float za, float xb, float yb, float zb, float xc, float yc, float zc) {
		addShape(new ShapeTriangle(new Vec3d(xa, ya, za), new Vec3d(xb, yb, zb), new Vec3d(xc, yc, zc)));
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
	public ShapeFactory addShapeRectangleCross(float xa, float ya, float za, float xb, float yb, float zb) {
		addShape(new ShapeRectangle(new Vec3d(xa, ya, za), new Vec3d(xb, yb, zb), true));
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
	public ShapeFactory addShapeRectangle(float xa, float ya, float za, float xb, float yb, float zb) {
		addShape(new ShapeRectangle(new Vec3d(xa, ya, za), new Vec3d(xb, yb, zb), false));
		return this;
	}
	
	/**
	 * Completes the current build and returns a Shape that is like a definition of a SoftBody and can create multiple identical SoftBodys.
	 * @return A Shape representing the SoftBody
	 */
	public Shape build() {
		
		Shape shape = new Shape();
		
		for (IShapePart shapePart : this.shapeParts) {
			List<ConstrainDefinition> partConstrains = shapePart.getConstrains();
			for (ConstrainDefinition partConstrain : partConstrains) {
				
				ParticleDefinition pointA = partConstrain.pointA;
				shape.getConstrains().forEach((constrain) -> {
					if (constrain.pointA.pos.equals(pointA.pos)) constrain.pointA = pointA;
					if (constrain.pointB.pos.equals(pointA.pos)) constrain.pointB = pointA;
				});
				
				ParticleDefinition pointB = partConstrain.pointB;
				shape.getConstrains().forEach((constrain) -> {
					if (constrain.pointA.pos.equals(pointB.pos)) constrain.pointA = pointB;
					if (constrain.pointB.pos.equals(pointB.pos)) constrain.pointB = pointB;
				});
				
				if (!shape.getConstrains().contains(partConstrain)) shape.getConstrains().add(partConstrain);
				
			}
		}
		
		for (ConstrainDefinition constrain : shape.getConstrains()) {
			if (!shape.getParticles().contains(constrain.pointA)) shape.getParticles().add(constrain.pointA);
			if (!shape.getParticles().contains(constrain.pointB)) shape.getParticles().add(constrain.pointB);
		}
		
		return shape;
	}
	
}
