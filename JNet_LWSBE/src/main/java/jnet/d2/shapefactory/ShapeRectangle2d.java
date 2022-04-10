package jnet.d2.shapefactory;

import java.util.ArrayList;
import java.util.List;

import jnet.JNet;
import jnet.d2.shapefactory.Shape2d.ConstrainDefinition2d;
import jnet.d2.shapefactory.Shape2d.ParticleDefinition2d;
import jnet.util.Material;
import jnet.util.Vec2d;

/**
 * A default ShapeBuilder for a simple rectangle, with optional cross-beams for stabilization.
 * @author M_Marvin
 *
 */
public class ShapeRectangle2d implements IShapePart2d {
	
	protected Vec2d va;
	protected Vec2d vb;
	protected boolean stabelized;
	protected Material material;
	
	public ShapeRectangle2d(Vec2d va, Vec2d vb, boolean stabelized) {
		this.va = va;
		this.vb = vb;
		this.stabelized = stabelized;
		this.material = JNet.DEFAULT_MATERIAL;
	}
	
	public ShapeRectangle2d(Vec2d va, Vec2d vb, boolean stabelized, Material material) {
		this.va = va;
		this.vb = vb;
		this.stabelized = stabelized;
		this.material = material;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	public void setMaterial(Material material) {
		this.material = material;
	}
	
	/**
	 * Building the rectangle
	 */
	@Override
	public List<ConstrainDefinition2d> getConstrains() {
		List<ConstrainDefinition2d> constrainDefinitions = new ArrayList<ConstrainDefinition2d>();
		
		ParticleDefinition2d pointA = new ParticleDefinition2d(va);
		ParticleDefinition2d pointB = new ParticleDefinition2d(new Vec2d(va.x, vb.y));
		ParticleDefinition2d pointC = new ParticleDefinition2d(vb);
		ParticleDefinition2d pointD = new ParticleDefinition2d(new Vec2d(vb.x, va.y));
		
		constrainDefinitions.add(new ConstrainDefinition2d(pointA, pointB));
		constrainDefinitions.get(constrainDefinitions.size() - 1).changeMaterial(material);
		constrainDefinitions.add(new ConstrainDefinition2d(pointB, pointC));
		constrainDefinitions.get(constrainDefinitions.size() - 1).changeMaterial(material);
		constrainDefinitions.add(new ConstrainDefinition2d(pointC, pointD));
		constrainDefinitions.get(constrainDefinitions.size() - 1).changeMaterial(material);
		constrainDefinitions.add(new ConstrainDefinition2d(pointD, pointA));
		constrainDefinitions.get(constrainDefinitions.size() - 1).changeMaterial(material);
		
		if (stabelized) {
			constrainDefinitions.add(new ConstrainDefinition2d(pointA, pointC));
			constrainDefinitions.get(constrainDefinitions.size() - 1).changeMaterial(material);
			constrainDefinitions.add(new ConstrainDefinition2d(pointB, pointD));
			constrainDefinitions.get(constrainDefinitions.size() - 1).changeMaterial(material);
		}
		
		return constrainDefinitions;
	}
	
}
