package jnet.shapefactory;

import java.util.List;

import jnet.shapefactory.Shape.ConstrainDefinition;

/**
 * This is the interface for making custom ShapeBuilders for the ShapeFactory.
 * It simply provoides the ConstrainDefinitions for the new ShapePart, multiple ShapeBuilders then are combined by the ShapeFactory to build a complex Shape.
 * @author M_Marvin
 *
 */
public interface IShapePart {
	
	public List<ConstrainDefinition> getConstrains();
	
}
