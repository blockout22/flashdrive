package com.texture;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;

public class Texture {
	private int ID;
	private int width;
	private int height;
	private ByteBuffer buffer;

	public Texture(int iD, int width, int height, ByteBuffer buffer) {
		ID = iD;
		this.width = width;
		this.height = height;
		this.buffer = buffer;
	}

	public int getID() {
		return ID;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public ByteBuffer getBuffer() {
		return buffer;
	}

	public void cleanup() {
		GL11.glDeleteTextures(ID);
	}
}
