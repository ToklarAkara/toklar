package net.mcreator.toklar.config;

public class ModConfig {
    // Enable debug messages for SummonDamageBuffHandler
    public static boolean enableSummonDamageBuffDebug = true;

    // Damage multiplier for summoned creatures wearing bronze armor; range: 1.0 to 100.0
    private static float summonDamageMultiplier = 10.0F;

    // Damage multiplier for summoned creatures wearing toklar armor; range: 1.0 to 100.0
    private static float toklarSummonDamageMultiplier = 20.0F;  // example default

    public static float getSummonDamageMultiplier() {
        return summonDamageMultiplier;
    }

    public static void setSummonDamageMultiplier(float multiplier) {
        if (multiplier < 1.0F) multiplier = 1.0F;
        else if (multiplier > 100.0F) multiplier = 100.0F;
        summonDamageMultiplier = multiplier;
    }

    public static float getToklarSummonDamageMultiplier() {
        return toklarSummonDamageMultiplier;
    }

    public static void setToklarSummonDamageMultiplier(float multiplier) {
        if (multiplier < 1.0F) multiplier = 1.0F;
        else if (multiplier > 100.0F) multiplier = 100.0F;
        toklarSummonDamageMultiplier = multiplier;
    }
 // Bonus damage per level of Focus enchantment (e.g., 0.1 = 10%)
    public static float focusBonusDamagePerLevel = 0.10F;

    // Focus duration in seconds per level (e.g., 5 = 5 seconds per level)
    public static float focusDurationSecondsPerLevel = 5.0F;

    public static float getFocusBonusDamagePerLevel() {
        return focusBonusDamagePerLevel;
    }

    public static void setFocusBonusDamagePerLevel(float bonus) {
        if (bonus < 0.0F) bonus = 0.0F;
        if (bonus > 10.0F) bonus = 10.0F;  // sane cap
        focusBonusDamagePerLevel = bonus;
    }

    public static float getFocusDurationSecondsPerLevel() {
        return focusDurationSecondsPerLevel;
    }

    public static void setFocusDurationSecondsPerLevel(float duration) {
        if (duration < 0.0F) duration = 0.0F;
        if (duration > 600.0F) duration = 600.0F;  // max 10 minutes
        focusDurationSecondsPerLevel = duration;
    }
}
