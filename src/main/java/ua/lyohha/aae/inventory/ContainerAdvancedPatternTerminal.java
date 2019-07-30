package ua.lyohha.aae.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import ua.lyohha.aae.TilyEntity.AdvancedPatternTerminalTileEntity;

public class ContainerAdvancedPatternTerminal extends Container {

    AdvancedPatternTerminalTileEntity tileEntity;

    public ContainerAdvancedPatternTerminal(InventoryPlayer inventoryPlayer, AdvancedPatternTerminalTileEntity tileEntity) {
        this.tileEntity = tileEntity;
        addContainerSlots(8,21);
        addPlayerSlots(inventoryPlayer, 8, 106);
    }

    public AdvancedPatternTerminalTileEntity getTileEntity()
    {
        return tileEntity;
    }

    private void addContainerSlots(int x, int y) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                addSlotToContainer(new SlotFake(tileEntity, (i * 5) + j, x + (j * 18), y + i * 18));
            }
        }

        for(int i=0;i<4;i++)
            addSlotToContainer(new SlotFake(tileEntity, 20 + i, 116, y + i * 18));

        addSlotToContainer(new SlotStorage(tileEntity, 24, 152, 30));
        addSlotToContainer(new SlotStorage(tileEntity, 25, 152, 68));
    }

    private void addPlayerSlots(InventoryPlayer inventoryPlayer, int x, int y) {
        //инвентарь, позиция х, позиция у
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

    @Override
    public ItemStack slotClick(int slot, int p_75144_2_, int p_75144_3_, EntityPlayer player) {
        if (slot == -999)//костыль, чтобы при клике не на ложный слот игра не крашилась
            return super.slotClick(slot, p_75144_2_, p_75144_3_, player);
        if (slot < 0)
            return null;

        Slot slot2 = (Slot) inventorySlots.get(slot);//слот инвенторя по заданому номеру
        if (slot2 instanceof SlotFake) {
            tileEntity.setItem(slot, player.inventory.getItemStack());// пачка предметов которую держит игрок
            return null;
        }
        return super.slotClick(slot, p_75144_2_, p_75144_3_, player);//костыль для обработки остальных слотов кроме ложного
    }
}
