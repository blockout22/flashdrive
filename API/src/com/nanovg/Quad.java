package com.nanovg;

import com.nanovg.gui.Node;

public class Quad extends Node {

	int imgID;

	public Quad() {
		imgID = loadImage("test.jpg");
	}

	@Override
	public void update() {
		float x = 10;
		float y = 10;
		float width = 100;
		float height = 100;

		drawImage(imgID, 1, 0, 0, 512, 512, x, y, width, height);

		beginPath();
		rect(x, y, width, height);
		strokeColor(1, 1, 1, 1);
		stroke();
	}

	public void cleanup() {
	}

}
