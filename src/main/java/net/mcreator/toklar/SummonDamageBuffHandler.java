package net.mcreator.toklar;

import com.lycanitesmobs.core.entity.damagesources.MinionEntityDamageSource;
import com.lycanitesmobs.core.entity.damagesources.ElementDamageSource;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import electroblob.wizardry.entity.living.EntitySummonedCreature;
import net.mcreator.toklar.config.ModConfig;
import net.mcreator.toklar.item.ItemBronzeArmor;
import net.mcreator.toklar.item.ItemToklarArmor;

import com.windanesz.wizardryutils.capability.SummonedCreatureData;
import xzeroair.trinkets.api.EntityApiHelper;

import java.util.UUID;

@Mod.EventBusSubscriber
public class SummonDamageBuffHandler {

    private static void debugLog(String message) {
        if (ModConfig.enableSummonDamageBuffDebug) {
            System.out.println("[SummonDamageBuff] " + message);
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        DamageSource source = event.getSource();

        Entity immediateSource = source.getImmediateSource();
        Entity trueSource = source.getTrueSource();

        debugLog("Immediate source class: " + (immediateSource != null ? immediateSource.getClass().getName() : "null"));
        debugLog("True source class: " + (trueSource != null ? trueSource.getClass().getName() : "null"));

        EntityLivingBase attackerEntity = null;
        EntityPlayer owner = null;

        if (immediateSource instanceof EntityArrow) {
            EntityArrow arrow = (EntityArrow) immediateSource;
            Entity shooter = arrow.shootingEntity;

            if (shooter instanceof EntitySummonedCreature) {
                EntitySummonedCreature summonedShooter = (EntitySummonedCreature) shooter;
                owner = getOwnerFromEntity(summonedShooter);
                if (owner != null) {
                    attackerEntity = summonedShooter;
                    debugLog("Arrow shot by summoned creature owned by player: " + owner.getName());
                }
            } else if (shooter instanceof EntityLivingBase) {
                attackerEntity = (EntityLivingBase) shooter;
                owner = getOwnerFromEntity((EntityLivingBase) shooter);
                if (owner != null) {
                    debugLog("Arrow shot by summon owned by player: " + owner.getName());
                }
            }
        }

        if (attackerEntity == null) {
            if (immediateSource instanceof EntityLivingBase) {
                attackerEntity = (EntityLivingBase) immediateSource;
            } else if (trueSource instanceof EntityLivingBase) {
                attackerEntity = (EntityLivingBase) trueSource;
            } else {
                debugLog("No valid living attacker found.");
                return;
            }
        }

        if (attackerEntity instanceof EntityPlayer && !(immediateSource instanceof EntitySummonedCreature)) {
            EntityPlayer playerAttacker = (EntityPlayer) attackerEntity;
            debugLog("Damage caused directly by player: " + playerAttacker.getName() + ", no buff applied.");
            return;
        }

        if (attackerEntity instanceof EntityPlayer) {
            if (immediateSource instanceof EntitySummonedCreature) {
                EntitySummonedCreature summoned = (EntitySummonedCreature) immediateSource;
                UUID casterUUID = getCasterUUIDFromSummoned(summoned);
                if (casterUUID != null && casterUUID.equals(attackerEntity.getUniqueID())) {
                    owner = (EntityPlayer) attackerEntity;
                    attackerEntity = summoned;
                    debugLog("Damage caused by summoned creature owned by player: " + owner.getName());
                } else {
                    owner = (EntityPlayer) attackerEntity;
                    debugLog("Damage caused directly by player: " + owner.getName());
                }
            } else {
                owner = (EntityPlayer) attackerEntity;
                debugLog("Damage caused directly by player: " + owner.getName());
            }
        } else {
            owner = getOwnerFromEntity(attackerEntity);
            if (owner == null) {
                debugLog("Could not find owner of attacker entity. Dumping NBT:");
                debugPrintNBT(attackerEntity.getEntityData(), "");
                return;
            }
            debugLog("Owner found: " + owner.getName());
        }

        debugArmorCheck(owner);

        if (!isHuman(owner)) {
            debugLog("Player is not human, no summon buff applied.");
            return;
        }

        if (isWearingFullBronzeSet(owner)) {
            float oldDamage = event.getAmount();
            float multiplier = ModConfig.getSummonDamageMultiplier();
            float newDamage = oldDamage * multiplier;
            event.setAmount(newDamage);
            debugLog(String.format("Damage boosted from %.2f to %.2f for owner %s with bronze multiplier %.2f",
                    oldDamage, newDamage, owner.getName(), multiplier));
        } else if (isWearingFullToklarSet(owner)) {
            float oldDamage = event.getAmount();
            float multiplier = ModConfig.getToklarSummonDamageMultiplier();
            float newDamage = oldDamage * multiplier;
            event.setAmount(newDamage);
            debugLog(String.format("Damage boosted from %.2f to %.2f for owner %s with toklar multiplier %.2f",
                    oldDamage, newDamage, owner.getName(), multiplier));
        } else {
            debugLog("Owner not wearing full bronze or toklar set");
        }
    }

    private static boolean isHuman(EntityPlayer player) {
        String race = EntityApiHelper.getEntityRace(player);
        debugLog("Player race: " + race);
        return "none".equalsIgnoreCase(race);
    }

    private static UUID getCasterUUIDFromSummoned(EntitySummonedCreature summoned) {
        try {
            java.lang.reflect.Field field = EntitySummonedCreature.class.getDeclaredField("casterUUID");
            field.setAccessible(true);
            return (UUID) field.get(summoned);
        } catch (Exception e) {
            debugLog("Failed to get casterUUID: " + e.getMessage());
            return null;
        }
    }

    public static EntityPlayer getOwnerFromEntity(EntityLivingBase entity) {
        debugLog("Attacker class: " + entity.getClass().getName());

        NBTTagCompound nbt = entity.getEntityData();
        SummonedCreatureData data = SummonedCreatureData.get(entity);
        if (data != null) {
            EntityLivingBase caster = data.getCaster();
            if (caster instanceof EntityPlayer) {
                debugLog("Owner found via SummonedCreatureData.getCaster()");
                return (EntityPlayer) caster;
            }
        }

        if (entity instanceof IEntityOwnable) {
            Entity owner = ((IEntityOwnable) entity).getOwner();
            if (owner instanceof EntityPlayer) {
                debugLog("Owner found via IEntityOwnable");
                return (EntityPlayer) owner;
            }
        }

        if (entity instanceof EntitySummonedCreature) {
            EntityLivingBase caster = ((EntitySummonedCreature) entity).getCaster();
            if (caster instanceof EntityPlayer) {
                debugLog("Owner found via EntitySummonedCreature.getCaster()");
                return (EntityPlayer) caster;
            }
        }

        if (nbt.hasKey("wizardryutils:summonedcreaturedata", 10)) {
            NBTTagCompound summonData = nbt.getCompoundTag("wizardryutils:summonedcreaturedata");
            if (summonData.hasKey("casterUUID", 10)) {
                NBTTagCompound casterUUIDTag = summonData.getCompoundTag("casterUUID");
                if (casterUUIDTag.hasKey("casterUUIDMost") && casterUUIDTag.hasKey("casterUUIDLeast")) {
                    long most = casterUUIDTag.getLong("casterUUIDMost");
                    long least = casterUUIDTag.getLong("casterUUIDLeast");
                    UUID ownerId = new UUID(most, least);
                    EntityPlayer owner = entity.world.getPlayerEntityByUUID(ownerId);
                    if (owner != null) {
                        debugLog("Owner found via wizardryutils compound tag");
                        return owner;
                    }
                }
            }
        }

        String[] uuidKeys = {"OwnerId", "ownerUUIDMost", "ownerUUIDLeast", "OwnerUUIDMost", "OwnerUUIDLeast"};
        for (String key : uuidKeys) {
            if (nbt.hasKey(key)) {
                try {
                    UUID ownerId;
                    if (key.equals("OwnerId")) {
                        ownerId = UUID.fromString(nbt.getString(key));
                    } else {
                        long most = nbt.getLong(key.contains("Most") ? key : key.replace("Least", "Most"));
                        long least = nbt.getLong(key.contains("Least") ? key : key.replace("Most", "Least"));
                        ownerId = new UUID(most, least);
                    }
                    EntityPlayer owner = entity.world.getPlayerEntityByUUID(ownerId);
                    if (owner != null) {
                        debugLog("Owner found via NBT: " + key);
                        return owner;
                    }
                } catch (Exception e) {
                    debugLog("Failed to parse UUID from NBT key: " + key);
                }
            }
        }

        debugLog("No owner could be found");
        return null;
    }

    private static void debugPrintNBT(NBTTagCompound compound, String prefix) {
        for (String key : compound.getKeySet()) {
            NBTBase value = compound.getTag(key);
            debugLog(prefix + key + ": " + value);
            if (value instanceof NBTTagCompound) {
                debugPrintNBT((NBTTagCompound) value, prefix + key + ".");
            }
        }
    }

    private static void debugArmorCheck(EntityPlayer player) {
        debugLog("Checking armor:");
        debugLog("Helmet equipped: " + player.getItemStackFromSlot(EntityEquipmentSlot.HEAD));
        debugLog("Expected bronze helmet item: " + ItemBronzeArmor.helmet);
        debugLog("Expected toklar helmet item: " + ItemToklarArmor.helmet);
        debugLog("Chest equipped: " + player.getItemStackFromSlot(EntityEquipmentSlot.CHEST));
        debugLog("Expected bronze chest item: " + ItemBronzeArmor.body);
        debugLog("Expected toklar chest item: " + ItemToklarArmor.body);
        debugLog("Legs equipped: " + player.getItemStackFromSlot(EntityEquipmentSlot.LEGS));
        debugLog("Expected bronze legs item: " + ItemBronzeArmor.legs);
        debugLog("Expected toklar legs item: " + ItemToklarArmor.legs);
        debugLog("Boots equipped: " + player.getItemStackFromSlot(EntityEquipmentSlot.FEET));
        debugLog("Expected bronze boots item: " + ItemBronzeArmor.boots);
        debugLog("Expected toklar boots item: " + ItemToklarArmor.boots);
    }

    private static boolean isWearingFullBronzeSet(EntityPlayer player) {
        return isMatching(ItemBronzeArmor.helmet, player.getItemStackFromSlot(EntityEquipmentSlot.HEAD)) &&
               isMatching(ItemBronzeArmor.body, player.getItemStackFromSlot(EntityEquipmentSlot.CHEST)) &&
               isMatching(ItemBronzeArmor.legs, player.getItemStackFromSlot(EntityEquipmentSlot.LEGS)) &&
               isMatching(ItemBronzeArmor.boots, player.getItemStackFromSlot(EntityEquipmentSlot.FEET));
    }

    private static boolean isWearingFullToklarSet(EntityPlayer player) {
        return isMatching(ItemToklarArmor.helmet, player.getItemStackFromSlot(EntityEquipmentSlot.HEAD)) &&
               isMatching(ItemToklarArmor.body, player.getItemStackFromSlot(EntityEquipmentSlot.CHEST)) &&
               isMatching(ItemToklarArmor.legs, player.getItemStackFromSlot(EntityEquipmentSlot.LEGS)) &&
               isMatching(ItemToklarArmor.boots, player.getItemStackFromSlot(EntityEquipmentSlot.FEET));
    }

    private static boolean isMatching(Item expectedItem, ItemStack worn) {
        return worn != null && !worn.isEmpty() && worn.getItem() == expectedItem;
    }
}
