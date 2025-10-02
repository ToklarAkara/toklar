package net.mcreator.toklar.imbuement;

public class HarvestImbuementBonus {
    public final String harvestType;
    public final String shape;
    public final int speed;
    public final int[] range;
    public final int levelMin;
    public final int levelMax;

    public HarvestImbuementBonus(String harvestType, String shape, int speed, int[] range, int levelMin, int levelMax) {
        this.harvestType = harvestType;
        this.shape = shape;
        this.speed = speed;
        this.range = range;
        this.levelMin = levelMin;
        this.levelMax = levelMax;
    }

    public boolean appliesToLevel(int level) {
        return level >= levelMin && level <= levelMax;
    }
}