package com.github.naoboko;

public class GlobalSlotConstants {
    private static int BLUE_MULTIPLIER;
    private static int RED_MULTIPLIER;
    private static int BLACK_MULTIPLIER;
    private static int MAGENTA_MULTIPLIER;
    private static int LIME_MULTIPLIER;
    private static int PINK_MULTIPLIER;
    private static int YELLOW_MULTIPLIER;
    private static int LIGHT_BLUE_MULTIPLIER;

    public static int getBlueMultiplier() {
        return BLUE_MULTIPLIER;
    }
    public static void setBlueMultiplier(int blueMultiplier) {
        BLUE_MULTIPLIER = blueMultiplier;
    }

    public static int getRedMultiplier() {
        return RED_MULTIPLIER;
    }
    public static void setRedMultiplier(int redMultiplier) {
        RED_MULTIPLIER = redMultiplier;
    }

    public static int getBlackMultiplier() {
        return BLACK_MULTIPLIER;
    }
    public static void setBlackMultiplier(int blackMultiplier) {
        BLACK_MULTIPLIER = blackMultiplier;
    }

    public static int getMagentaMultiplier() {
        return MAGENTA_MULTIPLIER;
    }
    public static void setMagentaMultiplier(int magentaMultiplier) {
        MAGENTA_MULTIPLIER = magentaMultiplier;
    }

    public static int getLimeMultiplier() {
        return LIME_MULTIPLIER;
    }
    public static void setLimeMultiplier(int limeMultiplier) {
        LIME_MULTIPLIER = limeMultiplier;
    }

    public static int getPinkMultiplier() {
        return PINK_MULTIPLIER;
    }
    public static void setPinkMultiplier(int pinkMultiplier) {
        PINK_MULTIPLIER = pinkMultiplier;
    }

    public static int getYellowMultiplier() {
        return YELLOW_MULTIPLIER;
    }
    public static void setYellowMultiplier(int yellowMultiplier) {
        YELLOW_MULTIPLIER = yellowMultiplier;
    }

    public static int getLightBlueMultiplier() {
        return LIGHT_BLUE_MULTIPLIER;
    }
    public static void setLightBlueMultiplier(int lightBlueMultiplier) {
        LIGHT_BLUE_MULTIPLIER = lightBlueMultiplier;
    }

    public static void loadExist() {
        //todo ロードするわね
    }

    public synchronized static void save() {
        //todo configにsave
    }
}
