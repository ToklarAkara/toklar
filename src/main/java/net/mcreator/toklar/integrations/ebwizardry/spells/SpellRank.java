package net.mcreator.toklar.integrations.ebwizardry.spells;

import java.util.*;

public class SpellRank {
    public final String projectileName;
    public final int cooldown;
    public final int count;
    public final int bonus;
    public final int level;

    private static final Map<String, SpellRank> RANK_CACHE = new HashMap<>();
    private static final List<SpellRank> REGISTRY = new ArrayList<>();


    private SpellRank(String projectileName, int cooldown, int count, int bonus, int level) {
        this.projectileName = projectileName;
        this.cooldown = cooldown;
        this.count = count;
        this.bonus = bonus;
        this.level = level;
    }

    public static SpellRank getOrCreate(String projectileName, int cooldown, int count, int bonus, int level) {
        String key = projectileName + "_rank" + level;
        return RANK_CACHE.computeIfAbsent(key, k -> {
            System.out.println("[Toklar] Created SpellRank: " + key); // 🔍 Log creation
            SpellRank rank = new SpellRank(projectileName, cooldown, count, bonus, level);
            REGISTRY.add(rank);
            return rank;
        });

    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpellRank)) return false;
        SpellRank that = (SpellRank) o;
        return level == that.level && projectileName.equals(that.projectileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectileName, level);
    }
    public static List<SpellRank> allRanks() {
        return Collections.unmodifiableList(REGISTRY);
    }
}