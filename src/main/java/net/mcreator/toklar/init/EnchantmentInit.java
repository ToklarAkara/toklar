package net.mcreator.toklar.init;

import net.mcreator.toklar.enchantments.EnchantmentFocus;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.mcreator.toklar.enchantments.EnchantReach;

public class EnchantmentInit {
	public static final Enchantment FOCUS = new EnchantmentFocus();
	public static final Enchantment BLOCK_REACH = new EnchantReach();

	@SubscribeEvent
	public static void onRegisterEnchantments(RegistryEvent.Register<Enchantment> event) {
	    event.getRegistry().registerAll(FOCUS, BLOCK_REACH);
	}
}
