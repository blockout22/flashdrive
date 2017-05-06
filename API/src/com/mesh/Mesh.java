package com.mesh;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.vecmath.Vector3f;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import com.bulletphysics.util.ObjectArrayList;
import com.math.Matrix4;
import com.shader.Shader;
import com.window.Camera;

public class Mesh {

	private int vao;
	private int vbo;
	private int vboTexture;
	private int vboi;

	private int indicesSize;
	private boolean isOBJ = false;

	private ObjectArrayList<Vector3f> points = new ObjectArrayList<Vector3f>();

	private FloatBuffer verticesBuffer;

	public Mesh() {
		vao = GL30.glGenVertexArrays();
		vbo = GL15.glGenBuffers();
		vboTexture = GL15.glGenBuffers();
		vboi = GL15.glGenBuffers();
	}

	public void add(float[] vertices, float[] texCoords, int[] indices) {
		if (isOBJ) {
			System.out.println("Something tried overriding .OBJ coords");
			return;
		}
		indicesSize = indices.length;
		GL30.glBindVertexArray(vao);

		verticesBuffer = flip(vertices);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboTexture);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, flip(texCoords), GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		GL30.glBindVertexArray(0);

		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboi);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, flip(indices), GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

		// setBox(vertices);

	}

	public void setPointsArray(ObjectArrayList<Vector3f> points) {
		this.points = points;
	}

	public ObjectArrayList<Vector3f> getPoints() {
		return points;
	}

	public void setOBJ() {
		isOBJ = true;
	}

	public void enable() {
		GL30.glBindVertexArray(vao);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboi);

	}

	public void render(Shader shader, int modelMatrix, MeshObject object, Camera camera) {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, object.getTextureID());
		if (camera.isInBounds(object.getPosition().x, object.getPosition().y, object.getPosition().z)) {
			Matrix4 transformationMatrix = createTransformationMatrix(object.getPosition(), object.getRotation(), object.getScale());
			shader.loadMatrix(modelMatrix, transformationMatrix);

			if (object.getMoveListener() != null) {
				Vector3f curPos = object.getPosition();
				Vector3f prevPos = object.getPreviousPos();

				if (prevPos.x != curPos.x || prevPos.y != curPos.y || prevPos.z != curPos.z) {
					object.getMoveListener().actionPerformed(curPos);
					object.setPrevPosToCurrent();
				}
			}

			object.update();
			GL11.glDrawElements(GL11.GL_TRIANGLES, indicesSize, GL11.GL_UNSIGNED_INT, 0);
		}
	}

	public void render(Shader shader, int modelMatrix, MeshObject object, Camera camera, float[] m) {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, object.getTextureID());
		if (camera.isInBounds(object.getPosition().x, object.getPosition().y, object.getPosition().z)) {
			Matrix4 transformationMatrix = createTransformationMatrix(object.getPosition(), object.getRotation(), object.getScale());
			updateMatrix(transformationMatrix, m);
			shader.loadMatrix(modelMatrix, transformationMatrix);

			if (object.getMoveListener() != null) {
				Vector3f curPos = object.getPosition();
				Vector3f prevPos = object.getPreviousPos();

				if (prevPos.x != curPos.x || prevPos.y != curPos.y || prevPos.z != curPos.z) {
					object.getMoveListener().actionPerformed(curPos);
					object.setPrevPosToCurrent();
				}
			}

			object.update();

			GL11.glDrawElements(GL11.GL_TRIANGLES, indicesSize, GL11.GL_UNSIGNED_INT, 0);
		}
	}

	private void updateMatrix(Matrix4 matrix, float[] m) {
		matrix.set(m);
		// matrix.m00 = m[0];
		// matrix.m01 = m[1];
		// matrix.m02 = m[2];
		// matrix.m03 = m[3];
		//
		// matrix.m10 = m[4];
		// matrix.m11 = m[5];
		// matrix.m12 = m[6];
		// matrix.m13 = m[7];
		//
		// matrix.m20 = m[8];
		// matrix.m21 = m[9];
		// matrix.m22 = m[10];
		// matrix.m23 = m[11];
		//
		// matrix.m30 = m[12];
		// matrix.m31 = m[13];
		// matrix.m32 = m[14];
		// matrix.m33 = m[15];
	}

	private Matrix4 createTransformationMatrix(Vector3f translation, Vector3f rotation, Vector3f scale) {
		Matrix4 matrix = new Matrix4();
		matrix.setIdentity();
		Matrix4.translate(translation, matrix, matrix);
		Matrix4.rotate((float) Math.toRadians(rotation.x), new Vector3f(1, 0, 0), matrix, matrix);
		Matrix4.rotate((float) Math.toRadians(rotation.y), new Vector3f(0, 1, 0), matrix, matrix);
		Matrix4.rotate((float) Math.toRadians(rotation.z), new Vector3f(0, 0, 1), matrix, matrix);
		// matrix.initRotation(rotation.x, rotation.y, rotation.z);
		Matrix4.scale(new Vector3f(scale.x, scale.y, scale.z), matrix, matrix);

		return matrix;
	}

	public void disable() {

		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
	}

	public void setupLine() {
		float[] vertices = {
				//
				-1, -1, -1, //
				1, 1, 1, //
		};

		float[] texCoords = {
				//
				0, 0, //
				0, 1, //
				1, 1, //
				1, 0, //
		};

		int[] indices = {
				//
				0, 1 //
		};

		add(vertices, texCoords, indices);
		lineWidthRange.put(5);
		lineWidthRange.flip();
	}

	FloatBuffer lineWidthRange = BufferUtils.createFloatBuffer(1);

	public void drawLine(Shader shader, int modelMatrix, MeshObject object, Vector3f start, Vector3f end) {
		Matrix4 transformationMatrix = createTransformationMatrix(object.getPosition(), object.getRotation(), object.getScale());
		// updateMatrix(transformationMatrix, m);
		shader.loadMatrix(modelMatrix, transformationMatrix);
		object.update();
		// GL11.glGetFloatv(GL12.GL_ALIASED_LINE_WIDTH_RANGE, lineWidthRange );
		Vector3f[] vertices = new Vector3f[2];
		vertices[0] = start;
		vertices[1] = end;
		updateVBOData(vertices);
		GL11.glDrawElements(GL11.GL_LINE_STRIP, indicesSize, GL11.GL_UNSIGNED_INT, 0);
	}

	public void updateVBOData(Vector3f[] vertices) {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, updateFlippedBuffer(verticesBuffer, vertices)); // replace
																										// data
																										// in
																										// VBO
																										// with
																										// new
																										// data
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	public static FloatBuffer updateFlippedBuffer(FloatBuffer buffer, Vector3f[] vectors) {
		buffer.clear();
		for (int i = 0; i < vectors.length; i++) {
			buffer.put(vectors[i].x);
			buffer.put(vectors[i].y);
			buffer.put(vectors[i].z);
		}
		buffer.flip();
		return buffer;
	}

	public void cleanup() {
		GL30.glBindVertexArray(0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL15.glDeleteBuffers(vbo);
		GL15.glDeleteBuffers(vboTexture);
		GL15.glDeleteBuffers(vboi);
		GL30.glDeleteVertexArrays(vao);
	}

	private static IntBuffer flip(int[] data) {
		IntBuffer buffer;
		try (MemoryStack stack = MemoryStack.stackPush()) {
			// IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
			// IntBuffer buffer = MemoryUtil.memAllocInt(data.length);
			buffer = stack.mallocInt(data.length);
			buffer.put(data);
			buffer.flip();
		}

		return buffer;
	}

	private static FloatBuffer flip(float[] data) {
		FloatBuffer buffer;
		try (MemoryStack stack = MemoryStack.stackPush()) {
			// FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
			// FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length);
			buffer = stack.mallocFloat(data.length);
			buffer.put(data);
			buffer.flip();
		}

		return buffer;
	}

}
