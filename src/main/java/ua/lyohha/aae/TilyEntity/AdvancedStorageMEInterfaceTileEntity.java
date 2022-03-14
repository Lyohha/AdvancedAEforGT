package ua.lyohha.aae.TilyEntity;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridStorage;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.MachineSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.AECableType;
import appeng.util.item.AEItemStack;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.versioning.ComparableVersion;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import ua.lyohha.aae.AdvancedAE;
import ua.lyohha.aae.ae2.AdvancedStorageMEInterfaceGridBlock;
import ua.lyohha.aae.blocks.AdvancedAEBlocks;

import java.util.ArrayList;

public class AdvancedStorageMEInterfaceTileEntity extends TileEntity implements ISidedInventory, IActionHost {

    private ItemStack[] itemStacks = new ItemStack[72];
    private AdvancedStorageMEInterfaceGridBlock gridBlock = null;
    private boolean isFirstGetGridNode = true;
    private IGridNode node = null;
    private boolean isConnected = false;
    private int cycleExport = -1;
    private int cycleImport = -1;

    public AdvancedStorageMEInterfaceTileEntity() {
        for (int i = 0; i < 72; i++) itemStacks[i] = null;
        gridBlock = new AdvancedStorageMEInterfaceGridBlock(this);
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        getNode(ForgeDirection.UNKNOWN);

        updateInventoryImport();
        updateInventoryExport();
    }

    private void updateInventoryImport() {
        if (!isConnected)
            return;
        cycleImport++;
        if (cycleImport == (itemStacks.length / 2))
            cycleImport = 0;
        if (itemStacks[cycleImport] != null)
            return;
        IGrid grid = node.getGrid();
        if (grid == null)
            return;
        IStorageGrid storage = grid.getCache(IStorageGrid.class);
        if (storage == null)
            return;
        IMEMonitor<IAEItemStack> itemInv = storage.getItemInventory();
        if (itemStacks[cycleImport + 36] != null) {
            AEItemStack request = AEItemStack.create(itemStacks[cycleImport + 36].copy());
            request = (AEItemStack) itemInv.injectItems(request, Actionable.SIMULATE, new MachineSource(this));
            if (request == null) {
                request = AEItemStack.create(itemStacks[cycleImport + 36].copy());
                itemInv.injectItems(request, Actionable.MODULATE, new MachineSource(this));
                itemStacks[cycleImport + 36] = null;
            } else {
                int notAdded = request.getItemStack().stackSize;
                request = AEItemStack.create(itemStacks[cycleImport + 36].copy().splitStack(itemStacks[cycleImport + 36].stackSize - notAdded));
                itemInv.injectItems(request, Actionable.MODULATE, new MachineSource(this));
//                itemStacks[cycleImport + 36] = itemStacks[cycleImport + 36].copy().splitStack(notAdded);
                itemStacks[cycleImport + 36].splitStack(notAdded);
            }
        }
    }

    private void updateInventoryExport() {
        if (!isConnected)
            return;
        cycleExport++;
        if (cycleExport == (itemStacks.length / 2))
            cycleExport = 0;
        if (itemStacks[cycleExport] == null)
            return;
        IGrid grid = node.getGrid();
        if (grid == null)
            return;
        IStorageGrid storage = grid.getCache(IStorageGrid.class);
        if (storage == null)
            return;
        IMEMonitor<IAEItemStack> itemInv = storage.getItemInventory();
        if (itemStacks[cycleExport + 36] == null) {
            AEItemStack request = (AEItemStack) AEItemStack.create(itemStacks[cycleExport].copy()).setStackSize(itemStacks[cycleExport].getMaxStackSize());
            request = (AEItemStack) itemInv.extractItems(request, Actionable.SIMULATE, new MachineSource(this));
            if (request == null)
                return;
            request = (AEItemStack) itemInv.extractItems(request, Actionable.MODULATE, new MachineSource(this));
            itemStacks[cycleExport + 36] = request.getItemStack();
        } else {
            AEItemStack request = (AEItemStack)AEItemStack.create(itemStacks[cycleExport].copy()).setStackSize(itemStacks[cycleExport + 36].getMaxStackSize() - itemStacks[cycleExport + 36].stackSize);;
            request = (AEItemStack) itemInv.extractItems(request, Actionable.SIMULATE, new MachineSource(this));
            if (request == null)
                return;
            request = (AEItemStack) itemInv.extractItems(request, Actionable.MODULATE, new MachineSource(this));
            ItemStack stack = request.getItemStack();
            stack.stackSize = stack.stackSize + itemStacks[cycleExport + 36].stackSize;

            itemStacks[cycleExport + 36] = stack;
        }


    }


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
        for (int i = 0; i < itemStacks.length; i++) {
            if (itemStacks[i] != null) {
                NBTTagCompound dataSlot = new NBTTagCompound();
                itemStacks[i].writeToNBT(dataSlot);
                nbtTagCompound.setTag("itemStacks_" + i, dataSlot);
            }
        }
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            IGridNode node = getNode(ForgeDirection.UNKNOWN);
            if (node != null) {
                NBTTagCompound nodeTag = new NBTTagCompound();
                node.saveToNBT("node0", nodeTag);
                nbtTagCompound.setTag("nodes", nodeTag);
            }
        }

    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        super.readFromNBT(nbtTagCompound);
        int NBT_ID = 10;
        for (int i = 0; i < itemStacks.length; i++) {
            if (nbtTagCompound.hasKey("itemStacks_" + i, NBT_ID)) {
                NBTTagCompound nbt = nbtTagCompound.getCompoundTag("itemStacks_" + i);
                itemStacks[i] = ItemStack.loadItemStackFromNBT(nbt);
            }
        }

        try {
            if (FMLCommonHandler.instance().getEffectiveSide().isServer() && hasWorldObj()) {
                IGridNode node = getNode(ForgeDirection.UNKNOWN);
                if (nbtTagCompound.hasKey("nodes") && node != null) {
                    node.loadFromNBT("node0", nbtTagCompound.getCompoundTag("nodes"));
                    node.updateState();
                }
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }


    }

    //функции инвентаря

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        int[] SlotsForExtract = new int[36];
        int x = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 9; j++, x++) {
                SlotsForExtract[x] = i * 9 + j + 36;
            }
        }

        return SlotsForExtract;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        return isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        if (slot < 36)
            return false;

        if (itemStacks[slot] != null && itemStacks[slot - 36] != null)
            if (itemStacks[slot].getItem() == stack.getItem())
                return true;
        return false;
    }

    @Override
    public int getSizeInventory() {
        return itemStacks.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return itemStacks[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        ItemStack itemStackInSlot = getStackInSlot(slot);
        if (slot < 36) {
            itemStacks[slot] = null;
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            return null;
        }
        if (itemStackInSlot != null) {
            if (itemStackInSlot.stackSize <= amount) {
                setInventorySlotContents(slot, null);
            } else {
                itemStackInSlot = itemStackInSlot.splitStack(amount);
            }
        }
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        return itemStackInSlot;

    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        itemStacks[slot] = stack;
    }

    @Override
    public String getInventoryName() {
        return AdvancedAEBlocks.advancedStorageMEInterface.getUnlocalizedName() + ".container.name";
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
        if (slot < 36) {
            if (itemStacks[slot + 36] == null) {
                itemStacks[slot] = stack.copy();
                itemStacks[slot].stackSize = 1;
            }
            return false;
        }
        if (itemStacks[slot] == null) {
            if (itemStacks[slot - 36] == null) {
                return true;
            }
            if (itemStacks[slot - 36].getItem() == stack.getItem() && itemStacks[slot - 36].getItemDamage() == stack.getItemDamage()) {
                return true;
            }
            return false;
        }
        if (stack.getItem() == itemStacks[slot].getItem() && itemStacks[slot].getItemDamage() == stack.getItemDamage()) {
            return true;
        }
        return false;
    }

    //AE2
    //IGridHost

    private IGridNode getNode(ForgeDirection dir) {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            return null;
        }
        IGridNode node = getGridNode(dir);
        if (node != null && !isConnected) {
            node.updateState();
            isConnected = true;
        }
        return node;
    }

    @Override
    public IGridNode getGridNode(ForgeDirection dir) {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            return null;
        }
        if (this.isFirstGetGridNode) {
            this.isFirstGetGridNode = false;
            if (this.node == null)
                this.node = AEApi.instance().createGridNode(gridBlock);
        }
        return this.node;
    }

    @Override
    public AECableType getCableConnectionType(ForgeDirection dir) {
        return AECableType.COVERED;
    }

    @Override
    public void securityBreak() {
        //maybe need to do some? NOT!!!
    }

    //IActionHost
    @Override
    public IGridNode getActionableNode() {
        return node;
    }

    public void Destroy() {
        if (node != null && FMLCommonHandler.instance().getEffectiveSide().isServer())
            node.destroy();
    }

    public ArrayList<ItemStack> getDrop() {
        ArrayList<ItemStack> arrayList = new ArrayList<>();
        for (int i = itemStacks.length / 2; i < itemStacks.length; i++)
            if (itemStacks[i] != null)
                arrayList.add(itemStacks[i].copy());
        return arrayList;
    }
}
