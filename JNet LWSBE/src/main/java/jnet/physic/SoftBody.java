package jnet.physic;

import java.util.ArrayList;
import java.util.List;

import jnet.JNet;
import jnet.shapefactory.Material;
import jnet.shapefactory.Shape.ConstrainDefinition;
import jnet.shapefactory.Shape.ParticleDefinition;
import jnet.util.Vec2d;

/**
 * The SoftBody represents a object to simulate by the PhysicSolver, it must be added to a PhysicWorld.
 * @author M_Marvin
 *
 */
public class SoftBody {
	
	protected List<Constrain> constrains;
	protected List<Particle> particles;
	protected ContactListener contactListener;
	
	/**
	 * Creates a clone instance of the original SoftBody, it does not create a new SoftBody. The ContactListener is not copied.
	 * @param body The SoftBody to create a clone instance
	 */
	public SoftBody(SoftBody body) {
		this.constrains = body.constrains;
		this.particles = body.particles;
		this.contactListener = new ContactListener.DummyListener();
	}
	
	/**
	 * Creates a empty SoftBody, Constrains and its Particles can be added manually.
	 */
	public SoftBody() {
		this.constrains = new ArrayList<Constrain>();
		this.particles = new ArrayList<Particle>();
		this.contactListener = new ContactListener.DummyListener();
	}
	
	/**
	 * Adds the Constrain and (if not already added) its Particles to this SoftBody
	 * @param constrain The Constrain to add
	 */
	public void addConstrain(Constrain constrain) {
		this.constrains.add(constrain);
		if (!this.particles.contains(constrain.pointA)) this.particles.add(constrain.pointA);
		if (!this.particles.contains(constrain.pointB)) this.particles.add(constrain.pointB);
	}
	
	/**
	 * Manually changes the material-property of all Constrains and all Particles (Nodes) with the given combined Material-Info
	 * @param material The Material-Info with the new material-property
	 */
	public void changeMaterial(Material material) {
		this.constrains.forEach((constrain) -> constrain.changeMaterial(material));
	}
	
	public List<Constrain> getConstrains() {
		return constrains;
	}
	
	public List<Particle> getParticles() {
		return particles;
	}
	
	/**
	 * Sets the ContactListener which receives any collision-events between this and all other objects (including collisions between Constrains of this object).
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
	
	public static class Constrain {
		
		public Particle pointA;
		public Particle pointB;
		public double length;
		public double originalLength;
		public boolean broken = false;
		public float stiffness;
		public float deformForce;
		public float maxBending;
		
		/**
		 * Construct Constrain using ConstrainDefinition
		 * @param definition Definition for this Constrain
		 */
		public Constrain(ConstrainDefinition definition) {
			this.pointA = definition.pointA.lastBuild;
			this.pointB = definition.pointB.lastBuild;
			this.length = pointA.pos.distance(pointB.pos);
			this.originalLength = length;
			this.stiffness = definition.stiffness;
			this.deformForce = definition.deformForce;
			this.maxBending = definition.maxBending;
		}
		
		/***
		 * Construct Constrain using Nodes and combined Material-Info
		 * @param pointA Node A of the Constrain
		 * @param pointB Node B of the Constrain
		 * @param material Combined Material-Info
		 */
		public Constrain(Particle pointA, Particle pointB, Material material) {
			this.pointA = pointA;
			this.pointB = pointB;
			this.length = pointA.pos.distance(pointB.pos);
			this.originalLength = length;
			this.changeMaterial(material);
		}
		
		/**
		 * Construct Constrain using Nodes and default Material
		 * @param pointA Node A of the Constrain
		 * @param pointB Node B of the Constrain
		 */
		public Constrain(Particle pointA, Particle pointB) {
			this.pointA = pointA;
			this.pointB = pointB;
			this.length = pointA.pos.distance(pointB.pos);
			this.originalLength = length;
			this.changeMaterial(JNet.DEFAULT_MATERIAL);
		}
		
		/**
		 * Manually changes the material-property of the Constrain and its Particles (Nodes) with the given combined Material-Info
		 * @param material The Material-Info with the new material-property
		 */
		public void changeMaterial(Material material) {
			this.stiffness = material.getStiffness();
			this.deformForce = material.getDeformForce();
			this.maxBending = material.getMaxBending();
			this.pointA.changeMaterial(material);
			this.pointB.changeMaterial(material);
		}
		
	}
	
	public static class Particle {
	
		public Vec2d pos = new Vec2d();
		public Vec2d lastPos = new Vec2d();
		public Vec2d acceleration = new Vec2d();
		public float mass;
		
		/**
		 * Construct Particle (Node) using ParticleDefinition
		 * @param definition Definition for this Particle
		 */
		public Particle(ParticleDefinition definition) {
			this.pos = definition.pos;
			this.lastPos = pos;
			this.acceleration = new Vec2d();
			this.mass = definition.mass;
		}
		
		/**
		 * Construct Particle (Node) using Position and combined Material-Info
		 * @param pos Position of the Node
		 * @param material Combined Material-Info
		 */
		public Particle(Vec2d pos, Material material) {
			this.pos = pos;
			this.lastPos = pos;
			this.acceleration = new Vec2d();
			this.changeMaterial(material);
		}
		
		/**
		 * Construct Particle (Node) using Position and default Material
		 * @param pos Position of the Node
		 */
		public Particle(Vec2d pos) {
			this.pos = pos;
			this.lastPos = pos;
			this.acceleration = new Vec2d();
			this.changeMaterial(JNet.DEFAULT_MATERIAL);
		}
		
		/**
		 * Manually changes the material-property of the Particle with the given combined Material-Info
		 * @param material The Material-Info with the new material-property
		 */
		public void changeMaterial(Material material) {
			this.mass = material.getMass();
		}
		
		/**
		 * Manually set the position of the Particle (Node)
		 * @param x X position of the Particle
		 * @param y Y position of the Particle
		 */
		public void setPos(double x, double y) {
			this.setPos(new Vec2d(x, y));
		}
		/**
		 * Manually set the position of the Particle (Node)
		 * @param pos Position of the Particle
		 */
		public void setPos(Vec2d pos) {
			this.pos = pos;
			this.lastPos = new Vec2d(pos);
		}
		
		/**
		 * Calculates the motion-vector for this Particle
		 * @return A Vec2d that represents the motion of this Particle
		 */
		public Vec2d getMotion() {
			return this.pos.sub(this.lastPos);
		}
		
		/**
		 * Uses a motion-vector to set the new velocity
		 * @param motion A Vec2d that represents the new motion of this Particle
		 */
		public void setMotion(Vec2d motion) {
			this.lastPos = this.pos.sub(motion);
		}
			
	}
	
}
