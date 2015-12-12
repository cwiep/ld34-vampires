package de.cwiep.vampires;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Human extends Sprite {
    public enum HumanType {
        HUMAN, VAMPIRE, HUNTER
    };
    private ShapeRenderer renderer;
    private boolean isMoving;
    private float moveTimer;
    private Vector2 moveDirection;
    public HumanType humanType;

    public Human(int x, int y, HumanType type) {
        renderer = new ShapeRenderer();
        setBounds(x, y, 32, 64);
        isMoving = false;
        moveTimer = 0;
        moveDirection = new Vector2(0, 0);
        humanType = type;
    }

    public void draw(SpriteBatch batch, boolean vampireVision) {
        // drawing white rectangle as dummy
        renderer.setProjectionMatrix(batch.getProjectionMatrix());
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        if(vampireVision) {
            if (humanType == HumanType.HUNTER) {
                renderer.setColor(Color.RED);
            } else if (humanType == HumanType.VAMPIRE) {
                renderer.setColor(Color.CYAN);
            } else {
                renderer.setColor(Color.YELLOW);
            }
        } else {
            renderer.setColor(Color.GOLD);
        }
        renderer.rect(getX(), getY(), getWidth(), getHeight());
        renderer.end();
    }

    public void update(float dt) {
        moveTimer -= dt;
        if (moveTimer <= 0.0f) {
            moveTimer = MathUtils.random(0.5f, 2.0f);
            moveDirection.set(MathUtils.random(-20, 20), MathUtils.random(-20, 20));
        }
        this.translate(moveDirection.x * dt, moveDirection.y * dt);
        if(getX() <= 90 || getX() >= GameController.V_WIDTH - getWidth() - 90) {
            moveDirection.x *= -1;
        }
        if(getY() <= 0 || getY() >= GameController.V_HEIGHT / 2 - getHeight()) {
            moveDirection.y *= -1;
        }
    }
}
