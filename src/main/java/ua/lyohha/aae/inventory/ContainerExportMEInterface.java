package ua.lyohha.aae.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import ua.lyohha.aae.TilyEntity.AdvancedStorageMEInterfaceTileEntity;
import ua.lyohha.aae.part.PartExportMEInterface;

public class ContainerExportMEInterface extends Container {

    private PartExportMEInterface partExportMEInterface = null;

    public ContainerExportMEInterface(InventoryPlayer inventoryPlayer, PartExportMEInterface partExportMEInterface)//need tile entity
    {
        this.partExportMEInterface = partExportMEInterface;
        addContainerSlots(8, 29);
        addPlayerSlots(inventoryPlayer, 8, 69);
    }

    private void addContainerSlots(int x, int y) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(new SlotFake(partExportMEInterface,  j, x + (j * 18), y ));
            }
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(new SlotStorage(partExportMEInterface, j + 9, x + (j * 18), y  + 18));
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
        return partExportMEInterface.isUseableByPlayer(player);//need tile entity
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
        return null;
    }

    @Override
    public boolean canDragIntoSlot(Slot p_94531_1_) {
        return false;
    }

    @Override
    protected void retrySlotClick(int param1, int param2, boolean param3, EntityPlayer player) {

    }

    @Override
    public ItemStack slotClick(int slot, int p_75144_2_, int p_75144_3_, EntityPlayer player) {
        if (slot == -999)//костыль, чтобы при клике не на ложный слот игра не крашилась
            return super.slotClick(slot, p_75144_2_, p_75144_3_, player);
        if (slot < 0)
            return null;

        Slot slot2 = (Slot) inventorySlots.get(slot);//слот инвенторя по заданому номеру
        if (slot2 instanceof SlotFake) {
            ItemStack itemStack2 = player.inventory.getItemStack();// пачка предметов которую держит игрок
            if (itemStack2 == null)
                partExportMEInterface.decrStackSize(slot, 1);
            else
                partExportMEInterface.isItemValidForSlot(slot, itemStack2);
            return null;
        }
        return super.slotClick(slot, p_75144_2_, p_75144_3_, player);//костыль для обработки остальных слотов кроме ложного
    }
}
