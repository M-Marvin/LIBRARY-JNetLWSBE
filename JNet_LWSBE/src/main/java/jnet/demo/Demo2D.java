package jnet.demo;


import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

import java.awt.Color;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import jnet.JNet;
import jnet.d2.physic.PhysicSolver2d;
import jnet.d2.physic.PhysicWorld2d;
import jnet.d2.physic.SoftBody2d;
import jnet.d2.physic.SoftBody2d.Constrain2d;
import jnet.d2.shapefactory.Shape2d;
import jnet.render.ShapeBeamRenderer;
import jnet.util.Vec2d;

/**
 * <strong>CALL ONLY IF LWJGL OPENGL AND GLFW IS INSTALLED</strong>
 * Only for debugging and demo usage, a simple Window that demonstrates
 * the engine. <strong>Work in Progress</strong>
 * @author M_Marvin
 */
public class Demo2D {
	
	public static void main(String[] args) {
		new Demo2D().start();
	}
	
	protected long window;
	private static Demo2D instance;
	private static boolean running;
	
	public static Demo2D getInstance() {
		return instance;
	}
	
	public static boolean isRunning() {
		return running;
	}
	
	public void start() {
		
		instance = this;
		running = true;
		
		glfwInit();
		
		window = glfwCreateWindow(1000, 600, "Window", 0, 0);
		
		glfwMakeContextCurrent(window);
		GL.createCapabilities();
		
		glfwShowWindow(window);
		
		init();
		
		Thread physicThread = new Thread(() -> {
			while (running) {
				this.physicTick();
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Thread.currentThread().interrupt();
		}, "PhysicSolver");
		physicThread.start();
		
		while (!glfwWindowShouldClose(window)) {
			
			glfwPollEvents();
			
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			
			renderTick();
			
			glfwSwapBuffers(window);
			
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
		running = false;
		
		glfwDestroyWindow(window);
		
	}
	
	//####################################################
	
	public PhysicWorld2d world;
	public PhysicSolver2d solver;
	
	public ShapeBeamRenderer renderer;
	
	public void init() {
						
		this.renderer = JNet.setupShapeBeamRenderer(new Color(255, 255, 0, 128), new Color(0, 0, 255, 128), new Color(0, 0, 0), 20, 20);
		
		this.world = JNet.D2.setupWorld(new Vec2d());
		
		Shape2d shape2 = JNet.D2.buildShape()
				.addShapeRectangleCross(-300, -300, -200, -200)
				.addShapeRectangleCross(-300, -200, -200, -100)
				.addShapeRectangleCross(-300, -100, -200, -0)
				.addShapeRectangleCross(-200, -300, -100, -200)
				.addShapeRectangleCross(-200, -200, -100, -100)
				.addShapeRectangleCross(-200, -100, -100, -0)
				.addShapeRectangleCross(-100, -300, -0, -200)
				.addShapeRectangleCross(-100, -200, -0, -100)
				.addShapeRectangleCross(-100, -100, -0, -0)
				.build();
		shape2.changeMaterial(JNet.DEFAULT_MATERIAL_METAL);
		SoftBody2d object1 = shape2.build();
		
		this.world.addSoftBody(object1);
		
		Shape2d shape = JNet.D2.buildShape()
				.addShapeRectangle(0, 10, 120, 130)
				.addTriangle(0, 10, -120, 10, 0, 130)
				.addShapeRectangleCross(0, 130, 120, 250)
				.addTriangle(-120, 10, 0, 250, -120, 130)
				.build();
		SoftBody2d object2 = shape.build();
		this.world.addSoftBody(object2);
		
		Constrain2d joint = new Constrain2d(object1.getParticles().get(0), object2.getParticles().get(0), JNet.DEFAULT_MATERIAL_METAL);
		this.world.addJoint(joint);
		
		this.solver = JNet.D2.setupSolver(world);
		
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if (key == GLFW_KEY_RIGHT) {
				this.world.getSoftBodys().get(0).getConstrains().get(3).pointA.acceleration.x += 200F;
				
			} else if (key == GLFW_KEY_LEFT) {
				this.world.getSoftBodys().get(0).getConstrains().get(8).pointA.acceleration.x -= 200F;
				
			} else if (key == GLFW_KEY_UP) {
				this.world.getSoftBodys().get(0).getConstrains().get(26).pointA.acceleration.y += 200F;
				
			} else if (key == GLFW_KEY_DOWN) {
				this.world.getSoftBodys().get(0).getConstrains().get(9).pointA.acceleration.y -= 200F;
				
			} else if (key == GLFW.GLFW_KEY_Q) {
				if (!pressed) {
					this.run = !this.run;
					this.pressed = true;
				}
			} else if (key == GLFW.GLFW_KEY_S) {
				if (!pressed) {
					this.solver.solve(1 / 10F);
					this.pressed = true;
				}
			} else {
				this.pressed = false;
			}
		});
		
	}
	
	protected boolean pressed;
	protected boolean run;
	
	public void renderTick() {
		
		GL11.glPushMatrix();

		float scaleX = 1000;
		float scaleY = 600;
		GL11.glScalef(1 / scaleX, 1 / scaleY, 1);
		
		GL11.glPushMatrix();
		GL11.glColor4f(1, 1, 1, 1);
		
		this.world.getSoftBodys().forEach((shape) -> {

			this.renderer.drawShape(shape);
			
		});
		
		this.renderer.drawJoints(world);
		
		GL11.glPopMatrix();
		
		GL11.glPopMatrix();
		
	}
	
	public void physicTick() {
		
		if (run) this.solver.solve(1 / 10F);
		
	}
	
}
