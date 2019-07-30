package ua.lyohha.aae.TilyEntity;

import appeng.api.AEApi;
import appeng.api.implementations.ICraftingPatternItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.Sys;
import ua.lyohha.aae.blocks.AdvancedAEBlocks;
import ua.lyohha.aae.items.AdvancedAEItems;

import java.util.ArrayList;

public class AdvancedPatternTerminalTileEntity extends TileEntity implements ISidedInventory {

    private ItemStack[] itemStacksIn = new ItemStack[20];
    private ItemStack[] itemStacksOut = new ItemStack[4];
    private ItemStack[] itemStacksPattern = new ItemStack[2];


    //функции для NBT тегов

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        writeToNBT(nbtTagCompound);
        int metadata = getBlockMetadata();
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, metadata, nbtTagCompound);
    }


    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.func_148857_g());
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
        super.writeToNBT(nbtTagCompound);
        for (int i = 0; i < itemStacksIn.length; i++) {
            if (itemStacksIn[i] != null) {
                NBTTagCompound dataSlot = new NBTTagCompound();
                itemStacksIn[i].writeToNBT(dataSlot);
                nbtTagCompound.setTag("in_" + i, dataSlot);
            }
        }
        for (int i = 0; i < itemStacksOut.length; i++) {
            if (itemStacksOut[i] != null) {
                NBTTagCompound dataSlot = new NBTTagCompound();
                itemStacksOut[i].writeToNBT(dataSlot);
                nbtTagCompound.setTag("out_" + i, dataSlot);
            }
        }
        for (int i = 0; i < itemStacksPattern.length; i++) {
            if (itemStacksPattern[i] != null) {
                NBTTagCompound dataSlot = new NBTTagCompound();
                itemStacksPattern[i].writeToNBT(dataSlot);
                nbtTagCompound.setTag("pattern_" + i, dataSlot);
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        super.readFromNBT(nbtTagCompound);
        int INT_ID = 3, LONG_ID = 4, NBT_ID = 10;
        for (int i = 0; i < itemStacksIn.length; i++) {
            if (nbtTagCompound.hasKey("in_" + i, NBT_ID)) {
                NBTTagCompound nbt = nbtTagCompound.getCompoundTag("in_" + i);
                itemStacksIn[i] = ItemStack.loadItemStackFromNBT(nbt);
            }
        }
        for (int i = 0; i < itemStacksOut.length; i++) {
            if (nbtTagCompound.hasKey("out_" + i, NBT_ID)) {
                NBTTagCompound nbt = nbtTagCompound.getCompoundTag("out_" + i);
                itemStacksOut[i] = ItemStack.loadItemStackFromNBT(nbt);
            }
        }
        for (int i = 0; i < itemStacksPattern.length; i++) {
            if (nbtTagCompound.hasKey("pattern_" + i, NBT_ID)) {
                NBTTagCompound nbt = nbtTagCompound.getCompoundTag("pattern_" + i);
                itemStacksPattern[i] = ItemStack.loadItemStackFromNBT(nbt);
            }
        }

    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return new int[0];
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        return false;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        return false;
    }

    @Override
    public int getSizeInventory() {
        return itemStacksIn.length + itemStacksOut.length + itemStacksPattern.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot >= 24) return itemStacksPattern[slot - 24];
        else if (slot >= 20) return itemStacksOut[slot - 20];
        else return itemStacksIn[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        if (slot == 24 || slot == 25) {
            if (itemStacksPattern[slot - 24] == null)
                return null;
            ItemStack itemStackRemoved = null;
            if (itemStacksPattern[slot - 24].stackSize <= amount) {
                itemStackRemoved = itemStacksPattern[slot - 24];
                setInventorySlotContents(slot, null);
            } else {
                itemStackRemoved = itemStacksPattern[slot - 24].splitStack(amount);
                if (itemStacksPattern[slot - 24].stackSize == 0)
                    setInventorySlotContents(slot, null);
            }
            return itemStackRemoved;
        }
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        if (slot >= 24) itemStacksPattern[slot - 24] = stack;
        else if (slot >= 20) itemStacksOut[slot - 20] = stack;
        else itemStacksIn[slot] = stack;
    }

    @Override
    public String getInventoryName() {
        return AdvancedAEBlocks.advancedPatternTerminal.getUnlocalizedName() + ".container.name";
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
    public boolean isUseableByPlayer(EntityPlayer player) {
        return worldObj.getTileEntity(xCoord, yCoord, zCoord) != this ? false : player.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory() {

    }

    @Override
    public void closeInventory() {

    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (slot == 25) return false;
        if (slot == 24) return AEApi.instance().definitions().materials().blankPattern().isSameAs(stack);
        return true;
    }

    public void setItem(int slot, ItemStack stack) {
        markDirty();
        if (stack == null) {
            setInventorySlotContents(slot, null);
            return;
        }
        if (slot >= 20) {
            if (itemStacksOut[slot - 20] == null) {
                itemStacksOut[slot - 20] = stack.copy();
            } else if (itemStacksOut[slot - 20].getItem() == stack.getItem() && itemStacksOut[slot - 20].getItemDamage() == stack.getItemDamage()) {
                int size = itemStacksOut[slot - 20].stackSize + stack.stackSize;
                if (size > itemStacksOut[slot - 20].getMaxStackSize())
                    size = itemStacksOut[slot - 20].getMaxStackSize();
                //itemStacksOut[slot - 20] = new ItemStack(itemStacksOut[slot - 20].getItem(), size, itemStacksOut[slot - 20].getItemDamage());
                itemStacksOut[slot - 20].stackSize = size;
            } else {
                itemStacksOut[slot - 20] = stack.copy();
            }
        } else {
            if (itemStacksIn[slot] == null) {
                itemStacksIn[slot] = stack.copy();
            } else if (itemStacksIn[slot].getItem() == stack.getItem() && itemStacksIn[slot].getItemDamage() == stack.getItemDamage()) {
                int size = itemStacksIn[slot].stackSize + stack.stackSize;
                if (size > itemStacksIn[slot].getMaxStackSize())
                    size = itemStacksIn[slot].getMaxStackSize();
                itemStacksIn[slot].stackSize = size;
            } else {
                itemStacksIn[slot] = stack.copy();
            }
        }
    }

    public ArrayList<ItemStack> getDrop() {
        ArrayList<ItemStack> arrayList = new ArrayList<>();
        for (ItemStack itemStack : itemStacksPattern) if (itemStack != null) arrayList.add(itemStack.copy());
        return arrayList;
    }

    public void clearPattern() {
        for (int i = 0; i < itemStacksIn.length; i++)
            itemStacksIn[i] = null;
        for (int i = 0; i < itemStacksOut.length; i++)
            itemStacksOut[i] = null;
        markDirty();
    }

    public void createPattern() {
        if (itemStacksPattern[0] == null || itemStacksPattern[1] != null)
            return;
        if (!checkPresenceItem(itemStacksIn) || !checkPresenceItem(itemStacksOut))
            return;
        ItemStack itemStack = new ItemStack(AdvancedAEItems.encodedAdvancedPattern, 1);
        /*for (final ItemStack encodedPatternStack : AEApi.instance().definitions().items().encodedPattern().maybeStack(1).asSet())
            itemStack = encodedPatternStack;*/
        decrStackSize(24, 1);
        NBTTagCompound tagCompound = new NBTTagCompound();
        NBTTagList listIn = new NBTTagList();
        NBTTagList listOut = new NBTTagList();
        tagCompound.setBoolean("crafting", false);
        tagCompound.setBoolean("substitute", false);
        for (ItemStack stack : itemStacksIn) {
            NBTTagCompound tag = new NBTTagCompound();
            if (stack != null)
                stack.writeToNBT(tag);
            listIn.appendTag(tag);
        }
        for (ItemStack stack : itemStacksOut) {
            NBTTagCompound tag = new NBTTagCompound();
            if (stack != null) {
                stack.writeToNBT(tag);
                listOut.appendTag(tag);
            }
        }
        tagCompound.setTag("in", listIn);
        tagCompound.setTag("out", listOut);
        itemStack.setTagCompound(tagCompound);
        itemStacksPattern[1] = itemStack;
    }

    private boolean checkPresenceItem(ItemStack[] itemStack) {
        for (ItemStack stack : itemStack) if (stack != null) return true;
        return false;
    }
}
