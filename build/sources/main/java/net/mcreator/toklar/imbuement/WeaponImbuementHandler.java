package net.mcreator.toklar.imbuement;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

/**
 * Handles weapon imbuement logic for Toklar.
 * Stores and applies multiple Lycanite debuffs via NBT.
 */
public class WeaponImbuementHandler {

    private static final String IMBUEMENT_TAG = "toklar_imbuement";

    public WeaponImbuementHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * Imbues the weapon with a Lycanite potion effect.
     * Adds to the effect list instead of overwriting.
     */
    public static void imbueWeapon(ItemStack weapon, String effectId, int durationTicks, int amplifier, String target) {
        if (weapon.isEmpty()) return;

        System.out.println("[Toklar] Imbuing weapon with " + effectId + " (amp " + amplifier + ", dur " + durationTicks + ", target " + target + ")");

        NBTTagCompound tag = weapon.getOrCreateSubCompound(IMBUEMENT_TAG);
        NBTTagList effectList = tag.hasKey("effects", 9) ? tag.getTagList("effects", 10) : new NBTTagList();

        NBTTagCompound singleEffect = new NBTTagCompound();
        singleEffect.setString("effect", effectId);
        singleEffect.setInteger("duration", durationTicks);
        singleEffect.setInteger("amplifier", amplifier);
        singleEffect.setString("target", target);

        effectList.appendTag(singleEffect);
        tag.setTag("effects", effectList);
    }

    /**
     * Applies all imbuement effects to the correct entity.
     */
    public static void applyImbuementEffect(EntityLivingBase attacker, EntityLivingBase target, ItemStack weapon) {
        if (weapon.isEmpty() || target == null) return;

        NBTTagCompound tag = weapon.getTagCompound();
        if (tag == null || !tag.hasKey(IMBUEMENT_TAG)) return;

        NBTTagCompound imbueData = tag.getCompoundTag(IMBUEMENT_TAG);
        if (!imbueData.hasKey("effects", 9)) return;

        NBTTagList effectList = imbueData.getTagList("effects", 10);
        for (int i = 0; i < effectList.tagCount(); i++) {
            NBTTagCompound effectTag = effectList.getCompoundTagAt(i);
            String effectId = effectTag.getString("effect");
            int duration = effectTag.getInteger("duration");
            int amplifier = effectTag.getInteger("amplifier");
            String targetKey = effectTag.hasKey("target") ? effectTag.getString("target") : "target";

            Potion potion = ForgeRegistries.POTIONS.getValue(new ResourceLocation(effectId));
            if (potion != null) {
                EntityLivingBase recipient = targetKey.equalsIgnoreCase("self") ? attacker : target;
                recipient.addPotionEffect(new PotionEffect(potion, duration, amplifier));
            }
        }
    }

    /**
     * Event hook: applies imbuement effects on melee hit.
     */
    @SubscribeEvent
    public void onEntityHit(LivingHurtEvent event) {
        if (!(event.getSource().getTrueSource() instanceof EntityPlayer)) return;

        EntityPlayer attacker = (EntityPlayer) event.getSource().getTrueSource();
        ItemStack weapon = attacker.getHeldItemMainhand();

        applyImbuementEffect(attacker, event.getEntityLiving(), weapon);
    }
}