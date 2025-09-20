package net.mcreator.toklar.enchantments;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.mcreator.toklar.init.EnchantmentInit;
import net.mcreator.toklar.util.FocusTracker;
import net.minecraft.enchantment.EnchantmentHelper;

public class FocusEnchantmentHandler {

    @SubscribeEvent
    public void onPlayerAttack(AttackEntityEvent event) {
        System.out.println("[FocusEnchantmentHandler] AttackEntityEvent fired for player " + event.getEntityPlayer().getName());
    	EntityPlayer player = event.getEntityPlayer();
        ItemStack heldItem = player.getHeldItemMainhand();

        if (heldItem.isEmpty()) return;

        int focusLevel = EnchantmentHelper.getEnchantmentLevel(EnchantmentInit.FOCUS, heldItem);
        System.out.println("[FocusEnchantmentHandler] Focus level on held item: " + focusLevel);
        if (focusLevel > 0 && event.getTarget() instanceof EntityLivingBase) {
            EntityLivingBase target = (EntityLivingBase) event.getTarget();
            FocusTracker.setFocusTarget(player, target, focusLevel);
        }
    }
}
