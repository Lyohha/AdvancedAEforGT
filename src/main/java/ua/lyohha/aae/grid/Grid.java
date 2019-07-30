package ua.lyohha.aae.grid;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import ua.lyohha.aae.TilyEntity.MultiblockMEInterfaceControllerTileEntity;
import ua.lyohha.aae.interfaces.MultiblockMEInterfaceHatchInterface;
import ua.lyohha.aae.interfaces.NodeInterface;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.List;

public class Grid {

    private MultiblockMEInterfaceControllerTileEntity tileEntity;
    private List<NodeInterface> nodes = new ArrayList<>();
    private boolean isBuild = false;

    public Grid(MultiblockMEInterfaceControllerTileEntity tileEntity) {
        this.tileEntity = tileEntity;
    }

    public void build() {
        this.nodes.clear();
        isBuild = false;
        World w = this.tileEntity.getWorldObj();
        for (ForgeDirection f : ForgeDirection.values()) {
            TileEntity tileEntity = w.getTileEntity(this.tileEntity.xCoord + f.offsetX, this.tileEntity.yCoord + f.offsetY, this.tileEntity.zCoord + f.offsetZ);
            if (tileEntity != null) {
                if (tileEntity instanceof NodeInterface)
                    ((NodeInterface) tileEntity).updateGrid(this);
            }
        }
        if (this.nodes.size() != 0)
            this.isBuild = true;
    }

    public void destroy() {
        for (NodeInterface node : this.nodes)
            node.destroyGrid();
        this.tileEntity = null;
    }

    public boolean contains(NodeInterface node) {
        return this.nodes.contains(node);
    }

    public void addNode(NodeInterface node) {
        this.nodes.add(node);
        //System.out.println("Node " + node.getTileEntity().xCoord + " " + node.getTileEntity().yCoord + " " + node.getTileEntity().zCoord);
    }

    public void destroyNode() {
        this.isBuild = false;
    }

    public boolean isBuild() {
        return this.isBuild;
    }

    public boolean containsHatch(int num) {
        for (NodeInterface node : nodes)
            if (((MultiblockMEInterfaceHatchInterface) node.getTileEntity()).getNumberHatch() == num)
                return true;
        return false;
    }

    public NodeInterface getNode(int num) {
        if (num < 0)
            return null;
        for (NodeInterface n : nodes) {
            if (((MultiblockMEInterfaceHatchInterface) n.getTileEntity()).getNumberHatch() == num)
                return n;
        }
        return null;
    }

}
