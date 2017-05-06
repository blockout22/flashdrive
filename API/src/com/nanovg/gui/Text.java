package com.nanovg.gui;

public class Text extends Node{
	
	private String text = "";
	
	private float fontSize;
	
	public Text() {
		this("", 0, 0);
	}
	
	public Text(String text, float x, float y) {
		this.text = text;
		this.x = x;
		this.y = y;
		setFontSize(20f);
	}

	@Override
	public void update() {
		if(!isVisible()){
			return;
		}
		
		interaction(x, y, w, h);
		
		
		fontSize(fontSize);
		fillColor(0, 0, 0);
		textAlign(LEFT | MIDDLE);
		text(text, x, y);
	}
	
	public void setFontSize(float size){
		this.fontSize = size;
	}
	
	public void setText(String text){
		this.text = text;
	}
	
}
