package com.nanovg;

import static org.lwjgl.nanovg.NanoVG.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.lwjgl.BufferUtils;
import org.lwjgl.nanovg.NVGLUFramebuffer;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.system.MemoryUtil;

import com.window.Window;

public class NanoGui {

	private static long vg = -1;
	private static NVGLUFramebuffer frameBuffer;
	private static ByteBuffer fontBuffer;
	private static String fontName;

	private static boolean debug = false;
	// check if the 3D world can be iteracted with, e.g. if a menu if open you
	// don't want the mouse to rotate the camera around while your trying to
	// select a button
	private static boolean worldInteraction = true;

	public static void init() {
		vg = NanoVGGL3.nvgCreate((0) | NanoVGGL3.NVG_STENCIL_STROKES | NanoVGGL3.NVG_DEBUG);

		if (vg == MemoryUtil.NULL) {
			throw new RuntimeException("Could not init nanoVG");
		}

		frameBuffer = NanoVGGL3.nvgluCreateFramebuffer(vg, (int) (100 * 1), (int) (100 * 1),
				NVG_IMAGE_REPEATX | NVG_IMAGE_REPEATY);
		if (frameBuffer == null) {
			throw new RuntimeException("Could not create FBO.");
		}
	}

	public static void setFont(String fontFile) {
		try {
			String name = fontFile.substring(fontFile.lastIndexOf(File.separator) + 1).split("\\.")[0];
			fontBuffer = loadResource(fontFile, 150 * 1024);
			loadFonts(name);
			fontName = name;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static int loadFonts(String fontName) {
		int font = nvgCreateFontMem(vg, fontName, fontBuffer, 0);
		if (font == -1)
			throw new RuntimeException("Could not add font regular.");
		return font;
	}

	public static String getFontName() {
		return fontName;
	}

	private static ByteBuffer loadResource(String resource, int bufferSize) throws IOException {
		ByteBuffer buffer;

		Path path = Paths.get(resource);
		if (Files.isReadable(path)) {
			try (SeekableByteChannel fc = Files.newByteChannel(path)) {
				buffer = BufferUtils.createByteBuffer((int) fc.size() + 1);
				while (fc.read(buffer) != -1)
					;
			}
		} else {
			try (InputStream source = NanoGui.class.getClassLoader().getResourceAsStream(resource);
					ReadableByteChannel rbc = Channels.newChannel(source)) {
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

	private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
		ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
		buffer.flip();
		newBuffer.put(buffer);
		return newBuffer;
	}

	public static void enable() {
		nnvgBeginFrame(vg, Window.getWidth(), Window.getHeight(), 1);
	}

	public static void disable() {
		nvgEndFrame(vg);
	}

	public static void enableDebug() {
		NanoGui.debug = true;
	}

	public static boolean isDebug() {
		return debug;
	}

	public static void cleanup() {
		NanoVGGL3.nvgluDeleteFramebuffer(vg, frameBuffer);
		NanoVGGL3.nvgDelete(vg);
	}

	public static long getVG() {
		if (vg == -1) {
			System.err.println("NanoVG hasn't been initialized!");
		}

		return vg;
	}
	
	public static void setWorldInteraction(boolean interaction){
		NanoGui.worldInteraction = interaction;
		Window.releaseCursor();
	}

	public static boolean isWorldInteraction() {
		return worldInteraction;
	}
}
