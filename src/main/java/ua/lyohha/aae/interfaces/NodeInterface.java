package ua.lyohha.aae.interfaces;

import net.minecraft.tileentity.TileEntity;
import ua.lyohha.aae.grid.Grid;

public interface NodeInterface {

    /*
    *
    * Muiltiblock ME Interface build grid where used nodes
    * Implement this interface for using tile entity like Node
    *
     */

    //Calling when grid was build
    void updateGrid(Grid grid);

    //Return used Grid
    Grid getGrid();

    //Calling when grid destroyed
    void destroyGrid();

    TileEntity getTileEntity();
}
