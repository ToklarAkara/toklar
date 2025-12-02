package net.mcreator.toklar.item.bauble;

import baubles.api.IBauble;

import java.util.List;

import baubles.api.BaubleType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.resources.I18n;

public class ItemToklarsGirdle extends Item implements IBauble {

    
    @GameRegistry.ObjectHolder("toklar:toklars_girdle")
    public static final Item item = null;

    public ItemToklarsGirdle() {
        super();
        this.setRegistryName("toklars_girdle");
        this.setUnlocalizedName("toklars_girdle");
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabs.MISC);
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.BELT; // explicitly a belt
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase player) {
        // No tick-based logic — effect handled in event hook
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