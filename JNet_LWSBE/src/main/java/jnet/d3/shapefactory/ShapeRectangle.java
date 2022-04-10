package jnet.d3.shapefactory;

import java.util.ArrayList;
import java.util.List;

import jnet.JNet;
import jnet.d3.shapefactory.Shape.ConstrainDefinition;
import jnet.d3.shapefactory.Shape.ParticleDefinition;
import jnet.util.Vec3d;

/**
 * A default ShapeBuilder for a simple rectangle, with optional cross-beams for stabilization.
 * @author M_Marvin
 *
 */
public class ShapeRectangle implements IShapePart {
	
	protected Vec3d va;
	protected Vec3d vb;
	protected boolean stabelized;
	protected Material material;
	
	public ShapeRectangle(Vec3d va, Vec3d vb, boolean stabelized) {
		this.va = va;
		this.vb = vb;
		this.stabelized = stabelized;
		this.material = JNet.DEFAULT_MATERIAL;
	}
	
	public ShapeRectangle(Vec3d va, Vec3d vb, boolean stabelized, Material material) {
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
	public List<ConstrainDefinition> getConstrains() {
		List<ConstrainDefinition> constrainDefinitions = new ArrayList<ConstrainDefinition>();
		
		ParticleDefinition pointA = new ParticleDefinition(va);
		ParticleDefinition pointB = new ParticleDefinition(new Vec3d(va.x, vb.y, vb.z));
		ParticleDefinition pointC = new ParticleDefinition(vb);
		ParticleDefinition pointD = new ParticleDefinition(new Vec3d(vb.x, va.y, va.z));
		
		constrainDefinitions.add(new ConstrainDefinition(pointA, pointB));
		constrainDefinitions.get(constrainDefinitions.size() - 1).changeMaterial(material);
		constrainDefinitions.add(new ConstrainDefinition(pointB, pointC));
		constrainDefinitions.get(constrainDefinitions.size() - 1).changeMaterial(material);
		constrainDefinitions.add(new ConstrainDefinition(pointC, pointD));
		constrainDefinitions.get(constrainDefinitions.size() - 1).changeMaterial(material);
		constrainDefinitions.add(new ConstrainDefinition(pointD, pointA));
		constrainDefinitions.get(constrainDefinitions.size() - 1).changeMaterial(material);
		
		if (stabelized) {
			constrainDefinitions.add(new ConstrainDefinition(pointA, pointC));
			constrainDefinitions.get(constrainDefinitions.size() - 1).changeMaterial(material);
			constrainDefinitions.add(new ConstrainDefinition(pointB, pointD));
			constrainDefinitions.get(constrainDefinitions.size() - 1).changeMaterial(material);
		}
		
		return constrainDefinitions;
	}
	
}
