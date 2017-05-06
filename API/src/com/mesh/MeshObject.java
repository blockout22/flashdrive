package com.mesh;

import javax.vecmath.Vector3f;

import com.listener.MoveListener;
import com.texture.Texture;

public abstract class MeshObject {
	
	private Mesh mesh;
	protected Vector3f position;
	private Vector3f rotation;
	private Vector3f scale;
	private int textureID = 1;
	
	private boolean shouldUpdateOutsideBounds = false;
	
	private MoveListener moveListener = null;
	
	private Vector3f prevPos = new Vector3f();
	
	public MeshObject(Mesh mesh, Vector3f position, Vector3f rotation, Vector3f scale) {
		this.mesh = mesh;
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
		
		prevPos.x = position.x;
		prevPos.y = position.y;
		prevPos.z = position.z;
	}
	
	public abstract void update();
	public abstract void interact();
	
	public void setPrevPosToCurrent()
	{
		prevPos.x = position.x;
		prevPos.y = position.y;
		prevPos.z = position.z;
	}
	
	public Vector3f getPreviousPos()
	{
		return prevPos;
	}
	
	public void addListener(MoveListener moveListener){
		this.moveListener = moveListener;
	}
	
	public MoveListener getMoveListener()
	{
		return moveListener;
	}
	
	/**
	 * updates the Meshobject even if its outside the render distance of camera;
	 * @param update
	 */
	public void updateOutsideBounds(boolean update)
	{
		shouldUpdateOutsideBounds = update;
	}
	
	public boolean getUpdateOutsideBounts()
	{
		return shouldUpdateOutsideBounds;
	}
	
	public Mesh getMesh()
	{
		return mesh;
	}
	
	public int getTextureID() {
		return textureID;
	}

	public void setTexture(Texture texture) {
		this.textureID = texture.getID();
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public Vector3f getRotation() {
		return rotation;
	}

	public void setRotation(Vector3f rotation) {
		this.rotation = rotation;
	}

	public Vector3f getScale() {
		return scale;
	}

	public void setScale(Vector3f scale) {
		this.scale = scale;
	}
}
