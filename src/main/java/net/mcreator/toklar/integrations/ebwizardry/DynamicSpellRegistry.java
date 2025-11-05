package net.mcreator.toklar.integrations.ebwizardry;

import electroblob.wizardry.spell.Spell;
import net.mcreator.toklar.integrations.ebwizardry.spells.SpellProjectileFromRank;
import net.mcreator.toklar.integrations.ebwizardry.spells.SpellRank;
import net.mcreator.toklar.util.LycanitePartEffectRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraft.util.ResourceLocation;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

@Mod.EventBusSubscriber(modid = "toklar")
public final class DynamicSpellRegistry {

    private static final Map<String, Spell> SPELL_REGISTRY = new HashMap<>();
    private static final List<Spell> preparedSpells = new ArrayList<>();

    public static void initializeStaticSpells() {
        SPELL_REGISTRY.clear();
        Set<String> seen = new HashSet<>();

        for (SpellRank rank : SpellRank.allRanks()) {
            String spellId = rank.projectileName + "_rank" + rank.level;

            if (!seen.add(spellId)) {
                System.out.println("[Toklar] Skipped duplicate spell: " + spellId);
                continue;
            }

            ResourceLocation registryName = new ResourceLocation("toklar", spellId);
            if (SPELL_REGISTRY.containsKey(spellId)) {
                System.out.println("[Toklar] Already registered: " + spellId);
                continue;
            }

            System.out.println("[Toklar] Instantiating SpellProjectileFromRank for: " + spellId);
            Spell spell = new SpellProjectileFromRank(rank);

            if (spell.getRegistryName() == null) {
                spell.setRegistryName(registryName);
            } else {
                System.out.println("[Toklar] Spell already had registry name: " + spell.getRegistryName());
            }

            SPELL_REGISTRY.put(spellId, spell);
            System.out.println("[Toklar] Registered spell: " + spellId +
                               " with registry name: " + spell.getRegistryName());
        }

        System.out.println("[Toklar] Initialized " + SPELL_REGISTRY.size() + " rank-based spells.");
    }

    public static void prepareAllFromParts(Collection<String> partIds) {
        preparedSpells.clear();
        Set<String> seen = new HashSet<>();

        for (String partId : partIds) {
            for (SpellRank rank : LycanitePartEffectRegistry.getProjectileSpellsFor(partId)) {
                String spellId = rank.projectileName + "_rank" + rank.level;
                if (seen.add(spellId)) {
                    Spell spell = SPELL_REGISTRY.get(spellId);
                    if (spell != null) {
                        preparedSpells.add(spell);
                    }
                }
            }
        }

        System.out.println("[Toklar] Prepared " + preparedSpells.size() + " dynamic projectile spells.");
    }

    @SubscribeEvent
    public static void register(RegistryEvent.Register<Spell> event) {
        IForgeRegistry<Spell> registry = event.getRegistry();

        for (Spell spell : SPELL_REGISTRY.values()) {
            if (registry.getValue(spell.getRegistryName()) == null) {
                registry.register(spell);
            } else {
                System.out.println("[Toklar] Skipped already-registered spell: " + spell.getRegistryName());
            }

            // ✅ Safe icon assignment after registration
            try {
                String iconPath = "textures/spells/" + spell.getRegistryName().getResourcePath() + ".png";
                File iconFile = new File("resources/assets/toklar/" + iconPath);
                ResourceLocation icon = iconFile.exists()
                    ? new ResourceLocation("toklar", iconPath)
                    : new ResourceLocation("toklar", "textures/spells/fallback.png");

                Field iconField = Spell.class.getDeclaredField("icon");
                iconField.setAccessible(true);
                iconField.set(spell, icon);
            } catch (Exception e) {
                System.err.println("[Toklar] Failed to assign icon for spell: " + spell.getRegistryName());
                e.printStackTrace();
            }
        }
    }

    public static Spell get(String spellId) {
        for (Spell spell : preparedSpells) {
            if (spell.getRegistryName() != null &&
                spell.getRegistryName().getResourcePath().equals(spellId)) {
                return spell;
            }
        }
        return null;
    }

    public static Collection<Spell> getAll() {
        return Collections.unmodifiableList(preparedSpells);
    }
}