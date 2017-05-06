package com.nanovg.gui;

import com.input.Input;
import com.input.Key;

public class TextField extends Node{
	
	private String text = "";
	private float fontSize = 20f;
	private boolean focused = false;
	
	public TextField() {
		this("test", 10, 10, 200, 75);
	}
	
	public TextField(String text, int x, int y, int w, int h) {
		this.text = text;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	@Override
	public void update() {
		interaction(x, y, w, h);
		if(isMouseClicked()){
			focused = true;
		}
		
//		if(Input.isKeyPressed(Key.KEY_ESCAPE)){
//			focused = false;
//		}
		
//		if(focused){
			rect(x, y, w, h);
			strokeColor(0, 0, 1, 1);
			stroke();
//		}
	}

}
