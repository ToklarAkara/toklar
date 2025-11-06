package net.mcreator.toklar.entity.ai;

import net.mcreator.toklar.entity.EntityMonster;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;

public class EntityAIHurtByTargetFiltered extends EntityAIHurtByTarget {
    public EntityAIHurtByTargetFiltered(EntityCreature creature, boolean callsForHelp) {
        super(creature, callsForHelp);
    }

    @Override
    public boolean shouldExecute() {
        EntityLivingBase attacker = this.taskOwner.getRevengeTarget();

        // Skip if attacker is excluded
        if (attacker != null && this.taskOwner instanceof EntityMonster.EntityCustom) {
            EntityMonster.EntityCustom clanky = (EntityMonster.EntityCustom) this.taskOwner;
            if (attacker == clanky.lastExcludedTarget) return false;
        }

        return super.shouldExecute();
    }
}