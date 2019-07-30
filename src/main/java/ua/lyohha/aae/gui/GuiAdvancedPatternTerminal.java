package ua.lyohha.aae.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import ua.lyohha.aae.TilyEntity.AdvancedPatternTerminalTileEntity;
import ua.lyohha.aae.TilyEntity.MultiblockMEInterfaceControllerTileEntity;
import ua.lyohha.aae.inventory.ContainerAdvancedPatternTerminal;
import ua.lyohha.aae.inventory.ContainerMultiblockMEInterfaceController;
import ua.lyohha.aae.network.Message;
import ua.lyohha.aae.network.Network;

import java.awt.*;

public class GuiAdvancedPatternTerminal extends GuiContainer {

    AdvancedPatternTerminalTileEntity tileEntity;

    public GuiAdvancedPatternTerminal(InventoryPlayer inventoryPlayer, AdvancedPatternTerminalTileEntity tileEntity) {
        super(new ContainerAdvancedPatternTerminal(inventoryPlayer, tileEntity));
        this.tileEntity = tileEntity;
        xSize = 176;
        ySize = 188;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int x, int y) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("aae:textures/guis/AdvancedPatternTerminal.png"));
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        String s = I18n.format(tileEntity.getInventoryName());
        fontRendererObj.drawString(s, xSize / 2 - fontRendererObj.getStringWidth(s) / 2, 5, Color.darkGray.getRGB());
        for (int i = 0, num = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++, num++) {
                fontRendererObj.drawString(Integer.toString(num + 1), 16 + j * 18 - fontRendererObj.getStringWidth(Integer.toString(num + 1)) / 2, 26 + i * 18, Color.darkGray.getRGB());
            }
        }
    }

    @Override
    public void initGui() {
        buttonList.clear();
        int posX = (width - xSize) / 2;
        int posY = (height - ySize) / 2;
        buttonList.add(new GuiImgButton(0, posX + 152, posY + 49, 16, 16).setResourceLocation(new ResourceLocation("aae:textures/guis/AdvancedPatternTerminal.png"), 16, 16, 176, 0));
        buttonList.add(new GuiImgButton(1, posX + 134, posY + 20, 8, 8).setResourceLocation(new ResourceLocation("aae:textures/guis/AdvancedPatternTerminal.png"), 8, 8, 192, 0));
        super.initGui();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id)
        {
            case 0:
                Network.getNetworkWrapper().sendToServer(new Message(Message.MessageType.CREATE_PATTERN));
                break;
            case 1:
                Network.getNetworkWrapper().sendToServer(new Message(Message.MessageType.CLEAR_PATTERN));
                break;
        }
    }
}
