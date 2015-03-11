package com.agmcleod.sfh;

import com.badlogic.gdx.physics.box2d.*;

/**
 * Created by aaronmcleod on 15-03-10.
 */
public class CollisionListener implements ContactListener {
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
                && fixtureA.getUserData().equals("player")
                && fixtureB.getUserData() != null
                && fixtureB.getUserData().equals("mapCollision")) {

        }

        if(fixtureB.getUserData() != null
                && fixtureB.getUserData().equals("player")
                && fixtureA.getUserData() != null
                && fixtureA.getUserData().equals("mapCollision")){

        }
    }
}
