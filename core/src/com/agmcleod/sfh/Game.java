package com.agmcleod.sfh;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ObjectMap;

import java.lang.reflect.Constructor;

public class Game extends ApplicationAdapter {
    static final float WORLD_TO_BOX = 0.01f;
    static final float BOX_TO_WORLD = 100f;
    World world;

    private ObjectMap<String, String> entities;
    private OrthographicCamera camera;
    private Matrix4 cameraCpy;
    private SpriteBatch batch;
    private Box2DDebugRenderer debugRenderer;

    private FollowCamera followCamera;

    private OrthogonalTiledMapRenderer mapRenderer;

    private Player player;
    TextureAtlas atlas;
    
    @Override
    public void create () {
        batch = new SpriteBatch();
        atlas = new TextureAtlas("atlas.txt");
        world = new World(new Vector2(0, -5), true);

        world.setContactListener(new CollisionListener());

        debugRenderer = new Box2DDebugRenderer();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cameraCpy = camera.combined.cpy();

        TiledMap map = new TmxMapLoader().load("intro.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        MapBodyBuilder.buildShapes(map, world);

        entities = new ObjectMap<String, String>();
        entities.put("player", "com.agmcleod.sfh.Player");

        MapProperties properties = map.getProperties();
        int width = properties.get("width", Integer.class);
        int height = properties.get("height", Integer.class);

        int tileWidth = properties.get("tilewidth", Integer.class);
        int tileHeight = properties.get("tileheight", Integer.class);

        Rectangle mapBounds = new Rectangle(0, 0, width * tileWidth, height * tileHeight);

        player = (Player) ObjectMapToClass.getInstanceOfObject(entities, "player", this);
        followCamera = new FollowCamera(camera, player.getPosition(), mapBounds);
    }

    @Override
    public void dispose() {
        mapRenderer.dispose();
        atlas.dispose();
    }

    @Override
    public void render () {
        update();

        cameraCpy.set(camera.combined);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        mapRenderer.render();

        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        player.render(batch);
        batch.end();
        debugRenderer.render(world, cameraCpy.scl(BOX_TO_WORLD));
        world.step(1 / 60f, 6, 2);
    }

    public void update() {
        player.update();
        followCamera.update();
        camera.update();
        mapRenderer.setView(camera);
    }
}
