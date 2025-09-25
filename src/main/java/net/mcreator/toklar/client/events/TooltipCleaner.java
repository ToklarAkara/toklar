package net.mcreator.toklar.client.events;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.mcreator.toklar.util.LycanitePartEffectRegistry;
import net.mcreator.toklar.util.LycanitePartEffectRegistry.ImbuementEffect;
import net.mcreator.toklar.tile.TileEntityImbuementAltar;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;

import java.util.List;
import java.util.stream.Collectors;

@SideOnly(Side.CLIENT)
public class TooltipCleaner {

    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty() || stack.getItem() == null) return;

        ResourceLocation id = stack.getItem().getRegistryName();
        if (id == null) return;

        String itemId = id.getResourcePath();
        List<String> tooltip = event.getToolTip();

        boolean isPart = LycanitePartEffectRegistry.isKnownPart(itemId);
        boolean isCatalyst = TileEntityImbuementAltar.isValidCatalyst(stack);

        // Preserve "Charge Elements" line before clearing
        String preservedElementLine = null;
        for (String line : tooltip) {
            String clean = TextFormatting.getTextWithoutFormattingCodes(line).toLowerCase();
            if (clean.contains("elements:")) {
                preservedElementLine = line;
                break;
            }
        }

        if (isPart || isCatalyst) {
            String itemName = stack.getDisplayName();
            tooltip.clear();
            tooltip.add(TextFormatting.RESET + itemName); // Restore item name

            if (preservedElementLine != null) {
                tooltip.add(preservedElementLine);
            }
        }

        if (isPart) {
            tooltip.add(TextFormatting.GRAY + "Used in Imbuement rituals");

            if (LycanitePartEffectRegistry.hasEffects(itemId)) {
                List<ImbuementEffect> effects = LycanitePartEffectRegistry.getEffectsFor(itemId);
                NBTTagCompound partTag = stack.getTagCompound();
                int partLevel = (partTag != null && partTag.hasKey("equipmentLevel")) ? partTag.getInteger("equipmentLevel") : 1;

                List<ImbuementEffect> active = effects.stream()
                        .filter(e -> e.appliesToLevel(partLevel))
                        .collect(Collectors.toList());

                if (!active.isEmpty()) {
                    tooltip.add(TextFormatting.GRAY + "Imbuement Effects:");
                    for (ImbuementEffect effect : active) {
                        Potion potion = ForgeRegistries.POTIONS.getValue(new ResourceLocation(effect.type));
                        String cleanType = TextFormatting.getTextWithoutFormattingCodes(effect.type.trim());
                        String rawPotionName = potion != null ? I18n.format(potion.getName()) : cleanType;
                        String potionName = rawPotionName.replaceAll("[^\\p{Print}]", "").trim();

                        String cleanTarget = TextFormatting.getTextWithoutFormattingCodes(effect.target.trim())
                                                           .replaceAll("[^\\p{Print}]", "");

                        tooltip.add(TextFormatting.DARK_GRAY + "• " +
                                TextFormatting.GOLD + potionName + TextFormatting.GRAY + " → " +
                                TextFormatting.GREEN + cleanTarget + " " +
                                TextFormatting.AQUA + "+" + effect.strength +
                                TextFormatting.BLUE + " (" + (effect.duration / 20) + "s)");
                    }
                } else {
                    tooltip.add(TextFormatting.DARK_GRAY + "This part has imbuement effects, but none are active.");
                }
            } else {
                tooltip.add(TextFormatting.DARK_GRAY + "This part has no imbuement effects, you can safely convert it to charges.");
            }
        }

        if (isCatalyst) {
            tooltip.add(TextFormatting.GRAY + "Catalyst for Imbuement rituals");
            tooltip.add(TextFormatting.GRAY + "Consumed when activating effects");
            tooltip.add(TextFormatting.YELLOW + "Shift+Right click to shoot me");
            tooltip.add(TextFormatting.YELLOW + "Can also be used to level soulbound creatures");
        }

        // Re-add modid:itemid if advanced tooltips are enabled
        if (Minecraft.getMinecraft().gameSettings.advancedItemTooltips) {
            tooltip.add(TextFormatting.DARK_GRAY + id.toString());
        }
    }
}