package de.cwiep.vampires;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayScreen implements Screen {


    private GameController mGame;
    private OrthographicCamera mGameCam;
    private Hud mHud;
    private Player mPlayer;

    private boolean mGameOver;
    private boolean vampireVision;
    private float energy;

    private List<Human> humansList;

    public PlayScreen(GameController game) {
        mGame = game;
        mGameCam = new OrthographicCamera(GameController.V_WIDTH, GameController.V_HEIGHT);
        mHud = new Hud(mGame.batch);
        mPlayer = new Player();
        mGameOver = false;
        humansList = new ArrayList<Human>();
        for(int i=0; i<10; ++i) {
            int randx = MathUtils.random(10, GameController.V_WIDTH-50);
            int randy = MathUtils.random(10, GameController.V_HEIGHT/2);

            humansList.add(new Human(randx, randy));
        }
        vampireVision = false;
        energy = 100;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        update(delta);

        int clearColor = vampireVision ? 1 : 0;
        Gdx.gl.glClearColor(clearColor, clearColor, clearColor, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mPlayer.draw(mGame.batch, vampireVision);
        for(Human h : humansList) {
            h.draw(mGame.batch, vampireVision);
        }

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
        if(!vampireVision) {
            for (Human h : humansList) {
                h.update(dt);
            }
        }

        if(vampireVision) {
            energy -= 5 * dt;
        }
        mHud.setEnergyLevel(energy);

        if(energy <= 0) {
            mGameOver = true;
        }
    }

    private void handleInput(float dt) {
        // game status keys
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            //Gdx.app.exit();
            mGameOver = true;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            vampireVision = !vampireVision;
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
