package ua.lyohha.aae.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import ua.lyohha.aae.TilyEntity.MultiblockMEInterfaceHatchTileEntity;

public class GuiMultiblockMEInterfaceHatch extends GuiScreen {

    private int xSize = 136;
    private int ySize = 112;

    MultiblockMEInterfaceHatchTileEntity tileEntity = null;

    public GuiMultiblockMEInterfaceHatch(MultiblockMEInterfaceHatchTileEntity tileEntity) {
        this.tileEntity = tileEntity;

    }

    @Override
    public void drawScreen(int x, int y, float ticks) {
        int posX = (width - xSize) / 2;
        int posY = (height - ySize) / 2;
        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("aae:textures/guis/MultiblockMEInterfaceHatch.png"));
        drawTexturedModalRect(posX, posY, 0, 0, xSize, ySize);
        super.drawScreen(x, y, ticks);
    }

    @Override
    public void initGui() {
        initButtons();
        int id = tileEntity.getNumberHatch();
        if (id != 0)
            ((GuiButton) buttonList.get(id - 1)).enabled = false;
        super.initGui();
    }

    private void initButtons() {
        buttonList.clear();
        int posX = (width - xSize) / 2;
        int posY = (height - ySize) / 2;
        for (int i = 0, num = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++, num++) {
                GuiButton b = new GuiButton(num, posX + 10 + j * 24, posY + 10 + i * 24, 20, 20, Integer.toString(num + 1));
                buttonList.add(b);
            }
        }
    }

    @Override
    protected void keyTyped(char c, int key) {
        if (key == Keyboard.KEY_E)
            mc.displayGuiScreen(null);
        super.keyTyped(c, key);
    }


    @Override
    protected void actionPerformed(GuiButton button) {
        updateViewButton();
        button.enabled = false;
        tileEntity.setNumberHatch(button.id + 1);
    }

    private void updateViewButton() {
        for (Object o : buttonList) ((GuiButton) o).enabled = true;
    }

}
