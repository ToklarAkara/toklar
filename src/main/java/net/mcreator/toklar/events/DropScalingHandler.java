package net.mcreator.toklar.events;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.silentchaos512.scalinghealth.config.Config;

public class DropScalingHandler {

    @SubscribeEvent
    public void onMobDrops(LivingDropsEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
       // if (!(entity instanceof IMob)) return; #restore this later if farms are op

        // Get mob difficulty from NBT
        short difficulty = entity.getEntityData().getShort("scalinghealth:difficulty");

        // Apply XP boost as loot multiplier
        float multiplier = 1.0F + Config.Mob.xpBoost * difficulty;

        for (EntityItem drop : event.getDrops()) {
            ItemStack stack = drop.getItem();
            stack.setCount(Math.round(stack.getCount() * multiplier));
        }
    }
}