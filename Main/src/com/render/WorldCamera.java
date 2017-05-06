package com.render;

import javax.vecmath.Vector2f;

import com.input.Input;
import com.input.Key;
import com.window.Camera;
import com.window.Window;

public class WorldCamera extends Camera{
	
	private float pitch_min = -90;
	private float pitch_max = 90;
	
	private float sensitivity = 0.07f;
	private Vector2f previousPos = new Vector2f(-1, -1);
	private Vector2f curPos = new Vector2f(0, 0);

	public WorldCamera(float fov, float z_near, float z_far) {
		super(fov, z_near, z_far);
	}

	public void update() {
		if(Input.isMousePressed(Key.MOUSE_BUTTON_LEFT)){
			if(!Window.isMouseGrabbed()){
				Window.grabCursor();
			}else{
				Window.releaseCursor();
			}
		}
		
		curPos = getCursorPos();
		if(Window.isMouseGrabbed()){
			double dx = curPos.x - previousPos.x;
			double dy = curPos.y - previousPos.y;
			yaw += dx * sensitivity;
			pitch += dy * sensitivity;
		}
		
		previousPos.x = curPos.x;
		previousPos.y = curPos.y;
		
		if(getPitch() > pitch_max){
			setPitch(pitch_max);
		}else if(getPitch() < pitch_min){
			setPitch(pitch_min);
		}
		
		if(yaw > 360){
			yaw = 0;
		}else if(yaw < 0){
			yaw = 360;
		}
	}
	
	public Vector2f getCursorPos() {
		Vector2f result = new Vector2f((float)Window.getCursorXpos(), (float)Window.getCursorYpos());
		return result;
	}
}
