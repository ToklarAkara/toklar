package net.mcreator.toklar.init;

import net.mcreator.toklar.item.bauble.ItemToklarsGirdle;
import net.mcreator.toklar.item.bauble.ItemToklarsJewel;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = net.mcreator.toklar.Toklar.MODID)
public class ModItems {

    public static Item TOKLARS_GIRDLE;
    public static Item TOKLARS_JEWEL_1;

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        TOKLARS_GIRDLE = new ItemToklarsGirdle();
        TOKLARS_JEWEL_1 = new ItemToklarsJewel();

        event.getRegistry().registerAll(
            TOKLARS_GIRDLE,
            TOKLARS_JEWEL_1
        );
    }
}