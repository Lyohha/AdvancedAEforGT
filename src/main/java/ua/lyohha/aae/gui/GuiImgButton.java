package ua.lyohha.aae.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiImgButton extends GuiButton {


    private ResourceLocation resourceLocation;
    private int tWidth = 0;
    private int tHeight = 0;
    int posX, posY;

    public GuiImgButton(int id, int x, int y, int width, int height) {
        super(id, x, y, width, height, "");
        this.width = width;
        this.height = height;
    }

    public GuiImgButton setResourceLocation(ResourceLocation location, int width, int height, int posX, int posY) {
        resourceLocation = location;
        tWidth = width;
        tHeight = height;
        this.posX = posX;
        this.posY = posY;
        return this;
    }

    public void drawButton(Minecraft mc, int x, int y) {
        if (this.visible) {
            mc.getTextureManager().bindTexture(resourceLocation);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.field_146123_n = x >= this.xPosition && y >= this.yPosition && x < this.xPosition + this.width && y < this.yPosition + this.height;
            //GL11.glEnable(GL11.GL_BLEND);
            //OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            //GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, posX, posY, this.tWidth, this.tHeight);
            this.mouseDragged(mc, x, y);
        }
    }
}
