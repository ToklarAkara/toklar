package net.mcreator.toklar.block;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.util.ITickable;

public class TileEntityMonsterCandle extends TileEntity implements ITickable {
    private static final int SPAWN_RADIUS = 16;
    private static final int MAX_NEARBY_MOBS = 20;
    private static final int TICKS_PER_SPAWN = 100; // 5 seconds
    private static final int MAX_TOTAL_SPAWNS = 50;

    private int tickCounter = 0;
    private int totalSpawns = 0;

    @Override
    public void update() {
        if (world.isRemote || !(world instanceof WorldServer)) return;

        tickCounter++;
        if (tickCounter >= TICKS_PER_SPAWN) {
            tickCounter = 0;
            triggerNaturalSpawn((WorldServer) world, pos, world.rand);
        }
    }

    private void triggerNaturalSpawn(WorldServer world, BlockPos pos, Random rand) {
        if (world.getDifficulty() == EnumDifficulty.PEACEFUL) return;

        AxisAlignedBB area = new AxisAlignedBB(pos).grow(SPAWN_RADIUS);
        List<EntityLivingBase> nearby = world.getEntitiesWithinAABB(EntityLivingBase.class, area,
            e -> e.isCreatureType(EnumCreatureType.MONSTER, false));
        if (nearby.size() >= MAX_NEARBY_MOBS) return;

        Biome biome = world.getBiome(pos);
        List<Biome.SpawnListEntry> spawnList = biome.getSpawnableList(EnumCreatureType.MONSTER);
        if (spawnList.isEmpty()) return;

        Biome.SpawnListEntry entry = WeightedRandom.getRandomItem(rand, spawnList);
        if (entry == null || entry.entityClass == null) return;

        try {
            EntityLiving entity = (EntityLiving) entry.entityClass.getConstructor(world.getClass()).newInstance(world);

            for (int attempt = 0; attempt < 10; attempt++) {
                int dx = rand.nextInt(SPAWN_RADIUS * 2 + 1) - SPAWN_RADIUS;
                int dz = rand.nextInt(SPAWN_RADIUS * 2 + 1) - SPAWN_RADIUS;
                BlockPos base = pos.add(dx, 0, dz);
                BlockPos top = world.getTopSolidOrLiquidBlock(base);

                double x = top.getX() + 0.5;
                double y = top.getY() + 1;
                double z = top.getZ() + 0.5;

                entity.setLocationAndAngles(x, y, z, rand.nextFloat() * 360F, 0);
                entity.forceSpawn = true;
                entity.enablePersistence();

                if (entity.isNotColliding()) {
                    world.spawnEntity(entity);
                    totalSpawns++;

                    world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, x, y, z, 6, 0.1, 0.1, 0.1, 0.01);
                    world.playSound(null, x, y, z, SoundEvents.ENTITY_ZOMBIE_AMBIENT, SoundCategory.HOSTILE, 0.5F, 1.0F);

                    if (totalSpawns >= MAX_TOTAL_SPAWNS) {
                        world.setBlockToAir(pos);
                        world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 12, 0.2, 0.2, 0.2, 0.01);
                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("TickCounter", tickCounter);
        compound.setInteger("TotalSpawns", totalSpawns);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        tickCounter = compound.getInteger("TickCounter");
        totalSpawns = compound.getInteger("TotalSpawns");
    }
}
