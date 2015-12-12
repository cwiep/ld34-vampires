package de.cwiep.vampires;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Hud implements Disposable {
    public Stage mStage;

    private Label mLabel;
    private float mEnergy;

    public Hud(SpriteBatch spriteBatch, Viewport viewport) {
        mStage = new Stage(viewport, spriteBatch);

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        mEnergy = 100;
        mLabel = new Label(String.format("Energy: %.2f", mEnergy), new Label.LabelStyle(new BitmapFont(), Color.RED));
        table.add(mLabel).padTop(10).padLeft(10);
        table.add().expandX();

        mStage.addActor(table);
    }

    @Override
    public void dispose() {
        mStage.dispose();
    }

    public void setEnergyLevel(float energy) {
        mEnergy = energy;
        mLabel.setText(String.format("Energy: %.2f", mEnergy));
    }

}
