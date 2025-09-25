package net.mcreator.toklar.imbuement;

import net.minecraft.item.ItemStack;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.List;

@SideOnly(Side.CLIENT)
public class WeaponImbuementTooltipHandler {

    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty() || stack.getTagCompound() == null) return;

        NBTTagCompound tag = stack.getTagCompound();
        if (!tag.hasKey("toklar_imbuement")) return;

        NBTTagCompound imbueData = tag.getCompoundTag("toklar_imbuement");
        if (!imbueData.hasKey("effects", 9)) return;

        NBTTagList effectList = imbueData.getTagList("effects", 10);
        List<String> tooltip = event.getToolTip();

        tooltip.add(TextFormatting.GOLD + "Imbuements:");

        for (int i = 0; i < effectList.tagCount(); i++) {
            NBTTagCompound effectTag = effectList.getCompoundTagAt(i);
            String effectId = effectTag.getString("effect");
            int amplifier = effectTag.getInteger("amplifier");

            Potion potion = ForgeRegistries.POTIONS.getValue(new ResourceLocation(effectId));
            if (potion == null) continue;

            PotionEffect dummy = new PotionEffect(potion, 1, amplifier);
            String effectName = I18n.format(dummy.getEffectName());
            String roman = getRomanNumeral(amplifier + 1);

            tooltip.add(TextFormatting.GRAY + " - " +
                        TextFormatting.AQUA + effectName + " " +
                        TextFormatting.DARK_PURPLE + roman);
        }
    }

    private String getRomanNumeral(int level) {
        switch (level) {
            case 1: return "I";
            case 2: return "II";
            case 3: return "III";
            case 4: return "IV";
            case 5: return "V";
            default: return String.valueOf(level);
        }
    }
}