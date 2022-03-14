package ua.lyohha.aae.gui;

import appeng.client.gui.widgets.GuiNumberBox;
import appeng.core.localization.GuiText;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import ua.lyohha.aae.AdvancedAE;
import ua.lyohha.aae.inventory.ContainerGregTechStorageBus;
import ua.lyohha.aae.network.Message;
import ua.lyohha.aae.network.Network;
import ua.lyohha.aae.part.PartGregTechStorageBus;

import java.awt.*;

public class GuiGregTechStorageBus extends GuiContainer {

    PartGregTechStorageBus tileEntity;
//    private GuiTabButton priority;
    GuiNumberBox priority;

    public GuiGregTechStorageBus(InventoryPlayer inventoryPlayer, PartGregTechStorageBus tileEntity) {
        super(new ContainerGregTechStorageBus(inventoryPlayer, tileEntity));
        this.tileEntity = tileEntity;
        xSize = 176;
        ySize = 149;
    }

    @Override
    public void initGui() {
        super.initGui();
//        this.buttonList.add( this.priority = new GuiTabButton( this.guiLeft + 154, this.guiTop, 2 + 4 * 16, GuiText.Priority.getLocal(), itemRender ) );
        this.priority = new GuiNumberBox(this.fontRendererObj, guiLeft + 110, guiTop + 52, 59, this.fontRendererObj.FONT_HEIGHT, Integer.class);
        this.priority.setEnableBackgroundDrawing(false);
        this.priority.setMaxStringLength(16);
        this.priority.setTextColor(0xFFFFFF);
        this.priority.setVisible(true);
        this.priority.setFocused(true);
        this.priority.setText(Integer.toString(this.tileEntity.getPriority()));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int x, int y) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("aae:textures/guis/GregTechStorageBus.png"));
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        this.priority.drawTextBox();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        String s = I18n.format(tileEntity.getInventory().getInventoryName());
        fontRendererObj.drawString(s, 5, 5, Color.darkGray.getRGB());
        fontRendererObj.drawString(GuiText.Priority.getLocal(), 5, 52, Color.darkGray.getRGB());
    }

    @Override
    protected void keyTyped( final char character, final int key ) {
        if( !this.checkHotbarKeys( key ) ) {
            if ((key == 211 || key == 205 || key == 203 || key == 14 || character == '-' || Character.isDigit(character)) && this.priority.textboxKeyTyped(character, key)) {
                String out = this.priority.getText();

                boolean fixed = false;
                while (out.startsWith("0") && out.length() > 1) {
                    out = out.substring(1);
                    fixed = true;
                }

                if (fixed) {
                    this.priority.setText(out);
                }

                if (out.isEmpty()) {
                    out = "0";
                }
                Network.getNetworkWrapper().sendToServer(new Message(Message.MessageType.SET_PRIORITY, Integer.parseInt(out)));
            } else {
                super.keyTyped(character, key);
            }
        }
    }
}
