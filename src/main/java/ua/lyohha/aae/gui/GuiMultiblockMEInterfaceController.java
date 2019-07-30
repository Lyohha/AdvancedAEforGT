package ua.lyohha.aae.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import ua.lyohha.aae.TilyEntity.MultiblockMEInterfaceControllerTileEntity;
import ua.lyohha.aae.inventory.ContainerMultiblockMEInterfaceController;

import java.awt.*;

public class GuiMultiblockMEInterfaceController extends GuiContainer {

    MultiblockMEInterfaceControllerTileEntity tileEntity;

    public GuiMultiblockMEInterfaceController(InventoryPlayer inventoryPlayer, MultiblockMEInterfaceControllerTileEntity tileEntity) {
        super(new ContainerMultiblockMEInterfaceController(inventoryPlayer, tileEntity));
        this.tileEntity = tileEntity;
        xSize = 176;
        ySize = 188;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int x, int y) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("aae:textures/guis/MultiblockMEInterfaceController.png"));
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        String s = I18n.format(tileEntity.getInventoryName());
        fontRendererObj.drawString(s, xSize / 2 - fontRendererObj.getStringWidth(s) / 2, 5, Color.darkGray.getRGB());
    }
}
