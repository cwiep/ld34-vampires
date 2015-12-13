package de.cwiep.vampires;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Human extends Sprite {
    public enum HumanType {
        HUMAN, VAMPIRE, HUNTER
    }

    private ShapeRenderer renderer;
    private float moveTimer;
    private Vector2 moveDirection;
    public HumanType humanType;

    private TextureRegion mRegionStand;
    private TextureRegion mRegionScared;
    private Animation mHumanWalk;
    private TextureRegion mRegionHumanHeat;
    private TextureRegion mRegionVampireHeat;
    private TextureRegion mRegionHunterHeat;

    public Human(int x, int y, HumanType type, TextureAtlas textureAtlas) {
        super(textureAtlas.findRegion("human_1_walk"));
        renderer = new ShapeRenderer();
        setBounds(x, y, 32, 64);
        moveTimer = 0;
        moveDirection = new Vector2(0, 0);
        humanType = type;

        Array<TextureRegion> frames = new Array<TextureRegion>();
        for (int i = 1; i < 4; ++i) {
            frames.add(new TextureRegion(textureAtlas.findRegion("human_1_walk"), i * 32, 0, 32, 64));
        }
        mHumanWalk = new Animation(0.1f, frames);
        frames.clear();
        mRegionStand = new TextureRegion(textureAtlas.findRegion("human_1_walk"), 0, 0, 32, 64);
        mRegionScared = textureAtlas.findRegion("human_1_scared");
        mRegionHumanHeat = textureAtlas.findRegion("human_heat");
        mRegionHunterHeat = textureAtlas.findRegion("hunter_heat");
        mRegionVampireHeat = textureAtlas.findRegion("vampire_heat");
    }

    public void draw(SpriteBatch batch, boolean vampireVision) {
        super.draw(batch);
        // drawing white rectangle as dummy
        /*renderer.setProjectionMatrix(batch.getProjectionMatrix());
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        if (vampireVision) {
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
        renderer.end();*/
    }

    public void update(float dt, boolean vampireVision, boolean attackHappening) {
        if (!vampireVision && !attackHappening) {
            moveTimer -= dt;
            if (moveTimer <= 0.0f) {
                moveTimer = MathUtils.random(0.5f, 2.0f);
                moveDirection.set(MathUtils.random(-20, 20), MathUtils.random(-20, 20));
            }
            this.translate(moveDirection.x * dt, moveDirection.y * dt);
            if (getX() <= 90 || getX() >= GameRulesConstants.V_WIDTH - getWidth() - 90) {
                moveDirection.x *= -1;
            }
            if (getY() <= 0 || getY() >= GameRulesConstants.V_HEIGHT / 2 - getHeight()) {
                moveDirection.y *= -1;
            }
        }

        setRegion(getFrame(vampireVision, attackHappening));
    }

    private TextureRegion getFrame(boolean vampireVision, boolean attackHappening) {
        TextureRegion region;

        if (attackHappening) {
            region = mRegionScared;
        } else if (vampireVision) {
            if (humanType == HumanType.VAMPIRE) {
                region = mRegionVampireHeat;
            } else if (humanType == HumanType.HUNTER) {
                region = mRegionHunterHeat;
            } else {
                region = mRegionHumanHeat;
            }
        } else if (moveTimer > 0) {
            region = mHumanWalk.getKeyFrame(moveTimer, true);
        } else {
            region = mRegionStand;
        }

        if (moveDirection.x < 0 && !region.isFlipX()) {
            region.flip(true, false);
        } else if (moveDirection.x >= 0 && region.isFlipX()) {
            region.flip(true, false);
        }

        return region;
    }

    public void dispose() {
        renderer.dispose();
    }
}
