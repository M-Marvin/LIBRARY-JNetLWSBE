package jnet.d3.physic;

import java.util.ArrayList;
import java.util.List;

import jnet.d3.physic.SoftBody.Constrain;
import jnet.util.Vec3d;

/**
 * The PhysicWorld contains all SoftBodys to simulate, it must be bound to a PhysicSolver.
 * @author M_Marvin
 *
 */
public class PhysicWorld {
	
	protected Vec3d globalForce;
	protected List<SoftBody> shapes;
	protected List<Constrain> joints;
	protected ContactListener contactListener;
	
	public PhysicWorld() {
		this.shapes = new ArrayList<SoftBody>();
		this.globalForce = new Vec3d(0, 0, 0);
		this.joints = new ArrayList<SoftBody.Constrain>();
		this.contactListener = new ContactListener.DummyListener();
	}
	
	/**
	 * Adds the given SoftBody to this world and the simulation
	 * @param shape Shape to add
	 */
	public void addSoftBody(SoftBody shape) {
		if (!this.shapes.contains(shape)) this.shapes.add(shape);
	}
	
	public List<SoftBody> getSoftBodys() {
		return shapes;
	}
	
	/**
	 * Adds the given Constrain to a separate list of Constrains, it is not affected by collisions and operates like a joint.
	 * @param jointConstrain The Constrain to add as join
	 */
	public void addJoint(Constrain jointConstrain) {
		if (!this.joints.contains(jointConstrain)) this.joints.add(jointConstrain);
	}
	
	public List<Constrain> getJoints() {
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
	public void setContactListener(ContactListener contactListener) {
		if (contactListener == null) throw new RuntimeException(new IllegalArgumentException("Cant set the ContactListener to null!"));
		this.contactListener = contactListener;
	}
	
	/**
	 * Removes the ContactListener and replaces it by a DummyListener which always returns true on the beginContact() event.
	 */
	public void removeContactLitener() {
		setContactListener(new ContactListener.DummyListener());
	}
	
	public ContactListener getContactListener() {
		return contactListener;
	}
	
}
