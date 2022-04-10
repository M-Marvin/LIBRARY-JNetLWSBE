package jnet.d2.shapefactory;

import java.util.ArrayList;
import java.util.List;

import jnet.JNet;
import jnet.d2.shapefactory.Shape2d.ConstrainDefinition2d;
import jnet.d2.shapefactory.Shape2d.ParticleDefinition2d;
import jnet.util.Material;
import jnet.util.Vec2d;

/**
 * A default ShapeBuilder for a simple triangle.
 * @author M_Marvin
 *
 */
public class ShapeTriangle2d implements IShapePart2d {
	
	protected Vec2d va;
	protected Vec2d vb;
	protected Vec2d vc;
	protected Material material;
	
	public ShapeTriangle2d(Vec2d va, Vec2d vb, Vec2d vc) {
		this.va = va;
		this.vb = vb;
		this.vc = vc;
		this.material = JNet.DEFAULT_MATERIAL;
	}
	
	public ShapeTriangle2d(Vec2d va, Vec2d vb, Vec2d vc, Material material) {
		this.va = va;
		this.vb = vb;
		this.vc = vc;
		this.material = material;
	}
	
	public void setMaterial(Material material) {
		this.material = material;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	/**
	 * Building the triangle
	 */
	@Override
	public List<ConstrainDefinition2d> getConstrains() {
		List<ConstrainDefinition2d> constrainDefinitions = new ArrayList<ConstrainDefinition2d>();
		
		ParticleDefinition2d pointA = new ParticleDefinition2d(va);
		ParticleDefinition2d pointB = new ParticleDefinition2d(vb);
		ParticleDefinition2d pointC = new ParticleDefinition2d(vc);
		
		constrainDefinitions.add(new ConstrainDefinition2d(pointA, pointB));
		constrainDefinitions.get(constrainDefinitions.size() - 1).changeMaterial(material);
		constrainDefinitions.add(new ConstrainDefinition2d(pointB, pointC));
		constrainDefinitions.get(constrainDefinitions.size() - 1).changeMaterial(material);
		constrainDefinitions.add(new ConstrainDefinition2d(pointC, pointA));
		constrainDefinitions.get(constrainDefinitions.size() - 1).changeMaterial(material);
		
		return constrainDefinitions;
	}
	
}
