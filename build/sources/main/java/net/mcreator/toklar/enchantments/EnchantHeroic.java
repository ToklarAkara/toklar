package net.mcreator.toklar.enchantments;

import com.mujmajnkraft.bettersurvival.items.ItemCustomWeapon;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class EnchantHeroic extends Enchantment {

    public static EnchantHeroic INSTANCE;

    public EnchantHeroic() {
        super(Rarity.RARE, EnumEnchantmentType.ALL,
              new EntityEquipmentSlot[]{
                  EntityEquipmentSlot.MAINHAND,
                  EntityEquipmentSlot.OFFHAND,
                  EntityEquipmentSlot.HEAD,
                  EntityEquipmentSlot.CHEST
              });
        this.setName("heroic");
        INSTANCE = this;
    }

    @Override
    public int getMaxLevel() {
        return 1; // only one rank
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return isValidItem(stack);
    }

    @Override
    public boolean canApply(ItemStack stack) {
        return isValidItem(stack);
    }

    private boolean isValidItem(ItemStack stack) {
        return stack.getItem() instanceof ItemSword ||
               stack.getItem() instanceof ItemShield ||
               stack.getItem() instanceof ItemCustomWeapon ||
               (stack.getItem() instanceof ItemArmor &&
                (((ItemArmor) stack.getItem()).armorType == EntityEquipmentSlot.HEAD ||
                 ((ItemArmor) stack.getItem()).armorType == EntityEquipmentSlot.CHEST));
    }

        @Override
    public boolean canApplyTogether(Enchantment ench) {
        if (ench == null) return true; // prevent NPE
        return super.canApplyTogether(ench) && ench != this;
    }

    // -----------------------------------------

    @SubscribeEvent
    public static void onEntityKill(LivingDeathEvent event) {
        if (!(event.getSource().getTrueSource() instanceof EntityPlayer)) return;
        if (!(event.getEntity() instanceof EntityLivingBase)) return;

        EntityPlayer attacker = (EntityPlayer) event.getSource().getTrueSource();
        EntityLivingBase target = (EntityLivingBase) event.getEntity();

        int pieces = 0;

        // Mainhand
        if (EnchantmentHelper.getEnchantmentLevel(INSTANCE, attacker.getHeldItemMainhand()) > 0) {
            pieces++;
        }

        // Offhand
        if (EnchantmentHelper.getEnchantmentLevel(INSTANCE, attacker.getHeldItemOffhand()) > 0) {
            pieces++;
        }

        // Helmet
        ItemStack helmet = attacker.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        if (!helmet.isEmpty() && EnchantmentHelper.getEnchantmentLevel(INSTANCE, helmet) > 0) {
            pieces++;
        }

        // Chest
        ItemStack chest = attacker.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (!chest.isEmpty() && EnchantmentHelper.getEnchantmentLevel(INSTANCE, chest) > 0) {
            pieces++;
        }

        if (pieces <= 0) return;

        // Cap at 3 pieces max
        pieces = Math.min(pieces, 3);

        // Base heal: 5% of target HP, minimum 1
        int baseHeal = (int) Math.max(Math.floor(target.getMaxHealth() * 0.05F), 1.0D);

        // Flat bonus: +1 per enchanted piece (capped at 3)
        int restore = baseHeal + pieces;

        // Cap total heal at 20 HP (10 hearts)
        restore = Math.min(restore, 20);

        if (restore > 0 && attacker.getHealth() < attacker.getMaxHealth()) {
            attacker.heal(restore);
            attacker.world.playEvent(2007, attacker.getPosition(), 0); // heart particles
        }
    }
}