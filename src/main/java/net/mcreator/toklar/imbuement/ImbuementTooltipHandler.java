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
public class ImbuementTooltipHandler {

	@SubscribeEvent
	public void onItemTooltip(ItemTooltipEvent event) {
	    ItemStack stack = event.getItemStack();
	    if (stack.isEmpty() || stack.getTagCompound() == null) return;

	    NBTTagCompound tag = stack.getTagCompound();
	    if (!tag.hasKey("toklar_imbuement")) return;

	    NBTTagCompound imbueData = tag.getCompoundTag("toklar_imbuement");
	    List<String> tooltip = event.getToolTip();

	    boolean wroteHeader = false;

	    if (imbueData.hasKey("effects", 9)) {
	        NBTTagList effectList = imbueData.getTagList("effects", 10);
	        if (!wroteHeader) {
	            tooltip.add(TextFormatting.GOLD + "Imbuements:");
	            wroteHeader = true;
	        }

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

	    if (imbueData.hasKey("bonuses", 9)) {
	        NBTTagList bonusList = imbueData.getTagList("bonuses", 10);
	        if (!wroteHeader) {
	            tooltip.add(TextFormatting.GOLD + "Imbuements:");
	            wroteHeader = true;
	        }

	        for (int i = 0; i < bonusList.tagCount(); i++) {
	            NBTTagCompound bonusTag = bonusList.getCompoundTagAt(i);
	            String type = bonusTag.getString("type");
	            String shape = bonusTag.getString("shape");
	            int speed = bonusTag.getInteger("speed");
	            int[] range = bonusTag.getIntArray("range");
	            if (range.length == 3 && range[0] == 1 && range[1] == 1 && range[2] == 1) continue;


	            tooltip.add(TextFormatting.GRAY + " - " +
	                        TextFormatting.GREEN + capitalize(type) + " Harvest " +
	                        TextFormatting.YELLOW + "[" + shape + "] " +
	                        TextFormatting.DARK_AQUA + "Range: " + range[0] + "x" + range[1] + "x" + range[2] +
	                        TextFormatting.GRAY + " Speed: " + speed);
	        }
	    }
	}
	
	private String capitalize(String s) {
	    return s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
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