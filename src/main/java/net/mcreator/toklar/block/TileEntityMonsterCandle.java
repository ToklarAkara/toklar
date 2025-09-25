package net.mcreator.toklar.block;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.util.ITickable;

public class TileEntityMonsterCandle extends TileEntity implements ITickable {
    private static final int SPAWN_RADIUS = 16;
    private static final int MAX_NEARBY_MOBS = 20;
    private static final int TICKS_PER_SPAWN = 100;
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
        AxisAlignedBB area = new AxisAlignedBB(pos).grow(SPAWN_RADIUS);
        List<EntityLivingBase> nearby = world.getEntitiesWithinAABB(EntityLivingBase.class, area);
        if (nearby.size() >= MAX_NEARBY_MOBS) {
          //  System.out.println("Spawn blocked: too many nearby entities (" + nearby.size() + ")");
            return;
        }

        Biome biome = world.getBiome(pos);
        List<Biome.SpawnListEntry> spawnList = biome.getSpawnableList(EnumCreatureType.MONSTER);
        if (spawnList.isEmpty()) {
          //  System.out.println("Spawn blocked: biome has no MONSTER entries");
            return;
        }

        Biome.SpawnListEntry entry = WeightedRandom.getRandomItem(rand, spawnList);
        if (entry == null || entry.entityClass == null) {
         //   System.out.println("Spawn blocked: no valid spawn entry selected");
            return;
        }

       // System.out.println("Attempting to spawn: " + entry.entityClass.getSimpleName());

        try {
            EntityLiving entity = (EntityLiving) entry.entityClass
                .getConstructor(World.class)
                .newInstance(world);

            SpawnPlacementType placement;
            if (EntityWaterMob.class.isAssignableFrom(entry.entityClass)) {
                placement = SpawnPlacementType.IN_WATER;
            } else if (EntityFlying.class.isAssignableFrom(entry.entityClass)) {
                placement = SpawnPlacementType.IN_AIR;
            } else {
                placement = SpawnPlacementType.ON_GROUND;
            }

            for (int attempt = 0; attempt < 10; attempt++) {
                int dx = rand.nextInt(SPAWN_RADIUS * 2 + 1) - SPAWN_RADIUS;
                int dz = rand.nextInt(SPAWN_RADIUS * 2 + 1) - SPAWN_RADIUS;

                BlockPos spawnPos = null;

                if (placement == SpawnPlacementType.ON_GROUND) {
                    BlockPos base = world.getTopSolidOrLiquidBlock(pos.add(dx, 0, dz));
                    spawnPos = base.up(rand.nextInt(4));
                } else if (placement == SpawnPlacementType.IN_WATER) {
                    for (int y = pos.getY(); y > 5; y--) {
                        BlockPos check = new BlockPos(pos.getX() + dx, y, pos.getZ() + dz);
                        if (world.getBlockState(check).getMaterial().isLiquid()) {
                            spawnPos = check;
                            break;
                        }
                    }
                } else if (placement == SpawnPlacementType.IN_AIR) {
                    for (int y = pos.getY(); y < world.getActualHeight(); y++) {
                        BlockPos check = new BlockPos(pos.getX() + dx, y, pos.getZ() + dz);
                        if (world.isAirBlock(check) && world.isAirBlock(check.down())) {
                            spawnPos = check;
                            break;
                        }
                    }
                }

                if (spawnPos == null) {
                 //   System.out.println("Attempt " + attempt + ": No valid spawn position found for placement type " + placement);
                    continue;
                }

                double x = spawnPos.getX() + 0.5;
                double y = spawnPos.getY();
                double z = spawnPos.getZ() + 0.5;

                entity.setLocationAndAngles(x, y, z, rand.nextFloat() * 360F, 0);
                entity.forceSpawn = true;
                entity.enablePersistence();

                boolean notInsideBlock = entity.isNotColliding();
                boolean canSpawnHere = entity.getCanSpawnHere();

               // System.out.println("Attempt " + attempt + ": spawnPos=" + spawnPos +
                             //      ", colliding=" + !notInsideBlock +
                              //     ", canSpawnHere=" + canSpawnHere);

                if (notInsideBlock && canSpawnHere) {
                    entity.onInitialSpawn(world.getDifficultyForLocation(spawnPos), null);
                    world.spawnEntity(entity);
                    totalSpawns++;

                    //System.out.println("Spawned " + entry.entityClass.getSimpleName() + " at " + spawnPos);

                    world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, x, y, z, 6, 0.1, 0.1, 0.1, 0.01);
                    world.playSound(null, x, y, z, SoundEvents.ENTITY_ZOMBIE_AMBIENT, SoundCategory.HOSTILE, 0.5F, 1.0F);

                    if (totalSpawns >= MAX_TOTAL_SPAWNS) {
                        world.setBlockToAir(pos);
                        world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 12, 0.2, 0.2, 0.2, 0.01);
                      //  System.out.println("Candle extinguished after max spawns");
                    }
                    break;
                } else {
                    //System.out.println("Attempt " + attempt + ": spawn blocked by collision or getCanSpawnHere()");
                }
            }
        } catch (Exception e) {
            //System.out.println("Spawn failed: " + e.getMessage());
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