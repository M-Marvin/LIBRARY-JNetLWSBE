package jnet.d3.shapefactory;

import java.util.List;

import jnet.d3.shapefactory.Shape3d.ConstrainDefinition3d;

/**
 * This is the interface for making custom ShapeBuilders for the ShapeFactory.
 * It simply provoides the ConstrainDefinitions for the new ShapePart, multiple ShapeBuilders then are combined by the ShapeFactory to build a complex Shape.
 * @author M_Marvin
 *
 */
public interface IShapePart3d {
	
	public List<ConstrainDefinition3d> getConstrains();
	
}
