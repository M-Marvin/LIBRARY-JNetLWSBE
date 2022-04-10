package jnet.d2.physic;

import java.util.ArrayList;
import java.util.List;

import jnet.d2.physic.SoftBody2d.Constrain2d;
import jnet.util.Vec2d;

/**
 * The PhysicWorld contains all SoftBodys to simulate, it must be bound to a PhysicSolver.
 * @author M_Marvin
 *
 */
public class PhysicWorld2d {
	
	protected Vec2d globalForce;
	protected List<SoftBody2d> shapes;
	protected List<Constrain2d> joints;
	protected ContactListener2d contactListener;
	
	public PhysicWorld2d() {
		this.shapes = new ArrayList<SoftBody2d>();
		this.globalForce = new Vec2d(0, 0);
		this.joints = new ArrayList<Constrain2d>();
		this.contactListener = new ContactListener2d.DummyListener();
	}
	
	/**
	 * Adds the given SoftBody to this world and the simulation
	 * @param shape Shape to add
	 */
	public void addSoftBody(SoftBody2d shape) {
		if (!this.shapes.contains(shape)) this.shapes.add(shape);
	}
	
	public List<SoftBody2d> getSoftBodys() {
		return shapes;
	}
	
	/**
	 * Adds the given Constrain2d to a separate list of Constrain2ds, it is not affected by collisions and operates like a joint.
	 * @param jointConstrain2d The Constrain2d to add as join
	 */
	public void addJoint(Constrain2d jointConstrain2d) {
		if (!this.joints.contains(jointConstrain2d)) this.joints.add(jointConstrain2d);
	}
	
	public List<Constrain2d> getJoints() {
		return joints;
	}
	
	/**
	 * Gets the global-force (gravity) that is applied to every Object
	 * @return A Vec2d representing the global-force
	 */
	public Vec2d getGlobalForce() {
		return globalForce;
	}
	
	/**
	 * Sets the global-force (gravity) that is applied to every Object
	 * @param globalForce A Vec2d representing the global-force
	 */
	public void setGlobalForce(Vec2d globalForce) {
		this.globalForce = globalForce;
	}
	
	/**
	 * Sets the ContactListener which receives any collision-events from all objects in this world.
	 * @param contactListener The ContactListener that receives all events
	 */
	public void setContactListener(ContactListener2d contactListener) {
		if (contactListener == null) throw new RuntimeException(new IllegalArgumentException("Cant set the ContactListener to null!"));
		this.contactListener = contactListener;
	}
	
	/**
	 * Removes the ContactListener and replaces it by a DummyListener which always returns true on the beginContact() event.
	 */
	public void removeContactLitener() {
		setContactListener(new ContactListener2d.DummyListener());
	}
	
	public ContactListener2d getContactListener() {
		return contactListener;
	}
	
}
