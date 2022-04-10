package jnet.d3.physic;

import jnet.d3.physic.SoftBody3d.CollisionPlane3d;
import jnet.d3.physic.SoftBody3d.Constrain3d;
import jnet.d3.physic.SoftBody3d.Particle3d;
import jnet.util.Vec3d;

/**
 * Represents a collision between a Particle and a Constrain and the informations needed to solve it.
 * @author M_Marvin
 *
 */
public class Contact3d {
	
	protected Vec3d collisionNormal;
	protected double collisionDepth;
	protected Particle3d particle;
	protected CollisionPlane3d plane;
	
	protected Contact3d() {}
	
	/**
	 * Creates a empty Contact instance, that contains no collision data
	 * @return A new Contact instance
	 */
	public static Contact3d noContact() {
		return new Contact3d();
	}
	
	/**
	 * Creates a empty Contact instance with the given collision parameters
	 * @param collisionNormal The normalized vector of the collision
	 * @param collisionDepth The intersection-depth of the collision
	 * @param particle The Particle of the collision
	 * @param constrain The Constrain of the collision
	 * @return A new Contact instance
	 */
	public static Contact3d contact(Vec3d collisionNormal, double collisionDepth, Particle3d particle, CollisionPlane3d plane) {
		Contact3d contact = new Contact3d();
		contact.particle = particle;
		contact.plane = plane;
		contact.collisionDepth = collisionDepth;
		contact.collisionNormal = collisionNormal;
		return contact;
	}
	
	/**
	 * Checks if this Contact contains data for a collision
	 * @return true if 
	 */
	public boolean isCollision() {
		return this.collisionNormal != null && this.collisionDepth != 0;
	}
	
	/**
	 * Gets the normalized vector of the collision
	 * @returnA normalized vector representing the collision
	 */
	public Vec3d getCollisionNormal() {
		return collisionNormal;
	}
	
	/**
	 * Gets the collision depth of the collision
	 * @returnA Collision depth of the collision
	 */
	public double getCollisionDepth() {
		return collisionDepth;
	}
	
	/**
	 * Gets the Particle of the collision
	 * @returnA The Particle of the collision
	 */
	public Particle3d getParticle() {
		return particle;
	}
	
	/**
	 * Ge5ts the Constrain of the collision
	 * @return The Constrain of the collision
	 */
	public CollisionPlane3d getConstrain() {
		return plane;
	}
	
}
