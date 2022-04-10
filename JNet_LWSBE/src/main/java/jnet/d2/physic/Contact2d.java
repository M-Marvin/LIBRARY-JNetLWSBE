package jnet.d2.physic;

import jnet.d2.physic.SoftBody2d.Constrain2d;
import jnet.d2.physic.SoftBody2d.Particle2d;
import jnet.util.Vec2d;

/**
 * Represents a collision between a Particle and a Constrain and the informations needed to solve it.
 * @author M_Marvin
 *
 */
public class Contact2d {
	
	protected Vec2d collisionNormal;
	protected double collisionDepth;
	protected Particle2d particle;
	protected Constrain2d constrain;
	
	protected Contact2d() {}
	
	/**
	 * Creates a empty Contact instance, that contains no collision data
	 * @return A new Contact instance
	 */
	public static Contact2d noContact() {
		return new Contact2d();
	}
	
	/**
	 * Creates a empty Contact instance with the given collision parameters
	 * @param collisionNormal The normalized vector of the collision
	 * @param collisionDepth The intersection-depth of the collision
	 * @param particle The Particle of the collision
	 * @param constrain The Constrain of the collision
	 * @return A new Contact instance
	 */
	public static Contact2d contact(Vec2d collisionNormal, double collisionDepth, Particle2d particle, Constrain2d constrain) {
		Contact2d contact = new Contact2d();
		contact.particle = particle;
		contact.constrain = constrain;
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
	public Vec2d getCollisionNormal() {
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
	public Particle2d getParticle() {
		return particle;
	}
	
	/**
	 * Ge5ts the Constrain of the collision
	 * @return The Constrain of the collision
	 */
	public Constrain2d getConstrain() {
		return constrain;
	}
	
}
