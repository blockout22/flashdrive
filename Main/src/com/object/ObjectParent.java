package com.object;

import javax.vecmath.Vector3f;

import com.mesh.Mesh;
import com.mesh.MeshObject;

public class ObjectParent extends MeshObject{
	
	private String name = "";

	public ObjectParent(Mesh mesh, Vector3f position, Vector3f rotation, Vector3f scale) {
		super(mesh, position, rotation, scale);
	}
	

	@Override
	public void update() {
	}

	@Override
	public void interact() {
	}
	
	public void setName(String name){
		this.name = name.toLowerCase();
	}
	
	public String getName()
	{
		return name;
	}
}
