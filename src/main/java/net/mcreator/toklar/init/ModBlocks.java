package net.mcreator.toklar.init;

import net.mcreator.toklar.block.MonsterCandle;
import net.mcreator.toklar.block.TileEntityMonsterCandle;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber
public class ModBlocks {

    public static final Block MONSTER_CANDLE = new MonsterCandle()
        .setRegistryName("monster_candle")
        .setUnlocalizedName("monster_candle");

    @SubscribeEvent
    public static void onBlockRegister(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(MONSTER_CANDLE);
    }

    @SubscribeEvent
    public static void onItemRegister(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new ItemBlock(MONSTER_CANDLE).setRegistryName(MONSTER_CANDLE.getRegistryName()));
    }

    public static void preInit(FMLPreInitializationEvent event) {
        GameRegistry.registerTileEntity(TileEntityMonsterCandle.class, new ResourceLocation("toklar", "monster_candle"));
    }
}