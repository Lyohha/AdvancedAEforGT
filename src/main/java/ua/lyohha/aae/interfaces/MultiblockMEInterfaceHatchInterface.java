package ua.lyohha.aae.interfaces;

import net.minecraft.item.ItemStack;
import ua.lyohha.aae.grid.Grid;

public interface MultiblockMEInterfaceHatchInterface {

    /*
     * If needed add more type of hatch only implements this interface.
     *
     */


    //Return number was given this hatch
    int getNumberHatch();

    //Set number for this hatch
    void setNumberHatch(int num);

    boolean canPush(ItemStack stack);

    void push(ItemStack stack);
}
