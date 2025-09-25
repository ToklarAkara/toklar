package net.mcreator.toklar.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class EnchantArcaneLeech extends Enchantment {

    public static EnchantArcaneLeech INSTANCE;

    public EnchantArcaneLeech() {
        super(Rarity.RARE, EnumEnchantmentType.WEAPON,
              new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
        this.setName("arcane_leech");
        INSTANCE = this;
    }
    @Override
    public boolean canApplyTogether(Enchantment ench) {
        if (ench == null) return true; // prevent NPE
        return super.canApplyTogether(ench) && ench != this;
    }


    @Override
    public int getMaxLevel() {
        return 3; // same scaling as Lifesteal
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return this.type.canEnchantItem(stack.getItem());
    }

    @Override
    public boolean canApply(ItemStack stack) {
        return this.type.canEnchantItem(stack.getItem());
    }

    @SubscribeEvent(priority = net.minecraftforge.fml.common.eventhandler.EventPriority.LOW)
    public static void onLivingHurt(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        if (source == null) return;

        // Only trigger on magic or indirectMagic damage
        String type = source.damageType;
        if (!"magic".equals(type) && !"indirectMagic".equals(type)) return;

        if (!(source.getTrueSource() instanceof EntityLivingBase)) return;
        EntityLivingBase attacker = (EntityLivingBase) source.getTrueSource();
        if (attacker == null) return;

        // Check mainhand and offhand for the enchantment
        int level = Math.max(
            EnchantmentHelper.getEnchantmentLevel(INSTANCE, attacker.getHeldItemMainhand()),
            EnchantmentHelper.getEnchantmentLevel(INSTANCE, attacker.getHeldItemOffhand())
        );
        if (level <= 0) return;

        if (event.getAmount() <= 1.0F) return; // ignore tiny chip damage

        // Heal: 3% of damage per level (same as Lifesteal)
        float heal = event.getAmount() * 0.03F * level;
        if (heal > 0) {
            attacker.heal(heal);
            attacker.world.playEvent(2007, attacker.getPosition(), 0); // heart particles
        }
    }
}