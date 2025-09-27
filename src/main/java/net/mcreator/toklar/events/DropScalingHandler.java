package net.mcreator.toklar.events;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.silentchaos512.scalinghealth.config.Config;

public class DropScalingHandler {

    @SubscribeEvent
    public void onMobDrops(LivingDropsEvent event) {
        if (!(event.getEntityLiving() instanceof IMob)) return;

        // Apply XP boost directly as loot multiplier
        float multiplier = 1.0F + Config.Mob.xpBoost;

        for (EntityItem drop : event.getDrops()) {
            ItemStack stack = drop.getItem();
            stack.setCount(Math.round(stack.getCount() * multiplier));
        }
    }
}