package jnet.d3.physic;

import java.util.ArrayList;
import java.util.List;

import jnet.JNet;
import jnet.d3.shapefactory.Shape3d.ConstrainDefinition3d;
import jnet.d3.shapefactory.Shape3d.ParticleDefinition3d;
import jnet.util.Material;
import jnet.util.Vec3d;

/**
 * The SoftBody represents a object to simulate by the PhysicSolver, it must be added to a PhysicWorld.
 * @author M_Marvin
 *
 */
public class SoftBody3d {
	
	protected List<Constrain3d> constrains;
	protected List<Particle3d> particles;
	protected ContactListener3d contactListener;
	
	/**
	 * Creates a clone instance of the original SoftBody, it does not create a new SoftBody. The ContactListener is not copied.
	 * @param body The SoftBody to create a clone instance
	 */
	public SoftBody3d(SoftBody3d body) {
		this.constrains = body.constrains;
		this.particles = body.particles;
		this.contactListener = new ContactListener3d.DummyListener();
	}
	
	/**
	 * Creates a empty SoftBody, Constrains and its Particles can be added manually.
	 */
	public SoftBody3d() {
		this.constrains = new ArrayList<Constrain3d>();
		this.particles = new ArrayList<Particle3d>();
		this.contactListener = new ContactListener3d.DummyListener();
	}
	
	/**
	 * Adds the Constrain and (if not already added) its Particles to this SoftBody
	 * @param constrain The Constrain to add
	 */
	public void addConstrain(Constrain3d constrain) {
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
	
	public List<Constrain3d> getConstrains() {
		return constrains;
	}
	
	public List<Particle3d> getParticles() {
		return particles;
	}
	
	/**
	 * Sets the ContactListener which receives any collision-events between this and all other objects (including collisions between Constrains of this object).
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
	
	public static class Constrain3d {
		
		public Particle3d pointA;
		public Particle3d pointB;
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
		public Constrain3d(ConstrainDefinition3d definition) {
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
		public Constrain3d(Particle3d pointA, Particle3d pointB, Material material) {
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
		public Constrain3d(Particle3d pointA, Particle3d pointB) {
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
	
	public static class Particle3d {
	
		public Vec3d pos = new Vec3d();
		public Vec3d lastPos = new Vec3d();
		public Vec3d acceleration = new Vec3d();
		public float mass;
		
		/**
		 * Construct Particle (Node) using ParticleDefinition
		 * @param definition Definition for this Particle
		 */
		public Particle3d(ParticleDefinition3d definition) {
			this.pos = definition.pos;
			this.lastPos = pos;
			this.acceleration = new Vec3d();
			this.mass = definition.mass;
		}
		
		/**
		 * Construct Particle (Node) using Position and combined Material-Info
		 * @param pos Position of the Node
		 * @param material Combined Material-Info
		 */
		public Particle3d(Vec3d pos, Material material) {
			this.pos = pos;
			this.lastPos = pos;
			this.acceleration = new Vec3d();
			this.changeMaterial(material);
		}
		
		/**
		 * Construct Particle (Node) using Position and default Material
		 * @param pos Position of the Node
		 */
		public Particle3d(Vec3d pos) {
			this.pos = pos;
			this.lastPos = pos;
			this.acceleration = new Vec3d();
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
		public void setPos(double x, double y, float z) {
			this.setPos(new Vec3d(x, y, z));
		}
		/**
		 * Manually set the position of the Particle (Node)
		 * @param pos Position of the Particle
		 */
		public void setPos(Vec3d pos) {
			this.pos = pos;
			this.lastPos = new Vec3d(pos);
		}
		
		/**
		 * Calculates the motion-vector for this Particle
		 * @return A Vec3d that represents the motion of this Particle
		 */
		public Vec3d getMotion() {
			return this.pos.sub(this.lastPos);
		}
		
		/**
		 * Uses a motion-vector to set the new velocity
		 * @param motion A Vec3d that represents the new motion of this Particle
		 */
		public void setMotion(Vec3d motion) {
			this.lastPos = this.pos.sub(motion);
		}
			
	}
	
}
