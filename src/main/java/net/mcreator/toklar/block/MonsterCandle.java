package net.mcreator.toklar.block;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class MonsterCandle extends Block implements ITileEntityProvider {
    public MonsterCandle() {
        super(Material.ROCK);
        this.setHardness(1.5F);
        this.setResistance(10F);
    }
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }
    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    // Required by ITileEntityProvider in 1.12.2
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityMonsterCandle();
    }
}