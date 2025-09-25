package net.mcreator.toklar.gui;

import net.mcreator.toklar.tile.TileEntityImbuementAltar;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiHandler implements IGuiHandler {

    public static final int IMBUEMENT_ALTAR_GUI_ID = 1;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        System.out.println("[Toklar]Server: getServerGuiElement called with ID = " + ID);
        if (ID == IMBUEMENT_ALTAR_GUI_ID) {
            TileEntityImbuementAltar tile = (TileEntityImbuementAltar) world.getTileEntity(new BlockPos(x, y, z));
            return new ContainerImbuementAltar(tile, player.inventory);
        }
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        System.out.println("[Toklar]Client: getClientGuiElement called with ID = " + ID);
        if (ID == IMBUEMENT_ALTAR_GUI_ID) {
            TileEntityImbuementAltar tile = (TileEntityImbuementAltar) world.getTileEntity(new BlockPos(x, y, z));
            return new GuiImbuementAltar(tile, player.inventory);
        }
        return null;
    }
}