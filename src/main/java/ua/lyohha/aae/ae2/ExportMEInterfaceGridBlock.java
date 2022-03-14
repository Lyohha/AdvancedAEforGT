package ua.lyohha.aae.ae2;

import appeng.api.networking.*;
import appeng.api.parts.PartItemStack;
import appeng.api.util.AEColor;
import appeng.api.util.DimensionalCoord;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import ua.lyohha.aae.part.PartExportMEInterface;
import ua.lyohha.aae.part.PartGregTechStorageBus;

import java.util.EnumSet;

public class ExportMEInterfaceGridBlock implements IGridBlock
{
    protected AEColor color;
    protected PartExportMEInterface host;
    protected IGrid grid;
    protected int usedChannels;

    public ExportMEInterfaceGridBlock(PartExportMEInterface host)
    {
        this.host = host;
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
        return this.host.getLocation();
    }

    @Override
    public AEColor getGridColor() {
        return this.color == null ? AEColor.Transparent : this.color;
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
        return this.host;
    }

    @Override
    public void gridChanged() {

    }

    @Override
    public ItemStack getMachineRepresentation() {
        return this.host.getItemStack(PartItemStack.Network);
    }
}
