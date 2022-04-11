package jnet.d3.shapefactory;

import java.util.ArrayList;
import java.util.List;

import jnet.JNet;
import jnet.d3.shapefactory.Shape3d.CollisionPlaneDefinition3d;
import jnet.d3.shapefactory.Shape3d.ConstrainDefinition3d;
import jnet.d3.shapefactory.Shape3d.ParticleDefinition3d;
import jnet.util.Material;
import jnet.util.Vec3d;

/**
 * A default ShapeBuilder for a simple triangle.
 * @author M_Marvin
 *
 */
public class ShapeTriangle3d implements IShapePart3d {
	
	protected Vec3d va;
	protected Vec3d vb;
	protected Vec3d vc;
	protected Material material;
	protected boolean collisionPlane;
	
	public ShapeTriangle3d(Vec3d va, Vec3d vb, Vec3d vc) {
		this.va = va;
		this.vb = vb;
		this.vc = vc;
		this.material = JNet.DEFAULT_MATERIAL;
	}
	
	public ShapeTriangle3d(Vec3d va, Vec3d vb, Vec3d vc, boolean collisionPlane) {
		this.va = va;
		this.vb = vb;
		this.vc = vc;
		this.material = JNet.DEFAULT_MATERIAL;
		this.collisionPlane = collisionPlane;
	}
	
	public ShapeTriangle3d(Vec3d va, Vec3d vb, Vec3d vc, Material material) {
		this.va = va;
		this.vb = vb;
		this.vc = vc;
		this.material = material;
	}

	public ShapeTriangle3d(Vec3d va, Vec3d vb, Vec3d vc, boolean collisionPlane, Material material) {
		this.va = va;
		this.vb = vb;
		this.vc = vc;
		this.material = material;
		this.collisionPlane = collisionPlane;
	}
	
	public void setMaterial(Material material) {
		this.material = material;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	public void setCollisionPlane(boolean collisionPlane) {
		this.collisionPlane = collisionPlane;
	}
	
	public boolean getsetCollisionPlane() {
		return collisionPlane;
	}
	
	/**
	 * Building the triangle
	 */
	@Override
	public List<?>[] getConstrainsAndPlanes() {
		List<ConstrainDefinition3d> constrainDefinitions = new ArrayList<ConstrainDefinition3d>();
		List<CollisionPlaneDefinition3d> planeDefinitions = new ArrayList<CollisionPlaneDefinition3d>();
		
		ParticleDefinition3d pointA = new ParticleDefinition3d(va);
		ParticleDefinition3d pointB = new ParticleDefinition3d(vb);
		ParticleDefinition3d pointC = new ParticleDefinition3d(vc);
		
		constrainDefinitions.add(new ConstrainDefinition3d(pointA, pointB));
		constrainDefinitions.get(constrainDefinitions.size() - 1).changeMaterial(material);
		constrainDefinitions.add(new ConstrainDefinition3d(pointB, pointC));
		constrainDefinitions.get(constrainDefinitions.size() - 1).changeMaterial(material);
		constrainDefinitions.add(new ConstrainDefinition3d(pointC, pointA));
		constrainDefinitions.get(constrainDefinitions.size() - 1).changeMaterial(material);
		
		if (collisionPlane) {
			planeDefinitions.add(new CollisionPlaneDefinition3d(constrainDefinitions.get(0), constrainDefinitions.get(1), constrainDefinitions.get(2)));
		}
		
		return new List<?>[] {constrainDefinitions, planeDefinitions};
	}
	
}
