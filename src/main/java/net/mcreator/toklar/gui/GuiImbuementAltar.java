package net.mcreator.toklar.gui;

import net.mcreator.toklar.tile.TileEntityImbuementAltar;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiImbuementAltar extends GuiContainer {

    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("toklar:textures/gui/imbuement_altar.png");
    private final TileEntityImbuementAltar altar;

    public GuiImbuementAltar(TileEntityImbuementAltar altar, InventoryPlayer playerInv) {
        super(new ContainerImbuementAltar(altar, playerInv));
        this.altar = altar;
        this.xSize = 176;
        this.ySize = 166;
    
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        ItemStack preview = altar.getPreviewOutput();
        String effect = altar.getPendingImbuementType();

        // Fallback: extract effect from preview item NBT if needed
        if ((effect == null || effect.isEmpty()) && preview.hasTagCompound()) {
            NBTTagCompound tag = preview.getSubCompound("toklar_imbuement");
            if (tag != null && tag.hasKey("effect")) {
                effect = tag.getString("effect");
            }
        }

        if (effect != null && !effect.isEmpty()) {
            int slotX = 125;
            int slotY = 31;
            int relX = mouseX - this.guiLeft;
            int relY = mouseY - this.guiTop;

            if (relX >= slotX && relX < slotX + 18 && relY >= slotY && relY < slotY + 18) {
                this.drawHoveringText("Imbuement: " + effect, relX, relY);
            }
        }

        // Static label in top-right corner
        this.fontRenderer.drawString("Imbuement", this.xSize - 54, 6, 0x404040);
    }
    

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground(); // Fixes the missing dark backdrop
        super.drawScreen(mouseX, mouseY, partialTicks);

        // Prevent default rendering of slot 3 (commented out for stability)
        // if (this.inventorySlots.getSlot(3) != null) {
        //     this.inventorySlots.getSlot(3).putStack(ItemStack.EMPTY);
        // }

        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(GUI_TEXTURE);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);

        //  Manually render scaled preview item

            ItemStack preview = altar.getPreviewOutput();
            if (!preview.isEmpty()) {
                int slotX = x + 125;
                int slotY = y + 31;

                GlStateManager.pushMatrix();
                GlStateManager.translate(slotX, slotY, 0);
            //    GlStateManager.scale(1.5F, 1.5F, 1.0F);
                RenderHelper.enableGUIStandardItemLighting();
                this.itemRender.renderItemAndEffectIntoGUI(preview, 0, 0);
                this.itemRender.renderItemOverlayIntoGUI(this.fontRenderer, preview, 0, 0, null);
                RenderHelper.disableStandardItemLighting();
                GlStateManager.popMatrix();
            }
        }
    }
