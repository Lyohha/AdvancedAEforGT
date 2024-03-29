package ua.lyohha.aae;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.FMLControlledNamespacedRegistry;
import cpw.mods.fml.common.registry.GameData;
import net.minecraft.item.Item;
import net.minecraft.util.WeightedRandom;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.lyohha.aae.TilyEntity.TileEntity;
import ua.lyohha.aae.ae2.AERegistry;
import ua.lyohha.aae.blocks.AdvancedAEBlocks;
import ua.lyohha.aae.crafting.CraftRegistry;
import ua.lyohha.aae.gui.GuiHandler;
import ua.lyohha.aae.items.AdvancedAEItems;
import ua.lyohha.aae.network.Network;
import ua.lyohha.aae.part.AdvancedAEParts;
import ua.lyohha.aae.proxy.CommonProxy;

import java.util.Set;

@Mod(modid = AdvancedAE.modid, version = AdvancedAE.version, name = AdvancedAE.modname, dependencies = "after:waila;required-after:appliedenergistics2;required-after:gregtech")
public class AdvancedAE {
    //Mod Info
    public static final String modid = "aae";
    public static final String modname = "Advanced AE for GT";
    private static final int build = 1;
    public static final String version = "$VERSION$" + "." + build;

    //not used now
    public static final Logger logger = LogManager.getLogger(modname);

    @Mod.Instance(AdvancedAE.modid)
    public static AdvancedAE instance;

    @SidedProxy(clientSide = "ua.lyohha.aae.proxy.ClientProxy", serverSide = "ua.lyohha.aae.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preLoad(FMLPreInitializationEvent event) {
        AdvancedAEBlocks.preLoad();
        GuiHandler.RegisterGuiHandler();
        TileEntity.RegisterTileEntity();
        AERegistry.registry();
        (new CraftRegistry()).MCStyleRegistry();
        AdvancedAEItems.RegisterItems();
        AdvancedAEParts.RegisterItems();

    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Network.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

    }


}
