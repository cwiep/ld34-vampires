package de.cwiep.vampires;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

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

    private boolean vampireVision;
    private boolean isAttacking;

    Human selectedHuman;

    private TextureRegion mRegionHeat;

    public Player(TextureAtlas textureAtlas) {
        renderer = new ShapeRenderer();
        setBounds(GameController.V_WIDTH / 2 - 32, GameController.V_HEIGHT / 2 - 64, 32, 64);
        energy = GameRulesConstants.FULL_BLOOD_BAR_AMOUNT;
        drainEnergyCounter = 0.0f;
        targetEnergyLevel = 0;
        vampireVision = false;
        isAttacking = false;
        mRegionHeat = textureAtlas.findRegion("vampire_heat");
    }

    public void update(float dt) {
        if (isAttacking) {
            updateAttack(dt);
        }

        if (vampireVision) {
            energy -= GameRulesConstants.VISION_ENERGY_DRAIN * dt;
        }
    }

    public void draw(SpriteBatch batch, boolean vampireVision) {
        if(vampireVision) {
            setRegion(mRegionHeat);
            batch.begin();
            super.draw(batch);
            batch.end();
        } else {
            // drawing white rectangle as dummy
            renderer.setProjectionMatrix(batch.getProjectionMatrix());
            renderer.begin(ShapeRenderer.ShapeType.Filled);
            renderer.setColor(vampireVision ? Color.BLACK : Color.WHITE);
            renderer.rect(getX(), getY(), getWidth(), getHeight());
            renderer.end();
        }
    }

    public void startAttack() {
        isAttacking = true;
        moveToEnemyCounter = MOVE_TO_ENEMY_DURATION;

        // attack will happen from left or right depending on where the human is on screen
        boolean humanLeftOfCenter = selectedHuman.getX() + selectedHuman.getWidth() / 2 <= GameController.V_WIDTH / 2;
        float targetX = humanLeftOfCenter ? selectedHuman.getX() + selectedHuman.getWidth() + 5 : selectedHuman.getX() - 5 - getWidth();
        float targetY = selectedHuman.getY();
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
                selectedHuman = null;
            }
        }
    }

    public void startDraining() {
        if (selectedHuman.humanType == Human.HumanType.HUNTER) {
            targetEnergyLevel = energy - GameRulesConstants.HUNTER_ENERGY_DRAIN;
            energyChange = -GameRulesConstants.HUNTER_ENERGY_DRAIN;
        } else if (selectedHuman.humanType == Human.HumanType.VAMPIRE) {
            // he already is a vampire and hurts you
            targetEnergyLevel = energy - GameRulesConstants.VAMPIRE_ENERGY_DRAIN;
            energyChange = -GameRulesConstants.VAMPIRE_ENERGY_DRAIN;
        } else {
            // make vampire and gain a little energy
            selectedHuman.humanType = Human.HumanType.VAMPIRE;
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
