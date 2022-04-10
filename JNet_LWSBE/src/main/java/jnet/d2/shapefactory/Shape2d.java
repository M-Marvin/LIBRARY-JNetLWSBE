package jnet.d2.shapefactory;

import java.util.ArrayList;
import java.util.List;

import jnet.JNet;
import jnet.d2.physic.SoftBody2d;
import jnet.d2.physic.SoftBody2d.Constrain2d;
import jnet.d2.physic.SoftBody2d.Particle2d;
import jnet.util.Material;
import jnet.util.Vec2d;

/**
 * The Shape is like a Definition of a SoftBody, it can produce multiple identical SoftBodys.
 * @author M_Marvin
 *
 */
public class Shape2d {
	
	protected List<ConstrainDefinition2d> constrains;
	protected List<ParticleDefinition2d> particles;
	
	public Shape2d() {
		this.constrains = new ArrayList<ConstrainDefinition2d>();
		this.particles = new ArrayList<ParticleDefinition2d>();
	}
	
	/**
	 * Adds a constrain definition and (if not already added) its particle definitions to this Shape
	 * @param constrain The constrain definition to add
	 */
	public void addConstrain(ConstrainDefinition2d constrain) {
		this.constrains.add(constrain);
		if (!this.particles.contains(constrain.pointA)) this.particles.add(constrain.pointA);
		if (!this.particles.contains(constrain.pointB)) this.particles.add(constrain.pointB);
	}
	
	/**
	 * Like the method of the SoftBody, changes the material-property of all constrain definitions and particle definitions
	 * @param material The new material-info
	 */
	public void changeMaterial(Material material) {
		this.constrains.forEach((constrain) -> constrain.changeMaterial(material));
	}
	
	/**
	 * Creates a new SoftBody with the parameters of this Shape
	 * @return A new SoftBody instance with the parameters of this Shape
	 */
	public SoftBody2d build() {
		SoftBody2d body = new SoftBody2d();
		this.particles.forEach((particleDefinition) -> {
			particleDefinition.build();
		});
		this.constrains.forEach((constrainDefinition) -> {
			body.addConstrain(constrainDefinition.build());
		});
		return body;
	}
	
	/**
	 * Returns the first ParticleDefinition with the given position or null if no matching ParticleDefinition is found
	 * @param x The x position of the ParticleDefinition
	 * @param y The y position of the ParticleDefinition
	 * @return The first matching ParticleDefinition or null if no one is found
	 */
	public ParticleDefinition2d searchNode(float x, float y) {
		Vec2d position = new Vec2d(x, y);
		for (ParticleDefinition2d node : this.particles) {
			if (node.pos.equals(position)) return node;
		}
		return null;
	}
	/**
	 * Returns the first ParticleDefinition with the given position or null if no matching ParticleDefinition is found
	 * @param position The position of the ParticleDefinition
	 * @return The first matching ParticleDefinition or null if no one is found
	 */
	public ParticleDefinition2d searchNode(Vec2d position) {
		for (ParticleDefinition2d node : this.particles) {
			if (node.pos.equals(position)) return node;
		}
		return null;
	}
	
	/**
	 * Returns the first ConstrainDefinition with the ends at the given positions or null if no matching ConstrainDefinition or no ParticleDefinitions at the positions are found
	 * @param position1 The position of the first ParticleDefinition
	 * @param position2 The position of the second ParticleDefinition
	 * @return The first matching ConstrainDefinition or null if no one is found
	 */
	public ConstrainDefinition2d searchBeam(Vec2d position1, Vec2d position2) {
		ParticleDefinition2d node1 = searchNode(position1);
		if (node1 == null) return null;
		ParticleDefinition2d node2 = searchNode(position2);
		if (node2 == null) return null;
		for (ConstrainDefinition2d beam : this.constrains) {
			if ((beam.pointA.equals(node1) && beam.pointB.equals(node2)) || (beam.pointA.equals(node2) && beam.pointB.equals(node1))) return beam;
		}
		return null;
	}
	
	/**
	 * Returns the first ConstrainDefinition with the ends at the given positions or null if no matching ConstrainDefinition or no ParticleDefinitions at the positions are found
	 * @param x1 The x position of the first ParticleDefinition
	 * @param y1 The y position of the first ParticleDefinition
	 * @param x2 The x position of the second ParticleDefinition
	 * @param y2 The y position of the second ParticleDefinition
	 * @return The first matching ConstrainDefinition or null if no one is found
	 */
	public ConstrainDefinition2d searchBeam(float x1, float y1, float x2, float y2) {
		ParticleDefinition2d node1 = searchNode(x1, y1);
		if (node1 == null) return null;
		ParticleDefinition2d node2 = searchNode(x2, y2);
		if (node2 == null) return null;
		for (ConstrainDefinition2d beam : this.constrains) {
			if ((beam.pointA.equals(node1) && beam.pointB.equals(node2)) || (beam.pointA.equals(node2) && beam.pointB.equals(node1))) return beam;
		}
		return null;
	}
	
	/** ##########################################################**/
	
	public static class ConstrainDefinition2d {
		
		public ParticleDefinition2d pointA = new ParticleDefinition2d(); // Only for the equals check
		public ParticleDefinition2d pointB = new ParticleDefinition2d(); // Only for the equals check
		public float stiffness;
		public float deformForce;
		public float maxBending;
		
		public ConstrainDefinition2d() {}
		
		public ConstrainDefinition2d(ParticleDefinition2d pointA, ParticleDefinition2d pointB) {
			this.pointA = pointA;
			this.pointB = pointB;
			this.changeMaterial(JNet.DEFAULT_MATERIAL);
		}
		
		/**
		 * Like the method of the Constrain, changes the material-property of the constrain definition and its particle definitions
		 * @param material The new material-info
		 */
		public void changeMaterial(Material material) {
			this.stiffness = material.getStiffness();
			this.deformForce = material.getDeformForce();
			this.maxBending = material.getMaxBending();
			this.pointA.changeMaterial(material);
			this.pointB.changeMaterial(material);
		}
		
		/**
		 * Creates a new instance of Constrain with this parameters
		 * IMPORTANT: Build the ParticleDfinitions first!
		 * @return A new Constrain with the parameters of this definition
		 * @throws RuntimeException of a IllegalStateException when the used ParticleDefinitions are not build
		 */
		public Constrain2d build() {
			if (this.pointA.lastBuild == null || this.pointB.lastBuild == null) throw new RuntimeException(new IllegalStateException("Cant build Constrain bevore the Particles are not build!"));
			return new Constrain2d(this);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof ConstrainDefinition2d) {
				return	((ConstrainDefinition2d) obj).pointA.equals(this.pointA) &&
						((ConstrainDefinition2d) obj).pointB.equals(this.pointB) &&
						((ConstrainDefinition2d) obj).stiffness == this.stiffness &&
						((ConstrainDefinition2d) obj).deformForce == this.deformForce &&
						((ConstrainDefinition2d) obj).maxBending == this.maxBending;
			}
			return false;
		}
		
	}
	
	public static class ParticleDefinition2d {
		
		public Vec2d pos = new Vec2d();
		public float mass;
		
		public Particle2d lastBuild;
		
		public ParticleDefinition2d() {}
		
		public ParticleDefinition2d(Vec2d pos) {
			this.pos = pos;
			this.changeMaterial(JNet.DEFAULT_MATERIAL);
		}
		/**
		 * Like the method of the Particle, changes the material-property of the definition
		 * @param material The new material-info
		 */
		public void changeMaterial(Material material) {
			this.mass = material.getMass();
		}
		
		/**
		 * Creates a new instance of Particle with this parameters
		 * @return A new Particle with the parameters of this definition
		 */
		public Particle2d build() {
			this.lastBuild = new Particle2d(this);
			return this.lastBuild;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof ParticleDefinition2d) {
				return	((ParticleDefinition2d) obj).pos.equals(this.pos) &&
						((ParticleDefinition2d) obj).mass == this.mass;
			}
			return false;
		}
		
	}

	public List<ConstrainDefinition2d> getConstrains() {
		return constrains;
	}

	public List<ParticleDefinition2d> getParticles() {
		return particles;
	}
	
}
