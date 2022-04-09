package jnet;

import java.awt.Color;

import jnet.physic.PhysicSolver;
import jnet.physic.PhysicWorld;
import jnet.render.ShapeBeamRenderer;
import jnet.shapefactory.Material;
import jnet.shapefactory.ShapeFactory;
import jnet.util.Vec2d;

/**
 * The main class of the SoftBody PhsyicEngine.
 * This class contains helper methods for creating a setup of this engine, the methods must not be used.
 * Also this class contains the default values and settings that are used by creating SoftBodys, Constrain, Particles and the PhysicSolver.
 * @author M_Marvin
 *
 */
public class JNet {
	
	/** The default material of all created SoftBodys and Shapes **/
	public static final Material DEFAULT_MATERIAL = new Material(0.01F, 1F, 1.4F, 1F);
	/** An alternate example material that is more stable, like metal **/
	public static final Material DEFAULT_MATERIAL_METAL = new Material(0.9F, 3F, 1.1F, 2F);
	
	/** The default number of iterations used in the PhysicSolver **/
	public static final int DEFAULT_NUM_ITTERATIONS = 20;
	
	/**
	 * Creates a new PhysicSolver for the given world, with default parameters. The Solver handles the physic-simulation. This is only a helper method, the solver can also created manually.
	 * @param world The world for the new solver
	 * @return A new Solver
	 */
	public static PhysicSolver setupSolver(PhysicWorld world) {
		return new PhysicSolver(world);
	}
	
	/**
	 * Creates a new PhysicWorld to hold all SoftBodys to simulate. Every world needs a PhysicSolver to simulate. This is only a helper method, the world can also created manually.
	 * @param gravity The gravity (also called global-force) for the world, it can be changes every time.
	 * @return A new World for storing the SoftBodys
	 */
	public static PhysicWorld setupWorld(Vec2d gravity) {
		PhysicWorld world = new PhysicWorld();
		world.setGlobalForce(gravity);
		return world;
	}
	
	/**
	 * Creates a new ShapeFactory to build a new Shape (like a SoftBodyDefinition) to create new SoftBodys. This is only a helper method, the ShapeFactory can also created manually.
	 * @return A new instance of the ShapeFactory
	 */
	public static ShapeFactory buildShape() {
		return new ShapeFactory();
	}
	
	/**
	 * LWJGL3 ONLY: Creates a new ShapeBeamRenderer to visualize the simulated SoftBodys Particles (Nodes) and Constrains (Beams). This is only a helper method, the Renderer can also created manually.
	 * @param stripColor The Color for the Beams
	 * @param pointColor The Color for the Nodes
	 * @param pointSize The Size of the Nodes
	 * @param stripWidth The Size of the Beams
	 * @return A new ShapeBeamRenderer
	 */
	public static ShapeBeamRenderer setupShapeBeamRenderer(Color stripColor, Color pointColor, float pointSize, float stripWidth) {
		return new ShapeBeamRenderer(stripColor, pointColor, pointSize, stripWidth);
	}
	
}
