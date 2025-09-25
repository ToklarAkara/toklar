package net.mcreator.toklar;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.config.Configuration;
import net.mcreator.toklar.config.ModConfig;
import net.mcreator.toklar.StructureTradeFixer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.potion.Potion;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.block.Block;
import net.mcreator.toklar.enchantments.FocusRetargetHandler;
import net.mcreator.toklar.gui.GuiHandler;
import net.mcreator.toklar.imbuement.WeaponImbuementHandler;
import net.mcreator.toklar.imbuement.WeaponImbuementTooltipHandler;

import java.util.function.Supplier;

import net.mcreator.toklar.enchantments.EnchantReach;
import net.mcreator.toklar.enchantments.FocusEnchantmentHandler;
import net.mcreator.toklar.init.ModBlocks;
import net.mcreator.toklar.init.ModRecipes;
import net.mcreator.toklar.util.LycanitePartEffectRegistry;
import net.minecraft.util.ResourceLocation;

@Mod(modid = Toklar.MODID, version = Toklar.VERSION)
public class Toklar {
    public static final String MODID = "toklar";
    public static final String VERSION = "1.1.9";
    public static final SimpleNetworkWrapper PACKET_HANDLER = NetworkRegistry.INSTANCE.newSimpleChannel("toklar:a");

    @SidedProxy(clientSide = "net.mcreator.toklar.ClientProxyToklar", serverSide = "net.mcreator.toklar.ServerProxyToklar")
    public static IProxyToklar proxy;

    @Mod.Instance(MODID)
    public static Toklar instance;

    public ElementsToklar elements = new ElementsToklar();

    public static Configuration config;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	
    	GameRegistry.registerTileEntity(
    		    net.mcreator.toklar.block.TileEntityMonsterCandle.class,
    		    new ResourceLocation(MODID, "monster_candle")
    		);
    	
        // Load and sync config values
        config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();

        ModConfig.enableSummonDamageBuffDebug = config.getBoolean(
            "enableSummonDamageBuffDebug",
            Configuration.CATEGORY_GENERAL,
            ModConfig.enableSummonDamageBuffDebug,
            "Enable debug messages for SummonDamageBuffHandler"
        );

        float bronzeMultiplier = config.getFloat(
            "summonDamageMultiplierBronze",
            Configuration.CATEGORY_GENERAL,
            ModConfig.getSummonDamageMultiplier(),
            1.0F, 100.0F,
            "Damage multiplier for summons when wearing full bronze armor"
        );
        ModConfig.setSummonDamageMultiplier(bronzeMultiplier);

        float toklarMultiplier = config.getFloat(
            "summonDamageMultiplierToklar",
            Configuration.CATEGORY_GENERAL,
            ModConfig.getToklarSummonDamageMultiplier(),
            1.0F, 100.0F,
            "Damage multiplier for summons when wearing full Toklar armor"
        );
        ModConfig.setToklarSummonDamageMultiplier(toklarMultiplier);


        float focusBonus = config.getFloat(
        	    "focusBonusDamagePerLevel",
        	    Configuration.CATEGORY_GENERAL,
        	    ModConfig.getFocusBonusDamagePerLevel(),
        	    0.0F, 10.0F,
        	    "Bonus damage per level of Focus enchantment (e.g., 0.1 = 10%)"
        	);
        	ModConfig.setFocusBonusDamagePerLevel(focusBonus);

        	float focusDuration = config.getFloat(
        	    "focusDurationSecondsPerLevel",
        	    Configuration.CATEGORY_GENERAL,
        	    ModConfig.getFocusDurationSecondsPerLevel(),
        	    0.0F, 600.0F,
        	    "Focus duration in seconds per level (e.g., 5 = 5 seconds per level)"
        	);
        	ModConfig.setFocusDurationSecondsPerLevel(focusDuration);
            if (config.hasChanged()) {
                config.save();
            }
        // Register your trade fixer to listen for events
            MinecraftForge.EVENT_BUS.register(new WeaponImbuementHandler());
            MinecraftForge.EVENT_BUS.register(new EnchantReach());
        MinecraftForge.EVENT_BUS.register(new StructureTradeFixer());
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(net.mcreator.toklar.init.EnchantmentInit.class);
        MinecraftForge.EVENT_BUS.register(new FocusRetargetHandler());
        MinecraftForge.EVENT_BUS.register(new FocusEnchantmentHandler());
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
        GameRegistry.registerWorldGenerator(elements, 5);
        GameRegistry.registerFuelHandler(elements);
        ModBlocks.preInit(event);
        elements.preInit(event);
        MinecraftForge.EVENT_BUS.register(elements);
        elements.getElements().forEach(element -> element.preInit(event));
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        elements.getElements().forEach(element -> element.init(event));
        proxy.init(event);
        ModRecipes.init();
        LootTableList.register(new ResourceLocation("toklar", "entities/clanky"));
        MinecraftForge.EVENT_BUS.register(new StructureTradeFixer());
        LycanitePartEffectRegistry.loadAll();
        System.out.println("[Mod Init] Registering SummonDamageBuffHandler");
        // SummonDamageBuffHandler.register();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        elements.getElements().forEach(element -> element.serverLoad(event));
        proxy.serverLoad(event);
    }

   
    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(elements.getBlocks().stream().map(Supplier::get).toArray(Block[]::new));
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(elements.getItems().stream().map(Supplier::get).toArray(Item[]::new));
    }

    @SubscribeEvent
    public void registerBiomes(RegistryEvent.Register<Biome> event) {
        event.getRegistry().registerAll(elements.getBiomes().stream().map(Supplier::get).toArray(Biome[]::new));
    }

    @SubscribeEvent
    public void registerEntities(RegistryEvent.Register<EntityEntry> event) {
        event.getRegistry().registerAll(elements.getEntities().stream().map(Supplier::get).toArray(EntityEntry[]::new));
    }

    @SubscribeEvent
    public void registerPotions(RegistryEvent.Register<Potion> event) {
        event.getRegistry().registerAll(elements.getPotions().stream().map(Supplier::get).toArray(Potion[]::new));
    }

    @SubscribeEvent
    public void registerSounds(RegistryEvent.Register<net.minecraft.util.SoundEvent> event) {
        elements.registerSounds(event);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void registerModels(ModelRegistryEvent event) {
        elements.getElements().forEach(element -> element.registerModels(event));
    }

    static {
        FluidRegistry.enableUniversalBucket();
    }

}
