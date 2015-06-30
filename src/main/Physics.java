package main;

import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.threed.jpct.Matrix;

import javax.vecmath.Vector3f;

public class Physics {
	BroadphaseInterface broadphase = null;
	DefaultCollisionConfiguration collisionConfiguration = null;
	CollisionDispatcher dispatcher = null;
	SequentialImpulseConstraintSolver solver = null;
	DiscreteDynamicsWorld dynamicsWorld = null;
	
	public void stepSimulation(float timeStep, int arg2) {
		 dynamicsWorld.stepSimulation(timeStep, arg2);
	}
	
	
	public Physics() {
		broadphase = new DbvtBroadphase();
		collisionConfiguration = new DefaultCollisionConfiguration();
		dispatcher = new CollisionDispatcher(collisionConfiguration);
		solver = new SequentialImpulseConstraintSolver();
		
		Vector3f worldAabbMin = new Vector3f(-10000,-10000,-10000);
		Vector3f worldAabbMax = new Vector3f(10000,10000,10000);
		AxisSweep3 overlappingPairCache = new AxisSweep3(worldAabbMin, worldAabbMax);
		
		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, overlappingPairCache, solver, collisionConfiguration);
		dynamicsWorld.getDispatchInfo().allowedCcdPenetration = 0f;
	}
	
	public static Matrix Quat4fToMatrix(javax.vecmath.Quat4f input)
	{
		float[] MatrixDump = new float[16];
		float xx = input.x * input.x;
		float xy = input.x * input.y;
		float xz = input.x * input.z;
		float xw = input.x * input.w;
		float yy = input.y * input.y;
		float yz = input.y * input.z;
		float yw = input.y * input.w;
		float zz = input.z * input.z;
		float zw = input.z * input.w;
		
		MatrixDump[0] = 1 - 2 * ( yy + zz );
		MatrixDump[4] = 2 * ( xy - zw );
		MatrixDump[8] = 2 * ( xz + yw );
		MatrixDump[1] = 2 * ( xy + zw );
		MatrixDump[5] = 1 - 2 * ( xx + zz );
		MatrixDump[9] = 2 * ( yz - xw );
		MatrixDump[2] = 2 * ( xz - yw );
		MatrixDump[6] = 2 * ( yz + xw );
		MatrixDump[10] = 1 - 2 * ( xx + yy );
		MatrixDump[15] = 1;
		
		Matrix buffer = new Matrix();
		buffer.setDump(MatrixDump);
		return buffer;
	}
	
}
