package ua.lyohha.aae.TilyEntity;

import appeng.api.networking.IGridNode;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import ua.lyohha.aae.AdvancedAE;
import ua.lyohha.aae.blocks.MultiblockMEInterfaceHatch;
import ua.lyohha.aae.grid.Grid;
import ua.lyohha.aae.interfaces.MultiblockMEInterfaceHatchInterface;
import ua.lyohha.aae.interfaces.NodeInterface;
import net.minecraft.tileentity.TileEntity;

public class MultiblockMEInterfaceHatchTileEntity extends TileEntity implements NodeInterface, MultiblockMEInterfaceHatchInterface {

    private int numberHatch = 0;
    private Grid grid = null;
    private boolean isFirstSearch = false;
    private IInventory inventory = null;
    private int inventorySlot = -1;

    public MultiblockMEInterfaceHatchTileEntity() {

    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (!worldObj.isRemote) {
            if (!isFirstSearch)
                searchGrid();
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
        nbtTagCompound.setInteger("numberHatch", numberHatch);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        super.readFromNBT(nbtTagCompound);
        int INT_ID = 3, NBT_ID = 10;
        if (nbtTagCompound.hasKey("numberHatch", INT_ID))
            numberHatch = nbtTagCompound.getInteger("numberHatch");


    }


    //MultiblockMEInterfaceHatch Interface

    public int getNumberHatch() {
        return numberHatch;
    }

    public void setNumberHatch(int num) {
        numberHatch = num;
        //worldObj.markBlockForUpdate(xCoord,yCoord,zCoord);
        markDirty();
    }

    @Override
    public boolean canPush(ItemStack stack) {
        for (ForgeDirection f : ForgeDirection.values()) {
            if (f == ForgeDirection.UNKNOWN)
                continue;
            TileEntity entity = worldObj.getTileEntity(xCoord + f.offsetX, yCoord + f.offsetY, zCoord + f.offsetZ);
            if (entity != null) {
                if (entity instanceof MultiblockMEInterfaceControllerTileEntity)
                    continue;
                if (entity instanceof ISidedInventory) {
                    if (((ISidedInventory) entity).getSizeInventory() > 0 && ((ISidedInventory) entity).getInventoryStackLimit() >= stack.stackSize) {
                        int[] slots = ((ISidedInventory) entity).getAccessibleSlotsFromSide(getSide(f));
                        for (int i : slots) {
                            if (((ISidedInventory) entity).canInsertItem(i, stack, getSide(f)))
                                if (((ISidedInventory) entity).getStackInSlot(i) == null) {
                                    inventory = ((ISidedInventory) entity);
                                    inventorySlot = i;
                                    return true;
                                }
                        }
                    }
                }
                else if (entity instanceof IInventory) {
                    if (((IInventory) entity).getSizeInventory() > 0 && ((IInventory) entity).getInventoryStackLimit() >= stack.stackSize) {
                        int size = ((IInventory) entity).getSizeInventory();
                        for (int i = 0; i < size; i++) {
                            if(((IInventory)entity).getStackInSlot(i) == null)
                            {
                                inventory = (IInventory) entity;
                                inventorySlot = i;
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private int getSide(ForgeDirection f) {

        switch (f) {
            case UP:
                return 0;
            case SOUTH:
                return 3;
            case EAST:
                return 5;
            case DOWN:
                return 1;
            case NORTH:
                return 2;
            case WEST:
                return 4;
            default:
                return 6;
        }
    }

    @Override
    public void push(ItemStack stack) {
        if (inventory == null || inventorySlot == -1)
            return;
        inventory.setInventorySlotContents(inventorySlot, stack);
        inventory = null;
        inventorySlot = -1;
    }

    //Node Interface

    public void updateGrid(Grid grid) {
        this.grid = grid;
        if (!grid.contains(this)) {
            grid.addNode(this);
            for (ForgeDirection f : ForgeDirection.values()) {
                if (f == ForgeDirection.UNKNOWN)
                    continue;
                TileEntity tileEntity = worldObj.getTileEntity(this.xCoord + f.offsetX, this.yCoord + f.offsetY, this.zCoord + f.offsetZ);
                if (tileEntity != null) {
                    if (tileEntity instanceof NodeInterface)
                        ((NodeInterface) tileEntity).updateGrid(grid);
                }
            }
        }
    }

    private void searchGrid() {
        isFirstSearch = true;
        if (grid != null)
            return;
        for (ForgeDirection f : ForgeDirection.values()) {
            if (f == ForgeDirection.UNKNOWN)
                continue;
            TileEntity tileEntity = worldObj.getTileEntity(this.xCoord + f.offsetX, this.yCoord + f.offsetY, this.zCoord + f.offsetZ);
            if (tileEntity != null) {
                if (tileEntity instanceof NodeInterface) {
                    Grid tempGrid = ((NodeInterface) tileEntity).getGrid();
                    if (tempGrid != null) {
                        tempGrid.build();
                        return;
                    }
                }
            }
        }
    }

    public Grid getGrid() {
        return grid;
    }

    @Override
    public void destroyGrid() {
        this.grid = null;
    }

    @Override
    public TileEntity getTileEntity() {
        return this;
    }

    public void destroy() {
        if (grid != null)
            grid.destroyNode();
    }
}
