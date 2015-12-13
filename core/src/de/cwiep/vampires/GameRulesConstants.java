package de.cwiep.vampires;

public class GameRulesConstants {
    public static final int V_WIDTH = 640;
    public static final int V_HEIGHT = 480;

    public static final int PLAYFIELD_LEFT = 90;
    public static final int PLAYFIELD_RIGHT = V_WIDTH - 90;
    public static final int PLAYFIELD_TOP = V_HEIGHT / 2 - 20;
    public static final int PLAYFIELD_BOTTOM = 10;

    public static final int FULL_BLOOD_BAR_AMOUNT = 50;

    public static final float HUNTER_ENERGY_DRAIN = 30;
    public static final float VAMPIRE_ENERGY_DRAIN = 10;
    public static final float HUMAN_ENERGY_GAIN = 5;
    public static final float VISION_ENERGY_DRAIN = 25;

    public static int NUM_HUMANS = 10;
    public static int NUM_HUNTERS = 3;
}
