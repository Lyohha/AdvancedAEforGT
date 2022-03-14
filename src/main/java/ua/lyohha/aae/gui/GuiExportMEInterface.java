package ua.lyohha.aae.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import ua.lyohha.aae.TilyEntity.AdvancedStorageMEInterfaceTileEntity;
import ua.lyohha.aae.inventory.ContainerAdvancedStorageMEInterface;
import ua.lyohha.aae.inventory.ContainerExportMEInterface;
import ua.lyohha.aae.part.PartExportMEInterface;

import java.awt.*;

@SideOnly(Side.CLIENT)
public class GuiExportMEInterface extends GuiContainer {
    private PartExportMEInterface tileEntity;

    public GuiExportMEInterface(InventoryPlayer inventoryPlayer, PartExportMEInterface tileEntity) {
        super(new ContainerExportMEInterface(inventoryPlayer, tileEntity));
        xSize = 176;
        ySize = 151;
        this.tileEntity = tileEntity;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int x, int y)
    {
        Minecraft.getMinecraft().getTextureManager().bindTexture( new ResourceLocation("aae:textures/guis/ExportMEInterface.png"));
        drawTexturedModalRect(guiLeft,guiTop,0,0,xSize,ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y)
    {
        String s = I18n.format(tileEntity.getInventoryName());
        fontRendererObj.drawString(s, 5, 5, Color.darkGray.getRGB());

    }
}
