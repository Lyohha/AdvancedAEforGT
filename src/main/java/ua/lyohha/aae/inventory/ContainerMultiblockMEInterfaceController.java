package ua.lyohha.aae.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import ua.lyohha.aae.TilyEntity.MultiblockMEInterfaceControllerTileEntity;

public class ContainerMultiblockMEInterfaceController extends Container {

    MultiblockMEInterfaceControllerTileEntity tileEntity;

    public ContainerMultiblockMEInterfaceController(InventoryPlayer inventoryPlayer, MultiblockMEInterfaceControllerTileEntity tileEntity) {
        this.tileEntity = tileEntity;
        addContainerSlots(8, 17);
        addPlayerSlots(inventoryPlayer, 8, 88);

    }

    private void addContainerSlots(int x, int y) {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(new SlotStorage(tileEntity, (i * 9) + j, x + (j * 18), y + i * 36));
            }
        }
    }

    private void addPlayerSlots(InventoryPlayer inventoryPlayer, int x, int y) {
        //инвентарь, номер слота, позиция х, позиция у
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, x + j * 18, y + i * 18));
            }
        }

        for (int i = 0; i < 9; ++i) {
            this.addSlotToContainer(new Slot(inventoryPlayer, i, x + i * 18, y + 58));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tileEntity.isUseableByPlayer(player);//need tile entity
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
        return null;
    }

    @Override
    public boolean canDragIntoSlot(Slot p_94531_1_) {
        return false;
    }
}
