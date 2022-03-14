package ua.lyohha.aae.ae2;

import appeng.api.AEApi;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import ua.lyohha.aae.AdvancedAE;
import ua.lyohha.aae.blocks.AdvancedAEBlocks;
import ua.lyohha.aae.items.AdvancedAEItems;
import ua.lyohha.aae.part.AdvancedAEParts;
import ua.lyohha.aae.part.PartGregTechStorageBus;

public class StorageBusFilter implements IInventory {
    final private int size;
    private ItemStack[] inventory;
    final private PartGregTechStorageBus part;


    public StorageBusFilter(PartGregTechStorageBus part, int size) {
        this.size = size;
        this.part = part;
        this.inventory = new ItemStack[size];
    }

    // save params
    public void writeToNBT(NBTTagCompound nbtTagCompound, String name) {
        NBTTagCompound config = new NBTTagCompound();

        for(int i = 0; i < this.size; i++)
        {
            if(inventory[i] != null) {
                NBTTagCompound item = new NBTTagCompound();
                inventory[i].writeToNBT(item);
                config.setTag("inventory_" + i, item);
            }
        }

        nbtTagCompound.setTag(name, config);
    }

    public void readFromNBT(NBTTagCompound nbtTagCompound, String name) {
        NBTTagCompound config = nbtTagCompound.getCompoundTag(name);

        for(int i = 0; i < this.size; i++) {
            this.inventory[i] = ItemStack.loadItemStackFromNBT(config.getCompoundTag("inventory_" + i));

        }
    }


    @Override
    public int getSizeInventory() {
        return this.size;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot >= size)
            return null;
        return inventory[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int number) {
        if (slot < size) {
            inventory[slot] = null;
        }
        markDirty();
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack itemStack) {
        inventory[slot] = itemStack;
        markDirty();
//        if(inventory[slot] != null)
//            inventory[slot].stackSize = 1;
    }

    @Override
    public String getInventoryName() {
        return AdvancedAEItems.partGregTechStorageBus.getUnlocalizedName() + ".container.name";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {
        if(this.part != null)
            this.part.saveData();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory() {
        markDirty();
    }

    @Override
    public void closeInventory() {
        markDirty();
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if(slot < this.size)
        {
            inventory[slot] = stack.copy();
            inventory[slot].stackSize = 1;
            markDirty();
        }

        return false;
    }

    public IItemList<IAEItemStack> getWhiteList() {
        IItemList<IAEItemStack> whiteList = AEApi.instance().storage().createItemList();

        for(int i = 0; i < this.size; i++)
        {
            if(this.inventory[i] != null)
            {
                whiteList.add(AEApi.instance().storage().createItemStack(this.inventory[i]));
            }
        }

        return whiteList;
    }
}
