package jnet.d2.physic;

import java.util.HashMap;

import javax.management.RuntimeErrorException;

import jnet.JNet;
import jnet.d2.physic.SoftBody2d.Constrain2d;
import jnet.d2.physic.SoftBody2d.Particle2d;
import jnet.d3.physic.SoftBody3d.Constrain3d;
import jnet.demo.Demo2D;
import jnet.util.Vec2d;
import jnet.util.VecMath;

/**
 * The core of the PhysicEngine, the solver handles the forces on the particles, the movement, the collisions and the constrains.
 * It is bound to a PhysicWorld2d and simulates the objects in it.
 * @author M_Marvin
 *
 */
public class PhysicSolver2d {
	
	protected int itterationCount;
	protected PhysicWorld2d world;
	
	public PhysicSolver2d(PhysicWorld2d world) {
		this.world = world;
		this.itterationCount = JNet.DEFAULT_NUM_ITTERATIONS;
	}
	
	/**
	 * Manually changes the world, that this solver handles
	 * @param world The new world for this solver
	 */
	public void setWorld(PhysicWorld2d world) {
		this.world = world;
	}
	
	/**
	 * Gets the world that this solver currently handles
	 * @return
	 */
	public PhysicWorld2d getWorld() {
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
	 */
	public void solve() {
		
		if (this.world == null) {
			throw new RuntimeErrorException(new Error("Cant run without world set!"), "Error on run PhysicSolver!");
		}
		
		// Solve Objects
		this.world.getSoftBodys().forEach((shape) -> {
			
			// Integrate motion
			shape.getParticles().forEach((point) -> {
				integrate(point);
			});
			
			// Accumulate Global Forces
			shape.getParticles().forEach((point) -> {
				if (!point.isStatic) {
					point.lastPos = point.lastPos.sub(new Vec2d(world.getGlobalForce().mul(0.5F)));
					point.pos = point.pos.add(new Vec2d(world.getGlobalForce().mul(0.5F)));
				}
			});
			
			// Repeat constrain satisfying, limited cycles
			for (int itteration = 0; itteration < itterationCount; itteration++) {
				
				// Satisfy Constrains
				for (Constrain2d constrain : shape.getConstrains()) {
					if (!constrain.broken) {
						constrain(itteration + 1, constrain);
					}
				}
				
				// Satisfy Joints
				for (Constrain2d constrain : this.world.getJoints()) {
					if (!constrain.broken) {
						constrain(itteration + 1, constrain);
					}
				}
				
			}
			
		});
		
		this.world.getSoftBodys().forEach((shape) -> {

			// Check Constrain-collisions
			HashMap<Contact2d, SoftBody2d> collisions = new HashMap<Contact2d, SoftBody2d>();
			shape.getParticles().forEach((particle) -> {
				this.world.getSoftBodys().forEach((shape2) -> shape2.getConstrains().forEach((constrain) -> {
					if (constrain.pointA != particle && constrain.pointB != particle && !constrain.broken) {
						Contact2d contact = checkContact(particle, constrain);
						if (contact.isCollision()) {
							collisions.put(contact, shape2);
						}
					}
				}));
			});
			
			// Solve contacts
			collisions.keySet().forEach((collision) -> {
				if (this.world.getContactListener().beginContact(collision)) {
					boolean processCollision = true;
					SoftBody2d shape2 = collisions.get(collision);
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
			
		});
		
	}
	
	/**
	 * Solve a single constrain (restore original length, if deformed)
	 * @param itteration The number of the iteration, used to calculate the strength of the "reform-force", higher number -> less strength
	 * @param constrain The Constrain to reform
	 */
	public void constrain(int itteration, Constrain2d constrain) {
		
		// Calculate force on the constrain
		Vec2d forceA = constrain.pointA.getMotion().mul(constrain.pointA.mass);
		Vec2d forceB = constrain.pointB.getMotion().mul(constrain.pointA.mass);
		double angle = constrain.pointA.pos.angle(constrain.pointB.pos);
		double force = forceA.forceByAngle(angle).add(forceB.forceByAngle(angle + Math.PI)).summ();
		force = (force < 0 ? -force : force);
		
		// Calculate spring deformation
		Vec2d delta = constrain.pointB.pos.sub(constrain.pointA.pos);
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
	public void integrate(Particle2d point) {
		Vec2d temp = point.pos;//.add(point.acceleration.mul(deltaT * deltaT))
		
		if (!point.isStatic) point.pos = point.pos.add(point.pos.sub(point.lastPos));
		point.lastPos = temp;
	}
	
	/**
	 * Check if there is a collision between the given particle and the Constrain since the last simulation step
	 * @param particle The Particle
	 * @param constrain The Constrain
	 * @return A Contact representing the collision between the two instances, its variables are null if there is no collision
	 */
	public Contact2d checkContact(Particle2d particle, Constrain2d constrain) {
		
		//if (!constrain.collision) return Contact2d.noContact();
		
		// Nötige gleichungen
		// Treffpunkt von 2 Strahlen
		// "On Line" Prüfung
		// Nearest Point on Line
		
		
		boolean result = checkIntersection(particle, constrain);
		Vec2d tlp = particle.lastPos;
		Vec2d mv = constrain.pointA.pos.sub(constrain.pointA.lastPos).add(constrain.pointB.pos.sub(constrain.pointB.lastPos).mul(2));
		
		if (result) {
			
			// Calculate movement of the collision point on the constrain
			Vec2d collisionPoint = VecMath.nearestPointOnLine(particle.pos, constrain.pointA.pos, constrain.pointB.pos);
			Vec2d collisionNormal = particle.pos.noramlVec(collisionPoint);
			double collisionDepth = particle.pos.distance(collisionPoint);
			
			if (collisionDepth - tlp.distance(particle.pos) > mv.length()) return Contact2d.noContact();
			
			return Contact2d.contact(collisionNormal, collisionDepth, particle, constrain);
			
		}
		
		return Contact2d.noContact();
		
		
		
		
		
		
		
		
//		// Calculate movement of the collision point on the constrain
//		Vec2d collisionPoint = nearestPointOnLine(particle.pos, constrain.pointA.pos, constrain.pointB.pos);
//		double distPointA = collisionPoint.distance(constrain.pointA.pos);
//		double distPointB = collisionPoint.distance(constrain.pointB.pos);
//		Vec2d motionA = constrain.pointA.pos.sub(constrain.pointA.lastPos).mul(distPointB / (distPointA + distPointB));
//		Vec2d motionB = constrain.pointB.pos.sub(constrain.pointB.lastPos).mul(distPointA / (distPointA + distPointB));
//		Vec2d collisionPointMovement = motionA.add(motionB);
//		
//		// Check intersection of the particle and the constrain, compensate constrain movement
//		Contact2d contact = checkLineIntersection(constrain.pointA.pos, constrain.pointB.pos, particle.pos.add(collisionPointMovement), particle.lastPos.add(collisionPointMovement), constrain, particle);
//		
//		return contact;
		
	};
	
	protected boolean checkIntersection(Particle2d particle, Constrain2d constrain) {
		
		Vec2d L11 = constrain.pointA.pos;
		Vec2d L12 = constrain.pointB.pos;
		Vec2d L1last1 = constrain.pointA.lastPos;
		Vec2d L1last2 = constrain.pointB.lastPos;
		Vec2d P1 = particle.pos;
		Vec2d P1last = particle.lastPos;
		
		Vec2d p1 = VecMath.lineIntersection(P1last, P1, L11, L12);
		Vec2d p2 = VecMath.lineIntersection(P1last, P1, L1last1, L1last2);
		Vec2d p3 = VecMath.lineIntersection(P1last, P1, L11, L1last1);
		Vec2d p4 = VecMath.lineIntersection(P1last, P1, L12, L1last2);
		
		// Particle collision
		
		if (VecMath.pointOnLine(p1, L11, L12) && VecMath.pointOnLine(p2, L1last1, L1last2)) {
			
			if (VecMath.pointOnLine(P1, p1, p2)) {
				
				if (VecMath.pointOnLine(P1, p1, p2) && VecMath.pointOnLine(p1, P1, P1last)) return true;
				if (VecMath.pointOnLine(P1last, p1, p2) && VecMath.pointOnLine(p1, P1, P1last)) return true;
			}
			
			if (VecMath.pointOnLine(p1, P1, P1last) && VecMath.pointOnLine(p2, P1, P1last)) return true;
			
		} else {

			// Constrain-Particle collision
			
			if (VecMath.pointOnLine(p3, P1, P1last) || VecMath.pointOnLine(p4, P1, P1last)) {
				if (VecMath.pointOnLine(p3, L11, L1last1)) return true;
				if (VecMath.pointOnLine(p4, L12, L1last2)) return true;
			} else {
				
				// Constrain collision
				// FIXME
				
				double distP1 = P1.distance(L11) + P1.distance(L12);
				double distP2 = P1last.distance(L11) + P1last.distance(L12);
				
				Vec2d pointNear = distP1 < distP2 ? P1 : P1last;
				Vec2d pointFar = distP1 < distP2 ? P1last : P1;
				
				Vec2d lineMovement1 = L11.sub(L1last1);
				Vec2d lineMovement2 = L12.sub(L1last2);
				
				Vec2d lineMovement = lineMovement1.length() > lineMovement2.length() ? lineMovement1 : lineMovement2;
				
				pointNear = pointNear.add(lineMovement.mul(2));
				pointFar = pointFar.sub(lineMovement.mul(2));
				
				Vec2d p1near = VecMath.lineIntersection(pointFar, pointNear, L11, L12);
				Vec2d p1far = VecMath.lineIntersection(pointFar, pointNear, L1last1, L1last2);
				
				if (VecMath.pointOnLine(p1near, L11, L12) && VecMath.pointOnLine(p1far, L1last1, L1last2) &&
					VecMath.pointOnLine(p1near, pointFar, pointNear) && VecMath.pointOnLine(p1far, pointFar, pointNear)) {
					
					return true;
					
				}
				
			}
			
		}

		return false;
				
	}
	
	/**
	 * Sets position and forces to solve the given collision
	 * @param contact The Contact that represents the collision
	 */
	public void solveCollision(Contact2d contact) {
		
		System.out.println("CONTACT" + contact.getCollisionDepth());
		
		if (!contact.isCollision()) return;
		
		Particle2d particle1 = contact.getParticle();
		Particle2d particle2A = contact.getConstrain().pointA;
		Particle2d particle2B = contact.getConstrain().pointB;
		
		boolean staticPart = particle1.isStatic || particle2A.isStatic || particle2B.isStatic;
		
		if (!particle1.isStatic) particle1.pos = particle1.pos.add(contact.getCollisionNormal().mul(contact.getCollisionDepth() * (staticPart ? 2 : 1.5)));
		
		double distA = particle1.pos.distance(particle2A.pos);
		double distB = particle1.pos.distance(particle2B.pos);
		double ca = distA / (distA + distB);
		double cb = distB / (distA + distB);

//		Vec2d particle1movement = particle1.pos.sub(particle1.lastPos).forceByAngle(contact.getCollisionNormal().angle(new Vec2d()) + 90);
//		Vec2d particle2Amovement = particle2A.pos.sub(particle2A.lastPos).forceByAngle(contact.getCollisionNormal().angle(new Vec2d()) + 90);
//		Vec2d particle2Bmovement = particle2B.pos.sub(particle2B.lastPos).forceByAngle(contact.getCollisionNormal().angle(new Vec2d()) + 90);
		
		if (!particle2A.isStatic) particle2A.pos = particle2A.pos.add(contact.getCollisionNormal().mul(-contact.getCollisionDepth() * (staticPart ? 2 : 1.5) * cb));
		if (!particle2B.isStatic) particle2B.pos = particle2B.pos.add(contact.getCollisionNormal().mul(-contact.getCollisionDepth() * (staticPart ? 2 : 1.5) * ca));
		
//		if (!particle1.isStatic) particle1.pos = particle1.pos.add(particle1movement.mul(-0.6F).add(contact.getCollisionNormal()).mul(0.5F));
//		if (!particle2A.isStatic) particle2A.pos = particle2A.pos.add(particle2Amovement.mul(-0.6F).add(contact.getCollisionNormal()).mul(-0.5F));
//		if (!particle2B.isStatic) particle2B.pos = particle2B.pos.add(particle2Bmovement.mul(-0.6F).add(contact.getCollisionNormal()).mul(-0.5F));
//		
//		System.out.println("R" + particle1movement);
		
	}
	
	static double c;
	
}
