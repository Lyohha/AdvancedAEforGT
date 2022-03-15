package ua.lyohha.aae.blocks;

import appeng.core.CreativeTab;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import ua.lyohha.aae.TilyEntity.MultiblockMEInterfaceControllerTileEntity;
import ua.lyohha.aae.TilyEntity.MultiblockMEInterfaceHatchTileEntity;
import ua.lyohha.aae.gui.GuiMultiblockMEInterfaceHatch;

public class MultiblockMEInterfaceHatch extends BlockContainer {
    protected MultiblockMEInterfaceHatch() {
        super(Material.iron);
        this.setBlockName("MultiblockMEInterfaceHatch");
        this.setBlockTextureName("aae:MultiblockMEInterfaceHatch");
        this.setHardness(1F);
        this.setResistance(1F);
        this.setHarvestLevel("pickaxe", 1);
        this.setCreativeTab(CreativeTab.instance);
    }

    @Override
    public TileEntity createNewTileEntity(World w, int num) {
        return new MultiblockMEInterfaceHatchTileEntity();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            TileEntity tileEntity = world.getTileEntity(x, y, z);
            if (tileEntity instanceof MultiblockMEInterfaceHatchTileEntity)
                Minecraft.getMinecraft().displayGuiScreen(new GuiMultiblockMEInterfaceHatch((MultiblockMEInterfaceHatchTileEntity) tileEntity));
        }
        return true;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int metadata) {

        if(!world.isRemote)
        {
            TileEntity tileEntity = world.getTileEntity(x,y,z);
            if(tileEntity != null)
                if(tileEntity instanceof MultiblockMEInterfaceHatchTileEntity)
                    ((MultiblockMEInterfaceHatchTileEntity)tileEntity).destroy();
        }

        super.breakBlock(world, x, y, z, block, metadata);
    }
}
