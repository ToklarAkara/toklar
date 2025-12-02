package net.mcreator.toklar.config;

public class ModConfig {
    // Enable debug messages for SummonDamageBuffHandler
    public static boolean enableSummonDamageBuffDebug = true;

    // Damage multiplier for summoned creatures wearing bronze armor; range: 1.0 to 100.0
    private static float summonDamageMultiplier = 10.0F;

    // Damage multiplier for summoned creatures wearing toklar armor; range: 1.0 to 100.0
    private static float toklarSummonDamageMultiplier = 20.0F;  // example default

    // Bonus damage per level of Focus enchantment (e.g., 0.1 = 10%)
    public static float focusBonusDamagePerLevel = 0.10F;

    // Focus duration in seconds per level (e.g., 5 = 5 seconds per level)
    public static float focusDurationSecondsPerLevel = 5.0F;

    // --- NEW CONFIG OPTIONS ---
    // Max distance in blocks for summon attribution
    public static int summonOwnerMaxDistance = 32;

    // Idle timeout in ticks for AFK guard (default 600 = 30s)
    public static int summonOwnerIdleTimeoutTicks = 600;

    // Existing getters/setters
    public static float getSummonDamageMultiplier() { return summonDamageMultiplier; }
    public static void setSummonDamageMultiplier(float multiplier) {
        if (multiplier < 1.0F) multiplier = 1.0F;
        else if (multiplier > 100.0F) multiplier = 100.0F;
        summonDamageMultiplier = multiplier;
    }
    public static float toklarsBeltDamageReduction = 0.8F; // default = no reduction
    
    public static float toklarsJewelHealAmount = 2.0F; // default heal per valid hit
    // Toggle: use cooldown instead of per-hit healing
    public static boolean toklarsJewelUseCooldown = false; // default off

    // Cooldown duration in ticks (20 ticks = 1 second)
    public static int toklarsJewelCooldownTicks = 40; // default = 2 seconds
    
    public static float getToklarSummonDamageMultiplier() { return toklarSummonDamageMultiplier; }
    public static void setToklarSummonDamageMultiplier(float multiplier) {
        if (multiplier < 1.0F) multiplier = 1.0F;
        else if (multiplier > 100.0F) multiplier = 100.0F;
        toklarSummonDamageMultiplier = multiplier;
    }

    public static float getFocusBonusDamagePerLevel() { return focusBonusDamagePerLevel; }
    public static void setFocusBonusDamagePerLevel(float bonus) {
        if (bonus < 0.0F) bonus = 0.0F;
        if (bonus > 10.0F) bonus = 10.0F;  // sane cap
        focusBonusDamagePerLevel = bonus;
    }

    public static float getFocusDurationSecondsPerLevel() { return focusDurationSecondsPerLevel; }
    public static void setFocusDurationSecondsPerLevel(float duration) {
        if (duration < 0.0F) duration = 0.0F;
        if (duration > 600.0F) duration = 600.0F;  // max 10 minutes
        focusDurationSecondsPerLevel = duration;
    }
}