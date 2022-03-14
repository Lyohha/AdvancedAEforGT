package ua.lyohha.aae.items;

import appeng.api.AEApi;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.core.CreativeTab;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import javax.annotation.Nullable;
import java.util.List;

public class PartExportMEInterface extends Item implements IPartItem {

    public PartExportMEInterface() {
        this.setCreativeTab(CreativeTab.instance);
        this.setUnlocalizedName("PartExportMEInterface");
        this.setMaxStackSize(64);
        AEApi.instance().partHelper().setItemBusRenderer(this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getSpriteNumber() {
        return 0;
    }

    @Override
    public void registerIcons(IIconRegister _iconRegister) {
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void addInformation(ItemStack stack, EntityPlayer player, List lines, boolean displayMoreInfo) {
        lines.add("Try push items to inventory");
    }


    @Nullable
    @Override
    public IPart createPartFromItemStack(ItemStack itemStack) {
        return new ua.lyohha.aae.part.PartExportMEInterface(itemStack);
    }
}
