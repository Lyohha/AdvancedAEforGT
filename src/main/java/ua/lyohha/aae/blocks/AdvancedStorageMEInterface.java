package ua.lyohha.aae.blocks;


import appeng.core.CreativeTab;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import ua.lyohha.aae.AdvancedAE;
import ua.lyohha.aae.TilyEntity.AdvancedStorageMEInterfaceTileEntity;
import ua.lyohha.aae.gui.GuiHandler;

import java.util.ArrayList;
import java.util.Random;


public class AdvancedStorageMEInterface extends BlockContainer {

    public AdvancedStorageMEInterface() {
        super(Material.glass);
        this.setBlockName("advancedstoragemeinterface");
        this.setBlockTextureName("aae:BlockAdvancedInterface");
        this.setHardness(1F);
        this.setResistance(1F);
        this.setHarvestLevel("pickaxe", 1);
        this.setCreativeTab(CreativeTab.instance);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int num) {
        return new AdvancedStorageMEInterfaceTileEntity();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            TileEntity tileEntity = world.getTileEntity(x, y, z);
            if (tileEntity != null) {
                if (tileEntity instanceof AdvancedStorageMEInterfaceTileEntity)
                    player.openGui(AdvancedAE.instance, GuiHandler.GUIID_ADVANCED_STORAGE_ME_INTERFACE, world, x, y, z);
            }
        }
        return true;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int metadata) {
        if (!world.isRemote) {
            dropItems(world, x, y, z);
            dropBlock(world, x, y, z);
            TileEntity tileEntity = world.getTileEntity(x, y, z);
            if (tileEntity != null) {
                if (tileEntity instanceof AdvancedStorageMEInterfaceTileEntity)
                    ((AdvancedStorageMEInterfaceTileEntity) tileEntity).Destroy();
            }
        }

        super.breakBlock(world, x, y, z, block, metadata);
    }

    private void dropBlock(World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity != null) {
            if (tileEntity instanceof AdvancedStorageMEInterfaceTileEntity) {
                AdvancedStorageMEInterfaceTileEntity entity = (AdvancedStorageMEInterfaceTileEntity) tileEntity;
                int itemsCount = entity.getSizeInventory() / 2;
                ItemStack stack = new ItemStack(Item.getItemFromBlock(this));
                NBTTagCompound nbtTagCompound = new NBTTagCompound();
                for (int i = 0; i < itemsCount; i++) {
                    if (entity.getStackInSlot(i) != null) {
                        NBTTagCompound nbtSlot = new NBTTagCompound();
                        entity.getStackInSlot(i).writeToNBT(nbtSlot);
                        nbtTagCompound.setTag("itemStacks_" + i, nbtSlot);
                    }
                }
                stack.setTagCompound(nbtTagCompound);
                spawnItemEntity(stack, world, x, y, z);
            }
        }
    }

    private void spawnItemEntity(ItemStack stack, World world, int x, int y, int z) {
        Random rand = new Random();
        float rx = rand.nextFloat() * 0.8F + 0.1F;
        float ry = rand.nextFloat() * 0.8F + 0.1F;
        float rz = rand.nextFloat() * 0.8F + 0.1F;
        EntityItem entityItem = new EntityItem(world, x + rx, y + ry, z + rz, stack);
        entityItem.motionX = rand.nextGaussian() * 0.05F;
        entityItem.motionY = rand.nextGaussian() * 0.05F + 0.2F;
        entityItem.motionZ = rand.nextGaussian() * 0.05F;
        world.spawnEntityInWorld(entityItem);
    }

    @Override
    public Item getItemDropped(int state, Random rand, int fortune) {
        return null;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        int NBT_ID = 10;
        if (!world.isRemote) {
            TileEntity tileEntity = world.getTileEntity(x, y, z);
            if (tileEntity != null) {
                if (tileEntity instanceof AdvancedStorageMEInterfaceTileEntity) {
                    AdvancedStorageMEInterfaceTileEntity entity = (AdvancedStorageMEInterfaceTileEntity) tileEntity;
                    int itemsCount = entity.getSizeInventory() / 2;
                    NBTTagCompound nbtTagCompound = stack.getTagCompound();
                    if (nbtTagCompound != null) {
                        for (int i = 0; i < itemsCount; i++) {
                            if (nbtTagCompound.hasKey("itemStacks_" + i, NBT_ID)) {
                                NBTTagCompound nbtSlot = nbtTagCompound.getCompoundTag("itemStacks_" + i);
                                entity.setInventorySlotContents(i, ItemStack.loadItemStackFromNBT(nbtSlot));
                            }
                        }
                    }
                }
            }
        }
    }


    private void dropItems(World w, int x, int y, int z) {
        TileEntity tileEntity = w.getTileEntity(x, y, z);
        if (tileEntity != null)
            if (tileEntity instanceof AdvancedStorageMEInterfaceTileEntity) {
                ArrayList<ItemStack> arrayList = ((AdvancedStorageMEInterfaceTileEntity) tileEntity).getDrop();
                for (ItemStack i : arrayList) {
                    spawnItemEntity(i.copy(), w, x, y, z);
                }
            }
    }
}
