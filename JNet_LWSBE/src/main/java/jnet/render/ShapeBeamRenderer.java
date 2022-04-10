package jnet.render;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import jnet.d2.physic.PhysicWorld2d;
import jnet.d2.physic.SoftBody2d;
import jnet.d3.physic.PhysicWorld3d;
import jnet.d3.physic.SoftBody3d;
import jnet.util.Vec2d;
import jnet.util.Vec3d;

/**
 * <strong>CALL ONLY IF LWJGL OPENGL AND GLFW IS INSTALLED</strong>
 * The ShapeBeamRenderer is a class that can draw Constrains Joints and Particles on the screen using LWJGL 3.
 * It must not be used and is only for debuging.
 * @author M_Marvin
 *
 */
public class ShapeBeamRenderer {
	
	protected Color stripColor;
	protected Color pointColor;
	protected float pointSize;
	protected float stripWidth;
	
	protected HashMap<SoftBody3d, Mesh[]> shapeMeshCache;
	
	public ShapeBeamRenderer(Color stripColor, Color pointColor, float pointSize, float stripWidth) {
		this.pointColor = pointColor;
		this.stripColor = stripColor;
		this.pointSize = pointSize;
		this.stripWidth = stripWidth;
		this.shapeMeshCache = new HashMap<SoftBody3d, Mesh[]>();
	}
	
	protected Mesh[] makeShapeMesh(SoftBody3d shape) {

		List<Vec3d> renderedPoints = new ArrayList<Vec3d>();
		
		FloatBuffer stripBuffer = BufferUtils.createFloatBuffer(shape.getConstrains().size() * 6);
		
		shape.getConstrains().forEach((strip) -> {
						
			Vec3d stripStart = strip.pointA.pos;
			Vec3d stripEnd = strip.pointB.pos;
			
			if (!strip.broken) {
				stripBuffer.put((float) stripStart.x);
				stripBuffer.put((float) stripStart.y);
				stripBuffer.put((float) stripStart.z);
				stripBuffer.put((float) stripEnd.x);
				stripBuffer.put((float) stripEnd.y);
				stripBuffer.put((float) stripEnd.z);
			}
			
			if (!renderedPoints.contains(stripEnd)) renderedPoints.add(stripEnd);
			if (!renderedPoints.contains(stripStart)) renderedPoints.add(stripStart);
			
		});
		
		FloatBuffer pointBuffer = BufferUtils.createFloatBuffer(renderedPoints.size() * 3);
		
		renderedPoints.forEach((point) -> {
			
			pointBuffer.put((float) point.x);
			pointBuffer.put((float) point.y);
			pointBuffer.put((float) point.z);
			
		});
		
		pointBuffer.flip();
		stripBuffer.flip();
		Mesh pointMesh = new Mesh();
		pointMesh.uploadVertecies(pointBuffer);
		Mesh stripMesh = new Mesh();
		stripMesh.uploadVertecies(stripBuffer);
		
		Mesh[] meshes = new Mesh[] {stripMesh, pointMesh};
		
		return meshes;
		
	}

	protected Mesh[] makeShapeMesh(SoftBody2d shape) {

		List<Vec2d> renderedPoints = new ArrayList<Vec2d>();
		
		FloatBuffer stripBuffer = BufferUtils.createFloatBuffer(shape.getConstrains().size() * 6);
		
		shape.getConstrains().forEach((strip) -> {
						
			Vec2d stripStart = strip.pointA.pos;
			Vec2d stripEnd = strip.pointB.pos;
			
			if (!strip.broken) {
				stripBuffer.put((float) stripStart.x);
				stripBuffer.put((float) stripStart.y);
				stripBuffer.put(0);
				stripBuffer.put((float) stripEnd.x);
				stripBuffer.put((float) stripEnd.y);
				stripBuffer.put(0);
			}
			
			if (!renderedPoints.contains(stripEnd)) renderedPoints.add(stripEnd);
			if (!renderedPoints.contains(stripStart)) renderedPoints.add(stripStart);
			
		});
		
		FloatBuffer pointBuffer = BufferUtils.createFloatBuffer(renderedPoints.size() * 3);
		
		renderedPoints.forEach((point) -> {
			
			pointBuffer.put((float) point.x);
			pointBuffer.put((float) point.y);
			pointBuffer.put(0);
			
		});
		
		pointBuffer.flip();
		stripBuffer.flip();
		Mesh pointMesh = new Mesh();
		pointMesh.uploadVertecies(pointBuffer);
		Mesh stripMesh = new Mesh();
		stripMesh.uploadVertecies(stripBuffer);
		
		Mesh[] meshes = new Mesh[] {stripMesh, pointMesh};
		
		return meshes;
		
	}
	
	protected Mesh makeJointMesh(PhysicWorld3d world) {

		List<Vec3d> renderedPoints = new ArrayList<Vec3d>();
		
		FloatBuffer stripBuffer = BufferUtils.createFloatBuffer(world.getJoints().size() * 6);
		
		world.getJoints().forEach((strip) -> {
						
			Vec3d stripStart = strip.pointA.pos;
			Vec3d stripEnd = strip.pointB.pos;
			
			if (!strip.broken) {
				stripBuffer.put((float) stripStart.x);
				stripBuffer.put((float) stripStart.y);
				stripBuffer.put((float) stripStart.z);
				stripBuffer.put((float) stripEnd.x);
				stripBuffer.put((float) stripEnd.y);
				stripBuffer.put((float) stripEnd.z);
			}
			
			if (!renderedPoints.contains(stripEnd)) renderedPoints.add(stripEnd);
			if (!renderedPoints.contains(stripStart)) renderedPoints.add(stripStart);
			
		});
		
		stripBuffer.flip();
		Mesh stripMesh = new Mesh();
		stripMesh.uploadVertecies(stripBuffer);
		
		return stripMesh;
		
	}

	protected Mesh makeJointMesh(PhysicWorld2d world) {

		List<Vec2d> renderedPoints = new ArrayList<Vec2d>();
		
		FloatBuffer stripBuffer = BufferUtils.createFloatBuffer(world.getJoints().size() * 6);
		
		world.getJoints().forEach((strip) -> {
						
			Vec2d stripStart = strip.pointA.pos;
			Vec2d stripEnd = strip.pointB.pos;
			
			if (!strip.broken) {
				stripBuffer.put((float) stripStart.x);
				stripBuffer.put((float) stripStart.y);
				stripBuffer.put(0);
				stripBuffer.put((float) stripEnd.x);
				stripBuffer.put((float) stripEnd.y);
				stripBuffer.put(0);
			}
			
			if (!renderedPoints.contains(stripEnd)) renderedPoints.add(stripEnd);
			if (!renderedPoints.contains(stripStart)) renderedPoints.add(stripStart);
			
		});
		
		stripBuffer.flip();
		Mesh stripMesh = new Mesh();
		stripMesh.uploadVertecies(stripBuffer);
		
		return stripMesh;
		
	}
	
	/**
	 * Renders the Particles (Nodes) and Constrains of a SoftBody
	 * @param shape The SoftBody to render
	 */
	public void drawShape(SoftBody3d shape) {
		
		Mesh[] meshes = makeShapeMesh(shape);
		
		GL11.glEnable(GL11.GL_LINE_WIDTH);
		GL11.glLineWidth(this.stripWidth);
		GL11.glEnable(GL11.GL_POINT_SIZE);
		GL11.glPointSize(this.pointSize);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		GL11.glPushMatrix();
		
		GL11.glColor4f(this.stripColor.getRed() / 255F, this.stripColor.getGreen() / 255F, this.stripColor.getBlue() / 255F, this.stripColor.getAlpha() / 255F);
		meshes[0].draw(GL11.GL_LINES);
		
		GL11.glColor4f(this.pointColor.getRed() / 255F, this.pointColor.getGreen() / 255F, this.pointColor.getBlue() / 255F, this.pointColor.getAlpha() / 255F);
		meshes[1].draw(GL11.GL_POINTS);
		
		GL11.glPopMatrix();
		
	}

	/**
	 * Renders the Particles (Nodes) and Constrains of a SoftBody
	 * @param shape The SoftBody to render
	 */
	public void drawShape(SoftBody2d shape) {
		
		Mesh[] meshes = makeShapeMesh(shape);
		
		GL11.glEnable(GL11.GL_LINE_WIDTH);
		GL11.glLineWidth(this.stripWidth);
		GL11.glEnable(GL11.GL_POINT_SIZE);
		GL11.glPointSize(this.pointSize);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		GL11.glPushMatrix();
		
		GL11.glColor4f(this.stripColor.getRed() / 255F, this.stripColor.getGreen() / 255F, this.stripColor.getBlue() / 255F, this.stripColor.getAlpha() / 255F);
		meshes[0].draw(GL11.GL_LINES);
		
		GL11.glColor4f(this.pointColor.getRed() / 255F, this.pointColor.getGreen() / 255F, this.pointColor.getBlue() / 255F, this.pointColor.getAlpha() / 255F);
		meshes[1].draw(GL11.GL_POINTS);
		
		GL11.glPopMatrix();
		
	}
	
	/**
	 * Renders all the Joints in a World
	 * @param shape The World to render its Joints
	 */
	public void drawJoints(PhysicWorld3d world) {

		Mesh mesh = makeJointMesh(world);
		
		GL11.glEnable(GL11.GL_LINE_WIDTH);
		GL11.glLineWidth(this.stripWidth);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		GL11.glPushMatrix();
		
		GL11.glColor4f(this.stripColor.getRed() / 255F, this.stripColor.getGreen() / 255F, this.stripColor.getBlue() / 255F, this.stripColor.getAlpha() / 255F);
		mesh.draw(GL11.GL_LINES);
		
		GL11.glPopMatrix();
		
	}

	/**
	 * Renders all the Joints in a World
	 * @param shape The World to render its Joints
	 */
	public void drawJoints(PhysicWorld2d world) {

		Mesh mesh = makeJointMesh(world);
		
		GL11.glEnable(GL11.GL_LINE_WIDTH);
		GL11.glLineWidth(this.stripWidth);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		GL11.glPushMatrix();
		
		GL11.glColor4f(this.stripColor.getRed() / 255F, this.stripColor.getGreen() / 255F, this.stripColor.getBlue() / 255F, this.stripColor.getAlpha() / 255F);
		mesh.draw(GL11.GL_LINES);
		
		GL11.glPopMatrix();
		
	}
	
	/** Only for storing data of the ShapeBeamRenderer **/
	public class Mesh {
		
		private int vertexArrayObject;
		private int vertexBufferObject;
		private int vertexCount;
		
		public Mesh() {
			vertexArrayObject = glGenVertexArrays();
		}
		
		public void uploadVertecies(float[] vertecies) {
			glBindVertexArray(vertexArrayObject);
			vertexBufferObject = glGenBuffers();
			glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObject);
			glBufferData(GL_ARRAY_BUFFER, vertecies, GL_STATIC_DRAW);
			glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
			glBindVertexArray(0);
			vertexCount = vertecies.length / 3;
			
		}

		public void uploadVertecies(FloatBuffer vertecies) {
			glBindVertexArray(vertexArrayObject);
			vertexBufferObject = glGenBuffers();
			glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObject);
			glBufferData(GL_ARRAY_BUFFER, vertecies, GL_STATIC_DRAW);
			glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
			glBindVertexArray(0);
			vertexCount = vertecies.capacity() / 3;
		}
		
		public void destroy() {
			glDeleteBuffers(vertexBufferObject);
			glDeleteVertexArrays(vertexArrayObject);
		}
		
		public void draw(int mode) {
			glBindVertexArray(vertexArrayObject);
			glEnableVertexAttribArray(0);
			glDrawArrays(mode, 0, vertexCount);
			glDisableVertexAttribArray(0);
			glBindVertexArray(0);
		}
		
	}
	
}
