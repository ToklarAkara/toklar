package net.mcreator.toklar.gui;

import net.mcreator.toklar.tile.TileEntityImbuementAltar;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.SlotCrafting;


public class ContainerImbuementAltar extends Container {
    private final TileEntityImbuementAltar altar;
    private final IInventory tile;

    public ContainerImbuementAltar(IInventory tile, InventoryPlayer playerInv) {
        this.tile = tile;
        this.altar = (TileEntityImbuementAltar) tile;

     // Weapon slot
        this.addSlotToContainer(new Slot(tile, 0, 45, 17) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return altar.isValidWeapon(stack);
            }

            @Override
            public int getSlotStackLimit() {
                return 1;
            }
        });

        // Monster part slot
        this.addSlotToContainer(new Slot(tile, 1, 67, 17) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return altar.isValidMonsterPart(stack);
            }

            @Override
            public int getSlotStackLimit() {
                return 1;
            }
        });

        // Catalyst slot
        this.addSlotToContainer(new Slot(tile, 2, 56, 53) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return altar.isValidCatalyst(stack);
            }

            @Override
            public int getSlotStackLimit() {
                return 1;
            }
        });

        this.addSlotToContainer(new SlotImbuementOutput(altar, tile, 3, 125, 31)); // Output

        // Player inventory (slots 9–35)
        for (int row = 0; row < 3; ++row)
            for (int col = 0; col < 9; ++col)
                this.addSlotToContainer(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));

        // Hotbar (slots 0–8)
        for (int i = 0; i < 9; ++i)
            this.addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 142));
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return tile.isUsableByPlayer(playerIn);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack stackInSlot = slot.getStack();
            itemstack = stackInSlot.copy();

            int altarSlotCount = 4;
            int playerInventoryStart = altarSlotCount;
            int playerInventoryEnd = this.inventorySlots.size();

            // If clicked slot is in altar inventory
            if (index < altarSlotCount) {
                // Prevent shift-clicking output slot
                if (index == 3) return ItemStack.EMPTY;

                if (!this.mergeItemStack(stackInSlot, playerInventoryStart, playerInventoryEnd, true))
                    return ItemStack.EMPTY;
            } else {
                // Try to place into altar slots
                if (!this.mergeItemStack(stackInSlot, 0, altarSlotCount - 1, false))
                    return ItemStack.EMPTY;
            }

            if (stackInSlot.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }
}