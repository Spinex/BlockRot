package main;

import com.bulletphysics.linearmath.MatrixUtil;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import com.threed.jpct.Matrix;
import com.threed.jpct.Object3D;
import com.threed.jpct.SimpleVector;

public class GameObjectMotion extends MotionState {

	Transform centerOfMassOffset = new Transform();
	Object3D object;
	
	private void setGraphicFromTransform(Transform tran)
	{
		
	    SimpleVector pos = object.getTransformedCenter();
	    object.translate(tran.origin.x - pos.x,
			  (-tran.origin.y) - pos.y, 
			  (-tran.origin.z) - pos.z);
	   
	    float[] ma = new float[4];
	    float[] dump = object.getRotationMatrix().getDump(); //new float[16]; 
	    Matrix matrixGfx = new Matrix();
	    MatrixUtil.getOpenGLSubMatrix(tran.basis, dump);
	    
	    matrixGfx.setDump(dump);
	    matrixGfx.rotateX((float)Math.PI);
	    
	    object.setRotationMatrix(matrixGfx);
	    
    }
	
	private void setTransformFromGraphic(Transform tran)
	{
		  SimpleVector p = object.getTransformedCenter();
			tran.origin.set(p.x, -p.y, -p.z); // not sure if translation or position
			
			Matrix matrixGfx = object.getRotationMatrix();
			//matrixGfx.rotateX((float)Math.PI);
			MatrixUtil.getOpenGLSubMatrix(tran.basis, matrixGfx.getDump());
    }
	
	public GameObjectMotion(Transform atransform, Object3D aobject)
	{
		centerOfMassOffset.setIdentity();
		object = aobject;
		setGraphicFromTransform(atransform);
	}
	
	@Override
	public Transform getWorldTransform(Transform tran) {
		setTransformFromGraphic(tran);
	    return tran;
	}

	@Override
	public void setWorldTransform(Transform tran) {
		setGraphicFromTransform(tran);
	}

}
