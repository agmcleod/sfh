package com.agmcleod.sfh;

import com.badlogic.gdx.physics.box2d.*;

/**
 * Created by aaronmcleod on 15-03-10.
 */
public class CollisionListener implements ContactListener {

    public CollisionListener() {
        super();

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        if(fixtureA.getUserData() != null
                && ((GameObject) fixtureA.getUserData()).name.equals("player")
                && fixtureB.getUserData() != null
                && ((GameObject) fixtureB.getUserData()).name.equals("mapCollision")) {
            Player player = ((Player) fixtureA.getUserData());
            player.setJumping(false);
        }

        if(fixtureA.getUserData() != null
                && ((GameObject) fixtureA.getUserData()).name.equals("mapCollision")
                && fixtureB.getUserData() != null
                && ((GameObject) fixtureB.getUserData()).name.equals("player")) {
            Player player = ((Player) fixtureB.getUserData());
            player.setJumping(false);
        }
    }
}
