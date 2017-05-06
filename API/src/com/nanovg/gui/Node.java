package com.nanovg.gui;

import static org.lwjgl.nanovg.NanoVG.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.lwjgl.BufferUtils;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import com.input.Input;
import com.input.Key;
import com.nanovg.NanoGui;
import com.nanovg.gui.action.ActionEvent;
import com.window.Window;

public abstract class Node {

	private boolean isVisible = true;

	protected float x;
	protected float y;
	// width
	protected float w;
	// height
	protected float h;

	private ActionEvent event = null;
	private boolean hover = false;
	private boolean mouseDown = false;
	private boolean mouseUp = false;
	private boolean mouseClicked = false;

	public int LEFT = NVG_ALIGN_LEFT;
	public int MIDDLE = NVG_ALIGN_MIDDLE;

	private NVGColor color, gradientColor1, gradientColor2;
	private NVGPaint paint, imagePaint;

	public Node() {
		color = NVGColor.create();
		gradientColor1 = NVGColor.create();
		gradientColor2 = NVGColor.create();
		paint = NVGPaint.create();
		imagePaint = NVGPaint.create();
	}

	public abstract void update();

	public boolean isHover() {
		return hover;
	}

	private void setHover(boolean hover) {
		this.hover = hover;
	}

	public boolean isMouseDown() {
		return mouseDown;
	}

	private void setMouseDown(boolean mouseDown) {
		this.mouseDown = mouseDown;
	}

	public boolean isMouseUp() {
		return mouseUp;
	}

	private void setMouseUp(boolean mouseUp) {
		this.mouseUp = mouseUp;
	}

	public boolean isMouseClicked() {
		return mouseClicked;
	}

	private void setMouseClicked(boolean mouseClicked) {
		this.mouseClicked = mouseClicked;
	}

	protected void interaction(float x, float y, float w, float h) {
		float mouseX = (float) Window.getCursorXpos();
		float mouseY = (float) Window.getCursorYpos();

		if (mouseX > x && mouseX < x + w && mouseY > y && mouseY < y + h) {
			if (event != null) {
				event.hover();
			}
			setHover(true);
			if (Input.isMouseDown(Key.MOUSE_BUTTON_LEFT)) {
				if (event != null) {
					event.mouseDown();
				}
				setMouseDown(true);
				setMouseUp(false);
			} else if (Input.isMouseReleased(Key.MOUSE_BUTTON_LEFT)) {
				if (event != null) {
					event.mouseUp();
				}
				setMouseUp(true);
				setMouseDown(false);
			}

			if (Input.isMousePressed(Key.MOUSE_BUTTON_LEFT)) {
				if (event != null) {
					event.clicked();
				}
				setMouseClicked(true);
			} else {
				setMouseClicked(false);
			}
		} else {
			setHover(false);
		}
	}

	public void setActionListener(ActionEvent event) {
		this.event = event;
	}

	public void beginPath() {
		nvgBeginPath(NanoGui.getVG());
	}

	public void fillColor(float r, float g, float b) {
		fillColor(r, g, b, 1);
	}

	public void fillColor(float r, float g, float b, float a) {
		color.r(r);
		color.g(g);
		color.b(b);
		color.a(a);
		nvgFillColor(NanoGui.getVG(), color);
	}

	public NVGColor gradientColor1(float r, float g, float b, float a) {
		gradientColor1.r(r);
		gradientColor1.g(g);
		gradientColor1.b(b);
		gradientColor1.a(a);
		return gradientColor1;
	}

	public NVGColor gradientColor2(float r, float g, float b, float a) {
		gradientColor2.r(r);
		gradientColor2.g(g);
		gradientColor2.b(b);
		gradientColor2.a(a);
		return gradientColor1;
	}

	public void strokeColor(float r, float g, float b, float a) {
		color.r(r);
		color.g(g);
		color.b(b);
		color.a(a);
		nvgStrokeColor(NanoGui.getVG(), color);
	}

	public void stroke() {
		nvgStroke(NanoGui.getVG());
	}

	public void fillPaint() {
		nvgFillPaint(NanoGui.getVG(), paint);
	}

	public void linearGradient(float sx, float sy, float ex, float ey, NVGColor color1, NVGColor color2) {
		nvgLinearGradient(NanoGui.getVG(), sx, sy, ex, ey, color1, color2, paint);
	}

	public void rect(float x, float y, float width, float height) {
		nvgRect(NanoGui.getVG(), x, y, width, height);
	}

	public void loadImage(float startX, float startY, float endX, float endY, float angle, int imageID) {
		nvgImagePattern(NanoGui.getVG(), startX, startY, endX, endY, 0.0f / 180.0f * NVG_PI, imageID, 1, imagePaint);
	}

	public void renderImage(float x, float y, float width, float height) {
		beginPath();
		roundedRect(x, y, width, height, 5);
		nvgFillPaint(NanoGui.getVG(), imagePaint);
		fill();
	}

	public void drawImage(int image, float alpha, float sx, float sy, float sw, float sh, float x, float y, float width, float height) {
		NVGPaint shadowPaint = NVGPaint.create();
		NVGPaint imgPaint = NVGPaint.create();
		NVGPaint fadePaint = NVGPaint.create();
		
		float ix, iy, iw, ih;
		float thumb = 60.0f;
		float dv = 1.0f;
		float t = 1;
		float u2 = (1 - (float) Math.cos(t * 0.2f)) * 0.5f;
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer imgw = stack.mallocInt(1), imgh = stack.mallocInt(1);

			float tx, ty, v, a;
			tx = x + 10;
			ty = y + 10;
			tx += (thumb + 10);
			ty += (thumb + 10);
			nvgImageSize(NanoGui.getVG(), image, imgw, imgh);
			if (imgw.get(0) < imgh.get(0)) {
				iw = thumb;
				ih = iw * (float) imgh.get(0) / (float) imgw.get(0);
				ix = 0;
				iy = -(ih - thumb) * 0.5f;
			} else {
				ih = thumb;
				iw = ih * (float) imgw.get(0) / (float) imgh.get(0);
				ix = -(iw - thumb) * 0.5f;
				iy = 0;
			}

			v = dv;
			a = clampf((u2 - v) / dv, 0, 1);

			nvgImagePattern(NanoGui.getVG(), tx + ix, ty + iy, iw, ih, 0.0f / 180.0f * NVG_PI, image, a, imgPaint);
			nvgBeginPath(NanoGui.getVG());
			nvgRoundedRect(NanoGui.getVG(), tx, ty, thumb, thumb, 5);
			nvgFillPaint(NanoGui.getVG(), imgPaint);
			nvgFill(NanoGui.getVG());

//			nvgBoxGradient(NanoGui.getVG(), tx - 1, ty, thumb + 2, thumb + 2, 5, 3, color, color, shadowPaint);
			nvgBeginPath(NanoGui.getVG());
			nvgRect(NanoGui.getVG(), tx - 5, ty - 5, thumb + 10, thumb + 10);
			nvgRoundedRect(NanoGui.getVG(), tx, ty, thumb, thumb, 6);
			nvgPathWinding(NanoGui.getVG(), NVG_HOLE);
			nvgFillPaint(NanoGui.getVG(), shadowPaint);
			nvgFill(NanoGui.getVG());

			nvgBeginPath(NanoGui.getVG());
			nvgRoundedRect(NanoGui.getVG(), tx + 0.5f, ty + 0.5f, thumb - 1, thumb - 1, 4 - 0.5f);
			nvgStrokeWidth(NanoGui.getVG(), 1.0f);
			nvgStrokeColor(NanoGui.getVG(), color);
			nvgStroke(NanoGui.getVG());
		}

	}
	
	private static float clampf(float a, float mn, float mx) {
		return a < mn ? mn : (a > mx ? mx : a);
	}

	public int loadImage(String imageFile) {
		ByteBuffer img = loadResource(imageFile, 32 * 1024);
		int id = nvgCreateImageMem(NanoGui.getVG(), 0, img);

		return id;
	}

	private ByteBuffer loadResource(String resource, int bufferSize) {
		try {
			return ioResourceToByteBuffer(resource, bufferSize);
		} catch (IOException e) {
			throw new RuntimeException("Failed to load resource: " + resource, e);
		}
	}

	private ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
		ByteBuffer buffer;

		Path path = Paths.get(resource);
		if (Files.isReadable(path)) {
			try (SeekableByteChannel fc = Files.newByteChannel(path)) {
				buffer = BufferUtils.createByteBuffer((int) fc.size() + 1);
				while (fc.read(buffer) != -1)
					;
			}
		} else {
			System.out.println(resource);
			try (InputStream source = Node.class.getClassLoader().getResourceAsStream(resource); ReadableByteChannel rbc = Channels.newChannel(source)) {
				buffer = BufferUtils.createByteBuffer(bufferSize);

				while (true) {
					int bytes = rbc.read(buffer);
					if (bytes == -1)
						break;
					if (buffer.remaining() == 0)
						buffer = resizeBuffer(buffer, buffer.capacity() * 2);
				}
			}
		}

		buffer.flip();
		return buffer;
	}

	private ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
		ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
		buffer.flip();
		newBuffer.put(buffer);
		return newBuffer;
	}

	public void roundedRect(float x, float y, float width, float height, float cornerRadius) {
		nvgRoundedRect(NanoGui.getVG(), x, y, width, height, cornerRadius);
	}

	public void circle(float x, float y, float radius) {
		nvgCircle(NanoGui.getVG(), x, y, radius);
	}

	public void scale(float x, float y) {
		nvgScale(NanoGui.getVG(), x, y);
	}

	public void fill() {
		nvgFill(NanoGui.getVG());

		if (NanoGui.isDebug()) {
			drawBounds();
		}
	}

	private void drawBounds() {
		beginPath();
		rect(x, y, w, h);

		strokeColor(1, 0, 0, 1);
		nvgStroke(NanoGui.getVG());
	}

	public void fontSize(float size) {
		nvgFontSize(NanoGui.getVG(), size);
		nvgFontFace(NanoGui.getVG(), NanoGui.getFontName());
	}

	public void textAlign(int alignment) {
		nvgTextAlign(NanoGui.getVG(), alignment);
	}

	public void text(String text, float x, float y) {
		nvgText(NanoGui.getVG(), x, y, text);
	}

	public void text(ByteBuffer buffer, float x, float y) {
		nvgText(NanoGui.getVG(), x, y, buffer);
	}

	public float textBounds(float x, float y, ByteBuffer string, FloatBuffer bounds) {
		return nvgTextBounds(NanoGui.getVG(), x, y, string, bounds);
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isShowing) {
		this.isVisible = isShowing;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getW() {
		return w;
	}

	public void setW(float w) {
		this.w = w;
	}

	public float getH() {
		return h;
	}

	public void setH(float h) {
		this.h = h;
	}

}
