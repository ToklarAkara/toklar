package net.mcreator.toklar.client;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.mcreator.toklar.init.ModBlocks;

@Mod.EventBusSubscriber(modid = "toklar", value = net.minecraftforge.fml.relauncher.Side.CLIENT)
public class ClientModelRegistry {

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(
            Item.getItemFromBlock(ModBlocks.MONSTER_CANDLE),
            0,
            new ModelResourceLocation("toklar:monster_candle", "inventory")
        );
    }
}