package net.mcreator.toklar.tile;

import java.util.List;
import java.util.Set;

import com.mujmajnkraft.bettersurvival.items.ItemCustomWeapon;

import net.mcreator.toklar.imbuement.WeaponImbuementHandler;
import net.mcreator.toklar.util.LycanitePartEffectRegistry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;

public class TileEntityImbuementAltar extends TileEntity implements IInventory, ITickable {

    private NonNullList<ItemStack> inventory = NonNullList.withSize(4, ItemStack.EMPTY);
    private int imbuementTimer = 0;
    private static final int IMBUEMENT_DELAY = 0;


    private String pendingImbuementType = null;

    @Override
    public void update() {
        if (world.isRemote) return;
      //  System.out.println("[Toklar] update() ticked");
        ItemStack weapon = inventory.get(0);
        ItemStack part = inventory.get(1);
        ItemStack catalyst = inventory.get(2);

        if (!isValidWeapon(weapon) || !isValidMonsterPart(part) || !isValidCatalyst(catalyst)) {
            imbuementTimer = 0;
            clearPreview();
        //    if (!isValidWeapon(weapon)) System.out.println("[Toklar] Weapon invalid");
         //   if (!isValidMonsterPart(part)) System.out.println("[Toklar] Monster part invalid");
         //   if (!isValidCatalyst(catalyst)) System.out.println("[Toklar] Catalyst invalid");
            return;
        }

        imbuementTimer++;

        if (imbuementTimer % 10 == 0) {
            world.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE,
                pos.getX() + 0.5 + (world.rand.nextDouble() - 0.5),
                pos.getY() + 1.2,
                pos.getZ() + 0.5 + (world.rand.nextDouble() - 0.5),
                0, 0.1, 0);
        }

        if (imbuementTimer >= IMBUEMENT_DELAY) {
            ItemStack currentPreview = inventory.get(3);
 

            if (currentPreview.isEmpty() || !ItemStack.areItemsEqual(currentPreview, weapon)) {
                generatePreview();
            }

            imbuementTimer = 0;
        }
    }

    private void generatePreview() {
        ItemStack weapon = inventory.get(0);
        ItemStack part = inventory.get(1);
        ItemStack catalyst = inventory.get(2);

        if (!isValidWeapon(weapon) || !isValidMonsterPart(part) || !isValidCatalyst(catalyst)) return;

        String itemId = part.getItem().getRegistryName().getResourcePath();
        NBTTagCompound partTag = part.getTagCompound();
        int partLevel = (partTag != null && partTag.hasKey("equipmentLevel")) ? partTag.getInteger("equipmentLevel") : 1;

        List<LycanitePartEffectRegistry.ImbuementEffect> effects = LycanitePartEffectRegistry.getEffectsFor(itemId);
        if (effects.isEmpty()) return;

        ItemStack currentPreview = inventory.get(3);
        if (currentPreview.isEmpty() || !ItemStack.areItemsEqual(currentPreview, weapon)) {
            ItemStack preview = weapon.copy();
            NBTTagCompound previewTag = preview.getOrCreateSubCompound("toklar_imbuement");
            NBTTagList effectList = new NBTTagList();

            for (LycanitePartEffectRegistry.ImbuementEffect effect : effects) {
                if (!effect.appliesToLevel(partLevel)) continue;

                NBTTagCompound singleEffect = new NBTTagCompound();
                singleEffect.setString("effect", effect.type);
                singleEffect.setInteger("amplifier", effect.strength);
                singleEffect.setInteger("duration", effect.duration);
                singleEffect.setString("target", effect.target);
                effectList.appendTag(singleEffect);
            }

            if (effectList.tagCount() > 0) {
                previewTag.setTag("effects", effectList);
                inventory.set(3, preview);
                markDirty();
                world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
            }
        }
    }



    public void applyImbuementOnPickup(ItemStack stack) {
        ItemStack part = inventory.get(1);
        ItemStack catalyst = inventory.get(2);

        if (part.isEmpty() || catalyst.isEmpty()) return;

        String itemId = part.getItem().getRegistryName().getResourcePath();
        NBTTagCompound partTag = part.getTagCompound();
        int partLevel = (partTag != null && partTag.hasKey("equipmentLevel")) ? partTag.getInteger("equipmentLevel") : 1;

        List<LycanitePartEffectRegistry.ImbuementEffect> effects = LycanitePartEffectRegistry.getEffectsFor(itemId);
        if (effects.isEmpty()) return;

        NBTTagCompound imbueTag = stack.getOrCreateSubCompound("toklar_imbuement");
        NBTTagList effectList = new NBTTagList();

        for (LycanitePartEffectRegistry.ImbuementEffect effect : effects) {
            if (!effect.appliesToLevel(partLevel)) continue;

            NBTTagCompound singleEffect = new NBTTagCompound();
            singleEffect.setString("effect", effect.type);
            singleEffect.setInteger("amplifier", effect.strength);
            singleEffect.setInteger("duration", effect.duration);
            singleEffect.setString("target", effect.target);
            effectList.appendTag(singleEffect);
        }

        imbueTag.setTag("effects", effectList);

        inventory.set(3, stack); // Replace preview with imbued weapon
        inventory.set(0, ItemStack.EMPTY);
        inventory.set(1, ItemStack.EMPTY);
        inventory.set(2, ItemStack.EMPTY);

        world.playSound(null, pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0F, 1.0F);
        markDirty();
    }


    public ItemStack getPreviewOutput() {
        return inventory.get(3);
    
    }

    public String getPendingImbuementType() {
        return pendingImbuementType;
    }

    public void clearPreview() {
        inventory.set(3, ItemStack.EMPTY);
        pendingImbuementType = null;
        markDirty();
    }


    public void dropInventory(World world, BlockPos pos) {
        for (ItemStack stack : inventory) {
            if (!stack.isEmpty()) {
                world.spawnEntity(new EntityItem(world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, stack));
            }
        }
    }

    // IInventory methods
    @Override public int getSizeInventory() { return inventory.size(); }
    @Override public boolean isEmpty() { return inventory.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getStackInSlot(int index) { return inventory.get(index); }
    @Override public ItemStack decrStackSize(int index, int count) { return ItemStackHelper.getAndSplit(inventory, index, count); }
    @Override public ItemStack removeStackFromSlot(int index) { return ItemStackHelper.getAndRemove(inventory, index); }
    @Override public void setInventorySlotContents(int index, ItemStack stack) {
        inventory.set(index, stack);
        if (stack.getCount() > getInventoryStackLimit()) stack.setCount(getInventoryStackLimit());
        markDirty();
    }
    @Override public int getInventoryStackLimit() { return 64; }
    @Override public boolean isUsableByPlayer(EntityPlayer player) {
        return world.getTileEntity(pos) == this && player.getDistanceSq(pos) <= 64;
    }
    @Override public void openInventory(EntityPlayer player) {}
    @Override public void closeInventory(EntityPlayer player) {}
    @Override public boolean isItemValidForSlot(int index, ItemStack stack) { return true; }
    @Override public int getField(int id) { return 0; }
    @Override public void setField(int id, int value) {}
    @Override public int getFieldCount() { return 0; }
    @Override public void clear() { inventory.clear(); }

    // IWorldNameable methods
    @Override public String getName() { return "container.imbuement_altar"; }
    @Override public boolean hasCustomName() { return false; }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
    	compound.setString("PendingImbuementType", pendingImbuementType == null ? "" : pendingImbuementType);
        super.writeToNBT(compound);
        ItemStackHelper.saveAllItems(compound, inventory);
        compound.setInteger("ImbuementTimer", imbuementTimer);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
    	compound.setString("PendingImbuementType", pendingImbuementType == null ? "" : pendingImbuementType);
    	String type = compound.getString("PendingImbuementType");
    	pendingImbuementType = type.isEmpty() ? null : type;
        super.readFromNBT(compound);
        inventory = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, inventory);
        imbuementTimer = compound.getInteger("ImbuementTimer");
    }

    public boolean isValidWeapon(ItemStack stack) {
        if (stack.isEmpty()) return false;

        return stack.getItem() instanceof net.minecraft.item.ItemSword
            || stack.getItem() instanceof net.minecraft.item.ItemAxe
            || stack.getItem() instanceof ItemCustomWeapon;
    }

    public boolean isValidMonsterPart(ItemStack stack) {
        if (stack.isEmpty()) return false;
        String itemId = stack.getItem().getRegistryName().getResourcePath();
        return LycanitePartEffectRegistry.hasEffects(itemId);
    }



    public static boolean isValidCatalyst(ItemStack stack) {
        if (stack.isEmpty()) return false;
        ResourceLocation id = stack.getItem().getRegistryName();
        return id != null &&
               id.getResourceDomain().equals("lycanitesmobs") &&
               id.getResourcePath().contains("charge");
    }
    
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new SPacketUpdateTileEntity(this.pos, 1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }
}