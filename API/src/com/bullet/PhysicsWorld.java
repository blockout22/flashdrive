package com.bullet;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

public class PhysicsWorld {
	
	public BroadphaseInterface broadphase = new DbvtBroadphase();
	public DefaultCollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
	public CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
	public SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();
	
	private DynamicsWorld world = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);

	public PhysicsWorld(){
		world.setGravity(new Vector3f(0, -9.81f, 0));
	}
	
	/**
	 * creates a BoxShape in bullet
	 * @param halfExtents
	 * @return
	 */
	public BoxShape createBoxShape(Vector3f halfExtents){
		BoxShape shape = new BoxShape(halfExtents);
		return shape;
	}
	
	public void stepSimulation(float timeStep)
	{
		world.stepSimulation(timeStep);
	}
	
	/**
	 * adds the Shape to the world and sets its properties
	 * @param shape
	 * @param position
	 * @param mass
	 * @param allowRotation
	 * @return
	 */
	public RigidBody addShapeToWorld(CollisionShape shape, Vector3f position, float mass, boolean allowRotation){
		Transform transform = new Transform();
		transform.basis.set(new Quat4f(0, 0, 0, 1));
		transform.origin.set(position);
		
		DefaultMotionState motionState = new DefaultMotionState(transform);
		
		Vector3f inertia = new Vector3f(0, 0, 0);
		shape.calculateLocalInertia(mass, inertia);
		
		RigidBodyConstructionInfo info = new RigidBodyConstructionInfo(mass, motionState, shape, inertia);
		RigidBody body = new RigidBody(info);
		
		if(!allowRotation){
			body.setAngularFactor(0f);
		}
		
		world.addRigidBody(body);
		return body;
	}
	
	/**
	 * cleans up any memory stuff
	 */
	public void cleanup()
	{
		world.destroy();
	}
}
