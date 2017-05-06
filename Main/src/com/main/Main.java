package com.main;

import com.nanovg.NanoGui;
import com.render.GuiRenderer;
import com.render.WorldRenderer;
import com.window.Window;

public class Main {

	private WorldRenderer world;
	private GuiRenderer gui;

	private long startTime;

	public Main() {
		startTime = System.currentTimeMillis();
		Window.createWindow(1920 / 2, 1080 / 2, "Game", true);
		Window.enableVSync();
		// Window.enableVSync();

		world = new WorldRenderer();
		gui = new GuiRenderer();

		System.out.println("Load Time: " + (System.currentTimeMillis() - startTime) + "ms");
		while (!Window.isCloseRequested()) {

			world.render();
			if (NanoGui.isWorldInteraction()) {
				world.update();
			}

			gui.render();
			gui.update();

			// Input.update();
			Window.update();
		}

		world.cleanup();
		gui.cleanup();
		Window.close();
	}

	public static void main(String[] args) {
		new Main();
		// Vulkan vulkan = new Vulkan();
		// vulkan.init();
	}

}
