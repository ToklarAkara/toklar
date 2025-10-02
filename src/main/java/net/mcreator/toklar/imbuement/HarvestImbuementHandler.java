package net.mcreator.toklar.imbuement;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;

public class HarvestImbuementHandler {

    private static final String HARVEST_TAG = "toklar_imbuement";

    public static void imbueHarvest(ItemStack tool, HarvestImbuementBonus bonus) {
        if (tool.isEmpty()) return;

        NBTTagCompound tag = tool.getOrCreateSubCompound(HARVEST_TAG);
        NBTTagList harvestList = tag.hasKey("bonuses", 9) ? tag.getTagList("bonuses", 10) : new NBTTagList();

        NBTTagCompound bonusTag = new NBTTagCompound();
        bonusTag.setString("type", bonus.harvestType);
        bonusTag.setString("shape", bonus.shape);
        bonusTag.setInteger("speed", bonus.speed);
        bonusTag.setIntArray("range", bonus.range);
        bonusTag.setInteger("levelMin", bonus.levelMin);
        bonusTag.setInteger("levelMax", bonus.levelMax);

        harvestList.appendTag(bonusTag);
        tag.setTag("bonuses", harvestList);
    }

    public static List<HarvestImbuementBonus> getHarvestBonuses(ItemStack tool) {
        List<HarvestImbuementBonus> bonuses = new ArrayList<>();
        NBTTagCompound tag = tool.getSubCompound(HARVEST_TAG);
        if (tag == null || !tag.hasKey("bonuses", 9)) return bonuses;

        NBTTagList list = tag.getTagList("bonuses", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound b = list.getCompoundTagAt(i);
            bonuses.add(new HarvestImbuementBonus(
                b.getString("type"),
                b.getString("shape"),
                b.getInteger("speed"),
                b.getIntArray("range"),
                b.getInteger("levelMin"),
                b.getInteger("levelMax")
            ));
        }
        return bonuses;
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        EntityPlayer player = event.getPlayer();
        ItemStack tool = player.getHeldItemMainhand();
        if (tool.isEmpty()) return;

        List<HarvestImbuementBonus> bonuses = getHarvestBonuses(tool);
        for (HarvestImbuementBonus bonus : bonuses) {
            if (matchesHarvestToolType(tool, bonus.harvestType)) {
                applyMultiblockHarvest(event, bonus);
            }
        }
    }
    public static boolean matchesHarvestToolType(ItemStack stack, String harvestType) {
        if (stack.isEmpty()) return false;

        ResourceLocation id = stack.getItem().getRegistryName();
        if (id == null) return false;

        String modid = id.getResourceDomain(); // 1.12.2-compatible
        String path = id.getResourcePath();

        if (!modid.equals("tconstruct")) return false;

        switch (harvestType) {
            case "shovel":
                return path.matches(".*(excavator|shovel|mattock).*");
            case "pickaxe":
                return path.matches(".*(hammer|pickaxe).*");
            case "axe":
                return path.matches(".*(hatchet|lumberaxe|mattock).*");
            case "hoe":
                return path.matches(".*(kama|scythe).*");
            default:
                return false;
        }
    }
    
    private void applyMultiblockHarvest(BlockEvent.BreakEvent event, HarvestImbuementBonus bonus) {
        World world = event.getWorld();
        BlockPos origin = event.getPos();
        EntityPlayer player = event.getPlayer();
        ItemStack tool = player.getHeldItemMainhand();
        IBlockState originState = world.getBlockState(origin);
        Block originBlock = originState.getBlock();
        
        int rx = bonus.range[0];
        int ry = bonus.range[1];
        int rz = bonus.range[2];

        for (int dx = -rx; dx <= rx; dx++) {
            for (int dy = -ry; dy <= ry; dy++) {
                for (int dz = -rz; dz <= rz; dz++) {
                    BlockPos targetPos = origin.add(dx, dy, dz);
                    if (targetPos.equals(origin)) continue;

                    // Shape filtering
                    if (bonus.shape.equals("cross") && dx != 0 && dz != 0) continue;

                    IBlockState state = world.getBlockState(targetPos);
                    Block block = state.getBlock();

                 // Skip air, unbreakables, or blocks the tool can't harvest
                    if (block.isAir(state, world, targetPos)) continue;
                    if (state.getBlockHardness(world, targetPos) < 0) continue;
                    if (!tool.canHarvestBlock(state)) continue;

                    // Skip blocks that don't match the original block type
                    if (!block.equals(originBlock)) continue;

                    // Optional: check if player can break (e.g., spawn protection)
                    if (!world.isBlockModifiable(player, targetPos)) continue;

                    // Break the block and drop items
                    world.destroyBlock(targetPos, true);
                }
            }
        }
    }
}
