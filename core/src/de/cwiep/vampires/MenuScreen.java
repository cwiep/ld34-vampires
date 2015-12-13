package de.cwiep.vampires;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MenuScreen implements Screen {

    GameController mGame;
    Viewport mViewport;
    Stage mStage;

    Texture titleTexture;
    Texture tutorialTexture;

    public MenuScreen(GameController game) {
        mGame = game;
        mViewport = new FitViewport(GameRulesConstants.V_WIDTH, GameRulesConstants.V_HEIGHT, new OrthographicCamera());
        mStage = new Stage(mViewport, (game.batch));

        Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.BLACK);

        Table table = new Table();
        table.center();
        table.setFillParent(true);

        Label playAgainLabel = new Label("Press any key to start", font);
        table.add(playAgainLabel).expandX();
        mStage.addActor(table);
        titleTexture = new Texture("title.png");
        tutorialTexture = new Texture("howto.png");
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mGame.batch.begin();
        mGame.batch.draw(titleTexture, GameRulesConstants.V_WIDTH / 2 - titleTexture.getWidth() / 2, GameRulesConstants.V_HEIGHT - titleTexture.getHeight() - 20);
        mGame.batch.draw(tutorialTexture, 0, 0);
        mGame.batch.end();

        mStage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        if (Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
            mGame.setScreen((new PlayScreen(mGame)));
            dispose();
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
        mStage.dispose();
        titleTexture.dispose();
        tutorialTexture.dispose();
    }
}
