package ua.lyohha.aae.items;

import appeng.api.AEApi;
import appeng.api.implementations.ICraftingPatternItem;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;
import appeng.core.CommonHelper;
import appeng.core.CreativeTab;
import appeng.core.localization.GuiText;
import appeng.util.Platform;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.event.ForgeEventFactory;
import ua.lyohha.aae.ae2.AdvancedPatternHelper;

import java.util.List;

public class EncodedAdvancedPattern extends Item implements ICraftingPatternItem {

    public EncodedAdvancedPattern() {
        this.setCreativeTab(CreativeTab.instance);
        this.setUnlocalizedName("EncodedAdvancedPattern");
        this.setTextureName("aae:EncodedAdvancedPattern");
        this.setMaxStackSize(1);

        if (Platform.isClient()) {
            MinecraftForgeClient.registerItemRenderer(this, new EncodedAdvancedPatternRender());
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World w, EntityPlayer player) {
        this.clearPattern(stack, player);

        return stack;
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (ForgeEventFactory.onItemUseStart(player, stack, 1) <= 0)
            return true;

        return this.clearPattern(stack, player);
    }

    private boolean clearPattern(ItemStack stack, EntityPlayer player) {
        if (player.isSneaking()) {
            if (Platform.isClient()) return false;

            InventoryPlayer inv = player.inventory;

            for (int s = 0; s < player.inventory.getSizeInventory(); s++) {
                if (inv.getStackInSlot(s) == stack) {
                    for (final ItemStack blankPattern : AEApi.instance().definitions().materials().blankPattern().maybeStack(stack.stackSize).asSet()) {
                        inv.setInventorySlotContents(s, blankPattern);
                    }

                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public ICraftingPatternDetails getPatternForItem(ItemStack is, World w) {
        return new AdvancedPatternHelper(is).decode();
    }

    public ItemStack getItemRender(ItemStack stack) {
        World w = CommonHelper.proxy.getWorld();
        if(w == null)
            return null;

        return new AdvancedPatternHelper(stack).getOut();
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void addInformation(ItemStack stack, EntityPlayer player, List lines, boolean displayMoreInfo) {
        ICraftingPatternDetails details = getPatternForItem(stack, player.worldObj);
        if (details == null) {
            lines.add(EnumChatFormatting.RED + GuiText.InvalidPattern.getLocal());
            return;
        }

        IAEItemStack[] in = details.getCondensedInputs();
        IAEItemStack[] out = details.getCondensedOutputs();

        String label = GuiText.Creates.getLocal() + ": ";
        String and = ' ' + GuiText.And.getLocal() + ' ';
        String with = GuiText.With.getLocal() + ": ";

        boolean first = true;

        for (IAEItemStack anOut : out) {
            lines.add((first ? label : and) + anOut.getStackSize() + ' ' + Platform.getItemDisplayName(anOut));
            first = false;
        }

        first = true;
        for (IAEItemStack anIn : in) {
            lines.add((first ? with : and) + anIn.getStackSize() + ' ' + Platform.getItemDisplayName(anIn));
            first = false;
        }

        lines.add(GuiText.Substitute.getLocal() + " " + GuiText.No.getLocal());
    }
}
