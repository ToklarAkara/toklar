package net.mcreator.toklar.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import electroblob.wizardry.constants.Element;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.entity.living.EntityEvilWizard;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.SpellProperties;
import electroblob.wizardry.util.WandHelper;

import net.minecraft.entity.EntityLiving;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = "toklar") // use your modid here
public class WizardSpawnHandler {

    @SubscribeEvent
    public static void onSpecialSpawn(LivingSpawnEvent.SpecialSpawn event) {
        // Only run if Wizardry is present
        if (!Loader.isModLoaded("ebwizardry")) return;

        if (event.getEntityLiving() instanceof EntityEvilWizard) {
            EntityEvilWizard wizard = (EntityEvilWizard) event.getEntityLiving();
            Random rand = wizard.getRNG();

            // Clear and rebuild spell list
            wizard.getSpells().clear();
            wizard.getSpells().add(Spells.magic_missile);

            // Allow Master tier this time
            Tier maxTier = populateSpellsWithMasters(wizard, wizard.getSpells(), wizard.getElement(), true, 3, rand);

            // Rebuild wand with new spells + Heal
            ItemStack wand = new ItemStack(WizardryItems.getWand(maxTier, wizard.getElement()));
            List<Spell> spellList = new ArrayList<>(wizard.getSpells());
            spellList.add(Spells.heal);
            WandHelper.setSpells(wand, spellList.toArray(new Spell[0]));
            wizard.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, wand);
        }
    }

    private static Tier populateSpellsWithMasters(EntityLiving wizard, List<Spell> spells, Element e, boolean master, int n, Random random) {
        Tier maxTier = Tier.NOVICE;
        List<Spell> npcSpells = Spell.getSpells(s -> s.canBeCastBy(wizard, false));

        for (int i = 0; i < n; i++) {
            Tier tier;
            Element element = (e == Element.MAGIC) ? Element.values()[random.nextInt(Element.values().length)] : e;
            int roll = random.nextInt(20);

            if (roll < 10) tier = Tier.NOVICE;
            else if (roll < 16) tier = Tier.APPRENTICE;
            else if (roll < 19 || !master) tier = Tier.ADVANCED;
            else tier = Tier.MASTER;

            if (tier.ordinal() > maxTier.ordinal()) maxTier = tier;

            List<Spell> pool = Spell.getSpells(new Spell.TierElementFilter(tier, element, SpellProperties.Context.NPCS));
            pool.retainAll(npcSpells);
            pool.removeAll(spells);

            if (pool.isEmpty()) {
                pool = new ArrayList<>(npcSpells);
                pool.removeAll(spells);
            }

            if (!pool.isEmpty()) {
                spells.add(pool.get(random.nextInt(pool.size())));
            }
        }
        return maxTier;
    }
}