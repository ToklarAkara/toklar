package net.mcreator.toklar.enchantments;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityCreature;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;

import net.mcreator.toklar.util.FocusTracker;
import net.mcreator.toklar.SummonDamageBuffHandler;
import net.mcreator.toklar.init.EnchantmentInit;
import net.mcreator.toklar.enchantments.EnchantmentFocus;

public class FocusRetargetHandler {

    @SubscribeEvent
    public void onMinionAttack(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        EntityLivingBase immediateSource = source.getImmediateSource() instanceof EntityLivingBase
                ? (EntityLivingBase) source.getImmediateSource()
                : null;
        if (immediateSource == null) return;

        EntityPlayer owner = SummonDamageBuffHandler.getOwnerFromEntity(immediateSource);
        if (owner == null) return;

        EntityLivingBase focusTarget = FocusTracker.getFocusTarget(owner);
        int focusLevel = FocusTracker.getFocusLevel(owner);
        if (focusTarget == null || focusLevel <= 0) return;

        EntityLivingBase target = event.getEntityLiving();

        // Apply bonus damage if minion is attacking the focus target
        if (target == focusTarget) {
            float bonusMultiplier = EnchantmentFocus.getBonusDamageMultiplier(focusLevel);
            float bonus = event.getAmount() * bonusMultiplier;
            event.setAmount(event.getAmount() + bonus);
            System.out.println("[FocusRetarget] Bonus damage applied: +" + bonus);
        } else {
            // Try retargeting to focus
            if (immediateSource instanceof EntityCreature) {
                ((EntityCreature) immediateSource).setAttackTarget(focusTarget);
                System.out.println("[FocusRetarget] Vanilla mob retargeted to focus.");
            } else if (immediateSource instanceof BaseCreatureEntity) {
                ((BaseCreatureEntity) immediateSource).setAttackTarget(focusTarget);
                System.out.println("[FocusRetarget] Lycanites mob retargeted to focus.");
            }
        }
    }
}
