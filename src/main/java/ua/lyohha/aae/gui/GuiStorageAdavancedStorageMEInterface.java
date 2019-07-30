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

@SideOnly(Side.CLIENT)
public class GuiStorageAdavancedStorageMEInterface extends GuiContainer {
    private AdvancedStorageMEInterfaceTileEntity advancedStorageMEInterfaceTileEntity;

    public GuiStorageAdavancedStorageMEInterface(InventoryPlayer inventoryPlayer, AdvancedStorageMEInterfaceTileEntity advancedStorageMEInterfaceTileEntity) {
        super(new ContainerAdvancedStorageMEInterface(inventoryPlayer, advancedStorageMEInterfaceTileEntity));
        xSize = 176;
        ySize = 251;
        this.advancedStorageMEInterfaceTileEntity = advancedStorageMEInterfaceTileEntity;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int x, int y)
    {
        Minecraft.getMinecraft().getTextureManager().bindTexture( new ResourceLocation("aae:textures/guis/advancedinterface.png"));
        drawTexturedModalRect(guiLeft,guiTop,0,0,xSize,ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y)
    {

    }
}
