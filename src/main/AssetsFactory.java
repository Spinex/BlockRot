package main;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.threed.jpct.SimpleVector;

/**
 * Created by spx on 30.06.15.
 */
public class AssetsFactory {
    static public ActiveGameObject getHero(GameWorld world, SimpleVector position)
    {
        //ActiveGameObject result = PhysicsFactory.getBox("hero", new SimpleVector(6,12,6), position, new SimpleVector(0,0,0), world, 18, 1.1f, 0.2f, 0, 0);
        ActiveGameObject result = PhysicsFactory.getBox("hero", new SimpleVector(6,12,6), position, new SimpleVector(0,0,0), world, 18, 1.1f, 0.2f, 0, 0);
        result.object.setAdditionalColor(100, 100, 0);
        result.rigidBody.setAngularFactor(0.0f);
        result.rigidBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
        return result;
    }

    static public ActiveGameObject getStatic(GameWorld world, SimpleVector position, SimpleVector rotation, SimpleVector scale)
    {
        ActiveGameObject result = PhysicsFactory.getStaticBox("static", scale,
                position, rotation, world, 1.0f);
        return result;
    }

    static public ActiveGameObject getRotational(GameWorld world, SimpleVector position, SimpleVector rotation, SimpleVector scale, float mass)
    {
        ActiveGameObject result = PhysicsFactory.getBox("rotational", scale,
                position, rotation, world, mass, 0.0f, 0.0f, 0, 0 );

        result.object.setAdditionalColor(0, 0, 255);
        //result.rigidBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
        return result;
    }
}
