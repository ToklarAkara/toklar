package net.mcreator.toklar.item.bauble;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.mcreator.toklar.SummonDamageBuffHandler;
import net.mcreator.toklar.config.ModConfig; // <-- your config class
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import baubles.api.BaublesApi;
import java.util.List;

public class ToklarsBeltHandler {

	@SubscribeEvent
	public void onLivingHurt(LivingHurtEvent event) {
	    if (!(event.getEntityLiving() instanceof EntityPlayer)) return;
	    EntityPlayer player = (EntityPlayer) event.getEntityLiving();

	    // Check if Toklar's Jewel is equipped
	    if (BaublesApi.isBaubleEquipped(player, ItemToklarsGirdle.item) != -1) {

	        // Look for nearby entities within 32 blocks
	        List<EntityLivingBase> nearby = player.world.getEntitiesWithinAABB(
	            EntityLivingBase.class,
	            player.getEntityBoundingBox().grow(ModConfig.summonOwnerMaxDistance) // configurable distance
	        );

	        for (EntityLivingBase entity : nearby) {
	            // Ownership check: who owns this entity?
	            EntityPlayer owner = SummonDamageBuffHandler.resolveValidSummonOwner(entity);

	            // If the summon is owned by the player being hurt
	            if (owner != null && owner == player) {
	                // Apply configurable damage reduction
	                float reductionFactor = ModConfig.toklarsBeltDamageReduction;
	                event.setAmount(event.getAmount() * reductionFactor);
	                break; // stop after first valid match
	            }
	        }
	    }
	}
}