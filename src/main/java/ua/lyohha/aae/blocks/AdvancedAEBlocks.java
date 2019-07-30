package ua.lyohha.aae.blocks;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

public class AdvancedAEBlocks
{
    public static AdvancedStorageMEInterface advancedStorageMEInterface = new AdvancedStorageMEInterface();
    public static MultiblockMEInterfaceController multiblockMEInterfaceController = new MultiblockMEInterfaceController();
    public static MultiblockMEInterfaceHatch multiblockMEInterfaceHatch = new MultiblockMEInterfaceHatch();
    public static AdvancedPatternTerminal advancedPatternTerminal = new AdvancedPatternTerminal();

    //регистрация блоков
    public static void preLoad()
    {
        GameRegistry.registerBlock(advancedStorageMEInterface,"advancedstoragemeinterface");
        GameRegistry.registerBlock(multiblockMEInterfaceController, "MultiblockMEInterfaceController");
        GameRegistry.registerBlock(multiblockMEInterfaceHatch,"MultiblockMEInterfaceHatch");
        GameRegistry.registerBlock(advancedPatternTerminal, "AdvancedPatternTerminal");
    }
}
