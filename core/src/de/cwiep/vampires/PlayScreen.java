package de.cwiep.vampires;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PlayScreen implements Screen {

    private static final float ENERGY_CHANGE_DURATION = 2.0f;

    public static int NUM_HUMANS = 10;
    public static int NUM_HUNTERS = 3;

    private GameController mGame;
    private OrthographicCamera mGameCam;
    private Viewport mViewport;
    private Hud mHud;
    private Player mPlayer;

    private boolean mGameOver;
    private boolean vampireVision;
    private boolean isAttacking;
    private float energy;

    private float drainEnergyCounter;
    private float targetEnergyLevel;
    private float energyChange;

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

            humansList.add(new Human(randx, randy, Human.HumanType.HUMAN));
        }
        for (int i = 0; i < NUM_HUNTERS; ++i) {
            int randx = MathUtils.random(10, GameController.V_WIDTH - 50);
            int randy = MathUtils.random(10, GameController.V_HEIGHT / 2);

            humansList.add(new Human(randx, randy, Human.HumanType.HUNTER));
        }
        vampireVision = false;
        isAttacking = false;
        energy = 100;
        renderer = new ShapeRenderer();
        drainEnergyCounter = 0.0f;
        targetEnergyLevel = 0;
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

        if (isAttacking) {
            selectedHuman.draw(mGame.batch, vampireVision);
        } else {
            for (Human h : humansList) {
                h.draw(mGame.batch, vampireVision);
            }
        }

        if (selectedHuman != null) {
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

        if (isAttacking) {
            drainEnergyCounter -= dt;
            energy = MathUtils.clamp(energy + energyChange * dt / ENERGY_CHANGE_DURATION, 0, 100);
            if (drainEnergyCounter <= 0) {
                energy = targetEnergyLevel;
                isAttacking = false;
                selectedHuman = null;
            }
        }

        if (vampireVision) {
            energy -= 10 * dt;
        }
        mHud.setEnergyLevel(energy);

        // count remaining humans for win condition
        if (!vampireVision && !isAttacking) {
            int numHumans = 0;
            for (Human h : humansList) {
                h.update(dt);
                if(h.humanType == Human.HumanType.HUMAN) {
                    ++numHumans;
                }
            }

            if (energy <= 0) {
                mGameOver = true;
            }

            if(numHumans == 0) {
                mGame.setScreen(new WinScreen(mGame));
                dispose();
            }
        }
    }

    private void handleInput(float dt) {
        if (isAttacking) {
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            vampireVision = !vampireVision;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.W) && selectedHuman != null) {
            startAttack();
        }
        if (Gdx.input.justTouched()) {
            handleHumanSelection(Gdx.input.getX(), Gdx.input.getY());
        }

        // player.handleInput(mController, dt, getNextInteractionObject());
    }

    private void startAttack() {
        isAttacking = true;
        // attack will happen from left or right depending on where the human is on screen
        boolean humanLeftOfCenter = selectedHuman.getX() + selectedHuman.getWidth() / 2 <= GameController.V_WIDTH / 2;
        // TODO: move player to human
        mPlayer.setPosition(humanLeftOfCenter ? selectedHuman.getX() + selectedHuman.getWidth() + 5 : selectedHuman.getX() - 5 - mPlayer.getWidth(), selectedHuman.getY());

        if (selectedHuman.humanType == Human.HumanType.HUNTER) {
            targetEnergyLevel = energy - 30;
            energyChange = -30;
        } else if (selectedHuman.humanType == Human.HumanType.VAMPIRE) {
            // he already is a vampire and hurts you
            targetEnergyLevel = energy - 10;
            energyChange = -10;
        } else {
            // make vampire and gain a little energy
            selectedHuman.humanType = Human.HumanType.VAMPIRE;
            targetEnergyLevel = MathUtils.clamp(energy + 10, 0, 100);
            energyChange = 10;
        }
        drainEnergyCounter = ENERGY_CHANGE_DURATION;
    }

    private void handleHumanSelection(int touchx, int touchy) {
        Vector3 touchPoint = new Vector3(touchx, touchy, 0);
        mViewport.unproject(touchPoint);

        // at this point humansList is sorted by y decreasing.
        // we want the "topmost" human at touchpoint x,y, meaning the one with smallest y
        selectedHuman = null;
        for (int i = 0; i < humansList.size(); ++i) {
            if (humansList.get(i).getBoundingRectangle().contains(touchPoint.x, touchPoint.y)) {
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
