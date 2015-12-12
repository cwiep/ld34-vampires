package de.cwiep.vampires;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameController extends Game {
    public static final int V_WIDTH = 720;
    public static final int V_HEIGHT = 480;

    public SpriteBatch batch;
    private AssetManager mAssetManager;

    @Override
    public void create() {
        batch = new SpriteBatch();
        //mAssetManager = new AssetManager();
        //mAssetManager.load("loop.ogg", Music.class);

        setScreen(new PlayScreen(this));
        //setScreen(new MenuScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        //mAssetManager.dispose();
    }

    public AssetManager getAssetManager() {
        return mAssetManager;
    }
}
