package ua.lyohha.aae.part;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.implementations.IPowerChannelState;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.events.MENetworkCellArrayUpdate;
import appeng.api.networking.events.MENetworkChannelsChanged;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.events.MENetworkPowerStatusChange;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.MachineSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.*;
import appeng.api.storage.*;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.AECableType;
import appeng.api.util.AEColor;
import appeng.api.util.DimensionalCoord;
import appeng.core.settings.TickRates;
import appeng.helpers.IPriorityHost;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import appeng.util.prioitylist.PrecisePriorityList;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
//import net.minecraft.client.renderer.RenderBlocks;
//import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import ua.lyohha.aae.AdvancedAE;
import ua.lyohha.aae.ae2.*;
import ua.lyohha.aae.gui.GuiHandler;
import ua.lyohha.aae.items.AdvancedAEItems;

import java.io.IOException;
import java.util.*;

/**
 * Allow push items to connect inventory
 */

public class PartExportMEInterface implements IPowerChannelState, IGridTickable, IPart, IGridHost, IActionHost, ISidedInventory {

    private IGridNode node;
    private ForgeDirection side;
    private IPartHost host;
    protected TileEntity tile;
    private ExportMEInterfaceGridBlock gridBlock;
    private TileEntity hostTile;
    private boolean isActive;
    private boolean isPowerd = false;
    private EntityPlayer owner;
    private ItemStack is;
    private final int[] slots = {9, 10, 11, 12, 13, 14, 15, 16, 17};
    private ItemStack[] inventory;
    private boolean hasWork = true;

    public PartExportMEInterface(ItemStack is) {
        this.is = is;
        this.inventory = new ItemStack[this.getSizeInventory()];
    }

    private IPartHost getHost() {
        return this.host;
    }

    public final DimensionalCoord getLocation() {
        return new DimensionalCoord(this.tile.getWorldObj(), this.tile.xCoord, this.tile.yCoord, this.tile.zCoord);
    }

    public void saveData() {
        if (this.host != null)
            this.host.markForSave();
        this.tile.markDirty();
        this.hostTile.markDirty();
    }

    private boolean hasWorkToDo() {
        return hasWork;
    }

    private TickRateModulation pushItems(TickRateModulation next) {
        final TileEntity self = this.getHost().getTile();
        final TileEntity targetTileEntity = self.getWorldObj().getTileEntity(self.xCoord + this.side.offsetX, self.yCoord + this.side.offsetY, self.zCoord + this.side.offsetZ);

        final int oppositeSide = this.side.getOpposite().ordinal();

        if (targetTileEntity instanceof ISidedInventory) {
            ISidedInventory target = (ISidedInventory) targetTileEntity;

            int[] slots = target.getAccessibleSlotsFromSide(oppositeSide);

            for (int slot : slots) {
//                for(ItemStack inventoryStack : this.inventory) {
                for (int i = 0; i < 9; i++) {
                    ItemStack inventoryStack = this.inventory[i + 9];
                    if (inventoryStack == null)
                        continue;

                    if (target.canInsertItem(slot, inventoryStack, oppositeSide)) {
                        ItemStack in = target.getStackInSlot(slot);
                        if (in == null) {
                            target.setInventorySlotContents(slot, this.inventory[i + 9]);
                            this.inventory[i + 9] = null;
                            next = TickRateModulation.URGENT;
                            break;
                        } else {
                            if (in.stackSize < target.getInventoryStackLimit()) {
                                int value = Math.min(inventoryStack.stackSize, target.getInventoryStackLimit() - in.stackSize);
                                in.stackSize += value;
                                inventoryStack.stackSize -= value;
                                if (inventoryStack.stackSize == 0)
                                    this.inventory[i + 9] = null;
                                next = TickRateModulation.URGENT;
                                break;
                            }
                        }
                    }
                }
            }
        }

        return next;
    }

    private TickRateModulation requestInventory(TickRateModulation next) {

        IGrid grid = node.getGrid();
        if (grid == null)
            return next;
        IStorageGrid storage = grid.getCache(IStorageGrid.class);
        if (storage == null)
            return next;
        IMEMonitor<IAEItemStack> itemInv = storage.getItemInventory();

        for (int i = 0; i < 9; i++) {
            if (this.inventory[i] == null && this.inventory[i + 9] == null)
                continue;
            // push in me
            if (this.inventory[i] == null && this.inventory[i + 9] != null) {
                AEItemStack request = AEItemStack.create(this.inventory[i + 9].copy());
                request = (AEItemStack) itemInv.injectItems(request, Actionable.SIMULATE, new MachineSource(this));
                if(request == null)
                {
                    request = AEItemStack.create(this.inventory[i + 9].copy());
                    itemInv.injectItems(request, Actionable.MODULATE, new MachineSource(this));
                    this.inventory[i + 9] = null;
                    next = TickRateModulation.URGENT;
                    this.hasWork = true;
                }
                else
                {
                    int notAdded = request.getItemStack().stackSize;
                    request = AEItemStack.create( this.inventory[i + 9].copy().splitStack(this.inventory[i + 9].stackSize - notAdded));
                    itemInv.injectItems(request, Actionable.MODULATE, new MachineSource(this));
                    this.inventory[i + 9].splitStack(notAdded);
                    next = TickRateModulation.URGENT;
                }
            // get from me
            } else if (this.inventory[i] != null) {
                if (this.inventory[i + 9] == null) {
                    AEItemStack request = (AEItemStack) AEItemStack.create(this.inventory[i].copy())
                            .setStackSize(this.inventory[i].getMaxStackSize());
                    request = (AEItemStack) itemInv.extractItems(request, Actionable.SIMULATE, new MachineSource(this));
                    if (request == null)
                        continue;

                    request = (AEItemStack) itemInv.extractItems(request, Actionable.MODULATE, new MachineSource(this));
                    this.inventory[i + 9] = request.getItemStack();
                    next = TickRateModulation.URGENT;
                    this.hasWork = true;
                } else {
                    if(this.inventory[i + 9].stackSize == this.inventory[i + 9].getMaxStackSize())
                        continue;
                    AEItemStack request = (AEItemStack) AEItemStack.create(this.inventory[i + 9].copy())
                            .setStackSize(this.inventory[i + 9].getMaxStackSize() - this.inventory[i+9].stackSize );
                    request = (AEItemStack) itemInv.extractItems(request, Actionable.SIMULATE, new MachineSource(this));
                    if (request == null)
                        continue;

                    request = (AEItemStack) itemInv.extractItems(request, Actionable.MODULATE, new MachineSource(this));
                    this.inventory[i + 9].stackSize += request.getStackSize();

                    next = TickRateModulation.URGENT;
                }
            }

        }
        return next;
    }

    // IGridTickable
    @Override
    public TickingRequest getTickingRequest(IGridNode iGridNode) {
        return new TickingRequest(TickRates.Interface.getMin(), TickRates.Interface.getMax(), !this.hasWorkToDo(), true);
    }

    @Override
    public TickRateModulation tickingRequest(final IGridNode iGridNode, final int ticksFromLast) {
        TickRateModulation next = TickRateModulation.SLOWER;

        next = this.requestInventory(next);
        next = this.pushItems(next);

        return next;
    }

    // events
    @MENetworkEventSubscribe
    @SuppressWarnings("unused")
    public void setPower(MENetworkPowerStatusChange notUsed) {
        if (this.node != null) {
            this.isActive = this.node.isActive();
            IGrid grid = this.node.getGrid();
            if (grid != null) {
                IEnergyGrid energy = grid.getCache(IEnergyGrid.class);
                if (energy != null)
                    this.isPowerd = energy.isNetworkPowered();
            }
            this.host.markForUpdate();
            node.getGrid().postEvent(new MENetworkCellArrayUpdate());
        }
    }


    @MENetworkEventSubscribe
    public void updateChannels(MENetworkChannelsChanged channel) {
//        logs.info("updateChannels");
        IGridNode node = getGridNode();
        if (node != null) {
            boolean isNowActive = node.isActive();
            if (isNowActive != isActive()) {
                this.isActive = isNowActive;
                onNeighborChanged();
                getHost().markForUpdate();
            }
        }
//		node.getGrid().postEvent( new MENetworkStorageEvent(getGridBlock().getFluidMonitor(), StorageChannel.FLUIDS));
        node.getGrid().postEvent(new MENetworkCellArrayUpdate());
    }

    @MENetworkEventSubscribe
    public void powerChange(MENetworkPowerStatusChange event) {
//        logs.info("powerChange");
        IGridNode node = getGridNode();
        if (node != null) {
            boolean isNowActive = node.isActive();
            if (isNowActive != isActive()) {
                this.isActive = isNowActive;
                onNeighborChanged();
                getHost().markForUpdate();
            }
        }
//		node.getGrid().postEvent(new MENetworkStorageEvent(getGridBlock().getFluidMonitor(), StorageChannel.FLUIDS));
        node.getGrid().postEvent(new MENetworkCellArrayUpdate());
    }


    // IPart
    @Override
    public ItemStack getItemStack(PartItemStack partItemStack) {
        return this.is;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderInventory(IPartRenderHelper rh, net.minecraft.client.renderer.RenderBlocks renderer) {
        net.minecraft.client.renderer.Tessellator ts = net.minecraft.client.renderer.Tessellator.instance;

        IIcon side = TextureManager.getInstance().StorageBus1Side;
        rh.setTexture(side, side, TextureManager.getInstance().StorageBus1Back, TextureManager.getInstance().StorageBus1Front, side, side);
        rh.setBounds(2, 2, 14, 14, 14, 16);
        rh.renderInventoryBox(renderer);

        IIcon side1 = TextureManager.getInstance().StorageBus2Side1;
        rh.setTexture(side1, side1, side1, TextureManager.getInstance().StorageBus2Front1, side1, side1);
        rh.setBounds(3, 3, 15, 13, 13, 16);
        rh.setInvColor(AEColor.Orange.blackVariant);
        ts.setBrightness(15 << 20 | 15 << 4);
        rh.renderInventoryBox(renderer);

        rh.setInvColor(0xFFFFFF);
        IIcon side2 = TextureManager.getInstance().StorageBus2Side2;
        rh.setTexture(side2, side2, side2, TextureManager.getInstance().StorageBus2Front2, side2, side2);
        rh.setBounds(5, 5, 15, 11, 11, 16);
        rh.renderInventoryBox(renderer);

        IIcon side3 = TextureManager.getInstance().BusSide;
        rh.setTexture(side3, side3, side3, side3, side3, side3);
        rh.setBounds(5, 5, 12, 11, 11, 14);
        rh.renderInventoryBox(renderer);

//        rh.setBounds(4,4, 13, 12, 12, 14);
//        ts.setBrightness(13 << 20 | 13 << 4);
//        rh.setInvColor(AEColor.Transparent.blackVariant);
//        rh.renderInventoryFace(TextureManager.getInstance().BusColor, ForgeDirection.UP, renderer);
//        rh.renderInventoryFace(TextureManager.getInstance().BusColor, ForgeDirection.DOWN, renderer);
//        rh.renderInventoryFace(TextureManager.getInstance().BusColor, ForgeDirection.EAST, renderer);
//        rh.renderInventoryFace(TextureManager.getInstance().BusColor, ForgeDirection.WEST, renderer);


    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderStatic(int x, int y, int z, IPartRenderHelper rh, net.minecraft.client.renderer.RenderBlocks renderer) {
        net.minecraft.client.renderer.Tessellator ts = net.minecraft.client.renderer.Tessellator.instance;

        IIcon side = TextureManager.getInstance().StorageBus1Side;
        rh.setTexture(side, side, TextureManager.getInstance().StorageBus1Back, TextureManager.getInstance().StorageBus1Front, side, side);
        rh.setBounds(2, 2, 14, 14, 14, 16);
        rh.renderBlock(x, y, z, renderer);

        IIcon side1 = TextureManager.getInstance().StorageBus2Side1;
        rh.setBounds(3, 3, 15, 13, 13, 16);
        ts.setColorOpaque_I(AEColor.Orange.blackVariant);
        ts.setBrightness(15 << 20 | 15 << 4);
//        rh.renderFace(x, y, z, TextureManager.getInstance().StorageBus2Side1, ForgeDirection.UP, renderer);
//        rh.renderFace(x, y, z, TextureManager.getInstance().StorageBus2Side1, ForgeDirection.DOWN, renderer);
//        rh.renderFace(x, y, z, TextureManager.getInstance().StorageBus2Side1, ForgeDirection.EAST, renderer);
//        rh.renderFace(x, y, z, TextureManager.getInstance().StorageBus2Side1, ForgeDirection.WEST, renderer);
        rh.renderFace(x, y, z, TextureManager.getInstance().StorageBus2Front1, ForgeDirection.SOUTH, renderer);

        rh.setInvColor(0xFFFFFF);
        IIcon side2 = TextureManager.getInstance().StorageBus2Side2;
        rh.setTexture(side2, side2, side2, TextureManager.getInstance().StorageBus2Front2, side2, side2);
        rh.setBounds(5, 5, 15, 11, 11, 16);
        rh.renderBlock(x, y, z, renderer);

        IIcon side3 = TextureManager.getInstance().BusSide;
        rh.setTexture(side3, side3, side3, side3, side3, side3);
        rh.setBounds(5, 5, 12, 11, 11, 14);
        rh.renderBlock(x, y, z, renderer);

        rh.normalRendering();
        rh.setBounds(5, 5, 13, 11, 11, 14);

        if (isActive()) {
            ts.setBrightness(13 << 20 | 13 << 4);
            ts.setColorOpaque_I(this.host.getColor().blackVariant);
        } else {
            ts.setColorOpaque_I(0x000000);
        }

        rh.renderFace(x, y, z, TextureManager.getInstance().BusColor, ForgeDirection.UP, renderer);
        rh.renderFace(x, y, z, TextureManager.getInstance().BusColor, ForgeDirection.DOWN, renderer);
        rh.renderFace(x, y, z, TextureManager.getInstance().BusColor, ForgeDirection.EAST, renderer);
        rh.renderFace(x, y, z, TextureManager.getInstance().BusColor, ForgeDirection.WEST, renderer);
    }

    @Override
    public void renderDynamic(double v, double v1, double v2, IPartRenderHelper iPartRenderHelper, net.minecraft.client.renderer.RenderBlocks renderBlocks) {

    }

    @Override
    public IIcon getBreakingTexture() {
        return TextureManager.getInstance().StorageBus1Side;
    }

    @Override
    public boolean requireDynamicRender() {
        return false;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean canConnectRedstone() {
        return false;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
        if (this.node != null) {
            NBTTagCompound nodeTag = new NBTTagCompound();
            this.node.saveToNBT("node0", nodeTag);
            nbtTagCompound.setTag("node", nodeTag);
        }

        for (int i = 0; i < 18; i++) {
            if (this.inventory[i] != null) {
                NBTTagCompound item = new NBTTagCompound();
                this.inventory[i].writeToNBT(item);
                nbtTagCompound.setTag("inventory_" + i, item);
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        if (nbtTagCompound.hasKey("node") && this.node != null) {
            this.node.loadFromNBT("node0", nbtTagCompound.getCompoundTag("node"));
            this.node.updateState();
        }
        for (int i = 0; i < 18; i++) {
            this.inventory[i] = ItemStack.loadItemStackFromNBT(nbtTagCompound.getCompoundTag("inventory_" + i));
        }
        this.onNeighborChanged();
    }

    @Override
    public int getLightLevel() {
        return 0;
    }

    @Override
    public boolean isLadder(EntityLivingBase entityLivingBase) {
        return false;
    }

    @Override
    public void onNeighborChanged() {

        IGridNode node = getGridNode();
        if (node != null) {
            IGrid grid = node.getGrid();
            if (grid != null) {
                grid.postEvent(new MENetworkCellArrayUpdate());
                node.getGrid().postEvent(new MENetworkCellArrayUpdate());
            }
            getHost().markForUpdate();
        }

        getHost().markForUpdate();
    }

    @Override
    public int isProvidingStrongPower() {
        return 0;
    }

    @Override
    public int isProvidingWeakPower() {
        return 0;
    }

    @Override
    public void writeToStream(ByteBuf byteBuf) throws IOException {
        byteBuf.writeBoolean(this.node != null && this.node.isActive());
        byteBuf.writeBoolean(this.isPowerd);
    }

    @Override
    public boolean readFromStream(ByteBuf byteBuf) throws IOException {
        this.isActive = byteBuf.readBoolean();
        this.isPowerd = byteBuf.readBoolean();
        return true;
    }

    @Override
    public IGridNode getGridNode() {
        return this.node;
    }

    @Override
    public void onEntityCollision(Entity entity) {

    }

    @Override
    public void removeFromWorld() {
        if (this.node != null)
            this.node.destroy();
    }

    @Override
    public void addToWorld() {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;

        this.gridBlock = new ExportMEInterfaceGridBlock(this);

        if(this.host != null)
            this.gridBlock.setGridColor(this.host.getColor());

        this.node = AEApi.instance().createGridNode(this.gridBlock);
        if (this.node != null) {
            if (this.owner != null)
                this.node.setPlayerID(AEApi.instance().registries().players().getID(this.owner));
            this.node.updateState();
        }

        setPower(null);
        onNeighborChanged();
    }

    @Override
    public IGridNode getExternalFacingNode() {
        return null;
    }

    @Override
    public void setPartHostInfo(ForgeDirection forgeDirection, IPartHost iPartHost, TileEntity tileEntity) {
        this.side = forgeDirection;
        this.host = iPartHost;
        this.tile = tileEntity;
        this.hostTile = tileEntity;

        setPower(null);
    }

    @Override
    public boolean onActivate(EntityPlayer entityPlayer, Vec3 vec3) {
        entityPlayer.openGui(AdvancedAE.instance, GuiHandler.GUIID_GREGTECH_EXPORT_ME_INTERFACE << 4 | (this.side.ordinal()), this.hostTile.getWorldObj(), this.hostTile.xCoord, this.hostTile.yCoord, this.hostTile.zCoord);
        return true;
    }

    @Override
    public boolean onShiftActivate(EntityPlayer entityPlayer, Vec3 vec3) {
//        if (entityPlayer != null && entityPlayer instanceof EntityPlayerMP)
//            GuiHandler.launchGui(GuiHandler.getGuiId(this), player,
//                    this.hostTile.getWorldObj(), this.hostTile.xCoord,
//                    this.hostTile.yCoord, this.hostTile.zCoord);
//        return true;
        return false;
    }

    @Override
    public void getDrops(List<ItemStack> list, boolean b) {

    }

    @Override
    public int cableConnectionRenderTo() {
        return 4;
    }

    @Override
    public void randomDisplayTick(World world, int i, int i1, int i2, Random random) {

    }

    @Override
    public void onPlacement(EntityPlayer entityPlayer, ItemStack itemStack, ForgeDirection forgeDirection) {
        this.owner = entityPlayer;
    }

    @Override
    public boolean canBePlacedOn(BusSupport busSupport) {
        return busSupport != BusSupport.DENSE_CABLE;
    }

    @Override
    public void getBoxes(IPartCollisionHelper iPartCollisionHelper) {
        iPartCollisionHelper.addBox(2, 2, 14, 14, 14, 16);
        iPartCollisionHelper.addBox(5, 5, 15, 11, 11, 16);
        iPartCollisionHelper.addBox(5, 5, 12, 11, 11, 14);

    }

    // IPowerChannelState
    @Override
    public boolean isPowered() {
        return this.isPowerd;
    }

    @Override
    public boolean isActive() {
        return this.node != null ? this.node.isActive() : this.isActive;
    }

    // IGridHost
    @Override
    public IGridNode getGridNode(ForgeDirection forgeDirection) {
        return this.node;
    }

    @Override
    public AECableType getCableConnectionType(ForgeDirection forgeDirection) {
        return AECableType.GLASS;
    }

    @Override
    public void securityBreak() {
        getHost().removePart(this.side, false);

        List<ItemStack> items = new ArrayList<ItemStack>();

        for(int i = 0; i < 9 ; i++) {
            if(this.inventory[i + 9] != null) {
                items.add(this.inventory[i + 9]);
            }
        }

        if(items.size() > 0)
            Platform.spawnDrops(this.tile.getWorldObj(), this.tile.xCoord, this.tile.yCoord, this.tile.zCoord, items);
    }

    @Override
    public IGridNode getActionableNode() {
        return this.getGridNode();
    }

    // ISidedInventory
    @Override
    public int[] getAccessibleSlotsFromSide(int p_94128_1_) {
        return this.slots;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        if (slot < 9)
            return false;
        return this.isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        if (slot < 9)
            return false;

        if (this.inventory[slot] != null
                && this.inventory[slot - 9] != null
                && this.inventory[slot].getItem() == stack.getItem()
                && this.inventory[slot].getItemDamage() == stack.getItemDamage())
            return true;

        return false;
    }

    @Override
    public int getSizeInventory() {
        return 18;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.inventory[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        if (slot < 9) {
            this.inventory[slot] = null;
            this.saveData();
            return null;
        }

        ItemStack itemStack = this.inventory[slot];
        if (itemStack != null) {
            if (itemStack.stackSize <= amount)
                this.setInventorySlotContents(slot, null);
            else
                itemStack = itemStack.splitStack(amount);
        }

        return itemStack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int p_70304_1_) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        this.inventory[slot] = stack;
    }

    @Override
    public String getInventoryName() {
        return AdvancedAEItems.partExportMEInterface.getUnlocalizedName() + ".container.name";
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
        if (this.host != null)
            this.host.markForSave();
//        this.tile.markDirty();
//        this.hostTile.markDirty();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
        return true;
    }

    @Override
    public void openInventory() {

    }

    @Override
    public void closeInventory() {
        this.markDirty();
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (slot < 9) {
            if (this.inventory[slot + 9] == null) {
                this.inventory[slot] = stack.copy();
                this.inventory[slot].stackSize = 1;
            }
        } else {
            if (this.inventory[slot] == null) {
                if (this.inventory[slot - 9] == null)
                    return true;
                if (this.inventory[slot - 9].getItem() == stack.getItem()
                        && this.inventory[slot - 9].getItemDamage() == stack.getItemDamage())
                    return true;
            } else {
                if (this.inventory[slot].getItem() == stack.getItem()
                        && this.inventory[slot].getItemDamage() == stack.getItemDamage())
                    return true;
            }
        }

        return false;
    }

}
