package com.shader;

import com.math.Matrix4;
import com.util.Util;
import com.window.Camera;

public class WorldShader extends Shader{
	
	private static String VERTEX_SHADER = "res/shader/vertexShader.glsl";
	private static String FRAGMENT_SHADER = "res/shader/fragmentShader.glsl";

	private int viewMatrix, modelMatrix, projectionMatrix;
	
	public WorldShader() {
		super(VERTEX_SHADER, FRAGMENT_SHADER);
		
		bindAttribLocation(0, "position");
		linkAndValidate();
		
		modelMatrix = getUniformLocation("modelMatrix");
		projectionMatrix = getUniformLocation("projectionMatrix");
		viewMatrix = getUniformLocation("viewMatrix");
	}
	
	public void loadViewMatrix(Camera camera) {
		Matrix4 matrix = Util.createViewMatrix(camera);
		loadMatrix(viewMatrix, matrix);
	}

	public int getViewMatrix() {
		return viewMatrix;
	}

	public int getModelMatrix() {
		return modelMatrix;
	}

	public int getProjectionMatrix() {
		return projectionMatrix;
	}

}
