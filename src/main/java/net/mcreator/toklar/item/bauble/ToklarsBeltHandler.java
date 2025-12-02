package net.mcreator.toklar.item.bauble;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.mcreator.toklar.SummonDamageBuffHandler;
import net.mcreator.toklar.config.ModConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;
import baubles.api.BaublesApi;

import java.util.HashMap;
import java.util.UUID;

public class ToklarsBeltHandler {

    // Track cooldowns per player
    private static final HashMap<UUID, Long> lastHealTick = new HashMap<>();

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        Entity sourceEntity = event.getSource().getTrueSource();
        if (sourceEntity == null) return;

        EntityPlayer owner = SummonDamageBuffHandler.resolveValidSummonOwner(event.getSource());
        if (owner == null) return;

        if (BaublesApi.isBaubleEquipped(owner, ItemToklarsGirdle.item) != -1) {
            if (ModConfig.toklarsBeltUseCooldown) {
                // Cooldown mode
                long now = owner.world.getTotalWorldTime();
                long last = lastHealTick.getOrDefault(owner.getUniqueID(), 0L);

                if (now - last >= ModConfig.toklarsBeltCooldownTicks) {
                    owner.heal(ModConfig.toklarsBeltHealAmount);
                    lastHealTick.put(owner.getUniqueID(), now);
                }
            } else {
                // Flat heal per hit mode
                owner.heal(ModConfig.toklarsBeltHealAmount);
            }
        }
    }
}