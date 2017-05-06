package com.nanovg.gui;

public class ProgressBar extends Node {

	private float perc = 0f;
	private float maxPerc = 100f;

	public ProgressBar() {
		this(0, 0, 200, 25);
	}

	public ProgressBar(float x, float y, float w, float h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	public void update() {
		if(!isVisible()){
			return;
		}
		interaction(x, y, w, h);

		beginPath();
		roundedRect(x, y, w, h, 5f);
		fillColor(1, 1, 1);
		fill();

		float size = Math.min(Math.max(perc / maxPerc, 0), 1);
		float scale = 2f;

		beginPath();
		roundedRect(x + scale, y + scale / 2, size * (w - scale * 2), h - scale, 5f);
		fillColor(1, 0, 0, 0.5f);
		fill();
	}

	public void setPercentage(float perc) {
		if (perc > maxPerc) {
			this.perc = 100f;
			return;
		}
		this.perc = perc;
	}

	public float getPercentage() {
		return perc;
	}
}
