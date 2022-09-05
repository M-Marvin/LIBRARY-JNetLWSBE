package jnet.d3.physic;

import java.util.HashMap;

import javax.management.RuntimeErrorException;

import jnet.JNet;
import jnet.d2.physic.SoftBody2d.Particle2d;
import jnet.d3.physic.SoftBody3d.CollisionPlane3d;
import jnet.d3.physic.SoftBody3d.Constrain3d;
import jnet.d3.physic.SoftBody3d.Particle3d;
import jnet.util.Vec2d;
import jnet.util.Vec3d;

/**
 * The core of the PhysicEngine, the solver handles the forces on the Particles, the movement, the collisions and the constrains.
 * It is bound to a PhysicWorld and simulates the objects in it.
 * @author M_Marvin
 *
 */
public class PhysicSolver3d {
	
	protected int itterationCount;
	protected PhysicWorld3d world;
	
	public PhysicSolver3d(PhysicWorld3d world) {
		this.world = world;
		this.itterationCount = JNet.DEFAULT_NUM_ITTERATIONS;
	}
	
	/**
	 * Manually changes the world, that this solver handles
	 * @param world The new world for this solver
	 */
	public void setWorld(PhysicWorld3d world) {
		this.world = world;
	}
	
	/**
	 * Gets the world that this solver currently handles
	 * @return
	 */
	public PhysicWorld3d getWorld() {
		return world;
	}
	
	/**
	 * Changes the number of iterations to try solving the constrains and the collisions
	 * @param itteratioonCount The number of iterations to try solving the constrains and the collisions
	 */
	public void setItterationCount(int itteratioonCount) {
		this.itterationCount = itteratioonCount;
	}
	
	/**
	 * Gets the number of iterations to try solving the constrains and the collisions
	 * @return The number of iterations to try solving the constrains and the collisions
	 */
	public int getItterationCount() {
		return itterationCount;
	}
	
	/**
	 * Perform a single simulation-step in the current world
	 * @param timeStep The size of the simulation-step, larger steps make the simulation faster, but less accurate
	 */
	public void solve(float timeStep) {
		
		if (this.world == null) {
			throw new RuntimeErrorException(new Error("Cant run without world set!"), "Error on run PhysicSolver!");
		}
		
		// Solve Joints
		for (int itteration = 0; itteration < itterationCount; itteration++) {
			for (Constrain3d constrain : this.world.getJoints()) {
				if (!constrain.broken) {
					constrain(itteration + 1, constrain);
				}
			}
		}
				
		// Solve Objects
		this.world.getSoftBodys().forEach((shape) -> {
			
			// Integrator
			shape.getParticles().forEach((point) -> {
				integrate(timeStep, point);
			});
			
			for (int itteration = 0; itteration < itterationCount; itteration++) {
				
				// Satisfy Constrains
				for (Constrain3d constrain : shape.getConstrains()) {
					if (!constrain.broken) {
						constrain(itteration + 1, constrain);
					}
				}
				
				// Check Constrain-collisions
				HashMap<Contact3d, SoftBody3d> collisions = new HashMap<Contact3d, SoftBody3d>();
				shape.getParticles().forEach((particle) -> {
					this.world.getSoftBodys().forEach((shape2) -> shape2.getPlanes().forEach((plane) -> {
						if (plane.particleA != particle && plane.particleB != particle && plane.particleC != particle && !plane.isBroken()) {
							Contact3d contact = checkContact(particle, plane);
							if (contact.isCollision()) collisions.put(contact, shape2);
						}
					}));
				});
				
				// Solve contacts
				collisions.keySet().forEach((collision) -> {
					if (this.world.getContactListener().beginContact(collision)) {
						boolean processCollision = true;
						SoftBody3d shape2 = collisions.get(collision);
						if (shape == shape2) {
							if (!shape.getContactListener().beginContact(collision)) processCollision = false;
						} else {
							if (!shape.getContactListener().beginContact(collision) && !shape2.getContactListener().beginContact(collision)) processCollision = false;
						}
						if (processCollision) {
							solveCollision(collision);
							shape.getContactListener().endContact(collision);
							if (shape != shape2) shape2.getContactListener().endContact(collision);
						}
						this.world.getContactListener().endContact(collision);
					}
				});
				
			}
			
			// Accumulate Global Forces
			shape.getParticles().forEach((point) -> {
				point.acceleration = new Vec3d(world.getGlobalForce());
			});
			
		});
		
	}
	
	/**
	 * Solve a single constrain (restore original length, if deformed)
	 * @param itteration The number of the iteration, used to calculate the strength of the "reform-force", higher number -> less strength
	 * @param constrain The Constrain to reform
	 */
	public void constrain(int itteration, Constrain3d constrain) {
		
		// Calculate force on the constrain
		Vec3d forceA = constrain.pointA.getMotion().mul(constrain.pointA.mass);
		Vec3d forceB = constrain.pointB.getMotion().mul(constrain.pointA.mass);
		Vec2d angle = constrain.pointA.pos.angle(constrain.pointB.pos);
		double force = forceA.forceByAngle(angle).add(forceB.forceByAngle(new Vec2d(angle.x + Math.PI, angle.y + Math.PI))).summ();
		force = (force < 0 ? -force : force);
		
		// Calculate spring deformation
		Vec3d delta = constrain.pointB.pos.sub(constrain.pointA.pos);
		double deltalength = Math.sqrt(delta.dot(delta));
		double diff = (deltalength - constrain.length) / deltalength;
		
		// If force is to high, deform spring permanent
		if (constrain.deformForce > -1) {
			if (force > constrain.deformForce) {
				constrain.length += diff * ((force - constrain.deformForce) / constrain.deformForce);
			}
		}
		if (constrain.length > constrain.originalLength * constrain.maxBending && constrain.maxBending > -1) constrain.broken = true;
		
		// Reform spring
		double stiffnessLinear = 1 - Math.pow((1 - constrain.stiffness), 1 / itteration);
		constrain.pointA.pos = constrain.pointA.pos.add(delta.mul(constrain.pointB.mass / (constrain.pointA.mass + constrain.pointB.mass)).mul(diff).mul(stiffnessLinear));
		constrain.pointB.pos = constrain.pointB.pos.sub(delta.mul(constrain.pointA.mass / (constrain.pointA.mass + constrain.pointB.mass)).mul(diff).mul(stiffnessLinear));
		
	}
	
	/**
	 * Integrate a single Particle, handles motion and acceleration
	 * @param deltaT The size of the simulation-step, larger steps make the simulation faster, but less accurate 
	 * @param point The Particle to integrate
	 */
	public void integrate(float deltaT, Particle3d point) {
		Vec3d temp = point.pos;
		point.pos = point.pos.add(point.pos.sub(point.lastPos).add(point.acceleration.mul(deltaT * deltaT)));
		point.lastPos = temp;
	}
	
	/**
	 * Check if there is a collision between the given Particle and the CollisionPlane since the last simulation step
	 * @param particle The Particle
	 * @param constrain The Constrain
	 * @return A Contact representing the collision between the two instances, its variables are null if there is no collision
	 */
	public Contact3d checkContact(Particle3d particle, CollisionPlane3d plane) {
		
		float closeZero = 0.00000001F;
		
		// Get corners of the triangle
		Vec3d v0 = plane.particleA.pos;
		Vec3d v1 = plane.particleB.pos;
		Vec3d v2 = plane.particleC.pos;
		
		// Make ray from particle motion
		Vec3d orig = particle.lastPos;
		Vec3d dir = particle.lastPos.sub(particle.pos).normalize();
		
		// Get plane normal
		Vec3d v0v1 = v1.sub(v0); 
		Vec3d v0v2 = v2.sub(v0); 
		Vec3d pvec = dir.cross(v0v2); 
		
	    // Ray and triangle are parallel if det is close to 0
	    double det = v0v1.dot(pvec); 
	    if (Math.abs(det) < closeZero) return Contact3d.noContact(); 
	    
	    // Intersection check
	    double invDet = 1 / det; 
	    Vec3d tvec = orig.sub(v0); 
	    double u = tvec.dot(pvec) * invDet; 
	    if (u < 0 || u > 1) return Contact3d.noContact(); 
	    Vec3d qvec = tvec.cross(v0v1); 
	    double v = dir.dot(qvec) * invDet; 
	    if (v < 0 || u + v > 1) return Contact3d.noContact(); 
	    
	    // Get intersection point
	    double t = v0v2.dot(qvec) * invDet; 
	    Vec3d P = orig.add(dir.mul(t));
	    
	    // Check if intersection point P is in line
		if (P.x > particle.pos.x ? P.x < particle.lastPos.x : P.x > particle.lastPos.x &&
			P.y > particle.pos.y ? P.y < particle.lastPos.y : P.y > particle.lastPos.y &&
			P.z > particle.pos.z ? P.z < particle.lastPos.z : P.z > particle.lastPos.z) {
			
			// Get collision normal and depth
			Vec3d collisionNormal = particle.pos.noramlVec(P);
			double collisionDepth = particle.pos.distance(P);
			return Contact3d.contact(collisionNormal, collisionDepth, particle, plane);
			
		}
		
		return Contact3d.noContact();
		
	};
		
	/**
	 * Sets position and forces to solve the given collision
	 * @param contact The Contact that represents the collision
	 */
	public void solveCollision(Contact3d contact) {
		
//		if (!contact.isCollision()) return;
//		
		Particle3d particle1 = contact.getParticle();
		Particle3d particle2A = contact.getPlane().particleA;
		Particle3d particle2B = contact.getPlane().particleB;
		Particle3d particle2C = contact.getPlane().particleC;
//		
//		particle1.pos = particle1.pos.add(contact.getCollisionNormal().mul(contact.getCollisionDepth()));
//		
//		double distA = particle1.pos.distance(particle2A.pos);
//		double distB = particle1.pos.distance(particle2A.pos);
//		double distC = particle1.pos.distance(particle2A.pos);
//		
//		double ca = distA / (distA + distB + distC);
//		double cb = distB / (distA + distB + distC);
//		double cc = distC / (distA + distB + distC);
//		
//		particle2A.pos = particle2A.pos.add(contact.getCollisionNormal().mul(-contact.getCollisionDepth() * ca));
//		particle2B.pos = particle2B.pos.add(contact.getCollisionNormal().mul(-contact.getCollisionDepth() * cb));
//		particle2C.pos = particle2C.pos.add(contact.getCollisionNormal().mul(-contact.getCollisionDepth() * cc));
		
		
		
		if (!contact.isCollision()) return;
		
		
		boolean staticPart = particle1.isStatic || particle2A.isStatic || particle2B.isStatic;
		
		if (!particle1.isStatic) particle1.pos = particle1.pos.add(contact.getCollisionNormal().mul(contact.getCollisionDepth() * (staticPart ? 2 : 1.5)));
		
		double distA = particle1.pos.distance(particle2A.pos);
		double distB = particle1.pos.distance(particle2A.pos);
		double distC = particle1.pos.distance(particle2A.pos);
		
		double ca = distA / (distA + distB + distC);
		double cb = distB / (distA + distB + distC);
		double cc = distC / (distA + distB + distC);
		
		if (!particle2A.isStatic) particle2A.pos = particle2A.pos.add(contact.getCollisionNormal().mul(-contact.getCollisionDepth() * (staticPart ? 2 : 1.5) * cb));
		if (!particle2B.isStatic) particle2B.pos = particle2B.pos.add(contact.getCollisionNormal().mul(-contact.getCollisionDepth() * (staticPart ? 2 : 1.5) * ca));
		if (!particle2C.isStatic) particle2C.pos = particle2B.pos.add(contact.getCollisionNormal().mul(-contact.getCollisionDepth() * (staticPart ? 2 : 1.5) * cc));
		
	}
	
}
