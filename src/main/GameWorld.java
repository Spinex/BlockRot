package main;

import com.threed.jpct.Light;
import com.threed.jpct.World;

import java.util.ArrayList;

import javax.vecmath.Vector3f;

/**
 * Created by spx on 25.06.15.
 */
public class GameWorld {
    World graphicsWorld = null;
    Physics gamePhysics = null;

    float gravityForce = -25.0f;

    Light sun = null;
    ActiveGameObject hero = null;

    ArrayList<ActiveGameObject> staticObjects = new ArrayList<ActiveGameObject>();
    ArrayList<ActiveGameObject> rotationalObjects = new ArrayList<ActiveGameObject>();

    public GameWorld()
    {
        graphicsWorld = new World();
        gamePhysics = new Physics();
    }

    public void updateObjects()
    {
        hero.rigidBody.applyForce(new Vector3f(0, gravityForce, 0), new Vector3f(0, 0, 0));
        for (ActiveGameObject gameObject : rotationalObjects)
        {
            gameObject.rigidBody.setLinearVelocity(new Vector3f(0, 0, 0));
        }
    }

    public void reinitObjects()
    {
        hero.setPosition(hero.initialPosition);
        hero.setRotation(hero.initialRotation);
        for (ActiveGameObject gameObject : rotationalObjects)
        {
            gameObject.setPosition(gameObject.initialPosition);
            gameObject.setRotation(gameObject.initialRotation);
        }
    }

    public void clearActiveObjects()
    {
        for (ActiveGameObject gameObject : staticObjects)
        {
            gamePhysics.dynamicsWorld.removeCollisionObject(gameObject.rigidBody);
        }

        for (ActiveGameObject gameObject : rotationalObjects)
        {
            gamePhysics.dynamicsWorld.removeCollisionObject(gameObject.rigidBody);
        }

        if (hero != null)
            gamePhysics.dynamicsWorld.removeCollisionObject(hero.rigidBody);
        graphicsWorld.removeAllObjects();
        hero = null;
        staticObjects.clear();
        rotationalObjects.clear();
    }
}
