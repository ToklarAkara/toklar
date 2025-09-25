package net.mcreator.toklar.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class EnchantReach extends Enchantment {

    private static final String NBT_REACH_ON = "reachon";
    private static final String NBT_ORIGINAL_REACH = "reach_original";
    private static final String NBT_LAST_APPLIED = "reach_last_applied";
    private static final double EPS = 1.0e-6;

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

    private static boolean approxEq(double a, double b) {
        return Math.abs(a - b) <= EPS;
    }

    private static void applyOrMaintain(EntityPlayer player) {
        if (player.world.isRemote) return;

        IAttributeInstance reachAttr = player.getAttributeMap().getAttributeInstanceByName("generic.reachDistance");
        if (reachAttr == null) return;

        boolean active = player.getEntityData().getBoolean(NBT_REACH_ON);
        double currentBase = reachAttr.getBaseValue();
        double lastApplied = player.getEntityData().hasKey(NBT_LAST_APPLIED)
                ? player.getEntityData().getDouble(NBT_LAST_APPLIED) : -1.0;

        if (!active) {
            // First-time equip: save underlying base and double once.
            player.getEntityData().setDouble(NBT_ORIGINAL_REACH, currentBase);
            double newBase = currentBase * 2.0;
            reachAttr.setBaseValue(newBase);
            player.getEntityData().setDouble(NBT_LAST_APPLIED, newBase);
            player.getEntityData().setBoolean(NBT_REACH_ON, true);
            System.out.println("[ReachEnchant] Applied: orig=" + currentBase + " new=" + newBase);
            return;
        }

        // Already active:
        // If the base equals the last value we applied, do nothing (stable).
        if (approxEq(currentBase, lastApplied)) {
            return;
        }

        // External change detected (race/other mod changed base).
        // Treat the current base as the new underlying base and reapply doubling exactly once.
        double newUnderlying = currentBase;
        double newBase = newUnderlying * 2.0;
        reachAttr.setBaseValue(newBase);
        player.getEntityData().setDouble(NBT_ORIGINAL_REACH, newUnderlying);
        player.getEntityData().setDouble(NBT_LAST_APPLIED, newBase);
        System.out.println("[ReachEnchant] Reapplied after external change: newOrig=" + newUnderlying + " new=" + newBase);
    }

    private static void removeSafely(EntityPlayer player) {
        if (player.world.isRemote) return;

        IAttributeInstance reachAttr = player.getAttributeMap().getAttributeInstanceByName("generic.reachDistance");
        if (reachAttr == null) return;

        boolean active = player.getEntityData().getBoolean(NBT_REACH_ON);
        if (!active) return;

        double savedOrig = player.getEntityData().hasKey(NBT_ORIGINAL_REACH)
                ? player.getEntityData().getDouble(NBT_ORIGINAL_REACH) : -1.0;
        double lastApplied = player.getEntityData().hasKey(NBT_LAST_APPLIED)
                ? player.getEntityData().getDouble(NBT_LAST_APPLIED) : -1.0;
        double currentBase = reachAttr.getBaseValue();

        // Only restore if we still see our last applied value; otherwise, don't clobber external updates.
        if (lastApplied > 0 && approxEq(currentBase, lastApplied) && savedOrig > 0) {
            reachAttr.setBaseValue(savedOrig);
            System.out.println("[ReachEnchant] Removed: restored=" + savedOrig);
        } else {
            System.out.println("[ReachEnchant] Removed: skipped restore (external change). currentBase=" +
                    currentBase + " lastApplied=" + lastApplied + " savedOrig=" + savedOrig);
        }

        player.getEntityData().setBoolean(NBT_REACH_ON, false);
        player.getEntityData().removeTag(NBT_LAST_APPLIED);
        player.getEntityData().removeTag(NBT_ORIGINAL_REACH);
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
            applyOrMaintain(player);
        } else if (player.getEntityData().getBoolean(NBT_REACH_ON)) {
            removeSafely(player);
        }
    }
}