package net.mcreator.toklar.item.bauble;

import baubles.api.IBauble;

import java.util.List;

import baubles.api.BaubleType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemToklarsJewel extends Item implements IBauble {

    
    @GameRegistry.ObjectHolder("toklar:toklars_jewel_1")
    public static final Item item = null;

    public ItemToklarsJewel() {
        super();
        this.setRegistryName("toklars_jewel_1");
        this.setUnlocalizedName("toklars_jewel_1");
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabs.MISC);
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.CHARM; // explicitly a charm
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase player) {
        // No tick-based logic — damage reduction will be handled in the event hook
    }
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);

        
        tooltip.add(TextFormatting.RED + "Only works on humans");
        tooltip.add(TextFormatting.RED + "Requires Summoner Armor");

        
        tooltip.add(TextFormatting.GRAY + I18n.format(this.getUnlocalizedName() + ".desc"));
    }
}