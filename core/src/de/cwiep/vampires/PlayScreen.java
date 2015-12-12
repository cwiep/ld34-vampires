package de.cwiep.vampires;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PlayScreen implements Screen {

    public static int NUM_HUMANS = 10;

    private GameController mGame;
    private OrthographicCamera mGameCam;
    private Viewport mViewport;
    private Hud mHud;
    private Player mPlayer;

    private boolean mGameOver;
    private boolean vampireVision;
    private float energy;

    private List<Human> humansList;
    private Human selectedHuman;
    private ShapeRenderer renderer;

    public PlayScreen(GameController game) {
        mGame = game;
        mGameCam = new OrthographicCamera();
        mViewport = new FitViewport(GameController.V_WIDTH, GameController.V_HEIGHT, mGameCam);
        mHud = new Hud(mGame.batch, mViewport);
        mPlayer = new Player();
        mGameOver = false;
        humansList = new ArrayList<Human>();
        for (int i = 0; i < NUM_HUMANS; ++i) {
            int randx = MathUtils.random(10, GameController.V_WIDTH - 50);
            int randy = MathUtils.random(10, GameController.V_HEIGHT / 2);

            humansList.add(new Human(randx, randy));
        }
        vampireVision = false;
        energy = 100;
        renderer = new ShapeRenderer();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        sortHumansByYCoordinate();
        update(delta);

        int clearColor = vampireVision ? 1 : 0;
        Gdx.gl.glClearColor(clearColor, clearColor, clearColor, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mPlayer.draw(mGame.batch, vampireVision);

        for (Human h : humansList) {
            h.draw(mGame.batch, vampireVision);
        }

        if(selectedHuman != null) {
            renderer.setProjectionMatrix(mGame.batch.getProjectionMatrix());
            renderer.begin(ShapeRenderer.ShapeType.Filled);
            renderer.setColor(Color.RED);
            renderer.triangle(selectedHuman.getX(), selectedHuman.getY() + selectedHuman.getHeight() + 20,
                    selectedHuman.getX() + selectedHuman.getWidth(), selectedHuman.getY() + selectedHuman.getHeight() + 20,
                    selectedHuman.getX() + selectedHuman.getWidth() / 2, selectedHuman.getY() + selectedHuman.getHeight() + 5);
            renderer.end();
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

    private void sortHumansByYCoordinate() {
        Collections.sort(humansList, new Comparator<Human>() {
            @Override
            public int compare(Human human, Human other) {
                // sort list by y-coord descending
                return (int) (other.getY() - human.getY());
            }
        });
    }

    public void update(float dt) {
        handleInput(dt);
        mGameCam.update();
        // player.update(dt);
        if (!vampireVision) {
            for (Human h : humansList) {
                h.update(dt);
            }
        }

        if (vampireVision) {
            energy -= 5 * dt;
        }
        mHud.setEnergyLevel(energy);

        if (energy <= 0) {
            mGameOver = true;
        }
    }

    private void handleInput(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            vampireVision = !vampireVision;
        }
        if(Gdx.input.justTouched()) {
            handleHumanSelection(Gdx.input.getX(), Gdx.input.getY());
        }

        // player.handleInput(mController, dt, getNextInteractionObject());
    }

    private void handleHumanSelection(int touchx, int touchy) {
        Vector3 touchPoint = new Vector3(touchx, touchy, 0);
        mViewport.unproject(touchPoint);

        // at this point humansList is sorted by y decreasing.
        // we want the "topmost" human at touchpoint x,y, meaning the one with smallest y
        selectedHuman = null;
        for(int i=0; i<humansList.size();++i) {
            if(humansList.get(i).getBoundingRectangle().contains(touchPoint.x, touchPoint.y)) {
                selectedHuman = humansList.get(i);
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        mHud.mStage.getViewport().update(width, height);
        mViewport.update(width, height);
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
