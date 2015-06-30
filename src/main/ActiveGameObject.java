package main;

import com.bulletphysics.collision.dispatch.CollisionWorld;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.QuaternionUtil;
import com.bulletphysics.linearmath.Transform;
import com.threed.jpct.Object3D;
import com.threed.jpct.SimpleVector;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class ActiveGameObject {
	
	public RigidBody rigidBody;
	public Object3D object;
	public String name;

	public SimpleVector initialPosition = new SimpleVector(0, 0, 0);
	public SimpleVector initialRotation = new SimpleVector(0, 0, 0);

	public ActiveGameObject(Object3D aobject, RigidBody abodyComponent) {
		rigidBody = abodyComponent;
		object = aobject;

	}

	private static SimpleVector getEulerRotation(Quat4f quat)
	{
		float q0 = quat.x;
		float q1 = quat.y;
		float q2 = quat.z;
		float q3 = quat.w;
		float x = (float) Math.atan2((2 * (q0 * q1 + q2 * q3)), (1.0f - 2 * (q1 * q1 + q2 * q2)));
		float y = (float) Math.asin(2*(q0*q2 - q3*q1));
		float z = (float) Math.atan2(2*(q0*q3 + q1*q2), 1.0f - 2*(q2*q2 + q3*q3));
		return new SimpleVector(x, y, z);
	}

	public SimpleVector getRotation()
	{
		Transform transform = new Transform();
		rigidBody.getCenterOfMassTransform(transform);
		Quat4f quat = new Quat4f();
		transform.getRotation(quat);
		return getEulerRotation(quat);
	}

	public void setRotation(SimpleVector eulerRotation)
	{
		rigidBody.setAngularVelocity(new Vector3f(0, 0, 0));
		Transform transform = new Transform();
		rigidBody.getCenterOfMassTransform(transform);
		Quat4f quat = new Quat4f();
		QuaternionUtil.setEuler(quat, eulerRotation.y, eulerRotation.x, eulerRotation.z);
		transform.setRotation(quat);
		rigidBody.setCenterOfMassTransform(transform);
	}

	public void setPosition(SimpleVector absolutePosition)
	{
		rigidBody.clearForces();
		rigidBody.setLinearVelocity(new Vector3f(0, 0, 0));
		Transform transform = new Transform();
		rigidBody.getCenterOfMassTransform(transform);
		transform.origin.set(absolutePosition.x, -absolutePosition.y, -absolutePosition.z);
		rigidBody.setCenterOfMassTransform(transform);
	}



	private boolean testRayCollision(DiscreteDynamicsWorld dynamicsWorld, Vector3f ray)
	{
		/* */
		Vector3f buf = new Vector3f(0, 0, 0);
		rigidBody.getCenterOfMassPosition(buf);

		ray.add(buf);
		CollisionWorld.ClosestRayResultCallback res = new CollisionWorld.ClosestRayResultCallback(buf, ray);
		dynamicsWorld.rayTest(buf, ray, res);
		return res.hasHit();
		/* */
	}

	public boolean isOnTheGround(DiscreteDynamicsWorld dynamicsWorld)
	{
		float ray = 6.5f;
		return (testRayCollision(dynamicsWorld, new Vector3f(0, -ray, 0))) ||
				(testRayCollision(dynamicsWorld, new Vector3f(0, -ray, -ray))) ||
				(testRayCollision(dynamicsWorld, new Vector3f(0, -ray, ray))) ||
				(testRayCollision(dynamicsWorld, new Vector3f(-ray, -ray, 0))) ||
				(testRayCollision(dynamicsWorld, new Vector3f(-ray, -ray, -ray))) ||
				(testRayCollision(dynamicsWorld, new Vector3f(-ray, -ray, ray))) ||
				(testRayCollision(dynamicsWorld, new Vector3f(ray, -ray, 0))) ||
				(testRayCollision(dynamicsWorld, new Vector3f(ray, -ray, -ray))) ||
				(testRayCollision(dynamicsWorld, new Vector3f(ray, -ray, ray)));
	}


}
