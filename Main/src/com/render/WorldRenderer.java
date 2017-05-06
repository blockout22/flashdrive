package com.render;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;

import javax.vecmath.Vector3f;

import com.input.Input;
import com.input.Key;
import com.loader.OBJLoader;
import com.loader.TextureLoader;
import com.mesh.Mesh;
import com.mesh.MeshObject;
import com.object.ObjectFloor;
import com.object.ObjectParent;
import com.object.ObjectPlayer;
import com.object.ObjectWall;
import com.shader.WorldShader;
import com.texture.Texture;
import com.window.Window;

public class WorldRenderer {

	private boolean closed = false;
	private Thread fileListenerThread;

	private WorldCamera camera;
	private WorldShader shader;

	private Texture boxTexture;
	private Mesh box;

	private ObjectPlayer player;
	private ObjectParent mam, dad;

	private Texture wallTexture, floorTexture;
	private MeshObject wall1, wall2, wall3, wall4;
	private MeshObject floor1;
	
	private ArrayList<MeshObject> walls = new ArrayList<MeshObject>();

	public WorldRenderer() {
		camera = new WorldCamera(70f, 0.1f, 2000f);
		shader = new WorldShader();

		boxTexture = TextureLoader.loadTexture("res/images/box.jpg");
		box = OBJLoader.load("res/models/box.obj");

		player = new ObjectPlayer(box, new Vector3f(-18, -1, -20), new Vector3f(0, 0, 0), new Vector3f(0.2f, 0.5f, 0.2f));
		mam = new ObjectParent(box, new Vector3f(-20, 0, -19), new Vector3f(0, 0, 0), new Vector3f(0.4f, 1.5f, 0.4f));
		mam.setName("mam");
		dad = new ObjectParent(box, new Vector3f(-15, 0, -20), new Vector3f(0, 0, 0), new Vector3f(0.4f, 1.5f, 0.4f));
		dad.setName("dad");

		buildHouse();

		shader.bind();
		shader.loadMatrix(shader.getProjectionMatrix(), camera.getProjectionMatrix());
		shader.unbind();

		fileListener();
	}

	private void buildHouse() {
		Vector3f offset = new Vector3f(0, 0, -10);
		// walls
		wallTexture = TextureLoader.loadTexture("res/images/wallTexture.jpg");
		floorTexture = TextureLoader.loadTexture("res/images/floorTexture.jpg");
		wall1 = new ObjectWall(box, new Vector3f(-3 + offset.x, 1f + offset.y, 0 + offset.z), new Vector3f(0, 0, 0), new Vector3f(0.2f, 1, 3f));
		wall1.setTexture(wallTexture);

		wall2 = new ObjectWall(box, new Vector3f(0f + offset.x, 1f + offset.y, -3 + offset.z), new Vector3f(0, 0, 0), new Vector3f(3f, 1, 0.2f));
		wall2.setTexture(wallTexture);

		wall3 = new ObjectWall(box, new Vector3f(3f + offset.x, 1f + offset.y, 0 + offset.z), new Vector3f(0, 0, 0), new Vector3f(0.2f, 1, 3f));
		wall3.setTexture(wallTexture);

		wall4 = new ObjectWall(box, new Vector3f(0f + offset.x, 1f + offset.y, 3f + offset.z), new Vector3f(0, 0, 0), new Vector3f(3f, 1, 0.2f));
		wall4.setTexture(wallTexture);

		// floor
		floor1 = new ObjectFloor(box, new Vector3f(0 + offset.x, 0 + offset.y, 0 + offset.z), new Vector3f(0, 0, 0), new Vector3f(3f, 0.2f, 3f));
		floor1.setTexture(floorTexture);
	}

	public void render() {
		Window.enableDepthBuffer();
		shader.bind();
		shader.loadViewMatrix(camera);
		{
			box.enable();
			{
				// renderBox(player);
				// renderBox(mam);
				// renderBox(dad);

				// renderHouse
//				renderBox(wall1);
//				renderBox(wall2);
//				renderBox(wall3);
//				renderBox(wall4);
//
//				renderBox(floor1);
				
				for(int i = 0; i < walls.size(); i++){
					renderBox(walls.get(i));
				}
			}
			box.disable();
		}
		shader.unbind();

	}

	// tempcode
	private void fileListener() {
		
		fileListenerThread = new Thread(new Runnable() {
			public void run() {
				File loc = new File("loc.txt");
				try {
					readFile(loc);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				Path dir = loc.getAbsoluteFile().getParentFile().toPath();
				System.out.println(dir);
				try {
					WatchService watcher = FileSystems.getDefault().newWatchService();
					WatchKey key = dir.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);

					while (!closed) {
						try {
							final WatchKey wk = watcher.take();
							
							 for (WatchEvent<?> event : wk.pollEvents()) {
								 final Path changed = (Path) event.context();
								 System.out.println(changed);
								 
								 if(changed.endsWith("loc.txt")){
									 System.out.println("My File Has Changed");
									 
									 try {
										readFile(loc);
									} catch (Exception e) {
										e.printStackTrace();
									}
								 }
							 }
							 
							 boolean valid = wk.reset();
							 
							 if(!valid){
								 System.out.println("Key has been unregisterede");
							 }
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		fileListenerThread.start();
	}
	
	private void readFile(File loc) throws Exception{
		 BufferedReader br = new BufferedReader(new FileReader(loc));
		 
		 walls.clear();
		 String line;
		 while((line = br.readLine()) != null){
			 String pos = line.split(" ")[0];
			 String scl = line.split(" ")[1];
			 
			 Vector3f position = new Vector3f(Float.valueOf(pos.split(",")[0]), Float.valueOf(pos.split(",")[1]), Float.valueOf(pos.split(",")[2]));
			 Vector3f scale = new Vector3f(Float.valueOf(scl.split(",")[0]), Float.valueOf(scl.split(",")[1]), Float.valueOf(scl.split(",")[2]));
			 
			 walls.add(new ObjectWall(box, position, new Vector3f(0, 0, 0), scale));
		 }
		 
		 br.close();
	}

	public void renderBox(MeshObject object) {
		box.render(shader, shader.getModelMatrix(), object, camera);
	}

	public void update() {
		camera.update();

		movement();
	}

	private void movement() {
		if (Input.isKeyDown(Key.KEY_W)) {
			camera.moveForward();
		} else if (Input.isKeyDown(Key.KEY_S)) {
			camera.moveBack();
		}

		if (Input.isKeyDown(Key.KEY_A)) {
			camera.moveLeft();
		} else if (Input.isKeyDown(Key.KEY_D)) {
			camera.moveRight();
		}
	}

	public void cleanup() {
		floorTexture.cleanup();
		wallTexture.cleanup();
		boxTexture.cleanup();
		shader.cleanup();
		closed = true;
		fileListenerThread.interrupt();
	}
}
