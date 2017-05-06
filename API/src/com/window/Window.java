package com.window;

import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import com.input.Input;
import com.math.Time;

public class Window {

	private static boolean GLFW_INIT = false;
	public static long window;

	private static int WIDTH;
	private static int HEIGHT;
	private static String title;

	private static double cursorXpos = 0;
	private static double cursorYpos = 0;

	private static long lastFPStime;
	private static long variableYieldTime;
	private static long lastTime;
	private static int fps;
	private static int fps_buffer;
	private static ArrayList<Integer> fps_history = new ArrayList<Integer>();
	private static int avarage_fps = 0;

	private static GLFWCursorPosCallback cursorCallback;
	private static GLFWWindowSizeCallback windowsizeCallback;
	private static GLFWKeyCallback keyCallback;

	private static boolean mouseGrabbed = false;

	private static boolean fullscreen = false;
	private static boolean requestClose = false;

	public static void createWindow() {
		createWindow(800, 600, "Game", true);
	}

	public static void createWindow(int width, int height) {
		createWindow(width, height, "Game", true);
	}

	public static void createWindow(int width, int height, String title) {
		createWindow(width, height, title, true);
	}

	public static void createWindow(int width, int height, boolean vsync) {
		createWindow(width, height, "Game", vsync);
	}

	public static void createWindow(int width, int height, String title, boolean vsync) {
		Window.WIDTH = width;
		Window.HEIGHT = height;
		Window.title = title;

		if (!GLFW_INIT) {
			if (!GLFW.glfwInit()) {
				throw new IllegalStateException();
			}
		}
		
		GLFW_INIT = true;

		window = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);

		if (window == MemoryUtil.NULL) {
			System.err.println("Window returned NULL");
			System.exit(-1);
			return;
		}

		GLFW.glfwMakeContextCurrent(window);

		GLFW.glfwShowWindow(window);
		GL.createCapabilities();
		clearColor(0, 1, 0, 1);
		GL11.glViewport(0, 0, width, height);

		callbacks();

		lastFPStime = Time.getTime();
	}
	
	public static void setFullscreen(boolean fullScreen, int width, int height){
		if(Window.fullscreen == fullScreen){
			return;
		}
		
		Window.fullscreen = fullScreen;
		long val = fullScreen ? GLFW.glfwGetPrimaryMonitor() : MemoryUtil.NULL;
		System.out.println("VALUE: " + val);
		System.out.println("WIDHT: " + WIDTH + " HEIGHT: " + HEIGHT + " TITLE: " + title + " Window: " + window);
		long fsWindowHandle = GLFW.glfwCreateWindow(WIDTH, HEIGHT, title, val, window);
		closeResources();
//		GLFW.glfwDestroyWindow(window);
		window = fsWindowHandle;
		
		GLFW.glfwMakeContextCurrent(window);
		
		GLFW.glfwShowWindow(window);
//		GL.createCapabilities();
		clearColor(0, 1, 0, 1);
		GL11.glViewport(0, 0, width, height);
		
//		Window.WIDTH = width;
//		Window.HEIGHT = height;
		
		callbacks();
	}
	
	public static boolean isFullscreen(){
		return fullscreen;
	}

	public static void clearColor(float red, float green, float blue, float alpha) {
		GL11.glClearColor(red, green, blue, alpha);
	}

	private static void callbacks() {
		cursorCallback = new GLFWCursorPosCallback() {
			public void invoke(long window, double xpos, double ypos) {
				cursorXpos = xpos;
				cursorYpos = ypos;
			}
		};
		GLFW.glfwSetCursorPosCallback(Window.window, cursorCallback);

		windowsizeCallback = new GLFWWindowSizeCallback() {
			public void invoke(long window, int width, int height) {
				Window.WIDTH = width;
				Window.HEIGHT = height;
				
				GL11.glViewport(0, 0, WIDTH, HEIGHT);
			}
		};
		GLFW.glfwSetWindowSizeCallback(window, windowsizeCallback);
		
		keyCallback = new GLFWKeyCallback() {
			public void invoke(long window, int key, int scancode, int action, int mods) {
				Input.setKey(key, scancode, action, mods);
			}
		};
//		GLFW.glfwSetKeyCallback(window, keyCallback);
	}

	public static void requestClose()
	{
		Window.requestClose = true;
	}

	public static boolean isCloseRequested() {
		if(requestClose){
			return true;
		}
		boolean closeStage = GLFW.glfwWindowShouldClose(window);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		Time.setDelta();
		if (Time.getTime() - lastFPStime >= 1000) {
			lastFPStime += 1000;
			fps = fps_buffer;

			fps_history.add(fps);
			if (fps_history.size() > 25) {
				fps_history.remove(0);
			}
			avarage_fps = avarageFPS();

			fps_buffer = 0;
		}
		fps_buffer++;
		return closeStage;
	}

	private static int avarageFPS() {
		int sum = 0;
		for (int i = 0; i < fps_history.size(); i++) {
			sum += fps_history.get(i);
		}
		int avarage = sum / fps_history.size();
		return avarage;
	}

	public static void sync(int fps) {
		if (fps <= 0) {
			return;
		}

		long sleepTime = 1000000000 / fps;
		long yieldTime = Math.min(sleepTime, variableYieldTime + sleepTime % (1000 * 1000));
		long overSleep = 0;

		try {
			while (true) {
				long t = System.nanoTime() - lastTime;

				if (t < sleepTime - yieldTime) {
					Thread.sleep(1);
				} else if (t < sleepTime) {
					Thread.yield();
				} else {
					overSleep = t - sleepTime;
					break;
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lastTime = System.nanoTime() - Math.min(overSleep, sleepTime);

			if (overSleep > variableYieldTime) {
				variableYieldTime = Math.min(variableYieldTime + 200 * 1000, sleepTime);
			} else if (overSleep < variableYieldTime - 200 * 1000) {
				variableYieldTime = Math.max(variableYieldTime - 2 * 1000, 0);
			}
		}
	}

	public static void grabCursor() {
		GLFW.glfwSetInputMode(Window.window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
		Window.mouseGrabbed = true;
	}

	public static void releaseCursor() {
		GLFW.glfwSetInputMode(Window.window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
		Window.mouseGrabbed = false;
	}

	/**
	 * gets the window width
	 * 
	 * @return
	 */
	public static int getWidth() {
		return Window.WIDTH;
	}

	/**
	 * gets the window height
	 * 
	 * @return
	 */
	public static int getHeight() {
		return Window.HEIGHT;
	}

	/**
	 * gets the cursor position between 0:width;
	 * 
	 * @return
	 */
	public static double getCursorXpos() {
		return cursorXpos;
	}

	/**
	 * gets the cursor position between 0:height;
	 * 
	 * @return
	 */
	public static double getCursorYpos() {
		return cursorYpos;
	}

	/**
	 * gets the cursor position between -1:1;
	 * 
	 * @return
	 */
	public static double getOpenGLx() {
		double width = getWidth();
		double openGLx = Window.getCursorXpos() / (width / 2) - 1;
		return openGLx;
	}

	/**
	 * gets the cursor position between -1:1;
	 * 
	 * @return
	 */
	public static double getOpenGly() {
		double height = getHeight();
		double openGLy = 1 - (Window.getCursorYpos() / (height / 2));

		return openGLy;
	}

	public static int getFPS() {
		return fps;
	}

	public static int getAvarageFPS() {
		return avarage_fps;
	}

	public static void setTitle(String title) {
		GLFW.glfwSetWindowTitle(window, title);
		Window.title = title;
	}

	public String getTitle() {
		return title;
	}

	public static void update() {
		Input.update();
		GLFW.glfwSwapBuffers(window);
		GLFW.glfwPollEvents();
	}

	public static boolean isMouseGrabbed() {
		return mouseGrabbed;
	}

	public static void enableDepthBuffer() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	public static void disableDepthBuffer() {
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}

	public static void enableVSync() {
		GLFW.glfwSwapInterval(1);
	}

	public static void disableVSync() {
		GLFW.glfwSwapBuffers(0);
	}
	
	private static void closeResources()
	{
		cursorCallback.close();
		windowsizeCallback.close();
		GLFW.glfwDestroyWindow(window);
	}

	public static void close() {
		closeResources();
		GLFW.glfwTerminate();
	}

}
