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
    private Animation mHumanAttack;
    private TextureRegion mRegionHumanHeat;
    private TextureRegion mRegionVampireHeat;
    private TextureRegion mRegionHunterHeat;

    private float attackTimer;

    public Human(int x, int y, HumanType type, TextureAtlas textureAtlas, boolean isMale) {
        super(textureAtlas.findRegion("human_1_walk"));
        String walkfile, attackfile, scarefile;
        if(isMale) {
            walkfile = "human_1_walk";
            attackfile = "human_1_attacking";
            scarefile = "human_1_scared";
        } else {
            walkfile = "human_2_walk";
            attackfile = "human_2_attacking";
            scarefile = "human_2_scared";
        }
        renderer = new ShapeRenderer();
        setBounds(x, y, 32, 64);
        moveTimer = 0;
        moveDirection = new Vector2(0, 0);
        humanType = type;

        Array<TextureRegion> frames = new Array<TextureRegion>();
        for (int i = 1; i < 4; ++i) {
            frames.add(new TextureRegion(textureAtlas.findRegion(walkfile), i * 32, 0, 32, 64));
        }
        mHumanWalk = new Animation(0.1f, frames);
        frames.clear();
        for (int i = 1; i < 3; ++i) {
            frames.add(new TextureRegion(textureAtlas.findRegion(attackfile), i * 32, 0, 32, 64));
        }
        mHumanAttack = new Animation(0.2f, frames);
        frames.clear();
        mRegionStand = new TextureRegion(textureAtlas.findRegion(walkfile), 0, 0, 32, 64);
        mRegionScared = textureAtlas.findRegion(scarefile);
        mRegionHumanHeat = textureAtlas.findRegion("human_heat");
        mRegionHunterHeat = textureAtlas.findRegion("hunter_heat");
        mRegionVampireHeat = textureAtlas.findRegion("vampire_heat");
    }

    public void draw(SpriteBatch batch) {
        super.draw(batch);
    }

    public void update(float dt, boolean vampireVision, boolean attackHappening, int playerX) {
        if (!vampireVision && !attackHappening) {
            attackTimer = 0;
            moveTimer -= dt;
            if (moveTimer <= 0.0f) {
                moveTimer = MathUtils.random(0.5f, 2.0f);
                moveDirection.set(MathUtils.random(-20, 20), MathUtils.random(-20, 20));
            }
            this.translate(moveDirection.x * dt, moveDirection.y * dt);
            if (getX() <= GameRulesConstants.PLAYFIELD_LEFT || getX() >= GameRulesConstants.PLAYFIELD_RIGHT - getWidth()) {
                moveDirection.x *= -1;
            }
            if (getY() <= GameRulesConstants.PLAYFIELD_BOTTOM || getY() >= GameRulesConstants.PLAYFIELD_TOP - getHeight()) {
                moveDirection.y *= -1;
            }
        } else if(attackHappening) {
            attackTimer += dt;
        }

        setRegion(getFrame(vampireVision, attackHappening, playerX));
    }

    private TextureRegion getFrame(boolean vampireVision, boolean attackHappening, int playerX) {
        TextureRegion region;

        if (attackHappening) {
            if(humanType == HumanType.HUMAN) {
                region = mRegionScared;
            } else {
                region = mHumanAttack.getKeyFrame(attackTimer, true);
            }
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

        if(attackHappening) {
            boolean lookRight = playerX > getX();
            if (lookRight && region.isFlipX()) {
                region.flip(true, false);
            } else if (!lookRight && !region.isFlipX()) {
                region.flip(true, false);
            }
        } else {
            if (moveDirection.x < 0 && !region.isFlipX()) {
                region.flip(true, false);
            } else if (moveDirection.x >= 0 && region.isFlipX()) {
                region.flip(true, false);
            }
        }

        return region;
    }

    public void dispose() {
        renderer.dispose();
    }
}
