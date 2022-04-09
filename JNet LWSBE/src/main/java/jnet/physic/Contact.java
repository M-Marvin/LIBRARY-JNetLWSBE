package jnet.physic;

import jnet.physic.SoftBody.Constrain;
import jnet.physic.SoftBody.Particle;
import jnet.util.Vec2d;

/**
 * Represents a collision between a Particle and a Constrain and the informations needed to solve it.
 * @author M_Marvin
 *
 */
public class Contact {
	
	protected Vec2d collisionNormal;
	protected double collisionDepth;
	protected Particle particle;
	protected Constrain constrain;
	
	protected Contact() {}
	
	/**
	 * Creates a empty Contact instance, that contains no collision data
	 * @return A new Contact instance
	 */
	public static Contact noContact() {
		return new Contact();
	}
	
	/**
	 * Creates a empty Contact instance with the given collision parameters
	 * @param collisionNormal The normalized vector of the collision
	 * @param collisionDepth The intersection-depth of the collision
	 * @param particle The Particle of the collision
	 * @param constrain The Constrain of the collision
	 * @return A new Contact instance
	 */
	public static Contact contact(Vec2d collisionNormal, double collisionDepth, Particle particle, Constrain constrain) {
		Contact contact = new Contact();
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
	public Particle getParticle() {
		return particle;
	}
	
	/**
	 * Ge5ts the Constrain of the collision
	 * @return The Constrain of the collision
	 */
	public Constrain getConstrain() {
		return constrain;
	}
	
}
