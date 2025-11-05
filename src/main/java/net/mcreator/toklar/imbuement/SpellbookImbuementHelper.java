package net.mcreator.toklar.imbuement;

import java.util.List;

import electroblob.wizardry.spell.Spell;
import net.mcreator.toklar.integrations.ebwizardry.DynamicSpellRegistry;
import net.mcreator.toklar.integrations.ebwizardry.spells.SpellRank;
import net.mcreator.toklar.util.LycanitePartEffectRegistry;

public class SpellbookImbuementHelper {

	public static Spell getSpellForPart(String partId, int level) {
		System.out.println("[Toklar] Looking up ranks for part: " + partId);
		List<SpellRank> ranks = LycanitePartEffectRegistry.getProjectileSpellsFor(partId);
		for (SpellRank rank : ranks) {
			String spellId = rank.projectileName + "_rank" + rank.level;
			System.out.println("[Toklar] Found rank: " + spellId);
			if (rank.level == level) {
				Spell spell = DynamicSpellRegistry.get(spellId);
				System.out.println("[Toklar] Matched spell: " + (spell != null ? spell.getRegistryName() : "null"));
				return spell;
			}
		}
		System.out.println("[Toklar] No matching rank found for level: " + level);
		return null;
	}
}