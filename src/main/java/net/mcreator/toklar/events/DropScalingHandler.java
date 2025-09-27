package net.mcreator.toklar.events;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.item.ItemStack;
import net.minecraft.world.GameRules;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DropScalingHandler {

    @SubscribeEvent
    public void onMobDrops(LivingDropsEvent event) {
        if (!(event.getEntityLiving() instanceof IMob)) return;

        // Read Scaling Health difficulty (assuming it's exposed as a game rule)
        int difficulty = event.getEntityLiving().world.getGameRules().getInt("ScalingHealthDifficulty");

        // Calculate multiplier
        float multiplier = 1.0F + (difficulty / 100F); // Example: difficulty 200 → 3x drops

        // Scale each drop
        for (EntityItem drop : event.getDrops()) {
            ItemStack stack = drop.getItem();
            stack.setCount(Math.round(stack.getCount() * multiplier));
        }
    }
}