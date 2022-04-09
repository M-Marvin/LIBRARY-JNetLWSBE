package jnet.shapefactory;

import java.util.ArrayList;
import java.util.List;

import jnet.JNet;
import jnet.shapefactory.Shape.ConstrainDefinition;
import jnet.shapefactory.Shape.ParticleDefinition;
import jnet.util.Vec2d;

/**
 * A default ShapeBuilder for a simple triangle.
 * @author M_Marvin
 *
 */
public class ShapeTriangle implements IShapePart {
	
	protected Vec2d va;
	protected Vec2d vb;
	protected Vec2d vc;
	protected Material material;
	
	public ShapeTriangle(Vec2d va, Vec2d vb, Vec2d vc) {
		this.va = va;
		this.vb = vb;
		this.vc = vc;
		this.material = JNet.DEFAULT_MATERIAL;
	}
	
	public ShapeTriangle(Vec2d va, Vec2d vb, Vec2d vc, Material material) {
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
	public List<ConstrainDefinition> getConstrains() {
		List<ConstrainDefinition> constrainDefinitions = new ArrayList<ConstrainDefinition>();
		
		ParticleDefinition pointA = new ParticleDefinition(va);
		ParticleDefinition pointB = new ParticleDefinition(vb);
		ParticleDefinition pointC = new ParticleDefinition(vc);
		
		constrainDefinitions.add(new ConstrainDefinition(pointA, pointB));
		constrainDefinitions.get(constrainDefinitions.size() - 1).changeMaterial(material);
		constrainDefinitions.add(new ConstrainDefinition(pointB, pointC));
		constrainDefinitions.get(constrainDefinitions.size() - 1).changeMaterial(material);
		constrainDefinitions.add(new ConstrainDefinition(pointC, pointA));
		constrainDefinitions.get(constrainDefinitions.size() - 1).changeMaterial(material);
		
		return constrainDefinitions;
	}
	
}
