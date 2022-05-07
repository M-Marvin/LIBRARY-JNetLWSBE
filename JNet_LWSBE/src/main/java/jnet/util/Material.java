package jnet.util;

/**
 * A class that combines all important properties of Constrains and Particles, like stiffness, deformForce, maxBending and mass.
 * @author M_Marvin
 *
 */
public class Material {
	
	public float stiffness;
	public float deformForce;
	public float maxBending;
	public float mass;
	public boolean isStatic;
	
	public Material(float stiffness, float deformForce, float maxBending, float mass, boolean isStatic) {
		this.stiffness = stiffness;
		this.deformForce = deformForce;
		this.maxBending = maxBending;
		this.mass = mass;
		this.isStatic = isStatic;
	}
	
	public Material(float stiffness, float deformForce, float maxBending, float mass) {
		this.stiffness = Math.max(0, Math.min(1, stiffness));
		this.deformForce = deformForce;
		this.maxBending = maxBending;
		this.mass = mass;
		this.isStatic = false;
	}
		
	public float getStiffness() {
		return stiffness;
	}

	public void setStiffness(float stiffness) {
		this.stiffness = stiffness;
	}

	public float getDeformForce() {
		return deformForce;
	}

	public void setDeformForce(float deformForce) {
		this.deformForce = deformForce;
	}

	public float getMaxBending() {
		return maxBending;
	}

	public void setMaxBending(float maxBending) {
		this.maxBending = maxBending;
	}

	public float getMass() {
		return Math.max(mass, 0);
	}

	public void setMass(float mass) {
		this.mass = mass;
	}
	
	public boolean isStatic() {
		return this.isStatic;
	}
	
}
