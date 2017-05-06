package com.test;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4d;

import com.window.Window;

public class RayCast {
	
	private static Vector2f cursorPos = new Vector2f();
	private static Vector3f ray_nds = new Vector3f();
	private static Vector4d ray_clip = new Vector4d();
//	private static Vector4d ray_eye = new Vector4d();
	
	public static void cursorPos()
	{
		cursorPos.x = (float) Window.getCursorXpos();
		cursorPos.y = (float) Window.getCursorYpos();
	}
	
	public static void normalised_Device_Coords()
	{
		float x = (2.0f * cursorPos.x) / Window.getWidth() - 1.0f;
		float y = 1.0f - (2.0f * cursorPos.y) / Window.getHeight();
		float z = 1.0f;
		
		ray_nds.x = x;
		ray_nds.y = y;
		ray_nds.z = z;
	}
	
	public static void homogeneous_Clip_Coords()
	{
		ray_clip.x = ray_nds.x;
		ray_clip.y = ray_nds.y;
		ray_clip.z = -1.0;
		ray_clip.w = 1.0;
	}
	
	public static void eye_Camera_Coords(){
//		Matrix4f matrix = new Matrix4f();
//		ray_eye = matrix.determinant()
	}

}
