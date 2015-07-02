package main;

import android.app.Activity;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Pair;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Logger;
import com.threed.jpct.RGBColor;
import com.threed.jpct.util.AAConfigChooser;
import com.threed.jpct.util.MemoryHelper;

import java.lang.reflect.Field;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

/**
 * A simple demo. This shows more how to use jPCT-AE than it shows how to write
 * a proper application for Android. It includes basic activity management to
 * handle pause and resume...
 * 
 * @author EgonOlsen
 * 
 */
public class AppRenderingActivity extends Activity {

	// Used to handle pause and resume...
	private static AppRenderingActivity master = null;
	private GameLogic logic = null;
	private GLSurfaceView mGLView;
	private MyRenderer renderer = null;
	private FrameBuffer fb = null;
	
	private RGBColor backgroundColor = new RGBColor(128, 128, 230);

	private float touchTurn = 0;
	private float touchTurnUp = 0;

	private float xpos = -1;
	private float ypos = -1;

	private int fps = 0;
	private boolean gl2 = true;

	//zbior wskaznikow, jakie beda znajdowac sie na ekranie
	//z flaga, czy wskaznik jest juz wykorzystywany czy tez nie
	//oraz wspolrzednymi wskaznika
    //kluczem jest ID wskaznika
	private SparseArray<Pair<Boolean, Point>> touches = new SparseArray();


	protected void onCreate(Bundle savedInstanceState) {

		logic = new GameLogic(this);
		Logger.log("onCreate");

		// Erase the title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Make it full Screen
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		if (master != null) {
			copy(master);
		}

		super.onCreate(savedInstanceState);
		mGLView = new GLSurfaceView(getApplication());

		if (gl2) {
			mGLView.setEGLContextClientVersion(2);
		} else {
			//mGLView.setEGLConfigChooser(new AAConfigChooser(mGLView));
			/* */
			mGLView.setEGLConfigChooser(new GLSurfaceView.EGLConfigChooser() {
				public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
					// Ensure that we get a 16bit framebuffer. Otherwise, we'll
					// fall back to Pixelflinger on some device (read: Samsung
					// I7500). Current devices usually don't need this, but it
					// doesn't hurt either.
					int[] attributes = new int[] { EGL10.EGL_DEPTH_SIZE, 16, EGL10.EGL_NONE };
					EGLConfig[] configs = new EGLConfig[1];
					int[] result = new int[1];
					egl.eglChooseConfig(display, attributes, configs, 1, result);
					return configs[0];
				}
			});
			/* */

		}

		renderer = new MyRenderer();
		mGLView.setRenderer(renderer);
		setContentView(mGLView);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mGLView.onPause();
		System.exit(0);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mGLView.onResume();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	private void copy(Object src) {
		try {
			Logger.log("Copying data from master Activity!");
			Field[] fs = src.getClass().getDeclaredFields();
			for (Field f : fs) {
				f.setAccessible(true);
				f.set(this, f.get(src));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public boolean onTouchEvent(MotionEvent me) {

		/* */
		int action = me.getActionMasked();
		int pointerIndex = me.getActionIndex();
		int pointerId = me.getPointerId(pointerIndex);
		if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
			Point p = new Point((int)me.getX(pointerIndex), (int)me.getY(pointerIndex));
			touches.put(pointerId, new Pair(false, p));
			return true;
		}

		if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_CANCEL) {
			touches.remove(pointerId);
		}

		if (action == MotionEvent.ACTION_MOVE) {
			for (int i = 0; i < me.getPointerCount(); i++)
			{
				Pair<Boolean, Point> p = touches.get(me.getPointerId(i));
				if (p != null)
				{
					p.second.x = (int)me.getX(i);
					p.second.y = (int)me.getY(i);
				}
			}
		}


		/* */
		try {
			Thread.sleep(15);
		} catch (Exception e) {
			// No need for this...
		}
		return super.onTouchEvent(me);
		/* */
	}

	protected boolean isFullscreenOpaque() {
		return true;
	}

	class MyRenderer implements GLSurfaceView.Renderer {

		private long time = System.currentTimeMillis();

		public MyRenderer() {
		}

		public void onSurfaceChanged(GL10 gl, int w, int h) {
			if (fb != null) {
				fb.dispose();
			}

			if (gl2) {
				fb = new FrameBuffer(w, h); // OpenGL ES 2.0 constructor
			} else {
				fb = new FrameBuffer(gl, w, h); // OpenGL ES 1.x constructor
			}

			if (master == null) {
				/* */
				logic.Start();
				
				/* */
				MemoryHelper.compact();

				if (master == null) {
					Logger.log("Saving master Activity!");
					master = AppRenderingActivity.this;
				}
			}
		}

		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		}

		public void onDrawFrame(GL10 gl) {
			fb.clear(backgroundColor);
			logic.world.graphicsWorld.renderScene(fb);
			/* */
			logic.world.graphicsWorld.draw(fb);
			logic.skyBox.render(logic.world.graphicsWorld, fb);
			logic.Update(touches, fb);
						
			fb.display();

			if (System.currentTimeMillis() - time >= 1000) {
				Logger.log(fps + "fps");
				fps = 0;
				time = System.currentTimeMillis();
			}
			fps++;
			/* */
		}


	}
}
