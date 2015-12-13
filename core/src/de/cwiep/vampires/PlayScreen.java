package de.cwiep.vampires;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PlayScreen implements Screen {

    private static final float BLOOD_BAR_PIXEL_WIDTH = 200;

    private GameController mGame;
    private OrthographicCamera mGameCam;
    private Viewport mViewport;
    private Player mPlayer;

    private List<Human> humansList;
    private ShapeRenderer renderer;

    private TextureAtlas mTextureAtlas;

    public PlayScreen(GameController game) {
        mGame = game;
        mGameCam = new OrthographicCamera();
        mViewport = new FitViewport(GameController.V_WIDTH, GameController.V_HEIGHT, mGameCam);
        mGameCam.setToOrtho(false, mViewport.getWorldWidth(), mViewport.getWorldHeight());
        renderer = new ShapeRenderer();
        mTextureAtlas = new TextureAtlas("vampires.pack");
        mPlayer = new Player(mTextureAtlas);

        initHumansAndHunters();
    }

    private void initHumansAndHunters() {
        humansList = new ArrayList<Human>();
        for (int i = 0; i < GameRulesConstants.NUM_HUMANS; ++i) {
            int randx = MathUtils.random(90, GameController.V_WIDTH - 50 - 90);
            int randy = MathUtils.random(10, GameController.V_HEIGHT / 2);

            humansList.add(new Human(randx, randy, Human.HumanType.HUMAN, mTextureAtlas));
        }
        for (int i = 0; i < GameRulesConstants.NUM_HUNTERS; ++i) {
            int randx = MathUtils.random(90, GameController.V_WIDTH - 50 - 90);
            int randy = MathUtils.random(10, GameController.V_HEIGHT / 2);

            humansList.add(new Human(randx, randy, Human.HumanType.HUNTER, mTextureAtlas));
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        sortHumansByYCoordinate();
        update(delta);

        // TODO: remove when actual background
        int clearColor = mPlayer.getVampireVisionActive() ? 1 : 0;
        Gdx.gl.glClearColor(clearColor, clearColor, clearColor, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mPlayer.draw(mGame.batch, mPlayer.getVampireVisionActive());

        mGame.batch.begin();
        if (mPlayer.isAttacking()) {
            mPlayer.getSelectedHuman().draw(mGame.batch, mPlayer.getVampireVisionActive());
        } else {
            for (Human h : humansList) {
                h.draw(mGame.batch, mPlayer.getVampireVisionActive());
            }
        }
        mGame.batch.end();

        renderer.setProjectionMatrix(mGame.batch.getProjectionMatrix());

        drawSelectionIndicator();
        drawBloodMeter();

        //drawWindowCorners();

        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            drawAndroidController();
        }

        mGame.batch.setProjectionMatrix(mGameCam.combined);

        checkWinCondition(delta);
        checkGameOverCondition();
    }

    private void drawWindowCorners() {
        // for debugging camera and viewport
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Color.BLUE);
        renderer.rect(0, 0, 20, 20);
        renderer.rect(0, GameController.V_HEIGHT - 20, 20, 20);
        renderer.rect(GameController.V_WIDTH - 20, GameController.V_HEIGHT - 20, 20, 20);
        renderer.rect(GameController.V_WIDTH - 20, 0, 20, 20);
        renderer.end();
    }

    private void drawSelectionIndicator() {
        Human selectedHuman = mPlayer.getSelectedHuman();
        if (selectedHuman != null) {
            renderer.begin(ShapeRenderer.ShapeType.Filled);
            renderer.setColor(Color.RED);
            renderer.triangle(selectedHuman.getX(), selectedHuman.getY() + selectedHuman.getHeight() + 20,
                    selectedHuman.getX() + selectedHuman.getWidth(), selectedHuman.getY() + selectedHuman.getHeight() + 20,
                    selectedHuman.getX() + selectedHuman.getWidth() / 2, selectedHuman.getY() + selectedHuman.getHeight() + 5);
            renderer.end();
        }
    }

    private void drawBloodMeter() {
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        float rectx = GameController.V_WIDTH / 2 - BLOOD_BAR_PIXEL_WIDTH / 2;
        float recty = GameController.V_HEIGHT - 40;
        float rectw = BLOOD_BAR_PIXEL_WIDTH * mPlayer.getEnergy() / GameRulesConstants.FULL_BLOOD_BAR_AMOUNT;

        //background
        renderer.setColor(Color.DARK_GRAY);
        renderer.rect(rectx, recty, BLOOD_BAR_PIXEL_WIDTH, 20);

        //bloodmeter
        renderer.setColor(Color.RED);
        renderer.rect(rectx, recty, rectw, 20);

        renderer.end();
    }

    private void drawAndroidController() {
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(new Color(0xaaaaaa99));
        renderer.circle(GameController.V_WIDTH - 45, 45, 40);
        renderer.circle(45, 45, 40);
        renderer.end();
    }

    private void checkWinCondition(float delta) {
        // count remaining humans for win condition
        boolean humansShouldMove = !mPlayer.getVampireVisionActive() && !mPlayer.isAttacking();
        int numHumans = 0;
        for (Human h : humansList) {
            h.update(delta, mPlayer.getVampireVisionActive(), humansShouldMove);
            if (h.humanType == Human.HumanType.HUMAN) {
                ++numHumans;
            }
        }
        if (humansShouldMove) {
            // only check win condition, if all animations have finished
            if (numHumans == 0) {
                mGame.setScreen(new WinScreen(mGame));
                dispose();
            }
        }
    }

    private void checkGameOverCondition() {
        if (mPlayer.getEnergy() <= 0) {
            mGame.setScreen(new GameOverScreen(mGame));
            dispose();
        }
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
        mPlayer.update(dt);
    }

    private void handleInput(float dt) {
        if (mPlayer.isAttacking()) {
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            mPlayer.toggleVision();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.W) && mPlayer.getSelectedHuman() != null) {
            mPlayer.startAttack();
        }
        if (Gdx.input.justTouched()) {
            int touchX = Gdx.input.getX();
            int touchY = Gdx.input.getY();
            Vector3 touchPoint = new Vector3(touchX, touchY, 0);
            mViewport.unproject(touchPoint);
            boolean onAndroid = Gdx.app.getType() == Application.ApplicationType.Android;
            boolean androidTouchVision = new Circle(45, 45, 40).contains(touchPoint.x, touchPoint.y);
            boolean androidTouchAttack = new Circle(GameController.V_WIDTH - 45, 45, 40).contains(touchPoint.x, touchPoint.y);
            if (androidTouchVision && onAndroid) {
                mPlayer.toggleVision();
            } else if (androidTouchAttack && mPlayer.getSelectedHuman() != null && onAndroid) {
                mPlayer.startAttack();
            } else {
                handleHumanSelection(touchPoint);
            }
        }

        // player.handleInput(mController, dt, getNextInteractionObject());
    }

    private void handleHumanSelection(Vector3 touchPoint) {
        // at this point humansList is sorted by y decreasing.
        // we want the "topmost" human at touchpoint x,y, meaning the one with smallest y
        mPlayer.selectHuman(null);
        for (int i = 0; i < humansList.size(); ++i) {
            if (humansList.get(i).getBoundingRectangle().contains(touchPoint.x, touchPoint.y)) {
                mPlayer.selectHuman(humansList.get(i));
            }
        }
    }

    @Override
    public void resize(int width, int height) {
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
        for (Human h : humansList) {
            h.dispose();
        }
        renderer.dispose();
        mPlayer.dispose();
        mTextureAtlas.dispose();
    }
}
