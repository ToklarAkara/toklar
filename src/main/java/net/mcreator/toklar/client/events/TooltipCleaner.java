package net.mcreator.toklar.client.events;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.mcreator.toklar.util.LycanitePartEffectRegistry;
import net.mcreator.toklar.util.LycanitePartEffectRegistry.ImbuementEffect;
import net.mcreator.toklar.imbuement.HarvestImbuementBonus;
import net.mcreator.toklar.integrations.ebwizardry.spells.SpellRank;
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

import java.util.ArrayList;
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

            NBTTagCompound partTag = stack.getTagCompound();
            int partLevel = (partTag != null && partTag.hasKey("equipmentLevel")) ? partTag.getInteger("equipmentLevel") : 1;

            if (LycanitePartEffectRegistry.hasEffects(itemId)) {
                List<ImbuementEffect> effects = LycanitePartEffectRegistry.getEffectsFor(itemId);
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
            List<HarvestImbuementBonus> bonuses = LycanitePartEffectRegistry.getHarvestBonusesFor(itemId);
            List<HarvestImbuementBonus> activeBonuses = bonuses.stream()
                    .filter(b -> b.appliesToLevel(partLevel))
                    .filter(b -> !(b.range[0] == 1 && b.range[1] == 1 && b.range[2] == 1)) // filter out 1x1x1
                    .filter(b -> !b.harvestType.trim().equalsIgnoreCase("sword"))
                    .collect(Collectors.toList());

            if (!activeBonuses.isEmpty()) {
                tooltip.add(TextFormatting.GRAY + "Harvest Bonuses:");
                for (HarvestImbuementBonus bonus : activeBonuses) {
                    String type = capitalize(bonus.harvestType);
                    String shape = bonus.shape;
                    int[] range = bonus.range;
                    int speed = bonus.speed;

                    tooltip.add(TextFormatting.DARK_GRAY + "• " +
                            TextFormatting.GREEN + type + " " +
                            TextFormatting.YELLOW + "[" + shape + "] " +
                            TextFormatting.DARK_AQUA + "Range: " + range[0] + "x" + range[1] + "x" + range[2] +
                            TextFormatting.GRAY + " Speed: " + speed);
                }
            } else {
                tooltip.add(TextFormatting.DARK_GRAY + "This part has harvest bonuses, but none are active.");
            }
            List<SpellRank> ranks = LycanitePartEffectRegistry.getProjectileSpellsFor(itemId).stream()
            	    .filter(r -> r.level == partLevel)
            	    .collect(Collectors.collectingAndThen(
            	        Collectors.toMap(
            	            r -> r.projectileName + "_rank" + r.level,
            	            r -> r,
            	            (r1, r2) -> r1 // keep first if duplicate
            	        ),
            	        map -> new ArrayList<>(map.values())
            	    ));
            if (!ranks.isEmpty()) {
                tooltip.add(TextFormatting.GRAY + "Projectile Spells:");
                for (SpellRank rank : ranks) {
                    tooltip.add(TextFormatting.DARK_GRAY + "• " +
                        TextFormatting.GOLD + capitalize(rank.projectileName) +
                        TextFormatting.GRAY + " Rank " + rank.level +
                        TextFormatting.AQUA + " x" + rank.count +
                        TextFormatting.BLUE + " +" + rank.bonus + " dmg");
                }
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
    private String capitalize(String s) {
        return s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}