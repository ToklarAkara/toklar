package net.mcreator.toklar;

import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.mcreator.toklar.item.ItemSummonClankyElement;
import net.mcreator.toklar.client.events.TooltipCleaner;
import net.mcreator.toklar.imbuement.WeaponImbuementTooltipHandler;
import net.mcreator.toklar.init.ModBlocks;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.item.Item;

public class ClientProxyToklar implements IProxyToklar {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        OBJLoader.INSTANCE.addDomain("toklar");

        // Register this class to listen for model events
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new TooltipCleaner());
        MinecraftForge.EVENT_BUS.register(new WeaponImbuementTooltipHandler());
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
    }

    @Override
    public void serverLoad(FMLServerStartingEvent event) {
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onModelRegister(ModelRegistryEvent event) {
        // Register Monster Candle item model
        ModelLoader.setCustomModelResourceLocation(
            Item.getItemFromBlock(ModBlocks.MONSTER_CANDLE),
            0,
            new ModelResourceLocation("toklar:monster_candle", "inventory")
        );

        // Register Imbuement Altar item model
        ModelLoader.setCustomModelResourceLocation(
            Item.getItemFromBlock(ModBlocks.IMBUEMENT_ALTAR),
            0,
            new ModelResourceLocation("toklar:imbuement_altar", "inventory")
        );

        // Register Ghastly Potion item model
        ModelLoader.setCustomModelResourceLocation(
            ItemSummonClankyElement.block,
            0,
            new ModelResourceLocation("toklar:ghastly_potion", "inventory")
        );
    }
}