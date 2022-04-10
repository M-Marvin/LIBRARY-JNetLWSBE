package jnet.d3.physic;

import java.util.ArrayList;
import java.util.List;

import jnet.d3.physic.SoftBody3d.Constrain3d;
import jnet.util.Vec3d;

/**
 * The PhysicWorld contains all SoftBodys to simulate, it must be bound to a PhysicSolver.
 * @author M_Marvin
 *
 */
public class PhysicWorld3d {
	
	protected Vec3d globalForce;
	protected List<SoftBody3d> shapes;
	protected List<Constrain3d> joints;
	protected ContactListener3d contactListener;
	
	public PhysicWorld3d() {
		this.shapes = new ArrayList<SoftBody3d>();
		this.globalForce = new Vec3d(0, 0, 0);
		this.joints = new ArrayList<SoftBody3d.Constrain3d>();
		this.contactListener = new ContactListener3d.DummyListener();
	}
	
	/**
	 * Adds the given SoftBody to this world and the simulation
	 * @param shape Shape to add
	 */
	public void addSoftBody(SoftBody3d shape) {
		if (!this.shapes.contains(shape)) this.shapes.add(shape);
	}
	
	public List<SoftBody3d> getSoftBodys() {
		return shapes;
	}
	
	/**
	 * Adds the given Constrain to a separate list of Constrains, it is not affected by collisions and operates like a joint.
	 * @param jointConstrain The Constrain to add as join
	 */
	public void addJoint(Constrain3d jointConstrain) {
		if (!this.joints.contains(jointConstrain)) this.joints.add(jointConstrain);
	}
	
	public List<Constrain3d> getJoints() {
		return joints;
	}
	
	/**
	 * Gets the global-force (gravity) that is applied to every Object
	 * @return A Vec3d representing the global-force
	 */
	public Vec3d getGlobalForce() {
		return globalForce;
	}
	
	/**
	 * Sets the global-force (gravity) that is applied to every Object
	 * @param globalForce A Vec3d representing the global-force
	 */
	public void setGlobalForce(Vec3d globalForce) {
		this.globalForce = globalForce;
	}
	
	/**
	 * Sets the ContactListener which receives any collision-events from all objects in this world.
	 * @param contactListener The ContactListener that receives all events
	 */
	public void setContactListener(ContactListener3d contactListener) {
		if (contactListener == null) throw new RuntimeException(new IllegalArgumentException("Cant set the ContactListener to null!"));
		this.contactListener = contactListener;
	}
	
	/**
	 * Removes the ContactListener and replaces it by a DummyListener which always returns true on the beginContact() event.
	 */
	public void removeContactLitener() {
		setContactListener(new ContactListener3d.DummyListener());
	}
	
	public ContactListener3d getContactListener() {
		return contactListener;
	}
	
}
