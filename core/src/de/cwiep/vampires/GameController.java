package de.cwiep.vampires;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameController extends Game {
    public SpriteBatch batch;
    private AssetManager mAssetManager;

    @Override
    public void create() {
        batch = new SpriteBatch();
        Gdx.graphics.setTitle("LD34 - Vampires suck!");
        //mAssetManager = new AssetManager();
        //mAssetManager.load("loop.ogg", Music.class);

        //setScreen(new PlayScreen(this));
        setScreen(new MenuScreen(this));
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
