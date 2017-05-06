package com.util;

import javax.vecmath.Vector3f;

import com.math.Matrix4;
import com.window.Camera;

public class Util {
	
	public static Matrix4 createViewMatrix(Camera camera)
	{
		Matrix4 viewMatrix = new Matrix4();
		viewMatrix.setIdentity();
		Matrix4.rotate((float)Math.toRadians(camera.getPitch()), new Vector3f(1, 0, 0), viewMatrix, viewMatrix);
		Matrix4.rotate((float)Math.toRadians(camera.getYaw()), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
		Matrix4.rotate((float)Math.toRadians(camera.getRoll()), new Vector3f(0, 0, 1), viewMatrix, viewMatrix);
		Vector3f cameraPos = camera.getPosition();
		Vector3f negativeCameraPos = new Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z);
		Matrix4.translate(negativeCameraPos, viewMatrix, viewMatrix);
		
		return viewMatrix;
	}

}
