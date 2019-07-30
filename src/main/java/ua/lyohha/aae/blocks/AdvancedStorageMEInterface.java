package ua.lyohha.aae.blocks;


import appeng.core.CreativeTab;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
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
            TileEntity tileEntity = world.getTileEntity(x, y, z);
            if (tileEntity != null) {
                if (tileEntity instanceof AdvancedStorageMEInterfaceTileEntity)
                    ((AdvancedStorageMEInterfaceTileEntity) tileEntity).Destroy();
            }
        }

        super.breakBlock(world, x, y, z, block, metadata);
    }

    private void dropItems(World w, int x, int y, int z) {
        TileEntity tileEntity = w.getTileEntity(x, y, z);
        if (tileEntity != null)
            if (tileEntity instanceof AdvancedStorageMEInterfaceTileEntity) {
                ArrayList<ItemStack> arrayList = ((AdvancedStorageMEInterfaceTileEntity) tileEntity).getDrop();
                Random rand = new Random();
                for (ItemStack i : arrayList) {
                    float rx = rand.nextFloat() * 0.8F + 0.1F;
                    float ry = rand.nextFloat() * 0.8F + 0.1F;
                    float rz = rand.nextFloat() * 0.8F + 0.1F;
                    EntityItem entityItem = new EntityItem(w, x + rx, y + ry, z + rz, i.copy());
                    if (i.hasTagCompound())
                        entityItem.getEntityItem().setTagCompound((NBTTagCompound) i.getTagCompound().copy());
                    entityItem.motionX = rand.nextGaussian() * 0.05F;
                    entityItem.motionY = rand.nextGaussian() * 0.05F + 0.2F;
                    entityItem.motionZ = rand.nextGaussian() * 0.05F;
                    w.spawnEntityInWorld(entityItem);
                }
            }
    }
}
