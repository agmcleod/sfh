package com.agmcleod.sfh;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapImageLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ObjectMap;

import java.lang.reflect.Constructor;
import java.util.Iterator;

public class Game extends ApplicationAdapter {
    final float WORLD_TO_BOX = 0.01f;
    final float BOX_TO_WORLD = 100f;
    final float VIRTUAL_HEIGHT = 5f; // 5 meters
    World world;

    private ObjectMap<String, String> entities;
    private OrthographicCamera camera;
    private Matrix4 cameraCpy;
    private TextureRegion backgroundImage;
    private SpriteBatch batch;
    private MapBodyBuilder bodyBuilder;
    private Box2DDebugRenderer debugRenderer;

    private FollowCamera followCamera;

    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;

    private Player player;
    TextureAtlas atlas;
    
    @Override
    public void create () {
        batch = new SpriteBatch();
        atlas = new TextureAtlas("atlas.txt");
        world = new World(new Vector2(0, -9.81f), true); // use earth gravity.

        world.setContactListener(new CollisionListener());

        debugRenderer = new Box2DDebugRenderer();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cameraCpy = camera.combined.cpy();

        bodyBuilder = new MapBodyBuilder(this, world);

        loadLevel("intro.tmx");
    }

    @Override
    public void dispose() {
        mapRenderer.dispose();
        atlas.dispose();
    }

    public void loadLevel(String name) {
        map = new TmxMapLoader().load(name);
        mapRenderer = new OrthogonalTiledMapRenderer(map);
        bodyBuilder.buildShapes(map, world);

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

        TiledMapImageLayer background = (TiledMapImageLayer) map.getLayers().get("background");
        backgroundImage = background.getTextureRegion();
    }

    @Override
    public void render() {
        update();

        cameraCpy.set(camera.combined);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(backgroundImage, (camera.position.x - camera.viewportWidth / 2) * 0.7f, (camera.position.y - camera.viewportHeight / 2) * 0.7f);
        batch.end();

        renderMap();
        batch.begin();
        player.render(batch);
        batch.end();
        debugRenderer.render(world, cameraCpy.scl(BOX_TO_WORLD));
        world.step(1 / 60f, 6, 2);
    }

    public void renderMap() {
        TiledMapImageLayer imageLayer = (TiledMapImageLayer) map.getLayers().get("background");
        mapRenderer.getBatch().begin();

        for (MapLayer layer : map.getLayers()) {
            if (layer.isVisible() && !layer.getName().equals("collision")) {
                if (layer instanceof TiledMapTileLayer) {
                    mapRenderer.renderTileLayer((TiledMapTileLayer) layer);
                } else {
                    mapRenderer.renderObjects(layer);
                }
            }
        }
        mapRenderer.getBatch().end();
    }

    public void update() {
        player.update();
        followCamera.update();
        camera.update();
        mapRenderer.setView(camera);
    }
}
