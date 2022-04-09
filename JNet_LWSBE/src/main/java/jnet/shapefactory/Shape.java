package jnet.shapefactory;

import java.util.ArrayList;
import java.util.List;

import jnet.JNet;
import jnet.physic.SoftBody;
import jnet.physic.SoftBody.Constrain;
import jnet.physic.SoftBody.Particle;
import jnet.util.Vec2d;

/**
 * The Shape is like a Definition of a SoftBody, it can produce multiple identical SoftBodys.
 * @author M_Marvin
 *
 */
public class Shape {
	
	protected List<ConstrainDefinition> constrains;
	protected List<ParticleDefinition> particles;
	
	public Shape() {
		this.constrains = new ArrayList<ConstrainDefinition>();
		this.particles = new ArrayList<ParticleDefinition>();
	}
	
	/**
	 * Adds a constrain definition and (if not already added) its particle definitions to this Shape
	 * @param constrain The constrain definition to add
	 */
	public void addConstrain(ConstrainDefinition constrain) {
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
	public SoftBody build() {
		SoftBody body = new SoftBody();
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
	public ParticleDefinition searchNode(float x, float y) {
		Vec2d position = new Vec2d(x, y);
		for (ParticleDefinition node : this.particles) {
			if (node.pos.equals(position)) return node;
		}
		return null;
	}
	/**
	 * Returns the first ParticleDefinition with the given position or null if no matching ParticleDefinition is found
	 * @param position The position of the ParticleDefinition
	 * @return The first matching ParticleDefinition or null if no one is found
	 */
	public ParticleDefinition searchNode(Vec2d position) {
		for (ParticleDefinition node : this.particles) {
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
	public ConstrainDefinition searchBeam(Vec2d position1, Vec2d position2) {
		ParticleDefinition node1 = searchNode(position1);
		if (node1 == null) return null;
		ParticleDefinition node2 = searchNode(position2);
		if (node2 == null) return null;
		for (ConstrainDefinition beam : this.constrains) {
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
	public ConstrainDefinition searchBeam(float x1, float y1, float x2, float y2) {
		ParticleDefinition node1 = searchNode(x1, y1);
		if (node1 == null) return null;
		ParticleDefinition node2 = searchNode(x2, y2);
		if (node2 == null) return null;
		for (ConstrainDefinition beam : this.constrains) {
			if ((beam.pointA.equals(node1) && beam.pointB.equals(node2)) || (beam.pointA.equals(node2) && beam.pointB.equals(node1))) return beam;
		}
		return null;
	}
	
	/** ##########################################################**/
	
	public static class ConstrainDefinition {
		
		public ParticleDefinition pointA = new ParticleDefinition(); // Only for the equals check
		public ParticleDefinition pointB = new ParticleDefinition(); // Only for the equals check
		public float stiffness;
		public float deformForce;
		public float maxBending;
		
		public ConstrainDefinition() {}
		
		public ConstrainDefinition(ParticleDefinition pointA, ParticleDefinition pointB) {
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
		public Constrain build() {
			if (this.pointA.lastBuild == null || this.pointB.lastBuild == null) throw new RuntimeException(new IllegalStateException("Cant build Constrain bevore the Particles are not build!"));
			return new Constrain(this);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof ConstrainDefinition) {
				return	((ConstrainDefinition) obj).pointA.equals(this.pointA) &&
						((ConstrainDefinition) obj).pointB.equals(this.pointB) &&
						((ConstrainDefinition) obj).stiffness == this.stiffness &&
						((ConstrainDefinition) obj).deformForce == this.deformForce &&
						((ConstrainDefinition) obj).maxBending == this.maxBending;
			}
			return false;
		}
		
	}
	
	public static class ParticleDefinition {
		
		public Vec2d pos = new Vec2d();
		public float mass;
		
		public Particle lastBuild;
		
		public ParticleDefinition() {}
		
		public ParticleDefinition(Vec2d pos) {
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
		public Particle build() {
			this.lastBuild = new Particle(this);
			return this.lastBuild;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof ParticleDefinition) {
				return	((ParticleDefinition) obj).pos.equals(this.pos) &&
						((ParticleDefinition) obj).mass == this.mass;
			}
			return false;
		}
		
	}

	public List<ConstrainDefinition> getConstrains() {
		return constrains;
	}

	public List<ParticleDefinition> getParticles() {
		return particles;
	}
	
}
