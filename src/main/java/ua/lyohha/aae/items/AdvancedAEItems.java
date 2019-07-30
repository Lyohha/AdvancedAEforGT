package ua.lyohha.aae.items;

import cpw.mods.fml.common.registry.GameRegistry;

public class AdvancedAEItems {

    public static EncodedAdvancedPattern encodedAdvancedPattern = new EncodedAdvancedPattern();

    public static void RegisterItems()
    {
        GameRegistry.registerItem(encodedAdvancedPattern, "EncodedAdvancedPattern");
    }
}
