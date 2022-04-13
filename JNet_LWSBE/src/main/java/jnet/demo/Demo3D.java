package jnet.demo;


import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
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
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL40;

import jnet.JNet;
import jnet.d3.physic.PhysicSolver3d;
import jnet.d3.physic.PhysicWorld3d;
import jnet.d3.physic.SoftBody3d;
import jnet.d3.shapefactory.Shape3d;
import jnet.render.ShapeBeamRenderer;
import jnet.util.Vec2f;
import jnet.util.Vec3d;
import jnet.util.Vec3f;

/**
 * <strong>CALL ONLY IF LWJGL OPENGL AND GLFW IS INSTALLED</strong>
 * Only for debugging and demo usage, a simple Window that demonstrates
 * the engine. <strong>Work in Progress</strong>
 * @author M_Marvin
 */
public class Demo3D {
	
	public static void main(String[] args) {
		new Demo3D().start();
	}
	
	protected Vec2f camRot;
	protected Vec3f camPos;
	protected long window;
	private static Demo3D instance;
	private static boolean running;
	
	public static Demo3D getInstance() {
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
		
//		GL11.glMatrixMode(GL11.GL_PROJECTION);
//		GL11.glLoadIdentity();
//		gluPerspective(70, 1000 / 600, 0.1F, 100);
//        GL11.glMatrixMode(GL11.GL_MODELVIEW);
//        GL11.glLoadIdentity();
		
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
	
	public PhysicWorld3d world;
	public PhysicSolver3d solver;
	
	
	public SoftBody3d staticObject;
	public ShapeBeamRenderer renderer;
	
	public void init() {
		
		this.camPos = new Vec3f();
		this.camRot = new Vec2f();
		
		this.renderer = JNet.setupShapeBeamRenderer(new Color(255, 255, 0, 128), new Color(0, 0, 255, 128), new Color(0, 255, 0, 128), 20, 20);
		
		this.world = JNet.D3.setupWorld(new Vec3d());
		
//		Shape3d shape2 = JNet.D3.buildShape()
//				.addShapeRectangleCross(-300, -300, 0, -200, -200, 0).addCollisionTriangle(-300, -300, 0, -300, -200, 0, -200, -200, 0)
//				.addShapeRectangleCross(-300, -200, 0, -200, -100, 0)
//				.addShapeRectangleCross(-300, -100, 0, -200, -0, 0)
//				.addShapeRectangleCross(-200, -300, 0, -100, -200, 0)
//				.addShapeRectangleCross(-200, -200, 0, -100, -100, 0)
//				.addShapeRectangleCross(-200, -100, 0, -100, -0, 0)
//				.addShapeRectangleCross(-100, -300, 0, -0, -200, 0)
//				.addShapeRectangleCross(-100, -200, 0, -0, -100, 0)
//				.addShapeRectangleCross(-100, -100, 0, -0, -0, 0)
//				.build();
//		shape2.changeMaterial(JNet.DEFAULT_MATERIAL_METAL);
//		SoftBody3d object1 = shape2.build();
		
		Shape3d shape2 = JNet.D3.buildShape()
				.addTriangle(-200, -200, -200, -200, -200, 200, -250, 200, 0)
				.addCollisionTriangle(-200, -200, -200, -200, -200, 200, -250, 200, 0)
				.build();
		shape2.changeMaterial(JNet.DEFAULT_MATERIAL);
		SoftBody3d object1 = shape2.build();
		this.world.addSoftBody(object1);
		staticObject = object1;
		
		Shape3d shape = JNet.D3.buildShape()
				.addShapeRectangle(0, 10, 0, 120, 130, 0)
				.addTriangle(0, 10, 0, -120, 10, 0, 0, 130, 0)
				.addShapeRectangleCross(0, 130, 0, 120, 250, 0)
				.addTriangle(-120, 10, 0, 0, 250, 0, -120, 130, 0)
				.build();
		SoftBody3d object2 = shape.build();
		this.world.addSoftBody(object2);
		
		Shape3d shape3 = JNet.D3.buildShape()
				.addTriangle(-300, -200, 0, -200, -200, 0, -159, 200, 0)
				.addCollisionTriangle(-300, -200, 0, -200, -200, 0, -159, 200, 0)
				.build();
		SoftBody3d object3 = shape3.build();
		this.world.addSoftBody(object3);
		
//		Constrain3d joint = new Constrain3d(object1.getParticles().get(0), object2.getParticles().get(0), JNet.DEFAULT_MATERIAL_METAL);
//		this.world.addJoint(joint);
		
		this.solver = JNet.D3.setupSolver(world);
		
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if (key == GLFW_KEY_RIGHT) {
				this.world.getSoftBodys().get(1).getConstrains().get(0).pointA.acceleration.x += 200F;
				
			} else if (key == GLFW.GLFW_KEY_LEFT) {
				this.world.getSoftBodys().get(1).getConstrains().get(0).pointA.acceleration.x -= 200F;
				
			} else if (key == GLFW.GLFW_KEY_UP) {
				this.world.getSoftBodys().get(1).getConstrains().get(0).pointA.acceleration.y += 200F;
				
			} else if (key == GLFW.GLFW_KEY_DOWN) {
				this.world.getSoftBodys().get(1).getConstrains().get(0).pointA.acceleration.y -= 200F;
				
				
			} else if (key == GLFW.GLFW_KEY_W) {
				this.camPos.z -= 1;						
			} else if (key == GLFW.GLFW_KEY_A) {
				this.camPos.x += 1;						
			} else if (key == GLFW.GLFW_KEY_S) {
				this.camPos.z += 1;						
			} else if (key == GLFW.GLFW_KEY_D) {
				this.camPos.x -= 1;						
			} else if (key == GLFW.GLFW_KEY_I) {
				this.camRot.y += 1;				
			} else if (key == GLFW.GLFW_KEY_K) {
				this.camRot.y -= 1;						
			} else if (key == GLFW.GLFW_KEY_J) {
				this.camRot.x -= 1;						
			} else if (key == GLFW.GLFW_KEY_L) {
				this.camRot.x += 1;						
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
		
		//System.out.println(this.rotation);
		//GL11.glTranslated(0, 0, -300);
		GL11.glRotatef(this.camRot.x, 1, 0, 0);
		GL11.glRotatef(this.camRot.y, 0, 1, 0);
		GL11.glTranslated(this.camPos.x, this.camPos.y, this.camPos.z);
		//GL11.glTranslated(0, 0, -300);
		
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
	
	
	private static final FloatBuffer matrix = BufferUtils.createFloatBuffer(16);
	private static final float[] IDENTITY_MATRIX =
			new float[] {
				1.0f, 0.0f, 0.0f, 0.0f,
				0.0f, 1.0f, 0.0f, 0.0f,
				0.0f, 0.0f, 1.0f, 0.0f,
				0.0f, 0.0f, 0.0f, 1.0f };
	
	private static void __gluMakeIdentityf(FloatBuffer m) {
		int oldPos = m.position();
		m.put(IDENTITY_MATRIX);
		m.position(oldPos);
	}
	
	public static void gluPerspective(float fovy, float aspect, float zNear, float zFar) {
		float sine, cotangent, deltaZ;
		float radians = (float) (fovy / 2 * Math.PI / 180);

		deltaZ = zFar - zNear;
		sine = (float) Math.sin(radians);

		if ((deltaZ == 0) || (sine == 0) || (aspect == 0)) {
			return;
		}

		cotangent = (float) Math.cos(radians) / sine;

		__gluMakeIdentityf(matrix);

		matrix.put(0 * 4 + 0, cotangent / aspect);
		matrix.put(1 * 4 + 1, cotangent);
		matrix.put(2 * 4 + 2, - (zFar + zNear) / deltaZ);
		matrix.put(2 * 4 + 3, -1);
		matrix.put(3 * 4 + 2, -2 * zNear * zFar / deltaZ);
		matrix.put(3 * 4 + 3, 0);
		
		GL40.glMultMatrixf(matrix);
	}
	
}
