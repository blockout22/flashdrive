package com.nanovg.gui;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.system.MemoryStack;

public class Button extends Node{
	
	private String text;
	private float fontSize = 20f;
	
	public Button(){
		this("Button", 10, 10, 200, 75);
	}

	public Button(String text, int x, int y, int w, int h) {
		this.text = text;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}
	
	public void setText(String text){
		this.text = text;
	}

	@Override
	public void update() {
		if(!isVisible()){
			return;
		}
		
		interaction(x, y, w, h);
		
		float cornerRadius = 4.0f;
		float tw = 0;
		float iw = 0;
		
		beginPath();
		roundedRect(x, y, w, h, cornerRadius);
		strokeColor(0, 0, 0, 48);
		stroke();
		
		beginPath();
		roundedRect(x + 1, y + 1, w - 2, h - 2, cornerRadius - 1);
		fillColor(1, 0, 0);
		fill();
		
		try(MemoryStack stack = MemoryStack.stackPush()){
			ByteBuffer textEncoded = stack.ASCII(text, false);
			fontSize(fontSize);
			
			tw = textBounds(0, 0, textEncoded, (FloatBuffer)null);


			textAlign(LEFT | MIDDLE);
			fillColor(0, 0, 0);
			text(text, x + w * 0.5f - tw * 0.5f - iw * 0.75f, y + h * 0.5f);
//			fill();
		}
	}

}
