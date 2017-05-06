package com.window;

import javax.vecmath.Vector3f;

import com.math.Matrix4;
import com.math.Time;

public abstract class Camera {

	public float y_height = 0;
	private Vector3f position = new Vector3f(0, 1f, 0);
	
	protected float pitch = 0;
	protected float yaw = 0;
	protected float roll = 0;

	private float FOV;
	private float z_near;
	private float z_far;

	private Matrix4 projectionMatrix;

	public float SPEED = 0.0081f;

	public Camera(float fov, float z_near, float z_far) {
		this.FOV = fov;
		this.z_near = z_near;
		this.z_far = z_far;
		
		createProjectionMatrix(Window.getWidth(), Window.getHeight());
	}
	
	public abstract void update();

	public boolean isInBounds(float x, float y, float z) {
		if (x - position.x < z_far && x - position.x > -z_far) {
			if (y - position.y < z_far && y - position.y > -z_far - 10) {
				if (z - position.z < z_far && z - position.z > -z_far) {
					return true;
				}
			}
		}

		return false;
	}

	protected void createProjectionMatrix(int width, int height) {
		// width / height
		float aspectRatio = (float) width / height;
		float y_scale = 1f / (float) Math.tan(Math.toRadians(FOV / 2f)) * aspectRatio;
		float x_scale = y_scale / aspectRatio;
		float frustum_length = z_far - z_near;

		projectionMatrix = new Matrix4();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((z_far + z_near) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * z_near * z_far) / frustum_length);
		projectionMatrix.m33 = 0;
	}

	public void moveForward() {
//		getPosition().x += Math.sin(getYaw() * Math.PI / 180) * SPEED * Time.getDelta();
//		getPosition().z += -Math.cos(getYaw() * Math.PI / 180) * SPEED * Time.getDelta();
		
		getPosition().y += -Math.sin(Math.toRadians(getPitch())) * SPEED * Time.getDelta();
		getPosition().x += Math.sin(Math.toRadians(getYaw())) * Math.cos(Math.toRadians(getPitch())) * SPEED * Time.getDelta();
		getPosition().z += -Math.cos(Math.toRadians(getYaw())) * Math.cos(Math.toRadians(getPitch())) * SPEED * Time.getDelta();
	}

	public void moveBack() {
		getPosition().x -= Math.sin(getYaw() * Math.PI / 180) * SPEED * Time.getDelta();
		getPosition().z -= -Math.cos(getYaw() * Math.PI / 180) * SPEED * Time.getDelta();
	}

	public void moveLeft() {
		getPosition().x += Math.sin((getYaw() - 90) * Math.PI / 180) * SPEED * Time.getDelta();
		getPosition().z += -Math.cos((getYaw() - 90) * Math.PI / 180) * SPEED * Time.getDelta();
	}

	public void moveRight() {
		getPosition().x += Math.sin((getYaw() + 90) * Math.PI / 180) * SPEED * Time.getDelta();
		getPosition().z += -Math.cos((getYaw() + 90) * Math.PI / 180) * SPEED * Time.getDelta();
	}

	public Vector3f rayForward(float r) {
		
		
		Vector3f dir = directionForward();
		dir.scale(r);
		return dir;
	}
	
	public Vector3f directionForward()
	{
		float y = (float) -Math.sin(Math.toRadians(getPitch()));
		float x = (float) (Math.sin(Math.toRadians(getYaw())) * Math.cos(Math.toRadians(getPitch())));
		float z = (float) (-Math.cos(Math.toRadians(getYaw())) * Math.cos(Math.toRadians(getPitch())));
		
		
		Vector3f dir = new Vector3f(x, y, z);
		return dir;
	}

	public void moveX(float amt) {
		this.getPosition().x += amt;
	}

	public void moveY(float amt) {
		this.getPosition().y += amt;
	}

	public void moveZ(float amt) {
		this.getPosition().z += amt;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getRoll() {
		return roll;
	}

	public void setRoll(float roll) {
		this.roll = roll;
	}

	public float getFOV() {
		return FOV;
	}

	public void setFOV(float fOV) {
		FOV = fOV;
	}

	public float getZFar() {
		return z_far;
	}

	public Matrix4 getProjectionMatrix() {
		return projectionMatrix;
	}

	public void setProjectionMatrix(Matrix4 projectionMatrix) {
		this.projectionMatrix = projectionMatrix;
	}
}
