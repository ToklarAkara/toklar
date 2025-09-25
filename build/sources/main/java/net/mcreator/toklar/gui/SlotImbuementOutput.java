package net.mcreator.toklar.gui;

import net.mcreator.toklar.tile.TileEntityImbuementAltar;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotImbuementOutput extends Slot {
    private final TileEntityImbuementAltar altar;

    public SlotImbuementOutput(TileEntityImbuementAltar altar, IInventory inventory, int index, int xPosition, int yPosition) {
        super(inventory, index, xPosition, yPosition);
        this.altar = altar;
    }
    @Override
    public ItemStack getStack() {
        return altar.getPreviewOutput().copy();
    
    }
    @Override
    public boolean isItemValid(ItemStack stack) {
        return false; // Prevent inserting items into output slot
    }
    @Override
    public ItemStack decrStackSize(int amount) {
        ItemStack result = altar.getPreviewOutput().copy();
        altar.clearPreview(); 
        return result;
    }
    @Override
    public ItemStack onTake(EntityPlayer player, ItemStack stack) {
        altar.applyImbuementOnPickup(stack);
        return stack;
    }
}