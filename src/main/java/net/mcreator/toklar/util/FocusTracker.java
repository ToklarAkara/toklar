package net.mcreator.toklar.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;
import java.util.UUID;

public class FocusTracker {

    private static class FocusData {
        public EntityLivingBase target;
        public int focusLevel;
        public long timestamp;

        public FocusData(EntityLivingBase target, int focusLevel) {
            this.target = target;
            this.focusLevel = focusLevel;
            this.timestamp = System.currentTimeMillis();
        }

        public long getDurationMillis() {
        	return net.mcreator.toklar.enchantments.EnchantmentFocus.getFocusDurationMillis(focusLevel);
        }
    }

    private static final HashMap<UUID, FocusData> focusedTargets = new HashMap<>();

    // Old method kept for backward compatibility (focus level 1 assumed)
    public static void setFocusTarget(EntityPlayer player, EntityLivingBase target) {
        setFocusTarget(player, target, 1);
    }

    // New method with focus level
    public static void setFocusTarget(EntityPlayer player, EntityLivingBase target, int focusLevel) {
        focusedTargets.put(player.getUniqueID(), new FocusData(target, focusLevel));
    }

    public static EntityLivingBase getFocusTarget(EntityPlayer player) {
        FocusData data = focusedTargets.get(player.getUniqueID());
        if (data == null) {
            System.out.println("[FocusTracker] No focus data for player " + player.getName());
            return null;
        }

        long elapsed = System.currentTimeMillis() - data.timestamp;
        if (elapsed > data.getDurationMillis()) {
            System.out.println("[FocusTracker] Focus expired for player " + player.getName());
            focusedTargets.remove(player.getUniqueID());
            return null;
        }

        System.out.println("[FocusTracker] Player " + player.getName() + " has active focus on " + data.target.getName());
        return data.target;
    }

    // Optionally get focus level if needed
    public static int getFocusLevel(EntityPlayer player) {
        FocusData data = focusedTargets.get(player.getUniqueID());
        return (data != null) ? data.focusLevel : 0;
    }
}
