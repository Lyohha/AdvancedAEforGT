package ua.lyohha.aae.TilyEntity;

import cpw.mods.fml.common.registry.GameRegistry;

public class TileEntity
{
    //регистрация TileEntity
    public static void RegisterTileEntity()
    {
        GameRegistry.registerTileEntity(AdvancedStorageMEInterfaceTileEntity.class,"AdvancedStorageMEInterfaceTileEntity");
        GameRegistry.registerTileEntity(MultiblockMEInterfaceHatchTileEntity.class, "MultiblockMEInterfaceHatchTileEntity");
        GameRegistry.registerTileEntity(MultiblockMEInterfaceControllerTileEntity.class, "MultiblockMEInterfaceControllerTileEntity");
        GameRegistry.registerTileEntity(AdvancedPatternTerminalTileEntity.class, "AdvancedPatternTerminalTileEntity");
    }

}
