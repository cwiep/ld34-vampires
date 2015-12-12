package de.cwiep.vampires;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Human extends Sprite {
    private ShapeRenderer renderer;
    private boolean isMoving;
    private float moveTimer;
    private Vector2 moveDirection;

    public Human(int x, int y) {
        renderer = new ShapeRenderer();
        setBounds(x, y, 32, 64);
        isMoving = false;
        moveTimer = 0;
        moveDirection = new Vector2(0, 0);
    }

    public void draw(SpriteBatch batch, boolean vampireVision) {
        // drawing white rectangle as dummy
        renderer.setProjectionMatrix(batch.getProjectionMatrix());
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(vampireVision ? Color.CYAN : Color.YELLOW);
        renderer.rect(getX(), getY(), getWidth(), getHeight());
        renderer.end();
    }

    public void update(float dt) {
        moveTimer -= dt;
        if (moveTimer <= 0.0f) {
            moveTimer = MathUtils.random(0.5f, 5.0f);
            moveDirection.set(MathUtils.random(-1, 1), MathUtils.random(-1, 1));
        }
        this.translate(moveDirection.x, moveDirection.y);
        setX(MathUtils.clamp(getX(), 0, GameController.V_WIDTH - getWidth()));
        setY(MathUtils.clamp(getY(), 0, GameController.V_HEIGHT / 2));
    }
}
