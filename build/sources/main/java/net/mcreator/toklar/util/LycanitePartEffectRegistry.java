package net.mcreator.toklar.util;

import com.google.gson.*;
import java.io.*;
import java.util.*;
import net.minecraft.util.ResourceLocation;

public class LycanitePartEffectRegistry {

    private static final Map<String, List<ImbuementEffect>> effectMap = new HashMap<>();
    private static final Set<String> allPartIds = new HashSet<>();
    private static final Set<String> effectPartIds = new HashSet<>();

    public static void loadAll() {
        File dir = new File("config/lycanitesmobs/equipment");
        if (!dir.exists() || !dir.isDirectory()) {
            System.out.println("[Toklar] Lycanites equipment folder not found.");
            return;
        }

        File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
        if (files == null || files.length == 0) {
            System.out.println("[Toklar] No equipment part JSONs found.");
            return;
        }

        for (File file : files) {
            String itemId = file.getName().replace(".json", "");
            allPartIds.add(itemId); // Track all parts

            boolean hasEffect = false;

            try (FileReader reader = new FileReader(file)) {
                JsonObject root = new JsonParser().parse(reader).getAsJsonObject();
                JsonArray features = root.getAsJsonArray("features");
                if (features == null) continue;

                for (JsonElement el : features) {
                    JsonObject feature = el.getAsJsonObject();
                    if (!feature.has("featureType")) continue;

                    String featureType = feature.get("featureType").getAsString();
                    if (!featureType.equals("effect")) continue;

                    hasEffect = true;

                    String effectType = feature.get("effectType").getAsString();
                    int strength = feature.has("effectStrength") ? feature.get("effectStrength").getAsInt() : 0;
                    int duration = feature.has("effectDuration") ? feature.get("effectDuration").getAsInt() : 80;
                    int levelMin = feature.has("levelMin") ? feature.get("levelMin").getAsInt() : 1;
                    int levelMax = feature.has("levelMax") ? feature.get("levelMax").getAsInt() : Integer.MAX_VALUE;
                    String target = feature.has("effectTarget") ? feature.get("effectTarget").getAsString() : "target";

                    effectMap.computeIfAbsent(itemId, k -> new ArrayList<>())
                             .add(new ImbuementEffect(effectType, strength, duration, levelMin, levelMax, target));
                }

                if (hasEffect) {
                    effectPartIds.add(itemId); // Track only parts with imbuement effects
                }

            } catch (Exception e) {
                System.err.println("[Toklar] Failed to parse: " + file.getName());
                e.printStackTrace();
            }
        }

        System.out.println("[Toklar] Loaded " + effectMap.size() + " Lycanites part effects.");
    }

    public static List<ImbuementEffect> getEffectsFor(String itemId) {
        return effectMap.getOrDefault(itemId, Collections.emptyList());
    }

    public static boolean hasEffects(String itemId) {
        return effectPartIds.contains(itemId);
    }

    public static boolean isKnownPart(String itemId) {
        return allPartIds.contains(itemId);
    }

    public static boolean isValidImbuementPart(String itemId) {
        return effectPartIds.contains(itemId);
    }

    public static class ImbuementEffect {
        public final String type;
        public final int strength;
        public final int duration;
        public final int levelMin;
        public final int levelMax;
        public final String target;

        public ImbuementEffect(String type, int strength, int duration, int levelMin, int levelMax, String target) {
            this.type = type;
            this.strength = strength;
            this.duration = duration;
            this.levelMin = levelMin;
            this.levelMax = levelMax;
            this.target = target;
        }

        public boolean appliesToLevel(int level) {
            return level >= levelMin && level <= levelMax;
        }

        public ResourceLocation getEffectResource() {
            return new ResourceLocation(type);
        }
    }
}