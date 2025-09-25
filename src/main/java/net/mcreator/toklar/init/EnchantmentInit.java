package net.mcreator.toklar.init;

import net.mcreator.toklar.enchantments.EnchantmentFocus;
import net.mcreator.toklar.enchantments.EnchantHeroic;
import net.mcreator.toklar.enchantments.EnchantReach;
import net.mcreator.toklar.enchantments.EnchantArcaneLeech;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EnchantmentInit {
    public static final Enchantment FOCUS = new EnchantmentFocus()
        .setRegistryName("toklar", "focus")
        .setName("toklar.focus");

    // public static final Enchantment REACH = new EnchantReach()
    //     .setRegistryName("toklar", "reach")
    //     .setName("toklar.reach");

    public static final Enchantment HEROIC = new EnchantHeroic()
        .setRegistryName("toklar", "heroic")
        .setName("toklar.heroic");

    public static final Enchantment ARCANE_LEECH = new EnchantArcaneLeech()
        .setRegistryName("toklar", "arcane_leech")
        .setName("toklar.arcane_leech");

    @SubscribeEvent
    public static void onRegisterEnchantments(RegistryEvent.Register<Enchantment> event) {
        event.getRegistry().registerAll(
            FOCUS,
            // REACH,
            HEROIC,
            ARCANE_LEECH
        );
    }
}