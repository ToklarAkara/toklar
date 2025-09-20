package net.mcreator.toklar;

import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.mcreator.toklar.item.ItemSummonClanky;
import net.mcreator.toklar.item.ItemSummonClankyElement;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;

public class ClientProxyToklar implements IProxyToklar {
	@Override
	public void init(FMLInitializationEvent event) {
	    ModelLoader.setCustomModelResourceLocation(
	        ItemSummonClankyElement.block, 0,
	        new ModelResourceLocation("toklar:ghastly_potion", "inventory")
	    );
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		OBJLoader.INSTANCE.addDomain("toklar");
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
	}

	@Override
	public void serverLoad(FMLServerStartingEvent event) {
	}
}
