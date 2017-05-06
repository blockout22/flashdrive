package com.object;

import javax.vecmath.Vector3f;

import com.mesh.Mesh;
import com.mesh.MeshObject;

public class ObjectWall extends MeshObject{

	public ObjectWall(Mesh mesh, Vector3f position, Vector3f rotation, Vector3f scale) {
		super(mesh, position, rotation, scale);
	}

	@Override
	public void update() {
	}

	@Override
	public void interact() {
	}

}
