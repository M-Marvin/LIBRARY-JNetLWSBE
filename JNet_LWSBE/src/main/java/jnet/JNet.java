package jnet;

import java.awt.Color;

import jnet.d2.physic.PhysicSolver2d;
import jnet.d2.physic.PhysicWorld2d;
import jnet.d2.shapefactory.ShapeFactory2d;
import jnet.d3.physic.PhysicSolver3d;
import jnet.d3.physic.PhysicWorld3d;
import jnet.d3.shapefactory.ShapeFactory3d;
import jnet.render.ShapeBeamRenderer;
import jnet.util.Material;
import jnet.util.Vec2d;
import jnet.util.Vec3d;

public class JNet {

	/** The default material of all created SoftBodys and Shapes **/
	public static final Material DEFAULT_MATERIAL = new Material(0.01F, 1F, 1.4F, 1F);
	/** An alternate example material that is more stable, like metal **/
	public static final Material DEFAULT_MATERIAL_METAL = new Material(0.9F, 3F, 1.1F, 2F);
	
	/** The default number of iterations used in the PhysicSolver **/
	public static final int DEFAULT_NUM_ITTERATIONS = 20;

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
	
	public static class D2 {
		
		/**
		 * Creates a new PhysicSolver for the given world, with default parameters. The Solver handles the physic-simulation. This is only a helper method, the solver can also created manually.
		 * @param world The world for the new solver
		 * @return A new Solver
		 */
		public static PhysicSolver2d setupSolver(PhysicWorld2d world) {
			return new PhysicSolver2d(world);
		}
		
		/**
		 * Creates a new PhysicWorld to hold all SoftBodys to simulate. Every world needs a PhysicSolver to simulate. This is only a helper method, the world can also created manually.
		 * @param gravity The gravity (also called global-force) for the world, it can be changes every time.
		 * @return A new World for storing the SoftBodys
		 */
		public static PhysicWorld2d setupWorld(Vec2d gravity) {
			PhysicWorld2d world = new PhysicWorld2d();
			world.setGlobalForce(gravity);
			return world;
		}
		
		/**
		 * Creates a new ShapeFactory to build a new Shape (like a SoftBodyDefinition) to create new SoftBodys. This is only a helper method, the ShapeFactory can also created manually.
		 * @return A new instance of the ShapeFactory
		 */
		public static ShapeFactory2d buildShape() {
			return new ShapeFactory2d();
		}
		
	}

	public static class D3 {
		
		/**
		 * Creates a new PhysicSolver for the given world, with default parameters. The Solver handles the physic-simulation. This is only a helper method, the solver can also created manually.
		 * @param world The world for the new solver
		 * @return A new Solver
		 */
		public static PhysicSolver3d setupSolver(PhysicWorld3d world) {
			return new PhysicSolver3d(world);
		}
		
		/**
		 * Creates a new PhysicWorld to hold all SoftBodys to simulate. Every world needs a PhysicSolver to simulate. This is only a helper method, the world can also created manually.
		 * @param gravity The gravity (also called global-force) for the world, it can be changes every time.
		 * @return A new World for storing the SoftBodys
		 */
		public static PhysicWorld3d setupWorld(Vec3d gravity) {
			PhysicWorld3d world = new PhysicWorld3d();
			world.setGlobalForce(gravity);
			return world;
		}
		
		/**
		 * Creates a new ShapeFactory to build a new Shape (like a SoftBodyDefinition) to create new SoftBodys. This is only a helper method, the ShapeFactory can also created manually.
		 * @return A new instance of the ShapeFactory
		 */
		public static ShapeFactory3d buildShape() {
			return new ShapeFactory3d();
		}
		
	}
	
}
