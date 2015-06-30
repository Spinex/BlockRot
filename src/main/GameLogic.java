package main;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Pair;
import android.util.SparseArray;
import android.view.Display;

import com.threed.jpct.Camera;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Light;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.util.SkyBox;

import javax.vecmath.Vector3f;

import src.R;

public class GameLogic {
	public GameWorld world = null;
	private GUIButton jumpButton = null;
	private GUIController analogMovementController = null;
	private GUIController analogCameraController = null;
	private TextureManager texManager = null;
	private Camera cam = null;

	private float camRotationalSlowness = 30.0f;
	private float camDistance = 50.0f;
	private float jumpHeight = 4.0f;
	public SkyBox skyBox = null;

	private float maxMovementSpeed = 60.0f;
	private float movementForce = 100.0f;

	private int width = 0;
	private int height = 0;
	
	public AppRenderingActivity main = null;
	
	public GameLogic(AppRenderingActivity amain) {
		main = amain;
		texManager = TextureManager.getInstance();

		Display display = main.getWindowManager().getDefaultDisplay();
		width = display.getWidth();
		height = display.getHeight();
	}

	public void initSkyBox()
	{
		Texture skyBoxFront = new Texture(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(main.getResources(),
				R.drawable.plain_sky_front), 256, 256, true), true);
		Texture skyBoxBack = new Texture(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(main.getResources(),
				R.drawable.plain_sky_back), 256, 256, true), true);
		Texture skyBoxLeft = new Texture(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(main.getResources(),
				R.drawable.plain_sky_left), 256, 256, true), true);
		Texture skyBoxRight = new Texture(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(main.getResources(),
				R.drawable.plain_sky_right), 256, 256, true), true);
		Texture skyBoxTop = new Texture(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(main.getResources(),
				R.drawable.plain_sky_top), 256, 256, true), true);

		texManager.addTexture("skybox_front", skyBoxFront);
		texManager.addTexture("skybox_back", skyBoxBack);
		texManager.addTexture("skybox_left", skyBoxLeft);
		texManager.addTexture("skybox_right", skyBoxRight);
		texManager.addTexture("skybox_top", skyBoxTop);
		texManager.addTexture("skybox_bottom", skyBoxTop);

		skyBox = new SkyBox("skybox_left", "skybox_front", "skybox_right", "skybox_back", "skybox_top", "skybox_bottom", 1.0f);
		skyBox.compile();
	}

	public void initGUI()
	{
		//analogIcon = new Texture(BitmapHelper.rescale(BitmapHelper.convert(main.getResources().getDrawable(R.drawable.analog)), 128, 128));
		//analogIcon = new Texture(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(main.getResources(), R.drawable.analog), 128, 128, true));
		jumpButton = new GUIButton(main, R.drawable.button, 256, 128);
		jumpButton.setPosition(width - jumpButton.icon.getWidth(), height - jumpButton.icon.getHeight());

		jumpButton.setOnTouchCallback(new OnTouchCallback() {
			@Override
			public Object call(GUIElement element, int posX, int posY) {
				if (world.hero.isOnTheGround(world.gamePhysics.dynamicsWorld)) {
					Vector3f velocity = new Vector3f(0, 0, 0);
					velocity = world.hero.rigidBody.getLinearVelocity(velocity);
					world.hero.rigidBody.setLinearVelocity(new Vector3f(velocity.x, jumpHeight, velocity.z));
				}
				return null;
			}
		});

		analogMovementController = new GUIController(main, R.drawable.controllercross, 256, 256, R.drawable.controllerstick, 64, 64, 0);
		analogCameraController = new GUIController(main, R.drawable.controllercross, 128, 128, R.drawable.controllerstick, 32, 32, 0);
		analogMovementController.setPosition(10, height / 4);
		analogCameraController.setPosition(width - 128, height/2 - 64);

		OnTouchCallback controllersUntouchCallback = new OnTouchCallback() {
			@Override
			public Object call(GUIElement element, int posX, int posY) {
				GUIController elementt = (GUIController) element;
				elementt.stickOffsetX = 0;
				elementt.stickOffsetY = 0;
				return null;
			}
		};

		analogMovementController.setOnUntouchCallback(controllersUntouchCallback);
		analogCameraController.setOnUntouchCallback(controllersUntouchCallback);

		OnTouchCallback controllersCallback = new OnTouchCallback() {
			@Override
			public Object call(GUIElement element, int posX, int posY) {
				GUIController elementt = (GUIController) element;
				int graphicOriginX = elementt.x;
				int graphicOriginY = elementt.y;

				int graphicOffsetX = posX - graphicOriginX;
				int graphicOffsetY = posY - graphicOriginY;

				int logicOffsetX = graphicOffsetX - elementt.icon.getWidth() / 2;
				int logicOffsetY = -(graphicOffsetY - elementt.icon.getHeight() / 2);

				double distanceFromOrigin = Math.sqrt(logicOffsetX*logicOffsetX + logicOffsetY*logicOffsetY);
				SimpleVector xAxisVersor = new SimpleVector(1, 0, 0);
				SimpleVector directionalVector = new SimpleVector(logicOffsetX, logicOffsetY, 0);
				

				double angle = directionalVector.calcAngle(xAxisVersor);
				if (distanceFromOrigin >= elementt.icon.getWidth()/2)
				{
					logicOffsetX = (int) (Math.cos(angle) * (elementt.icon.getWidth()/2));
					logicOffsetY = logicOffsetY < 0 ? -(int) (Math.sin(angle) * (elementt.icon.getHeight()/2)) : (int) (Math.sin(angle) * (elementt.icon.getHeight()/2));
				}

				elementt.stickOffsetX = logicOffsetX;
				elementt.stickOffsetY = logicOffsetY;
				return null;
			}
		};

		analogMovementController.setOnTouchCallback(controllersCallback);
		analogCameraController.setOnTouchCallback(controllersCallback);
	}

	public void initGameObjects()
	{
		/* */
		world = new GameWorld();
		world.graphicsWorld.setAmbientLight(50, 50, 100);
		world.sun = new Light(world.graphicsWorld);
		//world.sun.setIntensity(250, 250, 250);
		world.sun.setIntensity(90, 90, 90);
		world.gamePhysics.dynamicsWorld.setGravity(new Vector3f(0, 0, 0));
		WorldLoader.refillWorldWithString(world, "NULL");
		initGUI();
		initSkyBox();
		cam = world.graphicsWorld.getCamera();
		cam.setPosition(world.hero.object.getTransformedCenter());
		cam.moveCamera(Camera.CAMERA_MOVEOUT, camDistance);
		cam.lookAt(world.hero.object.getTransformedCenter());
		SimpleVector sv = new SimpleVector(0, -50, 0);
		world.sun.setPosition(sv);
		/* */
	}

	//to co dzieje sie logicznie w swiecie gry po uruchomieniu aplikacji
	public void Start() {
		initGameObjects();
	}

	//to sie dzieje co tick, co frame, zwykle odswiez
	public void Update(SparseArray<Pair<Boolean, Point>> touches, FrameBuffer fb) {
		/* */
		analogMovementController.checkClicks(touches);
		analogCameraController.checkClicks(touches);
		jumpButton.checkClicks(touches);

		world.updateObjects();

		//jezeli wypadniemy poza poziom, to ustaw gracza na pozycji poczatkowej
		if (world.hero.object.getTransformedCenter().y > 20)
		{
			//world.reinitObjects();
			WorldLoader.refillWorldWithString(world, "NULL");
		}
		float rotation = (float) analogCameraController.getNormalizedFactorX();
		float yrotation = (float) analogCameraController.getNormalizedFactorY();
		cam.setPosition(world.hero.object.getTransformedCenter());
		//cam.moveCamera(Camera.CAMERA_MOVEIN, camDistance);
		float trigLevel = 0.2f;
		cam.rotateY(Math.abs(rotation) > trigLevel ? (-rotation / camRotationalSlowness) : 0.0f);

		cam.moveCamera(Camera.CAMERA_MOVEOUT, camDistance);
		cam.moveCamera(Camera.CAMERA_MOVEUP, -yrotation);
		cam.lookAt(world.hero.object.getTransformedCenter());

		//wez kierunek kamery w przestrzeni swiata i rzutuj na plaszczyzne pozioma XZ
		SimpleVector cameraDirection = cam.getDirection();
		cameraDirection.y = 0;
		//float angleAbsoluteRotation = (cameraDirection.calcAngle(new SimpleVector(1, 0, 0)));
		SimpleVector xAxisVersor = new SimpleVector(1, 0, 0);
		float angleAbsoluteRotation = (float) Math.atan2( cameraDirection.x*xAxisVersor.z - cameraDirection.z*xAxisVersor.x,
				cameraDirection.x*xAxisVersor.x + cameraDirection.z*xAxisVersor.z );
		world.hero.setRotation(new SimpleVector(-angleAbsoluteRotation, 0 ,0));

		cameraDirection = cameraDirection.normalize();
		cameraDirection.scalarMul((int) (analogMovementController.getNormalizedFactorY() * movementForce));
		world.hero.rigidBody.applyForce(new Vector3f(cameraDirection.x, cameraDirection.y, -cameraDirection.z), new Vector3f(0, 0, 0));

		cam.rotateY((float)(-Math.PI/2.0f));
		SimpleVector perpendicularCameraDirection = cam.getDirection();
		cam.rotateY((float)(Math.PI/2.0f));
		perpendicularCameraDirection.y = 0;
		perpendicularCameraDirection = perpendicularCameraDirection.normalize();
		perpendicularCameraDirection.scalarMul((int) (analogMovementController.getNormalizedFactorX() * movementForce));
		world.hero.rigidBody.applyForce(new Vector3f(perpendicularCameraDirection.x, perpendicularCameraDirection.y, -perpendicularCameraDirection.z), new Vector3f(0, 0, 0));

		Vector3f velocity = new Vector3f(0, 0, 0);
		world.hero.rigidBody.getLinearVelocity(velocity);
		if (velocity.length() > maxMovementSpeed)
		{
			velocity.normalize();
			velocity.scale(maxMovementSpeed);
			world.hero.rigidBody.setLinearVelocity(velocity);
		}

		// nie dziel timestepu przez zbyt duza wartosc
		// bo symulacja nie zadziala (obliczenia zmiennoprzecinkowe)
		world.gamePhysics.stepSimulation(1 / 12.f, 10);

		jumpButton.render(fb);
		analogMovementController.render(fb);
		analogCameraController.render(fb);

		/* * /
		if (touchTurn != 0 || touchTurnUp != 0)
		{

			touchTurn = 0;
			touchTurnUp = 0;
		}
		/* */
	}
}
