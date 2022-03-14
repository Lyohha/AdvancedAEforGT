package ua.lyohha.aae.ae2;

import appeng.api.config.Actionable;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.StorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import appeng.util.item.AEItemStack;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.metatileentity.BaseMetaTileEntity;
import gregtech.api.util.GT_Utility;
import gregtech.common.tileentities.storage.GT_MetaTileEntity_QuantumChest;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import ua.lyohha.aae.AdvancedAE;
import ua.lyohha.aae.blocks.AdvancedPatternTerminal;
import ua.lyohha.aae.items.AdvancedAEItems;

public class GregTechMEInventory implements IMEInventory<IAEItemStack> {

    private TileEntity target;

    public GregTechMEInventory(TileEntity target) {
        this.target = target;
    }

    @Override
    public IAEItemStack injectItems(IAEItemStack iaeStack, Actionable actionable, BaseActionSource baseActionSource) {

        if (target instanceof BaseMetaTileEntity) {
            IMetaTileEntity metaTileEntity = ((BaseMetaTileEntity) target).getMetaTileEntity();

            if (metaTileEntity instanceof GT_MetaTileEntity_QuantumChest) {
                GT_MetaTileEntity_QuantumChest chest = (GT_MetaTileEntity_QuantumChest) metaTileEntity;
                ItemStack is = iaeStack.getItemStack();
                if (actionable == Actionable.SIMULATE) {

                    if (chest.mItemStack == null) {
                        long count = iaeStack.getStackSize() - chest.getMaxItemCount();
                        if (count < 0) return null;
                        return iaeStack.copy().setStackSize(count);
                    } else {
                        if (GT_Utility.areStacksEqual(is, chest.mItemStack)) {
                            long count = iaeStack.getStackSize() - (chest.getMaxItemCount() - chest.mItemCount);
                            if (count < 0) return null;
                            return iaeStack.copy().setStackSize(count);
                        } else if (chest.mInventory[0] == null || chest.mInventory[1] == null) {
                            long count = iaeStack.getStackSize() - is.getMaxStackSize();
                            if (count < 0) return null;
                            return iaeStack.copy().setStackSize(count);
                        } else if (GT_Utility.areStacksEqual(is, chest.mInventory[0])) {
                            long count = iaeStack.getStackSize() - (is.getMaxStackSize() - chest.mInventory[0].stackSize);
                            if (count < 0) return null;
                            return iaeStack.copy().setStackSize(count);
                        } else if (GT_Utility.areStacksEqual(is, chest.mInventory[1])) {
                            long count = iaeStack.getStackSize() - (is.getMaxStackSize() - chest.mInventory[1].stackSize);
                            if (count < 0) return null;
                            return iaeStack.copy().setStackSize(count);
                        }
                    }

                } else {

                    if (chest.mItemStack == null) {
                        int insert = Math.min((int) iaeStack.getStackSize(), chest.getMaxItemCount());

                        chest.mItemStack = is.copy();
                        chest.mItemCount = insert;

                        long count = iaeStack.getStackSize() - chest.getMaxItemCount();
                        if (count < 0) return null;
                        return iaeStack.copy().setStackSize(count);
                    } else {
                        if (GT_Utility.areStacksEqual(is, chest.mItemStack)) {
                            int insert = Math.min((int) iaeStack.getStackSize(), chest.getMaxItemCount() - chest.mItemCount);

                            chest.mItemCount += insert;

                            long count = iaeStack.getStackSize() - (chest.getMaxItemCount() - chest.mItemCount);
                            if (count < 0) return null;
                            return iaeStack.copy().setStackSize(count);
                        } else if (chest.mInventory[0] == null) {
                            int insert = Math.min((int) iaeStack.getStackSize(), is.getMaxStackSize());

                            chest.mInventory[0] = is.copy();
                            chest.mInventory[0].stackSize = insert;

                            long count = iaeStack.getStackSize() - is.getMaxStackSize();
                            if (count < 0) return null;
                            return iaeStack.copy().setStackSize(count);
                        } else if (chest.mInventory[1] == null) {
                            int insert = Math.min((int) iaeStack.getStackSize(), is.getMaxStackSize());

                            chest.mInventory[1] = is.copy();
                            chest.mInventory[1].stackSize = insert;

                            long count = iaeStack.getStackSize() - is.getMaxStackSize();
                            if (count < 0) return null;
                            return iaeStack.copy().setStackSize(count);
                        } else if (GT_Utility.areStacksEqual(is, chest.mInventory[0])) {

                            chest.mInventory[0].stackSize += Math.min((int) iaeStack.getStackSize(), chest.mInventory[0].getMaxStackSize() - chest.mInventory[0].stackSize);

                            long count = iaeStack.getStackSize() - (chest.mInventory[0].getMaxStackSize() - chest.mInventory[0].stackSize);
                            if (count < 0) return null;
                            return iaeStack.copy().setStackSize(count);
                        } else if (GT_Utility.areStacksEqual(is, chest.mInventory[1])) {
                            chest.mInventory[1].stackSize += Math.min((int) iaeStack.getStackSize(), chest.mInventory[1].getMaxStackSize() - chest.mInventory[1].stackSize);

                            long count = iaeStack.getStackSize() - (chest.mInventory[1].getMaxStackSize() - chest.mInventory[1].stackSize);
                            if (count < 0) return null;
                            return iaeStack.copy().setStackSize(count);
                        }
                    }
                }
            }
        }

        return iaeStack.copy();
    }

    @Override
    public IAEItemStack extractItems(IAEItemStack iaeStack, Actionable actionable, BaseActionSource baseActionSource) {

        if (target instanceof BaseMetaTileEntity) {
            IMetaTileEntity metaTileEntity = ((BaseMetaTileEntity) target).getMetaTileEntity();

            if (metaTileEntity instanceof GT_MetaTileEntity_QuantumChest) {
                GT_MetaTileEntity_QuantumChest chest = (GT_MetaTileEntity_QuantumChest) metaTileEntity;
                ItemStack is = iaeStack.getItemStack();
                if (actionable == Actionable.SIMULATE) {

                    // first check in main buffer
                    if (GT_Utility.areStacksEqual(is, chest.mItemStack)) {
                        return iaeStack.copy().setStackSize(Math.min(is.stackSize, chest.mItemCount));
                    }
                    // check top slot
                    else if (GT_Utility.areStacksEqual(is, chest.mInventory[0])) {
                        return iaeStack.copy().setStackSize(Math.min(is.stackSize, chest.mInventory[0].stackSize));
                    }
                    // check bottom slot
                    else if (GT_Utility.areStacksEqual(is, chest.mInventory[1])) {
                        return iaeStack.copy().setStackSize(Math.min(is.stackSize, chest.mInventory[1].stackSize));
                    }
                } else {


                    if (GT_Utility.areStacksEqual(is, chest.mItemStack)) {
                        // TODO пересчет количества, чтобы с нижнего слота достать мог
                        int count = Math.min(is.stackSize, chest.mItemCount);

                        chest.mItemCount -= count;
                        if (chest.mItemCount <= 0)
                            chest.mItemStack = null;

                        return iaeStack.copy().setStackSize(count);
                    }
                    // check top slot
                    else if (GT_Utility.areStacksEqual(is, chest.mInventory[0])) {
                        int count = Math.min(is.stackSize, chest.mInventory[0].stackSize);

                        chest.mInventory[0].stackSize -= count;
                        if (chest.mInventory[0].stackSize <= 0)
                            chest.mInventory[0] = null;

                        return iaeStack.copy().setStackSize(count);
                    }
                    // check bottom slot
                    else if (GT_Utility.areStacksEqual(is, chest.mInventory[1])) {
                        int count = Math.min(is.stackSize, chest.mInventory[1].stackSize);

                        chest.mInventory[1].stackSize -= count;
                        if (chest.mInventory[1].stackSize <= 0)
                            chest.mInventory[1] = null;

                        return iaeStack.copy().setStackSize(count);
                    }
                }
            }
        }

        return null;
    }

    @Override
    public IItemList getAvailableItems(IItemList iItemList) {
//        iItemList.add(AEItemStack.create(new ItemStack(AdvancedAEItems.encodedAdvancedPattern, 256)));

        if (target instanceof BaseMetaTileEntity) {
            IMetaTileEntity metaTileEntity = ((BaseMetaTileEntity) target).getMetaTileEntity();

            if (metaTileEntity instanceof GT_MetaTileEntity_QuantumChest) {
                GT_MetaTileEntity_QuantumChest chest = (GT_MetaTileEntity_QuantumChest) metaTileEntity;
                // inside buffer
                if (chest.mItemStack != null) {
                    ItemStack qunatum = chest.mItemStack.copy();
                    qunatum.stackSize = chest.mItemCount;

                    iItemList.add(AEItemStack.create(qunatum));
                }

                if (chest.mInventory[0] != null) {
                    iItemList.add(AEItemStack.create(chest.mInventory[0].copy()));
                }
                if (chest.mInventory[1] != null) {
                    iItemList.add(AEItemStack.create(chest.mInventory[1].copy()));
                }


            }
        }

        return iItemList;
    }

    @Override
    public StorageChannel getChannel() {
        return StorageChannel.ITEMS;
    }
}
