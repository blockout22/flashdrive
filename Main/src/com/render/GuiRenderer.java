package com.render;

import com.input.Input;
import com.input.Key;
import com.nanovg.NanoGui;
import com.nanovg.gui.Button;
import com.nanovg.gui.ProgressBar;
import com.nanovg.gui.Text;
import com.nanovg.gui.action.ActionEvent;
import com.window.Window;

public class GuiRenderer {
	
	private ProgressBar bar = new ProgressBar();
	private Text fps, avarageFPS;
	private Button button, toggleFullscreen;
	
	public GuiRenderer()
	{
		NanoGui.init();
		NanoGui.enableDebug();
		NanoGui.setFont("sans.ttf");
		bar.setVisible(false);
		
		fps = new Text("Hello World", 10, Window.getHeight() - 10);
		avarageFPS = new Text("Hello World", 10, fps.getY() - 20);
		
		button = new Button("close", Window.getWidth() / 2 - 100, Window.getHeight() / 2 - 25, 200, 25);
		button.setVisible(false);
		
		toggleFullscreen = new Button("Toggle Fullscreen", Window.getWidth() / 2 - 100, 25, 200, 25);
		toggleFullscreen.setVisible(false);
		
		actions();
	}
	
	private void actions()
	{
		button.setActionListener(new ActionEvent() {
			public void mouseUp() {
			}
			public void mouseDown() {
			}
			public void hover() {
				button.setText("Hover");
			}
			public void clicked() {
				Window.requestClose();
			}
		});
		
		toggleFullscreen.setActionListener(new ActionEvent() {
			public void mouseUp() {
			}
			public void mouseDown() {
			}
			public void hover() {
			}
			public void clicked() {
				Window.setFullscreen(!Window.isFullscreen(), !Window.isFullscreen() ? 1920 : 800, !Window.isFullscreen() ? 1080 : 600);
			}
		});
	}
	
	public void render(){
		NanoGui.enable();
		{
			bar.update();
			fps.update();
			avarageFPS.update();
			button.update();
			toggleFullscreen.update();
			
			button.setText("close");
		}
		NanoGui.disable();
	}
	
	public void update(){
		if(NanoGui.isWorldInteraction() && !bar.isVisible()){
			if(Input.isKeyPressed(Key.KEY_ESCAPE)){
				NanoGui.setWorldInteraction(false);
				bar.setVisible(true);
				button.setVisible(true);
				toggleFullscreen.setVisible(true);
			}
		}else if(bar.isVisible()){
			if(Input.isKeyPressed(Key.KEY_ESCAPE)){
				NanoGui.setWorldInteraction(true);
				bar.setVisible(false);
				button.setVisible(false);
				toggleFullscreen.setVisible(false);
			}
		}
		
		fps.setText("FPS: " + Window.getFPS());
		avarageFPS.setText("Avarage FPS: " + Window.getAvarageFPS());
	}
	
	public void cleanup()
	{
		NanoGui.cleanup();
	}

}
