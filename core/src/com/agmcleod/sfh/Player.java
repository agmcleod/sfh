package com.agmcleod.sfh;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

/**
 * Created by aaronmcleod on 15-02-27.
 */
public class Player {
    private static final int FRAME_COLS = 4;
    private static final int FRAME_ROWS = 3;
    private static final float VEL_X = 3;

    private BodyDef playerDef;
    private Body playerBody;

    private float stateTime;
    private Animation walkAnimation;
    private TextureRegion[] walkFrames;
    private Vector2 position;
    private boolean processAnimation;
    private boolean flip;

    public Player(Game game, float x, float y) {
        TextureAtlas.AtlasRegion region = game.atlas.findRegion("player");
        TextureRegion[][] tmp = region.split(region.getRegionWidth() / FRAME_COLS, region.getRegionHeight() / FRAME_ROWS);
        position = new Vector2(50, 50);
        walkFrames = new TextureRegion[]{
                tmp[0][0], tmp[0][1], tmp[0][2], tmp[0][3],
                tmp[1][0], tmp[1][1], tmp[1][2], tmp[1][3],
                tmp[2][0], tmp[2][1]
        };

        walkAnimation = new Animation(0.025f, walkFrames);
        flip = false;

        playerDef = new BodyDef();
        playerDef.type = BodyDef.BodyType.DynamicBody;
        playerDef.position.set((position.x + 16) * Game.WORLD_TO_BOX, (position.y + 16) * Game.WORLD_TO_BOX);
        playerBody = game.world.createBody(playerDef);
        playerBody.setFixedRotation(true);

        PolygonShape playerShape = new PolygonShape();
        playerShape.setAsBox(16 * Game.WORLD_TO_BOX, 16 * Game.WORLD_TO_BOX);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = playerShape;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 0f;

        Fixture fixture = playerBody.createFixture(fixtureDef);

        playerShape.dispose();
    }

    public Vector2 getPosition() {
        return this.position;
    }

    public void render(SpriteBatch batch) {
        TextureRegion region;
        if (processAnimation) {
            region = walkAnimation.getKeyFrame(stateTime, true);
        }
        else {
            region = walkAnimation.getKeyFrame(0, true);
        }

        if (flip) {
            batch.draw(region, position.x, position.y, -32, 32);
        }
        else {
            batch.draw(region, position.x - 32, position.y, 32, 32);
        }
    }

    public void update() {
        stateTime += Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            playerBody.setLinearVelocity(-VEL_X, playerBody.getLinearVelocity().y);
            flip = true;
            processAnimation = true;
        }

        else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            playerBody.setLinearVelocity(VEL_X, playerBody.getLinearVelocity().y);
            flip = false;
            processAnimation = true;
        }
        else {
            playerBody.setLinearVelocity(0, playerBody.getLinearVelocity().y);
            processAnimation = false;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && playerBody.getLinearVelocity().y <= 0) {
            playerBody.applyForceToCenter(0, 10, false);
        }


        position.set(playerBody.getPosition().x * Game.BOX_TO_WORLD + 16, playerBody.getPosition().y * Game.BOX_TO_WORLD - 16);
    }


}
