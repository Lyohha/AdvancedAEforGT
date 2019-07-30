package ua.lyohha.aae.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotFake extends Slot {
    public SlotFake(IInventory p_i1824_1_, int p_i1824_2_, int p_i1824_3_, int p_i1824_4_) {
        super(p_i1824_1_, p_i1824_2_, p_i1824_3_, p_i1824_4_);
    }

    @Override
    public void onPickupFromSlot(EntityPlayer player, ItemStack stack) {

    }

    @Override
    public ItemStack decrStackSize(int slot) {
        if (slot < 36)
            inventory.setInventorySlotContents(slot, null);
        return null;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return inventory.isItemValidForSlot(getSlotIndex(), stack);
    }

    @Override
    public void putStack(ItemStack is) {
        if (is != null) {
            is = is.copy();
        }

        super.putStack(is);
    }

    @Override
    public boolean canTakeStack(EntityPlayer par1EntityPlayer) {
        return false;
    }
}
