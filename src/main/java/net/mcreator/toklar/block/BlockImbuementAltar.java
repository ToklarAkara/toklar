package net.mcreator.toklar.block;

import net.mcreator.toklar.tile.TileEntityImbuementAltar;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;

public class BlockImbuementAltar extends BlockContainer {

	public BlockImbuementAltar() {
	    super(Material.ROCK);
	    this.setUnlocalizedName("imbuement_altar");
	    this.setHardness(3.0F);
	    this.setResistance(10.0F);
	    this.setHarvestLevel("pickaxe", 1);
	    this.setLightLevel(0.5F);
	}

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state,
                                    EntityPlayer playerIn, EnumHand hand, EnumFacing facing,
                                    float hitX, float hitY, float hitZ) {
    	System.out.println("Imbuement Altar was right-clicked");
        if (!worldIn.isRemote) {
            playerIn.openGui(net.mcreator.toklar.Toklar.instance,
                             net.mcreator.toklar.gui.GuiHandler.IMBUEMENT_ALTAR_GUI_ID,
                             worldIn, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityImbuementAltar();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.SOLID;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileEntityImbuementAltar) {
            ((TileEntityImbuementAltar) tile).dropInventory(worldIn, pos);
        }
        super.breakBlock(worldIn, pos, state);
    }
}