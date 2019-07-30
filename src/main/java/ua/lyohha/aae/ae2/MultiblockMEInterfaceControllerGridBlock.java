package ua.lyohha.aae.ae2;

import appeng.api.networking.*;
import appeng.api.util.AEColor;
import appeng.api.util.DimensionalCoord;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import ua.lyohha.aae.TilyEntity.AdvancedStorageMEInterfaceTileEntity;
import ua.lyohha.aae.TilyEntity.MultiblockMEInterfaceControllerTileEntity;

import java.util.EnumSet;

public class MultiblockMEInterfaceControllerGridBlock implements IGridBlock
{

    private MultiblockMEInterfaceControllerTileEntity tileEntity;
    protected IGrid grid;
    protected int usedChannels;

    public MultiblockMEInterfaceControllerGridBlock(MultiblockMEInterfaceControllerTileEntity tileEntity)
    {
        this.tileEntity = tileEntity;
    }

    @Override
    public double getIdlePowerUsage() {
        return 1.0D;
    }

    @Override
    public EnumSet<GridFlags> getFlags() {
        return EnumSet.of(GridFlags.REQUIRE_CHANNEL);
    }

    @Override
    public boolean isWorldAccessible() {
        return true;
    }

    @Override
    public DimensionalCoord getLocation() {
        return new DimensionalCoord(tileEntity);
    }

    @Override
    public AEColor getGridColor() {
        return AEColor.Transparent;
    }

    @Override
    public void onGridNotification(GridNotification notification) {

    }

    @Override
    public void setNetworkStatus(IGrid grid, int channelsInUse) {
        this.grid = grid;
        this.usedChannels = channelsInUse;
    }

    @Override
    public EnumSet<ForgeDirection> getConnectableSides() {
        return EnumSet.allOf(ForgeDirection.class);
    }

    @Override
    public IGridHost getMachine() {
        return tileEntity;
    }

    @Override
    public void gridChanged() {

    }

    @Override
    public ItemStack getMachineRepresentation() {
        DimensionalCoord dim = new DimensionalCoord(tileEntity);
        if(dim == null)
            return null;
        return new ItemStack(dim.getWorld().getBlock(dim.x,dim.y, dim.z),1);

    }
}
