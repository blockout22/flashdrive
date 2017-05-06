package com.input;

import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;

import com.window.Window;


public class Input {
	public static ArrayList<Key> downKeys = new ArrayList<Key>();
	public static ArrayList<Key> downMouseButtons = new ArrayList<Key>();
	
	public static void update() {
		for (int i = 0; i < downKeys.size(); i++) {
			isKeyReleased(downKeys.get(i));
		}
		
		for(int i = 0; i < downMouseButtons.size(); i++)
		{
			isMouseReleased(downMouseButtons.get(i));
		}
	}
	
	public static boolean isKeyDown(Key keyCode) {
		int key = GLFW.glfwGetKey(Window.window, keyCode.getKeyCode());
		boolean isDown;

		if (key == GLFW.GLFW_PRESS) {
			isDown = true;
		} else {
			isDown = false;
		}

		return isDown;
	}
	
	public static void setKey(int key, int scancode, int action, int mods){
		System.out.println("Key: " + key + " ScanCode: " + scancode + " action: " + action + " Mods: " + mods);
		//action 1 = pressed
		//action 2 = down
		//action = 0 released
		//mods 1 = shift
		//mods 2 = ctrl
		//mods 4 = alt
		//mods 8 = windows key
	
	}
	
	public static boolean isKeyPressed(Key keyCode) {
		if (isKeyDown(keyCode)) {
			for (int i = 0; i < downKeys.size(); i++) {
				if (downKeys.get(i).equals(keyCode)) {
					return false;
				}
			}
			downKeys.add(keyCode);
			return true;
		}

		return false;
	}
	
	/**
	 * requires isKeyPressed called before 
	 * @param window
	 * @param keyCode
	 * @return
	 */
	public static boolean isKeyReleased(Key keyCode) {
		isKeyPressed(keyCode);
		if (!isKeyDown(keyCode)) {
			for (int i = 0; i < downKeys.size(); i++) {
				if (downKeys.get(i).equals(keyCode)) {
					downKeys.remove(i);
					return true;
				}
			}
			return false;
		}
		return false;
	}
	
	public static boolean isMousePressed(Key keyCode)
	{
		if(isMouseDown(keyCode))
		{
			for(int i = 0; i < downMouseButtons.size(); i++){
				if(downMouseButtons.get(i).equals(keyCode)){
					return false;
				}
			}
			downMouseButtons.add(keyCode);
			return true;
		}
		return false;
	}
	
	public static boolean isMouseReleased(Key keyCode)
	{
		isMousePressed(keyCode);
		if(!isMouseDown( keyCode))
		{
			for(int i = 0; i < downMouseButtons.size(); i++)
			{
				if(downMouseButtons.get(i).equals(keyCode))
				{
					downMouseButtons.remove(i);
					return true;
				}
			}
			return false;
		}
		return false;
	}
	
	public static boolean isMouseDown(Key keyCode){
		int key = GLFW.glfwGetMouseButton(Window.window, keyCode.getKeyCode());
		boolean isDown;
		
		if(key == GLFW.GLFW_PRESS){
			isDown = true;
		}else{
			isDown = false;
		}
		
		return isDown;
	}
}
