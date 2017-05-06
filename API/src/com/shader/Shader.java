package com.shader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.vecmath.Vector3f;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import com.math.Matrix4;

public abstract class Shader {
	
	private int program;
	private int vertex;
	private int fragment;
	
	private FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(4 * 4);
	private static ArrayList<String> uniformsDetection = new ArrayList<String>();
	
	private boolean shouldValidateUniform = false;
	
	public Shader(String vertexShader, String fragmentShader){
		vertex = loadShader(vertexShader, GL20.GL_VERTEX_SHADER);
		fragment = loadShader(fragmentShader, GL20.GL_FRAGMENT_SHADER);
		
		createProgram();
		validateAllUniforms();
	}
	
	public void validateAllUniforms() {
		shouldValidateUniform = true;
	}
	
	private void createProgram()
	{
		program = GL20.glCreateProgram();
		GL20.glAttachShader(program, vertex);
		GL20.glAttachShader(program, fragment);
	}
	
	public void bindAttribLocation(int index, String name){
		GL20.glBindAttribLocation(program, index, name);
	}
	
	public void linkAndValidate()
	{
		GL20.glLinkProgram(program);
		GL20.glValidateProgram(program);
	}
	
	public void bind()
	{
		GL20.glUseProgram(program);
	}
	
	public void unbind()
	{
		GL20.glUseProgram(0);
	}
	
	public void loadMatrix(int location, Matrix4 matrix){
		matrix.store(matrixBuffer);
		matrixBuffer.flip();
		GL20.glUniformMatrix4fv(location, false, matrixBuffer);
	}
	
	public void loadvector3f(int location, Vector3f vector3f) {
		GL20.glUniform3f(location, vector3f.x, vector3f.y, vector3f.z);
	}

	public void loadFloat(int location, float value) {
		GL20.glUniform1f(location, value);
	}
	
	public static int loadShader(String fileName, int type) {
		StringBuilder sb = new StringBuilder();

		try {
			InputStream in = Shader.class.getResourceAsStream("/" + fileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			String line;
			String previousString = "";
			String lastString = "";
			while ((line = br.readLine()) != null) {
				for (String s : line.split("\\s+")) {
					for (String s2 : s.split(";")) {
						if (lastString.equals("uniform")) {
							String uniform = s2.replace(" ", "").replace(";", "");
							uniformsDetection.add(uniform);
						}

						lastString = previousString;
						previousString = s2;
					}
				}
				sb.append(line).append("\n");
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// System.out.println("Shader: " + type);
		// System.out.println(sb);
		int shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, sb);
		GL20.glCompileShader(shaderID);

		if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			System.err.println(GL20.glGetShaderInfoLog(shaderID));
			String error = "Shader.class\n" + GL20.glGetShaderInfoLog(shaderID);
			System.err.println(error);
			// System.exit(-1);
		}

		return shaderID;
	}
	
	public int getUniformLocation(String uniform) {
		if (shouldValidateUniform) {
			for (String s : uniformsDetection) {
				if (s.equals(uniform)) {
					return GL20.glGetUniformLocation(program, uniform);
				}
			}

			System.err.println("Uniform not found: " + uniform);
		}
		return GL20.glGetUniformLocation(program, uniform);
	}
	
	public void cleanup() {
		unbind();
		GL20.glDetachShader(program, vertex);
		GL20.glDetachShader(program, fragment);
		GL20.glDeleteShader(vertex);
		GL20.glDeleteShader(fragment);
		GL20.glDeleteProgram(program);
	}

}
