package net.mcreator.toklar.tile;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import com.mujmajnkraft.bettersurvival.items.ItemCustomWeapon;
import electroblob.wizardry.item.ItemSpellBook;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Spell;
import net.mcreator.toklar.imbuement.HarvestImbuementBonus;
import net.mcreator.toklar.imbuement.HarvestImbuementHandler;
import net.mcreator.toklar.imbuement.SpellbookImbuementHelper;
import net.mcreator.toklar.imbuement.WeaponImbuementHandler;
import net.mcreator.toklar.util.LycanitePartEffectRegistry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
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
import net.minecraftforge.fml.common.registry.ForgeRegistries;
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

        if (!(isValidWeapon(weapon) || isValidTool(weapon) || isValidSpellbook(weapon)) 
        	    || !isValidMonsterPart(part) || !isValidCatalyst(catalyst)) {
        	    imbuementTimer = 0;
        	    clearPreview();
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
    private static Field SPELL_ID_FIELD;

    static {
        try {
            SPELL_ID_FIELD = Spell.class.getDeclaredField("id");
            SPELL_ID_FIELD.setAccessible(true);
        } catch (Exception e) {
            System.out.println("[Toklar] Failed to cache Spell.id field: " + e);
        }
    }

    private void generatePreview() {
        ItemStack item = inventory.get(0);
        ItemStack part = inventory.get(1);
        ItemStack catalyst = inventory.get(2);

        if (!isValidMonsterPart(part) || !isValidCatalyst(catalyst)) {
            System.out.println("[Toklar] Invalid part or catalyst");
            return;
        }

        String itemId = part.getItem().getRegistryName().getResourcePath();
        System.out.println("[Toklar] Part ID: " + itemId);

        NBTTagCompound partTag = part.getTagCompound();
        int partLevel = (partTag != null && partTag.hasKey("equipmentLevel")) ? partTag.getInteger("equipmentLevel") : 1;
        System.out.println("[Toklar] Part level: " + partLevel);

        ItemStack currentPreview = inventory.get(3);
        if (!currentPreview.isEmpty() && ItemStack.areItemsEqual(currentPreview, item)) {
            System.out.println("[Toklar] Preview already matches item, skipping");
            return;
        }

        ItemStack preview = item.copy();
        NBTTagCompound previewTag = preview.getOrCreateSubCompound("toklar_imbuement");

        boolean wroteAnything = false;

        if (isValidWeapon(item)) {
            System.out.println("[Toklar] Item is a valid weapon");
            List<LycanitePartEffectRegistry.ImbuementEffect> effects = LycanitePartEffectRegistry.getEffectsFor(itemId);
            System.out.println("[Toklar] Found " + effects.size() + " weapon effects");
            NBTTagList effectList = new NBTTagList();
            for (LycanitePartEffectRegistry.ImbuementEffect effect : effects) {
                if (!effect.appliesToLevel(partLevel)) continue;
                NBTTagCompound tag = new NBTTagCompound();
                tag.setString("effect", effect.type);
                tag.setInteger("amplifier", effect.strength);
                tag.setInteger("duration", effect.duration);
                tag.setString("target", effect.target);
                effectList.appendTag(tag);
            }
            if (effectList.tagCount() > 0) {
                previewTag.setTag("effects", effectList);
                wroteAnything = true;
                System.out.println("[Toklar] Wrote weapon effects to preview");
            }
        }

        if (isValidTool(item)) {
            System.out.println("[Toklar] Item is a valid tool");
            List<HarvestImbuementBonus> bonuses = LycanitePartEffectRegistry.getHarvestBonusesFor(itemId);
            System.out.println("[Toklar] Found " + bonuses.size() + " harvest bonuses");
            NBTTagList bonusList = new NBTTagList();
            for (HarvestImbuementBonus bonus : bonuses) {
                if (!bonus.appliesToLevel(partLevel)) {
                    System.out.println("[Toklar] Bonus skipped due to level");
                    continue;
                }
                if (!HarvestImbuementHandler.matchesHarvestToolType(item, bonus.harvestType)) {
                    System.out.println("[Toklar] Bonus skipped due to tool mismatch: " + bonus.harvestType);
                    continue;
                }
                if (bonus.range[0] == 1 && bonus.range[1] == 1 && bonus.range[2] == 1) {
                    System.out.println("[Toklar] Bonus skipped due to trivial range");
                    continue;
                }

                NBTTagCompound tag = new NBTTagCompound();
                tag.setString("type", bonus.harvestType);
                tag.setString("shape", bonus.shape);
                tag.setInteger("speed", bonus.speed);
                tag.setIntArray("range", bonus.range);
                bonusList.appendTag(tag);
            }
            if (bonusList.tagCount() > 0) {
                previewTag.setTag("bonuses", bonusList);
                wroteAnything = true;
                System.out.println("[Toklar] Wrote harvest bonuses to preview");
            }
        }
        if (isValidSpellbook(item)) {
            System.out.println("[Toklar] Item is a ruined spellbook");
            Spell rawSpell = SpellbookImbuementHelper.getSpellForPart(itemId, partLevel);
            if (rawSpell != null) {
                int spellIndex = rawSpell.metadata(); // ✅ Canonical damage value for spellbook
                ItemStack spellbook = new ItemStack(WizardryItems.spell_book, 1, spellIndex);
                System.out.println("[Toklar] Preview spellbook created with spell: " + rawSpell.getRegistryName() + " (Index: " + spellIndex + ")");
                wroteAnything = true;

                preview = spellbook;
                inventory.set(3, spellbook); // ✅ Assign to preview slot
            } else {
                System.out.println("[Toklar] No matching spell found for part: " + itemId + " level: " + partLevel);
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

        NBTTagCompound imbueTag = stack.getOrCreateSubCompound("toklar_imbuement");
        boolean wroteAnything = false;

        if (isValidWeapon(stack)) {
            List<LycanitePartEffectRegistry.ImbuementEffect> effects = LycanitePartEffectRegistry.getEffectsFor(itemId);
            NBTTagList effectList = new NBTTagList();
            for (LycanitePartEffectRegistry.ImbuementEffect effect : effects) {
                if (!effect.appliesToLevel(partLevel)) continue;
                NBTTagCompound tag = new NBTTagCompound();
                tag.setString("effect", effect.type);
                tag.setInteger("amplifier", effect.strength);
                tag.setInteger("duration", effect.duration);
                tag.setString("target", effect.target);
                effectList.appendTag(tag);
            }
            if (effectList.tagCount() > 0) {
                imbueTag.setTag("effects", effectList);
                wroteAnything = true;
            }
        }

        if (isValidTool(stack)) {
            List<HarvestImbuementBonus> bonuses = LycanitePartEffectRegistry.getHarvestBonusesFor(itemId);
            NBTTagList bonusList = new NBTTagList();
            for (HarvestImbuementBonus bonus : bonuses) {
                if (!bonus.appliesToLevel(partLevel)) continue;
                if (!HarvestImbuementHandler.matchesHarvestToolType(stack, bonus.harvestType)) continue;
                if (bonus.range[0] == 1 && bonus.range[1] == 1 && bonus.range[2] == 1) continue;

                NBTTagCompound tag = new NBTTagCompound();
                tag.setString("type", bonus.harvestType);
                tag.setString("shape", bonus.shape);
                tag.setInteger("speed", bonus.speed);
                tag.setIntArray("range", bonus.range);
                bonusList.appendTag(tag);
            }
            if (bonusList.tagCount() > 0) {
                imbueTag.setTag("bonuses", bonusList);
                wroteAnything = true;
            }
        }
        if (isValidSpellbook(stack)) {
            System.out.println("[Toklar] Item is a ruined spellbook");
            String partId = part.getItem().getRegistryName().getResourcePath();
            Spell rawSpell = SpellbookImbuementHelper.getSpellForPart(partId, partLevel);

            if (rawSpell != null) {
                int spellIndex = rawSpell.metadata(); // ✅ Canonical damage value
                ItemStack spellbook = new ItemStack(WizardryItems.spell_book, 1, spellIndex);
                System.out.println("[Toklar] Created usable spellbook with spell: " + rawSpell.getRegistryName() + " (Index: " + spellIndex + ")");
                inventory.set(3, spellbook); // ✅ Assign to output slot
                wroteAnything = true;
            } else {
                System.out.println("[Toklar] No matching spell found for part: " + partId + " level: " + partLevel);
            }
        }

        // ✅ Fallback for tools/weapons
        if (!wroteAnything) {
            inventory.set(3, stack); // Assign original item to output
            wroteAnything = true;
        }

        // ✅ Clear inputs if anything was written to slot 3
        if (wroteAnything) {
            inventory.set(0, ItemStack.EMPTY);
            inventory.set(1, ItemStack.EMPTY);
            inventory.set(2, ItemStack.EMPTY);
        }

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
        return stack.getItem() instanceof ItemSword
            || stack.getItem() instanceof ItemAxe
            || stack.getItem() instanceof ItemCustomWeapon;
    }

    public boolean isValidTool(ItemStack stack) {
        if (stack.isEmpty()) return false;

        ResourceLocation id = stack.getItem().getRegistryName();
        if (id == null || !id.getResourceDomain().equals("tconstruct")) return false;

        String path = id.getResourcePath();
        return path.matches(".*(hammer|pickaxe|excavator|shovel|mattock|hatchet|lumberaxe|kama|scythe).*");
    }
    public boolean isValidSpellbook(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.getItem().getRegistryName().getResourcePath().equals("ruined_spell_book");
    }
    public boolean isValidMonsterPart(ItemStack stack) {
        if (stack.isEmpty()) return false;
        String itemId = stack.getItem().getRegistryName().getResourcePath();
        return LycanitePartEffectRegistry.hasEffects(itemId)
            || LycanitePartEffectRegistry.hasHarvestBonus(itemId);
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