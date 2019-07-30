package ua.lyohha.aae.TilyEntity;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.implementations.ICraftingPatternItem;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.networking.crafting.ICraftingProviderHelper;
import appeng.api.networking.events.MENetworkCraftingPatternChange;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.MachineSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.networking.ticking.ITickManager;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.AECableType;
import appeng.util.item.AEItemStack;
import com.sun.istack.internal.NotNull;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.Sys;
import ua.lyohha.aae.ae2.MultiblockMEInterfaceControllerGridBlock;
import ua.lyohha.aae.blocks.AdvancedAEBlocks;
import ua.lyohha.aae.grid.Grid;
import ua.lyohha.aae.interfaces.MultiblockMEInterfaceHatchInterface;
import ua.lyohha.aae.interfaces.NodeInterface;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiblockMEInterfaceControllerTileEntity extends TileEntity implements ISidedInventory, IActionHost, ICraftingProvider {

    private Grid grid;
    private ItemStack[] itemStacks = new ItemStack[18];
    private MultiblockMEInterfaceControllerGridBlock gridBlock;
    private boolean isFirstGetGridNode = true;
    private IGridNode node = null;
    private boolean isConnected = false;
    private List<ICraftingPatternDetails> craftingList = null;
    private List<ExportItem> export = new ArrayList<>();
    private int cycleImport = 17;
    private boolean update = true;

    private class ExportItem {
        public int Hatch;
        public IAEItemStack Stack;

        public ExportItem(int hatch, IAEItemStack stack) {
            Hatch = hatch;
            Stack = stack;
        }
    }


    public MultiblockMEInterfaceControllerTileEntity() {
        grid = new Grid(this);
        gridBlock = new MultiblockMEInterfaceControllerGridBlock(this);
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (!worldObj.isRemote) {
            if (!grid.isBuild())
                grid.build();
            if (update)
                genCraftingList();
            updateInventoryImport();
            pushItems();
        }
    }

    private void pushItems() {
        if (export.isEmpty() || !grid.isBuild())
            return;
        for (int i = 0; i < export.size(); i++) {
            if (grid.containsHatch(export.get(i).Hatch)) {
                NodeInterface nodeInterface = grid.getNode(export.get(i).Hatch);
                if (((MultiblockMEInterfaceHatchInterface) nodeInterface.getTileEntity()).canPush(export.get(i).Stack.getItemStack())) {
                    ((MultiblockMEInterfaceHatchInterface) nodeInterface.getTileEntity()).push(export.get(i).Stack.getItemStack());
                    export.remove(i);
                    i--;
                }
            }
        }
    }

    private void updateInventoryImport() {
        if (!isConnected)
            return;
        cycleImport++;
        if (cycleImport == getSizeInventory())
            cycleImport = 9;

        IGrid grid = node.getGrid();
        if (grid == null)
            return;
        IStorageGrid storage = grid.getCache(IStorageGrid.class);
        if (storage == null)
            return;

        IMEMonitor<IAEItemStack> itemInv = storage.getItemInventory();
        if (itemStacks[cycleImport] != null) {
            AEItemStack request = AEItemStack.create(itemStacks[cycleImport].copy());
            request = (AEItemStack) itemInv.injectItems(request, Actionable.SIMULATE, new MachineSource(this));
            if (request == null) {
                request = AEItemStack.create(itemStacks[cycleImport].copy());
                itemInv.injectItems(request, Actionable.MODULATE, new MachineSource(this));
                itemStacks[cycleImport] = null;
            } else {
                int notAdded = request.getItemStack().stackSize;
                request = AEItemStack.create(itemStacks[cycleImport].copy().splitStack(itemStacks[cycleImport].copy().stackSize - notAdded));
                itemInv.injectItems(request, Actionable.MODULATE, new MachineSource(this));
                itemStacks[cycleImport] = itemStacks[cycleImport].copy().splitStack(notAdded);
            }
        }
    }

    public void destroyBlock() {
        grid.destroy();
        try {
            if (getNode(ForgeDirection.UNKNOWN) != null && !worldObj.isRemote)
                getNode(ForgeDirection.UNKNOWN).destroy();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public ArrayList<ItemStack> getDrop() {
        ArrayList<ItemStack> arrayList = new ArrayList<>();
        for (int i = 0; i < itemStacks.length; i++)
            if (itemStacks[i] != null)
                arrayList.add(itemStacks[i].copy());
        for (ExportItem exportItem : export)
            arrayList.add(exportItem.Stack.getItemStack());

        return arrayList;
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
                nbtTagCompound.setTag("slot_" + i, dataSlot);
            }
        }
        if (!this.export.isEmpty()) {
            for (ExportItem exportItem : export) {
                NBTTagCompound dataExport = new NBTTagCompound();
                exportItem.Stack.writeToNBT(dataExport);
                nbtTagCompound.setTag("export_" + exportItem.Hatch, dataExport);
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
        int INT_ID = 3, LONG_ID = 4, NBT_ID = 10;
        for (int i = 0; i < itemStacks.length; i++) {
            if (nbtTagCompound.hasKey("slot_" + i, NBT_ID)) {
                NBTTagCompound dataSlot = nbtTagCompound.getCompoundTag("slot_" + i);
                itemStacks[i] = ItemStack.loadItemStackFromNBT(dataSlot);
            }
        }


        this.export.clear();
        for (int i = 1; i <= 20; i++) {
            if (nbtTagCompound.hasKey("export_" + i)) {
                NBTTagCompound dataExport = nbtTagCompound.getCompoundTag("export_" + i);
                this.export.add(new ExportItem(i, AEItemStack.loadItemStackFromNBT(dataExport)));
            }
        }

        try {
            if (FMLCommonHandler.instance().getEffectiveSide().isServer() && hasWorldObj()) {
                IGridNode node = getNode(ForgeDirection.UNKNOWN);
                if (nbtTagCompound.hasKey("nodes") && node != null) {
                    node.loadFromNBT("node0", nbtTagCompound.getCompoundTag("nodes"));
                    node.updateState();
                    update = true;
                }
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
        //genCraftingList = true;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return new int[]{9, 10, 11, 12, 13, 14, 15, 16, 17};
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {

        if (itemStacks[slot] == null)
            return true;
        if (stack != null) {
            if (itemStacks[slot].getItem() == stack.getItem() && itemStacks[slot].getItemDamage() == stack.getItemDamage())
                return true;
        }

        return false;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
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

        if (itemStacks[slot] == null)
            return null;
        ItemStack itemStackRemoved = itemStacks[slot];
        if (itemStackRemoved.stackSize <= amount) {
            setInventorySlotContents(slot, null);
        } else {
            itemStackRemoved = itemStacks[slot].splitStack(amount);
            if (itemStacks[slot].stackSize == 0)
                setInventorySlotContents(slot, null);

        }
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        update = true;
        return itemStackRemoved;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int alot) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        itemStacks[slot] = stack;
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        update = true;
    }

    @Override
    public String getInventoryName() {
        return AdvancedAEBlocks.multiblockMEInterfaceController.getUnlocalizedName() + ".container.name";
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
        if (slot < 9) {
            if (stack.getItem() instanceof ICraftingPatternItem) return true;
            else return false;
        }
        return true;
    }

    //ME Network

    private IGridNode getNode(ForgeDirection dir) {
        if (worldObj.isRemote) {
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
    public IGridNode getActionableNode() {
        return node;
    }

    @Override
    public IGridNode getGridNode(ForgeDirection dir) {
        if (worldObj.isRemote)
            return null;
        if (this.isFirstGetGridNode) {
            this.isFirstGetGridNode = false;
            if (this.node == null) {
                this.node = AEApi.instance().createGridNode(gridBlock);

            }
        }
        return this.node;
    }

    @Override
    public AECableType getCableConnectionType(ForgeDirection dir) {
        return AECableType.COVERED;
    }

    @Override
    public void securityBreak() {

    }


    //crafting

    private void genCraftingList() {
        if (craftingList == null)
            craftingList = new ArrayList<>();
        craftingList.clear();

        for (int i = 0; i < itemStacks.length / 2; i++) {
            if (itemStacks[i] != null) {
                if (itemStacks[i].getItem() instanceof ICraftingPatternItem) {
                    ICraftingPatternDetails details = ((ICraftingPatternItem) itemStacks[i].getItem()).getPatternForItem(itemStacks[i], worldObj);
                    if (details != null)
                        craftingList.add(details);
                }
            }
        }
        try {
            this.node.getGrid().postEvent(new MENetworkCraftingPatternChange(this, this.node));
        } catch (Exception ex) {
            // :P
        }
    }

    @Override
    public void provideCrafting(ICraftingProviderHelper craftingTracker) {

        if (node.isActive()) {
            if(craftingList == null) {
                update = true;
                return;
            }
            for (final ICraftingPatternDetails details : this.craftingList) {
                craftingTracker.addCraftingOption(this, details);
            }
            update = false;
        }
    }

    @Override
    public boolean pushPattern(ICraftingPatternDetails patternDetails, InventoryCrafting table) {
        if (this.isBusy()) return false;
        if (this.craftingList == null || !this.craftingList.contains(patternDetails)) return false;


        IGrid grid = this.node.getGrid();
        if (grid == null)  return false;
        IStorageGrid storage = grid.getCache(IStorageGrid.class);
        if (storage == null) return false;

        int num = 0;
        for (IAEItemStack stack : patternDetails.getInputs()) {
            num++;
            if (stack == null)
                continue;
            this.export.add(new ExportItem(num, stack));
        }
        return true;
    }

    @Override
    public boolean isBusy() {
        //System.out.println("Size " + export.size());
        return !export.isEmpty();
        //return false;
    }
}
