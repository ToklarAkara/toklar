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
public class ItemBronzeArmor extends ElementsToklar.ModElement {
    @GameRegistry.ObjectHolder("toklar:bronzearmorhelmet")
    public static final Item helmet = null;
    @GameRegistry.ObjectHolder("toklar:bronzearmorbody")
    public static final Item body = null;
    @GameRegistry.ObjectHolder("toklar:bronzearmorlegs")
    public static final Item legs = null;
    @GameRegistry.ObjectHolder("toklar:bronzearmorboots")
    public static final Item boots = null;

    public ItemBronzeArmor(ElementsToklar instance) {
        super(instance, 3);
    }

    @Override
    public void initElements() {
        ItemArmor.ArmorMaterial enuma = EnumHelper.addArmorMaterial("BRONZEARMOR", "toklar:bronze", 23, new int[]{1, 4, 5, 1}, 8,
                (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("")), 0f);
        elements.items.add(() -> new ItemWithTooltip(enuma, 0, EntityEquipmentSlot.HEAD).setUnlocalizedName("bronzearmorhelmet")
                .setRegistryName("bronzearmorhelmet").setCreativeTab(CreativeTabs.COMBAT));
        elements.items.add(() -> new ItemWithTooltip(enuma, 0, EntityEquipmentSlot.CHEST).setUnlocalizedName("bronzearmorbody")
                .setRegistryName("bronzearmorbody").setCreativeTab(CreativeTabs.COMBAT));
        elements.items.add(() -> new ItemWithTooltip(enuma, 0, EntityEquipmentSlot.LEGS).setUnlocalizedName("bronzearmorlegs")
                .setRegistryName("bronzearmorlegs").setCreativeTab(CreativeTabs.COMBAT));
        elements.items.add(() -> new ItemWithTooltip(enuma, 0, EntityEquipmentSlot.FEET).setUnlocalizedName("bronzearmorboots")
                .setRegistryName("bronzearmorboots").setCreativeTab(CreativeTabs.COMBAT));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(helmet, 0, new ModelResourceLocation("toklar:bronzearmorhelmet", "inventory"));
        ModelLoader.setCustomModelResourceLocation(body, 0, new ModelResourceLocation("toklar:bronzearmorbody", "inventory"));
        ModelLoader.setCustomModelResourceLocation(legs, 0, new ModelResourceLocation("toklar:bronzearmorlegs", "inventory"));
        ModelLoader.setCustomModelResourceLocation(boots, 0, new ModelResourceLocation("toklar:bronzearmorboots", "inventory"));
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
