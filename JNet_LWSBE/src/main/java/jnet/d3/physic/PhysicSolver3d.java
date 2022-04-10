package jnet.d3.physic;

import java.util.HashMap;

import javax.management.RuntimeErrorException;

import jnet.JNet;
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
	 * Check if there is a collision between the given particle and the Constrain since the last simulation step
	 * @param particle The Particle
	 * @param constrain The Constrain
	 * @return A Contact representing the collision between the two instances, its variables are null if there is no collision
	 */
	public Contact3d checkContact(Particle3d particle, CollisionPlane3d plane) {
		
		Vec3d v0 = plane.particleA.pos;
		Vec3d v1 = plane.particleB.pos;
		Vec3d v2 = plane.particleC.pos;
		
		// Make ray from particle motion
		Vec3d orig = particle.lastPos;
		Vec3d dir = particle.lastPos.sub(particle.pos).normalize();
		
		//double t = 01010101F; // ????
		float tol = 0.0000008F; // kEpsi...
		double t = tol;
		
		// compute plane's normal
		Vec3d v0v1 = v1.sub(v0);
		Vec3d v0v2 = v2.sub(v0);
		Vec3d N = v0v1.cross(v0v2);
		double area2 = N.length();
		
		
		
		// Step 1: finding P
			 
		// check if ray and plane are parallel ?
		double NdotRayDirection = N.dot(dir); 
		if (Math.abs(NdotRayDirection) < tol) return Contact3d.noContact(); // they are parallel so they don't intersect ! 
		
		
		double d = N.mul(-1).dot(v0); 
		t = -(N.dot(orig) + d) / NdotRayDirection; 
		
		// check if the triangle is in behind the ray
		if (t < 0) return Contact3d.noContact(); // the triangle is behind 
		
		// compute the intersection point using equation 1
		Vec3d P = orig.add(dir.mul(t)); 
		
		// Step 2: inside-outside test
		Vec3d C; // vector perpendicular to triangle's plane 
		
		// edge 0
		Vec3d edge0 = v1.sub(v0); 
		Vec3d vp0 = P.sub(v0); 
		C = edge0.cross(vp0); 
		if (N.dot(C) < 0) return Contact3d.noContact(); // P is on the right side 
		
		// edge 1
		Vec3d edge1 = v2.sub(v1); 
		Vec3d vp1 = P.sub(v1); 
		C = edge1.cross(vp1); 
		if (N.dot(C) < 0)  return Contact3d.noContact(); // P is on the right side 
		
		// edge 2
		Vec3d edge2 = v0.sub(v2); 
		Vec3d vp2 = P.sub(v2); 
		C = edge2.cross(vp2); 
		if (N.dot(C) < 0) return Contact3d.noContact(); // P is on the right side; 
		
		
		Vec3d collisionNormal = particle.pos.noramlVec(P);
		double collisionDepth = particle.pos.distance(P);
		return Contact3d.contact(collisionNormal, collisionDepth, particle, plane);
		
//		// Phase 1 check: Vector (infinity line) intersection check
//		Vec3d line1a = constrain.pointA.pos;
//		Vec3d line1b = constrain.pointB.pos;
//		Vec3d line2a = particle.pos;
//		Vec3d line2b = particle.lastPos;
//		double denom =	(line2b.y - line2a.y) * (line1b.x - line1a.x) - (line2b.x - line2a.x) * (line1b.y - line1a.y);
//		
//		
//		if (Math.abs(denom) < 0.000000008) return Contact.noContact();
//		
//		// Phase 2 check: Line intersection check
//		double ua = ((line2b.x - line2a.x) * (line1a.y - line2a.y) - (line2b.y - line2a.y) * (line1a.x - line2a.x)) / denom;
//		double ub = ((line1b.x - line1a.x) * (line1a.y - line2a.y) - (line1b.y - line1a.y) * (line1a.x - line2a.x)) / denom;
//		if (!(ua >= 0 && ua <= 1 && ub >= 0 && ub <= 1)) return Contact.noContact();
//		
//		// Calculate nearest point on constrain
//		Vec2d v = line1b.sub(line1a);
//		Vec2d w = particle.pos.sub(line1a);
//		double b = w.dot(v) / v.dot(v);
//		Vec2d nearestOnConstrain = line1a.add(v.mul(b));
//		
//		double collisionDepth = particle.pos.distance(nearestOnConstrain);
//		Vec2d collisionNormal = particle.pos.noramlVec(nearestOnConstrain);
//		return Contact.contact(collisionNormal, collisionDepth, particle, constrain);
		
	};
	
	/**
	 * Sets position and forces to solve the given collision
	 * @param contact The Contact that represents the collision
	 * @throws A RuntimeException of an IllegalStateException if the given Contact is not a collision
	 */
	public void solveCollision(Contact3d contact) {
		
		if (!contact.isCollision()) throw new RuntimeException(new IllegalStateException("Can not handle non-collision Contact instance!"));
		
		System.out.println("COLLISION");
		
//		Particle3d particle1 = contact.getParticle();
//		Particle3d particle2A = contact.getConstrain().pointA;
//		Particle3d particle2B = contact.getConstrain().pointB;
//		
//		particle1.pos = particle1.pos.add(contact.getCollisionNormal().mul(contact.getCollisionDepth() / 1.9F));
//		
//		double distA = particle1.pos.distance(particle2A.pos);
//		double distB = particle1.pos.distance(particle2B.pos);
//		double ca = distA / (distA + distB);
//		double cb = distB / (distA + distB);
//		
//		particle2A.pos = particle2A.pos.add(contact.getCollisionNormal().mul(-contact.getCollisionDepth() * cb / 1.9F));
//		particle2B.pos = particle2B.pos.add(contact.getCollisionNormal().mul(-contact.getCollisionDepth() * ca / 1.9F));
		
	}
	
}
