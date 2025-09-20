package net.mcreator.toklar.enchantments;

import java.util.UUID;

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
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EnchantReach extends Enchantment {

    private static final UUID REACH_BOOST_UUID = UUID.fromString("12345678-1234-1234-1234-123456789abc");
    private static final String NBT_REACH_ON = "reachon";

    public EnchantReach() {
    	super(Rarity.VERY_RARE, EnumEnchantmentType.ARMOR_CHEST, new EntityEquipmentSlot[]{EntityEquipmentSlot.CHEST});
        this.setRegistryName("block_reach");
        this.setName("block_reach");
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

    private void turnReachOn(EntityPlayer player) {
        player.getEntityData().setBoolean(NBT_REACH_ON, true);
        IAttributeInstance reachAttr = player.getAttributeMap().getAttributeInstanceByName("generic.reachDistance");
        if (reachAttr != null) {
            reachAttr.removeModifier(REACH_BOOST_UUID);
            AttributeModifier modifier = new AttributeModifier(REACH_BOOST_UUID, "EnchantReachBoost", 1.0, 1);
            reachAttr.applyModifier(modifier);
        }
    }

    private void turnReachOff(EntityPlayer player) {
        player.getEntityData().setBoolean(NBT_REACH_ON, false);
        IAttributeInstance reachAttr = player.getAttributeMap().getAttributeInstanceByName("generic.reachDistance");
        if (reachAttr != null) {
            reachAttr.removeModifier(REACH_BOOST_UUID);
        }
    }

    @SubscribeEvent
    public void onEntityUpdate(LivingUpdateEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer)) return;

        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        ItemStack armor = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        int level = 0;

        if (!armor.isEmpty() &&
            EnchantmentHelper.getEnchantments(armor) != null &&
            EnchantmentHelper.getEnchantments(armor).containsKey(this)) {
            level = EnchantmentHelper.getEnchantments(armor).get(this);
        }

        if (level > 0) {
            turnReachOn(player);
        } else if (player.getEntityData().hasKey(NBT_REACH_ON) && player.getEntityData().getBoolean(NBT_REACH_ON)) {
            turnReachOff(player);
        }
    }
}