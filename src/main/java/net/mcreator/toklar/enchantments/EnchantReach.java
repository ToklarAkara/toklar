package net.mcreator.toklar.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.UUID;

@Mod.EventBusSubscriber
public class EnchantReach extends Enchantment {

    private static final UUID REACH_MODIFIER_UUID = UUID.fromString("e3a1f6e0-8c2d-4f9b-9a3e-1b2d3c4f5a6b");
    private static final double REACH_BONUS = 3.0;

    public static EnchantReach INSTANCE;

    public EnchantReach() {
        super(Rarity.VERY_RARE, EnumEnchantmentType.ARMOR_CHEST,
              new EntityEquipmentSlot[]{EntityEquipmentSlot.CHEST});
        this.setName("block_reach");
        INSTANCE = this;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean canApply(ItemStack stack) {
        return stack.getItem() instanceof ItemArmor &&
               ((ItemArmor) stack.getItem()).armorType == EntityEquipmentSlot.CHEST;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return canApply(stack);
    }

    private static void applyModifier(EntityPlayer player) {
        if (player.world.isRemote) return;

        IAttributeInstance reachAttr = player.getAttributeMap().getAttributeInstanceByName("generic.reachDistance");
        if (reachAttr == null) return;

        if (reachAttr.getModifier(REACH_MODIFIER_UUID) == null) {
            AttributeModifier modifier = new AttributeModifier(
                REACH_MODIFIER_UUID,
                "ReachEnchantBonus",
                REACH_BONUS,
                0 // for direct addition
            );
            reachAttr.applyModifier(modifier);
            System.out.println("[ReachEnchant] Applied +3 reach");
        }
    }

    private static void removeModifier(EntityPlayer player) {
        if (player.world.isRemote) return;

        IAttributeInstance reachAttr = player.getAttributeMap().getAttributeInstanceByName("generic.reachDistance");
        if (reachAttr == null) return;

        if (reachAttr.getModifier(REACH_MODIFIER_UUID) != null) {
            reachAttr.removeModifier(reachAttr.getModifier(REACH_MODIFIER_UUID));
                    System.out.println("[ReachEnchant] Removed +3 reach");
        }
    }

    @SubscribeEvent
    public static void onEntityUpdate(LivingUpdateEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer)) return;

        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        ItemStack armor = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);

        int level = 0;
        if (!armor.isEmpty() &&
            EnchantmentHelper.getEnchantments(armor) != null &&
            EnchantmentHelper.getEnchantments(armor).containsKey(INSTANCE)) {
            level = EnchantmentHelper.getEnchantments(armor).get(INSTANCE);
        }

        if (level > 0) {
            applyModifier(player);
        } else {
            removeModifier(player);
        }
    }
}