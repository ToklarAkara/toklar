package net.mcreator.toklar.imbuement;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
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

        int latRange  = bonus.range[0]; // left/right
        int vertRange = bonus.range[1]; // up/down
        int lonRange  = bonus.range[2]; // forward

        // Determine facing based on pitch
        EnumFacing facing = player.getHorizontalFacing();
        if (player.rotationPitch > 45)  facing = EnumFacing.DOWN;
        if (player.rotationPitch < -45) facing = EnumFacing.UP;

        // Lateral direction (left/right)
        EnumFacing latFacing = facing.rotateY();

        // Build directional ranges
        Vec3i lonMin = new Vec3i(
                Math.min(0, lonRange * facing.getFrontOffsetX()),
                Math.min(0, lonRange * facing.getFrontOffsetY()),
                Math.min(0, lonRange * facing.getFrontOffsetZ())
        );
        Vec3i lonMax = new Vec3i(
                Math.max(0, lonRange * facing.getFrontOffsetX()),
                Math.max(0, lonRange * facing.getFrontOffsetY()),
                Math.max(0, lonRange * facing.getFrontOffsetZ())
        );

        Vec3i latMin = new Vec3i(
                -latRange * Math.abs(latFacing.getFrontOffsetX()),
                -latRange * Math.abs(latFacing.getFrontOffsetY()),
                -latRange * Math.abs(latFacing.getFrontOffsetZ())
        );
        Vec3i latMax = new Vec3i(
                 latRange * Math.abs(latFacing.getFrontOffsetX()),
                 latRange * Math.abs(latFacing.getFrontOffsetY()),
                 latRange * Math.abs(latFacing.getFrontOffsetZ())
        );

        Vec3i vertMin, vertMax;

        if (facing != EnumFacing.UP && facing != EnumFacing.DOWN) {
            // Horizontal mining → vertical range is centered
            int offset = (vertRange != 0) ? -1 : 0;
            vertMin = new Vec3i(0, offset, 0);
            vertMax = new Vec3i(0, vertRange + offset, 0);
        } else {
            // Vertical mining → vertical range follows facing
            vertMin = new Vec3i(
                    (int)(vertRange * -0.5F * Math.abs(facing.getFrontOffsetX())),
                    (int)(vertRange * -0.5F * Math.abs(facing.getFrontOffsetY())),
                    (int)(vertRange * -0.5F * Math.abs(facing.getFrontOffsetZ()))
            );
            vertMax = new Vec3i(
                    (int)(vertRange * 0.5F * Math.abs(facing.getFrontOffsetX())),
                    (int)(vertRange * 0.5F * Math.abs(facing.getFrontOffsetY())),
                    (int)(vertRange * 0.5F * Math.abs(facing.getFrontOffsetZ()))
            );
        }

        String shape = bonus.shape.toLowerCase();
        boolean isRandom = shape.equals("random");

        // -----------------------------
        // ⭐ BLOCK + RANDOM SHAPES
        // -----------------------------
        if (shape.equals("block") || shape.equals("random")) {

            for (int lx = lonMin.getX(); lx <= lonMax.getX(); lx++) {
                for (int ly = lonMin.getY(); ly <= lonMax.getY(); ly++) {
                    for (int lz = lonMin.getZ(); lz <= lonMax.getZ(); lz++) {

                        for (int tx = latMin.getX(); tx <= latMax.getX(); tx++) {
                            for (int ty = latMin.getY(); ty <= latMax.getY(); ty++) {
                                for (int tz = latMin.getZ(); tz <= latMax.getZ(); tz++) {

                                    for (int vx = vertMin.getX(); vx <= vertMax.getX(); vx++) {
                                        for (int vy = vertMin.getY(); vy <= vertMax.getY(); vy++) {
                                            for (int vz = vertMin.getZ(); vz <= vertMax.getZ(); vz++) {

                                                if (isRandom && world.rand.nextBoolean() == false)
                                                    continue;

                                                BlockPos target = origin.add(lx + tx + vx, ly + ty + vy, lz + tz + vz);
                                                if (target.equals(origin)) continue;

                                                IBlockState state = world.getBlockState(target);
                                                Block block = state.getBlock();

                                                if (block.isAir(state, world, target)) continue;
                                                if (state.getBlockHardness(world, target) < 0) continue;
                                                if (!tool.canHarvestBlock(state)) continue;
                                                if (!block.equals(originBlock)) continue;
                                                if (!world.isBlockModifiable(player, target)) continue;

                                                world.destroyBlock(target, true);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return;
        }

        // -----------------------------
        // ⭐ CROSS SHAPE
        // -----------------------------
        if (shape.equals("cross")) {

            // Part 1: lon + lat
            for (int lx = lonMin.getX(); lx <= lonMax.getX(); lx++) {
                for (int ly = lonMin.getY(); ly <= lonMax.getY(); ly++) {
                    for (int lz = lonMin.getZ(); lz <= lonMax.getZ(); lz++) {

                        for (int tx = latMin.getX(); tx <= latMax.getX(); tx++) {
                            for (int ty = latMin.getY(); ty <= latMax.getY(); ty++) {
                                for (int tz = latMin.getZ(); tz <= latMax.getZ(); tz++) {

                                    BlockPos target = origin.add(lx + tx, ly + ty, lz + tz);
                                    if (target.equals(origin)) continue;

                                    IBlockState state = world.getBlockState(target);
                                    Block block = state.getBlock();

                                    if (block.isAir(state, world, target)) continue;
                                    if (state.getBlockHardness(world, target) < 0) continue;
                                    if (!tool.canHarvestBlock(state)) continue;
                                    if (!block.equals(originBlock)) continue;
                                    if (!world.isBlockModifiable(player, target)) continue;

                                    world.destroyBlock(target, true);
                                }
                            }
                        }

                        // Part 2: lon + vert
                        for (int vx = vertMin.getX(); vx <= vertMax.getX(); vx++) {
                            for (int vy = vertMin.getY(); vy <= vertMax.getY(); vy++) {
                                for (int vz = vertMin.getZ(); vz <= vertMax.getZ(); vz++) {

                                    BlockPos target = origin.add(lx + vx, ly + vy, lz + vz);
                                    if (target.equals(origin)) continue;

                                    IBlockState state = world.getBlockState(target);
                                    Block block = state.getBlock();

                                    if (block.isAir(state, world, target)) continue;
                                    if (state.getBlockHardness(world, target) < 0) continue;
                                    if (!tool.canHarvestBlock(state)) continue;
                                    if (!block.equals(originBlock)) continue;
                                    if (!world.isBlockModifiable(player, target)) continue;

                                    world.destroyBlock(target, true);
                                }
                            }
                        }
                    }
                }
            }
        }
    }


}
