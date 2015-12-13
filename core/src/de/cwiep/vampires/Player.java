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

public class Player extends Sprite {

    private static final float ENERGY_CHANGE_DURATION = 1.0f;
    private static final float MOVE_TO_ENEMY_DURATION = 1.0f;

    private ShapeRenderer renderer;

    private float energy;
    private float drainEnergyCounter;
    private float targetEnergyLevel;
    private float energyChange;

    private float moveToEnemyCounter;
    private Vector2 moveToEnemyTarget;
    private boolean moveToEnemyFinished;
    private boolean facingRight;

    private boolean vampireVision;
    private boolean isAttacking;
    private boolean isGettingHit;

    Human selectedHuman;

    private TextureRegion mRegionHeat;
    private TextureRegion mRegionStand;
    private TextureRegion mRegionWalk;
    private TextureRegion mRegionHit;
    private Animation mAttackAnimation;

    public Player(TextureAtlas textureAtlas) {
        renderer = new ShapeRenderer();
        setBounds(GameRulesConstants.V_WIDTH / 2 - 32, GameRulesConstants.V_HEIGHT / 2 - 64, 32, 64);
        energy = GameRulesConstants.FULL_BLOOD_BAR_AMOUNT;
        drainEnergyCounter = 0.0f;
        targetEnergyLevel = 0;
        vampireVision = false;
        isAttacking = false;
        mRegionStand = textureAtlas.findRegion("player_stand");
        mRegionWalk = textureAtlas.findRegion("player_walk");
        mRegionHeat = textureAtlas.findRegion("vampire_heat");
        mRegionHit = textureAtlas.findRegion("player_hit");
        Array<TextureRegion> frames = new Array<TextureRegion>();
        for (int i = 1; i < 5; ++i) {
            frames.add(new TextureRegion(textureAtlas.findRegion("player_attacking"), i * 32, 0, 32, 64));
        }
        mAttackAnimation = new Animation(0.1f, frames);
        frames.clear();
    }

    public void update(float dt) {
        if (isAttacking) {
            updateAttack(dt);
        }
        if (vampireVision) {
            energy -= GameRulesConstants.VISION_ENERGY_DRAIN * dt;
        }

        setRegion(getFrame());
    }

    private TextureRegion getFrame() {
        TextureRegion region;

        if (isAttacking) {
            if (moveToEnemyCounter > 0) {
                region = mRegionWalk;
            } else if (isGettingHit) {
                region = mRegionHit;
            } else {
                region = mAttackAnimation.getKeyFrame(drainEnergyCounter, true);
            }
            if (!facingRight && !region.isFlipX() || facingRight && region.isFlipX()) {
                region.flip(true, false);
            }
        } else if (vampireVision) {
            region = mRegionHeat;
        } else {
            region = mRegionStand;
            if (!facingRight && !region.isFlipX() || facingRight && region.isFlipX()) {
                region.flip(true, false);
            }
        }

        return region;
    }

    public void draw(SpriteBatch batch) {
        super.draw(batch);
    }

    public void startAttack() {
        isAttacking = true;
        moveToEnemyCounter = MOVE_TO_ENEMY_DURATION;

        // attack will happen from left or right depending on where the human is on screen
        boolean humanLeftOfCenter = selectedHuman.getX() + selectedHuman.getWidth() / 2 <= GameRulesConstants.V_WIDTH / 2;
        float targetX = humanLeftOfCenter ? selectedHuman.getX() + selectedHuman.getWidth() - 10 : selectedHuman.getX() - getWidth() + 10;
        float targetY = selectedHuman.getY();
        facingRight = targetX > getX();
        moveToEnemyTarget = new Vector2(targetX, targetY).sub(getX(), getY());
        moveToEnemyFinished = false;
    }

    public void updateAttack(float dt) {
        if (!moveToEnemyFinished) {
            moveToEnemyCounter -= dt;
            translate(moveToEnemyTarget.x * dt / MOVE_TO_ENEMY_DURATION, moveToEnemyTarget.y * dt / MOVE_TO_ENEMY_DURATION);
            if (moveToEnemyCounter <= 0) {
                moveToEnemyFinished = true;
                startDraining();
            }
        } else {
            drainEnergyCounter -= dt;
            energy = MathUtils.clamp(energy + energyChange * dt / ENERGY_CHANGE_DURATION, 0, GameRulesConstants.FULL_BLOOD_BAR_AMOUNT);
            if (drainEnergyCounter <= 0) {
                energy = targetEnergyLevel;
                isAttacking = false;
                isGettingHit = false;
                if(selectedHuman.humanType == Human.HumanType.HUMAN) {
                    selectedHuman.humanType = Human.HumanType.VAMPIRE;
                }
                selectedHuman = null;
            }
        }
    }

    public void startDraining() {
        facingRight = selectedHuman.getX() > getX();
        if (selectedHuman.humanType == Human.HumanType.HUNTER) {
            targetEnergyLevel = energy - GameRulesConstants.HUNTER_ENERGY_DRAIN;
            energyChange = -GameRulesConstants.HUNTER_ENERGY_DRAIN;
            isGettingHit = true;
        } else if (selectedHuman.humanType == Human.HumanType.VAMPIRE) {
            // he already is a vampire and hurts you
            targetEnergyLevel = energy - GameRulesConstants.VAMPIRE_ENERGY_DRAIN;
            energyChange = -GameRulesConstants.VAMPIRE_ENERGY_DRAIN;
            isGettingHit = true;
        } else {
            // make vampire and gain a little energy
            targetEnergyLevel = MathUtils.clamp(energy + GameRulesConstants.HUMAN_ENERGY_GAIN, 0, GameRulesConstants.FULL_BLOOD_BAR_AMOUNT);
            energyChange = GameRulesConstants.HUMAN_ENERGY_GAIN;
        }
        drainEnergyCounter = ENERGY_CHANGE_DURATION;
    }

    public void toggleVision() {
        vampireVision = !vampireVision;
    }

    public boolean getVampireVisionActive() {
        return vampireVision;
    }

    public boolean isAttacking() {
        return isAttacking;
    }

    public float getEnergy() {
        return energy;
    }

    public Human getSelectedHuman() {
        return selectedHuman;
    }

    public void selectHuman(Human human) {
        selectedHuman = human;
    }

    public void dispose() {
        renderer.dispose();
    }
}
