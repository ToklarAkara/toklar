package net.mcreator.toklar.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAILeapTowardTarget extends EntityAIBase {
    private final EntityLiving entity;
    private final double leapStrength;
    private int cooldown;

    public EntityAILeapTowardTarget(EntityLiving entity, double leapStrength) {
        this.entity = entity;
        this.leapStrength = leapStrength;
        this.setMutexBits(1); // avoid pathing conflict
    }

    @Override
    public boolean shouldExecute() {
        EntityLivingBase target = entity.getAttackTarget();
        if (target == null || !entity.onGround || cooldown > 0) return false;

        double distSq = entity.getDistanceSq(target);
        return distSq >= 4.0D && distSq <= 225.0D; // 2–15 blocks
    }

    @Override
    public void startExecuting() {
        EntityLivingBase target = entity.getAttackTarget();
        if (target == null) return;

        double dx = target.posX - entity.posX;
        double dz = target.posZ - entity.posZ;
        double dist = Math.sqrt(dx * dx + dz * dz);
        if (dist > 0.1D) {
            entity.motionX = dx / dist * leapStrength;
            entity.motionY = 0.6D;
            entity.motionZ = dz / dist * leapStrength;
            entity.isAirBorne = true;
            entity.velocityChanged = true;
            cooldown = 40; // 2s cooldown
        }
    }

    @Override
    public boolean shouldContinueExecuting() {
        return false;
    }

    @Override
    public void updateTask() {
        if (cooldown > 0) cooldown--;
    }
}