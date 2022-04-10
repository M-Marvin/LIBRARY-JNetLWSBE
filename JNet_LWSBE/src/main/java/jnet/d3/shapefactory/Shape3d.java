package jnet.d3.shapefactory;

import java.util.ArrayList;
import java.util.List;

import jnet.JNet;
import jnet.d3.physic.SoftBody3d;
import jnet.d3.physic.SoftBody3d.CollisionPlane3d;
import jnet.d3.physic.SoftBody3d.Constrain3d;
import jnet.d3.physic.SoftBody3d.Particle3d;
import jnet.util.Material;
import jnet.util.Vec3d;

/**
 * The Shape is like a Definition of a SoftBody, it can produce multiple identical SoftBodys.
 * @author M_Marvin
 *
 */
public class Shape3d {
	
	protected List<CollisionPlaneDefinition3d> planes;
	protected List<ConstrainDefinition3d> constrains;
	protected List<ParticleDefinition3d> particles;
	
	public Shape3d() {
		this.planes = new ArrayList<CollisionPlaneDefinition3d>();
		this.constrains = new ArrayList<ConstrainDefinition3d>();
		this.particles = new ArrayList<ParticleDefinition3d>();
	}
	
	/**
	 * Adds the CollisionPlane and (if not already added) its Constrains to this SoftBody
	 * @param constrain The Constrain to add
	 */
	public void addPlane(CollisionPlaneDefinition3d plane) {
		this.planes.add(plane);
		if (!this.constrains.contains(plane.constrainA)) addConstrain(plane.constrainA);
		if (!this.constrains.contains(plane.constrainB)) addConstrain(plane.constrainB);
		if (!this.constrains.contains(plane.constrainC)) addConstrain(plane.constrainC);
	}
	
	/**
	 * Adds a constrain definition and (if not already added) its particle definitions to this Shape
	 * @param constrain The constrain definition to add
	 */
	public void addConstrain(ConstrainDefinition3d constrain) {
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
	public SoftBody3d build() {
		SoftBody3d body = new SoftBody3d();
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
	public ParticleDefinition3d searchNode(float x, float y, float z) {
		Vec3d position = new Vec3d(x, y, z);
		for (ParticleDefinition3d node : this.particles) {
			if (node.pos.equals(position)) return node;
		}
		return null;
	}
	/**
	 * Returns the first ParticleDefinition with the given position or null if no matching ParticleDefinition is found
	 * @param position The position of the ParticleDefinition
	 * @return The first matching ParticleDefinition or null if no one is found
	 */
	public ParticleDefinition3d searchNode(Vec3d position) {
		for (ParticleDefinition3d node : this.particles) {
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
	public ConstrainDefinition3d searchBeam(Vec3d position1, Vec3d position2) {
		ParticleDefinition3d node1 = searchNode(position1);
		if (node1 == null) return null;
		ParticleDefinition3d node2 = searchNode(position2);
		if (node2 == null) return null;
		for (ConstrainDefinition3d beam : this.constrains) {
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
	public ConstrainDefinition3d searchBeam(float x1, float y1, float z1, float x2, float y2, float z2) {
		ParticleDefinition3d node1 = searchNode(x1, y1, z1);
		if (node1 == null) return null;
		ParticleDefinition3d node2 = searchNode(x2, y2, z1);
		if (node2 == null) return null;
		for (ConstrainDefinition3d beam : this.constrains) {
			if ((beam.pointA.equals(node1) && beam.pointB.equals(node2)) || (beam.pointA.equals(node2) && beam.pointB.equals(node1))) return beam;
		}
		return null;
	}
	
	/** ##########################################################**/
	
	public static class CollisionPlaneDefinition3d {
		
		public ConstrainDefinition3d constrainA = new ConstrainDefinition3d(); // Only for the equals check
		public ConstrainDefinition3d constrainB = new ConstrainDefinition3d(); // Only for the equals check
		public ConstrainDefinition3d constrainC = new ConstrainDefinition3d(); // Only for the equals check

		public CollisionPlane3d lastBuild;
		
		public CollisionPlaneDefinition3d() {}
		
		public CollisionPlaneDefinition3d(ConstrainDefinition3d constrainA, ConstrainDefinition3d constrainB, ConstrainDefinition3d constrainC) {
			this.constrainA = constrainA;
			this.constrainB = constrainB;
			this.constrainC = constrainC;
		}

		/**
		 * Like the method of the CollisionPlane, changes the material-property of the plane definition and its constrain definitions
		 * @param material The new material-info
		 */
		public void changeMaterial(Material material) {
			this.constrainA.changeMaterial(material);
			this.constrainB.changeMaterial(material);
			this.constrainC.changeMaterial(material);
		}

		/**
		 * Creates a new instance of CollisionPlane with this parameters
		 * IMPORTANT: Build the ConstrainDefinitions first!
		 * @return A new CollisionPlane with the parameters of this definition
		 * @throws RuntimeException of a IllegalStateException when the used ConstrainDefinitions are not build
		 */
		public CollisionPlane3d build() {
			if (this.constrainA.lastBuild == null || this.constrainB.lastBuild == null || this.constrainC.lastBuild == null) throw new RuntimeException(new IllegalStateException("Cant build CollisionPlane before the Constrains are build!"));
			this.lastBuild = new CollisionPlane3d(this);
			return this.lastBuild;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof CollisionPlaneDefinition3d) {
				return	((CollisionPlaneDefinition3d) obj).constrainA.equals(this.constrainA) &&
						((CollisionPlaneDefinition3d) obj).constrainB.equals(this.constrainB) &&
						((CollisionPlaneDefinition3d) obj).constrainC.equals(this.constrainC);
			}
			return false;
		}
		
	}
	
	public static class ConstrainDefinition3d {
		
		public ParticleDefinition3d pointA = new ParticleDefinition3d(); // Only for the equals check
		public ParticleDefinition3d pointB = new ParticleDefinition3d(); // Only for the equals check
		public float stiffness;
		public float deformForce;
		public float maxBending;
		
		public Constrain3d lastBuild;
		
		public ConstrainDefinition3d() {}
		
		public ConstrainDefinition3d(ParticleDefinition3d pointA, ParticleDefinition3d pointB) {
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
		public Constrain3d build() {
			if (this.pointA.lastBuild == null || this.pointB.lastBuild == null) throw new RuntimeException(new IllegalStateException("Cant build Constrain bevore the Particles are not build!"));
			this.lastBuild = new Constrain3d(this);
			return this.lastBuild;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof ConstrainDefinition3d) {
				return	((ConstrainDefinition3d) obj).pointA.equals(this.pointA) &&
						((ConstrainDefinition3d) obj).pointB.equals(this.pointB) &&
						((ConstrainDefinition3d) obj).stiffness == this.stiffness &&
						((ConstrainDefinition3d) obj).deformForce == this.deformForce &&
						((ConstrainDefinition3d) obj).maxBending == this.maxBending;
			}
			return false;
		}
		
	}
	
	public static class ParticleDefinition3d {
		
		public Vec3d pos = new Vec3d();
		public float mass;
		
		public Particle3d lastBuild;
		
		public ParticleDefinition3d() {}
		
		public ParticleDefinition3d(Vec3d pos) {
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
		public Particle3d build() {
			this.lastBuild = new Particle3d(this);
			return this.lastBuild;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof ParticleDefinition3d) {
				return	((ParticleDefinition3d) obj).pos.equals(this.pos) &&
						((ParticleDefinition3d) obj).mass == this.mass;
			}
			return false;
		}
		
	}

	public List<ConstrainDefinition3d> getConstrains() {
		return constrains;
	}

	public List<ParticleDefinition3d> getParticles() {
		return particles;
	}
	
}
