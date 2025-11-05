package net.mcreator.toklar.integrations.ebwizardry.datagen;

import com.google.gson.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

import net.mcreator.toklar.integrations.ebwizardry.spells.SpellRank;

public class SpellJsonGenerator {

    private static final Path OUTPUT_DIR = Paths.get("src/main/resources/assets/toklar/spells");

    private static final Map<String, String> ELEMENT_MAP = new HashMap<>();

    static {
        ELEMENT_MAP.put("scorchfireball", "fire");
        ELEMENT_MAP.put("chaosorb", "sorcery");
        ELEMENT_MAP.put("icefireball", "ice");
        ELEMENT_MAP.put("devilstar", "necromancy");
        ELEMENT_MAP.put("hellfireball", "fire");
        ELEMENT_MAP.put("doomfireball", "fire");
        ELEMENT_MAP.put("quill", "earth");
        ELEMENT_MAP.put("magma", "fire");
        ELEMENT_MAP.put("ember", "fire");
        ELEMENT_MAP.put("throwingscythe", "earth");
        ELEMENT_MAP.put("poop", "earth");
        ELEMENT_MAP.put("aquapulse", "ice");
        ELEMENT_MAP.put("bloodleech", "necromancy");
        ELEMENT_MAP.put("mudshot", "earth");
        ELEMENT_MAP.put("boulderblast", "earth");
        ELEMENT_MAP.put("poisonray", "earth");
        ELEMENT_MAP.put("frostweb", "ice");
        ELEMENT_MAP.put("lightball", "sorcery");
        ELEMENT_MAP.put("waterjet", "ice");
        ELEMENT_MAP.put("demonicspark", "lightning");
        ELEMENT_MAP.put("arcanelaserstorm", "sorcery");
        ELEMENT_MAP.put("spectralbolt", "necromancy");
        ELEMENT_MAP.put("frostbolt", "ice");
        ELEMENT_MAP.put("venomshot", "earth");
        ELEMENT_MAP.put("blizzard", "ice");
        ELEMENT_MAP.put("summoningseed", "earth");
        ELEMENT_MAP.put("lifedrain", "necromancy");
        ELEMENT_MAP.put("aetherwave", "healing");
        ELEMENT_MAP.put("crystalshard", "earth");
        ELEMENT_MAP.put("tundra", "ice");
    }

    private static String getElementForProjectile(String projectileName) {
        return ELEMENT_MAP.getOrDefault(projectileName.toLowerCase(), "sorcery");
    }

    public static void generateAll() {
        List<SpellRank> ranks = SpellRank.allRanks();
        System.out.println("Total ranks found: " + ranks.size());
        if (ranks.isEmpty()) {
            throw new IllegalStateException("SpellRank.allRanks() returned empty — no spells to generate.");
        }

        for (SpellRank rank : ranks) {
            System.out.println("Generating for: " + rank.projectileName + " rank " + rank.level);
            JsonObject json = buildJson(rank);
            String filename = rank.projectileName + "_rank" + rank.level + ".json";
            writeJson(json, filename);
        }
    }

    private static JsonObject buildJson(SpellRank rank) {
        JsonObject json = new JsonObject();

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

        json.addProperty("tier", getTierForLevel(rank.level));
        json.addProperty("element", getElementForProjectile(rank.projectileName));
        json.addProperty("type", "attack");

        int cost = rank.level * 15;
        json.addProperty("cost", cost);
        json.addProperty("cooldown", rank.cooldown);

        JsonObject baseProps = new JsonObject();
        baseProps.addProperty("damage", rank.bonus);
        json.add("base_properties", baseProps);

        return json;
    }

    private static String getTierForLevel(int level) {
        switch (level) {
            case 1: return "novice";
            case 2: return "apprentice";
            case 3: return "advanced";
            default: return "advanced";
        }
    }

    private static void writeJson(JsonObject json, String filename) {
        try {
            Files.createDirectories(OUTPUT_DIR);
            Path filePath = OUTPUT_DIR.resolve(filename);
            String prettyJson = new GsonBuilder().setPrettyPrinting().create().toJson(json);
            Files.write(filePath, prettyJson.getBytes());
            System.out.println("Generated: " + filePath.toAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("Failed to write spell JSON: " + filename, e);
        }
    }

    public static void main(String[] args) {
        System.out.println("Starting SpellJsonGenerator...");
        System.out.println("Working directory: " + System.getProperty("user.dir"));
        System.out.println("Output path: " + OUTPUT_DIR.toAbsolutePath());
        generateAll();
        System.out.println("Done.");
    }
}