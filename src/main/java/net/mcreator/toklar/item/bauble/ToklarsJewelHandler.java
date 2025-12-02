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

public class ToklarsJewelHandler {

    // Track cooldowns per player
    private static final HashMap<UUID, Long> lastHealTick = new HashMap<>();

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        Entity sourceEntity = event.getSource().getTrueSource();
        if (sourceEntity == null) return;

        EntityPlayer owner = SummonDamageBuffHandler.resolveValidSummonOwner(event.getSource());
        if (owner == null) return;

        if (BaublesApi.isBaubleEquipped(owner, ItemToklarsJewel.item) != -1) {
            if (ModConfig.toklarsJewelUseCooldown) {
                // Cooldown mode
                long now = owner.world.getTotalWorldTime();
                long last = lastHealTick.getOrDefault(owner.getUniqueID(), 0L);

                if (now - last >= ModConfig.toklarsJewelCooldownTicks) {
                    owner.heal(ModConfig.toklarsJewelHealAmount);
                    lastHealTick.put(owner.getUniqueID(), now);
                }
            } else {
                // Flat heal per hit mode
                owner.heal(ModConfig.toklarsJewelHealAmount);
            }
        }
    }
}