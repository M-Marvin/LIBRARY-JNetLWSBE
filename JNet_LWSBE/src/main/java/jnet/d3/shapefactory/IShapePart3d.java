package jnet.d3.shapefactory;

import java.util.ArrayList;
import java.util.List;

import jnet.d3.shapefactory.Shape3d.CollisionPlaneDefinition3d;
import jnet.d3.shapefactory.Shape3d.ConstrainDefinition3d;

/**
 * This is the interface for making custom ShapeBuilders for the ShapeFactory.
 * It simply provides the ConstrainDefinitions for the new ShapePart, multiple ShapeBuilders then are combined by the ShapeFactory to build a complex Shape.
 * @author M_Marvin
 *
 */
public interface IShapePart3d {
	
	public default List<ConstrainDefinition3d> getConstrains() { return null; }
	
	public default List<?>[] getConstrainsAndPlanes() {
		return new List<?>[] { getConstrains(), new ArrayList<CollisionPlaneDefinition3d>() };
	}
	
}
