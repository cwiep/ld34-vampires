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
        renderer.setColor(Color.RED);
        renderer.rect(getX()+5, getY()+5, getWidth()-10, getHeight()-10);
        renderer.end();
    }

    public void update(float dt) {
        moveTimer -= dt;
        if (moveTimer <= 0.0f) {
            moveTimer = MathUtils.random(0.5f, 2.0f);
            moveDirection.set(MathUtils.random(-20, 20), MathUtils.random(-20, 20));
        }
        this.translate(moveDirection.x * dt, moveDirection.y * dt);
        if(getX() <= 0 || getX() >= GameController.V_WIDTH - getWidth()) {
            moveDirection.x *= -1;
        }
        if(getY() <= 0 || getY() >= GameController.V_HEIGHT / 2 - getHeight()) {
            moveDirection.y *= -1;
        }
    }
}
