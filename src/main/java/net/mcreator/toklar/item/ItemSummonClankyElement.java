package net.mcreator.toklar.item;

import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;

import net.mcreator.toklar.ElementsToklar;
import net.mcreator.toklar.item.ItemSummonClanky;

@ElementsToklar.ModElement.Tag
public class ItemSummonClankyElement extends ElementsToklar.ModElement {

    public static Item block;

    public ItemSummonClankyElement(ElementsToklar elements) {
        super(elements, 5); // sortid 5 or next available
    }

    @Override
    public void initElements() {
        elements.items.add(() -> {
            block = (new ItemSummonClanky())
                .setRegistryName("toklar", "ghastly_potion")
                .setUnlocalizedName("ghastly_potion")
                .setCreativeTab(CreativeTabs.MISC);
            return block;
        });
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerModels(net.minecraftforge.client.event.ModelRegistryEvent event) {
        net.minecraftforge.client.model.ModelLoader.setCustomModelResourceLocation(
            block, 0,
            new net.minecraft.client.renderer.block.model.ModelResourceLocation("toklar:ghastly_potion", "inventory")
        );
    }
}
