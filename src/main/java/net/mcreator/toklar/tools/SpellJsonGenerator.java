package net.mcreator.toklar.tools;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.mcreator.toklar.integrations.ebwizardry.spells.SpellRank;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SpellJsonGenerator {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String OUTPUT_DIR = "src/main/resources/assets/toklar/spells/";

    public static void generateAllSpellJsons() {
        for (SpellRank rank : SpellRank.allRanks()) {
            try {
                String spellId = rank.projectileName + "_rank" + rank.level;
                ProjectileInfo info = ProjectileManager.getInstance().getProjectile(rank.projectileName);
                if (info == null) {
                    System.err.println("[Toklar] Skipping: No projectile info for " + rank.projectileName);
                    continue;
                }

                JsonObject json = new JsonObject();

                // Enabled flags
                JsonObject enabled = new JsonObject();
                enabled.addProperty("book", true);
                enabled.addProperty("scroll", true);
                enabled.addProperty("wands", true);
                enabled.addProperty("npcs", false);
                enabled.addProperty("dispensers", false);
                enabled.addProperty("commands", true);
                enabled.addProperty("treasure", false);
                enabled.addProperty("trades", false);
                enabled.addProperty("looting", true);
                json.add("enabled", enabled);

                // Core spell properties
                json.addProperty("tier", getTierName(rank.level));
                json.addProperty("element", getElementForProjectile(rank.projectileName));
                json.addProperty("type", "projectile");
                json.addProperty("cost", calculateCost(rank));
                json.addProperty("cooldown", rank.cooldown);
                json.addProperty("chargeup", 0);

                // Base properties
                JsonObject baseProps = new JsonObject();
                baseProps.addProperty("damage", info.damage + rank.bonus);
                baseProps.addProperty("count", rank.count);
                json.add("base_properties", baseProps);

                // Write to file
                Path outputPath = Paths.get(OUTPUT_DIR + spellId + ".json");
                Files.createDirectories(outputPath.getParent());
                try (FileWriter writer = new FileWriter(outputPath.toFile())) {
                    GSON.toJson(json, writer);
                }

                System.out.println("[Toklar] Generated spell JSON: " + spellId);
            } catch (Exception e) {
                System.err.println("[Toklar] Error generating spell JSON for: " + rank.projectileName);
                e.printStackTrace();
            }
        }
    }

    private static String getTierName(int level) {
        switch (level) {
            case 1: return "novice";
            case 2: return "apprentice";
            case 3: return "advanced";
            case 4: return "master";
            default: return "novice";
        }
    }

    private static String getElementForProjectile(String projectileName) {
        // Optional: map projectile names to elements
        if (projectileName.contains("ice")) return "ice";
        if (projectileName.contains("fire")) return "fire";
        if (projectileName.contains("shadow")) return "necromancy";
        return "magic";
    }

    private static int calculateCost(SpellRank rank) {
        return 10 + rank.level * 10 + rank.bonus;
    }

    public static void main(String[] args) {
    	SpellRank dummy = SpellRank.getOrCreate("testprojectile", 20, 3, 5, 2);
        generateAllSpellJsons();
    }
}