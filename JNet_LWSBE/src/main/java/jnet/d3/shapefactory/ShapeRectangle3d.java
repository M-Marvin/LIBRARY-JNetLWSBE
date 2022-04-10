package jnet.d3.shapefactory;

import java.util.ArrayList;
import java.util.List;

import jnet.JNet;
import jnet.d3.shapefactory.Shape3d.ConstrainDefinition3d;
import jnet.d3.shapefactory.Shape3d.ParticleDefinition3d;
import jnet.util.Material;
import jnet.util.Vec3d;

/**
 * A default ShapeBuilder for a simple rectangle, with optional cross-beams for stabilization.
 * @author M_Marvin
 *
 */
public class ShapeRectangle3d implements IShapePart3d {
	
	protected Vec3d va;
	protected Vec3d vb;
	protected boolean stabelized;
	protected Material material;
	
	public ShapeRectangle3d(Vec3d va, Vec3d vb, boolean stabelized) {
		this.va = va;
		this.vb = vb;
		this.stabelized = stabelized;
		this.material = JNet.DEFAULT_MATERIAL;
	}
	
	public ShapeRectangle3d(Vec3d va, Vec3d vb, boolean stabelized, Material material) {
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
	public List<ConstrainDefinition3d> getConstrains() {
		List<ConstrainDefinition3d> constrainDefinitions = new ArrayList<ConstrainDefinition3d>();
		
		ParticleDefinition3d pointA = new ParticleDefinition3d(va);
		ParticleDefinition3d pointB = new ParticleDefinition3d(new Vec3d(va.x, vb.y, vb.z));
		ParticleDefinition3d pointC = new ParticleDefinition3d(vb);
		ParticleDefinition3d pointD = new ParticleDefinition3d(new Vec3d(vb.x, va.y, va.z));
		
		constrainDefinitions.add(new ConstrainDefinition3d(pointA, pointB));
		constrainDefinitions.get(constrainDefinitions.size() - 1).changeMaterial(material);
		constrainDefinitions.add(new ConstrainDefinition3d(pointB, pointC));
		constrainDefinitions.get(constrainDefinitions.size() - 1).changeMaterial(material);
		constrainDefinitions.add(new ConstrainDefinition3d(pointC, pointD));
		constrainDefinitions.get(constrainDefinitions.size() - 1).changeMaterial(material);
		constrainDefinitions.add(new ConstrainDefinition3d(pointD, pointA));
		constrainDefinitions.get(constrainDefinitions.size() - 1).changeMaterial(material);
		
		if (stabelized) {
			constrainDefinitions.add(new ConstrainDefinition3d(pointA, pointC));
			constrainDefinitions.get(constrainDefinitions.size() - 1).changeMaterial(material);
			constrainDefinitions.add(new ConstrainDefinition3d(pointB, pointD));
			constrainDefinitions.get(constrainDefinitions.size() - 1).changeMaterial(material);
		}
		
		return constrainDefinitions;
	}
	
}
