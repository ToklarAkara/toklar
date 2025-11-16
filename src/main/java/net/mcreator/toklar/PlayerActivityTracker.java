package net.mcreator.toklar;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class PlayerActivityTracker {

    private static final Map<UUID, Long> lastActiveTicks = new HashMap<>();

    private PlayerActivityTracker() {}

    /** Mark a player as active at the current world tick */
    public static void markActive(EntityPlayer player) {
        lastActiveTicks.put(player.getUniqueID(), player.world.getTotalWorldTime());
    }

    /** Get the last tick when the player was active */
    public static long getLastActiveTick(EntityPlayer player) {
        return lastActiveTicks.getOrDefault(player.getUniqueID(), 0L);
    }

    // Hook into Forge events to automatically mark activity
    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent event) {
        markActive(event.getEntityPlayer());
    }

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        markActive(event.getEntityPlayer());
    }
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        markActive(event.getPlayer());
    }

    @SubscribeEvent
    public static void onItemUse(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            markActive((EntityPlayer) event.getEntityLiving());
        }
    }

}