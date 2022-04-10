package jnet.d2.shapefactory;

import java.util.List;

import jnet.d2.shapefactory.Shape2d.ConstrainDefinition2d;

/**
 * This is the interface for making custom ShapeBuilders for the ShapeFactory.
 * It simply provides the ConstrainDefinitions for the new ShapePart, multiple ShapeBuilders then are combined by the ShapeFactory to build a complex Shape.
 * @author M_Marvin
 *
 */
public interface IShapePart2d {
	
	public List<ConstrainDefinition2d> getConstrains();
	
}
