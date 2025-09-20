package net.mcreator.toklar.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.mcreator.toklar.config.ModConfig;


public class EnchantmentFocus extends Enchantment {

    public EnchantmentFocus() {
        // null is okay since we use custom logic
        super(Rarity.UNCOMMON, null, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
        this.setName("focus");
        this.setRegistryName("toklar:focus");
    }

    @Override
    public int getMinEnchantability(int level) {
        return 10 + (level - 1) * 15;
    }

    @Override
    public int getMaxEnchantability(int level) {
        return getMinEnchantability(level) + 20;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean canApply(ItemStack stack) {
        // Only allow if the item is enchantable AND not armor
        return stack.getItem().isEnchantable(stack)
            && !(stack.getItem() instanceof ItemArmor);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return canApply(stack);
    }

    @Override
    public boolean isAllowedOnBooks() {
        return true;
    }

    /** Returns bonus damage multiplier. Example: 0.10f = 10% */
    public static float getBonusDamageMultiplier(int level) {
        return ModConfig.getFocusBonusDamagePerLevel() * level;
    }

    /** Returns focus duration in milliseconds */
    public static long getFocusDurationMillis(int level) {
        return (long)(ModConfig.getFocusDurationSecondsPerLevel() * 1000L * level);
    }

    @Override
    public String getName() {
        return "enchantment.toklar.focus";
    }

}
