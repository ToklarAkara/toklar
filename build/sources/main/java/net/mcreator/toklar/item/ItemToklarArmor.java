
package net.mcreator.toklar.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.Item;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.world.World;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.item.ItemStack;

import java.util.List;

import net.mcreator.toklar.ElementsToklar;

@ElementsToklar.ModElement.Tag
public class ItemToklarArmor extends ElementsToklar.ModElement {
    @GameRegistry.ObjectHolder("toklar:toklararmorhelmet")
    public static final Item helmet = null;
    @GameRegistry.ObjectHolder("toklar:toklararmorbody")
    public static final Item body = null;
    @GameRegistry.ObjectHolder("toklar:toklararmorlegs")
    public static final Item legs = null;
    @GameRegistry.ObjectHolder("toklar:toklararmorboots")
    public static final Item boots = null;

    public ItemToklarArmor(ElementsToklar instance) {
        super(instance, 1);
    }

    @Override
    public void initElements() {
        ItemArmor.ArmorMaterial enuma = EnumHelper.addArmorMaterial("TOKLARARMOR", "toklar:toklar", 15, new int[]{4, 12, 10, 4}, 9,
                (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("")), 2f);
        elements.items.add(() -> new ItemWithTooltip(enuma, 0, EntityEquipmentSlot.HEAD).setUnlocalizedName("toklararmorhelmet")
                .setRegistryName("toklararmorhelmet").setCreativeTab(CreativeTabs.COMBAT));
        elements.items.add(() -> new ItemWithTooltip(enuma, 0, EntityEquipmentSlot.CHEST).setUnlocalizedName("toklararmorbody")
                .setRegistryName("toklararmorbody").setCreativeTab(CreativeTabs.COMBAT));
        elements.items.add(() -> new ItemWithTooltip(enuma, 0, EntityEquipmentSlot.LEGS).setUnlocalizedName("toklararmorlegs")
                .setRegistryName("toklararmorlegs").setCreativeTab(CreativeTabs.COMBAT));
        elements.items.add(() -> new ItemWithTooltip(enuma, 0, EntityEquipmentSlot.FEET).setUnlocalizedName("toklararmorboots")
                .setRegistryName("toklararmorboots").setCreativeTab(CreativeTabs.COMBAT));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(helmet, 0, new ModelResourceLocation("toklar:toklararmorhelmet", "inventory"));
        ModelLoader.setCustomModelResourceLocation(body, 0, new ModelResourceLocation("toklar:toklararmorbody", "inventory"));
        ModelLoader.setCustomModelResourceLocation(legs, 0, new ModelResourceLocation("toklar:toklararmorlegs", "inventory"));
        ModelLoader.setCustomModelResourceLocation(boots, 0, new ModelResourceLocation("toklar:toklararmorboots", "inventory"));
    }

    // Inner subclass to add tooltip text to each armor item
    public static class ItemWithTooltip extends ItemArmor {
        public ItemWithTooltip(ArmorMaterial material, int renderIndex, EntityEquipmentSlot slot) {
            super(material, renderIndex, slot);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
            super.addInformation(stack, world, tooltip, flag);
            tooltip.add(TextFormatting.RED + "Only works on humans");
        }
    }
}
