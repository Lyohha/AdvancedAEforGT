package ua.lyohha.aae.part;

import appeng.api.AEApi;
import appeng.api.implementations.IPowerChannelState;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.events.*;
import appeng.api.networking.security.IActionHost;
import appeng.api.parts.*;
import appeng.api.storage.ICellContainer;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.StorageChannel;
import appeng.api.util.AECableType;
import appeng.api.util.AEColor;
import appeng.api.util.DimensionalCoord;
import appeng.helpers.IPriorityHost;
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

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * New Storage Bus for access to Super and Quantum Chest from GregTech
 */

public class PartGregTechStorageBus implements IPowerChannelState, IPart, IGridHost, IPriorityHost, ICellContainer, IActionHost {

    private IGridNode node;
    private ForgeDirection side;
    private IPartHost host;
    protected TileEntity tile;
    private GregTechStorageBusGridBlock gridBlock;
    private TileEntity hostTile;
    private boolean isActive;
    private boolean isPowerd = false;
    private EntityPlayer owner;
    private int priority = 1000;
    private GregTechInventoryHundler handler = null;
    public String hash;
    private ItemStack is;
//    private final AENetworkProxy proxy;

    private final StorageBusFilter Config = new StorageBusFilter(this, 9);

    public PartGregTechStorageBus(ItemStack is) {
        this.is = is;
        hash = Double.toString(Math.random());
    }

    private IPartHost getHost() {
        return this.host;
    }

    public final DimensionalCoord getLocation() {
        return new DimensionalCoord(this.tile.getWorldObj(), this.tile.xCoord, this.tile.yCoord, this.tile.zCoord);
    }

    public IInventory getInventory() {
        return this.Config;
    }

    public void saveData()
    {
        if(this.host != null)
            this.host.markForSave();
        this.tile.markDirty();
        this.hostTile.markDirty();
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
        rh.setBounds(2, 2, 14, 14, 14, 15);
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
        rh.setBounds(2, 2, 14, 14, 14, 15);
        rh.renderBlock(x, y, z, renderer);

        IIcon side1 = TextureManager.getInstance().StorageBus2Side1;
        rh.setBounds(3, 3, 15, 13, 13, 16);
        ts.setColorOpaque_I(AEColor.Orange.blackVariant);
        ts.setBrightness(15 << 20 | 15 << 4);
        rh.renderFace(x, y, z, TextureManager.getInstance().StorageBus2Side1, ForgeDirection.UP, renderer);
        rh.renderFace(x, y, z, TextureManager.getInstance().StorageBus2Side1, ForgeDirection.DOWN, renderer);
        rh.renderFace(x, y, z, TextureManager.getInstance().StorageBus2Side1, ForgeDirection.EAST, renderer);
        rh.renderFace(x, y, z, TextureManager.getInstance().StorageBus2Side1, ForgeDirection.WEST, renderer);
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
        nbtTagCompound.setInteger("priority", this.priority);
        this.Config.writeToNBT(nbtTagCompound, "config");
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        if (nbtTagCompound.hasKey("node") && this.node != null) {
            this.node.loadFromNBT("node0", nbtTagCompound.getCompoundTag("node"));
            this.node.updateState();
        }
        this.priority = nbtTagCompound.getInteger("priority");
        this.Config.readFromNBT(nbtTagCompound, "config");
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
        byteBuf.writeInt(this.priority);
    }

    @Override
    public boolean readFromStream(ByteBuf byteBuf) throws IOException {
        this.isActive = byteBuf.readBoolean();
        this.isPowerd = byteBuf.readBoolean();
        this.priority = byteBuf.readInt();
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

        this.gridBlock = new GregTechStorageBusGridBlock(this);
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
        entityPlayer.openGui(AdvancedAE.instance, GuiHandler.GUIID_GREGTECH_STORAGE_BUS << 4 | (this.side.ordinal()), this.hostTile.getWorldObj(), this.hostTile.xCoord, this.hostTile.yCoord, this.hostTile.zCoord);
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
        iPartCollisionHelper.addBox(2, 2, 14, 14, 14, 15);
        iPartCollisionHelper.addBox(3, 3, 15, 13, 13, 16);
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
    }

    @Override
    public List<IMEInventoryHandler> getCellArray(StorageChannel storageChannel) {


        final TileEntity self = this.getHost().getTile();
        final TileEntity target = self.getWorldObj().getTileEntity(self.xCoord + this.side.offsetX, self.yCoord + this.side.offsetY, self.zCoord + this.side.offsetZ);

        if (storageChannel == StorageChannel.ITEMS) {
            GregTechMEInventory inv = new GregTechMEInventory(target);
            this.handler = new GregTechInventoryHundler(inv, StorageChannel.ITEMS);

            this.handler.setPartitionList(new PrecisePriorityList(this.Config.getWhiteList()));

            return Collections.singletonList(this.handler);
        }
        return Arrays.asList(new IMEInventoryHandler[]{});
    }

    // IPriorityHost
    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public void setPriority(int i) {
        if(this.handler != null)
            this.handler.setPriority(i);
        this.priority = i;
        this.getHost().markForSave();
    }

    // IAEAppEngInventory
//    @Override
//    public void saveChanges() {
//
//    }
//
//    // ICellContainer
//    @Override
//    public void onChangeInventory(IInventory iInventory, int i, InvOperation invOperation, ItemStack itemStack, ItemStack itemStack1) {
//
//    }

    @Override
    public void blinkCell(int i) {

    }

    @Override
    public IGridNode getActionableNode() {
        return this.getGridNode();
    }

    @Override
    public void saveChanges(IMEInventory imeInventory) {
        saveData();
    }
}
