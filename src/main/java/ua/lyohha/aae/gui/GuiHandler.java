package ua.lyohha.aae.gui;

import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import ua.lyohha.aae.AdvancedAE;
import ua.lyohha.aae.TilyEntity.AdvancedPatternTerminalTileEntity;
import ua.lyohha.aae.TilyEntity.AdvancedStorageMEInterfaceTileEntity;
import ua.lyohha.aae.TilyEntity.MultiblockMEInterfaceControllerTileEntity;
import ua.lyohha.aae.blocks.AdvancedPatternTerminal;
import ua.lyohha.aae.inventory.ContainerAdvancedPatternTerminal;
import ua.lyohha.aae.inventory.ContainerAdvancedStorageMEInterface;
import ua.lyohha.aae.inventory.ContainerMultiblockMEInterfaceController;

public class GuiHandler implements IGuiHandler {

    public static void RegisterGuiHandler() {
        NetworkRegistry.INSTANCE.registerGuiHandler(AdvancedAE.instance, new GuiHandler());
    }

    public final static int GUIID_ADVANCED_STORAGE_ME_INTERFACE = 21;
    public final static int GUIID_MULTIBLOCK_ME_INTERFACE_HATCH = 22;//Don't used? because have GuiScreen
    public final static int GUIID_MULTIBLOCK_ME_INTERFACE_CONTROLLER = 23;
    public final static int GUIID_ADVANCED_PATTERN_TERMINAL = 24;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (!world.isRemote) {
            net.minecraft.tileentity.TileEntity tileEntity = world.getTileEntity(x, y, z);
            switch (ID) {
                case GUIID_ADVANCED_STORAGE_ME_INTERFACE:
                    if (tileEntity instanceof AdvancedStorageMEInterfaceTileEntity)
                        return new ContainerAdvancedStorageMEInterface(player.inventory, (AdvancedStorageMEInterfaceTileEntity) tileEntity);
                case GUIID_MULTIBLOCK_ME_INTERFACE_CONTROLLER:
                    if (tileEntity instanceof MultiblockMEInterfaceControllerTileEntity)
                        return new ContainerMultiblockMEInterfaceController(player.inventory, (MultiblockMEInterfaceControllerTileEntity) tileEntity);
                case GUIID_ADVANCED_PATTERN_TERMINAL:
                    if (tileEntity instanceof AdvancedPatternTerminalTileEntity)
                        return new ContainerAdvancedPatternTerminal(player.inventory, (AdvancedPatternTerminalTileEntity) tileEntity);
            }
        }

        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        net.minecraft.tileentity.TileEntity tileEntity = world.getTileEntity(x, y, z);
        switch (ID) {
            case GUIID_ADVANCED_STORAGE_ME_INTERFACE:
                if (tileEntity instanceof AdvancedStorageMEInterfaceTileEntity)
                    return new GuiStorageAdavancedStorageMEInterface(player.inventory, (AdvancedStorageMEInterfaceTileEntity) tileEntity);
            case GUIID_MULTIBLOCK_ME_INTERFACE_CONTROLLER:
                if (tileEntity instanceof MultiblockMEInterfaceControllerTileEntity)
                    return new GuiMultiblockMEInterfaceController(player.inventory, (MultiblockMEInterfaceControllerTileEntity) tileEntity);
            case GUIID_ADVANCED_PATTERN_TERMINAL:
                if (tileEntity instanceof AdvancedPatternTerminalTileEntity)
                    return new GuiAdvancedPatternTerminal(player.inventory, (AdvancedPatternTerminalTileEntity) tileEntity);
        }
        return null;
    }
}
