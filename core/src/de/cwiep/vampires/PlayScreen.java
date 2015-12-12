package de.cwiep.vampires;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class PlayScreen implements Screen {


    private GameController mGame;
    private OrthographicCamera mGameCam;
    private Hud mHud;

    private boolean mGameOver;

    public PlayScreen(GameController game) {
        mGame = game;
        mGameCam = new OrthographicCamera(GameController.V_WIDTH, GameController.V_HEIGHT);
        mHud = new Hud(mGame.batch);

        mGameOver = false;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mGame.batch.setProjectionMatrix(mGameCam.combined);
        mGame.batch.begin();
        // player.draw(mGame.batch);
        mGame.batch.end();

        mGame.batch.setProjectionMatrix(mHud.mStage.getCamera().combined);
        mHud.mStage.draw();

        if (mGameOver) {
            mGame.setScreen(new GameOverScreen(mGame));
            dispose();
        }
        /*if (player.getHasFinishedLevel()) {
            mGame.setScreen(new WinScreen(mGame));
            dispose();
        }*/
    }

    public void update(float dt) {
        handleInput(dt);
        mGameCam.update();
        // player.update(dt);

        mHud.setEnergyLevel(100);
    }

    private void handleInput(float dt) {
        // game status keys
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            //Gdx.app.exit();
            mGameOver = true;
        }

        // player.handleInput(mController, dt, getNextInteractionObject());
    }

    @Override
    public void resize(int width, int height) {
        mHud.mStage.getViewport().update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        mHud.dispose();
    }
}
