package net.mcreator.toklar.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.mcreator.toklar.entity.EntityMonster;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

public class ItemSummonClanky extends Item {

    public ItemSummonClanky() {
        super();
        this.maxStackSize = 1;
        this.setMaxDamage(0);  // Optional: if you want it not to break
        this.setFull3D();
        // Set creative tab if you want here, e.g.
        // this.setCreativeTab(CreativeTabs.MISC);
    }
    @GameRegistry.ObjectHolder("toklar:ghastly_potion")
    public static final Item item = null;

    @SideOnly(Side.CLIENT)
    public static void registerModels() {
        ModelLoader.setCustomModelResourceLocation(
            item, 0,
            new ModelResourceLocation("toklar:ghastly_potion", "inventory")
        );
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return "Ghastly Potion";
    }

    // Use the drink animation
    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.DRINK;
    }

    // How long to use (same as potion)
    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 32;
    }

    // Called when right-clicked
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        player.setActiveHand(hand);
        return super.onItemRightClick(world, player, hand);
    }

    // Called when finished using (after 32 ticks of "drinking")
    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entity) {
        if (!world.isRemote && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;

            // Summon boss near player
            EntityMonster.EntityCustom boss = new EntityMonster.EntityCustom(world);
            BlockPos pos = player.getPosition();
            boss.setPosition(pos.getX() + 1, pos.getY(), pos.getZ() + 1);
            world.spawnEntity(boss);

            // Play summon sound
            world.playSound(null, pos, SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.HOSTILE, 1.0F, 1.0F);
        }

        if (entity instanceof EntityPlayer && !((EntityPlayer) entity).capabilities.isCreativeMode) {
            stack.shrink(1);
        }

        return stack;
    }
}
