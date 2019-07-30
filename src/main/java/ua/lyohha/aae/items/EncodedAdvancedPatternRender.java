package ua.lyohha.aae.items;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class EncodedAdvancedPatternRender implements IItemRenderer {

    private RenderItem renderItem = new RenderItem();
    private boolean recursive = false;

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {

        boolean isShift = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
        if (!this.recursive && type == ItemRenderType.INVENTORY && isShift) {
            EncodedAdvancedPattern pattern = (EncodedAdvancedPattern) item.getItem();
            if (pattern.getItemRender(item) != null)
                return true;
        }
        return false;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return false;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {

        this.recursive = true;

        EncodedAdvancedPattern pattern = (EncodedAdvancedPattern) item.getItem();
        ItemStack stack = pattern.getItemRender(item);
        Minecraft mc = Minecraft.getMinecraft();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT);
        RenderHelper.enableGUIStandardItemLighting();
        this.renderItem.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), stack, 0, 0);
        RenderHelper.disableStandardItemLighting();
        GL11.glPopAttrib();

        this.recursive = false;
    }
}
