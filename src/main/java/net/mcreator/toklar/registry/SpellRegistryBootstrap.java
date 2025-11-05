package net.mcreator.toklar.registry;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraft.util.ResourceLocation;
import electroblob.wizardry.spell.Spell;

//this is added to fix spell overlap with more than 256 spells in ebwizardry, didnt work

//@Mod.EventBusSubscriber(modid = "toklar") 
//public class SpellRegistryBootstrap {

 //   @SubscribeEvent
  //  public static void onNewRegistry(RegistryEvent.NewRegistry event) {
  //      new RegistryBuilder<Spell>()
  //          .setName(new ResourceLocation("ebwizardry", "spells"))
 //           .setType(Spell.class)
 //           .setIDRange(0, 4096)
//            .create();
 //   }
//}