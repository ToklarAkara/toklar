package net.mcreator.toklar.init;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraft.item.crafting.Ingredient;
import net.mcreator.toklar.init.ModBlocks;

public class ModRecipes {
    public static void init() {
        GameRegistry.addShapedRecipe(
            new ResourceLocation("toklar", "imbuement_altar"),
            null,
            new ItemStack(ModBlocks.IMBUEMENT_ALTAR),
            "SSS", "SXS", "SSS",
            'S', Ingredient.fromItems(
                Item.getByNameOrId("lycanitesmobs:ashenstone"),
                Item.getByNameOrId("lycanitesmobs:lushstone"),
                Item.getByNameOrId("lycanitesmobs:demonstone"),
                Item.getByNameOrId("lycanitesmobs:aberrantstone"),
                Item.getByNameOrId("lycanitesmobs:streamstone"),
                Item.getByNameOrId("lycanitesmobs:desertstone"),
                Item.getByNameOrId("lycanitesmobs:shadowstone"),
                Item.getByNameOrId("lycanitesmobs:saltstone"),
                Item.getByNameOrId("lycanitesmobs:freshwaterstone"),
                Item.getByNameOrId("lycanitesmobs:mountainstone"),
                Item.getByNameOrId("lycanitesmobs:foreststone"),
                Item.getByNameOrId("lycanitesmobs:infernostone"),
                Item.getByNameOrId("lycanitesmobs:swampstone"),
                Item.getByNameOrId("lycanitesmobs:junglestone"),
                Item.getByNameOrId("lycanitesmobs:plainsstone"),
                Item.getByNameOrId("lycanitesmobs:voidstone"),
                Item.getByNameOrId("lycanitesmobs:bloodstone"),
                Item.getByNameOrId("lycanitesmobs:darkstone")
            ),
            'X', Ingredient.fromItems(Item.getByNameOrId("lycanitesmobs:soulstone"))
        );
    }
}