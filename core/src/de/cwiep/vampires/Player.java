package de.cwiep.vampires;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Player extends Sprite {
    private ShapeRenderer renderer;
    public Player() {
        renderer = new ShapeRenderer();
        setBounds(GameController.V_WIDTH / 2 - 32, GameController.V_HEIGHT / 2 - 64, 32, 64);
    }

    public void draw(SpriteBatch batch, boolean vampireVision) {
        // drawing white rectangle as dummy
        renderer.setProjectionMatrix(batch.getProjectionMatrix());
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(vampireVision ? Color.BLACK : Color.WHITE);
        renderer.rect(getX(), getY(), getWidth(), getHeight());
        renderer.end();
    }
}
