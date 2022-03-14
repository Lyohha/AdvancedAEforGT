package ua.lyohha.aae.items;

import cpw.mods.fml.common.registry.GameRegistry;

public class AdvancedAEItems {

    public static EncodedAdvancedPattern encodedAdvancedPattern = new EncodedAdvancedPattern();
    public static PartGregTechStorageBus partGregTechStorageBus = new PartGregTechStorageBus();
    public static PartExportMEInterface partExportMEInterface = new PartExportMEInterface();

    public static void RegisterItems()
    {
        GameRegistry.registerItem(encodedAdvancedPattern, "EncodedAdvancedPattern");
        GameRegistry.registerItem(partGregTechStorageBus, "PartGregTechStorageBus");
        GameRegistry.registerItem(partExportMEInterface, "PartExportMEInterface");
    }
}
