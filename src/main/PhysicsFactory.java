package main;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.threed.jpct.Object3D;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.util.ExtendedPrimitives;

import javax.vecmath.Vector3f;

public class PhysicsFactory {
	static ActiveGameObject getBox(String name, SimpleVector scale, SimpleVector position, SimpleVector rotation, GameWorld world, float mass, float restitution, float friction, float linearDamping, float angularDamping) {
		 BoxShape shape = new BoxShape(new Vector3f(scale.x/2, scale.y/2, scale.z/2));
 	     Vector3f localInertia = new Vector3f(0,0,0);
	     shape.calculateLocalInertia(mass, localInertia);

	     Transform startTransform = new Transform();
	     startTransform.setIdentity();
	     startTransform.origin.set(position.x, -position.y, -position.z);

	     
	     Object3D box = ExtendedPrimitives.createBox(scale);
		 box.setName(name);
		 box.setOrigin(position);
		 box.strip();
		 box.build();
		 world.graphicsWorld.addObject(box);
	    
	     GameObjectMotion ms = new GameObjectMotion(startTransform, box);
	    
	     RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass, ms, shape, localInertia);
	     RigidBody body = new RigidBody(rbInfo);
		 body.setRestitution(restitution);
	     body.setFriction(friction);
	     body.setDamping(linearDamping, angularDamping);
		 //body.setUserPointer(boxgfax);

	     world.gamePhysics.dynamicsWorld.addRigidBody(body);

		 ActiveGameObject result = new ActiveGameObject(box, body);
		 result.initialPosition.set(position);
		 result.initialRotation.set(rotation);
		 box.setUserObject(result);
		 body.setUserPointer(result);
		 result.setRotation(rotation);
		 return result;
	}
	
	static ActiveGameObject getStaticBox(String name, SimpleVector scale, SimpleVector position, SimpleVector rotation, GameWorld world, float friction) {
		CollisionShape groundShape = new BoxShape(new Vector3f(scale.x/2, scale.y/2, scale.z/2));
		Transform groundTransform = new Transform();
		groundTransform.setIdentity();
		groundTransform.origin.set(new Vector3f(position.x, -position.y, -position.z));
		float mass = 0f;
		Vector3f localInertia = new Vector3f(0, 0, 0);
		DefaultMotionState myMotionState = new DefaultMotionState(groundTransform);
		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(
					mass, myMotionState, groundShape, localInertia);
		RigidBody body = new RigidBody(rbInfo);
		body.setFriction(friction);
		world.gamePhysics.dynamicsWorld.addRigidBody(body);
		
		Object3D box = ExtendedPrimitives.createBox(scale);
		box.setName(name);
		box.setOrigin(position);
		box.strip();
		box.build();
		world.graphicsWorld.addObject(box);

		ActiveGameObject result = new ActiveGameObject(box, body);
		result.initialPosition.set(position);
		result.initialRotation.set(rotation);
		box.setUserObject(result);
		body.setUserPointer(result);
		result.setRotation(rotation);
		return result;
	}
	
	
}
